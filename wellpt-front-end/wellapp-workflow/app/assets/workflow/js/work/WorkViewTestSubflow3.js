define(['constant', 'commons', 'appContext', 'appModal', 'WorkView'], function(constant, commons, appContext, appModal, WorkView) {
	// 流程二开测试子流程分发人员选择组织选择框
	var WorkViewTestSubflow3 = function() { };
	commons.inherit(WorkViewTestSubflow3, WorkView, {
		// 获取主办或协办人员字段名
		getMajorOrMinorUserFieldName: function() {
			return "do_leader_user";
		},
		// 获取主办人员字段名
		getMajorUserFieldName: function() {
			return "do_main_user";
		},
		// 获取协办人员字段名
		getMinorUserFieldName: function() {
			return "do_assist_user";
		}
	});
	return WorkViewTestSubflow3;
});
