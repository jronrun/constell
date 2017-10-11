'use strict';

var index = {};
(function ($, root, register) {

    function liveClk(selector, callback) {
        $('body').on('click', selector, function (evt) {
            var el = evt.currentTarget;
            callback && callback(el, evt);
        });
    }

    var pageInfo = null, load = 'loading', formId = '#edit-form', editId = '#edit-content', core = {

        index: {
            init: function () {
                pageInfo = decodes($$('[data-page-info]').data('pageInfo'));
            }
        },

        list: {
            init: function () {
                liveClk('[data-page-no]', function (el) {
                    var pn = parseInt($(el).data('pageNo'));
                    if ($(el).hasClass(load)) {
                        return;
                    }

                    $(el).addClass(load);
                    core.list.query($.extend({
                        pageNo: pn
                    }, getFormData('#form-search')), function () {
                        $(el).removeClass(load);
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
                    listInfo = decodes($$(infoEl).data('listInfo')) || {};
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
                if ($(el).hasClass(load)) {
                    return;
                }

                $(el).addClass(load);
                var data = getFormData(formId);

                mgr[method](action, JSON.stringify(data))
                    .fail(function (xhr) {
                        $(el).removeClass(load);
                        if (400 === xhr.status) {
                            $.each(xhr.responseJSON.messages, function (k, v) {
                                $('#field_' + k)
                                    .addClass('error')
                                    .append('<div class="ui basic red pointing prompt label transition visible">' + v + '</div>')
                                    ;
                            });
                            //$(formId).form('clear')
                        } else {
                            swal('Oops...', mgr.failMsg(xhr), 'warning');
                        }
                    })
                    .done(function (resp) {
                        $(el).removeClass(load);
                        swal(getMessage(fmt('render.alert.{0}.title', msgKey)),
                            getMessage(fmt('render.alert.{0}.text', msgKey)), 'success');

                        delay(function () {
                            $('.ui.modal').modal('hide');
                        }, 1100);
                        core.list.query({pageNo: 0});
                    });
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

        initialize: function () {
            core.index.init();
            core.list.init();
            core.edit.init();
        }
    };

    $.extend(register, {});

    $(function () {
        core.initialize();
    });

})(jQuery, window, index);