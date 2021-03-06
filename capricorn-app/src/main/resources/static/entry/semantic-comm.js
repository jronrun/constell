'use strict';

/**
    dependency semantic-ui

    css
    .ui.fullscreen.modal {
        width: 100% !important;
        left: 0;
        height: 100% !important;
        border-radius: 0;
    }
    .ui.fullscreen.modal .content {
        padding: 0rem !important;
    }
    .modals.dimmer .ui.scrolling.fullscreen.modal {
        margin-top: 0rem !important;
        margin-bottom: 0rem !important;
    }
    template
    <script type="text/html" id="modal_tmpl">
        <div id="<%= id %>" class="ui <%= size %> modal">
            <% if (close) { %> <i class="close red icon"></i> <% } %>
            <% if (header.length > 0) { %>
                <div class="header"> <%= header %> </div>
            <% } %>
            <div class="content" id="<%= id + '-content' %>"> <%= content %> </div>
            <% if (buttons.length > 0) { %>
            <div class="actions">
                <% $.each(buttons, function(idx, btn) { %>
                    <% if (btn.html && btn.html.length > 0) { %>
                        <%= btn.html %>
                    <% } else { %>
                        <div class="ui button <%= btn.clazz %>" id="<%= btn.id %>"><%= btn.text %></div>
                    <% } %>
                <% }); %>
            </div>
            <% } %>
        </div>
    </script>
 */

