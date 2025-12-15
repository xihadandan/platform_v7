!function($) {
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
		SaveData : "SaveData", // 表单数据保存失败
		RequiredFieldIsBlank : "RequiredFieldIsBlank" // 必填域为空
	};

	// 创建DIV元素
	function createDiv(selector) {
		var id = selector;
		if (selector.indexOf("#") == 0) {
			id = selector.substring(1);
		}
		// 放置
		if ($(selector).length == 0) {
			$("body").append("<div id='" + id + "' />");
		}
	}

	var isNotBlank = StringUtils.isNotBlank;

	$.workflowException = {
		handle : function(option) {
			var $element = $(option.element);
			var workData = option.workData;
			var msg = option.msg;
			var callback = option.callback;

			if (WorkFlowErrorCode.WorkFlowException === msg.errorCode) {
				WorkFlowException(msg.data, $element, workData, callback);
			} else if (WorkFlowErrorCode.TaskNotAssignedUser === msg.errorCode) {
				TaskNotAssignedUser(msg.data, $element, workData, callback);
			} else if (WorkFlowErrorCode.TaskNotAssignedCopyUser === msg.errorCode) {
				TaskNotAssignedCopyUser(msg.data, $element, workData, callback);
			} else if (WorkFlowErrorCode.TaskNotAssignedMonitor === msg.errorCode) {
				TaskNotAssignedMonitor(msg.data, $element, workData, callback);
			} else if (WorkFlowErrorCode.ChooseSpecificUser === msg.errorCode) {
				if (msg.data.unitUser) {
					ChooseUnitUser(msg.data, $element, workData, callback);
				} else {
					ChooseSpecificUser(msg.data, $element, workData, callback);
				}
			} else if (WorkFlowErrorCode.OnlyChooseOneUser === msg.errorCode) {
				if (msg.data.unitUser) {
					ChooseUnitUser(msg.data, $element, workData, callback);
				} else {
					OnlyChooseOneUser(msg.data, $element, workData, callback);
				}
			} else if (WorkFlowErrorCode.JudgmentBranchFlowNotFound === msg.errorCode) {
				JudgmentBranchFlowNotFound(msg.data, $element, workData,
						callback);
			} else if (WorkFlowErrorCode.MultiJudgmentBranch === msg.errorCode) {
				MultiJudgmentBranch(msg.data, $element, workData, callback);
			} else if (WorkFlowErrorCode.IdentityNotFlowPermission === msg.errorCode) {
				IdentityNotFlowPermission(msg.data, $element, workData,
						callback);
			} else if (WorkFlowErrorCode.SaveData === msg.errorCode) {
				var msgText = "<div><a id='wf_save_data_error_msg' title='"
						+ msg.msg
						+ "' style='color: #000;text-decoration: none;'>表单数据保存失败！</a></div>";
				oAlert2(msgText);
				$("#wf_save_data_error_msg").mouseover(function() {
					alert($(this).attr("title"));
				});
			} else if (WorkFlowErrorCode.RequiredFieldIsBlank === msg.errorCode) {
				oAlert2("表单必填字段为空异常!");
			} else {
				oAlert2("工作流无法处理的未知异常：" + jqXHR.responseText);
			}
		}
	};
	// 1、工作流异常
	function WorkFlowException(eData, $element, workData, callback) {
		if (eData.hasOwnProperty("autoClose")) {
			if (eData["autoClose"] == true) {
				oAlert(eData["msg"], function() {
					// 重新触发提交事件
					if (callback) {
						callback.call($element);
					}
				});
			} else {
				oAlert2(eData["msg"], function() {
					// 重新触发提交事件
					if (callback) {
						callback.call($element);
					}
				});
			}
		} else {
			oAlert(eData, function() {
				// 重新触发提交事件
				if (callback) {
					callback.call($element);
				}
			});
		}
	}
	// 2、任务没有指定参与者，弹出人员选择框选择参与人(人员、部门及群组)
	function TaskNotAssignedUser(eData, $element, workData, callback) {
		var title = "";
		if (isNotBlank(eData.title)) {
			title = eData.title;
		}
		$.unit.open({
			title : "选择承办人" + title,
			selectType : 4,
			afterSelect : function(laReturn) {
				var taskUsers = {};
				var taskId = eData.taskId;
				if (isNotBlank(laReturn.id)) {
					// 在原来的环节办理人上增加环节办理人
					taskUsers = workData.taskUsers;
					var userIds = laReturn.id.split(";");
					taskUsers[taskId] = userIds;
				} else {
					taskUsers[taskId] = null;
					workData.taskUsers = taskUsers;
				}
				// 重新触发提交事件
				if (callback) {
					callback.call($element);
				} else {
					$element.trigger("click");
				}
			}
		});
	}
	// 3、任务没有指定抄送人，弹出人员选择框选择抄送人(人员、部门及群组)
	function TaskNotAssignedCopyUser(eData, $element, workData, callback) {
		var title = "";
		if (isNotBlank(eData.title)) {
			title = eData.title;
		}
		$.unit.open({
			title : "选择抄送人" + title,
			selectType : 4,
			close : function() {
			},
			afterSelect : function(laReturn) {
				var taskCopyUsers = workData.taskCopyUsers;
				var taskId = eData.taskId;
				if (isNotBlank(laReturn.id)) {
					var userIds = laReturn.id.split(";");
					taskCopyUsers[taskId] = userIds;
					workData.taskCopyUsers = taskCopyUsers;
				} else {
					taskCopyUsers[taskId] = null;
					workData.taskCopyUsers = taskCopyUsers;
				}
				// 重新触发提交事件
				if (callback) {
					callback.call($element);
				} else {
					$element.trigger("click");
				}
			}
		});
	}
	// 4、任务没有指定督办人，弹出人员选择框选择督办人(人员和部门及群组)
	function TaskNotAssignedMonitor(eData, $element, workData, callback) {
		var title = "";
		if (isNotBlank(eData.title)) {
			title = eData.title;
		}
		$.unit.open({
			title : "选择督办人" + title,
			close : function() {
			},
			afterSelect : function(laReturn) {
				var taskMonitors = workData.taskMonitors;
				var taskId = eData.taskId;
				if (isNotBlank(laReturn.id)) {
					var userIds = laReturn.id.split(";");
					taskMonitors[taskId] = userIds;
					workData.taskMonitors = taskMonitors;
				} else {
					taskMonitors[taskId] = null;
					workData.taskMonitors = taskMonitors;
				}
				// 重新触发提交事件
				if (callback) {
					callback.call($element);
				} else {
					$element.trigger("click");
				}
			}
		});
	}

	// 选择组织选择框人员
	function ChooseUnitUser(eData, $element, workData, callback) {
		var unitUser = eData.unitUser;
		var multiple = unitUser.multiple;
		var selectType = unitUser.selectType;
		var taskName = eData.taskName;
		var filterCondition = "";
		for ( var i = 0; i < eData.unitUser.initIds.length; i++) {
			var val = eData.unitUser.initIds[i];
			if (filterCondition != "") {
				filterCondition += ",";
			}
			filterCondition += val;
		}
		filterCondition = encodeURI(filterCondition);
		$.unit.open({
			initNames : "",
			initIDs : "",
			title : "选择承办人(" + taskName + ")",
			multiple : multiple,
			selectType : selectType,
			nameType : "21",
			type : "Mixture",
			loginStatus : false,
			excludeValues : null,
			showType : false,
			filterCondition : filterCondition,
			close : function() {
			},
			afterSelect : function(laReturn) {
				var taskUsers = {};
				var taskId = eData.taskId;
				if (isNotBlank(laReturn.id)) {
					// 在原来的环节办理人上增加环节办理人
					taskUsers = workData.taskUsers;
					var userIds = laReturn.id.split(";");
					taskUsers[taskId] = userIds;
				} else {
					taskUsers[taskId] = null;
					workData.taskUsers = taskUsers;
				}
				// 重新触发提交事件
				if (callback) {
					callback.call($element);
				} else {
					$element.trigger("click");
				}
			}
		});
	}
	// 5、选择具体办理人
	function ChooseSpecificUser(eData, $element, workData, callback) {
		var taskName = eData.taskName;
		var taskId = eData.taskId;
		var userIds = eData.userIds;
		// 创建弹出框Div
		var dlgSelector = "#dlg_choose_specific_user";
		createDiv(dlgSelector);
		var searchBox = "<div class='query-fields form_operate'>"
				+ "<input name='query_input' />"
				+ "<button type='button' class='btn'>查询</button></div>";
		$(dlgSelector).append(searchBox);
		$(dlgSelector).append("<div class='user-to-choose'></div>");
		var listChooseUser = function(userIds, queryValue) {
			JDS
					.call({
						service : "workService.queryUsers",
						data : [ userIds, queryValue ],
						async : false,
						success : function(result) {
							var users = result.data;
							$("div.user-to-choose", dlgSelector).html("");
							for ( var i = 0; i < users.length; i++) {
								var user = users[i];
								var $checkbox = $("<label class='checkbox inline'><input id='"
										+ user.id
										+ "' name='chooseSpecificUser' type='checkbox' value='"
										+ user.id
										+ "'>"
										+ user.userName
										+ "</label>");
								if (i % 2 == 0) {
									$checkbox.css("width", "150px").css(
											"margin-left", "50px");
								}
								$("div.user-to-choose", dlgSelector).append(
										$checkbox);
							}
						}
					});
		};
		var options = {
			title : "选择具体办理人" + "(" + taskName + ")",
			modal : true,
			autoOpen : true,
			resizable : false,
			width : 400,
			height : 300,
			open : function() {
				// 列表查询
				$("input[name='query_input']", dlgSelector).keypress(
						function(e) {
							if (e.keyCode == 13) {
								listChooseUser(userIds, $(this).val());
								return false;
							}
						});
				$("button[type='button']", dlgSelector).click(
						function(e) {
							var queryValue = $("input[name='query_input']",
									dlgSelector).val();
							listChooseUser(userIds, queryValue);
						});
				listChooseUser(userIds, "");
			},
			buttons : {
				"确定" : function(e) {
					if ($("input[name=chooseSpecificUser]:checked").length < 1) {
						oAlert("请选择具体办理人!");
						return;
					}
					var $checkboxes = $("input[name=chooseSpecificUser]:checked");
					var userIds = [];
					$.each($checkboxes, function(i) {
						userIds.push($(this).val());
					});
					workData.taskUsers[taskId] = userIds;
					$(this).oDialog("close");
					// 重新触发提交事件
					if (callback) {
						callback.call($element);
					} else {
						$element.trigger("click");
					}
				},
				"取消" : function(e) {
					$(this).oDialog("close");
				}
			},
			close : function() {
				$(dlgSelector).html("");
			}
		};
		$(dlgSelector).oDialog(options);
	}
	// 6、只能选择一个人办理
	function OnlyChooseOneUser(eData, $element, workData, callback) {
		var taskName = eData.taskName;
		var taskId = eData.taskId;
		var userIds = eData.userIds;
		// 创建弹出框DIV
		var dlgSelector = "#dlg_choose_one_user";
		createDiv(dlgSelector);
		var searchBox = "<div class='query-fields form_operate'>"
				+ "<input name='query_input' />"
				+ "<button type='button' class='btn'>查询</button></div>";
		$(dlgSelector).append(searchBox);
		$(dlgSelector).append("<div class='user-to-choose'></div>");
		var listChooseUser = function(userIds, queryValue) {
			JDS
					.call({
						service : "workService.queryUsers",
						data : [ userIds, queryValue ],
						async : false,
						success : function(result) {
							var users = result.data;
							$("div.user-to-choose", dlgSelector).html("");
							for ( var i = 0; i < users.length; i++) {
								var user = users[i];
								var $radio = $("<label class='radio inline'><input id='"
										+ user.id
										+ "' name='onlyOneUser' type='radio' value='"
										+ user.id
										+ "'>"
										+ user.userName
										+ "</label>");
								if (i % 2 == 0) {
									$radio.css("width", "150px").css(
											"margin-left", "50px");
								}
								$("div.user-to-choose", dlgSelector).append(
										$radio);
							}
						}
					});
		};
		var options = {
			title : "选择一个办理人" + "(" + taskName + ")",
			modal : true,
			autoOpen : true,
			resizable : false,
			width : 400,
			height : 300,
			open : function() {
				// 列表查询
				$("input[name='query_input']", dlgSelector).keypress(
						function(e) {
							if (e.keyCode == 13) {
								listChooseUser(userIds, $(this).val());
								return false;
							}
						});
				$("button[type='button']", dlgSelector).off("click");
				$("button[type='button']", dlgSelector).on(
						"click",
						function(e) {
							var queryValue = $("input[name='query_input']",
									dlgSelector).val();
							listChooseUser(userIds, queryValue);
						});
				listChooseUser(userIds, "");
			},
			buttons : {
				"确定" : function(e) {
					if ($("input[name=onlyOneUser]:checked").val() == null) {
						oAlert("请选择一个承办人!");
						return;
					}
					var val = $("input[name=onlyOneUser]:checked").val();
					workData.taskUsers[taskId] = [ val ];
					$(this).oDialog("close");
					// 重新触发提交事件
					if (callback) {
						callback.call($element);
					} else {
						$element.trigger("click");
					}
				},
				"取消" : function(e) {
					workData.taskUsers = {};
					$(this).oDialog("close");
				}
			},
			close : function() {
				$(dlgSelector).html("");
			}
		};
		$(dlgSelector).oDialog(options);
	}
	// 7、弹出环节选择框选择下一流程环节
	function JudgmentBranchFlowNotFound(eData, $element, workData, callback) {
		var toTasks = eData.toTasks;
		workData.fromTaskId = eData.fromTaskId;
		var multiselect = eData.multiselect;
		// 创建弹出框DIV
		var dlgSelector = "#dlg_select_task";
		createDiv(dlgSelector);
		if (toTasks != null) {
			for ( var i = 0; i < toTasks.length; i++) {
				var task = toTasks[i];
				if (multiselect) {
					var checkbox = "<div><label class='checkbox inline'><input id='"
							+ task.id
							+ "' name='toTaskId' type='checkbox' value='"
							+ task.id + "'>" + task.name + "</label></div>";
					$(dlgSelector).append(checkbox);
				} else {
					var radio = "<div><label class='radio inline' style='margin-left: 20px;'><input id='"
							+ task.id
							+ "' name='toTaskId' type='radio' value='"
							+ task.id + "'>" + task.name + "</label></div>";
					$(dlgSelector).append(radio);
				}
			}
		}
		// 显示选择下一环节弹出框
		showSelectTaskDialog({}, eData, $element, workData, callback);
	}
	// 显示选择下一环节弹出框
	function showSelectTaskDialog(option, eData, $element, workData, callback) {
		// 初始化下一流程选择框
		var options = {
			title : "选择下一环节",
			autoOpen : true,
			height : 300,
			width : 350,
			resizable : false,
			modal : true,
			buttons : {
				"确定" : function(e) {
					var $checkbox = $("input[name=toTaskId]:checked");
					if ($checkbox.length == 0) {
						workData.fromTaskId = null;
						oAlert("请选择环节!");
						return;
					}

					var toTaskId = "";
					$.each($checkbox, function(i) {
						toTaskId += $(this).val();
						if (i != $checkbox.length - 1) {
							toTaskId += ";";
						}
					});
					workData.toTaskId = toTaskId;
					$(this).oDialog("close");
					// 重新触发提交事件
					if (callback) {
						callback.call($element);
					} else {
						$element.trigger("click");
					}
				},
				"取消" : function(e) {
					$(this).oDialog("close");
				}
			},
			close : function() {
				$("#dlg_select_task").html("");
			}
		};
		options = $.extend(true, options, option);
		$("#dlg_select_task").oDialog(options);
	}
	// 9、找到多个判断分支流向
	function MultiJudgmentBranch(eData, $element, workData, callback) {
		JudgmentBranchFlowNotFound(eData, $element, workData, callback);
	}
	// 11、用户没有权限访问流程
	function IdentityNotFlowPermission(eData, $element, workData, callback) {
		var taskId = eData.taskId;
		if (workData.taskUsers != null
				&& workData.taskUsers.hasOwnProperty(taskId)) {
			delete workData.taskUsers[taskId];
		}
		oAlert2(eData.msg, function() {
			// 重新触发提交事件
			if (callback) {
				callback.call($element);
			} else {
				$element.trigger("click");
			}
		});
	}
}(window.jQuery);