'use strict';

var transform = {};
(function ($, root, register) {

    var marked = {}, mdInitialized = false, beautyInitialized = false,
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
                        mirror: mirror
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
                convert: function (input, theme, resultHandle) {
                    initMarkdown(function () {
                        return marked.render(input, {}, theme, function (aResult) {
                            pi.isFunc(resultHandle) && resultHandle(2, aResult);
                        });
                    });
                }
            }, {
                type: 'name',
                key: ['HTML'],
                convert: function (input, theme, resultHandle) {
                    pi.isFunc(resultHandle) && resultHandle(3, input);
                }
            }
        ];

    var core = {

        beautify: function (aData, resultHandle) {
            var matched = false, input = aData.content,
                lang = aData.lang.mime || aData.lang.name, langMeta = mirror.modeInfo(lang);

            $.each(beautifies, function (idx, aRender) {
                if (aRender.key.indexOf(langMeta[aRender.type || 'mode']) !== -1) {
                    matched = true;
                    initBeauty(function () {
                        resultHandle(aRender.beautify(input));
                    });
                    return false;
                }
            });

            return matched;
        },

        /**
         * @param aData         mirror notify content
         * ex: {"lang":{"name":"","mime":""},"th":"","content":"","shows":""}
         * shows: 1 preview & format source, 2 format source, 3 original source
         *
         * @param resultHandle  (sourceType, aResult, langMeta, theme);)
         * sourceType: 1 code mirror run mode (depend loaded css), 2 div container (depend loaded css), 3 iFrame (standalone)
         * @returns {boolean}
         */
        get: function (aData, resultHandle) {
            var input = aData.content,
                //show type 1 preview & format, 2 format source, 3 original source
                showType = parseInt(aData.shows || 1),
                lang = aData.lang.mime || aData.lang.name, theme = aData.th,
                langMeta = mirror.modeInfo(lang),
                mirrorSource = function (beautifyInput) {
                    mirror.highlights({
                        input: beautifyInput || input,
                        mode: lang,
                        theme: theme,
                        doneHandle: function (aResult) {
                            pi.isFunc(resultHandle) && resultHandle(1, aResult, langMeta, theme);
                        }
                    });
                },
                formatSource = function () {
                    var matched = false;
                    $.each(beautifies, function (idx, aRender) {
                        if (aRender.key.indexOf(langMeta[aRender.type || 'mode']) !== -1) {
                            matched = true;
                            initBeauty(function () {
                                mirrorSource(aRender.beautify(input));
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
                            aRender.convert(input, theme, function (aSourceType, aResult) {
                                pi.isFunc(resultHandle) && resultHandle(aSourceType, aResult, langMeta, theme);
                            });
                            return false;
                        }
                    });

                    return matched;
                };

            if (langMeta) {
                if (1 === showType) {
                    if (convertSource()) {
                        return true;
                    } else if (formatSource(true)) {
                        return true;
                    }
                } else if (2 === showType) {
                    if (formatSource()) {
                        return true;
                    }
                }
            }

            mirrorSource();
        }
    };

    exporter(core, register);

})(jQuery, window, transform);