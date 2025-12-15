var WorkFlow = WorkFlow || {};
$.ajax({
	url : ctx + "/resources/pt/js/workflow/work/jquery.workflowException.js",
	dataType : "script",
	async : false
});
$(function() {
	// 套打角本
	var printScript = '<div style="display: none">'
			+ '<iframe id="print_form_iframe" name="print_form_iframe" style="width: 1px; height: 0px;"></iframe>'
			+ '<form id="print_form" name="print_form" action="" method="post"'
			+ '	target="print_form_iframe">' + '	<input type="hidden" name="filename" value="" />'
			+ '	<input type="hidden" name="taskUuid" value="" />'
			+ '	<input type="hidden" name="formUuid" value="" />'
			+ '	<input type="hidden" name="dataUuid" value="" />'
			+ '	<input type="hidden" name="printTemplateId"' + '		value="sdf" /></form>'
			+ '<div id="dlg_select_print_template"></div>' + '</div>';
	var bean = null;
	var loadWorkData = true;
	var getDraftService = "workService.getDraft";
	var getTodoService = "workService.getTodo";

	var dlg_select_goto_task_selector = "#dlg_select_goto_task";
	var dlg_select_rollback_task_selector = "#dlg_select_rollback_task";

	// 批量操作，需进行复杂交互
	// 提交
	var submitService = "workService.submit";
	var isAllowedSubmitService = "listWorkService.isAllowedSubmit";
	var isRequiredSignOpinionService = "listWorkService.isRequiredSignOpinion";
	var checkSubmitTaskService = "listWorkService.checkSubmitTask";
	// 挂职流程检测
	var isGzWorkDataService = "listWorkService.isGzWorkData";
	// 送审批
	var gotoApproveService = "listWorkService.gotoApprove";
	// 转办
	var transferService = "workService.transfer";
	var isAllowedTransferService = "listWorkService.isAllowedTransfer";
	var isRequiredTransferOpinionService = "listWorkService.isRequiredTransferOpinion";
	// 会签
	var counterSignService = "workService.counterSign";
	var isAllowedCounterSignService = "listWorkService.isAllowedCounterSign";
	var isRequiredCounterSignOpinionService = "listWorkService.isRequiredCounterSignOpinion";
	// 抄送
	var copyToService = "workService.copyTo";
	// 套打
	var printUrl = "/workflow/work/print";
	// 移交
	var handOverService = "listWorkService.handOver";
	var isAllowedHandOverService = "listWorkService.isAllowedHandOver";
	var isRequiredHandOverOpinionService = "listWorkService.isRequiredHandOverOpinion";
	// 获取可跳转的环节
	var getGotoTaskService = "workService.getGotoTasks";
	// 跳转环节
	var gotoTaskService = "workService.gotoTask";
	var checkGotoTasksService = "listWorkService.checkGotoTasks";
	var isRequiredGotoTaskOpinionService = "listWorkService.isRequiredGotoTaskOpinion";

	// 批量操作，无需进行复杂交互
	// 删除草搞
	var deleteDraftService = "workService.deleteDraft";
	// 删除工作
	var deleteService = "workService.delete";
	// 管理员删除工作
	var deleteByAdminService = "workService.deleteByAdmin";
	// 退回
	var rollbackService = "listWorkService.rollback";
	var checkAndGetToRollbackTasksService = "listWorkService.checkAndGetToRollbackTasks";
	var isAllowedRollbackService = "listWorkService.isAllowedRollback";
	var isRequiredRollbackOpinionService = "listWorkService.isRequiredRollbackOpinion";
	// 直接退回
	var directRollbackService = "workService.directRollback";
	// 撤回
	var cancelService = "workService.cancel";

	var isAllowedCancelService = "listWorkService.isAllowedCancel";
	// 撤回结束流程
	var cancelOverService = "workService.cancelOver";
	// 关注
	var attentionService = "workService.attention";
	// 取消关注
	var unfollowService = "workService.unfollow";
	// 签署意见
	var signOpinionService = "workService.signOpinion";
	// 已阅
	var markReadService = "workService.markRead";
	// 未阅
	var markUnreadService = "workService.markUnread";
	/* modified by huanglinchuan 2014.11.1 begin */
	// 催办
	var remindService = "listWorkService.remind";
	/* modified by huanglinchuan 2014.11.1 end */
	/* add by linz 2015年5月11日 10:56:21 */
	var genTimeService = "workService.isSameFlow";

	// 流程错误代码
	var WorkFlowErrorCode = {
		WorkFlowException : "WorkFlowException", // 工作流异常
		IdentityNotFlowPermission : "IdentityNotFlowPermission"// 用户没有权限访问流程
	};

	// 如果工作信息不存在则加载工作信息
	function loadTodoWorkData(taskUuid) {
		var bean = null;
		JDS.call({
			service : getTodoService,
			data : [ taskUuid, null ],
			async : false,
			success : function(result) {
				bean = result.data;
			},
			error : function(jqXHR) {
				oAlert2("操作失败!");
			}
		});
		return bean;
	}

	// 提交草搞
	WorkFlow.newWorkById = function(flowDefId) {
		var url = ctx + "/workflow/work/new/" + flowDefId;
		window.open(url);
	};

	// 提交草搞
	WorkFlow.submitDraft = function(flowInstUuid) {
		var $element = $(this);

		var flowInstUuids = [];
		if (isTopBtn($element)) {
			$("input[class=checkeds]:checked").each(function() {
				flowInstUuids.push($(this).val());
			});
			if (flowInstUuids.length == 0) {
				oAlert("请选择一条记录!");
				return;
			}
		} else {
			flowInstUuids.push(flowInstUuid);
		}
		if (flowInstUuids.length > 1) {
			oAlert2("只能选择一条记录!");
			return;
		}

		// 如果工作信息不存在则加载工作信息
		if (loadWorkData === true) {
			JDS.call({
				service : getDraftService,
				data : [ flowInstUuids[0] ],
				async : false,
				success : function(result) {
					bean = result.data;
				},
				error : function(jqXHR) {
					oAlert2("操作失败!");
				}
			});
		} else {
			loadWorkData = true;
		}
		// 提交工作
		doSubmit($element, bean);
	};

	// 提交工作
	WorkFlow.submit = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		// 提交权限检测
		if (isAllowedSubmit($element, taskInstUuids) === false) {
			oAlert2("没有权限提交所选工作!");
			return;
		}

		// 判断是否提交必填意见
		if (isRequiredSignOpinion($element, taskInstUuids)) {
			openToSignOpinion($element, taskInstUuids);
			return;
		}
		var opinionName = getOpinionName($element, taskInstUuids);
		var opinionValue = getOpinionValue($element, taskInstUuids);
		var opinionText = getOpinionText($element, taskInstUuids);

		// 流程定义必填域检验
		var uuids = [];
		for (var i = 0; i < taskInstUuids.length; i++) {
			var uuid = taskInstUuids[i];
			JDS.call({
				service : checkSubmitTaskService,
				data : [ uuid ],
				async : false,
				success : function(result) {
					uuids.push(uuid);
				},
				error : function(jqXHR) {
				}
			});
		}

		// 提交工作，按顺序递归调用提交工作
		var size = uuids.length;
		if (size > 0) {
			var all = taskInstUuids.length == size;
			doSubmit($element, uuids, 0, size, opinionName, opinionValue, opinionText, all);
		} else {
			oAlert2("提交失败，需打开工作进行提交!");
		}
	};
	// 提交工作
	function doSubmit($element, taskInstUuids, index, size, opinionName, opinionValue, opinionText, all) {
		if (index >= size && all == true) {
			oAlert("提交成功!", function() {
				refreshWindow($element);
			});
			return;
		} else if (index >= size) {
			oAlert2("工作提交处理结束，但有错误!", function() {
				refreshWindow($element);
			});
			return;
		}
		var taskInstUuid = taskInstUuids[index];

		// 加载流程数据
		var workData = getTodoWorkData($element, taskInstUuid);
		if (workData != null) {
			// 操作动作及类型
			workData.action = "提交";
			workData.actionType = "Submit";
			// 办理意见
			workData.opinionLabel = opinionName;
			workData.opinionValue = opinionValue;
			workData.opinionText = opinionText;
			// 提交工作
			JDS.call({
				service : submitService,
				data : [ workData ],
				async : false,
				success : function(result) {
					// 按顺序递归调用提交工作
					doSubmit($element, taskInstUuids, index + 1, size, opinionName, opinionValue,
							opinionText, all);
				},
				error : function(jqXHR) {
					var msg = JSON.parse(jqXHR.responseText);
					var callback = {};
					if (WorkFlowErrorCode.WorkFlowException === msg.errorCode
							|| WorkFlowErrorCode.IdentityNotFlowPermission === msg.errorCode) {
						callback = function() {
							// 按顺序递归调用提交工作
							doSubmit($element, taskInstUuids, index + 1, size, opinionName, opinionValue,
									opinionText, all);
						};
					} else {
						callback = function() {
							// 按顺序递归调用提交工作
							doSubmit($element, taskInstUuids, index, size, opinionName, opinionValue,
									opinionText, all);
						};
					}
					handlerException(msg, $element, taskInstUuid, workData, callback);
				}
			});
		} else {
			// 按顺序递归调用提交工作
			doSubmit($element, taskInstUuids, index + 1, size, opinionName, opinionValue, opinionText, all);
		}
	}
	// 处理流程操作返回 的错误信息
	function handlerException(msg, $element, taskInstUuid, workData, callback) {
		$.workflowException.handle({
			element : $element,
			workData : workData,
			msg : msg,
			callback : callback
		});
	}
	// 处理流程操作返回 的错误信息
	function handlerError(jqXHR, $element, taskInstUuid, workData, callback) {
		var msg = JSON.parse(jqXHR.responseText);
		$.workflowException.handle({
			element : $element,
			workData : workData,
			msg : msg,
			callback : callback
		});
	}
	
	// 送审批
	WorkFlow.gotoApprove = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid, false);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		// 送审批处理
		JDS.call({
			service : gotoApproveService,
			data : [ taskInstUuids[0] ],
			success : function(result) {
				oAlert2("所选工作无法送审批!", function() {
					// refreshWindow($element);
				});
			},
			error : function(jqXHR) {
				handlerError(jqXHR, $element);
			}
		});
	}

	// 转办
	WorkFlow.transfer = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		// 转办权限检测
		if (isAllowedTransfer($element, taskInstUuids) === false) {
			oAlert2("没有权限转办所选工作!");
			return;
		}

		// 判断是否转办必填意见
		if (isRequiredTransferOpinion($element, taskInstUuids)) {
			openToSignOpinion($element, taskInstUuids);
			return;
		}
		var opinionName = getOpinionName($element, taskInstUuids);
		var opinionValue = getOpinionValue($element, taskInstUuids);
		var opinionText = getOpinionText($element, taskInstUuids);
		$.unit.open({
			title : "选择转办人员",
			selectType : 4,
			afterSelect : function(laReturn) {
				if (StringUtils.isNotBlank(laReturn.id)) {
					var transferUserIds = laReturn.id.split(";");
					JDS.call({
						service : transferService,
						data : [ taskInstUuids, transferUserIds, opinionName, opinionValue, opinionText ],
						success : function(result) {
							oAlert("转办成功!", function() {
								refreshWindow($element);
							});
						},
						error : function(jqXHR) {
							oAlert2("转办失败!");
						}
					});
				} else {
					oAlert2("转办人员不能为空!");
				}
			}
		});
	};

	// 会签
	WorkFlow.counterSign = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		// 转办权限检测
		if (isAllowedCounterSign($element, taskInstUuids) === false) {
			oAlert2("没有权限会签所选工作!");
			return;
		}

		// 判断是否转办必填意见
		if (isRequiredCounterSignOpinion($element, taskInstUuids)) {
			openToSignOpinion($element, taskInstUuids);
			return;
		}
		var opinionName = getOpinionName($element, taskInstUuids);
		var opinionValue = getOpinionValue($element, taskInstUuids);
		var opinionText = getOpinionText($element, taskInstUuids);
		$.unit.open({
			title : "选择会签人员",
			selectType : 4,
			afterSelect : function(laReturn) {
				if (StringUtils.isNotBlank(laReturn.id)) {
					var counterSignUserIds = laReturn.id.split(";");
					JDS.call({
						service : counterSignService,
						data : [ taskInstUuids, counterSignUserIds, opinionName, opinionValue, opinionText ],
						success : function(result) {
							oAlert("会签成功!", function() {
								refreshWindow($element);
							});
						},
						error : function(jqXHR) {
							oAlert2("会签失败!");
						}
					});
				} else {
					oAlert2("会签人员不能为空!");
				}
			}
		});
	};

	// 抄送
	WorkFlow.copyTo = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		// 抄送处理
		$.unit.open({
			title : "选择抄送人员",
			selectType : 4,
			afterSelect : function(laReturn) {
				if (StringUtils.isNotBlank(laReturn.id)) {
					var copyToUserIds = laReturn.id.split(";");
					JDS.call({
						service : copyToService,
						data : [ taskInstUuids, copyToUserIds ],
						success : function(result) {
							oAlert("抄送成功!", function() {
								refreshWindow($element);
							});
						},
						error : function(jqXHR) {
							oAlert2("抄送失败!");
						}
					});
				}
			}
		});
	};

	// 套打
	// iframe加载内容事件处理
	var document = this;
	WorkFlow.print = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid, false);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}

		if ($("#print_form_iframe").length == 0) {
			$($element).append($(printScript));
		}

		// 加载工作信息
		var bean = loadTodoWorkData(taskInstUuids[0]);
		$("#print_form_iframe").load(function(e) {
			var doc = this.contentWindow.document;
			var msg = $("#print_response_msg", doc).val();
			var pts = eval(msg);
			doPrintMsgLoad.call(document, pts);
		});
		var $printForm = $("#print_form");
		$("input[name=filename]", $printForm).val(bean.title + ".doc");
		$("input[name=taskUuid]", $printForm).val(bean.taskUuid);
		$("input[name=formUuid]", $printForm).val(bean.formUuid);
		$("input[name=dataUuid]", $printForm).val(bean.dataUuid);
		$("input[name=printTemplateId]", $printForm).val(bean.printTemplateId);
		$printForm.attr("action", ctx + printUrl);
		$printForm[0].submit();
		// 重置打印表单
		$printForm[0].reset();
	};
	// 提示后台返回的打印信息
	function doPrintMsgLoad(pts) {
		// 重置打印表单
		var $printForm = $("#print_form");
		$printForm[0].reset();

		if (pts && pts.constructor == Array) {
			for (var i = 0; i < pts.length; i++) {
				var printTemplate = pts[i];
				var radio = "<div><input id='print_template_" + printTemplate.id
						+ "' name='selectPrintTemplateId' type='radio' value='" + printTemplate.id
						+ "'><label for='print_template_" + printTemplate.id + "'>" + printTemplate.name
						+ "</label></div>";
				$("#dlg_select_print_template").append(radio);
			}
			$("#dlg_select_print_template").dialog({
				autoOpen : true
			});
		} else if (typeof pts == 'object') {
			oAlert(pts.msg);
		} else {
			oAlert(pts);
		}
	}

	// 移交
	WorkFlow.handOver = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		// 移交权限检测
		if (isAllowedHandOver($element, taskInstUuids) === false) {
			oAlert2("工作已结束或已被处理或没有权限，无法进行操作!");
			return;
		}
		// 判断是否特送个人必填意见
		if (isRequiredHandOverOpinion($element, taskInstUuids)) {
			openToSignOpinion($element, taskInstUuids);
			return;
		}
		var opinionName = getOpinionName($element, taskInstUuids);
		var opinionValue = getOpinionValue($element, taskInstUuids);
		var opinionText = getOpinionText($element, taskInstUuids);
		// 移交处理
		$.unit.open({
			title : "选择移交人员",
			selectType : 4,
			afterSelect : function(laReturn) {
				if (StringUtils.isNotBlank(laReturn.id)) {
					var handOverUserIds = laReturn.id.split(";");
					JDS.call({
						service : handOverService,
						data : [ taskInstUuids, handOverUserIds, opinionName, opinionValue, opinionText ],
						success : function(result) {
							oAlert("移交成功!", function() {
								refreshWindow($element);
							});
						},
						error : function(jqXHR) {
							oAlert2("移交失败!");
						}
					});
				} else {
					oAlert2("请选择移交人员!");
				}
			}
		});
	};

	// 跳转
	WorkFlow.doGoto = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}

		// 跳转权限、相同环节检测
		if (taskInstUuids.length > 0) {
			var allowed = false;
			JDS.call({
				service : checkGotoTasksService,
				data : [ taskInstUuids ],
				async : false,
				success : function(result) {
					allowed = true;
				},
				error : function(jqXHR) {
					// 处理流程操作返回 的错误信息
					handlerError(jqXHR, $element, taskInstUuid);
				}
			});
			if (allowed != true) {
				return;
			}
		}
		
		// 判断是否特送环节必填意见
		if (isRequiredGotoTaskOpinion($element, taskInstUuids)) {
			openToSignOpinion($element, taskInstUuids);
			return;
		}
		var opinionName = getOpinionName($element, taskInstUuids);
		var opinionValue = getOpinionValue($element, taskInstUuids);
		var opinionText = getOpinionText($element, taskInstUuids);

		// 选择要跳转的环节
		var gotoTaskId = chooseGotoTaskId($element, taskInstUuids);
		if (gotoTaskId == null) {
			return;
		}

		// 跳转工作，按顺序递归调用跳转工作
		var size = taskInstUuids.length;
		var all = true;
		doGotoTask($element, taskInstUuids, 0, size, opinionName, opinionValue, opinionText, all, gotoTaskId);
	};
	function doGotoTask($element, taskInstUuids, index, size, opinionName, opinionValue, opinionText, all,
			gotoTaskId) {
		if (index >= size && all == true) {
			oAlert("跳转成功!", function() {
				refreshWindow($element);
			});
			return;
		} else if (index >= size) {
			oAlert2("工作跳转处理结束，但有错误!", function() {
				refreshWindow($element);
			});
			return;
		}
		var taskInstUuid = taskInstUuids[index];

		// 加载流程数据
		var workData = getTodoWorkData($element, taskInstUuid);
		if (workData != null) {
			// 办理意见
			workData.opinionLabel = opinionName;
			workData.opinionValue = opinionValue;
			workData.opinionText = opinionText;
			// 跳转环节
			workData.gotoTaskId = gotoTaskId;
			// 提交工作
			JDS.call({
				service : gotoTaskService,
				data : [ workData ],
				async : false,
				success : function(result) {
					// 按顺序递归调用跳转工作
					doGotoTask($element, taskInstUuids, index + 1, size, opinionName, opinionValue,
							opinionText, all, gotoTaskId);
				},
				error : function(jqXHR) {
					var msg = JSON.parse(jqXHR.responseText);
					var callback = {};
					if (WorkFlowErrorCode.WorkFlowException === msg.errorCode
							|| WorkFlowErrorCode.IdentityNotFlowPermission === msg.errorCode) {
						callback = function() {
							// 按顺序递归调用跳转工作
							doGotoTask($element, taskInstUuids, index + 1, size, opinionName, opinionValue,
									opinionText, all, gotoTaskId);
						};
					} else {
						callback = function() {
							// 按顺序递归调用跳转工作
							doGotoTask($element, taskInstUuids, index, size, opinionName, opinionValue,
									opinionText, all, gotoTaskId);
						};
					}
					handlerException(msg, $element, taskInstUuid, workData, callback);
				}
			});
		} else {
			// 按顺序递归调用跳转工作
			doGotoTask($element, taskInstUuids, index + 1, size, opinionName, opinionValue, opinionText, all,
					gotoTaskId);
		}
	}
	/** *************************** 批量操作开始，无需进行复杂交互 *************************** */
	// 删除草搞
	WorkFlow.deleteDraft = function(flowInstUuid) {
		var $element = $(this);
		var flowInstUuids = getSelectedRowIds($element, flowInstUuid);
		if (!flowInstUuids) {
			return;
		}
		oConfirm("确定删除工作草稿?", function() {
			JDS.call({
				service : deleteDraftService,
				data : [ flowInstUuids ],
				success : function(result) {
					oAlert("删除成功!", function() {
						refreshWindow($element);
					});
				}
			});
		});
	};

	// 删除工作
	WorkFlow.deleteTask = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		JDS.call({
			service : deleteService,
			data : [ taskInstUuids ],
			success : function(result) {
				oAlert("删除成功!", function() {
					refreshWindow($element);
				});
			},
			error : function(jqXHR) {
				handlerError(jqXHR, $element);
			}
		});
	};

	// 管理员删除工作
	WorkFlow.deleteTaskByAdmin = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		JDS.call({
			service : deleteByAdminService,
			data : [ taskInstUuids ],
			success : function(result) {
				oAlert("删除成功!", function() {
					refreshWindow($element);
				});
			},
			error : function(jqXHR) {
				handlerError(jqXHR, $element);
			}
		});
	};

	// 退回
	WorkFlow.rollback = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		// 退回权限检测
		if (isAllowedRollback($element, taskInstUuids) === false) {
			oAlert2("没有权限退回所选工作!");
			return;
		}

		// 获取多个流程实例处理相同的流程定义（包含不同版本）及相同的环节，可退回的环节取各个流程实例可退回环节的交集
		var toRollbackTasksMap = checkAndGetToRollbackTasks($element, taskInstUuids);
		if (toRollbackTasksMap.allowed != true) {
			return;
		}
		if (toRollbackTasksMap[taskInstUuids[0]].length == 0) {
			oAlert2("所选工作没有相同的可退回环节!");
			return;
		}

		// 判断是否退回必填意见
		if (isRequiredRollbackOpinion($element, taskInstUuids)) {
			openToSignOpinion($element, taskInstUuids);
			return;
		}
		var opinionName = getOpinionName($element, taskInstUuids);
		var opinionValue = getOpinionValue($element, taskInstUuids);
		var opinionText = getOpinionText($element, taskInstUuids);

		// 选择要退回的环节
		var rollBackTaskId = chooseRollBackTaskId($element, taskInstUuids, taskInstUuids[0],
				toRollbackTasksMap[taskInstUuids[0]]);

		// 多个流程实例退回到同一环节，则能退回的流程就退回，不能退回的不退回
		if (rollBackTaskId != null) {
			var hasError = false;
			for (var index = 0; index < taskInstUuids.length; index++) {
				// 获取退回到的环节对象
				var rollBackTask = {};
				var rollBackTasks = toRollbackTasksMap[taskInstUuids[index]];
				for (var j = 0; j < rollBackTasks.length; j++) {
					if (rollBackTaskId == rollBackTasks[j]["id"]) {
						rollBackTask = rollBackTasks[j];
						break;
					}
				}
				if (rollBackTask != null) {
					var rollbackToTaskId = rollBackTask["id"];
					var rollbackToTaskInstUuid = rollBackTask["taskInstUuid"];
					JDS.call({
						service : rollbackService,
						async : false,
						data : [ taskInstUuids[index], rollbackToTaskId, rollbackToTaskInstUuid, opinionName,
								opinionValue, opinionText ],
						success : function(result) {
						},
						error : function(jqXHR) {
							hasError = true;
						}
					});
				}
			}
			// 退回操作后提示
			if (hasError == true) {
				oAlert2("退回有错!", function() {
					refreshWindow($element);
				});
			} else {
				oAlert("退回成功!", function() {
					refreshWindow($element);
				});
			}
		}
	};

	// 直接退回
	WorkFlow.directRollback = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		JDS.call({
			service : directRollbackService,
			data : [ taskInstUuids ],
			success : function(result) {
				oAlert("直接退回成功!", function() {
					refreshWindow($element);
				});
			},
			error : function(jqXHR) {
				handlerError(jqXHR, $element);
			}
		});
	};

	// 撤回
	WorkFlow.cancel = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		// 撤回权限检测
		if (isAllowedCancel($element, taskInstUuids) === false) {
			oAlert2("没有权限或不能撤回所选工作!");
			return;
		}
		JDS.call({
			service : cancelService,
			data : [ taskInstUuids ],
			success : function(result) {
				oAlert("撤回成功！", function() {
					refreshWindow($element);
				});
			},
			error : function(jqXHR) {
				handlerError(jqXHR, $element);
			}
		});
	};

	// 撤回已结束流程
	WorkFlow.cancelOver = function(flowInstUuid) {
		var $element = $(this);
		var flowInstUuids = getSelectedRowIds($element, flowInstUuid);
		if (!flowInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		JDS.call({
			service : cancelOverService,
			data : [ flowInstUuids ],
			success : function(result) {
				oAlert("撤回成功！", function() {
					refreshWindow($element);
				});
			},
			error : function(jqXHR) {
				handlerError(jqXHR, $element);
			}
		});
	};

	// 关注
	WorkFlow.attention = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		JDS.call({
			service : attentionService,
			data : [ taskInstUuids ],
			success : function(result) {
				oAlert("关注成功!", function() {
					refreshWindow($element);
				});
			},
			error : function(jqXHR) {
				oAlert2("关注失败!");
			}
		});
	};

	// 取消关注
	WorkFlow.unfollow = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		JDS.call({
			service : unfollowService,
			data : [ taskInstUuids ],
			success : function(result) {
				oAlert("取消关注成功!", function() {
					refreshWindow($element);
				});
			},
			error : function(jqXHR) {
				oAlert2("取消关注失败!");
			}
		});
	};

	// 工作标记已阅
	WorkFlow.markRead = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		JDS.call({
			service : markReadService,
			data : [ taskInstUuids ],
			success : function(result) {
				oAlert("标记已阅成功!", function() {
					refreshWindow($element);
				});
			},
			error : function(jqXHR) {
				oAlert2("标记已阅失败!");
			}
		});
	};

	// 工作标记未阅
	WorkFlow.markUnread = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		JDS.call({
			service : markUnreadService,
			data : [ taskInstUuids ],
			success : function(result) {
				oAlert("标记未阅成功!", function() {
					refreshWindow($element);
				});
			},
			error : function(jqXHR) {
				oAlert2("标记未阅失败!");
			}
		});
	};

	// 签署意见
	WorkFlow.signOpinion = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid, false);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		// 弹出签署意见弹出框
		$.workflowOpinion.open({
			ok : function(retVal) {
				var text = retVal.text;
				var value = retVal.value;
				JDS.call({
					service : signOpinionService,
					data : [ taskInstUuids, text, value ],
					success : function(result) {
						oAlert("意见签署成功!", function() {
							refreshWindow($element);
						});
					},
					error : function(jqXHR) {
						oAlert2("意见签署失败!");
					}
				});
			},
			cancel : function() {
			}
		});
	};

	// 催办
	WorkFlow.remind = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		// 催办必填意见
		var opinionName = getOpinionName($element, taskInstUuids);
		var opinionValue = getOpinionValue($element, taskInstUuids);
		var opinionText = getOpinionText($element, taskInstUuids);
		if (StringUtils.isBlank(opinionText)) {
			openToSignOpinion($element, taskInstUuids);
			return;
		}

		JDS.call({
			service : remindService,
			data : [ taskInstUuids, opinionName, opinionValue, opinionText ],
			success : function(result) {
				oAlert("催办成功!", function() {
					refreshWindow($element);
				});
			},
			error : function(jqXHR) {
				oAlert2("催办失败!");
			}
		});
	};

	// 打开工作
	WorkFlow.openWork = function() {
		var workInfo = new Object();
		workInfo = eval("(" + urldecode($(this).attr("jsonstr") + ")"));
		var taskInstUuid = workInfo["taskInstanceUuid"];
		if (taskInstUuid === "94e6bc82-7741-4047-b84b-a71c448a3f5d") {
			// 单点登录
			$.ajax({
				type : "GET",
				url : ctx + "/security/switch/tenant/user",
				async : false,
				data : {
					toTenant : "T003"
				},
				success : function(success, statusText, jqXHR) {
					window.location.reload();
				},
				error : function(jqXHR, statusText, error) {
				}
			});
		}
	};

	/**
	 * 
	 */
	WorkFlow.genTime = function(taskInstUuid) {
		var $element = $(this);
		var taskInstUuids = getSelectedRowIds($element, taskInstUuid, true);
		if (!taskInstUuids) {
			return;
		}
		
		// 挂职流程数据判断
		if(isGzWorkData($element, taskInstUuids)) {
			return;
		}
		
		JDS.call({
			service : genTimeService,
			data : [ taskInstUuids ],
			success : function(result) {
				if ("y" == result.data) {
					var url = ctx + "/workflow/work/genTime?taskInstUuids=" + taskInstUuids[0];
					for (var i = 1; i < taskInstUuids.length; i++) {
						url = url + "&taskInstUuids=" + taskInstUuids[i];
					}
					window.open(url);
				} else {
					oAlert2("请选择同一类型流程!");
				}

			},
			error : function(jqXHR) {
				handlerError(jqXHR, $element);
			}
		});
	};

	/** *************************** 批量操作结束，无需进行复杂交互 *************************** */

	// 获取选择的流程环节实例UUID
	function getSelectedRowIds($element, rowId, multiple) {
		var rowIds = [];
		if (isTopBtn($element)) {
			$("input[class=checkeds]:checked").each(function() {
				rowIds.push($(this).val());
			});
			if (rowIds.length == 0) {
				oAlert2("请选择记录!");
				return;
			}
		} else {
			rowIds.push(rowId);
		}
		if (multiple === false && rowIds.length > 1) {
			oAlert2("只能选择一条记录!");
			return;
		}
		return rowIds;
	}
	function openToSignOpinion($element, taskInstUuids) {
		var key = taskInstUuids.join("_") + "_" + "opinionText";
		// 弹出签署意见弹出框
		$.workflowOpinion.open({
			ok : function(retVal) {
				var text = retVal.text;
				$element.data(key, text);

				$element.trigger("click");
			},
			cancel : function() {
			}
		});
	}
	function getTodoWorkData($element, taskInstUuid) {
		var key = taskInstUuid + "_" + "getWorkData";
		var bean = $element.data(key);
		if (bean == null) {
			JDS.call({
				service : "listWorkService.getTodoWorkData",
				data : [ taskInstUuid ],
				async : false,
				success : function(result) {
					bean = result.data;
					$element.data(key, bean);
				},
				error : function(jqXHR) {
					oAlert("获取流程数据失败!");
					bean = null;
				}
			});
		}
		return bean;
	}
	// 挂职流程数据判断
	function isGzWorkData($element, taskInstUuids) {
		var isGz = false;
		JDS.call({
			service : isGzWorkDataService,
			data : [ taskInstUuids ],
			async : false,
			success : function(result) {
			},
			error : function(jqXHR) {
				handlerError(jqXHR, $element);
				isGz = true;
			}
		});
		return isGz;
	}
	function getOpinionName($element, taskInstUuids) {
		var key = taskInstUuids.join("_") + "_" + "opinionName";
		var opinionName = $element.data(key);
		return StringUtils.isNotBlank(opinionName) ? opinionName : "";
	}
	function getOpinionValue($element, taskInstUuids) {
		var key = taskInstUuids.join("_") + "_" + "opinionValue";
		var opinionValue = $element.data(key);
		return StringUtils.isNotBlank(opinionValue) ? opinionValue : "";
	}
	function getOpinionText($element, taskInstUuids) {
		var key = taskInstUuids.join("_") + "_" + "opinionText";
		var opinionText = $element.data(key);
		return StringUtils.isNotBlank(opinionText) ? opinionText : "";
	}
	function isAllowedSubmit($element, taskInstUuids) {
		return checkPermission($element, taskInstUuids, isAllowedSubmitService, "isAllowedSubmit");
	}
	// 判断是否提交必填意见
	function isRequiredSignOpinion($element, taskInstUuids) {
		if (StringUtils.isNotBlank(getOpinionText($element, taskInstUuids))) {
			return false;
		}
		return checkPermission($element, taskInstUuids, isRequiredSignOpinionService, "isRequiredSignOpinion");
	}
	// 判断是否特送环节必填意见
	function isRequiredGotoTaskOpinion($element, taskInstUuids) {
		if (StringUtils.isNotBlank(getOpinionText($element, taskInstUuids))) {
			return false;
		}
		return checkPermission($element, taskInstUuids, isRequiredGotoTaskOpinionService,
				"isRequiredGotoTaskOpinion");
	}
	function chooseGotoTaskId($element, taskInstUuids) {
		var key = taskInstUuids.join("_") + "_" + "chooseGotoTaskId";
		var gotoTaskId = $element.data(key);
		if (gotoTaskId == null) {
			if ($(dlg_select_goto_task_selector, $element).length == 0) {
				$element.append($("<div id='dlg_select_goto_task'></div>"));
			}
			// 获取要跳转的环节
			var toTasks = [];
			JDS.call({
				service : getGotoTaskService,
				data : [ taskInstUuids[0] ],
				async : false,
				success : function(result) {
					var data = result.data;
					toTasks = data.toTasks;
				},
				error : function(jqXHR) {
					// 处理流程操作返回 的错误信息
					oAlert2("操作失败!");
				}
			});

			for (var i = 0; i < toTasks.length; i++) {
				var task = toTasks[i];
				var radio = "<div><label class='radio inline'><input id='" + task.id
						+ "' name='toTaskId' type='radio' value='" + task.id + "'>" + task.name
						+ "</label></div>";
				$(dlg_select_goto_task_selector).append(radio);
			}
			$(dlg_select_goto_task_selector).oDialog({
				title : "选择特送环节",
				autoOpen : true,
				height : 300,
				width : 350,
				resizable : false,
				modal : true,
				buttons : {
					"确定" : function(e) {
						var $checkbox = $("input[name=toTaskId]:checked");
						if ($checkbox.length == 0) {
							oAlert2("请选择跳转环节!");
							return;
						}
						var gotoTaskId = $checkbox.val();
						$element.data(key, gotoTaskId);
						$element.trigger("click");
						$(dlg_select_goto_task_selector).oDialog("close");
					},
					"取消" : function(e) {
						$(dlg_select_goto_task_selector).oDialog("close");
					}
				},
				close : function() {
					$(dlg_select_goto_task_selector).oDialog("destroy");
					$(dlg_select_goto_task_selector).remove();
				}
			});
		} else {
			return gotoTaskId;
		}
	}
	function isAllowedHandOver($element, taskInstUuids) {
		return checkPermission($element, taskInstUuids, isAllowedHandOverService, "isAllowedHandOver");
	}
	// 判断是否特送个人必填意见
	function isRequiredHandOverOpinion($element, taskInstUuids) {
		if (StringUtils.isNotBlank(getOpinionText($element, taskInstUuids))) {
			return false;
		}
		return checkPermission($element, taskInstUuids, isRequiredHandOverOpinionService,
				"isRequiredHandOverOpinion");
	}
	function isAllowedTransfer($element, taskInstUuids) {
		return checkPermission($element, taskInstUuids, isAllowedTransferService, "isAllowedTransfer");
	}
	// 判断是否转办必填意见
	function isRequiredTransferOpinion($element, taskInstUuids) {
		if (StringUtils.isNotBlank(getOpinionText($element, taskInstUuids))) {
			return false;
		}
		return checkPermission($element, taskInstUuids, isRequiredTransferOpinionService,
				"isRequiredTransferOpinion");
	}
	function isAllowedCounterSign($element, taskInstUuids) {
		return checkPermission($element, taskInstUuids, isAllowedCounterSignService, "isAllowedCounterSign");
	}
	// 判断是否会签必填意见
	function isRequiredCounterSignOpinion($element, taskInstUuids) {
		if (StringUtils.isNotBlank(getOpinionText($element, taskInstUuids))) {
			return false;
		}
		return checkPermission($element, taskInstUuids, isRequiredCounterSignOpinionService,
				"isRequiredCounterSignOpinion");
	}
	function isAllowedRollback($element, taskInstUuids) {
		return checkPermission($element, taskInstUuids, isAllowedRollbackService, "isAllowedRollback");
	}
	function checkAndGetToRollbackTasks($element, taskInstUuids) {
		var key = taskInstUuids.join("_") + "_" + "checkAndGetToRollbackTasks";
		var toRollbackTasksMap = $element.data(key);
		if (!toRollbackTasksMap) {
			toRollbackTasksMap = {};
		} else {
			return toRollbackTasksMap;
		}
		JDS.call({
			service : checkAndGetToRollbackTasksService,
			data : [ taskInstUuids ],
			async : false,
			success : function(result) {
				toRollbackTasksMap = result.data;
				toRollbackTasksMap.allowed = true;
			},
			error : function(jqXHR) {
				// 处理流程操作返回 的错误信息
				handlerError(jqXHR, $element, taskInstUuids);
				toRollbackTasksMap.allowed = false;
			}
		});
		$element.data(key, toRollbackTasksMap);
		return toRollbackTasksMap;
	}
	function chooseRollBackTaskId($element, taskInstUuids, taskInstUuid, rollBackTasks) {
		var key = taskInstUuids.join("_") + "_" + "chooseRollBackTask";
		var rollBackTaskId = $element.data(key);
		if (rollBackTaskId == null) {
			if ($(dlg_select_rollback_task_selector, $element).length == 0) {
				$element.append($("<div id='dlg_select_rollback_task'></div>"));
			}
			for (var i = 0; i < rollBackTasks.length; i++) {
				var task = rollBackTasks[i];
				var radio = "<div><label class='radio inline'><input id='" + task.id
						+ "' name='toTaskId' type='radio' value='" + task.id + "'>" + task.name
						+ "</label></div>";
				$(dlg_select_rollback_task_selector).append(radio);
			}
			$(dlg_select_rollback_task_selector).oDialog({
				title : "选择退回环节",
				autoOpen : true,
				width : 350,
				height : 300,
				buttons : {
					"确定" : function(e) {
						var $checkbox = $("input[name=toTaskId]:checked");
						if ($checkbox.length == 0) {
							oAlert2("请选择退回环节!");
							return;
						}
						var taskId = $checkbox.val();
						$element.data(key, taskId);
						$element.trigger("click");
						$(dlg_select_rollback_task_selector).oDialog("close");
					},
					"取消" : function(e) {
						$(dlg_select_rollback_task_selector).oDialog("close");
					}
				},
				close : function() {
					$(dlg_select_rollback_task_selector).oDialog("destroy");
					$(dlg_select_rollback_task_selector).remove();
				}
			});
		} else {
			return rollBackTaskId;
		}
	}
	// 判断是否退回必填意见
	function isRequiredRollbackOpinion($element, taskInstUuids) {
		if (StringUtils.isNotBlank(getOpinionText($element, taskInstUuids))) {
			return false;
		}
		return checkPermission($element, taskInstUuids, isRequiredRollbackOpinionService,
				"isRequiredRollbackOpinion");
	}
	function isAllowedCancel($element, taskInstUuids) {
		return checkPermission($element, taskInstUuids, isAllowedCancelService, "isAllowedCancel");
	}
	function checkPermission($element, taskInstUuids, service, suffix) {
		var key = taskInstUuids.join("_") + "_" + suffix;
		var isAllowedTransfer = $element.data(key);
		if (isAllowedTransfer === true || isAllowedTransfer === false) {
			return isAllowedTransfer;
		} else {
			isAllowedTransfer = false;
		}
		JDS.call({
			service : service,
			data : [ taskInstUuids ],
			async : false,
			success : function(result) {
				isAllowedTransfer = result.data;
				$element.data(key, isAllowedTransfer);
			},
			error : function(jqXHR) {
				isAllowedTransfer = false;
			}
		});
		return isAllowedTransfer;
	}
});
