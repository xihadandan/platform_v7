define([ "mui", "constant", "commons", "server", "mui-DyformField" ], function($, constant, commons, server,
		DyformField) {
	// 树形下拉框 
	var wComboTree = function($placeHolder, options) {
		DyformField.apply(this, arguments);
	};
	commons.inherit(wComboTree, DyformField);
	return wComboTree;
});