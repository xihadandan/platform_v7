define([ "jquery", "commons", "constant", "server", "appContext", "DmsListViewActionBase" ], function($, commons,
		constant, server, appContext, DmsListViewActionBase) {
	var StringUtils = commons.StringUtils;
	// 视图列表编辑操作
	var DmsListViewViewAction = function() {
		DmsListViewActionBase.apply(this, arguments);
	}
	commons.inherit(DmsListViewViewAction, DmsListViewActionBase, {
		btn_list_view_view : function(options) {
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
					rowdata : rowdata,
				};
				var urlParams = _self.getUrlParams(urlParamOptions);
				// 解析事件参数作为附加传递的参数
				var rawParams = $.extend(true, {}, options.rawParams);
				var rawRowData = options.rowData;
				options.rowData = selection[0];
				var eventParams = appContext.resolveParams(rawParams, options);
				options.rowData = rawRowData;
				urlParams = $.extend(urlParams, options.params, eventParams);
				if (!urlParams.idValue) {
					// 无主键值的情况下
					return;
				}
				// urlParams.ep_displayAsLabel = true;
				urlParams.target = options.target;
				if (options.urlEpParams) {
					for ( var key in options.urlEpParams) {
						urlParams[key] = options.urlEpParams[key];
					}
				}
				if (constant.TARGET_POSITION.DIALOG == options.target) { // 弹窗展示
					options.urlParams = urlParams;
					_self.dmsDataServices.openDialog(options);

				} else {
					_self.dmsDataServices.openWindow({
						urlParams : urlParams,
						ui : ui
					});
				}

			} else { // 多选模式，固定是通过打开新窗口来实现
				// 批量打开编辑
				for (var i = 0; i < selection.length; i++) {
					var rowdata = selection[i];
					var urlParamOptions = {
						ui : ui,
						appFunction : options.appFunction,
						rowdata : rowdata,
					};
					var urlParams = _self.getUrlParams(urlParamOptions);
					// 解析事件参数作为附加传递的参数
					var rawParams = $.extend(true, {}, options.rawParams);
					var rawRowData = options.rowData;
					options.rowData = rowdata;
					var eventParams = appContext.resolveParams(rawParams, options);
					options.rowData = rawRowData;
					urlParams = $.extend(urlParams, options.params, eventParams);
					if (!urlParams.idValue) {
						// 无主键值的情况下
						continue;
					}
					urlParams.target = options.target;
					urlParams.ep_displayAsLabel = true;
					if (options.urlEpParams) {
						for ( var key in options.urlEpParams) {
							urlParams[key] = options.urlEpParams[key];
						}
					}
					// 设置每一行的不同参数
					if (options.rowUrlEpParams && options.rowUrlEpParams.length > 0
							&& options.rowUrlEpParams[i] != undefined) {
						for ( var key in options.rowUrlEpParams[i]) {
							urlParams[key] = options.rowUrlEpParams[i][key];
						}
					}
					_self.dmsDataServices.openWindow({
						urlParams : urlParams,
						ui : ui
					});
				}

			}

		}
	});

	return DmsListViewViewAction;
});