define(["constant", "commons", "server", "HeaderWidgetDevelopment"], function (constant, commons, server,
                                                                               HeaderWidgetDevelopment) {
    // 页面组件二开基础
    var AppNoticeHeaderWidgetDevelopment = function () {
        HeaderWidgetDevelopment.apply(this, arguments);
    };
    // 接口方法
    commons.inherit(AppNoticeHeaderWidgetDevelopment, HeaderWidgetDevelopment, {
        // 组件初始化
        init: function () {
            var _self = this;
            var pageContainer = _self.getPageContainer();
            pageContainer.on("AppNotice.Change", function () {
                // _self.getWidget().trigger(constant.WIDGET_EVENT.BadgeRefresh);
                _self.getWidget().refreshBadge();
            });
        }
    });
    return AppNoticeHeaderWidgetDevelopment;
});