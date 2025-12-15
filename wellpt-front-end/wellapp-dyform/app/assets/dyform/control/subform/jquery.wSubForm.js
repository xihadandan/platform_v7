;
(function($) {
	/*
	 * SUBFORM CLASS DEFINITION ======================
	 */
	var SubForm = function(element, options) {
		this.$element = $(element);
		this.options = $.extend({}, $.fn["wsubForm"].defaults, options, this.$element.data());
	};

	SubForm.prototype = {
		constructor : SubForm,
		_init : function() {
			
		}
	};

	/*
	 * SUBFORM PLUGIN DEFINITION =========================
	 */
	$.fn.wsubForm = function(option) {
		var method = false;
		var args = null;
		if (arguments.length == 2) {
			method = true;
			args = arguments[1];
		}

		if (typeof option == 'string') {
			if (option === 'getObject') { // 通过getObject来获取实例
				var $this = $(this);
				var data = $this.data('wsubForm');
				if (data) {
					return data; // 返回实例对象
				} else {
					throw new Error('This object is not available');
				}
			}
		}

		return this.each(function() {
			var $this = $(this), data = $this.data('wsubForm'), options = typeof option == 'object' && option;
			if (!data) {
				data = new SubForm(this, options);
				var formUuid = options.formUuid;
				var subform = options.formDefinition.subforms[formUuid];
				var data1 = $.extend(data, $.wSubFormMethod, (subform && subform.layoutType == "1") ? $.wSubFormMethod2 : {});
				data1.init();
				$this.data('wsubForm', data1);
			}
			if (typeof option == 'string') {
				if (method == true && args != null) {
					return data[option](args);
				} else {
					return data[option]();
				}
			}

		});
	};

	$.fn.wsubForm.Constructor = SubForm;

	$.fn.wsubForm.defaults = {
		$paranentelement : {},
		formDefinition : '',
		subformDefinition : '',
		readOnly : false,
		mainformDataUuid : '',
		formUuid : ''
	};

})(jQuery);