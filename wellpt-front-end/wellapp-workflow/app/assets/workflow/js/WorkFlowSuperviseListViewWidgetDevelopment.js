define([ "jquery", "commons", "constant", "server", "WorkFlowListViewWidgetDevelopmentBase" ], function($, commons,
		constant, server, WorkFlowListViewWidgetDevelopmentBase) {
	var StringBuilder = commons.StringBuilder;
	// 平台应用_工作流程_工作督办_视图组件二开
	var WorkFlowSuperviseListViewWidgetDevelopment = function() {
		WorkFlowListViewWidgetDevelopmentBase.apply(this, arguments);
	};
	commons.inherit(WorkFlowSuperviseListViewWidgetDevelopment, WorkFlowListViewWidgetDevelopmentBase, {
		getWorkViewUrl : function(index, row) {
			var url = ctx + "/workflow/work/v53/view/supervise?taskInstUuid={0}&flowInstUuid={1}";
			var taskInstUuid = row.taskInstUuid ? row.taskInstUuid : row.uuid;
			var flowInstUuid = row.flowInstUuid;
			var sb = new StringBuilder();
			sb.appendFormat(url, taskInstUuid, flowInstUuid);
			return sb.toString();
		}
	});
	return WorkFlowSuperviseListViewWidgetDevelopment;
});