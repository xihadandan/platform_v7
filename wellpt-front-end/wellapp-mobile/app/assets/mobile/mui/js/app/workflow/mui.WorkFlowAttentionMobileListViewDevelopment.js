define([ "mui", "commons", "constant", "server", "formBuilder", "mui-WorkFlowMobileListViewDevelopmentBase",
		"WorkView", "WorkViewProxy", "formBuilder" ], function($, commons, constant, server, formBuilder,
		WorkFlowMobileListViewDevelopmentBase, WorkView, workView, formBuilder) {
	// 工作流程_关注
	var WorkFlowAttentionMobileListViewDevelopment = function() {
		WorkFlowMobileListViewDevelopmentBase.apply(this, arguments);
	};
	commons.inherit(WorkFlowAttentionMobileListViewDevelopment, WorkFlowMobileListViewDevelopmentBase, {
		getWorkService : function() {
			return "mobileWorkService.getAttention";
		}
	});

	return WorkFlowAttentionMobileListViewDevelopment;
});