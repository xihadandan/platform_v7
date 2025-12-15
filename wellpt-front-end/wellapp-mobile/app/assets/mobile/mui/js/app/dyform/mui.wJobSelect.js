define([ "mui", "constant", "commons", "server", "mui-DyformField", "mui-wSelect" ], function($, constant, commons,
	server, DyformField, wSelect) {
var StringUtils = commons.StringUtils;
// 职位控件
var wJobSelect = function($placeHolder, options) {
	DyformField.apply(this, arguments);
};
commons.inherit(wJobSelect, wSelect, {
	// 返回初始值，默认选中第一个职位
	getInitValue : function() {
		var _self = this;
		var initValue = _self._superApply(arguments);
		if (StringUtils.isBlank(initValue)) {
			var datas = _self.getDataProvider();
			if (datas.length > 0) {
				// var v = {};
				// v[datas[0].value] = datas[0].text;
				// return v;
				initValue = datas[0].value;
			}
		}
		return initValue;
	}
});
return wJobSelect;
});