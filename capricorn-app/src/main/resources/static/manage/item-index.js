'use strict';

var index = {};
(function ($, root, register) {

    function liveClk(selector, callback, event) {
        $('body').on(event || 'click', selector, function (evt) {
            var el = evt.currentTarget;
            callback && callback(el, evt);
        });
    }

    var pageInfo = null, formId = '#edit-form', editId = '#edit-content', core = {

        index: {
            init: function () {
                pageInfo = JSON.parse(mgr.us($$('[data-page-info]').data('pageInfo'))) || {};
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
                    }, getFormData('#form-search')), function () {
                        mgr.unloading(el);
                    });
                });

                core.list.query();
            },

            query: function (param, callback) {
                param = $.extend({
                    pageNo: 1
                }, param || {});

                var infoEl = '[data-list-info]', listInfo = {};

                if ($$(infoEl).length) {
                    listInfo = JSON.parse(mgr.us($$(infoEl).data('listInfo'))) || {};
                }

                if (param.pageNo === listInfo.index) {
                    return;
                }

                $.get(pageInfo.list, param, function (data) {
                    $$('[data-replaceable=1]').remove();
                    $$('#form-search').append(data);
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

                mgr[method](action, JSON.stringify(data))
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
                            mgr.delete(fmt(pageInfo.delete, itemId))
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

        component: {
            init: function () {
                $('[data-com-dropdown]').dropdown();
            }
        },

        initialize: function () {
            core.index.init();
            core.list.init();
            core.edit.init();
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
        reload: function () {
            core.list.query({pageNo: 0});
        },
        register: function (selector, handle, event) {
            return reg(selector, handle, event);
        }
    });

    $(function () {
        core.initialize();
    });

})(jQuery, window, index);