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
                        $('#toc').load('/manage/side-small-view', function () {
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
                        $(".colorlist li a").on("click", function (b) {
                            var c = $(this).attr("data-addClass");
                            b.preventDefault();
                            $(".navmenu").removeClass(a).addClass(c);
                            a = c;
                        });

                        $(".sidecolor li a").on("click", function (a) {
                            var c = $(this).attr("data-addClass");
                            a.preventDefault();
                            $(".sidemenu").removeClass(b).addClass(c);
                            $(".accordion").removeClass("inverted").addClass("inverted");
                            b = c;
                        });

                        $(".colorize-logo").popup({on: "click"});
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

    register.isMobile = isMobile;
    register.scrollable = scrollable;

    $(function () {
        core.initialize();
    });

})(jQuery, window, mgr);