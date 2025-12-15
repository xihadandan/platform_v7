define([ "constant", "commons", "server", "WidgetDevelopment" ],
		function(constant, commons, server, WidgetDevelopment) {
			// wHtml组件二开基础
			var HtmlWidgetDevelopment = function() {
				WidgetDevelopment.apply(this, arguments);
			};
			// 接口方法
			commons.inherit(HtmlWidgetDevelopment, WidgetDevelopment, {});
			return HtmlWidgetDevelopment;
		});