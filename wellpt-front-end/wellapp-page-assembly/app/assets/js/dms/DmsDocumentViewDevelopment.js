define([ "jquery", "commons", "constant", "server", "appContext" ], function($, commons, constant, server, appContext) {
	// 数据管理配置对象
	var DmsConfiguration = function() {
	};
	// 数据管理单据开发
	var DmsDocumentViewDevelopment = function() {
		this.errorHandler = server.ErrorHandler.getInstance();
	}
	$.extend(DmsDocumentViewDevelopment.prototype, {
		// 返回数据管理ID
		getId : function() {
			return this.dmsId;
		},
		// 获取数据管理配置
		getConfiguration : function() {
			return this.configuration;
		},
		// 初始化
		init : $.noop,
		// 初始化成功回调
		onInitSuccess : function() {
		},
		// 初始化失败回调
		onInitFailure : function() {
			this.handlerError.apply(this, arguments);
		},
		// 加载数据
		load : $.noop,
		// 加载数据成功回调
		onLoadSuccess : function() {
		},
		// 加载数据失败回调
		onLoadFailure : function() {
			this.errorHandler.handle.apply(this, arguments);
		},
		// 初始化文档加载的数据
		initDocumentLoadedData : function() {
		},
		// 获取文档数据
		getDocumentData : function() {
		},
		// 获取文档额外数据
		getExtras : function() {
		},
		// 获取文档额外数据
		getExtra : function(key) {
		},
		// 设置文档额外数据
		setExtra : function(key, value) {
		},
		// 表单初始化成功回调
		onDyformInitSuccess : function() {
		},
		// 表单初始化成功回调，已过时
		onInitDyformSuccess : function() {
		},
		// 表单初始化失败回调
		onDyformInitFailure : function() {
		},
		// 表单初始化失败回调，已过时
		onInitDyformFailure : function() {
		},
		// 返回文档的操作列表
		getActions : function() {
			return this.actions || [];
		},
		// 执行操作
		performed : function(action) {
		},
		// 刷新
		refresh : function() {
			appContext.getWindowManager().refresh();
		}
	});
	return DmsDocumentViewDevelopment;
});