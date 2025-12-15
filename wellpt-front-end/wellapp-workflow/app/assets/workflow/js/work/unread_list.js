//加载全局国际化资源
I18nLoader.load("/resources/pt/js/global");
$(function() {
	var bean = {
		"flowInstUuid" : null,
		"title" : null,
		"endTime" : null,
		"flowName" : null
	};
	$("#list").jqGrid({
		url : ctx + '/common/jqgrid/query?serviceName=workUnreadService',
		mtype : 'POST',
		datatype : "json",
		colNames : [ "flowInstUuid", "标题", "流程" ],
		colModel : [ {
			name : "flowInstUuid",
			index : "flowInstUuid",
			width : "180",
			hidden : true,
		}, {
			name : "title",
			index : "title",
			width : "180"
		}, {
			name : "flowName",
			index : "flowName",
			width : "180"
		} ],
		rowNum : 20,
		rownumbers : true,
		rowList : [ 10, 20, 50, 100, 200 ],
		rowId : "flowInstUuid",
		pager : "#pager",
		sortname : "endTime",
		recordpos : "right",
		viewrecords : true,
		sortable : true,
		sortorder : "desc",
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
			bean.flowInstUuid = rowData.flowInstUuid;
			bean.title = rowData.title;
			bean.endTime = rowData.endTime;
			bean.flowName = rowData.flowName;
		},
		ondblClickRow : function(id) {
			var rowData = $(this).getRowData(id);
			if (rowData.flowInstUuid != null && rowData.flowInstUuid != "") {
				window.open(ctx + "/workflow/work/view/unread?flowInstUuid=" + rowData["flowInstUuid"]);
			}
		},
		afterInsertRow : function(rowid, rowData) {
			$(this).jqGrid("setRowData", rowid, false, "unread");
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

	// 查看待办工作
	$("#btn_todo_view").click(function() {
		if (bean.flowInstUuid == null || bean.flowInstUuid == "") {
			alert("请选择工作！");
			return true;
		}
		window.open(ctx + "/workflow/work/view/unread?flowInstUuid=" + bean["flowInstUuid"], "_blank");
	});

});