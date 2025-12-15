var todoButtons = {
	"B004002" : "B004002",
	"B004003" : "B004003",
	"B004004" : "B004004",
	"B004006" : "B004006",
	"B004007" : "B004007",
	"B004010" : "B004010",
	"B004011" : "B004011"
};
// 提交
var btn_submit = "B004002";
var submitService = "workService.submit";
// 退回
var btn_rollback = "B004003";
// 直接退回
var btn_direct_rollback = "B004004";
var rollbackWithWorkDataService = "workService.rollbackWithWorkData";
// 转办
var btn_transfer = "B004006";
var transferWithWorkDataService = "workService.transferWithWorkData";
// 会签
var btn_counterSign = "B004007";
var counterSignWithWorkDataService = "workService.counterSignWithWorkData";
// 抄送
var btn_copyTo = "B004010";
// 签署意见
var btn_sign_opinion = "B004011";
// 必须签署意见
var btn_required_sign_opinion = "B004026";
// 转办必填意见
var btn_required_transfer_opinion = "B004029";
// 会签必填意见
var btn_required_counterSign_opinion = "B004030";
// 退回必填意见
var btn_required_rollback_opinion = "B004031";
// 是否需要签署意见
var requiredSignOpinion = false;
// 是否转办必填意见
var requiredTransferOpinion = false;
// 是否会签必填意见
var requiredCounterSignOpinion = false;
// 是否退回必填意见
var requiredRollbackOpinion = false;
function workTodoDetail(obj) {
	var uuid = $(obj).attr("uuid");
	var workData = {};
	workData.taskInstUuid = uuid;
	JDS.call({
		service : "mobileWorkService.getTodoWorkData",
		data : [ uuid, null ],
		success : function(result) {
			workData = result.data;
			$("#workTodoDetail").data("workData" + "_" + uuid, result.data);
			getTodoDyFormData();
		}
	});
	function getTodoDyFormData() {
		$.ui.showMask("数据加载中...");
		$.ajax({
			url : ctx + "/dyformdata/getMobileHtml",
			data : {
				"formUuid" : workData.formUuid,
				"dataUuid" : workData.dataUuid
			},
			contentType : 'application/json',
			success : function(result) {
				$("#work_todo_dyform").html(result);
				$.ui.loadContent("#workTodoDetail", false, false, $(obj).attr("title"));
				$.ui.setTitle(workData.title);

				// 操作按钮
				var allowOperate = false;
				if (workData.buttons) {
					var moreButtons = [];
					for ( var i = 0; i < workData.buttons.length; i++) {
						var btn = workData.buttons[i];
						// 签署意见
						if (btn.id == btn_sign_opinion) {
							$("#work_todo_sign_opinion").unbind("click");
							$("#work_todo_sign_opinion").bind("click", function() {
								openToSignOpinion(workData, onSubmit);
							});
						}
						if (btn.id == btn_required_sign_opinion) {
							requiredSignOpinion = true;
						}
						if (btn.id == btn_required_transfer_opinion) {
							requiredTransferOpinion = true;
						}
						if (btn.id == btn_required_counterSign_opinion) {
							requiredCounterSignOpinion = true;
						}
						if (btn.id == btn_required_rollback_opinion) {
							requiredRollbackOpinion = true;
						}
						// 提交
						if (btn.id == btn_submit) {
							$("#work_todo_submit").unbind("click");
							$("#work_todo_submit").bind("click", function() {
								onSubmit.call(this, workData);
							});
						}
						// 更多操作
						if (todoButtons[btn.id] && btn.id != btn_submit && btn.id != btn_sign_opinion
								&& btn.id != btn_required_sign_opinion
								&& btn.id != btn_required_transfer_opinion
								&& btn.id != btn_required_counterSign_opinion
								&& btn.id != btn_required_rollback_opinion) {
							moreButtons.push(btn);
						}
					}
					if (moreButtons.length > 0) {
						$("#work_todo_more").unbind("click");
						$("#work_todo_more").bind("click", function() {
							var actions = [];
							$(moreButtons).each(function() {
								var $this = this;
								var action = {
									text : this.name,
									cssClasses : 'blue',
									handler : function(e) {
										var btnId = $this.id;
										if (btnId == btn_rollback) {
											// 退回
											onRollback.call(this, workData);
										}
										if (btnId == btn_direct_rollback) {
											// 直接退回
											onDirectRollback.call(this, workData);
										}
										if (btnId == btn_transfer) {
											// 转办
											onTransfer.call(this, workData);
										}
										if (btnId == btn_counterSign) {
											// 会签
											onCounterSign.call(this, workData);
										}
										if (btnId == btn_copyTo) {
											// 抄送
											onCopyTo.call(this, workData);
										}
									}
								};
								actions.push(action);
							});
							$("#afui").actionsheet(actions);
							$("#af_actionsheet").find(".cancel").text("取消");
						});
					} else {
						$("#work_todo_more").hide();
					}
				}
				if (allowOperate) {
					$.ui.toggleNavMenu();
				}
				$.ui.hideMask();
			},
			error : function(data) {
				$.ui.hideMask();
				// $.ui.popup("请先配置表单的手机设置！");
				$.ui.popup('请先配置表单的手机设置！');
				// $("#workTodoDetail").html(data);
			}
		});
	}
}
function openToSignOpinion(workData, submitHandler) {
	$.workflowOpinion.open({
		opinionText : workData.opinionText,
		submitHandler : submitHandler,
		workData : workData,
		afterSelect : function(opinionText) {
			workData.opinionText = opinionText;
		}
	});
}

