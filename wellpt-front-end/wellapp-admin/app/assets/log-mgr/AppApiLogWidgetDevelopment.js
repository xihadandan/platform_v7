define([ "constant", "commons", "server", "appContext","appModal", "HtmlWidgetDevelopment" ],
    function(constant, commons, server,appContext,appModal,HtmlWidgetDevelopment) {
        var JDS = server.JDS;

        var AppApiLogWidgetDevelopment = function() {
            HtmlWidgetDevelopment.apply(this, arguments);
        };
        commons.inherit(AppApiLogWidgetDevelopment, HtmlWidgetDevelopment, {
            init: function() {
                var bean = {
                    uuid : null,
                    recVer : null,
                    apiServiceName : null,
                    clientBrowser : null,
                    clientIp : null,
                    requestJson : null,
                    responseBody : null,
                    logTime : null,
                    tenantId : null,
                    userId : null,
                    username : null
                };
                $("#api_access_log_form").json2form(bean);
                var uuid = this.getWidgetParams().uuid
                getApiAccessLog(uuid)
                function getApiAccessLog(uuid) {
                    JDS.call({
                        service : "apiAccessLogMgr.getBean",
                        data : uuid,
                        version:"",
                        success : function(result) {
                            bean = result.data;
                            $("#api_access_log_form").json2form(bean);
                        }
                    });
                }
            },
            refresh:function(){
                this.init()
            }
        })
        return AppApiLogWidgetDevelopment;
    })

