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

                delay(function () {
                    core.scroll.build();
                }, 200);
            });
        },
        scroll: {
            map: null,
            syncAble: false,
            lastLineNo: 0,
            to: function (lineInfo) {
                if (!core.scroll.map) {
                    core.scroll.build();
                }

                lineInfo = lineInfo || {};
                var i, lineNo = parseInt(lineInfo.line || 0), realLineNo = 0, posTo = core.scroll.map[lineNo];
                if (lineNo === core.scroll.lastLineNo) {
                    return;
                }

                if (pi.isUndefined(posTo)) {
                    if (lineNo > core.scroll.lastLineNo) {
                        var lineCount = parseInt(lineInfo.count || 0);
                        for (i = lineNo; i <= lineCount; i++) {
                            if (!pi.isUndefined(posTo = core.scroll.map[i])) {
                                realLineNo = i;
                                break;
                            }
                        }
                    } else {
                        for (i = lineNo; i >= 0; i--) {
                            if (!pi.isUndefined(posTo = core.scroll.map[i])) {
                                realLineNo = i;
                                break;
                            }
                        }
                    }
                }

                if (pi.isUndefined(posTo) || core.scroll.lastLineNo === realLineNo) {
                    return;
                }

                console.warn('lineNo: ' + lineNo + ' ' + posTo + ' realLineNo: ' + realLineNo);

                $sel(viewId).stop(true).animate({
                    scrollTop: posTo
                }, 100, 'linear');
                core.scroll.lastLineNo = lineNo;
            },
            build: function () {
                if (!core.scroll.syncAble) {
                    return;
                }

                console.info('show scroll build');
                var i, _scrollMap = [], nonEmptyList = [],
                    lines = $sel(viewId, ' .line'), linesCount = lines.length,
                    $view = $sel(viewId), offset = $view.scrollTop() - $view.offset().top;

                for (i = 0; i < linesCount; i++) {
                    _scrollMap.push(-1);
                }

                nonEmptyList.push(0);
                _scrollMap[0] = 0;

                lines.each(function (n, el) {
                    var $el = $(el), t = pi.data(el, 'line');
                    if (t === '') { return; }
                    if (t !== 0) { nonEmptyList.push(t); }
                    _scrollMap[t] = Math.round($el.offset().top + offset);
                });

                nonEmptyList.push(linesCount);
                _scrollMap[linesCount] = $view[0].scrollHeight;

                var pos = 0, a, b;
                for (i = 1; i < linesCount; i++) {
                    if (_scrollMap[i] !== -1) {
                        pos++;
                        continue;
                    }

                    a = nonEmptyList[pos];
                    b = nonEmptyList[pos + 1];
                    _scrollMap[i] = Math.round((_scrollMap[b] * (i - a) + _scrollMap[a] * (b - i)) / (b - a));
                }

                core.scroll.map = _scrollMap;
            },
            script: function (callback) {
                if (core.scroll.loaded) {
                    callback();
                } else {
                    pi.script('/module/lodash.js', function () {
                        core.scroll.loaded = true;
                        callback();
                    });
                }
            }
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
                SYNC_SCROLL: function (evtName, evtData) {
                    console.log('evt: ' + evtName + ' ' + JSON.stringify(evtData));
                    core.scroll.syncAble = evtData.sync;
                },
                SCROLL: function (evtName, evtData) {
                    console.log('evt: ' + evtName + ' ' + JSON.stringify(evtData));
                    core.scroll.to(evtData);
                },
                REFRESH: function (evtName, evtData) {
                    core.load(evtData);
                },
                MIRROR_INPUT_READ_NOTIFY: function (evtName, evtData) {
                    console.log('evt: ' + evtName + ' ' );
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