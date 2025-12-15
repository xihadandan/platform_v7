/**
 * 空的图表定义js：使用json字符串格式的option定义进行展示
 */
define(["appContext", "design_commons", "constant", "commons", "server", "bootbox", 'formBuilder',
        'appModal', 'jsoneditor', 'select2', 'wSelect2'],
    function (appContext, designCommons, constant, commons, server,
              bootbox, formBuilder, appModal, JSONEditor) {
        var component = $.ui.component.BaseComponent();
        var StringUtils = commons.StringUtils;
        var collectClass = "w-configurer-option";


        var configurer = $.ui.component.BaseComponentConfigurer();
        configurer.prototype.initBaseInfo = function (configuration, $container) {
            var _self = this;
            designCommons.setElementValue(configuration, $container);

            // 加载的JS模块
            $("#jsModule", $container).wSelect2({
                serviceName: "appJavaScriptModuleMgr",
                params: {
                    dependencyFilter: "ChartWidgetDevelopment"
                },
                labelField: "jsModuleName",
                valueField: "jsModule",
                remoteSearch: false,
                multiple: true
            });


        };

        configurer.prototype.initOptionInfo = function (configuration, $container) {
            //json编辑器化
            var jsonContainer = document.getElementById("optionJsonEditor");
            var options = {
                modes: ['code', 'tree'],
                onChange: function () {
                    var $jsoneditor = $("#optionJsonEditor", $container).data('jsoneditor');
                    var json;
                    try {
                        json = $jsoneditor.getText();
                    } catch (e) {
                    }
                    if (json) {
                        $("#optionJson", $container).val(json);
                    }
                }
            };
            var jsoneditor = new JSONEditor(jsonContainer, options);
            if (configuration.optionJson) {
                jsoneditor.set(JSON.parse(configuration.optionJson));
            }
            $("#optionJsonEditor", $container).data('jsoneditor', jsoneditor);
        }


        configurer.prototype.onLoad = function ($container, options) {
            // 初始化页签项
            $("#widget_bootstrap_table_tabs ul a", $container).on("click", function (e) {
                e.preventDefault();
                $(this).tab("show");
                var panelId = $(this).attr("href");
                $(panelId + " .definition_info").bootstrapTable("resetView");
            })
            var configuration = $.extend(true, {}, options.configuration);
            this.initBaseInfo(configuration, $container);
            this.initOptionInfo(configuration, $container);

        }
        configurer.prototype.onOk = function ($container) {
            if (this.component.isReferenceWidget()) {
                return;
            }
            var opt = designCommons.collectConfigurerData($("#widget_chart_base_info", $container),
                collectClass);

            opt.optionJson = $("#optionJson", $container).val();
            try {
                if (opt.optionJson !== '') {
                    JSON.parse(opt.optionJson);
                }
            } catch (e) {
                appModal.error('不合法的json格式');
                return false;
            }

            this.component.options.configuration = $.extend({}, opt);

        }

        // configurer.prototype.getTemplateUrl = function () {
        //     var wtype = this.component.options.wtype.replace(/([A-Z])/g, "_$1").toLowerCase();
        //     wtype = wtype.replace("__", "_");
        //     return ctx + "/web/app/page/configurer/report/widget" + wtype.substring(1);
        // };


        component.prototype.usePropertyConfigurer = function () {
            return true;
        }
        // 返回属性配置器
        component.prototype.getPropertyConfigurer = function () {
            return configurer;
        }
        component.prototype.create = function () {
            $(this.element).find(".widget-body").html(this.options.content);
        };
        component.prototype.getDefinitionJson = function () {
            var options = this.options;
            var id = this.getId();
            options.id = id;
            return options;
        }


        return component;
    });