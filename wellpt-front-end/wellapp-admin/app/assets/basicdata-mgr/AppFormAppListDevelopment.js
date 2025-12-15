define(["constant", "commons", "server", "appContext", "appModal", "formBuilder", "ListViewWidgetDevelopment"],
    function(constant, commons, server, appContext, appModal, formBuilder, ListViewWidgetDevelopment) {
        var StringUtils = commons.StringUtils;
        var StringBuilder = commons.StringBuilder;
        var JDS = server.JDS;

        // 平台应用_公共资源_表单应用配置_列表二开
        var AppFormAppListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppFormAppListDevelopment, ListViewWidgetDevelopment, {
            // 准备创建回调
            prepare: function() {},
            // 创建后回调
            create: function() {},
            // 初始化回调
            init: function() {},

            //删除
            btn_del: function() {
                var _self = this;
                var ids = _self.getSelectionsFields('applicationUuid');
                if (!ids.length) {
                    appModal.alert("请选择记录!");
                    return false;
                }
                appModal.confirm('确定要删除所选记录吗?', function(res) {
                    if (res) {
                        JDS.call({
                            service: "businessApplicationService.deleteByIds",
                            data: [ids],
                            success: function(result) {
                                appModal.success("删除成功!");
                                _self.refresh(true);
                            }
                        });
                    }
                });
            }
        });
        return AppFormAppListDevelopment;
    });