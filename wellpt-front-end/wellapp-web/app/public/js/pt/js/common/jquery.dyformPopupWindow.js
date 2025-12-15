;
(function($) {
	/*
	 * POPUPWINDOW CLASS DEFINITION ======================
	 */
	var DyformPopupWindow = function(element, options) {
		this.init("dyformPopupWindow", element, options);
	};

	DyformPopupWindow.prototype = {
		constructor : DyformPopupWindow,
		init : function(type, element, options) {
			this.$element = $(element);
			var $element = this.$element;
			this.options = this.getOptions(options);
			var options = this.options;
			var dialogId = $element.attr("id") + "_" + "dialog";
			var selectId = $element.attr("id") + "_" + "select";
			var selectFieldId = $element.attr("id") + "_field";
			this.dialogId = dialogId;
			this.selectId = selectId;
			this.selectFieldId = selectFieldId;
			var _this = this;
			var formDiv = '<div id="'
					+ dialogId
					+ '"><select id="'
					+ selectId
					+ '" name="'
					+ selectId
					+ '"><option value="-1" uuid="-1">--请选择--</option></select><div id="'
					+ selectFieldId + '"></div></div>';
			$(formDiv).insertAfter($element);

			// 初始化数据
			JDS.call({
				service : "dutyAgentService.getAllDyFormDefinitionBasicInfo",
				data : [],
				success : function(result) {
					// 清空
					$.each(result.data, function(index) {
						var uuid = this.uuid;
						var id = this.id;
						var name = this.name;
						var option = "<option value='" + uuid + "' uuid='" + uuid
								+ "'>" + name + "</option>";
						$("#" + selectId).append(option);
					});
				}
			});
			$("#" + this.selectId).select2({
				sortResults : function(results, container, query) {
					var limits = _this.options["limits"];
					if (limits.length == 0) {
						return results;
					}
					if(limits.length == 1 && limits[0] == "-1"){
						return results;
					}
					var newResults = [];
					$.each(limits, function(i) {
						var formId = this;
						$.each(results, function(j) {
							if (this["id"] == formId) {
								if(newResults.indexOf(this) == -1) {
									newResults.push(this);
								}
							}
						});
					});
					return newResults;
				},
				width : '100%'
			});
			// 根据表单定义UUID加载表单字段
			$("#" + this.selectId).bind("change", function(e) {
				var formUuid = $(this).find("option:selected").attr("uuid");
				getFieldByFormUuid(formUuid);
			});

			function getFieldByFormUuid(formUuid) {
				$("#" + selectFieldId).html("");
				if (formUuid === "-1") {
					return;
				}
				JDS.call({
					service : "dutyAgentService.getDyFormFieldDefinition",
					data : [ formUuid ],
					success : function(result) {
						$.each(options.defaultOptions, function(index) {
							var id = this.id;
							var name = this.name;
							addRadioOption(id, name);
						});
						$.each(result.data, function(index) {
							var id = this.id;
							var name = this.name;
							addRadioOption(id, name);
						});
					}
				});
			}
			function addRadioOption(id, name) {
				var radio = '<div><input id="' + id
						+ '" name="formField" type="radio" value="' + id
						+ '" />' + '<label for="' + id
						+ '" style="margin-left: 5px; display: inline-block">'
						+ name + '</label></div>';
				$("#" + selectFieldId).append(radio);
			}

			var $this = this;
			this.$element.focus($.proxy(this.open, $this));
			// 初始化窗口
			$("#" + this.dialogId).dialog(
					{
						title : options.title,
						resizable : options.resizable,
						autoOpen : options.autoOpen,
						height : options.height,
						width : options.width,
						modal : options.modal,
						close : function(e) {
						},
						open : function() {
							$this._open();
						},
						buttons : {
							"确定" : function() {
								var data = {};
								// 表单定义UUID
								var formUuid = $("#" + selectId).find(
										"option:selected").attr("uuid");
								// 表单ID
								var formId = $("#" + selectId).find(
										"option:selected").val();
								// 表单名称
								var formName = $("#" + selectId).find(
										"option:selected").text();
								// 表单字段ID
								var fieldId = $("#" + selectFieldId).find(
										"input:checked").val();
								// 表单字段名称
								var fieldName = $("#" + selectFieldId).find(
										"label[for='" + fieldId + "']").text();
								if (formUuid != "-1") {
									data.formUuid = formUuid;
									data.formId = formId;
									data.formName = formName;
									data.fieldId = fieldId;
									data.fieldName = fieldName;
								}
								if ($this.options.afterSelect) {
									$this.options.afterSelect.call(
											$this.$element[0], data);
								}
								$(this).dialog("close");
							},
							"取消" : function() {
								if ($this.options.afterCancel) {
									$this.options.afterCancel
											.call($this.$element[0]);
								}
								$(this).dialog("close");
							}
						}
					});
		},
		getOptions : function(options) {
			options = $.extend({}, $.fn.dyformPopupWindow.defaults, options,
					this.$element.data());
			return options;
		},
		setParams : function(option) {
			this.options = $.extend(true, this.options, option);
		},
		open : function() {
			$("#" + this.dialogId).dialog("open");
		},
		_open : function() {
		},
		_close : function() {
		},
		updateOptions : function(options) {
			this.options = $.extend({}, this.options || {}, options);
		}
	};

	/*
	 * POPUPWINDOW PLUGIN DEFINITION =========================
	 */
	$.fn.dyformPopupWindow = function(option) {
		var method = false;
		var args = null;
		if (arguments.length == 2) {
			method = true;
			args = arguments[1];
		}

		return this.each(function() {
			var $this = $(this);
			var data = $this.data('dyformPopupWindow');
			var options = $.extend({}, typeof option == 'object' && option);
			if (!data) {
				$this.data('dyformPopupWindow', (data = new DyformPopupWindow(
						this, options)));
			} else {
				$this.data('dyformPopupWindow').updateOptions(options);
			}
			if (typeof option == 'string') {
				if (method == true && args != null) {
					data[option](args);
				} else {
					data[option]();
				}
			}
		});
	};

	$.fn.dyformPopupWindow.Constructor = DyformPopupWindow;

	$.fn.dyformPopupWindow.defaults = {
		title : "弹出框",
		autoOpen : false,
		resizable : false,
		height : 400,
		width : 450,
		modal : true,
		initValues : null,
		enableTreeView : true,
		defaultOptions : [],
		limits : []
	};
})(jQuery);