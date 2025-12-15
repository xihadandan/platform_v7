define([ "jquery", "commons", "constant", "server", "appContext", "ListViewWidgetDevelopment" ], function($, commons,
		constant, server, appContext, ListViewWidgetDevelopment) {
	var JDS = server.JDS;
	var UrlUtils = commons.UrlUtils;
	var StringBuilder = commons.StringBuilder;
	// 平台应用_公共功能_表单数据查看工作_视图组件二开
	var WorkFlowDyformListViewWidgetDevelopment = function() {
		ListViewWidgetDevelopment.apply(this, arguments);
	};
	commons.inherit(WorkFlowDyformListViewWidgetDevelopment, ListViewWidgetDevelopment, {
		onClickRow : function(index, row) {
			var _self = this;
			var url = _self.getWorkViewUrl(index, row);
			var customUrlParams = _self.getCustomUrlParams(index, row);
			url = UrlUtils.appendUrlParams(url, customUrlParams);
			var options = {};
			options.url = url;
			options.ui = _self.getWidget();
			options.size = "large";
			appContext.openWindow(options);
		},
		getWorkViewUrl : function(index, row) {
			var url = ctx + "/workflow/work/v53/view/work?taskInstUuid={0}&flowInstUuid={1}";
			var taskInstUuid = row.task_inst_uuid || row.TASK_INST_UUID || row.taskInstUuid || "";
			var flowInstUuid = row.flow_inst_uuid || row.FLOW_INST_UUID || row.flowInstUuid || "";
			var sb = new StringBuilder();
			sb.appendFormat(url, taskInstUuid, flowInstUuid);
			return sb.toString();
		},
		// 获取要附加的自定义参数
		getCustomUrlParams : function(index, row) {
			return {};
		},
		// 删除工作
		deleteWork : function(event, options) {
			var _self = this;
			var selection = _self.getSelection();
			if (selection.length === 0) {
				return;
			}
			var flowInstUuids = [];
			$.each(selection, function(i, row) {
				var flowInstUuid = row.flow_inst_uuid || row.FLOW_INST_UUID || row.flowInstUuid || "";
				flowInstUuids.push(flowInstUuid);
			});
			var params = options.params || {};
			var confirmMsg = params.confirmMsg || "确认删除表单及流程数据？";
			var successMsg = params.successMsg || "删除成功!";
			appModal.confirm(confirmMsg, function(result) {
				if (result) {
					JDS.call({
						service : "listWorkService.deleteWork",
						data : [ flowInstUuids ],
						version : "",
						success : function(result) {
							appModal.success(successMsg);
							_self.refresh(true);
							_self.trigger(constant.WIDGET_EVENT.BadgeRefresh);
						}
					});
				}
			});
		},
		getSelection : function(multiple) {
			var _self = this;
			var selection = _self.getWidget().getSelections();
			if (selection.length === 0) {
				appModal.error("请选择记录!");
				return [];
			}
			if (!multiple && selection.length > 1) {
				appModal.error("只能选择一条记录!");
				return [];
			}
			return selection;
		}
	});
	return WorkFlowDyformListViewWidgetDevelopment;
});