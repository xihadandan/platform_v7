(function($) {
	//树形下拉框获取所有的文件夹树
	$.folderTree = {
			open : function(options) {
				options = $.extend({
					labelField : null,
					valueField : null,
					width: null,
					height: null
				},options);
			
			var setting = {
					async : {
						otherParam : {
							"serviceName" : "folderManagerService",
							"methodName" : "getFolderTreeWithIcon",
						}
					},
					check : {
						enable : false,
					},
					callback : {
						onClick:function (event, treeId, treeNode) {
							$(  "#"+options.labelField  ).val(treeNode.name);
							$(  "#"+options.valueField  ).val(treeNode.id);
						},
					}
			};
			
			 $( "#"+options.labelField ).comboTree({
				 autoInitValue : true,
				 labelField: options.labelField,
				 valueField: options.valueField,
				 treeSetting : setting,
				 width: options.width,
				height: options.height,
				initService : "folderManagerService.getFolderNameByUuid",
				initServiceParam : []
			});
			}
	};
})(jQuery);

//获取具体的文件夹树(包含文档)
function getSpecificFolderTree(libId){
	// JQuery zTree设置
	var dataFolderSetting1 = {
			async : {
				enable : true,
				contentType : "application/json",
				url : ctx + "/json/data/services",
				otherParam : {
					"serviceName" : "folderManagerService",
					"methodName" : "getSpecificFolderTree",
					"data":[libId]
				},
				type : "POST"
			},
			callback : {
				beforeClick : beforeClick1,
				onAsyncSuccess : choseIcon
			}
	};
	// 树结点点击处理
	function beforeClick1(treeId, treeNode) {
		// 最新选择的树结点
		latestSelectedNode = treeNode;
		if (treeNode.id != null && treeNode.id != -1) {
			// 查看详细
			$("#folderTreeValue").val(treeNode.id);
		}
		return true;
	}
	$.fn.zTree.init($("#folder_tree"), dataFolderSetting1);
}

//获取具体的文件夹树(不包含文档，最后一层夹)
function getSpecificFolderTreeWithoutFile(libId){
	// JQuery zTree设置
	var dataFolderSetting2 = {
			async : {
				enable : true,
				contentType : "application/json",
				url : ctx + "/json/data/services",
				otherParam : {
					"serviceName" : "folderManagerService",
					"methodName" : "getSpecificFolderTreeWithoutFile",
					"data":[libId]
				},
				type : "POST"
			},
			callback : {
				beforeClick : beforeClick2,
				onAsyncSuccess : choseIcon
			}
	};
	// 树结点点击处理
	function beforeClick2(treeId, treeNode) {
		// 最新选择的树结点
		latestSelectedNode = treeNode;
		if (treeNode.id != null && treeNode.id != -1) {
			// 查看详细
			$("#folderTreeValue").val(treeNode.id);
		}
		return true;
	}
	$.fn.zTree.init($("#folder_tree"), dataFolderSetting2);
}

//获取具体的文件夹树(不包含文档，非最后一层夹)
function getSpecificFolderTreeWithoutFileAndLastFolder(libId){
	// JQuery zTree设置
	var dataFolderSetting3 = {
		async : {
			enable : true,
			contentType : "application/json",
			url : ctx + "/json/data/services",
			otherParam : {
				"serviceName" : "folderManagerService",
				"methodName" : "getSpecificFolderTreeWithoutFileAndLastFolder",
				 "data":[libId]
			},
			type : "POST"
		},
		callback : {
			beforeClick : beforeClick3,
			onAsyncSuccess : choseIcon
		}
	};
	// 树结点点击处理
	function beforeClick3(treeId, treeNode) {
		// 最新选择的树结点
		latestSelectedNode = treeNode;
		if (treeNode.id != null && treeNode.id != -1) {
			// 查看详细
			$("#folderTreeValue").val(treeNode.id);
		}
		return true;
	}
	$.fn.zTree.init($("#folder_tree"), dataFolderSetting3);
}

function choseIcon(){
	var closeFCount = 1;
	var openFCount = 1;
	var fileCount = 1;
	$(".node_folder_ico_close").each(function(){
		if(closeFCount%6==1){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -7px -4px transparent');
		}else if(closeFCount%6==2){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -7px -26px transparent');
		}else if(closeFCount%6==3){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -7px -47px transparent');
		}else if(closeFCount%6==4){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -7px -70px transparent');
		}else if(closeFCount%6==5){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -7px -93px transparent');
		}else if(closeFCount%6==0){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -7px -112px transparent');
		}
		closeFCount++;
	});
	$(".node_folder_ico_open").each(function(){
		if(openFCount%6==0){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -41px -4px transparent');
		}else if(openFCount%6==5){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -41px -26px transparent');
		}else if(openFCount%6==4){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -41px -47px transparent');
		}else if(openFCount%6==3){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -41px -70px transparent');
		}else if(openFCount%6==2){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -41px -93px transparent');
		}else if(openFCount%6==1){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -41px -112px transparent');
		}
		openFCount++;
	});
	$(".node_file_ico_docu").each(function(){
		if(fileCount%6==1){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -71px -4px transparent');
		}else if(fileCount%6==2){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -71px -26px transparent');
		}else if(fileCount%6==3){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -71px -47px transparent');
		}else if(fileCount%6==4){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -71px -70px transparent');
		}else if(fileCount%6==5){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -71px -93px transparent');
		}else if(fileCount%6==0){
			$(this).parent().prev().css("background",'url("'+ctx+'/resources/theme/images/v1_file_icon.png") no-repeat scroll -71px -112px transparent');
		}
		fileCount++;
	});
}

