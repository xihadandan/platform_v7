define("DmsFileManagerDyformDocumentView", [ "jquery", "server", "commons", "constant", "appContext", "appModal",
		"DmsDyformDocumentView" ], function($, server, commons, constant, appContext, appModal, DmsDyformDocumentView) {
	var StringBuilder = commons.StringBuilder;
	// 数据管理_文件库_单据开发_表单实现
	var DmsFileManagerDyformDocumentView = function() {
		DmsDyformDocumentView.apply(this, arguments);
	};
	var getFileDocumentActionId = "btn_file_manager_dyform_get";
	commons.inherit(DmsFileManagerDyformDocumentView, DmsDyformDocumentView, {
		// 初始化
		init : function(options) {
			var _self = this;
			_self.folderUuid = options.folderUuid;
			// 调用父类方法
			_self._superApply(arguments);
		},
		// 获取加载数据的操作ID
		getLoadDataActionId : function() {
			return getFileDocumentActionId;
		},
		// 准备加载数据
		prepareLoad : function(loadOptions) {
			this._fillFileOptionData(loadOptions);
		},
		// 准备执行操作
		preparePerformed : function(performedOptions) {
			this._fillFileOptionData(performedOptions);
		},
		_fillFileOptionData : function(actionOptions) {
			var _self = this;
			var options = _self.options;
			if (actionOptions.data == null) {
				actionOptions.data = {};
			}
			actionOptions.data.folderUuid = _self.folderUuid || options.folderUuid;
			actionOptions.data.fileUuid = options.fileUuid;
		},
		getFolderUuid : function() {
			return this.folderUuid || this.options.folderUuid;
		},
		getFileUuid : function() {
			return this.options.fileUuid;
		}
	});
	return DmsFileManagerDyformDocumentView;
});