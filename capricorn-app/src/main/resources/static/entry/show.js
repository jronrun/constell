'use strict';

var show = {};
(function ($, root, register) {

    var pvw, taId = 'show_ta', viewId = 'show_view', core = {
        layout: {
            init: function () {
                var showContent = [
                    '<textarea rows="1" style="border: none;display: none;" id="{0}"></textarea>',
                    '<div id="{1}" style="height: 100%; width: 100%;"></div>'
                ].join('');
                pvw = comm.previews({
                    popup: false,
                    toggle: false,
                    parentSet: 2,
                    content: {
                        context: fmt(showContent, taId, viewId),
                        callback: function (elId) {
                            $sel(elId).css({
                                padding: 0
                            });
                        }
                    }
                });
                pvw.resize(-1);

                //TODO rem
                window.aa=pvw;
            }
        },
        load: function (aData) {
            var input = aData.content,
                lang = aData.lang.mime || aData.lang.name, theme = aData.th;

            mirror.highlights({
                input: input,
                mode: lang,
                theme: theme,
                outputEl: sel(viewId),
                doneHandle: function () {

                }
            });
        },
        initialize: function () {
            core.layout.init();
        }
    };

    //TODO rem
    window.show = core;

    $.extend(register, {
    });

    $(function () {
        core.initialize();
    });

})(jQuery, window, show);