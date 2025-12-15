define(["constant", "commons", "server", "ListViewWidgetDevelopment"],
    function(constant, commons, server, ListViewWidgetDevelopment) {
        var AppRouteListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        commons.inherit(AppRouteListDevelopment, ListViewWidgetDevelopment, {
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
                            service: "exchangeRouteService.deleteAllByIds",
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

        return AppRouteListDevelopment;
    });