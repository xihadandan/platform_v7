define([ "constant", "commons", "server","appModal", "ListViewWidgetDevelopment" ],
    function(constant, commons, server,appModal, ListViewWidgetDevelopment) {
        var AppShortMsgLogListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        commons.inherit(AppShortMsgLogListDevelopment, ListViewWidgetDevelopment, {
            btn_check:function(e) {
                var index = $(e.target).parents("tr").data("index")
                var data = this.getData()
                var message = data[index].body || ""
                appModal.dialog({
                    message:message,
                    size:"middle",
                    title:"查看发送内容"
                })
            }
        });
        return AppShortMsgLogListDevelopment;
    });

