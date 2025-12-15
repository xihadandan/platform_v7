define([ "constant", "commons", "server", "FileManagerWidgetDevelopment" ], function(constant, commons, server,
		FileManagerWidgetDevelopment) {
	var JDS = server.JDS;
	var StringUtils = commons.StringUtils;
	// 页面组件二开基础
	var PersonalDocumentRecentVisitFileManagerWidgetDevelopment = function() {
		FileManagerWidgetDevelopment.apply(this, arguments);
	};
	// 接口方法
	commons.inherit(PersonalDocumentRecentVisitFileManagerWidgetDevelopment, FileManagerWidgetDevelopment, {
		// 将文件管理的归属夹调整为最近访问
		prepare : function() {
			var _self = this;
			// 调用父类提交方法
			_self._superApply(arguments);
			var widget = _self.getWidget();
			// 变更归属夹到最近访问
			widget.getRootFolder().name = "最近访问";
			// 显示导航夹操作按钮
			widget.setShowNavFolderActions(true);
			// 显示工具栏操作按钮
			widget.setShowToolbarActions(true);
			// 使用自定义的文件库数据源
			widget.setUseCustomFileManagerDataStore(true);
			JDS.call({
				service : "personalDocumentService.getMyFolder",
				success : function(result) {
					var folder = result.data;
					// 设置弹出框使用的根目录
					widget.setRootFolderForDialog({
						uuid : folder.uuid,
						name : "全部文档"
					});
				}
			});
		}
	});
	return PersonalDocumentRecentVisitFileManagerWidgetDevelopment;
});