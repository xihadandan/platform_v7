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

  // 平台管理_产品集成_模块_新版流水号定义列表_视图组件二开
  var AppSnSerialNumberDefinitionListViewWidgetDevelopment = function () {
    AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppSnSerialNumberDefinitionListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
    // 组件准备
    prepare: function () { },

    beforeRender: function (options, configuration) {
      // 归属模块ID
      this.widget.addParam('moduleId', this._moduleId());
    },

    _moduleId: function () {
      return this.getWidgetParams().moduleId;
    },

    // 删除
    btn_delete: function (event, ui) {
      var _self = this;
      var rowData = _self.getSelectRowData(event);
      if (rowData.length == 1) {
        var name = rowData[0].name;
        appModal.confirm(`确认要删除流水号定义[${name}]吗?`, function (result) {
          if (result) {
            server.JDS.restfulPost({
              url: ctx + '/proxy/api/sn/serial/number/definition/deleteWhenNotUsed',
              data: {
                uuid: rowData[0].uuid
              },
              contentType: 'application/x-www-form-urlencoded',
              success: function (result) {
                if (result.data == "-1") {
                  appModal.error('流水号被引用，无法删除！');
                } else {
                  appModal.info('刪除成功');
                  _self.refresh(); //刷新表格
                }
              }
            });
          }
        });
      } else {
        appModal.error('请选择一条记录！');
        return false;
      }
    },

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

    btn_add: function () {
      // 触发数据仓库列表行点击新增事件
      this.widget.trigger('AppSnSerialNumberDefinitionListView.editRow', {
        ui: this.widget
      });
    },

    // 行点击查看详情
    onClickRow: function (rowNum, rowData, $element, field) {
      // 触发应用列表行点击事件
      this.widget.trigger('AppSnSerialNumberDefinitionListView.editRow', {
        rowData: rowData,
        ui: this.widget
      });
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

    definition_export: function (event, ui) {
      // 定义导出
      var rowData = this.getSelectRowData(event);
      if (rowData.length > 0) {
        $.iexportData['export']({
          uuid: rowData[0].uuid,
          type: 'snSerialNumberDefinition'
        });
      } else {
        appModal.alert('请选择导出的流水号定义！');
      }
    },

    afterRender: function (options, configuration) {
      var _self = this;
      this.widget.on('AppSnSerialNumberDefinitionListView.refresh', function () {
        _self.refresh();
      });
    },

    refresh: function () {
      //刷新徽章
      var tabpanel = this.widget.element.parents('.active');
      if (tabpanel.length > 0) {
        var id = tabpanel.attr('id');
        id = id.substring(0, id.indexOf('-'));
        $('#' + id).trigger(constant.WIDGET_EVENT.BadgeRefresh, {
          targetTabName: '流水号定义'
        });
      }
      return this.getWidget().refresh(this.options);
    }
  });
  return AppSnSerialNumberDefinitionListViewWidgetDevelopment;
});
