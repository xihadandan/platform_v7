$(function() {
	
	//列表
	var jqgrid_op = {
			gid: "#trash_list",
			url: ctx + '/exchange/trash/jqgrid',
			colNames: [ "documentUUID", exchange_base.title, exchange_base.docno, exchange_base.sender
			            , exchange_base.sender_date, exchange_base.feedback_limit ],
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
				"formatoptions": {srcformat: 'Y-m-d', newformat: 'Y-m-d'}
			} ],
			pager:"#pager",
			sortname:"senderTime"	
	};
	ExchangeJqgrid.open(jqgrid_op);
	
	//删除操作
	$("#btn_del").click(function() {
		if(confirm(exchange_base.confirm_delete)){
			var selectedId = $("#trash_list").jqGrid("getGridParam", "selrow");
			if(selectedId == null){
				alert(global.selectARecord);
				return;
			}
			var rowData = $("#trash_list").jqGrid("getRowData", selectedId);
			JDS.call({
				service : 'trashService.delTrash',
				data : [rowData.documentUUID],
				success : function(result) {
					$("#trash_list").trigger("reloadGrid");
				},
				error : function(result) {
					ExchangeHelper.hanlderError(result);
				}
			});
		}
	});
	
	//清空操作
	$("#btn_empty").click(function() {
		if(confirm(exchange_base.confirm_trash_empty)){
			JDS.call({
				service : 'trashService.emptyTrash',
				data : [],
				success : function(result) {
					$("#trash_list").trigger("reloadGrid");
				},
				error : function(result) {
					ExchangeHelper.hanlderError(result);
				}
			});
		}
	});
	
	//还原操作
	$("#btn_revert").click(function() {
		var selectedId = $("#trash_list").jqGrid("getGridParam", "selrow");
		if(selectedId == null){
			alert(global.selectARecord);
			return;
		}
		var rowData = $("#trash_list").jqGrid("getRowData", selectedId);
		JDS.call({
			service : 'trashService.revertTrash',
			data : [rowData.documentUUID],
			success : function(result) {
				$("#trash_list").trigger("reloadGrid");
			},
			error : function(result) {
				ExchangeHelper.hanlderError(result);
			}
		});
	});
});