define([ "constant", "commons", "server", "ListViewWidgetDevelopment" ], function(constant, commons, server,
		ListViewWidgetDevelopment) {
	// 平台组件_公共功能_点击刷新徽章数量_视图组件二开
	var Click2RefreshBadgeListViewWidgetDevelopment = function() {
		ListViewWidgetDevelopment.apply(this, arguments);
	};
	// 接口方法
	commons.inherit(Click2RefreshBadgeListViewWidgetDevelopment, ListViewWidgetDevelopment, {
		onClickRow : function() {
			var _self = this;
			setTimeout(function() {
				_self.getWidget().trigger(constant.WIDGET_EVENT.BadgeRefresh);
			}, 1500);
		}
	});
	return Click2RefreshBadgeListViewWidgetDevelopment;
});