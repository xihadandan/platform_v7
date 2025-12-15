define(['constant', 'commons', 'server', 'appContext', 'appModal', 'ztree', 'multiOrg', 'comboTree', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  ztree,
  multiOrg,
  comboTree,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppSysParamsWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppSysParamsWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var bean = {
        key: null,
        value: null,
        name: null,
        sourcetype: null,
        code: null,
        type: null
      };
      var validator = $.common.validation.validate('#sys_param_item_form', 'sysParamItem');
      var uuid = GetRequestParam().uuid;
      if (uuid) {
        getSysParamItem(uuid);
        var val = bean.sourcetype;
        $('#value_tr').show();
        if (val == '数据库') {
          $('#key').removeAttr('readonly');
          $('#value').removeAttr('readonly');
        } else {
          // $('#key').attr({
          //     readonly : "readonly"
          // });
          // $('#value').attr({
          //     readonly : "readonly"
          // });
        }
        var name;
        var type = $('#sourcetype').val();
        if (type == 0) {
          name = '数据库';
        } else if (type == 1) {
          name = 'JVM';
        } else if (type == 2) {
          name = '配置文件';
        }
        // $('#sourcetype_name').val(name);
        // $('#sourcetype_name').show();
        // $('#sourcetype').hide();
      } else {
        $('#sys_param_item_form').clearForm(true);
        $('#value').removeAttr('readonly');
        $('#key').removeAttr('readonly');
        $('#sourcetype').val(0);
        $('#sourcetype_name').hide();
        $('#sourcetype').show();
        $('#type').val('').trigger('change');
      }

      function getSysParamItem(uuid) {
        JDS.call({
          service: 'sysParamItemConfigMgr.getBean',
          data: uuid,
          async: false,
          version: '',
          success: function (result) {
            bean = result.data;
            $('#sys_param_item_form').json2form(bean);
            $('#type').trigger('change');
            validator.form();
          }
        });
      }

      $('#system_param_btn_save').click(function () {
        if (!validator.form()) {
          return false;
        }
        var sourcetype = $('#sourcetype').val();
        var value = $('#value').val();
        if (sourcetype == 0 && (value == null || value == '')) {
          appModal.error('值：不能为空！');
          return false;
        }
        $('#sys_param_item_form').form2json(bean);
        JDS.call({
          service: 'sysParamItemConfigMgr.saveBean',
          data: bean,
          version: '',
          success: function (result) {
            if (result.success) {
              appModal.success('保存成功！');
              appContext.getNavTabWidget().closeTab();
            }
          }
        });
      });

      $('#sourcetype').change(function () {
        var val = $(this).val();
        if (val == 0) {
          $('#value_tr').show();
          $('#value').removeAttr('readonly');
        } else {
          $('#value_tr').hide();
          // $('#value').attr({
          //     readonly : "readonly"
          // });
        }
      });

      // 分类下拉框 add by xiem
      $('#type').wSelect2({
        serviceName: 'dataDictionaryService',
        params: {
          type: 'MODULE_CATEGORY'
        },
        remoteSearch: false,
        width: '100%',
        height: 250
      });
    },
    refresh: function () {
      this.init();
    }
  });
  return AppSysParamsWidgetDevelopment;
});
