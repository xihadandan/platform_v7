define(["constant", "commons", "server", "ListViewWidgetDevelopment"],
    function (constant, commons, server, ListViewWidgetDevelopment) {

        var AppDingtalkPushListDevelopment = function () {
            ListViewWidgetDevelopment.apply(this, arguments);
        };

        commons.inherit(AppDingtalkPushListDevelopment, ListViewWidgetDevelopment, {
            afterRender: function () {
                $(this.widget.element).prepend("<div style='line-height:1;margin: 10px;color:#666;'><i class='iconfont icon-ptkj-xinxiwenxintishi' style='margin-right: 5px;font-size:20px;vertical-align: middle;'></i>1个用户的1个待办消息为1条记录，根据动作流转，更新记录的数据，待办处理完成且推送钉钉成功时，将从列表记录中删除</div>")
            }
        });
        return AppDingtalkPushListDevelopment;
    });

