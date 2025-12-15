define([ "mui", "commons", "constant", "server", "formBuilder", "MobileListDevelopmentBase"], function
		($, commons, constant, server, formBuilder, MobileListDevelopmentBase) {
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	
	var MobliePersonalDocumentMyShareInfoFileManagerWidgetDevelopment = function() {
		MobileListDevelopmentBase.apply(this, arguments);
	};
	//我的分享
	commons.inherit(MobliePersonalDocumentMyShareInfoFileManagerWidgetDevelopment, MobileListDevelopmentBase, {
		beforeRender : function() {
			var widget = this.getWidget();
			var configuration = widget.getConfiguration();		
		}
	});
	return MobliePersonalDocumentMyShareInfoFileManagerWidgetDevelopment;
});