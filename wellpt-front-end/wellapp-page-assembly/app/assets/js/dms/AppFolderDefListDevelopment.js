define([ "constant", "commons", "server", "ListViewWidgetDevelopment" ],
    function(constant, commons, server, ListViewWidgetDevelopment) {
        // 视图组件二开基础
        var AppFolderDefListWidgetDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppFolderDefListWidgetDevelopment, ListViewWidgetDevelopment, {
            getTableOptions:function(bootstrapTableOptions){
                this.addOtherConditions([{
                    columnIndex : 'systemUnitId',
                    value : SpringSecurityUtils.getCurrentUserUnitId(),
                    type : 'eq'
                }])
            },
            btn_delAll: function(e){           // 批量删除
                var self = this
                var uuids = this.getSelectionUuids()
                if (uuids.length == 0) {
                    appModal.error("请选择记录！");
                    return;
                }
                appModal.confirm("确定要删除所选记录？",function(result){
                    if(result){
                        JDS.call({
                            service:"dmsFolderMgr.removeAll",
                            data:[uuids],
                            version:"",
                            success:function(result) {
                                appModal.success("删除成功!",function(){
                                    self.refresh()
                                });
                            }
                        });
                    }
                })
            },
            btn_export:function(e){
                this.onExport("dmsFolder")
            },
            btn_import:function(e){
                this.onImport()
            }
        });

        return AppFolderDefListWidgetDevelopment;
    });

