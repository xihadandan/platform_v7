define([ "constant", "commons", "server", "appContext","appModal", "formBuilder", "ListViewWidgetDevelopment" ],
    function(constant, commons, server,appContext,appModal, formBuilder, ListViewWidgetDevelopment) {
        var StringUtils = commons.StringUtils;
        var StringBuilder = commons.StringBuilder;
        var JDS = server.JDS;

        // 平台应用_运维管理_应用节点信息列表二开
        var AppOpsServerListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppOpsServerListDevelopment, ListViewWidgetDevelopment, {
            // 准备创建回调
            prepare: function () {},
            // 创建后回调
            create: function () {},
            // 初始化回调
            init: function() {},
            onLoadSuccess: function () {
                var _self = this;
                var dataList = _self.getData();
                for (var i = 0; i < dataList.length; i++) {
                    (function (index) {
                        var ip = dataList[index].ip;
                        var port = dataList[index].port;
                        JDS.call({
                            service: "serverRegisterCenterFacadeService.testConnect",
                            data: [ip, port],
                            async: true,
                            success: function (result) {
                                var tipClass = result.data ? 'server_running' : 'server_stoped';
                                var text = result.data ? '运行中' : '已停止';
                                $(_self.widget.$tableElement.find('tr')[index+1]).find('td').eq(1).append('<span class="' + tipClass + '">' + text + '</span>');
                            }
                        });
                    })(i);
                }
            }
        });
        return AppOpsServerListDevelopment;
    });

