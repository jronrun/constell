'use strict';

// John Resig - https://johnresig.com/ - MIT Licensed
(function () {
    var cache = {};

    this.tmpl = function tmpl(str, data) {
        // Figure out if we're getting a template, or if we need to
        // load the template - and be sure to cache the result.
        var fn = !/\W/.test(str) ? cache[str] = cache[str] ||
            tmpl(document.getElementById(str).innerHTML) : // Generate a reusable function that will serve as a template
            // generator (and which will be cached).
            new Function("obj",
                "var p=[],print=function(){p.push.apply(p,arguments);};" +

                // Introduce the data as local variables using with(){}
                "with(obj){p.push('" +

                // Convert the template into pure JavaScript
                str
                .replace(/[\r\t\n]/g, " ")
                .split("<%").join("\t")
                .replace(/((^|%>)[^\t]*)'/g, "$1\r")
                .replace(/\t=(.*?)%>/g, "',$1,'")
                .split("\t").join("');")
                .split("%>").join("p.push('")
                .split("\r").join("\\'")
                + "');}return p.join('');");

        // Provide some basic currying to the user
        return data ? fn(data) : fn;
    };
})();

Date.prototype.fmt = function (fmt) {
    fmt = fmt || 'yyyy-MM-dd HH:mm:ss';
    var o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "H+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S": this.getMilliseconds()
    };

    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }

    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1,
                (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }

    return fmt;
};

var mgr = {};

