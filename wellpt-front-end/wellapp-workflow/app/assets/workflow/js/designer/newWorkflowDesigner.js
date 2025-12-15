function launchFullscreen(element) {
    if(element.requestFullscreen) {
        element.requestFullscreen();
    } else if(element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
    } else if(element.webkitRequestFullscreen) {
        element.webkitRequestFullscreen();
    } else if(element.msRequestFullscreen) {
        element.msRequestFullscreen();
    }
}
function fullScreen () {
    if(!$('.left-view-mode').hasClass('pack')) {
        $('.left-view-mode').trigger('click');
    }
    if(!$('.right-view-mode').hasClass('pack')) {
        $('.right-view-mode').trigger('click');
    }
    launchFullscreen(document.documentElement);
}

function switchPropertyTab(tab) {
    $('#workFlowMainTab li[data-name="' + tab + '"] a').tab('show');
}

$('#workFlowMainTab a[data-toggle="tab"]').on('click', function (e) {
  var tabName = $(this).parent().siblings().data('name');
  temporarySaveProperty(false, tabName);
});

$(document).ready(function () {
    $('#workFlowExport').on('click',function () {
        var saveStatus = isSave();
        if(saveStatus) {
            var uuid = goWorkFlow.flowXML.attr('uuid');
            $.iexportData["export"]({
                uuid : uuid,
                type : 'flowDefinition'
            });
        }
    });
    $('.left-view-mode').on('click', function () {
        var $this = $(this);
        $this.toggleClass('pack');
        var ispack = $this.hasClass('pack');
        if (ispack) {
            $('.designer-body').addClass('left-simple-view');
        } else {
            $('.designer-body').removeClass('left-simple-view');
        }
        $("#designerLeft").getNiceScroll().resize();
    });

    $('.right-view-mode').on('click', function () {
        var $this = $(this);
        $this.toggleClass('pack');
        var ispack = $this.hasClass('pack');
        if (ispack) {
            $('.designer-body').addClass('right-hide');
        } else {
            $('.designer-body').removeClass('right-hide');
        }
    });

    $('#designerLeft').niceScroll({
        cursorcolor: "#ccc", //滚动条的颜色
        cursoropacitymax: 0.9, //滚动条的透明度，从0-1
        touchbehavior: false, //使光标拖动滚动像在台式电脑触摸设备 true滚动条拖动不可用
        cursorwidth: "6px", //滚动条的宽度  单位默认px
        cursorborder: "0", // 游标边框css定义
        cursorborderradius: "3px", //滚动条两头的圆角
        autohidemode: true, //是否隐藏滚动条  true的时候默认不显示滚动条，当鼠标经过的时候显示滚动条
        zindex: "auto", //给滚动条设置z-index值
        railvalign: 'defaul',
        railpadding: {
            top: 0,
            right: -6,
            left: 0,
            bottom: 0
        },
    });
});