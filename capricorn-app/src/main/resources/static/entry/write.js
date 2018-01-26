'use strict';

var write = {};
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

})(jQuery, window, write);

$(function () {
    window.aa = comm.previews({
        toggle: true,
        rail:false,
        tabHead:false,
        content: [
            {
                id: 'testa',
                title: 'test a',
                context: 'http://10.0.40.210:9060/swagger-ui.html#/'
            },{
                id: 'testb',
                title: 'test b',
                context: 'test b body'
            }
        ]});
});