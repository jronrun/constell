'use strict';

var write = {};
(function ($, root, register) {

    var pvs = null, core = {
        initialize: function () {
            pvs = comm.previews({
                popup: false,
                toggle: false,
                parentSet: 2,
                content: {
                    context: ''
                }
            });

            //TODO rem
            window.aa=pvs;
        }
    };

    $.extend(register, {
    });

    $(function () {
        core.initialize();
    });

})(jQuery, window, write);