define([ "constant", "commons", "appContext", "appModal" ], function(constant, commons, appContext, appModal) {
	// 流程二开片段测试
	return {
		init : function(options) {
			console.log("初始化流程二开WorkViewFragmentTest2.js");
			appModal.info("初始化流程二开WorkViewFragmentTest2.js");
			var _self = this;
			_self._superApply(arguments);
		},
		// 准备初始化表单
		prepareInitDyform : function(dyformOptions) {
			var _self = this;
			// 调用父类方法
			_self._superApply(arguments);
		},
		// 表单初始化成功
		onDyformInitSuccess : function() {
			var _self = this;
			// 调用父类方法
			_self._superApply(arguments);
		},
		// 重写提交方法
		submit : function() {
			var _self = this;
			var workData = _self.getWorkData();
			// 调用父类提交方法
			_self._superApply(arguments);
		}
	};
});