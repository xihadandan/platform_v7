define("DmsDyformSaveNewVersionWithRemarkAction", [ "jquery", "commons", "constant", "server", "appContext",
		"appModal", "DmsDyformActionBase" ], function($, commons, constant, server, appContext, appModal,
		DmsDyformActionBase) {
	var StringBuilder = commons.StringBuilder;
	var StringUtils = commons.StringUtils;
	// 表单单据查看阅读记录操作
	var DmsDyformSaveNewVersionWithRemarkAction = function() {
		DmsDyformActionBase.apply(this, arguments);
	}
	// 备注表单
	var remarkForm = "";
	remarkForm += '<form role="form">';
	remarkForm += '<div class="form-group">';
	remarkForm += '  <label for="name">备注</label>';
	remarkForm += '  <textarea class="form-control dyform-version-remark" rows="6"></textarea>';
	remarkForm += '</div>';
	remarkForm += '</form>';
	// 显示历史版本弹出框
	var showHistoryVersionDialog = function(versions) {
		var versionString = getVersionString(versions);
		var options = {
			title : "历史版本",
			message : versionString
		};
		appModal.dialog(options);
	};
	// 获取历史版本字符串
	var getVersionString = function(versions) {
		if (versions == null) {
			return "";
		}
		var sb = new StringBuilder();
		sb.append("<table class='table'>");
		sb.append("<thead>");
		sb.append("<tr><th>标题</th><th>版本</th><th>创建时间</th><th>备注</th></tr>");
		sb.append("</thead>");
		sb.append("<tbody>");
		$.each(versions, function(i, version) {
			sb.append("<tr>");
			sb.append("<td>");
			sb.append(version.title);
			sb.append("</td>");
			sb.append("<td>");
			sb.append(version.version);
			sb.append("</td>");
			sb.append("<td>");
			sb.append(version.createTime);
			sb.append("</td>");
			sb.append("<td>");
			sb.append(version.remark);
			sb.append("</td>");
			sb.append("</tr>");
		});
		sb.append("</tbody>");
		sb.append("</table>");
		return sb.toString();
	}
	commons.inherit(DmsDyformSaveNewVersionWithRemarkAction, DmsDyformActionBase, {
		btn_dyform_save_new_version_with_remark : function(options) {
			var _self = this;
			var documentData = _self.getDocumentData();
			var remark = _self.getExtra("remark");
			if (StringUtils.isBlank(remark)) {
				_self._showSetRemarkDialog(options);
			}
			// 重新执行操作的服务端处理
			// _self.dmsDataServices.performed(options);
		},
		_showSetRemarkDialog : function(dmsOptions) {
			var _self = this;
			var options = {
				title : "新版本备注",
				message : remarkForm,
				size : "middle",
				callback : function(result) {
					if (!result) {
						return;
					}
					var $remark = $(".dyform-version-remark", $(this));
					var remark = $remark.val();
					if (StringUtils.isBlank(remark)) {
						appModal.error("备注不能为空!");
						return false;
					}
					if (StringUtils.trim(remark).length > 255) {
						appModal.error("备注不能大于255个字符!");
						return false;
					}
					_self.setExtra("remark", StringUtils.trim(remark));
					_self.dmsDataServices.performed(dmsOptions);
				}
			};
			appModal.confirm(options);
		}
	});
	return DmsDyformSaveNewVersionWithRemarkAction;
});