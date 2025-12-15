define([ "constant", "commons", "server", "ListViewWidgetDevelopment" ],
    function(constant, commons, server, ListViewWidgetDevelopment) {
        // 视图组件二开基础
        var AppDyformCommonFieldListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 平台管理_公共资源_表单字段列表组件二开
        commons.inherit(AppDyformCommonFieldListDevelopment, ListViewWidgetDevelopment, {
            btn_del: function(){
                var self = this;
                var uuids = self.getSelectionUuids();
                var rowDatas = self.getSelections();
                if (!uuids.length) {
                    appModal.error("请选择记录！");
                    return;
                }
                $.each(rowDatas,function (i,item) {
                    if(item.scope > 0){
                        appModal.alert("字段[" + item.displayName + "]被引用，无法删除！");
                        return false;
                    }
                });

                var ids = uuids.join(",");
                appModal.confirm("确定要删除所选资源？",function(result){
                    if(result){
                        $.ajax({
                            type : "POST",
                            async : false,
                            url : ctx + "/pt/dyform/field/deleteAll",
                            data : {
                                "ids" : ids
                            },
                            success : function(result) {
                                appModal.success("删除成功！");
                                self.refresh()
                            },
                            error : function(result) {
                            }
                        });
                    }
                });
            }
        });

        return AppDyformCommonFieldListDevelopment;
    });