var doneButtons = {
	"B004005" : "B004005",
	"B004010" : "B004010"
};
function workDoneDetail(obj) {
	var uuid = $(obj).attr("uuid");
	var workData = {};
	workData.taskInstUuid = uuid;
	JDS.call({
		service : "mobileWorkService.getDoneWorkData",
		data : [ uuid, null ],
		success : function(result) {
			workData = result.data;
			$("#workDoneDetail").data("workData" + "_" + uuid, result.data);
			getDoneDyFormData();
		}
	});
	function getDoneDyFormData() {
		$.ui.showMask("数据加载中...");
		$.ajax({
			url : ctx + "/dyformdata/getMobileHtml",
			data : {
				"formUuid" : workData.formUuid,
				"dataUuid" : workData.dataUuid
			},
			contentType : 'application/json',
			success : function(result) {
				$("#work_done_dyform").html(result);
				$.ui.loadContent("#workDoneDetail", false, false, $(obj).attr("title"));
				$.ui.setTitle(workData.title);

				// 操作按钮
				var allowOperate = false;
				if (workData.buttons) {
					$("#work_done_footer").html("");
					for ( var i = 0; i < workData.buttons.length; i++) {
						var btn = workData.buttons[i];
						if (doneButtons[btn.id]) {
							allowOperate = true;
							var btnElm = $("<a href='#aftransitions' id='" + btn.id + "' class='icon stack'>"
									+ btn.name + "</a>");
							$(btnElm).data("taskInstUuid", workData.taskInstUuid);
							$("#work_done_footer").append(btnElm);
						}
					}
					var penc = 100 / $("#work_done_footer").find("a").length;
					$("#work_done_footer").find("a").each(function() {
						$(this).css("width", penc + "%");
					});
				}
				if (!allowOperate) {
					$.ui.toggleNavMenu();
				}
				bindBtnEvent(doneButtons, workData);
				$.ui.hideMask();
			},
			error : function(data) {
				$.ui.hideMask();
				$.ui.popup("请先配置表单的手机设置！");
				// $("#workDoneDetail").html(data);
			}
		});
	}
}

