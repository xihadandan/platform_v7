define([ "constant", "commons", "server", "ListViewWidgetDevelopment" ], function(constant, commons, server,
		ListViewWidgetDevelopment) {
	// 平台组件_公共功能_点击刷新_视图组件二开
	var Click2RefreshListViewWidgetDevelopment = function() {
		ListViewWidgetDevelopment.apply(this, arguments);
	};
	// 接口方法
	commons.inherit(Click2RefreshListViewWidgetDevelopment, ListViewWidgetDevelopment, {
		onClickRow : function() {
			var _self = this;
			setTimeout(function() {
				_self.getWidget().refresh();
			}, 1500);
		}
	});
	return Click2RefreshListViewWidgetDevelopment;
});