(function ($, root, register) {

    var isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);

    function request(action, data, options, headers) {
        options = options || {};
        headers = $.extend({
            'Content-Type': 'application/json'
        }, options.headers || {}, headers || {});

        return $.ajax($.extend({
            type: 'GET',
            async: true,
            url: action,
            data: data || {}
        }, options, {
            headers: headers
        }));
    }

    $.each(['post', 'put', 'delete', 'get'], function (i, method) {
        register[method] = function (action, data, options, headers) {
            return request(action, data, $.extend(options || {}, {
                type: method
            }), headers);
        }
    });

    function jsonp(action, data, options, headers) {
        return request(action, data, $.extend(options || {}, {
            type: 'GET',
            async: false,
            dataType: 'jsonp'
        }), headers);
    }

    function script(action, callback) {
        $.ajax({
            url: action,
            dataType: 'script',
            async: true,
            success: function (data, textStatus, jqXHR) {
                callback && callback(data, textStatus, jqXHR);
            }
        });
    }

    root.paceOptions = {
        restartOnRequestAfter: false
    };

    $(document).ajaxStart(function () {
        Pace.restart();
    });

    function getURI() {
        return location.href.replace(location.origin, '');
    }

    var onEndCalls = {};
    var pageable = '.containerli.pageable', pjax = function (url, headerParams, container) {
        var callId = 'call-' + mgr.uniqueId();
        headerParams = headerParams || {};
        if (headerParams.onEnd && $.isFunction(headerParams.onEnd)) {
            onEndCalls[callId] = headerParams.onEnd;
            delete headerParams.onEnd;
        }

        $.pjax({
            url: url,
            container: container || pageable,
            target: $.extend(headerParams, {
                callId: callId,
                directly_params: true
            })
        });
    };

    pjax.reload = function (container) {
        pjax(getURI(), container);
    };

    $(document).pjax('a[data-pjax]', pageable);

    var headers = {};

    function addHeader(name, value) {
        var aHeader = {};
        aHeader[name] = value;
        $.extend(headers, aHeader);
    }

    /**
     * <a data-pjax data-query="{{selector}}" data-queries="{{encode json data}}" href="{{href}}"> {{text}} </a>
     */
    $(document).on('pjax:beforeSend', function (event, xhr, options) {
        var target = event.relatedTarget, type = $(target).data('pjax');

        if ('menu' === type) {
            index && index.touchClear();
            var $prevEl = $('a[data-pjax=menu].blue'), $nextEl = $(target);
            $prevEl.css({color: ''});
            $prevEl.removeClass('blue');

            $nextEl.css({color: 'blue'});
            $nextEl.addClass('blue');
        }

        var innerHeaders = {};
        if (target.directly_params) {
            delete target.directly_params;
            $.extend(innerHeaders, target || {})
        } else {
            var ds = target ? (target.dataset || {}) : {};

        }

        var origin = location.origin;
        xhr.setRequestHeader('Referer-Source', location.href.replace(origin, ''));

        $.each($.extend({}, innerHeaders, headers), function (name, value) {
            xhr.setRequestHeader(name, value);
        });

        return true;
    });

    $(document).on('pjax:end', function (event) {
        var target = event.relatedTarget || {};
        if (target.callId) {
            onEndCalls[target.callId] && onEndCalls[target.callId](event);
        }
    });

    function getFormData(selector, isGetEmptyField) {
        var indexed = {};
        $.map($(selector).serializeArray(), function (n) {
            var key = n['name'], val = n['value'];
            if (isGetEmptyField) {
                indexed[key] = val;
            } else {
                if (val && val.length > 0) {
                    indexed[key] = val;
                }
            }
        });

        $.map($(selector + ' input[type="checkbox"]:checked'), function (el) {
            indexed[$(el).attr('name')] = 'true';
        });

        return indexed;
    }

    var lz = LZString;
    LZString = undefined;

    function sign(target) {
        return lz.compressToEncodedURIComponent(target);
    }

    function unSign(target) {
        return lz.decompressFromEncodedURIComponent(target);
    }

    var handles = {};
    function liveClk(selector, callback, event) {
        event = event || 'click';
        var handleId = selector + '-' + event;
        if (!handles[handleId]) {
            $('body').on(event, selector, function (evt) {
                var el = evt.currentTarget;
                callback && callback(el, evt);
            });

            handles[handleId] = true;
        }
    }

    $.extend(root, {
        liveClk: function (selector, callback, event) {
            return liveClk(selector, callback, event);
        },
        $$: function (selector) {
            return $(pageable + ' ' + selector);
        },

        delay: function (func, wait) {
            var args = Array.prototype.slice.call(arguments, 2);
            return setTimeout((function() {
                return func.apply(null, args);
            }), wait);
        },

        getFormData: function (selector, isGetEmptyField) {
            return getFormData(selector, isGetEmptyField);
        },

        fmt: function () {
            var s = arguments[0];
            for (var i = 0; i < arguments.length - 1; i++) {
                var reg = new RegExp("\\{" + i + "\\}", "gm");
                s = s.replace(reg, arguments[i + 1]);
            }

            return s;
        },

        getMessage: function () {
            var msg = messages[arguments[0]] || '';
            if (1 === arguments.length) {
                return msg;
            }

            var args = Array.prototype.slice.apply(arguments).slice(1);
            args.unshift(msg);
            return fmt.apply(this, args);
        }
    });

    $.each(['error', 'info', 'success', 'warning'], function (idx, type) {
        root[type] = function (title, msg, arg1, arg2, arg3) {
            swal(title || '', getMessage(msg, arg1, arg2, arg3) || msg, type);
        }
    });

    var load = 'loading';
    function loading(selector, clazz) {
        if ($(selector).hasClass(clazz || load)) {
            return false;
        }

        $(selector).addClass(clazz || load);
        return true;
    }

    function unloading(selector, clazz) {
        $(selector).removeClass(clazz || load);
    }

    function hasLoading(selector, clazz) {
        return $(selector).hasClass(clazz || load);
    }

    function scrollRefresh(selector) {
        $(selector || 'body').getNiceScroll().resize();
    }

    function scrollable(selector, options) {
        $(selector || 'body').niceScroll($.extend({
            cursorcolor: 'grey',
            cursorwidth: '8px',
            cursorborder: '0px solid #000',
            scrollspeed: 50,
            autohidemode: true,
            hidecursordelay: 400,
            cursorfixedheight: false,
            cursorminheight: 20,
            enablekeyboard: true,
            horizrailenabled: true,
            bouncescroll: true,
            smoothscroll: true,
            iframeautoresize: true,
            touchbehavior: false,
            zindex: 9999999
        }, options || {}));
    }

    function hamburger() {
        $('.hamburger').on('click', function () {
            var $toc = $('.toc');
            if ('show' === $(this).data('name')) {
                $toc.animate({
                    width: '80px'
                }, 350);
                $('#logo').animate({
                    width: '60px',
                    'margin-left': '0.6em'
                }, 350);

                $toc.load('/manage/thin-menu', function () {
                    $('.ui .dropdown').dropdown({
                        transition: 'fade up',
                        on: 'hover',
                        onChange: function(value, text, $choice) {
                            // $('[data-root-id].selected')  $('[data-root-id]:not(.selected)')
                            /*
                            var rootMenuId = $choice.data('rootId'), clazz = 'blue',
                                $prevRootId = $('[data-root-id].' + clazz), $nextRootId = $('[data-root-id=' + rootMenuId + ']'),
                                $prevRootIcon = $('[data-root-icon].' + clazz), $nextRootIcon = $('[data-root-icon=' + rootMenuId + ']');
                            $prevRootId.css({color: ''});
                            $prevRootId.removeClass(clazz);
                            $nextRootId.css({color: 'blue'});
                            $nextRootId.addClass(clazz);

                            $prevRootIcon.css({color: ''});
                            $prevRootIcon.removeClass(clazz);
                            $nextRootIcon.css({color: 'blue'});
                            $nextRootIcon.addClass(clazz);
                             */
                            var rootMenuId = $choice.data('rootId'), clazz = 'blue';
                            $('[data-root-id].' + clazz).removeClass(clazz);
                            $('[data-root-id=' + rootMenuId + ']').addClass(clazz);

                            $('[data-root-icon].' + clazz).removeClass(clazz);
                            $('[data-root-icon=' + rootMenuId + ']').addClass(clazz);
                        }
                    });

                    $('.logoImg').transition('jiggle');
                });

                $(this).data('name', 'hide');
            } else {
                $toc.animate({
                    width: '250px'
                }, 350);
                $('#logo').animate({
                    width: '195px',
                    'margin-left': '3.2em'
                }, 350);

                $toc.load('/manage/side-menu', function () {
                    $('.ui.accordion').accordion();
                    $('.logoImg').transition('tada');
                });
                $(this).data('name', 'show');
            }
        });
    }

    function modal(options, events) {
        var sized = { 0: '', 1: 'mini', 2: 'tiny', 3: 'small', 4: 'large', 5: 'fullscreen' },
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
            id: 'modal-' + mgr.uniqueId(),
            close: false,
            once: true,         //destroy on hidden if true
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
                    btn.id = 'modal-btn-' + mgr.uniqueId();
                }
            });

            $('body').append(tmpl($('#modal_tmpl').html(), options));

            events = $.extend({onShow: null, onVisible: null, onHide: null, onHidden: null, onApprove: null, onDeny: null}, events || {});
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
                    if (options.once) {
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

        var result = { elId: modalId, target: target, settings: modalOptions };
        $.each(['show', 'hide', 'toggle', 'refresh', 'show dimmer', 'hide dimmer', 'hide others', 'hide all', 'is active'],
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
    }

    var core = {

        menu: {
            side: {
                init: function () {
                    if (isMobile) {
                        $('#toc').load('/manage/side-menu-small', function () {
                            $('.ui.accordion').accordion();
                        });
                    } else {
                        $('.toc').load('/manage/side-menu', function () {
                            $('.ui.accordion').accordion();
                            scrollable('.sidemenu');
                        });
                    }
                }
            },

            nav: {
                init: function () {
                    if (isMobile) {
                        $('.mobilenavbar').load('/manage/navigation-small', function () {
                            $('#toc').sidebar('attach events', '.launch.button, .view-ui, .launch.item');
                            $('.ui.dropdown').dropdown({
                                allowCategorySelection: true
                            });
                        });
                    } else {
                        $('.navbarmenu').load('/manage/navigation', function () {
                            hamburger();
                            $('.ui.dropdown').dropdown();
                        });
                    }
                }
            }

        },

        foot: {
            init: function () {
                var a, b;
                $('.footer').load('/manage/footer', function () {
                    if (!isMobile) {
                        $('.colorlist li a').on('click', function (b) {
                            var c = $(this).attr('data-addClass');
                            b.preventDefault();
                            $('.navmenu').removeClass(a).addClass(c);
                            a = c;
                        });

                        $('.sidecolor li a').on('click', function (a) {
                            var c = $(this).attr('data-addClass');
                            a.preventDefault();
                            $('.sidemenu').removeClass(b).addClass(c);
                            $('.accordion').removeClass('inverted').addClass('inverted');
                            b = c;
                        });

                        $('.colorize-logo').popup({on: 'click'});
                    }
                });
            }
        },

        initialize: function () {
            core.menu.side.init();
            core.menu.nav.init();
            core.foot.init();
            scrollable();
        }
    };

    var theUniqueID = 0;
    $.extend(register, {
        pjax: pjax,
        getURI: getURI,
        isMobile: isMobile,
        scrollable: scrollable,
        scrollRefresh: scrollRefresh,
        failMsg: function (xhr, defaultMessage) {
            var resp = (xhr.responseJSON || {});
            return resp.message || resp.result || xhr.responseText || (defaultMessage || 'request fail');
        },
        request: function (action, data, options, headers) {
            return request(action, data, options, headers);
        },
        uniqueId: function(prefix) {
            return (prefix || '') + (++theUniqueID);
        },
        jsonp: function (action, data, options) {
            return jsonp(action, data, options);
        },
        script: function (action, callback) {
            return script(action, callback)
        },
        loading: function (selector, clazz) {
            return loading(selector, clazz);
        },
        unloading: function (selector, clazz) {
            unloading(selector, clazz);
        },
        hasLoading: function (selector, clazz) {
            return hasLoading(selector, clazz);
        },
        modal: function (options, events, callback) {
            return modal(options, events, callback);
        },
        header: function (name, value) {
            addHeader(name, value);
            return register;
        },
        s: function (target) {
            return sign(target);
        },
        us: function (target) {
            return unSign(target);
        }
    });

    $(function () {
        core.initialize();
    });

})(jQuery, window, mgr);

$(function () {

    function getTitleKey(code) {
        return fmt('render.code.{0}.title', code);
    }

    function getTextKey(code) {
        return fmt('render.code.{0}.text', code);
    }

    $(document).ajaxError(function( event, xhr, settings, thrownError ) {
        switch (xhr.status) {
            case 403:
                warning(getMessage(getTitleKey(403)), getMessage(getTextKey(403)));
                break;
            case 500:
                swal(
                    'Oops... <i style="font-size:15px">Something went wrong!</i>',
                    tmpl($('#err_tmpl').html(), {
                        item: xhr.responseJSON
                    }),
                    'error'
                );
                break;
        }
    }).ajaxComplete(function(/* event,request, settings */){
        mgr.scrollRefresh();
    });

});

/*
'use strict';

var index = {};
(function ($, root, register) {

    var core = {
        initialize: function () {

        }
    };

    $.extend(register, {
    });

    $(function () {
        core.initialize();
    });

})(jQuery, window, index);
 */