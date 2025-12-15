define([ "constant", "commons", "server", "HeaderWidgetDevelopment" ], function(constant, commons, server,
		HeaderWidgetDevelopment) {
	// 页面组件二开基础
	var AppEmailHeaderWidgetDevelopment = function() {
		HeaderWidgetDevelopment.apply(this, arguments);
	};
	// 接口方法
	commons.inherit(AppEmailHeaderWidgetDevelopment, HeaderWidgetDevelopment, {
		// 组件初始化
		init : function() {
			var _self = this;
			var pageContainer = _self.getPageContainer();
			pageContainer.on("AppEmail.Change", function() {
				// _self.getWidget().trigger(constant.WIDGET_EVENT.BadgeRefresh);
				_self.getWidget().refreshBadge();
			});
		}
	});
	return AppEmailHeaderWidgetDevelopment;
});