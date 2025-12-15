define(['constant', 'commons', 'server', 'appContext', 'appModal', 'wSelect2', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  wSelect2,
  HtmlWidgetDevelopment
) {
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;

  // 平台管理_产品集成_页面引用资源列表_视图组件二开

  var AppEmailSetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppEmailSetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var form_selector = '#wm_mail_config_form';
      // WmMailConfig的VO类
      var bean = {
        uuid: null, // UUID
        recVer: null, // 版本号
        domain: null, // 域名
        pop3Server: null, // POP3服务器
        pop3Port: null, // POP3服务器端口110
        smtpServer: null, // 发送服务器
        smtpPort: null, // 发送服务器端口25
        imapServer: null, // IMAP服务器
        imapPort: null, // IMAP服务器端口143
        keepOnServer: null, // 在服务器保留备份
        defaultCapacity: null,
        deadlineCapacity: null,
        mailServerType: null,
        apiPort: null,
        attachmentSizeLimit: null,
        isPublicEmail: null, // 是否公网邮箱
        sendReceipt: null // 是否自动发送回执 0：否，1：是
      };
      var validator = $.common.validation.validate(form_selector, 'wmMailConfigEntity');
      var wmMailConfig = {};

      // 根据UUID获取组织选择项
      function getWmMailConfig(uuid) {
        wmMailConfig.uuid = uuid;
        JDS.call({
          service: 'wmMailConfigMgr.getBean',
          data: wmMailConfig.uuid,
          version: '',
          success: function (result) {
            bean = result.data;
            $('#mail_btn_del').show();
            $(form_selector).json2form(bean);
            validator.form();
            $('#mailServerType').trigger('change');
            if (bean.defaultCapacity != null) {
              $('#mailCapacityLimit').prop('checked', true);
              $('.deadlineCapacity').show();
              $('.defaultCapacity').show();
            }
          }
        });
      }

      function reloadWmMailConfig(reset) {
        JDS.call({
          service: 'wmMailConfigMgr.getConfig',
          version: '',
          success: function (result) {
            if (result.data != null) {
              bean = result.data;
              $('#mail_btn_del').show();
              $('#mail_btn_reset').show();
              $(form_selector).json2form(bean);
              validator.form();
              $('#mailServerType').trigger('change');
              $('#allowOrgOptions').wellSelect('val', bean.allowOrgOptions).trigger('change');
              if (bean.defaultCapacity != null) {
                $('#mailCapacityLimit').prop('checked', true);
                $('.deadlineCapacity').show();
                $('.defaultCapacity').show();
              }
              if (reset) {
                $('#mail_btn_reset').trigger('click');
              }
            }
          }
        });
      }

      $('#allowOrgOptions').wSelect2({
        serviceName: 'multiOrgOptionService',
        queryMethod: 'loadSelectDataNoId',
        params: { excludeId: 'Role' },
        multiple: true,
        remoteSearch: false
      });

      $('#mailServerType')
        .wSelect2({
          data: [
            {
              id: 'JamesMail',
              text: 'JamesMail'
            },
            {
              id: 'CoreMail',
              text: 'CoreMail'
            }
          ],
          valueField: 'mailServerType',
          remoteSearch: false
        })
        .on('change', function () {
          $('.form-mailServerType').hide();
          $('.form-mailServerType')
            .filter("[mailserver='" + $(this).val() + "']")
            .show();
        });

      var configUuid = $('#uuid').val();
      if (StringUtils.isNotBlank(configUuid)) {
        getWmMailConfig(configUuid);
      } else {
        reloadWmMailConfig(false);
        $('#mail_btn_reset').hide();
      }

      $('#mail_btn_reset').on('click', function () {
        if (!validator.form()) {
          return false;
        }
        if (StringUtils.isBlank(bean.uuid)) {
          appModal.error('请先保存配置');
          return false;
        }
        appModal.confirm('确定要重置所有用户的邮箱客户端密码为初始化密码吗？', function (res) {
          if (res) {
            JDS.call({
              service: 'wmMailConfigMgr.resetMailUser',
              data: bean.uuid,
              version: '',
              success: function (result) {
                appModal.success('重置成功!');
              }
            });
          }
        });
      });

      $('#isPublicEmail').on('change', function () {
        if ($(this).prop('checked')) {
          if (!$('#attachmentSizeLimit').val()) {
            $('#attachmentSizeLimit').val('50');
          }
        }
      });

      $('#mailCapacityLimit').on('change', function () {
        if ($(this).prop('checked')) {
          $('.deadlineCapacity').show();
          $('.defaultCapacity').show();
        } else {
          $('.deadlineCapacity').hide();
          $('.defaultCapacity').hide();
        }
        $('#defaultCapacity,#deadlineCapacity').val('');
      });

      // 保存用户信息
      $('#mail_btn_save').click(function () {
        if (!validator.form()) {
          return false;
        }
        $(form_selector).form2json(bean);
        if ($('#mailCapacityLimit').prop('checked')) {
          var defaultCapacity = $('#defaultCapacity').val();
          var deadlineCapacity = $('#deadlineCapacity').val();
          var capacityTip = '';
          if (!/^[1-9]\d*$/.test(defaultCapacity)) {
            capacityTip += '默认限额取值必须为正整数';
            $('#defaultCapacity').val('');
          }
          if (!/^[1-9]\d*$/.test(deadlineCapacity) && parseInt(deadlineCapacity) > 100) {
            capacityTip += '</br>容量提醒取值必须为1-100的正整数';
            $('#deadlineCapacity').val('');
          }
          if (capacityTip) {
            appModal.error(capacityTip);
            return false;
          }
        }
        JDS.call({
          service: 'wmMailConfigMgr.saveBean',
          data: bean,
          version: '',
          success: function (result) {
            appModal.success('保存成功');
            // 删除成功刷新列表
            reloadWmMailConfig(false);
          }
        });
      });

      // 删除操作
      $('#mail_btn_del').click(function () {
        if (bean.uuid == '' || bean.uuid == null) {
          appModal.error('请选择记录！');
          return true;
        }
        var name = bean.name;
        appModal.confirm('确定要删除选择项[' + name + ']吗？', function (result) {
          if (result) {
            JDS.call({
              service: 'wmMailConfigMgr.remove',
              data: bean.uuid,
              version: '',
              success: function (result) {
                appModal.success('删除成功!');
                $(form_selector).clearForm(true);
              }
            });
          }
        });
      });
    }
  });
  return AppEmailSetDevelopment;
});
