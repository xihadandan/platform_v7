define("mui-WorkFlowDevelopment", ["mui", "WorkFlowDevelopment" ], function($, WorkFlowDevelopment) {
	$.extend(WorkFlowDevelopment.prototype, {
		// 显示操作按钮
		showButtons : function() {
			$(".wf_operate").show();
		},
		// 隐藏操作按钮
		hideButtons : function() {
			$(".wf_operate").hide();
		},
	});
	define("WorkFlowDevelopment", [], function() {
		return WorkFlowDevelopment;
	})
	return WorkFlowDevelopment;
})