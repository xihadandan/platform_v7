define([ "mui", "commons", "constant", "server", "formBuilder", "mui-WorkFlowMobileListViewDevelopmentBase",
		"WorkView", "WorkViewProxy", "formBuilder" ], function($, commons, constant, server, formBuilder,
		WorkFlowMobileListViewDevelopmentBase, WorkView, workView, formBuilder) {
	// 工作流程_办结
	var WorkFlowOverMobileListViewDevelopment = function() {
		WorkFlowMobileListViewDevelopmentBase.apply(this, arguments);
	};
	commons.inherit(WorkFlowOverMobileListViewDevelopment, WorkFlowMobileListViewDevelopmentBase, {
		getWorkService : function() {
			return "mobileWorkService.getOver";
		}
	});

	return WorkFlowOverMobileListViewDevelopment;
});