define(["constant", "commons", "server", "appContext", "appModal", "wSelect2", "HtmlWidgetDevelopment"],
    function (constant, commons, server, appContext, appModal, wSelect2, HtmlWidgetDevelopment) {
        var JDS = server.JDS;

        var AppDingtalkSetWidgetDevelopment = function () {
            HtmlWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppDingtalkSetWidgetDevelopment, HtmlWidgetDevelopment, {
            init: function () {
                this.timer = null;
                var _self = this;
                var bean = {
                    "app_key": null,/* 微应用key */
                    "app_secret": null,/* 微应用secret */
                    "corp_id": null,/* 企业ID */
                    "corp_domain_uri": null,/* 企业回调域名 */
                    "agent_id": null,/* 企业代理ID */
                    "org.system_unit_name": null,/* 系统单位名称 */
                    "org.system_unit_id": null,/* 系统单位ID */
                    "org.org_version_id": null,/* 组织版本 */
                    "org.org_version_name": null,/* 组织版本名称 */
                    "sns_app_id": null,/* 授权信息appid */
                    "sns_app_secret": null,/* 授权信息appsecret */
                    "app_aes_key": null,
                    "event_call_back_token": null,
                    "org.self_business_unit_id": null,
                    "org.sync_last_message": {}
                };
                var rules = {
                    "app_key": "微应用key不能为空",/* 微应用key */
                    "app_secret": "微应用secret不能为空",/* 微应用secret */
                    "corp_id": "企业ID不能为空",/* 企业ID */
                    "corp_domain_uri": "企业回调域名不能为空",/* 企业回调域名 */
                    "agent_id": "企业代理ID不能为空",/* 企业代理ID */
                    "org.system_unit_id": "系统单位不能为空",/* 组织版本 */
                    "org.org_version_id": "组织版本不能为空",/* 组织版本 */
                    "org.org_version_name": "组织版本名称不能为空",/* 组织版本名称 */
                    "sns_app_id": "授权信息appid不能为空",/* 授权信息appid */
                    "sns_app_secret": "授权信息appsecret不能为空" /* 授权信息appsecret */
                }
                var userDetail = SpringSecurityUtils.getUserDetails();

//                $("#org_version_name").wSelect2({
//                    serviceName: "multiOrgVersionFacade",
//                    queryMethod: "loadSelectData",
//                    valueField: "org_version_id",
//                    labelField:"org_version_name",
//                    placeholder: "请选择",
//                    params: {
//                        unitId: userDetail['systemUnitId'],
//                        status: "1",
//                    },
//                    multiple: false,
//                    remoteSearch: false,
//                    width: "100%",
//                    height: 250
//                });
                $("#system_unit_id").wSelect2({
                    serviceName: "multiOrgSystemUnitService",
                    queryMethod: "queryUnitListForSelect2",
                    labelField: "system_unit_name",
                    valueField: "system_unit_id",
                    placeholder: "请选择",
                    multiple: false,
                    remoteSearch: false,
                    width: "100%",
                    height: 250
                }).on("change", function (e, type) {
                    if (!type) {
                        var orgVersionId = "", systemUnitId = $("#system_unit_id").val();
                        if ($.trim(systemUnitId).length > 0) {
                            JDS.call({
                                version: "",
                                async: false,
                                data: [systemUnitId],
                                service: "multiOrgVersionFacade.getDefaultVersionBySystemUnitId",
                                success: function (result) {
                                    if (result.data && result.data.id) {
                                        orgVersionId = result.data.id
                                    }
                                }
                            });
                        }
                    }

                    $("#org_version_id").wSelect2({
                        serviceName: "multiOrgVersionFacade",
                        queryMethod: "loadSelectData",
                        valueField: "org_version_id",
                        labelField: "org_version_name",
                        placeholder: "请选择",
                        multiple: false,
                        remoteSearch: false,
                        width: "100%",
                        height: 250,
                        params: {
                            unitId: $("#system_unit_id").val(),
                            status: "1"
                        }
                    })
                    if (!type) {
                        $("#org_version_id").val(orgVersionId).trigger("change");
                        if (!orgVersionId) {
                            setTimeout(function () {
                                $("#org_version_id").prev().find(".well-select-selected-value").hide();
                                $("#org_version_id").prev().find(".well-select-placeholder").show();
                            }, 300);
                        }
                    }
                });

                getDingtalkDetail();

                // 钉钉对接设置
                function getDingtalkDetail() {
                    $.ajax({
                        url: ctx + "/pt/dingtalk/getConfig",
                        type: "GET",
                        dataType: "json",
                        success: function (result) {
                            bean = result.data;
                            setInputVal(bean);
                            $("#system_unit_id").trigger("change", "init");
                            if ($("#system_unit_id").val() == "") {
                                $("#system_unit_id").val(userDetail['systemUnitId']).trigger("change")
                            }
                            if ($.trim(bean["org.org_version_id"]).length > 0) {
                                $("#org_version_id").val(bean["org.org_version_id"]).trigger("change");
                            }
                        },
                        error: function (jqXHR) {

                        }
                    });
                }

                function setInputVal(data) {
                    for (var i in data) {
                        if (i.indexOf("org") > -1) {
                            if (i == "org.org_version_id" && $.trim(data[i]).length) {
                                $("#org_version_id").val(data[i]);
                            } else if (i == "org.org_version_name") {
                                $("#org_version_name").val(data[i]);
                            } else if (i == "org.system_unit_id" && $.trim(data[i]).length) {
                                $("#system_unit_id").val(data[i]);
                            } else if (i == "org.system_unit_name") {
                                $("#system_unit_name").val(data[i]);
                            } else if (i == "org.sync_last_message" && data[i]) {
                                var lastMsg = JSON.parse(data[i]);
                                if (lastMsg["time"]) {
                                    var msg = lastMsg["code"] == 0 ? "（同步失败，点击查看详情）" : "（同步成功，点击查看详情）";
                                    $("#syncTime").attr("data-code", lastMsg["code"]).html("上次同步时间：" + lastMsg["time"]).show()
                                    $("#syncTimeTip").html(msg);
                                }
                            } else {
                                var newKey = i.substring(4);
                                $("#" + newKey).val(data[i])
                                if ($("." + newKey).size() > 0) {
                                    $("." + newKey).text(data[i])
                                }
                            }
                        } else {
                            $("#" + i).val(data[i])
                        }
                    }
                }

                function getInputVal(data) {
                    for (var i in data) {
                        if (i.indexOf("org") > -1) {
                            var newKey = i.substring(4);
                            bean[i] = $("#" + newKey).val();

                        } else {
                            bean[i] = $("#" + i).val()
                        }
                    }
                    return bean;
                }

                function registerDefaultCallBack(corp_domain_uri, first) {
                    if ($.trim(corp_domain_uri).length <= 0) {
                        return;
                    }

                    function registerCallBack(call_back_tag) {
                        $.ajax({
                            type: "POST",
                            url: ctx + "/pt/dingtalk/registerCallBack",
                            data: {"call_back_tag": call_back_tag},
                            dataType: "json",
                            success: function (result) {
                                var data = result.data;
                                if (data.errcode != 0 && data.errmsg) {
                                    appModal.success(data.errmsg);
                                } else if ($.trim(call_back_tag).length <= 0) {
                                    appModal.success("注册成功");
                                }
                            }
                        })
                    }

                    function deleteAndRegisterCallBack(call_back_tag) {
                        $.ajax({
                            type: "GET",
                            url: ctx + "/pt/dingtalk/deleteCallBack",
                            dataType: "json",
                            success: function (result) {
                                registerCallBack(call_back_tag);
                            }
                        })
                    }

                    $.ajax({
                        type: "GET",
                        url: ctx + "/pt/dingtalk/getCallBack",
                        dataType: "json",
                        success: function (result) {
                            var data = result.data;
                            if (result.success && data) {
                                if ($.trim(data.url).length <= 0) {
                                    if (first) {
                                        appModal.confirm({
                                            title: "提示",
                                            message: "将为您初始化业务事件注册，您可以删除不需要的业务事件，和注册其他业务事件",
                                            callback: function (ret) {
                                                ret && deleteAndRegisterCallBack("");
                                            }
                                        })
                                    } else if (data.errcode = 71007) { // 回调地址已不存在
                                        registerCallBack("");
                                    } else {
                                        deleteAndRegisterCallBack("");
                                    }
                                } else if (data.url.indexOf(corp_domain_uri) < 0 && data.call_back_tag) {
                                    deleteAndRegisterCallBack(JSON.stringify(data.call_back_tag));
                                }
                            }
                        }
                    })
                }

                //保存
                $("#dingtalk_btn_save").off().on("click", function () {

                    var first = $.trim(bean["last_save_time"]).length <= 0;
                    bean = getInputVal(bean)
                    bean["last_save_time"] = (new Date()).getTime() + "";
                    for (var i in rules) {
                        if (bean[i] == "" || bean[i] == null) {
                            appModal.error(rules[i]);
                            return false;
                        }
                    }

                    $.ajax({
                        url: ctx + "/pt/dingtalk/saveConfig",
                        type: "POST",
                        dataType: "json",
                        data: {
                            jsonParams: JSON.stringify(bean)
                        },
                        success: function (result) {
                            _self.refresh();
                            appModal.success("保存成功")
                            registerDefaultCallBack(bean.corp_domain_uri, first);
                        },
                        error: function (jqXHR) {

                        }
                    });
                });

                // 组织同步
                $("#dingtalk_sync").off().on("click", function () {
                    if (bean.app_key == null || bean.app_key == "") {
                        appModal.error("请先保存数据");
                        return false;
                    }
                    appModal.confirm({
                        title: "组织同步",
                        message: "<div>钉钉组织架构将同步至系统，是否确定？</div><div style='color:#999;'>(与钉钉对接初始化时使用)</div>",
                        callback: function () {
                            $.ajax({
                                url: ctx + "/pt/dingtalk/syncOrg",
                                type: "GET",
                                dataType: "json",
                                mask: true,
                                success: function (result) {
                                    clearInterval(_self.timer)
                                    setTimeout(function (t) {
                                        $(".modal.confirmDialog").modal("hide");
                                    }, 500);
                                    if (result.success) {

                                    } else if (result.msg) {
                                        appModal.alert({
                                            title: "组织同步",
                                            message: result.msg,
                                            callback: function () {
                                                _self.refresh();
                                            }
                                        })
                                    }
                                },
                                error: function (jqXHR) {

                                }
                            });
                            getSyncStatusInterval()
                        }
                    })
                });

                $("#syncTime").off().on("click", function () {
                    var code = $(this).attr("data-code");
                    var message = getMessage(code);
                    appModal.alert({
                        title: "组织同步",
                        message: message
                    })
                })

                function getMessage(code) {
                    var message = code == 0 ? "<div style='text-align: left;'>组织同步失败！</div><div style='color:#999;text-align: left;'>异常信息：<span style='color:#f00;'>代码错误或异常</span></div>" : "钉钉组织架构同步完成！";
                    return message;
                }

                getSyncStatus();

                // 组织同步状态查询
                function getSyncStatus() {
                    $.ajax({
                        url: ctx + "/pt/dingtalk/syncOrgStatue",
                        type: "GET",
                        async: false,
                        dataType: "json",
                        success: function (result) {
                            var data = (typeof result.data === "string" ? JSON.parse(result.data) : result.data);
                            if (data.code == 2) {                  // 正在同步
                                getSyncStatusInterval();
                            } else {                                    // 同步完成
                                clearInterval(_self.timer)
                            }
                        },
                        error: function (jqXHR) {
                        }
                    });
                }

                function getSyncStatusInterval() {
                    appModal.alert({
                        className: "confirmDialog",
                        title: "组织同步",
                        message: "钉钉组织架构同步中...",
                        closeButton: false,
                        showConfirmButton: false
                    })

                    _self.timer = setInterval(function () {
                        $.ajax({
                            url: ctx + "/pt/dingtalk/syncOrgStatue",
                            type: "GET",
                            async: false,
                            dataType: "json",
                            success: function (result) {
                                var data = (typeof result.data === "string" ? JSON.parse(result.data) : result.data);
                                if (data.code == 2) {            // 正在同步

                                } else if (data.code == 1 || data.code == 0) {           // 同步完成
                                    clearInterval(_self.timer)
                                    $(".modal.confirmDialog").modal("hide");
                                    var message = getMessage(data.code);
                                    appModal.alert({
                                        title: "组织同步",
                                        message: message,
                                        callback: function () {
                                            _self.refresh();

                                        }
                                    })
                                }
                            },
                            error: function (jqXHR) {

                            }
                        });
                    }, 2000)
                }
            },
            refresh: function () {
                this.init()
            }
        })
        return AppDingtalkSetWidgetDevelopment;
    })

