define([ "commons", "constant", "server", "CalendarWidgetDevelopment" ], 
		function(commons, constant, server,	CalendarWidgetDevelopment) {
	var CalendarDevelopment = function(wWidget) {
		CalendarWidgetDevelopment.apply(this, arguments);
	};
	// 接口方法
	commons.inherit(CalendarDevelopment, CalendarWidgetDevelopment, {});
	return CalendarDevelopment;
});