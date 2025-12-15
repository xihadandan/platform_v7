define([ "mui", "commons", "constant", "server", "formBuilder", "MobileListDevelopmentBase"], function
		($, commons, constant, server, formBuilder, MobileListDevelopmentBase) {
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	
	var MobliePersonalDocumentShareWithMeFileManagerWidgetDevelopment = function() {
		MobileListDevelopmentBase.apply(this, arguments);
	};
	//与我分享
	commons.inherit(MobliePersonalDocumentShareWithMeFileManagerWidgetDevelopment, MobileListDevelopmentBase, {
		beforeRender : function() {
			var widget = this.getWidget();
			var configuration = widget.getConfiguration();		
		}
	});
	return MobliePersonalDocumentShareWithMeFileManagerWidgetDevelopment;
});