define([ "constant", "commons", "server", "WidgetDevelopment" ],
		function(constant, commons, server, WidgetDevelopment) {
			// 页面组件二开基础
			var PanelWidgetDevelopment = function() {
				WidgetDevelopment.apply(this, arguments);
			};
			// 接口方法
			commons.inherit(PanelWidgetDevelopment, WidgetDevelopment, {
				// 组件渲染前回调方法。
				beforeRender : function(options, configuration) {
				},
				// 组件渲染前回调方法。
				afterRender : function(options, configuration) {
				},
				// 初始化面板。
				initPanel : function(configuration) {
				}
			});
			return PanelWidgetDevelopment;
		});