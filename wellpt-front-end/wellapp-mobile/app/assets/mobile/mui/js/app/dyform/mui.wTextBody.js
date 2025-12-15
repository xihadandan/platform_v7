define([ "mui", "constant", "commons", "server", "mui-DyformField", "mui-wFileUpload" ], function($, constant, commons, server,
		DyformField, wFileUpload) {
	// 正文
	var wTextBody = function($placeHolder, options) {
		wFileUpload.apply(this, arguments);
	};
	commons.inherit(wTextBody, wFileUpload);
	return wTextBody;
});