define([ "server", "commons", "constant", "jquery-ui", "bootbox", "appContext" ], function(server, commons, constant,
        ui, bootbox, appContext) {
    var JDS = server.JDS;
    var UrlUtils = commons.UrlUtils;

    // 启动门户配置
    var startAppPortalConfig = function(options) {
        var pageUuid = WebApp.pageDefinition.uuid || WebApp.currentUserAppData.pageUuid;
        var url = ctx + "/web/app/portal/config/page/" + pageUuid;
        appContext.getWindowManager().refresh(url);
    }
    return startAppPortalConfig;
});