var comm = {};
(function ($, root, register) {

    function bindBehavior(methods, result, componentName) {
        $.each(methods, function (i, method) {
                result[$.camelCase(method.replace(/ /g, '-'))] = function () {
                    return $(result.target)[componentName](method);
                }
            }
        );
    }

    var SETTER = {
        1: 'append',
        2: 'prepend',
        3: 'html'
    }, core = {
        /**
         * Preview in top window
         * @param text
         * @param callback                function(view, previewM) {}  view: iFrame instance, previewM: modal instance
         * @param domReadyCallbackIfUrl   function(view, previewM) {}  view: iFrame instance, previewM: modal instance
         * @param modalOptions
         * @param modalEvents
         * @returns {*}
         */
        preview: function (text, callback, domReadyCallbackIfUrl, modalOptions, modalEvents) {
            modalEvents = modalEvents || {};
            var originalOnVisible = modalEvents.onVisible,
                createViewInstance = null != text,
                previewModalId = 'preview-modal-' + pi.uniqueId(), contextId = sel(previewModalId, '-content');
            modalOptions = $.extend({}, modalOptions || {}, {
                size: 5,
                id: previewModalId
            });

            modalEvents = $.extend(modalEvents, {
                onVisible: function () {
                    if (isMarked(contextId)) {
                        return;
                    }

                    if (createViewInstance) {
                        var viewport = pi.viewport();

                        var view = iFrame.create({
                            frameborder: 0
                        }, contextId);

                        view.attr({
                            //style: 'background-color: white'
                            width: viewport.w,
                            height: viewport.h - 4
                        });

                        if (pi.isUrl(text)) {
                            view.openUrl(text, function() {
                                $.isFunction(domReadyCallbackIfUrl) && domReadyCallbackIfUrl(view, previewM);
                            });
                        } else {
                            text = (text || '').replace(/\\\//g, '/');
                            view.write(text);
                            if (view.docInited) {
                                view.doc.keydown(function(e){
                                    //esc key
                                    if (27 === e.keyCode) {
                                        previewM.hide();
                                    }
                                });
                            }
                        }
                    }

                    setMarked(contextId);
                    $.isFunction(originalOnVisible) && originalOnVisible();
                    $.isFunction(callback) && callback(view, previewM);
                }
            });

            var previewM = core.modal(modalOptions, modalEvents);
            previewM.show();
            return previewM;
        },

        previews: function (options, tabOptions, modalOptions, modalEvents) {
            // sidebar work only if one pusher class in page
            options = $.extend({
                popup: true,
                parent: 'body',     // content append to the given selector element if popup is false
                parentSet: 1,       // set content to parent given element, jQuery method: 1 append, 2 prepend, 3 html
                toggle: true,
                toggleId: '',
                tabActiveIdx: 0,
                tabHead: false,
                tail: false,
                content: [
                    {
                        id: '',
                        title: '',
                        context: '',
                        callback: null
                    }
                ]
            }, options || {});

            options.toggleId = '' === options.toggleId ? pi.uniqueId('tgl-head-') : options.toggleId;
            options.content = $.isArray(options.content) ? options.content : [options.content];

            var containerId = pi.uniqueId('previews-');
            $.each(options.content, function (idx, tab) {
                tab.id = tab.id || pi.uniqueId(containerId + '-tab-');
            });

            var result = {}, views = {}, innerOptions = {
                pushable: $('.page-struct').length < 1,
                containerId: containerId,
                headId: (containerId + '-head'),
                railId: (containerId + '-rail'),
                sidebarItem: (containerId + '-side'),
                tabHeadItem: (containerId + '-item'),
                tabBodyItem: (containerId + '-body')
            }, modalContent = tmpls('previews_tmpl', $.extend(innerOptions, options)),
                viewResize = function (tabId, aView) {
                    var $tab = $sel(tabId);
                    aView.attr({
                        //style: 'background-color: white'
                        width: $tab.width(),
                        height: $tab.height() - 4
                    });
                },
                refreshSize = function (heightOffset) {
                    var headH = $visible(innerOptions.headId).height() || 0,
                        viewport = pi.viewport(),
                        bodyH = viewport.h - headH - (heightOffset || 1);
                    $('.' + innerOptions.tabBodyItem).css({
                        margin: 0,
                        width: '100%',
                        height: bodyH,
                        overflow: 'hidden'
                    });

                    $.each(views, function (tabId, aView) {
                        viewResize(tabId, aView);
                    });
                },
                refreshTab = function () {
                    result.target = core.tab('.' + innerOptions.tabHeadItem, tabOptions || {});
                    refreshSize();
                },
                openFrame = function (aTab) {
                    var view = iFrame.create({
                        frameborder: 0
                    }, sel(aTab.id));

                    $sel(aTab.id).css({
                        padding: 0
                    });

                    viewResize(aTab.id, view);
                    view.openUrl(aTab.context, function() {
                        $.isFunction(aTab.callback) && aTab.callback(aTab.id, view, previewM);
                    });

                    views[aTab.id] = view;
                };

            var previewM = null, showCallback = function () {
                refreshTab();
                if (options.rail) {
                    $sel(innerOptions.railId).show();
                }

                if (options.toggle) {
                    $sel(options.toggleId).show();
                    $sel(options.toggleId).click(function () {
                        result.toggleHead(function () {
                            $sel(options.toggleId, '-icon').toggleClass('green',
                                $visible(innerOptions.headId).length);

                            if ($sel(options.toggleId, '-icon').hasClass('green')) {
                                if (options.rail) {
                                    result.showRail();
                                }
                            } else {
                                result.hideRail();
                            }
                        });
                    });
                }

                $.each(options.content, function (idx, target) {
                    if (pi.isUrl(target.context)) {
                        openFrame(target);
                    } else {
                        $.isFunction(target.callback) && target.callback(target.id, false, previewM);
                    }
                });
            };

            if (options.popup) {
                previewM = core.preview(null, function () {
                    showCallback();
                }, false, $.extend(modalOptions || {}, {
                    content: modalContent
                }), modalEvents);
            } else {
                $(options.parent)[SETTER[options.parentSet]](modalContent);
                showCallback();
            }

            var appendTab = function (defineTab) {
                if ($sel(defineTab.id).length) {
                    result.target.changeTab(defineTab.id);
                    return;
                }

                var define = {
                    tab: defineTab,
                    tabHeadItem: innerOptions.tabHeadItem,
                    tabBodyItem: innerOptions.tabBodyItem
                };

                tmpls('atab_head_tmpl', define, sel(innerOptions.headId));
                tmpls('atab_body_tmpl', define, sel(innerOptions.containerId));

                refreshTab();
                result.target.changeTab(defineTab.id);
                openFrame(defineTab);
            }, remTab = function (path) {
                var nextTab = $sel(innerOptions.tabBodyItem, ':not(.active):eq(0)', 2).data('tab');
                $sel(innerOptions.containerId, ' [data-tab=' + path + ']').remove();
                refreshTab();
                result.target.changeTab(nextTab);
            }, initSidebar = function (direct, sidebarOptions) {
                if (result[direct]) {
                    return result[direct];
                }

                sidebarOptions = $.extend(sidebarOptions || {}, {
                    context: (options.popup ? sel(previewM.elId, '-content') : options.parent)
                });

                if (options.toggle && ['right', 'top'].indexOf(direct) !== -1) {
                    var onVisible = sidebarOptions.onVisible, onHidden = sidebarOptions.onHidden;
                    sidebarOptions = $.extend(sidebarOptions, {
                        onVisible: function () {
                            pi.data(sel(innerOptions.headId), states());
                            result.hideRail();
                            $sel(options.toggleId).hide();
                            $.isFunction(onVisible) && onVisible();
                        },
                        onHidden: function () {
                            var beforeStates = pi.data(sel(innerOptions.headId));
                            if (beforeStates.rail) {
                                result.showRail();
                            }
                            if (beforeStates.toggle) {
                                $sel(options.toggleId).show();
                            }
                            $.isFunction(onHidden) && onHidden();
                        }
                    });
                }

                return (result[direct] =
                    core.sidebar(sel(innerOptions.sidebarItem, '.' + direct, 2), sidebarOptions));
            }, states = function () {
                return {
                    rail: $visible(innerOptions.railId).length,
                    toggle: $visible(innerOptions.toggleId).length,
                    tabHead: $visible(innerOptions.headId).length
                };
            }, control = function (id, suffix) {
                $.each(['toggle', 'show', 'hide'], function (idx, me) {
                    result[$.camelCase(fmt('{0}-{1}', me, suffix))] = function () {
                        $sel(id)[me]();
                    };
                });
            }, appendRail = function (options) {
                options = $.extend({
                    icon: '',
                    title: '',
                    onClick: null,
                    id: pi.uniqueId('rail-ctl-'),
                    color: 'green'
                }, options || {});
                tmpls('arail_tmpl', options, sel(innerOptions.railId, '-content'));

                if ($.isFunction(options.onClick)) {
                    $sel(options.id).click(function () {
                        options.onClick(result);
                    });
                }
            };

            $(window).resize(function () {
                refreshSize();
            });

            control(innerOptions.railId, 'rail');
            control(innerOptions.headId, 'head');
            control(options.toggleId, 'ctl');

            $.extend(result, {
                preview: previewM,
                addCloseRailCtl: function () {
                    appendRail({
                        icon: 'delete',
                        title: 'Close Current Tab',
                        color: 'red',
                        onClick: function (inst) {
                            inst.remTab(inst.curTab());
                        }
                    });
                },
                addRailCtl: function (options) {
                    appendRail(options);
                },
                curTab: function () {
                    return $sel(innerOptions.tabBodyItem, '.active', 2).data('tab');
                },
                addTab: function (defineTab) {
                    appendTab(defineTab);
                },
                remTab: function(path) {
                    remTab(path || result.curTab());
                },
                refreshHead: function (title, path) {
                    $sel(path || result.curTab(), '-th').html(title);
                },
                refreshTab: function (html, path) {
                    $sel(path || result.curTab()).html(html);
                },
                state: function () {
                    return states();
                },
                toggleHead: function (callback) {
                    $sel(innerOptions.headId).slideToggle(200, function () {
                        refreshSize();
                        $.isFunction(callback) && callback();
                    });
                },
                resize: function (heightOffset) {
                    refreshSize(heightOffset);
                },
                active: function (path) {
                    result.target.changeTab(path);
                }
            });

            if (innerOptions.pushable) {
                $.each(['left', 'right', 'top', 'bottom'], function (idx, direct) {
                    result[$.camelCase(fmt('init-{0}-sidebar', direct))] = function (sidebarOptions) {
                        initSidebar(direct, sidebarOptions);
                    };
                });
            }

            return result;
        },

        modal: function (options, events) {
            var sized = {0: '', 1: 'mini', 2: 'tiny', 3: 'small', 4: 'large', 5: 'fullscreen'},
                modalOptions = $.extend({
                    /*
                    https://semantic-ui.com/modules/modal.html#/settings
                    detachable: true,
                    autofocus: false,
                    observeChanges: false,
                    allowMultiple: false,
                    offset: 0,
                    transition: 'scale',
                    duration: 400,
                    queue: false
                     */
                    context: 'body',
                    closable: false,
                    keyboardShortcuts: false
                }, (options = options || {}).modal || {});

            options = $.extend({
                id: 'modal-' + pi.uniqueId(),
                close: false,
                cache: false,       //destroy on hidden if false
                size: 0,
                remote: '',         //content load by remote url
                header: '',
                content: '',
                buttons: [
                    /*
                    {
                        text: '',
                        clazz: '',
                        onClick: function(){}
                    },
                    {
                        html: ''
                    }
                     */
                ]
            }, options);

            delete options.modal;
            options.size = sized[options.size];

            var modalId = sel(options.id), target = null, noneExists;
            if (noneExists = !$(modalId).length) {
                //attach button id
                $.each(options.buttons || [], function (idx, btn) {
                    if (btn.text && btn.text.length > 0) {
                        btn.id = 'modal-btn-' + pi.uniqueId();
                    }
                });

                tmpls('modal_tmpl', options, 'body');
                events = $.extend({
                    /*
                    onShow: null,
                    onVisible: null,
                    onHide: null,
                    onHidden: null,
                    onApprove: null,
                    onDeny: null
                     */
                }, events || {});

                modalOptions = $.extend({
                    onShow: function () {events.onShow && events.onShow();},
                    onVisible: function () {events.onVisible && events.onVisible();},
                    onApprove: function () {events.onApprove && events.onApprove();},
                    onDeny: function () {events.onDeny && events.onDeny();},
                    onHide: function () {events.onHide && events.onHide();},
                    onHidden: function () {
                        if (!options.cache) {
                            $(modalId).remove();
                        }
                        events.onHidden && events.onHidden();
                    }
                }, modalOptions);

                target = $(modalId).modal(modalOptions);
            }

            var result = {elId: modalId, target: target, settings: modalOptions};
            bindBehavior(['show', 'hide', 'toggle', 'refresh', 'show dimmer', 'hide dimmer', 'hide others', 'hide all',
                'is active'], result, 'modal');

            result.attach = function (selector, action) {
                $(result.target).modal('attach events', selector, action);
            };
            result.destroy = function () {
                result.hide();
                $(result.target).remove();
            };

            if (noneExists) {
                //attach button event
                $.each(options.buttons || [], function (idx, btn) {
                    if (btn.id && $.isFunction(btn.onClick)) {
                        $sel(btn.id).click(function () {
                            btn.onClick({button: btn, modal: result});
                        });
                    }
                });
            }

            return result;
        },

        sidebar: function (selector, options) {
            options = $.extend({
                //more https://semantic-ui.com/modules/sidebar.html#/settings
                // push, overlay, scale down
                // Vertical-Only Animations (left, right): uncover, slide along, slide out
                transition: 'push',
                closable: false,
                dimPage: false
                /*
                context: 'body',
                exclusive: false,
                scrollLock: false,
                returnScroll: false,
                delaySetup: false,

                onVisible: null,
                onShow: null,
                onChange: null,
                onHide: null,
                onHidden: null
                 */
            }, options || {});

            var target = $(selector).sidebar(options),
                result = {target: target, settings: options};

            result.attach = function (selector, action) {
                $(result.target).sidebar('attach events', selector, action);
            };

            $.extend(result, {
                target: selector,
                refresh: function (html, callback) {
                    $(selector).empty().html(html);
                    $.isFunction(callback) && callback(selector);
                },
                resize: function (size) {
                    size = size || '50%';
                    $(selector).css(
                        ['left', 'right'].indexOf(result.getDirection()) !== -1 ? {width: size} : {height: size});
                    return result;
                }
            });

            bindBehavior(['toggle', 'show', 'is visible', 'is hidden', 'get transition event', 'pull page',
                'get direction', 'push page', 'hide'], result, 'sidebar');

            return result;
        },

        dropdown: function (selector, options) {
            options = $.extend({
                /*
                //https://semantic-ui.com/modules/dropdown.html#/settings

                onChange(value, text, $choice)
                onAdd(addedValue, addedText, $addedChoice)
                onRemove(removedValue, removedText, $removedChoice)
                onLabelCreate(value, text)
                onLabelRemove(value)
                onLabelSelect($selectedLabels)
                onNoResults(searchValue)
                onShow
                onHide
                */
            }, options || {});

            var target = $(selector).dropdown(options),
                result = {target: target, settings: options}, doDropdown = function (method, value) {
                    return $(result.target).dropdown(method, value);
                };

            $.extend(result, {
                //value or value array
                select: function (value) {
                    doDropdown('set selected', value);
                },
                removeSelected: function (value) {
                    doDropdown('remove selected', value);
                },
                setExactly: function (value) {
                    doDropdown('set exactly', value);
                },
                setText: function (text) {
                    doDropdown('set text', text);
                },
                setValue: function (value) {
                    doDropdown('set value', value);
                },
                //Returns DOM element that matches a given input value
                getItem: function (value) {
                    doDropdown('get item', value);
                },
                changeValues: function (values) {
                    doDropdown('change values', values);
                }
            });

            bindBehavior([
                'refresh', 'toggle', 'show', 'hide', 'clear', 'hide others', 'restore defaults',
                'restore default text', 'restore placeholder text', 'restore default value',
                'save defaults', 'get text', 'get value', 'bind touch events', 'bind mouse events',
                'set active', 'set visible', 'remove active', 'remove visible', 'is selection',
                'is animated', 'is visible', 'is hidden', 'get default text', 'get placeholder text'
            ], result, 'dropdown');

            return result;
        },

        tab: function (selector, options) {
            options = $.extend({
                /*
                //https://semantic-ui.com/modules/tab.html#/settings
                auto: false,
                // siblings, all
                deactivate: 'siblings',
                history: false,
                ignoreFirstLoad: false,
                // true, false, once
                evaluateScripts: 'once',
                alwaysRefresh: false,
                cache: true

                //parameters: tabPath, parameterArray, historyEvent
                onFirstLoad: null,
                //parameters: tabPath, parameterArray, historyEvent
                onLoad: null,
                //parameters: tabPath
                onRequest: null,
                //parameters: tabPath
                onVisible: null
                */
            }, options || {});

            var target = $(selector).tab(options),
                result = {target: target, settings: options};

            $.extend(result, {
                attach: function (selector, action) {
                    $(result.target).tab('attach events', selector, action);
                },
                //Changes tab to path
                changeTab: function (path) {
                    if (!path) {return;}
                    $(result.target).tab('change tab', path);
                }
            });

            bindBehavior(['get path', 'is tab'], result, 'tab');

            return result;
        },

        initialize: function () {

        }
    };

    exporter(core, register);

    $(function () {
        core.initialize();
    });

})(jQuery, window, comm);