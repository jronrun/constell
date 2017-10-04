'use strict';

var index = {};
(function ($, root, register) {
    var pageInfo = null, core = {
        initialize: function () {
            $$('[data-attrs]').each(function (idx, item) {
                $(item).attr(decodes($(item).data('attrs')));
                pageInfo = decodes($$('[data-page-info]').data('pageInfo'));

                $$('#item-list').load(pageInfo.list, function (data) {
                    console.log(data);
                });
            });
        }
    };

    $.extend(register, {
    });

    $(function () {
        core.initialize();
    });

})(jQuery, window, index);