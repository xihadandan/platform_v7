define(["jquery", "constant", "appContext"], function ($, constant, appContext) {
    // 设置底部始终在底部，但不会一直浮动在底部；
    if (appContext.isMobileApp()) {

    } else {
        /*function computedHeight(){
            var height = document.documentElement.clientHeight;
            var headerHeight = $(".ui-wHeader").height();
            if($(".ui-wNavbar").length!=0){
                var navHeight = $(".ui-wNavbar").height();
                headerHeight = headerHeight+navHeight;
            }
            var footerHeight;
            if($(".ui-wFooter").length !=0){
                footerHeight = $(".ui-wFooter").height();
                $('div.web-app-container').eq(0).css("padding-bottom",footerHeight+"px");
            }else{
                footerHeight = 0;
            }
            $("body").css("height", height+"px");
            $(".container-box").css("height",height-headerHeight-footerHeight+'px');
        }
        computedHeight();
        window.addEventListener("resize",function(){
            computedHeight();
        })*/
    }
})