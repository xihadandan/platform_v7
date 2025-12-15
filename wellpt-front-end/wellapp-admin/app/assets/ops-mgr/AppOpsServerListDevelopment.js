define(["constant", "commons", "server", "appContext", "appModal", "formBuilder", "ListViewWidgetDevelopment"],
    function(constant, commons, server, appContext, appModal, formBuilder, ListViewWidgetDevelopment) {
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
            prepare: function() {},
            // 创建后回调
            create: function() {},
            // 初始化回调
            init: function() {},
            onLoadSuccess: function() {
                var _self = this;
                var dataList = _self.getData();
                var ipPortList = new Array();
                for (var i = 0; i < dataList.length; i++) {
                    (function(index) {
                        var uuid = dataList[index].uuid;
                        var ip = dataList[index].ip;
                        var port = dataList[index].port;
                        var strs = [uuid, ip + ":" + port];
                        ipPortList.push(strs);
                    })(i);
                }
                JDS.call({
                    service: "serverRegisterCenterFacadeService.testConnectList",
                    data: [ipPortList],
                    async: true,
                    success: function(result) {
                        for (var i = 0; i < result.data.length; i++) {
                            var uuid = result.data[i][0];
                            var flg = result.data[i][1] === "false" ? false : true;
                            var tipClass = flg ? 'server_running' : 'server_stoped';
                            var text = flg ? '运行中' : '已停止';
                            var tdlist = $(_self.widget.$tableElement.find('tr')[i + 1]).find('td');
                            if (tdlist.eq(0).find('input').val() == uuid) {
                                tdlist.eq(1).append('<span class="' + tipClass + '">' + text + '</span>');
                            }
                        }
                    }
                });
            }
        });
        return AppOpsServerListDevelopment;
    });