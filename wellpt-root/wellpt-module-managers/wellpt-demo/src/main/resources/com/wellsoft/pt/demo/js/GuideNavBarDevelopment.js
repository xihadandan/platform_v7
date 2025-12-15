define(["constant", "commons", "server", "HeaderWidgetDevelopment"], function (constant, commons, server,
                                                                               HeaderWidgetDevelopment) {

    // 定义组件二开类
    var GuideNavBarDevelopment = function () {

        HeaderWidgetDevelopment.apply(this, arguments);
    };

    //继承HeaderWidgetDevelopment, 二开钩子在HeaderWidgetDevelopment的父类WidgetDevelopment中声明, 如prepare等
    commons.inherit(GuideNavBarDevelopment, HeaderWidgetDevelopment, {
        // 组件初始化
        init: function () {
            var _self = this;

            //获取组件对象实例
            var _thisWidget = _self.getWidget();

            //获取组件对象实例
            //组件对象实例是一个jquery对象
            //实例对外提供了哪些方法，参照jquery-ui-wWidget.js
            var _thisWidget = _self.getWidget();

            //获取组件对象实例定义参数
            var _thisConfig = _thisWidget.getConfiguration();


            //获取页面容器对象实例
            //页面容器对象实例也是一个组件对象实例
            //实例对外提供了哪些方法，也一样参照jquery-ui-wWidget.js
            var pageContainer = _self.getPageContainer();
            //var pageContainer = appContext.pageContainer;//这个方式也可获取到页面容器对象


        },	// 组件准备
        prepare: function () {
            console.log("prepare...demo..");
        },
        // 组件创建
        create: function () {
            console.log("create...demo..");
        }
    });
    return GuideNavBarDevelopment;
});