(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery', 'commons', 'server', 'constant', 'metisMenu' ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, commons, server, constant) {
	"use strict";
	var jds = server.JDS;
	var StringBuilder = commons.StringBuilder;
	var StringUtils = commons.StringUtils;
	var DateUtils = commons.DateUtils;
	$.widget("ui.wTagTree", $.ui.wWidget, {
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
			// 生成页面组件
			var _self = this;
			var options = _self.options;

			_self.dataItemMap = {};
			var items = _self.getDataProvider();
			// 渲染NAV
			var navHtml = '<div class="metismenu">'//
					+ '<aside class="metismenu sidebar">'//
					+ '	<nav class="metismenu sidebar-nav">'//
					+ '		<ul class="metismenuul">';

			navHtml += _self._getNavItemHtml(items, 1);
			navHtml += '</ul></nav></aside></div>';
			$(_self.element).append(navHtml);

			$(".metismenuul", self.element).metisMenu({
				toggle : false
			});

			// 滚动条
			// $(_self.element).slimScroll();
		},
		_getNavItemHtml : function(items, navLevel) {
			var _self = this;

			for (var i = 0; i < items.length; i++) {
				var item = items[i];
				_self.dataItemMap[item.uuid] = item;
			}

			var itemClass = "nav-menu-item";
			// 区分一、二、三级菜单

			if (navLevel == 1) {
				itemClass += " mainnav-menu-item";
			}

			if (navLevel == 2) {
				itemClass += " subnav-menu-item";
			}

			if (navLevel == 3) {
				itemClass += " thirdnav-menu-item";
			}

			var sb = new StringBuilder();
			for (var i = 0; i < items.length; i++) {
				var item = items[i];
				sb.appendFormat('<li class="sidebar-nav-item-li {0}" itemId="{1}">', itemClass, item.uuid);
				sb.appendFormat('<a title="{0}">', item.text);
				sb.appendFormat('	<span class="sidebar-nav-item">{0}</span>', item.text);

				// 展开箭头
				if (item.children && item.children.length > 0) {
					sb.append('<span class="fa arrow fa-fw"></span>');
				}

				sb.append('</a>');

				if (item.children && item.children.length > 0) {
					sb.append('<ul>');
					sb.append(_self._getNavItemHtml(item.children, navLevel + 1));
					sb.append('</ul>');
				}

				sb.append('</li>');
			}
			return sb.toString();
		},
		_bindEvents : function() {
			var _self = this;
			var configuration = _self.getConfiguration();
			var dataType = configuration.dataType;
			$(".metismenuul", self.element).on("click", ".sidebar-nav-item-li", function() {
				var itemId = $(this).attr("itemId");
				if (_self.dataItemMap[itemId]) {
					var item = _self.dataItemMap[itemId];
					item.dataType = dataType;
					_self.setSelectedItem(item);
					var eventData = {
						selectedItem : item
					};
					var eventType = constant.WIDGET_EVENT.ItemClick;
					_self.trigger(eventType, eventData);
					return false;
				}
			});
		},
		getDataProvider : function() {
			var _self = this;
			if (_self.data) {
				return _self.data;
			}
			_self.data = [];
			var configuration = _self.getConfiguration();
			var dataType = configuration.dataType;
			// 获取常量
			if (dataType === "1") {
				_self.data = _self._getOptionData(configuration);
			}
			// 获取数据字典
			if (dataType === "2") {
				_self.data = _self._getDataDictData(configuration);
			}
			// 获取数据仓库
			if (dataType === "3") {
				_self.data = _self._getDataStoreData(configuration);
			}
			// 获取年份
			if (dataType === "4") {
				_self.data = _self._getYearData();
			}
			return _self.data;
		},
		// 获取常量
		_getOptionData : function(configuration) {
			return configuration.optionValue;
		},
		// 获取数据字典
		_getDataDictData : function(configuration) {
			var dataDicUuid = configuration.dataDic;
			if (StringUtils.isBlank(dataDicUuid)) {
				dataDicUuid = "-1";
			}
			var items = [];
			var convertItems = function(data) {
				data.uuid = data.id;
				data.text = data.name;
				data.code = data.data;
				if (data.children) {
					convertChildrens(data.children);
				}
			}
			var convertChildrens = function(children) {
				$.each(children, function() {
					convertItems(this);
				});
			}
			jds.call({
				service : "dataDictionaryService.getAllAsTree",
				data : [ dataDicUuid ],
				async : false,
				version : "",
				success : function(result) {
					var data = result.data;
					if (data && data.children) {
						$.each(data.children, function() {
							convertItems(this);
						})
						items = data.children;
					}
				}
			});
			return items;
		},
		// 获取数据仓库
		_getDataStoreData : function(configuration) {
			var dataStoreId = configuration.dataStoreId;
			var valueColumn = configuration.valueColumn;
			var textColumn = configuration.textColumn;
			var items = [];
			jds.call({
				service : "viewComponentService.loadAllSelectData",
				data : [ true, dataStoreId, valueColumn, textColumn ],
				async : false,
				success : function(result) {
					$.each(result.data.data, function(i, data) {
						// null值不显示
						if (data[valueColumn] == null || data[textColumn] == null) {
							return;
						}
						items.push({
							uuid : commons.UUID.createUUID(),
							value : data[valueColumn],
							text : data[textColumn],
							valueColumn : valueColumn,
							textColumn : textColumn
						});
					})
				}
			});
			return items;
		},
		// 获取年份
		_getYearData : function() {
			var DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
			var year = (new Date()).getFullYear();
			var items = [];
			items.push({
				uuid : commons.UUID.createUUID(),
				value : "",
				text : "全部"
			});
			for (var i = 0; i < 7; i++) {
				// 月份
				var months = [];
				for (var j = 0; j < 12; j++) {
					var monthDate = new Date((year - i), j, 1);
					var monthArray = [];
					monthArray.push(DateUtils.format(monthDate, DATE_TIME_FORMAT));
					monthArray.push(DateUtils.format(new Date((year - i), j + 1, 1), DATE_TIME_FORMAT));
					months.push({
						uuid : commons.UUID.createUUID(),
						value : monthArray,
						text : (j + 1) + "&nbsp;月",
						type : "month",
					});
				}
				// 年份
				var yearDate = new Date((year - i), 0, 1);
				var yearArray = [];
				yearArray.push(DateUtils.format(yearDate, DATE_TIME_FORMAT));
				yearArray.push(DateUtils.format(new Date((year - i + 1), 0, 1), DATE_TIME_FORMAT));
				var item = {
					uuid : commons.UUID.createUUID(),
					value : yearArray,
					text : (year - i) + "&nbsp;年",
					type : "year",
					children : months
				};
				items.push(item);
			}
			return items;
		},
		setSelectedItem : function(item) {
			this.selectedItem = item;
		},
		getSelectedItem : function() {
			return this.selectedItem;
		}
	});
}));