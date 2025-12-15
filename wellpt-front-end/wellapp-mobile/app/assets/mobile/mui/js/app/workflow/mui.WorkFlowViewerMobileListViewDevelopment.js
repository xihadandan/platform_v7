define([ "mui", "commons", "constant", "server", "formBuilder", "mui-WorkFlowMobileListViewDevelopmentBase",
		"WorkView", "WorkViewProxy", "formBuilder" ], function($, commons, constant, server, formBuilder,
		WorkFlowMobileListViewDevelopmentBase, WorkView, workView, formBuilder) {
	// 工作流程_监控
	var WorkFlowMonitorMobileListViewDevelopment = function() {
		WorkFlowMobileListViewDevelopmentBase.apply(this, arguments);
	};
	commons.inherit(WorkFlowMonitorMobileListViewDevelopment, WorkFlowMobileListViewDevelopmentBase, {
		getWorkService : function() {
			return "mobileWorkService.getWork";
		}
	});

	return WorkFlowMonitorMobileListViewDevelopment;
});