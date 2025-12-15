(function($) {
	//树形下拉框获取指定类别的所有消息格式树
	$.messageTemplateTree = {
			open : function(options) {
				options = $.extend({
					labelField : null,
					valueField : null,
					category:null,
					width: null,
					height: null
				},options);
			
			var setting = {
					async : {
						otherParam : {
							"serviceName" : "messageTemplateService",
							"methodName" : "getAllMessageTemplate",
							"data":options.category
						}
					},
					check : {
						enable : false,
						chkStyle : "radio"
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
				height: options.height
			});
		}
	};
})(jQuery);




