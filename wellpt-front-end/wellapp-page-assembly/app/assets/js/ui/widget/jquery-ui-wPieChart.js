/**
 * 饼图组件：使用ECharts的图表插件进行渲染
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
    $.widget("ui.wPieChart", $.ui.wChart, {
        options: {
            // 组件定义
            widgetDefinition: {},
            // 上级容器定义
            containerDefinition: {},
            jsModules: {},
        },

        _setEvent: function () {
            var _self = this;
            this._initChartEvents();

            //饼图事件绑定
            var eventNames = ['pieselectchanged', 'pieselected', 'pieunselected'];
            for (var i = 0, len = eventNames.length; i < len; i++) {
                var eventName = eventNames[i];
                this.chart.on(eventName, function (value) {
                    _self.invokeDevelopmentMethod(eventName, [value]);
                });
            }
        },




    });
}));