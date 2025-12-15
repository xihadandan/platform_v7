define([ "constant", "commons", "server", "PersonalDocumentFileManagerWidgetDevelopment" ], function(constant, commons,
		server, PersonalDocumentFileManagerWidgetDevelopment) {
	var JDS = server.JDS;
	// 页面组件二开基础
	var PersonalDocumentRecycleBinFileManagerWidgetDevelopment = function() {
		PersonalDocumentFileManagerWidgetDevelopment.apply(this, arguments);
	};
	// 接口方法
	commons.inherit(PersonalDocumentRecycleBinFileManagerWidgetDevelopment,
			PersonalDocumentFileManagerWidgetDevelopment, {
				// 将文件管理的归属夹调整为我的文件夹
				prepare : function() {
					var _self = this;
					// 调用父类提交方法
					_self._superApply(arguments);

					var widget = _self.getWidget();
					// 不显示导航夹操作按钮
					widget.setShowNavFolderActions(false);
					// 使用自定义的文件库数据源
					widget.setUseCustomFileManagerDataStore(true);
					// 禁用数据项点击事件
					widget.setDisableItemClick(true);
				}
			});
	return PersonalDocumentRecycleBinFileManagerWidgetDevelopment;
});