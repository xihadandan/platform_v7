(function(factory) {
	/* global define */
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery', 'appContext' ], factory);
	} else if (typeof module === 'object' && module.exports) {
		// Node/CommonJS
		module.exports = factory(require('jquery'));
	} else {
		// Browser globals
		factory(window.jQuery);
	}
}(function($, appContext) {
	'use strict';
	var DmsDocumentView = function(element, options) {
		var _self = this;
		_self.options = options;
		_self.$element = $(element);
		_self._createDocumentView();
	}
	// 创建流程二开
	DmsDocumentView.prototype._createDocumentView = function() {
		var _self = this;
		_self._create();
		_self._init();
		_self._loadData();
	}
	// 创建并绑定事件
	DmsDocumentView.prototype._create = function() {
		var _self = this;
		var documentViewModule = _self.options.documentViewModule;
		_self.documentView = new documentViewModule(_self.$element);
		_self.documentView.$element = _self.$element;
	}
	// 初始化
	DmsDocumentView.prototype._init = function() {
		this.documentView.init(this.options);
	}
	// 加载数据
	DmsDocumentView.prototype._loadData = function() {
		this.documentView.load();
	}

	// jQuery插件
	$.fn.dmsDocumentView = function(options) {
		return this.each(function() {
			var $this = $(this);
			if (!$this.data('DmsDocumentView')) {
				options = $.extend(true, {}, $.fn.dmsDocumentView.defaults, options);
				$this.data('DmsDocumentView', new DmsDocumentView($this, options));
			}
		});
	};

	// jQuery插件默认参数
	$.fn.dmsDocumentView.defaults = {
		documentViewModule : null,// 单据二开模块
		dyformSelector : "#dyform",// 表单选择器
		toolbarPlaceholder : ".dms-widget-toolbar"// 操作按钮占位符
	};
}));