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
	$.widget("ui.wAcePage", $.ui.wWidget, {
		_createView : function() {
			// 监听事件
			this.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function(e, ui) {
			});

			// 初始化基本信息
			this.element.addClass("ui-wPage");
			// "生成布局"（flow）和"绘制"（paint）这两步，合称为"渲染"（render）
			this._renderView();

			// 初始化页面
			require([ 'pt/js/app/ace/inspinia' ]);
			// 初始化设置
			require([ 'pt/js/app/ace/init_config' ]);
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
			var callback = function(widget) {
				var widgetId = widget.getId();
				// 左导航
				if (widgetId === widgetDefinition.leftSidebar.id) {
					// 获取当前模块下的应用作为导航
					var userAppData = appContext.getCurrentUserAppData();
					var currentModule = userAppData.getModule();
					var currentApp = userAppData.getApplication();
					var $div = $("<div>动态渲染组件</div>");
					$div.on("click", function(e) {
						// renderTo
						var renderOptions = {
							renderTo : "#tab-dashboard",
							widgetDefId : "wBootstrapTable_C73D80A344A00001D6C1A8D0EED617DE"
						}
						appContext.renderWidget(renderOptions);
					});
					$(".panel-body").append($div);
					$(".dynamic-widget").on("click", "a", function() {
						var widgetDefId = $(this).attr("widgetId");
						var renderOptions = {
							renderTo : "#tab-dashboard",
							widgetDefId : widgetDefId
						}
						appContext.renderWidget(renderOptions);
					});
					// if (currentModule != null) {
					// var apps = userAppData.getModuleApps();
					// $.each(apps, function(i) {
					// var $li = $('<li><a href="#">' + this.name +
					// '</a></li>');
					// $(".nav-second-level").append($li);
					// });
					// }
				}
				// 仪表盘
				if (widgetId === widgetDefinition.dashboard.id) {
					$(widget.element).parent().slimScroll({
						height : "500px"
					});
				}
			};
			$.each(widgetDefinition.items, function(index, widgetDefinition) {
				// 头部隐藏二级导航及工具按钮
				if (widgetDefinition.id == widgetDefinition.header.id) {
					widgetDefinition.subNavAndToolBarHidden = true;
				}
				appContext.createWidget(widgetDefinition, widgetDefinition, callback);
			});
			// // 头部
			// $.appContext.createWidget(widgetDefinition.header,
			// widgetDefinition,
			// function(headerWidget) {
			// $(".ui-wHeader-navbar2").hide();
			// $(".ui-wHeader-navbar1").removeClass("navbar-inverse");
			// });
			// // 左导航
			// $.appContext.createWidget(widgetDefinition.leftSidebar,
			// widgetDefinition);
			// // 仪表盘
			// $.appContext.createWidget(widgetDefinition.dashboard,
			// widgetDefinition, function(dashboardWidget) {
			// $(dashboardWidget.element).parent().slimScroll({
			// height : "500px"
			// });
			// });
			// // 底部
			// $.appContext.createWidget(widgetDefinition.footer,
			// widgetDefinition);
		}
	});
}));