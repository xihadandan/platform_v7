define([ "mui", "constant", "commons", "server", "LeftSidebarWidgetDevelopment" ], function($, constant, commons, server,
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
			_self.getWidget().on("tap", function(event, data) {
            	data = event.detail;
            });
			
            /*
            _self.widget.element.on('tap', "li.mui-table-view-cell", function(event) {
            });
			
			var widget = _self.getWidget();
			if(widget && widget.element[0].classList.contains("mail-nav-tag")) {
				var menuItems = _self.getWidget().getMenuItems();
				var menuCells = $(".mui-table-view-cell[menuid]", widget.element[0]);
				menuCells.each(function(idx, element){
					var menuid = element.getAttribute("menuid");
					var menuItem = menuItems[menuid];
					if(menuItem && menuItem.tagColor){
						element.style.color = menuItem.tagColor;
					}
				});
			}
			*/
		}
	
	});
	return AppEmailLeftSidebarWidgetDevelopment;
});