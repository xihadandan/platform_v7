define([ "jquery", "commons", "constant", "server", "WorkFlowListViewWidgetDevelopmentBase" ], function($, commons,
		constant, server, WorkFlowListViewWidgetDevelopmentBase) {
	var StringBuilder = commons.StringBuilder;
	// 平台应用_工作流程_工作查看_视图组件二开
	var WorkFlowDraftListViewWidgetDevelopment = function() {
		WorkFlowListViewWidgetDevelopmentBase.apply(this, arguments);
	};
	commons.inherit(WorkFlowDraftListViewWidgetDevelopment, WorkFlowListViewWidgetDevelopmentBase, {
		getWorkViewUrl : function(index, row) {
			var url = ctx + "/workflow/work/v53/view/draft?flowInstUuid={0}";
			var flowInstUuid = row.flowInstUuid || row.uuid;
			var sb = new StringBuilder();
			sb.appendFormat(url, flowInstUuid);
			return sb.toString();
		}
	});
	return WorkFlowDraftListViewWidgetDevelopment;
});