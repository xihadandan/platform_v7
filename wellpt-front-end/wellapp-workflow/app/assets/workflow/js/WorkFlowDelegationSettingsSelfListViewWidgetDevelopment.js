define(['jquery', 'commons', 'constant', 'server', 'appModal', 'WorkFlowListViewWidgetDevelopmentBase'], function (
  $,
  commons,
  constant,
  server,
  appModal,
  ListViewWidgetDevelopment
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var jds = server.JDS;

  // 平台应用_工作委托_委托设置_视图组件二开
  var WorkFlowDelegationSettingsSelfListViewWidgetDevelopment = function () {
    var _self = this;
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(WorkFlowDelegationSettingsSelfListViewWidgetDevelopment, ListViewWidgetDevelopment, {
    onClickRow: function (index, row) {
      var _self = this;
      var url = ctx + '/workflow/delegation/settings/edit?uuid=' + row.UUID;
      var options = {};
      options.url = url;
      options.ui = _self.getWidget();
      options.size = 'large';
      appContext.openWindow(options);
    },
    // 新建
    add: function () {
      var _self = this;
      var url = ctx + '/workflow/delegation/settings/add';
      var options = {};
      options.url = url;
      options.ui = _self.getWidget();
      options.size = 'large';
      options.useUniqueName = false;
      appContext.openWindow(options);
    },
    // 删除
    delete: function () {
      var _self = this;
      var selection = _self.getWidget().getSelections();
      if (selection.length === 0) {
        appModal.error('请选择记录！');
        return;
      }

      var uuids = [];
      $.each(selection, function () {
        uuids.push(this.UUID);
      });
      appModal.confirm('确认要删除吗?', function (result) {
        if (result) {
          jds.restfulPost({
            url: ctx + '/api/workflow/delegation/settiongs/deleteAll',
            data: { uuids: uuids },
            contentType: 'application/x-www-form-urlencoded',
            mask: true,
            success: function (result) {
              appModal.success('删除成功！', function () {
                _self.getWidget().refresh(true);
              });
            }
          });
        }
      });
    },
    // 激活
    active: function () {
      var _self = this;
      var selection = _self.getSelection();
      if (selection.length === 0) {
        appModal.error('请选择要激活的委托！');
        return;
      }
      var errorMsg = '';
      var uuids = [];
      $.each(selection, function () {
        if (this.STATUS == 1) {
          errorMsg = '只可激活状态为终止或征求受托人意见的委托！';
        }
        uuids.push(this.UUID);
      });
      if (StringUtils.isNotBlank(errorMsg)) {
        appModal.error(errorMsg);
        return;
      }
      jds.restfulPost({
        url: ctx + '/api/workflow/delegation/settiongs/activeAll',
        data: { uuids: uuids },
        contentType: 'application/x-www-form-urlencoded',
        mask: true,
        success: function (result) {
          appModal.info('激活成功！', function () {
            _self.getWidget().refresh(true);
          });
        }
      });
    },
    // 终止
    deactive: function () {
      var _self = this;
      var selection = _self.getSelection();
      if (selection.length === 0) {
        appModal.error('请选择要终止的委托！');
        return;
      }
      var errorMsg = '';
      var uuids = [];
      $.each(selection, function () {
        if (this.STATUS == 0) {
          errorMsg = '只可终止状态为激活或征求受托人意见的委托！';
        }
        uuids.push(this.UUID);
      });
      if (StringUtils.isNotBlank(errorMsg)) {
        appModal.error(errorMsg);
        return;
      }
      jds.restfulPost({
        url: ctx + '/api/workflow/delegation/settiongs/deactiveAll',
        data: { uuids: uuids },
        contentType: 'application/x-www-form-urlencoded',
        mask: true,
        success: function (result) {
          appModal.info('终止成功！', function () {
            _self.getWidget().refresh(true);
          });
        }
      });
    }
  });
  return WorkFlowDelegationSettingsSelfListViewWidgetDevelopment;
});
