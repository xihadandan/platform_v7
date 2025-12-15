define(["constant", "commons", "server", "appModal", "AppPtMgrDetailsWidgetDevelopment", "AppPtMgrCommons", 'dataStoreBase', 'formBuilder'],
    function (constant, commons, server, appModal, AppPtMgrDetailsWidgetDevelopment, AppPtMgrCommons, DataStore, formBuilder) {
        var validator;
        var listView;
        // 平台管理_产品集成_消息格式配置详情_HTML组件二开
        var AppMessageTemplateDetailsWidgetDevelopment = function () {
            AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
        };

        // 接口方法
        commons.inherit(AppMessageTemplateDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
            // 组件初始化
            init: function () {
                var _self = this;
                // 验证器
                validator = server.Validation.validate({
                    beanName: "messageTemplate",
                    container: this.widget.element,
                    wrapperForm: true
                });

                _self._formInputRender();
                // 绑定事件
                _self._bindEvents();
            },

            _formInputRender: function () {
                var _self = this;
                var widget = _self.getWidget();
                var $container = $(widget.element);


            },


            _moduleId: function () {
                return this.getWidgetParams().moduleId
            },
            _bean: function () {
                return {
                    "uuid": null,
                    "moduleId": this._moduleId(),
                    "name": null,
                    "id": null,
                    "code": null,
                    "category": null,
                    "type": null,
                    "systemUnitId": null,
                    "sendWays": null,
                    "sendTime": null,
                    "scheduleTime": null,
                    "mappingRule": null,
                    "messageEvent": null,
                    "messageEventText": null,
                    "messageInteface": null,
                    "messageIntefaceText": null,
                    "isOnlinePopup": null,
                    "showViewpoint": null,
                    "viewpointY": null,
                    "viewpointN": null,
                    "viewpointNone": null,
                    "askForSchedule": null,
                    "foregroundEvent": null,
                    "foregroundEventText": null,
                    "backgroundEvent": null,
                    "backgroundEventText": null,
                    "onlineSubject": null,
                    "onlineBody": null,
                    "relatedTitle": null,
                    "relatedUrl": null,
                    "onlineAttach": null,
                    "smsBody": null,
                    "emailSubject": null,
                    "emailBody": null,
                    "emailAttach": null,
                    "scheduleTitle": null,
                    "scheduleDates": null,
                    "scheduleDatee": null,
                    "scheduleAddress": null,
                    "reminderTime": null,
                    "repeatType": null,
                    "scheduleBody": null,
                    "srcTitle": null,
                    "srcAddress": null,
                    "webServiceUrl": null,
                    "usernameKey": null,
                    "usernameValue": null,
                    "passwordKey": null,
                    "passwordValue": null,
                    "tenantidKey": null,
                    "tenantidValue": null,
                    "children": [],
                    "changedChildren": [],
                    "deletedChildren": []
                };
            },


            $msgTemplateTable: function () {
                return $("#wBootstrapTable_C87B228379000001609A4007D2FE1CD1", this.getPageContainer().element).data('uiWBootstrapTable');
            },

            _initWsParamsTable: function () {
                var _self = this;
                var $container = $(this.widget.element);
                var $wsParamsTable = $("#table_ws_params_info", $container);
                // 定义新增，删除按钮事件
                formBuilder.bootstrapTable.initTableTopButtonToolbar("table_ws_params_info", "ws_params", $container, {
                    parmName: '',
                    parmValue: '',
                    parmdesc: ''
                }, 'GUUID');


                $wsParamsTable.bootstrapTable("destroy").bootstrapTable({
                    data: [],
                    idField: "GUUID",
                    striped: false,
                    showColumns: false,
                    toolbar: $("#div_ws_params_toolbar", $container),
                    width: 500,
                    columns: [{
                        field: "checked",
                        formatter: function (value, row, index) {
                            if (value) {
                                return true;
                            }
                            return false;
                        },
                        checkbox: true
                    }, {
                        field: "GUUID",
                        title: "GUUID",
                        visible: false
                    }, {
                        field: "uuid",
                        title: "UUID",
                        visible: false
                    }, {
                        field: "parmName",
                        title: "参数名称",
                        editable: {
                            type: "text",
                            mode: "inline",
                            showbuttons: false,
                            onblur: "submit",
                            savenochange: true
                        }
                    }, {
                        field: "parmValue",
                        title: "参数值",
                        editable: {
                            type: "text",
                            mode: "inline",
                            showbuttons: false,
                            onblur: "submit",
                            savenochange: true
                        }
                    }, {
                        field: "parmdesc",
                        title: "参数说明",
                        editable: {
                            type: "text",
                            mode: "inline",
                            showbuttons: false,
                            onblur: "submit",
                            savenochange: true
                        }
                    },
                    ]
                });
            },

            _bindEvents: function () {
                var _self = this;
                var widget = _self.getWidget();
                var $container = $(widget.element);
                var pageContainer = _self.getPageContainer();

                $("#moduleId",$container).val(_self._moduleId());
                //模块选择
                $("#moduleId",$container).wSelect2({
                    valueField: "moduleId",
                    remoteSearch: false,
                    serviceName: "appModuleMgr",
                    queryMethod: "loadSelectData",
                    params: {
                        //systemUnitId:server.SpringSecurityUtils.getCurrentUserUnitId()
                        //excludeIds: _self._moduleId()
                    }
                });

                //接口实现
                $("#messageInteface").comboTree("clear");
                $("#messageInteface").comboTree("enable");
                $("#messageInteface").comboTree("setParams", {
                    treeSetting: {
                        async: {
                            otherParam: {
                                "serviceName": "messageService",
                                "methodName": "getIntefaceSourceList",
                                "data": 1
                            }
                        }, check: {
                            enable: false
                        },
                        callback: {
                            onClick: function (event, treeId, treeNode) {
                                //$("#messageIntefaceText").val(treeNode.name);
                                $("#messageInteface").val(treeNode.id);
                                //$("#messageIntefaceText").comboTree("hide");
                            }
                        }
                    }
                });

                _self._initWsParamsTable();


                $("input[name=sendTime]", $container).on('change', function () {
                    if ($("input[name=sendTime]:checked", $container).val() == 'SCHEDULE_TIME') {
                        $(".scheduleTime", $container).show();
                    } else {
                        $(".scheduleTime", $container).hide();
                    }
                }).trigger('change');

                $("#sendWay_inteface", $container).on("click", function () {
                    $(".messageInteface", $container).hide();
                    if ($(this).prop('checked')) {
                        $(".messageInteface", $container).show();
                    }
                });

                //发送消息触发事件
                $("#messageEvent", $container).comboTree("clear");
                $("#messageEvent", $container).comboTree("enable");
                $("#messageEvent", $container).comboTree("setParams", {
                    treeSetting: {
                        async: {
                            otherParam: {
                                "serviceName": "messageEventService",
                                "methodName": "getEventClientSourceList",
                                "data": 1
                            }
                        }, check: {
                            enable: false
                        },
                        callback: {
                            onClick: function (event, treeId, treeNode) {
                                //$("#messageIntefaceText").val(treeNode.name);
                                $("#messageEvent").val(treeNode.id);
                                //$("#messageIntefaceText").comboTree("hide");
                            }
                        }
                    }
                });

                //前台JS脚本事件
                $("#foregroundEvent", $container).wSelect2({
                    serviceName: "dataDictionaryService",
                    params: {type: "ONLINE_ARRIVE_JS_EVENT"},
                    valueField: "foregroundEvent",
                    remoteSearch: false
                });

                //后台事件
                $("#backgroundEvent", $container).wSelect2({
                    serviceName: "messageEventService",
                    valueField: "backgroundEvent",
                    remoteSearch: false
                });


                // 新增
                pageContainer.off('AppMsgTemplateListView.editRow');
                pageContainer.on("AppMsgTemplateListView.editRow", function (e) {
                    // 清空表单
                    AppPtMgrCommons.clearForm({
                        container: $container,
                        includeHidden: true
                    });

                    _self._uneditableForm(false);
                    $("#moduleId", $container).val(_self._moduleId());

                    if (!e.detail.rowData) {
                        //新增
                        // 生成ID
                        AppPtMgrCommons.idGenerator.generate($container.find("#id"), "MSG_");
                        $("#code", $container).val($("#id", $container).val().replace('MSG_', ''));
                        // ID可编辑
                        $("#id", $container).prop("readonly", false);
                    } else {
                        //编辑
                        server.JDS.call({
                            service: "messageTemplateService.getBeanById",
                            data: [e.detail.rowData.id],
                            version: '',
                            success: function (result) {
                                if (result.success) {
                                    var bean = _self._bean();
                                    $.extend(bean, result.data);
                                    bean = result.data;

                                    AppPtMgrCommons.json2form({
                                        json: bean,
                                        container: $container
                                    });
                                    // ID只读
                                    $("#id", $container).prop("readonly", true);
                                    validator.form();
                                    $("#foregroundEvent", $container).trigger("change");
                                    $("#backgroundEvent", $container).trigger("change");
                                    $("input[name=sendTime]", $container).trigger('change');
                                    _self._refreshWsParamsTable(bean.children);
                                    $("#sendWay_inteface", $container).trigger("change");

                                    if (e.detail.rowData.isRef == 1) {//被引用，不允许编辑
                                        _self._uneditableForm();
                                    }

                                }
                            }
                        });
                    }

                    // 显示第一个tab内容
                    $(".nav-tabs>li>a:first", $container).tab("show");

                    listView = e.detail.ui;

                });


                $("#btn_save_msgtemplate", $container).on('click', function () {
                    _self._save_msgTemplate();
                    return false;
                });

                $('a[data-toggle="tab"]', $container).on('shown.bs.tab', function (e) {
                    if ($(e.target).attr('aria-controls') == 'web_service') {
                        $("#table_ws_params_info", _self.widget.element).bootstrapTable('resetView');
                    }
                });

            },

            _uneditableForm: function (uneditable) {
                uneditable = uneditable == undefined ? true : Boolean(uneditable);
                AppPtMgrCommons.uneditableForm({
                    container: this.widget.element,
                }, uneditable);

                //表格不可编辑
                $("#div_ws_params_toolbar", this.widget.element).show();
                if (uneditable) {
                    $("#div_ws_params_toolbar", this.widget.element).hide();
                }

                $("#table_ws_params_info").each(function () {
                    $(this).bootstrapTable('refreshOptions', {
                        editable: !uneditable
                    })
                });

                //保存按钮不可用
                $("#btn_save_msgtemplate", this.widget.element).prop('disabled', uneditable);

                $("#moduleId", this.widget.element).prop('readonly', true);//模块id不可变
            },

            _refreshWsParamsTable: function (data) {
                $("#table_ws_params_info", this.widget.element).bootstrapTable('load', data);
                $("#table_ws_params_info", this.widget.element).bootstrapTable('resetView');
            },

            _save_msgTemplate: function () {
                var _self = this;
                var bean = this._bean();
                AppPtMgrCommons.form2json({
                    json: bean,
                    container: this.widget.element,
                    ignorePropertys: ['moduleId']
                });

                //收集webservice参数
                var children = $("#table_ws_params_info", this.widget.element).bootstrapTable('getData');
                bean.changedChildren = children;
                bean.systemUnitId = server.SpringSecurityUtils.getCurrentUserUnitId();
                if(!validator.form()){
                    return false;
                }
                server.JDS.call({
                    service: "messageTemplateService.saveBean",
                    data: [bean],
                    version: '',
                    validate: true,
                    success: function (result) {
                        appModal.success("保存成功！");
                        // 保存成功刷新列表
                        listView.trigger('AppMessageTemplateListView.refresh');
                        // 清空表单
                        AppPtMgrCommons.clearForm({
                            container: _self.widget.element,
                            includeHidden: true
                        });
                        _self._refreshWsParamsTable([]);
                    }
                });
            }
        });
        return AppMessageTemplateDetailsWidgetDevelopment;
    });