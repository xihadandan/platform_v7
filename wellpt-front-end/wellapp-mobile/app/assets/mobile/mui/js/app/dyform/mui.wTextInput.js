define([ "mui", "constant", "commons", "server", "mui-DyformField" ], function($, constant, commons, server,
		DyformField) {
	// 普通输入框
	var wTextInput = function($placeHolder, options) {
		DyformField.apply(this, arguments);
	};
	commons.inherit(wTextInput, DyformField);
	return wTextInput;
});