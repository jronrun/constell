'use strict';

var write = {};
(function ($, root, register) {

    var directive = {}, menuIndex = {}, actions = {
        new: function (args, cm) {

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
        view: function (args, cm) {

        },
        live: function (args, cm) {

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
                } else if (pi.isFunc(theHandle = redact[handle])) {
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

        directive[value] = props;
        return props;
    }, menu = function (aDirective, type, label, params) {
        var nameKey = '';
        if (!pi.isBlank(aDirective)) {
            nameKey = exNameKey(pi.isString(aDirective) ? aDirective : aDirective.value);
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
    ex('wnew', 'wn');
    ex('asnew', 'asn');
    ex('open', 'o');
    ex('menu');
    ex('quit', 'q');
    ex('share');
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
    ex('foldall', 'folda');
    ex('unfoldall', 'unfolda');
    ex('jump', false, 'jumpToLine');

    var menus = [
        // type 1 drop down, 2 link
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
                menu(directive.live),
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
                menu(directive.wrapword),
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
            type: 2,
            name: '.help',
            handle: function () {

            }
        }
    ];

    var pvw = null, redact = null, nid = pi.uniqueId('note-'), core = {
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

                //TODO rem
                window.aa=pvw;
                window.nn=redact;
            }  
        },
        preview: {
            init: function () {
                pvw.initRightSidebar();
                pvw.right.resize();
            }
        },
        menu: {
            visible: false,
            init: function () {
                var $trigger = $sel('write-menu'), menuOptions = {
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

                comm.dropdown('.dropdown', {
                    onShow: function () {
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
                        }, 230);
                    }
                });

                $trigger.click(pvw.top.toggle);
                $('.menu-logo').click(pvw.top.toggle);
            }
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

})(jQuery, window, write);