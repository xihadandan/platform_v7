$(function() {

	var rules = {
		rules : {
			name : {
				minlength : 2,
				required : true
			},
			email : {
				required : true,
				email : true
			},
			subject : {
				minlength : 2,
				required : true
			},
			message : {
				minlength : 2,
				required : true
			},
			validateSelect : {
				required : true
			},
			validateCheckbox : {
				required : true,
				minlength : 2
			},
			validateRadio : {
				required : true
			}
		}
	};

	function getValidatorContainer(elem) {
		return elem.form || $(elem).closest(".dyform")[0];
	}
	$.extend($.fn, {
		valid : function() {
			if ($(this[0]).is("form")) {
				return this.validate().form();
			} else {
				var valid = true;
				var validator = $(getValidatorContainer(this[0])).validate();
				this.each(function() {
					valid = valid && validator.element(this);
				});
				return valid;
			}
		},
		// http://docs.jquery.com/Plugins/Validation/rules
		rules : function(command, argument) {
			var element = this[0];

			if (command) {
				var settings = $.data(getValidatorContainer(element), "validator").settings;
				var staticRules = settings.rules;
				var existingRules = $.validator.staticRules(element);
				switch (command) {
				case "add":
					$.extend(existingRules, $.validator.normalizeRule(argument));
					// remove messages from rules, but allow them to be set
					// separetely
					delete existingRules.messages;
					staticRules[element.name] = existingRules;
					if (argument.messages) {
						settings.messages[element.name] = $.extend(settings.messages[element.name], argument.messages);
					}
					break;
				case "remove":
					if (!argument) {
						delete staticRules[element.name];
						return existingRules;
					}
					var filtered = {};
					$.each(argument.split(/\s/), function(index, method) {
						filtered[method] = existingRules[method];
						delete existingRules[method];
					});
					return filtered;
				}
			}

			var data = $.validator.normalizeRules($.extend({}, $.validator.classRules(element), $.validator.attributeRules(element), $.validator.dataRules(element), $.validator
					.staticRules(element)), element);

			// make sure required is at front
			if (data.required) {
				var param = data.required;
				delete data.required;
				data = $.extend({
					required : param
				}, data);
			}

			return data;
		}
	});

	$.extend($.validator, {
		staticRules : function(element) {
			var rules = {};
			var $form = getValidatorContainer(element);
			var validator = $.data($form, "validator");
			if (validator.settings.rules) {
				rules = $.validator.normalizeRule(validator.settings.rules[element.name]) || {};
			}
			return rules;
		},
		normalizeRules : function(rules, element) {
			// handle dependency check
			$.each(rules, function(prop, val) {
				// ignore rule when param is explicitly false, eg.
				// required:false
				if (val === false) {
					delete rules[prop];
					return;
				}
				if (val.param || val.depends) {
					var keepRule = true;
					switch (typeof val.depends) {
					case "string":
						keepRule = !!$(val.depends, getValidatorContainer(element)).length;
						break;
					case "function":
						keepRule = val.depends.call(element, element);
						break;
					}
					if (keepRule) {
						rules[prop] = val.param !== undefined ? val.param : true;
					} else {
						delete rules[prop];
					}
				}
			});

			// evaluate parameters
			$.each(rules, function(rule, parameter) {
				rules[rule] = $.isFunction(parameter) ? parameter(element) : parameter;
			});

			// clean number parameters
			$.each([ 'minlength', 'maxlength' ], function() {
				if (rules[this]) {
					rules[this] = Number(rules[this]);
				}
			});
			$.each([ 'rangelength', 'range' ], function() {
				var parts;
				if (rules[this]) {
					if ($.isArray(rules[this])) {
						rules[this] = [ Number(rules[this][0]), Number(rules[this][1]) ];
					} else if (typeof rules[this] === "string") {
						parts = rules[this].split(/[\s,]+/);
						rules[this] = [ Number(parts[0]), Number(parts[1]) ];
					}
				}
			});

			if ($.validator.autoCreateRanges) {
				// auto-create ranges
				if (rules.min && rules.max) {
					rules.range = [ rules.min, rules.max ];
					delete rules.min;
					delete rules.max;
				}
				if (rules.minlength && rules.maxlength) {
					rules.rangelength = [ rules.minlength, rules.maxlength ];
					delete rules.minlength;
					delete rules.maxlength;
				}
			}

			return rules;
		}
	});

	$.extend($.validator.prototype, {
		init : function() {
			this.labelContainer = $(this.settings.errorLabelContainer);
			this.errorContext = this.labelContainer.length && this.labelContainer || $(this.currentForm);
			this.containers = $(this.settings.errorContainer).add(this.settings.errorLabelContainer);
			this.submitted = {};
			this.valueCache = {};
			this.pendingRequest = 0;
			this.pending = {};
			this.invalid = {};
			this.reset();

			var groups = (this.groups = {});
			$.each(this.settings.groups, function(key, value) {
				if (typeof value === "string") {
					value = value.split(/\s/);
				}
				$.each(value, function(index, name) {
					groups[name] = key;
				});
			});
			var rules = this.settings.rules;
			$.each(rules, function(key, value) {
				rules[key] = $.validator.normalizeRule(value);
			});

			var _this = this;
			function delegate(event) {
				var $form = getValidatorContainer(this[0]);
				var validator = $.data($form, "validator"), eventType = "on" + event.type.replace(/^validate/, "");
				// add by wujx 20161101 begin
				// 避免form嵌套子form时，子form元素没有绑定校验规则，子form元素获取子form产生错误
				if (!validator || !validator.settings) {
					return;
				}
				// add by wujx 20161101 end

				if (validator.settings[eventType]) {
					validator.settings[eventType].call(validator, this[0], event);
				}

				// add by wujx 20160922 begin
				// 冒泡处理，如果是从表的控件，当从表某控件值发生变化后，触发从表校验
				try {
					var bubble = window.bubble;
					var ctrl = _this.getCurrentControl();
					var subformDefinition = ctrl.getFormDefinition();
					var subformcontrol = $($form).getSubformControl(subformDefinition.uuid);
					if (bubble && subformcontrol && subformcontrol.validateBlankRow()) {
						bubble.removeErrorItem(subformDefinition.uuid + '-blankrow');
					}
				} catch (e) {
					console.log(e);
				}
				// add by wujx 20160922 end
			}

			// modify by wujx 统一校验事件 reValidate
			$(this.currentForm).validateDelegate(
					":text, [type='password'], [type='file'], select, textarea, " + "[type='number'], [type='search'] ,[type='tel'], [type='url'], "
							+ "[type='email'], [type='datetime'], [type='date'], [type='month'], " + "[type='week'], [type='time'], [type='datetime-local'], "
							+ "[type='range'], [type='color'],[type='radio'], [type='checkbox'],select, option ", "reValidate", delegate);

			if (this.settings.invalidHandler) {
				$(this.currentForm).bind("invalid-form.validate", this.settings.invalidHandler);
			}
		},
		dependTypes : {
			"boolean" : function(param, element) {
				return param;
			},
			"string" : function(param, element) {
				return !!$(param, getValidatorContainer(element)).length;
			},
			"function" : function(param, element) {
				return param(element);
			}
		},
		// 单个控件的验证入口,为验证做准备及和善后处理
		control : function(control) {
			this.setCurrentControl(control)// 设置当前验证的控件
			var name = this.getName();
			this.reset();
			var element = control.get$InputElem()[0];
			if(element){
				this.settings.unhighlight.call( this, element, this.settings.errorClass, this.settings.validClass );
				//this.unhighlight(element, this.settings.errorClass, this.settings.validClass);
			}
			
			var result = this.validate() !== false;
			if (result) {
				delete this.invalid[name];// invalid用于保存所有验证没有通过的控件的唯一标识
			} else {
				this.invalid[name] = true;
			}
			return result;
		},

		// 设置当前验证的控件
		setCurrentControl : function(control) {
			this.currentCtl = control;// 当前控件
		},

		// 获取当前验证的控件
		getCurrentControl : function() {
			if (this.currentCtl == null) {
				throw new Error("请勿调用该方法,请通过调用control()进行验证");
			}
			return this.currentCtl;
		},

		// 获取当前控件对应的名称
		getName : function() {
			var currentCtl = this.getCurrentControl();
			/*
			 * if(currentCtl.isAttachCtl && currentCtl.isAttachCtl){//附件控件
			 * return currentCtl.$element.attr("name"); }
			 */
			return currentCtl.getCtlName();
		},

		// 获取控件的值
		getValue : function() {
			if (this.getCurrentControl().isAttachCtl && this.getCurrentControl().isAttachCtl()) {// 校验器对附件控件在检验时，只要确认是否已有做上传文件即可
				return WellFileUpload.files[this.getCurrentControl().getFielctlID()];
			} else {
				return this.getCurrentControl().getValue();
			}

		},

		getRules : function() {
			return this.getCurrentControl().getRule();
		},

		// 验证控件,通过返回true,不通过返回false
		validate : function() {
			var name = this.getName();
			var val = this.getValue();
			var rules = this.settings.rules[name];
			// rules = eval("(" + rules + ")");
			var result;

			for ( var method in rules) {
				// if(rules["isCustomizeRegularText"] != undefined &&
				// rules["isCustomizeRegularText"] != null &&
				// rules["isCustomizeRegularText"] != ""){
				// return this.optional(element) ||
				// pattern.test(rules["isCustomizeRegularText"] );
				// }else{
				var rule = {
					method : method,
					parameters : rules[method]
				};

				try {
					var element = this.getCurrentControl().get$InputElem()[0];
					result = $.validator.methods[method].call(this, val, element, rule.parameters);
					if (!result) {
						if (typeof element != "undefined")
							this.formatAndAdd(element, rule);
						return false;
					}
				} catch (e) {
					if (this.settings.debug && window.console) {
						console.log("Exception occurred when checking element " + name + ", check the '" + rule.method + "' method.", e);
					}
					throw e;
				}
				// }
			}
			return true;
		},

		findByName : function(name) {
			return $(this.currentForm).find(":input[name='" + name + "']");
		},

		getLength : function(value, element) {
			if (typeof element == "undefined") {
				if (typeof value == "string" || typeof value == Array) {
					return value.length;
				} else {
					return 0;
				}
			}
			switch (element.nodeName.toLowerCase()) {
			case "select":
				return $("option:selected", element).length;
			case "input":
				if (this.checkable(element)) {
					return this.findByName(element.name).filter(":checked").length;
				}
			}
			return value.length;
		},
		isElementDefinited : function(element) {
			if (element == null || typeof element == "undefined") {
				return false;
			} else {
				return true;
			}
		},
		optional : function(element) {
			if (!this.isElementDefinited(element)) {
				return true;
			} else {
				var val = this.elementValue(element);
				return !$.validator.methods.required.call(this, val, element);
			}
		},

	});

	$.extend($.validator.defaults, {
		onchange : function(element, event) {
			// click on selects, radiobuttons and checkboxes
			if (element.name in this.submitted) {
				this.element(element);
			}
			// or option elements, check parent select in that case
			else if (element.parentNode.name in this.submitted) {
				this.element(element.parentNode);
			}
		},
		// add by wujx 20161010
		onreValidate : function(element) {
			// click on selects, radiobuttons and checkboxes
			if (element.name in this.submitted || element === this.lastElement) {
				this.element(element);
			}
			// or option elements, check parent select in that case
			else if (element.parentNode && element.parentNode.name in this.submitted) {
				this.element(element.parentNode);
			}
		}
	});

	var methods = {
		// http://docs.jquery.com/Plugins/Validation/Methods/required
		required : function(value, element, param) {

			if (this.currentCtl != null) {// 表单控件验证
				var isAttachCtl = this.getCurrentControl().isAttachCtl && this.getCurrentControl().isAttachCtl();
			}

			if (isAttachCtl) {
				// 附件控件另外处理
				if (value == null || value == undefined) {
					return false;
				} else if (value instanceof Array) {
					if (value.length > 0) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return $.trim(value).length > 0;
			}
		},

		// http://docs.jquery.com/Plugins/Validation/Methods/email
		email : function(value, element) {
			var pattern = /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i;
			// modify by wujx 20161012 begin
			// 注释掉可编辑元素是否已定义判断，对于从表控件初始化为标签状态
			// if (this.isElementDefinited(element)) {
			// contributed by Scott Gonzalez:
			// http://projects.scottsplayground.com/email_address_validation/
			return this.optional(element) || pattern.test(value);
			// } else {
			// return pattern.test(value);
			// }
			// modify by wujx 20161012 end
		},

		// http://docs.jquery.com/Plugins/Validation/Methods/url
		url : function(value, element) {
			// contributed by Scott Gonzalez:
			// http://projects.scottsplayground.com/iri/
			return this.optional(element)
					|| /^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i
							.test(value);
		},

		// http://docs.jquery.com/Plugins/Validation/Methods/date
		date : function(value, element) {
			return this.optional(element) || !/Invalid|NaN/.test(new Date(value).toString());
		},

		// http://docs.jquery.com/Plugins/Validation/Methods/dateISO
		dateISO : function(value, element) {
			return this.optional(element) || /^\d{4}[\/\-]\d{1,2}[\/\-]\d{1,2}$/.test(value);
		},

		// http://docs.jquery.com/Plugins/Validation/Methods/number
		number : function(value, element) {
			return this.optional(element) || /^-?(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/.test(value);
		},

		// http://docs.jquery.com/Plugins/Validation/Methods/digits
		digits : function(value, element) {
			return this.optional(element) || /^\d+$/.test(value);
		},

		// http://docs.jquery.com/Plugins/Validation/Methods/creditcard
		// based on http://en.wikipedia.org/wiki/Luhn
		creditcard : function(value, element) {
			if (this.optional(element)) {
				return "dependency-mismatch";
			}
			// accept only spaces, digits and dashes
			if (/[^0-9 \-]+/.test(value)) {
				return false;
			}
			var nCheck = 0, nDigit = 0, bEven = false;

			value = value.replace(/\D/g, "");

			for (var n = value.length - 1; n >= 0; n--) {
				var cDigit = value.charAt(n);
				nDigit = parseInt(cDigit, 10);
				if (bEven) {
					if ((nDigit *= 2) > 9) {
						nDigit -= 9;
					}
				}
				nCheck += nDigit;
				bEven = !bEven;
			}

			return (nCheck % 10) === 0;
		},

		// http://docs.jquery.com/Plugins/Validation/Methods/minlength
		minlength : function(value, element, param) {
			var length = $.isArray(value) ? value.length : this.getLength($.trim(value), element);
			return this.optional(element) || length >= param;
		},

		// http://docs.jquery.com/Plugins/Validation/Methods/maxlength
		maxlength : function(value, element, param) {
			var length = $.isArray(value) ? value.length : this.getLength($.trim(value), element);
			return this.optional(element) || length <= param;
		},

		// http://docs.jquery.com/Plugins/Validation/Methods/rangelength
		rangelength : function(value, element, param) {
			var length = $.isArray(value) ? value.length : this.getLength($.trim(value), element);
			return this.optional(element) || (length >= param[0] && length <= param[1]);
		},

		// http://docs.jquery.com/Plugins/Validation/Methods/min
		min : function(value, element, param) {
			return this.optional(element) || value >= param;
		},

		// http://docs.jquery.com/Plugins/Validation/Methods/max
		max : function(value, element, param) {
			return this.optional(element) || value <= param;
		},

		// http://docs.jquery.com/Plugins/Validation/Methods/range
		range : function(value, element, param) {
			return this.optional(element) || (value >= param[0] && value <= param[1]);
		},

		// http://docs.jquery.com/Plugins/Validation/Methods/equalTo
		equalTo : function(value, element, param) {
			// bind to the blur event of the target in order to
			// revalidate whenever the target field is updated
			// TODO find a way to bind the event just once,
			// avoiding the unbind-rebind overhead
			var target = $(param);
			if (this.settings.onfocusout) {
				target.unbind(".validate-equalTo").bind("blur.validate-equalTo", function() {
					$(element).valid();
				});
			}
			return value === target.val();
		},

		// http://docs.jquery.com/Plugins/Validation/Methods/remote
		remote : function(value, element, param) {
			if (this.optional(element)) {
				return "dependency-mismatch";
			}

			var previous = this.previousValue(element);
			if (!this.settings.messages[element.name]) {
				this.settings.messages[element.name] = {};
			}
			previous.originalMessage = this.settings.messages[element.name].remote;
			this.settings.messages[element.name].remote = previous.message;

			param = typeof param === "string" && {
				url : param
			} || param;

			if (previous.old === value) {
				return previous.valid;
			}

			previous.old = value;
			var validator = this;
			this.startRequest(element);
			var data = {};
			data[element.name] = value;
			$.ajax($.extend(true, {
				url : param,
				mode : "abort",
				port : "validate" + element.name,
				dataType : "json",
				data : data,
				success : function(response) {
					validator.settings.messages[element.name].remote = previous.originalMessage;
					var valid = response === true || response === "true";
					if (valid) {
						var submitted = validator.formSubmitted;
						validator.prepareElement(element);
						validator.formSubmitted = submitted;
						validator.successList.push(element);
						delete validator.invalid[element.name];
						validator.showErrors();
					} else {
						var errors = {};
						var message = response || validator.defaultMessage(element, "remote");
						errors[element.name] = previous.message = $.isFunction(message) ? message(value) : message;
						validator.invalid[element.name] = true;
						validator.showErrors(errors);
					}
					previous.valid = valid;
					validator.stopRequest(element, valid);
				}
			}, param));
			return "pending";
		}
	};

	$.extend($.validator.methods, methods);
});
