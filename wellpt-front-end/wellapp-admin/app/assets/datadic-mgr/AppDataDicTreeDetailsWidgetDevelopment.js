define(["constant", "commons", "server", "appModal", "AppPtMgrDetailsWidgetDevelopment", "AppPtMgrCommons", 'dataStoreBase', 'ztree'],
    function(constant, commons, server, appModal, AppPtMgrDetailsWidgetDevelopment, AppPtMgrCommons, DataStore, ztree) {
        // 平台管理_产品集成_数据字典树形_HTML组件二开
        var AppDataDicTreeDetailsWidgetDevelopment = function() {
            AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
        };

        // 接口方法
        commons.inherit(AppDataDicTreeDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
            // 组件初始化
            init: function() {
                var _self = this;

                _self._ztreeInit();
                // 绑定事件
                _self._bindEvents();


            },

            _ztreeInit: function() {
                var _self = this;
                //
                var dataDataDictionarySetting = {
                    async: {
                        enable: true,
                        contentType: "application/json",
                        url: ctx + "/json/data/services",
                        otherParam: {
                            "serviceName": "appDataDicFacadeService",
                            "methodName": "loadAppDataDicNodes",
                            "data": ["-1", _self._moduleId(), true],
                            "version": ''
                        },
                        type: "POST"
                    },
                    view: {
                        fontCss: function(treeId, treeNode) {
                            return treeNode.data ? {
                                'color': '#1ed215'
                            } : {};
                        }
                    },
                    edit: {
                        enable: true,
                        showRemoveBtn: false,
                        showRenameBtn: false,
                        drag: { isMove: true, prev: true, next: true, inner: false }
                    },
                    callback: {
                        beforeClick: function(treeId, treeNode) {
                            if (treeNode.id != null && treeNode.id != -1) {
                                // 查看详细
                                // getDataDictionary(treeNode.id);
                                _self.widget.trigger("AppDataDicTreeView.editTreeNode", {
                                    uuid: treeNode.id,
                                    ui: _self.widget,
                                    isRef: treeNode.data
                                });
                                //引用字典，则删除按钮不可用
                                $(".btn_class_btn_delete", _self.widget.element).prop('disabled', treeNode.data);
                            }
                            return true;
                        },
                        onAsyncSuccess: function(event, treeId, treeNode, msg) {
                            var nodes = zTree.getNodes();
                            // 默认展开第一个节点
                            if (nodes.length > 0) {
                                var node = nodes[0];
                                zTree.expandNode(node, true, false, false, true);
                            }


                        },
                        beforeDrop: function(treeId, treeNodes, targetNode, moveType, isCopy) {
                            if (treeNodes[0].data) {
                                return false; //被引用的不允许移动
                            }
                            if (treeNodes[0].level == 0) {
                                //console.log("不允许拖拽库节点");
                                return false; //不允许移动根节点
                            }
                            if (treeNodes[0].level != targetNode.level) {
                                return false; //只能同级间移动
                            }
                        },
                        onDrop: function(event, treeId, treeNodes, targetNode, moveType) {
                            _self._updateNodeSeq(event, treeId, treeNodes, targetNode, moveType);
                        },
                        onExpand: function() {
                            var $element = _self.getWidget().element;
                            $("#datadict_tree", $element).getNiceScroll().resize()
                        }
                    }
                };


                var zTree = $.fn.zTree.init($("#datadict_tree"), dataDataDictionarySetting);

                _self.$dataDicTree = zTree;

            },

            _updateNodeSeq: function(event, treeId, treeNodes, targetNode, moveType) {
                if (!targetNode || treeNodes[0].level != targetNode.level) {
                    return false; //只能同级间移动
                }
                if (treeNodes[0].data) {
                    return false;
                }
                server.JDS.call({
                    service: "dataDictionaryService.moveDataDicAfterOther",
                    data: [treeNodes[0].id, (function() {
                        var prev = treeNodes[0].getPreNode();
                        return prev ? prev.id : null;
                    })()],
                    version: '',
                    validate: true,
                    success: function(result) {}
                });
            },


            getDataDicTree: function() {
                return this.$dataDicTree;
            },

            _bindEvents: function() {
                var _self = this;
                var widget = _self.getWidget();
                var $container = $(widget.element);
                var pageContainer = _self.getPageContainer();
                pageContainer.off('AppDataDicTreeView.refreshDataDicZtree');
                pageContainer.on('AppDataDicTreeView.refreshDataDicZtree', function(e) {
                    var param = e.detail;
                    if (param.deleteChildren && param.deleteChildren.length) {
                        //删除子节点
                        var node = _self.getDataDicTree().getSelectedNodes()[0];
                        var deleteNodes = [];
                        for (var i = 0, l = node.children.length; i < l; i++) {
                            for (var j = 0; j < param.deleteChildren.length; j++) {
                                if (param.deleteChildren[j].uuid == node.children[i].id) {
                                    deleteNodes.push(node.children[i]);
                                }
                            }
                        }
                        for (var i = 0, l = deleteNodes.length; i < l; i++) {
                            _self.getDataDicTree().removeNode(deleteNodes[i]);
                        }

                    }
                    refreshZtree("datadict_tree", param.action, param.saveUuid, param.uuid, param.name);
                    _self.refreshTabBage();
                });

                $("#datadict_tree", $container).height(500).niceScroll({
                    cursorcolor: '#ccc'
                })

                if (!this.getWidgetParams().moduleId) {
                    $(".li_class_reference_datadic").remove();
                    $(".li_class_btn_cancel_ref").remove();
                }

                $(".btn_class_btn_add", $container).on('click', function() {
                    _self.widget.trigger("AppDataDicTreeView.editTreeNode", {
                        uuid: null,
                        ui: _self.widget,
                        isRef: false
                    });
                    return false;
                });

                $(".btn_class_btn_delete", $container).on('click', function() {
                    var selected = _self.getDataDicTree().getSelectedNodes();
                    if (selected.length == 1 && selected[0].id != -1) {
                        appModal.confirm("确定要删除数据字典[" + selected[0].name + "]吗？", function(result) {
                            if (result) {
                                server.JDS.call({
                                    service: "dataDictionaryService.remove",
                                    data: [selected[0].id],
                                    mask: true,
                                    version: '',
                                    success: function(result) {
                                        appModal.success("删除成功!");
                                        refreshZtree("datadict_tree", "delete");
                                        _self.refreshTabBage();
                                        _self.widget.trigger("AppDataDicTreeView.editTreeNode", {
                                            uuid: null,
                                            ui: _self.widget,
                                            isRef: false
                                        });
                                    }
                                });
                            }
                        });
                    } else {
                        appModal.error("请选择记录！");
                        return false;
                    }
                    return false;
                });


                $(".li_class_definition_export", $container).on('click', function() {
                    if (_self._isRefTreeNode()) {
                        return false;
                    }
                    _self.definition_export();
                    return false;
                });

                $(".li_class_definition_import", $container).on('click', function() {
                    _self.definition_import();
                    return false;
                });

                $(".li_class_reference_datadic", $container).on('click', function() {
                    if (_self._isRefTreeNode()) {
                        appModal.info('不能在引用数据字典上操作引用');
                        return false;
                    }
                    _self.showReferenceDialog();
                    return false;
                });

                $(".li_class_btn_cancel_ref", $container).on('click', function() {
                    if (!_self._isRefTreeNode()) {
                        appModal.info('数据字典非引用的不允许操作取消引用');
                        return false;
                    }
                    if (_self._getSelectedParentNode().data) { //父级也是引用的，则不允许在子节点上取消引用
                        var refTopNode = _self._getSelectedRefTopNode();
                        appModal.info('取消引用只能在引用数据字典的顶级节点[' + refTopNode.name + ']进行操作');
                        return false;
                    }
                    _self.cancelReference();
                    return false;
                });

                $(".btn-query", $container).on('click', function() {
                    var nodeName = $("input[type='search']", $container).val();
                    searchZtreeNode(0, "datadict_tree", nodeName, function(uuid) {
                        var node = _self._getNodeByUuid(uuid, null);
                        _self.widget.trigger("AppDataDicTreeView.editTreeNode", {
                            uuid: uuid,
                            ui: _self.widget,
                            ifRef: node ? node.data : false
                        });
                    });
                    $("#datadict_tree", $container).getNiceScroll().resize()
                    return false;
                });
                $("input[type='search']", $container).on("keyup", function(e) {
                    if (e.keyCode == 13) {
                        $(".btn-query", $container).trigger("click");
                    }
                })

                $(".btn-reset", $container).on('click', function() {
                    $("input[type='search']", $container).val('');
                });

            },

            _getNodeByUuid: function(uuid, parentNode) {
                return this.getDataDicTree().getNodesByParamFuzzy("uuid", uuid, parentNode);
            },

            refresh: function() {
                //刷新树
                this._ztreeInit();
                this.widget.element.find('.btn-reset').click();
            },

            refreshTabBage: function() {
                //刷新徽章
                var tabpanel = this.widget.element.parents('.active');
                if (tabpanel.length > 0) {
                    var id = tabpanel.attr('id');
                    id = id.substring(0, id.indexOf('-'));
                    $("#" + id).trigger(constant.WIDGET_EVENT.BadgeRefresh, {
                        targetTabName: '数据字典',
                    });
                }
                //return this.getWidget().refresh(this.options);
            },

            definition_import: function() {
                var _self = this;
                // 定义导入
                $.iexportData["import"]({
                    callback: function() {
                        _self._ztreeInit();
                        _self.refreshTabBage();
                    }
                });
            },

            _getSelectedRefTopNode: function(child) {
                if (!child) {
                    var selected = this.getDataDicTree().getSelectedNodes();
                    if (selected.length == 1 && selected[0].id != -1 && selected[0].data) {
                        return this._getSelectedRefTopNode(selected[0]);
                    }
                    return null;
                } else {
                    var parentNode = child.getParentNode();
                    if (parentNode.id != -1 && parentNode.data) {
                        return this._getSelectedRefTopNode(parentNode);
                    }
                    return child;
                }
            },

            _getSelectedParentNode: function() {
                var selected = this.getDataDicTree().getSelectedNodes();
                if (selected.length == 1 && selected[0].id != -1) {
                    return selected[0].getParentNode();
                }
                return {};
            },

            _isRefTreeNode: function() {
                var selected = this.getDataDicTree().getSelectedNodes();
                if (selected.length == 1 && selected[0].id != -1) {
                    return selected[0].data;
                }
                return false;
            },

            definition_export: function() {
                // 定义导出
                var selected = this.getDataDicTree().getSelectedNodes();
                if (selected.length == 1) {
                    $.iexportData["export"]({
                        uuid: selected[0].id,
                        type: 'dataDictionary'
                    });
                } else {
                    appModal.alert('请选择导出的数据字典定义！');
                }

            },

            refrenceDataDicDialogHtml: function() {
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
                    $("<div>", { "class": "col-sm-2 required" }).text('引用数据字典'),
                    $("<div>", { "class": "col-sm-10" }).append(
                        $("<ul>", { "id": "datadict_tree_ref", "class": "ztree" })
                    )
                );

                $div.append($row1, $row2);
                return $div[0].outerHTML;
            },
            showReferenceDialog: function() {
                //弹窗
                var _self = this;
                var $dialog;
                var dialogOpts = {
                    title: '引用数据字典',
                    message: _self.refrenceDataDicDialogHtml(),
                    buttons: {
                        confirm: {
                            label: "确定",
                            className: "btn-primary",
                            callback: function(result) {
                                var commitResult = false;

                                var treeObj = $.fn.zTree.getZTreeObj("datadict_tree_ref");
                                var refSelected = treeObj.getSelectedNodes();
                                if (refSelected.length == 0 || refSelected[0].id == -1) {
                                    return false;
                                }
                                var selected = _self.getDataDicTree().getSelectedNodes();
                                var parentUuid = null;
                                if (selected.length == 1 && selected[0].id != -1) {
                                    parentUuid = selected[0].id;
                                }

                                server.JDS.call({
                                    service: 'cdDataDicRefService.saveDataDicRef',
                                    data: [{
                                        refUuid: refSelected[0].id,
                                        moduleId: _self._moduleId(),
                                        parentUuid: parentUuid
                                    }],
                                    mask: true,
                                    version: '',
                                    success: function(result) {
                                        if (result.success) {
                                            commitResult = true;
                                            appModal.info('引用数据字典成功');
                                            _self._ztreeInit();
                                            _self.refreshTabBage();
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

                            var zTree = $.fn.zTree.init($("#datadict_tree_ref"), {
                                async: {
                                    enable: true,
                                    contentType: "application/json",
                                    url: ctx + "/json/data/services",
                                    otherParam: {
                                        "serviceName": "appDataDicFacadeService",
                                        "methodName": "loadAppDataDicNodes",
                                        "data": ["-1", moduleId, false],
                                        "version": ''
                                    },
                                    type: "POST"
                                },
                                view: {
                                    fontCss: function(treeId, treeNode) {
                                        return treeNode.data ? {
                                            'color': '#1ed215'
                                        } : {};
                                    }
                                },
                            });

                        }).trigger('change');
                    },
                    size: "middle",

                };
                $dialog = appModal.dialog(dialogOpts);
            },

            cancelReference: function() {
                var _self = this;
                var selected = _self.getDataDicTree().getSelectedNodes();
                if (selected.length == 1 && selected[0].id != -1) {
                    var parentId = _self._getSelectedParentNode().id;
                    var param = {
                        refUuid: selected[0].id,
                        moduleId: _self._moduleId(),
                        parentUuid: parentId == -1 ? null : parentId
                    }
                    appModal.confirm("确认取消引用数据字典吗?", function(result) {
                        if (result) {
                            server.JDS.call({
                                service: 'cdDataDicRefService.deleteDataDicRef',
                                version: '',
                                mask: true,
                                data: [param],
                                success: function(result) {
                                    appModal.info("取消引用成功");
                                    refreshZtree("datadict_tree", "delete");
                                    _self.refreshTabBage();
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

            _moduleId: function() {
                return this.getWidgetParams().moduleId || ""
            },
        });
        return AppDataDicTreeDetailsWidgetDevelopment;
    });