/**
 * dyform_common_field.jsp对应的js
 */
$(function() {
	var bean = {
		"uuid" : null,
		"creator" : null,
		"createTime" : null,
		"modifier" : null,
		"modifyTime" : null,
		"scope" : null,
		"moduleId" : null,
		"moduleName" : null,
		"displayName" : null,
		"name" : null,
		"definitionJson" : null,
		"controlType" : null,
		"controlTypeName" : null,
		"categoryUuid" : null,
		"categoryName" : null,
		"notes" : null
	};

	var strArray = new Array();
	var plugins = getControlPlugins();
	strArray.push('<option value=""></option>');
	for (var i = 0; i < plugins.length; i++) {
		var code = plugins[i].code;
		var name = plugins[i].name;
		strArray.push('<option value="' + code + '">' + name + '</option>');
	}

	var controlTypeOptions = strArray.join("");// String.prototype.concat.apply("",
	// strArray);
	$("#controlType").append(controlTypeOptions);

	$("#list").jqGrid($.extend($.common.jqGrid.settings, {
		url : ctx + "/common/jqgrid/query?serviceName=formCommonFieldDefinitionService",
		datatype : 'json',
		mtype : "POST",
		colNames : [ "uuid", "字段名称", "控件类型", "编码", "所属模块", "关联表单" ],
		colModel : [ {
			name : "uuid",
			index : "uuid",
			width : "180",
			hidden : true,
			key : true
		}, {
			name : "displayName",
			index : "displayName",
			width : "40",
		}, {
			name : "controlTypeName",
			index : "controlTypeName",
			width : "30",
			formatter : function(celval, options, rowdata) {
				return celval;
			}
		}, {
			name : "name",
			index : "name",
			width : "30"
		}, {
			name : "moduleName",
			index : "moduleName",
			width : "30"
		}, {
			name : "scope",
			index : "scope",
			width : "30"
		} ],
		rowNum : 20,
		rownumbers : true,
		rowList : [ 10, 20, 50, 100, 200 ],
		rowId : "uuid",
		pager : "#pager",
		sortname : "name",
		recordpos : "right",
		viewrecords : true,
		sortable : true,
		sortorder : "asc",
		multiselect : true,
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
			getModuleById(id);
		},
		loadComplete : function(data) {
			$("#list").setSelection($("#list").getDataIDs()[0]);
		}
	}));

	// 根据模块ID获取模块信息
	function getModuleById(uuid) {
		$.ajax({
			type : "POST",
			url : ctx + "/pt/dyform/field/get",
			data : uuid,
			contentType : "application/json",
			dataType : "json",
			success : function(result) {
				bean = result.data.bean;
				$("#btn_prev").trigger("click");// 返回
				showDelButton();
				$("#main-tabs-1").json2form(bean);
                $("#categoryUuid").trigger("change");
				$("#controlType").trigger("change").select2("disable");
				// $("#moduleName").wCommonComboTree("setValue", bean.moduleId, true);
				$("#moduleId").val(bean.moduleId).trigger("change");
				// 显示
				var list = result.data.list;
				// ----------
				var str = '';
				if (list.length > 0) {
					str += '<div class="border_bottom">';
					str += '<div class="font_div_title">';
					str += '<div>关联表单（共' + list.length + '条）</div>';
					str += '</div>';
					str += '</div>';
					// ------添加具体内容;最后一行不加下边框
					for (var i = 0; i < list.length; i++) {
						var displayName = list[i].displayName;
						if (i == list.length - 1) {// 最后一个不加下边框
							str += '<div>';
						} else {
							str += '<div class="border_bottom">';
						}
						str += '<div class="font_div" data-form-uuid="'+list[i].formUuid+'">';
						str += displayName;
						str += '</div>';
						str += '</div>';
					}
				} else {
					str += '<div>';
					str += '<div class="font_div_title">';
					str += '<div>关联表单（共0条）</div>';
					str += '</div>';
					str += '</div>';
				}
				str += '';
				$("#main-tabs>.border_div").html(str);
				// TODO 打开表单
			},
			error : function(result) {
			}
		});
	}

	// 跳转表单详情
	$("#main-tabs>.border_div").on("click", "div[data-form-uuid]", function(event){
		window.open(ctx + "/pt/dyform/definition/form-designer?uuid=" + $(this).attr("data-form-uuid"), "_target");
	});

	// JQuery UI按钮
	$("input[type=submit], a, button", $(".btn-group")).button();
	// JQuery UI页签
	$("#main-tabs").tabs();

	// 新增
	$("#btn_add").click(function() {
		$("#btn_prev").trigger("click");// 返回
		$("#main-tabs-1").clearForm(true);
		$("#moduleId").val("").trigger("change");
		$("#categoryUuid").val("").trigger("change");
		$("#controlType").val("").trigger("change").select2("enable");
		hideDelButton();
	});
	// 隐藏删除按钮
	function hideDelButton() {
		$("#btn_del").hide();
	}
	// 显示删除按钮
	function showDelButton() {
		$("#btn_del").show();
	}

	// 单项删除
	$("#btn_del").click(function() {
		if ($(this).attr("id") == "btn_del") {
			var ids = $("#list").jqGrid('getGridParam', 'selrow');
			if (ids.length == 0) {
        appModal.error('请选择记录！');
				return true;
			}
		} else {
			if (bean.id == "" || bean.id == null) {
				appModal.error('请选择记录！');
				return true;
			}
		}
		var name = bean.displayName;// 字段名称
		if(bean.scope > 0){
			return alert("字段被引用，无法删除");
		} else if (confirm("确定要删除页面元素[" + name + "]吗？")) {
			$.ajax({
				type : "POST",
				url : ctx + "/pt/dyform/field/delete",
				data : JSON.stringify(bean),
				contentType : "application/json",
				dataType : "text",
				success : function(result) {
					alert("删除成功!");
					// 删除成功刷新列表
					$("#list").trigger("reloadGrid");
				},
				error : function(result) {
				}
			});

		}
	});

	// 批量删除
	$("#btn_delAll").click(function() {
		var rowids = $("#list").jqGrid('getGridParam', 'selarrrow');
		if (rowids.length == 0) {
			appModal.error('请选择记录！');
			return;
		}else {
			for(var i = 0; i < rowids.length; i++){
				var rowData = $("#list").jqGrid("getRowData", rowids[i]);
				if(rowData.scope > 0){
					return alert("字段[" + rowData.displayName + "]被引用，无法删除！");
				}
			}
		}
		var ids = rowids.join(",");
		if (confirm("确定要删除所选资源？")) {
			$.ajax({
				type : "POST",
				async : false,
				url : ctx + "/pt/dyform/field/deleteAll",
				data : {
					"ids" : ids
				},
				success : function(result) {
					alert("删除成功！");
					// 删除成功刷新列表
					$("#list").trigger("reloadGrid");
				},
				error : function(result) {
				}
			});
		}
	});

	// 查询方法
	function queryFunction() {
		var queryValue = $("#query_keyWord").val();
		$("#list").jqGrid("setGridParam", {
			postData : {
				"queryPrefix" : "query",
				"queryOr" : false,
				"query_LIKES_name_OR_displayName_OR_controlTypeName_OR_moduleName" : queryValue,
			},
			page : 1
		}).trigger("reloadGrid");
	}

	// 点击查询
	$("#btn_query").click(function() {
		queryFunction();
	});
	$("#query_keyWord").keypress(function(event) {
		var code = event.keyCode;
		if (code == 13) {
			queryFunction();
		}
	});

	Layout.layout({
		west__size : 480
	});
	var pluginBase = ctx + "/resources/pt/js/dyform/definition/ckeditor/plugins";
	CKEDITOR.plugins.basePath = pluginBase + "/";// 自定义ckeditor的插件路径
	var editor = {};
	var $fieldContainer = $("#field-container");
	window.formDefinition = window.formDefinition || $.extend({
		fields : {},
		subforms : {}
	}, formDefinitionMethod);
	$.extend(editor, {
		focusedDom : $fieldContainer
	});

	function fieldExists(uuid, moduleId, categoryUuid, fieldName, fieldValue){
		var fieldExists = true;
		JDS.call({
			service : "formCommonFieldDefinitionService.fieldExists",
			data : [ uuid, moduleId, categoryUuid, fieldName, fieldValue],
			async : false,
			success : function(result) {
				if (result.success) {
					fieldExists = result.data;
				}
			}
		});
		if (fieldExists === true) {
			alert("字段名重复，请重新设置");
			return false;
		}
	}

	$("#btn_prev").on("click", function(event){
		$("#field-property").hide();
		$("#main-tabs").show();
	});
	$("#btn_next").on("click", function(event){
		$("#main-tabs-1").form2json(bean);
		if (bean.moduleId == '') {
			alert('请选择所属模块')
			return;
		} else if (bean.categoryUuid == '') {
			alert('请选择所属分类')
			return;
		} else if (bean.controlType == '') {
			alert('请选择控件类型')
			return;
		}
		$("#main-tabs").hide();
		$("#field-property").show();
		if(bean.name && bean.definitionJson){
			editor.focusedDom = $fieldContainer;
			var field = $.parseJSON(bean.definitionJson);
			if(bean.controlType === CkPlugin.SUBFORM){
				formDefinition.subforms[bean.name] = field;
				formDefinition.subforms[field.formUuid] = field;
				$fieldContainer.attr("formUuid", field.formUuid);
			}else {
				$fieldContainer.attr("name", bean.name);
				formDefinition.fields[bean.name] = field;
			}
		}else {
			if(bean.controlType === CkPlugin.SUBFORM){
				formDefinition.subforms = {};
			}else {
				formDefinition.fields = {};
			}
		}
		$.ajax({
			type : "get",
			dataType : "text",
			url : pluginBase + "/" + bean.controlType + "/plugin.js?t=" + (new Date()).getTime(),
			success : function(data){
				var oAddPlugin = window.addPlugin;
				try{
					$fieldContainer.html("<div id=\"container_"+bean.controlType+"\"></div");
					window.addPlugin = function(pluginName, pluginToolHint, dialogTitle, controlConfig){
						var dialog = getDialog(pluginName, pluginToolHint, dialogTitle, controlConfig)(editor);
						dialog.onShow({
							callback : function(data){
								// 设置字段不可编辑
								if(bean.name && bean.definitionJson){
									$fieldContainer.find("input#name").attr("readonly", "readonly");
									$fieldContainer.find("select#dbDataType").attr("disabled", "disabled");
								}
							}
						});
						$("#btn_save").off("click").on("click", function(event){
							formDefinition.fields = {};
							formDefinition.subforms = {};
							if(bean.name && bean.definitionJson){
								$fieldContainer.find("select#dbDataType").removeAttr("disabled");
							}
							if(dialog.onOk() === false){
								if(bean.name && bean.definitionJson){
									$fieldContainer.find("select#dbDataType").attr("disabled", "disabled");
								}
								return;
							}
							if(pluginName === CkPlugin.SUBFORM){
								for(var fieldName in formDefinition.subforms){
									var field = formDefinition.subforms[fieldName];
									bean.name = field.name;
									bean.displayName = field.displayName;
									bean.definitionJson = JSON.stringify(field);
								}
							}else {
								for(var fieldName in formDefinition.fields){
									var field = formDefinition.fields[fieldName];
									bean.name = field.name;
									bean.displayName = field.displayName;
									bean.definitionJson = JSON.stringify(field);
								}
							}
							if(fieldExists(bean.uuid, bean.moduleId, bean.categoryUuid, "displayName", bean.displayName) === false){
								if(bean.name && bean.definitionJson){
									$fieldContainer.find("select#dbDataType").attr("disabled", "disabled");
								}
								return;
							}
							$.ajax({
								type : "POST",
								url : ctx + "/pt/dyform/field/save",
								data : JSON.stringify(bean),
								contentType : "application/json",
								dataType : "text",
								success : function(result) {
									// 保存成功刷新列表
									alert("保存成功");
									$("#list").trigger("reloadGrid");
									showDelButton();// 显示
								},
								error : function(result) {
								}
							});
						});
					};
					$.globalEval(data);
				}finally{
					window.addPlugin = oAddPlugin;
				}
			}
		});
	});
	// 控件类型
	$("#controlType").wSelect2({
		valueField : "controlType",
		labelField : "controlTypeName",
		width : "100%"
	});

    // 所属分类
    $("#categoryUuid").wSelect2({
        params : function() {
            return {
                params : {
                    moduleId : $("#moduleId").val()
                }
            };
        },
        multiple : false,
        enableAdd : true,
        enableDel : true,
        enableUpdate : true,
        serviceName : "formCommonFieldCategoryService",
        queryMethod : "loadSelectData",
        valueField : "categoryUuid",
        labelField : "categoryName",
        defaultBlank : true,
        width : "100%",
        height : 250,
        onSelect2delSuccess : function(data) {
            return alert("删除成功");
        },
        onSelect2del : function(event, callback) {
            var isInRef = true;
            JDS.call({
                service : "formCommonFieldCategoryService.isInRef",
                data : [ event.val ],
                async : false,
                success : function(result) {
                    if (result.success) {
                        isInRef = result.data;
                    }
                }
            });
            if (isInRef === true) {
                alert("无法删除，该分类已关联字段信息");
                return false;
            }
        },
        onSelect2add : function(event, callback) {
            if ($.trim(event.val).length > 15) {
                alert("分类名称不能超过15个汉字");
                return false;
            }
            if (event && event.object) {
                event.object.moduleId = $("#moduleId").val();
            }
        }
    });

	// 所属业务模块
	$("#moduleId").wSelect2({
		labelField: "moduleName",
		valueField: "moduleId",
		remoteSearch: false,
		serviceName: "appModuleMgr",
		queryMethod: "loadSelectData",
		params: {
			//excludeIds:$("#moduleId").val(),
			//systemUnitId:server.SpringSecurityUtils.getCurrentUserUnitId()
		}
	});
	// 所属模块
	/*
	$("#moduleName").wCommonComboTree({
		service : "appProductIntegrationMgr.getTreeByDataType2",
		serviceParams : [ [ "1", "2", "3" ], "全局" ],
		height : "280px",
		multiSelect : false, // 是否多选
		parentSelect : true, // 父节点选择有效，默认无效
		onAfterSetValue : function(event, self, value) {
			var valueNodes = self.options.valueNodes;
			var appName = [], appPath = [];
			if (valueNodes && valueNodes.length > 0) {
				appPath.push(valueNodes[0].id);
				appName.push(valueNodes[0].name);
			}
			$("#moduleId").val(appPath.join(","));
			$("#moduleName").val(appName.join(","));
		}
	});
	*/
});
