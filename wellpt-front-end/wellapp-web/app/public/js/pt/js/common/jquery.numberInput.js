;
(function($) {
	/*
	 * NUMBERINPUT CLASS DEFINITION ======================
	 */
	var NumberInput = function(element, options) {
		this.init("numberInput", element, options);
	};

	var MAX_INT_VALUE = new Number(2147483647);
	var MIN_INT_VALUE = new Number(-2147483648);
	var MAX_LONG_VALUE = new Number(9223372036854775807);
	var MIN_LONG_VALUE = new Number(-9223372036854775808);
	var MAX_DOUBLE_VALUE = new Number(1.7976931348623157E308);
	var MIN_DOUBLE_VALUE = new Number(4.9E-324);
	var KEY_CODE_MAP = {
		48 : "0",
		49 : "1",
		50 : "2",
		51 : "3",
		52 : "4",
		53 : "5",
		54 : "6",
		55 : "7",
		56 : "8",
		57 : "9"
	};

	NumberInput.prototype = {
		constructor : NumberInput,
		init : function(type, element, options) {
			this.type = type;
			this.$element = $(element);
			this.options = this.getOptions(options);
			this.$element.keydown($.proxy(this._preCheckInput, this));
			this.$element.keyup($.proxy(this._postCheckInput, this));
		},
		getOptions : function(options) {
			options = $.extend({}, $.fn[this.type].defaults, options,
					this.$element.data());
			return options;
		},
		_preCheckInput : function(event) {
			this.oldValue = this.$element.val();
//			alert(JSON.stringify(event.keyCode));
			// Allow: backspace, delete, tab and escape
			if (event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9
					|| event.keyCode == 27 ||
					// Allow: Ctrl+A
					(event.keyCode == 65 && event.ctrlKey === true) ||
					// Allow: home, end, left, right
					(event.keyCode >= 35 && event.keyCode <= 39)) {
				// let it happen, don't do anything
				return true;
			}
			// "-" 189
			if (event.keyCode == 189 && this.options.negative == true) {
				return true;
			}
			// "." 190
			if (event.keyCode == 190 && this.options.dataType == "double") {
				return true;
			}
			if (!(event.keyCode >= 48 && event.keyCode <= 57) && !(event.keyCode >= 95 && event.keyCode <= 105)) {
				return false;
			}
		},
		_postCheckInput : function(event) {
			var value = this.$element.val();
			if (this.options.negative == true && value == "-") {
				return true;
			}
			
			if(this.$element.val() == ""){
				return true;
			}

			var newValue = Number(this.$element.val());
			if (!isNumeric(newValue)) {
				this.$element.val(this.oldValue);
				return false;
			}
			var dataType = this.options.dataType;
			if (dataType === "int") {
				if (value.indexOf(".") != -1) {
					this.$element.val(this.oldValue);
					return false;
				}
				if (newValue > MAX_INT_VALUE || newValue < MIN_INT_VALUE) {
					this.$element.val(this.oldValue);
					return false;
				}
			} else if (dataType === "long") {
				if (value.indexOf(".") != -1) {
					this.$element.val(this.oldValue);
					return false;
				}
				if (newValue > MAX_LONG_VALUE || newValue < MIN_LONG_VALUE) {
					this.$element.val(this.oldValue);
					return false;
				}
			} else if (dataType === "double") {
				if (newValue > MAX_DOUBLE_VALUE || newValue < MIN_DOUBLE_VALUE) {
					this.$element.val(this.oldValue);
					return false;
				}
			} else {
				this.$element.val(this.oldValue);
				return false;
			}
		}
	};

	function isNumeric(obj) {
		return !isNaN(parseFloat(obj)) && isFinite(obj);
	}
	/*
	 * NUMBERINPUT PLUGIN DEFINITION =========================
	 */
	$.fn.numberInput = function(option) {
		return this
				.each(function() {
					var $this = $(this), data = $this.data('numberInput'), options = typeof option == 'object'
							&& option;
					if (!data) {
						$this.data('numberInput', (data = new NumberInput(this,
								options)));
					}
					if (typeof option == 'string') {
						data[option]();
					}
				});
	};

	$.fn.numberInput.Constructor = NumberInput;

	$.fn.numberInput.defaults = {
		decimal : 0,
		dataType : "double",// int,long,double
		format : false,
		pattern : null,
		negative : true
	};
})(jQuery);