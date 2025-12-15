define("AppNoticeDmsDyformEditAction", [ "jquery", "commons", "constant", "server", "appContext", "appModal",
		"DmsDyformActionBase" ], function($, commons, constant, server, appContext, appModal, DmsDyformActionBase) {
	var StringBuilder = commons.StringBuilder;
	// 表单单据公告编辑操作
	var AppNoticeDmsDyformEditAction = function() {
		DmsDyformActionBase.apply(this, arguments);
	}
	commons.inherit(AppNoticeDmsDyformEditAction, DmsDyformActionBase, {
		bnt_notice_dyform_eidt : function(options) {
			var _self = this;
			// 重新执行操作的服务端处理
			_self.dmsDataServices.performed(options);
		}
	});
	return AppNoticeDmsDyformEditAction;
});