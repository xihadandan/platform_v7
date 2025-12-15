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

  // 平台管理_业务流程管理_业务流程办件列表_视图组件二开
  var AppBizProcessInstanceListViewWidgetDevelopment = function () {
    AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppBizProcessInstanceListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
    // 组件准备
    prepare: function () {},

    beforeRender: function (options, configuration) {},

    getSelectRowData: function (event) {
      var _self = this;
      var $toolbarDiv = $(event.target).closest('div');
      var rowData = [];
      if ($toolbarDiv.is('.div_lineEnd_toolbar')) {
        //行级点击操作
        var index = $toolbarDiv.attr('index');
        var allData = _self.getData();
        rowData = [allData[index]];
      } else {
        if (_self.getSelectionIndexes().length == 0) {
          return [];
        }
        rowData = _self.getSelections();
      }
      return rowData;
    },

    // 行点击查看详情
    onClickRow: function (rowNum, rowData, $element, field) {
      var _self = this;
      // 查看业务流程
      var url = `/biz/process/instance/view?processInstUuid=${rowData.uuid}`;
      var options = {};
      options.url = url;
      options.ui = _self.getWidget();
      options.size = 'large';
      appContext.openWindow(options);
    }
  });
  return AppBizProcessInstanceListViewWidgetDevelopment;
});
