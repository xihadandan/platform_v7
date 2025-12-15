define([ "constant", "commons", "server", "ListViewWidgetDevelopment" ],
    function(constant, commons, server, ListViewWidgetDevelopment) {
        var AppConfigSysListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        commons.inherit(AppConfigSysListDevelopment, ListViewWidgetDevelopment, {
            delItems:function(uuids){
                var self = this;
                if (!uuids || uuids.length == 0) {
                    appModal.error("请选择记录！");
                    return true;
                }
                appModal.confirm("确定要删除所选记录吗？",function(result){
                    if(result){
                        JDS.call({
                            service: "apiOutSystemFacadeService.deleteConfig",
                            data: [uuids],
                            version:"",
                            success: function (result) {
                                appModal.success("删除成功",function(){
                                    self.refresh()
                                })
                            }
                        });
                    }
                })
            },
            btn_delAll:function(){
                var uuids = this.getSelectionUuids()
                this.delItems(uuids)
            },
            btn_export:function(e){
                this.onExport("apiOutSystemConfig")
            },
            btn_import:function(e){
                this.onImport()
            }
        });
        return AppConfigSysListDevelopment;
    });

