'use strict';

var write = {};
(function ($, root, register) {

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
                var $trigger = $sel('write-menu'), $icon = $trigger.children('i'), menuOptions = {
                    id: pi.uniqueId('menu-')
                };
                pvw.initTopSidebar({
                    onVisible: function () {
                        core.menu.visible = true;
                        $icon.hide();
                    },
                    onHidden: function () {
                        core.menu.visible = false;
                        $icon.show();
                    }
                });

                pvw.top.refresh(tmpls('menu_tmpl', menuOptions));
                $(pvw.top.target).each(function () {
                    this.style.setProperty('height', $sel(menuOptions.id).height() + 'px', 'important');
                });

                $trigger.click(function () {
                    pvw.top.toggle();
                });
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