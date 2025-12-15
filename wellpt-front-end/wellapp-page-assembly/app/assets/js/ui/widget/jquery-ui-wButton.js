(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery', 'commons', 'server', 'constant' ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, commons, server, constant) {
	"use strict";
	var mouseover = constant.getValueByKey(constant.EVENT_TYPE, "MOUSE_OVER");
	$.widget("ui.wButton", $.ui.wWidget, {
		options : {
			// 组件定义
			widgetDefinition : {},
			// 上级容器定义
			containerDefinition : {}
		},
		_createView : function() {
			this._renderView();
			this._bindEvents();
		},
		_renderView : function() {
		},
		_bindEvents : function() {
			var _self = this;
			var $element = $(_self.element);
			var configuration = _self.getConfiguration();
			var events = configuration.events;
			$element.addClass( configuration.customClass );

			// 按钮事件
			$.each(events, function(i, eventInfo) {
				var type = eventInfo.type;
				var selector = eventInfo.source;
				var eventCallback = function(event) {
					var handler = eventInfo.handler;
					var params = eventInfo.params || {};
					var target = eventInfo.target;
					var opt = {
						target : target.position,
						targetWidgetId : target.widgetId,
						widgetSelectorType : target.widgetSelectorType,
						widgetCssClass : target.widgetCssClass,
						refreshIfExists : target.refreshIfExists,
						appType : handler.type,
						appPath : handler.path,
						params : $.extend({}, params.params, appContext.parseEventHashParams(handler, "menuid")),
						event : event
					};
					_self.startApp(opt);
				};
				// 延迟0.2秒
				if (mouseover === type) {
					$(selector, $element).hoverDelay({
						hoverEvent : eventCallback
					});
				} else {
					$element.on(type, selector, eventCallback);
				}
			});
		}
	});
}));