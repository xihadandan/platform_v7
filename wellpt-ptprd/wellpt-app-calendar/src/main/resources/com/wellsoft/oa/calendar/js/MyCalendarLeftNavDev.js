define(["constant", "commons", "server", "LeftSidebarWidgetDevelopment", "appModal", "appContext"], function (constant, commons, server,
                                                                                                              LeftSidebarWidgetDevelopment, appModal, appContext) {
    var JDS = server.JDS;
    var currUser = server.SpringSecurityUtils.getUserDetails();
    // 页面组件二开基础
    var MyCalendarLeftNavDev = function () {
        LeftSidebarWidgetDevelopment.apply(this, arguments);
    };
    // 接口方法
    commons.inherit(MyCalendarLeftNavDev, LeftSidebarWidgetDevelopment, {
        //初始化编辑日历本图标的事件
        initCalendarEditIconEvent: function ($firstMenu, menus) {
            var _self = this;
            //定义编辑日历本事件
            $(menus.element).on("click", ".sidebar-nav-item-icon", function () {
                var $menuItem = $(this).parents(".mainnav-menu-item");
                var menuId = $menuItem.attr('menuid');
                var data = _self.widget.menuItemMap[menuId].calendar;
                console.log(data);
                //找到隐藏的日历本列表,触发对应的行的点击事件
                var index = $("td[title='" + data.uuid + "']").parents("tr").index();
                var $table = $("td[title='" + data.uuid + "']").parents("table");
                //要保持单选，所以需要先取消所有的check
                $table.bootstrapTable('uncheckAll');
                $table.bootstrapTable('check', index);

                //触发编辑按钮
                $(".btn_class_btn_edit_calendar").trigger("click");
                return false;
            });
        },
        // 组件初始化
        init: function () {
            var _self = this;
            var menus = this.getWidget();
            var $firstMenu = $(menus.element).find(".mainnav-menu-item").first();
            //监听日历本变更事件
            _self.initCalendarChangeListener();
            //初始化编辑图标的事件
            _self.initCalendarEditIconEvent($firstMenu, menus);

        },

        initCalendarChangeListener: function () {
            //监听表格刷新事件，当有新增或编辑日历本就刷新菜单
            var _self = this;
            this.getWidget().pageContainer.on(constant.WIDGET_EVENT.BootstrapTableRefresh, function (e, ui) {
                $("div.ui-wBootstrapTabs").find(".tab-pane.active").find("li.mainnav-menu-item").empty();
                appContext.getPageContainer().trigger(constant.WIDGET_EVENT.LeftSideBarRefreshDynamicItem);
            });
        }


    });
    return MyCalendarLeftNavDev;
});