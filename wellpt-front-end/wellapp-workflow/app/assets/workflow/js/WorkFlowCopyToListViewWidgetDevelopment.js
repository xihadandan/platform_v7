define(["jquery", "commons", "constant", "server", "WorkFlowTodoViewDevelopment"], function ($, commons, constant,
  server, WorkFlowTodoViewDevelopment) {
  var StringBuilder = commons.StringBuilder;
  // 平台应用_工作流程_工作抄送_视图组件二开
  var WorkFlowCopyToListViewWidgetDevelopment = function () {
    WorkFlowTodoViewDevelopment.apply(this, arguments);
  };
  commons.inherit(WorkFlowCopyToListViewWidgetDevelopment, WorkFlowTodoViewDevelopment, {
    getWorkViewUrl: function (index, row) {
      var url = ctx + "/workflow/work/v53/view/unread?taskInstUuid={0}&flowInstUuid={1}";
      var taskInstUuid = row.taskInstUuid ? row.taskInstUuid : row.uuid;
      var flowInstUuid = row.flowInstUuid;
      var sb = new StringBuilder();
      sb.appendFormat(url, taskInstUuid, flowInstUuid);
      return sb.toString();
    }
  });
  return WorkFlowCopyToListViewWidgetDevelopment;
});