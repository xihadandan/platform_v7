$(function() {	
	//列表
	var jqgrid_op = {
			gid: "#draft_list",
			url: ctx + '/exchange/draft/jqgrid',
			colNames: [ "documentUUID", exchange_base.title, exchange_base.docno, exchange_base.sender
			            , exchange_base.sender_date, exchange_base.feedback_limit ],
			colModel: [ {
				"name" : "documentUUID",
				"index" : "documentUUID",
				"hidden" : true,
				"key" : true,
			}, {
				"name" : "title",
				"index" : "title"
			}, {
				"name" : "docNO",
				"index" : "docNO"
			}, {
				"name" : "senderUserName",
				"index" : "senderUserName"
			}, {
				"name" : "senderTime",
				"index" : "senderTime"
			}, {
				"name" : "feedBackLimitTime",
				"index" : "feedBackLimitTime",
				"formatter" : "date",
				"formatoptions": {srcformat: 'Y-m-d', newformat: 'Y-m-d'}
			} ],
			pager:"#pager",
			sortname:"senderTime"	
	};
	//ExchangeJqgrid.open(jqgrid_op);
	
	//新建界面
	$("#btn_add").click(function() {
		JDS.call({
			service : 'documentService.getDytableData',
			data : [$("#formUUID").val(), ""],
			success : function(result) {
				//动态表单
				$("#draft_dytable").dytable({
					data : result.data.fetchDytableBean,
					open : function(){
						if(!exconfig.allowLimitTime){
							$("#dy_allow_limit_panel").hide();
						}
						
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
	
	//修改界面
	$("#btn_edit").click(function() {
		//获取选中数据
		var selectedId = $("#draft_list").jqGrid("getGridParam", "selrow");
		if(selectedId == null){
			alert(global.selectARecord);
			return;
		}
		var rowData = $("#draft_list").jqGrid("getRowData", selectedId);
		JDS.call({
			service : 'draftService.getDraft',
			data : [rowData.documentUUID],
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
				
				ExchangeHelper.objectToForm(result.data, $("#draft_form"));
				$("#draft_form_dialog").dialog("option", "title", exchange_button.edit );
				$("#draft_form_dialog").dialog("open");
			},
			error : function(result) {
				ExchangeHelper.hanlderError(result);
			}
		});
	});
	
	//删除操作
	$("#btn_del").click(function() {
		var selectedId = $("#draft_list").jqGrid("getGridParam", "selrow");
		if(selectedId == null){
			alert(global.selectARecord);
			return;
		}
		if (confirm(global.deleteConfirm)) {
			var rowData = $("#draft_list").jqGrid("getRowData", selectedId);
			JDS.call({
				service : 'draftService.delDraft',
				data : [rowData.documentUUID],
				success : function(result) {
					$("#draft_list").trigger("reloadGrid");
				},
				error : function(result) {
					ExchangeHelper.hanlderError(result);
				}
			});
		}
	});	
});
