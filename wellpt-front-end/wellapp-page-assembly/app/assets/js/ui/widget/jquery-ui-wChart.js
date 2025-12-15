(function (factory) {
    "use strict";
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define(['jquery', 'jquery-ui', 'constant', 'commons', 'server', 'appContext', 'echarts'], factory);
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function ($, ui, constant, commons, server, appContext, echarts) {
    "use strict";
    var StringUtils = commons.StringUtils;
    var UUID = commons.UUID;
    $.widget("ui.wChart", $.ui.wWidget, {

        chart: $.noop,//当前echart图表实例

        /**
         * 创建组件
         */
        _createView: function () {
            // 创建js数据源对象
            var _self = this;
            _self._beforeRenderView();
            _self._renderView();
            _self._afterRenderView();
            _self._setEvent();

        },

        _beforeRenderView: function () {
            var _self = this;
            _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
        },

        _afterRenderView: function () {
            var _self = this;
            _self.invokeDevelopmentMethod('afterRender', [_self.options, _self.getConfiguration()]);
        },


        _setEvent: function () {

            this._initChartEvents();


        },

        /**
         * 获取当前图表实例
         * @return {*}
         */
        getChartInstance: function () {
            return this.chart;
        },

        /**
         * 图表选项初始化
         * @return {{legend: {show: boolean}, tooltip: {}, backgroundColor: *}}
         * @private
         */
        _initChartOption: function () {

            var _self = this;
            var configuration = this.getConfiguration();
            var chartId = UUID.createUUID();
            var $element = $(_self.element);
            _self.chartId = chartId;
            $element.remove('#' + chartId);
            $element.append($("<div>", {"id": chartId}));
            _self.$chartContainer = $("#" + chartId);
            _self.$chartContainer.css({
                width: configuration.width, height: configuration.height || 600
            });

            var chart = echarts.init(document.getElementById(chartId));

            _self.chart = chart;

            var chartOption = {
                legend: {show: false},
                backgroundColor: configuration.backgroundColor
            };

            $.extend(chartOption, configuration.chartOption);

            _self._explainTitleLink(chartOption.title);

            if (configuration.chartDataInfo && configuration.chartDataInfo.dataSourceType === 'json') {
                chartOption.dataset = JSON.parse(configuration.chartDataInfo.defineJsonData);
                console.log('自定义json数据集：', chartOption.dataset);
            }
            //系列数据
            var ajaxLoadSeriesData = [];
            if (chartOption.series.length > 0) {
                for (var i = 0, len = chartOption.series.length; i < len; i++) {
                    var series = chartOption.series[i];
                    if (series.seriesDataType === 'json') {
                        series.data = JSON.parse(series.dataJson);
                        delete series.dataJson;
                    } else if (series.seriesDataType === 'dataInterface') {
                        ajaxLoadSeriesData.push(series);
                    }

                    //系列提示框内容格式化函数
                    if (series.tooltip && series.tooltip.formatterType === 'function'
                        && series.tooltip.formatter) {
                        series.tooltip.formatter = new Function('params', series.tooltip.formatter);
                    }

                    // 文本标签定义的文本格式化支持函数
                    if(series.label&&series.label.formatter&&series.label.formatter.indexOf('return ')==0){
                        series.label.formatter = new Function('params', series.label.formatter);
                    }
                }
            }
            if (chartOption.tooltip && chartOption.tooltip.formatterType === 'function'
                && chartOption.tooltip.formatter) {
                chartOption.tooltip.formatter = new Function('params', chartOption.tooltip.formatter);
            }


            _self.ajaxLoadSeriesData = ajaxLoadSeriesData;
            return chartOption;
        },

        _explainTitleLink: function (title) {
            //解析pageUlr的可替换参数

            var userParam;
            var getUserParam = function () {
                var user = server.SpringSecurityUtils.getUserDetails();
                userParam = {
                    currentUserName: user.userName,
                    currentLoginName: user.loginName,
                    currentUserId: user.id,
                    currentUserDepartmentId: user.mainDepartmentId,
                    currentUserDepartmentName: user.mainDepartmentName,
                    currentUserUnitId: user.systemUnitId
                }
            }

            if (title.link) {
                if (_.startsWith(title.link, '/')) {//项目相对路径
                    title.link = ctx + title.link;
                }
                getUserParam();
                title.link = appContext.resolveParams({url: title.link}, userParam).url;
            }
            if (title.sublink) {
                if (_.startsWith(title.sublink, '/')) {//项目相对路径
                    title.sublink = ctx + title.sublink;
                }
                if (!userParam) {
                    getUserParam();
                }
                title.sublink = appContext.resolveParams({url: title.sublink}, userParam).url;
            }

        },
        _renderView: function () {
            var _self = this;
            var configuration = this.getConfiguration();

            var chartOption = _self._initChartOption();

            // 使用刚指定的配置项和数据显示图表。
            this.chart.setOption(chartOption);

            console.log('初始化报表选项数据：', chartOption);

            this.ajaxLoad();

        },

        /**
         * 加载图表的数据集并渲染
         * @private
         */
        _loadDataSet: function (params) {
            var _self = this;
            var configuration = _self.getConfiguration();
            _self.chart.showLoading();
            server.JDS.call({
                service: "reportFacadeService.loadDataset",
                data: [configuration.chartDataInfo.dataInterface, params],
                version: '',
                async: true,
                success: function (result) {
                    if (result.success) {
                        _self.dataset = result.data;
                        console.log('服务器返回数据集：', _self.dataset);
                        _self.invokeDevelopmentMethod('onLoadDataSetSuccess', [_self.dataset]);
                        _self.chart.hideLoading();
                        _self.chart.setOption({
                            dataset: _self.dataset
                        });
                    }
                }
            });
        },

        ajaxLoad:function(params){
            var configuration = this.getConfiguration();
            if (configuration.chartDataInfo && configuration.chartDataInfo.dataSourceType === 'interface') {
                this._loadDataSet(params);
            }

            if (this.ajaxLoadSeriesData && this.ajaxLoadSeriesData.length > 0) {
                this._loadSeriesData(this.ajaxLoadSeriesData,params);
            }
         },



        /**
         * 加载系列数据并渲染
         * @param ajaxLoadSeriesData：待加载的系列
         * @private
         */
        _loadSeriesData: function (ajaxLoadSeriesData,params) {
            var _self = this;
            _self.chart.showLoading();
            var waitAjaxLoadSeriesDataCount = ajaxLoadSeriesData.length;
            for (var i = 0, len = ajaxLoadSeriesData.length; i < len; i++) {
                (function (index) {
                    server.JDS.call({
                        service: "reportFacadeService.loadSeriesData",
                        data: [ajaxLoadSeriesData[index].seriesDataInterface, params],
                        version: '',
                        async: true,
                        success: function (result) {
                            if (result.success) {
                                console.log('服务器返回系列数据：', result.data);
                                _self.invokeDevelopmentMethod('onLoadSeriesDataSuccess', [result.data, ajaxLoadSeriesData[index]]);
                                _self.chart.setOption({
                                    series: [{
                                        name: ajaxLoadSeriesData[index].name,
                                        data: result.data
                                    }]
                                });
                            }
                            if (--waitAjaxLoadSeriesDataCount == 0) {
                                _self.chart.hideLoading();
                            }
                        }
                    });
                })(i);
            }

        },

        _initChartEvents: function () {
            var _self = this;
            var eventNames = ['click', 'dblclick', 'mousedown', 'mousemove',
                'mouseup', 'mouseover', 'mouseout', 'contextmenu',
                'legendselectchanged', 'legendselected', 'legendunselected', 'legendscroll',
                'restore', 'finished', 'rendered'
            ];

            for (var i = 0, len = eventNames.length; i < len; i++) {
                var eventName = eventNames[i];
                this.chart.on(eventName, function (value) {
                    _self.invokeDevelopmentMethod(eventName, [value]);
                });
            }

        },

        refresh: function (option) {
            if (option != undefined) {
                this.chart.setOption(option);
            } else {
                this._renderView();
            }
        },


    });

}));