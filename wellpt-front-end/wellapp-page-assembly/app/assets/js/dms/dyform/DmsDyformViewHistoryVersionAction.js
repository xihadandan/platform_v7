define("DmsDyformViewHistoryVersionAction", [ "jquery", "commons", "constant", "server", "appContext", "appModal",
		"DmsDyformActionBase" ], function($, commons, constant, server, appContext, appModal, DmsDyformActionBase) {
	var StringBuilder = commons.StringBuilder;
	// 表单单据查看阅读记录操作
	var DmsDyformViewHistoryVersionAction = function() {
		DmsDyformActionBase.apply(this, arguments);
	}

	// 获取历史版本字符串
	var getVersionString = function(versions) {
		if (versions == null) {
			return "";
		}
		var sb = new StringBuilder();
		sb.append("<table class='table dms-history-version-table'>");
		sb.append("<thead>");
		sb.append("<tr><th>标题</th><th>版本</th><th>创建时间</th><th>备注</th></tr>");
		sb.append("</thead>");
		sb.append("<tbody>");
		$.each(versions, function(i, version) {
			sb.append("<tr versionUuid='" + version.uuid + "'>");
			sb.append("<td>");
			sb.append(version.title);
			sb.append("</td>");
			sb.append("<td>");
			sb.append(version.version);
			sb.append("</td>");
			sb.append("<td>");
			sb.append(version.createTime);
			sb.append("</td>");
			sb.append("<td class='remark' title='" + version.remark + "'>");
			sb.append(version.remark);
			sb.append("</td>");
			sb.append("</tr>");
		});
		sb.append("</tbody>");
		sb.append("</table>");
		return sb.toString();
	}
	commons.inherit(DmsDyformViewHistoryVersionAction, DmsDyformActionBase, {
		btn_dyform_view_history_version : function(options) {
			var _self = this;
			options.success = function(result) {
				_self.showHistoryVersionDialog(result.data);
			};
			// 重新执行操作的服务端处理
			_self.dmsDataServices.performed(options);
		},
		// 显示历史版本弹出框
		showHistoryVersionDialog : function(versions) {
			var _self = this;
			var versionString = getVersionString(versions);
			var options = {
				title : "历史版本",
				message : versionString,
				shown : function() {
					$(".dms-history-version-table").on("click", "tr", function() {
						var versionUuid = $(this).attr("versionUuid");
						if (StringUtils.isBlank(versionUuid)) {
							return;
						}
						_self.dmsDataServices.openWindow({
							ui : _self.ui,
							urlParams : {
								dms_id : _self.ui.getId(),
								ac_id : "btn_dyform_open_version",// 打开窗口的操作ID
								ep_ac_get : "btn_dyform_get_version_data",// 附加的打开窗口后获取版本数据的操作ID
								ep_ac_get_v_id : versionUuid,// 附加的版本UUID
								v_id : versionUuid
							}
						});
					});
				}
			};
			appModal.dialog(options);
		}
	});
	return DmsDyformViewHistoryVersionAction;
});