(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'commons', 'server', 'constant', 'appContext'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, commons, server, constant, appContext) {
  'use strict';
  var dataViewTypes = {
    bootstrapTableDataView: 'bootstrapTableDataView'
  };
  var KEY_DMS_ID = 'dms_id';
  var StringUtils = commons.StringUtils;
  $.widget('ui.wDocExchanger', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {}
    },
    getDmsId: function () {
      // return this.options.widgetDefinition.srcId || this.getId();
      // bug#46019
      return this.getId();
    },
    _createView: function () {
      this._renderView();
      this._bindEvents();
    },
    _renderView: function () {
      // 生成布局
      this._createLayout();
      // 生成页面组件
      this._createWidgets();
    },
    _createLayout: function () {},
    _createWidgets: function () {
      var _self = this;
      var configuration = _self.getConfiguration();
      var viewConfiguration = configuration.view || {};
      var listViewWidgetId = viewConfiguration.listViewId;
      // 数据管维护的视图定义
      var widgetDefinition = _self.options.widgetDefinition;
      var listViewDefinition = _self._getListViewDefinition(listViewWidgetId, widgetDefinition.items);
      if (listViewDefinition) {
        listViewDefinition.configuration.dmsWidgetDefinition = _self.options.widgetDefinition;
      }

      $.each(widgetDefinition.items, function (index, childWidgetDefinition) {
        appContext.createWidget(childWidgetDefinition, widgetDefinition);
      });
    },
    _getListViewDefinition: function (listViewWidgetId, items) {
      var _self = this;
      if (items == null || items.length === 0) {
        return null;
      }
      for (var i = 0; i < items.length; i++) {
        var childWidgetDefinition = items[i];
        if (listViewWidgetId === childWidgetDefinition.id) {
          return items[i];
        }
        var listViewDefinition = _self._getListViewDefinition(listViewWidgetId, childWidgetDefinition.items);
        if (listViewDefinition) {
          return listViewDefinition;
        }
      }
      return null;
    },
    _bindEvents: function () {
      var _self = this;
      if (!_self.options.widgetDefinition.configuration) {
        return;
      }
      var viewConfiguration = _self.options.widgetDefinition.configuration.view;
      var dataViewType = viewConfiguration.dataViewType;
      if (dataViewTypes.bootstrapTableDataView === dataViewType) {
        _self._bindBootstrapTableDataViewEvents();
      }
    },
    _bindBootstrapTableDataViewEvents: function () {
      var _self = this;
      var viewConfiguration = _self.options.widgetDefinition.configuration.view;
      var listViewWidgetId = viewConfiguration.listViewId;
      _self._bindViewEvent(null, listViewWidgetId, []);
    },

    _bindViewEvent: function (navWidgetId, listViewWidgetId, relationshipQuery) {
      var _self = this;
      var pageContainer = _self.pageContainer;
      // 监听容器创建完成事件
      pageContainer.onWidgetCreated(navWidgetId, function (e, navWidget) {
        var eventType = constant.WIDGET_EVENT.ItemClick + ' ' + constant.WIDGET_EVENT.LeftSidebarItemClick;
        navWidget.on(eventType, function (e, ui) {
          var listViewWidget = appContext.getWidgetById(listViewWidgetId);
          if (listViewWidget == null) {
            return;
          }
          var selectItem = e.detail.selectedItem;
          var columnValue = selectItem.value;
          var otherConditions = [];
          $.each(relationshipQuery, function (i) {
            var _query = this;
            // 忽略空值处理
            if (StringUtils.isBlank(columnValue) && _query.ignoreEmptyValue !== '0') {
              return;
            }
            // 日期类型的数据不进行区间查询时，取第一个值
            if (selectItem.dataType === '4' && _query.operator !== 'between' && $.isArray(columnValue) && columnValue.length > 0) {
              columnValue = columnValue[0];
            }
            var condition = {
              columnIndex: _query.columnIndex,
              value: columnValue,
              type: _query.operator
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

      // 视图创建完成处理
      pageContainer.onWidgetCreated(listViewWidgetId, function (e, listViewWidget) {
        $(listViewWidget.element).data(KEY_DMS_ID, _self.getDmsId());
        //表格数据标记功能初始化
        listViewWidget.$tableElement.bootstrapTable('initTableMarkable', {
          markDataDefine: _self.getConfiguration().markDataDefine,
          tableWidget: listViewWidget
        }); //bootstrap-table-label-mark.js
      });
    }
  });
});
