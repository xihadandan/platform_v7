define(["constant", "commons", "server", "appContext", "appModal", "wSelect2", "multiOrg", "AppPtMgrDetailsWidgetDevelopment", "AppPtMgrCommons", "formBuilder"],
    function(constant, commons, server, appContext, appModal, wSelect2, multiOrg, AppPtMgrDetailsWidgetDevelopment, AppPtMgrCommons, formBuilder) {
        var JDS = server.JDS;
        var StringUtils = commons.StringUtils;

        var AppDmsDataRangeWidgetDevelopment = function() {
            AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppDmsDataRangeWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
            init: function() {
                var form_selector = "#dms_data_permission_range_form";
                var range_rule_form_selector = "#dms_data_permission_range_rule_form";
                var dms_data_permission_range_list_selector = "#dms_data_permission_range_list";
                var dms_data_permission_range_rule_list_selector = "#dms_data_permission_range_rule_list";
                var self = this;
                var bean = {};
                var rangeBean = {
                    id: null, // id
                    name: null, // name
                    type: null, // type
                    owner: null, // owner
                    ownerName: null, // ownerName
                    includeSuperiorOrg: null, // includeSuperiorOrg
                    includeSiblingOrg: null, // includeSiblingOrg
                    includeSubordinateOrg: null, // includeSubordinateOrg
                    ownerFieldName: null, // ownerFieldName
                    ownerFieldDisplayName: null, // ownerFieldDisplayName
                    rules: [] // 数据规则
                };
                var rangeRuleBean = {
                    ruleId: null, // 规则ID
                    ruleName: null, // 规则名称
                    fieldName: null, // 字段名
                    fieldDisplayName: null, // 字段显示名
                    operator: null, // 操作符
                    operatorName: null, // 操作符名称
                    fieldValue: null, // 字段值
                    ruleConnector: null // 连接符
                };

                jQuery.validator.addMethod("ownerRequired", function(value, element) {
                    var type = $("#type").val();
                    var eleName = $(element).attr("name");
                    $(".current-user-info").find(".error").text("");
                    if (type == "1") {
                        if (eleName == "ownerName") {
                            return true;
                        }
                    } else {
                        if (eleName == "currentUserInfo") {
                            return true;
                        }
                    }
                    if (StringUtils.isNotBlank(value)) {
                        return true;
                    }
                    if (type == "1") {
                        setTimeout(function() {
                            $(".current-user-info").find(".error").text("不能为空！").show();
                        }, 100);
                    }
                    return false;
                }, "不能为空！");
                jQuery.validator.addMethod("nameUnique", function(value, element) {
                    var ranges = getRangesByName(value);
                    if (ranges.length > 1) {
                        return false;
                    } else if (ranges.length == 1) {
                        var rangeId = $("#id").val();
                        if (StringUtils.isNotBlank(rangeId) && ranges[0].id == rangeId) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    return true;
                }, "名称不能重复！");
                jQuery.validator.addMethod("ruleNameUnique", function(value, element) {
                    var rules = getRangeRulesByName(value);
                    if (rules.length > 1) {
                        return false;
                    } else if (rules.length == 1) {
                        var ruleId = $("#ruleId").val();
                        if (StringUtils.isNotBlank(ruleId) && rules[0].id == ruleId) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    return true;
                }, "名称不能重复！");

                var validator = $(form_selector).validate({
                    rules: {
                        name: {
                            required: true,
                            nameUnique: true
                        },
                        currentUserInfo: {
                            ownerRequired: true
                        },
                        ownerName: {
                            ownerRequired: true
                        }
                    },
                    messages: {
                        name: {
                            required: "不能为空！",
                            nameUnique: "名称不能重复！"
                        },
                        currentUserInfo: {
                            ownerRequired: ""
                        },
                        ownerName: {
                            ownerRequired: "不能为空！"
                        }
                    }
                });

                (function initDataOwnerProviderElements() {
                    JDS.call({
                        service: "dmsDataPermissionDefinitionFacadeService.getCurrentUserDataOwnerProviders",
                        async: false,
                        version: "",
                        success: function(result) {
                            var dataList = result.data;
                            var eleHtml = '';
                            for (var i = 0; i < dataList.length; i++) {
                                var data = dataList[i];
                                // 新行开始
                                if (i % 2 == 0) {
                                    eleHtml += '<div class="table-row">';
                                }
                                eleHtml += '<div class="table-cell-50">';
                                eleHtml += '<input id="currentUserInfo_' + i + '" name="currentUserInfo" value="' + data.value +
                                    '" type="checkbox" />';
                                eleHtml += '<label for="currentUserInfo_' + i + '">' + data.label + '</label>';
                                eleHtml += '</div>';
                                // 新行结束
                                if ((i + 1) % 2 == 0) {
                                    eleHtml += '</div>';
                                }
                            }
                            // 验证信息占位符
                            eleHtml += '<div class="table-row current-user-info">';
                            eleHtml += '<label for="name" class="error"></label>';
                            eleHtml += '</div>';
                            $(".current-user-info-table").html(eleHtml);
                        }
                    });
                })();

                var uuid = this.getWidgetParams().uuid
                getDmsDataPermissionDefinition(uuid)

                $(dms_data_permission_range_rule_list_selector).on("click", 'td[role="gridcell"]', function(e) {
                    // 事件处理
                    var rowid = $(this).parent().attr("id");
                    if ($(this).find("input").length > 0) {
                        return;
                    }
                    var rowData = getRangeRuleDefinitionById(rowid);
                    showRangeRuleDialog(rangeRuleConvert2RangeRuleBean(rowData));
                });

                $("#btn_range_add").on("click", function() {
                    if (StringUtils.isBlank(bean.uuid)) {
                        appModal.error("请先选择对象！");
                        return;
                    }
                    $.common.json.clearJson(rangeBean, true);
                    $(form_selector).clearForm(true);
                    $("#type").trigger("change");
                    $("#ownerFieldName").trigger("change");
                });

                $("#btn_range_del").on("click", function() {
                    var rowids = $(dms_data_permission_range_list_selector).bootstrapTable("getSelections");
                    if (rowids.length == 0) {
                        appModal.error("请选择记录！");
                        return true;
                    }
                    appModal.confirm("确定要删除所选记录吗?", function(res) {
                        if (res) {
                            var fieldsVal = [];
                            for (var i = 0; i < rowids.length; i++) {
                                fieldsVal.push(rowids[i].id)
                            }
                            $(dms_data_permission_range_list_selector).bootstrapTable("remove", {
                                field: "id",
                                values: fieldsVal
                            });
                            // 更新数据范围定义值
                            bean.rangeDefinition = JSON.stringify($(dms_data_permission_range_list_selector).bootstrapTable("getData"));
                            JDS.call({
                                service: "dmsDataPermissionDefinitionFacadeService.saveDto",
                                data: bean,
                                version: "",
                                success: function(result) {
                                    // 刷新数据范围列表数据
                                    appModal.success("删除成功!");
                                    getDmsDataPermissionDefinition(bean.uuid);
                                }
                            });
                        }
                    })
                });

                $("#range_btn_save").on("click", function() {
                    if (!validator.form()) {
                        return false;
                    }
                    var rangeDefinition = JSON.parse(bean.rangeDefinition || "[]");
                    rangeBean.currentUserInfo = null;
                    rangeBean.ownerFieldDisplayName = null;
                    $(form_selector).form2json(rangeBean);
                    if (rangeBean.type == 1) {
                        rangeBean.owner = rangeBean.currentUserInfo.join(";");
                    }
                    delete rangeBean.currentUserInfo;
                    var newData = $.extend(true, {}, rangeBean);
                    // 添加行
                    if (StringUtils.isBlank(newData.id)) {
                        newData.id = (new Date()).getTime();
                        $("#id").val(newData.id);
                        rangeDefinition.push(newData);
                    } else {
                        // 更新行
                        $.each(rangeDefinition, function(i, range) {
                            if (range.id == newData.id) {
                                $.extend(range, newData);
                            }
                        });
                    }
                    bean.rangeDefinition = JSON.stringify(rangeDefinition);
                    JDS.call({
                        service: "dmsDataPermissionDefinitionFacadeService.saveDto",
                        data: bean,
                        version: "",
                        success: function(result) {
                            toDataPermissionRangeList(rangeDefinition);
                            appModal.success("保存成功！");
                            appContext.getNavTabWidget().closeTab();;
                        }
                    });
                });

                $("#type").wSelect2({
                    data: [
                        { id: "1", text: "当前用户组织信息" },
                        { id: "2", text: "指定组织结点" }
                    ],
                    valueField: 'type',
                    remoteSearch: false
                }).on("change", function() {
                    var rangeType = $(this).val();
                    $(".range-type").hide();
                    $(".range-type-" + rangeType).show();
                }).trigger("change");

                $("#ownerName").on("click", function(e) {
                    $.unit2.open({
                        valueField: "owner",
                        labelField: "ownerName",
                        title: "数据所有者",
                        type: "all",
                        unitId: SpringSecurityUtils.getCurrentUserUnitId(),
                        valueFormat: "justId"
                    });
                });

                $("#btn_range_rule_add").on("click", function() {
                    if (StringUtils.isBlank(rangeBean.id)) {
                        appModal.error("请先选择数据范围！");
                        return;
                    }
                    $(range_rule_form_selector).clearForm(true);
                    showRangeRuleDialog();
                });

                $("#btn_range_rule_del").on("click", function() {
                    var rowids = $(dms_data_permission_range_rule_list_selector).bootstrapTable("getSelections");
                    if (rowids.length == 0) {
                        appModal.error("请选择记录！");
                        return true;
                    }
                    appModal.confirm("确定要临时删除(保存后生效)所选记录吗？", function(result) {
                        if (result) {
                            var fieldsVal = [];
                            for (var i = 0; i < rowids.length; i++) {
                                fieldsVal.push(rowids[i].id)
                            }
                            $(dms_data_permission_range_rule_list_selector).bootstrapTable("remove", {
                                field: "id",
                                values: fieldsVal
                            });

                            rangeBean.rules = $(dms_data_permission_range_rule_list_selector).bootstrapTable("getData");
                        }
                    })
                });

                $("#btn_range_rule_up").off("click").on("click", function() {
                    var allData = $(dms_data_permission_range_rule_list_selector).bootstrapTable("getData");
                    var selections = $(dms_data_permission_range_rule_list_selector).bootstrapTable("getSelections");
                    if (selections.length == 0) {
                        appModal.error("请选择记录！");
                        return;
                    }

                    for (var i = 0; i < selections.length; i++) {
                        var row = selections[i];
                        $.each(allData, function(j, row2) {
                            if (row.id === row2.id && j > 0) {
                                allData[j] = allData[j - 1];
                                allData[j - 1] = row;
                                rangeBean.rules = allData;
                                return $(dms_data_permission_range_rule_list_selector).bootstrapTable("load", allData);
                            }
                        });
                    }

                });

                $("#btn_range_rule_down").off("click").on("click", function() {
                    var allData = $(dms_data_permission_range_rule_list_selector).bootstrapTable("getData");
                    var size = allData.length;
                    var selections = $(dms_data_permission_range_rule_list_selector).bootstrapTable('getSelections');
                    if (selections.length == 0) {
                        appModal.error("请选择记录！");
                        return;
                    }

                    for (var i = selections.length - 1; i >= 0; i--) {
                        var row = selections[i];
                        for (var j = 0; j < allData.length; j++) {
                            var row2 = allData[j];
                            if (row.id === row2.id && j < size - 1) {
                                allData[j] = allData[j + 1];
                                allData[j + 1] = row;
                                rangeBean.rules = allData;
                                return $(dms_data_permission_range_rule_list_selector).bootstrapTable("load", allData);
                            }
                        }
                    }
                });

                function getRangesByName(name) {
                    var ranges = JSON.parse(bean.rangeDefinition || "[]");
                    var retRanges = [];
                    $.each(ranges, function(i, range) {
                        if (range.name == name) {
                            retRanges.push(range);
                        }
                    });
                    return retRanges;
                }

                function getRangeRulesByName(name) {
                    var rules = rangeBean.rules || [];
                    var retRules = [];
                    $.each(rules, function(i, rule) {
                        if (rule.name == name) {
                            retRules.push(rule);
                        }
                    });
                    return retRules;
                }

                function getDmsDataPermissionDefinition(uuid) {
                    JDS.call({
                        service: "dmsDataPermissionDefinitionFacadeService.getDto",
                        data: uuid,
                        version: "",
                        success: function(result) {
                            bean = result.data;
                            onGetDmsDataPermissionDefinition(bean);
                        }
                    });
                }

                function onGetDmsDataPermissionDefinition(bean) {
                    var rangeDefinition = bean.rangeDefinition || "[]";
                    showTable($(dms_data_permission_range_list_selector), JSON.parse(rangeDefinition));
                    showTable($(dms_data_permission_range_rule_list_selector), []);
                    $(form_selector).clearForm(true);
                    $(dms_data_permission_range_rule_list_selector).bootstrapTable("removeAll");
                    $("#ownerFieldName").off("change");
                    $("#ownerFieldName").wSelect2({
                        serviceName: "dmsDataPermissionDefinitionFacadeService",
                        queryMethod: "loadFieldNamesSelectData",
                        valueField: "ownerFieldName",
                        labelField: "ownerFieldDisplayName",
                        container: $(form_selector),
                        remoteSearch: false,
                        params: {
                            type: bean.type,
                            dataName: bean.dataName
                        }
                    });
                    $("#ownerFieldName").trigger("change");
                }

                function getDmsDataPermissionRangeDefinition(id) {
                    rangeBean = getRangeDefinitionById(id);
                    if (rangeBean.type == 1) {
                        rangeBean.currentUserInfo = rangeBean.owner.split(";");
                    }
                    $(form_selector).json2form(rangeBean);
                    $("#ownerFieldName").val(rangeBean.ownerFieldName);
                    // 刷新数据范围规则列表数据
                    toDataPermissionRangeRuleList(rangeBean.rules || []);
                    $("#type").trigger("change");
                    $("#ownerFieldName").trigger("change");
                }

                function getRangeDefinitionById(id) {
                    var rangeDefinition = JSON.parse(bean.rangeDefinition || "[]");
                    for (var i = 0; i < rangeDefinition.length; i++) {
                        if (rangeDefinition[i].id == id) {
                            return rangeDefinition[i];
                        }
                    }
                    return null;
                }

                function getRangeRuleDefinitionById(id) {
                    var rules = rangeBean.rules || [];
                    for (var i = 0; i < rules.length; i++) {
                        if (rules[i].id == id) {
                            return rules[i];
                        }
                    }
                    return null;
                }

                function toDataPermissionRangeList(rangeDefinition) {
                    var $rangeList = $(dms_data_permission_range_list_selector);
                    // 清空数据范围列表
                    $rangeList.bootstrapTable("removeAll");
                    for (var index = 0; index < rangeDefinition.length; index++) {
                        $rangeList.bootstrapTable("insertRow", { index: 0, row: rangeDefinition[index] });
                    }
                }

                function toDataPermissionRangeRuleList(rules) {
                    var $rangeRuleList = $(dms_data_permission_range_rule_list_selector);
                    // 清空数据范围规则列表
                    $rangeRuleList.bootstrapTable("removeAll");
                    for (var index = 0; index < rules.length; index++) {
                        rules[index].content = rules[index].fieldDisplayName + " " + rules[index].operatorName + " " + rules[index].fieldValue;
                        $rangeRuleList.bootstrapTable("insertRow", { index: rules[index].id, row: rules[index] });
                    }
                }

                function showRangeRuleDialog(rowData) {
                    var message = initDialogDom()
                    appModal.dialog({
                        title: "数据规则",
                        modal: true,
                        message: message,
                        size: "large",
                        shown: function() {
                            if (rowData) {
                                $(range_rule_form_selector).json2form(rowData);
                            }
                            $("#operatorName", range_rule_form_selector).wSelect2({
                                data: [
                                    { id: "<", text: "小于" },
                                    { id: "<=", text: "小于等于" },
                                    { id: ">", text: "大于" },
                                    { id: ">=", text: "大于等于" },
                                    { id: "=", text: "等于" },
                                    { id: "<>", text: "不等于" },
                                    { id: "like", text: "包含" },
                                    { id: "not like", text: "不包含" }
                                ],
                                labelField: "operatorName",
                                valueField: "operator",
                                remoteSearch: false
                            }).trigger("change");

                            $("#ruleConnector", range_rule_form_selector).wSelect2({
                                data: [
                                    { id: "and", text: "并且" },
                                    { id: "or", text: "或者" }
                                ],
                                valueField: "ruleConnector",
                                remoteSearch: false
                            }).trigger("change");

                            $("#fieldName", range_rule_form_selector).wSelect2({
                                serviceName: "dmsDataPermissionDefinitionFacadeService",
                                queryMethod: "loadFieldNamesSelectData",
                                valueField: "fieldName",
                                labelField: "fieldDisplayName",
                                remoteSearch: false,
                                params: {
                                    type: bean.type,
                                    dataName: bean.dataName
                                }
                            }).trigger("change");
                        },
                        buttons: {
                            confirm: {
                                label: "确定",
                                className: "btn-primary",
                                callback: function(e) {
                                    var rangeRuleValidator = $(range_rule_form_selector).validate({
                                        rules: {
                                            ruleName: {
                                                required: true,
                                                ruleNameUnique: true
                                            },
                                            fieldName: {
                                                required: true
                                            },
                                            operator: {
                                                required: true
                                            },
                                            fieldValue: {
                                                required: true
                                            }
                                        },
                                        messages: {
                                            ruleName: {
                                                required: "不能为空!"
                                            },
                                            fieldName: {
                                                required: "不能为空!"
                                            },
                                            operator: {
                                                required: "不能为空!"
                                            },
                                            fieldValue: {
                                                required: "不能为空!"
                                            }
                                        }
                                    });
                                    if (!rangeRuleValidator.form()) {
                                        return false;
                                    }
                                    $(range_rule_form_selector).form2json(rangeRuleBean);
                                    // rangeRuleBean.operatorName = $("#operator").find("option:selected").text();
                                    // 添加行
                                    var isAdded = false;
                                    if (StringUtils.isBlank(rangeRuleBean.ruleId)) {
                                        rangeRuleBean.ruleId = (new Date()).getTime();
                                        $("#ruleId").val(rangeRuleBean.ruleId);
                                        isAdded = true;
                                    }
                                    var rangeRule = rangeRuleBeanConvert2RangeRule(rangeRuleBean);
                                    var rules = rangeBean.rules || [];
                                    if (isAdded) {
                                        rules.push(rangeRule);
                                    } else {
                                        // 更新
                                        $.each(rules, function(i, rule) {
                                            if (rule.id == rangeRule.id) {
                                                $.extend(rule, rangeRule);
                                            }
                                        });
                                    }
                                    // 重新加载规则列表
                                    toDataPermissionRangeRuleList(rules);
                                    rangeBean.rules = rules;
                                }
                            },
                            cancel: {
                                label: "取消",
                                className: "btn-default"
                            }
                        }
                    });
                }

                function rangeRuleBeanConvert2RangeRule(rangeRuleBean) {
                    return {
                        id: rangeRuleBean.ruleId,
                        name: rangeRuleBean.ruleName,
                        fieldName: rangeRuleBean.fieldName,
                        fieldDisplayName: rangeRuleBean.fieldDisplayName,
                        operator: rangeRuleBean.operator,
                        operatorName: rangeRuleBean.operatorName,
                        fieldValue: rangeRuleBean.fieldValue,
                        connector: rangeRuleBean.ruleConnector
                    };
                }

                function rangeRuleConvert2RangeRuleBean(rangeRule) {
                    return {
                        ruleId: rangeRule.id,
                        ruleName: rangeRule.name,
                        fieldName: rangeRule.fieldName,
                        fieldDisplayName: rangeRule.fieldDisplayName,
                        operator: rangeRule.operator,
                        operatorName: rangeRule.operatorName,
                        fieldValue: rangeRule.fieldValue,
                        ruleConnector: rangeRule.connector
                    };
                }

                function showTable(ele, data) {
                    var id = ele.attr("id");
                    if (id == "dms_data_permission_range_list") {
                        var columns = [{
                            checkbox: true
                        }, {
                            field: "id",
                            title: "id",
                            visible: false
                        }, {
                            field: "name",
                            title: "数据范围名称"
                        }, {
                            field: "ownerFieldName",
                            title: "数据所有者字段",
                            visible: false
                        }, {
                            field: "ownerFieldDisplayName",
                            title: "数据所有者字段"
                        }]
                    } else {
                        var columns = [{
                            field: "checked",
                            checkbox: true
                        }, {
                            field: "id",
                            title: "id",
                            visible: false
                        }, {
                            field: "name",
                            title: "规则名称"
                        }, {
                            field: "content",
                            title: "内容"
                        }, {
                            field: "connector",
                            title: "组合关系",
                            formatter: function(value, row, index) {
                                if (value == "and") {
                                    return "并且";
                                } else if (value == "or") {
                                    return "或者";
                                }
                                if (value == null) {
                                    return "";
                                }
                                return value;
                            }
                        }]
                    }

                    initTable(ele, data, columns)
                }

                function initTable(ele, data, columns) {
                    var id = ele.attr("id");
                    var clickToSelect = id == "dms_data_permission_range_rule_list" ? false : true;

                    $("#" + id).bootstrapTable({
                        data: data,
                        idField: "id",
                        striped: false,
                        showColumns: false,
                        columns: columns,
                        clickToSelect: clickToSelect,
                        onCheck: function(row, $element, field) {
                            if (id == "dms_data_permission_range_list") {
                                getDmsDataPermissionRangeDefinition(row.id);
                            }
                        },
                        onDblClickRow: function(row, $element, field) {
                            if (id == "dms_data_permission_range_rule_list") {
                                var rowData = getRangeRuleDefinitionById(row.id);
                                showRangeRuleDialog(rangeRuleConvert2RangeRuleBean(rowData));
                            }
                        }
                    })
                }

                function initDialogDom() {
                    var html = ""
                    html += '<div id="dlg_range_rule">' +
                        '<form action="" id="dms_data_permission_range_rule_form" class="dlg-range-rule form-widget">' +
                        '<input type="hidden" id="ruleId" name="ruleId" />' +
                        '<div class="well-form form-horizontal">' +
                        '<div class="form-group">' +
                        '<label for="ruleName" class="well-form-label control-label required">规则名称</label>' +
                        '<div class="well-form-control">' +
                        '<input type="text" class="form-control" id="ruleName" name="ruleName">' +
                        '</div>' +
                        '</div>' +
                        '<div class="form-group rule-type rule-type-2">' +
                        '<label for="fieldName" class="well-form-label control-label required">字段名</label>' +
                        '<div class="well-form-control">' +
                        '<input type="text" class="form-control" id="fieldName" name="fieldName">' +
                        '<input type="hidden" id="fieldDisplayName" name="fieldDisplayName">' +
                        '</div>' +
                        '</div>' +
                        '<div class="form-group rule-type rule-type-2">' +
                        '<label for="operator" class="well-form-label control-label required">比较符</label>' +
                        '<div class="well-form-control">' +
                        '<input type="text" id="operatorName" name="operatorName"/>' +
                        '<input type="hidden" id="operator" name="operator"/>' +
                        '</div>' +
                        '</div>' +
                        '<div class="form-group rule-type rule-type-2">' +
                        '<label for="fieldValue" class="well-form-label control-label required">比较值</label>' +
                        '<div class="well-form-control">' +
                        '<input type="text" class="form-control" id="fieldValue" name="fieldValue">' +
                        '</div>' +
                        '</div>' +
                        '<div class="form-group range-type range-type-1 range-type-2">' +
                        '<label for="ruleConnector" class="well-form-label control-label">连接符</label>' +
                        '<div class="well-form-control">' +
                        '<input name="ruleConnector" id="ruleConnector" type="text"/>' +
                        '</div>' +
                        '</div>' +
                        '</div>' +
                        '</form>' +
                        '</div>'
                    return html;
                }
            },
            refresh: function() {
                this.init()
            }
        })
        return AppDmsDataRangeWidgetDevelopment;
    })