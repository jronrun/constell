'use strict';

var write = {};
(function ($, root, register) {

    var persistKey = 'write_state', views = {};
    function writePreview(input, domReadyCallbackIfUrl, callback, viewMark, viewParams) {
        comm.preview(input, callback, domReadyCallbackIfUrl, {
            close: true
        }, {
            onVisible: function () {
                if (viewMark) {
                    views[viewMark] = {
                        show: true,
                        args: viewParams
                    };
                }
                $sel(core.menu.tid).hide();
            },
            onHidden: function () {
                if (viewMark) {
                    views[viewMark] = {
                        show: false
                    };
                }
                if (!core.menu.visible) {
                    $sel(core.menu.tid).show();
                }
            }
        });
    }

    function actionView(showType) {
        writePreview(pi.fullUrl('/show'), function (view) {
            delay(function () {
                var evt = redact.getNotifyContent();
                evt.data.shows = showType;
                view.tellEvent(evt.event, evt.data);
            }, 300);
        }, false, 'view', showType);
    }

    function leave() {
        persist(persistKey, core.state());
    }

    var directive = {}, menuIndex = {}, actions = {
        new: function (args, cm) {

        },
        edit: function (args, cm) {

        },
        del: function (args, cm) {

        },
        help: function (args, cm) {

        },
        theme: function (args) {
            core.menu.theme.change(args.get(0));
        },
        joinline: function (args) {
            var start = 0, end = undefined;
            if (args.args) {
                var len = args.args.length;
                if (1 === len) {
                    start = parseInt(args.args[0]) - 1;
                } else if (len > 1) {
                    start = parseInt(args.args[0]) - 1;
                    end = parseInt(args.args[1]) - 1;
                }
            }
            redact.vim.joinLine(start, end);
        },
        mode: function (args) {
            core.menu.lang.change(args.get(0), args.get(1));
        },
        wnew: function (args, cm) {

        },
        asnew: function (args, cm) {

        },
        open: function (args, cm) {

        },
        menu: function (args, cm) {

        },
        quit: function (args, cm) {

        },
        share: function (args, cm) {

        },
        shared: function (args, cm) {

        },
        view: function (args) {
            actionView(args.get(0));
        },
        live: function (args) {
            core.preview.toggle();
        },
        fullscreen: function (args, cm) {

        },
        info: function (args, cm) {

        },
        w: function (args, cm) {

        },
        ww: function (args, cm) {

        },
        wq: function (args, cm) {

        },
        save: function (args, cm) {

        },
        rich: function (args, cm) {

        },
        compare: function (args, cm) {

        }
    }, exNameKey = function (directiveName) {
        return fmt('directive.{0}.name', directiveName);
    }, exDescKey = function (directiveName) {
        return fmt('directive.{0}.desc', directiveName);
    }, ex = function (value, shortValue, handle, name, describe) {
        var aHandle = null;
        handle = handle || value;
        shortValue = shortValue || '';
        name = name || exNameKey(value);
        describe = describe || exDescKey(value);

        if (pi.isFunc(handle)) {
            aHandle = handle;
        } else {
            aHandle = function (args, cm) {
                var theHandle;
                if (pi.isFunc(theHandle = actions[handle])) {
                    theHandle(args, cm);
                } else if (pi.isFunc(theHandle = (redact[handle] || redact.vim[handle]))) {
                    theHandle();
                }
            }
        }

        var props = {
            value: value,
            short: shortValue,
            name: name,
            describe: describe,
            handle: aHandle
        };

        note.defineEx(props.value, props.handle, props.describe, props.short);
        directive[value] = props;
        return props;
    }, menu = function (aDirective, type, label, params) {
        var nameKey = '';

        if (pi.isString(aDirective)) {
            aDirective = ex(aDirective);
        }

        if (!pi.isBlank(aDirective)) {
            nameKey = exNameKey(aDirective.value);
        }

        var aMenu = {
            // type 1 normal, 2 only show in child window, 3 toggle, 4 separator, 5 empty
            id: pi.uniqueId('mi-'),
            type: type || 1,
            label: label || '',
            params: params || {},
            nameKey: nameKey,
            ex: aDirective
        };

        menuIndex[aMenu.id] = aMenu;
        return aMenu;
    };

    ex('new', 'n');
    ex('edit', 'e');
    ex('del', 'd');
    ex('help', 'h');
    ex('theme', 'th');
    ex('mode', 'm');
    ex('joinline', 'jo');
    ex('wnew', 'wn');
    ex('asnew', 'asn');
    ex('open', 'o');
    ex('menu');
    ex('quit', 'q');
    ex('share');
    ex('shared');
    ex('view', 'v');
    ex('fullscreen', 'full');
    ex('live');
    ex('info');
    ex('w');
    ex('ww');
    ex('wq');
    ex('save', 'sa');
    ex('rich');
    ex('compare', 'comp');
    ex('wrapword', 'wrap', 'wordwrap');
    ex('foldall', 'folda', 'foldAll');
    ex('unfoldall', 'unfolda', 'unfoldAll');
    ex('jump', false, 'jumpToLine');

    var menus = [
        // type 1 drop down, 2 link, 3 language, 4 theme
        {
            type: 1,
            name: '.file',
            /*
                search: {
                    name: '',
                    placeholder: ''
                },
                header: ''
             */
            items: [
                menu(directive.new),
                menu(directive.wnew),
                menu(directive.asnew),
                menu(directive.open),
                menu(null, 4),
                menu(directive.menu, 3),
                menu(directive.quit, 2),
                menu(directive.share),
                menu(directive.view),
                menu(directive.live, 3),
                menu(directive.fullscreen, 1, 'Ctrl-L'),
                menu(null, 4),
                menu(directive.info),
                menu(directive.w),
                menu(directive.ww),
                menu(directive.wq),
                menu(directive.save),
                menu(null, 5, null, 90)
            ]
        },
        {
            type: 1,
            name: '.edit',
            items: [
                menu('undo', 1, 'u'),
                menu('redo', 1, 'Ctrl-R'),
                menu(null, 4),
                menu('editMode', 1, 'i'),
                menu('visualMode', 1, 'Esc'),
                menu(directive.rich),
                menu(directive.compare),
                menu(null, 4),
                menu('autocomplete', 1, 'Ctrl-J'),
                menu(directive.wrapword, 3),
                menu('toggleComment', 3, 'Ctrl-/'),
                menu(directive.foldall),
                menu(directive.unfoldall),
                menu('toggleFold', 3, 'Ctrl-Q'),
                menu('toMatchingTag', 1, 'Ctrl-K'),
                menu(null, 4),
                menu('selectAll', 1, 'Ctrl-A'),
                menu('find', 1, 'Cmd-F'),
                menu('findNext', 1, 'Cmd-G'),
                menu('findPrev', 1, 'Shift-Cmd-G'),
                menu('replace', 1, 'Cmd-Alt-F'),
                menu('replaceAll', 1, 'Shift-Cmd-Alt-F'),
                menu(directive.jump, 1, 'Alt-G'),
                menu(null, 5, null, 90)
            ]
        },
        {
            type: 1,
            name: '.note',
            items: []
        },
        {
            type: 3,
            name: '.language',
            items: mirror.languages
        },
        {
            type: 4,
            name: '.theme',
            items: mirror.themes
        },
        {
            type: 2,
            name: '.help',
            handle: function () {

            }
        }
    ];

    var pvw = null, redact = null, rightIfr = null, nid = pi.uniqueId('note-'), core = {
        layout: {
            init: function () {
                var viewContent = '<textarea style="border: 0; width: 100%; height: 100%;" id="{0}"></textarea>';
                pvw = comm.previews({
                    popup: false,
                    toggle: false,
                    parentSet: 2,
                    content: {
                        context: fmt(viewContent, nid),
                        callback: function (elId) {
                            $sel(elId).css({
                                padding: 0
                            });
                            redact = note(nid);
                        }
                    }
                });

                core.menu.init();
                delay(function () {
                    core.preview.init();
                }, 300);

                delay(function () {
                    core.state(false, true);
                }, 500);

                $(window).resize(function (e) {
                    core.preview.resize();
                });

                //TODO rem
                window.aa=pvw;
                window.nn=redact;
                window.show=core;
            }  
        },
        preview: {
            init: function () {
                pvw.initRightSidebar({
                    transition: 'overlay',
                    onVisible: function () {
                        redact.setSize(pi.viewport().w / 2);
                    },
                    onHidden: function () {
                        redact.setSize(pi.viewport().w);
                    }
                });
                pvw.right.resize();

                $(redact.target.getWrapperElement()).on('touchstart mouseover', function () {
                    rightIfr.tellEvent('SCROLL_NOTIFY_STOP');
                    core.preview.syncTgl(2);
                });
            },
            resize: function () {
                if (rightIfr) {
                    rightIfr.attr({
                        width: $(pvw.right.target).width(),
                        height: $(pvw.right.target).height()
                    });
                }

                redact.setSize(pi.viewport().w / (pvw.right.isVisible() ? 2 : 1));
            },
            toggle: function () {
                var isVisible = !pvw.right.isVisible(), notifyTglOpt = 1;
                pvw.right.toggle();

                if (isVisible) {
                    notifyTglOpt = 2;
                    if (!rightIfr) {
                        var notifyHandle = function (evt) {
                            rightIfr.tellEvent(evt.event, evt.data);
                        };

                        rightIfr = iFrame.create({}, pvw.right.target);
                        core.preview.resize();
                        rightIfr.openUrl(pi.fullUrl('/show'));
                        redact.setNotifyContentHandle(notifyHandle);

                        delay(function () {
                            notifyHandle(redact.getNotifyContent());
                            core.preview.resize();
                            core.preview.syncTgl(2);
                        }, 300);
                    }
                } else {
                    notifyTglOpt = 3;
                    core.preview.syncTgl(3);
                }

                redact.inputReadNotifyTgl(notifyTglOpt);
            },
            modeChange: function (langInfo) {
                if ('Markdown' !== langInfo.name && core.preview.syncAble) {
                    core.preview.syncTgl(3);
                }
            },
            syncTgl: function (opt) {
                // only support for Markdown
                var cur = core.preview.syncAble;
                //opt 1 toggle, 2 open, 3 close, 4 get
                switch (opt || 1) {
                    case 2: if (true === cur) {return;} break;
                    case 3: if (true !== cur) {return;}break;
                    case 4: return cur;
                }

                var evtN = 'scroll', doSyncTgl = function () {
                    if (true === core.preview.syncAble) {
                        core.preview.syncAble = false;
                        redact.target.off(evtN, core.preview.sync);
                    } else {
                        if ('Markdown' === redact.mode().name) {
                            redact.target.on(evtN, core.preview.sync);
                            core.preview.syncAble = true;
                        }
                    }

                    rightIfr.tellEvent('SYNC_SCROLL', {sync: core.preview.syncAble});
                };

                if (pi.isFunc(core.preview.sync)) {
                    doSyncTgl();
                } else {
                    core.preview.script(function () {
                        core.preview.sync = _.debounce(function () {
                            rightIfr.tellEvent('SCROLL', redact.visibleLines());
                        }, 100, { maxWait: 100 });

                        doSyncTgl();
                    });
                }
            },
            script: function (callback) {
                if (core.preview.lloaded) {
                    callback();
                } else {
                    pi.script('/module/lodash.js', function () {
                        core.preview.lloaded = true;
                        callback();
                    });
                }
            }
        },
        menu: {
            tid: 'write-menu',  //trigger id
            visible: false,
            lang: {
                chosenMimeOrExt: null,
                init: function () {
                    core.menu.lang.filter();

                    liveClk('a[data-lang-info]', function (el) {
                        core.menu.lang.chosenMimeOrExt = pi.data(el, 'langInfo');
                    });
                },
                filter: function () {
                    liveClk(sel('menu-lang-qry'), function(el, evt) {
                        var langSel = 'div[data-lang]', val = $(evt.currentTarget).val(), blastSel = sel('lang-qry-body');
                        if (pi.isBlank(val)) {
                            blastReset(blastSel);
                            $(langSel).show();
                            return;
                        }

                        val = val.replace(/\+/g,'\\+');
                        var ids = [], regEx = new RegExp(val), ismatch = false;
                        $.each(mirror.languages, function(idx, lang) {
                            ismatch = false;
                            if (ismatch = regEx.test(lang.name)) {
                            } else if (ismatch = regEx.test(lang.mode)) {
                            } else if (ismatch = regEx.test(lang.mime)) {
                            }

                            if (!ismatch) {
                                $.each(lang.mimes || [], function(idx, mime) {
                                    if (ismatch) { return false; }
                                    if (ismatch = regEx.test(mime)) { }
                                });
                            }

                            if (!ismatch) {
                                $.each(lang.ext || [], function(idx, ext) {
                                    if (ismatch) { return false; }
                                    if (ismatch = regEx.test(ext)) { }
                                });
                            }

                            if (!ismatch) {
                                $.each(lang.alias || [], function(idx, alias) {
                                    if (ismatch) { return false; }
                                    if (ismatch = regEx.test(alias)) { }
                                });
                            }

                            if (!ismatch && lang.file) {
                                ismatch = lang.file.test(val);
                            }

                            if (ismatch) {
                                ids.push('#lang_' + lang.id);
                            }
                        });

                        $(langSel).hide();
                        $(ids.join(',')).show();

                        blast(val, blastSel);
                    }, 'keyup');
                },
                chosen: function (lang, mimeOrExt) {
                    var markP = 'mark_lang_ch_', blockP = 'block_lang_ch_', infoP = 'info_lang_ch_',
                        $old = $(fmt('a[id^={0}]:visible', markP));
                    if ($old.length) {
                        $old.hide();
                        $(fmt('i[id^={0}]:visible', infoP)).hide();

                        /*
                        $sel(blockP + pi.data($old, 'langId')).css({
                            'box-shadow': 'none'
                        });
                         */
                    }

                    $sel(markP + lang.id).show();
                    $sel(blockP + lang.id).css('box-shadow', '');
                    $sel(infoP + pi.sign(mimeOrExt || '')).show();
                },
                change: function (langName, mimeOrExt) {
                    try {
                        var curM = redact.mode(langName, mimeOrExt);
                        redact.tip(getMessage('write.mode.current', curM.name + ' ' + (curM.mime || '')))
                        core.menu.lang.chosen(curM, mimeOrExt);
                        core.preview.modeChange(curM);
                    } catch (e) {
                        redact.tip(e.message);
                    }
                }
            },
            theme: {
                chosen: function (th) {
                    var thP = 'th_ch_';
                    $(fmt('i[id^={0}]:visible', thP)).hide();
                    $sel(thP + pi.sign(th)).show();
                },
                change: function (th) {
                    try {
                        var curTh = redact.theme(th);
                        redact.tip(getMessage('write.theme.current', curTh));
                        core.menu.theme.chosen(curTh);
                    } catch (e) {
                        redact.tip(e.message);
                    }
                }
            },
            dd: {
                init: function (aMenu) {
                    comm.dropdown(sel(pi.sign(aMenu.name)), {
                        onChange: function(value, text, $choice) {
                            core.menu.dd.change(value, text, $choice);
                        },
                        onShow: function () {
                            var delayTime = 260, delayFunc = null;
                            switch (aMenu.type) {
                                case 3:
                                    delayTime = 360;
                                    delayFunc = function () {
                                        core.menu.lang.chosen(redact.mode(), core.menu.lang.chosenMimeOrExt);
                                    };
                                    break;
                                case 4:
                                    delayFunc = function () {
                                        core.menu.theme.chosen(redact.theme());
                                    };
                                    break;
                            }

                            core.menu.dd.whenShow(delayTime, delayFunc);
                        }
                    });
                },
                change: function (value, text, $choice) {
                    // type 1 drop down, 2 link, 3 language, 4 theme
                    var data = pi.data($choice);
                    switch (data.type) {
                        case 1:
                            var menuId = $choice.attr('id');
                            menuIndex[menuId].ex.handle(data.params);
                            break;
                        case 2:
                            break;
                        case 3:
                            delay(function () {
                                core.menu.lang.change(data.lang.name, core.menu.lang.chosenMimeOrExt);
                                core.menu.lang.chosenMimeOrExt = null;
                            }, 200);
                            break;
                        case 4:
                            core.menu.theme.change(data.theme);
                            break;
                    }
                },
                whenShow: function (delayTime, delayFunc) {
                    delay(function () {
                        $('.menu.transition.visible').css({
                            '-webkit-box-shadow': '0 6px 12px rgba(0,0,0,.175)',
                            'box-shadow': '0 6px 12px rgba(0, 0,0,.175)',
                            border: 0
                        });

                        $('.scrolling.menu:visible').each(function (idx, el) {
                            var scrollH = pi.viewport().h * 75 / 100;
                            $(el).css({
                                height: scrollH + 'px',
                                'max-height': scrollH + 'px'
                            });

                            if (!isMarked(el)) {
                                $(el).niceScroll({
                                    cursorcolor: 'grey'
                                });
                                setMarked(el);
                            }
                        });

                        $.isFunction(delayFunc) && delayFunc();
                    }, delayTime);
                }
            },
            init: function () {
                var $trigger = $sel(core.menu.tid), menuOptions = {
                    id: pi.uniqueId('menu-'),
                    menus: menus
                };

                pvw.initTopSidebar({
                    onVisible: function () {
                        core.menu.visible = true;
                        $trigger.hide();
                    },
                    onHidden: function () {
                        core.menu.visible = false;
                        $trigger.fadeIn(800);
                    }
                });

                pvw.top.refresh(tmpls('menu_tmpl', menuOptions));
                $(pvw.top.target).each(function () {
                    this.style.setProperty('height', $sel(menuOptions.id).height() + 'px', 'important');
                });

                core.menu.lang.init();

                $.each(menus, function (idx, aMenu) {
                    if ([1, 3, 4].indexOf(aMenu.type) !== -1) {
                        core.menu.dd.init(aMenu);
                    }
                });

                $trigger.click(pvw.top.toggle);
                $('.menu-logo').click(pvw.top.toggle);
            }
        },
        state: function (snapdata, persistLoad) {
            if (true === persistLoad) {
                snapdata = snapdata || persist(persistKey);
            }

            if (pi.isUndefined(snapdata)) {
                return {
                    view: views.view,
                    menu: core.menu.visible,
                    live: pvw.right.isVisible(),
                    mirror: {
                        data: redact.getNotifyContent().data,
                        opts: redact.attrs(['foldGutter','lineNumbers','gutters']),
                        vimInsert: 'vim-insert' === redact.attrs('keyMap')
                    }
                };
            }

            var m, mm; if (m = snapdata.mirror) {
                redact.attrs(m.opts || {});
                if ((mm = m.data).th) {
                    core.menu.theme.change(mm.th);
                }
                if (mm.lang) {
                    core.menu.lang.change(mm.lang.name, mm.lang.mime);
                }
                if (m.vimInsert) {
                    redact.vim.editMode();
                }

                redact.val(mm.content);
            }

            if (snapdata.menu && !core.menu.visible) {
                pvw.top.show();
            }
            if (!snapdata.menu && core.menu.visible) {
                pvw.top.hide();
            }

            if (snapdata.live && !pvw.right.isVisible()) {
                core.preview.toggle();
            }
            if (!snapdata.live && pvw.right.isVisible()) {
                core.preview.toggle();
            }

            if (snapdata.view && snapdata.view.show) {
                actionView(snapdata.view.args);
            }
        },
        initialize: function () {
            core.layout.init();

            iFrame.registers({
                SCROLL: function (evtName, evtData) {
                    //redact.toLine(evtData.line)
                    var lineHeight = parseFloat($(redact.target.getWrapperElement()).css('line-height'));
                    //redact.target.scrollTo(null, parseInt(evtData.line) * lineHeight);
                    $('.CodeMirror-scroll').stop(true).animate({
                        scrollTop: parseInt(evtData.line) * lineHeight
                    }, 100, 'linear');
                },
                SCROLL_NOTIFY_STOP: function () {
                    core.preview.syncTgl(3);
                }
            });

            pi.unload(function () {
                if (iFrame.isRootWin()) {
                    leave();
                }
            });
        }
    };

    $(function () {core.initialize();});

})(jQuery, window, write);