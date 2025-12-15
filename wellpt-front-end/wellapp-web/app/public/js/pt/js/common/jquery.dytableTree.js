;
(function($) {
	$.dytableTree = {
		open : function(options) {
			options = $.extend({
				labelField : null,
				valueField : null,
				moduleId:null
			},options);
			var setting = {
					async : {
						otherParam : {
							"serviceName" : "getViewDataService",
							"methodName" : "getDyForms",
							"data":options.moduleId
						}
					},
					check : {
						enable : true
					},
					callback : {
//						onClick: treeNodeOnClick,
//						onCheck:treeNodeOnCheck
					}
					
			};
			 
			$("#"+options.labelField).comboTree({
				labelField: options.labelField,
				valueField: options.valueField,
				treeSetting : setting,
				initService : "getViewDataService.getKeyValuePair",
				initServiceParam : [options.moduleId],
				width: 220,
				height: 220
			});
			
			function treeNodeOnClick(event, treeId, treeNode) {
					$("#"+options.labelField).val(treeNode.data.descname);
					$("#"+options.valueField).val(treeNode.data.uuid);
//					alert(treeNode.data.descname);
//					alert(treeNode.data.uuid);
			}
			
			function treeNodeOnCheck(event, treeId, treeNode) {
				// 设置值
				var zTree = $.fn.zTree.getZTreeObj(treeId);
				var checkNodes = zTree.getCheckedNodes(true);
//				alert(JSON.stringify(checkNodes));
				var path = "";
				var value = "";
				for ( var index = 0; index < checkNodes.length; index++) {
					var checkNode = checkNodes[index];
						if (path == "") {
							path = getAbsolutePath(checkNode);
						} else {
							path = path + ";" + getAbsolutePath(checkNode);
						}
						if (value == "") {
							value = checkNode.id;
						} else {
							value = value + ";" + checkNode.id;
						}
				}
//				alert(path);
//				alert(value);
				$("#"+options.labelField).val(path);
				$("#"+options.valueField).val(value);
			}
			
			// 获取树结点的绝对路径
			function getAbsolutePath(treeNode) {
				var path = treeNode.name;
				var parentNode = treeNode.getParentNode();
				while (parentNode != null) {
					path = parentNode.name + "/" + path;
					parentNode = parentNode.getParentNode();
				}
				return path;
			}
		}
	};
})(jQuery);