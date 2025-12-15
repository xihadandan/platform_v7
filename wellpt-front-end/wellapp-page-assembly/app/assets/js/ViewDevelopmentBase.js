define([ "commons", "constant", "server", "ListViewWidgetDevelopment" ], function(commons, constant, server,
		ListViewWidgetDevelopment) {
	var ViewDevelopment = function(wWidget) {
		ListViewWidgetDevelopment.apply(this, arguments);
	};
	// 接口方法
	commons.inherit(ViewDevelopment, ListViewWidgetDevelopment, {});
	return ViewDevelopment;
});