'use strict';

var index = {};
(function ($, root, register) {
    var core = {
        initialize: function () {
            $$('[data-attrs]').each(function (idx, item) {
                var attrs = JSON.parse(LZString.decompressFromEncodedURIComponent($(item).data('attrs')));
                $(item).attr(attrs);
            });
        }
    };

    $.extend(register, {
    });

    $(function () {
        core.initialize();
    });

})(jQuery, window, index);