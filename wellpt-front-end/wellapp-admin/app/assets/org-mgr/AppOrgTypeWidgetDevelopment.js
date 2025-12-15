define(['constant', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppOrgTypeWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppOrgTypeWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var formBean = {
        uuid: null,
        id: null,
        name: null,
        code: null,
        remark: null,
        isForbidden: 0
      };
      $('#org_type_form').json2form(formBean);

      var validator = $.common.validation.validate('#org_type_form', 'multiOrgType', function (options) {
        options.ignore = '';
      });
      var uuid = GetRequestParam().uuid;
      if (uuid) {
        showOrgTypeInfo(uuid);
      }

      // 定义保存按钮的事件
      $('#org_type_save').click(function () {
        if (!validator.form()) {
          return false;
        }
        $('#org_type_form').form2json(formBean);

        formBean.isForbidden = formBean.isForbidden ? 1 : 0;
        formBean.systemUnitId = formBean.systemUnitId || SpringSecurityUtils.getCurrentUserUnitId();

        var url = '/api/org/multi/' + (formBean.uuid ? 'modifyOrgType' : 'addOrgType');
        $.ajax({
          type: 'POST',
          url: url,
          dataType: 'json',
          data: formBean,
          success: function (data) {
            appModal.success('保存成功！');
            appContext.getNavTabWidget().closeTab();
          }
        });
      });

      function showOrgTypeInfo(uuid) {
        $.ajax({
          url: ctx + '/api/org/multi/getOrgType',
          type: 'get',
          data: {
            uuid: uuid
          },
          success: function (result) {
            formBean = result.data;
            $('#org_type_form').json2form(formBean);
            if (formBean.isForbidden == 1) {
              $('#isForbidden').attr('checked', 'checked');
            } else {
              $('#isForbidden').removeAttr('checked');
            }
            $('#id').attr('readonly', 'readonly').addClass('input-disabled');
          }
        });
      }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppOrgTypeWidgetDevelopment;
});
