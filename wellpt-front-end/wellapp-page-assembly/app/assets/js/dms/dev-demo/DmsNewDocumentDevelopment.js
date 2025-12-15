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

  var UrlUtils = commons.UrlUtils;

  // DMS_新建文档二开_Demo
  var DmsNewDocumentDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(DmsNewDocumentDevelopment, ListViewWidgetDevelopment, {
    // 准备创建回调
    prepare: function () {},

    // 新建文档
    newDocDocument: function () {
      // 关联的文档交换器 UUID
      var dms_id = 'wDocExchanger_C8F1E52DE7400001A2291BBE14181CBE';
      // 传入的默认值
      var defaultValue = { doc_title: 'test' };
      // 操作类型，无需更改
      var ac_id = 'btn_list_view_add_doc_exchanger';

      var url = UrlUtils.appendUrlParams('/dms/data/services', {
        dms_id: dms_id,
        ac_id: ac_id,
        default: JSON.stringify(defaultValue)
      });

      appContext.openWindow({ id: ac_id, url: url });
    }
  });

  return DmsNewDocumentDevelopment;
});
