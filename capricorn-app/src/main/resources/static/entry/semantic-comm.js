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

    var core = {
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
            var rootW = iFrame.isRootWin() ? window : top.window;
            return rootW.comm.previewInSelfWin(text, callback, domReadyCallbackIfUrl, modalOptions, modalEvents);
        },
        /**
         * Preview in current self window
         * @param text
         * @param callback                function(view, previewM) {}  view: iFrame instance, previewM: modal instance
         * @param domReadyCallbackIfUrl   function(view, previewM) {}  view: iFrame instance, previewM: modal instance
         * @param modalOptions
         * @param modalEvents
         * @returns {*}
         */
        previewInSelfWin: function (text, callback, domReadyCallbackIfUrl, modalOptions, modalEvents) {
            modalEvents = modalEvents || {};
            var originalOnVisible = modalEvents.onVisible, toggleable = 'toggleable',
                createViewInstance = null != text,
                previewModalId = 'preview-modal-' + fiona.uniqueId(), contextId = sel(previewModalId, '-content');
            modalOptions = $.extend({}, modalOptions || {}, {
                size: 5,
                id: previewModalId
            });

            modalEvents = $.extend(modalEvents, {
                onVisible: function () {
                    if (toggleable === fiona.data(contextId, toggleable)) {
                        return;
                    }

                    if (createViewInstance) {
                        var viewport = fiona.viewport();

                        var view = iFrame.create({
                            frameborder: 0
                        }, contextId);

                        view.attr({
                            //style: 'background-color: white'
                            width: viewport.w,
                            height: viewport.h - 4
                        });

                        if (fiona.isUrl(text)) {
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

                    fiona.data(contextId, {toggleable: toggleable});
                    $.isFunction(originalOnVisible) && originalOnVisible();
                    $.isFunction(callback) && callback(view, previewM);
                }
            });

            var previewM = core.modal(modalOptions, modalEvents);
            previewM.show();
            return previewM;
        },

        previews: function (text, callback, domReadyCallbackIfUrl, modalOptions, modalEvents) {
            var rootW = iFrame.isRootWin() ? window : top.window;
            return rootW.comm.previewsInSelfWin(text, callback, domReadyCallbackIfUrl, modalOptions, modalEvents);
        },
        previewsInSelfWin: function (options, tabOptions, modalOptions, modalEvents) {
            options = $.extend({
                toggle: true,
                toggleId: '',
                tabActiveIdx: 0,
                tabHead: false,
                content: [
                    {
                        id: '',
                        title: '',
                        context: '',
                        loadedCallbackIfUrl: null
                    }
                ]
            }, options || {});

            options.toggleId = '' === options.toggleId ? fiona.uniqueId('tgl-head-') : options.toggleId;
            options.content = $.isArray(options.content) ? options.content : [options.content];

            var result = {}, containerId = fiona.uniqueId('previews-'), innerOptions = {
                containerId: containerId,
                headId: (containerId + '-head'),
                tabHeadItem: (containerId + '-item'),
                tabBodyItem: (containerId + '-body')
            }, modalContent = tmpls('previews_tmpl', $.extend(innerOptions, options)),
                refreshSize = function (heightOffset) {
                    var headH = $sel(innerOptions.headId, ':visible').height() || 0,
                        viewport = fiona.viewport(),
                        bodyH = viewport.h - headH - (heightOffset || 1);
                    $('.' + innerOptions.tabBodyItem).css({
                        margin: 0,
                        width: '100%',
                        height: bodyH
                    });
                },
                refreshTab = function () {
                    result.target = core.tab('.' + innerOptions.tabHeadItem, tabOptions || {});
                    refreshSize();
                };

            core.previewInSelfWin(null, function () {
                refreshTab();

                if (options.toggle) {
                    $sel(options.toggleId).click(function () {
                        $sel(innerOptions.headId).slideToggle(200, function () {
                            refreshSize();
                            $sel(options.toggleId, '-icon').toggleClass('green',
                                $sel(innerOptions.headId, ':visible').length);
                        });
                    });
                }
            }, false, {
                content: modalContent
            });

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

                $sel(innerOptions.headId).append(tmpls('atab_head_tmpl', define));
                $sel(innerOptions.containerId).append(tmpls('atab_body_tmpl', define));

                refreshTab();
                result.target.changeTab(defineTab.id);
            }, remTab = function (path) {
                $sel(innerOptions.containerId, ' [data-tab=' + path + ']').remove();
                refreshTab();
                result.target.changeTab($sel(innerOptions.tabHeadItem, ':eq(0)').data('tab'));
            };

            $.extend(result, {
                addTab: function (defineTab) {
                    appendTab(defineTab);
                },
                remTab: function(path) {
                    remTab(path);
                },
                resize: function (heightOffset) {
                    refreshSize(heightOffset);
                },
                active: function (path) {
                    result.target.changeTab(path);
                }
            });
            
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
                    context: 'body',
                    transition: 'scale',
                    duration: 400,
                    queue: false
                     */
                    closable: false,
                    keyboardShortcuts: false
                }, (options = options || {}).modal || {});

            options = $.extend({
                id: 'modal-' + fiona.uniqueId(),
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
                        btn.id = 'modal-btn-' + fiona.uniqueId();
                    }
                });

                $('body').append(tmpls('modal_tmpl', options));

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
                        sel(btn.id).click(function () {
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

            bindBehavior(['toggle', 'show', 'is visible', 'is hidden', 'get transition event', 'pull page',
                'get direction', 'push page', 'hide'], result, 'sidebar');

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