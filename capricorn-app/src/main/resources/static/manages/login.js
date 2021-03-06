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

            var info = pi.deepUnsign($(el).data('login'));
            pi.post(info.authorization, pi.sign(data)).fail(function (xhr) {
                warning('Oops...', mgr.failMsg(xhr));
                mgr.unloading(el);
            }).done(function (resp) {
                mgr.header("token", resp.result);
                location.href = info.redirect;
            });
        },
        initialize: function () {
            var loginBtn = '[data-login]';
            pi.enter('[data-name=password]', function () {
                core.login(loginBtn);
            });

            $(loginBtn).click(function (evt) {
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