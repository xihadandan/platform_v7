define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppOrgPostWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppOrgPostWidgetDevelopment, HtmlWidgetDevelopment, {
    // 初始化回调
    init: function () {
      var formBean = {
        uuid: null,
        id: null,
        name: null,
        systemUnitId: null,
        code: null,
        sapCode: null,
        remark: null
      };
      $('#org_duty_form').json2form(formBean);
      var validator = $.common.validation.validate('#org_duty_form', 'multiOrgDuty', function (options) {
        options.ignore = '';
      });

      var uuid = GetRequestParam().uuid;
      if (uuid) {
        showDutyInfo(uuid);
      }

      $('#org_post_save').click(function () {
        if (!validator.form()) {
          return false;
        }
        $('#org_duty_form').form2json(formBean);

        formBean.systemUnitId = formBean.systemUnitId || SpringSecurityUtils.getCurrentUserUnitId();

        var url = '/api/org/multi/' + (formBean.uuid ? 'modifyDuty' : 'addDuty');
        $.ajax({
          type: 'POST',
          url: url,
          dataType: 'json',
          data: formBean,
          success: function (result) {
            if (result.code == 0) {
              appModal.success('保存成功！');
              appContext.getNavTabWidget().closeTab();
            } else {
              appModal.error(result.msg);
            }
          }
        });
      });

      function showDutyInfo(uuid) {
        $.ajax({
          url: ctx + '/api/org/multi/getDuty',
          type: 'get',
          data: {
            uuid: uuid
          },
          success: function (result) {
            formBean = result.data;
            $('#org_duty_form').json2form(formBean);
          }
        });
      }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppOrgPostWidgetDevelopment;
});
