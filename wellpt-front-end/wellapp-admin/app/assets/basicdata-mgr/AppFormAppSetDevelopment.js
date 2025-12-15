define(["constant", "commons", "server", "appContext", "appModal", "formBuilder", "HtmlWidgetDevelopment"],
    function(constant, commons, server, appContext, appModal, formBuilder, HtmlWidgetDevelopment) {
        var StringUtils = commons.StringUtils;
        var StringBuilder = commons.StringBuilder;
        var JDS = server.JDS;

        //  平台应用_公共资源_表单应用配置_编辑二开
        var AppFormAppSetDevelopment = function() {
            HtmlWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppFormAppSetDevelopment, HtmlWidgetDevelopment, {
            // 准备创建回调
            prepare: function() {},
            // 创建后回调
            create: function() {
                var _self = this;

                function _f(type, serviceName, queryMethod) {
                    $.ajax({
                        type: "POST",
                        url: ctx + "/common/select2/query",
                        dataType: "json",
                        async: false,
                        data: JSON.stringify({
                            serviceName: serviceName,
                            queryMethod: queryMethod,
                            pageSize: 1000,
                            pageNo: 1
                        }),
                        success: function(result) {
                            result.results.unshift({
                                id: '',
                                text: ''
                            });
                            _self[type] = result.results;
                        }
                    });
                }

                _f('dict', 'businessApplicationService', 'querySelectDataForFormApp');
                _f('ruleUuid', 'botRuleConfFacadeService', 'querySelectDataFromBotRuleConfig');
                _f('formUuid', 'formDefinitionService', 'querySelectDataFromFormDefinition');
            },
            // 初始化回调
            init: function() {
                var _self = this;
                var _uuid;
                var rowData = {};
                if (_self.getWidgetParams().uuid) {
                    rowData = _self.getWidgetParams();
                    _uuid = _self.getWidgetParams().uuid;
                }
                // 表单选择器
                var form_selector = "#form_application_form";

                var oldRoles = [];
                var bean = {
                    "uuid": null,
                    "name": null,
                    "manageDeptValue": "",
                    "manageUserValue": ''
                };

                var validator = $.common.validation.validate(form_selector, "businessCategoryEntity", function(options) {
                    options.ignore = "";
                });

                $(form_selector).json2form(bean);
                $('#form_application_uuid').wSelect2({
                    serviceName: "businessCategoryService",
                    queryMethod: "querySelectDataFromBusinessCategory",
                    selectionMethod: "loadSelectDataFromBusinessCategory",
                    labelField: "form_application_name",
                    valueField: "form_application_uuid",
                    defaultBlank: true
                });

                $("#form_application_uuid").on('change', function() {
                    if ($(this).val() === '') {
                        $('#form_application_manageDeptValue').val('');
                        $('#form_application_manageUserValue').val('');
                        return;
                    }
                    JDS.call({
                        service: "businessCategoryService.getOne",
                        data: [$(this).val()],
                        success: function(result) {
                            if (result.data) {
                                $('#form_application_manageDeptValue').val(result.data.manageDeptValue);
                                $('#form_application_manageUserValue').val(result.data.manageUserValue);
                            }
                        }
                    });
                });

                // 获取数据
                function getFormApp() {
                    $(form_selector).json2form(rowData);
                    JDS.call({
                        service: "businessApplicationService.findBusinessApplicationConfig",
                        data: [rowData.applicationUuid],
                        success: function(result) {
                            bean = result.data;
                            $("#form_application_uuid").trigger('change');
                            setTimeout(function() {
                                initTable(bean);
                            }, 0);
                        }
                    });
                }

                var $form_application_table = $("#form_application_table", _self.element);

                function initTable(bean) {
                    var _data = bean || [];
                    $.each(_data, function(i, item) {
                        item.jUuid = commons.UUID.createUUID();
                        item.dictName = $.map(_self.dict, function(dict) {
                            if (dict.id === item.dict) {
                                return dict.text;
                            }
                        });
                        item.formUuidName = $.map(_self.formUuid, function(formUuid) {
                            if (formUuid.id === item.formUuid) {
                                return formUuid.text;
                            }
                        });
                        item.ruleUuidName = $.map(_self.ruleUuid, function(ruleUuid) {
                            if (ruleUuid.id === item.ruleUuid) {
                                return ruleUuid.text;
                            }
                        });
                    });
                    var _bean = {
                        checked: false,
                        jUuid: '',
                        uuid: '',
                        dict: '',
                        dictName: '',
                        formUuid: '',
                        formUuidName: '',
                        ruleUuid: '',
                        ruleUuidName: ''
                    };
                    console.log(_data, _self);
                    // 定义添加，删除，上移，下移4按钮事件
                    formBuilder.bootstrapTable.initTableTopButtonToolbar("form_application_table", "form_application", _self.element, _bean, 'jUuid');

                    $form_application_table.bootstrapTable("destroy").bootstrapTable({
                        data: _data,
                        idField: "jUuid",
                        striped: true,
                        width: 500,
                        onEditableHidden: function(field, row, $el, reason) {
                            $el.closest("table").bootstrapTable("resetView")
                        },
                        onEditableSave: function(field, row, oldValue, $el) {
                            if (field === 'attrType') {
                                var allData = $form_application_table.bootstrapTable('getData');
                                $.each(allData, function(i, item) {
                                    if (item.uuid === row.uuid) {
                                        row.attrDisplay = row.attrValue = '';
                                        $form_application_table.bootstrapTable("updateRow", i, row);
                                    }
                                })
                            }
                        },
                        onClickCell: function(field, value, rowData, $el) {
                            function _f(_field, fieldName, serviceName, queryMethod, selectionMethod, title) {
                                var container = $('<div>');
                                if (_field === 'dict') {
                                    container.html(_self.dlg_dict_Html());
                                } else if (_field === 'formUuid') {
                                    container.html(_self.dlg_formUuid_Html());
                                } else {
                                    container.html(_self.dlg_ruleUuid_Html());
                                }
                                appModal.dialog({
                                    title: title,
                                    size: "small",
                                    message: container,
                                    shown: function() {
                                        var _id = 'form_application_' + _field;
                                        var _label = _id + 'Name';
                                        $("#" + _id, container).val(rowData[_field]);

                                        $('#' + _id, container).wSelect2({
                                            serviceName: serviceName,
                                            queryMethod: queryMethod,
                                            selectionMethod: selectionMethod,
                                            valueField: _id,
                                            labelField: _label,
                                            defaultBlank: true
                                        })
                                    },
                                    callback: function() {
                                        var allData = $form_application_table.bootstrapTable('getData');
                                        var newRowData = {};
                                        newRowData[_field] = $("input[name='" + _field + "']", container).val();
                                        newRowData[fieldName] = $("input[name='" + fieldName + "']", container).val();
                                        $.each(allData, function(i, item) {
                                            if (item.jUuid === rowData.jUuid) {
                                                $form_application_table.bootstrapTable("updateRow", i, $.extend(rowData, newRowData));
                                                return false;
                                            }
                                        });
                                    }
                                })
                            }
                            if (field === 'dictName') {
                                _f('dict', field, 'businessApplicationService', 'querySelectDataForFormApp', 'loadSelectDataForFormApp', '应用于')
                            } else if (field === 'ruleUuidName') {
                                _f('ruleUuid', field, 'botRuleConfFacadeService', 'querySelectDataFromBotRuleConfig', 'loadSelectDataFromBotRuleConfig', '应用子表单')
                            } else if (field === 'formUuidName') {
                                _f('formUuid', field, 'formDefinitionService', 'querySelectDataFromFormDefinition', 'loadSelectDataFromFormDefinition', '表单转换规则')
                            }
                        },
                        toolbar: $("#div_form_application_toolbar", _self.element),
                        columns: [{
                            field: "checked",
                            checkbox: true,
                            formatter: function(value) {
                                if (value) {
                                    return true;
                                }
                                return false;
                            }
                        }, {
                            field: "jUuid",
                            title: "jUuid",
                            visible: false
                        }, {
                            field: "uuid",
                            title: "UUID",
                            visible: false
                        }, {
                            field: "businessApplicationUuid",
                            title: "businessApplicationUuid",
                            visible: false
                        }, {
                            field: "dictName",
                            title: "应用于",
                            width: "30%",
                            editable: {
                                type: "text",
                                showbuttons: false,
                                onblur: "submit",
                                mode: "inline",
                                formatter: function(value) {
                                    console.log(value, '111')
                                },
                                validate: function(value) {
                                    if (StringUtils.isBlank(value)) {
                                        return '请选择应用于!';
                                    }
                                }
                            }
                        }, {
                            field: "formUuidName",
                            title: "应用子表单",
                            width: "30%",
                            editable: {
                                type: "text",
                                showbuttons: false,
                                onblur: "submit",
                                mode: "inline",
                                validate: function(value) {
                                    if (StringUtils.isBlank(value)) {
                                        return '请选择应用子表单!';
                                    }
                                }
                            }
                        }, {
                            field: "ruleUuidName",
                            title: "表单转换规则",
                            editable: {
                                type: "text",
                                showbuttons: false,
                                onblur: "submit",
                                mode: "inline",
                                validate: function(value) {
                                    if (StringUtils.isBlank(value)) {
                                        return '请选择表单转换规则!';
                                    }
                                }
                            }
                        }]
                    })
                }

                if (_uuid) {
                    getFormApp()
                } else {
                    initTable();
                }

                // 保存脚本定义信息
                $("#form_application_btn_save").click(function() {
                    var businessCategoryUuid = $('#form_application_uuid').val();
                    if (!businessCategoryUuid) {
                        appModal.alert('请选择业务数据！');
                        return;
                    }
                    var allData = $form_application_table.bootstrapTable('getData');

                    $.each(allData, function(i, item) {
                        if (item.dict === '' || item.dict === '-1') {
                            appModal.alert('请选择应用于！');
                            return false;
                        } else if (item.formUuid === '') {
                            appModal.alert('请选择应用于！');
                            return false;
                        } else if (item.ruleUuid === '') {
                            appModal.alert('请选择应用于！');
                            return false;
                        }
                        $.extend(item, JSON.parse(item.dict));
                    });

                    var uuid = $('#applicationUuid').val();

                    var data = {
                        uuid: uuid,
                        businessCategoryUuid: businessCategoryUuid,
                        configs: allData
                    };

                    JDS.call({
                        service: "businessApplicationService.save",
                        data: [data],
                        async: false,
                        validate: true,
                        success: function(result) {
                            appModal.success("保存成功！");
                            appContext.getNavTabWidget().refreshParentTab();
                        }
                    });
                });
            },

            refresh: function() {
                var _self = this;
                _self.init()
            },

            dlg_dict_Html: function() {
                return '<div class="well-form well-dialog-form form-horizontal">' +
                    '        <div class="form-group" data-type="2">' +
                    '            <label class="well-form-label control-label">应用于</label>' +
                    '            <div class="well-form-control">' +
                    '                <input type="hidden" class="form-control" id="form_application_dict" name="dict">' +
                    '                <input type="hidden" class="form-control" id="form_application_dictName" name="dictName">' +
                    '            </div>' +
                    '        </div>' +
                    '    </div>'
            },
            dlg_formUuid_Html: function() {
                return '<div class="well-form well-dialog-form form-horizontal">' +
                    '        <div class="form-group" data-type="2">' +
                    '            <label class="well-form-label control-label">应用子表单</label>' +
                    '            <div class="well-form-control">' +
                    '                <input type="hidden" class="form-control" id="form_application_formUuid" name="formUuid">' +
                    '                <input type="hidden" class="form-control" id="form_application_formUuidName" name="formUuidName">' +
                    '            </div>' +
                    '        </div>' +
                    '    </div>'
            },
            dlg_ruleUuid_Html: function() {
                return '<div class="well-form well-dialog-form form-horizontal">' +
                    '        <div class="form-group" data-type="2">' +
                    '            <label class="well-form-label control-label">表单转换规则</label>' +
                    '            <div class="well-form-control">' +
                    '                <input type="hidden" class="form-control" id="form_application_ruleUuid" name="ruleUuid">' +
                    '                <input type="hidden" class="form-control" id="form_application_ruleUuidName" name="ruleUuidName">' +
                    '            </div>' +
                    '        </div>' +
                    '    </div>'
            },
        });
        return AppFormAppSetDevelopment;
    });