(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery', 'constant', 'appContext' ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, constant, appContext) {
	"use strict";
	$.widget("ui.wLayoutit", $.ui.wWidget, {
		options : {
			// 组件定义
			widgetDefinition : {},
			// 上级容器定义
			containerDefinition : {}
		},
		_createView : function() {
			// 监听事件
			this.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function(e, ui) {
				console.log("页面容器创建完成事件");
			});

			// 初始化基本信息
			this.element.addClass("ui-wLayoutit");
			// "生成布局"（flow）和"绘制"（paint）这两步，合称为"渲染"（render）
			this._renderView();
		},
		_init : function() {
			// 触发容器创建成功事件
			this.dispatchEvent(constant.WIDGET_EVENT.PageContainerCreationComplete);
		},
		_renderView : function() {
			// 生成布局
			this._createLayout();
			// 生成页面组件
			this._createWidgets();
		},
		_createLayout : function() {
		},
		_createWidgets : function() {
			var widgetDefinition = this.options.widgetDefinition;
			$.each(widgetDefinition.items, function(index, childWidgetDefinition) {
				appContext.createWidget(childWidgetDefinition, widgetDefinition);
			});
		},
		getData : function() {
			return [];
		}
	});
}));