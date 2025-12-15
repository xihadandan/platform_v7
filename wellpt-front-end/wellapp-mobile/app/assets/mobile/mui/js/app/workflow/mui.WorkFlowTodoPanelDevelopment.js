define([ "mui", "commons", "constant", "server", "appContext", "WorkView", "WorkViewProxy", "formBuilder",
		"mui-MobilePanelDevelopmentBase" ], function($, commons, constant, server, appContext, WorkView, WorkViewProxy,
		formBuilder, MobilePanelDevelopmentBase) {
	var browser = commons.Browser;
	var StringUtils = commons.StringUtils;
	// 工作流程_待办
	var MobileTodoDevelopment = function() {
		MobilePanelDevelopmentBase.apply(this, arguments);
	};
	commons.inherit(MobileTodoDevelopment, MobilePanelDevelopmentBase, {
		afterRender : function(options, configuration) {
			var self = this;
			var taskUuid = browser.getQueryString("taskUuid");
			var flowInstUuid = browser.getQueryString("flowInstUuid");
			var taskInstUuid = taskUuid || browser.getQueryString("taskInstUuid");
			if (!flowInstUuid || !taskInstUuid) {
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
			var customJsModule = workData.customJsModule;
			appContext.require([ customJsModule ], function(workViewModule) {
				var TodoWorkViewModule = function($element) {
					workViewModule.call(this, $element);
					this.$element = $element;
				};
				commons.inherit(TodoWorkViewModule, workViewModule, {
					onSubmitSuccess : function(result) {
						var self = this;
						var data = result.data;
						self._superApply(arguments);
						var submitTaskInstUuid = data.sameUserSubmitTaskInstUuid;
						var submitTaskOperationUuid = data.sameUserSubmitTaskOperationUuid;
						// 与前环节相同自动提交
						if (StringUtils.isNotBlank(submitTaskInstUuid) && StringUtils.isNotBlank(submitTaskOperationUuid)) {
							return;// 自动提交
						}
						$.trigger(document.body, "webview.close", {
							"confirm" : false
						});
					}
				});
				var pageContainer = appContext.getPageContainer();
				var renderPlaceholder = pageContainer.getRenderPlaceholder();
				var $wfWorkView = $(".panel", renderPlaceholder[0]);
				$wfWorkView[0].id = "wf_work_view";
				$wfWorkView.workView({
					workViewModule : TodoWorkViewModule,
					"workData" : workData
				});
				// console.log("afterRender");
			})
		},
		getWorkServiceParams : function(data) {
			var flowInstUuid = data["flowInstUuid"];
			var taskInstUuid = data["taskInstUuid"];
			return [ taskInstUuid, flowInstUuid ];
		},
		getWorkService : function() {
			return "mobileWorkService.getTodo";
		}
	});
	return MobileTodoDevelopment;
});