define(["constant", "commons", "server", "appContext", "appModal", "formBuilder", "HtmlWidgetDevelopment"],
    function (constant, commons, server, appContext, appModal, formBuilder, HtmlWidgetDevelopment) {
        var StringUtils = commons.StringUtils;
        var StringBuilder = commons.StringBuilder;
        var JDS = server.JDS;

        // 平台应用_运维管理_应用节点信息查看二开
        var AppOpsServerDetailDevelopment = function () {
            HtmlWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppOpsServerDetailDevelopment, HtmlWidgetDevelopment, {
            // 准备创建回调
            prepare: function () { },
            // 创建后回调
            create: function () { },
            // 初始化回调
            init: function () {
                var _self = this;
                var _uuid;
                if (_self.widget.options.containerDefinition.params && _self.widget.options.containerDefinition.params.uuid) {
                    _uuid = _self.widget.options.containerDefinition.params.uuid;
                }
                var form_selector = "#server_details_form";
                var formBean = {
                    "uuid": null,
                    "creator": null,
                    "createTime": null,
                    "modifier": null,
                    "moduleId": null,
                    "modifyTime": null,
                    "name": null,
                    "ip": null,
                    "port": null,
                    "remark": null,
                    "machine": null
                };

                $(form_selector).json2form(formBean);

                var validator = $.common.validation.validate("#server_details_form", "serverRegisterCenterEntity");

                if (_uuid) {
                    _self.showInfo(_uuid);
                }
            },
            // 根据组织UUID获取信息
            showInfo: function (_uuid) {
                var _self = this;
                var form_selector = "#server_details_form";
            JDS.call({
                service: "serverRegisterCenterFacadeService.getServerRegisterCenterDto",
                data: [_uuid],
                success: function (result) {
                    var bean = result.data;
                    $(form_selector).json2form(bean, true);
                    $(form_selector).find('input').prop('readonly', true);
                    $(form_selector).find('textarea').prop('readonly', true);
                    validator.form();
                }
            });
        },
            refresh: function () {
                var _self = this;
                _self.init()
            }
        });

        return AppOpsServerDetailDevelopment;
    });

