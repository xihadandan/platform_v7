define(["constant", "commons", "server", "appContext", "appModal", "AppPtMgrListViewWidgetDevelopment",
    "AppPtMgrCommons"
], function(constant, commons, server, appContext, appModal,
    AppPtMgrListViewWidgetDevelopment, AppPtMgrCommons) {
    var StringUtils = commons.StringUtils;
    var JDS = server.JDS;

    var AppDataIntegrationConfigListViewWidgetDevelopment = function() {
        AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
    };

    // 接口方法
    commons.inherit(AppDataIntegrationConfigListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
        // 组件准备
        prepare: function() {},

        beforeRender: function(options, configuration) {},


        btn_copy: function() {
            var rowData = this.getSelectRowData();
            if (rowData.length > 0) {
                this.widget.trigger("AppDIConfigListView.editRow", {
                    ui: this.widget,
                    copy: true,
                    rowData: rowData[0],
                });
            }
        },

        // 删除
        btn_del: function() {
            var _self = this;
            var rowData = _self.getSelectRowData();
            if (rowData.length > 0) {
                var name = rowData[0].name;
                appModal.confirm("确认要删除数据交换吗?", function(res) {
                    if (res) {
                        server.JDS.call({
                            service: "diConfigFacadeService.deleteDiConfigs",
                            version: '',
                            data: [(function() {
                                var uuids = [];
                                for (var i = 0, len = rowData.length; i < len; i++) {
                                    uuids.push(rowData[i].UUID);
                                }
                                return uuids;
                            })()],
                            success: function(result) {
                                if (result.success) {
                                    appModal.info("刪除成功");
                                    _self.refresh(); //刷新表格
                                }
                            },
                            error: function(jqXHR) {
                                var faultData = JSON.parse(jqXHR.responseText);
                                appModal.alert(faultData.msg);
                            }
                        });
                    }
                });
            }
        },

        getSelectRowData: function() {
            var _self = this;
            var $toolbarDiv = $(event.target).closest("div");
            var rowData = [];
            if ($toolbarDiv.is(".div_lineEnd_toolbar")) { //行级点击操作
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


        btn_add: function() {
            this.widget.trigger("AppDIConfigListView.editRow", {
                ui: this.widget
            });
        },

        // 行点击查看详情
        onClickRow: function(rowNum, rowData, $element, field) {
            // 触发应用列表行点击事件
            this.widget.trigger("AppDIConfigListView.editRow", {
                rowData: rowData,
                ui: this.widget
            });
        },

        definition_import: function() {
            var _self = this;
            // 定义导入
            $.iexportData["import"]({
                callback: function() {
                    _self.refresh();
                }
            });
        },

        definition_export: function() {
            // 定义导出
            var rowData = this.getSelectRowData();
            if (rowData.length > 0) {
                $.iexportData["export"]({
                    uuid: rowData[0].uuid,
                    type: 'diConfigEntity'
                });
            } else {
                appModal.alert('请选择导出的数据交换定义！');
            }

        },

        afterRender: function(options, configuration) {
            var _self = this;
            this.widget.on('AppDIConfigListView.refresh', function() {
                _self.refresh();
            });
        },

        refresh: function() {
            //刷新徽章
            var tabpanel = this.widget.element.parents('.active');
            if (tabpanel.length > 0) {
                var id = tabpanel.attr('id');
                id = id.substring(0, id.indexOf('-'));
                $("#" + id).trigger(constant.WIDGET_EVENT.BadgeRefresh, {
                    targetTabName: '数据交换',
                });
            }
            return this.getWidget().refresh(this.options);
        },


    });
    return AppDataIntegrationConfigListViewWidgetDevelopment;
});