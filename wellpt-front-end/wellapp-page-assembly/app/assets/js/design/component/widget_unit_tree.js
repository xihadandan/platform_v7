define(["ui_component", "constant", "commons", "server", "formBuilder", "appContext", "design_commons", "wSelect2"],
    function(ui_component, constant, commons, server, formBuilder, appContext, designCommons) {
        var collectClass = "w-configurer-option";
        var StringUtils = commons.StringUtils;
        var component = $.ui.component.BaseComponent();
        var isJSON = function(str) {
            try {
                JSON.parse(str);
                return true;
            } catch (e) {
                return false;
            }
        }
        var clearChecked = function(row) {
            row.checked = false;
            return row;
        };
        var checkRequire = function(propertyNames, options, $container) {
            for (var i = 0; i < propertyNames.length; i++) {
                var propertyName = propertyNames[i];
                if (StringUtils.isBlank(options[propertyName])) {
                    var title = $("label[for='" + propertyName + "']", $container).text();
                    appModal.error(title.replace("*", "") + "不允许为空！");
                    return false;
                }
            }
            return true;
        };
        var clearInputValue = function($container) {
            $container.find(".w-configurer-option").each(function() {
                var $element = $(this);
                var type = $element.attr("type");
                if (type == "text" || type == "hidden") {
                    $element.val('');
                } else if (type == "checkbox" || type == "radio") {
                    $element.prop("checked", false);
                }
                $element.trigger('change');
            });

        };
        var onEditHidden = function(field, row, $el, reason) {
            $el.closest("table").bootstrapTable("resetView")
        };
        component.prototype.create = function() {
            $(this.element).find(".widget-body").html(this.options.content);
        }
        component.prototype.usePropertyConfigurer = function() {
            return true;
        }
        component.prototype.getPropertyConfigurer = function() {
            var configurer = $.ui.component.BaseComponentConfigurer();
            configurer.prototype.onLoad = function($container, options) {
                var _self = this;
                var configuration = $.extend(true, {}, options.configuration);
                $("#widget_tree_tabs ul a", $container).on("click", function(e) {
                    e.preventDefault();
                    $(this).tab("show");
                })
                configuration.isAsync = configuration.isAsync ? "1" : "0";
                designCommons.setElementValue(configuration, $container, 'nodeTypeInfo');
                // 数据来源
                $("input[name='dataSource']", $container).on("change", function() {
                    if ($(this).attr("checked") != "checked") {
                        return;
                    }
                    var dataSource = $(this).val();
                    $(".data-source").hide();
                    $(".data-source-" + dataSource).show();
                }).trigger("change");
                // 数据仓库
                $("#treeDataStoreName", $container).wSelect2({
                    serviceName: "viewComponentService",
                    queryMethod: "loadSelectData",
                    labelField: "treeDataStoreName",
                    valueField: "treeDataStoreId",
                    remoteSearch: false,
                    params: {
                        piUuid: _self.component.pageDesigner.getPiUuid()
                    }
                });
                $(".treeDsColumnSelect", $container).each(function(i) {
                    var columnDatas = function() {
                        var colDatas = [];
                        server.JDS.call({
                            service: "viewComponentService.getColumnsById",
                            data: [$("#treeDataStoreId", $container).val()],
                            version: '',
                            async: false,
                            success: function(result) {
                                if (result.msg == 'success') {
                                    colDatas = $.map(result.data, function(data) {
                                        return {
                                            value: data.columnIndex,
                                            text: data.columnIndex,
                                            title: data.title,
                                            id: data.columnIndex
                                        };
                                    });
                                }
                            }
                        });
                        return colDatas;
                    };
                    $(this).on('reloadColumn', function() {
                        //展示列
                        var $this = $(this);
                        $this.wSelect2({
                            data: columnDatas(),
                            defaultBlank: true,
                            remoteSearch: false,
                            valueField: $this.attr('name')
                        });
                    }).trigger('reloadColumn');
                });
                $("#treeDataStoreName", $container).on('change', function() {
                    $(".treeDsColumnSelect", $container).trigger('reloadColumn');
                });

                var $tabelInfo = $("#table_node_info");
                $("#uncheckNodeType").wSelect2({
                    multiple: true,
                    data: [{
                        id: 'p',
                        text: '父节点'
                    }, {
                        id: 's',
                        text: '子节点'
                    }]
                });
                $("#checkedNodeType").wSelect2({
                    multiple: true,
                    data: [{
                        id: 'p',
                        text: '父节点'
                    }, {
                        id: 's',
                        text: '子节点'
                    }]
                });
                $("#chkStyle").wSelect2({
                    data: [{
                        id: 'radio',
                        text: '单选'
                    }, {
                        id: 'checkbox',
                        text: '多选'
                    }]
                });
                $("#radioType").wSelect2({
                    data: [{
                        id: 'all',
                        text: '全局树'
                    }, {
                        id: 'level',
                        text: '当前节点'
                    }]
                });
                var $checkDefinition = $("#div_check_definition", $container);
                $("#checkEnable", $container).on('change', function() {
                    if ($(this).is(':checked')) {
                        $checkDefinition.show();
                    } else {
                        $checkDefinition.hide();
                        clearInputValue($checkDefinition);
                    }
                }).trigger("change");
                var $checkboxDefinition = $("#div_checkbox_definition", $container);
                var $radioDefinition = $("#div_radio_definition", $container);
                $("#chkStyle", $container).on('change', function() {
                    if ($(this).val() == "checkbox") {
                        $checkboxDefinition.show();
                        $radioDefinition.hide();
                        clearInputValue($radioDefinition);
                    } else {
                        $checkboxDefinition.hide();
                        $radioDefinition.show();
                        clearInputValue($checkboxDefinition);
                    }
                }).trigger("change");
                // 加载的JS模块
                $("#jsModule", $container).wSelect2({
                    serviceName: "appJavaScriptModuleMgr",
                    params: {
                        dependencyFilter: "TreeWidgetDevelopment"
                    },
                    labelField: "jsModuleName",
                    valueField: "jsModule",
                    remoteSearch: false,
                    multiple: true
                });
                var $ztreeDefinition = $("#div_ztree_definition", $container);
                $("#customSetting", $container).on('change', function() {
                    if ($(this).is(':checked')) {
                        $ztreeDefinition.show();
                    } else {
                        $ztreeDefinition.hide();
                        $("#zTreeSetting").val("");
                    }
                }).trigger("change");
                $("#dataProvider").wSelect2({
                    serviceName: "treeComponentService",
                    queryMethod: "loadTreeComponent",
                    remoteSearch: false
                }).on("change", function() {
                    var dataProviderClz = $(this).val();
                    $tabelInfo.bootstrapTable("removeAll");
                    //动态变化组织节点配置的数据
                    server.JDS.call({
                        service: "treeComponentService.getTreeTypes",
                        data: [dataProviderClz],
                        async: false,
                        success: function(result) {
                            if (result.msg == 'success') {
                                var rowData = $.map(result.data, function(data) {
                                    return {
                                        type: data.type,
                                        name: data.name,
                                        icon: {},
                                        disableChecked: '0',
                                        isShow: "1",
                                    };
                                });
                                $tabelInfo.bootstrapTable("load", rowData);
                            }
                        }
                    });
                    //动态变化数据过滤的提示语
                    server.JDS.call({
                        service: "treeComponentService.getFilterHint",
                        data: [dataProviderClz],
                        version: '',
                        async: false,
                        success: function(result) {
                            $("#dataFilterHint").html(result.data);
                        }
                    });
                });
                $tabelInfo.bootstrapTable("destroy").bootstrapTable({
                    data: configuration.nodeTypeInfo,
                    idField: "type",
                    striped: true,
                    width: 500,
                    onEditableHidden: onEditHidden,
                    columns: [{
                        field: "type",
                        title: "类型",
                        visible: false
                    }, {
                        field: "name",
                        title: "类型名称",
                        width: "210px"
                    }, {
                        field: "icon",
                        title: "图标",
                        visible: false,
                        editable: {
                            onblur: "cancel",
                            type: "wCustomForm",
                            placement: "bottom",
                            savenochange: true,
                            value2input: designCommons.bootstrapTable.icon.value2input,
                            input2value: designCommons.bootstrapTable.icon.input2value,
                            value2display: designCommons.bootstrapTable.icon.value2display,
                            value2html: designCommons.bootstrapTable.icon.value2html
                        }
                    }, {
                        field: "disableChecked",
                        title: "禁止选择",
                        editable: {
                            type: "select",
                            mode: "inline",
                            showbuttons: false,
                            source: [{
                                value: '1',
                                text: "是"
                            }, {
                                value: '0',
                                text: "否"
                            }]
                        }
                    }, {
                        field: "isShow",
                        title: "是否展示",
                        editable: {
                            type: "select",
                            mode: "inline",
                            showbuttons: false,
                            source: [{
                                value: '1',
                                text: "是"
                            }, {
                                value: '0',
                                text: "否"
                            }]
                        }
                    }]
                });

                var editor = ace.edit('dataFilter');
                editor.setTheme("ace/theme/clouds");
                editor.session.setMode("ace/mode/sql");
                //启用提示菜单
                ace.require("ace/ext/language_tools");
                editor.setOptions({
                    enableBasicAutocompletion: true,
                    enableSnippets: true,
                    enableLiveAutocompletion: true,
                    showPrintMargin: false,
                    enableVarSnippets: {
                        value: "wComponentDataStoreCondition",
                        showSnippetsTabs: ['内置变量', '常用代码逻辑'],
                        scope: ['sql']
                    },
                    enableCodeHis: {
                        relaBusizUuid: this.component.options.id,
                        codeType: 'wUnitTree.dataFilter',
                        enable: true,
                    }
                });
                if (configuration.dataFilter) {
                    editor.setValue(configuration.dataFilter);
                }
                $('#dataFilter').data('codeEditor', editor);


                this.initButtonInfo(configuration, $container);
            }
            configurer.prototype.initButtonInfo = function(configuration, $container) {
                var buttonData = configuration.buttons ? configuration.buttons : [];
                console.log(buttonData);
                var piUuid = this.component.pageDesigner.getPiUuid();
                var system = appContext.getCurrentUserAppData().getSystem();
                var productUuid = system.productUuid;
                if (StringUtils.isNotBlank(system.piUuid)) {
                    piUuid = system.piUuid;
                }
                // 按钮定义
                var $buttonInfoTable = $("#table_button_info", $container);

                var buttonRowBean = {
                    checked: false,
                    uuid: '',
                    code: '',
                    text: '',
                    position: ['1'],
                    group: '',
                    cssStr: 'btn-bg-color',
                    cssClass: 'btn-default'
                };
                // 定义添加，删除，上移，下移4按钮事件
                formBuilder.bootstrapTable.initTableTopButtonToolbar("table_button_info", "button", $container, buttonRowBean);

                $buttonInfoTable.bootstrapTable("destroy").bootstrapTable({
                    data: buttonData,
                    idField: "uuid",
                    showColumns: true,
                    striped: true,
                    width: 500,
                    onEditableHidden: onEditHidden,
                    toolbar: $("#div_button_info_toolbar", $container),
                    columns: [{
                        field: "checked",
                        checkbox: true,
                        formatter: designCommons.checkedFormat
                    }, {
                        field: "uuid",
                        title: "UUID",
                        visible: false
                    }, {
                        field: "text",
                        title: "名称",
                        width: 80,
                        editable: {
                            type: "text",
                            mode: "inline",
                            showbuttons: false,
                            onblur: "submit",
                            validate: function(value) {
                                if (StringUtils.isBlank(value)) {
                                    return '请输入名称!';
                                }
                            }
                        }
                    }, {
                        field: "code",
                        title: "编码",
                        width: 80,
                        editable: {
                            type: "text",
                            mode: "inline",
                            showbuttons: false,
                            onblur: "submit",
                            savenochange: true,
                            validate: function(value) {
                                if (StringUtils.isBlank(value)) {
                                    return '请输入编码!';
                                }
                                // var regu = "^[a-zA-Z\_][0-9a-zA-Z]*$";
                                // var re = new RegExp(regu);
                                // if (!re.test(value)) {
                                // return '请输入数字、字母、下划线且不以数字开头!';
                                // }
                            }
                        }
                    }, {
                        field: "btnLib",
                        title: "按钮库",
                        width: 100,
                        editable: {
                            onblur: "save",
                            type: "wCustomForm",
                            placement: "bottom",
                            savenochange: true,
                            value2input: designCommons.bootstrapTable.btnLib.value2input,
                            input2value: designCommons.bootstrapTable.btnLib.input2value,
                            value2display: designCommons.bootstrapTable.btnLib.value2display,
                            value2html: designCommons.bootstrapTable.btnLib.value2html
                        }
                    }, {
                        field: "target",
                        title: "目标位置",
                        width: 100,
                        editable: {
                            onblur: "cancel",
                            type: "wCustomForm",
                            placement: "bottom",
                            savenochange: true,
                            value2input: designCommons.bootstrapTable.targePosition.value2input,
                            value2display: designCommons.bootstrapTable.targePosition.value2display,
                            inputCompleted: designCommons.bootstrapTable.targePosition.inputCompleted
                        }
                    }, {
                        field: "eventManger",
                        title: "事件管理",
                        width: 320,
                        editable: {
                            mode: "modal",
                            onblur: "ignore",
                            type: "wCustomForm",
                            placement: "bottom",
                            savenochange: true,
                            renderParams: {
                                defineJs: false,
                            },
                            value2input: designCommons.bootstrapTable.eventManager.value2input,
                            input2value: designCommons.bootstrapTable.eventManager.input2value,
                            validate: designCommons.bootstrapTable.eventManager.validate,
                            value2display: designCommons.bootstrapTable.eventManager.value2display
                        }
                    }]
                });
            }
            configurer.prototype.onOk = function($container) {
                if (this.component.isReferenceWidget()) {
                    return;
                }
                var opt = designCommons.collectConfigurerData($("#widget_tree_tabs_base_info", $container),
                    collectClass);
                opt.checkEnable = Boolean(opt.checkEnable);
                opt.hasSearch = Boolean(opt.hasSearch);
                opt.showTitleToolbar = Boolean(opt.showTitleToolbar);
                opt.customSetting = Boolean(opt.customSetting);
                opt.nodeTypeInfo = $("#table_node_info").bootstrapTable("getData");
                opt.isAsync = opt.isAsync == "1";
                opt.dataFilter = $("#dataFilter", $container).data('codeEditor').getValue();
                // 收集按钮定义
                this.collectButtonInfo(opt, $container);
                var buttons = opt.buttons;
                for (var i = 0; i < buttons.length; i++) {
                    var button = buttons[i];
                    if (StringUtils.isBlank(button.text)) {
                        appModal.error("按钮的名称不允许为空！");
                        return false;
                    }
                    if (StringUtils.isBlank(button.code)) {
                        appModal.error("按钮的编码不允许为空！");
                        return false;
                    }
                }
                var requeryFields = ['name'];
                if (opt.dataSource == "2") {
                    requeryFields.push('treeDataStoreName');
                    requeryFields.push('treeUniqueColumn');
                    requeryFields.push('treeDisplayColumn');
                } else {
                    requeryFields.push('dataProvider');
                }
                if (opt.checkEnable) {
                    requeryFields.push('chkStyle');
                }
                if (opt.chkStyle == 'radio') {
                    requeryFields.push('radioType');
                }
                if (!checkRequire(requeryFields, opt, $container)) {
                    return false;
                }
                if (opt.customSetting && StringUtils.isNotBlank(opt.zTreeSetting)) {
                    if (!isJSON(opt.zTreeSetting)) {
                        appModal.error("Ztree定义JSON非有效格式json！");
                        return false;
                    }
                }
                this.component.options.configuration = $.extend({}, opt);
            }
            configurer.prototype.collectButtonInfo = function(opt, $container) {
                // 按钮定义
                var $tableButtonInfo = $("#table_button_info", $container);
                var buttons = $tableButtonInfo.bootstrapTable("getData");

                opt.buttons = $.map(buttons, clearChecked);
            }
            return configurer;
        }
        component.prototype.toHtml = function() {
            var options = this.options;
            var id = this.getId();
            var html = '<div id="' + id + '" class="ui-wUnitTree"></div>';
            return html;
        }
        component.prototype.getDefinitionJson = function() {
            var options = this.options;
            var id = this.getId();
            options.id = id;
            return options;
        }
        return component;
    });