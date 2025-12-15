(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'commons', 'server', 'constant', 'appContext', 'api-wBootstrapTable', 'DmsDataServices'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, commons, server, constant, appContext, Api, DmsDataServices) {
  'use strict';
  var dataViewTypes = {
    bootstrapTableDataView: 'bootstrapTableDataView',
    tagTreeWithBootstrapTableDataView: 'tagTreeWithBootstrapTableDataView',
    unitTreeWithBootstrapTableDataView: 'unitTreeWithBootstrapTableDataView',
    leftSidebarWithBootstrapTableDataView: 'leftSidebarWithBootstrapTableDataView',
    dyformDataView: 'dyformDataView'
  };
  var KEY_DMS_ID = 'dms_id';
  var StringUtils = commons.StringUtils;
  $.widget('ui.wDataManagementViewer', $.ui.wWidget, {
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
      this.dmsDataServices = new DmsDataServices();
      this._renderView();
      this._bindEvents();
    },
    _renderView: function () {
      // 生成布局
      this._createLayout();
      // 生成页面组件
      this._createWidgets();
    },
    _createLayout: function () {
      var _self = this;
      var viewConfiguration = this.options.widgetDefinition.configuration.view;
      var dataViewType = viewConfiguration.dataViewType;
      if (dataViewTypes.dyformDataView === dataViewType) {
        this._createDyformDataViewLayout();
      }
    },

    _createDyformDataViewLayout: function () {
      //创建表单数据查看使用的iframe
      var _self = this;
      var $element = $(_self.element);
      var configuration = _self.getConfiguration();
      var eidtUnitOneFormData = configuration.view.editUnitOneFormData;
      var idValue = '';
      var autoClose = true;
      if (configuration.view.defaultConditionSql) {
        configuration.view.defaultConditionSql = appContext.resolveParamsNoConflics(
          { sql: configuration.view.defaultConditionSql },
          { location: location }
        ).sql;
      }
      var where = eidtUnitOneFormData ? ' system_unit_id=:currentUserUnitId ' : configuration.view.defaultConditionSql;
      autoClose = false;
      //查询表单在本单位数据的第一条数据，进行表单数据展示
      server.JDS.call({
        service: 'formDataService.getUniqueFormData',
        data: [configuration.store.formUuid, where],
        version: '',
        async: false,
        success: function (result) {
          if (result.success && result.data) {
            var formData = result.data[configuration.store.formUuid];
            idValue = formData[0]['UUID'] || formData[0]['uuid'];
          }
        }
      });

      $element.empty();

      this.dmsDataServices.openWindow({
        urlParams: {
          idKey: 'UUID',
          idValue: idValue,
          dms_id: _self.getDmsId(),
          ac_id: 'btn_list_view_add',
          ep_view_mode: '1', //编辑模式
          autoClose: autoClose
        },
        useUniqueName: false,
        ui: $(_self.element),
        target: constant.TARGET_POSITION.TARGET_WIDGET,
        targetWidgetId: _self.options.widgetDefinition.id
      });

      $(_self.element).on('dataManagementViewerAction', function (e) {
        //数据管理查看器按钮操作完成后
        _self._dealFormActionDoneEvent(e);
      });

      /*appModal.showMask();
            var $iframe = $("<iframe>")
            $iframe.attr('src', ctx + "/dms/data/services?idKey=UUID&idValue=" + idValue + "&dms_id="
                + _self.options.widgetDefinition.id + "&ac_id=btn_list_view_add&ep_view_mode=1&autoClose=" + autoClose);
            $iframe.attr('style', 'width: 100%;border:0px;height:0px;');
            var iframeId = commons.UUID.createUUID();
            $iframe.attr('id', iframeId);
            $element.append($iframe);
            $iframe.attr('scrolling', 'no');
            _self.$formFrame = $iframe;//

            $element.on('dyformViewLoadSuccess', function () {
                _self._adjustDyformframePageHeight();
            });*/
    },
    /**
         * 调整iframe报表的高度适应内容
         * @private
         _adjustDyformframePageHeight: function () {
            var _self = this;
            if ($(window.frames[_self.$formFrame.attr('id')].contentDocument)) {
                _self.$formFrame.height('0');
                var pageContainerHeight = window.setInterval(function () {
                    var $pageContainer = $(_self.element).parents('.web-app-container');
                    if ($pageContainer.length == 1) {
                        _self.$formFrame.height($pageContainer.height() - 10);
                        clearInterval(pageContainerHeight);
                        appModal.hideMask();
                    }
                }, 100);
            }
        },*/

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
      } else if (dataViewTypes.tagTreeWithBootstrapTableDataView === dataViewType) {
        _self._bindTagTreeWithBootstrapTableDataViewEvents();
      } else if (dataViewTypes.unitTreeWithBootstrapTableDataView === dataViewType) {
        _self._bindUnitTreeWithBootstrapTableDataViewEvents();
      } else if (dataViewTypes.leftSidebarWithBootstrapTableDataView === dataViewType) {
        _self._bindLeftSidebarTreeWithBootstrapTableDataViewEvents();
      }
    },
    _bindBootstrapTableDataViewEvents: function () {
      var _self = this;
      var viewConfiguration = _self.options.widgetDefinition.configuration.view;
      var listViewWidgetId = viewConfiguration.listViewId;
      _self._bindViewEvent(null, listViewWidgetId, []);
    },
    _bindTagTreeWithBootstrapTableDataViewEvents: function () {
      var _self = this;
      var viewConfiguration = _self.options.widgetDefinition.configuration.view;
      var tagTreeWidgetId = viewConfiguration.tagTreeId;
      var listViewWidgetId = viewConfiguration.listViewId;
      var relationshipQuery = viewConfiguration.relationshipQuery;

      _self._bindViewEvent(tagTreeWidgetId, listViewWidgetId, relationshipQuery);
    },
    _bindUnitTreeWithBootstrapTableDataViewEvents: function () {
      var _self = this;
      var viewConfiguration = _self.options.widgetDefinition.configuration.view;
      var unitTreeWidgetId = viewConfiguration.unitTreeId;
      var listViewWidgetId = viewConfiguration.listViewId;
      var relationshipQuery = viewConfiguration.relationshipQuery;

      _self._bindViewEvent(unitTreeWidgetId, listViewWidgetId, relationshipQuery);
    },
    _bindLeftSidebarTreeWithBootstrapTableDataViewEvents: function () {
      var _self = this;
      var viewConfiguration = _self.options.widgetDefinition.configuration.view;
      var unitTreeWidgetId = viewConfiguration.leftSidebarTreeId;
      var listViewWidgetId = viewConfiguration.listViewId;
      var relationshipQuery = viewConfiguration.relationshipQuery;

      _self._bindViewEvent(unitTreeWidgetId, listViewWidgetId, relationshipQuery);
    },
    _bindViewEvent: function (navWidgetId, listViewWidgetId, relationshipQuery) {
      var _self = this;
      var pageContainer = _self.pageContainer;
      if (!pageContainer.onWidgetCreated) {
        pageContainer = _self;
      }
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

      $(_self.element).on('dataManagementViewerAction', function (e) {
        // debugger;
        _self._dealDataEvent(e, listViewWidgetId);
      });
    },

    _dealDataActiveEvent: function (e) {
      var _self = this;
      var detail = e.detail;

      var dataActiveDefine = _self.getConfiguration().dataActiveDefine;
      if (dataActiveDefine && dataActiveDefine.length > 0) {
        var callDataActive = function (config) {
          var rowData = detail.options.rowData || detail.options.ui.element.data('uiWBootstrapTable').getSelections();
          if (rowData) {
            var params = [],
              serviceName,
              dataValues = [],
              dataTexts = [];
            for (var i = 0, len = rowData.length; i < len; i++) {
              if (rowData[i][config.dataValue]) {
                dataValues.push(rowData[i][config.dataValue]);
              }
              dataTexts.push(rowData[i][config.dataText]);
            }
            if ($.isEmptyObject(dataValues)) {
              return;
            }

            if (config.activeDataType == 'entity') {
              serviceName = 'dmsDataActiveReportFacadeService.batchAddDataActiveByClassName';
              params.push(dataValues, dataTexts, config.entityClassName);
            } else if (config.activeDataType == 'dyform') {
              serviceName = 'dmsDataActiveReportFacadeService.addDataActiveByDyform';
              params.push({
                formUuid: config.dyformUuid,
                dataValues: dataValues,
                dataTexts: dataTexts,
                dataValueColumn: config.dataValueColumn,
                dataTextColumn: config.dataTextColumn,
                lastActiveTimeColumn: config.lastActiveTimeColumn,
                activeCntColumn: config.activeCntColumn
              });
            }
            if (dataValues.length > 0) {
              server.JDS.call({
                service: serviceName,
                data: params,
                version: '',
                async: true,
                success: function (result) {}
              });
            }
          }
        };

        for (var i = 0, len = dataActiveDefine.length; i < len; i++) {
          if (dataActiveDefine[i].buttonTrigger && dataActiveDefine[i].buttonTrigger.btnCodes) {
            var btnCodes = dataActiveDefine[i].buttonTrigger.btnCodes.split(';');
            for (var b = 0, bLen = btnCodes.length; b < bLen; b++) {
              if (
                $(detail.options.event.currentTarget).hasClass('li_class_' + btnCodes[b]) ||
                $(detail.options.event.currentTarget).hasClass('btn_class_' + btnCodes[b])
              ) {
                callDataActive(dataActiveDefine[i].saveConfig);
                break;
              }
            }
          }
        }
      }
    },

    _dealDataMarkEvent: function (e) {
      var _self = this;
      var detail = e.detail;
      var markDataDefine = _self.getConfiguration().markDataDefine;
      if (markDataDefine && markDataDefine.statusMarks && markDataDefine.statusMarks.length > 0) {
        var statusMarks = markDataDefine.statusMarks;
        var markData = function (markConfig, uuids, isDeleteMark) {
          detail.options.ui.$tableElement.bootstrapTable(
            'dmsMarkRows',
            uuids,
            {
              isDeleteMark: isDeleteMark,
              entityClassName: markConfig.entityClassName,
              tableName: markConfig.databaseTable,
              statusColumn: markConfig.statusColumn,
              updateTimeColumn: markConfig.updateTimeColumn
            },
            function () {
              detail.options.ui.element.data('uiWBootstrapTable').refresh();
            }
          );
        };
        var rowData = detail.options.rowData;
        if (!rowData && detail.options.ui.element) {
          rowData = detail.options.ui.element.data('uiWBootstrapTable').getSelections();
        }
        if (rowData) {
          var uuids = (function () {
            var pcn = detail.options.ui.getPrimaryColumnName();
            return $.map(rowData, function (r) {
              return r[pcn] ? r[pcn] : null;
            });
          })();
          if ($.isEmptyObject(uuids)) {
            return;
          }

          for (var i = 0, len = statusMarks.length; i < len; i++) {
            //标记或者取消标记的其他事件触发按钮
            if (statusMarks[i]) {
              for (var key in statusMarks[i]) {
                if ((key == 'markBtnTrigger' || key == 'unmarkBtnTrigger') && statusMarks[i][key] && statusMarks[i][key].btnCodes) {
                  var btnCodes = statusMarks[i][key].btnCodes.split(';');
                  for (var b = 0, bLen = btnCodes.length; b < bLen; b++) {
                    if (
                      $(detail.options.event.currentTarget).hasClass('li_class_' + btnCodes[b]) ||
                      $(detail.options.event.currentTarget).hasClass('btn_class_' + btnCodes[b])
                    ) {
                      markData(statusMarks[i].markDataType, uuids, key != 'markBtnTrigger');
                      break;
                    }
                  }
                }
              }
            }
          }
        }
      }
    },

    _dealFormActionDoneEvent: function (e, listViewWidgetId) {
      var _self = this;
      var detail = e.detail;
      var actionButtons = _self.getConfiguration().document.buttons;
      if (detail.options.action) {
        for (var i = 0, len = actionButtons.length; i < len; i++) {
          if (actionButtons[i].eventHandler) {
            var paths = actionButtons[i].eventHandler.path.split('/');
            if (paths[paths.length - 1] == detail.options.action) {
              appContext.eval(actionButtons[i].eventHandler['afterEventHandlerCodes'], $(_self), {
                $this: _self,
                commons: commons,
                result: detail.invokeResult,
                server: server,
                Api: new Api($(listViewWidgetId))
              });

              break;
            }
          }
        }
      }
    },

    //数据事件
    _dealDataEvent: function (e, listViewWidgetId) {
      var _self = this;
      var detail = e.detail;
      //处理数据服务
      if (detail && detail.invokeResult && detail.invokeResult.success && detail.options && detail.options.event && detail.options.ui) {
        //1.触发数据活动事件
        _self._dealDataActiveEvent(e);

        //2.触发标记数据事件
        _self._dealDataMarkEvent(e);

        //3.事件处理操作执行完毕，执行处理后的自定义代码块
        _self._dealFormActionDoneEvent(e, listViewWidgetId);
      }
    }
  });
});
