define(['constant', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'AppPtMgrCommons'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  AppPtMgrCommons
) {
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;
  var Validation = server.Validation;
  var SelectiveDatas = server.SelectiveDatas;
  // 平台管理_业务流程管理_业务流程定义_配置项模板管理弹出框
  var AppBizProcessDefinitionTemplateDialog = {};

  AppBizProcessDefinitionTemplateDialog.show = function (options) {
    var _self = this;
    var title = '配置项模板管理';
    var dlgId = UUID.createUUID();
    var dlgSelector = '#' + dlgId;
    var message = "<div id='" + dlgId + "'></div>";
    var dlgOptions = {
      title: title,
      size: 'large',
      message: message,
      shown: function () {
        appContext.renderWidget({
          renderTo: dlgSelector,
          widgetDefId: 'wPanel_CA1073E3B2000001A13514F023F0E090',
          params: {
            processDefUuid: options.processDefUuid
          },
          callback: function () {},
          onPrepare: function () {}
        });
      },
      buttons: {
        confirm: {
          label: '关闭',
          className: 'btn-primary',
          callback: function () {
            return true;
          }
        }
      }
    };
    appModal.dialog(dlgOptions);
  };
  return AppBizProcessDefinitionTemplateDialog;
});
