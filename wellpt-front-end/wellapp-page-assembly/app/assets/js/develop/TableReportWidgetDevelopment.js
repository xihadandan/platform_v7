define(["constant", "commons", "server", "WidgetDevelopment"],
    function (constant, commons, server, WidgetDevelopment) {
        // 表格报表组件二开基础
        var TableReportWidgetDevelopment = function () {
            WidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(TableReportWidgetDevelopment, WidgetDevelopment, {

            // 表格报表组件渲染前回调方法，子类可覆盖
            beforeRender: function (options, configuration) {
            },
            // 表格报表组件渲染完成回调方法，子类可覆盖
            afterRender: function (options, configuration) {
            },
            // 清除表格报表参数
            clearParams: function () {
                this.getWidget().clearParams();
            },
            // 添加表格报表参数
            addParam: function (key, value) {
                this.getWidget().addParam(key, value);
            },
            // 移除表格报表参数
            removeParam: function (key) {
                return this.getWidget().removeParam(key);
            },
            // 获取表格报表参数
            getParam: function (key) {
                return this.getWidget().getParam(key);
            },
            refresh: function (options) {
                return this.getWidget().refresh(options);
            },

            //报表数据加载成功后的回调函数
            onLoadSuccess: function (options, configuration) {

            },

            //查询操作前的毁掉函数
            beforeSearch: function (options, configuration) {

            }


        });
        return TableReportWidgetDevelopment;
    });