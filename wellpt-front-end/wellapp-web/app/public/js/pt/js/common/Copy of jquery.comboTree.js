(function($) {
	var ComboTree = function(element, options) {
		this.$element = $(element);
		this.options = options;
		this.init();
	};
	ComboTree.prototype = {
		constructor : ComboTree,
		init : function() {
			var $element = this.$element;
			var treeId = $element.attr("id") + "_" + "ztree";
			var treeDivId = "content" + "_" + treeId;
			var divWidth = this.options.width || $element.width();
			var divHeight = this.options.height || 200;
			var treeDiv = "<div id='"
					+ treeDivId
					+ "' style='display: none; position: absolute; z-index: 9999; background-color: #fff; overflow: scroll; border: 1px solid #c5dbec;'>"
					+ "<ul id='" + treeId
					+ "' class='ztree' style='margin-top: 0; width: "
					+ divWidth + "px; height: " + divHeight + "px;'></ul>"
					+ "</div>";
			this.treeId = treeId;
			this.treeDivId = treeDivId;
			$(treeDiv).insertAfter($element);

			this.$element.click($.proxy(this._showComboTree, this));
		},
		_showComboTree:function(){
			var $element = this.$element;
			$element.treeInitialized = false;
			var options = this.options;
			var treeId = this.treeId;
			var treeDivId = this.treeDivId;
//			if(!window.ctx){
//				window.ctx = "/" + window.location.pathname.split("/")[1];
//			}
			// 获取cookie
			function getCookie(name) {
				var arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
				if (arr) {
					return decodeURI(arr[2]);
				} else {
					return null;
				}
			}
			// 确保contextPath有值
			function getContextPath() {
				if (window.ctx == null || window.contextPath == null) {
					window.contextPath = getCookie("ctx");
					window.contextPath = window.contextPath == "\"\"" ? "" : window.contextPath;
					window.ctx = window.contextPath;
				}
				return window.ctx;
			}
			getContextPath();
			var ztreeSetting = {
				view : {
					showLine : false
				},
				async : {
					enable : true,
					contentType : "application/json",
					url : ctx + "/json/data/services",
					otherParam : {
						"serviceName" : "dataDictionaryService",
						"methodName" : "getAsTreeAsync"
					},
					type : "POST"
				},
				check : {
					enable : true
				},
				callback : {
					onClick : onClick,
					onCheck : zTreeOnCheck,
					onExpand : zTreeOnExpand,
					onAsyncSuccess : onAsyncSuccess
				}
			};
			setting = $.extend(true, ztreeSetting, options.treeSetting || {});
			// 展开树
			function zTreeOnExpand(event, treeId, treeNode) {
				if (options.valueField != null && options.valueField != "") {
					// 设置值
					var zTree = $.fn.zTree.getZTreeObj(treeId);
					var nodes = zTree.getNodes();
					var value = $("#" + options.valueField).val();
					if (value != null && value != "") {
						var values = value.split(";");
						for ( var i = 0; i < nodes.length; i++) {
							var node = nodes[i];
							for ( var j = 0; j < values.length; j++) {
								checkNodeByData(zTree, node, values[j]);
							}
						}
					}
				}
			}
			function checkNodeByData(zTree, node, value) {
				if (value == node.data) {
					zTree.checkNode(node, true);
				}
				var children = node.children;
				for ( var i = 0; i < children.length; i++) {
					checkNodeByData(zTree, children[i], value);
				}
			}
			// 选中树
			function zTreeOnCheck(event, treeId, treeNode) {
				if (treeNode.isParent) {
					if (options.labelField != null) {
						$("#" + options.labelField).val("");
					}
					if (options.valueField != null) {
						$("#" + options.valueField).val("");
					}
					return;
				}
				// 设置值
				var zTree = $.fn.zTree.getZTreeObj(treeId);
				var checkNodes = zTree.getCheckedNodes(true);
				var path = "";
				var value = "";
				for ( var index = 0; index < checkNodes.length; index++) {
					var checkNode = checkNodes[index];
					if (options.labelField != null) {
						if (path == "") {
							path = getAbsolutePath(checkNode);
						} else {
							path = path + ";" + getAbsolutePath(checkNode);
						}
					}
					if (options.valueField != null) {
						if (value == "") {
							value = checkNode.data;
						} else {
							value = value + ";" + checkNode.data;
						}
					}
				}
				$("#" + options.labelField).val(path);
				$("#" + options.valueField).val(value);
			}
			// 点击树
			function onClick(event, treeId, treeNode) {
				if (treeNode.isParent) {
					if (options.labelField != null) {
						$("#" + options.labelField).val("");
					}
					if (options.valueField != null) {
						$("#" + options.valueField).val("");
					}
					return;
				}
				if (options.labelField != null) {
					var path = getAbsolutePath(treeNode);
					$("#" + options.labelField).val(path);
				}
				if (options.valueField != null) {
					$("#" + options.valueField).val(treeNode.data);
				}
			}
			function onAsyncSuccess(event, treeId, treeNode, msg) {
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

			if (this.treeInitialized !== true) {
				$.fn.zTree.init($("#" + treeId), setting);
				this.treeInitialized = true;
			}
			var comboTree = this;
			comboTree.show();
			
			$(document).mousedown(
					function(event) {
						var id = event.target.id;
						if ((id != null) && (typeof id == "string") && (id.lastIndexOf(treeId) == -1)
								&& (id != treeDivId)
								&& (id != $element.attr("id"))) {
							comboTree.hide();
						}
					});
		},
		show : function() {
			var treeDivId = this.treeDivId;
			var $element = this.$element;
			var outerHeight = $element.outerHeight();
			var offset = $element.position();
			$("#" + treeDivId).css({
				left : offset.left + "px",
				top : offset.top + outerHeight + "px"
			}).slideDown("fast");
		},
		hide : function() {
			var treeDivId = this.treeDivId;
			$("#" + treeDivId).fadeOut("fast");
		},
		initValue : function(value) {
			var options = this.options;
			var $element = this.$element;
			var cacheValueKey = "DY_FORM_FIELD_MAPPING" + "_" + value;
			if ($element.data[cacheValueKey]) {
				if (options.labelField != null) {
					$("#" + options.labelField).val(
							$element.data[cacheValueKey + "_label"]);
				}
				if (options.valueField != null) {
					$("#" + options.valueField).val(
							$element.data[cacheValueKey]);
				}
				return;
			}
			if (value != null && value != "") {
				JDS
						.call({
							service : "dataDictionaryService.getKeyValuePair",
							data : [ "DY_FORM_FIELD_MAPPING", value ],
							success : function(result) {
								if (result.data) {
									if (options.labelField != null) {
										$("#" + options.labelField).val(
												result.data.label);
									}
									if (options.valueField != null) {
										$("#" + options.valueField).val(
												result.data.value);
									}
									$element.data[cacheValueKey + "_label"] = result.data.label;
									$element.data[cacheValueKey] = result.data.value;
								}
							}
						});
			} else {
				if (options.labelField != null) {
					$("#" + options.labelField).val("");
				}
				if (options.valueField != null) {
					$("#" + options.valueField).val("");
				}
			}
		}
	};
	$.fn.comboTree = function(option) {
		var method = false;
		var args = null;
		if (arguments.length == 2) {
			method = true;
			args = arguments[1];
		}
		return this.each(function() {
			var $this = $(this), data = $this.data("comboTree"), options = $
					.extend({}, $this.data(), typeof option == 'object'
							&& option);
			if (!data) {
				$this.data('comboTree', (data = new ComboTree(this, options)));
			}
			if (typeof option == 'string') {
				if (method == true && typeof args == 'string') {
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