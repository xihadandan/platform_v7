define([
  'jquery',
  'constant',
  'commons',
  'server',
  'appContext',
  'appModal',
  'AppDyformDesignerSimpleUtils',
  'HtmlWidgetDevelopment',
  'AppDyformDesignerSimpleEditExtendDevelopment'
], function ($, constant, commons, server, appContext, appModal, DesignerUtils, HtmlWidgetDevelopment) {
  var JDS = server.JDS;

  var AppDyformDesignerDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 平台管理_公共资源_表单管理编辑组件二开
  commons.inherit(AppDyformDesignerDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var _self = this;
      //打开的是iframe需要从src上获取参数
      var formUuid = Browser.getQueryString('uuid');
      var formType = Browser.getQueryString('formType');
      var cFormUuid = Browser.getQueryString('cFormUuid');
      if (cFormUuid === null) {
        cFormUuid = null;
        formType = 'C';
      }
      if (cFormUuid) {
        formType = 'C';
      }
      addDyformTypeScript(cFormUuid || formUuid, formType);
      // P:存储单据, V:展现单据, M: 手机单据 MST: 子单据(模板单据)
      _self.widget.element.find('.dyform-' + formType).removeClass('hide');
    },
    refresh: function () {
      this.init();
    }
  });
  return AppDyformDesignerDevelopment;
});
