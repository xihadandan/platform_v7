define(["jquery", "commons", "constant", "server", "appContext", "DmsListViewActionBase"], function ($, commons,
                                                                                                     constant, server, appContext, DmsListViewActionBase) {
    var StringUtils = commons.StringUtils;
    // 视图列表编辑操作
    var DmsListViewEditAction = function () {
        DmsListViewActionBase.apply(this, arguments);
    }
    commons.inherit(DmsListViewEditAction, DmsListViewActionBase, {
        btn_list_view_edit: function (options) {
            var _self = this;
            var ui = options.ui;

            // 当前操作的数据
            var selection = options.rowData != null ? options.rowData : ui.getSelections();
            if(!_self.checkSelection(options)) {
                return;
            }

            if (selection.length == 1) { //单选情况下
                var rowdata = selection[0];
                var urlParamOptions = {
                    ui: ui,
                    appFunction: options.appFunction,
                    rowdata: rowdata,
                };
                var urlParams = _self.getUrlParams(urlParamOptions);
                if (!urlParams.idValue) {
                    //无主键值的情况下
                    return;
                }
                urlParams = $.extend(urlParams, options.params || {});
                // 编辑模式
                urlParams.ep_view_mode = "1";
                urlParams.target = options.target;
                if (constant.TARGET_POSITION.DIALOG == options.target) { //弹窗展示
                    options.urlParams = urlParams;
                    _self.dmsDataServices.openDialog(options);
                } else {
                    _self.dmsDataServices.openWindow({
                        urlParams: urlParams,
                        ui: ui
                    });
                }

            } else { //多选模式，固定是通过打开新窗口来实现
                // 批量打开编辑
                for (var i = 0; i < selection.length; i++) {
                    var rowdata = selection[i];
                    var urlParamOptions = {
                        ui: ui,
                        appFunction: options.appFunction,
                        rowdata: rowdata,
                    };
                    var urlParams = _self.getUrlParams(urlParamOptions);
                    if (!urlParams.idValue) {
                        //无主键值的情况下
                        continue;
                    }
                    // 编辑模式
                    urlParams.ep_view_mode = "1";
                    urlParams.target = options.target;
                    _self.dmsDataServices.openWindow({
                        urlParams: urlParams,
                        ui: ui
                    });
                }

            }


        }
    });

    return DmsListViewEditAction;
});