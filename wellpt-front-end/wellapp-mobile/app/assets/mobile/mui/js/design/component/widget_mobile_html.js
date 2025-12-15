define([ "ui_component", "design_commons", "constant", "commons", "summernote", "wSelect2" ], function(ui_component,
		designCommons, constant, commons) {
	var component = $.ui.component.BaseComponent();
	component.prototype.create = function() {
		$(this.element).css("overflow", "auto");
	}
	component.prototype.usePropertyConfigurer = function() {
		return true;
	}
	component.prototype.getPropertyConfigurer = function() {
		var collectClass = "w-configurer-option";
		var configurer = $.ui.component.BaseComponentConfigurer();
		configurer.prototype.onLoad = function($container, options) {
			// 初始化页签项
			$("#widget_html_tabs ul a", $container).on("click", function(e) {
				e.preventDefault();
				$(this).tab("show");
			})
			var configuration = $.extend(true, {}, options.configuration);
			this.initConfiguration(configuration, $container);
		}
		// 初始化配置信息
		configurer.prototype.initConfiguration = function(configuration, $container) {
			// 基本信息
			this.initBaseInfo(configuration, $container);
			// 内容信息
			this.initContentInfo(configuration, $container);
		};
		// 初始化配置信息
		configurer.prototype.initBaseInfo = function(configuration, $container) {
			// 设置值
			designCommons.setElementValue(configuration, $container);

			// 加载的JS模块
			$("#jsModule", $container).wSelect2({
				serviceName : "appJavaScriptModuleMgr",
				params : {
					dependencyFilter : "HtmlWidgetDevelopment"
				},
				labelField : "jsModuleName",
				valueField : "jsModule",
				remoteSearch : false,
				multiple : true
			});
		};
		// 初始化内容信息
		configurer.prototype.initContentInfo = function(configuration, $container) {
			var component = this.component;
			var options = component.options || {};
			var content = configuration.content || options.content;
			$("#wHtml_content").summernote({
				height : "300px"
			});
			$("#wHtml_content").summernote("code", content);
		};

		configurer.prototype.onOk = function($container) {
			this.component.options.configuration = this.collectConfiguration($container);
		}
		// 收集配置信息
		configurer.prototype.collectConfiguration = function($container) {
			var configuration = {};
			// 基本信息
			this.collectBaseInfo(configuration, $container);
			// 内容信息
			this.collectContentInfo(configuration, $container);
			return $.extend({}, configuration);
		};
		configurer.prototype.collectBaseInfo = function(configuration, $container) {
			var $form = $("#widget_html_tabs_base_info", $container);
			var opt = designCommons.collectConfigurerData($form, collectClass);
			$.extend(configuration, opt);
		};
		configurer.prototype.collectContentInfo = function(configuration, $container) {
			var $form = $("#widget_html_tabs_content_info", $container);
			var opt = designCommons.collectConfigurerData($form, collectClass);

			var content = $("#wHtml_content").summernote("code");
			opt.content = content;
			$.extend(configuration, opt);
		};
		return configurer;
	}
	component.prototype.toHtml = function() {
		var _self = this;
		var options = _self.options;
		var configuration = options.configuration || {};
		var content = configuration.content || options.content;
		var id = _self.getId();
		var html = '<div id="' + id + '" class="ui-wHtml">' + content + '</div>';
		return html;
	}
	component.prototype.getDefinitionJson = function() {
		var _self = this;
		var options = _self.options;
		var id = _self.getId();
		options.id = id;
		return options;
	}
	return component;
});