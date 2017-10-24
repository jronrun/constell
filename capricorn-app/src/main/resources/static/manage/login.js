'use strict';

var login = {};
(function ($, root, register) {

    var core = {
        login: function (el) {
            var data = {};
            $('[data-name]').each(function () {
                data[$(this).data('name')] = $(this).val();
            });

            if (!data.username || data.username.length < 1) {
                warning('', getMessage('render.account.assets.username'));
                return;
            }

            if (!data.password || data.password.length < 1) {
                warning('', getMessage('render.account.assets.password'));
                return;
            }

            if (!mgr.loading(el)) {
                return;
            }

            mgr.post('/capricorn/api/v1/user/login', mgr.s(JSON.stringify(data))).fail(function (xhr) {
                warning('Oops...', mgr.failMsg(xhr));
                mgr.unloading(el);
            }).done(function (resp) {
                alert(mgr.us(resp.result));
                mgr.unloading(el);
            });
        },
        initialize: function () {
            $('[data-login]').click(function (evt) {
                core.login(evt.currentTarget);
            });
        }
    };

    $.extend(register, {
    });

    $(function () {
        core.initialize();
    });

})(jQuery, window, login);