define(["constant", "commons", "server", "WidgetDevelopment"],
    function (constant, commons, server, WidgetDevelopment) {
        // 图表组件二开基础
        var ChartWidgetDevelopment = function () {
            WidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(ChartWidgetDevelopment, WidgetDevelopment, {
            //图表渲染前触发
            beforeRender: function (options, configuration) {
            },
            //图表渲染后触发
            afterRender: function (options, configuration) {
            },
            //刷新图表
            refresh: function (options) {
                return this.getWidget().refresh(options);
            },

            /**
             * 请求报表数据，并渲染
             * @param params 接口参数
             */
            ajaxLoad:function(params){
                this.getWidget().ajaxLoad(params);
            },

            //数据集加载成功后的回调函数
            onLoadDataSetSuccess: function (dataset) {

            },

            //系列数据加载成功后的回调函数
            onLoadSeriesDataSuccess: function (seriesData, series) {

            }

            //其他图表的事件响应：例如点击事件、图例选择事件

        });
        return ChartWidgetDevelopment;
    });