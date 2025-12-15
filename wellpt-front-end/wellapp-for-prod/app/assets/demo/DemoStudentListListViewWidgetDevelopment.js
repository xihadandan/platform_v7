define([ "constant", "commons", "server", "appContext","appModal", "formBuilder", "ListViewWidgetDevelopment" ],
    function(constant, commons, server,appContext,appModal, formBuilder, ListViewWidgetDevelopment) {
        var StringUtils = commons.StringUtils;
        var StringBuilder = commons.StringBuilder;
        var JDS = server.JDS;

        // 平台二开示例--培训demo--视图列表基础--学生列表视图组件二开
        var DemoStudentListListViewWidgetDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(DemoStudentListListViewWidgetDevelopment, ListViewWidgetDevelopment, {
          // 加载数据前回调方法
          beforeLoadData: function (options, configuration, request) {
            var _self = this;
            console.log("加载数据前回调方法");
          },
        });
        return DemoStudentListListViewWidgetDevelopment;
    });

