'use strict';

var show = {};
(function ($, root, register) {

    var pvw, taId = 'show_ta', core = {
        layout: {
            init: function () {
                var showContent = '<textarea rows="1" style="border: none;display: none;" id="{0}"></textarea>';
                pvw = comm.previews({
                    popup: false,
                    toggle: false,
                    parentSet: 2,
                    content: {
                        context: fmt(showContent, taId),
                        callback: function (elId) {
                            $sel(elId).css({
                                padding: 0
                            });
                        }
                    }
                });

                //TODO rem
                window.aa=pvw;
            }
        },
        load: function (aData) {

        },
        initialize: function () {
            core.layout.init();
        }
    };

    $.extend(register, {
    });

    $(function () {
        core.initialize();
    });

})(jQuery, window, show);