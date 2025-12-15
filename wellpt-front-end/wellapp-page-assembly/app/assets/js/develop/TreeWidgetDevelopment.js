define([ "constant", "commons", "server", "WidgetDevelopment" ],
        function(constant, commons, server, WidgetDevelopment) {
            // 通用树组件二开基础
            var TreeWidgetDevelopment = function() {
                WidgetDevelopment.apply(this, arguments);
            };
            // 接口方法
            commons.inherit(TreeWidgetDevelopment, WidgetDevelopment, {});
            return TreeWidgetDevelopment;
        });