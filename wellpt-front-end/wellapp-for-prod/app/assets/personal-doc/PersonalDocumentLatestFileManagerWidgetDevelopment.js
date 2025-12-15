define([ "constant", "commons", "server", "PersonalDocumentFileManagerWidgetDevelopment" ,"DmsTagFragment"], function(constant, commons,
		server, PersonalDocumentFileManagerWidgetDevelopment,DmsTagFragment) {
	var JDS = server.JDS;
	// 页面组件二开基础
	var PersonalDocumentLatestFileManagerWidgetDevelopment = function() {
		PersonalDocumentFileManagerWidgetDevelopment.apply(this, arguments);
	};
	var dmsTagFragment = new DmsTagFragment();

	// 接口方法
	commons.inherit(PersonalDocumentLatestFileManagerWidgetDevelopment, PersonalDocumentFileManagerWidgetDevelopment,dmsTagFragment.extend({
		// 将文件管理的归属夹调整为我的文件夹
		prepare : function() {
			var _self = this;
			// 调用父类提交方法
			_self._superApply(arguments);

			var widget = _self.getWidget();
			// 列出当前夹下的文件(包含子夹)
			widget.setListFileMode("listAllFiles");
			// 不显示导航夹操作按钮
			widget.setShowNavFolderActions(false);
			// 设置每页大小为20
			widget.setPageSize(20);
		},
		// 内容展示完回调
		onPostBody : function() {
			// 调用DmsTagFragment的markBodyTags进行打标签
			this.markBodyTags();
		}
	}));
	return PersonalDocumentLatestFileManagerWidgetDevelopment;
});