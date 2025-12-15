(function(factory) {
    "use strict";
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define([ 'jquery', 'constant', 'commons', 'appContext' ], factory);
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function($, constant, commons, appContext) {
    "use strict";
    var StorageUtils = commons.StorageUtils;
    $.widget("ui.wPage", $.ui.wWidget, {
        options : {
            // 组件定义
            widgetDefinition : {},
            // 上级容器定义
            containerDefinition : {}
        },
        _createView : function() {
            var _self = this;

            // "生成布局"（flow）和"绘制"（paint）这两步，合称为"渲染"（render）
            _self._renderView();

            // 绑定事件
            _self._bindEvents();

            // 触发容器创建成功事件，根据页面容器创建完成时间大于1秒进行触发，避免第一次加载页面重复触发
            _self._triggerPageContainerCreationComplete();
        },
        _renderView : function() {
            // 生成布局
            this._createLayout();
            // 生成页面组件
            this._createWidgets();
        },
        _createLayout : function() {
        },
        _createWidgets : function() {
            var widgetDefinition = this.options.widgetDefinition;
            $.each(widgetDefinition.items, function(index, childWidgetDefinition) {
                appContext.createWidget(childWidgetDefinition, widgetDefinition);
            });
        },
        _bindEvents : function() {
            var _self = this;
            // 监听事件
            _self.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function(e, ui) {
                console.log("页面容器创建完成事件!");
                // $("script").remove();
            });
            // 监听门户配置事件
            var configuration = _self.getConfiguration();
            if (configuration && configuration.portal) {
                // 监听门户配置事件
                _self.on("portal.config", function(e, ui) {
                    var pageContainer = _self.getPageContainer(true);
                    console.log(pageContainer);
                    console.log("门户配置！");
                });
            }
        },
        // 触发容器创建成功事件，根据页面容器创建完成时间大于1秒进行触发，避免第一次加载页面重复触发
        _triggerPageContainerCreationComplete : function() {
            var _self = this;
            var currentTime = new Date().getTime();
            var createTime = appContext.getItem(constant.WIDGET_EVENT.PageContainerCreationComplete);
            if(createTime == null) {
            	appContext.setItem(constant.WIDGET_EVENT.PageContainerCreationComplete, currentTime);
                _self.trigger(constant.WIDGET_EVENT.PageContainerCreationComplete);
            } else {
                if(currentTime - createTime > 1 * 1000) {
                	appContext.setItem(constant.WIDGET_EVENT.PageContainerCreationComplete, currentTime);
                    _self.trigger(constant.WIDGET_EVENT.PageContainerCreationComplete);
                }
            }
        },
        getData : function() {
            return [];
        }
    });
}));
