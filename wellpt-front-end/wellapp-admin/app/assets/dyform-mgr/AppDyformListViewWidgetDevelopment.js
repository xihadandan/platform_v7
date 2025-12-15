define(["constant", "commons", "server", "appContext", "appModal", "AppPtMgrListViewWidgetDevelopment",
    "AppPtMgrCommons"
], function(constant, commons, server, appContext, appModal,
    AppPtMgrListViewWidgetDevelopment, AppPtMgrCommons) {
    var StringUtils = commons.StringUtils;
    var JDS = server.JDS;

    // 平台管理_产品集成_模块_表单列表_视图组件二开
    var AppDyformListViewWidgetDevelopment = function() {
        AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
    };

    // 接口方法
    commons.inherit(AppDyformListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
        // 组件准备
        prepare: function() {},
        beforeRender: function(options, configuration) {
            // 归属模块ID
            this.widget.addParam('moduleId', this._moduleId())
        },

        afterRender: function(options, configuration) {
            var _self = this;
            var $element = $(_self.widget.element);
            var $searchInput = $element.find("input[type='search']");
            var $select = $("<select>", { "style": "width:100px;" });
            $select.append(
                $("<option>", { "value": "" }).html("&nbsp;"),
                $("<option>", { "value": "P" }).text("存储单据"),
                $("<option>", { "value": "V" }).text("展示单据"),
                $("<option>", { "value": "M" }).text("手机单据"),
                $("<option>", { "value": "MST" }).text("子表单")
            );
            var $div = $("<div>", { "class": "pull-right search" }).append($select);
            $div.insertAfter($searchInput.parent());

            $select.on('change', function() {
                var formType = $(this).val();
                _self.widget.getDataProvider().setKeyword({});
                //清除类型查询
                if (_self.widget.lastForTypeConditions) {
                    _self.widget.clearOtherConditions(_self.widget.lastForTypeConditions);
                }
                if (formType) {
                    var formTypeCondition = {
                        columnIndex: "formType",
                        value: formType,
                        type: "eq"
                    };
                    _self.widget.addOtherConditions([formTypeCondition]);
                    _self.widget.lastForTypeConditions = formTypeCondition;
                }

            });

            _self.$formTypeSelect = $select;

            if (!_self._moduleId()) {
                _self.widget.element.find(".btn_class_reference_form").hide()
            }

            this.widget.on('RefreshDyformDefinitionTable', function() {
                _self.refresh();
            });

        },

        onLoadSuccess: function() {
            var rows = this.getData();
            var $tableElement = this.widget.$tableElement;
            for (var i = 0, len = rows.length; i < len; i++) {
                if (rows[i].isRef === 1) {
                    var $nameTd = $tableElement.find("tr[data-index=" + i + "]").find('td:eq(1)');
                    (function($td, moduleId) {
                        server.JDS.call({
                            service: 'appModuleMgr.getModuleDetail',
                            data: [moduleId],
                            version: '',
                            success: function(result) {
                                if (result.success && result.data) {
                                    $td.append(
                                        $("<a>", {
                                            "class": "glyphicon glyphicon-link",
                                            "style": "color:#5cce66;margin-left:3px;transform: rotate(50deg);text-decoration: none;",
                                            "title": "引自：" + (result.data.appSystemBean ?
                                                result.data.appSystemBean.name + "/" : "") + (result.data.name)
                                        })
                                    )
                                }
                            },
                            error: function(jqXHR) {},
                            async: true
                        });

                    })($nameTd, rows[i].moduleId);

                }
            }
        },

        onResetQueryFields: function() {
            this.$formTypeSelect.find('option:eq(0)').prop('selected', true);
            this.$formTypeSelect.trigger('change');
        },

        refrenceFormDialogHtml: function() {
            var $div = $("<div>", { "class": "container-fluid" });
            var $row1 = $("<div>", { "class": "row form-group" }).append(
                $("<div>", { "class": "col-sm-2" }).text('所属模块'),
                $("<div>", { "class": "col-sm-10" }).append(
                    $("<input>", {
                        "type": "hidden",
                        "id": "moduleBelong",
                        "style": "width:400px;"
                    })
                )
            );
            var $row2 = $("<div>", { "class": "row form-group" }).append(
                $("<div>", { "class": "col-sm-2 required" }).text('引用表单'),
                $("<div>", { "class": "col-sm-10" }).append(
                    $("<input>", {
                        "type": "hidden",
                        "id": "moduleFormInput",
                        "style": "width:400px;"
                    })
                )
            );

            $div.append($row1, $row2);
            return $div[0].outerHTML;
        },

        refresh: function() {
            //刷新徽章
            var tabpanel = this.widget.element.parents('.active');
            if (tabpanel.length > 0) {
                var id = tabpanel.attr('id');
                id = id.substring(0, id.indexOf('-'));
                $("#" + id).trigger(constant.WIDGET_EVENT.BadgeRefresh, {
                    targetTabName: '表单',
                });
            }
            return this.getWidget().refresh(this.options);
        },

        _moduleId: function() {
            return this.getWidgetParams().moduleId
        },

        //引用其他模块的表单
        reference_form: function() {

            //弹窗
            var _self = this;
            var $dialog;
            var dialogOpts = {
                title: '引用表单',
                message: _self.refrenceFormDialogHtml(),
                buttons: {
                    confirm: {
                        label: "确定",
                        className: "btn-primary",
                        callback: function(result) {
                            var commitResult = false;
                            var moduleFormUuid = $("#moduleFormInput").val();
                            if (!moduleFormUuid) {
                                return false;
                            }
                            server.JDS.call({
                                service: 'moduleFunctionConfigRefFacadeService.saveModuleFunctionConfigRef',
                                data: [
                                    [{
                                        refUuid: moduleFormUuid,
                                        moduleId: _self._moduleId(),
                                        entityClass: 'FormDefinitionRefEntity'
                                    }]
                                ],
                                version: '',
                                success: function(result) {
                                    if (result.success) {
                                        commitResult = true;
                                        appModal.success('引用表单成功');
                                        _self.refresh();
                                    }
                                },
                                error: function(jqXHR) {
                                    appModal.alert("引用失败");
                                },
                                async: false
                            });
                            return commitResult;
                        }
                    },

                    cancel: {
                        label: "取消",
                        className: "btn-default",
                        callback: function(result) {}
                    }
                },
                shown: function() {
                    var $moduleSelect = $("#moduleBelong", $dialog);
                    $moduleSelect.wSelect2({
                        valueField: "moduleBelong",
                        remoteSearch: false,
                        serviceName: "appModuleMgr",
                        queryMethod: "loadSelectData",
                        defaultBlankText: '全部',
                        defaultBlankValue: '-1',
                        params: {
                            //systemUnitId:server.SpringSecurityUtils.getCurrentUserUnitId()
                            excludeIds: _self._moduleId()
                        }
                    });

                    $moduleSelect.on('change', function() {
                        var moduleId = $("#moduleBelong").val();
                        if (!moduleId) {
                            moduleId = -100;
                        }
                        if (moduleId == -1) {
                            moduleId = '';
                        }
                        $("#moduleFormInput").val("").wSelect2({
                            valueField: "moduleFormInput",
                            remoteSearch: false,
                            serviceName: "formDefinitionService",
                            queryMethod: "loadSelectDataFromFormDefinition",
                            params: {
                                moduleId: moduleId,
                                idProperty: 'uuid',
                                excludeModuleIds: _self._moduleId()
                            }
                        });
                    }).trigger('change');
                },
                size: "middle",

            };
            $dialog = appModal.dialog(dialogOpts);

        },

        // 删除
        btn_form_delete: function() {
            var _self = this;
            var rowData = _self.getSelectRowData();
            if (rowData.length > 0) {
                var name = rowData[0].name;
                appModal.confirm("确认要删除表单吗?", function(result) {
                    if (result) {
                        server.JDS.call({
                            service: "dyFormFacade.dropForm",
                            data: [rowData[0].uuid],
                            version: '',
                            success: function(result) {
                                appModal.success("刪除成功");
                                _self.refresh(); //刷新表格
                            },
                            error: function(jqXHR) {
                                var faultData = JSON.parse(jqXHR.responseText);
                                appModal.alert(faultData.msg);
                            }
                        });
                    }
                });
            }
        },

        onDblClickRow: function(rowNum, row, $element, field) {
            var windowOptions = {};
            windowOptions.url = ctx + "/pt/dyform/definition/form-designer";
            windowOptions.target = "_blank";
            windowOptions.urlParams = {
                "uuid": row.uuid,
                "isRef": row.isRef
            };
            appContext.openWindow(windowOptions);
        },

        getSelectRowData: function() {
            var _self = this;
            var $toolbarDiv = $(event.target).closest("div");
            var rowData = [];
            if ($toolbarDiv.is(".div_lineEnd_toolbar")) { //行级点击操作
                var index = $toolbarDiv.attr("index");
                var allData = _self.getData();
                rowData = [allData[index]];
            } else {
                if (!_self.checkSelection()) {
                    return [];
                }
                rowData = _self.getSelections();
            }
            return rowData;
        },

        form_def_import: function() {
            var _self = this;
            // 定义导入
            $.iexportData["import"]({
                callback: function() {
                    _self.refresh();
                }
            });
        },

        form_def_export: function() {
            // 定义导出
            var rowData = this.getSelectRowData();
            if (rowData.length > 0) {
                $.iexportData["export"]({
                    uuid: rowData[0].uuid,
                    type: 'formDefinition'
                });
            } else {
                appModal.alert('请选择导出的表单定义！');
            }

        },

        //格式化行级按钮
        lineEnderButtonHtmlFormat: function(format, row, index) {
            var $html = $(format.before);
            $html.find(row.isRef === 1 ? '.btn_class_btn_form_delete' : '.btn_class_btn_cancel_form_ref').remove();
            format.after = $html[0].outerHTML;
        },

        //取消引用
        btn_cancel_form_ref: function() {
            var _self = this;
            var rowData = _self.getSelectRowData();
            if (rowData.length == 0) {
                return;
            }
            appModal.confirm("确认取消引用表单吗?", function(result) {
                if (result) {
                    server.JDS.call({
                        service: 'moduleFunctionConfigRefFacadeService.deleteModuleFunctionConfigRef',
                        data: [
                            [{
                                refUuid: rowData[0].uuid,
                                moduleId: _self._moduleId(),
                                entityClass: 'FormDefinitionRefEntity'
                            }]
                        ],
                        version: '',
                        success: function(result) {
                            appModal.success("取消引用成功");
                            _self.refresh(); //刷新表格
                        },
                        error: function(jqXHR) {
                            var faultData = JSON.parse(jqXHR.responseText);
                            appModal.alert(faultData.msg);
                        }
                    });
                }
            });
        }

    });
    return AppDyformListViewWidgetDevelopment;
});