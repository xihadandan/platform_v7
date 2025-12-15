/**
 * 空的报表组件定义：使用echarts的options自定义组件
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
    $.widget("ui.wBareChart", $.ui.wChart, {
        options: {
            // 组件定义
            widgetDefinition: {},
            // 上级容器定义
            containerDefinition: {},
            jsModules: {},
        },


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
                width: configuration.width, height: configuration.height
            });

            var chart = echarts.init(document.getElementById(chartId));
            _self.chart = chart;


            var optJson = configuration.optionJson;
            return optJson === '' ? {} : JSON.parse(optJson);
        },
        _setEvent: function () {
            var _self = this;
            this._initChartEvents();

            // 事件绑定
            var eventNames = ['axisareaselected','pieselectchanged', 'pieselected', 'pieunselected'];
            for (var i = 0, len = eventNames.length; i < len; i++) {
                var eventName = eventNames[i];
                this.chart.on(eventName, function (value) {
                    _self.invokeDevelopmentMethod(eventName, [value]);
                });
            }
        },

    });
}));