// 撤回
var btn_cancel = "B004005";
var cancelService = "workService.cancel";
// 抄送
var btn_copyTo = "B004010";
var copyToService = "workService.copyTo";
function bindBtnEvent(buttons, workData) {
	for ( var key in buttons) {
		if (key == btn_cancel) {
			// 撤回
			$("#" + key).each(function() {
				$(this).click($.proxy(onCancel, this, workData));
			});
		}
		if (key == btn_copyTo) {
			// 抄送
			$("#" + key).each(function() {
				$(this).click($.proxy(onCopyTo, this, workData));
			});
		}
	}
}
// 流程错误代码
var WorkFlowErrorCode = {
	WorkFlowException : "WorkFlowException", // 工作流异常
	TaskNotAssignedUser : "TaskNotAssignedUser", // 任务没有指定参与者
	TaskNotAssignedCopyUser : "TaskNotAssignedCopyUser", // 任务没有指定抄送人
	TaskNotAssignedMonitor : "TaskNotAssignedMonitor", // 任务没有指定督办人
	ChooseSpecificUser : "ChooseSpecificUser", // 选择具体办理人
	OnlyChooseOneUser : "OnlyChooseOneUser", // 只能选择一个办理人
	JudgmentBranchFlowNotFound : "JudgmentBranchFlowNotFound", // 无法找到可用的判断分支流向
	MultiJudgmentBranch : "MultiJudgmentBranch", // 找到多个判断分支流向
	SubFlowNotFound : "SubFlowNotFound", // 没有指定子流程
	SubFlowMerge : "SubFlowMerge", // 子流程合并等待
	IdentityNotFlowPermission : "IdentityNotFlowPermission", // 用户没有权限访问流程
	RollbackTaskNotFound : "RollbackTaskNotFound", // 找不到退回操作的退回环节异常类
	SaveData : "SaveData" // 表单数据保存失败
};
// 处理流程操作返回 的错误信息
function handlerError(jqXHR, $btn, workData) {
	var msg = JSON.parse(jqXHR.responseText);
	if (WorkFlowErrorCode.WorkFlowException === msg.errorCode) {
		// $.ui.popup(msg.data.msg);
		$.ui.popup("系统内部错误，请稍后再试");
	} else if (WorkFlowErrorCode.TaskNotAssignedUser === msg.errorCode) {
		TaskNotAssignedUser(msg.data, $btn, workData);
	} else if (WorkFlowErrorCode.TaskNotAssignedCopyUser === msg.errorCode) {
		TaskNotAssignedCopyUser(msg.data, $btn, workData);
	} else if (WorkFlowErrorCode.TaskNotAssignedMonitor === msg.errorCode) {
		TaskNotAssignedMonitor(msg.data, $btn, workData);
	} else if (WorkFlowErrorCode.ChooseSpecificUser === msg.errorCode) {
		ChooseSpecificUser(msg.data, $btn, workData);
	} else if (WorkFlowErrorCode.OnlyChooseOneUser === msg.errorCode) {
		OnlyChooseOneUser(msg.data, $btn, workData);
	} else if (WorkFlowErrorCode.JudgmentBranchFlowNotFound === msg.errorCode) {
		JudgmentBranchFlowNotFound(msg.data, $btn, workData);
	} else if (WorkFlowErrorCode.MultiJudgmentBranch === msg.errorCode) {
		MultiJudgmentBranch(msg.data, $btn, workData);
	} else if (WorkFlowErrorCode.SubFlowNotFound === msg.errorCode) {
		SubFlowNotFound(msg.data);
	} else if (WorkFlowErrorCode.SubFlowMerge === msg.errorCode) {
		SubFlowMerge(msg.data);
	} else if (WorkFlowErrorCode.IdentityNotFlowPermission === msg.errorCode) {
		IdentityNotFlowPermission(msg.data);
	} else if (WorkFlowErrorCode.RollbackTaskNotFound === msg.errorCode) {
		RollbackTaskNotFound(msg.data, $btn, workData);
	} else if (WorkFlowErrorCode.SaveData === msg.errorCode) {
		$.ui.popup("表单数据保存失败！");
	} else {
		// $.ui.popup("工作流无法处理的未知异常：" + jqXHR.responseText);
		$.ui.popup("工作流无法处理的未知异常");
	}
}
function TaskNotAssignedUser(eData, $btn, workData) {
	selectUsers(eData, $btn, workData, []);
}
function TaskNotAssignedCopyUser(eData, $btn, workData) {
	$.mobileUnit.open({
		title : "选择人员",
		initUserIds : initUserIds,
		checkedType : "radio",
		afterSelect : function(laReturn) {
			var taskCopyUsers = {};
			var taskId = eData.taskId;
			if (laReturn.id != null && laReturn.id != "") {
				// 在原来的环节办理人上增加环节办理人
				taskCopyUsers = workData.taskCopyUsers;
				var userIds = laReturn.id.split(";");
				taskCopyUsers[taskId] = userIds;
			} else {
				taskCopyUsers[taskId] = null;
				workData.taskCopyUsers = taskCopyUsers;
			}
			// 重新触发提交事件
			if (eData.submitButtonId != null && eData.submitButtonId != "") {
				$("#" + eData.submitButtonId).trigger('click');
			} else if ($btn) {
				$btn.trigger('click');
			} else {
				// $("#" + btn_submit).trigger('click');
				$.ui.popup("请重新提交!");
			}
		}
	});
}
function TaskNotAssignedMonitor(eData, $btn, workData) {
	$.mobileUnit.open({
		title : "选择人员",
		initUserIds : initUserIds,
		checkedType : "radio",
		afterSelect : function(laReturn) {
			var taskMonitors = {};
			var taskId = eData.taskId;
			if (laReturn.id != null && laReturn.id != "") {
				// 在原来的环节办理人上增加环节办理人
				taskMonitors = workData.taskMonitors;
				var userIds = laReturn.id.split(";");
				taskMonitors[taskId] = userIds;
			} else {
				taskMonitors[taskId] = null;
				workData.taskMonitors = taskMonitors;
			}
			// 重新触发提交事件
			if (eData.submitButtonId != null && eData.submitButtonId != "") {
				$("#" + eData.submitButtonId).trigger('click');
			} else if ($btn) {
				$btn.trigger('click');
			} else {
				// $("#" + btn_submit).trigger('click');
				$.ui.popup("请重新提交!");
			}
		}
	});
}
function ChooseSpecificUser(eData, $btn, workData) {
	var userIds = eData.userIds;
	selectUsers(eData, $btn, workData, userIds);
}
function OnlyChooseOneUser(eData, $btn, workData) {
	var userIds = eData.userIds;
	selectUsers(eData, $btn, workData, userIds);
}
function selectUsers(eData, $btn, workData, initUserIds) {
	$.mobileUnit.open({
		title : "选择人员",
		initUserIds : initUserIds,
		checkedType : "radio",
		afterSelect : function(laReturn) {
			var taskUsers = {};
			var taskId = eData.taskId;
			if (laReturn.id != null && laReturn.id != "") {
				// 在原来的环节办理人上增加环节办理人
				taskUsers = workData.taskUsers;
				var userIds = laReturn.id.split(";");
				taskUsers[taskId] = userIds;
			} else {
				taskUsers[taskId] = null;
				workData.taskUsers = taskUsers;
			}
			// 重新触发提交事件
			if (eData.submitButtonId != null && eData.submitButtonId != "") {
				$("#" + eData.submitButtonId).trigger('click');
			} else if ($btn) {
				$btn.trigger('click');
			} else {
				// $("#" + btn_submit).trigger('click');
				$.ui.popup("请重新提交!");
			}
		}
	});
}
function JudgmentBranchFlowNotFound(eData, $btn, workData) {
	var toTasks = eData.toTasks;
	$.taskChosen.open({
		tasks : toTasks,
		afterSelect : function(taskId) {
			workData.toTaskId = taskId;
			// 重新触发提交事件
			if (eData.submitButtonId != null && eData.submitButtonId != "") {
				$("#" + eData.submitButtonId).trigger('click');
			} else if ($btn) {
				$btn.trigger('click');
			} else {
				// $("#" + btn_submit).trigger('click');
				$.ui.popup("请重新提交!");
			}
		}
	});
}
function MultiJudgmentBranch(eData, $btn, workData) {
	JudgmentBranchFlowNotFound(eData, $btn, workData);
}
function RollbackTaskNotFound(eData, $btn, workData) {
	var toTasks = eData.rollbackTasks;
	$.taskChosen.open({
		tasks : toTasks,
		afterSelect : function(taskId) {
			workData.rollbackToTaskId = taskId;
			for ( var i = 0; i < toTasks.length; i++) {
				var toTask = toTasks[i];
				if (toTask.id = taskId) {
					workData.rollbackToTaskInstUuid = toTask.taskInstUuid;
					break;
				}
			}
			// 重新触发提交事件
			if (eData.submitButtonId != null && eData.submitButtonId != "") {
				$("#" + eData.submitButtonId).trigger('click');
			} else if ($btn) {
				$btn.trigger('click');
			} else {
				// $("#" + btn_submit).trigger('click');
				$.ui.popup("请重新提交!");
			}
		}
	});
}
/** ********************************* 提交开始 ********************************* */
// 提交处理
function onSubmit(workData) {
	if (isRequiredSignOpinion(workData)) {
		openToSignOpinion(workData, onSubmit);
	} else {
		var $btn = $(this);
		// 操作动作及类型
		workData.action = "提交";
		workData.actionType = "Submit";
		// 获取表单数据
		JDS.call({
			service : submitService,
			data : [ workData ],
			success : function(result) {
				$.ui.popup("提交成功!");
				$.ui.goBack();
			},
			error : function(jqXHR) {
				// 处理流程操作返回 的错误信息
				handlerError(jqXHR, $btn, workData);
			}
		});
	}
}
/** ********************************* 提交结束 ********************************* */
/** ********************************* 退回开始 ********************************* */
// 退回处理
function onRollback(workData) {
	if (isRequiredRollbackOpinion(workData)) {
		openToSignOpinion(workData);
	} else {
		// 操作动作及类型
		workData.action = "退回";
		workData.actionType = "Rollback";
		JDS.call({
			service : rollbackWithWorkDataService,
			data : [ workData ],
			success : function(result) {
				$.ui.popup("退回成功!");
				$.ui.goBack();
			},
			error : function(jqXHR) {
				var btns = $("#af_actionsheet").find("a");
				var rollbackBtn = null;
				btns.each(function() {
					if ($(this).text() == "退回") {
						rollbackBtn = $(this);
					}
				});
				// 处理流程操作返回 的错误信息
				handlerError(jqXHR, rollbackBtn, workData);
			}
		});
	}
}
// 直接退回处理
function onDirectRollback(workData) {
	if (isRequiredRollbackOpinion(workData)) {
		openToSignOpinion(workData);
	} else {
		// 操作动作及类型
		workData.action = "直接退回";
		workData.actionType = "DirectRollback";
		workData.rollbackToPreTask = true;
		JDS.call({
			service : rollbackWithWorkDataService,
			data : [ workData ],
			success : function(result) {
				$.ui.popup("直接退回成功!");
			},
			error : function(jqXHR) {
				var btns = $("#af_actionsheet").find("a");
				var rollbackBtn = null;
				btns.each(function() {
					if ($(this).text() == "直接退回") {
						rollbackBtn = $(this);
					}
				});
				// 处理流程操作返回 的错误信息
				handlerError(jqXHR, rollbackBtn, workData);
			}
		});
	}
}
/** ********************************* 退回结束 ********************************* */
/** ********************************* 撤回开始 ********************************* */
// 撤回处理
function onCancel() {
	var taskInstUuid = $(this).data("taskInstUuid");
	var taskInstUuids = [];
	taskInstUuids.push(taskInstUuid);
	JDS.call({
		service : cancelService,
		data : [ taskInstUuids ],
		success : function(result) {
			$.ui.popup("撤回成功！");
			$.ui.goBack();
		},
		error : function(jqXHR) {
			// 处理流程操作返回 的错误信息
			$.ui.popup("撤回失败!");
		}
	});
}
/** ********************************* 撤回结束 ********************************* */
/** ********************************* 转办开始 ********************************* */
// 转办处理
function onTransfer(workData) {
	if (isRequiredTransferOpinion(workData)) {
		openToSignOpinion(workData);
	} else {
		// 获取表单数据
		$.mobileUnit.open({
			title : "选择转办人员",
			afterSelect : function(laReturn) {
				var transferUserIds = laReturn.id.split(";");
				JDS.call({
					service : transferWithWorkDataService,
					data : [ workData, transferUserIds ],
					success : function(result) {
						$.ui.popup("转办成功!");
						$.ui.goBack();
					},
					error : function(jqXHR) {
						// 处理流程操作返回 的错误信息
						$.ui.popup("转办失败!");
					}
				});
			}
		});
	}
}
/** ********************************* 转办结束 ********************************* */
/** ********************************* 会签开始 ********************************* */
// 会签处理
function onCounterSign(workData) {
	if (isRequiredCounterSignOpinion(workData)) {
		openToSignOpinion(workData);
	} else {
		$.mobileUnit.open({
			title : "选择会签人员",
			afterSelect : function(laReturn) {
				var counterSignUserIds = laReturn.id.split(";");
				JDS.call({
					service : counterSignWithWorkDataService,
					data : [ workData, counterSignUserIds ],
					success : function(result) {
						$.ui.popup("会签成功!");
						$.ui.goBack();
					},
					error : function(jqXHR) {
						// 处理流程操作返回 的错误信息
						$.ui.popup("会签失败!");
					}
				});
			}
		});
	}
}
/** ********************************* 会签结束 ********************************* */
/** ********************************* 抄送开始 ********************************* */
// 抄送处理
function onCopyTo(workData) {
	var taskInstUuid = null;
	if (workData != null) {
		taskInstUuid = workData.taskInstUuid;
	} else {
		taskInstUuid = $(this).data("taskInstUuid");
	}
	$.mobileUnit.open({
		title : "选择抄送人员",
		afterSelect : function(laReturn) {
			if (laReturn.id != null && laReturn.id != "") {
				var copyToUserIds = laReturn.id.split(";");
				JDS.call({
					service : copyToService,
					data : [ [ taskInstUuid ], copyToUserIds ],
					success : function(result) {
						$.ui.popup("抄送成功!");
					},
					error : function(jqXHR) {
						// 处理流程操作返回 的错误信息
						$.ui.popup("抄送失败!");
					}
				});
			}
		}
	});
}
/** ********************************* 抄送开始 ********************************* */
/** ******************************* 签署意见相关开始 ******************************* */
// 判断是否需要签署意见
function isRequiredSignOpinion(workData) {
	if (isNotBlank(workData.taskInstUuid) && !isNotBlank(workData.opinionText) && requiredSignOpinion == true) {
		return true;
	}
	return false;
}
// 判断是否转办必填意见
function isRequiredTransferOpinion(workData) {
	if (isNotBlank(workData.taskInstUuid) && !isNotBlank(workData.opinionText)
			&& requiredTransferOpinion == true) {
		return true;
	}
	return false;
}
// 判断是否会签必填意见
function isRequiredCounterSignOpinion(workData) {
	if (isNotBlank(workData.taskInstUuid) && !isNotBlank(workData.opinionText)
			&& requiredCounterSignOpinion == true) {
		return true;
	}
	return false;
}
// 判断是否退回必填意见
function isRequiredRollbackOpinion(workData) {
	if (isNotBlank(workData.taskInstUuid) && !isNotBlank(workData.opinionText)
			&& requiredRollbackOpinion == true) {
		return true;
	}
	return false;
}
/** ******************************* 签署意见相关结束 ******************************* */
