define(["ui_component", "commons", "formBuilder", "appContext", "design_commons"], function(ui_component, commons,
    formBuilder, appContext, designCommons) {
    var StringUtils = commons.StringUtils;
    var StringBuilder = commons.StringBuilder;
    var collectClass = "w-configurer-option";
    var component = $.ui.component.BaseComponent();
    component.prototype.create = function() {}

    // 返回定义的HTML
    component.prototype.toHtml = function() {
        var options = this.options;
        var id = this.getId();
        var html = '<div id="' + id + '" class="ui-wTagTree"></div>';
        return html;
    }

    // 使用属性配置器
    component.prototype.usePropertyConfigurer = function() {
        return true;
    }
    var clearChecked = function(row) {
        row.checked = false;
        return row;
    };
    var checkedFormat = function(value, row, index) {
        if (value) {
            return true;
        }
        return false;
    };
    // 验证配置信息的合法性
    var validateConfiguration = function(configuration) {
        // 常量的显示值不能为空
        if (configuration.dataType == "1") {
            var options = configuration.optionValue;
            if (options) {
                for (var i = 0; i < options.length; i++) {
                    if (StringUtils.isBlank(options[i].text)) {
                        appModal.error("数据来源为常量的显示值不能为空!");
                        return false;
                    }
                }
            }
        }
        return true;
    };
    // 返回属性配置器
    component.prototype.getPropertyConfigurer = function() {
        var configurer = $.ui.component.BaseComponentConfigurer();
        configurer.prototype.onLoad = function($container, options) {
            // 初始化页签项
            $("#widget_tag_tree_tabs ul a", $container).on("click", function(e) {
                e.preventDefault();
                $(this).tab("show");
            })
            var _self = this.component;
            var configuration = $.extend(true, {}, options.configuration);
            this.initConfiguration(configuration, $container);
        };
        configurer.prototype.onOk = function($container) {
            var _self = this;
            var component = _self.component;
            component.options.configuration = _self.collectConfiguration($container);
            return validateConfiguration(component.options.configuration);
        };

        configurer.prototype.initConfiguration = function(configuration, $container) {
            // 基本信息
            this.initBaseInfo(configuration, $container);
        };
        configurer.prototype.initBaseInfo = function(configuration, $container) {
            // 设置值
            designCommons.setElementValue(configuration, $container, "query");

            // 数据来源
            $(".data-type", $container).hide();
            $("input[name=dataType]", $container).on("change", function() {
                $(".data-type", $container).hide();
                var val = $(this).val();
                // 常量
                if (val === "1") {}
                // 数据字典
                if (val === "2") {}
                // 数据仓库
                if (val === "3") {}
                // 年份
                if (val === "4") {}
                $(".data-type-" + val, $container).show();
            });

            // 常量
            var $element = $(".data-type-1 .data-type-constant", $container);
            var optionValue = configuration.optionValue;
            formBuilder.bootstrapTable.build({
                container: $element,
                name: "optionValue",
                ediableNest: true,
                table: {
                    data: optionValue,
                    striped: true,
                    idField: "uuid",
                    columns: [{
                        field: "checked",
                        formatter: checkedFormat,
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
                        field: "value",
                        title: "真实值",
                        editable: {
                            type: "text",
                            showbuttons: false,
                            onblur: "submit",
                            mode: "inline"
                        }
                    }, {
                        field: "text",
                        title: "显示值",
                        editable: {
                            type: "text",
                            showbuttons: false,
                            onblur: "submit",
                            mode: "inline",
                            validate: function(value) {
                                if (StringUtils.isBlank(value)) {
                                    return '请输入显示值!';
                                }
                            }
                        }
                    }]
                }
            });

            // 数据字典
            var dataType = configuration.dataType;
            var dataDic = configuration.dataDic;
            $("input[name=dataType]:checked", $container).trigger("change");
            $("#dataDicName", $container).wCommonComboTree({
                service: "dataDictionaryMaintain.getAllAsTree",
                serviceParams: "-1",
                value: dataDic,
                multiSelect: false, // 是否多选
                parentSelect: true, // 父节点选择有效，默认无效
                onAfterSetValue: function(event, self, value) {
                    console.log(value)
                    $("#dataDic").val(value);
                }
            });

            // 数据源
            var columnSelect2Options = {
                serviceName: "viewComponentService",
                queryMethod: "loadColumnsSelectData",
                params: function() {
                    return {
                        dataStoreId: $("#dataStoreId").val()
                    }
                },
                remoteSearch: false
            };
            $("#dataStoreId", $container).wSelect2({
                serviceName: "viewComponentService",
                queryMethod: "loadSelectData",
                labelField: "dataStoreName",
                valueField: "dataStoreId",
                remoteSearch: false
            }).on("change", function() {
                $("#valueColumn,#textColumn", $container).wSelect2("destroy").wSelect2(columnSelect2Options);
            }).trigger("change");
        };

        configurer.prototype.collectConfiguration = function($container) {
            var configuration = {};
            // 基本信息
            this.collectBaseInfo(configuration, $container);
            return $.extend({}, configuration);
        }
        configurer.prototype.collectBaseInfo = function(configuration, $container) {
            var $form = $("#widget_tag_tree_tabs_base_info", $container);
            var opt = designCommons.collectConfigurerData($form, collectClass);
            opt.optionValue = $container.find("#table_optionValue_info").bootstrapTable("getData");
            $.extend(configuration, opt);
        };
        return configurer;
    }

    // 返回组件定义
    component.prototype.getDefinitionJson = function() {
        var options = this.options;
        var id = this.getId();
        options.id = id;
        return options;
    }

    return component;
});