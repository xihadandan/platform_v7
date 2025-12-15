define([ "constant", "commons", "appContext", "appModal" ], function(constant, commons, appContext, appModal) {
	// 流程二开片段测试
	return {
  			// 流程初始化
		init : function(options) {
			var _self = this;
			_self._superApply(arguments);
			var workDataApi = _self.getWorkDataApi();
			// 获取业务类型ID
			var businessTypeId = workDataApi.getBusinessTypeId();
		},
	};
});