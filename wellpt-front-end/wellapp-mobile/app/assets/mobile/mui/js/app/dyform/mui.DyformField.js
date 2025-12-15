define([ "mui", "constant", "commons", "server", "appModal", "appContext", "mui-DyformConstant", "mui-DyformCommons" ], function($, constant, commons, server, appModal, appContext, DyformConstant, dyformCommons) {
	// 验证字段错误
	var DyformFieldError = function(field, validResult) {
		// 验证的字段对象
		this.field = field;
		// 验证的字段名
		this.fieldName = field.getName();
		// 验证的字段显示名
		this.displayName = field.getDisplayName();
		// 验证不通过的字段值
		this.rejectedValue = field.getValue();
		// 验证规则
		this.checkedRule = validResult.checkedRule;
		// 验证错误提示信息
		this.message = validResult.tipMsg;
		// 验证的表单ID
		this.formId = field.formScope.getFormId();
		// 验证的数据UUID
		this.dataUuid = field.formScope.getDataUuid();
		// 是否为从表字段
		this.isSubformField = field.formScope.isSubform();
	};
	var CLS_ERROR = "error";
	var CLS_HIDDEN = "mui-hidden";
	var ObjectUtils = commons.ObjectUtils;
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	var proxiedAfterInvoke = dyformCommons.proxiedAfterInvoke;
	var addClass = dyformCommons.addClass;
	var removeClass = dyformCommons.removeClass;
	//单行的显示模式
	var placeHolderTplId_1 = 'mui-DyformField-placeHolder-default';
	//双行的显示模式
	var placeHolderTplId_2 = 'mui-DyformField-placeHolder-default-2';
	var DyformField = function($placeHolder, options) {
		var _self = this;
		_self.options = _self._options(options);
		_self.formScope = options.formScope;
		_self.dyform = _self.formScope.getDyform();
		_self.formValidation = _self.dyform.formValidation;
		_self.definition = options.fieldDefinition;
		_self.value = ObjectUtils.nullToEmpty(options.fieldValue);
		_self.id = _self.formScope.createFieldId(_self.definition);
		_self.$editableElem = null;
		_self.$labelElem = null;
		_self.$requiredMarkerElem = null;
		_self.$errorElem = null;
		_self.$placeHolder = $placeHolder;
		// 创建验证器
		_self.createValidators();
		proxiedAfterInvoke.call(_self, "setValue", "afterSetValue", true);
		proxiedAfterInvoke.call(_self, "render", "afterRender");
	};
	
	DyformField.fn = {};
	$.extend(DyformField.fn, {
		bind : function(type, listener, custom){
			var self = this;
			if(custom === true){
				self.options[type] = listener;
			}else {
				return self.on.apply(self, arguments);
			}
		},
		get$InputElem : function() {
			var self = this;
			return self.$editableElem;
		},
		clean : function(){
			var self = this;
			return self.setValue("", true);
		},
		clear : function(){
			var self = this;
			return self.clean();
		},
		hideEditableElem : function(){
			
		},
		addMustMark : function(isAdd){
			var self = this;
			self.setRequired(isAdd);
		}
	})
	
	$.extend(DyformField.prototype, {
		// 构造函数初始化时调用
		_options : function(options) {
			return options || {};
		},
		// 初始化，构造函数执行后自动调用
		init : function() {
			var self = this;
			// 必填的验证器添加"*"标识
			var isRequired = false;
			$.each(self.validators, function(i, validator) {
				if (validator.isRequired()) {
					isRequired = true;
				}
			});
			// JSON值转成JSON
			self.orininalValue = self.value;
			if(StringUtils.isBlank(self.value)){
				self.value = self.getInitValue();
			}
			self.options.initComplete = true;
			self.options.required = isRequired;
		},
		// 获取字段ID(dyfs_{主表/从表数据UUID}_{字段编号})
		getId : function() {
			return this.id;
		},
		// 获取字段所在定义UUID
		getFormUuid : function() {
			return this.formScope.getFormUuid();
		},
		// 获取字段所在数据UUID
		getDataUuid : function() {
			return this.formScope.getDataUuid();
		},
		// 获取字段名
		getName : function() {
			return this.definition.name;
		},
		// 获取字段显示名
		getDisplayName : function() {
			return this.definition.displayName;
		},
		// 渲染
		render : function(tplId) {
			var _self = this;
			var options = _self.definition;
			var data = _self._getTemplateData();
			var displayStyle = parseInt( _self.options.fieldDefinition.displayStyle );
			var templateEngine = appContext.getJavaScriptTemplateEngine();
			var templateId = _self.getTemplateId( tplId, displayStyle );
			var html = templateEngine.renderById(templateId, data);

			var wrapper = document.createElement("div");
			wrapper.id = "div_" + _self.getId();
			var sb = new StringBuilder();
			if (_self.$element == null) {
				wrapper.innerHTML = html;
				_self.$placeHolder[0].appendChild(wrapper);
				_self.$element = $(wrapper);
			} else {
				_self.$element[0].innerHTML = html;
			}
			_self.$editableElem = $(".dyform-field-editable", wrapper);
			var editableElem = _self.$editableElem[0];
			if(options.ctlWidth && editableElem) {
				// editableElem.style.width = options.ctlWidth;
			}
			if(options.ctlHight && editableElem) {
				// editableElem.style.height = options.ctlHight;
			}
			_self.renderEditableElem(_self.$editableElem);
			_self.$labelElem = $(".dyform-field-label", wrapper);
			_self.renderLabelElem(_self.$labelElem);
			_self.$errorElem = $(".dyform-field-error", wrapper);
			_self.$requiredMarkerElem = $(".required-marker", wrapper);

			// 绑定事件
			_self._bindEvents();
		},
		// 渲染编辑元素
		renderEditableElem : function($editableElem) {
			var self = this;
			if (self.$editableElem) {
				self.$editableElem[0].value = self.getDisplayValue();
			}

		},
		// 渲染文本元素
		renderLabelElem : function($labelElem) {
			var self = this;
			if (self.$labelElem) {
				self.$labelElem[0].innerHTML = self.getDisplayValue();
			}
		},
		// 绑定事件
		_bindEvents : function() {
			var _self = this;
			var editableElem = _self.$editableElem[0];
			var inputMode = _self.definition.inputMode;
			if (editableElem && (inputMode === "1" || inputMode === "2" || inputMode === "20" || inputMode === "31")) {
				// text、ckedit、textarea、number
				var focusIn = function(event) {
					var focusElement = this;
					$.ui.scrollIntoView(focusElement, true);
				}
				editableElem.addEventListener("tap", focusIn);
				// editableElem.addEventListener("click", focusIn);
				// editableElem.addEventListener("focus", focusIn); // tap > focus
			};
			// 职位为unknown的提示
			if(_self.value === "unknown" && _self.definition.defaultValue === "{CURRENTCREATORMAINJOBNAME}") {
				var dyform = _self.formScope.getDyform();
				if(dyform.isCreate()) {
					var eventType = DyformConstant.dyformEvent.DyformCreationComplete;
					dyform.element[0].addEventListener(eventType, function() {
						var errorMsg = "您的主职位为空，请联系管理员进行调整再做业务操作，以免出现错误的业务数据!";
						appModal.error(errorMsg);
					});
				}
			}
		},
		// 获取渲染的模板ID
		getTemplateId : function(tplId, displayStyle) {
			if( tplId == undefined ){
				//console.log("getTemplateId,=" +  displayStyle );
				return displayStyle == 2 ? placeHolderTplId_2 : placeHolderTplId_1;
			}else{
				return tplId;
			}
		},
		_getTemplateData : function() {
			var data = {
				displayName : this.definition.tagName || this.definition.displayName,
				fieldValue : this.value,
				fieldName : this.definition.name
			};
			return data;
		},
		// 渲染后处理
		afterRender : function() {
			var _self = this;
			var showType = _self.definition.showType;
			var isShow = _self.definition.isShow;
			var dyshowType = DyformConstant.dyshowType;
			// 手机设置的隐藏
			if (isShow === false) {
				_self.setHidden(true);
			} else if (showType == dyshowType.showAsLabel) {
				_self.setDisplayAsLabel();
			} else if (showType == dyshowType.readonly) {
				// _self.setReadonly(true);
				_self.setDisplayAsLabel();
			} else if (showType == dyshowType.disabled) {
				// _self.setEditable(false);
				_self.setDisplayAsLabel();
			} else if (showType == dyshowType.hide) {
				_self.setHidden(true);
			} else if (showType == dyshowType.edit) {
				_self.setEditable(true);
			}
			
			// 设置必填会显示编辑域(与PC端不同)
			_self.setRequired(_self.isRequired());
			
			// 表单全局设置，是否显示为文本
			if(_self.formScope.isDisplayAsLabel()) {
				_self.setDisplayAsLabel();
			}
			
			// 注册挂起的事件监听
			_self._registerPendingListeners();
				
			var editableEle = _self.$editableElem[0];
			if (editableEle && _self.isBindChange()) {
				var changeFn = function(event) {
					var value = _self.collectValue();
					_self.setValue(value, false);
					if (event.which === 9 && value === "") {
						return;
					}
					// _self.afterSetValue(value);
				}
				editableEle.addEventListener("paste", changeFn);
				editableEle.addEventListener("input", changeFn);
				editableEle.addEventListener("keyup", changeFn);
				editableEle.addEventListener("change", changeFn);
			}
		},
		isBindChange : function() {
			return true;
		},
		// 是否从表字段
		isSubform : function() {
			return false;
		},
		// 控件值是否修改
		isValueChanged : function() {
			return this.getValue() !== this.orininalValue;
		},
		collectValue : function(event) {
			return this.$editableElem[0].value;
		},
		renderValue : function() {
		},
		// 获取字段值
		getValue : function() {
			var _self = this;
			return _self.getRealValue();
		},
		getInitValue : function() {
			var self = this;
			var defaultValue = self.options.fieldDefinition.defaultValue;
			if (defaultValue && defaultValue.indexOf(":") < 0 && defaultValue.charAt(0) === "{"
				&& defaultValue.charAt(defaultValue.length - 1) === "}") {
				defaultValue = "";
			}
			return defaultValue || "";
		},
		// 获取字段真实值
		getRealValue : function() {
			var _self = this;
			var value = _self.value;
			if (_self.isValueMap() && $.isArray(value)) {
				return value.join(";");
			}
			return value;
		},
		// 获取字段显示值
		getDisplayValue : function() {
			var _self = this;
			if (!_self.isValueMap()) {
				return _self.value;
			}else if(_self.getDataProvider){
				return dyformCommons.getDisplayValue2(_self.value, _self.getDataProvider());
			}
			return dyformCommons.getDisplayValue(_self.value);
		},
		// 获取字段值
		getValueMap : function() {
			var self = this;
			return dyformCommons.getValueMap(self.value, self.getDataProvider());
		},
		// 设置字段值
		setValue : function(value, isRender) {
			var _self = this
			if(value != null) {
				_self.value = value;
			} else {
				_self.value = "";
			}

			if (_self.initComplete === true && isRender !== false) {
				_self.renderLabelElem(_self.$labelElem);
				_self.renderEditableElem(_self.$editableElem);
			}
		},
		// @param isRender 是否对控件再进行界面渲染,false表示不渲染,true或者没定义表示要渲染
		setValueByMap : function(valuemap, isRender) {
			var self = this;
			if(valuemap){
				var real = [];
				for(var i in valuemap){
					real.push(i);
				}
				self.value = real.join(";");
			}else {
				self.value = "";
			}
			if (labelEle = self.$labelElem[0]) {
				labelEle.innerHTML = self.getDisplayValue();
			}
			// if (self.isRender()) {// 该控件被隐藏时则不进行渲染
			// self.render(isRender);// 将值渲染到页面元素上
			// }
			if (self.options.initComplete) {// 在初始化完成后,才会进行这个操作
				self.setToRealDisplayColumn();// 设置到真实值，显示值字段上
			}
		},
		// 设值到真实值显示值字段
		setToRealDisplayColumn : function() {
			var self = this, realDisplay;
			if (!(realDisplay = self.options.fieldDefinition.realDisplay)) {
				return;
			}
			var value = self.getValue();
			var displayValue = self.getDisplayValue();
			var real = realDisplay.real, display = realDisplay.display;
			if (typeof real != "undefined" && real) {
				self.dyform.setFieldValue(real, value);
			}
			if (typeof display != "undefined" && display) {
				self.dyform.setFieldValue(display, displayValue);
			}
		},
		// 设置字段值后调用，自动调用
		afterSetValue : function(value) {
			// 显示值、真实值处理
			var _self = this;
			var realDisplay = _self.definition.realDisplay;
			if (_self.isValueMap() && realDisplay != null) {
				var display = realDisplay.display;
				var real = realDisplay.real;
				var displayField = _self.formScope.getField(display);
				var realField = _self.formScope.getField(real);
				if (displayField != null) {
					displayField.setValue(_self.getDisplayValue(), true);
				}
				if (realField != null) {
					realField.setValue(_self.getRealValue(), true);
				}
			}
			
			// 触发计算公式
			_self.formScope.triggerFormula(_self);
			// 触发设值后事件
			_self.trigger("afterSetValue", value);
			// console.log("afterSetValue: " + JSON.stringify(value));
			// 验证
			_self.validate();
		},
		setValue2LabelElem : function(value) {
			var self = this, labelEle;
			if (labelEle = self.$labelElem[0]) {
				labelEle.innerHTML = (value ? value : self.getValue());
				self.setDisplayAsLabel();
			}
		},
		// 设置字段显示为文本
		setDisplayAsLabel : function() {
			var self = this;
			addClass(self.$editableElem, CLS_HIDDEN);
			removeClass(self.$labelElem, CLS_HIDDEN);
		},
		// 判断字段是否必填
		isRequired : function() {
			// this.definition.editable === 0 为从表字段设置的不可编辑
			return this.options.required && this.definition.editable !== "0";
		},
		// 设置字段是否必填
		setRequired : function(required) {
			var self = this;
			self.options.required = required;
			if (required) {
				// self.setEditable(true);
				removeClass(self.$requiredMarkerElem, CLS_HIDDEN);
				var fieldCheckRule = {
					label : "非空",
					value : dyCheckRule.notNull
				};
				var validator = self.dyform.formValidation.createValidator(fieldCheckRule);
				self.validators[dyCheckRule.notNull] = validator;
			} else {
				delete self.validators[dyCheckRule.notNull];
				addClass(self.$requiredMarkerElem, CLS_HIDDEN);
			}
		},
		// 判断字段是否可编辑
		isEditable : function() {
			return this.options.editable;
		},
		// 获取字段所在区块代码
		getBlockCode : function(){
			return this.definition.blockCode;
		},
		// 设置字段是否可编辑
		setEditable : function(editable) {
			this.options.editable = !!editable;
			if (editable) {
				removeClass(this.$editableElem, CLS_HIDDEN);
				addClass(this.$labelElem, CLS_HIDDEN);
			} else {
				addClass(this.$editableElem, CLS_HIDDEN);
				removeClass(this.$labelElem, CLS_HIDDEN);
			}
		},
		// 判断字段是否只读
		isReadonly : function() {
			return this.options.readonly;
		},
		// 设置字段是否只读
		setReadonly : function(readonly) {
			this.options.readonly = !!readonly;
			this.setEditable(!readonly);
			if (readonly) {
				this.$editableElem[0].setAttribute("readonly", readonly);
			} else {
				this.$editableElem[0].remove("readonly");
			}
		},
		// 判断字段是否隐藏
		isHidden : function() {
			return this.options.hidden;
		},
		// 设置字段是否隐藏
		setHidden : function(hidden) {
			this.options.hidden = !!hidden;
			if (hidden) {
				addClass(this.$element, CLS_HIDDEN);
			} else {
				removeClass(this.$element, CLS_HIDDEN);
			}
		},
		// 设置字段是否显示
		setVisible : function(visible) {
			var self = this;
			self.setHidden(!visible);
		},
		// 创建字段验证器
		createValidators : function() {
			var _self = this;
			var validators = {};
			var fieldCheckRules = _self.getFieldCheckRules(_self.definition);
			$.each(fieldCheckRules, function(i, fieldCheckRule) {
				if (fieldCheckRule.value != null) {
					// 去重复
					var validator = _self.dyform.formValidation.createValidator(fieldCheckRule);
					if($.isFunction(validator.fn)){
						validators[fieldCheckRule.value] = validator;
					}else {
						console.log("field[" + _self.getName() + "]rule[" + fieldCheckRule.value + "] fn no implement");
					}
				}
			});
			_self.validators = validators;
		},
		getFieldCheckRules : function(options){
			options.fieldCheckRules = options.fieldCheckRules || [];
			if(options.length > 0){
				options.fieldCheckRules.push({
					value : "10",
					label : "普通"
				});
			}
			return options.fieldCheckRules;
		},
		// 判断是否键值控件域
		isValueMap : function() {
			return false;
		},
		// 获取字段验证器
		getValidators : function() {
			return this.validators;
		},
		// 验证
		validate : function() {
			var _self = this;
			// 表单创建完成才可验证
			if(!_self.formScope.isDyformCreateComplete()) {
				return;
			}
			var results = [];
			var validators = this.getValidators();
			$.each(validators, function(i, validator) {
				validator.validate && results.push(validator.validate(_self));
			});
			var fieldErrors = [];
			$.each(results, function(i, validResult) {
				if (validResult.valid == false) {
					fieldErrors.push(new DyformFieldError(_self, validResult));
				}
			});
			// 显示错误信息
			if (fieldErrors.length > 0) {
				_self.showErrors(fieldErrors);
			} else {
				// 隐藏错误信息
				_self.hideErrors();
			}
			return fieldErrors;
		},
		// 显示验证错误信息
		showErrors : function(errors) {
			var self = this, errorElem;
			if (errorElem = self.$errorElem[0]) {
				errorElem.innerHTML = errors[0].message;
				errorElem.classList.remove(CLS_HIDDEN);
				errorElem.parentNode.classList.add(CLS_ERROR);
			}
		},
		hideErrors : function() {
			var self = this, errorElem;
			if (errorElem = self.$errorElem[0]) {
				errorElem.classList.add(CLS_HIDDEN);
				errorElem.parentNode.classList.remove(CLS_ERROR);
			}
			var bubble;
			if (bubble = self.formValidation.bubble) {
				bubble.removeErrorItem(self.getName());
			}
		},
		// 添加验证规则
		addCustomValidate : function(fn) {
			// this.validators.push(this.dyform.formValidation.createCustomValidator(fn));
			var self = this;
			var validator = self.dyform.formValidation.createCustomValidator(fn);
			self.validators[validator.rule] = validator;
		},
		// 获取数据数据新增、修改、删除的状态
		getState : function() {
		},
		// 返回控件DOM事件的元素
		getEventElem : function() {
			var _self = this;
			if (_self.$editableElem) {
				return _self.$editableElem[0];
			}
			return null;
		},
		trigger : function(eventType, eventData) {
			var _self = this;
			var wrapperType = eventType;
			if(wrapperType === "click") {
				wrapperType = "tap";
			}
			var eventElem = _self.getEventElem();
			if (eventElem) {
				var data = $.extend({}, eventData);;
				data.field = _self;
				$.trigger(eventElem, wrapperType, data)
			}
			if($.isFunction(_self.options[eventType])) {
				var args = $.slice.call(arguments, 1);
				return _self.options[eventType].apply(_self, args);
			}
		},
		on : function(type, listener, options) {
			var _self = this;
			var wrapperType = type;
			if(type === "click") {
				wrapperType = "tap";
			}
			var eventElem = _self.getEventElem();
			if (eventElem) {
				eventElem.addEventListener(wrapperType, listener, options);
			} else {
				// 挂起的事件监听
				if(_self._pendingListeners == null) {
					_self._pendingListeners = [];
				}
				_self._pendingListeners.push({
					type : wrapperType,
					listener : listener,
					options : options
				})
			}
		},
		_registerPendingListeners : function() {
			var _self = this;
			// 挂起的事件监听
			if(_self._pendingListeners) {
				var elem = _self.getEventElem();
				if(!elem && _self.$editableElem) {
					elem = _self.$editableElem[0];
				} else if(!elem && _self.$placeHolder) {
					elem = _self.$placeHolder[0];
				}
				$.each(_self._pendingListeners, function() {
					var pl = this;
					if(elem) {
						elem.addEventListener(pl.type, pl.listener, pl.options);
					} else {
						console.error("event elememt not found");
						console.error(pl);
					}
				});
				delete _self._pendingListeners;
			}
		}
	}, DyformField.fn);
	return DyformField;
});