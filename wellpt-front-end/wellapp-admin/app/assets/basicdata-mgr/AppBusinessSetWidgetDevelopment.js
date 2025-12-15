define(['constant', 'commons', 'server', 'appContext', 'appModal', 'ztree', 'comboTree', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  ztree,
  comboTree,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppBusinessSetWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppBusinessSetWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var bean = {
        uuid: null,
        attrValue: '',
        attrName: ''
      };

      var setting = {
        async: {
          otherParam: {
            serviceName: 'flowOpinionCategoryService',
            methodName: 'getFlowOpinionCategoryTreeByBusinessAppDataDic',
            data: [null]
          }
        }
      };

      JDS.call({
        version: null,
        service: 'multiOrgSystemUntAttrService.getCurrentUnitOneAttr',
        data: ['PT_BUSINESS_APP_CATEGORY'],
        sync: false,
        success: function (result) {
          bean = result.data || bean;
          $('#business_set_form').json2form(bean);
          $('#attrValue').val(bean.attrValue);
          $('#attrName').val(bean.attrName);
          $('#attrName').comboTree({
            labelField: 'attrName',
            valueField: 'attrValue',
            treeSetting: setting,
            width: 220,
            height: 220,
            mutiSelect: true,
            autoInitValue: false,
            autoCheckByValue: true
          });
        }
      });

      $('#business_set_btn_save').click(function () {
        $('#business_set_form').form2json(bean);
        if (!bean.attrValue) {
          appModal.error('请先选择所属行业');
          return false;
        }
        JDS.call({
          version: null,
          service: 'multiOrgSystemUntAttrService.saveUpdateCurrentUnitAttr',
          data: {
            attrCode: 'PT_BUSINESS_APP_CATEGORY',
            attrName: bean.attrName,
            attrValue: bean.attrValue,
            remark: '系统单位的行业设置',
            uuid: bean.uuid
          },
          success: function (result) {
            if (result.success) {
              appModal.success('保存成功');
            } else {
              appModal.error(result.msg);
              return false;
            }
          },
          error: function (err) {
            appModal.error('未知错误');
            return false;
          }
        });
      });
    },
    refresh: function () {
      this.init();
    }
  });
  return AppBusinessSetWidgetDevelopment;
});
