define([ "constant", "commons", "server", "appContext", "appModal", "AppPtMgrListViewWidgetDevelopment",
        "AppPtMgrCommons" ], function(constant, commons, server, appContext, appModal,
        AppPtMgrListViewWidgetDevelopment, AppPtMgrCommons) {
    var StringUtils = commons.StringUtils;
    var JDS = server.JDS;
    var Validation = server.Validation;
    var FileDownloadUtils = server.FileDownloadUtils;

    // 平台管理_产品集成_应用列表_视图组件二开
    var AppApplicationListViewWidgetDevelopment = function() {
        AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
    };

    // 接口方法
    commons.inherit(AppApplicationListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
        // 组件准备
        prepare : function() {
            var _self = this;
            var widget = _self.getWidget();
            // 获取传给组件的参数
            var params = _self.getWidgetParams();
            var otherConditions = [];
            // 归属产品UUID
            if (StringUtils.isNotBlank(params.appProductUuid)) {
                var condition = {
                    columnIndex : "appProductUuid",
                    value : params.appProductUuid,
                    type : "eq"
                };
                otherConditions.push(condition);
            }
            // 归属系统UUID
            if (StringUtils.isNotBlank(params.appSystemUuid)) {
                var condition = {
                    columnIndex : "appSystemUuid",
                    value : params.appSystemUuid,
                    type : "eq"
                };
                otherConditions.push(condition);
            }
            // 上级产品信息信息UUID
            if (StringUtils.isNotBlank(params.appPiUuid)) {
                var condition = {
                    columnIndex : "parentAppPiUuid",
                    value : params.appPiUuid,
                    type : "eq"
                };
                otherConditions.push(condition);
            }
            console.log(params);
            console.log(otherConditions);
            widget.addOtherConditions(otherConditions);
            // 触发列表准备加载事件
            _self.trigger("AppListView.prepare", params);
        },
        // 新增
        btn_add : function() {
            var _self = this;
            var widget = _self.getWidget();
            // 触发应用列表行点击新增事件
            widget.trigger("AppApplicationListView.addRow", {
                ui:_self
            });
        },
        // 删除
        btn_del : function() {
            var _self = this;
            if (!_self.checkSelection()) {
                return;
            }
            var selection = _self.getSelection();
            var name = selection[0].name;
            appModal.confirm("确定要删除应用[" + name + "]吗？", function(result) {
                if (result) {
                    JDS.call({
                        service : "appApplicationManager.remove",
                        data : selection[0],
                        success : function(result) {
                            appModal.success("删除成功！");
                            _self.trigger("AppApplicationListView.deleteRow", {
                                rowData : selection[0]
                            });
                            // 删除成功刷新列表
                            _self.refresh();
                        }
                    });
                }
            });
        },
        // 定义导出
        btn_export : function() {
            var _self = this;
            if (!_self.checkSelection()) {
                return;
            }
            var selection = _self.getSelection();
            var rowData = selection[0];
            var uuid = rowData.uuid;
            var type = "appApplication";
            if (StringUtils.isNotBlank(rowData.appPiUuid)) {
                uuid = rowData.appPiUuid;
                type = "appProductIntegration";
            }
            $.iexportData["export"]({
                uuid : uuid,
                type : type
            });
        },
        // 定义导入
        btn_import : function() {
            var _self = this;
            $.iexportData["import"]({
                callback : function() {
                    _self.refresh();
                }
            });
        },
        // 定义导入日志
        btn_imp_log : function() {
            $.iexportData.viewImportLog({});
        },
        // 集成信息导出
        btn_pi_info_export : function() {
            var _self = this;
            if (!_self.checkSelection(true)) {
                return;
            }
            var selection = _self.getSelection();
            for (var i = 0; i < selection.length; i++) {
                var piInfoExportUrl = getBackendUrl() + "/app/app/product/integration/export/pi/" + selection[i].appPiUuid;
                FileDownloadUtils.downloadTools(piInfoExportUrl);
            }
        },
        // 应用配置
        btn_app_config : function(e, options, rowData) {
            console.log(rowData);
        },
        // 行点击查看应用详情详情
        onClickRow : function(rowNum, rowData, $element, field) {
            var _self = this;
            var widget = _self.getWidget();
            // 触发应用列表行点击事件
            widget.trigger("AppApplicationListView.clickRow", {
                rowData : rowData
            });
        },

        afterRender: function (options, configuration) {

            var _self=this;
            this.widget.on('RefreshView',function(){
                _self.refresh();
            });
        },

    });
    return AppApplicationListViewWidgetDevelopment;
});