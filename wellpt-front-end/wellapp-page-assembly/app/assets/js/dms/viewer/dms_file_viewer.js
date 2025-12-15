define([ "jquery", "commons", "constant", "server", "appContext", "DmsFileOnlineViewer" ], function($, commons,
		constant, server, appContext, DmsFileOnlineViewer) {
	var StringUtils = commons.StringUtils;
	var $viewer = $(".viewer");
	var fileUuid = $("input[name='fileUuid']").val();
	var viewFileUrl = $("input[name='viewFileUrl']").val();
	var dmsFileOnlineViewer = new DmsFileOnlineViewer($viewer, {
		container : $viewer
	});
	if (StringUtils.isNotBlank(viewFileUrl)) {
		dmsFileOnlineViewer.viewWithUrl(viewFileUrl);
	} else {
		dmsFileOnlineViewer.viewByFileUuid(fileUuid);
	}
});