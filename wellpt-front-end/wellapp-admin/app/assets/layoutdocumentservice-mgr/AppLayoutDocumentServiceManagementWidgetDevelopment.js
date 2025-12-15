define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppLayoutDocumentServiceManagementWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppLayoutDocumentServiceManagementWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var _self = this;

      var bean = {
        serverName: null,
        serverUniqueCode: null,
        serverUniqueCodeTemp: null,
        code: null,
        serverUrl: null,
        fileExtensions: null,
        status: null,
        priority: null
      };
      var formId = 'layoutdocument_service_conf_form';
      var $form = $('#' + formId);

      var validator = $.common.validation.validate('#' + formId, 'layoutDocumentServiceConfEntity');
      var uuid = GetRequestParam().uuid;
      if (uuid) {
        getLayoutDocumentServiceConf(uuid);
      } else {
        setStatusSwitch();
        setServiceNameSelect();
      }

      function setStatusSwitch() {
        // 自动刷新
        var $statusSetSwitch = $('#' + formId + ' #statusSetSwitch');
        if (bean.status === '1') {
          $statusSetSwitch.addClass('active');
          $('#' + formId + ' #status').val(bean.status);
        } else {
          $statusSetSwitch.removeClass('active');
          $('#' + formId + ' #status').val('0');
        }
        $statusSetSwitch.on('click', function () {
          if ($(this).hasClass('active')) {
            $(this).removeClass('active');
            $('#' + formId + ' #status').val('0');
          } else {
            $(this).addClass('active');
            $('#' + formId + ' #status').val('1');
          }
        });
      }

      function setServiceNameSelect() {
        $('#serverName', $form)
          .wSelect2({
            defaultBlank: true,
            serviceName: 'dataDictionaryService',
            container: $form,
            params: {
              type: 'LAYOUT_DOCUMENT_SERVICE'
            },
            labelField: 'serverName',
            queryMethod: 'loadSelectData',
            valueField: 'serverUniqueCodeTemp',
            remoteSearch: false
          })
          .on('change', function () {
            $('#serverUniqueCode', $form).val($('#serverUniqueCodeTemp', $form).val());
          })
          .trigger('change');
      }

      function getLayoutDocumentServiceConf(uuid) {
        $.ajax({
          // 版式文档服务配置
          url: ctx + `/api/basicdata/layoutDocumentServiceConf/getByUuid?uuid=${uuid}`,
          type: 'get',
          // data: {uuids: uuids},
          success: function (res) {
            if (res.code === 0) {
              bean = res.data;
              bean.serverUniqueCodeTemp = bean.serverUniqueCode;

              $form.json2form(bean);

              setStatusSwitch();

              setServiceNameSelect();
              validator.form();
            } else {
              appModal.error(res.msg);
            }
          }
        });
      }

      $('#layoutdocument_service_conf_btn_save').click(function () {
        if (!validator.form()) {
          return false;
        }
        var sourcetype = $('#sourcetype').val();
        var value = $('#value').val();
        if (sourcetype == 0 && (value == null || value == '')) {
          appModal.error('值：不能为空！');
          return false;
        }

        $form.form2json(bean);

        if (bean.status === '1') {
          appModal.confirm(
            '系统仅允许存在一个"启用"的服务，继续保存将修改其他"启用"的服务的状态为"禁用"，是否确定保存？',
            function (result) {
              if (result) {
                _self._saveBean(bean);
              }
            }
          );
        } else {
          _self._saveBean(bean);
        }
      });
    },

    _saveBean: function (bean) {
      $.ajax({
        // 删除版式文档服务配置
        url: ctx + '/api/basicdata/layoutDocumentServiceConf/saveBean',
        type: 'post',
        data: bean,
        success: function (res) {
          if (res.code === 0) {
            appModal.success('保存成功！');
            appContext.getNavTabWidget().closeTab();
          } else {
            appModal.error(res.msg);
          }
        }
      });
    },

    refresh: function () {
      this.init();
    }
  });
  return AppLayoutDocumentServiceManagementWidgetDevelopment;
});
