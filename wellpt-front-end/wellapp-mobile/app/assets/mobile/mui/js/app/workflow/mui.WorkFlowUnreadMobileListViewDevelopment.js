define([ "mui", "commons", "constant", "server", "formBuilder", "mui-WorkFlowMobileListViewDevelopmentBase",
		"WorkView", "WorkViewProxy", "formBuilder" ], function($, commons, constant, server, formBuilder,
		WorkFlowMobileListViewDevelopmentBase, WorkView, workView, formBuilder) {
	// 工作流程_未阅
	var WorkFlowUnreadMobileListViewDevelopment = function() {
		WorkFlowMobileListViewDevelopmentBase.apply(this, arguments);
	};
	commons.inherit(WorkFlowUnreadMobileListViewDevelopment, WorkFlowMobileListViewDevelopmentBase, {
		getWorkService : function() {
			return "mobileWorkService.getUnread";
		},
		getWorkServiceParams : function(data) {
			var params = this._superApply(arguments);
			params.push(true);
			return params;
		}
	});

	return WorkFlowUnreadMobileListViewDevelopment;
});