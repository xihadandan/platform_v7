define([ "constant", "commons", "server", "WidgetDevelopment" ],
    function(constant, commons, server, WidgetDevelopment) {
        // 页面组件二开基础
        var AccordionWidgetDevelopment = function() {
            WidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AccordionWidgetDevelopment, WidgetDevelopment,  {
            //渲染前事件
            beforeRender : function( options, configuration){
            },
            //渲染后事件
            afterRender : function( options, configuration){
            },
            //切换手风琴的触发事项
            onShowTab : function( tabIndex, options, configuration){
            }


        });
        return AccordionWidgetDevelopment;
    });