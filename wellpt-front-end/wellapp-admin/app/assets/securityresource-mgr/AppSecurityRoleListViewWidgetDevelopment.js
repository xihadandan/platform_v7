define(['constant', 'commons', 'server', 'appContext', 'appModal', 'AppPtMgrListViewWidgetDevelopment', 'AppPtMgrCommons'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  AppPtMgrListViewWidgetDevelopment,
  AppPtMgrCommons
) {
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;

  // 平台管理_产品集成_模块_角色列表_视图组件二开
  var AppSecurityRoleListViewWidgetDevelopment = function () {
    AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppSecurityRoleListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
    // 组件准备
    prepare: function () {},

    beforeRender: function (options, configuration) {
      // 归属系统/模块/应用ID
      this.widget.addParam('appId', this._appId());
    },

    afterRender: function (options, configuration) {
      var _self = this;
      this.widget.on('AppSecurityRoleListView.refresh', function () {
        _self.refresh();
      });
    },

    refresh: function () {
      //刷新徽章
      var tabpanel = this.widget.element.parents('.active');
      if (tabpanel.length > 0) {
        var id = tabpanel.attr('id');
        id = id.substring(0, id.indexOf('-'));
        $('#' + id).trigger(constant.WIDGET_EVENT.BadgeRefresh, {
          targetTabName: '角色'
        });
      }
      return this.getWidget().refresh(this.options);
    },

    _appId: function () {
      var widgetParams = this.getWidgetParams();
      var piTypeProperty = { 模块: 'moduleId', 应用: 'appId', 系统: 'systemId' };
      return widgetParams[piTypeProperty[widgetParams.appProdIntegrateType]];
    },

    // 删除
    btn_delete: function () {
      var _self = this;
      var rowData = _self.getSelectRowData();
      if (rowData.length > 0) {
        var name = rowData[0].name;
        appModal.confirm('确认要删除角色吗?', function (result) {
          if (result) {
            var uuids = [];
            for (var i = 0, len = rowData.length; i < len; i++) {
              uuids.push(rowData[i].uuid);
            }
            server.JDS.restfulPost({
              url: '/proxy/api/security/role/removeAll',
              data: uuids,
              success: function (result) {
                if (result.code == 0) {
                  appModal.info('刪除成功');
                  _self.refresh(); //刷新表格
                }
              },
              error: function (jqXHR) {
                var faultData = JSON.parse(jqXHR.responseText);
                appModal.alert(faultData.msg);
              }
            });
          }
        });
      }
    },

    getSelectRowData: function () {
      var _self = this;
      var $toolbarDiv = $(event.target).closest('div');
      var rowData = [];
      if ($toolbarDiv.is('.div_lineEnd_toolbar')) {
        //行级点击操作
        var index = $toolbarDiv.attr('index');
        var allData = _self.getData();
        rowData = [allData[index]];
      } else {
        if (!_self.checkSelection(true)) {
          return [];
        }
        rowData = _self.getSelections();
      }
      return rowData;
    },

    definition_import: function () {
      var _self = this;
      // 定义导入
      $.iexportData['import']({
        callback: function () {
          _self.refresh();
        }
      });
    },

    definition_export: function () {
      // 定义导出
      var rowData = this.getSelectRowData();
      if (rowData.length > 0) {
        var uuids = [];
        for (var i = 0; i < rowData.length; i++) {
          uuids.push(rowData[i].uuid);
        }
        $.iexportData['export']({
          uuid: uuids.join(';'),
          type: 'role'
        });
      }
    },

    btn_add: function () {
      // 触发角色列表行点击新增事件
      this.widget.trigger('AppSecurityRoleListView.editRow', {
        ui: this.widget
      });
    },

    // 行点击查看详情
    onClickRow: function (rowNum, rowData, $element, field) {
      // 触发角色列表行点击事件
      this.widget.trigger('AppSecurityRoleListView.editRow', {
        rowData: rowData,
        ui: this.widget
      });
    }
  });
  return AppSecurityRoleListViewWidgetDevelopment;
});
