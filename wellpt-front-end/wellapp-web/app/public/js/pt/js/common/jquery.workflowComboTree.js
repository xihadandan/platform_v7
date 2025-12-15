(function($) {
	var WorkflowComboTree = function(element, options) {
		this.$element = $(element);
		this.options = options;
		this.init();
	};
	WorkflowComboTree.prototype = {
		constructor : WorkflowComboTree,
		init : function() {
			var options = $.extend({
				labelField : null,
				valueField : null,
				width : 220,
				height : 220
			}, this.options);

			var setting = {
				async : {
					otherParam : {
						"serviceName" : "flowSchemeService",
						"methodName" : "getFlowTree",
						"data" : [ -1 ]
					}
				},
				check : {
					enable : true,
					chkStyle : "radio"
				},
				callback : {
				// onClick : treeNodeOnClick,
				}

			};

			$("#" + options.labelField).comboTree({
				labelField : options.labelField,
				valueField : options.valueField,
				treeSetting : setting,
				initService : "flowSchemeService.getFlowKeyValuePair",
				initServiceParam : [ "workflow" ],
				width : options.width,
				height : options.height
			});

			function treeNodeOnClick(event, treeId, treeNode) {
				if (!treeNode.isParent) {
					var parentNode = treeNode.getParentNode();
					if (parentNode) {
						$("#" + options.labelField).val(
								parentNode.name + "/" + treeNode.name);
					} else {
						$("#" + options.labelField).val(treeNode.name);
					}
					$("#" + options.valueField).val(treeNode.data);
				} else {
					$("#" + options.labelField).val("");
					$("#" + options.valueField).val("");
				}
			}
		}
	};
	$.fn.workflowComboTree = function(option) {
		var method = false;
		var args = null;
		if (arguments.length == 2) {
			method = true;
			args = arguments[1];
		}
		return this
				.each(function() {
					var $this = $(this), data = $this.data("workflowComboTree"), options = $
							.extend({}, $this.data(), typeof option == 'object'
									&& option);
					if (!data) {
						$this.data('workflowComboTree',
								(data = new WorkflowComboTree(this, options)));
					}
					if (typeof option == 'string') {
						if (method == true && args != null) {
							data[option](args);
						} else {
							data[option]();
						}
					} else if (options.show) {
						data.show();
					}
				});
	};
})(jQuery);