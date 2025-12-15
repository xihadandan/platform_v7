/**
 * 折线图定义js
 */
define(["appContext", "design_commons", "constant", "commons", "server", "bootbox", 'formBuilder', 'appModal',
        'select2', 'wSelect2', 'ui_chart_component'],
    function (appContext, designCommons, constant, commons, server,
              bootbox, formBuilder, appModal) {
        var component = $.ui.component.BaseComponent();
        var StringUtils = commons.StringUtils;
        var collectClass = "w-configurer-option";

        var clearChecked = function (row) {
            row.checked = false;
            return row;
        };

        var onEditHidden = function (field, row, $el, reason) {
            $el.closest("table").bootstrapTable("resetView")
        };
        var checkedFormat = function (value, row, index) {
            if (value) {
                return true;
            }
            return false;
        };


        var clearInputValue = function ($container) {
            $container.find(".w-configurer-option").each(function () {
                var $element = $(this);
                var type = $element.attr("type");
                if (type == "text" || type == "hidden") {
                    $element.val('');
                } else if (type == "checkbox" || type == "radio") {
                    $element.prop("checked", false);
                }
                $element.trigger('change');
            });

        };

        var configurer = $.ui.chartComponent.ChartBaseComponentConfigurer();


        configurer.prototype.onLoad = function ($container, options) {
            // 初始化页签项
            $("#widget_bootstrap_table_tabs ul a", $container).on("click", function (e) {
                e.preventDefault();
                $(this).tab("show");
                var panelId = $(this).attr("href");
                $(panelId + " .definition_info").bootstrapTable("resetView");
            })
            var configuration = $.extend(true, {}, options.configuration);
            configuration.chartType = 'line';//折线图类型
            this.initBaseDefineInfo(configuration, $container);
            this.initTitleDefineInfo(configuration, $container);
            this.initChartDataInfo(configuration, $container);
            this.initSeriesDefineInfo(configuration, $container);
            this.initLegendDefineInfo(configuration, $container);
            this.initToolboxDefineInfo(configuration, $container);
            this.initTooltipDefineInfo(configuration,$container);
            this.initGridDefineInfo(configuration, $container);
            this.initAxisDefineInfo(configuration, $container, 'xAxis');
            this.initAxisDefineInfo(configuration, $container, 'yAxis');
        }


        configurer.prototype.onOk = function ($container) {
            if (this.component.isReferenceWidget()) {
                return;
            }
            //收集图表基础信息
            var opt = {
                chartOption: {} //初始化echart图表的参数选项
            };
            var baseOption = this.collectBasicDefineInfo($container);
            $.extend(opt, baseOption);

            //收集图表数据信息
            opt.chartDataInfo = designCommons.collectConfigurerData($("#widget_chart_data_info", $container),
                collectClass);

            //收集图表的标题信息
            opt.titleDefineInfo = this.collectTitleDefineInfo($container);
            opt.chartOption.title = $.extend({}, opt.titleDefineInfo.title);

            //收集图表的图例信息
            opt.legendDefineInfo = this.collectLegendDefineInfo($container);
            opt.chartOption.legend = $.extend({}, opt.legendDefineInfo.legend);

            //收集图表的工具框信息
            opt.toolboxDefineInfo = this.collectToolboxDefineInfo($container);
            opt.chartOption.toolbox = $.extend({}, opt.toolboxDefineInfo.toolbox);

            opt.tooltipDefineInfo = this.collectTooltipDefineInfo($container);
            opt.chartOption.tooltip = $.extend({}, opt.tooltipDefineInfo.tooltip);

            //收集图表的系列列表信息
            opt.seriesDefineInfo = this.collectSeriesDefineInfo($container);
            opt.chartOption.series = opt.seriesDefineInfo.series;

            opt.gridDefineInfo = this.collectGridDefineInfo($container);
            opt.chartOption.grid = opt.gridDefineInfo.grid;

            opt.xAxisDefineInfo = this.collectAxisDefineInfo($container, 'xAxis');
            opt.chartOption.xAxis = opt.xAxisDefineInfo.xAxis;

            opt.yAxisDefineInfo = this.collectAxisDefineInfo($container, 'yAxis');
            opt.chartOption.yAxis = opt.yAxisDefineInfo.yAxis;

            this.component.options.configuration = $.extend({}, opt);


            console.log(opt);

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