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
  var Validation = server.Validation;
  var FileDownloadUtils = server.FileDownloadUtils;

  // 平台管理_产品集成_主题定义列表_视图组件二开
  var AppThemeDefinitionListViewWidgetDevelopment = function () {
    AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppThemeDefinitionListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
    // 新增
    btn_add: function () {
      var _self = this;
      var widget = _self.getWidget();
      // 触发应用列表行点击新增事件
      widget.trigger('AppThemeDefinitionListView.addRow', {
        ui: _self
      });
    },
    // 删除
    btn_del: function () {
      var _self = this;
      if (!_self.checkSelection()) {
        return;
      }
      var selection = _self.getSelection();
      var name = selection[0].name;
      appModal.confirm('确定要删除主题[' + name + ']吗？', function (result) {
        if (result) {
          server.JDS.restfulPost({
            url: ctx + '/proxy/api/app/theme/definition/deleteAll',
            traditional: true,
            data: {
              uuids: [selection[0].uuid]
            },
            contentType: 'application/x-www-form-urlencoded',
            success: function (result) {
              appModal.info('刪除成功');
              _self.refresh(); //刷新表格
            }
          });
        }
      });
    },
    // 定义导出
    btn_export: function () {
      var _self = this;
      if (!_self.checkSelection()) {
        return;
      }
      var selection = _self.getSelection();
      var rowData = selection[0];
      var uuid = rowData.uuid;
      var type = 'appThemeDefinition';
      $.iexportData['export']({
        uuid: uuid,
        type: type
      });
    },
    // 定义导入
    btn_import: function () {
      var _self = this;
      $.iexportData['import']({
        callback: function () {
          _self.refresh();
        }
      });
    },
    // 定义导入日志
    btn_imp_log: function () {
      $.iexportData.viewImportLog({});
    },
    // 集成信息导出
    btn_pi_info_export: function () {
      var _self = this;
      if (!_self.checkSelection(true)) {
        return;
      }
      var selection = _self.getSelection();
      for (var i = 0; i < selection.length; i++) {
        var piInfoExportUrl = getBackendUrl() + '/app/app/product/integration/export/pi/' + selection[i].appPiUuid;
        FileDownloadUtils.downloadTools(piInfoExportUrl);
      }
    },
    // 行点击查看应用详情详情
    onClickRow: function (rowNum, rowData, $element, field) {
      var _self = this;
      var widget = _self.getWidget();
      // 触发应用列表行点击事件
      widget.trigger('AppThemeDefinitionListView.clickRow', {
        rowData: rowData
      });
    },

    afterRender: function (options, configuration) {
      var _self = this;
      this.widget.on('RefreshView', function () {
        _self.refresh();
      });
    }
  });
  return AppThemeDefinitionListViewWidgetDevelopment;
});
