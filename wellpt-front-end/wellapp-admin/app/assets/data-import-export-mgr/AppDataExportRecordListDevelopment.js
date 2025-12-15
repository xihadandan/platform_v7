//
define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment'], function (constant, commons, server, ListViewWidgetDevelopment) {
  var AppDataExportRecordListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  //平台应用_数据导入导出管理_数据导出记录_列表二开
  commons.inherit(AppDataExportRecordListDevelopment, ListViewWidgetDevelopment, {
    // 查看
    btn_view: function (events, options, rowData) {
      var self = this;
      window.open(
        ctx + '/web/app/page/preview/6001a4908f9afe87a4b4b9a278bff001?pageUuid=c38c12c7-f479-4d9b-8421-76b8ad82154e&uuid=' + rowData.UUID
      );
    }
  });
  return AppDataExportRecordListDevelopment;
});
