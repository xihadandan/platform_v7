$(function() {

	//列表
	var jqgrid_op = {
			gid: "#outbox_list",
			url: ctx + '/exchange/outbox/jqgrid',
			colNames: [ "senderUUID", exchange_button.del + "?", exchange_base.title, exchange_base.docno, exchange_base.sender, 
			            exchange_base.sender_date, exchange_base.feedback_limit, "documentUUID", "feedbackID" ],
			colModel: [ {
				"name" : "senderUUID",
				"index" : "senderUUID",
				"hidden" : true,
				"key" : true,
			}, {
				"name" : "checkboxID",				
				"formatter" : "checkbox",
				formatoptions:{disabled:false},
				disabled:true
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
				"width" : "180",
				"formatter" : "date",
				"formatoptions": {srcformat: 'Y-m-d', newformat: 'Y-m-d'}					
			}, {
				"name" : "documentUUID",
				"index" : "documentUUID",				
				"hidden" : true
			}, {
				"name" : "feedbackID",
				"index" : "feedbackID",
				"hidden" : true
			} ],
			pager:"#pager",
			sortname:"senderTime",
			afterInsertRow: function(id, data)
	        {
	            if(data.feedbackID) {
	                $('tr#' + id).css(ExchangeDefine.jqgrid_striking_row);
	            }
	        }
	};
	ExchangeJqgrid.open(jqgrid_op);
	//设置点击事件
	$(jqgrid_op.gid).jqGrid("setGridParam", {
	    onSelectRow: function(rowid, status) {
	    	var rowData = $(this).jqGrid("getRowData", rowid);
	    	$.extend(rowData, {"sendeeUUID" : rowData.feedbackID});	//带入feedback作为已读
	    	ExchangeUI.bindDocumentViewForm(rowData, jqgrid_op.gid);
	    }
	});
	
	//查看
	$("#document_view_dialog").dialog({
		autoOpen : false,
		modal : true,
		width : ExchangeDefine.exchange_dialog_width,
		height : ExchangeDefine.exchange_dialog_height,
		buttons : [{
			"text": exchange_button.sendadd,
			"class": "btn",
			"click":function() {	
				//DocumentUUID SignLimit FeedBackLimit SendeeUser
				var jdialog_op = {
						title: exchange_button.sendadd,
						cmtype:"SENDADD",
						cmopinion:exchange_button.sendadd + exchange_base.opinion,
						cmsendee:exchange_button.sendadd + exchange_base.object,
						cmexcute:$("#docView_sendeeUser").val(),//排除
						limit:true,
						fileupload:true
				};
				ExchangeDialog.open("sendadd", jdialog_op, ExchangeUI.reloadDocumentViewGrid);
			}
		},{
			"text": exchange_button.withdraw,
			"class": "btn",
			"click":function() {
				//sendeeUser senderUUID
				var jdialog_op = {
						title: exchange_button.withdraw,
						cmtype:"WITHDRAW",
						cmopinion:exchange_button.withdraw + exchange_base.opinion,
						cmsendee:exchange_button.withdraw + exchange_base.object,
						cmsendeetree:true,
						cmtreehaschild:1
				};
				ExchangeDialog.open("withdraw", jdialog_op, ExchangeUI.reloadDocumentViewGrid);
			}
		},{
			"text": exchange_button.finish,
			"class": "btn",
			"click":function() {
				//SenderUUID
				var jdialog_op = {
						title: exchange_button.finish,
						cmtype:"FINISH",
						cmopinion:exchange_button.finish + exchange_base.opinion,
						cmsendee:exchange_button.finish + exchange_base.object,
						cmsendeetree:true,
						cmtreehaschild:0
				};
				ExchangeDialog.open("finish", jdialog_op, ExchangeUI.reloadDocumentViewGrid);
			}
		},{
			"text": exchange_button.remind,
			"class": "btn",
			"click":function() {
				var jdialog_op = {
						title: exchange_button.remind,
						cmtype:"REMIND",
						cmopinion:exchange_button.remind + exchange_base.opinion,
						cmsendee:exchange_button.remind + exchange_base.object,			
						cmsendeetree:true,
						cmtreehaschild:1
				};
				ExchangeDialog.open("remind", jdialog_op, ExchangeUI.reloadDocumentViewGrid);
			}
		},{
			"text": exchange_button.cancel,
			"class": "btn",
			"click":function() {
				//SenderUUID
				var jdialog_op = {
						title: exchange_button.cancel,
						cmtype:"CANCEL",
						cmopinion:exchange_button.cancel + exchange_base.opinion
				};
				ExchangeDialog.open("cancel", jdialog_op, cancelAfterHandle);
			}
		},{
			"text": exchange_button.close,
			"class": "btn",
			"click":function() {
				$(this).dialog("close");
			}
		}]
	});
	
	//撤销操作后执行
	function cancelAfterHandle(){
		$("#document_view_dialog").dialog("close");
		$('#outbox_list').length ? $("#outbox_list").trigger("reloadGrid") : undefined;
	}
	
	//新增	
	$("#btn_add").click(function() {
		JDS.call({
			service : 'documentService.getDytableData',
			data : [$("#formUUID").val(), ""],
			success : function(result) {
				$("#draft_dytable").dytable({
					data : result.data.fetchDytableBean,
					open : function(){
						if(!exconfig.allowLimitTime){
							$("#dy_allow_limit_panel").hide();
						}
						
						//发送人单位
						JDS.call({
							service : "documentService.getTreeByUserUnit",
							data : [],
							success : function(result) {
								var data = result.data.children;
								for (var i = 0; i < data.length; i++) { 
									$("#dy_sender_user").append("<option value='" + data[i].id + "'>" + data[i].name + "</option>"); 
								}
							}
						});
					}
				});
				
				$("#draft_form_dialog").dialog("option", "title", exchange_button.add );
				$("#draft_form_dialog").dialog("open");
			},
			error : function(result) {
				ExchangeHelper.hanlderError(result);
			}
		});
	});
	
	//删除操作
	$("#btn_del").click(function() {
		
		var docIDArr = new Array();
		$("#outbox_list").find('input[type=checkbox]:checked').each(function() {
			var colid = $(this).parents('tr:last').attr('id');
			var rowData = $("#outbox_list").jqGrid("getRowData", colid);
			docIDArr.push(rowData.documentUUID);			
		});
		//alert(docIDArr.join());
		if (confirm(global.deleteConfirm)) {
			JDS.call({
				service : 'outboxService.delOutbox',
				data : [docIDArr.join()],
				success : function(result) {
					$("#outbox_list").trigger("reloadGrid");
				},
				error : function(result) {
					ExchangeHelper.hanlderError(result);
				}
			});
		}
	});
});