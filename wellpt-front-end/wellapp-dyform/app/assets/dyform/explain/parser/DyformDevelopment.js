define([ "jquery", "server", "commons", "constant", "appContext", "appModal" ], function($, server, commons, constant,
		appContext, appModal) {
	var StringUtils = commons.StringUtils;
	var DyformDevelopment = function(dyform) {
		this.dyform = dyform;
	};
	$.extend(DyformDevelopment.prototype, {
		getDyform : function() {
			return this.dyform;
		},
		// 初始化
		onInit : $.noop,
		onLoadDefinitionsSuccess : $.noop,
		onLoadDefinitionsFailure : function() {
			appModal.error({
				message : "加载表单定义失败"
			});
		},
		onLoadDataSuccess : $.noop,
		onLoadDataFailure : function() {
			appModal.error({
				message : "加载表单数据失败"
			});
		},
		onLoadDefinitionsAndDataSuccess : $.noop,
		onLoadDefinitionsAndDataFailure : function() {
			appModal.error({
				message : "加载表单定义及数据失败"
			});
		},
		beforeParseForm : $.noop,
		afterParseForm : $.noop,
		beforeSetData : $.noop,
		afterSetData : $.noop,
		beforeImportData : $.noop,
		beforeFillImportData : $.noop,
		beforeSetReadOnly : $.noop,
		afterSetReadOnly : $.noop,
		afterSetEditable : $.noop,
		// 保存
		save : $.noop,
		onSaveSuccess : function(result) {
			var _self = this;
			appModal.alert({
				message : "保存成功！",
				callback : function() {
					_self.refresh();
				}
			});
		},
		onSaveFailure : function() {
		},
		// 刷新窗口
		refresh : function() {
			var _self = this;
			var windowManager = appContext.getWindowManager();
			windowManager.refresh();
		}
	});
	return DyformDevelopment;
});