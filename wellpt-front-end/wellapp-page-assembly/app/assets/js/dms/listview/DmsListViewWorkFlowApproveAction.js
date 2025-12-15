define([ "jquery", "commons", "constant", "server", "appContext", "DmsListViewActionBase", "appModal",
		"WorkFlowApprove" ], function($, commons, constant, server, appContext, DmsListViewActionBase, appModal,
		WorkFlowApprove) {
	var StringUtils = commons.StringUtils;
	// 视图列表新增操作
	var DmsListViewWorkFlowApproveAction = function() {
		DmsListViewActionBase.apply(this, arguments);
	}
	commons.inherit(DmsListViewWorkFlowApproveAction, DmsListViewActionBase, {
		// 新开页面新增
		btn_list_view_send_to_approve : function(options) {
			var _self = this;
			var ui = options.ui;
			var actionFunction = options.appFunction;

			var selection = ui.getSelections();
			var selectionLength = selection.length;
			var promptMsg = actionFunction.promptMsg;
			var singleSelect = actionFunction.singleSelect;
			if (selectionLength === 0 && StringUtils.isNotBlank(promptMsg)) {
				appModal.error(promptMsg);
				return;
			}
			if (selectionLength === 0) {
				ui.refresh(false);
				return;
			}
			if ((singleSelect == true || options.params.singleSelect == "true") && selectionLength > 1) {
				// 单选提示
				if(StringUtils.isNotBlank(options.params.singleSelectPromptMsg)) {
					appModal.error(options.params.singleSelectPromptMsg);
				} else {
					appModal.error(actionFunction.singleSelectPromptMsg);
				}
				return;
			}

			// 加入rowData参数，重新解析事件处理参数
            var rowData = selection[0];

            var paramsArr = [];
            $.each(selection, function (index, selectionElement) {
                options.rowData = selectionElement;
                var eventParams = $.extend(true, {}, {
                	linkTitle: options.rawParams.linkTitle,
                	linkUrl: options.rawParams.linkUrl
                });
                var params = appContext.resolveParams(eventParams, options);
                paramsArr.push(params);
            });

			options.rowData = rowData;
            options.params = $.extend(true, options.params || {}, _self._paramSplitByComma(paramsArr));

			// 组装参数
			var urlParams = _self.getUrlParams();
			var params = $.extend(true, {}, options.params || {});
			var approveOptions = $.extend(urlParams, {
				formUuid : rowData.formUuid,
				dataUuid : rowData.dataUuid
			}, params, {
				ui : options.ui,
                selectionLength: selectionLength
			});
			
			// 选择多笔数据只能配置为源文作为链接送审批
			if(selectionLength > 1 && StringUtils.isNotBlank(options.params.contentType) && options.params.contentType != "3") {
				appModal.error("选择多笔数据只能配置为源文作为链接送审批！");
				return false;
			}
			
			// 自动提交结果参数组装
			if (approveOptions.autoSubmit == "true") {
				var autoSubmitResult = {
					close : approveOptions.autoSubmitResultClose == "true",
					refresh : approveOptions.autoSubmitResultRefresh == "true",
					refreshParent : approveOptions.autoSubmitResultRefreshParent == "true",
					msg : approveOptions.autoSubmitResultMsg
				};
				approveOptions.autoSubmitResult = autoSubmitResult;
			}

			// 送审批
			var workFlowApprove = new WorkFlowApprove(approveOptions);
			workFlowApprove.sendToApprove();
		},
        _paramSplitByComma: function (arr) {
            var result = {};
            $.each(arr[0], function (key, val) {
                var valArr = [];
                $.each(arr, function (index, item) {
                    if (item[key]) {
                        valArr.push(item[key] + '');
                    } else {
                        valArr.push('');
                    }
                });

                result[key] = valArr.join(';');
            });
            return result;
        },
	});

	return DmsListViewWorkFlowApproveAction;
});