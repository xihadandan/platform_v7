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

  // 平台管理_产品集成_产品列表_视图组件二开
  var AppProductListViewWidgetDevelopment = function () {
    AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppProductListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
    // 组件准备
    prepare: function () {
      var _self = this;
      // 获取传给组件的参数
      var params = _self.getWidgetParams();
      // 触发列表准备加载事件
      _self.trigger('AppListView.prepare', params);
    },
    // 新增
    btn_add: function () {
      var _self = this;
      var widget = _self.getWidget();
      // 触发产品列表行点击新增事件
      widget.trigger('AppProductListView.addRow', {
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
      appModal.confirm('确定要删除产品[' + name + ']吗？', function (result) {
        if (result) {
          JDS.call({
            service: 'appProductMgr.remove',
            data: selection[0].uuid,
            success: function (result) {
              appModal.success('删除成功！');
              _self.trigger('AppProductListView.deleteRow', {
                rowData: selection[0]
              });
              // 删除成功刷新列表
              _self.refresh();
            }
          });
        }
      });
    },
    // 定义导出
    btn_export: function () {
      var _self = this;
      if (!_self.checkSelection(true)) {
        return;
      }
      var uuid = _self.getSelectionUuids().join(';');
      var type = 'appProduct';
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
        },
        ext: ['defpf']
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
        var piInfoExportUrl = getBackendUrl() + '/app/app/product/integration/export/' + selection[i].uuid;
        FileDownloadUtils.downloadTools(piInfoExportUrl);
      }
    },
    // 产品管理
    btn_prd_mgr: function (e, options, rowData) {
      console.log(rowData);
    },
    // 行点击查看产品详情详情
    onClickRow: function (rowNum, rowData, $element, field) {
      var _self = this;
      var widget = _self.getWidget();
      // 触发产品列表行点击事件
      widget.trigger('AppProductListView.clickRow', {
        rowData: rowData
      });
    }
  });
  return AppProductListViewWidgetDevelopment;
});
