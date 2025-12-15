define([ "jquery", "server", "commons", "constant", "appContext", "appModal", "multiOrg" ], function($, server,
		commons, constant, appContext, appModal, multiOrg) {
	var JDS = server.JDS;
	var UUID = commons.UUID;
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	var tplId = "pt-dms-file-attributes";

	// 检测数据选择条数
	var preCheck = function(options) {
		var selection = options.selection;
		if (selection.length == 0) {
			appModal.error("请选择一个文件（夹）！")
			return false;
		}
		if (selection.length > 1) {
			appModal.error("只能选择一个文件（夹）！")
			return false;
		}
		return true;
	}
	// 返回弹出框标题
	var getTitle = function(fileItem, options) {
		return options.dmsFileServices.isFolder(fileItem) ? "查看夹属性" : "查看文件属性";
	}
	// 判断是否夹
	var isViewFolderAttributes = function(fileItem, options) {
		return options.dmsFileServices.isFolder(fileItem);
	}
	// 编辑夹属性权限判断
	var hasEditFolderAttributesPermission = function(fileItem, options) {
		return options.dmsFileServices.hasFolderPermission(fileItem.uuid, "editFolderAttributes");
	}
	// 不能编辑夹属性禁用表单域
	var disableFormControls = function(dlgIdSelector) {
		$("input", dlgIdSelector).attr("disabled", true);
		$("select", dlgIdSelector).attr("disabled", true);
		$("button", dlgIdSelector).attr("disabled", true);
		$("textarea", dlgIdSelector).attr("disabled", true);
	}
	// 返回角色分类
	var getRoleCategories = function() {
		var categoies = [];
		JDS.call({
			service : "dmsFileManagerService.getRoleCategories",
			mask : true,
			async : false,
			success : function(result) {
				categoies = result.data;
			}
		});
		return categoies;
	}
	// 初始化角色权限分配
	var initAssignRoles = function(assignRoles, dlgIdSelector) {
		var sb = new StringBuilder();
		$.each(assignRoles, function(i, assignRole) {
			sb.appendFormat('<div class="form-group col-xs-12">');
			sb.appendFormat('<label class="col-xs-2 control-label">{0}：</label>', assignRole.roleName);
			sb.appendFormat('<div class="col-xs-10">');
			sb.appendFormat(
					'<input id="orgNames_{0}" name="orgNames" type="text" value="{1}" class="form-control"></input>',
					i, assignRole.orgNames);
			sb.appendFormat('<input id="orgIds_{0}" name="orgIds" type="hidden" value="{1}"></input>', i,
					assignRole.orgIds);
			sb.appendFormat('</div>');
			sb.appendFormat('</div>');
		});
		$(".dms-assign-roles", dlgIdSelector).html(sb.toString());
		$(".dms-assign-roles .form-group", dlgIdSelector).on("click", "input[type='text']", function() {
			var $input = $(this);
			var orgNames = $input.val();
			var orgIds = $input.next().val();
			// 人员选择
			$.unit2.open({
				initValues : orgIds,
				initLabels : orgNames,
				valueField : "",
				labelField : "",
				title : "选择人员",
				type : "all",
				multiple : true,
				selectTypes : "all",
				valueFormat : "justId",
				callback : function(values, labels) {
					$input.val(labels.join(";"));
					$input.next().val(values.join(";"));
				}
			});
		});
	}
	// 初始化文件属性
	var initAttributes = function(data, dlgIdSelector, isFolder) {
		var templateEngine = appContext.getJavaScriptTemplateEngine();
		data.isFolder = isFolder;
		var text = templateEngine.renderById(tplId, data);
		$(dlgIdSelector).html(text);
		// $("#dms_file_attributes_tabs li:eq(1) a").tab("show");
		// $("#dms_file_attributes_tabs li:eq(0) a").tab("show");
		$("#dms_file_attributes_tabs ul a", dlgIdSelector).on("click", function(e) {
			$(this).tab("show");
			e.preventDefault();
		})

		// 夹配置信息
		var folderConfiguration = data.folderConfiguration || {};

		// 夹权限
		// 继承上级夹
		$("input[name='isInheritFolderRole']", dlgIdSelector).on("change", function(e) {
			var isInheritFolderRole = $(this).val();
			if (isInheritFolderRole == "1") {
				$(".custom-folder-role", dlgIdSelector).addClass("hidden");
			} else {
				$(".custom-folder-role", dlgIdSelector).removeClass("hidden");
			}
			e.preventDefault();
		});
		var isInheritFolderRole = folderConfiguration.isInheritFolderRole;
		$("input[name='isInheritFolderRole'][value='" + isInheritFolderRole + "']", dlgIdSelector).attr("checked",
				"checked").trigger("change");
		// 归属应用
		var categories = getRoleCategories();
		$.each(categories, function(i, category) {
			var option = "<option value='" + category.value + "'>" + category.label + "</option>";
			$("select[name='appId']", dlgIdSelector).append(option);
		});
		$("select[name='appId']", dlgIdSelector).val(folderConfiguration.appId);
		$("select[name='appId']", dlgIdSelector).on("change", function(e) {
			var appId = $(this).val();
			JDS.call({
				service : "dmsFileManagerService.getRolesByCategory",
				data : [ appId ],
				mask : true,
				success : function(result) {
					var assignRoles = [];
					$.each(result.data, function(i, role) {
						assignRoles.push({
							uuid : UUID.createUUID(),
							roleUuid : role.uuid,
							roleName : role.name,
							orgNames : "",
							orgIds : "",
							permit : "Y",
							permitName : "是",
							sortOrder : i
						});
					});
					initAssignRoles(assignRoles, dlgIdSelector);
					folderConfiguration.assignRoles = assignRoles;
				}
			});
		});
		initAssignRoles(folderConfiguration.assignRoles, dlgIdSelector);

		// 其他
		// 动态表单
//		$("#formName", dlgIdSelector).val(folderConfiguration.formName);
//		$("#formUuid", dlgIdSelector).val(folderConfiguration.formUuid);
//		$("#formName", dlgIdSelector).wSelect2({
//			serviceName : "dataManagementViewerComponentService",
//			queryMethod : "getDataTypeOfDyFormSelectData",
//			selectionMethod : "getDataTypeOfDyFormSelectDataByIds",
//			labelField : "formName",
//			valueField : "formUuid",
//			defaultBlank : true,
//			width : "100%",
//			height : 250
//		});
//		// 展示视图
//		$("#listViewName", dlgIdSelector).val(folderConfiguration.listViewName);
//		$("#listViewId", dlgIdSelector).val(folderConfiguration.listViewId);
//		$("#listViewName", dlgIdSelector).wSelect2({
//			serviceName : "appWidgetDefinitionMgr",
//			labelField : "listViewName",
//			valueField : "listViewId",
//			params : {
//				wtype : "wBootstrapTable",
//				uniqueKey : "id",
//				includeWidgetRef : "false"
//			},
//			defaultBlank : true,
//			width : "100%",
//			remoteSearch : false
//		});
		// 送审批流程
		$("#approveFlowDefNames", dlgIdSelector).val(folderConfiguration.approveFlowDefNames);
		$("#approveFlowDefIds", dlgIdSelector).val(folderConfiguration.approveFlowDefIds);
		$("#approveFlowDefNames", dlgIdSelector).wSelect2({
			serviceName : "dmsFileManagerService",
			queryMethod : "getApproveFlowSelectData",
			selectionMethod : "getApproveFlowSelectDataByIds",
			labelField : "approveFlowDefNames",
			valueField : "approveFlowDefIds",
			defaultBlank : true,
			multiple : true,
			width : "100%",
			height : 250
		});
	}
	// 收集夹配置信息
	var collectFolderConfiguration = function(folderConfiguration, dlgIdSelector) {
		// 权限
		// 权限配置
		folderConfiguration.isInheritFolderRole = $("input[name='isInheritFolderRole']:checked", dlgIdSelector).val();
		// 归属应用
		folderConfiguration.appId = $("select[name='appId']", dlgIdSelector).val();
		// 分配的权限
		var assignRoles = folderConfiguration.assignRoles;
		$.each(assignRoles, function(i, assignRole) {
			assignRole.orgNames = $("#orgNames_" + i, dlgIdSelector).val();
			assignRole.orgIds = $("#orgIds_" + i, dlgIdSelector).val();
		});

		// 其他
		// 视图设置
//		var listViewId = $("input[name='listViewId']").val();
//		var listViewName = $("input[name='listViewName']").val();
//		folderConfiguration.listViewId = listViewId;
//		folderConfiguration.listViewName = listViewName;
//		// 动态表单
//		var formUuid = $("input[name='formUuid']").val();
//		var formName = $("input[name='formName']").val();
//		folderConfiguration.formUuid = formUuid;
//		folderConfiguration.formName = formName;
//		// 视图、表单设置不为空，若数据类型为空，则数据类型设置为表单
//		if (StringUtils.isNotBlank(listViewId) && StringUtils.isNotBlank(formUuid)) {
//			if (StringUtils.isBlank(folderConfiguration.dataType)) {
//				folderConfiguration.dataType = "DYFORM";
//			}
//		}
//		// 视图为空、表单设置不为空，若数据类型为空，则数据类型设置为混全类型
//		if (StringUtils.isBlank(listViewId) && StringUtils.isNotBlank(formUuid)) {
//			if (StringUtils.isBlank(folderConfiguration.dataType)) {
//				folderConfiguration.dataType = "MIXTURE";
//			}
//		}
		// 送审批流程
		folderConfiguration.approveFlowDefNames = $("input[name='approveFlowDefNames']", dlgIdSelector).val();
		folderConfiguration.approveFlowDefIds = $("input[name='approveFlowDefIds']", dlgIdSelector).val();
	}
	// 查看文件属性
	return function(options) {
		if (!preCheck(options)) {
			return;
		}
		var fileItem = options.selection[0];
		var dlgId = UUID.createUUID();
		var dlgIdSelector = '#' + dlgId;
		var title = getTitle(fileItem, options);
		var isFolder = isViewFolderAttributes(fileItem, options);
		var editFolderAttributes = hasEditFolderAttributesPermission(fileItem, options);
		var message = new StringBuilder();
		message.appendFormat('<div id="{0}" class="file-attributes-container">', dlgId);
		message.appendFormat('</div">');
		var folderConfiguration = null;
		var dialogOptions = {
			title : title,
			size : "large",
			message : message.toString(),
			shown : function() {
				JDS.call({
					service : "dmsFileManagerService.getAttributes",
					data : [ fileItem ],
					mask : true,
					success : function(result) {
						folderConfiguration = result.data.folderConfiguration;
						if (StringUtils.isBlank(folderConfiguration.folderUuid) && isFolder) {
							folderConfiguration.folderUuid = fileItem.uuid;
						}
						initAttributes(result.data, dlgIdSelector, isFolder);
						// 不能编辑夹属性禁用表单域
						if (!(isFolder && editFolderAttributes)) {
							disableFormControls(dlgIdSelector);
						}
					}
				});
			}
		};
		// 编辑夹属性权限判断
		if (isFolder && editFolderAttributes) {
			dialogOptions.buttons = {
				confirm : {
					label : "保存",
					className : "btn-primary",
					callback : function() {
						collectFolderConfiguration(folderConfiguration, dlgIdSelector);
						var isSuccess = false;
						JDS.call({
							service : "dmsFileManagerService.saveFolderConfiguration",
							data : [ folderConfiguration ],
							mask : true,
							async : false,
							success : function(result) {
								isSuccess = result.success;
								options.dmsFileServices.clearFolderConfiguration();
								appModal.success("保存成功！");
							}
						});
						return isSuccess;
					}
				}
			};
		}
		appModal.dialog(dialogOptions);
	};

});