define(['constant', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  ListViewWidgetDevelopment
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var JDS = server.JDS;

  // 平台应用_报表管理_ureport报表列表二开
  var AppUreportList = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppUreportList, ListViewWidgetDevelopment, {
    // 准备创建回调
    prepare: function () {},
    // 创建后回调
    create: function () {},
    // 初始化回调
    init: function () {},
    btn_add: function () {
      this.openReport();
    },
    btn_del: function () {
      var _self = this;
      var uuids = _self.getSelectionUuids();
      if (!uuids.length) {
        appModal.error('请选择记录！');
        return;
      }
      appModal.confirm('确定要删除报表吗？', function (result) {
        if (result) {
          JDS.call({
            service: 'rpFileRepositoryService.deleteRps',
            data: [uuids],
            success: function (result) {
              appModal.success('删除成功！');
              _self.refresh();
            }
          });
        }
      });
    },
    btn_export: function () {
      this.onExport('ureportFileRepository');
    },
    btn_import: function () {
      this.onImport();
    },
    btn_edit: function (event, ui, rowData) {
      this.openReport(rowData);
    },
    btn_preview: function (event, ui, rowData) {
      this.openReport(rowData, 'preview');
    },
    openReport: function (rowData, type) {
      var url = getBackendUrl() + '/ureport/' + (type ? type : 'designer');

      function getCookie(name) {
        var reg = new RegExp('(^| )' + name + '=([^;]*)(;|$)');
        var arr = document.cookie.match(reg);
        return arr ? unescape(arr[2]) : null;
      }

      var jwt = getCookie('jwt');
      var _loginid = getCookie('_loginid');

      if (rowData) {
        var _u = rowData.fileId ? 'mongo:' : 'db:';
        url += '?_u=' + _u + rowData.fileName + '&jwt=' + jwt + '&_loginid=' + _loginid;
      }
      if (type === 'preview') {
        appContext.getNavTabWidget().createTab('ureport', 'ureport报表-预览', 'iframe', url);
      } else {
        var params = {};
        if (rowData) {
          params.tableBtnEditTag = rowData.uuid;
        }
        appContext.getNavTabWidget().createTab('ureport', rowData ? 'ureport报表-编辑' : 'ureport报表-新增', 'iframe', url);
      }
    }
  });
  return AppUreportList;
});
