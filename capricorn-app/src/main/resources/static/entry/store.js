'use strict';

(function ($, root, register) {

    function storeData(key, value) {
        if (pi.isUndefined(value)) {
            return store.get(key);
        }
        if (pi.isNull(value)) {
            var v = store.get(key);
            store.remove(key);
            return v;
        }
        store.set(key, value);
        return value;
    }

    function storeSign(key, value) {
        var cur = pi.deepUnsign(storeData(key) || {});
        if (pi.isUndefined(value)) {
            return cur;
        }

        var v = $.extend({}, cur, value);
        storeData(key, pi.sign(v));
        return v;
    }

    root[register] = function (key, value) {
        return storeSign(key, value);
    };

    root[register].store = function (key, value) {
        return storeData(key, value);
    };

})(jQuery, window, 'persist');