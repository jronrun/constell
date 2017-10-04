'use strict';

var mgr = {};

(function ($, root, register) {

    var isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);

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

    root.$$ = function (selector) {
        return $(pageable + ' ' + selector);
    };

    function decodes(target) {
        return JSON.parse(LZString.decompressFromEncodedURIComponent(target));
    }

    root.decodes = function (target) {
        return decodes(target);
    };

    root.fmt = function() {
        var s = arguments[0];
        for (var i = 0; i < arguments.length - 1; i++) {
            var reg = new RegExp("\\{" + i + "\\}", "gm");
            s = s.replace(reg, arguments[i + 1]);
        }

        return s;
    };

    function scrollable(selector, options) {
        $(selector || 'body').niceScroll($.extend({
            cursorcolor: 'grey',
            cursorwidth: '10px',
            cursorborder: '0px solid #000',
            scrollspeed: 50,
            autohidemode: true,
            hidecursordelay: 400,
            cursorfixedheight: false,
            cursorminheight: 20,
            enablekeyboard: true,
            horizrailenabled: true,
            bouncescroll: false,
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
            },

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
        scrollable: scrollable
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