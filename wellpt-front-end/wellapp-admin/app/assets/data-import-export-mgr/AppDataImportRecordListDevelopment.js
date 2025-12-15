//
define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment'], function (constant, commons, server, ListViewWidgetDevelopment) {
  var AppDataImportRecordListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  //平台应用_数据导入导出管理_数据导入记录_列表二开
  commons.inherit(AppDataImportRecordListDevelopment, ListViewWidgetDevelopment, {
    // 查看
    btn_view: function (events, options, rowData) {
      var self = this;
      window.open(
        ctx + '/web/app/page/preview/56111a4071fa57436e77120e3d55eb67?pageUuid=34425c4e-0773-4be9-8081-98c53320447e&uuid=' + rowData.UUID
      );
    }
  });
  return AppDataImportRecordListDevelopment;
});
