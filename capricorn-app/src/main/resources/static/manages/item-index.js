'use strict';

var index = {};
(function ($, root, register) {

    var pageInfo = null, access = null, formId = null, editId = null, searchId = null, core = {

        index: {
            init: function () {
                var pageData = $$('[data-page-info]').data() || {};
                pageInfo = pi.deepUnsign(pageData.pageInfo);
                access = pi.deepUnsign(pageData.access);
                formId = '#' + pageInfo.editId;
                editId = '#' + pageInfo.contentId;
                searchId = '#' + pageInfo.searchId;
            }
        },

        list: {
            init: function () {
                liveClk('[data-page-no]', function (el) {
                    var pn = parseInt($(el).data('pageNo'));
                    if (!mgr.loading(el)) {
                        return;
                    }

                    core.list.query($.extend({
                        pageNo: pn
                    }, getFormData(searchId)), function () {
                        mgr.unloading(el);
                    });
                });

                core.list.query(getFormData(searchId));
            },

            query: function (param, callback) {
                param = $.extend({
                    pageNo: 1
                }, param || {});

                var infoEl = '[data-list-info]', listInfo = {};

                if ($$(infoEl).length) {
                    listInfo = pi.deepUnsign($$(infoEl).data('listInfo')) || {};
                }

                if (param.pageNo === listInfo.index) {
                    return;
                }

                if (core.touch.current) {
                    var defined = core.touch.current.define,
                        isTouchOwner = $('[data-touch-show-all] input:checked').length < 1;
                    $.extend(param, {
                        touchId: defined.touchId,
                        touchModule: defined.module,
                        touchModuleId: defined.moduleId,
                        touchOwner: isTouchOwner,
                        switchable: defined.switchable,
                        touchListTitleFragment: defined.titleFragment,
                        touchListCellFragment: defined.cellFragment,
                        touchFromItemFragment: defined.touchFragment
                    });
                }

                $.get(pageInfo.list, param, function (data) {
                    $$('[data-replaceable=1]').remove();
                    $$(searchId).append(data);
                    callback && callback();
                });
            }
        },

        edit: {
            init: function () {
                liveClk('[data-delete-id]', function (el) {
                    core.edit.delete(el);
                });

                liveClk('[data-edit-id]', function (el) {
                    core.edit.retrieve(el);
                });

                liveClk('div[data-action-type]', function (el) {
                    core.edit.save(el);
                });
            },

            save: function (el) {
                var actionType = parseInt($(el).data('actionType')),
                    msgKey = 1 === actionType ? 'create' : 'update',
                    action = 1 === actionType ? pageInfo.create : pageInfo.update,
                    method = 1 === actionType ? 'post' : 'put';
                if (!mgr.loading(el)) {
                    return;
                }

                core.edit.formCleanError();
                var data = getFormData(formId);

                pi[method](action, JSON.stringify(data))
                    .fail(function (xhr) {
                        mgr.unloading(el);
                        if (400 === xhr.status) {
                            var fieldCount = 0;
                            $.each(xhr.responseJSON.messages, function (k, v) {
                                var $field = $('#field_' + k);
                                if ($field.length) {
                                    fieldCount++;
                                    $field
                                        .addClass('error')
                                        .append('<div data-error-tip="1" class="ui basic red pointing prompt label transition visible">' + v + '</div>')
                                        ;
                                }
                            });

                            if (fieldCount < 1) {
                                swal('Oops...', mgr.failMsg(xhr), 'warning');
                            }
                        } else {
                            swal('Oops...', mgr.failMsg(xhr), 'warning');
                        }
                    })
                    .done(function (resp) {
                        mgr.unloading(el);
                        swal(getMessage(fmt('render.alert.{0}.title', msgKey)),
                            getMessage(fmt('render.alert.{0}.text', msgKey)), 'success');

                        delay(function () {
                            $('.ui.modal').modal('hide');
                        }, 800);
                        core.list.query({pageNo: 0});
                    });
            },

            formClear: function () {
                $(formId).form('clear');
            },

            formCleanError: function () {
                $(formId + ' .field').removeClass('error');
                $('div[data-error-tip="1"]').remove();
            },

            retrieve: function (el) {
                var itemId = parseInt($(el).data('editId'));
                $.get(fmt(pageInfo.retrieve, itemId), function (data) {
                    var modalId = editId + ' .ui.modal';
                    $(modalId).remove();
                    $$(editId).html(data);

                    $(modalId).modal({
                        autofocus: false,
                        observeChanges: true,
                        closable: false,
                        transition: 'fade',
                        onHidden: function () {
                            $('.ui.modal').remove();
                        },
                        onShow: function () {
                            core.component.init();
                        },
                        onVisible: function () {
                            // when change disappear error
                            var fields = {};
                            $.each(getFormData(formId, true), function (k) {
                                fields[k] = {};
                            });

                            $(formId).form({
                                inline: true,
                                on: 'blur',
                                fields: fields
                            });
                        }
                        // blurring: true
                    }).modal('show');
                });
            },

            delete: function (el) {
                var itemId = parseInt($(el).data('deleteId'));
                swal({
                    title: getMessage('render.confirm.title'),
                    text: getMessage('render.confirm.delete.text'),
                    type: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: getMessage('render.confirm.delete.yes'),
                    cancelButtonText: getMessage('render.confirm.cancel'),
                    showLoaderOnConfirm: true,
                    allowOutsideClick: false,
                    preConfirm: function () {
                        return new Promise(function (resolve, reject) {
                            pi.delete(fmt(pageInfo.delete, itemId))
                            .fail(function (xhr) {
                                var msg = mgr.failMsg(xhr);
                                swal('Oops...', msg, 'warning');
                                reject();
                            })
                            .done(function (resp) {
                                resolve(resp);
                            });
                        })
                    }
                }).then(function (resp) {
                    swal(getMessage('render.alert.delete.title'), getMessage('render.alert.delete.text'), 'success');
                    core.list.query({pageNo: 0});
                }, function (dismiss) {
                    // dismiss can be 'cancel', 'overlay', 'close', and 'timer'
                    if (dismiss === 'cancel') {}
                });
            }
        },

        touch: {
            column: '[data-touch-toggle-all]',
            current: null,
            init: function () {
                liveClk('[data-touch-action]', function (el) {
                    var defineTouch = pi.deepUnsign($('#touch-' + $(el).data('touchAction')).data('defineTouch'));
                    defineTouch.touchId = parseInt($(el).data('touchId'));
                    core.touch.touch(defineTouch, true);
                });

                liveClk('[data-touch-show-all]', function (el) {
                    if (mgr.loading(el)) {
                        core.list.query({pageNo: 0}, function () {
                            mgr.unloading(el);
                        });
                    }

                });

                if (core.touch.current) {
                    $(searchId).prepend(core.touch.current.show);
                }
            },

            clear: function () {
                core.touch.current = null;
            },

            action: function () {
                var touchOneSelector = '[data-touch-toggle]', touchAllSelector = core.touch.column,
                    clazz = 'toggles', togglesActionClazz = 'actions', hasClazz = function (aClazz) {
                    return mgr.hasLoading(touchAllSelector, aClazz || clazz);
                };

                $(touchOneSelector).checkbox({
                    beforeChecked: function () {
                        return hasClazz(togglesActionClazz) ? true : (!hasClazz());
                    },
                    beforeUnchecked: function () {
                        return hasClazz(togglesActionClazz) ? true : (!hasClazz());
                    },
                    onChecked: function () {
                        if (!hasClazz()) {
                            core.touch.buildRelation([$(this).data('value')]);
                        }
                    },
                    onUnchecked: function () {
                        if (!hasClazz()) {
                            core.touch.buildRelation([$(this).data('value')], true);
                        }
                    }
                });

                $(touchAllSelector).checkbox({
                    beforeChecked: function () {
                        return !hasClazz();
                    },
                    beforeUnchecked: function () {
                        return !hasClazz();
                    },
                    onChecked: function () {
                        if (mgr.loading(touchAllSelector, clazz)) {
                            mgr.loading(touchAllSelector, togglesActionClazz);
                            var ids = core.touch.getRelationIds(true);
                            $(touchOneSelector).checkbox('check');
                            mgr.unloading(touchAllSelector, togglesActionClazz);
                            core.touch.buildRelation(ids, false, function () {
                                mgr.unloading(touchAllSelector, clazz)
                            });
                        }
                    },
                    onUnchecked: function () {
                        if (mgr.loading(touchAllSelector, clazz)) {
                            mgr.loading(touchAllSelector, togglesActionClazz);
                            var ids = core.touch.getRelationIds();
                            $(touchOneSelector).checkbox('uncheck');
                            mgr.unloading(touchAllSelector, togglesActionClazz);
                            core.touch.buildRelation(ids, true, function () {
                                mgr.unloading(touchAllSelector, clazz)
                            });
                        }
                    }
                });

            },

            buildRelation: function (touchToIds, isUnBuild, successCall) {
                touchToIds = touchToIds || [];
                if (touchToIds.length < 1) {
                    $.isFunction(successCall) && successCall(null);
                    return false;
                }

                var defineTouch = core.touch.current.define, touchId = defineTouch.touchId,
                    masterIds = defineTouch.master ? touchToIds : [touchId],
                    slaveIds = defineTouch.master ? [touchId] : touchToIds, data = {
                    module: defineTouch.module,
                    masterIds: masterIds,
                    slaveIds: slaveIds
                }, method = isUnBuild ? 'delete' : 'post';

                pi[method](defineTouch.relationHref, JSON.stringify(data)).fail(function (xhr) {
                    swal('Oops...', mgr.failMsg(xhr), 'warning');
                }).done(function (resp) {
                    $.isFunction(successCall) && successCall(resp);
                });
            },

            getRelationIds: function (isUnchecked) {
                var relationIds=[],
                    selector = 'input[id^=touch-tgl-]' + (isUnchecked ? ':not(:checked)' : ':checked');

                $.map($(selector), function (el) {
                    relationIds.push($(el).data('value'));
                });

                return relationIds;
            },

            touch: function (defineTouch, isOwnerOnly) {
                var touchItem = $(fmt('#{0}-{1}', defineTouch.id, defineTouch.touchId)).html();

                mgr.pjax(defineTouch.touchHref, {
                    touchable: !!isOwnerOnly,
                    onEnd: function () {
                        core.touch.current = {
                            define: defineTouch,
                            show: touchItem
                        };
                    }
                });
            }
        },

        component: {
            init: function () {
                $('[data-com-dropdown]').dropdown();
            }
        },

        initialize: function () {
            core.index.init();
            core.list.init();
            core.edit.init();
            core.touch.init();
            core.component.init();
        }
    };

    var regs = {};
    function reg(selector, handle, event) {
        event = event || 'click';
        var selectorHandle = regs[selector], eventHandle = (selectorHandle || {})[event];
        if (!selectorHandle && !eventHandle) {
            var aSelectorHandle = {};
            aSelectorHandle[event] = handle;
            regs[selector] = aSelectorHandle;

            liveClk(selector, function (el, evt) {
                handle(pageInfo, el, evt);
            }, event);
        }
    }

    $.extend(register, {
        init: function () {
          core.initialize();
        },
        touchClear: function () {
            core.touch.clear();
        },
        touchReady: function () {
            core.touch.action();
        },
        reload: function () {
            core.list.query({pageNo: 0});
        },
        register: function (selector, handle, event) {
            return reg(selector, handle, event);
        }
    });

})(jQuery, window, index);