define(['constant', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  ListViewWidgetDevelopment
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var JDS = server.JDS;

  // 平台管理_系统单位_单位管理员列表组件二开
  var AppOrgUserAccountListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppOrgUserAccountListDevelopment, ListViewWidgetDevelopment, {
    // 准备创建回调
    prepare: function () {},
    // 创建后回调
    create: function () {},
    // 初始化回调
    init: function () {
      var _self = this;
    },

    //禁用
    btn_forbid: function (event, opt) {
      this.updateStatus(event, opt, 'forbid');
    },
    //禁用
    btn_unforbid: function (event, opt) {
      this.updateStatus(event, opt, 'unforbid');
    },
    //解锁
    btn_unlock: function (event, opt) {
      this.updateStatus(event, opt, 'unlock');
    },

    updateStatus: function (event, opt, type) {
      var _self = this;
      var rowData = _self.getSelection()[0];
      if (rowData && rowData.id) {
        var msg = type === 'unlock' ? '解冻' : type === 'forbid' ? '禁用' : '启用';
        var method = type === 'unlock' ? 'unlockAccount' : type === 'forbid' ? 'forbidAccount' : 'unforbidAccount';
        appModal.confirm({
          message: '确认' + msg + '?',
          callback: function (res) {
            if (res) {
              $.ajax({
                url: ctx + '/api/org/user/account/' + method,
                type: 'POST',
                data: {
                  uuid: rowData.uuid
                },
                dataType: 'json',
                async: false,
                success: function (result) {
                  appModal.alert(msg + '成功！');
                  _self.refresh();
                }
              });
            }


          }
        });
      } else {
        appModal.alert('请先选中一行!');
      }
    }
  });
  return AppOrgUserAccountListDevelopment;
});
