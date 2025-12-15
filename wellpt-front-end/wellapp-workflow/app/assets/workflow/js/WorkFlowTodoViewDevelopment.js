define([ "jquery", "commons", "constant", "server", "WorkFlowListViewWidgetDevelopmentBase" ], function($, commons,
		constant, server, WorkFlowListViewWidgetDevelopmentBase) {
	var JDS = server.JDS;
	var StringBuilder = commons.StringBuilder;
	// 平台应用_工作流程_工作待办_视图组件二开
	var WorkFlowTodoViewDevelopment = function() {
		WorkFlowListViewWidgetDevelopmentBase.apply(this, arguments);
	};
	commons.inherit(WorkFlowTodoViewDevelopment, WorkFlowListViewWidgetDevelopmentBase, {
		// 行数据渲染后回调方法，子类可覆盖
		onPostBody : function(dataArray) {
			if (dataArray == null || dataArray.length == 0) {
				return;
			}
			var _self = this;
			var uuids = [];
			$.each(dataArray, function(i, data) {
				uuids.push(data.uuid);
			});
			JDS.call({
				service : "viewComponentService.filterUnread",
				data : [ uuids ],
				success : function(result) {
					_self.markUnreadStyle(result.data, dataArray);
				}
			});
		},
		// 标记未阅样式
		markUnreadStyle : function(unreadUuids, dataArray) {
			var _self = this;
			var widget = _self.getWidget();
			$.each(dataArray, function(index, data) {
				if ($.inArray(data.uuid, unreadUuids) != -1) {
					var trSelector = "tr[data-index='" + index + "']";
					var $tr = $(trSelector, widget.element);
					$tr.addClass("unread");
				}
			});
		}
	});
	return WorkFlowTodoViewDevelopment;
});