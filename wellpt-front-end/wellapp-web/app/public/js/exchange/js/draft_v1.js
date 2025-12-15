var DraftUI = {};

$(function() {
	
	DraftUI.draft_add = function(){
		JDS.call({
			service : 'documentService.getDytableData',
			data : [$("#formUUID").val(), ""],
			success : function(result) {
				//动态表单
				$("#draft_dytable").dytable({
					data : result.data.dytableData,
					open : function(){
						
					}
				});
			},
			error : function(result) {
				ExchangeHelper.hanlderError(result);
			}
		});
	};	
	DraftUI.draft_edit = function(uuid){
		JDS.call({
			service : 'draftService.getDraft',
			data : [uuid],
			success : function(result) {
				$("#draft_dytable").dytable({
					data : result.data.dytableData,
					open : function(){
											
					}
				});				
				ExchangeHelper.objectToForm(result.data, $("#draft_form"));
			},
			error : function(result) {
				ExchangeHelper.hanlderError(result);
			}
		});
	};
	DraftUI.transmit = function(uuid){
		JDS.call({
			service : 'documentService.copyDytableDataByDocID',
			data : [uuid],
			success : function(result) {
				$("#documentUUID").val('');//清空uuid,转发为新增
				$("#draft_dytable").dytable({
					data : result.data.dytableData,
					open : function(){						
						$("#dy_title").val("Fw:" + $("#dy_title").val());						
					}
				});
			},
			error : function(result) {
				ExchangeHelper.hanlderError(result);
			}
		});
	};
	
	$("#btn_save").click(function() {
		var dytableFormData = $("#draft_dytable").dytable("formData");
		var bean = $.extend({}, ExchangeDefine.FormDocumentBean);
		ExchangeHelper.formToObject($("#draft_form"), bean);
		bean.dytableData = dytableFormData;
		
		JDS.call({
			service : "draftService.saveDraft",
			data : [ bean ],
			success : function(result) {
				if (result.success) {
					window.close();
				} else {
					alert(result.msg);
				}
			},
			error : function(result) {
				ExchangeHelper.hanlderError(result);
			}
		});
	});
	
	$("#btn_send").click(function() {
		var dytableFormData = $("#draft_dytable").dytable("formData");
		var bean = $.extend({}, ExchangeDefine.FormDocumentBean);
		ExchangeHelper.formToObject($("#draft_form"), bean);
		bean.dytableData = dytableFormData;
		
		JDS.call({
			service : "draftService.saveSend",
			data : [ bean ],
			success : function(result) {
				if (result.success) {
					window.close();					
				} else {
					alert(result.msg);
				}
			},
			error : function(result) {
				ExchangeHelper.hanlderError(result);
			}
		});
	});
	
	$("#btn_close").click(function() {
		window.close();
	});
});