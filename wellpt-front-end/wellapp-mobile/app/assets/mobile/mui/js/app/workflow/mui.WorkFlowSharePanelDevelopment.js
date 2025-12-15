define([ "mui", "commons", "constant", "server", "WorkView", "WorkViewProxy","formBuilder", "mui-MobilePanelDevelopmentBase" ], function($, commons, constant, server, WorkView, WorkViewProxy, formBuilder, MobilePanelDevelopmentBase) {
	var browser = commons.Browser;
	// 工作流程_待办
	var MobileSharePanelDevelopment = function() {
		MobilePanelDevelopmentBase.apply(this, arguments);
	};
	commons.inherit(MobileSharePanelDevelopment, MobilePanelDevelopmentBase, {
		afterRender : function(options, configuration){
			var self = this;
			var taskUuid = browser.getQueryString("taskUuid");
			var flowInstUuid = browser.getQueryString("flowInstUuid");
			var taskInstUuid = taskUuid || browser.getQueryString("taskInstUuid");
			if(!flowInstUuid || !taskInstUuid){
				return $.alert("没有权限");
			}
			var data = {};
			data.flowInstUuid = flowInstUuid;
			data.taskInstUuid = taskInstUuid;
			var workData = null;
			var service = self.getWorkService();
			var params = self.getWorkServiceParams(data);
			server.JDS.call({
				service : service,
				data : params,
				async : false,
				success : function(result) {
					workData = result.data;
				}
			});
			var pageContainer = appContext.getPageContainer();
			var renderPlaceholder = pageContainer.getRenderPlaceholder();
			var $wfWorkView = $(".panel", renderPlaceholder[0]);
			$wfWorkView[0].id = "wf_work_view";
			$wfWorkView.workView({
				"workData" : workData
			});
			// console.log("afterRender");
		},
		getWorkServiceParams : function(data){
			var flowInstUuid = data["flowInstUuid"];
			var taskInstUuid = data["taskInstUuid"];
			return [ taskInstUuid, flowInstUuid ];
		},
		getWorkService : function() {
			return "mobileWorkService.getShare";
		}
	});
	return MobileSharePanelDevelopment;
});