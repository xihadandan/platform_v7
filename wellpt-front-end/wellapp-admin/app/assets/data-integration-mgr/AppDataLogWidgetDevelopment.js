define([ "constant", "commons", "server", "appContext","appModal", "HtmlWidgetDevelopment" ],
    function(constant, commons, server,appContext,appModal, HtmlWidgetDevelopment) {
        var JDS = server.JDS;

        var AppDataLogWidgetDevelopment = function() {
            HtmlWidgetDevelopment.apply(this, arguments);
        };
        commons.inherit(AppDataLogWidgetDevelopment, HtmlWidgetDevelopment, {
            init: function() {
                var bean = {
                    "uuid": null,
                    "batchId": null,
                    "dataId": null,
                    "recVer": null,
                    "code": null,
                    "node": null,
                    "toUnitId": null,
                    "fromUnitId" : null,
                    "status": null,
                    "msg": null,
                };
                $("#data_log_module_form").json2form(bean);
                getModuleById()
                function getModuleById() {
                    var uuid = this.getWidgetParams().uuid
                    JDS.call({
                        service : "exchangeDataConfigService.getExchangeDataLog",
                        data : [ uuid ],
                        version:"",
                        success : function(result) {
                            bean = result.data;
                            if(bean.status == '1'){
                                bean.status = '成功';
                            } else {
                                bean.status = '失败';
                            }

                            $("#data_log_module_form").json2form(bean);
                        }
                    });
                }
            },
            refresh:function(){
                this.init();
            }
        })
        return AppDataLogWidgetDevelopment;
    })

