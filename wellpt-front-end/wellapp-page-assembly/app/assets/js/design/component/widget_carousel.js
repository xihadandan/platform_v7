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

                $("#widget_carousel_tabs ul a", $container).on("click", function(e) {
                    e.preventDefault();
                    $(this).tab("show");
                });
                //初始化基本信息
                this.initBaseInfo(configuration, $container);

                //初始化手风琴子项
                this.initCarouselInfo(configuration, $container);
            };

            configurerPrototype.onOk = function($container) {
                if (this.component.isReferenceWidget()) {
                    return;
                }
                var _self = this;
                var opt = designCommons.collectConfigurerData($("#widget_carousel_base_info", $container),
                    collectClass);
                var nodes = _self.carouselTree.getNodes();
                opt.tabs = [];
                for (var i = 0; i < nodes.length; i++) {
                    opt.tabs.push(nodes[i].data);
                }
                var requeryFields = ['carousel_name','carousel_height'];
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
                        dependencyFilter: "CarouselWidgetDevelopment"
                    },
                    labelField: "jsModuleName",
                    valueField: "jsModule",
                    remoteSearch: false,
                    multiple: true
                });
                $('#jsModuleName',$container).val(configuration.jsModuleName);
                $('#jsModule',$container).val(configuration.jsModule);
                $('#carousel_name', $container).val(configuration.carousel_name);
                $('#carousel_height', $container).val(configuration.carousel_height);
                $('#carousel_interval', $container).val(configuration.carousel_interval);
                $('#customClass', $container).val(configuration.customClass);
            };

            configurerPrototype.initCarouselInfo = function(configuration, $container) {
                var _self = this;
                var tabs = configuration.tabs ? configuration.tabs : [];
                var system = appContext.getCurrentUserAppData().getSystem();
                var productUuid = system.productUuid;
                var $tabsForm = $('#widget_carousel_form');
                var carouselTreeId = "widget_carousel_tree";
                var $carouselTree = $("#" + carouselTreeId);
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
                            clearCarouselTree();
                            for (var i in data) {
                                var value = data[i];
                                if (typeof value === 'object') {
                                    for (var j in value) {
                                        $('#carouselTree_' + i + '_' + j, $tabsForm).val(value[j]);
                                    }
                                } else {
                                    console.log(i,value);
                                    $('#carouselTree_' + i, $tabsForm).val(value);
                                    if(i === 'filePath') {
                                        $('#carouselTree_fileImage').attr('src',value)
                                    }
                                }
                            }
                            $("#carouselTree_viewId", $tabsForm).trigger("change");
                            $("#carouselTree_eventHandler_name", $tabsForm).wCommonComboTree({
                                value: data.eventHandler.id
                            });
                            $("#carouselTree_eventHandler_name", $tabsForm).AppEvent('setValue', data.eventHandler.id);
                        }
                    }
                };

                function clearCarouselTree() {
                    $('input[type="text"]', $tabsForm).each(function () {
                        $(this).val('').trigger('change');
                    });
                    $('input[type="hidden"]', $tabsForm).each(function () {
                        $(this).val('').trigger('change');
                    });

                    $('input[type="checkbox"]', $tabsForm).each(function () {
                        $(this).prop("checked", false).trigger('change');
                    });
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

                var zTree = $.fn.zTree.init($carouselTree, treeSetting, zNodes);
                _self.carouselTree = zTree;
                // 添加
                $("#widget_carousel_add").on("click", function () {
                    clearCarouselTree();
                    var uuid = UUID.createUUID();
                    $("#carouselTree_uuid", $tabsForm).val(uuid);
                    $("#carouselTree_viewId", $tabsForm).trigger("change");
                    $("#carouselTree_filePath").val('');
                    $("#carouselTree_fileImage").attr('src','');
                    $("#carouselTree_caption").val('');
                });

                // 删除
                $("#widget_carousel_remove").on("click", function () {
                    appModal.confirm("确认要删除吗?", function (result) {
                        if (result) {
                            clearCarouselTree();
                            var selectedNode = getSelectedNode();
                            var data = selectedNode.data;
                            zTree.removeNode(selectedNode);

                            for (var i = 0; i < tabs.length; i++) {
                                if (tabs[i].uuid == data.uuid) {
                                    tabs.splice(i, 1);
                                    return;
                                }
                            }
                            $("#carouselTree_uuid", $tabsForm).val(UUID.createUUID());
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

                $("#carouselTree_eventHandler_name", $tabsForm).AppEvent({
                    ztree:{params:[productUuid]},
                    okCallback:function($el,data){
                        if(data) {
                            $('#carouselTree_eventHandler_id', $tabsForm).val(data.id);
                            $('#carouselTree_eventHandler_type', $tabsForm).val(data.data.type);
                            $('#carouselTree_eventHandler_path', $tabsForm).val(data.data.path);
                        }
                    }
                });

                $("#carouselFileImageSelectBtn", $container).on("click", function() {
                    $.WCommonPictureLib.show({
                        selectTypes : [ 1, 2 ],
                        initPrevImg : $("#carouselTree_filePath", $container).val(),
                        confirm : function(data) {
                            var pictureFilePath = data.filePaths;
                            if (StringUtils.isBlank(pictureFilePath)) {
                                return;
                            }
                            $("#carouselTree_filePath", $container).val(pictureFilePath);
                            $("#carouselTree_fileImage", $container).show();
                            $("#carouselTree_fileImage", $container).attr("src", ctx + pictureFilePath);
                        }
                    });
                });
                $("#carouselFileImageRemoveBtn", $container).on("click", function() {
                    $("#carouselTree_filePath", $container).val("");
                    $("#carouselTree_fileImage", $container).removeAttr("src");
                });

                // 保存
                $("#carouselTree_save").on("click", function () {
                    var name = $('#carouselTree_name').val();
                    if(StringUtils.isBlank(name)) {
                        appModal.warning("名称不能为空！");
                        return;
                    }

                    var data = {};

                    $('#widget_carousel_form input[type="text"]').each(function () {
                        var $this = $(this);
                        var id = $this.attr('id');
                        var fields = id.split('_');
                        renderData(data,fields,$this);
                    });

                    $('#widget_carousel_form input[type="hidden"]').each(function () {
                        var $this = $(this);
                        var id = $this.attr('id');
                        var fields = id.split('_');
                        renderData(data,fields,$this);
                    });

                    $('#widget_carousel_form textarea').each(function () {
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

                    for (var i = 0; i < tabs.length; i++) {
                        if (tabs[i].uuid == $('#carouselTree_uuid').val()) {
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