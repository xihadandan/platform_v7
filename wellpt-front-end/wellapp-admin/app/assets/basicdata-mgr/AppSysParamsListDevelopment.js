define(["constant", "commons", "server", "ListViewWidgetDevelopment"],
    function(constant, commons, server, ListViewWidgetDevelopment) {
        // 视图组件二开基础
        var AppSysParamsListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppSysParamsListDevelopment, ListViewWidgetDevelopment, {

            btn_delAll: function(e) { // 批量删除
                var self = this
                var uuids = this.getSelectionUuids()
                if (uuids.length == 0) {
                    appModal.error("请选择记录!");
                    return;
                }
                appModal.confirm("确定要删除所选记录吗？", function(res) {
                    if (res) {
                        JDS.call({
                            service: "sysParamItemConfigMgr.removeAll",
                            data: [uuids],
                            version: "",
                            success: function(result) {
                                appModal.success("删除成功!", function() {
                                    self.refresh()
                                });
                            }
                        });
                    }
                })
            }
        });

        return AppSysParamsListDevelopment;
    });