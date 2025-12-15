$(function() {
	var bean = {
		"no" : null,
		"name" : null,
		"type" : null,
		"email" : null,
		"uuid" : null
	};

	var outunit_op = {
			gid: "#outunit_list",
			url: ctx + '/common/jqgrid/query?queryType=exchangeOutunit',
			"colNames" : [ "uuid", exchange_base.no, exchange_base.name, exchange_base.cate, "Email" ],
			"colModel" : [ {
				"name" : "uuid",
				"index" : "uuid",
				"width" : "180",
				"hidden" : true,
				"key" : true,
			}, {
				"name" : "no",
				"index" : "no",
				"width" : "180",
				"editable" : true
			}, {
				"name" : "name",
				"index" : "name",
				"width" : "180",
				"editable" : true
			}, {
				"name" : "type",
				"index" : "type",
				"width" : "180",
				"editable" : true
			}, {
				"name" : "email",
				"index" : "email",
				"width" : "180",
				"editable" : true
			} ],
			pager:"#pager",
			sortname:"name"
	};
	ExchangeJqgrid.open(outunit_op);
	//设置点击事件
	$(outunit_op.gid).jqGrid("setGridParam", {
		onSelectRow: function(rowid, status) {
			//$("#btn_edit").trigger("click");
	    }
	});
	$(".demoform").Validform({btnSubmit: "#demobutton",tiptype:1});	
	$("#outunit_form").Validform({ btnSubmit: "#btnOutunitSave", tiptype:1 });
	//表单	
	$("#outunit_form_dialog").dialog({
		autoOpen : false,
		modal : true,
		width : ExchangeDefine.exchange_dialog_width,
		height : ExchangeDefine.exchange_dialog_height,
		buttons : [{
			"id":	"btnOutunitSave",
			"text": exchange_button.save,
			"class": "btn",
			"click": function() {				
				ExchangeHelper.formToObject($("#outunit_form"), bean); 
				JDS.call({
					service : "outunitService.saveBean",
					data : [ bean ],
					success : function(result) {
						if (result.success) {
							$("#outunit_form_dialog").dialog("close");
							$("#outunit_list").trigger("reloadGrid");
							alert(exchange_base.success);
						} else {
							alert(result.msg);
						}
					},
					error : function(result) {
						ExchangeHelper.hanlderError(result);
					}
				});
			}
		},{
			"text": exchange_button.close,
			"class": "btn",
			"click": function() {
				$(this).dialog("close");
			}
		}]
	});
	// 新增操作
	$("#btn_add").click(function() {
		$("#outunit_form").clearForm(true);
		bean.uuid = null;
		$("#outunit_form_dialog").dialog("option", "title", exchange_button.add );
		$("#outunit_form_dialog").dialog("open");
	});	
	//修改操作
	$("#btn_edit").click(function() {
		$("#outunit_form").clearForm(true);
		bean.uuid = null;
		//获取选中数据
		var selectedId = $("#outunit_list").jqGrid("getGridParam", "selrow");
		if(selectedId == null){
			alert(global.selectARecord);
			return;
		}
		var rowData = $("#outunit_list").jqGrid("getRowData", selectedId);
		//设置表单值
		ExchangeHelper.objectToForm(rowData, $("#outunit_form"));
		$("#outunit_form_dialog").dialog("option", "title", exchange_button.edit );
		$("#outunit_form_dialog").dialog("open");
	});
	// 删除操作
	$("#btn_del").click(function() {
		//获取选中数据
		var selectedId = $("#outunit_list").jqGrid("getGridParam", "selrow");
		if(selectedId == null){
			alert(global.selectARecord);
			return;
		}
		var rowData = $("#outunit_list").jqGrid("getRowData", selectedId);
		
		if (confirm(global.deleteConfirm)) {
			JDS.call({
				service : "outunitService.delete",
				data : [ rowData.uuid ],
				success : function(result) {
					$("#outunit_list").trigger("reloadGrid");
				}
			});
		}
	});
	// 查询
	$("#btn_query").click(function() {
		if(!$("#query_type").val()) return;
		var searchData = {"queryPrefix" : "query"};
		var queryCol = "query_LIKES_" + $("#query_type").val();
		searchData[queryCol] = $("#query_key").val();
		$("#outunit_list").jqGrid('setGridParam',{ postData: null });
		$("#outunit_list").jqGrid("setGridParam", {
			postData : searchData,
			page : 1
		}).trigger("reloadGrid");
	});
	$("#query_key").keydown(function(event){
	    if(event.keyCode == 13){
	        $("#btn_query").click();
	    }
	});
	
	function queryType(){
		JDS.call({
			service : "outunitService.getJsonType",
			data : [ ],
			success : function(result) {
				var data = result.data;
				for (var i = 0; i < data.length; i++) {
					$("#type").append("<option value='" + data[i] + "'>" + data[i] + "</option>");									 
				}
			}
		});
	}
	queryType();
		
	function clear() {
		$("#outunit_form").clearForm(true);
		bean.uuid = null;
		// 清空列表
		var $childrenList = $("#outunit_list");
		$childrenList.jqGrid("clearGridData");
	}
});