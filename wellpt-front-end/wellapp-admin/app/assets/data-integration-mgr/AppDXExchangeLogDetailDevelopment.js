define([ "constant", "commons", "server", "appContext","appModal", "HtmlWidgetDevelopment" ],
    function(constant, commons, server,appContext,appModal, HtmlWidgetDevelopment) {
        var JDS = server.JDS;

        var AppDXExchangeLogDetailDevelopment = function() {
            HtmlWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppDXExchangeLogDetailDevelopment, HtmlWidgetDevelopment, {
            init: function() {
                var bean = {
                    "uuid": null,
                    "batchId": null,
                    "fromUnitId" : null,
                    "toUnitId": null,
                    "createTime":null,
                    "msg": null
                };

                var uuid = this.getWidgetParams().uuid;
                if (uuid) {
                    getModuleById(uuid);
                }

                function getModuleById(uuid) {
                    JDS.call({
                        service : "exchangeDataConfigService.getDXExchangeDataLog",
                        async: false,
                        data: [uuid],
                        success: function (result) {
                            bean = result.data;
                            $("#dxExchangeLog_form").json2form(bean);
                        }
                    });
                }
            },
            refresh: function () {
                this.init()
            }
        });
        return AppDXExchangeLogDetailDevelopment;
    });

