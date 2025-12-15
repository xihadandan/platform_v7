//加载全局国际化资源
I18nLoader.load("/resources/pt/js/global");
$(function() {
	var bean = {
		"uuid" : null,
		"name" : null,
		"id" : null,
		"version" : null,
		"category" : null
	};
	var categories = {};
	JDS.call({
		service : "flowCategoryService.getAll",
		data : [],
		async : true,
		success : function(result) {
			var data = result.data;
			$(data).each(function() {
				categories[this["code"]] = this["name"];
			});
		}
	});
	$("#list").jqGrid({
		url : ctx + '/common/jqgrid/query?serviceName=workService',
		mtype : 'POST',
		datatype : "json",
		colNames : [ "uuid", "名称", "别名", "版本", "分类" ],
		colModel : [ {
			name : "uuid",
			index : "uuid",
			width : "180",
			hidden : true,
			key : true
		}, {
			name : "name",
			index : "name",
			width : "180"
		}, {
			name : "id",
			index : "id",
			width : "180"
		}, {
			name : "version",
			index : "version",
			width : "180"
		}, {
			name : "category",
			index : "category",
			width : "180",
			formatter : function(cellvalue, options, rowObject) {
				return categories[cellvalue] == null ? cellvalue : categories[cellvalue];
			}
		} ],
		rowNum : 20,
		rownumbers : true,
		rowList : [ 10, 20, 50, 100, 200 ],
		pager : "#pager",
		sortname : "category",
		recordpos : "right",
		viewrecords : true,
		sortable : true,
		sortorder : "asc",
		multiselect : false,
		autowidth : true,
		height : 450,
		scrollOffset : 0,
		jsonReader : {
			root : "dataList",
			total : "totalPages",
			page : "currentPage",
			records : "totalRows",
			repeatitems : false
		},// 行选择事件
		onSelectRow : function(id) {
			var rowData = $(this).getRowData(id);
			bean.uuid = rowData.uuid;
			bean.name = rowData.name;
			bean.id = rowData.id;
			bean.version = rowData.version;
			bean.category = rowData.category;
		},
		ondblClickRow : function(id) {
			var rowData = $(this).getRowData(id);
			if (rowData.uuid != null && rowData.uuid != "") {
				var uuid = rowData.uuid;
				window.open(ctx + '/workflow/work/new?flowDefUuid=' + uuid, "_blank");
			}
		},
		loadComplete : function(data) {
			$("#list").setSelection($("#list").getDataIDs()[0]);
		}
	});

	// JQuery layout布局变化时，更新jqGrid高度与宽度
	function resizeJqGrid(position, pane, paneState) {
		if (grid = $('.ui-jqgrid-btable:visible')) {
			grid.each(function(index) {
				var gridId = $(this).attr('id');
				$('#' + gridId).setGridWidth(pane.width() - 2);
				$('#' + gridId).setGridHeight(pane.height() - 44);
				if (Browser.isIE()) {
					$('#' + gridId).setGridWidth(pane.width() - 2);
					$('#' + gridId).setGridHeight(pane.height() - 89);
				} else if (Browser.isChrome()) {
					$('#' + gridId).setGridWidth(pane.width() - 10);
					$('#' + gridId).setGridHeight(pane.height() - 99);
				} else if (Browser.isMozila()) {
					$('#' + gridId).setGridWidth(pane.width() - 2);
					$('#' + gridId).setGridHeight(pane.height() - 89);
				} else {
					$('#' + gridId).setGridWidth(pane.width());
					$('#' + gridId).setGridHeight(pane.height());
				}
			});
		}
	}
	// JQuery UI按钮
	$("input[type=submit], a, button", $(".btn-group")).button();
	// JQuery UI页签
	$("#tabs").tabs();
	// JQuery layout布局
	$('#container').layout({
		center : {
			closable : false,
			resizable : false,
			slidable : false,
			onresize : resizeJqGrid,
			minSize : 500,
			triggerEventsOnLoad : true
		}
	});
	
	// 列表查询
	$("#query_flow").keypress(function(e) {
		if (e.keyCode == 13) {
			$("#btn_query").trigger("click");
		}
	});
	$("#btn_query").click(function(e) {
		var queryValue = $("#query_flow").val();
		var postData = {
			"queryPrefix" : "query",
			"queryOr" : true,
			"query_LIKES_name_OR_id" : queryValue
		};
		$("#list").jqGrid("setGridParam", {
			postData : null
		});
		$("#list").jqGrid("setGridParam", {
			postData : postData,
			page : 1
		}).trigger("reloadGrid");
	});

	// 新增操作
	$("#btn_work_new").click(function() {
		if (bean.uuid == null || bean.uuid == "") {
			alert("请选择流程！");
			return true;
		}
		var uuid = bean.uuid;
		window.open(ctx + '/workflow/work/new?flowDefUuid=' + uuid, "_blank");
	});

});