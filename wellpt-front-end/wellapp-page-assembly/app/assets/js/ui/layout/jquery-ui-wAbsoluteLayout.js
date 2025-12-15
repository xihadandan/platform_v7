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
	$.widget("ui.wAbsoluteLayout", $.ui.wWidget, {
		options : {
			// 组件定义
			widgetDefinition : {},
			// 上级容器定义
			containerDefinition : {}
		},
		_createView : function() {
			// "生成布局"（flow）和"绘制"（paint）这两步，合称为"渲染"（render）
			this._renderView();
		},
		_renderView : function() {
			// 生成布局
			this._createLayout();
			// 生成页面组件
			this._createWidgets();
		},
		_createLayout : function() {
			var _self = this;
			var options = _self.options;
			var width = options.widgetDefinition.width;
			var height = options.widgetDefinition.height;
			var $element = $(_self.element);
			var initScreenWidth = $(window).width();
			var initLayoutWidth = $element.parent().width();
			_self.layoutDensity = initLayoutWidth / width;
			_self.screenWidthFixedSize = 0;
			// 布局缩放比例
			console.log("AbsoluteLayout width init density: " + _self.layoutDensity);
			$element.height(height * _self.layoutDensity);

			// 窗口变更处理
			$(window).on("resize", function() {
				var newLayoutWidth = $element.parent().width() + _self.screenWidthFixedSize;
				$element.width(newLayoutWidth);
				_self.layoutDensity = newLayoutWidth / width;
				console.log("AbsoluteLayout width new density" + _self.layoutDensity)
				$element.height(height * _self.layoutDensity);

				// 更新窗口布局
				$.each(options.widgetDefinition.items, function(index, childWidgetDefinition) {
					_self._renderWbox(options.widgetDefinition, childWidgetDefinition);
				});
			});

			// 更新屏幕宽度修正的大小
			_self.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function() {
				// _self.screenWidthFixedSize = initScreenWidth -
				// $(window).width();
			});

			// 滚动条
			// $element.slimScroll({
			// width : width
			// });
		},
		_createWidgets : function() {
			var _self = this;
			var absoluteLayoutDefinition = this.options.widgetDefinition;
			$.each(absoluteLayoutDefinition.items, function(index, childWidgetDefinition) {
				_self._renderWbox(absoluteLayoutDefinition, childWidgetDefinition);
				appContext.createWidget(childWidgetDefinition, absoluteLayoutDefinition);
			});
		},
		_renderWbox : function(absoluteLayoutDefinition, widgetDefinition) {
			var item = widgetDefinition;
			var wboxIdSelector = "#" + item.wboxId;
			var $wbox = $(wboxIdSelector, this.element);
			$wbox.width(this._getDisplaySizeInInPixels(item.wboxWidth));
			$wbox.height(this._getDisplaySizeInInPixels(item.wboxHeight));
			$wbox.css("top", this._getDisplaySizeInInPixels(item.positionTop));
			$wbox.css("left", this._getDisplaySizeInInPixels(item.positionLeft));
		},
		_getDisplaySizeInInPixels : function(rawSize) {
			return this.layoutDensity * rawSize;
		}
	});
}));