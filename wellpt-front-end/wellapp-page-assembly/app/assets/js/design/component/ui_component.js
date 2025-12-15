define([ "jquery", "jquery-ui", "commons" ], function($, jqueryui, commons) {
	$.ui.component = $.ui.component || {};

	// 属性配置器
	var BaseComponentConfigurer = function(component) {
		this.component = component;
	};
	// 获取属性配置的内容，若为空会调用getTemplateUrl获取内容
	BaseComponentConfigurer.prototype.getTemplate = function() {
		return null;
	};
	// 获取属性配置的地址
	BaseComponentConfigurer.prototype.getTemplateUrl = function() {
		var wtype = this.component.options.wtype.replace(/([A-Z])/g, "_$1").toLowerCase();
		wtype = wtype.replace("__", "_");
		return ctx + "/web/design/component_config/widget" + wtype.substring(1);
	};
	// 配置开始
	BaseComponentConfigurer.prototype.onLoad = function($container, options) {
	};
	// 引用组件定义
	BaseComponentConfigurer.prototype.onReferenceWidgetDefinition = function($container, options) {
		this.onLoad($container, options);
	};
	// 从组件定义加载
	BaseComponentConfigurer.prototype.onLoadFromWidgetDefinition = function($container, options) {
		this.onLoad($container, options);
	};
	// 确定
	BaseComponentConfigurer.prototype.onOk = function($container) {
	};
	// 清空
	BaseComponentConfigurer.prototype.onClear = function($container, options) {
		this.onLoad($container, options);
	};
	// 重置
	BaseComponentConfigurer.prototype.onReset = function($container, options) {
		this.onLoad($container, options);
	};
	// 关闭/取消
	BaseComponentConfigurer.prototype.onCancel = function() {
	};
	// 基本组件对象
	$.ui.component.BaseComponentConfigurer = function(prototype) {
		var configurer = function(component) {
			BaseComponentConfigurer.call(this, component);
		};
		commons.inherit(configurer, BaseComponentConfigurer, prototype);
		return configurer;
	};
	
	// 门户属性配置器
    var BaseComponentPortalPropertyConfigurer = function(component) {
        this.component = component;
    };
    // 配置开始
    BaseComponentPortalPropertyConfigurer.prototype.onLoad = function($container, options) {
    };
    // 确定
    BaseComponentPortalPropertyConfigurer.prototype.onOk = function($container) {
    };
    // 基本组件对象
    $.ui.component.BaseComponentPortalPropertyConfigurer = function(prototype) {
        var configurer = function(component) {
            BaseComponentPortalPropertyConfigurer.call(this, component);
        };
        commons.inherit(configurer, BaseComponentPortalPropertyConfigurer, prototype);
        return configurer;
    };

	// 基本组件对象
	$.ui.component.BaseComponent = function() {
		// 构造函数
		var BaseComponent = function(element, options) {
			this.element = $(element);
			this.options = options;
		};
		// 创建
		BaseComponent.prototype.create = function() {
		};
		// 刷新
		BaseComponent.prototype.refresh = function() {
		};
		// 事件监听
		BaseComponent.prototype.on = function() {
		};
		// 获取组件选项
		BaseComponent.prototype.getOptions = function() {
			return this.options;
		};
		// 设置选项值
		BaseComponent.prototype.setOption = function(key, value) {
			this.options[key] = value;
		};
		// 设置页面设计器
		BaseComponent.prototype.setPageDesigner = function(pageDesigner) {
			this.pageDesigner = pageDesigner;
		};
		// 返回页面设计器
		BaseComponent.prototype.getPageDesigner = function() {
			return this.pageDesigner;
		};
		// 是否使用组件配置器，true使用getPropertyConfigurer返回的配置器，false使用configure方法的实现
		BaseComponent.prototype.usePropertyConfigurer = function() {
			return false;
		};
		// 组件配置
		BaseComponent.prototype.configure = function() {
		};
		// 获取属性配置器
		BaseComponent.prototype.getPropertyConfigurer = function() {
			return $.ui.component.BaseComponentConfigurer();
		};
		// 设置属性配置器
		BaseComponent.prototype.setPropertyConfigurer = function(configurer) {
			this.configurer = configurer;
		};
		// 是否引用组件
		BaseComponent.prototype.isReferenceWidget = function() {
			return commons.StringUtils.isNotBlank(this.options.refWidgetDefUuid);
		};
		// 组件销毁
		BaseComponent.prototype.destroy = function() {
			var _self = this;
			var parent = _self.parent;
			if (parent != null) {
				var children = parent.getChildren();
				var newChildren = [];
				for (var index = 0; index < children.length; index++) {
					var child = children[index];
					if (_self === child) {
						continue;
					}
					newChildren.push(child);
				}
				parent.setChildren(newChildren);

				// 回调子结点删除
				parent.childDestroy(_self);
			}
			_self.setParent(null);
			$(_self.element).remove();
		};
		// 组件子结点销毁
		BaseComponent.prototype.childDestroy = function(child) {
		};
		// 设置父结点
		BaseComponent.prototype.setParent = function(parent) {
			this.parent = parent;
		};
		// 获取父结点
		BaseComponent.prototype.getParent = function() {
			return this.parent;
		};
		// 添加子结点
		BaseComponent.prototype.addChild = function(child) {
			if (this.children == null) {
				this.children = [];
			}
			this.children.push(child);

			child.setParent(this);
		};
		// 获取子结点
		BaseComponent.prototype.getChildren = function() {
			if (this.children == null) {
				this.children = [];
			}
			return this.children;
		};
		// 设置子结点
		BaseComponent.prototype.setChildren = function(children) {
			this.children = children;
		};
		// 更新子结点
		BaseComponent.prototype.updateChildren = function($children) {
			var parent = this;
			var children = [];
			$.each($children, function() {
				var component = $(this).data("component");
				if (component != null) {
					component.setParent(parent);
					children.push(component);
				}
			});
			parent.setChildren(children);
		};
		// 返回组件定义HTML
		BaseComponent.prototype.toHtml = function() {
			var id = this.getId();
			var widgetHtml = "<div id='" + id + "'></div>";
			return widgetHtml;
		};
		// 返回组件定义JSON
		BaseComponent.prototype.getDefinitionJson = function() {
			var json = this.options;
			if (json.id == null) {
				json.id = this.getId();
			}
			return json;
		};
		// 返回组件ID
		BaseComponent.prototype.getId = function() {
			var options = this.options;
			if (options.id != null) {
				return options.id;
			}
			var wtype = options.wtype;
			var id = wtype + "_" + commons.UUID.createUUID();
			return id;
		};
		return BaseComponent;
	};
	return $.ui.component;
});