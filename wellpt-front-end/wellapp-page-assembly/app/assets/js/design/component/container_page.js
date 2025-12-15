define([ "ui_component", "commons", "formBuilder", "appContext", "design_commons" ], function(ui_component, commons,
        formBuilder, appContext, designCommons) {
    var StringUtils = commons.StringUtils;
    var component = $.ui.component.BaseComponent();
    // 布局数据有效性检验
    var validateLayouts = function(configuration) {
        var layouts = configuration.portal.layouts;
        for (var i = 0; i < layouts.length; i++) {
            if (StringUtils.isBlank(layouts[i].text)) {
                appModal.error("布局名称不能为空！");
                return false;
            }
            if (StringUtils.isBlank(layouts[i].layout)) {
                appModal.error("布局不能为空！");
                return false;
            }
        }
        return true;
    }
    // 应用数据有效性检验
    var validateApps = function(configuration) {
        var apps = configuration.portal.apps;
        for (var i = 0; i < apps.length; i++) {
            if (StringUtils.isBlank(apps[i].text)) {
                appModal.error("应用名称不能为空！");
                return false;
            }
        }
        return true;
    }

    component.prototype.create = function() {
        var _self = this;
        var options = _self.options;
        var $element = $(_self.element);
        _self.pageDesigner.sortable(_self, $element, $element);

        // 设置页面容器
        if (_self.pageDesigner.supportsWysiwyg()) {
            var previewOptions = $.extend(true, {}, options);
            appContext.require([ previewOptions.wtype ], function() {
                previewOptions.items = [];
                // 初始化页面容器
                $("body")[previewOptions.wtype]({
                    widgetDefinition : previewOptions,
                    onCreated : function() {
                        appContext.setPageContainer(this);
                    }
                });
                // 初始化容器结点
                _self.createChildren();
            });
        } else {
            // 初始化容器结点
            _self.createChildren();
        }
    }

    component.prototype.createChildren = function() {
        var _self = this;
        var options = _self.options;
        var $element = $(_self.element);
        // 初始化容器结点
        if (options.items != null) {
            $.each(options.items, function(i) {
                var item = this;
                var $draggable = _self.pageDesigner.createDraggableByDefinitionJson(item);
                _self.pageDesigner.drop(_self, $element, $draggable, item);
            });
        }
    }

    // 使用属性配置器
    component.prototype.usePropertyConfigurer = function() {
        return true;
    }

    // 返回属性配置器
    component.prototype.getPropertyConfigurer = function() {
        var collectClass = "w-configurer-option";
        var configurerPrototype = {};
        configurerPrototype.getTemplateUrl = function() {
            // 调用父类提交方法
            var wtype = this._superApply(arguments);
            wtype = wtype.replace("_page", "_portal");
            return wtype;
        };
        configurerPrototype.onLoad = function($container, options) {
            var _self = this;
            var $dialog = $container.closest(".modal-dialog");
            // 设置弹出框标题
            $dialog.find(".modal-title").text("门户设置");
            // 设置弹出框按钮
            $dialog.find(".modal-footer button").hide();
            $dialog.find(".modal-footer button[data-bb-handler='confirm']").show();
            $dialog.find(".modal-footer button[data-bb-handler='cancel']").show();
            // 初始化页签项
            $("#container_portal_tabs ul a", $container).on("click", function(e) {
                e.preventDefault();
                $(this).tab("show");
            })
            var configuration = $.extend(true, {}, options.configuration);
            _self.initPortalConfiguration(configuration.portal || {}, $container);
        };
        configurerPrototype.onOk = function($container) {
            var _self = this;
            var configuration = _self.component.options.configuration || {};
            configuration.portal = _self.collectPortalConfiguration($container);
            _self.component.options.configuration = configuration;
            // 布局数据有效性检验
            if (!validateLayouts(configuration)) {
                return false;
            }
            // 应用数据有效性检验
            if (!validateApps(configuration)) {
                return false;
            }
        };
        // 初始化配置信息
        configurerPrototype.initPortalConfiguration = function(configuration, $container) {
            var _self = this;
            // 门户基本信息
            _self.initPortalBaseInfo(configuration, $container);
            // 门户内置应用设置
            _self.initPortalBuildinAppsInfo(configuration, $container);
            // 门户布局设置
            _self.initPortalLayoutsInfo(configuration, $container);
            // 门户组件应用
            _self.initPortalAppsInfo(configuration, $container);
        };
        // 门户基本信息
        configurerPrototype.initPortalBaseInfo = function(configuration, $container) {
            // 设置值
            designCommons.setElementValue(configuration, $container);

            var allThemes = [];
            JDS.call({
                service : "appContextService.getAllThemes",
                async : false,
                success : function(result) {
                    allThemes = result.data;
                }
            });
            // 分类
            $("#themes", $container).wSelect2({
                data : $.map(allThemes, function(theme) {
                    return {
                        id : theme.id,
                        text : theme.name
                    }
                }),
                labelField : "themeNames",
                valueField : "themes",
                multiple : true,
                remoteSearch : false
            });
            $("#themes", $container).trigger("change");
        }
        // 门户内置应用设置
        configurerPrototype.initPortalBuildinAppsInfo = function(configuration, $container) {
            var _self = this;
            var $element = $(".buildinApps", $container);
            var name = "buildinApps";
            var buildinApps = configuration.buildinApps || [];
            var buildinAppsDatas = [];
            var pageUuid = _self.component.pageDesigner.getPageUuid();
            JDS.call({
                service : "appPageDesignerFacadeService.getWidgetsByPageUuid",
                data : [ pageUuid ],
                async : false,
                success : function(result) {
                    buildinAppsDatas = result.data;
                    $.each(buildinAppsDatas, function(i, widgetApp) {
                        widgetApp.widgetId = widgetApp.id;
                        widgetApp.remark = "";
                        delete widgetApp.id;
                    });
                }
            });
            // 数据合并更新处理
            $.each(buildinApps, function(i, buildinApp) {
                $.each(buildinAppsDatas, function(i, app) {
                    if (buildinApp.widgetId === app.widgetId) {
                        app.buildin = buildinApp.buildin;
                        app.remark = buildinApp.remark;
                    }
                });
            });
            buildinApps = buildinAppsDatas;
            formBuilder.bootstrapTable.build({
                container : $element,
                name : name,
                toolbar : false,
                ediableNest : true,
                table : {
                    data : buildinApps,
                    striped : true,
                    idField : "uuid",
                    columns : [ {
                        field : "checked",
                        formatter : designCommons.checkedFormat,
                        checkbox : true
                    }, {
                        field : "uuid",
                        title : "UUID",
                        visible : false,
                        editable : {
                            type : "text",
                            showbuttons : false,
                            onblur : "submit",
                            mode : "inline"
                        }
                    }, {
                        field : "text",
                        title : "名称"
                    }, {
                        field : "buildin",
                        title : "内置应用",
                        formatter : function(value, row, index) {
                            return designCommons.bootstrapTable.checkbox.formatter(value, "buildin");
                        },
                        events : designCommons.bootstrapTable.checkbox.events
                    }, {
                        field : "widgetId",
                        title : "组件ID",
                        visible : false
                    }, {
                        field : "remark",
                        title : "备注",
                        width : "250px",
                        editable : {
                            type : "text",
                            mode : "inline",
                            showbuttons : false,
                            onblur : "submit"
                        }
                    } ]
                }
            });
        }
        // 门户布局设置
        configurerPrototype.initPortalLayoutsInfo = function(configuration, $container) {
            var $element = $(".layouts", $container);
            var name = "layouts";
            var layouts = configuration.layouts || [];
            var layoutDatas = [];
            JDS.call({
                service : "appPageDesignerFacadeService.getLayouts",
                data : [ WebApp.containerDefaults.wtype ],
                async : false,
                success : function(result) {
                    layoutDatas = result.data;
                }
            });
            formBuilder.bootstrapTable.build({
                container : $element,
                name : name,
                ediableNest : true,
                table : {
                    data : layouts,
                    striped : true,
                    idField : "uuid",
                    onEditableSave : function(field, row, oldValue, $el) {
                        // 选择布局时，名称为空，设置名称为选择的布局名称
                        if (field == "layout" && StringUtils.isBlank(row.text)) {
                            $.each(layoutDatas, function(index, layoutData) {
                                if (layoutData.id == row.layout) {
                                    row.text = layoutData.text;
                                }
                            });
                            var $tableLayoutsInfo = $("#table_layouts_info", $container);
                            var dataList = $tableLayoutsInfo.bootstrapTable("getData");
                            $.each(dataList, function(index, rowData) {
                                if (row == rowData) {
                                    $tableLayoutsInfo.bootstrapTable("updateRow", index, rowData);
                                }
                            });
                        }
                    },
                    columns : [ {
                        field : "checked",
                        formatter : designCommons.checkedFormat,
                        checkbox : true
                    }, {
                        field : "uuid",
                        title : "UUID",
                        visible : false,
                        editable : {
                            type : "text",
                            showbuttons : false,
                            onblur : "submit",
                            mode : "inline"
                        }
                    }, {
                        field : "text",
                        title : "名称",
                        width : "25%",
                        editable : {
                            type : "text",
                            mode : "inline",
                            showbuttons : false,
                            onblur : "submit",
                            savenochange : true,
                            validate : function(value) {
                                if (StringUtils.isBlank(value)) {
                                    return '请输入名称!';
                                }
                            }
                        }
                    }, {
                        field : "icon",
                        title : "缩略图",
                        width : "25%",
                        editable : {
                            onblur : "cancel",
                            type : "wCustomForm",
                            placement : "right",
                            savenochange : true,
                            iconSelectTypes : [ 2 ],
                            value2input : designCommons.bootstrapTable.icon.value2input,
                            input2value : designCommons.bootstrapTable.icon.input2value,
                            value2display : designCommons.bootstrapTable.icon.value2display,
                            value2html : designCommons.bootstrapTable.icon.value2html
                        }
                    }, {
                        field : "layout",
                        title : "布局",
                        editable : {
                            type : "select",
                            mode : "inline",
                            onblur : "submit",
                            showbuttons : false,
                            savenochange : true,
                            source : $.map(layoutDatas, function(layoutInfo) {
                                return {
                                    text : layoutInfo.text,
                                    value : layoutInfo.id
                                };
                            }),
                            validate : function(value) {
                                if (StringUtils.isBlank(value)) {
                                    return '请选择布局!';
                                }
                            }
                        }
                    }, {
                        field : "remark",
                        title : "备注",
                        editable : {
                            type : "text",
                            mode : "inline",
                            showbuttons : false,
                            onblur : "submit"
                        }
                    } ]
                }
            });
        }
        // 门户组件应用设置
        configurerPrototype.initPortalAppsInfo = function(configuration, $container) {
            var system = appContext.getCurrentUserAppData().getSystem();
            var productUuid = system.productUuid;
            var $element = $(".apps", $container);
            var name = "apps";
            var apps = configuration.apps || [];
            formBuilder.bootstrapTable.build({
                container : $element,
                name : name,
                ediableNest : true,
                table : {
                    data : apps,
                    striped : true,
                    idField : "uuid",
                    columns : [ {
                        field : "checked",
                        formatter : designCommons.checkedFormat,
                        checkbox : true
                    }, {
                        field : "uuid",
                        title : "UUID",
                        visible : false,
                        editable : {
                            type : "text",
                            showbuttons : false,
                            onblur : "submit",
                            mode : "inline"
                        }
                    }, {
                        field : "text",
                        title : "名称",
                        width : "15%",
                        editable : {
                            type : "text",
                            mode : "inline",
                            showbuttons : false,
                            onblur : "submit",
                            savenochange : true,
                            validate : function(value) {
                                if (StringUtils.isBlank(value)) {
                                    return '请输入名称!';
                                }
                            }
                        }
                    }, {
                        field : "icon",
                        title : "图标",
                        width : "15%",
                        editable : {
                            onblur : "cancel",
                            type : "wCustomForm",
                            placement : "right",
                            savenochange : true,
                            iconSelectTypes : [ 3 ],
                            value2input : designCommons.bootstrapTable.icon.value2input,
                            input2value : designCommons.bootstrapTable.icon.input2value,
                            value2display : designCommons.bootstrapTable.icon.value2display,
                            value2html : designCommons.bootstrapTable.icon.value2html
                        }
                    }, {
                        field : "eventHandler",
                        title : "组件",
                        width : "50%",
                        editable : {
                            type : "wCommonComboTree",
                            mode : "modal",
                            showbuttons : false,
                            onblur : "submit",
                            otherProperties : {
                                'type' : 'data.type',
                                'path' : 'data.path'
                            },
                            otherPropertyPath : "data",
                            wCommonComboTree : {
                                inlineView : true,
                                service : "appProductIntegrationMgr.getTreeWithPtProductAndFunctionType",
                                serviceParams : [ productUuid, [ "appWidgetDefinition" ] ],
                                multiSelect : false, // 是否多选
                                parentSelect : false,// 父节点选择有效，默认无效
                                beforeTreeExpand : function(treeId, treeNode) {
                                    // 非组件功能子结点，不可选择
                                    $.each(treeNode.children, function(i, child) {
                                        console.log(child.name);
                                        if (StringUtils.isBlank(child.name) || !child.name.startsWith("功能: 组件定义_")) {
                                            child.nocheck = true;
                                        }
                                    });
                                }
                            }
                        }
                    }, {
                        field : "remark",
                        title : "备注",
                        editable : {
                            type : "text",
                            mode : "inline",
                            showbuttons : false,
                            onblur : "submit"
                        }
                    } ]
                }
            });
        }

        // 收集配置信息
        configurerPrototype.collectPortalConfiguration = function($container) {
            var _self = this;
            var configuration = {};
            // 收集门户基本信息
            _self.collectPortalBaseInfo(configuration, $container);
            // 收集门户内置应用信息
            _self.collectPortalBuildinAppsInfo(configuration, $container);
            // 收集门户布局信息
            _self.collectPortalLayoutsInfo(configuration, $container);
            // 收集门户组件应用信息
            _self.collectPortalAppsInfo(configuration, $container);
            return $.extend({}, configuration);
        };
        // 收集门户基本信息
        configurerPrototype.collectPortalBaseInfo = function(configuration, $container) {
            var $form = $("#container_portal_tabs_base_info", $container);
            var opt = designCommons.collectConfigurerData($form, collectClass);
            $.extend(configuration, opt);
        };
        // 收集门户内置应用信息
        configurerPrototype.collectPortalBuildinAppsInfo = function(configuration, $container) {
            // 门户内置应用
            var $tableBuildinAppsInfo = $("#table_buildinApps_info", $container);
            var buildinApps = $tableBuildinAppsInfo.bootstrapTable("getData");
            $.each(buildinApps, function(i, buildinApp) {
                buildinApp.buildin = buildinApp.buildin == "1";
            });
            configuration.buildinApps = buildinApps;
        }
        // 收集门户布局信息
        configurerPrototype.collectPortalLayoutsInfo = function(configuration, $container) {
            // 布局
            var $tableLayoutsInfo = $("#table_layouts_info", $container);
            var layouts = $tableLayoutsInfo.bootstrapTable("getData");
            configuration.layouts = layouts;
        }
        // 收集门户组件应用信息
        configurerPrototype.collectPortalAppsInfo = function(configuration, $container) {
            // 组件应用
            var $tableAppsInfo = $("#table_apps_info", $container);
            var apps = $tableAppsInfo.bootstrapTable("getData");
            configuration.apps = apps;
        }

        var configurer = $.ui.component.BaseComponentConfigurer(configurerPrototype);
        return configurer;
    }

    component.prototype.toHtml = function() {
        var options = this.options;
        var children = this.getChildren();
        var id = this.getId();
        var html = "<div id='" + id + "' class='web-app-container container-fluid'>";
        if (children != null) {
            $.each(children, function(i) {
                var child = this;
                var childHtml = child.toHtml.call(child);
                html += childHtml;
            });
        }
        html += "</div>";
        return html;
    }
    component.prototype.getDefinitionJson = function($element) {
        var definitionJson = this.options;
        definitionJson.id = this.getId();
        // definitionJson.title = "我的主页";
        definitionJson.items = [];
        var children = this.getChildren();
        $.each(children, function(i) {
            var child = this;
            definitionJson.items.push(child.getDefinitionJson());
        });
        return definitionJson;
    }
    return component;
});