define([ "mui", "constant", "commons", "server", "mui-DyformFieldOptionBase", "mui-DyformConstant", "mui-DyformCommons" ], function($,
		constant, commons, server, DyformFieldOptionBase, DyformConstant, dyformCommons) {
	// 单选框
	var wRadio = function($placeHolder, options) {
		DyformFieldOptionBase.apply(this, arguments);
	};
	var UUID = commons.UUID;
	var StringBuilder = commons.StringBuilder;
	var tplSb = new StringBuilder();
	tplSb.append('<li class="mui-table-view-cell mui-radio mui-left">');
	tplSb.append('<label for="${id}">${text}</label>');
	tplSb.append('<input type="radio" id="${id}" name="${name}_${groupId}" value="$${value}" text="${text}">');
	tplSb.append('</li>');
	var radioTpl = tplSb.toString();
	commons.inherit(wRadio, DyformFieldOptionBase, {
		// 返回渲染的模板
		getTemplateId : function() {
			return "mui-DyformField-placeHolder-radio";
		},
		// 渲染可编辑元素
		renderEditableElem : function($editableElem) {
			var _self = this;
			// 生成单选框列表
			var radioElem = $editableElem[0].querySelector(".dyform-field-editable-radio");
			_self.radioElem = radioElem;
			var dataArray = _self.getDataProvider();
			var templateEngine = appContext.getJavaScriptTemplateEngine();
			var sb = new StringBuilder();
			$.each(dataArray, function() {
				sb.append(templateEngine.render(radioTpl, this));
			});
			radioElem.innerHTML = sb.toString();

			// 选中元素
			var realValue = _self.getRealValue();
			var radioSelector = "input[type=radio]";
			var radios = radioElem.querySelectorAll(radioSelector);
			$.each(radios, function() {
				if (this.value == realValue) {
					this.checked = true;
				}
			});
		},
		// 渲染显示元素
		renderLabelElem : function($editableElem) {
			var _self = this;
			var radioElem = $editableElem[0].querySelector(".dyform-field-label-radio");
			var displayValue = _self.getDisplayValue();
			radioElem.innerHTML = displayValue;
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
		// 返回控件DOM事件的元素
		getEventElem : function() {
			return this.radioElem;
		},
		// 绑定可编辑元素的更改事件
		_bindEvents : function() {
			var _self = this;
			_self._superApply(arguments);
			$(_self.radioElem).on("change", "input[type=radio]", function() {
				_self.setValue(this.value);
			});
			var dyform = _self.formScope.getDyform();
			var eventType = DyformConstant.dyformEvent.DyformCreationComplete;
			dyform.element[0].addEventListener(eventType, function() {
				$("input[type=radio]:checked", _self.checkboxElem).each(function() {
					$.trigger(this, "change");
					return false;// break
				});
			})
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
	return wRadio;
});