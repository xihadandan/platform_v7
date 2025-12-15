(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'mui', 'mui-wWidget', 'commons', 'appContext', "app-adapter" ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, wWidget, commons, appContext) {
	"use strict";
	$.widget("mui.wMobilePage", $.ui.wWidget, {
		options : {
			// 组件定义
			widgetDefinition : {},
			// 上级容器定义
			containerDefinition : {},
		},
		_createView : function() {
			// 初始化基本信息
			// this.element.addClass("ui-wPage");
			// "生成布局"（flow）和"绘制"（paint）这两步，合称为"渲染"（render）
			this._renderView();
		},
		_renderView : function() {
			// 生成布局
			this._createLayout();
			// 生成页面组件
			this._createWidgets();

			$.ui.triggerMenuChange();
			$.ui.triggerTitleChange();
		},
		_createLayout : function() {
		},
		_createWidgets : function() {
			var self = this;
			var widgetDefinition = self.options.widgetDefinition;
			widgetDefinition.instance = self;
			$.each(widgetDefinition.items, function(index, childWidgetDefinition) {
				appContext.createWidget(childWidgetDefinition, widgetDefinition);
			});
			// 创建侧滑菜单
			this.__createOffCanvasSide();

		},
		__createOffCanvasSide : function() {
			// <!--侧滑菜单部分-->
			var StringBuilder = commons.StringBuilder;
			var sb = new StringBuilder();
			sb.append('<aside class="mui-off-canvas-left">');
			sb.append('	<div class="mui-scroll-wrapper">');
			sb.append('		<div class="mui-scroll">');
			sb.append('			<div class="title"></div>');
			sb.append('			<div class="content">');
			sb.append('			</div>');
			sb.append('			<div class="title"></div>');
			sb.append('			<ul class="mui-table-view mui-table-view-chevron mui-table-view-inverted">');
			sb.append('				<li class="mui-table-view-cell">');
			sb.append('					<a class="mui-navigate-right">');
			sb.append('						Item 1');
			sb.append('					</a>');
			sb.append('				</li>');
			sb.append('			</ul>');
			sb.append('		</div>');
			sb.append('	</div>');
			sb.append('</aside>');
		}
	});
}));