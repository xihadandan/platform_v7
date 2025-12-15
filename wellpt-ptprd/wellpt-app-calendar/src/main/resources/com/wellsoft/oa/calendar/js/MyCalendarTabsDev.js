define(["jquery", "commons", "constant", "server", "BootstrapTabsWidgetDevelopment", "appModal", "multiOrg"], function ($, commons, constant, server, BootstrapTabsWidgetDevelopment, appModal, multiOrg) {
    var JDS = server.JDS;
    var currUser = server.SpringSecurityUtils.getUserDetails();

    var MyCalendarTabsDev = function () {
        BootstrapTabsWidgetDevelopment.apply(this, arguments);
    };
    commons.inherit(MyCalendarTabsDev, BootstrapTabsWidgetDevelopment, {
        init: function () {
            var _self = this;
            console.log("MyAttentionTabsDev.init");
            var widget = this.getWidget();
            //检查tabs上面的日历本搜索栏
            _self._initTopSearchBar();


        },

        onShowTab: function (tabIndex, options, configuration) {
            console.log("BootstrapTabs.onShowTab=========" + tabIndex);
        },

        _initTopSearchBar: function () {
            $("#btn_search_calendar").click(function () {
                var query = $("#query_text").val();
                var $tabWidget = $("div.ui-wBootstrapTabs");
                if (commons.StringUtils.isBlank(query)) {
                    $tabWidget.find(".tab-pane.active").find("li.mainnav-menu-item").show();
                } else {
                    $tabWidget.find(".tab-pane.active").find("li.mainnav-menu-item").each(function () {
                        var menuName = $(this).find("span").text();
                        if (menuName.indexOf(query) > -1) {
                            $(this).show();
                        } else {
                            $(this).hide();
                        }
                    })
                }
            });
            $("#btn_search_remove").click(function () {
                $("#query_text").val(null);
                $("#btn_search_calendar").trigger("click");
            });

        },

    });
    return MyCalendarTabsDev;
});