define(["constant", "commons", "server", "appContext", "appModal", "AppPtMgrListViewWidgetDevelopment",
    "AppPtMgrCommons"], function (constant, commons, server, appContext, appModal,
                                  AppPtMgrListViewWidgetDevelopment, AppPtMgrCommons) {
    var StringUtils = commons.StringUtils;
    var JDS = server.JDS;

    // 平台管理_产品集成_模块_导入规则定义列表_视图组件二开
    var AppImportRuleListViewWidgetDevelopment = function () {
        AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
    };

    // 接口方法
    commons.inherit(AppImportRuleListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
        // 组件准备
        prepare: function () {
        },

        beforeRender: function (options, configuration) {
            // 归属模块ID
            this.widget.addParam('moduleId', this._moduleId())
        },


        _moduleId: function () {
            return this.getWidgetParams().moduleId
        },


        // 删除
        btn_delete: function () {
            var _self = this;
            var rowData = _self.getSelectRowData();
            if (rowData.length > 0) {
                var name = rowData[0].name;
                appModal.confirm("确认要删除数据导入规则定义吗?", function (result) {
                    if (result) {
                        server.JDS.call({
                            service: "excelImportRuleService.removeAll",
                            version: '',
                            data: [(function () {
                                var uuids = [];
                                for (var i = 0, len = rowData.length; i < len; i++) {
                                    uuids.push(rowData[i].uuid);
                                }
                                return uuids;
                            })()],
                            success: function (result) {
                                appModal.info("刪除成功");
                                _self.refresh();//刷新表格
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


        btn_add: function () {

            this.widget.trigger("AppImportRuleListView.editRow", {
                ui: this.widget
            });
        },

        // 行点击查看详情
        onClickRow: function (rowNum, rowData, $element, field) {
            this.widget.trigger("AppImportRuleListView.editRow", {
                rowData: rowData,
                ui: this.widget
            });
        },

        definition_import: function () {
            var _self = this;
            // 定义导入
            $.iexportData["import"]({
                callback: function () {
                    _self.refresh();
                }
            });
        },

        definition_export: function () {
            // 定义导出
            var rowData = this.getSelectRowData();
            if (rowData.length > 0) {
                $.iexportData["export"]({
                    uuid: rowData[0].uuid,
                    type: 'excelImportRule'
                });
            } else {
                appModal.alert('请选择导出的数据导入规则定义！');
            }

        },

        afterRender: function (options, configuration) {
            var _self=this;
            this.widget.on('AppImportRuleListView.refresh',function(){
                _self.refresh();
            });
        },

        refresh: function () {
            //刷新徽章
            var tabpanel = this.widget.element.parents('.active');
            if (tabpanel.length > 0) {
                var id = tabpanel.attr('id');
                id = id.substring(0, id.indexOf('-'));
                $("#" + id).trigger(constant.WIDGET_EVENT.BadgeRefresh, {
                    targetTabName: '数据导入规则',
                });
            }
            return this.getWidget().refresh(this.options);
        },

    });
    return AppImportRuleListViewWidgetDevelopment;
});