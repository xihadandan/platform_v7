var InboxUI = {};
$(function() {
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
	InboxUI.Forward = function(){
		JDS.call({
			service : 'documentService.getDytableData',
			data : [$("#formUUID").val(), ""],
			success : function(result) {
				//动态表单
				$("#forward_dytable").dytable({
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
	//补充发送
	$("#btn_forward").click(function() {
		var dytableFormData = $("#forward_dytable").dytable("formData");
		var bean = $.extend({}, ExchangeDefine.FormDocumentBean);
		ExchangeHelper.formToObject($("#draft_form"), bean);				
		bean.dytableData = dytableFormData;
		
		JDS.call({
			service : "inboxService.forward",
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
	InboxUI.InitSendTree = function(uuid, hasSendee) {
		var treeSetting = {
			check : {
				enable : true
			},
			callback : {
				onCheck : null
			}
		};
		JDS.call({
			service : "documentService.getTreeByDocumentID",
			data : [ uuid, hasSendee ],
			success : function(result) {
				var zTree = $.fn.zTree.init($("#sender_tree"), treeSetting, result.data.children);
				zTree.expandAll(true);
			}
		});
	};
	//处理事务
	$("#btn_save").click(function() {
		var bean = $.extend({}, ExchangeDefine.FormDocumentBean);
		ExchangeHelper.formToObject($("#draft_form"), bean);
		bean.opinion = $("#dy_opinion").val();
		
		var zTree = $.fn.zTree.getZTreeObj("sender_tree");
		var checkedNodes = zTree.getCheckedNodes(true);
		var checkedUUIDs = new Array();
		$.each(checkedNodes, function(i, n) {
			if (n.isParent)
				return true;// 排除父节点 |return false;//相当于break;
			checkedUUIDs.push(n.id);
		});
		bean.ouuids = checkedUUIDs.join(";");
		
		JDS.call({
			service : "inboxService.feedback",
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
});