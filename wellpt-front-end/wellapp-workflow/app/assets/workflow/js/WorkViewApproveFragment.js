define([ "constant", "commons", "server", "appContext", "appModal", "DyformFunction" ], function(constant, commons, server, appContext,
		appModal, DyformFunction) {
	// 平台应用_工作流程_送审批_流程片段二开
	var UrlUtils = commons.UrlUtils;
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	var JDS = server.JDS;
	return {
		// 表单初始化成功后处理
		onDyformInitSuccess : function() {
			var _self = this;
			// 调用父类方法
			_self._superApply(arguments);
			var workData = _self.getWorkData();

			// 获取发送的内容
			var epSendContent = $("input[name='ep_sendContent']").val();
			if (StringUtils.isBlank(epSendContent)) {
				epSendContent = _self.getExtraParam("custom_rt_sentContent");
			}
			if (StringUtils.isNotBlank(epSendContent)) {
				var sendContent = JSON.parse(epSendContent);
				var sendContentType = sendContent.type;
				// 复制源文送审批
				if (sendContentType == 2 && StringUtils.isBlank(workData.flowInstUuid)) {
					_self.setCopySource2Dyform(sendContent);
				}
				// 原文作为链接送审批
				_self.setLink2Dyform(sendContent);
			}
		},
		// 复制源文送审批，设置表单字段值
		setCopySource2Dyform : function(sendContent) {
			var _self = this;
			var formUuid = sendContent.formUuid;
			var dataUuid = sendContent.dataUuid;
			var botRuleId = sendContent.botRuleId;
			if(StringUtils.isNotBlank(botRuleId)) {
				_self.setBotSource2Dyform(formUuid, dataUuid, botRuleId, sendContent);
			} else {
				// 表单数据
				var dyformData = DyformFunction.loadFormDefinitionData(formUuid, dataUuid);
				_self.setNewFormData2Dyform(dyformData, sendContent);
			}
		},
		// 单据转换后的表单数据设置到表单
		setBotSource2Dyform : function(formUuid, dataUuid, botRuleId, sendContent) {
			var _self = this;
			var dyform = _self.getDyform();
			JDS.restfulPost({
				url: ctx + "/api/workflow/approve/convertDyformDataByBotRuleId",
				data: {
					sourceFormUuid: formUuid,
					sourceDataUuid: dataUuid,
					botRuleId: botRuleId
				},
				success : function(result) {
					if(result.data) {
						_self.setNewFormData2Dyform(result.data, sendContent);
					}
				}
			});
		},
		// 设置新的表单数据到表单中
		setNewFormData2Dyform : function(dyformData, sendContent) {
			var _self = this;
			var onlyFillEditableField = true;
			if(sendContent.onlyFillEditableField == "false") {
				onlyFillEditableField = false;
			}
			// 表单定义
			var formDefinition = JSON.parse(dyformData.formDefinition);
			// 设置主表字段
			$.extend(dyformData, formDefinitionMethod);
			var formDataOfMainform = dyformData.getFormDataOfMainform();
			var dyform = _self.getDyform();
			for ( var fieldName in formDataOfMainform) {
				if(fieldName == "uuid") {
					continue;
				}
				var field = dyform.getField(fieldName);
				if(field != null) {
					// 只填充可编辑的字段
					if(onlyFillEditableField == true) {
						if(field.isEditable()) {
							field.setValue(formDataOfMainform[fieldName]);
						}
					} else {
						field.setValue(formDataOfMainform[fieldName]);
					}
				}
			}
			// 设置从表字段
			var formDatas = dyformData.formDatas;
			for ( var subformUuid in formDatas) {
				// 从表定义
				var subformDefinition = formDefinition.subforms[subformUuid];
				if (subformDefinition) {
					var subform = dyform.getSubform(subformDefinition.outerId);
					if (subform) {
						// 只填充有操作按钮的从表数据
						if(onlyFillEditableField == true) {
							if(subform.isOperateBtnShow()) {
								var subformDatas = formDatas[subformUuid];
								$.each(subformDatas, function(i, rowData) {
									subform.addRowData(rowData);
								});
							}
						} else {
							var subformDatas = formDatas[subformUuid];
							$.each(subformDatas, function(i, rowData) {
								subform.addRowData(rowData);
							});
						}
					}
				}
			}
		},
		// 原文作为链接送审批
		setLink2Dyform : function(sendContent) {
			var _self = this;
			var dyformSelector = _self.getDyformSelector();
			var linkUrl = sendContent.linkUrl;
			var linkTitle = decodeURIComponent(sendContent.linkTitle);
			if (StringUtils.isBlank(linkUrl) || StringUtils.isBlank(linkTitle)) {
				console.error("linkUrl or linkTitle is blank");
				return;
			}
			var workData = _self.getWorkData();
			var linkUrls = linkUrl.split(";");
			var linkTitles = linkTitle.split(";");
			var sb = new StringBuilder();
			sb.appendFormat('<div class="sent-link-content" style="margin-bottom:30px;">');
			sb.appendFormat('<table style="margin-bottom:0;"><tbody><tr><td class="title">相关文档</td></tr></tbody></table>');
			for (var i = 0; i < linkUrls.length; i++) {
				sb.appendFormat('<p/>');
				var linkTpl = '<span class="btn-link link-title" linkUrl="{0}">{1}</span>';
				sb.appendFormat(linkTpl, linkUrls[i], linkTitles[i]);
			}
			sb.appendFormat('<p> </p>');
			sb.appendFormat('</div>');
			$(dyformSelector).append(sb.toString());
			$(dyformSelector).on("click", ".link-title", function() {
				var linkUrl = $(this).attr("linkUrl");
				if (StringUtils.isNotBlank(linkUrl)) {
					if (StringUtils.isNotBlank(workData.flowInstUuid)) {
						linkUrl = UrlUtils.appendUrlParams(linkUrl, {
							approveFlowInstUuid : workData.flowInstUuid
						});
					}
					// 默认发起流程
					var openLinkOptions = {};
					openLinkOptions.url = linkUrl + '&isXGWD=true';
					openLinkOptions.ui = _self;
					openLinkOptions.size = "large";
					appContext.openWindow(openLinkOptions);
				}
			});
			// 发起流程时添加流程运行时参数
			if (StringUtils.isBlank(workData.flowInstUuid)) {
				_self.addExtraParam("custom_rt_sentContent", JSON.stringify(sendContent));
				_self.addExtraParam("custom_rt_workViewFragment", "WorkViewApproveFragment");
			}
		}
	};
});
