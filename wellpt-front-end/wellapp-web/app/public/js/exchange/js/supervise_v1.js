var Supervise = {};
$(function() {	
	
	Supervise.Remind = function(){
		
	};
	
	$("#document_view_dialog").dialog({
		autoOpen : false,
		modal : true,
		width : ExchangeDefine.exchange_dialog_width,
		height : ExchangeDefine.exchange_dialog_height,
		buttons : [{
			"text": exchange_button.remind,
			"class": "btn",
			"click":function() {
				var jdialog_op = {
						title: exchange_button.remind,
						cmuuid: $("#docView_docID").val(),
						cmsenderUUID: $("#docView_senderUUID").val(),
						cmtype:"REMIND",
						cmopinion:exchange_button.remind + exchange_base.opinion,
						cmsendee:exchange_button.remind + exchange_base.object,
						cmsendeetree:true
				};
				ExchangeDialog.open("remind", jdialog_op, ExchangeUI.reloadDocumentViewGrid);
			}
		},{
			"text": exchange_button.close,
			"class": "btn",
			"click":function() {
				$(this).dialog("close");
			}
		}]
	});
});