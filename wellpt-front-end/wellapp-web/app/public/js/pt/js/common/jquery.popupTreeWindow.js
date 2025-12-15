;
(function($) {
	/*
	 * POPUPWINDOW CLASS DEFINITION ======================
	 */
	var PopupTreeWindow = function(element, options) {
		this.init("popupTreeWindow", element, options);
	};

	PopupTreeWindow.prototype = {
		constructor : PopupTreeWindow,
		init : function(type, element, options) {
			this.$element = $(element);
			var $element = this.$element;
			this.options = this.getOptions(options);
			var options = this.options;
			var dialogId = $element.attr("id") + "_" + "dialog";
			var treeId = $element.attr("id") + "_" + "ztree";
			var treeDiv = "<div id='" + dialogId + "'>" + "<ul id='" + treeId
					+ "' class='ztree' style='margin-top: 0;'></ul>" + "</div>";
			this.dialogId = dialogId;
			this.treeId = treeId;
			$(treeDiv).insertAfter($element);

			var $this = this;
			this.$element.focus($.proxy(this.open, $this));

			// 初始化窗口
			$("#" + this.dialogId).dialog({
				title : options.title,
				resizable : options.resizable,
				autoOpen : options.autoOpen,
				height : options.height,
				width : options.width,
				modal : options.modal,
				close : function(e) {
					$this._close();
					$("#" + this.dialogId).dialog("destroy");
					if (options.close) {
						options.close(e);
					}
				},
				open : function() {
					$this._open();
				},
				buttons : {
					"确定" : function() {
						var treeObj = $.fn.zTree.getZTreeObj($this.treeId);
						var checkNodes = treeObj.getCheckedNodes(true);
						// if (checkNodes == null || checkNodes.length == 0) {
						// // alert("请选择记录！");
						// } else {
						var retVal = {};
						var name = "";
						var path = "";
						var value = "";
						for ( var index = 0; index < checkNodes.length; index++) {
							var checkNode = checkNodes[index];
							if (name == "") {
								name = checkNode.name;
								path = checkNode.path;
								value = checkNode.data;
							} else {
								name = name + ";" + checkNode.name;
								path = path + ";" + checkNode.path;
								value = value + ";" + checkNode.data;
							}
						}
						retVal.name = name;
						retVal.path = path;
						retVal.value = value;
						if ($this.options.afterSelect) {
							$this.options.afterSelect.call($this.$element[0], retVal);
						}
						$(this).dialog("close");
						// }
					},
					"取消" : function() {
						if ($this.options.afterCancel) {
							$this.options.afterCancel.call($this.$element[0]);
						}
						$(this).dialog("close");
					}
				}
			});
			// $("#" + this.dialogId).on("dialogopen", function(event, ui) {
			// $this._open();
			// });
		},
		getOptions : function(options) {
			options = $.extend({}, $.fn.popupTreeWindow.defaults, options, this.$element.data());
			return options;
		},
		updateOptions : function(options) {
			this.options = $.extend({}, this.options || {}, options);
		},
		open : function() {
			$("#" + this.dialogId).dialog("open");
		},
		_open : function() {
			var options = this.options;
			var ztreeSetting = {
				view : {
					showLine : false
				},
				async : {
					enable : true,
					contentType : "application/json",
					url : ctx + "/json/data/services",
					otherParam : {
						"serviceName" : options.serviceName,
						"methodName" : options.methodName,
						"data" : options.data
					},
					type : "POST"
				},
				check : {
					enable : true
				},
				callback : {
					onClick : onClick,
					onAsyncSuccess : onAsyncSuccess
				}
			};
			function onClick(event, treeId, treeNode) {
				var zTree = $.fn.zTree.getZTreeObj(treeId);
				var checked = !treeNode.checked;
				var checkNodes = zTree.getCheckedNodes(true);
				$.each(checkNodes, function() {
					zTree.checkNode(this, false);
				});
				zTree.checkNode(treeNode, checked);
			}
			// 展开树
			function onAsyncSuccess(event, treeId, treeNode, msg) {
				if (options.initValues != null && options.initValues != "") {
					// 设置值
					var zTree = $.fn.zTree.getZTreeObj(treeId);
					var nodes = zTree.getNodes();
					var values = options.initValues.split(";");
					for ( var i = 0; i < nodes.length; i++) {
						var node = nodes[i];
						for ( var j = 0; j < values.length; j++) {
							checkNodeByData(zTree, node, values[j]);
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
			var setting = $.extend(true, ztreeSetting, options.treeSetting || {});
			$.fn.zTree.init($("#" + this.treeId), setting);
		},
		_close : function() {
			$.fn.zTree.destroy($("#" + this.treeId));
		}
	};

	/*
	 * POPUPWINDOW PLUGIN DEFINITION =========================
	 */
	$.fn.popupTreeWindow = function(option) {
		return this.each(function() {
			var $this = $(this);
			var data = $this.data('popupTreeWindow');
			var options = $.extend({}, typeof option == 'object' && option);
			if (!data) {
				$this.data('popupTreeWindow', (data = new PopupTreeWindow(this, options)));
			} else {
				$this.data('popupTreeWindow').updateOptions(options);
			}
			if (typeof option == 'string') {
				data[option]();
			}
		});
	};

	$.fn.popupTreeWindow.Constructor = PopupTreeWindow;

	$.fn.popupTreeWindow.defaults = {
		title : "弹出框",
		autoOpen : false,
		resizable : false,
		height : 400,
		width : 450,
		modal : true,
		initValues : null,
		enableTreeView : true,
		serviceName : "dataDictionaryService",
		methodName : "getFromTypeAsTreeAsync",
		data : "SECURITY_DYBTN"
	};
})(jQuery);