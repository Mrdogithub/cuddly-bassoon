(function ($, sr) {
    // debouncing function from John Hann
    // http://unscriptable.com/index.php/2009/03/20/debouncing-javascript-methods/
    var debounce = function (func, threshold, execAsap) {
        var timeout;

        return function debounced() {
            var obj = this,
                args = arguments;

            function delayed() {
                if (!execAsap)
                    func.apply(obj, args);
                timeout = null;
            }

            if (timeout)
                clearTimeout(timeout);
            else if (execAsap)
                func.apply(obj, args);

            timeout = setTimeout(delayed, threshold || 100);
        };
    };

    // smartresize 
    jQuery.fn[sr] = function (fn) {
        return fn ? this.bind('resize', debounce(fn)) : this.trigger(sr);
    };

})(jQuery, 'smartresize');


// Sidebar
function init_sidebar() {

    // TODO: This is some kind of easy fix, maybe we can improve this
    $("#sidebar-menu").load("menu.html", function () {

        var CURRENT_URL = window.location.href.split('#')[0].split('?')[0],
            $BODY = $('body'),
            $MENU_TOGGLE = $('#menu_toggle'),
            $SIDEBAR_MENU = $('#sidebar-menu'),
            $SIDEBAR_FOOTER = $('.sidebar-footer'),
            $LEFT_COL = $('.left_col'),
            $RIGHT_COL = $('.right_col'),
            $NAV_MENU = $('.nav_menu'),
            $FOOTER = $('footer');


        var setContentHeight = function () {
            // reset height
            $RIGHT_COL.css('min-height', $(window).height());

            var bodyHeight = $BODY.outerHeight(),
                footerHeight = $BODY.hasClass('footer_fixed') ? -10 : $FOOTER.height(),
                leftColHeight = $LEFT_COL.eq(1).height() + $SIDEBAR_FOOTER.height(),
                contentHeight = bodyHeight < leftColHeight ? leftColHeight : bodyHeight;

            // normalize content
            contentHeight -= $NAV_MENU.height() + footerHeight;

            $RIGHT_COL.css('min-height', contentHeight);
        };

        $SIDEBAR_MENU.find('a').on('click', function (ev) {
            console.log('clicked - sidebar_menu');
            var $li = $(this).parent();

            if ($li.is('.active')) {
                $li.removeClass('active active-sm');
                $('ul:first', $li).slideUp(function () {
                    setContentHeight();
                });
            } else {
                // prevent closing menu if we are on child menu
                if (!$li.parent().is('.child_menu')) {
                    $SIDEBAR_MENU.find('li').removeClass('active active-sm');
                    $SIDEBAR_MENU.find('li ul').slideUp();
                } else {
                    if ($BODY.is(".nav-sm")) {
                        $SIDEBAR_MENU.find("li").removeClass("active active-sm");
                        $SIDEBAR_MENU.find("li ul").slideUp();
                    }
                }
                $li.addClass('active');

                $('ul:first', $li).slideDown(function () {
                    setContentHeight();
                });
            }
        });

        // toggle small or large menu 
        $MENU_TOGGLE.on('click', function () {
            console.log('clicked - menu toggle');

            if ($BODY.hasClass('nav-md')) {
                $SIDEBAR_MENU.find('li.active ul').hide();
                $SIDEBAR_MENU.find('li.active').addClass('active-sm').removeClass('active');
            } else {
                $SIDEBAR_MENU.find('li.active-sm ul').show();
                $SIDEBAR_MENU.find('li.active-sm').addClass('active').removeClass('active-sm');
            }

            $BODY.toggleClass('nav-md nav-sm');

            setContentHeight();

            $('.dataTable').each(function () {
                $(this).dataTable().fnDraw();
            });
        });

        // check active menu
        $SIDEBAR_MENU.find('a[href="' + CURRENT_URL + '"]').parent('li').addClass('current-page');

        $SIDEBAR_MENU.find('a').filter(function () {
            return this.href == CURRENT_URL;
        }).parent('li').addClass('current-page').parents('ul').slideDown(function () {
            setContentHeight();
        }).parent().addClass('active');

        // recompute content when resizing
        $(window).smartresize(function () {
            setContentHeight();
        });

        setContentHeight();

        // fixed sidebar
        if ($.fn.mCustomScrollbar) {
            $('.menu_fixed').mCustomScrollbar({
                autoHideScrollbar: true,
                theme: 'minimal',
                mouseWheel: {
                    preventDefault: true
                }
            });
        }
    })
};
// /Sidebar

function init_echartareapro() {

    if (typeof (echarts) === 'undefined') {
        return;
    }

    var echart_areapro = echarts.init(document.getElementById('echart_areapro'))
    
    var xAxisdatapro=[],seriesdatapro=[]

    $.when(
        $.ajax({
            type: "get",
            async: false,
            url: "http://10.20.118.28:9002/stock-static/province",
            dataType: "json",
            timeout: 1000,
            beforeSend: function (xhr) {
                echart_areapro.showLoading();
            },
            success: function (dataresult) {
                echart_areapro.hideLoading();
                console.log(dataresult.length)
                if (dataresult) {
                    for (var i = 0; i < dataresult.length; i++) {
                        xAxisdatapro.push(dataresult[i].key);
                        seriesdatapro.push(dataresult[i].count);
                    }
                }
                console.log(xAxisdatapro);
            },
            error: function (errorMsg) {
                console.log("zz data get failed");
            }
        })).done(function () {

        echart_areapro.hideLoading();
        console.log("done");
        
        echart_areapro.setOption({
            
                    backgroundColor: '#0f375f',
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'shadow'
                        }
                    },
                    legend: {
                        data: ['本省上市公司数量'],
                        textStyle: {
                            color: '#ccc'
                        }
                    },
                    xAxis: {
                        data: xAxisdatapro,
                        axisLine: {
                            lineStyle: {
                                color: '#ccc'
                            }
                        },
                        axisLabel:{
                          interval:0  
                        }
                    },
                    yAxis: {
                        splitLine: {show: false},
                        axisLine: {
                            lineStyle: {
                                color: '#ccc'
                            }
                        }
                    },
                    series: [
                   {
                        name: '本省上市公司数量',
                        type: 'bar',
                        barWidth: 10,
                        itemStyle: {
                            normal: {
                                label:{
                                    show: true,
                                    position: 'top',
                                    textStyle: {
                                                color:"#fcf8f8",
                                                fontStyle:"normal",
                                                fontWeight:"normal",
                                                fontSize:16
                                        }
                                },
                                barBorderRadius: 3,
                                color: new echarts.graphic.LinearGradient(
                                    0, 0, 0, 1,
                                    [
                                        {offset: 0, color: '#14c8d4'},
                                        {offset: 1, color: '#43eec6'}
                                    ]
                                )
                            }
                        },
                        data: seriesdatapro
                    }]
                       
            
        });        
    }).fail(function () {
        echart_areapro.hideLoading();
        alert("we failed to get the data for this echart");        
        console.log("we failed to get the data for this echart!");　　　　
    });
    

}



$(document).ready(function () {
    init_sidebar();
    init_echartareapro();
});