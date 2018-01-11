'use strict';

var iFrame = {};
(function ($, register) {

    var ifrUniqueId = 100000, encode = function (target) {
        return JSON.stringify(target || {});
    }, decode = function (target) {
        return JSON.parse(target || '{}');
    };

    function uniqueId() {
        return (++ifrUniqueId);
    }

    var core = {

        isRootWin: function(targetWin) {
            if (targetWin) {
                return window.top === targetWin;
            }

            return window.top === window.self;
        },
        setEncryption: function (encodeFunc, decodeFunc) {
            if ($.isFunction(encodeFunc)) {
                encode = encodeFunc;
            }
            if ($.isFunction(encodeFunc)) {
                decode = decodeFunc;
            }
        },
        wrap: function(target) {
            var iframe = null;
            if (typeof target === 'string') {
                var selector = /^#/.test(target) ? target : ('iframe[name="' + target + '"]');
                if ($(selector).length) {
                    iframe = $(selector)[0];
                } else {
                    var el;
                    if (parent && (el = parent.document.querySelector(selector))) {
                        iframe = el;
                    } else if (top && (el = top.document.querySelector(selector))) {
                        iframe = el;
                    }
                }
            } else {
                iframe = target || window.frameElement;
            }

            var ackCalls = function(eventId, ackCallback) {
                var rootW = core.isRootWin() ? window : top.window, varN = '__defineIframeACKer__';
                rootW[varN] = rootW[varN] || {};
                if (undefined === ackCallback) {
                    return rootW[varN][eventId];
                }

                if (null === ackCallback) {
                    delete rootW[varN][eventId];
                } else {
                    rootW[varN][eventId] = ackCallback;
                }
            };

            /**
             *
             * @param eventName
             * @param type     1 tell, 2 reply, 3 ack
             * @param data
             * @param sendFunction
             */
            var eventOn = function(eventName, type, data, sendFunction, ackCallback, eventId) {
                if (sendFunction && eventName && eventName.length > 0) {
                    eventId = eventId || (type + uniqueId() + '_' + Date.now());
                    if ($.isFunction(ackCallback)) {
                        ackCalls(eventId, ackCallback);
                    }

                    sendFunction({
                        id: eventId,
                        event: eventName,
                        type: type,
                        data: data || {}
                    });
                }
            }, meta = {
                srcIsUrl: false,
                iframe: iframe,
                isAvailable: function() {
                    return null != iframe;
                },
                getId: function() {
                    return meta.attr('id');
                },
                getName: function() {
                    return meta.attr('name');
                },
                attr: function(options) {
                    if (typeof options === 'string') {
                        return iframe.getAttribute(options);
                    }

                    $.each(options, function(k, v) {
                        if (null !== v) { iframe.setAttribute(k, v); }
                    });
                    return meta;
                },
                fit: function(selector, offset) {
                    offset = $.extend({
                        height: 0,
                        width: 0
                    }, offset || {});
                    meta.attr({
                        width: $(selector).width() + offset.width,
                        height: $(selector).height() + offset.height
                    });
                    return meta;
                },
                write: function(text) {
                    var target = meta.getDocument();
                    target.open();
                    target.write(text);
                    target.close();
                    return meta;
                },
                openUrl: function(url, onready) {
                    meta.attr({ src: url });
                    meta.srcIsUrl = true;

                    /*
                    $('#' + meta.getId()).load(function() {
                        $.isFunction(onready) && onready(meta);
                    });
                     */
                    $('#' + meta.getId()).on('load', function() {
                        $.isFunction(onready) && onready(meta);
                    });

                    return meta;
                },
                reload: function() {
                    meta.getDocument().location.reload(true);
                    return meta;
                },
                getDocument: function() {
                    return iframe.contentDocument || iframe.contentWindow.document;
                },
                post: function(data, origin, sender) {
                    if (data && sender) {
                        try {
                            data.iframe = meta.getInfo();
                            sender.postMessage(encode(data), origin || '*');
                        } catch (e) {
                            window.console && console.warn(e.message, 'iframe.post');
                        }
                    }
                },
                /**
                 * Post a message to this iframe, parent -> iFrame
                 * @param data
                 * @param origin
                 */
                tell: function(data, origin) {
                    meta.post(data, origin, iframe.contentWindow);
                },
                /**
                 * Publish a message to parent from this iFrame, iFrame -> parent
                 * @param data
                 * @param origin
                 */
                reply: function(data, origin) {
                    try {
                        var theRoot = iframe.contentWindow.parent,
                            target = theRoot.postMessage ? theRoot : (theRoot.document.postMessage ? theRoot.document : undefined);
                        meta.post(data, origin, target);
                    } catch (e) { /**/ }
                },
                tellEvent: function(eventName, data, ackCallback) {
                    eventOn(eventName, 1, data, meta.tell, ackCallback);
                },
                replyEvent: function(eventName, data, ackCallback) {
                    eventOn(eventName, 2, data, meta.reply, ackCallback);
                },
                listen: function(callback, once, aListener) {
                    if (aListener && aListener.postMessage) {
                        if ($.isFunction(callback)) {
                            var _cb = null;
                            _cb = function (e) {
                                if (once) {
                                    $(aListener).unbind('message', _cb);
                                }

                                var evtData = decode(e.originalEvent.data),
                                    ackFunc = ackCalls(evtData.id), ackFuncAvail = $.isFunction(ackFunc);

                                if (3 === evtData.type) {
                                    ackFuncAvail && ackFunc(evtData.data || {}, evtData);
                                    ackCalls(evtData.id, null);
                                } else if ([1, 2].indexOf(evtData.type) !== -1) {
                                    var ackData = callback(evtData, e) || {};
                                    if (ackFuncAvail) {
                                        //1 tell, 2 reply
                                        switch (evtData.type) {
                                            case 1: ackFunc = meta.reply; break;
                                            case 2:
                                                ackFunc = function (data, origin) {
                                                    var ackTell = core.wrap(evtData.iframe.id);
                                                    if (ackTell.isAvailable()) {
                                                        ackTell.tell(data, origin);
                                                    }
                                                };
                                                break;
                                        }
                                        eventOn(evtData.event, 3, ackData, ackFunc, false, evtData.id);
                                    }
                                } else {
                                    callback(evtData, e);
                                }
                            };

                            $(aListener).bind('message', _cb);
                        }
                    }
                },
                listenTell: function(callback, once) {
                    meta.listen(callback, once, iframe.contentWindow);
                },
                listenReply: function(callback, once) {
                    meta.listen(callback, once, window);
                },
                getInfo: function() {
                    return {
                        id: iframe.getAttribute("id"),
                        name: iframe.getAttribute("name"),
                        src: iframe.getAttribute('src')
                    };
                },
                $: function(selector) {
                    return $(selector, $('#' + meta.getId()).contents());
                }
            };

            return meta;
        },
        create: function(options, selector) {
            var uid = 'ifr_' + uniqueId();
            options = $.extend({
                id: uid,
                name: uid,
                align: null,
                allowfullscreen: null,
                frameborder: null,
                height: null,
                width: null,
                longdesc: null,
                marginheight: null,
                marginwidth: null,
                mozallowfullscreen: null,
                webkitallowfullscreen: null,
                referrerpolicy: null,
                scrolling: null,
                sandbox: null,
                seamless: null,
                src: null,
                srcdoc: null
            }, options || {});

            var iframe = document.createElement('iframe');
            var iframes = core.wrap(iframe);
            iframes.attr(options);
            $(selector || 'body').append(iframe);

            iframes.docInited = false;
            try {
                iframes.dom = $(window.frames[options.name].document).contents();
                iframes.doc = $(iframes.getDocument());
                iframes.docInited = true;
            } catch (e) {/**/}

            return iframes;
        },

        initialize: function () {
            
        }
    };

    $.extend(register, {
        create: function(options, selector) {
            return core.create(options, selector)
        },
        wrap: function(target) {
            return core.wrap(target);
        },
        isRootWin: function(targetWin) {
            return core.isRootWin(targetWin);
        },
        setEncryption: function (encodeFunc, decodeFunc) {
            return core.setEncryption(encodeFunc, decodeFunc);
        }
    });

    $(function () {
        core.initialize();
    });

})(jQuery, iFrame);