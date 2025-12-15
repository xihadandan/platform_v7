define([ "constant", "commons", "appContext", "appModal", "DmsDataServices" ], function(constant, commons, appContext,
		appModal, DmsDataServices) {
	var StringUtils = commons.StringUtils;
	// 分支流、子流程添加承办子表单处理
	return {
		// 初始化文档加载的数据
		initDocumentLoadedData : function(result) {
			var _self = this;
			// 添加确定操作
			var actions = [];
			actions.push({
				id : "btn_wf_undertake_situation_add_subflow",
				name : "确定",
				executeJsModule : "WorkFlowUndertakeSituationFragment"
			});
			result.actions = actions;
			// 调用父类提交方法
			_self._superApply(arguments);
		},
		// 绑定添加操作事件处理，对应action的id
		btn_wf_undertake_situation_add_subflow : function(options) {
			// 操作后可关闭窗口的判断条件
			options.ui.options.target = "_dialog";

			var data = options.data;
			var childDyformData = data.dyFormData;
			var undertakeData = JSON.parse(data.extras.ep_undertakeData);
			undertakeData.childDyformData = childDyformData;
			undertakeData.action = data.action;
			options.data = undertakeData;

			var dmsDataServices = new DmsDataServices();
			dmsDataServices.performed(options)
		}
	};
});