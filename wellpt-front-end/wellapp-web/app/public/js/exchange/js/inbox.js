$(function() {
	
	//列表
	var jqgrid_op = {
			gid: "#inbox_list",
			url: ctx + '/exchange/inbox/jqgrid',
			colNames: [ "senderUUID", "sendeeUUID", "documentUUID"
			            , exchange_base.title, exchange_base.docno, exchange_base.sender
			            , exchange_base.sender_date, exchange_base.feedback_limit, "readTime", "signTime" ],
			colModel: [ {
				"name" : "sendeeUUID",
				"index" : "sendeeUUID",
				"hidden" : true,
				"key" : true,
			}, {
				"name" : "senderUUID",
				"index" : "senderUUID",
				"hidden" : true
			}, {
				"name" : "documentUUID",
				"index" : "documentUUID",
				"hidden" : true
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
				"width" : "180"
			}, {
				"name" : "readTime",
				"index" : "readTime",
				"hidden" : true
			}, {
				"name" : "signTime",
				"index" : "signTime",
				"hidden" : true
			} ],
			pager:"#pager",
			sortname:"senderTime",
			afterInsertRow: function(id, data)
	        {
	            if(!data.readTime) {
	                $('tr#' + id).css(ExchangeDefine.jqgrid_striking_row);
	            }
	        }
	};
	ExchangeJqgrid.open(jqgrid_op);
	//设置点击事件,已读操作
	$(jqgrid_op.gid).jqGrid("setGridParam", {
		onSelectRow: function(rowid, status) {
	    	var rowData = $(this).jqGrid("getRowData", rowid);
	    	ExchangeUI.bindDocumentViewForm(rowData, jqgrid_op.gid);
	    }
	});
	//$(jqgrid_op.gid).jqGrid('setSelection', rowid);//选中
	
	//查看
	$("#document_view_dialog").dialog({
		autoOpen : false,
		modal : true,
		width : ExchangeDefine.exchange_dialog_width,
		height : ExchangeDefine.exchange_dialog_height,
		buttons : [ {
			"text" : exchange_button.sign,
			"class" : "btn",
			"click" : function() {
				//SendeeUUID
				if(confirm(exchange_base.confirm_sign)){
					//强制签名
					if(exconfig.forceSignature){
						$("#dialog-forceSignature").dialog('open');
					}else{
						SignaturePost();
					}
	    		}
			}
		}, {
			"text" : exchange_button.feedback,
			"class" : "btn",
			"click" : function() {
				//DocumentUUID SignLimit FeedBackLimit SenderUUID
				var jdialog_op = {
						title: exchange_button.feedback,
						cmtype:"FEEDBACK",
						cmopinion:exchange_button.feedback + exchange_base.opinion,
						limit:true,
						fileupload:exconfig.attachmentForOpinion//意见框允许增加附件
				};
				ExchangeDialog.open("feedback", jdialog_op, ExchangeUI.reloadDocumentViewGrid);
			}
		}, {
			"text" : exchange_button.forward,
			"class" : "btn",
			"click" : function() {
				//DocumentUUID SignLimit FeedBackLimit SendeeUser
				var jdialog_op = {
						title: exchange_button.forward,
						cmtype:"FORWARD",
						cmopinion:exchange_button.forward + exchange_base.opinion,
						cmsendee:exchange_button.forward + exchange_base.object,
						limit:true
				};
				ExchangeDialog.open("forward", jdialog_op, ExchangeUI.reloadDocumentViewGrid);
			}
		}, {
			"text" : exchange_button.transmit,
			"class" : "btn",
			"click" : function() {
				Transmit_dialog($("#docView_docID").val());
			}
		}, {
			"text" : exchange_button.close,
			"class" : "btn",
			"click" : function() {
				$(this).dialog("close");
			}
		} ]
	});
	
	//签名提交函数
	function SignaturePost(){		
		var rowID = $("#inbox_list").jqGrid('getGridParam', 'selrow');
		var rowData = $("#inbox_list").jqGrid("getRowData", rowID);
		var submitBean = {
			"documentUUID" : rowData.documentUUID,
			"sendeeUUID" : rowData.sendeeUUID,
			"senderUUID" : rowData.senderUUID,
			"type" : "RECEIVESIGN",
			"opinion" : exchange_base.sign_signuser + ":" + $("#forceSignatureName").val(),		    			
			"signLimit" : null,
			"feedBackLimit" : null
		};
		JDS.call({
			service : "documentService.saveOperate",
			data : [ submitBean ],
			success : function(result) {
				alert(exchange_base.complete_sign);
				$("#forceSignatureName").val('');
				ExchangeUI.reloadDocumentViewGrid();
			}
		});
	}
	
	//签收强制签名
	$("#dialog-forceSignature").dialog({
        autoOpen: false,
        title: exchange_base.sign_signature,
        modal: true,
        buttons:[ {
        	"text" : exchange_button.confirm,
			"class" : "btn",
			"click" : function() {
				if(ExchangeHelper.isEmptyValue($("#forceSignatureName").val())){
            		alert(exchange_base.sign_signature + exchange_base.no_empty);
					return;
            	}
            	SignaturePost();
                $(this).dialog("close");                
            }
        }, {
            "text" : exchange_button.close,
			"class" : "btn",
			"click" : function() {
                $(this).dialog("close");
                return false;
            }
        }]        
    });
	
	//转发
	function Transmit_dialog(documentUUID){
		JDS.call({
			service : 'documentService.copyDytableDataByDocID',
			data : [documentUUID],
			success : function(result) {
				$("#draft_dytable").dytable({
					data : result.data.fetchDytableBean,
					open : function(){
						
						$("#title").val("Fw:" + result.data.docTitle);
						if(!exconfig.allowLimitTime){
							$("#dy_allow_limit_panel").hide();
						}
						
						JDS.call({
							service : "documentService.getTreeByUserUnit",
							data : [],
							success : function(subresult) {
								var data = subresult.data.children;
								for (var i = 0; i < data.length; i++) { 
									$("#dy_sender_user").append("<option value='" + data[i].id + "'>" + data[i].name + "</option>"); 
								}
								if(result.data.senderUser) $("#dy_sender_user").val(result.data.senderUser);
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
});