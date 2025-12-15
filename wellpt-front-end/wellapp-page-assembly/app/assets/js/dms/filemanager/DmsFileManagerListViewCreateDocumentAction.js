define([ "jquery", "commons", "constant", "server", "appContext", "DmsListViewActionBase" ], function($, commons,
		constant, server, appContext, DmsListViewActionBase) {
	var StringUtils = commons.StringUtils;
	// 视图列表新建文档操作
	var DmsListViewAddAction = function() {
		DmsListViewActionBase.apply(this, arguments);
	}
	commons.inherit(DmsListViewAddAction, DmsListViewActionBase, {
		btn_file_manager_list_view_create_document : function(options) {
			var _self = this;
			var urlParams = _self.getUrlParams();
			var dmsId = urlParams["dms_id"];
			var fileManagerWidget = appContext.getWidgetById(dmsId);
			if (fileManagerWidget) {
				var folderUuid = fileManagerWidget.getCurrentFolderUuid();
				if (StringUtils.isNotBlank(folderUuid)) {
					var options = {
						folderUuid : folderUuid,
						ui : fileManagerWidget
					}
					fileManagerWidget.dmsFileServices.createDocument(options);
				}
			}
		}
	});

	return DmsListViewAddAction;
});