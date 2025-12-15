define([ "mui", "constant", "commons", "server", "mui-DyformField" ], function($, constant, commons, server,
		DyformField) {
	// XML
	var wXml = function($placeHolder, options) {
		DyformField.apply(this, arguments);
	};
	commons.inherit(wXml, DyformField);
	return wXml;
});