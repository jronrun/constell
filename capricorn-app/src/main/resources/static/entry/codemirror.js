'use strict';

(function ($, root, register) {

    var languages = {}, modes = {}, loadedTheme = ['default', 'lemon'], themes = [
        'default','lemon','3024-day','3024-night','abcdef','ambiance','base16-dark','base16-light','bespin','blackboard','cobalt',
        'colorforth','dracula','eclipse','elegant','erlang-dark','hopscotch','icecoder','isotope','lesser-dark','liquibyte',
        'material','mbo','mdn-like','midnight','monokai','neat','neo','night','paraiso-dark','paraiso-light','pastel-on-dark',
        'railscasts','rubyblue','seti','solarized,dark','solarized,light','the-matrix','tomorrow-night-bright','tomorrow-night-eighties',
        'ttcn','twilight','vibrant-ink','xq-dark','xq-light','yeti','zenburn'
    ], thirdThemes = [
        'chrome-devtools','cssedit','eiffel','github','mac-classic','one-dark','sidewalkchalk','summerfruit','tomorrow-night-blue','toy-chest'
    ], blackBGMark = [
        'dark', 'night', 'black', 'abcdef'
    ];

    //merge third themes
    $.each(thirdThemes, function (idx, a3rdTh) { themes.push(a3rdTh); });

    function getMimes(lang) {
        if (lang.mimes && lang.mimes.length > 0) {
            return lang.mimes;
        }

        if ('null' !== lang.mime) {
            return [lang.mime];
        }

        return [];
    }

    function initializeLangs() {
        //["name", "mime", "mode"], Optional property: ["ext", "mimes", "file", "alias"]
        var langs = [];
        $.each(CodeMirror.modeInfo || [], function(idx, lang) {
            lang.id = pi.uniqueId();
            lang.theMimes = getMimes(lang);
            languages[lang.name.toLowerCase()] = lang;
            modes[lang.mode.toLowerCase()] = lang;
            langs.push(lang);
        });
        return CodeMirror.modeInfo = langs;
    }

    initializeLangs();

    function langInfo(lang) {
        var m, info;
        lang = String(lang || '').toLowerCase();
        info = languages[lang] || modes[lang];
        if (info) {
            return info;
        }

        if (m = /.+\.([^.]+)$/.exec(lang)) {
            info = CodeMirror.findModeByExtension(m[1]);
        } else if (/\//.test(lang)) {
            info = CodeMirror.findModeByMIME(lang);
            info.mime = lang;
        }

        if (info) {
            return info;
        }

        info = CodeMirror.findModeByFileName(lang);
        if (info) {
            return info;
        }

        info = CodeMirror.findModeByName(lang);
        if (info) {
            return info;
        }

        info = CodeMirror.findModeByExtension(lang);
        if (info) {
            return info;
        }

        return null;
    }

    function isThirdTheme(target) {
        var isThirdTheme = false;

        $.each(thirdThemes, function (idx, a3rdth) {
            if (target.indexOf(a3rdth) !== -1) {
                isThirdTheme = true;
                return false;
            }
        });

        return isThirdTheme;
    }

    function loadTheme(th) {
        th = /^solarized/.test(th) ? 'solarized' : th;
        if (themes.indexOf(th) === -1) {
            throw Error('Cound not find theme ' + th);
        }

        if (loadedTheme.indexOf(th) === -1) {
            mirror.css(fmt('/theme/{0}.css', th));
            loadedTheme.push(th);
        }

        return th;
    }

    function fmtjson() {
        return JSON.stringify(target, false, 2);
    }

    var keyMappings = {
        escKey: { k: 'Esc', f: 'fullscreenTgl'},
        ctrlLKey: { k: 'Ctrl-L', f: 'guttersTgl'}
    }, customEvts = [ 'fullscreen' ], inputReadNotifyEvt = 'inputReadNotifyEvt',
        inputReadNotifyEvtHandle = null,
        helper = function(cm, events) {
        events = events || {};
        var tools = {
            target: cm,
            langInfo: langInfo,
            handleCmd: function (input) {
                return cm.execCommand(input);
            },
            selected: function (noMirrorTextIfNoneSelected) {
                var text = ''; if (tools.doc().somethingSelected()) {
                    text = tools.doc().getSelection();
                } else {
                    if (!noMirrorTextIfNoneSelected) {
                        text = tools.val();
                    }
                }

                return text;
            },
            repSelected: function (code, collapse, origin) {
                tools.doc().replaceSelection(code, collapse, origin);
                tools.format();
            },
            attrs: function (optionKey, optionVal) {
                if (pi.isJson(optionKey)) {
                    $.each(optionKey, function (k, v) {
                        cm.setOption(k, v);
                    });
                    return optionKey;
                }

                if (pi.isUndefined(optionKey)) {
                    return cm.options;
                }

                if (pi.isArray(optionKey)) {
                    var rAttr = {};
                    $.each(optionKey, function (idx, okey) {
                        rAttr[okey] = cm.getOption(okey);
                    });
                    return rAttr;
                }

                var aVal = cm.getOption(optionKey);
                if (pi.isUndefined(optionVal)) {
                    return aVal;
                }

                cm.setOption(optionKey, optionVal);
                return aVal;
            },
            theme: function(th) {
                if (!th) {
                    return tools.attrs('theme');
                }

                th = loadTheme(th);
                tools.attrs('theme', th);
                return th;
            },
            mode: function(lan, optionalChosenMimeOrExt) {
                if (!lan) {
                    var rInfo = {};
                    $.extend(rInfo, tools.langInfo(tools.attrs('mode')));
                    $.extend(rInfo, {
                        chosenMimeOrExt: tools.attrs('chosenMimeOrExt') || ''
                    });
                    return rInfo;
                }

                var info = tools.langInfo(lan);
                if (!info) {
                    throw Error('Could not find a mode corresponding to ' + lan);
                }

                var spec = info.mime, mode = info.mode;
                spec = 'null' === spec ? mode : spec;
                tools.attrs('mode', spec);
                tools.autoLoadMode(mode);

                if (optionalChosenMimeOrExt) {
                    tools.attrs('chosenMimeOrExt', optionalChosenMimeOrExt);
                }

                return info;
            },
            autoLoadMode: function (mode) {
                if (!$.isFunction(CodeMirror.autoLoadMode)) {
                    mirror.script('/addon/mode/loadmode.js', function () {
                        CodeMirror.modeURL = pi.fullUrl('/components/codemirror/mode/%N/%N.js');
                        CodeMirror.autoLoadMode(cm, mode);
                    });
                } else {
                    CodeMirror.autoLoadMode(cm, mode);
                }
            },
            tip: function (msg, tipOptions) {
                return cm.openNotification('<span style="color: orange">' + msg + '</span>', $.extend({
                    bottom: true,
                    duration: 5000
                }, tipOptions || {}));
            },
            //opt 1 toggle, 2 true, 3 false, 4 get
            tglOption: function (optionKey, opt) {
                switch (opt = opt || 1) {
                    case 1:
                        tools.attrs(optionKey, !tools.attrs(optionKey));
                        break;
                    case 2:
                        tools.attrs(optionKey, true);
                        break;
                    case 3:
                        tools.attrs(optionKey, false);
                        break;
                    case 4:
                        break;
                }

                return tools.attrs(optionKey);
            },
            wordwrap: function() {
                return tools.tglOption('lineWrapping');
            },
            elId: function () {
                var theId = $(tools.el()).attr('id');
                if (!theId) {
                    theId = 'mirror_' + pi.uniqueId();
                    $(tools.el()).attr({id: theId});
                }

                return '#' + theId;
            },
            el: function () {
                return cm.getWrapperElement();
            },
            doc: function() {
                return cm.doc;
            },
            prependHtml: function (aHtml) {
                $(tools.el()).prepend(aHtml);
            },
            mapkey: function (keymap) {
                cm.setOption("extraKeys", $.extend(cm.getOption('extraKeys') || {}, keymap || {}));
            },
            toLastLine: function () {
                //cm.scrollIntoView({line: cm.lastLine()})
                tools.toLine(cm.lastLine() + 1);
            },
            toLine: function (line, ch) {
                cm.setCursor((line || 1) - 1, ch || 0)
            },
            //opt 1 toggle, 2 show, 3 unshow, 4 get
            guttersTgl: function (opt, options) {
                var hasGutters = ((tools.attrs('gutters') || []).length > 0);
                switch (opt = opt || 1) {
                    case 1:
                        return tools.guttersTgl(options, hasGutters ? 3 : 2);
                    case 2:
                        if (!hasGutters) {
                            options = $.extend({
                                foldGutter: true,
                                lineNumbers: true,
                                gutters: ['CodeMirror-linenumbers', 'CodeMirror-foldgutter']
                            }, options || {});

                            tools.attrs(options);
                        }
                        break;
                    case 3:
                        if (hasGutters) {
                            options = $.extend({
                                foldGutter: false,
                                lineNumbers: false,
                                gutters: []
                            }, options || {});

                            tools.attrs(options);
                        }
                        break;
                    case 4:
                        break;
                }

                return (tools.attrs('gutters') || []).length > 0;
            },
            fullscreenTgl: function (full) {
                if (!cm.getOption('fullScreen')) {
                    tools.fullBefore = {
                        lineNumbers: cm.getOption('lineNumbers'),
                        styleActiveLine: cm.getOption('styleActiveLine')
                    };
                }

                var isFullscreen = null;
                cm.setOption("fullScreen", !full ? !cm.getOption("fullScreen") : full);
                if (cm.getOption('fullScreen')) {
                    cm.setOption('lineNumbers', true);
                    cm.setOption('styleActiveLine', true);
                    isFullscreen = true;
                } else {
                    $.each(tools.fullBefore, function (k, v) {
                        cm.setOption(k, v);
                    });
                    isFullscreen = false;
                }
                $.isFunction(events.fullscreen) && events.fullscreen(isFullscreen);
            },
            checkIsJson: function (text) {
                if (!mirror.isJson(text || tools.selected())) {
                    alert('The selected or content is not a valid json.');
                    return false;
                }

                return true;
            },
            isJson: function(noneLogWarnMsg) {
                return mirror.isJson(cm.getValue(), noneLogWarnMsg);
            },
            format: function () {
                var cursor = cm.getCursor();
                if (tools.isJson()) {
                    cm.setValue(fmtjson(cm.getValue()));
                } else {
                    return;
                }
                cm.setCursor(cursor);
                tools.refreshDelay();
            },
            getNotifyContent: function (customData, evtName) {
                var evtN = evtName || 'MIRROR_INPUT_READ_NOTIFY', cMode = tools.mode(), mirrorData = {
                    event: evtN,
                    data: {
                        lang: {
                            name: cMode.name,
                            mime: cMode.chosenMimeOrExt || cMode.mime
                        },
                        th: tools.theme(),
                        content: tools.val()
                    }
                };

                mirrorData.data =  $.extend(customData || {}, mirrorData.data);
                return mirrorData;
            },
            setNotifyContentHandle: function (handle) {
                inputReadNotifyEvtHandle = handle;
            },
            notifyContent: function (customData, evtName) {
                $.isFunction(inputReadNotifyEvtHandle)
                    && inputReadNotifyEvtHandle(tools.getNotifyContent(customData, evtName));
            },
            inputReadNotifyTgl: function (opt) {
                return tools.tglOption(inputReadNotifyEvt, opt);
            },
            readonlyTgl: function (isNocursor) {
                if (!isNocursor) {
                    return tools.tglOption('readOnly');
                } else {
                    return tools.attrs('readOnly', 'nocursor');
                }
            },
            chgFontSize: function (size) {
                tools.chgStyle({
                    'font-size': size + 'px'
                });
            },
            chgStyle: function (styles) {
                $.each(styles || {}, function (k, v) {
                    cm.getWrapperElement().style[k] = v;
                });
                tools.refreshDelay();
            },
            setSize: function(width, height) {
                cm.setSize(width, height);
            },
            val: function(data) {
                if (!data) {
                    return cm.getValue();
                }

                cm.setValue(data || '');
                tools.refreshDelay();
                return data;
            },
            refreshDelay: function(wait) {
                delay(function () {
                    cm.refresh();
                }, wait || 100);
            }
        };

        events = $.extend({
            inputRead: function(cm, changeObj) {
                tools.format();
            },
            keyHandled: function (cm, keyName, event) {

            }
        }, events);

        $.each(events, function (k, v) {
            //CodeMirror event
            if (customEvts.indexOf(k) === -1 && $.isFunction(v)) {
                if ('keyHandled' === k) {
                    cm.on(k, function (cm, keyName, event) {
                        v(cm, keyName, event);
                        if ('Backspace' === keyName && tools.attrs(inputReadNotifyEvt)) {
                            tools.notifyContent();
                        }
                    });
                } else if ('inputRead' === k) {
                    cm.on(k, function (cm, changeObj) {
                        v(cm, changeObj);
                        if (tools.attrs(inputReadNotifyEvt)) {
                            tools.notifyContent();
                        }
                    });
                } else {
                    cm.on(k, v);
                }
            }
        });

        /* [
            "selectAll", "singleSelection", "killLine", "deleteLine", "delLineLeft", "delWrappedLineLeft", "delWrappedLineRight",
            "undo", "redo", "undoSelection", "redoSelection", "goDocStart", "goDocEnd", "goLineStart", "goLineStartSmart",
            "goLineEnd", "goLineRight", "goLineLeft", "goLineLeftSmart", "goLineUp", "goLineDown", "goPageUp", "goPageDown",
            "goCharLeft", "goCharRight", "goColumnLeft", "goColumnRight", "goWordLeft", "goGroupRight", "goGroupLeft", "goWordRight",
            "delCharBefore", "delCharAfter", "delWordBefore", "delWordAfter", "delGroupBefore", "delGroupAfter", "indentAuto",
            "indentMore", "indentLess", "insertTab", "insertSoftTab", "defaultTab", "transposeChars", "newlineAndIndent",
            "openLine", "toggleOverwrite", "toggleComment", "closeTag", "newlineAndIndentContinueMarkdownList", "toMatchingTag",
            "toggleFold", "fold", "unfold", "foldAll", "unfoldAll", "autocomplete", "jumpToLine", "find", "findPersistent",
            "findPersistentNext", "findPersistentPrev", "findNext", "findPrev", "clearSearch", "replace", "replaceAll",
            "wrapLines", "goNextDiff", "goPrevDiff"
          ] */
        tools.cmds = [];
        $.each(CodeMirror.commands, function (k, v) {
            var commandM = {};
            commandM[k] = function () {
                return tools.handleCmd(k);
            };
            $.extend(tools, commandM);
            tools.cmds.push(k);
        });

        return tools;
    };

    /**
     *
     * @param elId
     * @param options
     * @param events {
     *      fullscreen: function(isFullscreen) {}
     * }
     * @see http://codemirror.net/doc/manual.html#events
     */
    var mirror = function (elId, options, events) {
        options = options || {}; events = events || {};

        var custOptions = {};
        if (false === options.cust) {

        } else {
            //default order (Shift-Cmd-Ctrl-Alt)
            custOptions = $.extend({
                escKey: true,          //full screen toggle
                ctrlLKey: true         //gutters toggle
            }, options.cust || {});
        }
        delete options.cust;

        var extraKeys = {};
        if (false === options.extraKeys) {

        } else {
            extraKeys = $.extend({
                //http://codemirror.net/doc/manual.html#commands
                'Ctrl-K': 'toMatchingTag',
                'Ctrl-J': 'autocomplete',
                'Ctrl-Q': 'toggleFold'
            }, options.extraKeys || {});
        }
        delete options.extraKeys;

        var targetId = /^#/.test(elId) ? elId.substring(1) : elId;
        // http://codemirror.net/doc/manual.html#config
        var rich = CodeMirror.fromTextArea(document.getElementById(targetId), $.extend({
                autofocus: false,
                lineNumbers: false,
                matchBrackets: true,
                theme: 'lemon',
                styleActiveLine: false,
                readOnly: false,
                mode: "application/ld+json",
                autoCloseBrackets: true,
                autoCloseTags: true,
                lineWrapping: true,
                foldGutter: true,
                content: '',
                scrollbarStyle: 'null', //native
                gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"],
                matchTags: {
                    bothTags: true
                },
                extraKeys: extraKeys
            }, options)
        );

        var aHelp = helper(rich, events), custKeys = {};

        $.each(custOptions, function (ck) {
            if (true === custOptions[ck]) {
                var km = keyMappings[ck];
                if (typeof km.f === 'string') {
                    custKeys[km.k] = function (cm) {
                        return aHelp[km.f]();
                    };
                } else if ($.isFunction(km.f)) {
                    custKeys[km.k] = function (cm) {
                        return km.f(aHelp);
                    };
                }
            }
        });

        aHelp.mapkey(custKeys);
        return aHelp;
    };

    mirror.themes = themes;
    mirror.languages = languages;

    var htmpl = [
        '<textarea style="display: none;" id="hsrc_<%= id %>"></textarea>',
        '<div  id="hctx_<%= id %>" style="display: none;"><pre class="CodeMirror cm-s-<%= theme %>"></pre></div>'
    ].join(''), extra = {
        css: function (target) {
            var aPath = '/components/codemirror' + target;

            if (isThirdTheme(target)) {
                aPath = '/css/mirror' + target;
            }

            pi.css(pi.fullUrl(aPath));
        },

        script: function (target, callback) {
            pi.script(pi.fullUrl('/components/codemirror' + target), function (data) {
                $.isFunction(callback) && callback(data);
            });
        },

        highlight: function(target, mode, output) {
            CodeMirror.runMode(pi.query(target).value, mode, pi.query(output));
        },

        highlights: function(options) {
            options = $.extend({
                input: '',
                outputEl: '',           //alternative options.resultHandle
                beforeHandle: false,    //after highlight and before final result generate event, function(preEl) {}
                resultHandle: false,    //alternative options.outputEl, function (highlighted, modeInfo, theme) {}
                doneHandle: false,      //call when is done, function (highlighted, modeInfo, theme) {}
                mode: 'text',
                theme: 'default',

                rightTip: '',       //right corner tip
                inputIsEl: false,
                inputIsEncode: false,
                style: {
                    height: '100%',
                    margin: 0,
                    padding: '1rem',
                    'overflow-x': 'auto'
                },
                attrs: {},

                id: pi.uniqueId('highlighted')
            }, options || {});

            var content = options.input, hsrc = '#hsrc_' + options.id,
                hctx = '#hctx_' + options.id, hpre = hctx + ' pre';
            if (options.inputIsEl) {
                content = pi.query(content).value;
            }

            if (options.inputIsEncode) {
                content = pi.unsign(content);
            }

            options.theme = mirror.requireTheme(options.theme);
            $('body').append(tmpl(htmpl, options));
            $(hsrc).val(content);
            if (options.style) {
                $(hpre).css(options.style);
            }
            if (options.attrs) {
                $(hpre).css(options.attrs);
            }

            mirror.requireMode(options.mode, function (modeInfo) {
                mirror.highlight(hsrc, modeInfo.mime, hpre);
                if (options.rightTip) {
                    $(hpre).prepend(fmt('<p class="pull-right text-muted">{0}</p>', options.rightTip));
                }

                $.isFunction(options.beforeHandle) && options.beforeHandle(hpre);
                var theOutput = $(hctx).html();
                $(hsrc + ',' + hctx).remove();

                if ($.isFunction(options.resultHandle)) {
                    options.resultHandle(theOutput, modeInfo, options.theme);
                } else if (options.outputEl && $(options.outputEl).length) {
                    $(options.outputEl).html(theOutput);
                }

                $.isFunction(options.doneHandle) && options.doneHandle(theOutput, modeInfo, options.theme);
            });
        },

        requireMode: function (mode, callback) {
            var lang = langInfo(mode);
            if (null == lang) {
                console && console.warn('mirror.requireMode ignored. invalid mode ' + mode);
                return;
            }

            var reqM = function (aMode) {
                if (!CodeMirror.modes.hasOwnProperty(aMode)) {
                    CodeMirror.requireMode(aMode, function() {
                        $.isFunction(callback) && callback(lang);
                    });
                } else {
                    $.isFunction(callback) && callback(lang);
                }
            };

            if (!$.isFunction(CodeMirror.autoLoadMode)) {
                mirror.script('/addon/mode/loadmode.js', function () {
                    CodeMirror.modeURL = pi.fullUrl('/components/codemirror/mode/%N/%N.js');
                    reqM(lang.mode);
                });
            } else {
                reqM(lang.mode);
            }
        },
        isJson: function(target, noneLogWarnMsg) {
            try {
                if (!pi.isString(target)) {
                    target = JSON.stringify(target);
                }
                JSON.parse(target);
            } catch (e) {
                if (!noneLogWarnMsg) {
                    console && console.warn('mirror.isJson: ' + e.message);
                }
                return false;
            }
            return true;
        },

        parse: function (target) {
            if (!pi.isString(target)) {
                target = JSON.stringify(target);
            }
            return JSON.parse(target);
        },
        isBlackBGTheme: function (th) {
            var isBlackBG = false;
            $.each(blackBGMark, function (idx, bbg) {
                if (th.indexOf(bbg) !== -1) {
                    isBlackBG = true;
                    return false;
                }
            });

            return isBlackBG;
        }
    };

    mirror.helpers = helper;
    mirror.modeInfo = langInfo;
    mirror.requireTheme = loadTheme;

    exporter(extra, mirror);
    root[register] = mirror;

})(jQuery, window, 'mirror');