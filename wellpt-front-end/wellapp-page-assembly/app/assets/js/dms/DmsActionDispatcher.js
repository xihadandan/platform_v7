define([ "jquery", "commons", "constant", "server", "appContext", "appModal", "DmsDataServices" ], function($, commons,
		constant, server, appContext, appModal, DmsDataServices) {
	var StringUtils = commons.StringUtils;
	var KEY_DMS_ID = "dms_id";
	// 操作服务
	var dmsDataServices = new DmsDataServices();
	// 数据管理_操作派发
	return function(options) {
		// 1、执行前台JS操作
		var actionFunction = options.appFunction || {};
		var executeJsModule = actionFunction.executeJsModule;
		if (StringUtils.isNotBlank(executeJsModule)) {
			appContext.require([ executeJsModule ], function(app) {
				appContext.executeJsModule(app, options);
			});
		} else {
			// 2、执行后台Java操作
			var ui = options.ui;
			// 2.1 视图操作
			if (ui && ui.getSelections) {
				// 当前操作的数据
				var selection = options.rowData != null ? options.rowData : ui.getSelections();
				if(selection.length === 0 && options.selectedRow){
					selection.push(options.selectedRow);
				}
				var selectionLength = selection.length;
				var params = options.params || {};
				var promptMsg = params.promptMsg ? params.promptMsg : actionFunction.promptMsg;
				var singleSelect = actionFunction.singleSelect;
				if (selectionLength === 0 && StringUtils.isNotBlank(promptMsg)) {
					appModal.error(promptMsg);
				} else if (selectionLength === 0) {
					ui.refresh(false);
				} else if (singleSelect == true && selectionLength > 1) {
					// 单选提示
				    var singleSelectPromptMsg = params.singleSelectPromptMsg ? params.singleSelectPromptMsg : actionFunction.singleSelectPromptMsg;
					appModal.error(singleSelectPromptMsg);
				} else {
	                var data = {
	                        action : actionFunction,
	                        selection : selection,
	                        extras : params
	                };
					var dmsId = $(ui.element).data(KEY_DMS_ID);
					var acId = actionFunction.id;
					var urlParams = {
						dms_id : dmsId,
						ac_id : acId
					};
					dmsDataServices.performed({
						ui : ui,
						event : ui.event,
						params : params,
						urlParams : urlParams,
						data : data
					});
				}
			} else if (options.data) {
				// 2.2 单据操作
				dmsDataServices.performed(options);
			}
		}
	};

});