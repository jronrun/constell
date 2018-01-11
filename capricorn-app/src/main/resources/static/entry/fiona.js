'use strict';

var fiona = {};
(function ($, root, register) {

    var lz = LZString;
    LZString = undefined;
    var theUniqueID = 0;

    var core = {
        sign: function (target) {
            if (typeof target !== 'string') {
                target = JSON.stringify(target);
            }
            return lz.compressToEncodedURIComponent(target);
        },

        unsign: function (target) {
            return lz.decompressFromEncodedURIComponent(target);
        },

        deepUnsign: function (val) {
            try {
                val = core.unsign(val);
            } catch (e) {
            }
            try {
                val = JSON.parse(val);
            } catch (e) {
            }
            return val;
        },

        uniqueId: function (prefix) {
            return (prefix || '') + (++theUniqueID);
        },

        request: function (action, data, options, headers, noneLayout) {
            options = options || {};
            headers = $.extend({
                'Content-Type': 'application/json'
            }, options.headers || {}, headers || {});

            if (true === noneLayout) {
                $.extend(headers, {'X-PJAX': true});
            }

            return $.ajax($.extend({
                type: 'GET',
                async: true,
                url: action,
                data: data || {}
            }, options, {
                headers: headers
            }));
        },

        jsonp: function (action, data, options, headers) {
            return core.request(action, data, $.extend(options || {}, {
                type: 'GET',
                async: false,
                dataType: 'jsonp'
            }), headers);
        },

        script: function (action, callback) {
            $.ajax({
                url: action,
                dataType: 'script',
                async: true,
                success: function (data, textStatus, jqXHR) {
                    callback && callback(data, textStatus, jqXHR);
                }
            });
        },

        enter: function (selector, options) {
            if ($.isFunction(options)) {
                options = {enter: options};
            }

            options = $.extend({
                enter: null,
                ctrlEnter: null,
                shiftEnter: null,
                preventDefault: true
            }, options || {});

            $(selector).keypress(function (event) {
                if (event.ctrlKey && event.which === 13 || event.which === 10) {
                    if (options.preventDefault) {
                        event.preventDefault();
                    }

                    $.isFunction(options.ctrlEnter) && options.ctrlEnter(event);
                } else if (event.shiftKey && event.which === 13 || event.which === 10) {
                    if (options.preventDefault) {
                        event.preventDefault();
                    }

                    $.isFunction(options.shiftEnter) && options.shiftEnter(event);
                } else if (event.keyCode === 13) {
                    if (options.preventDefault) {
                        event.preventDefault();
                    }

                    $.isFunction(options.enter) && options.enter(event);
                }
            });
        },

        deepCopy: function (originalTarget) {
            return $.extend(true, {}, originalTarget);
        },

        escape: function (target) {
            return $('<div>').text(target).html();
        },

        unescape: function (target) {
            return $('<textarea/>').html(target).text();
        },

        /**
         * Opposite of jQuery.camelCase
         * @param target
         * @returns {*}
         */
        deCase: function (target) {
            return target.replace(/[A-Z]/g, function (a) {
                return '-' + a.toLowerCase()
            });
        },

        cloneHtml: function (selector) {
            return $('<div>').append($(selector).clone()).html();
        },

        data: function (selector, target) {
            var prefix = 'data-';

            //get all
            if (undefined === target) {
                var $el = $(selector), r = $el.data() || {}, rdata = {};
                $.each(r, function (k) {
                    rdata[k] = core.deepUnsign($el.attr(prefix + deCase(k)));
                });

                return rdata;
            }

            //get
            if (typeof target === 'string') {
                return core.deepUnsign($(selector).attr(prefix + deCase(target)));
            } else if ($.isArray(target)) {
                var ar = {};
                $.each(target, function (idx, tkey) {
                    ar[tkey] = core.deepUnsign($(selector).attr(prefix + deCase(tkey)));
                });

                return ar;
            }

            //set
            var theData = {};
            $.each(target, function (k, v) {
                theData[prefix + deCase(k)] = core.sign(v);
            });
            $(selector).attr(theData);
        },

        getURI: function () {
            return location.href.replace(location.origin, '');
        },

        initialize: function () {

        }
    };

    $.each(['post', 'put', 'delete', 'get'], function (i, method) {
        register[method] = function (action, data, options, headers, noneLayout) {
            return core.request(action, data, $.extend(options || {}, {
                type: method
            }), headers, noneLayout);
        }
    });

    var handles = {}, base = {
        // https://stackoverflow.com/questions/6393943/convert-javascript-string-in-dot-notation-into-an-object-reference
        getset: function (target, path, value) {
            path = path || '';
            var isWrite = value !== undefined;
            if (typeof path === 'string') {
                return getset(target, path.split('.'), value);
            } else if (path.length === 1 && isWrite) {
                return target[path[0]] = value;
            } else if (path.length === 0) {
                return target;
            } else {
                var key = path[0];
                if (isWrite) {
                    target[key] = target[key] || {};
                }
                return getset(target[key] || {}, path.slice(1), value);
            }
        },

        getFormData: function (selector, isGetEmptyField) {
            var indexed = {};
            $.map($(selector).serializeArray(), function (n) {
                var key = n['name'], val = n['value'];
                if (isGetEmptyField) {
                    getset(indexed, key, val);
                } else {
                    if (val && val.length > 0) {
                        getset(indexed, key, val);
                    }
                }
            });

            $.map($(selector + ' input[type="checkbox"]:checked'), function (el) {
                getset(indexed, $(el).attr('name'), 'true');
            });

            return indexed;
        },

        liveClk: function (selector, callback, event) {
            event = event || 'click';
            var handleId = selector + '-' + event;
            if (!handles[handleId]) {
                $('body').on(event, selector, function (evt) {
                    var el = evt.currentTarget;
                    callback && callback(el, evt);
                });

                handles[handleId] = true;
            }
        },
        delay: function (func, wait) {
            var args = Array.prototype.slice.call(arguments, 2);
            return setTimeout((function () {
                return func.apply(null, args);
            }), wait);
        },
        fmt: function () {
            var s = arguments[0];
            for (var i = 0; i < arguments.length - 1; i++) {
                var reg = new RegExp("\\{" + i + "\\}", "gm");
                s = s.replace(reg, arguments[i + 1]);
            }

            return s;
        },
        exports: function (target, host) {
            $.each(target, function (k, v) {
                if ($.isFunction(v)) {
                    host[k] = function () {
                        return target[k].apply(this, arguments);
                    };
                }
            });
        }
    };

    base.exports(base, root);
    base.exports(core, register);

    $(function () {
        core.initialize();
    });

})(jQuery, window, fiona);

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