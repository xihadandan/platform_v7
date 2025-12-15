define([ "ui_component","constant", "commons", "formBuilder", "appContext", "design_commons" ],
    function(ui_component,constant, commons,formBuilder, appContext, designCommons) {
        var UUID = commons.UUID;
        var StringUtils = commons.StringUtils;
        var StringBuilder = commons.StringBuilder;
        var component = $.ui.component.BaseComponent();


        function checkRequire (propertyNames, options, $container) {
            for (var i = 0; i < propertyNames.length; i++) {
                var propertyName = propertyNames[i];
                if (StringUtils.isBlank(options[propertyName])) {
                    var title = $("label[for='" + propertyName + "']", $container).text();
                    appModal.error(title.replace("*", "") + "不允许为空！");
                    return false;
                }
            }
            return true;
        }

        component.prototype.create = function() {

        };

        // 使用属性配置器
        component.prototype.usePropertyConfigurer = function() {
            return true;
        };

        // 返回属性配置器
        component.prototype.getPropertyConfigurer = function() {
            var collectClass = "w-configurer-option";
            var configurerPrototype = {};
            configurerPrototype.getTemplateUrl = function() {
                // 调用父类提交方法
                var wtype = this._superApply(arguments);
                return wtype;
            };
            configurerPrototype.onLoad = function($container, options) {
                var _self = this;
                var configuration = $.extend(true, {}, options.configuration);

                $("#widget_accordion_tabs ul a", $container).on("click", function(e) {
                    e.preventDefault();
                    $(this).tab("show");
                });
                $("#accordionTree_backgroundColor", $container).minicolors('create', options.minicolors || {});
                //初始化基本信息
                this.initBaseInfo(configuration, $container);

                //初始化手风琴子项
                this.initAccordionInfo(configuration, $container);
            };

            configurerPrototype.onOk = function($container) {
                if (this.component.isReferenceWidget()) {
                    return;
                }
                var _self = this;
                var opt = designCommons.collectConfigurerData($("#widget_accordion_base_info", $container),
                    collectClass);
                var nodes = _self.accordionTree.getNodes();
                opt.tabs = [];
                for (var i = 0; i < nodes.length; i++) {
                    opt.tabs.push(nodes[i].data);
                }
                var requeryFields = ['accordion_name','accordion_height'];
                if (!checkRequire(requeryFields, opt, $container)) {
                    return false;
                }
                this.component.options.configuration = $.extend({}, opt);
            };

            configurerPrototype.initBaseInfo = function(configuration, $container) {
                designCommons.setElementValue(configuration, $container, 'tabs');
                // 二开JS模块
                $("#jsModule", $container).wSelect2({
                    serviceName: "appJavaScriptModuleMgr",
                    params: {
                        dependencyFilter: "AccordionWidgetDevelopment"
                    },
                    labelField: "jsModuleName",
                    valueField: "jsModule",
                    remoteSearch: false,
                    multiple: true
                });
                $('#jsModuleName',$container).val(configuration.jsModuleName);
                $('#jsModule',$container).val(configuration.jsModule);
                $('#accordion_name', $container).val(configuration.accordion_name);
                $('#accordion_height', $container).val(configuration.accordion_height);
                $('input[type="radio"][name="accordion_type"][value="'+ configuration.accordion_type +'"]').attr('checked',true);
                $('#customClass', $container).val(configuration.customClass);
            };

            configurerPrototype.initAccordionInfo = function(configuration, $container) {
                var _self = this;
                var tabs = configuration.tabs ? configuration.tabs : [];
                var system = appContext.getCurrentUserAppData().getSystem();
                var productUuid = system.productUuid;
                var $tabsForm = $('#widget_accordion_form');
                var accordionTreeId = "widget_accordion_tree";
                var $accordionTree = $("#" + accordionTreeId);
                var treeSetting = {
                    edit: {
                        drag: {
                            autoExpandTrigger: true,
                            isCopy: false,
                            isMove: true,
                            prev: true,
                            inner: false,
                            next: true
                        },
                        enable: true,
                        showRemoveBtn: false,
                        showRenameBtn: false
                    },
                    view: {
                        dblClickExpand: false,
                        selectedMulti: false
                    },
                    data: {
                        simpleData: {
                            enable: false
                        }
                    },
                    callback: {
                        onClick: function (event, treeId, treeNode) {
                            var data = treeNode.data;
                            clearAccordionTree();
                            for (var i in data) {
                                var value = data[i];
                                if (typeof value === 'object') {
                                    for (var j in value) {
                                        $('#accordionTree_' + i + '_' + j, $tabsForm).val(value[j]);
                                    }
                                } else {
                                    $('#accordionTree_' + i, $tabsForm).val(value);
                                }
                            }
                            $("#accordionTree_backgroundColor", $tabsForm).minicolors('value', $('#accordionTree_backgroundColor', $tabsForm).val());
                            var iconClass = $("#accordionTreeIconSnap", $tabsForm).attr("iconClass");
                            $("#accordionTreeIconSnap", $tabsForm).removeClass(iconClass)
                                .addClass($('#accordionTree_icon_className').val())
                                .attr("iconClass", $('#accordionTree_icon_className').val());
                            $("#accordionTree_viewId", $tabsForm).trigger("change");
                            $("#accordionTree_BadgeType,#accordionTree_BadgeTypeCountJs," +
                                "#accordionTree_BadgeTypeCountDs,#accordionTree_BadgeTypeCountJsWBootstrapTable", $tabsForm).trigger('change');

                            $("#accordionTree_eventHandler_name", $tabsForm).wCommonComboTree({
                                value: data.eventHandler.id
                            });
                            $("#accordionTree_eventHandler_name", $tabsForm).AppEvent('setValue', data.eventHandler.id);

                            if ($('#accordionTree_isShowBadge').val() == '1') {
                                $('#accordionTree_isShowBadge').prop("checked", true).trigger("change");
                            } else {
                                $('#accordionTree_isShowBadge').prop("checked", false).trigger("change");
                            }
                        }
                    }
                };

                function clearAccordionTree() {
                    $('input[type="text"]', $tabsForm).each(function () {
                        $(this).val('').trigger('change');
                    });
                    $('input[type="hidden"]', $tabsForm).each(function () {
                        $(this).val('').trigger('change');
                    });

                    $('input[type="checkbox"]', $tabsForm).each(function () {
                        $(this).prop("checked", false).trigger('change');
                    });

                    var iconClass = $("#accordionTreeIconSnap", $tabsForm).attr("iconClass");
                    $("#accordionTreeIconSnap", $tabsForm).removeClass(iconClass).attr("iconClass", '');
                }
                var zNodes = [];
                for (var i = 0; i < tabs.length; i++) {
                    var _zNode = {
                        id: tabs[i].uuid,
                        name: tabs[i].name,
                        data: tabs[i]
                    };
                    zNodes.push(_zNode);
                }

                var zTree = $.fn.zTree.init($accordionTree, treeSetting, zNodes);
                _self.accordionTree = zTree;
                // 添加
                $("#widget_accordion_add").on("click", function () {
                    clearAccordionTree();
                    var uuid = UUID.createUUID();
                    $("#accordionTree_uuid", $tabsForm).val(uuid);
                    $("#accordionTree_viewId", $tabsForm).trigger("change");
                    $("#accordionTree_isShowBadge", $tabsForm).trigger("change");
                    $("#accordionTree_backgroundColor", $tabsForm).minicolors('value', {});
                });

                // 删除
                $("#widget_accordion_remove").on("click", function () {
                    appModal.confirm("确认要删除吗?", function (result) {
                        if (result) {
                            clearAccordionTree();
                            var selectedNode = getSelectedNode();
                            var data = selectedNode.data;
                            zTree.removeNode(selectedNode);

                            for (var i = 0; i < tabs.length; i++) {
                                if (tabs[i].uuid == data.uuid) {
                                    tabs.splice(i, 1);
                                    return;
                                }
                            }
                            $("#accordinTree_uuid", $tabsForm).val(UUID.createUUID());
                        }
                    });
                });

                var getSelectedNode = function () {
                    var selectedNodes = zTree.getSelectedNodes();
                    if (selectedNodes.length == 0) {
                        return null;
                    }
                    return selectedNodes[0];
                };

                $("#accordionTree_eventHandler_name", $tabsForm).AppEvent({
                    ztree:{params:[productUuid]},
                    okCallback:function($el,data){
                        if(data) {
                            $('#accordionTree_eventHandler_id', $tabsForm).val(data.id);
                            $('#accordionTree_eventHandler_type', $tabsForm).val(data.data.type);
                            $('#accordionTree_eventHandler_path', $tabsForm).val(data.data.path);
                        }
                    }
                });

                // 显示标签数量的视图组件选择控制
                $("#accordionTree_isShowBadge", $container).on("change", function () {
                    if (this.checked === true) {
                        $(".accordionTree_BadgeType_select", $tabsForm).show();
                        $("#accordionTree_BadgeType", $tabsForm).trigger('change');
                    } else {
                        $(".showBadge", $tabsForm).hide();
                    }
                });

                $("#accordionTree_BadgeType", $tabsForm).wSelect2({
                    valueField: "accordionTree_BadgeType",
                    remoteSearch: false,
                    data: [{id: 'countJs', text: '通过数量统计JS脚本'}, {id: 'datastore', text: '通过数据仓库获取数量'}]
                });

                $("#accordionTree_BadgeType", $tabsForm).on('change', function () {
                    $(".accordionTree_BadgeType", $tabsForm).hide();
                    var typeValue = $(this).val();
                    console.log(typeValue);
                    $(".accordionTree_BadgeType" + "_" + typeValue).show();
                });

                $("#accordionTree_BadgeTypeCountJsWBootstrapTable", $tabsForm).wSelect2({
                    serviceName: "appWidgetDefinitionMgr",
                    valueField:'accordionTree_BadgeTypeCountJsWBootstrapTable',
                    params: {
                        wtype: 'wBootstrapTable',
                        uniqueKey: 'id',
                        includeWidgetRef: "true"
                    },
                    remoteSearch: false
                });

                $("#accordionTree_BadgeTypeCountJs", $tabsForm).wSelect2({
                    serviceName: "appJavaScriptModuleMgr",
                    params: {
                        dependencyFilter: "GetCountBase"
                    },
                    valueField: 'accordionTree_BadgeTypeCountJs',
                    defaultBlank: true,
                    remoteSearch: false
                });

                $("#accordionTree_BadgeTypeCountJs", $tabsForm).on('change', function () {
                    $(".accordionTree_BadgeType_countJs[js]", $tabsForm).hide();
                    if ($(this).val() === 'BootstrapTableViewGetCount') {
                        $(".accordionTreeBootstrapTableViewGetCount", $tabsForm).show();
                    } else {
                        $(".accordionTreeBootstrapTableViewGetCount", $tabsForm).hide();
                    }
                });

                $("#accordionTree_BadgeTypeCountDs", $tabsForm).wSelect2({
                    serviceName: "viewComponentService",
                    queryMethod: "loadSelectData",
                    valueField: 'accordionTree_BadgeTypeCountDs',
                    defaultBlank: true,
                    remoteSearch: false,
                    params: {
                        piUuid: _self.component.pageDesigner.getPiUuid()
                    }
                });

                // 保存
                $("#accordionTree_save").on("click", function () {
                    var name = $('#accordionTree_name').val();
                    var background = $('#accordionTree_backgroundColor').val();
                    var accordionTree_eventHandler_name = $('#accordionTree_eventHandler_name').val();
                    if(StringUtils.isBlank(name)) {
                        appModal.warning("名称不能为空！");
                        return;
                    }
                    if(StringUtils.isBlank(background)) {
                        appModal.warning("背景颜色不能为空！");
                        return;
                    }
                    if(StringUtils.isBlank(accordionTree_eventHandler_name)) {
                        appModal.warning("请选择事件处理！");
                        return;
                    }


                    var data = {};

                    $('#widget_accordion_form input[type="text"]').each(function () {
                        var $this = $(this);
                        var id = $this.attr('id');
                        var fields = id.split('_');
                        renderData(data,fields,$this);
                    });

                    $('#widget_accordion_form input[type="hidden"]').each(function () {
                        var $this = $(this);
                        var id = $this.attr('id');
                        var fields = id.split('_');
                        renderData(data,fields,$this);
                    });
                    function renderData(data,fields,$this) {
                        switch (fields.length) {
                            case 1:
                                data[fields[0]] =$this.val();
                                break;
                            case 2:
                                data[fields[1]] =$this.val();
                                break;
                            case 3:
                                if (!data[fields[1]]) {
                                    data[fields[1]] = {};
                                }
                                data[fields[1]][fields[2]] = $this.val();
                                break;
                        }
                        return data;
                    }

                    data['isShowBadge'] = $('#accordionTree_isShowBadge').is(':checked') ? '1' : '0';

                    for (var i = 0; i < tabs.length; i++) {
                        if (tabs[i].uuid == $('#accordionTree_uuid').val()) {
                            tabs[i] = data;
                            var node = zTree.getNodesByParam("id", data.uuid, null)[0];
                            node.name = data.name;
                            node.data = data;
                            zTree.updateNode(node);
                            $.WCommonAlert("保存成功！");
                            return;
                        }
                    }

                    var zNode = {
                        id: data.uuid,
                        name: data.name,
                        data: data
                    };
                    tabs.push(data);
                    zTree.addNodes(null, zNode);
                    zTree.selectNode(zTree.getNodesByParam("id", data.uuid, null)[0]);
                    $.WCommonAlert("保存成功！");
                });
            };
            var configurer = $.ui.component.BaseComponentConfigurer(configurerPrototype);
            return configurer;
        };



        component.prototype.getDefinitionJson = function() {
            var options = this.options;
            var id = this.getId();
            options.id = id;
            return options;
        }

        return component;
    });