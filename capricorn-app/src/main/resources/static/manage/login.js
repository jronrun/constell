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

            var info = JSON.parse(mgr.us($(el).data('login')));
            mgr.post(info.authorization, mgr.s(JSON.stringify(data))).fail(function (xhr) {
                warning('Oops...', mgr.failMsg(xhr));
                mgr.unloading(el);
            }).done(function (resp) {
                mgr.header(mgr.us(resp.result), 1);
                mgr.unloading(el);
                $.pjax.defaults.fragment = 'html';
                mgr.pjax(info.redirect, 'html');
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