define(["constant", "commons", "server", "MyCalendarLeftNavDev", "appModal"], function (constant, commons, server,
                                                                                        MyCalendarLeftNavDev, appModal) {
//	var JDS = server.JDS;
//	var currUser=server.SpringSecurityUtils.getUserDetails();
    // 页面组件二开基础
    var OtherUserCalendarLeftNavDev = function () {
        MyCalendarLeftNavDev.apply(this, arguments);
    };
    // 接口方法
    commons.inherit(OtherUserCalendarLeftNavDev, MyCalendarLeftNavDev, {
        initCalendarEditIconEvent: function ($firstMenu, menus) {

        },

        initCalendarChangeListener: function () {
            //他人日程不需要监听
        }
    });
    return OtherUserCalendarLeftNavDev;
});