define(["jquery", "commons", "constant", "server",
        "ListViewWidgetDevelopment", "appModal"],
    function ($, commons, constant, server, ListViewWidgetDevelopment, appModal) {
        var AppEmailContactBookListDevelopment = function () {
            ListViewWidgetDevelopment.apply(this, arguments);
        };

        commons.inherit(AppEmailContactBookListDevelopment, ListViewWidgetDevelopment, {

            beforeRender: function (options, configuration) {
                var _self = this;

            },

            deleteContactGroups: function () {
                var _self = this;
                var uuids = this.getSelectedRowIds();
                if (uuids.length==0) {
                    appModal.info('请选择要删除的分组');
                    return;
                }

                server.JDS.call({
                    service: 'wmMailContactBookGrpService.deleteByGroupUuids',
                    data: [uuids],
                    version: '',
                    success: function (result) {
                        _self.refresh();
                    },
                    error: function (jqXHR) {
                    },
                    async: false
                });

            },


            //获取选中的行数据uuid
            getSelectedRowIds: function () {
                var selections = this.getSelections()
                var uuids = [];
                if (selections != null && selections.length > 0) {
                    for (var i = 0; i < selections.length; i++) {
                        uuids.push(selections[i]['UUID']);
                    }
                }
                return uuids
            }

        });


        return AppEmailContactBookListDevelopment;
    });