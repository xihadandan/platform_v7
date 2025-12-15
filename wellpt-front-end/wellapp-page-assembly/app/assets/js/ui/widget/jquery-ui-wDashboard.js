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
	$.widget("ui.wDashboard", $.ui.wWidget, {
		options : {
			// 组件定义
			widgetDefinition : {},
			// 上级容器定义
			containerDefinition : {}
		},
		_createView : function() {
			this._renderView();
		},
		_renderView : function() {
			// 生成页面组件
			$(this.element).load("/passport/admin/home");
		}
	});
}));