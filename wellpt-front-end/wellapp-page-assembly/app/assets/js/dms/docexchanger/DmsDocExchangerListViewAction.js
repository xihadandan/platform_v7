define(["jquery", "commons", "constant", "server", "appContext", "DmsListViewViewAction", "appModal"], function ($, commons,
                                                                                                                 constant, server, appContext, DmsListViewViewAction, appModal) {
    // 文档交换_视图列表操作
    var DmsDocExchangerListViewAction = function () {
        DmsListViewViewAction.apply(this, arguments);
    }
    commons.inherit(DmsDocExchangerListViewAction, DmsListViewViewAction, {
        //查看
        btn_list_view_doc_exchanger: function (options) {
            var _self = this;
            var ui = options.ui;
            // 选中数据检验
            var selection = [];
            if (options.view.selectRowData) {//单行选中数据
                selection = [options.view.selectRowData];
                delete options.view.selectRowData;
            } else {
                selection = ui.getSelections();
            }
            var actionFunction = options.appFunction;
            var promptMsg = actionFunction.promptMsg;
            if (selection.length === 0 && commons.StringUtils.isNotBlank(promptMsg)) {
                appModal.error(promptMsg);
                return;
            } else if (selection.length === 0) {
                ui.refresh(false);
                return;
            }
            //多选情况下，如果是弹窗的话，默认切成新窗口
            if (selection.length > 1 && constant.TARGET_POSITION.DIALOG == options.target) {
                options.target = constant.TARGET_POSITION.BLANK;
            }

            for (var i = 0; i < selection.length; i++) {
                this.openPage(selection[i], options, options.displayAsLabel != undefined ? options.displayAsLabel : true);
            }


        },

        openPage: function (rowData, options, displayAsLabel) {
            var _self = this, moduleId;
            var urlParamOptions = {
                ui: options.ui,
                appFunction: options.appFunction,
                rowdata: rowData,
            };
            var urlParams = _self.getUrlParams(urlParamOptions);
            var currentUserAppData = appContext.getCurrentUserAppData();
            if (currentUserAppData.appData.module) {
                moduleId = currentUserAppData.appData.module.id;
            } else if (currentUserAppData.appData.dispatchAppPath) {
                moduleId = currentUserAppData.appData.dispatchAppPath.split("/")[2];
            } else {
                moduleId = options.appPath.split("/")[2];
            }
            urlParams.ep_displayAsLabel = displayAsLabel;
            urlParams.ep_moduleId = moduleId;
            urlParams.target = options.target;
            urlParams.docExchangeRecordUuid = this.getDocExchangeRecordUuid(options, rowData);
            if (constant.TARGET_POSITION.DIALOG == options.target) { // 弹窗展示
                options.urlParams = urlParams;
                _self.dmsDataServices.openDialog(options);

            } else {
                _self.dmsDataServices.openWindow({
                    urlParams: urlParams,
                    ui: options.ui
                });
            }


        },


        //编辑
        btn_list_edit_doc_exchanger: function (options) {
            options.displayAsLabel = false;
            this.btn_list_view_doc_exchanger(options);
        },

        getDocExchangeRecordUuid: function (options, rowData) {
            var ui = options.ui;
            var docExchangerConfiguration = ui.options.containerDefinition.configuration;
            var docExchangeUuidColumn = docExchangerConfiguration.view.docExchangeUuidColumn;
            if (!docExchangeUuidColumn) {
                appModal.alert('文档交换器配置未指定文档交换UUID');
                throw new Error('未指定文档交换UUID');
            }
            return rowData[docExchangeUuidColumn];

        },

        //签收
        btn_batch_sign_doc_exchanger: function (options) {
            var _self = this;
            var ui = options.ui;
            // 选中数据检验
            var selection = ui.getSelections();
            if (selection.length === 0) {
                appModal.error("请选择操作记录!");
                return;
            }

            var docExchangeUuids = [];
            for (var i = 0, len = selection.length; i < len; i++) {
                docExchangeUuids.push({
                    docExcRecordUuid: this.getDocExchangeRecordUuid(options, selection[i])
                });

            }

            if (docExchangeUuids.length == 0) {
                appModal.alert('CMS文档交换器配置可视化未指定文档交换UUID');
                return false;
            }

            console.log(docExchangeUuids);
            options.data = docExchangeUuids;
            var dmsId = $(ui.element).data('dms_id');
            var acId = options.appFunction.id;
            var lvId = ui.getId();

            var urlParams = {
                dms_id: dmsId,
                ac_id: acId,
                lv_id: lvId,
            };
            options.urlParams = urlParams;
            this.dmsDataServices.performed(options);
            return false;

        }
    });

    return DmsDocExchangerListViewAction;
});