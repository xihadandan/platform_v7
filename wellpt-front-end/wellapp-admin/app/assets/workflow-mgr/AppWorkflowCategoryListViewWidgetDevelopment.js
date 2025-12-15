define(["constant", "commons", "server", "appContext", "appModal", "formBuilder", "AppPtMgrListViewWidgetDevelopment"],
    function(constant, commons, server, appContext, appModal, formBuilder, AppPtMgrListViewWidgetDevelopment) {
        var StringUtils = commons.StringUtils;
        var StringBuilder = commons.StringBuilder;
        var JDS = server.JDS;

        // 平台管理_流程管理_流程分类_列表组件二开
        var AppWorkflowCategoryListViewWidgetDevelopment = function() {
            AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppWorkflowCategoryListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
            // 准备创建回调
            prepare: function() {
                var creatorCondition = {
                    columnIndex: "systemUnitId",
                    value: SpringSecurityUtils.getCurrentUserUnitId(),
                    type: "eq"
                };

                var otherConditions = [];
                otherConditions.push(creatorCondition);
                this.addOtherConditions(otherConditions)
            },
            // 创建后回调
            create: function() {},
            // 初始化回调
            init: function() {},

            //删除
            btn_del: function() {
                var _self = this;
                var uuids = _self.getSelectionUuids();
                if (!uuids.length) {
                    appModal.alert("请选择记录!");
                    return false;
                }
                appModal.confirm('确定要删除所选记录吗?', function(res) {
                    if (res) {
                        JDS.restfulPost({
							url: ctx + "/api/workflow/category/deleteAll",
                            data: {uuids: uuids},
							contentType: 'application/x-www-form-urlencoded',
                            success: function(result) {
                                appModal.success("删除成功!");
                                _self.refresh(true);
                            }
                        });
                    }
                });
            },
            //定义导出
            btn_export: function() {
                this.onExport('flowCategory');
            },
            //定义导入
            btn_import: function() {
                this.onImport();
            },
            //查看依赖
            btn_dependence: function() {
                this.onDependence('flowCategory');
            }
        });
        return AppWorkflowCategoryListViewWidgetDevelopment;
    });