define([ "mui", "commons", "constant", "server", "formBuilder", "MobileListDevelopmentBase"], function
		($, commons, constant, server, formBuilder, MobileListDevelopmentBase) {
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	
	var MobliePersonalDocumentRecentVisitFileManagerWidgetDevelopment = function() {
		MobileListDevelopmentBase.apply(this, arguments);
	};
	//最近访问
	commons.inherit(MobliePersonalDocumentRecentVisitFileManagerWidgetDevelopment, MobileListDevelopmentBase, {
		beforeRender : function() {
			var widget = this.getWidget();
			var configuration = widget.getConfiguration();	
		}
	});
	return MobliePersonalDocumentRecentVisitFileManagerWidgetDevelopment;
});