define([ "mui", "commons", "constant", "server", "formBuilder", "mui-WorkFlowMobileListViewDevelopmentBase",
		"WorkView", "WorkViewProxy", "formBuilder" ], function($, commons, constant, server, formBuilder,
		WorkFlowMobileListViewDevelopmentBase, WorkView, workView, formBuilder) {
	// 工作流程_草稿
	var WorkFlowDraftMobileListViewDevelopment = function() {
		WorkFlowMobileListViewDevelopmentBase.apply(this, arguments);
	};
	commons.inherit(WorkFlowDraftMobileListViewDevelopment, WorkFlowMobileListViewDevelopmentBase, {
		getWorkService : function() {
			return "workV53Service.getDraft";
		},
		getWorkServiceParams : function(data) {
			var flowInstUuid = data.flowInstUuid || data.uuid;
			return [ flowInstUuid ];
		}
	});

	return WorkFlowDraftMobileListViewDevelopment;
});