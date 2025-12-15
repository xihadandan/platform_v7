define(["jquery", "commons", "constant", "server", "BootstrapTabsWidgetDevelopment", "appModal", "multiOrg"], function ($, commons, constant, server, BootstrapTabsWidgetDevelopment, appModal) {
    var JDS = server.JDS;
    var currUser = server.SpringSecurityUtils.getUserDetails();

    var MyAttentionTabsDev = function () {
        BootstrapTabsWidgetDevelopment.apply(this, arguments);
    };
    commons.inherit(MyAttentionTabsDev, BootstrapTabsWidgetDevelopment, {
        init: function () {
            var _self = this;
            console.log("MyAttentionTabsDev.init");
            var widget = this.getWidget();
            //默认关注群组按钮先隐藏
            $(widget.element).parent().find("button.js-add-attention-group").css("margin-top", "15px");
            $(widget.element).parent().find("button.js-add-attention-user").css("margin-top", "15px");

            //检查tabs上面的搜索栏
            _self._initTopSearchBar();


        },

        onShowTab: function (tabIndex, options, configuration) {
            console.log("BootstrapTabs.onShowTab=========" + tabIndex);
            var widget = this.getWidget();
            if (tabIndex == 0) {
                $(widget.element).parent().find("button.js-add-attention-user").show();
                $(widget.element).parent().find("button.js-add-attention-group").hide();
            } else if (tabIndex == 1) {
                $(widget.element).parent().find("button.js-add-attention-user").hide();
                $(widget.element).parent().find("button.js-add-attention-group").show();
            }
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
    return MyAttentionTabsDev;
});