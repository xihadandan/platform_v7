define([ "jquery", "commons", "constant", "server", "appModal", "WorkFlowListViewWidgetDevelopmentBase" ], function($,
		commons, constant, server, appModal, ListViewWidgetDevelopment) {
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	var jds = server.JDS;

	// 平台应用_工作委托_委托设置_视图组件二开
	var WorkFlowDelegationSettingsOtherListViewWidgetDevelopment = function() {
		var _self = this;
		ListViewWidgetDevelopment.apply(this, arguments);
	};
	commons.inherit(WorkFlowDelegationSettingsOtherListViewWidgetDevelopment, ListViewWidgetDevelopment, {
		onClickRow : function(index, row) {
			var _self = this;
			var url = ctx + "/workflow/delegation/settings/view/" + row.UUID;
			if (row.STATUS == 2) {
				url = ctx + "/workflow/delegation/settings/consult/" + row.UUID;
			}
			var options = {};
			options.url = url;
			options.ui = _self.getWidget();
			options.size = "large";
			appContext.openWindow(options);
		}
	});
	return WorkFlowDelegationSettingsOtherListViewWidgetDevelopment;
});