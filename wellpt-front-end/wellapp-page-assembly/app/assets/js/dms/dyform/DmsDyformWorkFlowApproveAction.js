define("DmsDyformWorkFlowApproveAction", [ "jquery", "commons", "constant", "server", "appContext", "appModal",
		"DmsDyformActionBase", "WorkFlowApprove" ], function($, commons, constant, server, appContext, appModal,
		DmsDyformActionBase, WorkFlowApprove) {
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	// 表单单据操作_送审批
	var DmsDyformWorkFlowApproveAction = function() {
		DmsDyformActionBase.apply(this, arguments);
	}
	commons.inherit(DmsDyformWorkFlowApproveAction, DmsDyformActionBase, {
		btn_dyform_send_to_approve : function(options) {
			// 组装参数
			var appFunction = options.appFunction || {};
			var approveFlowDefIds = appFunction.properties && appFunction.properties.approveFlowDefIds;
			var approveFlowDefId = null;
			if (!StringUtils.contains(approveFlowDefIds, ";")) {
				approveFlowDefId = approveFlowDefIds;
			}
			var params = $.extend(true, {}, options.params || {});
			var documentData = options.data;
			var approveOptions = $.extend({
				formUuid : documentData.formUuid,
				dataUuid : documentData.dataUuid,
				flowDefIds : approveFlowDefIds,
				flowDefId : approveFlowDefId,
				autoSubmit : true,
			}, params, {
				ui : options.ui
			});

			// 自动提交结果参数组装
			if (approveOptions.autoSubmit == "true") {
				var autoSubmitResult = {
					close : approveOptions.autoSubmitResultClose == "true",
					refresh : approveOptions.autoSubmitResultRefresh == "true",
					refreshParent : approveOptions.autoSubmitResultRefreshParent == "true",
					msg : approveOptions.autoSubmitResultMsg
				};
				approveOptions.autoSubmitResult = autoSubmitResult;
			}

			// 送审批
			var workFlowApprove = new WorkFlowApprove(approveOptions);
			workFlowApprove.sendToApprove();
		}
	});
	return DmsDyformWorkFlowApproveAction;
});