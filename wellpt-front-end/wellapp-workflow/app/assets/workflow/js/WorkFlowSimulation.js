define([ "jquery", "server", "commons", "constant", "appContext", "appModal", "WorkFlowErrorHandler",
		"WorkFlowInteraction", "formBuilder", "multiOrg" ], function($, server, commons, constant, appContext,
		appModal, WorkFlowErrorHandler, WorkFlowInteraction, formBuilder, multiOrg) {
	var JDS = server.JDS;
	var UUID = commons.UUID;
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	var startNewWorkModule = "app-workflow-start-new-work";
	var WorkFlowSimulation = function(options) {
		var _self = this;
		_self.options = options;
		_self.simulationData = {};
		_self.workFlow = new WorkFlowInteraction();
		_self.errorHandler = new WorkFlowErrorHandler(_self);
		// 指定流程ID
		var flowDefId = options.flowDefId;
		if (StringUtils.isNotBlank(flowDefId)) {
			_self.simulationData.flowDefId = flowDefId;
		}
	};
	$.extend(WorkFlowSimulation.prototype, {
		// 启动流程仿真
		startSimulation : function() {
			var _self = this;
			var flowDefId = _self.simulationData.flowDefId;
			var simulationParams = _self.simulationParams;
			// 仿真流程为空，选择仿真流程
			if (StringUtils.isBlank(flowDefId)) {
				_self.selectSimulationFlow();
			} else if (simulationParams == null) {
				_self.setSimulationParams();
			} else {
				// 仿真处理
				_self.doSimulation();
			}
		},
		// 启动下一步仿真
		startNextStepSimulationIfRequire : function(result) {
			var _self = this;
			var data = result.data;
			_self.workFlow.clearTempData();
			JDS.restfulGet({
				url: ctx + "/api/workflow/simulation/getSimulationData",
				data: {flowInstUuid: data.flowInstUuid},
				contentType: "application/x-www-form-urlencoded",
				mask : true,
				success : function(result) {
					// 仿真参数
					var simulationParams = _self.simulationData.simulationParams;
					_self.simulationData = result.data;
					_self.simulationData.simulationParams = simulationParams;
					// 进入下一步仿真
					if (_self.simulationData.isOver !== true) {
						_self.startSimulation();
					} else {
						_self.onSimulationSuccess();
					}
				}
			});
		},
		// 流程仿真成功处理
		onSimulationSuccess : function() {
			var _self = this;
			var simulationParams = _self.simulationParams;
			var workUrl = "/workflow/work/v53/view/work?flowInstUuid=" + _self.simulationData.flowInstUuid;
			if (simulationParams && simulationParams.autoViewWorkAfterSimulation) {
				appContext.getWindowManager().open(workUrl);
			} else {
				appModal.confirm("流程仿真成功，是否查看流程数据！", function(result) {
					if (result) {
						appContext.getWindowManager().open(workUrl);
					}
				});
			}
		},
		// 仿真处理
		doSimulation : function() {
			var _self = this;
			var simulationData = _self.simulationData;
			_self.workFlow._tempData2WorkData();
			var interactionTaskData = _self.workFlow.getWorkData();
			simulationData = $.extend(simulationData, interactionTaskData);
			var taskName = simulationData.taskName;
			var todoUserName = simulationData.todoUserName;
			var autoShowMask = true;
			if (StringUtils.isNotBlank(taskName) && StringUtils.isNotBlank(todoUserName)) {
				autoShowMask = false;
				appModal.showMask("正在仿真 " + todoUserName + " 提交环节 " + taskName + "...");
			}
			JDS.restfulPost({
				url: ctx + "/api/workflow/simulation/simulationSubmit",
				data : simulationData,
				mask : autoShowMask,
				success : function(result) {
					appModal.hideMask();
					_self.startNextStepSimulationIfRequire(result);
				},
				error : function(jqXHR, statusText, error) {
					appModal.hideMask();
					var options = {};
					options.callback = _self.startSimulation;
					options.callbackContext = _self;
					options.workFlow = _self.workFlow;
					if (!_self.formDataInteractionIfRequired(jqXHR)) {
						_self.errorHandler.handle(jqXHR, null, null, options);
					}
				}
			});
		},
		// 表单交互数据管理需要的接口getId
		getId : function() {
			return "workFlowSimulation";
		},
		// 表单交互数据管理需要的接口refresh
		refresh : function() {
		},
		// 清理仿真数据
		cleanSimulationData : function() {
			$.get({
				url: ctx + "/api/workflow/simulation/cleanSimulationData",
				mask : false
			});
		},
		// 表单数据交互
		formDataInteractionIfRequired : function(jqXHR) {
			var _self = this;
			var faultData = {};
			if(jqXHR.responseText) {
			  	faultData =	JSON.parse(jqXHR.responseText);
			} else if(jqXHR.data) {
				faultData = jqXHR.data;
			}
			// api接口返回的错误信息
			if(faultData.code == -5002 && faultData.data) {
				faultData = faultData.data;
			}
			if (faultData.errorCode != "WorkFlowException" || !faultData.data || faultData.data.simulation != true) {
				return false;
			}
			appContext.require([ "DmsDataServices" ], function(DmsDataServices) {
				// 记录当前仿真信息
				window.workFlowSimulationContext = {
					interaction : faultData.data,
					context : _self
				};
				var simulationData = _self.simulationData;
				new DmsDataServices().openDialog({
					urlParams : {
						bug : "bug",
						ac_id : "btn_list_view_edit",
						dms_id : "wDataManagementViewer_C93D4DB6A59000013726CF00E3E01085",
						target : "_dialog",
						idKey : "uuid",
						idValue : faultData.data.dataUuid,
						formUuid : faultData.data.formUuid,
						dataUuid : faultData.data.dataUuid,
						ep_ac_get : "btn_workflow_simulation_dyform_get",
						ep_view_mode : faultData.data.canEditForm,
						ep_flowDefId : simulationData.flowDefId,
						ep_flowInstUuid : simulationData.flowInstUuid || "",
						ep_dataInteractionTaskId : faultData.data.dataInteractionTaskId
					},
					ui : _self
				});
			});
			return true;
		},
		// 选择仿真流程
		selectSimulationFlow : function() {
			var _self = this;
			var options = _self.options;
			appContext.require([ startNewWorkModule ], function(startNewWorkModule) {
				// 设置发起流程标题
				var title = options.flowTitle || "选择仿真流程";
				var categoryCode = options.flowCategoryCode;
				var startWorkOptions = {
					ui : options.ui,
					callbackContext : _self,
					callback : function(retVal) {
						_self.simulationData.flowDefId = retVal.flowDefId;
						// 重新启动仿真
						setTimeout(function() {
							_self.startSimulation();
						}, 1);
					},
					params : $.extend(_self.options, _self.options.params, {
						title : title,
						categoryCode : categoryCode
					})
				};
				appContext.executeJsModule(startNewWorkModule, startWorkOptions);
			})
		},
		// 设置仿真参数
		setSimulationParams : function() {
			var _self = this;
			var dlgId = UUID.createUUID();
			var dlgIdSelector = '#' + dlgId;
			var title = "设置仿真参数";
			var dialogOptions = {
				title : title,
				size : "large",
				dlgId : dlgId,
				templateId : "app-workflow-set-flow-simulation-params",
				shown : function() {
					_self.initFlowSimulationParams(dlgIdSelector);
				},
				buttons : {
					// loadSimulationParam : {
					// label : "选择仿真参数",
					// className : "btn-primary",
					// callback : function() {
					// }
					// },
					// saveSimulationParam : {
					// label : "保存仿真参数",
					// className : "btn-primary",
					// callback : function() {
					// }
					// },
					confirm : {
						label : "开始仿真",
						className : "btn-primary",
						callback : function() {
							// 收集仿真参数
							_self.collectionSimulationParam(dlgIdSelector);
							// 重新启动仿真
							setTimeout(function() {
								_self.startSimulation();
							}, 1);
						}
					}
				}
			};
			appModal.dialog(dialogOptions);
		},
		// 收集仿真参数
		collectionSimulationParam : function($container) {
			var _self = this;
			_self.simulationParams = {};
			var simulationParams = _self.simulationParams;
			// 基本信息
			// 启动人
			var startUserId = $("#startUserId", $container).val();
			// 是否生成流水号
			var generateSerialNumber = $("#generateSerialNumber", $container)[0].checked;
			// 是否发送消息
			var sendMsg = $("#sendMsg", $container)[0].checked;
			// 是否归档
			var archive = $("#archive", $container)[0].checked;
			// 仿真结束后自动打开查看流程数据
			var autoViewWorkAfterSimulation = $("#autoViewWorkAfterSimulation", $container)[0].checked;

			// 表单数据
			var formDataSource = $("input[name='formDataSource']:checked", $container).val();
			var interactionTasks = $("input[name='interactionTasks']", $container).val();

			// 办理意见
			var opinions = [];
			var $opinionRows = $(".table-simulation-opinion-columns", $container).find("tbody").children();
			$.each($opinionRows, function(i) {
				var $row = $(this);
				var taskId = $row.find(".column-task>select").val();
				var taskUserName = $row.find(".column-user").find("input[name='taskUserName']").val();
				var taskUserId = $row.find(".column-user").find("input[name='taskUserId']").val();
				var opinionText = $row.find(".column-opinionText>input").val();
				var opinionValue = $row.find(".column-opinionValue>select").val();
				var opinionLabel = $row.find(".column-opinionValue>select").find("option:selected").text();
				opinions.push({
					taskId : taskId,
					taskUserName : taskUserName,
					taskUserId : taskUserId,
					opinionText : opinionText,
					opinionValue : opinionValue,
					opinionLabel : opinionLabel
				});
			});

			simulationParams.startUserId = startUserId;
			simulationParams.generateSerialNumber = generateSerialNumber;
			simulationParams.sendMsg = sendMsg;
			simulationParams.archive = archive;
			simulationParams.autoViewWorkAfterSimulation = autoViewWorkAfterSimulation;
			simulationParams.formDataSource = formDataSource;
			simulationParams.interactionTasks = StringUtils.isBlank(interactionTasks) ? [] : interactionTasks
					.split(";");
			simulationParams.opinions = opinions;

			// 仿真数据
			_self.simulationData.simulationParams = simulationParams;
		},
		initFlowSimulationParams : function($container) {
			var _self = this;
			$("#wf_flow_simulation_params_tabs ul a", $container).on("click", function(e) {
				$(this).tab("show");
				e.preventDefault();
			})
			// 交互环节
			_self.loadFormDataInteractionTasks($container, function(tasks) {
				_self.tasks = tasks || [];
				$("#interactionTasks", $container).wSelect2({
					data : _self.tasks,
					multiple : true
				})
				// 绑定设置仿真参数事件
				_self.bindSimulationParamEvents($container);
			});
		},
		// 表单数据交互环节
		loadFormDataInteractionTasks : function($container, callback) {
			var _self = this;
			var flowDefId = _self.simulationData.flowDefId;
			$.get({
				url: ctx + "/api/workflow/simulation/listTaskByFlowDefId",
				data: {flowDefId: flowDefId},
				mask : true,
				success : function(result) {
					callback.call(_self, result.data);
				}
			});
		},
		// 绑定设置仿真参数事件
		bindSimulationParamEvents : function($container) {
			var _self = this;
			// 启动人
			$("#startUserName", $container).on("click", function() {
				// 人员选择
				$.unit2.open({
					valueField : "startUserId",
					labelField : "startUserName",
					title : "选择人员",
					type : "all",
					multiple : false,
					selectTypes : "U",
					valueFormat : "justId"
				});
			});
			// 办理意见
			// 添加
			$("#btn_opinion_add", $container).on("click", function() {
				_self.addSimulationOpinionColumnRow($container, {});
			});
			// 删除
			$("#btn_opinion_del", $container).on("click", function() {
				var $selectedRows = $(".table-simulation-opinion-columns .active", $container);
				if ($selectedRows.length == 0) {
					appModal.error('请选择记录！');
				} else {
					$selectedRows.remove();
				}
			});
			// 行选择
			$(".table-simulation-opinion-columns").on("click", "tr", function() {
				$(".table-simulation-opinion-columns").find("tr").removeClass("active");
				$(this).addClass("active");
			});
			// 仿真数据交互提交
			$(document).off("simulationDataSubmit").on("simulationDataSubmit", function(event) {
				var detail = event.detail;
				var simulationContext = window.workFlowSimulationContext;
				if (!simulationContext || !detail) {
					return;
				}
				var context = simulationContext.context;
				context.simulationData.formUuid = detail.formUuid;
				context.simulationData.dataUuid = detail.dataUuid;
				// 记录已交互的环节
				var userInteractedTasks = context.simulationData.simulationParams.userInteractedTasks || [];
				userInteractedTasks.push(simulationContext.interaction.dataInteractionTaskId);
				context.simulationData.simulationParams.userInteractedTasks = userInteractedTasks;
				simulationContext.context.doSimulation();
			});
		},
		// 添加仿真办理意见
		addSimulationOpinionColumnRow : function($container, column) {
			var _self = this;
			var $body = $(".table-simulation-opinion-columns tbody", $container);
			var sb = new StringBuilder();
			var index = $body.children().length;
			sb.append("<tr>");
			sb.append("<td class='column-task'><select/></td>");
			sb.append("<td class='column-user'>");
			sb.appendFormat("<input id='taskUserName{0}' name='taskUserName' type='text'/>", index);
			sb.appendFormat("<input id='taskUserId{0}' name='taskUserId' type='hidden'/>", index);
			sb.append("</td>");
			sb.append("<td class='column-opinionText'><input/></td>");
			sb.append("<td class='column-opinionValue'><select/></td>");
			sb.append("</tr>");
			var $row = $(sb.toString());
			$row.data("column", column);
			$body.append($row);

			// 环节意见立场
			var tasks = _self.tasks || [];
			$.each(tasks, function(i, task) {
				var $option1 = $("<option>", {
					text : task.text,
					value : task.id
				}).data("task", task);
				$row.find(".column-task>select").append($option1);
				// 环节变更更新意见立场
				$row.find(".column-task>select").on("change", function() {
					var $taskSelect = $(this);
					var newTaskId = $taskSelect.val();
					var $selectedOptin = $taskSelect.find("option:selected");
					var tmp = $selectedOptin.data("task") || {};
					// 意见立场
					var options = tmp.options || [ {
						name : "",
						value : ""
					} ];
					$taskSelect.closest("tr").find(".column-opinionValue>select").html("");
					$.each(options, function(j, option) {
						var $option2 = $("<option>", {
							text : option.name,
							value : option.value
						})
						$taskSelect.closest("tr").find(".column-opinionValue>select").append($option2);
					});
				}).trigger("change");
			});
			// 办理人人员选择
			$("#taskUserName" + index).on("click", function() {
				$.unit2.open({
					valueField : "taskUserId" + index,
					labelField : "taskUserName" + index,
					title : "选择办理人",
					type : "all",
					multiple : false,
					selectTypes : "U",
					valueFormat : "justId"
				});
			});
		}
	});
	return WorkFlowSimulation;
});
