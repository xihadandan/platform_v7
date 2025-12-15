define([ "constant", "commons", "server", "appContext","appModal", "ListViewWidgetDevelopment" ],
    function(constant, commons, server,appContext,appModal, ListViewWidgetDevelopment) {

        var AppOrgTypeListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppOrgTypeListDevelopment, ListViewWidgetDevelopment, {
            // 初始化回调

        });
        return AppOrgTypeListDevelopment;
    });

