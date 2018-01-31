'use strict';

var write = {};
(function ($, root, register) {

    var pvs = null, instance = null, nid = pi.uniqueId('note-'), core = {
        initialize: function () {
            pvs = comm.previews({
                popup: false,
                toggle: false,
                parentSet: 2,
                content: {
                    context: fmt('<textarea style="border: 0; width: 100%; height: 100%;" id="{0}"></textarea>', nid),
                    callback: function (elId) {
                        $sel(elId).css({
                            padding: 0
                        });
                        instance = note(nid);
                    }
                }
            });

            //TODO rem
            window.aa=pvs;
            window.nn=instance;
        }
    };

    $.extend(register, {
    });

    $(function () {
        core.initialize();
    });

})(jQuery, window, write);