define([ "commons", "constant", "server", "CalendarWidgetDevelopment" ], 
		function(commons, constant, server,	CalendarWidgetDevelopment) {
	var MyAgendaCalendarDevelopment = function(wWidget) {
		CalendarWidgetDevelopment.apply(this, arguments);
	};
	// 接口方法
	commons.inherit(MyAgendaCalendarDevelopment, CalendarWidgetDevelopment, {
		init :function(){
			console.log("MyAgendaCalendarDevelopment.init");
		}
	});
	return MyAgendaCalendarDevelopment;
});