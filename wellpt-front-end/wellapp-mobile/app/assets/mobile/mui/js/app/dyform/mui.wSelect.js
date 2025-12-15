define([ "mui", "constant", "commons", "server", "appModal", "formBuilder", "mui-DyformFieldOptionBase",
		"mui-DyformCommons" ], function($, constant, commons, server, appModal, formBuilder, DyformFieldOptionBase,
		dyformCommons) {
	var UUID = commons.UUID;
	var StringBuilder = commons.StringBuilder;
	var StringUtils = commons.StringUtils;
	// 下拉选项框
	var wSelect = function($placeHolder, options) {
		DyformFieldOptionBase.apply(this, arguments);
	};
	var selectTpl = '<option value="$${value}">${text}</option>';
	var StringBuilder = commons.StringBuilder;
	var UUID = commons.UUID;
	var placeHolderTplId_1 = "mui-DyformField-placeHolder-select";
	var placeHolderTplId_2 = "mui-DyformField-placeHolder-select-2";
	commons.inherit(wSelect, DyformFieldOptionBase, {
		// 返回渲染的模板
		getTemplateId : function(tplId, displayStyle) {
			var _self = this;
			// if (_self.isMultiSelect()) {
			// return "mui-DyformField-placeHolder-select-multiple";
			// }
			return displayStyle == 2 ? placeHolderTplId_2 : placeHolderTplId_1;
		},
		// 渲染可编辑元素
		renderEditableElem : function($editableElem) {
			var _self = this;
			// 生成input弹出框
			// if (_self.isMultiSelect()) {
			var displayValue = _self.getDisplayValue();
			$editableElem[0].value = displayValue;
			// } else {
			// // 生成下原生拉框列表
			// _self.renderNativeEditableElem($editableElem);
			// }
		},
		// 生成下原生拉框列表
		renderNativeEditableElem : function($editableElem) {
			var _self = this;
			// 生成下拉框列表
			// $editableElem[0].querySelector(".dyform-field-editable-select");
			var selectElem = $editableElem[0];
			var dataArray = _self.getDataProvider();
			var templateEngine = appContext.getJavaScriptTemplateEngine();
			var sb = new StringBuilder();
			$.each(dataArray, function() {
				sb.append(templateEngine.render(selectTpl, this));
			});
			selectElem.innerHTML = sb.toString();

			// 选中元素
			var realValue = _self.getRealValue();
			var optionSelector = "option";
			var selects = selectElem.querySelectorAll(optionSelector);
			$.each(selects, function() {
				if (this.value == realValue) {
					this.selected = true;
				}
			});
		},
		// 渲染显示元素
		renderLabelElem : function($labelElem) {
			var _self = this;
			// $labelElem[0].querySelector(".dyform-field-label-select");
			var selectElem = $labelElem[0];
			var displayValue = _self.getDisplayValue();
			selectElem.innerHTML = displayValue;
		},
		// 返回可选的数据
		getDataProvider : function() {
			var _self = this;
			if (_self.dataProvider != null) {
				return _self.dataProvider;
			}
			_self.dataProvider = dyformCommons.getOptionData(_self);
			return _self.dataProvider;
		},
		// 绑定可编辑元素的更改事件
		_bindEvents : function() {
			var _self = this;
			_self._superApply(arguments);
			// 绑定input弹出框
			// if (_self.isMultiSelect()) {
			_self._bindDialogEditableElemEvents();
			// } else {
			// // 绑定原生下拉框列表
			// _self._bindNativeEditableElemEvents();
			// }
		},
		// 绑定弹出选择框
		_bindDialogEditableElemEvents : function() {
			var _self = this;
			_self.$editableElem[0].addEventListener("tap", function(event) {
				this.blur();
				_self._showMultiSelectDialog(event);
			});
		},
		// 显示弹出选择框
		_showMultiSelectDialog : function(event) {
			var _self = this;
			var items = _self.getDataProvider();
			var isMultiSelect = _self.isMultiSelect();
			var uuid = UUID.createUUID();
			var fieldDefinition = _self.options.fieldDefinition;
			return formBuilder.selectEditor({
				items : items,
				val : _self.getRealValue() || "",
				title : _self.getDisplayName(),
				name : "chooseSelectOption",
				multiple : isMultiSelect,
				selectCheckAll:fieldDefinition.selectCheckAll,
				maxSelectionLength:fieldDefinition.maxSelectionLength,
				callback : function(value, event, panel){
					var result = _self.setValue(value.ids.join(";"), true);
					return false;
				}
			}, event);
		},
		// 绑定原生下拉框列表
		_bindNativeEditableElemEvents : function() {
			var _self = this;
			_self.$editableElem[0].addEventListener("change", function() {
				var val = {};
				var option = this.options[this.selectedIndex];
				if (option != null) {
					var text = option.getAttribute("text");
					var value = option.value;
					val[value] = text;
				}
				_self.setValue(val);
			});
		},
		// 是否多选
		isMultiSelect : function() {
			var self = this;
			var fieldDefinition = self.options.fieldDefinition;
			return fieldDefinition.multiSelect == true || fieldDefinition.selectMultiple == true;
		},
		// 是否JSON值对像
		isValueMap : function() {
			return true;
		},
		isBindChange : function() {
			return false;
		}
	});
	return wSelect;
});