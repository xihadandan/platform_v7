define([ "mui", "commons", "constant", "server", "formBuilder", "mui-WorkFlowMobileListViewDevelopmentBase",
		"WorkView", "WorkViewProxy", "formBuilder" ], function($, commons, constant, server, formBuilder,
		WorkFlowMobileListViewDevelopmentBase, WorkView, workView, formBuilder) {
	// 工作流程_待办
	var WorkFlowTodoMobileListViewDevelopment = function() {
		WorkFlowMobileListViewDevelopmentBase.apply(this, arguments);
	};
	commons.inherit(WorkFlowTodoMobileListViewDevelopment, WorkFlowMobileListViewDevelopmentBase, {
		getWorkService : function() {
			return "mobileWorkService.getTodo";
		}
	});

	return WorkFlowTodoMobileListViewDevelopment;
});