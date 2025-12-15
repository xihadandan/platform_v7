define(['constant', 'commons', 'server', 'appContext', 'appModal', 'wSelect2', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  wSelect2,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppDmsRoleWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppDmsRoleWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var bean = {
        uuid: null, // UUID
        recVer: null, // 版本号
        actions: null, // ACTIONS
        code: null, // CODE
        id: null, // ID
        name: null, // NAME
        category: null, // CATEGORY
        remark: null, // REMARK
        systemUnitId: null
      };
      var roleActions = [
        { id: 'createFolder', name: '创建夹' },
        { id: 'readFolder', name: '读取当前夹' },
        { id: 'listFolder', name: '列出当前夹下的子夹' },
        { id: 'listAllFolder', name: '列出当前夹下的所有子夹(包含子夹)' },
        { id: 'listFiles', name: '列出当前夹下的文件' },
        { id: 'listAllFiles', name: '列出当前夹下的文件(包含子夹)' },
        { id: 'listFolderAndFiles', name: '列出当前夹下的子夹及文件' },
        { id: 'listAllFolderAndFiles', name: '列出当前夹下的子夹及文件(包含子夹)' },
        { id: 'shareFolder', name: '分享夹' },
        { id: 'viewFolderAttributes', name: '查看夹属性' },
        { id: 'editFolderAttributes', name: '编辑夹属性' },
        { id: 'renameFolder', name: '重命名夹' },
        { id: 'moveFolder', name: '移动夹' },
        { id: 'copyFolder', name: '复制夹' },
        { id: 'deleteFolder', name: '删除夹(有子夹、文件不可删除)' },
        { id: 'forceDeleteFolder', name: '删除夹(有子夹、文件一起删除)' },
        { id: 'createFile', name: '创建文件' },
        { id: 'createDocument', name: '创建文档' },
        { id: 'readFile', name: '读取文件' },
        { id: 'openFile', name: '打开文件' },
        { id: 'downloadFile', name: '下载文件' },
        { id: 'shareFile', name: '分享文件' },
        { id: 'viewFileAttributes', name: '查看文件属性' },
        { id: 'renameFile', name: '重命名文件' },
        { id: 'editFile', name: '编辑文件' },
        { id: 'moveFile', name: '移动文件' },
        { id: 'copyFile', name: '复制文件' },
        { id: 'deleteFile', name: '删除文件' }
      ];
      var validator = $.common.validation.validate('#dms_role_form', 'dmsRoleEntity');

      var uuid = GetRequestParam().uuid;
      var actions = GetRequestParam().action;
      var roleHtml = '';
      $.each(roleActions, function (index, item) {
        if (index % 3 == 0) {
          roleHtml += "<div class='role-actions-item'>";
        }
        roleHtml +=
          "<input type='checkbox' id='" +
          item.id +
          "' name='actions' value='" +
          item.id +
          "'><label for='" +
          item.id +
          "'>" +
          item.name +
          '</label>';
        if (index % 3 == 2 || index == roleActions.length - 1) {
          roleHtml += '</div>';
        }
      });
      $('#actions').html(roleHtml);

      if (uuid) {
        getDmsRole(uuid);
      } else {
        $.common.idGenerator.generate('#id', 'RA_');
      }

      function getDmsRole(uuid) {
        JDS.call({
          service: 'dmsRoleMgr.getBean',
          data: uuid,
          version: '',
          success: function (result) {
            bean = result.data;

            if (actions == 'copy') {
              bean.uuid = null;
              bean.id = null;
              bean.code = null;
              $('#dms_role_form').json2form(bean, true);
              $.common.idGenerator.generate('#id', 'RA_');
              $('#id').prop('readonly', '');
            } else {
              $('#dms_role_form').json2form(bean);
              $('#id').prop('readonly', 'readonly');
              var newActions = bean.actions.split(',');
              for (var i = 0; i < newActions.length; i++) {
                $("input[name='actions'][value='" + newActions[i] + "']").attr('checked', true);
              }
            }
            validator.form();
            $('#category').trigger('change');
          }
        });
      }

      $('#category').wSelect2({
        serviceName: 'dataDictionaryService',
        params: {
          type: 'MODULE_CATEGORY'
        },
        remoteSearch: false,
        width: '100%',
        height: 250
      });

      $('#role_btn_save').click(function () {
        if (!validator.form()) {
          return false;
        }
        $('#dms_role_form').form2json(bean);
        bean.actions = bean.actions.join(',');
        JDS.call({
          service: 'dmsRoleMgr.saveBean',
          data: bean,
          version: '',
          success: function (result) {
            appModal.success('保存成功！');
            appContext.getNavTabWidget().closeTab();
          },
          error: function () {}
        });
      });
    },
    refresh: function () {
      this.init();
    }
  });
  return AppDmsRoleWidgetDevelopment;
});
