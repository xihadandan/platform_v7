define(["ui_component", "constant", "commons", "server", "formBuilder", "appContext", "design_commons"], function (
    ui_component, constant, commons, server, formBuilder, appContext, designCommons) {
    var StringUtils = commons.StringUtils;
    var StringBuilder = commons.StringBuilder;
    var collectClass = "w-configurer-option";
    var component = $.ui.component.BaseComponent();
    component.prototype.create = function () {
        var _self = this;
        var options = _self.options;
        var $element = $(_self.element);
    }

    // 返回定义的HTML
    component.prototype.toHtml = function () {
        var _self = this;
        var options = _self.options;
        var configuration = options.configuration || {};
        var children = _self.getChildren();
        var id = _self.getId();
        var html = new StringBuilder();
        html.appendFormat('<div id="{0}" class="ui-wPersonalDocument">', id);
        html.appendFormat('<div class="row personal-document">');
        html.appendFormat('<div class="col-xs-2 personal-document-nav"></div>');
        html.appendFormat('<div class="col-xs-10 personal-document-view">');
        if (children != null) {
            $.each(children, function (i) {
                var child = this;
                html.appendFormat(child.toHtml.call(child));
            });
        }
        html.appendFormat('</div>');
        html.appendFormat('</div>');
        html.appendFormat('</div>');
        return html;
    }

    // 使用属性配置器
    component.prototype.usePropertyConfigurer = function () {
        return true;
    }
    var clearInputValue = function ($container) {
        $container.find(".w-configurer-option").each(function () {
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
    var nameSource = null;
    var loadDyformFields = function () {
        if (!nameSource) {
            var source = [];
            var formUuid = $("#formUuid").val();
            server.JDS.call({
                service: "dataManagementViewerComponentService.getDyformFieldDefinitionsByUuid",
                data: [formUuid],
                async: false,
                success: function (result) {
                    nameSource = $.map(result.data, function (data) {
                        return {
                            value: data.displayName,
                            text: data.displayName
                        };
                    });
                }
            });
        }
        return nameSource;
    };
    var loadOperator = function () {
        var operatorSource = [];
        server.JDS.call({
            service: "viewComponentService.getQueryOperators",
            async: false,
            success: function (result) {
                if (result.msg == 'success') {
                    operatorSource = result.data;
                }
            }
        });
        return operatorSource;
    };
    // 验证配置信息的合法性
    var validateConfiguration = function (configuration) {
        // 单据管理按钮名称、编号不能为空
        if (configuration.document && configuration.document.buttons) {
            var buttons = configuration.document.buttons;
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
        }
        return true;
    };
    // 返回属性配置器
    component.prototype.getPropertyConfigurer = function () {
        var configurer = $.ui.component.BaseComponentConfigurer();
        configurer.prototype.onLoad = function ($container, options) {
            // 初始化页签项
            $("#widget_personal_document_tabs ul a", $container).on("click", function (e) {
                e.preventDefault();
                $(this).tab("show");
            })
            var _self = this.component;
            var configuration = $.extend(true, {}, options.configuration);
            this.initConfiguration(configuration, $container);
        };
        configurer.prototype.onOk = function ($container) {
            var _self = this;
            var component = _self.component;
            component.options.configuration = _self.collectConfiguration($container);
            return validateConfiguration(component.options.configuration);
        };

        configurer.prototype.initConfiguration = function (configuration, $container) {
            // 基本信息
            this.initBaseInfo(configuration, $container);
            // 数据源
            this.initNavInfo(configuration, $container);
        };
        configurer.prototype.initBaseInfo = function (configuration, $container) {
            // 设置值
            designCommons.setElementValue(configuration, $container, "query");

            // 分类
            $("#categoryCode", $container).wSelect2({
                serviceName: "dataDictionaryService",
                params: {
                    type: "MODULE_CATEGORY"
                },
                labelField: "categoryName",
                valueField: "categoryCode",
                remoteSearch: false
            });

            // 产品集成信息-树设置
            $("#belongToFolderUuid").wCommonComboTree({
                service: "fileManagerComponentService.getFolderTree",
                // serviceParams : "-1",
                width: "100%",
                multiSelect: false, // 是否多选
                parentSelect: true, // 父节点选择有效，默认无效
                onAfterSetValue: function (event, self, value) {
                    if (self.options && self.options.valueNodes && self.options.valueNodes.length == 1) {
                        $("#belongToFolderName").val(self.options.valueNodes[0].name);
                    } else {
                        $("#belongToFolderName").val("");
                    }
                }
            });
        };
        configurer.prototype.initNavInfo = function (configuration, $container) {
            var _self = this;
            var nav = configuration.nav || [];
            // 设置值
            designCommons.setElementValue(nav, $container);
            var appPageUuid = _self.component.pageDesigner.getPageUuid();
            var system = appContext.getCurrentUserAppData().getSystem();
            var productUuid = system.productUuid;

            // 导航
            var $element = $(".nav-menuItems", $container);
            var menuItems = nav || [];
            // 填充默认导航
            fillDefaultNavs(menuItems);
            buildMenuItemBootstrapTable($element, "nav-menuItems", menuItems, productUuid);
        };

        // 默认导航：主题设置，个人信息，我的消息
        function fillDefaultNavs(menuItems) {
            var defaultNavs = [{
                uuid: "latest_document",
                text: "最新文档",
                icon: {}
            }, {
                uuid: "recent_visit",
                text: "最近访问",
                icon: {}
            }, {
                uuid: "my_share",
                text: "我的分享",
                icon: {}
            }, {
                uuid: "share_with_me",
                text: "与我分享",
                icon: {}
            }, {
                uuid: "my_folder",
                text: "我的文件夹",
                icon: {}
            }, {
                uuid: "my_tags",
                text: "我的标签",
                icon: {}
            }, {
                uuid: "recycle_bin",
                text: "回收站",
                icon: {}
            }];
            // 判断现在有导航项是否存在默认的导航，没有全部放入
            var hasDefaultNavs = false;
            $.each(menuItems, function (i) {
                var menuItem = this;
                $.each(defaultNavs, function (j) {
                    if (menuItem.uuid = this.uuid) {
                        hasDefaultNavs = true;
                    }
                });
            });
            if (hasDefaultNavs != true) {
                $.each(defaultNavs, function (i) {
                    menuItems.push(this);
                });
            }
        }

        function buildMenuItemBootstrapTable($element, name, menuItems, productUuid) {
            formBuilder.bootstrapTable.build({
                container: $element,
                name: name,
                ediableNest: true,
                table: {
                    data: menuItems,
                    striped: true,
                    idField: "uuid",
                    onEditableSave: function (field, row, oldValue, $el) {
                        var $tableSubNavMenuItemsInfo = $("#table_nav-menuItems_info", $element);
                        if (field == 'defaultSelected' && row[field] == '1') {
                            var data = $tableSubNavMenuItemsInfo.bootstrapTable("getData");
                            $.each(data, function (index, rowData) {
                                if (row != rowData) {
                                    rowData.defaultSelected = 0;
                                    $tableSubNavMenuItemsInfo.bootstrapTable("updateRow", index, rowData);
                                }
                            });
                        }
                    },
                    columns: [{
                        field: "checked",
                        formatter: designCommons.checkedFormat,
                        checkbox: true
                    }, {
                        field: "uuid",
                        title: "UUID",
                        visible: false,
                        editable: {
                            type: "text",
                            showbuttons: false,
                            onblur: "submit",
                            mode: "inline"
                        }
                    }, {
                        field: "text",
                        title: "名称",
                        editable: {
                            type: "text",
                            mode: "inline",
                            showbuttons: false,
                            onblur: "submit"
                        }
                    }, {
                        field: "icon",
                        title: "图标",
                        editable: {
                            onblur: "cancel",
                            type: "wCustomForm",
                            placement: "bottom",
                            savenochange: true,
                            iconSelectTypes: [3],
                            value2input: designCommons.bootstrapTable.icon.value2input,
                            input2value: designCommons.bootstrapTable.icon.input2value,
                            value2display: designCommons.bootstrapTable.icon.value2display,
                            value2html: designCommons.bootstrapTable.icon.value2html
                        }
                    }, {
                        field: "hidden",
                        title: "是否隐藏",
                        editable: {
                            type: "select",
                            mode: "inline",
                            onblur: "submit",
                            showbuttons: false,
                            source: [{
                                value: "-1",
                                text: ""
                            }, {
                                value: "0",
                                text: "显示"
                            }, {
                                value: "1",
                                text: "隐藏"
                            }]
                        }
                    }, {
                        field: "badge",
                        visible: false,
                        title: "徽章",
                        editable: {
                            onblur: "cancel",
                            type: "wCustomForm",
                            placement: "bottom",
                            savenochange: true,
                            value2input: designCommons.bootstrapTable.badge.value2input(),
                            value2display: designCommons.bootstrapTable.badge.value2display
                        }
                    }, {
                        field: "defaultSelected",
                        title: "默认选中",
                        editable: {
                            type: "select",
                            mode: "inline",
                            onblur: "submit",
                            showbuttons: false,
                            source: [{
                                value: "-1",
                                text: ""
                            }, {
                                value: '1',
                                text: "是"
                            }, {
                                value: '0',
                                text: "否"
                            }]
                        }
                    }, {
                        field: "eventType",
                        title: "事件类型",
                        editable: {
                            type: "select",
                            mode: "inline",
                            onblur: "submit",
                            showbuttons: false,
                            source: function () {
                                var eventTypes = constant.EVENT_TYPE
                                var types = [{
                                    value: "-1",
                                    text: ""
                                }];
                                var etes = $.map(eventTypes, function (eventType) {
                                    return {
                                        value: eventType.value,
                                        text: eventType.name
                                    }
                                });
                                types = types.concat(etes);
                                return types;
                            }
                        }
                    }, {
                        field: "target",
                        title: "目标位置",
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
                        field: "eventHandler",
                        title: "事件处理",
                        width: 150,
                        editable: {
                            type: "wCommonComboTree",
                            mode: "inline",
                            showbuttons: false,
                            onblur: "submit",
                            otherProperties: {
                                'type': 'data.type',
                                'path': 'data.path'
                            },
                            otherPropertyPath: "data",
                            wCommonComboTree: {
                                service: "appProductIntegrationMgr.getTree",
                                serviceParams: [productUuid],
                                multiSelect: false, // 是否多选
                                parentSelect: true
                                // 父节点选择有效，默认无效
                            }
                        }
                    }]
                }
            });
        }

        configurer.prototype.collectConfiguration = function ($container) {
            var configuration = {};
            // 基本信息
            this.collectBaseInfo(configuration, $container);
            // 数据源
            this.collectNavInfo(configuration, $container);
            return $.extend({}, configuration);
        }
        configurer.prototype.collectBaseInfo = function (configuration, $container) {
            var $form = $("#widget_personal_document_tabs_base_info", $container);
            var opt = designCommons.collectConfigurerData($form, collectClass);
            opt.showBreadcrumbNav = Boolean(opt.showBreadcrumbNav);
            $.extend(configuration, opt);
        };
        configurer.prototype.collectNavInfo = function (configuration, $container) {
            var $form = $("#widget_personal_document_tabs_nav_info", $container);
            var opt = designCommons.collectConfigurerData($form, collectClass);
            // 导航
            var $tableNavMenuItemsInfo = $("#table_nav-menuItems_info", $container);
            var menuItems = $tableNavMenuItemsInfo.bootstrapTable("getData");
            opt.nav = menuItems;
            configuration.nav = configuration.nav || {};
            $.extend(configuration, opt);
        };
        return configurer;
    }

    // 返回组件定义
    component.prototype.getDefinitionJson = function () {
        var _self = this;
        var definitionJson = _self.options;
        definitionJson.id = _self.getId();
        definitionJson.items = [];
        var children = _self.getChildren();
        $.each(children, function (i) {
            var child = this;
            definitionJson.items.push(child.getDefinitionJson());
        });
        return definitionJson;
    }

    return component;
});