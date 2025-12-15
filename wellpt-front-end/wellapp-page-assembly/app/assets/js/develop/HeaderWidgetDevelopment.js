define([ "constant", "commons", "server", "WidgetDevelopment" ],
		function(constant, commons, server, WidgetDevelopment) {
			// 页面组件二开基础
			var HeaderWidgetDevelopment = function() {
				WidgetDevelopment.apply(this, arguments);
			};
			// 接口方法
			commons.inherit(HeaderWidgetDevelopment, WidgetDevelopment, {});
			return HeaderWidgetDevelopment;
		});