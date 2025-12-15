//加载全局国际化资源
I18nLoader.load("/resources/pt/js/global");
// 加载动态表单定义模块国际化资源
I18nLoader.load("/resources/pt/js/dyform/commons/dyform");

$(function() {
	var form_def_list_selector = "#list";

	$(form_def_list_selector).jqGrid({
		treeGrid : true,
		treeGridModel : 'adjacency',
		ExpandColumn : 'name',
		url : contextPath + '/pt/dyform/definition/list',
		mtype : 'POST',
		datatype : 'json',
		colNames : [ 'uuid', '单据名称', '单据类型(true)', '单据类型', '编号', '表单ID', '数据库表名', '版本' ],
		colModel : [ {
			name : 'uuid',
			index : 'uuid',
			width : 150,
			hidden : true,
			key : true
		}, {
			name : 'name',
			index : 'name',
			width : 200
		}, {
			name : 'formType',
			index : 'formType',
			width : 25,
			hidden : true,
		}, {
			name : 'formTypeName',
			index : 'formTypeName',
			width : 25
		}, {
			name : 'code',
			index : 'code',
			width : 25
		}, {
			name : 'id',
			index : 'id',
			width : 50
		}, {
			name : 'tableName',
			index : 'tableName',
			width : 200
		}, {
			name : 'version',
			index : 'version',
			width : 50
		} ],
		sortname : "code",
		rowNum : 500,
		viewrecords : true,
		pager : "#pager",
		height : 'auto',
		scrollOffset : 0,
		jsonReader : {
			root : "dataList",
			total : "totalPages",
			page : "currentPage",
			records : "totalRows"
		},
		ondblClickRow : function(id) {// 双击视图打开新界面
			var uuid = id;
			if (uuid && uuid.length > 0) {
				var selectedRowData = $(form_def_list_selector).jqGrid("getRowData", id)
				FormUtils.openFormDefinition(selectedRowData["formType"], uuid);
			}
		},
		loadComplete : function(data) {
		}
	});

	// JQuery UI按钮
	$("input[type=submit], a, button", $(".btn-group")).button();

	// 新增动态表单
	$("#btn_add_mstform").click(function() {
		// openPformDefinition();
		FormUtils.openFormDefinition(FORM_TYPE.MSTFORM);
	});

	// 新增动态表单
	$("#btn_add_pform").click(function() {
		// openPformDefinition();
		FormUtils.openFormDefinition(FORM_TYPE.PFORM);
	});

	// 新增动态表单
	$("#btn_add_vform").click(function() {
		FormUtils.openFormDefinition(FORM_TYPE.VFORM);
	});

	// 新增动态表单
	$("#btn_add_mform").click(function() {
		FormUtils.openFormDefinition(FORM_TYPE.MFORM);
	});

	$("#btn_delete").click(function() {
		var uuid = $(form_def_list_selector).jqGrid('getGridParam', 'selrow');// 获取选中行的ID
		// var rowData = $(form_def_list_selector).jqGrid('getRowData', uuid);
		if (uuid && uuid.length > 0) {
			JDS.call({
				service : "dyFormFacade.dropForm",
				data : [uuid],
				success : function(result) {
					$("#btn_query").trigger("click");
					alert("刪除成功");
				},
				error : function(jqXHR){
					var faultData = JSON.parse(jqXHR.responseText);
					alert(faultData.msg);
				}
			});

		} else {
			alert(dymsg.selectRecordMod);
		}
	});

	$("#btn_open").click(function() {
		var uuid = $(form_def_list_selector).jqGrid('getGridParam', 'selrow');// 获取选中行的ID
		// var rowData = $(form_def_list_selector).jqGrid('getRowData', uuid);
		if (uuid && uuid.length > 0) {
			var url = ctx + '/pt/dyform/definition/open?formUuid=' + uuid;
			window.open(url);
		} else {
			alert(dymsg.selectRecordMod);
		}
	});


	$("#btn_copy").click(function() {
		var uuid = $(form_def_list_selector).jqGrid('getGridParam', 'selrow');// 获取选中行的ID
		if (uuid && uuid.length > 0) {
			var url = ctx + '/pt/dyform/definition/form-copy?uuid=' + uuid;
			window.open(url);
		} else {
			alert(dymsg.selectRecordMod);
		}
	});

	// 定义导出
	$("#btn_iexp").on("click", function() {
		var uuid = $(form_def_list_selector).jqGrid('getGridParam', 'selrow');
		if (uuid == null || uuid == "") {
			appModal.error('请选择记录！');
			return true;
		}
		var type = "formDefinition";
		$.iexportData["export"]({
			uuid : uuid,
			type : type
		});
	});

	// 查看依赖
	$("#btn_idep").on("click", function() {
		var uuid = $(form_def_list_selector).jqGrid('getGridParam', 'selrow');
		if (uuid == null || uuid == "") {
			appModal.error('请选择记录！');
			return true;
		}
		var type = "formDefinition";
		$.iexportData.dependencie({
			uuid : uuid,
			type : type
		});
	});
	// 定义导入
	$("#btn_iimp").on("click", function() {
		$.iexportData["import"]({
			callback : function() {
				$("#btn_query").trigger("click");
			}
		});
	});

	// 列表查询
	$("#query_keyWord").keypress(function(e) {
		if (e.keyCode == 13) {
			$("#btn_query").trigger("click");
		}
	});

	// 列表查询
	$("#form_type").change(function(e) {
		$("#btn_query").trigger("click");
	});

	// 视图查询方法
	$("#btn_query").click(function() {
		var queryValue = $("#query_keyWord").val();
		$(form_def_list_selector).jqGrid("setGridParam", {
			postData : {
				"query_EQS_formType" : $("#form_type").val(),
				"queryPrefix" : "query",
				"queryOr" : true,
				"query_LIKES_name_OR_tableName_OR_version_OR_id" : queryValue,
			},
			page : 1
		}).trigger("reloadGrid");
	});

	// JQuery layout布局变化时，更新jqGrid高度与宽度
	function resizeJqGrid(position, pane, paneState) {
		if (grid = $('.ui-jqgrid-btable:visible')) {
			grid.each(function(index) {
				var paneWidth = pane.width();
				var paneHeight = pane.height() - 25;
				if (Browser.isIE()) {// 检测是否是IE浏览器
					$(this).setGridWidth(paneWidth - 22);
					$(this).setGridHeight(paneHeight - 84);
				} else if (Browser.isChrome()) {// 检测是否是chrome浏览器
					$(this).setGridWidth(paneWidth - 30);
					$(this).setGridHeight(paneHeight - 114);
				} else if (Browser.isMozila()) {// 检测是否是Firefox浏览器
					$(this).setGridWidth(paneWidth - 22);
					$(this).setGridHeight(paneHeight - 84);
				} else {
					$(this).setGridWidth(paneWidth);
					$(this).setGridHeight(pane.height());
				}
			});
		}
	}
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

});
