$(function() {
	
	var dataFormId = "config_form";	
	var bean = {
			allowLimitTime: null,
			attachmentForOpinion: null,
			attachmentForSign: null,
			draftLeaveDays: null,
			feedbackIsUnread: null,
			forceSignature: null,
			formName: null,
			formVersion: null,
			moduleManager: null,
			moduleName: null,
			moduleSupervisor: null,
			repeatIsUnread: null,
			showLogs: null,
			showTraceAll: null,
			showTraceUser: null,
			trashLeaveDays: null,
			messageTemplate: null
		};
	
	function load(){
		JDS.call({
			service : "configService.getBean",
			data : [],
			success : function(result) {
				if(result.success){					
					ExchangeHelper.objectToForm(result.data, $("#" + dataFormId));
				}else{
					alert(result.msg);
				}
			}
		});
	}
	load();
	// 保存
	$("#btn_save").click(function() {
		//获取选中数据
		ExchangeHelper.formToObject($("#" + dataFormId), bean);
		JDS.call({
			service : "configService.saveBean",
			data : [ bean ],
			success : function(result) {
				if (result.success) {
					alert(exchange_base.success);
					load();					
				} else {
					alert(result.msg);
				}
			},
			error : function(result) {
				ExchangeHelper.hanlderError(result);
			}
		});
	});
	
	$("#moduleManagerName").click(function() {
		$.unit.open({
			title : exchange_base.select_user,
			labelField : "moduleManagerName",
			valueField : "moduleManager"
		});
	});
	$("#moduleSupervisorName").click(function() {
		$.unit.open({
			title : exchange_base.select_user,
			labelField : "moduleSupervisorName",
			valueField : "moduleSupervisor"
		});
	});
	
});