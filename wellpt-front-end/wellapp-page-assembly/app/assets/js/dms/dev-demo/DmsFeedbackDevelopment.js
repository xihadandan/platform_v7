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

  // DMS_反馈弹窗二开_Demo
  var DmsFeedbackDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(DmsFeedbackDevelopment, ListViewWidgetDevelopment, {
    // 准备创建回调
    prepare: function () {},

    // 反馈弹窗
    feedbackTest: function () {
      var rows = this.getSelections();
      if (rows.length !== 1) {
        appModal.error('必须选择一行');
      }

      var docExcRecordUuid = rows[0].rUuid;

      appContext.require(['DmsFeedbackDialog'], function (showDialog) {
        showDialog({ docExcRecordUuid: docExcRecordUuid });
      });
    }
  });

  return DmsFeedbackDevelopment;
});
