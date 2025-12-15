(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery' ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($) {
	"use strict";
	$.widget("ui.wHtml", $.ui.wWidget, {
		options : {
			// 组件定义
			widgetDefinition : {},
			// 上级容器定义
			containerDefinition : {}
		},
		_createView : function() {
			// 渲染组件内容
			this._renderView();
			// 绑定组件事件
			this._bindEvents();
		},
		_renderView : function() {
			// 渲染组件内容
		},
		_bindEvents : function() {
			// 绑定组件事件
		}
	});
}));