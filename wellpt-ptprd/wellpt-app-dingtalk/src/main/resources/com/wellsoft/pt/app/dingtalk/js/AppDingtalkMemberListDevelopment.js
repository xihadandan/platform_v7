define(["constant", "commons", "server", "ListViewWidgetDevelopment"],
    function (constant, commons, server, ListViewWidgetDevelopment) {

        var AppDingtalkMemberListDevelopment = function () {
            ListViewWidgetDevelopment.apply(this, arguments);
        };

        commons.inherit(AppDingtalkMemberListDevelopment, ListViewWidgetDevelopment, {
            lineEnderButtonHtmlFormat: function (format, row, index) {             // 隐藏确认按钮
                if (row["待确认"] == 1) {
                    format.after = "<div class='div_lineEnd_toolbar' style='display:inline-block;margin-left:10px;margin-right:10px;padding-bottom:1px;overflow: visible' index='0'><button type='button' class='btn btn-primary btn-bg-color btn_class_btn_sure' title='确认'>确认</button></div>"
                } else {
                    format.after = "<div></div>";
                }
            },

            btn_sure: function (e) {
                var index = $(e.target).parents("tr").data("index");
                var uuid = this.getData()[index].UUID;
                this.sureMember([uuid])
            },
            btn_batchSure: function () {
                var data = this.getSelections();
                var uuid = [];

                if (data.length <= 0) {
                    appModal.error("请先选择要确认的记录");
                    return false;
                }
                for (var i = 0; i < data.length; i++) {
                    uuid.push(data[i].UUID);
                }
                this.sureMember(uuid)
            },
            sureMember: function (uuid) {
                var _self = this;
                JDS.call({
                    service: "multiOrgDingUserService.confirmUsers",
                    data: [uuid],
                    version: "",
                    mask: false,
                    success: function (result) {
                        if (result.success) {
                            appModal.success("已确认")
                            _self.refresh()
                        }
                    },
                    error: function (err) {
                        appModal.error(err.responseJSON.msg)
                    }
                })
            }
        });
        return AppDingtalkMemberListDevelopment;
    });

