define([ "jquery", "jquery-ui", "bootstrap", "server", "commons", "appContext", "appWindowManager", "appModal",
        "AppPageDesigner" ], function($, ui, bootstrap, server, commons, appContext, appWindowManager, appModal,
        AppPageDesigner) {
    var UUID = commons.UUID;
    var Browser = commons.Browser;
    var StringUtils = commons.StringUtils;
    var StringBuilder = commons.StringBuilder;
    // 1、门户配置器
    var AppPortalConfigurer = function(options) {
        var _self = this;
        AppPageDesigner.apply(_self, arguments);
        // 应用组件内部的组件ID
        _self.portalAppChildrenIdMap = {};
        // 内置应用组件信息
        _self.initPortalBuildinAppMap();
    };
    // 接口方法
    commons.inherit(AppPortalConfigurer, AppPageDesigner, {
        // 获取加载页面定义服务
        getLoadAppPageDefinitionService : function() {
            return "appPortalFacadeService.getAppPageDefinitionByPageUuid";
        },
        // 获取检查组件被引用服务
        getCheckWidgetReferencedService : function() {
            return "appPortalFacadeService.isWidgetReferencedById";
        },
        // 初始化内置应用组件信息
        initPortalBuildinAppMap : function() {
            var _self = this;
            var options = _self.options;
            _self.portalBuildinAppMap = {};
            var buildinApps = (options.portal && options.portal.buildinApps) || [];
            $.each(buildinApps, function(i, buildinApp) {
                _self.portalBuildinAppMap[buildinApp.widgetId] = buildinApp;
            });
        },
        // 初始化元素放入容器的通用处理
        addToContainer : function(container, $placeHolder, $widget, options) {
            var _self = this;
            if ($widget == null || $widget.length == 0) {
                return;
            }
            var widgetDefaults = $widget.data("defaults");
            var portalWidgetDefinition = $widget.data("portalWidgetDefinition");
            if (portalWidgetDefinition != null) {
                arguments[3] = portalWidgetDefinition;
                // 应用组件不可放置子组件
                widgetDefaults.sortable = false;
            } else if (_self.isPortalApp(options)) {
                // 应用组件不可放置子组件
                widgetDefaults.sortable = false;
                // 添加应用组件内部的组件ID
                _self.addPortalAppChildrenIds(options);
            } else if (_self.isPortalBuildinApp(options)) {
                // 内置应用组件不可放置子组件
                widgetDefaults.sortable = false;
            } else if (_self.isPortalBuildinWidget(options)) {
                // 内置组件，但不是应用，不可放置子组件、拖动、配置、删除
                widgetDefaults.sortable = false;
                widgetDefaults.draggable = false;
                widgetDefaults.configable = false;
                widgetDefaults.deletable = false;
            }

            // 判断组件定义是否为门户应用内嵌的组件，内嵌的组件不可放置子组件、拖动、配置、删除
            if (_self.isPortalAppInnerWidgetDefinition(options)) {
                widgetDefaults.sortable = false;
                widgetDefaults.draggable = false;
                widgetDefaults.configable = false;
                widgetDefaults.deletable = false;
            }

            _self._superApply(arguments);

            // 门户配置
            var component = $widget.data("component");
            if (component == null || !$.isFunction(component.getPortalPropertyConfigurer)) {
                return;
            }
            _self.initComponentPortalPropertyConfigurer(component);
        },
        // 1.1、初始化门户
        initPortal : function() {
            var _self = this;
            _self.portalInfo = _self.options.portal || {};
            _self.portalLayoutMap = {};
            _self.portalAppMap = {};
            // 门户布局可拖动
            _self.portalLayoutSortable();
            // 门户应用可拖动
            _self.portalAppSortable();
        },
        // 1.2、门户布局可拖动
        portalLayoutSortable : function() {
            var _self = this;
            $(".portal-layouts-info ul").draggable({
                connectToSortable : ".ui-sortable",
                containment : "document",
                handle : "li",
                cursorAt : {
                    top : 12,
                    left : 120
                },
                revert : "invalid",
                helper : function(event) {
                    var $target = $(event.target);
                    var $li = $target;
                    if (!$target.is("li")) {
                        $li = $target.closest("li");
                    }
                    var layoutId = $li.attr("layoutId");
                    var portalLayout = _self.getPortalLayoutById(layoutId);
                    var $layoutLi = $("li[type='" + portalLayout.layout + "']");
                    var $widget = $layoutLi.find(".widget").clone();
                    var widgetDefaults = $.extend(true, {}, $layoutLi.data("defaults") || {}, {
                        title : portalLayout.text
                    });
                    $widget.data("defaults", widgetDefaults);
                    $widget.width("30em");
                    return $widget.show();
                }
            });
        },
        // 1.3、门户应用可拖动
        portalAppSortable : function() {
            var _self = this;
            $(".portal-apps-info ul").draggable({
                connectToSortable : ".ui-sortable",
                containment : "document",
                handle : "li",
                cursorAt : {
                    top : 12,
                    left : 120
                },
                revert : "invalid",
                helper : function(event) {
                    var $target = $(event.target);
                    var $li = $target;
                    if (!$target.is("li")) {
                        $li = $target.closest("li");
                    }
                    var appId = $li.attr("appId");
                    var portalApp = _self.getPortalAppById(appId);
                    var widgetDefinition = _self.getPortalWidgetDefinition(portalApp);
                    if (widgetDefinition == null) {
                        return $target.clone().append("——应用组件设置错误，不能放入门户配置！").css("background-color", "red");
                    }
                    var $appLi = $("li[type='" + widgetDefinition.wtype + "']");
                    var $widget = $appLi.find(".widget").clone();
                    $widget.data("portalWidgetDefinition", widgetDefinition);
                    $widget.find(".widget-title").text(portalApp.text);
                    var widgetDefaults = $.extend(true, {}, $appLi.data("defaults") || {}, {
                        title : portalApp.text
                    });
                    $widget.data("defaults", widgetDefaults);
                    $widget.width("30em");
                    return $widget.show();
                }
            });
        },
        // 1.4、获取布局信息
        getPortalLayoutById : function(layoutId) {
            var _self = this;
            if (_self.portalLayoutMap[layoutId] != null) {
                return _self.portalLayoutMap[layoutId];
            }
            var layouts = _self.portalInfo.layouts;
            for (var i = 0; i < layouts.length; i++) {
                _self.portalLayoutMap[layouts[i].uuid] = layouts[i];
            }
            return _self.portalLayoutMap[layoutId];
        },
        // 1.5、获取应用信息
        getPortalAppById : function(appId) {
            var _self = this;
            if (_self.portalAppMap[appId] != null) {
                return _self.portalAppMap[appId];
            }
            var apps = _self.portalInfo.apps;
            for (var i = 0; i < apps.length; i++) {
                _self.portalAppMap[apps[i].uuid] = apps[i];
            }
            return _self.portalAppMap[appId];
        },
        // 1.6、获取应用的组件定义信息
        getPortalWidgetDefinition : function(portalApp) {
            var _self = this;
            var widgetDefinition = null;
            if (portalApp.eventHandler == null || StringUtils.isBlank(portalApp.eventHandler.id)) {
                return widgetDefinition;
            }
            JDS.call({
                service : "appPortalFacadeService.copyAppWidgetDefinitionByPiUud",
                data : [ portalApp.eventHandler.id ],
                async : false,
                success : function(result) {
                    widgetDefinition = JSON.parse(result.data.definitionJson);
                }
            });
            // 标记为组件应用
            widgetDefinition.portalApp = true;
            // 添加应用组件内部的组件ID
            _self.addPortalAppChildrenIds(widgetDefinition);
            return widgetDefinition;
        },
        // 添加应用组件内部的组件ID
        addPortalAppChildrenIds : function(widgetDefinition) {
            var _self = this;
            var items = widgetDefinition.items || [];
            $.each(items, function(i, childWidgetDefinition) {
                _self.portalAppChildrenIdMap[childWidgetDefinition.id] = childWidgetDefinition.id;
                // 添加子组件定义
                _self.addPortalAppChildrenIds(childWidgetDefinition);
            });
        },
        // 判断组件定义是否为门户应用内嵌的组件
        isPortalAppInnerWidgetDefinition : function(options) {
            if (options == null || StringUtils.isBlank(options.id)) {
                return false;
            }
            return this.portalAppChildrenIdMap[options.id] != null;
        },
        // 获取门户应用列表
        getPortalApps : function() {
            return $.extend(true, [], this.portalInfo.apps || []);
        },
        // 判断是否为组件应用
        isPortalApp : function(options) {
            return options != null && options.portalApp == true;
        },
        // 判断是否内置组件应用
        isPortalBuildinApp : function(options) {
            if (options == null || StringUtils.isBlank(options.id)) {
                return false;
            }
            return this.portalBuildinAppMap[options.id] && this.portalBuildinAppMap[options.id].buildin === true;
        },
        // 判断是否内置组件
        isPortalBuildinWidget : function(options) {
            if (options == null || StringUtils.isBlank(options.id)) {
                return false;
            }
            return this.portalBuildinAppMap[options.id] != null;
        },
        // 初始化组件门户配置器
        initComponentPortalPropertyConfigurer : function(component) {
            var _self = this;
            var ComponentPortalPropertyConfigurer = component.getPortalPropertyConfigurer();
            if (!$.isFunction(ComponentPortalPropertyConfigurer)) {
                ComponentPortalPropertyConfigurer = $.ui.component
                        .BaseComponentPortalPropertyConfigurer(ComponentPortalPropertyConfigurer);
            }
            // 获取门户应用列表
            ComponentPortalPropertyConfigurer.prototype.getPortalApps = function() {
                return _self.getPortalApps();
            }
            var portalPropertyConfigurer = new ComponentPortalPropertyConfigurer(component);
            var componentPortalPropertyEditor = new ComponentPortalPropertyEditor(component, portalPropertyConfigurer,
                    _self);
            componentPortalPropertyEditor.init();
        },
        // 判断门户设置是否处理编辑状态
        isInEditState : function() {
            return $(".widget-portal-container:visible", ".web-app-container").length > 0;
        }
    });

    // 组件门户配置编辑器
    var ComponentPortalPropertyEditor = function(component, portalPropertyConfigurer, pageDesigner) {
        var _self = this;
        _self.component = component;
        _self.portalPropertyConfigurer = portalPropertyConfigurer;
        _self.pageDesigner = pageDesigner;
    }
    // 初始化
    ComponentPortalPropertyEditor.prototype.init = function() {
        var _self = this;
        var $widget = $(_self.component.element);
        // 添加门户配置按钮
        var $portalConfig = $("<a>", {
            "class" : "btn-portal-edit"
        }).text("编辑");
        $widget.children(".widget-header").find(".widget-actions").prepend($portalConfig);
        $portalConfig.on("click", $.proxy(function(e) {
            this.onConfig();
        }, _self));
    }
    // 配置处理
    ComponentPortalPropertyEditor.prototype.onConfig = function() {
        var _self = this;
        _self.initDom();
        // 属性配置加载回调
        var tempOptions = $.extend(true, {}, _self.component.getOptions());
        _self.portalPropertyConfigurer.onLoad.call(_self.portalPropertyConfigurer, _self.$body, tempOptions);
    }
    // 初始化页面元素
    ComponentPortalPropertyEditor.prototype.initDom = function() {
        var _self = this;
        var $element = $(_self.component.element);
        if (_self.$portalContainer) {
            _self.$portalContainer.show();
            return;
        }
        var sb = new StringBuilder();
        sb.append('<div class="widget-portal-container">');
        sb.append('<div class="panel panel-default">');
        sb.append('<div class="panel-heading">');
        sb.append($element.children(".widget-header").find(".widget-title").text());
        sb.append('</div>');
        sb.append('<div class="panel-body">');
        sb.append('</div>');
        sb.append('<div class="panel-footer">');
        sb.append('<div class="row pull-right">');
        sb.append('<button type="button" class="btn btn-primary btn_ok">确定</button>');
        sb.append('<button type="button" class="btn btn-default btn_cancel">取消</button>');
        sb.append('</div>');
        sb.append('</div>');
        sb.append('</div>');
        sb.append('</div>');
        $element.append($(sb.toString()));
        var $portalContainer = $element.children(".widget-portal-container");
        _self.$portalContainer = $portalContainer;
        _self.$header = $portalContainer.find(".panel-heading");
        _self.$body = $portalContainer.find(".panel-body");
        _self.$footer = $portalContainer.find(".panel-footer");

        // 绑定事件
        // 确定
        _self.$footer.on("click", ".btn_ok", function() {
            var result = _self.portalPropertyConfigurer.onOk.call(_self.portalPropertyConfigurer, _self.$body);
            if (result !== false) {
                _self.$body.html("");
                _self.$portalContainer.hide();
                // 组件预览
                _self.pageDesigner.preview(_self.component);
            }
        });
        // 取消
        _self.$footer.on("click", ".btn_cancel", function() {
            _self.$portalContainer.hide();
        });
    }
    return AppPortalConfigurer;
});
