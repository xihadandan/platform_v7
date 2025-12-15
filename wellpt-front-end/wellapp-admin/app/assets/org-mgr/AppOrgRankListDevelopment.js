define([ "constant", "commons", "server", "appContext","appModal", "ListViewWidgetDevelopment" ],
    function(constant, commons, server,appContext,appModal, ListViewWidgetDevelopment) {
        var AppOrgRankListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppOrgRankListDevelopment, ListViewWidgetDevelopment, {
            // 初始化回调

        });
        return AppOrgRankListDevelopment;
    });

