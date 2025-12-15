define([ "jquery", "commons", "constant", "server", "appContext", "DmsDataServices" ], function($, commons, constant,
		server, appContext, DmsDataServices) {
	var dmsUrl = ctx + "/dms/data/services";
	var dmsParameters = "?idKey={0}&idValue={1}&dataStoreId={2}&dms_id={3}";
	var StringBuilder = commons.StringBuilder;
	var StringUtils = commons.StringUtils;
	var dmsDataServices = new DmsDataServices();
	var getViewInfo = function(ui, row) {
		var configuration = ui.getConfiguration();
		var idKeys = [];
		var idValues = [];
		$.each(configuration.columns, function() {
			if (this.idField === "1") {
				idKeys.push(this.name);
			}
		});
		if (idKeys.length === 0) {
			idKeys.push("UUID");
		}
		for (var i = 0; i < idKeys.length; i++) {
			if (row) {
				idValues.push(row[idKeys[i]]);
			} else {
				idValues.push("");
			}
		}
		var idKey = idKeys.join(";");
		var idValue = idValues.join(";");
		var dataStoreId = ui.getDataProvider().options.dataStoreId;
		return {
			idKey : idKey,
			idValue : idValue,
			dataStoreId : dataStoreId
		}
	};

	return function(options) {
		var ui = options.ui;
		var viewInfo = getViewInfo(ui);
		var idKey = viewInfo.idKey;
		var idValue = viewInfo.idValue;
		var dataStoreId = viewInfo.dataStoreId;
		var dmsId = $(ui.element).data("dms_id");
		var acId = "open_view";
		var lvId = ui.getId();

		var selection = ui.getSelections();
		var actionFunction = options.appFunction;
		var promptMsg = actionFunction.promptMsg;
		if (selection.length === 0 && StringUtils.isNotBlank(promptMsg)) {
			appModal.error(promptMsg);
			return;
		} else if (selection.length === 0) {
			ui.refresh(false);
			return;
		}

		for (var i = 0; i < selection.length; i++) {
			var rowData = selection[i];
			var urlParams = {
				idKey : idKey,
				idValue : rowData[idKey],
				dataStoreId : dataStoreId,
				dms_id : dmsId,
				ac_id : acId,
				lv_id : lvId
			};
			dmsDataServices.openWindow({
				urlParams : urlParams,
				ui : ui
			});
		}
	};
});