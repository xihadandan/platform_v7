(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery' ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($) {
	"use strict";
	// 核心类
	$.widget("ui.wCommonCore", {
		version : "5.3",
		options : {},
		_create : function() {
			var self = this;
			self.$element = $(self.element).uniqueId();
			self.$element.addClass("wCommon");
			self._render();
			self._rendered();
			// self._setOptions();
		},
		_render : $.noop,
		_rendered : $.noop,
		_init : function() {
			var self = this, p = this.options;

			self._inited();
			self._setOptions(p);
		},
		_inited : function() {

		},
		_setOptions : function(options) {
			var self = this;
			// 控件初始化时，设置参数
			for ( var key in options) {
				self.setOption(key, options[key]);
			}
		},
		setOption : function(key, value) {
			var self = this;
			// 设置参数
			this.options[key] = value;
			var setMethod = "_set" + key.replace(/(^|\s+)\w/g, function(s) {
				// 首字母大写
				return s.toUpperCase();
			});
			if (self[setMethod]) {
				self[setMethod].call(this, value);
			}
		}
	});

	// INPUT基类
	$.widget("ui.wCommonInput", $.ui.wCommonCore, {
		_setValue : function(value) {
			var self = this;
			self.$element.val(value);
		},
		_getValue : function() {
			var self = this;
			self.$element.val();
		},
		setValue : function(value) {
			var self = this;
			self._setValue.apply(self, arguments);
		},
		getValue : function() {
			var self = this;
			return self._getValue();
		}
	});

	// 弹窗基类
	$.wCommonBaseDialog = {
		version : '5.3'
	}
}));