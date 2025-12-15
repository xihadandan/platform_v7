(function ($) {
    var ComboTree = function (element, options) {
        this.$element = $(element);
        this.options = $.extend({}, $.fn.comboTree.defaults, options);
        this.init();
    };
    ComboTree.prototype = {
        constructor: ComboTree,
        init: function () {
            var _this = this;
            var $element = this.$element;
            var treeId = $element.attr("id") + "_" + "ztree";
            var treeDivId = "content" + "_" + treeId;

            if (_this.options.readonly) {
                $element.attr({
                    'readonly': 'readonly'
                })
            }

            /* lmw 2015-4-30 14:44 begin */
            // 记录控件的ids
            var ztreeIds = {
                ztree_check_all_div: 'ztree_check_all_div',
                ztree_select_div: 'ztree_select_div',
                ztree_check_all: 'ztree_check_all'
            };
            ztreeIds[treeId] = treeId;
            ztreeIds[treeDivId] = treeDivId;
            /* lmw 2015-4-30 14:44 end */

            var divWidth = this.options.width || $element.outerWidth();

            try {
                divWidth = parseInt(divWidth, 10);
            } catch (e) {
                divWidth = 500;
            }
            var divHeight = this.options.height || 200;

            this.$element.hide();
            var selectTypeHtml = '';

            var ctlName = $element.attr("id");
            var mutiSelect = this.options.mutiSelect ? true : false;
            if (this.options.treeSetting && this.options.treeSetting.check) {
                mutiSelect = this.options.treeSetting.check.enable && this.options.treeSetting.check.chkStyle === 'checkbox';
            }
            this.options.mutiSelect = mutiSelect;
            this.$displayEditableElem = $('<div>', {
                'id': 'display_' + ctlName,
                'class': 'well-select well-select-default ' + (mutiSelect ? 'well-select-multiple' : 'well-select-single') + ' ' + this.editableClass,
                'name': 'display_' + ctlName
            });
            if (mutiSelect) {
                selectTypeHtml = '<div class="well-tag-list"></div>';
            } else {
                selectTypeHtml = '<span class="well-select-selected-value" style="display:none;"></span>';
            }
            var arrow = mutiSelect ? 'icon-ptkj-xianmiaoshuangjiantou-xia' : 'icon-ptkj-xianmiaojiantou-xia';
            var $selection = $('<div class="well-select-selection">' +
                '<div>' + selectTypeHtml +
                '<span class="well-select-placeholder">' + (this.options.placeholder || "请选择") + '</span>' +
                '<i class="iconfont ' + arrow + ' well-select-arrow"></i>' +
                '</div>' +
                '</div>');
            this.$displayEditableElem.append($selection);
            this.$_editableElemLabelField = $("<input type='hidden' id='label_" + ctlName + "' name='label_" + ctlName + "' value=''>");

            var treeDiv = "<div id='"
                + treeDivId
                + "' class='w-zTree-content' style='display: none; width: "
                + divWidth
                + "px; position: absolute; z-index: 9999; background-color: #fff; overflow-x: auto;overflow-y: auto; padding:10px;box-sizing: border-box;'>"
                + "<ul id='" + treeId + "' class='ztree full-width' style='margin-top: 0; " + " height: "
                + divHeight + "px;'></ul>"// ( parseInt(divWidth) -
                // 28)//不-28
                + "</div>";

            /* lmw 2015-4-30 14:29 begin */
            treeDiv = $(treeDiv);

            /*
             * 阻止默认行为
             */
            {
                treeDiv.mousedown(function (e) {
                    e.stopPropagation();
                    // e.preventDefault();
                });
            }

            var $selectElement = $element;
            /*
             * 添加搜索框 update by xiem 2016-03-11 搜索事件进行统一
             */
            {
                if (_this.options.showSelect) {
                    $element.attr("readonly", "readonly");
                    var selectDiv = $('<div id="ztree_select_div" class="ztree_select_div"><input class="ztree_select" style="width:100%;" placeholder="多个搜索可用;隔开" type="text" /><div class="search-icon"><i class="iconfont icon-ptkj-sousuochaxun"></i></div></div>');

                    $selectElement = selectDiv.children('.ztree_select');
                    treeDiv.find('#' + treeId).before(selectDiv);
                }
            }

            var $treeEventDom = $('<div class="tree-event" style="padding-left: 10px"></div>');

            // 添加全选
            if ((_this.options.showCheckAll)
                && (!_this.options.treeSetting.check || _this.options.treeSetting.check.chkStyle != 'radio')) {
                var labelField = this.options.labelField;
                var valueField = this.options.valueField;
                var checkAllDiv = $('<div id="ztree_check_all_div" style="line-height: 30px"><input id="ztree_check_all" type="checkbox" value="全选"><label for="ztree_check_all">全选</label></input></div>');
                checkAllDiv.on("change.#ztree_check_all", function (e) {
                    var $this = $(this).children('#ztree_check_all');
                    var zTree = $.fn.zTree.getZTreeObj(treeId);
                    if ($this.prop("checked") === true) {
                        if (zTree) {
                            zTree.checkAllNodes(true);
                            _this.triggerCheck();
                        }
                    } else {
                        if (zTree) {
                            zTree.checkAllNodes(false);
                            _this.triggerCheck();
                            if (labelField != null && valueField != null) {
                                $("#" + labelField).val("");
                                $("#" + valueField).val("");
                            }
                        }
                    }
                });
                $treeEventDom.append(checkAllDiv);
            }

            // 添加全部展开/折叠
            if (_this.options.expandAndCollapse) {
                var $expandAndCollapse = $('<div class="expand-and-collapse"><div class="item expand-btn">展开</div><div class="item collapse-btn">折叠</div></div>')
                $treeEventDom.append($expandAndCollapse);
                $expandAndCollapse.find('.item').on('click', function () {
                    var $this = $(this);
                    $this.addClass('active').siblings().removeClass('active');
                    var zTree = $.fn.zTree.getZTreeObj(treeId);
                    if ($this.hasClass('expand-btn')) {
                        zTree.expandAll(true);
                    } else {
                        zTree.expandAll(false);
                    }
                });
            }

            if ($treeEventDom.html() !== '') {
                treeDiv.find('#' + treeId).before($treeEventDom);
            }
            /* lmw 2015-4-30 14:29 end */

            this.treeId = treeId;
            this.treeDivId = treeDivId;

            $element.parent().css('position', 'relative');
            $element.after($(treeDiv)).after(this.$_editableElemLabelField).after(this.$displayEditableElem);

            // this.$displayEditableElem.click($.proxy(this._showComboTree, this));
            var _self = this;
            this.$displayEditableElem.on('click', function () {
                var _$this = $(this);
                if ($('#' + treeDivId).length && ($('#' + treeDivId).css('display') === 'block')) {
                    $('#' + treeDivId).hide();
                    _$this.removeClass('well-select-visible');
                } else {
                    _$this.addClass('well-select-visible');
                    $.proxy(_self._showComboTree, _self)()
                }
            })
            // 初始化值
            if ($("#" + this.options.valueField).val() && this.options.autoInitValue === true) {
                var _valueField = $("#" + this.options.valueField).val().toString();
                this.initValue(_valueField);
            }

            if ($("#" + _this.options.valueField).val() && !_this.options.autoInitValue) {
                var _$valueField = $("#" + _this.options.valueField);
                var _$labelField = $("#" + _this.options.labelField);

                if (_$valueField.val()) {
                    if (_$labelField.val()) {
                        var __valueField = _$valueField.val().split(';');
                        var __labelField = _$labelField.val().split(';');
                        $.each(__valueField, function (i, v) {
                            if (_this.options.mutiSelect) {
                                _this.$displayEditableElem.find('.well-tag-list').append(
                                    '<div class="well-tag well-tag-checked" data-value="' + v + '" data-name="' + __labelField[i] + '">' +
                                    '<span class="well-tag-text">' + __labelField[i] + '</span>' +
                                    '<i class="tag-close iconfont icon-ptkj-dacha"></i></div>');
                            } else {
                                _this.$displayEditableElem.find('.well-select-selected-value').text(__labelField).show();
                            }
                        });
                        _this.$displayEditableElem.find('.well-select-placeholder').hide();
                    } else {

                    }
                }
            }

            /* lmw 2015-4-30 17:07 begin */
            // 输入搜索事件的绑定
            // update by xiem 2016-03-11 搜索事件进行统一
            $selectElement.keyup(function (event) {
                var inputValue = $selectElement.val();
                if (inputValue == "") {
                    $.fn.zTree.init($("#" + treeId), window.settingParam);
                } else {
                    var v = inputValue.split(';');
                    var zTree = $.fn.zTree.getZTreeObj(treeId);
                    var nodes = zTree.getNodes();
                    for (var i = 0; i < nodes.length; i++) {
                        ergodicTree(zTree, nodes[i], v);
                    }
                    zTree.expandAll(true);
                    zTree.refresh();
                }
            });

            function isContain(names, name) {
                for (var i = 0; i < names.length; i++) {
                    // modify time 2016-01-25 yuyq 数据源名称为null时会报错
                    if (name != null && names[i] != "" && name.indexOf(names[i]) >= 0) {
                        return true;
                    }
                }
                return false;
            }

            function ergodicTree(ztree, node, names) {
                if (node.isParent) {
                    var nodeschild = node.children;
                    var isShow = false;// 是否隐藏node的父节点
                    for (var i = 0; i < nodeschild.length; i++) {
                        isShow = (ergodicTree(ztree, nodeschild[i], names) || isShow);
                    }
                    isShow = isShow || isContain(names, node.name);

                    if (!isShow) {
                        ztree.hideNode(node);
                    } else {
                        ztree.showNode(node);
                    }
                    return isShow;
                } else {
                    if (!isContain(names, node.name)) {
                        ztree.hideNode(node);
                        return false;
                    } else {
                        ztree.showNode(node);
                        return true;
                    }
                }
            }

            this.$displayEditableElem.on('click', '.well-tag i.tag-close', function (e) {
                e.stopPropagation();
                var node_id = $(this).parent().data('value');
                var node_name = $(this).parent().data('name');
                var $wellTagList = _this.$displayEditableElem.find('.well-tag-list');
                var $wellTagListH = $wellTagList.height();
                $(this).parent().remove();
                var _labelFiledValue = $("#" + _this.options.labelField).val();
                ;
                var _labelFiledValueArr = _labelFiledValue.split(';');
                _labelFiledValueArr.splice(_labelFiledValueArr.indexOf(node_name), 1);
                $("#" + _this.options.labelField).val(_labelFiledValueArr.join(';'));
                _this.cancelCheckNode(node_id, ctlName + '_ztree');
                //检查高度是否有变化$wellTagList高度
                var $wellTagListH_new = $wellTagList.height();
                var $treeDiv = _this.$displayEditableElem.next();
                var $treeDivTop = parseFloat($treeDiv.css('top'));

                var $wellTag = _this.$displayEditableElem.find('.well-tag');
                if ($wellTag.length > 0) {
                    $treeDiv.css('top', $treeDivTop + $wellTagListH_new - $wellTagListH);
                    _this.$displayEditableElem.find('.well-select-placeholder').hide();
                } else {
                    _this.$displayEditableElem.find('.well-select-placeholder').show();
                }
            });
        },
        setDropdownPosition: function (_mount) {
            var self = this;
            _mount = _mount || self.$displayEditableElem;
            var windowInnerHeight = window.innerHeight;
            var _scrollTop = $(document).scrollTop();
            var _mountOffset = _mount.offset();
            var _mountOuterHeight = _mount.outerHeight();
            var $dropdown = _mount.siblings('.w-zTree-content');
            var $dropdownOuterHeight = $dropdown.outerHeight() < self.options.height ? self.options.height + 55 : $dropdown.outerHeight();
            var _w = _mount.outerWidth();
            if (windowInnerHeight + _scrollTop - $dropdownOuterHeight - _mountOuterHeight - _mountOffset.top >= 0) {
                $dropdown.css({
                    'position': 'fixed',
                    'left': _mountOffset.left + 'px',
                    'top': _mountOffset.top + _mountOuterHeight - _scrollTop + 'px',
                    'bottom': 'auto',
                    'width': _w
                })
            } else {
                $dropdown.css({
                    'position': 'fixed',
                    'left': _mountOffset.left + 'px',
                    'top': 'auto',
                    'bottom': windowInnerHeight - _mountOffset.top + _scrollTop + 'px',
                    'width': _w
                })
            }
        },

        cancelCheckNode: function (currId, treeId) {
            var self = this;
            var checkValue = [];
            var checkName = [];

            self.$displayEditableElem.find('.well-tag').each(function () {
                var $this = $(this);
                checkValue.push($this.data('value'));
                checkName.push($this.data('name'));
            });

            if ($('#' + self.treeDivId).css('display') !== 'none') {
                var treeObj = $.fn.zTree.getZTreeObj(treeId);//获取到树
                var nodes = treeObj.getCheckedNodes();
                var cancelNode = null;
                $.each(nodes, function (i, v) {
                    if (v.id == currId || v.data == currId) {
                        cancelNode = v;
                    }
                });
                if (cancelNode) {
                    treeObj.checkNode(cancelNode, false, false);
                }
            }
            if (checkValue.length) {
                $("#" + self.options.valueField).val(checkValue.join(';'));
            } else {
                $("#" + self.options.valueField).val('');
                self.$displayEditableElem.find('.well-select-placeholder').show();
            }


            var _path = checkName.join(';');
            var _value = checkValue.join(';');

            $("#" + self.options.labelField).val(_path);
            // $("#label_" + self.options.valueField).val(_path);
            $("#" + self.options.valueField).val(_value);

            if (self.options.treeSetting.callback) {
                self.options.treeSetting.callback.onCheck && self.options.treeSetting.callback.onCheck();//手动调树形下拉配置方法
            }
            // 缓存key-label
            var cacheValueKey = self._getCacheValueKey(_value);
            var cacheLabelKey = self._getCacheLabelKey(_value);
            self.$element.data[cacheLabelKey] = _path;
            self.$element.data[cacheValueKey] = _value;
        },

        reInit: function (treeSetting) {
            this.treeInitialized = false;
            this.options.treeSetting = treeSetting;
            this.options.mutiSelect = treeSetting.options;
            this.enableRadio = this.options.mutiSelect;
            this.$displayEditableElem.find('.well-select-placeholder').show().prev().remove();

            var mutiSelect = treeSetting.mutiSelect;
            if (treeSetting && treeSetting.check) {
                mutiSelect = treeSetting.check.enable && treeSetting.check.chkStyle === 'checkbox';
            }
            if (mutiSelect) {
                this.$displayEditableElem.addClass('well-select-multiple').removeClass('well-select-single')
                    .find('.well-select-placeholder').before('<div class="well-tag-list"></div>');
                this.$displayEditableElem.find('.well-select-arrow').removeClass('icon-ptkj-xianmiaojiantou-xia').addClass('icon-ptkj-xianmiaoshuangjiantou-xia');
            } else {
                this.$displayEditableElem.addClass('well-select-single').removeClass('well-select-multiple')
                    .find('.well-select-placeholder').before('<span class="well-select-selected-value" style="display:none;"></span>');
                this.$displayEditableElem.find('.well-select-arrow').removeClass('icon-ptkj-xianmiaoshuangjiantou-xia').addClass('icon-ptkj-xianmiaojiantou-xia');
            }
        },

        setParams: function (option) {
            this.options = $.extend(true, this.options, option);
        },
        _showComboTree: function () {
            //todo 显示为fixed
            if (this._enable === false) {
                return;
            }
            var $element = this.$element;
            $element.treeInitialized = false;
            var $this = this;
            var options = this.options;
            var treeId = this.treeId;
            var treeDivId = this.treeDivId;
            $("#" + treeDivId).css({'width': this.$displayEditableElem.outerWidth()});//调整宽度
            // 获取cookie
            function getCookie(name) {
                var arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
                if (arr) {
                    return decodeURI(arr[2]);
                } else {
                    return null;
                }
            }

            // 确保contextPath有值
            function getContextPath() {
                if (window.ctx == null || window.contextPath == null) {
                    window.contextPath = getCookie("ctx");
                    window.contextPath = window.contextPath == "\"\"" ? "" : window.contextPath;
                    window.ctx = window.contextPath;
                }
                return window.ctx;
            }

            getContextPath();
            var ztreeSetting = {
                view: {
                    showLine: false
                },
                async: {
                    enable: true,
                    contentType: "application/json",
                    url: ctx + "/json/data/services",
                    otherParam: {
                        "serviceName": options.serviceName,
                        "methodName": options.methodName
                    },
                    type: "POST",
                    dataType: 'json',
                    dataFilter: function (treeId, node, newNodes) {
                        if (!options.selectParent && $.isArray(newNodes)) {
                            cascadeNoCheckboxNodes(newNodes, null);
                        }
                        return newNodes;
                    }
                },
                check: {
                    enable: true,
                    chkboxType: {
                        "Y": "",
                        "N": ""
                    }
                },
                callback: {
                    onClick: zTreeOnClick,
                    onCheck: zTreeOnCheck,
                    onExpand: zTreeOnExpand,
                    onAsyncSuccess: onAsyncSuccess,
                    beforeAsync: function (treeId, treeNode) {
                        if (treeNode) {
                            var zTree = $.fn.zTree.getZTreeObj(treeId);
                            var otherParam = zTree.setting.async.otherParam;
                            //修改构建树形节点的请求参数，传递父字段值
                            if (otherParam.serviceName === 'cdDataStoreService' && otherParam.methodName === 'loadTreeNodes') {
                                otherParam.data[0].parentColumnValue = treeNode.id;
                            } else {
                                if ($.isArray(otherParam.data)) {
                                    otherParam.data[0] = treeNode.id;
                                } else {
                                    otherParam.data = [treeNode.id];
                                }
                            }
                        }
                        return true;
                    }
                }
            };
            var ztreeSettingCopy = {};
            $.extend(true, ztreeSettingCopy, ztreeSetting);
            var setting = $.extend(true, ztreeSetting, options.treeSetting || {});
            options.ztreeSetting = setting;

            if (typeof setting.src != "undefined" && setting.src == "control") {
                setting.callback.onCheck = function (event, treeId, treeNode) {
                    if (setting.check.enable) {// 多选
                        ztreeSettingCopy.callback.onCheck(event, treeId, treeNode);
                    }
                    if (typeof options.treeSetting.callback != "undefined") {
                        if (typeof options.treeSetting.callback.onCheck != "undefined") {
                            options.treeSetting.callback.onCheck(event, treeId, treeNode);
                        } else if (typeof options.treeSetting.callback.onClick != "undefined") {
                            options.treeSetting.callback.onClick(event, treeId, treeNode);
                        }
                    }
                };

                setting.callback.onClick = function (event, treeId, treeNode) {
                    if (!setting.check.enable) {// 单选
                        ztreeSettingCopy.callback.onClick(event, treeId, treeNode);
                    }
                    if (typeof options.treeSetting.callback != "undefined") {
                        if (typeof options.treeSetting.callback.onClick != "undefined") {
                            options.treeSetting.callback.onClick(event, treeId, treeNode);
                        } else if (typeof options.treeSetting.callback.onCheck != "undefined") {
                            options.treeSetting.callback.onCheck(event, treeId, treeNode);
                        }
                    }
                };
            }

            window.settingParam = setting;

            if (setting.check && setting.check.chkStyle === 'radio') {
                $this.enableRadio = true;
            }

            function cascadeNoCheckboxNodes(nodes, zTree) {
                if (nodes.length > 0) {
                    for (var i = 0, len = nodes.length; i < len; i++) {
                        //隐藏父节点的checkbox
                        if (nodes[i].isParent) {
                            nodes[i].nocheck = true;
                            // zTree.updateNode(nodes[i]);
                            cascadeNoCheckboxNodes(nodes[i].children, zTree);
                        }
                    }
                }
            }

            // 展开树
            function zTreeOnExpand(event, treeId, treeNode) {
                if (options.valueField != null && options.valueField != "") {
                    // 设置值
                    var zTree = $.fn.zTree.getZTreeObj(treeId);
                    var nodes = zTree.getNodes();
                    var value = $("#" + options.valueField).val();
                    var label = $("#" + options.labelField).val();
                    // var label = $("#label_" + options.valueField).val();
                    // if (!options.selectParent) {//无法选中父节点的情况下，需要隐藏父节点的checkbox
                    // cascadeNoCheckboxNodes(nodes, zTree);
                    // }
                    if (value != null && value != "") {
                        var values = value.split(";");
                        var labels = label.split(";");
                        for (var i = 0; i < nodes.length; i++) {
                            var node = nodes[i];
                            checkNodeByData(zTree, node, values, labels);
                        }
                        if (options.autoCheckByValue == true) {
                            for (var i = 0; i < values.length; i++) {
                                var nodes = zTree.getNodesByParam("id", values[i], null);
                                if (nodes.length == 0) {
                                    nodes = zTree.getNodesByParam("data", values[i], null);
                                }
                                $.each(nodes, function (j, node) {
                                    zTree.checkNode(node, true);
                                });
                            }
                        }
                    }
                }
            }

            function ifCheckNode(node, values, labels) {
                var nodeVal = $this.getNodeValue(node);
                var path = getAbsolutePath(node);
                var index = values.indexOf(nodeVal);
                if (index > -1) {
                    index = values.indexOf(node.id);
                }
                if (index > -1 && (path.indexOf(labels[index]) > -1 || labels[index].indexOf(path) > -1)) {
                    return true;
                }
                return false;

            }

            function checkNodeByData(zTree, node, values, labels) {
                if (node.isParent === true) {
                    if (options.selectParent) {
                        if (ifCheckNode(node, values, labels)) {
                            zTree.checkNode(node, true);
                        }
                    }
                    var children = node.children;
                    //是父节点，但是还没异步加载出来子节点的情况下，是无法选中的
                    if (children.length == 0) {
                        return false;
                    }
                    var isAllChecked = true;
                    for (var i = 0, len = children.length; i < len; i++) {
                        isAllChecked = checkNodeByData(zTree, children[i], values, labels) && isAllChecked;
                    }
                    if (isAllChecked) {//子节点全部选中，需要选中该父节点
                        //zTree.checkNode(node, true);
                    }
                    return isAllChecked;
                } else {
                    if (values) {
                        if (ifCheckNode(node, values, labels)) {
                            zTree.checkNode(node, true);
                            return true;
                        }
                    }
                    return false;
                }
            }


            // 检查节点是否未选中 true:未选中, false
            function isNodeUnchecked(zTree, nodeValue) {
                var uncheckNodes = zTree.getCheckedNodes(false);
                for (var index = 0; index < uncheckNodes.length; index++) {
                    var unCheckNode = uncheckNodes[index];
                    var unCheckNodeValue = unCheckNode.data;
                    if (unCheckNodeValue == nodeValue) {
                        return true;
                    }
                }
                return false;
            }

            // 选中树
            function zTreeOnCheck(event, treeId, treeNode) {
                /* lmw 2015-5-5 8:51 begin */
                var tre = $("#" + treeDivId);
                var zTree = $.fn.zTree.getZTreeObj(treeId);
                var unchecked = zTree.getCheckedNodes(false);
                if (unchecked.length == 0) {
                    tre.find('#ztree_check_all').prop({
                        checked: true
                    });
                } else {
                    tre.find('#ztree_check_all').prop({
                        checked: false
                    });
                }
                /* lmw 2015-5-5 8:51 begin */

                if (options.includeParentValue == false) {
                    /*
                     * if (treeNode && treeNode.isParent) { if
                     * (options.labelField != null) { $("#" +
                     * options.labelField).val(""); } if (options.valueField !=
                     * null) { $("#" + options.valueField).val(""); } //return; }
                     */
                } else {
                    // $("#" + options.labelField).val("");
                    // $("#" + options.valueField).val("");
                }
                // 设置值
                var zTree = $.fn.zTree.getZTreeObj(treeId);

                var path = "";
                var value = "";

                var checkNodes = zTree.getCheckedNodes(true);
                for (var index = 0; index < checkNodes.length; index++) {
                    var checkNode = checkNodes[index];

                    if (!options.selectParent && checkNode.isParent === true && !options.includeParentValue) {
                        continue;
                    }

                    var nodePath = "";
                    var nodeValue = "";
                    if ($this.enableRadio && $this.enableRadio == true) {
                        if (checkNode !== treeNode) {
                            zTree.checkNode(checkNode, false);
                            continue;
                        }
                    }

                    if (options.valueField != null) {
                        nodeValue = $this.getNodeValue(checkNode);
                        if (value == "") {
                            value = nodeValue;
                        } else {
                            value = value + ";" + nodeValue;
                        }
                        if (!$this.$element.data["comboTreeValueLabelMap"]) {
                            $this.$element.data["comboTreeValueLabelMap"] = {};
                        }
                        $this.$element.data["comboTreeValueLabelMap"][nodeValue] = nodePath;
                    }

                    var nodePath = '';
                    if (options.labelField != null) {
                        if (options.labelBy) {
                            nodePath = checkNode[options.labelBy];
                        } else {
                            nodePath = getAbsolutePath(checkNode);
                        }
                        if (path == "") {
                            path = nodePath;
                        } else {
                            path = path + ";" + nodePath;
                        }
                    }
                }

                $this._renderTreeLabel(path, value, unchecked);
            }

            //单选时触发赋值
            function zTreeOnlyClick(event, treeId, treeNode) {
                // 设置值
                var zTree = $.fn.zTree.getZTreeObj(treeId);

                var path = "";
                var value = "";

                var nodePath = "";
                var nodeValue = "";

                if (options.valueField != null) {
                    nodeValue = $this.getNodeValue(treeNode);
                    if (value == "") {
                        value = nodeValue;
                    } else {
                        value = value + ";" + nodeValue;
                    }
                    if (!$this.$element.data["comboTreeValueLabelMap"]) {
                        $this.$element.data["comboTreeValueLabelMap"] = {};
                    }
                    $this.$element.data["comboTreeValueLabelMap"][nodeValue] = nodePath;
                }

                if (options.labelField != null) {
                    if (options.labelBy) {
                        path = treeNode[options.labelBy];
                    } else {
                        nodePath = getAbsolutePath(treeNode);
                        if (path == "") {
                            path = nodePath;
                        } else {
                            path = path + ";" + nodePath;
                        }
                    }
                }

                $this._renderTreeLabel(path, value);
                $("#" + options.labelField).val(path);
                // $("#label_" + options.valueField).val(path);
                $("#" + options.valueField).val(value);

                $this.hide();
                if ($this.options.treeSetting && $this.options.treeSetting.callback && $this.options.treeSetting.callback.onCheck) {
                    $this.options.treeSetting.callback.onCheck();
                }
                // 缓存key-label
                var cacheValueKey = $this._getCacheValueKey(value);
                var cacheLabelKey = $this._getCacheLabelKey(value);
                $element.data[cacheLabelKey] = path;
                $element.data[cacheValueKey] = value;
            }

            // 点击树
            function zTreeOnClick(event, treeId, treeNode) {
                if (options.includeParentValue == false && !options.selectParent) {
                    if (treeNode.isParent) {
                        if (options.labelField != null) {
                            $("#" + options.labelField).val("");
                        }
                        if (options.valueField != null) {
                            $("#" + options.valueField).val("");
                        }
                        var zTree = $.fn.zTree.getZTreeObj(treeId);
                        var checkNodes = zTree.getCheckedNodes(true);
                        var selectedNodes = zTree.getSelectedNodes(true);
                        $.each(selectedNodes, function () {
                            zTree.checkNode(this, !this.checked);
                            zTreeOnCheck(null, treeId, this);
                        });
                        return;
                    }
                }
                if (options.mutiSelect) {
                    var zTree = $.fn.zTree.getZTreeObj(treeId);
                    zTree.checkNode(treeNode, !treeNode.checked);
                    zTreeOnCheck(null, treeId, treeNode);
                } else {
                    zTreeOnlyClick(event, treeId, treeNode)
                }
            }

            function onAsyncSuccess(event, treeId, treeNode, msg) {
                // 选中结点
                zTreeOnExpand(null, treeId, null);
            }

            // 获取树结点的绝对路径
            function getAbsolutePath(treeNode) {
                var path = treeNode.name;
                var parentNode = treeNode.getParentNode();
                while (parentNode != null) {
                    path = parentNode.name + "/" + path;
                    parentNode = parentNode.getParentNode();
                }
                return path;
            }

            if (this.treeInitialized !== true) {
                $.fn.zTree.init($("#" + treeId), setting);
                this.treeInitialized = true;
            } else {
                var zTree = $.fn.zTree.getZTreeObj(treeId);
                zTree.checkAllNodes(false);
                // 选中结点
                zTreeOnExpand(null, treeId, null);
            }
            var comboTree = this;
            comboTree.show();

            $(document).mousedown(function (event) {
                /* lmw 2015-4-30 15:20 begin */
                if (event.target.className && event.target.className.indexOf('tag-close') > -1) {
                    return;
                }
                comboTree.hide();
                /*
                 * var id = event.target.id; if ((id != null) && (typeof id ==
                 * "string") && (id.lastIndexOf(treeId) == -1) && (id !=
                 * treeDivId) && (id != $element.attr("id"))) {
                 * comboTree.hide(); }
                 */
                /* lmw 2015-4-30 15:20 end */
            });
            /* lmw 2015-5-6 15:20 begin */
            $("iframe").contents().find("body").mousedown(function (event) {
                if (event.target.className && event.target.className.indexOf('tag-close') > -1) {
                    return;
                }
                comboTree.hide();
            });
            /* lmw 2015-5-6 15:20 end */
        },

        getNodeValue: function (checkNode) {
            var options = this.options;
            var nodeValue;
            var otherParam = options.treeSetting.async.otherParam;
            if (otherParam.serviceName === 'cdDataStoreService' && otherParam.methodName === 'loadTreeNodes') {
                //构造树节点服务加载的数据
                var valueColumn = otherParam.data[0].valueColumn || otherParam.data[0].uniqueColumn;
                nodeValue = checkNode.data[valueColumn];
            } else if (otherParam.serviceName === 'dataDictionaryService' && otherParam.methodName === 'getAllDataDicAsTree') {
                nodeValue = checkNode.data['uuid'];
            } else if (options.valueBy) {
                nodeValue = checkNode[options.valueBy];
            } else if (otherParam.methodName === 'getFormFieldApplyToRootDicts') {
                nodeValue = checkNode.data;
            } else {
                nodeValue = checkNode.id || checkNode.data;
            }
            return nodeValue;
        },

        show: function () {
            var self = this;
            var treeDivId = self.treeDivId;
            var $element = self.$element;
            // var outerHeight = $element.outerHeight();
            // var offset = $element.position();
            var outerHeight = self.$displayEditableElem.outerHeight();
            var offset = self.$displayEditableElem.position();

            // if (this.options.inbody) {
            //     offset = $element.offset();
            // }
            var tre = $("#" + treeDivId);
            // /* lmw 2015-5-4 9:45 end */
            // tre.css({
            //     position: 'fixed',
            //     left: offset.left + "px",
            //     top: offset.top + outerHeight + "px"
            // }).slideDown("fast");
            tre.slideDown('fast');
            self.setDropdownPosition();
            var resize = 'resize.' + self.$displayEditableElem;
            var scroll = 'scroll.' + self.$displayEditableElem;
            self.$displayEditableElem.parents().add(window).each(function () {
                $(this).on(resize + " " + scroll, function (e) {
                    self.setDropdownPosition();
                });
            });
            var treeId = self.treeId;
            setTimeout(function () {
                var zTree = $.fn.zTree.getZTreeObj(treeId);
                var unchecked = zTree.getCheckedNodes(false);
                var checked = zTree.getCheckedNodes(true);
                /* lmw 2015-5-4 9:45 begin */
                if (unchecked.length == 0) {
                    if (checked.length != 0) {
                        tre.find('#ztree_check_all').prop({
                            checked: true
                        });
                    }
                } else {
                    tre.find('#ztree_check_all').prop({
                        checked: false
                    });
                }
            }, 250)
        },
        hide: function () {
            var treeDivId = this.treeDivId;
            $("#" + treeDivId).fadeOut("fast");
            this.$displayEditableElem.removeClass('well-select-visible');
        },
        triggerCheck: function () {
            var options = this.options;
            var treeId = this.treeId;

            if (options.ztreeSetting.callback && options.ztreeSetting.callback.onCheck) {
                options.ztreeSetting.callback.onCheck(null, treeId, null);
            }
        },
        _getCacheValueKey: function (value) {
            var cacheValueKey = "DY_FORM_FIELD_MAPPING" + "_" + value;
            return cacheValueKey;
        },
        _getCacheLabelKey: function (value) {
            var cacheLabelKey = this._getCacheValueKey(value) + "_label";
            return cacheLabelKey;
        },

        _renderTreeLabel: function (labels, values, uncheckeds) {
            var self = this;
            var $displayEditableElem = self.$displayEditableElem;
            var labelsArr = labels ? labels.split(';') : [];
            var valuesArr = values ? values.split(';') : [];
            if ($displayEditableElem.hasClass('well-select-multiple')) {
                var $wellTagList = $displayEditableElem.find('.well-tag-list');
                var $wellTagListH = $wellTagList.height() || 33;

                var oldTagsLabels = [];
                var oldTagsValues = [];
                $wellTagList.find('.well-tag').each(function () {
                    var $this = $(this);
                    var _value = $this.attr('data-value');
                    var _name = $this.attr('data-name');
                    if (values.indexOf(_value) < 0) {
                        var tag = true;
                        $.each(uncheckeds, function (i, item) {
                            if (item.id === _value || item.data === _value) {
                                tag = false;
                                return false;
                            }
                        });
                        if (tag) {
                            oldTagsValues.push(_value);
                            oldTagsLabels.push(_name);
                        }
                    }
                });
                labelsArr = oldTagsLabels.concat(labelsArr);
                valuesArr = oldTagsValues.concat(valuesArr);

                if (labelsArr) {
                    $displayEditableElem.find('.well-select-placeholder').hide();
                } else {
                    $displayEditableElem.find('.well-select-placeholder').show();
                }

                $wellTagList.empty();

                $.each(labelsArr, function (i, v) {
                    $wellTagList.append('<div class="well-tag well-tag-checked" data-value="' + valuesArr[i] + '" data-name="' + v + '">' +
                        '<span class="well-tag-text">' + v + '</span>' +
                        '<i class="tag-close iconfont icon-ptkj-dacha"></i>' +
                        '</div>');
                });

                labels = labelsArr.join(';');
                values = valuesArr.join(';');

                //检查高度是否有变化$wellTagList高度
                var $wellTagListH_new = $wellTagList.height();
                var $treeDiv = $('#' + self.treeDivId);
                var $treeDivTop = parseFloat($treeDiv.css('top'));
                if (labelsArr.length > 1) {
                    $treeDiv.css('top', $treeDivTop + $wellTagListH_new - $wellTagListH);
                } else {
                    var parentHeight = this.$element.parent().height();
                    $treeDiv.css('top', parentHeight + "px");
                }
            } else {
                if (labels) {
                    $displayEditableElem.find('.well-select-selected-value').text(labels).show().siblings('.well-select-placeholder').hide();
                } else {
                    $displayEditableElem.find('.well-select-selected-value').text(labels).hide().siblings('.well-select-placeholder').show();
                }
            }
            if (self.options.afterSetValue) {
                self.options.afterSetValue(values, labels);
            }
            $("#" + self.options.labelField).val(labels);
            $("#" + self.options.valueField).val(values);
            // 缓存key-label
            var cacheValueKey = self._getCacheValueKey(values);
            var cacheLabelKey = self._getCacheLabelKey(values);
            self.$element.data[cacheLabelKey] = labels;
            self.$element.data[cacheValueKey] = values;
        },

        initValue: function (value) {
            var _displayValue;
            if (typeof value === 'object' && value.displayValue) {
                _displayValue = value.displayValue;
                value = value.value;
            }
            var self = this;
            var initService = this.options.initService;
            var initServiceParam = this.options.initServiceParam;
            var param = initServiceParam.concat([value]);
            var options = this.options;
            var $element = this.$element;

            var cacheValueKey = this._getCacheValueKey(value);
            var cacheLabelKey = this._getCacheLabelKey(value);
            if ($element.data[cacheValueKey] && $element.data[cacheLabelKey]) {
                if (options.valueField != null) {
                    $("#" + options.valueField).val($element.data[cacheValueKey]);
                }
                if (options.labelField != null) {
                    $("#" + options.labelField).val($element.data[cacheLabelKey]);
                }
                this._renderTreeLabel($element.data[cacheLabelKey], $element.data[cacheValueKey]);
                return $element.data[cacheLabelKey];
            }
            if (value != null && value != "") {
                var async = options.treeSetting.async || {};
                var otherParam = async.otherParam || {};
                if ((otherParam.serviceName === "cdDataStoreService" && otherParam.methodName === "loadTreeNodes") ||
                    (otherParam.serviceName === 'dataDictionaryService' && otherParam.methodName === 'getAllDataDicAsTree') ||
                    (otherParam.methodName === 'getNodes')) {
                    otherParam.data[0].async = false;
                    // if(options.treeData == null || typeof options.treeData === "undefined"){
                    // 始终获取最新数据
                    // }
                    if (otherParam.methodName === 'getNodes') {
                        JDS.call({
                            async: false,
                            data: [value.split(';')],
                            service: otherParam.serviceName + ".getNodeNamesByKeys",
                            success: function (result) {
                                options.treeData = result.data;
                            }
                        })
                    } else {
                        JDS.call({
                            async: false,
                            data: otherParam.data,
                            service: otherParam.serviceName + "." + otherParam.methodName,
                            success: function (result) {
                                options.treeData = result.data;
                            }
                        })
                    }
                    if (typeof value === "string") {
                        value = value.split(";");
                    } else if (false === $.isArray(value)) {
                        value = [value];
                    }
                    if (options.treeData) {
                        var valueColumn = otherParam.serviceName === 'cdDataStoreService' ? (otherParam.data[0].valueColumn || otherParam.data[0].uniqueColumn) : 'uuid';

                        function getLabels(treeDate, values, labels, lablePaths) {
                            if (!(treeDate instanceof Array)) {
                                treeDate = [treeDate];
                            }
                            for (var i = 0; i < treeDate.length; i++) {
                                var data = treeDate[i];
                                if ($.inArray(data.id, values) > -1 || (data.data && ($.inArray(data.data[valueColumn], values) > -1))) {
                                    labels.push(lablePaths.concat(data.name || data.text).join("/"));
                                }
                                if (data.children && data.children.length > 0) {
                                    lablePaths.push(data.name || data.text);
                                    getLabels(data.children, values, labels, lablePaths);
                                    lablePaths.pop();// 出栈
                                }
                            }
                        }

                        var labels = [];
                        getLabels(options.treeData, value, labels, []);
                        if (options.labelField != null) {
                            if (!labels.length && _displayValue) {
                                labels = _displayValue.split(self.options.separator);
                            }
                            $("#" + options.labelField).val(labels.join(";"));
                            this._renderTreeLabel(labels.join(";"), value.join(";"));
                        }
                        if (options.valueField != null) {
                            $("#" + options.valueField).val(value.join(";"));
                        }
                        $element.data[cacheValueKey] = value.join(";");
                        return $element.data[cacheLabelKey] = labels.join(";");
                    }
                }

                var async = options.treeSetting.async || {};
                var otherParam = async.otherParam || {};
                var dictUuid = otherParam.data && otherParam.data[0];
                if (dictUuid && param[0] === $.fn.comboTree.defaults.initServiceParam[0]) {
                    param[0] = dictUuid;
                }
                JDS.call({
                    async: false,
                    service: initService,
                    data: param,
                    success: function (result) {
                        if (result.data) {
                            if (!result.data.label && !result.data.value) {
                                self._renderTreeLabel($("#" + options.labelField).val(), $("#" + options.valueField).val());
                                return '';
                            }

                            if (options.labelField != null) {
                                $("#" + options.labelField).val(result.data.label);
                                // $("#label_" + options.valueField).val(result.data.label);
                                self._renderTreeLabel(result.data.label, result.data.value);
                            }
                            if (options.valueField != null) {
                                $("#" + options.valueField).val(result.data.value);
                            }
                            $element.data[cacheLabelKey] = result.data.label;
                            $element.data[cacheValueKey] = result.data.value;
                            if (result.data.value) {
                                var values = result.data.value.split(";");
                                var labels = result.data.label.split(";");
                                $element.data["comboTreeValueLabelMap"] = {};
                                for (var index = 0; values.length == labels.length && index < values.length; index++) {
                                    var nodeValue = values[index];
                                    var nodePath = labels[index];
                                    $element.data["comboTreeValueLabelMap"][nodeValue] = nodePath;
                                }
                            }

                        }
                    }
                });
                return $element.data[cacheLabelKey];
            } else {
                if (options.labelField != null) {
                    $("#" + options.labelField).val("");
                    // $("#label_" + options.valueField).val("");
                    if ($element.hasClass('well-select-multiple')) {
                        $element.find('.well-tag-list').empty();
                        $element.find('.well-select-placeholder').show();
                    } else {
                        $element.find('.well-select-selected-value').text('').hide();
                    }
                }
                if (options.valueField != null) {
                    $("#" + options.valueField).val("");
                }
            }
            return "";

        },
        clear: function () {
            if (this.options.labelField != null) {
                $("#" + this.options.labelField).val("");
                // $("#label_" + options.valueField).val("");
                if ($element.hasClass('well-select-multiple')) {
                    $element.find('.well-tag-list').empty();
                    $element.find('.well-select-placeholder').show();
                } else {
                    $element.find('.well-select-selected-value').text('').hide();
                }
            }
            if (this.options.valueField != null) {
                $("#" + this.options.valueField).val("");
            }
        },
        disable: function () {
            this._enable = false;
            this.treeInitialized = false;
            $.fn.zTree.destroy(this.treeId);
        },
        enable: function () {
            this._enable = true;
        },
        reset: function () {
            this.clear();
            this.disable();
            this.enable();
        }
    };
    $.fn.comboTree = function (option) {
        var method = false;
        var args = null;
        if (arguments.length == 2) {
            method = true;
            args = arguments[1];
        }

        return this.each(function () {

            var $this = $(this), data = $this.data("comboTree"),
                options = $.extend({}, $this.data(),
                    typeof option == 'object' && option);
            if (!data) {
                $this.data('comboTree', (data = new ComboTree(this, options)));
            }
            if (typeof option == 'string') {
                if (method == true && args != null) {
                    data[option](args);
                } else {
                    data[option]();
                }
            } else if (options.show) {
                data.show();
            }
        });
    };

    $.fn.comboTree.defaults = {
        includeParentValue: false,
        autoInitValue: true,
        serviceName: "dataDictionaryService",
        methodName: "getAsTreeAsync",
        initService: "dataDictionaryService.getKeyValuePair",
        initServiceParam: ["DY_FORM_FIELD_MAPPING"],
        showSelect: true,
        selectParent: false,
        autoCheckByValue: false,
        showCheckAll: false,
        readonly: false,
        inbody: false
    };
})(jQuery);