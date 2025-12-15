define([ "jquery", "commons", "constant", "server", "WorkFlowListViewWidgetDevelopmentBase" ], function($, commons,
		constant, server, WorkFlowListViewWidgetDevelopmentBase) {
	var StringBuilder = commons.StringBuilder;
	// 平台应用_工作流程_工作监控_视图组件二开
	var WorkFlowMonitorListViewWidgetDevelopment = function() {
		WorkFlowListViewWidgetDevelopmentBase.apply(this, arguments);
	};
	commons.inherit(WorkFlowMonitorListViewWidgetDevelopment, WorkFlowListViewWidgetDevelopmentBase, {
		getWorkViewUrl : function(index, row) {
			var url = ctx + "/workflow/work/v53/view/monitor?taskInstUuid={0}&flowInstUuid={1}";
			var taskInstUuid = row.taskInstUuid ? row.taskInstUuid : row.uuid;
			var flowInstUuid = row.flowInstUuid;
			var sb = new StringBuilder();
			sb.appendFormat(url, taskInstUuid, flowInstUuid);
			return sb.toString();
		}
	});
	return WorkFlowMonitorListViewWidgetDevelopment;
});