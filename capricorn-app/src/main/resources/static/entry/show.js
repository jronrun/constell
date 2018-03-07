'use strict';

var show = {};
(function ($, root, register) {

    var scrollMap = null, isScrollMapReady = false, isScrollMapBuilding = false,
        isScrollSynchronize = false;
    function buildMarkdownScrollMap() {
        if (isScrollMapBuilding) {
            return;
        }

        isScrollMapBuilding = true;
        isScrollMapReady = false;
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
            if (t === '') {
                return;
            }
            if (t !== 0) {
                nonEmptyList.push(t);
            }
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

        scrollMap = _scrollMap;
        isScrollMapReady = true;
        isScrollMapBuilding = false;
    }

    // Synchronize scroll position from source to result
    var syncFs = {
        lastLineNo: 0,
        to: function (lineInfo) {
            if (!scrollMap) {
                syncFs.build();
            }

            if (!isScrollMapReady) {
                return;
            }

            lineInfo = lineInfo || {};
            var i, lineNo = parseInt(lineInfo.top || 0), realLineNo = 0, posTo = scrollMap[lineNo];
            if (lineNo === syncFs.lastLineNo) {
                return;
            }

            if (pi.isUndefined(posTo)) {
                if (lineNo > syncFs.lastLineNo) {
                    var lineEnd = parseInt(lineInfo.bottom || 0);
                    for (i = lineNo; i <= lineEnd; i++) {
                        if (!pi.isUndefined(posTo = scrollMap[i])) {
                            realLineNo = i;
                            break;
                        }
                    }
                } else {
                    for (i = lineNo; i >= 0; i--) {
                        if (!pi.isUndefined(posTo = scrollMap[i])) {
                            realLineNo = i;
                            break;
                        }
                    }
                }
            }

            if (pi.isUndefined(posTo) || syncFs.lastLineNo === realLineNo) {
                return;
            }

            $sel(viewId).stop(true).animate({
                scrollTop: posTo
            }, 100, 'linear');
            syncFs.lastLineNo = lineNo;
        },
        build: function () {
            if (!isScrollSynchronize) {
                return;
            }

            buildMarkdownScrollMap();
        },
        script: function (callback) {
            if (syncFs.loaded) {
                callback();
            } else {
                pi.script('/module/lodash.js', function () {
                    syncFs.loaded = true;
                    callback();
                });
            }
        }
    },
    // Synchronize scroll position from result to source
    toSrc = {
        isReady: false,
        linesInfo: [],
        initialized: false,
        lastLineNo: 0,
        initLinesInfo: function (callback) {
            iFrame.post('SCROLL_INFO', {}, function (n, evtData) {
                toSrc.isReady = true;
                toSrc.linesInfo = evtData.data.linesInfo;
                callback();
            });
        },
        init: function () {
            if (!isScrollSynchronize || true === toSrc.initialized) {
                return;
            }

            syncFs.script(function () {
                toSrc.sync = _.debounce(function () {
                    // line and html map
                    var doSync = function () {
                        var resultHtml = $sel(viewId),
                            scrollTop  = resultHtml.scrollTop(),
                            lines,
                            i,
                            line;

                        lines = Object.keys(toSrc.linesInfo);

                        if (lines.length < 1) {
                            return;
                        }

                        line = lines[0];

                        for (i = 1; i < lines.length; i++) {
                            if (toSrc.linesInfo[lines[i]] < scrollTop) {
                                line = lines[i];
                                continue;
                            }

                            break;
                        }

                        if (toSrc.lastLineNo !== line) {
                            toSrc.lastLineNo = line;
                            iFrame.post('SCROLL', {
                                line: line
                            });
                        }
                    };

                    if (toSrc.isReady) {
                        doSync();
                    } else {
                        toSrc.initLinesInfo(doSync);
                    }
                }, 50, { maxWait: 50 });

                $sel(viewId).on('touchstart mouseover', function () {
                    iFrame.post('SCROLL_NOTIFY_STOP');
                    $sel(viewId).on('scroll', toSrc.sync);
                });
            });
        },
        build: function () {
            toSrc.isReady = false;
        },
        close: function () {
            $sel(viewId).off('scroll', toSrc.sync);
        }
    };

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
                    delay(function () {
                        syncFs.build();
                        toSrc.build();
                    }, 500);
                });
            }
        },
        reset: function () {
            $sel(viewId).empty();
            ifr = null;
            pvw && pvw.resize(-1);
        },
        load: function (aData) {
            var doneEvent = function () {
                iFrame.post('LOAD_DONE');
            };

            transform.get(aData, function (sourceType, aResult) {
                showSource = aResult;
                switch (sourceType) {
                    case 1:
                        $sel(viewId).css({'margin-top': '0rem', padding: '0rem'})
                            .html(aResult);
                        $sel(viewId, ' pre.CodeMirror').niceScroll();
                        doneEvent();
                        break;
                    case 2:
                        $sel(viewId).css({'margin-top': '0rem', padding: '2rem'})
                            .html(aResult).fadeIn(1000);
                        $sel(viewId).niceScroll();
                        doneEvent();
                        break;
                    case 3:
                        ifr = ifr || iFrame.create({}, sel(viewId));
                        ifrResize();

                        if (pi.isUrl(aResult)) {
                            ifr.openUrl(aResult, function () {
                                doneEvent();
                            });
                        } else {
                            var txt = (aResult || '').replace(/\\\//g, '/');
                            ifr.write(txt);
                            doneEvent();
                        }
                        break;
                }

                delay(function () {
                    syncFs.build();
                    toSrc.build();
                }, 800);
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
                SYNC_SCROLL: function (evtName, evtData) {
                    isScrollSynchronize = evtData.sync;
                    //toSrc.init();
                },
                SCROLL: function (evtName, evtData) {
                    syncFs.to(evtData);
                },
                SCROLL_NOTIFY_STOP: function (evtName, evtData) {
                    toSrc.close();
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
    window.buildMarkdownScrollMap=buildMarkdownScrollMap;

    $(function () {
        core.initialize();
    });

})(jQuery, window, show);