define(["constant", "commons", "server", "appContext", "appModal", "AppPtMgrListViewWidgetDevelopment",
    "AppPtMgrCommons"], function (constant, commons, server, appContext, appModal,
                                  AppPtMgrListViewWidgetDevelopment, AppPtMgrCommons) {
    var StringUtils = commons.StringUtils;
    var JDS = server.JDS;

    var AppDILogListViewWidgetDevelopment = function () {
        AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
    };

    // 接口方法
    commons.inherit(AppDILogListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
        // 组件准备
        prepare: function () {
        },

        beforeRender: function (options, configuration) {
        },


        // 行点击查看详情
        onClickRow: function (rowNum, rowData, $element, field) {

            this.widget.trigger("AppDIProcessorLog.showDetails", {
                rowData: rowData,
                ui: this.widget
            });


        },

        batch_redelivery: function () {
            var _self = this;
            var rowData = _self.getSelectRowData();
            if (rowData.length > 0) {
                appModal.confirm("确认要重发数据交换吗?", function (result) {
                    if (result) {
                        server.JDS.call({
                            service: "diDataProcessorLogFacadeService.redeliverData",
                            version: '',
                            data: [(function () {
                                var uuids = [];
                                for (var i = 0, len = rowData.length; i < len; i++) {
                                    uuids.push(rowData[i].UUID);
                                }
                                return uuids;
                            })()],
                            success: function (result) {
                                appModal.info("服务重发中...");
                            },
                            error: function (jqXHR) {
                                var faultData = JSON.parse(jqXHR.responseText);
                                appModal.alert(faultData.msg);
                            }
                        });
                    }
                });
            }
        },

        getSelectRowData: function () {
            var _self = this;
            var $toolbarDiv = $(event.target).closest("div");
            var rowData = [];
            if ($toolbarDiv.is(".div_lineEnd_toolbar")) {//行级点击操作
                var index = $toolbarDiv.attr("index");
                var allData = _self.getData();
                rowData = [allData[index]];
            } else {
                if (_self.getSelectionIndexes().length == 0) {
                    return [];
                }
                rowData = _self.getSelections();
            }
            return rowData;
        },

        afterRender: function (options, configuration) {

        }


    });
    return AppDILogListViewWidgetDevelopment;
});