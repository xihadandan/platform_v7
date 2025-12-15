define([ "jquery", "commons", "constant", "server", "appContext", "DmsListViewActionBase" ], function($, commons,
		constant, server, appContext, DmsListViewActionBase) {
	var StringUtils = commons.StringUtils;
	// 视图列表复制操作
	var DmsListViewCopyAction = function() {
		DmsListViewActionBase.apply(this, arguments);
	}
	// 视图列表复制操作，等同于编辑操作，然后把对应的dataUuid清空
	commons.inherit(DmsListViewCopyAction, DmsListViewActionBase, {
		btn_list_view_copy : function(options) {
			var _self = this;
			var ui = options.ui;
			// 当前操作的数据
            var selection = options.rowData != null ? options.rowData : ui.getSelections();
			if(!_self.checkSelection(options)) {
                return;
            }

			if (selection.length == 1) { // 单选情况下
				var rowdata = selection[0];
				var urlParamOptions = {
					ui : ui,
					appFunction : options.appFunction,
					rowdata : rowdata
				};
				var urlParams = _self.getUrlParams(urlParamOptions);
				urlParams.ep_view_mode = "1";
				urlParams.target=options.target;
				if (constant.TARGET_POSITION.DIALOG == options.target) { // 弹窗展示
					options.urlParams = urlParams;
					_self.dmsDataServices.openDialog(options);
				} else {
					_self.dmsDataServices.openWindow({
						urlParams : urlParams,
						ui : ui,
						target : options.target,
						targetWidgetId : options.targetWidgetId
					});
				}
			}

		}
	});

	return DmsListViewCopyAction;
});