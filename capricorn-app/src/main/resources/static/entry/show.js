'use strict';

var show = {};
(function ($, root, register) {

    var marked = {}, mdInitialized = false, beautyInitialized = false, ifr = null, source = '',
    ifrResize = function () {
        if (ifr) {
            var viewport = pi.viewport();
            ifr.attr({
                width: viewport.w,
                height: viewport.h
            });
        }
    },
    initBeauty = function (callback) {
        if (beautyInitialized) {
            callback();
        } else {
            pi.script('/module/beautify.js', function () {
                beautyInitialized = true;
                callback();
            });
        }
    },
    initMarkdown = function (callback) {
        if (mdInitialized) {
            callback();
        } else {
            var mdFmt = '/module/markdown.{0}';
            pi.css(fmt(mdFmt, 'css'));
            pi.script(fmt(mdFmt, 'js'), function () {
                mdInitialized = true;
                marked = markdown({
                    mirror: mirror,
                    output: sel(viewId)
                });
                callback();
            });
        }
    }, beautifies = [
        {
            key: ['sql'],
            beautify: function (source, step) {
                return vkbeautify.sql(source, step);
            }
        },
        {
            key: ['xml'],
            beautify: function (source, step) {
                return vkbeautify.xml(source, step);
            }
        },
        {
            key: ['css'],
            beautify: function (source, options) {
                return css_beautify(source, options);
            }
        },
        {
            key: ['htmlembedded', 'htmlmixed'],
            beautify: function (source, options) {
                return html_beautify(source, options);
            }
        },

        {
            type: 'name',
            key: ['JSON', 'JSON-LD'],
            beautify: function (source, space) {
                return JSON.stringify(source, false, space || 2);
            }
        },
        {
            type: 'name',
            key: ['JavaScript', 'Embedded Javascript', 'TypeScript'],
            beautify: function (source, options) {
                return js_beautify(source, options);
            }
        }
    ], conversions = [
        {
            type: 'name',
            key: ['Markdown'],
            convert: function (input, theme) {
                $sel(viewId).css({
                    'margin-top': '0rem',
                    padding: '2rem'
                });

                initMarkdown(function () {
                    return marked.render(input, {}, theme, function (aResult) {
                        source = aResult;
                    });
                });

                $sel(viewId).niceScroll({
                    cursorcolor: 'grey'
                });
            }
        },{
            type: 'name',
            key: ['HTML'],
            convert: function (input, theme) {
                ifr = ifr || iFrame.create({}, sel(viewId));
                ifrResize();

                if (pi.isUrl(input)) {
                    source = input;
                    ifr.openUrl(input);
                } else {
                    var txt = (input || '').replace(/\\\//g, '/');
                    source = txt;
                    ifr.write(txt);
                }
            }
        }
    ];

    var pvw, taId = 'show_ta', viewId = 'show_view',
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
            var input = aData.content,
            //show type 1 preview & format, 2 format source, 3 original source
            showType = parseInt(aData.shows || 1),
            lang = aData.lang.mime || aData.lang.name, theme = aData.th,
            langMeta = mirror.modeInfo(lang),
            showInMirror = function (beautifyInput) {
                $sel(viewId).css({
                    'margin-top': '0rem',
                    padding: '0rem'
                });
                mirror.highlights({
                    input: beautifyInput || input,
                    mode: lang,
                    theme: theme,
                    outputEl: sel(viewId),
                    doneHandle: function (aResult) {
                        source = aResult;
                        $sel(viewId, ' pre.CodeMirror').niceScroll();
                    }
                });
            },
            formatSource = function () {
                var matched = false;
                $.each(beautifies, function (idx, aRender) {
                    if (aRender.key.indexOf(langMeta[aRender.type || 'mode']) !== -1) {
                        matched = true;
                        initBeauty(function () {
                            showInMirror(aRender.beautify(input));
                        });
                        return false;
                    }
                });

                return matched;
            },
            convertSource = function () {
                var matched = false;
                $.each(conversions, function (idx, aRender) {
                    if (aRender.key.indexOf(langMeta[aRender.type || 'mode']) !== -1) {
                        matched = true;
                        aRender.convert(input, theme);
                        return false;
                    }
                });

                return matched;
            };

            if (langMeta) {
                if (1 === showType) {
                    if (convertSource()) {
                        return true;
                    } else if (formatSource()) {
                        return true;
                    }
                } else if (2 === showType) {
                    if (formatSource()) {
                        return true;
                    }
                }
            }

            showInMirror();
        },
        initialize: function () {
            core.layout.init();

            iFrame.registers({
                RESET: function () {
                    core.reset();
                },
                SOURCE: function () {
                    return {src: source};
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
    window.marked=marked;

    $.extend(register, {
    });

    $(function () {
        core.initialize();
    });

})(jQuery, window, show);