(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery', 'commons', 'server', 'constant' ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, commons, server, constant) {
	"use strict";
	var UUID = commons.UUID;
	var StringBuilder = commons.StringBuilder;
	$.widget("ui.wPersonalDocument", $.ui.wWidget, {
		options : {
			// 组件定义
			widgetDefinition : {},
			// 上级容器定义
			containerDefinition : {}
		},
		_createView : function() {
			this._renderView();
			this._bindEvents();
		},
		_renderView : function() {
			// 生成导航
			this._createNav();
		},
		// 生成导航
		_createNav : function() {
			var _self = this;
			var $nav = $(".personal-document-nav", _self.element);
			var configuration = _self.getConfiguration();
			var nav = configuration.nav;
			var navHtml = new StringBuilder();
			var menuId = UUID.createUUID();
			navHtml.appendFormat('<div class="metismenu ui-wLeftSidebar">');
			navHtml.appendFormat('	<aside class="metismenu sidebar">');
			navHtml.appendFormat('		<nav class="metismenu sidebar-nav">');
			navHtml.appendFormat('			<ul class="metismenuul" id="{0}">', menuId);
			$.each(nav, function(i) {
				var menuItem = this;
				navHtml.appendFormat('<li class="nav-menu-item subnav-menu-item" isparent="0">');
				navHtml.appendFormat('<a href="#" >{0}</a>', menuItem.text)
				navHtml.appendFormat('</li>');
			});
			navHtml.appendFormat('			</ul>');
			navHtml.appendFormat('		</nav>');
			navHtml.appendFormat('	</aside>');
			navHtml.appendFormat('</div>');
			$nav.append(navHtml.toString());
			_self.$nav = $nav;
			// 生成导航插件
			$(".metismenuul", $nav).metisMenu({
				toggle : false
			});
		},
		// 绑定事件
		_bindEvents : function() {
			var _self = this;
			// 监听容器创建完成事件
			_self.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function(e, ui) {
			});
			// 监听左导航事件
			_self.on(constant.WIDGET_EVENT.LeftSidebarItemClick, function(e, ui) {
			});
		}
	});
}));