define(["ui_component", "constant", "commons", "formBuilder", "appContext", "design_commons"], function(ui_component,
    constant, commons, formBuilder, appContext, designCommons) {
    var component = $.ui.component.BaseComponent();
    var StringUtils = commons.StringUtils;
    var StringBuilder = commons.StringBuilder;
    var checkRequire = function(propertyNames, options, $container) {
        for (var i = 0; i < propertyNames.length; i++) {
            var propertyName = propertyNames[i];
            if (StringUtils.isBlank(options[propertyName])) {
                var title = $("label[for='" + propertyName + "']", $container).text();
                appModal.error(title.replace("*", "") + "不允许为空！");
                return false;
            }
        }
        return true;
    };
    component.prototype.create = function() {}
    component.prototype.usePropertyConfigurer = function() {
        return true;
    }
    component.prototype.getPropertyConfigurer = function() {
        var collectClass = "w-configurer-option";
        var configurerPrototype = {};
        configurerPrototype.onLoad = function($container, options) {
            // 初始化页签项
            $("#widget_button_tabs ul a", $container).on("click", function(e) {
                e.preventDefault();
                $(this).tab("show");
            })
            var configuration = $.extend(true, {}, options.configuration);
            this.initConfiguration(configuration, $container);
        };
        configurerPrototype.onOk = function($container) {
            var opts = this.collectConfiguration($container)
            var requeryFields = ['text'];
            if (!checkRequire(requeryFields, opts, $container)) {
                return false;
            }
            this.component.options.configuration = opts;
        };
        // 初始化配置信息
        configurerPrototype.initConfiguration = function(configuration, $container) {
            // 基本信息
            this.initBaseInfo(configuration, $container);
        };
        configurerPrototype.initBaseInfo = function(configuration, $container) {
            var _self = this;
            // 外观
            var appearanceOptionSb = new StringBuilder();
            appearanceOptionSb.append('<option>默认</option>');
            $.each(constant.WIDGET_COLOR, function() {
                appearanceOptionSb.appendFormat('<option value="{0}">{1}</option>', this.value, this.name);
            });
            appearanceOptionSb.append('<option value="link">链接</option>');
            $("#appearance", $container).append(appearanceOptionSb.toString());

            // 大小
            var sizeOptionSb = new StringBuilder();
            sizeOptionSb.append('<option>默认</option>');
            $.each(constant.WIDGET_SIZE, function() {
                sizeOptionSb.appendFormat('<option value="{0}">{1}</option>', this.value, this.name);
            });
            sizeOptionSb.append('<option value="block">块级</option>');
            $("#size", $container).append(sizeOptionSb.toString());

            // 设置值
            designCommons.setElementValue(configuration, $container);

            var appPageUuid = _self.component.pageDesigner.getPageUuid();
            var piUuid = _self.component.pageDesigner.getPiUuid();
            var system = appContext.getCurrentUserAppData().getSystem();
            var productUuid = system.productUuid;
            if (system != null && system.piUuid != null) {
                piUuid = system.piUuid;
            }

            // 头部事件
            var $element = $(".button-events", $container);
            var buttonEvents = configuration.events || [];

            formBuilder.bootstrapTable.build({
                container: $element,
                name: "buttonEvents",
                ediableNest: true,
                table: {
                    data: buttonEvents,
                    striped: true,
                    idField: "uuid",
                    columns: [{
                        field: "checked",
                        formatter: designCommons.checkedFormat,
                        checkbox: true
                    }, {
                        field: "uuid",
                        title: "UUID",
                        visible: false,
                        editable: {
                            type: "text",
                            showbuttons: false,
                            onblur: "submit",
                            mode: "inline"
                        }
                    }, {
                        field: "source",
                        title: "事件元素",
                        editable: {
                            type: "select",
                            mode: "inline",
                            showbuttons: false,
                            onblur: "submit",
                            emptytext: '请选择',
                            source: function() {
                                var sources = [];
                                sources.push({
                                    value: ".btn",
                                    text: "按钮"
                                });
                                return sources;
                            }
                        }
                    }, {
                        field: "type",
                        title: "事件类型",
                        editable: {
                            type: "select",
                            mode: "inline",
                            onblur: "submit",
                            showbuttons: false,
                            source: function() {
                                var eventTypes = constant.EVENT_TYPE
                                return $.map(eventTypes, function(eventType) {
                                    return {
                                        value: eventType.value,
                                        text: eventType.name
                                    }
                                });
                            }
                        }
                    }, {
                    	field: "target",
                        title: "目标位置",
                        editable: {
                            onblur: "cancel",
                            type: "wCustomForm",
                            placement: "bottom",
                            savenochange: true,
                            value2input: designCommons.bootstrapTable.targePosition.value2input,
                            value2display: designCommons.bootstrapTable.targePosition.value2display,
                            inputCompleted: designCommons.bootstrapTable.targePosition.inputCompleted
                        }
                    }, {
                        field: "handler",
                        title: "事件处理",
                        width: 150,
                        editable: {
                            onblur: "ignore",
                            type: "wCustomForm",
                            placement: "left",
                            savenochange: true,
                            value2input: designCommons.bootstrapTable.eventHandler.value2input,
                            input2value: designCommons.bootstrapTable.eventHandler.input2value,
                            validate: designCommons.bootstrapTable.eventHandler.validate,
                            value2display: designCommons.bootstrapTable.eventHandler.value2display
                        }
                    }, {
                        field: "params",
                        title: "事件参数",
                        editable: {
                            onblur: "ignore",
                            type: "wCustomForm",
                            placement: "left",
                            savenochange: true,
                            value2input: designCommons.bootstrapTable.eventParams.value2input,
                            input2value: designCommons.bootstrapTable.eventParams.input2value,
                            value2display: designCommons.bootstrapTable.eventParams.value2display
                        }
                    }]
                }
            });
        };

        // 收集配置信息
        configurerPrototype.collectConfiguration = function($container) {
            var configuration = {};
            // 基本信息
            this.collectBaseInfo(configuration, $container);
            return $.extend({}, configuration);
        };
        configurerPrototype.collectBaseInfo = function(configuration, $container) {
            var $form = $("#widget_button_tabs_base_info", $container);
            var opt = designCommons.collectConfigurerData($form, collectClass);
            // 头部事件
            var $tableheaderEventsInfo = $("#table_buttonEvents_info", $container);
            var events = $tableheaderEventsInfo.bootstrapTable("getData");
            opt.events = events;
            $.extend(configuration, opt);
        };
        var configurer = $.ui.component.BaseComponentConfigurer(configurerPrototype);
        return configurer;
    }
    component.prototype.toHtml = function() {
        var _self = this;
        var options = _self.options;
        var configuration = options.configuration || {};
        var id = _self.getId();
        var text = configuration.text;
        var appearance = configuration.appearance;
        var size = configuration.size;
        var html = new StringBuilder();
        // 外观
        if (StringUtils.isNotBlank(appearance)) {
            appearance = " btn-" + appearance;
        }
        // 大小
        if (StringUtils.isNotBlank(size)) {
            size = " btn-" + size;
        }
        // 样式类
        var cssClass = configuration.cssClass;
        var btnTpl = '<div id="{0}" class="ui-wButton"><button class="btn{2}{3} {4}">{1}</button></div>';
        html.appendFormat(btnTpl, id, text, appearance, size, cssClass);
        return html.toString();
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