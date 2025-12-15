define(["constant", "commons", "server", "ListViewWidgetDevelopment"],
    function(constant, commons, server, ListViewWidgetDevelopment) {
        // 视图组件二开基础
        var AppDXExchangeLogListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppDXExchangeLogListDevelopment, ListViewWidgetDevelopment, {
            btn_del: function() {
                var self = this;
                var uuids = this.getSelectionUuids();
                if (!uuids.length) {
                    appModal.error("请选择记录!");
                    return;
                }
                appModal.confirm("确定要删除所选记录吗？", function(res) {
                    if (res) {
                        JDS.call({
                            service: "exchangeDataConfigService.removeDXLogs",
                            data: [uuids],
                            success: function(result) {
                                appModal.success("删除成功!");
                                self.refresh()
                            }
                        });
                    }
                })
            }
        });

        return AppDXExchangeLogListDevelopment;
    });