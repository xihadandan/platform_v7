define([ "constant", "commons", "server", "PanelWidgetDevelopment" ], function(constant, commons, server,
		PanelWidgetDevelopment) {
	// 平台组件_公共功能_页面初始化完成后选中左导航第一个菜单项_面板组件二开
	var SelectFirstMenuItemOfLeftSidebarPanelWidgetDevelopment = function() {
		PanelWidgetDevelopment.apply(this, arguments);
	};
	// 接口方法
	commons.inherit(SelectFirstMenuItemOfLeftSidebarPanelWidgetDevelopment, PanelWidgetDevelopment, {
		init : function() {
			var _self = this;
			_self.selected = false;
			var pageContainer = _self.getPageContainer();
			// 监听容器创建完成事件
			pageContainer.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function(e, ui) {
				if (!_self.selected) {
					var $menuItems = $("li.nav-menu-item", ".ui-wLeftSidebar");
					if ($menuItems.length > 0) {
						$($menuItems[0]).trigger("click").removeClass("nav-menu-active");
					}
				}
				_self.selected = true;
			});
		}
	});
	return SelectFirstMenuItemOfLeftSidebarPanelWidgetDevelopment;
});