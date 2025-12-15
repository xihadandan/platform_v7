define(["constant", "commons", "server", "appContext", "appModal", "HtmlWidgetDevelopment"],
    function(constant, commons, server, appContext, appModal, HtmlWidgetDevelopment) {
        var JDS = server.JDS;

        var AppMessageViewDevelopment = function() {
            HtmlWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppMessageViewDevelopment, HtmlWidgetDevelopment, {
            init: function() {
                var bean = {
                    "uuid": null,
                    "imIp": null,
                    "loginName": null,
                    "loginPassword": null,
                    "apiCode": null,
                    "dbName": null,
                    "isOpen": null,
                    "sendLimit": null,
                    "apiAsync": null,
                    // ADD
                    "sWebService": null,
                    "sLoginName": null,
                    "sLoginPassword": null,
                    "sIsOpen": null,
                    "sSendLimit": null,
                    "clientBean": null,
                    "clientBeanName": null,
                    // CLOUD
                    "cloudMasHttp": null,
                    "ecName": null,
                    "apId": null,
                    "secretKey": null,
                    "cloudSign": null,
                    "addSerial": null,
                    "cloudSendLimit": null,
                    "cloudIsOpen": null
                };
                getMasConfig()
                    // 获取MAS设置信息
                function getMasConfig() {
                    JDS.call({
                        service: "shortMessageService.getBean",
                        version: "",
                        success: function(result) {
                            bean = result.data;
                            var activeIdx = bean.isOpen ? 0 : !bean.sIsOpen ? 0 : 1;
                            $(".tabs").tabs({
                                active: activeIdx
                            });
                            $("#mas_form").json2form(bean);
                        }
                    });
                }

                //保存mas设置
                $("#message_btn_save").click(function() {

                    if ($("#sendLimit").val() != "" && !($("#sendLimit").val().match("^[0-9]*[1-9][0-9]*$"))) {
                        alert("重发时限请输入合法的数字");
                        return false;
                    }

                    // 清空JSON
                    $.common.json.clearJson(bean);
                    // 收集表单数据
                    $("#mas_form").form2json(bean);

                    JDS.call({
                        service: "shortMessageService.saveMas",
                        data: [bean],
                        validate: true,
                        version: "",
                        success: function(result) {
                            if (result.data == 'success') {
                                appModal.confirm('保存设置成功，是否立即应用生效？', function(res) {
                                    if (res) {
                                        applySetting();
                                    } else {
                                        appModal.success("保存成功");
                                    }
                                })
                            } else {
                                appModal.error("保存失败");
                            }
                        }
                    });
                });

                function applySetting() {
                    if ($("#sendLimit").val() != '' && !($("#sendLimit").val().match("^[0-9]*[1-9][0-9]*$"))) {
                        appModal.error("重发时限请输入合法的数字");
                        return false;
                    }

                    // 清空JSON
                    $.common.json.clearJson(bean);
                    // 收集表单数据
                    $("#mas_form").form2json(bean);

                    JDS.call({
                        service: "shortMessageService.applyConfig",
                        data: [bean],
                        validate: true,
                        version: "",
                        success: function(result) {
                            if (result.data == true || result.data == 'true') {
                                appModal.success("应用成功！");
                            } else {
                                appModal.error("应用失败，保存不成功");
                            }
                        }
                    });
                }

                // MAS 与  S-MAS 与  云MAS只能选择一个
                $("#sIsOpen").click(function() {
                    if (this.checked) {
                        $("#isOpen").attr("checked", !this.checked);
                        $("#cloudIsOpen").attr("checked", !this.checked);
                    }
                });
                $("#isOpen").click(function() {
                    if (this.checked) {
                        $("#sIsOpen").attr("checked", !this.checked);
                        $("#cloudIsOpen").attr("checked", !this.checked);
                    }
                });
                $("#cloudIsOpen").click(function() {
                    if (this.checked) {
                        $("#isOpen").attr("checked", !this.checked);
                        $("#sIsOpen").attr("checked", !this.checked);
                    }
                });
            }
        })
        return AppMessageViewDevelopment;
    })