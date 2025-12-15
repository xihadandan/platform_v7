define(["constant", "commons", "server", "WidgetDevelopment"],
    function (constant, commons, server, WidgetDevelopment) {
        // 图表组件二开基础
        var StepNavWidgetDevelopment = function () {
            WidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(StepNavWidgetDevelopment, WidgetDevelopment, {
            //渲染前触发
            beforeRender: function (options, configuration) {
            },
            //渲染后触发
            afterRender: function (options, configuration) {
            },
            //按钮的二开（如有二开的按钮的uuid）
            setBtnDefaultEvent:function(options, configuration){
            	return [];
            }
        });
        return StepNavWidgetDevelopment;
    });