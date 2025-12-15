define(['constant', 'commons', 'server', 'appContext', 'appModal', 'AppPtMgrListViewWidgetDevelopment', 'AppPtMgrCommons'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  AppPtMgrListViewWidgetDevelopment,
  AppPtMgrCommons
) {
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;

  // 平台管理_业务流程管理_业务事项定义列表_视图组件二开
  var AppBizItemDefinitionListViewWidgetDevelopment = function () {
    AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppBizItemDefinitionListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
    // 组件准备
    prepare: function () {},

    beforeRender: function (options, configuration) {},

    getSelectRowData: function (event) {
      var _self = this;
      var $toolbarDiv = $(event.target).closest('div');
      var rowData = [];
      if ($toolbarDiv.is('.div_lineEnd_toolbar')) {
        //行级点击操作
        var index = $toolbarDiv.attr('index');
        var allData = _self.getData();
        rowData = [allData[index]];
      } else {
        if (_self.getSelectionIndexes().length == 0) {
          return [];
        }
        rowData = _self.getSelections();
      }
      return rowData;
    },

    // 新增单个事项定义
    btn_single_item_add: function (event, ui) {
      // 触发列表行点击新增事件
      this.widget.trigger('AppBizItemDefinitionListView.editRow', {
        ui: this.widget,
        type: '10'
      });
    },

    // 新增串联事项定义
    btn_series_item_add: function (event, ui) {
      // 触发列表行点击新增事件
      this.widget.trigger('AppBizItemDefinitionListView.editRow', {
        ui: this.widget,
        type: '20'
      });
    },

    // 新增并联事项定义
    btn_parallel_item_add: function (event, ui) {
      // 触发列表行点击新增事件
      this.widget.trigger('AppBizItemDefinitionListView.editRow', {
        ui: this.widget,
        type: '30'
      });
    },

    // 删除
    btn_delete: function (event, ui) {
      var _self = this;
      var rowData = _self.getSelectRowData(event);
      if (rowData.length > 0) {
        var name = rowData[0].name;
        appModal.confirm('确认要删除业务事项定义吗?', function (result) {
          if (result) {
            JDS.restfulPost({
              url: '/proxy/api/biz/item/definition/deleteAll',
              traditional: true,
              data: {
                uuids: (function () {
                  var uuids = [];
                  for (var i = 0, len = rowData.length; i < len; i++) {
                    uuids.push(rowData[i].uuid);
                  }
                  return uuids;
                })()
              },
              contentType: 'application/x-www-form-urlencoded',
              success: function (result) {
                appModal.info('刪除成功');
                _self.refresh(); //刷新表格
              }
            });
          }
        });
      } else {
        appModal.error('请选择记录！');
        return false;
      }
    },

    definition_import: function () {
      var _self = this;
      // 定义导入
      $.iexportData['import']({
        callback: function () {
          _self.refresh();
        }
      });
    },

    definition_export: function (event) {
      // 定义导出
      var rowData = this.getSelectRowData(event);
      if (rowData.length > 0) {
        $.iexportData['export']({
          uuid: rowData[0].uuid,
          type: 'bizItemDefinition'
        });
      } else {
        appModal.alert('请选择导出的事项定义！');
      }
    },

    // 行点击查看详情
    onClickRow: function (rowNum, rowData, $element, field) {
      // 触发应用列表行点击事件
      this.widget.trigger('AppBizItemDefinitionListView.editRow', {
        rowData: rowData,
        ui: this.widget,
        type: rowData.type
      });
    },

    afterRender: function (options, configuration) {
      var _self = this;
      this.widget.on('AppBizItemDefinitionListView.refresh', function () {
        _self.refresh();
      });
    }
  });
  return AppBizItemDefinitionListViewWidgetDevelopment;
});
