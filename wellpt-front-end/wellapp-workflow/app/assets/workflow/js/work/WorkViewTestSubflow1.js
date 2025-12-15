define(['constant', 'commons', 'appContext', 'appModal', 'WorkView'], function(constant, commons, appContext, appModal, WorkView) {
	// 流程二开测试子流程分发人员选择组织选择框
	var WorkViewTestSubflow1 = function() { };
	commons.inherit(WorkViewTestSubflow1, WorkView, {
		// 自定义添加主办或协办子流程
		customizeAddMajorOrMinorSubflow: function(newFlowInfos, callback) {
			setTimeout(function() {
				callback.call(this, newFlowInfos[0].ids, ["U0000000059", "U0000017547"]);
			}, 3000);
		},
		// 自定义添加主办子流程
		customizeAddMajorSubflow: function(newFlowInfos, callback) {
			setTimeout(function() {
				callback.call(this, newFlowInfos[0].ids, ["U0000000059"]);
			}, 3000);
		},
		// 自定义添加协办子流程
		customizeAddMinorSubflow: function(newFlowInfos, callback) {
			setTimeout(function() {
				callback.call(this, newFlowInfos[0].ids, ["U0000017547"]);
			}, 3000);
		}
	});
	return WorkViewTestSubflow1;
});
