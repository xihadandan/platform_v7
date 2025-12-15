define([ "jquery", "constant", "commons", "server", "appContext" ], function($, commons, constant, server, appContext) {
	// 页面组件二开基础
	var WidgetDevelopment = function(widget) {
		this.widget = widget;
	};
	// 接口方法
	$.extend(WidgetDevelopment.prototype, {
		// 返回组件对象
		getWidget : function() {
			return this.widget;
		},
		//返回组件容器
		getWidgetElement:function(){
			return this.widget.element;
		},
		//返回组件参数
        getWidgetParams : function() {
            return this.widget.options.widgetDefinition.params || {};
        },
		// 返回组件的上级组件
		getParentWidget : function() {
			var _self = this;
			var $element = $(_self.widget.element);
			var $parent = $element.parent();
			var parentWidget = null;
			while ($parent != null) {
				var parentId = $parent.attr("id");
				parentWidget = appContext.getWidgetById(parentId);
				if (parentWidget != null) {
					break;
				}
				$parent = $parent.parent();
			}
			if (parentWidget == null) {
				return _self.widget.pageContainer;
			}
			return parentWidget;
		},
		// 返回页面容器
		getPageContainer : function() {
			return this.widget.getPageContainer();
		},
		// 组件准备
		prepare : function() {
		},
		// 组件创建
		create : function() {
		},
		// 组件初始化
		init : function() {
		},
		// 调用了未知的二开方法回调
		onUnkonwnDevelopmentMethodInvoked : function(method, methodArgs) {
		}
	});
	return WidgetDevelopment;
});