;
(function($) {

	var columnProperty = {
		// 控件字段属性
		applyTo : null,// 应用于
		columnName : null,// 字段定义 fieldname
		displayName : null,// 描述名称 descname
		dbDataType : '',// 字段类型 datatype type
		indexed : null,// 是否索引
		showed : null,// 是否界面表格显示
		sorted : null,// 是否排序
		sysType : null,// 系统定义类型，包括三种（0：系统默认，1：管理员常量定义，2：表单添加后自定义）
		length : null,// 长度
		showType : '1',// 显示类型 1,2,3,4 datashow
		defaultValue : null,// 默认值
		valueCreateMethod : '1',// 默认值创建方式 1用户输入
		onlyreadUrl : null,// 只读状态下设置跳转的url
	};

	// 控件公共属性
	var commonProperty = {
		inputMode : null,// 输入样式 控件类型 inputDataType
		fieldCheckRules : null,
		fontSize : null,// 字段的大小
		fontColor : null,// 字段的颜色
		ctlWidth : null,// 宽度
		ctlHight : null,// 高度
		textAlign : null,// 对齐方式

	};

	/*
	 * DIALOG CLASS DEFINITION ======================
	 */
	var TimeEmploy = function($placeHolder, options) {
		this.options = $.extend({}, $.fn["wtimeEmploy"].defaults, options);
		this.value = "";
		this.$editableElem = null;
		this.$labelElem = null;
		this.$placeHolder = $placeHolder;

	};

	TimeEmploy.prototype = {
		constructor : TimeEmploy
	};

	$.TimeEmploy = {

		createEditableElem : function() {
			if (this.$editableElem != null) {// 创建可编辑框
				return;
			}
			var options = this.options;
			var ctlName = this.getCtlName();
			var editableElem = document.createElement("input");
			editableElem.setAttribute("class", this.editableClass);
			editableElem.setAttribute("name", ctlName);
			editableElem.setAttribute("type", "text");

			$(editableElem).css(this.getTextInputCss());

			this.$placeHolder.after($(editableElem));
			this.$editableElem = this.$placeHolder.next("." + this.editableClass);
			this.$editableElem.addClass("input-search");// css in wellnewoa.css
			var _this = this;
			this.create$DialogElement();

		},

		bind : function(event, callback) {
			this.options[event] = callback;
		},

		create$DialogElement : function() {
			var _this = this;
			this.$editableElem.unbind("click");
			this.$editableElem.click(function() {
				var json = {
					"beginTime" : "",
					"endTime" : "",
					"employName" : "",
					"uuid" : "",
					"resCode" : _this.options.resCode
				};

				if ($.trim(_this.value).length > 0) {
					if (typeof _this.value == "object") {
						json = _this.value;
					} else {
						json = eval("(" + _this.value + ")");
					}
				}

				_this.$editableElem.myTimeEmploy(json, function(data) {

					if (typeof data == "string") {
						_this.setValue(data);
					} else {
						_this.setValue(JSON.cStringify(data));
					}

					if (_this.options.afterResourceSelect) {
						_this.options.afterResourceSelect.call(_this, _this.getCtlName(), data);
					}

				});
			});

		},
		/* 设值到标签中 */
		setValue2LabelElem : function() {
			if (this.$labelElem == null) {
				return;
			}
			this.$labelElem.html(this.getDisplayValue());
			this.$labelElem.attr("title", this.getDisplayValue());
		},

		getDisplayValue : function() {
			if (this.value == null || $.trim(this.value).length == 0) {
				return "";
			}
			var val = eval("(" + this.value + ")");
			if (typeof val.uuid == "undefined" || $.trim(val.uuid).length == 0) {
				return "";
			}
			if (typeof val.uuid == "undefined" || $.trim(val.uuid).length == 0) {
				return "";
			}
			return val.employName + "(" + val.beginTime + "至" + val.endTime + ")"
		},

		/* 设置到可编辑元素中 */
		setValue2EditableElem : function() {
			if (this.$editableElem == null) {
				return;
			}
			this.$editableElem.val(this.getDisplayValue());
		}

	};

	/*
	 * DIALOG PLUGIN DEFINITION =========================
	 */
	$.fn.wtimeEmploy = function(option) {
		var method = false;
		var args = null;
		if (arguments.length == 2) {
			method = true;
			args = arguments[1];
		}

		if (typeof option == 'string') {
			if (option === 'getObject') { // 通过getObject来获取实例
				var $this = $(this);
				data = $this.data('wtimeEmploy');
				if (data) {
					return data; // 返回实例对象
				} else {
					throw new Error('This object is not available');
				}
			}
		}

		var $this = $(this), data = $this.data('wtimeEmploy'), options = typeof option == 'object' && option;
		if (!data) {
			data = new TimeEmploy($(this), options);
			$.extend(data, $.wControlInterface);
			$.extend(data, $.wTextCommonMethod);
			$.extend(data, $.TimeEmploy);
			data.init();
			$this.data('wtimeEmploy', data);
		}
		if (typeof option == 'string') {
			if (method == true && args != null) {
				return data[option](args);
			} else {
				return data[option]();
			}
		} else {
			return data;
		}

	};

	$.fn.wtimeEmploy.Constructor = TimeEmploy;

	$.fn.wtimeEmploy.defaults = {
		columnProperty : columnProperty,// 字段属性
		commonProperty : commonProperty,// 公共属性
		readOnly : false,
		isShowAsLabel : false,
		disabled : false,
		isHide : false,// 是否隐藏
		afterResourceSelect : function() {
		},
		resCode : timeResouceType.MEET_RESOURCE
	};

})(jQuery);