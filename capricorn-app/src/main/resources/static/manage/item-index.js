'use strict';

var index = {};
(function ($, root, register) {

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
                $('body').on('click', '[data-page-no]', function (evt) {
                    var load = 'loading', el = evt.currentTarget, pn = parseInt($(el).data('pageNo'));
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
                    $$('#item-list').empty().html(data);
                    callback && callback();
                });
            }
        },

        edit: {
            init: function () {

            }
        },
        
        initialize: function () {
            core.index.init();
            core.list.init();
        }
    };

    $.extend(register, {
    });

    $(function () {
        core.initialize();
    });

})(jQuery, window, index);