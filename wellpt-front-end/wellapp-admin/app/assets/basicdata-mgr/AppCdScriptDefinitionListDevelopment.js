define(["constant", "commons", "server", "appContext", "appModal", "formBuilder", "ListViewWidgetDevelopment"],
    function(constant, commons, server, appContext, appModal, formBuilder, ListViewWidgetDevelopment) {
        var StringUtils = commons.StringUtils;
        var StringBuilder = commons.StringBuilder;
        var JDS = server.JDS;

        // 平台应用_公共资源_脚本定义列表二开
        var AppCdScriptDefinitionListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppCdScriptDefinitionListDevelopment, ListViewWidgetDevelopment, {
            // 准备创建回调
            prepare: function() {},
            // 创建后回调
            create: function() {},
            // 初始化回调
            init: function() {},

            //删除
            btn_del: function() {
                var _self = this;
                var uuids = _self.getSelectionUuids();
                if (!uuids.length) {
                    appModal.error("请选择记录!");
                    return false;
                }
                appModal.confirm('确定要删除所选记录吗?', function(res) {
                    if (res) {
                        JDS.call({
                            service: "cdScriptDefinitionFacadeService.removeAll",
                            data: [uuids],
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
                this.onExport('cdScriptDefinition');
            },
            //定义导入
            btn_import: function() {
                this.onImport('cdScriptDefinition');
            },
            //查看依赖
            btn_dependence: function() {
                this.onDependence('cdScriptDefinition');
            }
        });
        return AppCdScriptDefinitionListDevelopment;
    });