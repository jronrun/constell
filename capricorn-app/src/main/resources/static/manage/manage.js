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

$(function () {
    $.ajaxSetup({
        error: function (xhr, status, error) {
            switch (xhr.status) {
                case 400:
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
        }
    });
});

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

    function request(action, data, options) {
        options = options || {};
        return $.ajax($.extend({
            type: options.type || 'GET',
            async: true,
            url: action,
            data: data || {},
            headers: {
                'Content-Type': 'application/json'
            }
        }, options));
    }

    function put(action, data, options) {
        return request(action, data, $.extend(options || {}, {
            type: 'PUT'
        }));
    }

    function del(action, data, options) {
        return request(action, data, $.extend(options || {}, {
            type: 'DELETE'
        }));
    }

    function jsonp(action, data, options) {
        return request(action, data, $.extend(options || {}, {
            type: 'GET',
            async: false,
            dataType: 'jsonp'
        }));
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

    var pageable = '.containerli.pageable', pjax = function (url, container) {
        $.pjax({url: url, container: container || pageable});
    };

    pjax.reload = function (container) {
        pjax(getURI(), container);
    };

    $(document).pjax('a[data-pjax]', pageable);

    /**
     * <a data-pjax data-query="{{selector}}" data-queries="{{encode json data}}" href="{{href}}"> {{text}} </a>
     */
    $(document).on('pjax:beforeSend', function (event, xhr, options) {
        var target = event.relatedTarget, ds = target ? (target.dataset || {}) : {};

        var origin = location.origin;
        xhr.setRequestHeader('Referer-Source', location.href.replace(origin, ''));

        return true;
    });

    $(document).on('pjax:end', function (event) {

    });

    function getFormData(selector) {
        var indexed = {};
        $.map($(selector).serializeArray(), function (n) {
            var val = n['value'];
            if (val && val.length > 0) {
                indexed[n['name']] = val;
            }
        });

        return indexed;
    }

    $.extend(root, {
        $$: function (selector) {
            return $(pageable + ' ' + selector);
        },

        getFormData: function (selector) {
            return getFormData(selector);
        },

        decodes: function (target) {
            return decodes(target);
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

    function decodes(target) {
        return JSON.parse(LZString.decompressFromEncodedURIComponent(target));
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
                        on: 'hover'
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

    $.extend(register, {
        pjax: pjax,
        getURI: getURI,
        isMobile: isMobile,
        scrollable: scrollable,
        request: function (action, data, options) {
            return request(action, data, options);
        },
        put: function (action, data, options) {
            return put(action, data, options);
        },
        del: function (action, data, options) {
            return del(action, data, options);
        },
        jsonp: function (action, data, options) {
            return jsonp(action, data, options);
        },
        script: function (action, callback) {
            return script(action, callback)
        }
    });

    $(function () {
        core.initialize();
    });

})(jQuery, window, mgr);

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