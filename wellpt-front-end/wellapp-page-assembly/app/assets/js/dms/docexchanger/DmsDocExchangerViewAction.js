define(["jquery", "commons", "constant", "server", "appContext", "DmsListViewViewAction", "appModal"], function ($, commons,
                                                                                                                 constant, server, appContext, DmsListViewViewAction, appModal) {
    // 视图列表查看文档交换记录操作
    var DmsDocExchangerViewAction = function () {
        DmsListViewViewAction.apply(this, arguments);
    }
    commons.inherit(DmsDocExchangerViewAction, DmsListViewViewAction, {
        //查看
        btn_view_doc_exchanger: function (options) {
            options.urlEpParams = {
                ep_ac_get: "btn_get_doc_exchanger"
            }
            this.btn_list_view_view(options);
        },

    });

    return DmsDocExchangerViewAction;
});