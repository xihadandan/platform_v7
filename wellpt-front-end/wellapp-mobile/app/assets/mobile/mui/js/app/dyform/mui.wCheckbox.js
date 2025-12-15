define([ "mui", "constant", "commons", "mui-DyformFieldOptionBase", "mui-DyformConstant", "mui-DyformCommons" ],
		function($, constant, commons, DyformFieldOptionBase, DyformConstant, dyformCommons) {
			// 复选框
			var wCheckbox = function($placeHolder, options) {
				DyformFieldOptionBase.apply(this, arguments);
			};
			var UUID = commons.UUID;
			var StringUtils = commons.StringUtils;
			var StringBuilder = commons.StringBuilder;
			var tplSb = new StringBuilder();
			tplSb.append('<li class="mui-table-view-cell mui-checkbox mui-left">');
			tplSb.append('<label for="${id}">${text}</label>');
			tplSb.append('<input type="checkbox"name="${name}_${groupId}" value="${value}" text="${text}">');
			tplSb.append('</li>');
			var checkboxTpl = tplSb.toString();
			commons.inherit(wCheckbox, DyformFieldOptionBase, {
				// 返回渲染的模板
				getTemplateId : function() {
					var _self = this;
					if (_self.isSingleCheck()) {
						return "mui-DyformField-placeHolder-checkbox";
					}
					return "mui-DyformField-placeHolder-checkbox";
				},
				// 渲染可编辑元素
				renderEditableElem : function($editableElem) {
					var _self = this;
					// 生成复选框列表
					var checkboxElem = $editableElem[0].querySelector(".dyform-field-editable-checkbox");
					_self.checkboxElem = checkboxElem;
					var dataArray = _self.getDataProvider();
					var templateEngine = appContext.getJavaScriptTemplateEngine();
					var sb = new StringBuilder();
					$.each(dataArray, function() {
						sb.append(templateEngine.render(checkboxTpl, this));
					});
					checkboxElem.innerHTML = sb.toString();

					// 选中元素
					var realValue = _self.getRealValue();
					if (StringUtils.isBlank(realValue)) {
						realValue = "";
					}
					var realValues = realValue.split(";");
					var checkboxSelector = "input[type=checkbox]";
					var checkboxs = checkboxElem.querySelectorAll(checkboxSelector);
					$.each(checkboxs, function() {
						for (var i = 0; i < realValues.length; i++) {
							var checkboxVal = realValues[i];
							if (this.value == checkboxVal) {
								this.checked = true;
							}
						}
					});
				},
				// 渲染显示元素
				renderLabelElem : function($editableElem) {
					var _self = this;
					var checkboxElem = $editableElem[0].querySelector(".dyform-field-label-checkbox");
					// 单选
					if (_self.isSingleCheck()) {
						var singleCheckContent = _self.options.fieldDefinition.singleCheckContent;
						var singleUnCheckContent = _self.options.fieldDefinition.singleUnCheckContent;
						var checkValue = dyformCommons.getRealValue(singleCheckContent);
						var unCheckValue = dyformCommons.getRealValue(singleUnCheckContent);
						var checkText = dyformCommons.getDisplayValue(singleCheckContent);
						var unCheckText = dyformCommons.getDisplayValue(singleUnCheckContent);
						var value = _self.getRealValue();
						if (value === checkValue) {
							checkboxElem.innerHTML = checkText;
						} else if (value === unCheckValue) {
							checkboxElem.innerHTML = unCheckText;
						} else {
							checkboxElem.innerHTML = "";
						}
					} else {
						// 多选
						var displayValue = _self.getDisplayValue();
						checkboxElem.innerHTML = displayValue;
					}
				},
				// 返回可选的数据
				getDataProvider : function() {
					var _self = this;
					if (_self.dataProvider != null) {
						return _self.dataProvider;
					}
					_self.dataProvider = [];
					// 单选
					if (_self.isSingleCheck()) {
						var singleCheckContent = _self.options.fieldDefinition.singleCheckContent;
						var checkValue = dyformCommons.getRealValue(singleCheckContent);
						var checkText = dyformCommons.getDisplayValue(singleCheckContent);
						var item = {
							id : UUID.createUUID(),
							value : checkValue,
							text : checkText, //  + "/" + unCheckText
							name : _self.getName()
						};
						_self.dataProvider.push(item);
					} else {
						// 多选
						_self.dataProvider = dyformCommons.getOptionData(_self);
					}
					return _self.dataProvider;
				},
				// 返回控件DOM事件的元素
				getEventElem : function() {
					return this.checkboxElem;
				},
				// 绑定可编辑元素的更改事件
				_bindEvents : function() {
					var _self = this;
					_self._superApply(arguments);
					function fnCheck() {
						var val = [];
						var checkboxSelector = "input[type=checkbox]";
						var checkboxs = _self.checkboxElem.querySelectorAll(checkboxSelector);
						$.each(checkboxs, function() {
							if (this.checked === true) {
								val.push(this.value);
							}
						});
						_self.setValue(val);
					}
					function fnSingle(){
						var singleCheckContent = _self.options.fieldDefinition.singleCheckContent;
						var singleUnCheckContent = _self.options.fieldDefinition.singleUnCheckContent;
						var checkValue = dyformCommons.getRealValue(singleCheckContent);
						// var checkText = dyformCommons.getDisplayValue(singleCheckContent);
						var	unCheckValue = dyformCommons.getRealValue(singleUnCheckContent);
						// var	unCheckText = dyformCommons.getDisplayValue(singleUnCheckContent);
						_self.setValue(this.checked === true ? checkValue : unCheckValue);
						// 单选字段控制
						_self.setCtlField();
					}
					var fn = _self.isSingleCheck() ? fnSingle : fnCheck;
					$(_self.checkboxElem).on("change", "input[type=checkbox]", fn);
					var dyform = _self.formScope.getDyform();
					var eventType = DyformConstant.dyformEvent.DyformCreationComplete;
					dyform.element[0].addEventListener(eventType, function() {
						// 有设置值,才出发change事件
						$("input[type=checkbox]:checked", _self.checkboxElem).each(function() {
							$.trigger(this, "change");
							return false;// break
						});
					});
				},
				setCtlField : function() {
					var _self = this;
					var ctrlField = _self.definition.ctrlField;
					if (StringUtils.isBlank(ctrlField)) {
						return;
					}
					var ctrlFields = ctrlField.split(",");
					if (ctrlFields.length !== 2) {
						return;
					}
					var fieldNames = ctrlFields[0].split(";");
					for (var i = 0; i < fieldNames.length; i++) {
						var fieldName = fieldNames[i];
						var field = _self.formScope.getField(fieldName);
						if (field == null) {
							return;
						}
						var singleCheckContent = _self.definition.singleCheckContent;
						var checkValue = dyformCommons.getRealValue(singleCheckContent);
						var value = _self.getRealValue();
						if (checkValue === value) {
							field.setHidden(false);
						} else {
							field.setHidden(true);
						}
					}
				},
				// 是否单选
				isSingleCheck : function() {
					return this.options.fieldDefinition.selectMode == '1';
				},
				// 是否JSON值对像
				isValueMap : function() {
					return true;
				},
				isBindChange : function() {
					return false;
				}
			});
			return wCheckbox;
		});