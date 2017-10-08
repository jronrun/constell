'use strict';

var index = {};
(function ($, root, register) {

    function liveClk(selector, callback) {
        $('body').on('click', selector, function (evt) {
            var el = evt.currentTarget;
            callback && callback(el, evt);
        });
    }

    var pageInfo = null, core = {

        index: {
            init: function () {
                $$('[data-attrs]').each(function (idx, item) {
                    $(item).attr(decodes($(item).data('attrs')));
                    pageInfo = decodes($$('[data-page-info]').data('pageInfo'));
                });
            }
        },

        list: {
            init: function () {
                liveClk('[data-page-no]', function (el) {
                    var load = 'loading', pn = parseInt($(el).data('pageNo'));
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
                                mgr.del(fmt(pageInfo.delete, itemId))
                                .fail(function (xhr) {
                                    var msg = (xhr.responseJSON || {}).result || xhr.responseText || 'request fail';
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
                        core.list.query();
                    }, function (dismiss) {
                        // dismiss can be 'cancel', 'overlay', 'close', and 'timer'
                        if (dismiss === 'cancel') {}
                    });
                });

                liveClk('[data-edit-id]', function (el) {
                    var itemId = parseInt($(el).data('editId'));
                    alert(itemId);
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