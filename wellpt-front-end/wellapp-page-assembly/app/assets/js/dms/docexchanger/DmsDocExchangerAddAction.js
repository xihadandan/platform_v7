define(["jquery", "commons", "constant", "server", "appContext", "DmsListViewActionBase", "appModal"], function ($, commons,
                                                                                                                 constant, server, appContext, DmsListViewActionBase, appModal) {
    // 视图列表新增操作
    var DmsDocExchangerAddAction = function () {
        DmsListViewActionBase.apply(this, arguments);
    }
    commons.inherit(DmsDocExchangerAddAction, DmsListViewActionBase, {
        //新开页面新增
        btn_list_view_add_doc_exchanger: function (options) {
            var _self = this, moduleId;
            var urlParams = _self.getUrlParams();
            urlParams.target = options.target;
            urlParams.ep_ac_get = "btn_get_doc_exchanger";
            var currentUserAppData = appContext.getCurrentUserAppData();
            if (currentUserAppData.appData.module) {
                moduleId = currentUserAppData.appData.module.id;
            } else if (currentUserAppData.appData.dispatchAppPath) {
                moduleId = currentUserAppData.appData.dispatchAppPath.split("/")[2];
            } else {
                moduleId = options.appPath.split("/")[2];
            }
            urlParams.ep_moduleId = moduleId;
            if (constant.TARGET_POSITION.DIALOG == options.target) { //弹窗展示
                options.urlParams = urlParams;
                _self.dmsDataServices.openDialog(options);
            } else {
                _self.dmsDataServices.openWindow({
                    urlParams: urlParams,
                    useUniqueName: false,
                    ui: options.ui,
                    target: options.target,
                    targetWidgetId: options.targetWidgetId
                });
            }
        },


    });

    return DmsDocExchangerAddAction;
});