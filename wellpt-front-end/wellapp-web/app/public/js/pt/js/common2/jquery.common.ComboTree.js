(function (factory) {
    "use strict";
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define(['jquery', 'commons', 'server', "ztree", "ztree-exhide"], factory);
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function ($, commons, server) {
    "use strict";

    var jds = null;
    var StringUtils = null;
    if (window.JDS) {
        jds = window.JDS;
    }
    if (window.StringUtils) {
        StringUtils = window.StringUtils;
    }
    if (server) {
        jds = server.JDS;
    }
    if (commons) {
        StringUtils = commons.StringUtils;
    }

    function isContain(names, name) {
        for (var i = 0; i < names.length; i++) {
            // modify time 2016-01-25 yuyq 数据源名称为null时会报错
            if (name != null && name.indexOf(names[i]) >= 0) {
                return true;
            }
        }
        return false;
    };

    function ergodicTree(ztree, node, names) {
        if (node.isParent) {
            var nodeschild = node.children;
            var isShow = false;// 是否隐藏node的父节点
            for (var i = 0; i < nodeschild.length; i++) {
                isShow = (ergodicTree(ztree, nodeschild[i], names) || isShow);
            }
            isShow = isShow || isContain(names, node.name);

            if (!isShow) {
                // ztree#hideNode 影响nocheck属性
                //node._nocheck = $(node).data("nocheck_state");
                ztree.hideNode(node);
            } else {
                //node._nocheck = $(node).data("nocheck_state");
                ztree.showNode(node);
            }
            return isShow;
        } else {
            if (!isContain(names, node.name)) {
                //node._nocheck = $(node).data("nocheck_state");
                ztree.hideNode(node);
                return false;
            } else {
                //node._nocheck = $(node).data("nocheck_state");
                ztree.showNode(node);
                return true;
            }
        }
    };

    $.widget("ui.wCommonComboTree", $.ui.wCommonInput, {
        options: {
            valueField: "id",
            nameField: "name",
            // service请求优先，如果没有则取data
            service: "",
            serviceParams: [],
            treeData: [],
            separator: ",",
            searchEnable: true,
            searchEvent : "search",
            showSearchInput: true,
            focusTriggerSearch : false,
            showCheckAll: true,
            showIcon:true,//默认展示图标
            multiSelect: false, // 是否多选
            parentSelect: false, // 父节点选择有效，默认无效
            expandRootNode : false, // 展开根结点
            searchTimeout: 1000,
            onBeforeShow: null,
            onAfterShow: null,
            onBeforeHide: null,
            onAfterHide: null,
            onAfterSetValue: null
        },
        _render: function () {
            var self = this, p = self.options;
            if(p.inlineView === true){
            	p.showSearchInput = p.inbody = false;
            }
            
            $.fn.zTree.destroy(self._getContentTreeId());
            self.$element.addClass("wCommonComboTree");
            if (self.options.readonly) {
                self.$element.attr({
                    'readonly': 'readonly'
                });
            }

            if (StringUtils.isBlank(p.value)) {
                p.value = self.$element.val();
            }
            // self.$element.attr("readonly", "readonly");

            self.$warpper = self.$element.wrap("<div></div>").parent();

            // 渲染下拉框
            self.$elementContent = $('<div id="' + self._getContentId()
                + '" class="menuContent" style="display:none;border: 1px solid #ccc;"></div>');
            // 渲染下拉框里面的树
            self.$elementContentTree = $('<ul id="' + self._getContentTreeId() + '" class="ztree" ></ul>');
            self.$elementContent.append(self.$elementContentTree);
            /*
             * 添加搜索框 update by xiem 2016-03-11 搜索事件进行统一
             */
            if (self.options.searchEnable === true) {
                var $selectElement = self.$element;
                {
                    if (self.options.showSearchInput) {
                        var selectDiv = $('<div id="ztree_select_div" style="width:100%;"><input id="ztree_select" style="width:100%;" placeholder="多个搜索可用;隔开" type="search" autocomplete="off"/></div>');
                        $selectElement = selectDiv.children('#ztree_select');
                        self.$elementContentTree.before(selectDiv);
                        // showSelect==true 焦点放到搜素框
                    }
                }
                self.$element.focus(function () {
                    if ($selectElement === self.$element) {
                    } else {
                        $selectElement.focus();
                        $selectElement.val(self.$element.val());
                    }
                    if(self.options.focusTriggerSearch === true){
                    	$selectElement.trigger(self.options.searchEvent);
                    }
                });
                //  搜索事件进行统一
                $selectElement.on(self.options.searchEvent, function (event) {
					event.preventDefault();
					event.stopPropagation();
                    var inputValue = $selectElement.val() || "";
                    //setTimeout(function(){
                    if (inputValue === self.searchValue) {
                        return;
                    }
                    self.searchValue = inputValue;
                    var v = inputValue.split(';');
                    var treeId = self._getContentTreeId();
                    //self.$elementContentTree.hide();
                    var zTree = $.fn.zTree.getZTreeObj(treeId);
                    var nodes = zTree.getNodes();
                    //for (var i = 0; i < nodes.length; i++) {
                    // nodes[i]["_nocheck"] = nodes[i]["nocheck"];
                    // // $(nodes[i]).data("nocheck_state", nodes[i].nocheck)
                    //}
                    setTimeout(function(){
                    	for (var i = 0; i < nodes.length; i++) {
                    		ergodicTree(zTree, nodes[i], v);
                    	}
                    	// 有搜索条件则全部展开，否则收起
                    	var expandFlag = StringUtils.isNotBlank(inputValue);
                    	zTree.expandAll(expandFlag);
                    	zTree.refresh();
                    }, 0);
                    //self.$elementContentTree.show();
                    //}, self.options.searchTimeout)
                });
                if(self.options.searchEvent && self.options.searchEvent !== "keypress"){
                	$selectElement.on("keypress", function(event){
                		if (event && event.keyCode == 13) {
                			event.preventDefault();
                			event.stopPropagation();
                			$selectElement.trigger(self.options.searchEvent);
                		}
                	});
                }
            }
            /*
             * 添加全选
             */
            {
                if (self.options.showCheckAll && self.options.multiSelect) {
                    var labelField = self.options.labelField;
                    var valueField = self.options.valueField;
                    var checkAllDiv = $('<div id="ztree_check_all_div"><input id="ztree_check_all" type="checkbox" style="margin-left: 1.5em;" value="全选/全不选"><label for="ztree_check_all">全选/全不选</label></input></div>');
                    checkAllDiv.on("change.#ztree_check_all", function (e) {
                        var _this = $(this).children('#ztree_check_all');
                        var zTree = $.fn.zTree.getZTreeObj(self._getContentTreeId());
                        if (typeof zTree === "undefined" || zTree == null) {
                            return;
                        }
                        if (_this.prop("checked") === true) {
                            zTree.checkAllNodes(true);
                            self.triggerCheck(true);
                        } else {
                            zTree.checkAllNodes(false);
                            self.triggerCheck(false);

                        }
                    });
                    self.$elementContentTree.before(checkAllDiv);
                }
            }
            // 宽度与高度
            var divWidth = self.options.width || 200;
            var divHeight = self.options.height || 250;
            $(self.$elementContent).width(divWidth);
            $(self.$elementContent).height(divHeight);

            if (!self.options.inbody) {
                self.$warpper.append(self.$elementContent);
            } else {
                $('body').append(self.$elementContent);
            }
            if (window.JDS) {
                self.$elementContent.width(self.$warpper.width());
            }

            self._createTreeSetting();
        },

        triggerCheck: function () {
            var self = this;
            var options = self.options;
            var treeId = self._getContentTreeId();
            if (self.treeSetting.callback && self.treeSetting.callback.onCheck) {
                self.treeSetting.callback.onCheck(null, treeId, null);
            }
        },
        _createTreeSetting: function () {
            var self = this, p = this.options;
            self.treeSetting = {
                check: {
                    enable: true,
                    chkStyle: p.multiSelect ? "checkbox" : "radio",
                    chkboxType: {
                        "Y": "",
                        "N": ""
                    },
                    radioType: "all"
                },
                view: {
                    dblClickExpand: false,
                    selectedMulti: false,
                    showIcon:p.showIcon,
                },
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                callback: {
                    beforeClick: function (treeId, treeNode) {
                        var zTree = $.fn.zTree.getZTreeObj(treeId);
                        zTree.expandNode(treeNode);
                        // zTree.checkNode(treeNode, !treeNode.checked, null,
                        // true);
                        return false;
                    },
                    onCheck: function (e, treeId, treeNode) {
                        var zTree = $.fn.zTree.getZTreeObj(treeId);
                        if (!p.parentSelect) {
                            if (treeNode.isParent) {
                                zTree.checkNode(treeNode, false, false);
                                return false;
                            }
                        }
                        var zTree = $.fn.zTree.getZTreeObj(treeId);
                        var nodes = zTree.getCheckedNodes(true);
                        var v = "";
                        for (var i = 0, l = nodes.length; i < l; i++) {
                            v += nodes[i][p.valueField] + p.separator;
                        }
                        if (v.length > 0) {
                            v = v.substring(0, v.length - 1);
                        }
                        self.setValue(v);
                    },
                    beforeExpand: p.beforeTreeExpand,
                    onAsyncSuccess: p.onAsyncSuccess
                }
            };
        },

        _rendered: function () {
            var self = this;
            var options = self.options;
            if(options.inlineView === true){
            	if (!self._isZtreeInited()) {
            		// 加载完成自动展示
            		self.$element.data('waitShowMenu',true);
            	}
        		self.showMenu();
            }else {
            	self.$element.on("click", function () {
            		if (!self._isZtreeInited()) {
            			//未初始化好zTree
            			self.$element.attr('placeholder','加载选项中...').css('cursor','progress');
            			self.$element.data('waitShowMenu',true);
            			return;
            		}
            		self.showMenu();
            	});
            }
        },
        _isZtreeInited:function(){
            var self = this;
            var treeId = self._getContentTreeId();
            var zTree = $.fn.zTree.getZTreeObj(treeId);
            return zTree!=undefined;
        },

        _getContentId: function () {
            var self = this;
            return self.$element.attr("id") + '-content';
        },
        _getContentTreeId: function () {
            var self = this;
            return self.$element.attr("id") + '-content-tree';
        },
        _inited: function () {
            var self = this;
            self.refreshData();
        },
        refreshData: function () {
            var self = this, p = this.options;
            var data = p.treeData;
            var serviceVersion = p.serviceVersion;
            if (p.service) {
                jds.call({
                    service: p.service,
                    data: p.serviceParams,
                    async: p.async === true,
                    version: serviceVersion,
                    success: function (result) {
                        data = result.data;
                        var zTree = $.fn.zTree.init(self.$elementContentTree, self.treeSetting, data);
                        var nodes = zTree.getNodes();
                        self.$element.removeAttr('placeholder').css('cursor','inherit');
                        var nocheckParent = function (node) {
                            if (!p.parentSelect) {
                                if (node.isParent) {
                                    node.nocheck = true;
                                }
                            }
                            var children = node.children;
                            if (children) {
                                $.each(children, function (i, child) {
                                    nocheckParent(child);
                                });
                            }
                        }
                        $.each(nodes, function (i, node) {
                            nocheckParent(node);
                        });
                        self.setValue(p.value);
                        if(self.$element.data('waitShowMenu')){//有点击过需要展示的情况下，加载完需要自动展示
                            self.showMenu();
                            self.$element.data('waitShowMenu',false);
                        }
                        // 展开根结点
                        if(p.expandRootNode) {
                        	if($.isArray(nodes) && nodes.length == 1) {
                        		zTree.expandNode(nodes[0]);
                        	} 
                        }
                    },
                    error: function (jqXHR, statusText, error) {
                        console.error("下拉树数据请求异常");
                    }
                });
            }


        },

        //通过节点的data数据的某个属性值勾选树
        _setValueByDataPropertyValue:function(value,dataProperty,expandNode){
            var self = this, p = this.options;
            if (StringUtils.isBlank(value)) {
                value = "";
            }
            p.value = value;
            var names = [], pnode = null;
            var treeId = self._getContentTreeId();
            var zTree = $.fn.zTree.getZTreeObj(treeId);
            if (zTree == undefined) {
                return;
            }
            // 展开选中节点，先关闭
            if(expandNode === true){
                zTree.expandAll(false);
            }
            zTree.checkAllNodes(false);
            p.valueNodes = [];
            if (StringUtils.isNotBlank(value)) {
                var nodes=zTree.getNodesByFilter(function(n){
                    return n.data&&n.data[dataProperty]==value;
                });
                p.valueNodes=nodes;
                for(var i=0,len=nodes.length;i<len;i++){
                    var node = nodes[i];
                    names.push(node[p.nameField]);
                    // 展开选中节点
                    if(expandNode === true && (pnode = node.getParentNode())){
                        zTree.expandNode(pnode, true);
                    }
                    // 勾选树节点
                    zTree.checkNode(node, true, false);
                    p.valueNodes.push(node);
                }
            }
            var namesStr = names.join(p.separator);
            self.$element.val(namesStr);
            self.$element.attr("title", namesStr);
            self._trigger("onAfterSetValue", null, [self, value]);
        },

        _setValue: function (value, expandNode) {
            var self = this, p = this.options;
            if (StringUtils.isBlank(value)) {
                value = "";
            }
            p.value = value;
            var names = [], pnode = null;
            var treeId = self._getContentTreeId();
            var zTree = $.fn.zTree.getZTreeObj(treeId);
            if (zTree == undefined) {
                return;
            }
            // 展开选中节点，先关闭
            if(expandNode === true){
            	zTree.expandAll(false);
            }
            zTree.checkAllNodes(false);
            p.valueNodes = [];
            if (StringUtils.isNotBlank(value)) {
                for (var i = 0; i < value.split(p.separator).length; i++) {
                    var val = value.split(p.separator)[i];
                    var nodes = zTree.getNodesByParam(p.valueField, val, null);
                    if (nodes.length > 0) {
                    	var node = nodes[0];
                        names.push(node[p.nameField]);
                        // 展开选中节点
                        if(expandNode === true && (pnode = nodes[0].getParentNode())){
                        	zTree.expandNode(pnode, true);
                        }
                        // 勾选树节点
                        zTree.checkNode(node, true, false);
                        p.valueNodes.push(node);
                    }
                }
            }
            var namesStr = names.join(p.separator);
            self.$element.val(namesStr);
            self.$element.attr("title", namesStr);
            self._trigger("onAfterSetValue", null, [self, value]);
        },
        _getValue: function () {
            var self = this, p = this.options;
            return p.value;
        },
        getValueNodes: function () {
            var self = this, p = this.options;
            return p.valueNodes;
        },
        showMenu: function () {
            var self = this;
            var options = self.options;
            self._trigger("onBeforeShow");
            var $element = self.$element;
            var elementWidth = options.width || self.$element.outerWidth();
            if(options.inlineView === true) {
            	self.$elementContent.css({
            		"background-color": "#FFFFFF",
            		"overflow": "auto",
            		"z-index": 1100,
            		"width": elementWidth //与输入框的宽度保持一致
            	}).show();
            } else {
            	var outerHeight = $element.outerHeight();
            	var offset = $element.position();
            	if (self.options.inbody) {
            		offset = $element.offset();
            	}
            	var pos = self.$element.position();
            	self.$elementContent.css({
            		"left": offset.left + "px",
            		"top": offset.top + outerHeight + "px",
            		"position": "absolute",
            		"background-color": "#FFFFFF",
            		"overflow": "auto",
            		"z-index": 1100,
            		"width": elementWidth //与输入框的宽度保持一致
            	}).slideDown("fast");
            	$("body").on("mousedown", function (event) {
            		if (!(event.target.id == self.$element.attr("id")
            				|| event.target.id == self._getContentId() || $(event.target).parents(
            						"#" + self._getContentId()).length > 0)) {
            			self.hideMenu();
            		}
            	});
            }
            self._trigger("onAfterShow");
        },
        hideMenu: function () {
            var self = this;
            var options = self.options;
            self._trigger("onBeforeHide");
            if(options.inlineView === true){
            	self.$elementContent.hide();
            } else {
            	self.$elementContent.fadeOut("fast");
            	$("body").off("mousedown");
            }
            self._trigger("onAfterHide");
        }
    });
}));