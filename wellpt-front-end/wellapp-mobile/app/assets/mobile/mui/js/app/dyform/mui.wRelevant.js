define([ "mui", "constant", "commons", "server", "mui-DyformField" ], function($, constant, commons, server,
		DyformField) {
	// 相关数据控件
	var wRelevant = function($placeHolder, options) {
		DyformField.apply(this, arguments);
	};
	commons.inherit(wRelevant, DyformField);
	return wRelevant;
});