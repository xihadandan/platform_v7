define([ "uuid", "commons", "bootstrapTable", "bootstrap-table-treegrid" ], function(uuidUtils, commons, bt) {
	(function($) {
		/*
		 * SUBFORM CLASS DEFINITION ======================
		 */
		var SubForm4Group = function(element, options) {
			this.$element = $(element);
			this.options = $.extend({}, $.fn["wsubForm4Group"].defaults, options, this.$element.data());
		};
	
		SubForm4Group.prototype = {
			constructor : SubForm4Group,
			_init : function() {
	
			}
		};
	
		/*
		 * SUBFORM PLUGIN DEFINITION =========================
		 */
		$.fn.wsubForm4Group = function(option) {
			var method = false;
			var args = null;
			if (arguments.length == 2) {
				method = true;
				args = arguments[1];
			}
	
			if (typeof option == 'string') {
				if (option === 'getObject') { // 通过getObject来获取实例
					var $this = $(this);
					var data = $this.data('wsubForm4Group');
					if (data) {
						return data; // 返回实例对象
					} else {
						throw new Error('This object is not available');
					}
				}
			}
	
			return this.each(function() {
				var $this = $(this), data = $this.data('wsubForm4Group'), options = typeof option == 'object' && option;
				if (!data) {
					data = new SubForm4Group(this, options);
					var datacopy = {};
					var data1 = $.extend(datacopy, data);
					var extenddata = $.extend(data, $.wSubFormMethod);
					var data2 = $.extend(extenddata, data1);
					data2.init();
					$this.data('wsubForm4Group', data2);
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
	
		$.fn.wsubForm4Group.Constructor = SubForm4Group;
	
		$.fn.wsubForm4Group.defaults = {
			$paranentelement : {},
			formDefinition : '',
			subformDefinition : '',
			readOnly : false,
			mainformDataUuid : '',
			formUuid : '',
			groupField : '',
			groupOrder : 'asc',
			groupColumnShow : true,
			groupCollapse : false
		};
	
	})(jQuery);	
});
