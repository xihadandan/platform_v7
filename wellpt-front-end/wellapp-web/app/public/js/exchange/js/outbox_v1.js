var OutboxUI = {};

$(function() {
	OutboxUI.Sendadd = function(){
		JDS.call({
			service : 'documentService.getDytableData',
			data : [$("#formUUID").val(), ""],
			success : function(result) {
				//动态表单
				$("#sendadd_dytable").dytable({
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
	$("#btn_sendadd").click(function() {
		var dytableFormData = $("#sendadd_dytable").dytable("formData");
		var bean = $.extend({}, ExchangeDefine.FormDocumentBean);
		ExchangeHelper.formToObject($("#draft_form"), bean);				
		bean.dytableData = dytableFormData;
		
		JDS.call({
			service : "outboxService.sendAdd",
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
	OutboxUI.InitSendTree = function(uuid, hasSendee) {
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
			service : "outboxService.operate",
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