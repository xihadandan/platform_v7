define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppOrgVerWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppOrgVerWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var formBean = {
        uuid: null,
        name: null,
        remark: null,
        status: null,
        functionType: null,
        functionTypeName: null,
        systemUnitId: null,
        isDefault: false
      };
      $('#org_version_form').json2form(formBean);
      // 从服务端获取表单的校验规则
      var validator = $.common.validation.validate('#org_version_form', 'orgVersionVo', function (options) {
        options.ignore = '';
      });
      initFunctionType($('#systemUnitId').val());
      var uuid = GetRequestParam().uuid;
      if (uuid) {
        showOrgVersionInfo(uuid);
      } else {
        $('#functionType').val('').trigger('change');
      }

      $('#org_ver_save').click(function () {
        if (!validator.form()) {
          return false;
        }
        $('#org_version_form').form2json(formBean);
        formBean.status = formBean.status ? '1' : '0';
        formBean.isDefault = $('#isDefault').prop('checked');
        formBean.systemUnitId = formBean.systemUnitId || SpringSecurityUtils.getCurrentUserUnitId();
        var service = '/proxy/api/org/version/' + (formBean.uuid ? 'modifyOrgVersionBaseInfo' : 'addMultiOrgVersion');
        // appModal.showMask(null, null, 999999);
        server.JDS.restfulPost({
          mask: true,
          type: 'POST',
          url: service,
          dataType: 'json',
          data: formBean,
          success: function (result) {
            appModal.hideMask();
            appModal.success('保存成功！');
            appContext.getNavTabWidget().closeTab();
          }
        });
      });

      function showOrgVersionInfo(uuid) {
        $.ajax({
          url: ctx + '/api/org/version/getOrgVersionVo',
          type: 'get',
          data: {
            orgVersionUuid: uuid
          },
          success: function (result) {
            var formBean = result.data;
            $('#org_version_form').json2form(formBean);
            $('#functionType')
              .wSelect2('data', {
                id: formBean.functionType,
                text: formBean.functionTypeName
              })
              .attr('readonly', 'readonly')
              .trigger('change');
          }
        });
      }

      function initFunctionType(systemUnitId) {
        $('#functionType').wSelect2({
          serviceName: 'multiOrgService',
          queryMethod: 'queryOrgTypeListForSelect2',
          labelField: 'functionTypeName',
          valueField: 'functionType',
          placeholder: '请选择',
          params: {
            systemUnitId: systemUnitId || SpringSecurityUtils.getCurrentUserUnitId()
          },
          multiple: false,
          remoteSearch: false,
          width: '100%',
          height: 250
        });
      }
    },
    refresh: function () {
      var _self = this;
      _self.init();
    }
  });
  return AppOrgVerWidgetDevelopment;
});
