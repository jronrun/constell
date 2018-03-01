'use strict';

var show = {};
(function ($, root, register) {

    var ifr = null,
    ifrResize = function () {
        if (ifr) {
            var viewport = pi.viewport();
            ifr.attr({
                width: viewport.w,
                height: viewport.h
            });
        }
    }, pvw, taId = 'show_ta', viewId = 'show_view', showSource = '',
        lastNotifyTime = 0, lastNotifyData = null, lastTell = false, core = {
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

                $(window).resize(function (e) {
                    //e.preventDefault();
                    ifrResize();
                });
            }
        },
        reset: function () {
            $sel(viewId).empty();
            ifr = null;
            pvw && pvw.resize(-1);
        },
        load: function (aData) {
            transform.get(aData, function (sourceType, aResult) {
                showSource = aResult;
                switch (sourceType) {
                    case 1:
                        $sel(viewId).css({'margin-top': '0rem', padding: '0rem'})
                            .html(aResult);
                        $sel(viewId, ' pre.CodeMirror').niceScroll();
                        break;
                    case 2:
                        $sel(viewId).css({'margin-top': '0rem', padding: '2rem'})
                            .html(aResult).fadeIn(1000);
                        $sel(viewId).niceScroll();
                        break;
                    case 3:
                        ifr = ifr || iFrame.create({}, sel(viewId));
                        ifrResize();

                        if (pi.isUrl(aResult)) {
                            ifr.openUrl(aResult);
                        } else {
                            var txt = (aResult || '').replace(/\\\//g, '/');
                            ifr.write(txt);
                        }
                        break;
                }
            });
        },
        initialize: function () {
            core.layout.init();

            iFrame.registers({
                RESET: function () {
                    core.reset();
                },
                SOURCE: function () {
                    return {src: showSource};
                },
                REFRESH: function (evtName, evtData) {
                    core.load(evtData);
                },
                MIRROR_INPUT_READ_NOTIFY: function (evtName, evtData) {
                    lastNotifyData = evtData;
                    var curTime = parseInt(pi.now()), pTell = function () {
                        core.load(lastNotifyData);
                    };

                    if (curTime - lastNotifyTime > 1500) {
                        lastNotifyTime = curTime;
                        pTell();
                    } else {
                        if (!lastTell) {
                            lastTell = true;
                            delay(function () {
                                pTell();
                                lastTell = false;
                            }, 1000);
                        }
                    }
                }
            });
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