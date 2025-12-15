define([ "constant", "commons", "server", "LeftSidebarWidgetDevelopment" ], function(constant, commons, server,
		LeftSidebarWidgetDevelopment) {
	// 页面组件二开基础
	var AppEmailLeftSidebarWidgetDevelopment = function() {
		LeftSidebarWidgetDevelopment.apply(this, arguments);
	};
	// 接口方法
	commons.inherit(AppEmailLeftSidebarWidgetDevelopment, LeftSidebarWidgetDevelopment, {
		// 组件初始化
		init : function() {
			var _self = this;
			var pageContainer = _self.getPageContainer();
			pageContainer.on("AppEmail.Change", function() {
				_self.getWidget().refreshBadge();
			});
			

            //监听子导航菜单项的点击回调，处理点击菜单后的逻辑
            _self.widget.on(constant.WIDGET_EVENT.LeftSidebarItemClick,function(event,data){

	        	

            });
			
            _self.widget.element.on('click',".subnav-menu-item",function(){
	        	if($('body').scrollTop()>=$(".subnav-menu-item:first").offset().top){
	        		$('body').animate({scrollTop: 0}, 300);
	        	}else if($('html').scrollTop()>=$(".subnav-menu-item:first").offset().top){
					$('html').animate({scrollTop: 0}, 300);
	        	}

            });

		}
	
	});
	return AppEmailLeftSidebarWidgetDevelopment;
});