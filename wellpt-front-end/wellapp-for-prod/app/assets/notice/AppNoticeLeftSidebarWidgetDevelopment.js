define([ "constant", "commons", "server", "LeftSidebarWidgetDevelopment" ], function(constant, commons, server,
		LeftSidebarWidgetDevelopment) {
	// 页面组件二开基础
	var AppNoticeLeftSidebarWidgetDevelopment = function() {
		LeftSidebarWidgetDevelopment.apply(this, arguments);
	};
	// 接口方法
	commons.inherit(AppNoticeLeftSidebarWidgetDevelopment, LeftSidebarWidgetDevelopment, {
		// 组件初始化
		init : function() {
			var _self = this;
			var pageContainer = _self.getPageContainer();
			pageContainer.on("AppNotice.Change", function() {
				_self.getWidget().refreshBadge();
			});
		}
	});
	return AppNoticeLeftSidebarWidgetDevelopment;
});