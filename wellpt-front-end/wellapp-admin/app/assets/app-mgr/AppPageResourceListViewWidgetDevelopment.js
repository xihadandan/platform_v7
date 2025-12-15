define([
  'constant',
  'commons',
  'server',
  'appContext',
  'appModal',
  'formBuilder',
  'AppPtMgrListViewWidgetDevelopment',
  'AppPtMgrCommons'
], function (constant, commons, server, appContext, appModal, formBuilder, AppPtMgrListViewWidgetDevelopment, AppPtMgrCommons) {
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;
  var UUID = commons.UUID;
  var ArrayUtils = commons.ArrayUtils;
  var Validation = server.Validation;
  var SelectiveDatas = server.SelectiveDatas;

  // 根据功能类型返回类型名称
  var getTypeName = function (type) {
    var items = SelectiveDatas.getItems('PT_APP_FUNCTION_TYPE');
    for (var i = 0; i < items.length; i++) {
      if (items[i].value == type) {
        return items[i].label;
      }
    }
  };

  // 平台管理_产品集成_页面引用资源列表_视图组件二开
  var AppPageResourceListViewWidgetDevelopment = function () {
    AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppPageResourceListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
    // 组件准备
    prepare: function () {
      var _self = this;
      _self.addDefaultCondition();
    },
    addDefaultCondition: function () {
      var _self = this;
      var widget = _self.getWidget();
      // 获取传给组件的参数
      var params = _self.getWidgetParams();
      var otherConditions = [];
      // 页面UUID
      if (params.page && StringUtils.isNotBlank(params.page.uuid)) {
        var condition = {
          columnIndex: 'appPageUuid',
          value: params.page.uuid,
          type: 'eq'
        };
        otherConditions.push(condition);
      }
      widget.addOtherConditions(otherConditions);
    },
    addFunctionTypeCondition: function () {
      var _self = this;
      var widget = _self.getWidget();
      // 获取传给组件的参数
      var params = _self.getWidgetParams();
      var otherConditions = [];
      // 页面UUID
      var functionType = $('select', widget.element).val();
      if (StringUtils.isNotBlank(functionType)) {
        var condition = {
          columnIndex: 'type',
          value: functionType,
          type: 'eq'
        };
        otherConditions.push(condition);
      }
      widget.addOtherConditions(otherConditions);
    },
    getTableOptions: function (bootstrapTableOptions) {
      var columns = bootstrapTableOptions.columns;
      var _this = this;
      $.each(columns, function (i, column) {
        var uuid = UUID.createUUID();

        // 本次出库数量列设置为可编辑
        if (column.field == 'isProtected') {
          if (_this._getSystem()) {
            column.visible = false;
          } else {
            column.formatter = function (value, prop) {
              var html =
                "<input type='checkbox' prop='" + prop + "'" + (value == 1 ? 'checked' : '') + " id=''>" + "<label for=''></label>";
              return html;
            };
            column.events = {
              'change :checkbox': function (event, value, row, index) {
                row.isProtected = this.checked ? 1 : 0;
              }
            };
          }
        }
      });
    },
    beforeLoadData: function () {
      var _self = this;
      var widget = _self.getWidget();
      // 获取传给组件的参数
      var params = _self.getWidgetParams();
      // 来自页面设计器，添加查询条件
      if (params.source == '1') {
        widget.clearOtherConditions();
        _self.addDefaultCondition();
        _self.addFunctionTypeCondition();
      }
    },
    // 数据加载成功后替换数据源的加载数据方法，不再请求后台数据
    onLoadSuccess: function (data) {
      var _self = this;
      var widget = _self.getWidget();
      // 获取传给组件的参数
      var params = _self.getWidgetParams();
      // 来自页面设计器，不重写数据源
      if (params.source == '1') {
        return;
      }
      var dataProvider = widget.getDataProvider();
      // 重写加载数据方法为本地数据
      dataProvider.load = function (filter, params) {
        if (dataProvider.dataArray == null) {
          dataProvider.dataArray = dataProvider.data;
        }
        var keyword = widget.getKeyword();
        dataProvider.data = _self._filterData(keyword, dataProvider.dataArray);
        this.notifyChange(params);
      };
      // 增加添加数据方法
      dataProvider.add = function (data) {
        if (dataProvider.dataArray == null) {
          dataProvider.dataArray = dataProvider.data;
        }
        dataProvider.dataArray.push(data);
        widget.refresh();
      };
      // 增加删除数据方法
      dataProvider.removeAll = function (dataList) {
        var _self = this;
        if (_self.dataArray == null) {
          _self.dataArray = _self.data;
        }
        var tmpArray = [];
        $.each(dataList, function (i, data) {
          _self.dataArray = ArrayUtils.removeElement(_self.dataArray, data);
        });
        widget.refresh();
      };

      // 复选框勾选
      for (var i = 0; i < data.rows.length; i++) {
        var inputEle = $('#page_resource_table .ui-wBootstrapTable').find('td[data-field="isProtected"]').eq(i).find('input');
        var labelEle = $('#page_resource_table .ui-wBootstrapTable').find('td[data-field="isProtected"]').eq(i).find('label');
        inputEle.prop('id', data.rows[i].uuid);
        labelEle.prop('for', data.rows[i].uuid);
      }
    },

    _filterData: function (keyword, dataArray) {
      var _self = this;
      var widget = _self.getWidget();
      var functionType = $('select', widget.element).val();
      var returnArray = [];
      for (var i = 0; i < dataArray.length; i++) {
        var data = dataArray[i];
        if (StringUtils.isNotBlank(functionType)) {
          if (data.type == functionType) {
            if (StringUtils.isNotBlank(keyword)) {
              for (var p in data) {
                if (!(p == 'name' || p == 'id' || p == 'typeRenderValue')) {
                  continue;
                }
                if (StringUtils.contains(data[p] + '', keyword)) {
                  returnArray.push(data);
                  break;
                }
              }
            } else {
              returnArray.push(data);
            }
          }
        } else {
          for (var p in data) {
            if (!(p == 'name' || p == 'id' || p == 'typeRenderValue')) {
              continue;
            }
            if (StringUtils.contains(data[p] + '', keyword)) {
              returnArray.push(data);
              break;
            }
          }
        }
      }
      return returnArray;
    },
    afterRender: function (options, configuration) {
      var _self = this;
      var widget = _self.getWidget();
      // 获取传给组件的参数
      var params = _self.getWidgetParams();
      var $element = $(_self.widget.element);
      var $searchInput = $element.find("input[type='search']");
      $('button.btn-reset', $element).hide();
      var $select = $('<select>', {
        style: 'width:100px;'
      });
      $select.append(
        $('<option>', {
          value: ''
        }).text('全部')
      );
      var items = SelectiveDatas.getItems('PT_APP_FUNCTION_TYPE');
      $.each(items, function (i, item) {
        var option = "<option value='" + item.value + "'>" + item.label + '</option>';
        $select.append(option);
      });
      var $div = $('<div>', {
        class: 'pull-right search'
      }).append($select);
      $div.insertAfter($searchInput.parent());
      // 类型变更选择
      $select.on('change', function () {
        _self.widget.refresh(true);
      });
      // 来自页面设计器，隐藏操作按钮
      if (params.source == '1' && widget.buttonElement && widget.buttonElement.headerToolbar) {
        $(widget.buttonElement.headerToolbar).hide();
        $select.removeAttr('style');
      } else {
        $searchInput.css({
          width: '100px'
        });
      }
    },
    // 添加
    btn_add: function () {
      var _self = this;
      var widget = _self.getWidget();
      _self.showAddFunctionDialog(function (data) {
        data.appFunctionUuid = data.uuid;
        data.configType = 2;
        data.nameRenderValue = '<i class="fa fa-file-code-o" aria-hidden="true" title="开发资源"></i>' + data.name;
        data.typeRenderValue = getTypeName(data.type);
        delete data.uuid;
        // 添加数据
        widget.getDataProvider().add(data);
      });
    },
    _getSystem: function () {
      return this.getWidgetParams().systemId;
    },
    showAddFunctionDialog: function (callback) {
      var _self = this;
      var dataList = _self.getData();
      var excludeDataUuids = $.map(dataList, function (data) {
        return data.appFunctionUuid;
      });
      appContext.require(['AppFunctionDialog'], function (AppFunctionDialog) {
        AppFunctionDialog.show({
          showRight: _self._getSystem() ? false : true,
          excludeDataUuids: excludeDataUuids,
          callback: callback
        });
      });
    },
    // 取消引用
    btn_cancel_ref: function () {
      var _self = this;
      var widget = _self.getWidget();
      if (!_self.checkSelection(true)) {
        return;
      }
      var selection = _self.getSelection();
      if (!_self.checkConfigType(selection)) {
        return;
      }
      appModal.confirm('确认取消引用资源？', function (result) {
        if (result) {
          // JDS.call({
          //     service : "appPageResourceManager.remove",
          //     data : [ selection ],
          //     async : false,
          //     success : function(result) {
          server.JDS.restfulPost({
            url: '/proxy/api/app/pagemanager/removeResource',
            data: selection,
            success: function (result) {
              appModal.success('取消引用资源成功！');
              var dataProvider = widget.getDataProvider();
              dataProvider.removeAll(selection);
            }
          });
        }
      });
    },
    checkConfigType: function (selection) {
      for (var i = 0; i < selection.length; i++) {
        if (selection[i].configType == '1') {
          appModal.error('请在页面设计界面中配置资源！');
          return false;
        }
      }
      return true;
    }
  });
  return AppPageResourceListViewWidgetDevelopment;
});
