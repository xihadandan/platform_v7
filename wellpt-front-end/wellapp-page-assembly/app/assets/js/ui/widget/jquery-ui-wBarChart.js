/**
 * 柱状图表组件：使用ECharts的图表插件进行渲染
 */
(function (factory) {
    "use strict";
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define(['jquery', 'commons', 'server', 'constant', 'dataStoreBase', 'formBuilder', 'moment',
            'appModal', "ViewDevelopmentBase", 'echarts', 'select2', 'wSelect2'], factory);
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function (jquery, commons, server, constant, DataStore, formBuilder, moment, appModal,
            ViewDevelopmentBase, echarts) {
    "use strict";
    var $ = jquery;
    var UUID = commons.UUID;
    var StringUtils = commons.StringUtils;
    $.widget("ui.wBarChart", $.ui.wChart, {
        options: {
            // 组件定义
            widgetDefinition: {},
            // 上级容器定义
            containerDefinition: {},
            jsModules: {},
        },


        _beforeRenderView: function () {
            var _self = this;
            _self._replaceAxisMaxMinFunction(this.getConfiguration().chartOption);
            _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
        },

        /**
         * max、min函数替换
         * @param option
         * @private
         */
        _replaceAxisMaxMinFunction: function (option) {

            var replaceFunction = function (axis, functionName, chartOption) {
                //解析x/y轴的函数min、max
                for (var i = 0, len = chartOption[axis].length; i < len; i++) {
                    if (chartOption[axis][i][functionName] != undefined) {
                        var calculateMaxScript = chartOption[axis][i][functionName];
                        chartOption[axis][i][functionName] = function (value) {
                            var values = [value, value.max, value.min];
                            var anonymousFunction = new Function('value,max,min', ' return ' + calculateMaxScript);
                            var rt = anonymousFunction.apply(this, values);
                            return rt;
                        }
                    }
                }
            };

            replaceFunction('xAxis', 'max', option);
            replaceFunction('xAxis', 'min', option);
            replaceFunction('yAxis', 'max', option);
            replaceFunction('yAxis', 'min', option);

        },

        _setEvent: function () {
            var _self = this;
            this._initChartEvents();

            // 事件绑定
            var eventNames = ['axisareaselected'];
            for (var i = 0, len = eventNames.length; i < len; i++) {
                var eventName = eventNames[i];
                this.chart.on(eventName, function (value) {
                    _self.invokeDevelopmentMethod(eventName, [value]);
                });
            }
        },


    });
}));