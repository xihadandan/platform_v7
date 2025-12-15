define([ "jquery", "constant", "appContext" ], function($, constant, appContext) {
	// 设置底部始终在底部，但不会一直浮动在底部；
	if(appContext.isMobileApp()){
		
	}else {
		function computedHeight(){
			var height = document.documentElement.clientHeight;
			var headerHeight = $(".ui-wHeader").height();
			var footerHeight = $(".ui-wFooter").height();
			$("body").css("height", height+"px");
			$(".container-box").css("height",height-headerHeight-footerHeight+'px');
		}
		computedHeight();		
		window.addEventListener("resize",function(){
			computedHeight();
		})
	} 
})