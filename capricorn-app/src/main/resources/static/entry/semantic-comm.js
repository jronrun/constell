'use strict';

var comm = {};
(function ($, root, register) {

    var core = {
        preview: function (text, callback, domReadyCallbackIfUrl, modalOptions, modalEvents) {
            modalEvents = modalEvents || {};
            var originalOnVisible = modalEvents.onVisible;

            modalOptions = $.extend({}, modalOptions || {}, {
                size: 5
            });

            modalEvents = $.extend(modalEvents, {
                onVisible: function () {

                    $.isFunction(originalOnVisible) && originalOnVisible();
                }
            });

            var previewM = modal(modalOptions, modalEvents);
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

            var modalId = (/^#/.test(options.id) ? '' : '#') + options.id, target = null, noneExists = false;
            if (noneExists = !$(modalId).length) {
                //attach button id
                $.each(options.buttons || [], function (idx, btn) {
                    if (btn.text && btn.text.length > 0) {
                        btn.id = 'modal-btn-' + fiona.uniqueId();
                    }
                });

                $('body').append(tmpl($('#modal_tmpl').html(), options));

                events = $.extend(
                    {onShow: null, onVisible: null, onHide: null, onHidden: null, onApprove: null, onDeny: null}, events
                    || {});
                modalOptions = $.extend({
                    onShow: function () {
                        events.onShow && events.onShow();
                    },
                    onVisible: function () {
                        events.onVisible && events.onVisible();
                    },
                    onHide: function () {
                        events.onHide && events.onHide();
                    },
                    onHidden: function () {
                        if (!options.cache) {
                            $(modalId).remove();
                        }
                        events.onHidden && events.onHidden();
                    },
                    onApprove: function () {
                        events.onApprove && events.onApprove();
                    },
                    onDeny: function () {
                        events.onDeny && events.onDeny();
                    }
                }, modalOptions);

                target = $(modalId).modal(modalOptions);
            }

            var result = {elId: modalId, target: target, settings: modalOptions};
            $.each(['show', 'hide', 'toggle', 'refresh', 'show dimmer', 'hide dimmer', 'hide others', 'hide all',
                    'is active'],
                function (i, method) {
                    result[$.camelCase(method.replace(' ', '-'))] = function () {
                        return $(result.target).modal(method);
                    }
                }
            );

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
                        $((/^#/.test(btn.id) ? '' : '#') + btn.id).click(function () {
                            btn.onClick({button: btn, modal: result});
                        });
                    }
                });
            }

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