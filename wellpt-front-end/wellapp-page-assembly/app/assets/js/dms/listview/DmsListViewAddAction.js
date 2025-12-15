define([ "jquery", "commons", "constant", "server", "appContext", "DmsListViewActionBase", "appModal" ], function($,
		commons, constant, server, appContext, DmsListViewActionBase, appModal) {
	// 视图列表新增操作
	var DmsListViewAddAction = function() {
		DmsListViewActionBase.apply(this, arguments);
	}
	commons.inherit(DmsListViewAddAction, DmsListViewActionBase, {
		// 新开页面新增
		btn_list_view_add : function(options) {
			var _self = this;
			var urlParams = _self.getUrlParams();
			urlParams = $.extend(urlParams, options.params || {});
			urlParams.target = options.target;
			if (constant.TARGET_POSITION.DIALOG == options.target) { // 弹窗展示
				options.urlParams = urlParams;
				_self.dmsDataServices.openDialog(options);
			} else {
				_self.dmsDataServices.openWindow({
					urlParams : urlParams,
					useUniqueName : false,
					ui : options.ui,
					target : options.target,
					targetWidgetId : options.targetWidgetId
				});
			}
		},

	});

	return DmsListViewAddAction;
});