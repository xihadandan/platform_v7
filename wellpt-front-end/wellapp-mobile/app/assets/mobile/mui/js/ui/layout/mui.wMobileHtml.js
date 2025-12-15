(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'mui', 'constant', 'commons', 'appContext', "app-adapter" ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, constant, commons, appContext ) {
	"use strict";
	$.widget("mui.wMobileHtml", $.ui.wWidget, {
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
			var options = this.options;
			var _self = this;

			// 监听容器创建完成事件
			this.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function(e, ui) {
			});

			// 监听左导航事件
			this.on(constant.WIDGET_EVENT.LeftSidebarItemClick, function(e, ui) {
			});

			// 滚动条
			// $(_self.element).slimScroll();
		}
	});
}));