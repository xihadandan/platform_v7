(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'mui', 'constant', 'commons', 'appContext', "formBuilder", "app-adapter" ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, constant, commons, appContext, formBuilder) {
	"use strict";
	var KEY_DMS_ID = "dms_id";
	var StringBuilder = commons.StringBuilder;
	var StringUtils = commons.StringUtils;
	$.widget("mui.wMobileDataManagementViewer", $.ui.wWidget, {
		options : {
			// 组件定义
			widgetDefinition : {},
			// 上级容器定义
			containerDefinition : {},
		},
		_createView : function() {
			var self = this;
			var requires = [];
			var configuration = self.options.widgetDefinition.configuration || {};
			self.options.widgetDefinition.configuration = configuration;
			if(configuration.document && configuration.document.jsModule){
				configuration.jsModule = configuration.document.jsModule;
				requires.push(configuration.jsModule);
			}
			appContext.require(requires, function(Development) {
				if (typeof Development === 'function') {
					self.development = new Development(self);;
				}
				self._executeJsModule('beforeRender', [ self.options, configuration ]);
				// "生成布局"（flow）和"绘制"（paint）这两步，合称为"渲染"（render）
				self._renderView();
				self._executeJsModule('afterRender', [ self.options, configuration ]);
			});
		},
		_init : function() {
		},
		_renderView : function() {
			var self = this;
			// 生成布局
			self._createLayout();
			// 生成页面组件
			self._createWidgets();
			// 绑定事件
			var viewConfiguration = self.options.widgetDefinition.configuration.view || {};
			var unitTreeWidgetId = viewConfiguration.leftSidebarTreeId;
			var listViewWidgetId = viewConfiguration.listViewId;
			var relationshipQuery = viewConfiguration.relationshipQuery;
			self._bindViewEvent(unitTreeWidgetId, listViewWidgetId, relationshipQuery);
		},
		_createLayout : function() {
			var _self = this;
			// 设置面板头部标题
			var appData = appContext.getCurrentUserAppData();
			var system = appData.getSystem();
			var module = appData.getModule();
			var app = appData.getApplication();
			var headerName = system.name;
			if (app) {
				headerName = app.title || app.name;
			}else if (module){
				headerName = module.title || module.name;
			}
			// TODO
		},
		_createWidgets : function() {
			var self = this;
			var widgetDefinition = self.options.widgetDefinition;
			var viewConfiguration = self.getConfiguration().view || {};
			var listViewWidgetId = viewConfiguration.listViewId;
			var listViewDefinition = self._getListViewDefinition(listViewWidgetId, widgetDefinition.items);
			if (listViewDefinition) {
				listViewDefinition.configuration.dmsWidgetDefinition = self.options.widgetDefinition;
			}
			widgetDefinition.instance = self;
			$.each(widgetDefinition.items, function(index, childWidgetDefinition) {
				appContext.createWidget(childWidgetDefinition, widgetDefinition);
			});
		},
		getListViewDefinition : function() {
			var self = this;
			var widgetDefinition = self.options.widgetDefinition;
			var viewConfiguration = self.getConfiguration().view || {};
			var listViewWidgetId = viewConfiguration.listViewId;
			return self._getListViewDefinition(listViewWidgetId, widgetDefinition.items);
		},
		_getListViewDefinition : function(listViewWidgetId, items) {
			var _self = this;
			if (items == null || items.length === 0) {
				return null;
			}
			for (var i = 0; i < items.length; i++) {
				var childWidgetDefinition = items[i];
				if (listViewWidgetId === childWidgetDefinition.id) {
					return items[i];
				}
				var listViewDefinition = _self._getListViewDefinition(listViewWidgetId, childWidgetDefinition.items)
				if (listViewDefinition) {
					return listViewDefinition;
				}
			}
			return null;
		},
		_bindViewEvent : function(navWidgetId, listViewWidgetId, relationshipQuery) {
			var _self = this;
			var pageContainer = _self.pageContainer;
			if(navWidgetId){
				// 监听容器创建完成事件
				pageContainer.onWidgetCreated(navWidgetId, function(e, navWidget) {
					var eventType = constant.WIDGET_EVENT.ItemClick + " " + constant.WIDGET_EVENT.LeftSidebarItemClick;
					navWidget.on(eventType, function(e, ui) {
						var listViewWidget = appContext.getWidgetById(listViewWidgetId);
						if (listViewWidget == null) {
							return;
						}
						var selectItem = e.detail.selectedItem;
						var columnValue = selectItem.value;
						var otherConditions = [];
						$.each(relationshipQuery, function(i) {
							var _query = this;
							// 忽略空值处理
							if (StringUtils.isBlank(columnValue) && _query.ignoreEmptyValue !== "0") {
								return;
							}
							// 日期类型的数据不进行区间查询时，取第一个值
							if (selectItem.dataType === "4" && _query.operator !== "between" && $.isArray(columnValue)
									&& columnValue.length > 0) {
								columnValue = columnValue[0];
							}
							var condition = {
									columnIndex : _query.columnIndex,
									value : columnValue,
									type : _query.operator
							};
							if (StringUtils.isNotBlank(selectItem.customCriterion)) {
								condition.customCriterion = selectItem.customCriterion;
							}
							otherConditions.push(condition);
						});
						listViewWidget.clearOtherConditions();
						listViewWidget.addOtherConditions(otherConditions);
						listViewWidget.refresh(true);
					});
				});
			}
			// 视图创建完成处理
			pageContainer.onWidgetCreated(listViewWidgetId, function(e, listViewWidget) {
				listViewWidget = listViewWidget || e.detail[1];
				// window.ListViewInstance = listViewWidget;
				$(listViewWidget.element).data(KEY_DMS_ID, _self.getId());
			});
		}
	});
}));