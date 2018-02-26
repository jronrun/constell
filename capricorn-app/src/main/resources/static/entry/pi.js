'use strict';

var pi = {};
(function ($, root, register) {

    var lz = LZString;
    LZString = undefined;
    var theUniqueID = 0, _ = {}, SELECT_TYPE = {
        1: '#',     //id
        2: '.'      //class
    }, MARKED = 'marked',
        definedTypes = [
            'Arguments', 'Array', 'Boolean', 'Date', 'Error', 'Function', 'Null',
            'Number', 'Object', 'RegExp', 'String', 'Undefined', 'global'
        ];

    _.capitalize = function(word) {
        return word.charAt(0).toUpperCase() + word.slice(1);
    };

    function dynamicMethod(delegate, methodN, methodBody, prefixN) {
        return (function(methodN) {
            return delegate[prefixN ? prefixN + _.capitalize(methodN) : methodN] = methodBody;
        })(methodN);
    }

    _.type = function(obj, full) {
        if (full == null) {
            full = false;
        }
        var aType = Object.prototype.toString.call(obj);
        if (full) {
            return aType;
        } else {
            return aType.slice(8, -1);
        }
    };

    function bindTypes(type) {
        return dynamicMethod(_, type, (function(obj) {
            return type === _.type(obj);
        }), 'is');
    }

    for (var j = 0, len1 = definedTypes.length; j < len1; j++) {
        bindTypes(definedTypes[j]);
    }

    _.isFunc = _.isFunction;
    _.isWin = _.isGlobal;

    _.isJson = function(obj) {
        return typeof obj === 'object' && _.isObject(obj) && !obj.length;
    };

    _.isEvent = function(obj) {
        return !_.isNull(obj) && !_.isUndefined(obj) && (!_.isUndefined(obj.altKey) || !_.isUndefined(obj.preventDefault));
    };

    _.prefix = function(target, length, fill) {
        return (Array(length).join(fill || '0') + target).slice(-length);
    };

    _.suffix = function(target, length, fill) {
        return target + Array(length + 1).join(fill || '0').slice(target.length);
    };

    _.trim = function(target, chars) {
        if (!_.isString(target)) {
            return target;
        }
        if (_.isUndefined(chars)) {
            return target.replace(/(^\s*)|(\s*$)/g, "");
        }
        return target.replace(new RegExp("(^(" + chars + ")*)|((" + chars + ")*$)", "gi"), "");
    };

    _.repeat = function(target, count) {
        return new Array(1 + count).join(target);
    };

    _.ltrim = function(target, chars) {
        if (!_.isString(target)) {
            return target;
        }
        return target.replace(new RegExp("(^" + (_.isBlank(chars) ? "\\s" : chars) + "*)"), "");
    };

    _.rtrim = function(target, chars) {
        if (!_.isString(target)) {
            return target;
        }
        return target.replace(new RegExp("(" + (_.isBlank(chars) ? "\\s" : chars) + "*$)"), "");
    };

    _.randomStr = function(length) {
        var str = '';
        length = length || 6;
        while (true) {
            str += Math.random().toString(36).substr(2);
            if (str.length >= length) {
                break;
            }
        }
        return str.substr(0, length);
    };

    _.keys = function(obj, precodition) {
        precodition = precodition || function () {
            return true;
        };
        if (_.isObject(obj) && Object.keys) {
            return Object.keys(obj);
        }

        var results1 = [];
        for (var k in obj) {
            if (!hasProp.call(obj, k)) {
                continue;
            }

            var v = obj[k];
            if (precodition(v, k)) {
                results1.push(k);
            }
        }

        return results1;
    };

    _.has = function(obj, target) {
        if (_.isBlank(obj)) {
            return false;
        } else if (_.isArray(obj)) {
            return indexOf.call(obj, target) >= 0;
        } else if (_.isObject(obj)) {
            return target in obj;
        } else {
            return Object.prototype.hasOwnProperty.call(obj, target);
        }
    };

    _.isBlank = function() {
        var len2, obj, q, v, valueAsBlank;
        obj = arguments[0]; valueAsBlank = 2 <= arguments.length ? slice.call(arguments, 1) : [];
        obj = _.isString(obj) ? _.trim(obj) : obj;
        if (valueAsBlank != null) {
            for (q = 0, len2 = valueAsBlank.length; q < len2; q++) {
                v = valueAsBlank[q];
                if (v === obj) {
                    return true;
                }
            }
        }

        if (_.isNull(obj) || _.isUndefined(obj)
            || (_.isString(obj) && obj.length < 1) || (_.isJson(obj) && (_.keys(obj)).length < 1)) {
            return true;
        }

        return false;
    };

    var core = {
        now: function() {
            return (Date.now || function() {
                return new Date().getTime();
            })();
        },
        sleep: function (milliseconds) {
            var i, start;
            start = core.now();
            i = -1;
            while (true) {
                i++;
                if ((core.now() - start) >= milliseconds) {
                    break;
                }
            }
        },
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

        css: function(style, styleId) {
            var link = document.createElement('link');
            link.setAttribute('rel', 'stylesheet');
            link.setAttribute('type', 'text/css');
            link.setAttribute('href', style);
            if (styleId) {
                link.setAttribute('id', styleId);
            }
            $('head').append(link);
        },

        querySelector: function(selector, isAll, context) {
            if (isAll == null) {
                isAll = false;
            }
            if (context == null) {
                context = document;
            }
            if (isAll) {
                return context.querySelectorAll(selector);
            } else {
                return context.querySelector(selector);
            }
        },

        query: function() {
            var doc, isAll, l, params, results, selector;
            params = 1 <= arguments.length ? [].slice.call(arguments, 0) : [];
            l = params.length;
            selector = '';
            isAll = false;
            switch (l) {
                case 1:
                case 2:
                case 3:
                    selector = params[0];
                    if (l === 2) {
                        if (typeof params[1] === 'boolean') {
                            isAll = params[1];
                        } else {
                            doc = params[1];
                        }
                    }
                    if (l === 3) {
                        doc = params[1];
                        isAll = params[2];
                    }
            }
            doc = doc || document;
            if (/^[A-Za-z0-9]+$/.test(selector)) {
                results = core.querySelector(selector, isAll, doc);
                if (results === null) {
                    selector = "[name=" + selector + "]";
                } else {
                    return results;
                }
            }
            return core.querySelector(selector, isAll, doc);
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

        clone: function(target) {
            if ($.isArray(target)) {
                return target.slice();
            } else {
                return $.extend(true, {}, target);
            }
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
                    rdata[k] = core.deepUnsign($el.attr(prefix + core.deCase(k)));
                });

                return rdata;
            }

            //get
            if (typeof target === 'string') {
                return core.deepUnsign($(selector).attr(prefix + core.deCase(target)));
            } else if ($.isArray(target)) {
                var ar = {};
                $.each(target, function (idx, tkey) {
                    ar[tkey] = core.deepUnsign($(selector).attr(prefix + core.deCase(tkey)));
                });

                return ar;
            }

            //set
            var theData = {};
            $.each(target, function (k, v) {
                theData[prefix + core.deCase(k)] = core.sign(v);
            });
            $(selector).attr(theData);
        },

        getURI: function () {
            return location.href.replace(location.origin, '');
        },
        isUrl: function(text) {
            return /^(http|ftp|https):\/\/[\w-]+(\.[\w-]+)+([\w.,@?^=%&amp;:\/~+#-]*[\w@?^=%&amp;\/~+#-])?/.test(text);
        },
        fullUrl: function(anURI) {
            return core.isUrl(anURI) ? anURI : (location.origin || '') + anURI;
        },

        viewport: function () {
            return {
                w: $(window).width(),
                h: $(window).height()
            };
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
        getMessage: function () {
            var msg = messages[arguments[0]] || '';
            if (1 === arguments.length) {
                return msg;
            }

            var args = Array.prototype.slice.apply(arguments).slice(1);
            args.unshift(msg);
            return fmt.apply(this, args);
        },
        $visible: function (elId, selectType) {
            return $sel(elId, ':visible', selectType);
        },
        $sel: function (elId, suffix, selectType) {
            return $(sel(elId, suffix, selectType));
        },
        sel: function (elId, suffix, selectType) {
            return fmt('{0}{1}{2}', (/^#/.test(elId) ? '' : SELECT_TYPE[selectType || 1]), elId, suffix || '');
        },
        tmpls: function (templateId, data, appendToSelector) {
            var aHtml = tmpl($sel(templateId).html(), data || {});
            if (appendToSelector) {
                $(appendToSelector).append(aHtml);
            }
            return aHtml
        },
        exporter: function (target, host) {
            $.each(target, function (k, v) {
                if ($.isFunction(v)) {
                    host[k] = function () {
                        return target[k].apply(this, arguments);
                    };
                }
            });
        },
        //opt 1 toggle, 2 mark, 3 unmark, 4 get
        attrTgl: function (selector, attrName, opt) {
            var markD = {};
            switch (opt = opt || 1) {
                case 1:
                    markD[attrName] = 1 === core.data(selector, attrName) ? 0 : 1;
                    break;
                case 2:
                    if (1 !== core.data(selector, attrName)) {
                        markD[attrName] = 1;
                    }
                    break;
                case 3:
                    if (1 === core.data(selector, attrName)) {
                        markD[attrName] = 0;
                    }
                    break;
            }

            if ([1, 2, 3].indexOf(opt) !== -1) {
                core.data(selector, markD);
            }

            return 1 === core.data(selector, attrName);
        },
        isMarked: function (selector) {
            return base.attrTgl(selector, MARKED, 4);
        },
        unMarked: function (selector) {
            return base.attrTgl(selector, MARKED, 3);
        },
        setMarked: function (selector) {
            return base.attrTgl(selector, MARKED, 2);
        }
    };

    base.exporter(base, root);
    base.exporter(_, register);
    base.exporter(core, register);

    $(function () {
        core.initialize();
    });

})(jQuery, window, pi);

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