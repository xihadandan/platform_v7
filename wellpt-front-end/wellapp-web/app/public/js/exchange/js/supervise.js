$(function() {
	
	//列表
	var jqgrid_op = {
			gid: "#supervise_list",
			url: ctx + '/exchange/supervise/jqgrid',
			colNames: [ "documentUUID", exchange_base.title, exchange_base.docno,  exchange_base.sender
			            , exchange_base.sender_date, exchange_base.feedback_limit, "senderUUID" ],
			colModel: [ {
				"name" : "documentUUID",
				"index" : "documentUUID",
				"hidden" : true,
				"key" : true,
			}, {
				"name" : "title",
				"index" : "title",
				"width" : "180"
			}, {
				"name" : "docNO",
				"index" : "docNO",
				"width" : "180"
			}, {
				"name" : "senderUserName",
				"index" : "senderUserName",
				"width" : "180"
			}, {
				"name" : "senderTime",
				"index" : "senderTime",
				"width" : "180"
			}, {
				"name" : "feedBackLimitTime",
				"index" : "feedBackLimitTime",
				"formatter" : "date",
				"formatoptions": {srcformat: 'Y-m-d', newformat: 'Y-m-d'},
				"width" : "180"
			}, {
				"name" : "senderUUID",
				"index" : "senderUUID",
				"hidden" : true
			} ],
			pager:"#pager",
			sortname:"senderTime",
			onSelectRow: function(rowid, status) {
		    	var rowData = $("#supervise_list").jqGrid("getRowData", rowid);
		    	ExchangeUI.bindDocumentViewForm(rowData, "#supervise_list");
		    }
	};
	ExchangeJqgrid.open(jqgrid_op);
	
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