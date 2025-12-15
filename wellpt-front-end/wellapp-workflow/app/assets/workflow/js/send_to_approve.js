define([ "server", "commons", "constant", "appContext", "appModal", "WorkFlowApprove" ], function(server, commons,
		constant, appContext, appModal, WorkFlowApprove) {
	var StringUtils = commons.StringUtils;
	var sendToApprove = function(options) {
		// 组装参数
		var params = $.extend(true, {}, options.params || {});
		var approveOptions = $.extend(true, params, {
			ui : options.ui
		});

		// 送审批
		var workFlowApprove = new WorkFlowApprove(approveOptions);
		workFlowApprove.sendToApprove();
	}
	return sendToApprove;
});