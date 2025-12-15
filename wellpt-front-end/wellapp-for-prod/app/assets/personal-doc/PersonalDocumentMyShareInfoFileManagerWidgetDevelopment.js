define([ "constant", "commons", "server", "FileManagerWidgetDevelopment" ], function(constant, commons, server,
		FileManagerWidgetDevelopment) {
	var JDS = server.JDS;
	var StringUtils = commons.StringUtils;
	// 页面组件二开基础
	var PersonalDocumentLatestFileManagerWidgetDevelopment = function() {
		FileManagerWidgetDevelopment.apply(this, arguments);
	};
	// 接口方法
	commons.inherit(PersonalDocumentLatestFileManagerWidgetDevelopment, FileManagerWidgetDevelopment, {
		// 将文件管理的归属夹调整为我的分享
		prepare : function() {
			var _self = this;
			var widget = _self.getWidget();
			var configuration = widget.getConfiguration();
			// 变更归属夹到我的分享
			configuration.belongToFolderUuid = "";
			configuration.belongToFolderName = "我的分享";
			// 不显示导航夹操作按钮
			widget.setShowNavFolderActions(false);
			// 使用自定义的文件库数据源
			widget.setUseCustomFileManagerDataStore(true);
			// 文件管理加载数据监听，进入子夹时隐藏取消分享按钮
			widget.on("wFileManager.beforeLoadData", function() {
				var currentFolderUuid = widget.getCurrentFolderUuid();
				if (StringUtils.isNotBlank(currentFolderUuid)) {
					$(".btn_class_cancelShare", widget.element).hide();
				} else {
					$(".btn_class_cancelShare", widget.element).show();
				}
			});
		}
	});
	return PersonalDocumentLatestFileManagerWidgetDevelopment;
});