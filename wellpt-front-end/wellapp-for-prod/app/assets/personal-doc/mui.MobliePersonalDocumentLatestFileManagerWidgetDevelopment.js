define([ "mui", "commons", "constant", "server", "formBuilder", "MobileListDevelopmentBase"], function
		($, commons, constant, server, formBuilder, MobileListDevelopmentBase) {
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	
	var MobliePersonalDocumentLatestFileManagerWidgetDevelopment = function() {
		MobileListDevelopmentBase.apply(this, arguments);
	};
	//最新文档
	commons.inherit(MobliePersonalDocumentLatestFileManagerWidgetDevelopment, MobileListDevelopmentBase, {
		beforeRender : function() {
			var widget = this.getWidget();
			var configuration = widget.getConfiguration();		
			widget.setListFileMode("listAllFiles");
			server.JDS.call({
				service : "personalDocumentService.getMyFolder",
				async : false,
				success : function(result) {
					var folder = result.data;
					configuration.belongToFolderUuid = folder.uuid;
				}
			});
		}
	});
	return MobliePersonalDocumentLatestFileManagerWidgetDevelopment;
});