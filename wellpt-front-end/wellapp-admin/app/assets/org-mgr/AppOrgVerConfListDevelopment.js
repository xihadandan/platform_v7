define(['constant', 'commons', 'server', 'appContext', 'appModal', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  ListViewWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppOrgVerConfListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppOrgVerConfListDevelopment, ListViewWidgetDevelopment, {
    getTableOptions: function (bootstrapTableOptions) {
      console.log(bootstrapTableOptions);
      var unit = SystemUnit.getUnitById(GetRequestParam().systemUnitId);
      if (unit['isGroupUnit'] == 1) {
        $(bootstrapTableOptions.toolbar).find('.btn_class_btn_add_sys_unit').show();
      } else {
        $(bootstrapTableOptions.toolbar).find('.btn_class_btn_add_sys_unit').hide();
      }
      var versionId = GetRequestParam().id;
      this.addParam('versionId', versionId);
    },
    btn_add_node: function (e) {
      this.openEditPage('type', 'O', '新增节点');
    },
    btn_add_bussiness_unit: function () {
      this.openEditPage('type', 'B', '新增业务单位');
    },
    btn_add_dept: function (e) {
      this.openEditPage('type', 'D', '新增部门');
    },
    btn_add_job: function () {
      this.openEditPage('type', 'J', '新增职位');
    },
    btn_add_sys_unit: function (e) {
      this.openEditPage('type', 'V', '引用系统单位');
    },
    btn_edit: function (e) {
      var index = $(e.target).parents('tr').data('index');
      var uuid = this.getData()[index].uuid;
      this.openEditPage('uuid', uuid, '编辑');
    },
    openEditPage: function (type, value, title) {
      var versionId = GetRequestParam().id;
      var systemUnitId = GetRequestParam().systemUnitId;
      var url =
        ctx +
        '/web/app/pt-mgr/pt-usr-mgr/pt-org-mgr.html?versionId=' +
        versionId +
        '&systemUnitId=' +
        systemUnitId +
        '&' +
        type +
        '=' +
        value;
      appContext.getNavTabWidget().createTab('org_ver_config_edit', '配置组织版本-' + title, 'iframe', url);
    },
    btn_del: function () {
      var self = this;
      var selections = this.getSelections();
      if (selections.length) {
        appModal.confirm('确定删除?', function (res) {
          if (res) {
            appModal.showMask('删除中');
            for (var i = 0, ilen = selections.length; i < ilen; i++) {
              (function (rowId, name) {
                $.ajax({
                  url: ctx + '/api/org/multi/deleteOrgChildNode',
                  type: 'POST',
                  async: false,
                  data: {
                    treeNodeUuid: rowId
                  },
                  dataType: 'json',
                  success: function (result) {
                    appModal.hideMask();
                    appModal.hide();
                    if (result.code === 0) {
                    } else {
                      appModal.error(result.msg);
                      throw new Error('[' + name + ']' + result.msg);
                    }
                  }
                });
              })(selections[i].uuid, selections[i].name);
            }
            appModal.success('删除成功!', function () {
              self.refresh();
              self.widget.element.parents('.ui-wBootgrid').find('#orgFullName').trigger('change');
            });
          }
        });
      } else {
        appModal.error('请选择需要删除的行数据');
      }
    },
    btn_check_relation: function () {
      var orgVersionFullName = GetRequestParam().orgVersionFullName;
      var versionId = GetRequestParam().id;
      var systemUnitId = GetRequestParam().systemUnitId;
      var url =
        ctx +
        '/web/app/pt-mgr/pt-usr-mgr/app_20200109154259.html?versionId=' +
        versionId +
        '&systemUnitId=' +
        systemUnitId +
        '&orgVersionFullName=' +
        orgVersionFullName;
      appContext.getNavTabWidget().createTab('org_ver_config_check_relation', '配置组织版本-查看领导关系', 'iframe', url);
    }
  });
  return AppOrgVerConfListDevelopment;
});
