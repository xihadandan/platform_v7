define(['jquery', 'commons', 'constant', 'server', 'WorkFlowListViewWidgetDevelopmentBase'], function (
  $,
  commons,
  constant,
  server,
  WorkFlowListViewWidgetDevelopmentBase
) {
  var StringBuilder = commons.StringBuilder;
  // 平台应用_工作委托_委托数据_视图组件二开
  var WorkFlowDelegationDataListViewWidgetDevelopment = function () {
    WorkFlowListViewWidgetDevelopmentBase.apply(this, arguments);
  };
  commons.inherit(WorkFlowDelegationDataListViewWidgetDevelopment, WorkFlowListViewWidgetDevelopmentBase, {
    init: function () {
      var _self = this;
      var widget = _self.getWidget();
      widget.alwaysRemoteQuery = true;
    },
    afterRender: function () {
      var _self = this;
      var widget = _self.getWidget();
      var sb = new StringBuilder();
      sb.append('<div class="columns pull-right col-xs-6">	');
      sb.append('<label class="col-xs-5 control-label" for="completionState"></label>');
      sb.append('<div class="col-xs-7 controls">');
      sb.append('<label class="checkbox-inline">');
      sb.append('<input class="w-search-option" height="34px" name="showCompletionState" type="checkbox" id="showCompletionState">');
      sb.append('<label for="showCompletionState">是否显示已审批的流程</label>');
      sb.append('</label>');
      sb.append('</div>');
      sb.append('</div>');
      $('.fixed-table-toolbar', widget.element).append(sb.toString());
      // 选择变更刷新视图
      $("input[name='showCompletionState']", widget.element).on('change', function () {
        widget.refresh(true);
      });
    },
    beforeLoadData: function () {
      var _self = this;
      var widget = _self.getWidget();
      var showCompletionState = $("input[name='showCompletionState']", widget.element).prop('checked');
      widget.clearParams();
      if (showCompletionState) {
        widget.addParam('showCompletionState', 'true');
      }
    },
    getWorkViewUrl: function (index, row) {
      var url = null;
      if (row.completionState == 0) {
        url = ctx + '/workflow/work/v53/view/work?taskInstUuid={0}&taskIdentityUuid={1}&flowInstUuid={2}';
      } else {
        url = ctx + '/workflow/work/v53/view/work?taskInstUuid={0}&flowInstUuid={2}';
      }
      var flowInstUuid = row.flowInstUuid;
      var taskInstUuid = row.taskInstUuid || '';
      var taskIdentityUuid = row.taskIdentityUuid || '';
      var sb = new StringBuilder();
      sb.appendFormat(url, taskInstUuid, taskIdentityUuid, flowInstUuid);
      return sb.toString();
    }
  });
  return WorkFlowDelegationDataListViewWidgetDevelopment;
});
