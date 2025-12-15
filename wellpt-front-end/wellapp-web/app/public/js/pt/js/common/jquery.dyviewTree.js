(function($) {
	$.dyviewTree = {
		open : function(options) {
			options = $.extend({
				labelField : null,
				valueField : null,
				width: 220,
				height: 220
			},options);
			var setting = {
					async : {
						otherParam : {
							"serviceName" : "viewDefinitionNewService",
							"methodName" : "getViewAsTreeAsync",
						}
					},
					check : {
						enable : true,
						chkStyle : "radio"
					},
			};
			
			$("#"+options.labelField).comboTree({
				labelField: options.labelField,
				valueField: options.valueField,
				treeSetting : setting,
				width: options.width,
				height: options.height
			});
		}
	};
})(jQuery);