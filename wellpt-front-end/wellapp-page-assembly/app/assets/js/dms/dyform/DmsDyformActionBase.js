define("DmsDyformActionBase", ["jquery", "commons", "constant", "server", "DmsDataServices" ], function($, commons, constant,
		server, DmsDataServices) {
	var KEY_DMS_ID = "dms_id";
	var DmsDyformActionBase = function(options) {
		this.options = options;
		this.ui = options.ui;
		this.dmsDataServices = new DmsDataServices();
	};

	$.extend(DmsDyformActionBase.prototype, {
		// 获取文档数据
		getDocumentData : function() {
			return this.ui.getDocumentData();
		},
		// 获取文档额外数据
		getExtra : function(key) {
			return this.ui.getExtra(key);
		},
		// 设置文档额外数据
		setExtra : function(key, value) {
			this.ui.setExtra(key, value);
		}
	});

	return DmsDyformActionBase;
});