define(["constant", "commons", "server", "ListViewWidgetDevelopment"],
    function(constant, commons, server, ListViewWidgetDevelopment) {
        var AppDataTransListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        commons.inherit(AppDataTransListDevelopment, ListViewWidgetDevelopment, {
            btn_delAll: function(e) { // 批量删除
                var self = this
                var datas = this.getSelection()
                var ids = []

                if (datas.length == 0) {
                    appModal.error("请选择记录!");
                    return;
                } else {
                    for (var i = 0; i < datas.length; i++) {
                        ids.push(datas[i].id)
                    }
                }
                appModal.confirm("确定要删除所选记录吗？", function(res) {
                    if (res) {
                        self.deleteOperate(ids)
                    }
                })
            },
            deleteOperate: function(ids) {
                var self = this;
                JDS.call({
                    service: "exchangeDataTypeService.deleteAllByIds",
                    data: [ids],
                    version: "",
                    success: function(result) {
                        appModal.success("删除成功!", function() {
                            self.refresh()
                        });
                    }
                });
            }
        });
        return AppDataTransListDevelopment;
    });