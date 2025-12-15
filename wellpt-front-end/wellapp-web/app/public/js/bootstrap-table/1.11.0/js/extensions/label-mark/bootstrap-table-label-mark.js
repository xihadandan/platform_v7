/**
 * 标签标记
 */
(function (factory) {
    "use strict";
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define(['jquery', 'commons', 'server', 'appModal', "minicolors",
            // "css!bootstrap-table-label-mark",
            "css!" + ctx + "/static/js/minicolors/css/jquery.minicolors"], factory);
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function (jquery, commons, server, appModal, minicolors) {


    $.extend($.fn.bootstrapTable.defaults, {});


    var options = {
        markDataDefine: null,
        tableWidget: null,
    };

    var BootstrapTable = $.fn.bootstrapTable.Constructor;


    var MarkUtil = {
        mark: function (options, e, mark, serverParam, successCallback, dataUuids, isDeleteMark) {
            var primaryUuids = options.tableWidget.getDataColumnValues(options.tableWidget.getPrimaryColumnName());
            var $toolbarDiv = $(e.target).closest("div");
            var uuids = dataUuids ? dataUuids : [];
            if (uuids.length == 0) {
                if ($toolbarDiv.is(".div_lineEnd_toolbar")) {//行末按钮
                    var di = $(this).parents('tr').attr('data-index');
                    uuids.push(primaryUuids[di]);
                } else {
                    uuids = options.tableWidget.getSelectionPrimaryKeys();
                }
            }

            if (uuids.length == 0) {
                appModal.info('请选择行数据');
                return;
            }

            var param = {
                dataList: [],
                isDeleteMark: isDeleteMark,
            };
            serverParam(param, mark);

            for (var k in uuids) {
                param.dataList.push({
                    'dataUuid': uuids[k]
                });
            }

            server.JDS.call({
                service: "dmsDataMarkFacadeService.saveOrDeleteMark",
                data: [param],
                version: '',
                async: true,
                success: function (res) {
                    if (res.success) {
                        successCallback();
                    }
                }
            });

        },

        markTr: function ($tr, mStyle, mark) {
            if (!$.isEmptyObject(mStyle)) {
                if (mStyle.textStyleClass) {//情境文本样式
                    if (!$tr.hasClass(mStyle.textStyleClass)) {
                        $tr.addClass(mStyle.textStyleClass);
                    }
                }
                if (mStyle.bgStyleClass) {//情境背景样式
                    if (!$tr.hasClass(mStyle.bgStyleClass)) {
                        $tr.addClass(mStyle.bgStyleClass);
                    }
                }
                if (mStyle.className) {//有图标
                    var $td = $tr.find('td:eq(0)');
                    if ($td.is('.bs-checkbox')) {
                        $td = $tr.find('td:eq(1)');
                    }

                    var iconClass = mStyle.className;
                    iconClass += " td-mark-icon ";
                    var $div = $("<div>", {"class": "td-mark-icon-container"});
                    var color = mStyle.iconColor;
                    var $markIcon = $("<span>", {
                        "class": iconClass
                    });
                    // var $markCloseIcon = $("<span>", {
                    //     "class": "td-mark-close-icon",
                    //     "title": "取消" + mark.text,
                    //     "data_uuid": primaryUuids[i],
                    // }).html('&times;');
                    if (color) {
                        $markIcon.css('color', color);
                        //$markCloseIcon.css('color', color);
                    }
                    $div.append($markIcon);
                    $td.html($div[0].outerHTML + $td.html());
                }

                if (mStyle.fontBolder) {//字体加粗
                    if (!$tr.hasClass('black-bold ')) {
                        $tr.addClass('black-bold ');
                    }
                }

            }
        },

        /**
         * 渲染标记的表格行
         * @param options
         * @param type
         * @param serverParam
         */
        renderMarkDataTr: function (options, mark, serverParam) {
            var primaryUuids = options.tableWidget.getDataColumnValues(options.tableWidget.getPrimaryColumnName());
            if (primaryUuids.length == 0) {
                return;
            }
            var param = {
                dataList: []
            };
            serverParam(param, mark);
            for (var k in primaryUuids) {
                param.dataList.push({
                    'dataUuid': primaryUuids[k]
                });
            }
            var $table = options.tableWidget.$tableElement;
            var isTreeView = $table.bootstrapTable('getOptions').treeView;
            server.JDS.call({
                service: "dmsDataMarkFacadeService.dataMarkResult",
                data: [param],
                version: '',
                async: true,
                success: function (res) {
                    if (res.success) {
                        for (var i = 0, len = primaryUuids.length; i < len; i++) {
                            var $tr = isTreeView ? $table.find('tr[uuid=' + primaryUuids[i] + ']') : $table.find('tr[data-index=' + i + ']');
                            if (res.data[primaryUuids[i]]) {//有标记数据
                                MarkUtil.markTr($tr, mark.markStyle, mark);
                            } else {//无标记
                                MarkUtil.markTr($tr, mark.unmarkStyle, mark);
                            }


                        }


                    }
                }
            });
        }

    };


    var Label = {
        $labelBtnGrp: null,
        $labelUl: null,
        tableWidget: null,//表格组件
        relaEntityClassName: null,//标签关联数据实体类
        options: null,

        init: function (options) {
            if (!options.markDataDefine.enableLabel) {
                return;
            }
            Label.options = options;
            Label.tableWidget = options.tableWidget;
            Label.relaEntityClassName = options.markDataDefine.lableRelaEntity;
            var $labelBtn = null;
            /**
             * 初始化标签按钮组
             */
            if ($(".btn_class_" + options.markDataDefine.labelBtnCode).length > 0) {
                $labelBtn = $(".btn_class_" + options.markDataDefine.labelBtnCode);
                $labelBtn.html($labelBtn.html().replace(options.markDataDefine.labelBtnName,'<span>'+ options.markDataDefine.labelBtnName +'</span>') );
                $labelBtn.addClass("dropdown-toggle");
                $labelBtn.attr("data-toggle", "dropdown");
                $(".btn_class_" + options.markDataDefine.labelBtnCode).append($("<i>", {"class": "iconfont icon-ptkj-xianmiaojiantou-xia"}));
                $labelBtn.wrap($("<div>", {"class": "btn-group"}));
                var $ul = $("<ul>", {
                    "class": "dropdown-menu label_dropdown_ul",
                    "role": "menu"
                });
                $ul.insertAfter($labelBtn);
                Label.$labelUl = $ul;
            } else if ($(".li_class_" + options.markDataDefine.labelBtnCode).length > 0) {
                $labelBtn = $(".li_class_" + options.markDataDefine.labelBtnCode);
                $labelBtn.parent('ul').addClass('open_label_ul');
                $labelBtn.find('a').append($("<span>", {
                    "class": "iconfont icon-more label_li_more_icon"
                }));
                Label.type = "li";
                var $div = $("<div>", {"style": "display:none;", "class": "li_label_div"});
                var $ul = $("<ul>", {
                    "class": "dropdown-menu label_dropdown_ul hide", "role": "menu"
                });
                $div.append($ul);
                $labelBtn.append($div);
                Label.$labelUl = $ul;
            }


            if ($labelBtn) {
                Label.$labelBtnGrp = $labelBtn;
                this.$labelUl.append(
                    $("<li>", {
                        "id": "newLabelAndMark",
                        "class": "label_li_split"
                    }).append($("<a>", {"href": "#"}).text("新建标签并标记")),
                    $("<li>", {"id": "removeAllLabel"}).append($("<a>", {"href": "#"}).text("取消所有标签"))
                );
                this.setEvent();
            }

        },


        loadTableTrLabel: function (trIndexs) {
            var _self = this;
            var uuids = _self.tableWidget.getDataColumnValues(_self.tableWidget.getPrimaryColumnName());
            var isTreeView = _self.tableWidget.$tableElement.bootstrapTable('getOptions').treeView;
            var lableShowTdIndex = parseInt(this.options.markDataDefine.columnIndexForShowLabel);
            for (var i = 0, len = uuids.length; i < len; i++) {
                if (!isTreeView && trIndexs && trIndexs.length > 0 && trIndexs.indexOf(i) == -1) {
                    continue;
                }

                var callLabelService = function (index) {
                    server.JDS.call({
                        service: "dmsDataLabelFacadeService.listLabelByDataUuidAndEntityClass",
                        data: [uuids[index], _self.relaEntityClassName],
                        version: '',
                        async: true,
                        success: function (res) {
                            if (res.success) {
                                var $tr = isTreeView ? _self.tableWidget.$tableElement.find('tr[uuid=' + uuids[index] + ']') : _self.tableWidget.$tableElement.find('tr[data-index=' + index + ']');
                                var $showTd = $tr.find('td:eq(' + lableShowTdIndex + ')');
                                $showTd.css("white-space", "pre-wrap");
                                $showTd.find('.tr_label_div').remove();
                                for (var d = 0; d < res.data.length; d++) {
                                    if (res.data[d].moduleId == _self.options.markDataDefine.labelModuleId) {
                                        var $labelDiv = $("<div>", {"class": "tr_label_div"}).append(
                                            $("<span>", {
                                                "class": "tr_label_span",
                                                "style": "background:" + res.data[d].labelColor
                                            }).text(res.data[d].labelName),
                                            $("<span>", {
                                                "class": "tr_label_close",
                                                "style": "background:" + res.data[d].labelColor
                                            }).append($("<b>", {
                                                "class": "tr_label_close_x",
                                                "label_rela_uuid": res.data[d].labelRelaUuid,
                                                "title": "取消标签"
                                            }).text('x'))
                                        );
                                        $showTd.append($labelDiv);
                                    }
                                }
                                _self.tableWidget.$tableElement.bootstrapTable("uncheckAll");
                            }
                        }
                    });
                };
                callLabelService(i);

            }
        },


        setEvent: function () {
            var _self = this;

            //表格数据加载完成后进行数据关联的标签加载
            this.tableWidget.$tableElement.on('post-body.bs.table', function (e) {
                _self.loadTableTrLabel();
            });
            _self.loadTableTrLabel();


            //标签hover事件：显示x关闭
            this.tableWidget.$tableElement.on('hover', '.tr_label_div', function (e) {
                if (e.type == 'mouseenter') {
                    $(this).addClass('tr_label_div_hover');
                } else {
                    $(this).removeClass('tr_label_div_hover');
                }

            });

            //标签x关闭事件
            this.tableWidget.$tableElement.on('click', '.tr_label_close_x', function (e) {
                _self.removeLables([$(this).attr('label_rela_uuid')], [parseInt($(this).parents('tr').attr('data-index'))]);

            });


            var hideTimeout = null;
            /**
             * 标签组按钮展示用户定义的所有标签
             */
            this.$labelBtnGrp.on(this.$labelBtnGrp.is("li") ? 'hover' : 'click', function (e) {

                if(_self.$labelBtnGrp.is("button")){
                    if(_self.$labelBtnGrp.parent().hasClass('open')){
                        _self.$labelBtnGrp.parent().removeClass('open');
                        return true;
                    }
                }

                var isShow = $(this).attr('aria-expanded') == undefined || $(this).attr('aria-expanded') == 'false';
                if (_self.$labelBtnGrp.is("li")) {
                    if (e.type == 'mouseenter') {
                        window.clearTimeout(hideTimeout);
                        $(".li_label_div").show();
                        _self.$labelBtnGrp.find('.label_dropdown_ul').removeClass('hide').addClass('show');
                        isShow = true;
                    } else {
                        isShow = false;
                        hideTimeout = window.setTimeout(function () {
                            $(".li_label_div").hide();
                            _self.$labelBtnGrp.find('.label_dropdown_ul').removeClass('show').addClass('hide');
                        }, 300);
                    }
                }

                if (isShow) {
                    _self.$labelUl.find('.li_data_label').remove();
                    server.JDS.call({
                        service: "dmsDataLabelFacadeService.loadUserDataLabelDtosByModuleId",
                        data: [_self.options.markDataDefine.labelModuleId],
                        version: '',
                        async: false,
                        success: function (res) {
                            if (res.success) {
                                if (res.data.results.length > 0) {
                                    for (var i = 0, len = res.data.results.length; i < len; i++) {
                                        var $li = $("<li>", {"class": "li_data_label"}).append(
                                            $("<a>").append(res.data.results[i].text)
                                        );
                                        $li.data('labelUuid', res.data.results[i].id);
                                        $li.insertBefore(_self.$labelUl.find('#newLabelAndMark'));
                                    }

                                }
                            }

                            if(_self.$labelBtnGrp.is('button')){
                                _self.$labelBtnGrp.parent().addClass('open');
                            }
                        }
                    });
                }

            });

            //新建标签并标记事件
            $("#newLabelAndMark", this.$labelUl).on('click', function () {
                var uuids = _self.tableWidget.getSelectionPrimaryKeys();
                if (uuids.length == 0) {
                    appModal.info('请选中需要标记的数据行');
                    return;
                }

                _self.popDialog(_self.labelDialogHtml(), '新建标签并标记', {
                    confirm: {
                        label: "确定", className: "btn-primary", callback: function () {
                            var labelData = {
                                "moduleId": _self.options.markDataDefine.labelModuleId,
                                "labelName": $("#labelName").val(),
                                "labelColor": $("#labelColor").val()
                            };
                            var markResult = false;
                            appModal.showMask('数据标记中...');
                            server.JDS.call({
                                service: "dmsDataLabelFacadeService.addLabelAndRelaDataEntity",
                                data: [labelData, uuids, _self.relaEntityClassName],
                                version: '',
                                async: false,
                                success: function (res) {
                                    markResult = res.success;
                                    if (markResult) {
                                        _self.loadTableTrLabel(_self.tableWidget.getSelectionIndexes());
                                    }
                                    appModal.hideMask();
                                },
                                error: function () {
                                    appModal.hideMask();
                                }
                            });
                            return markResult;
                        }
                    },
                    cancel: {
                        label: "取消", className: "btn-default", callback: function () {

                        }
                    }
                }, function () {
                    _self.miniColorsPluginInit($("#labelColor"));
                });
            });

            //取消选中行的所有标签
            $("#removeAllLabel", this.$labelUl).on('click', function () {
                var indexes = _self.tableWidget.getSelectionIndexes();
                if (indexes.length == 0) {
                    appModal.info('请选择需要取消标签的数据行');
                    return;
                }
                var labelRelaUuids = [];
                for (var i = 0; i < indexes.length; i++) {
                    var $trLabels = $("tr[data-index=" + indexes[i] + "]").find('.tr_label_close_x');
                    $trLabels.each(function (i) {
                        labelRelaUuids.push($(this).attr('label_rela_uuid'))
                    });
                }

                _self.removeLables(labelRelaUuids, indexes);
            });


            this.$labelUl.on('click', '.li_data_label', function (e) {
                var uuids = _self.tableWidget.getSelectionPrimaryKeys();
                if (uuids.length == 0) {
                    appModal.info('请选中需要标记的数据行');
                    return;
                }
                server.JDS.call({
                    service: "dmsDataLabelFacadeService.addLabelRelaEntity",
                    data: [uuids, $(this).data('labelUuid'), _self.relaEntityClassName],
                    version: '',
                    async: false,
                    success: function (res) {
                        if (res.success) {
                            //_self.tableWidget.refresh();
                            _self.loadTableTrLabel(_self.tableWidget.getSelectionIndexes());
                        }
                    }
                });

            });


        },


        /**
         * 移除数据上的标签
         * @param relaUuids
         */
        removeLables: function (relaUuids, trIndexs) {
            var _self = this;
            server.JDS.call({
                service: "dmsDataLabelFacadeService.removeLableOfDataByRelaUuidsAndRelaEntityClass",
                data: [relaUuids, _self.relaEntityClassName],
                version: '',
                async: true,
                success: function (res) {
                    if (res.success) {
                        //_self.loadTableTrLabel(trIndexs);
                        _self.tableWidget.refresh();
                        appModal.info('取消标签成功');
                    }
                }
            });
        },


        labelDialogHtml: function () {
            var $table = $("<table>", {
                "class": "table table-hover table-striped"
            });
            var $labelTr = $("<tr>").append($("<td>").html('请输入标签名称'));
            var $inputTr = $("<tr>");
            $inputTr.append($("<td>").append(
                $("<div>", {"class": "label_color_select_div"}).append($("<input>", {
                    "type": "hidden",
                    "id": "labelColor",
                    "style": ""
                })),
                $("<input>", {
                    "type": "text", "id": "labelName", "maxlength": 32,
                    "class": "label_name_input"
                })
            ));
            $table.append($labelTr, $inputTr);
            return $table[0].outerHTML;
        },

        miniColorsPluginInit: function ($target, tag) {
            var _self = this;
            var swatches = "#90caf9|#ef9a9a|#a5d6a7|#fff59d|#ffcc80|#bcaaa4|#eeeeee|#f44336|#2196f3|#4caf50|#ffeb3b|#ff9800|#795548|#9e9e9e";
            var defaultValue = tag ? tag.tagColor : '#90caf9';
            $target.minicolors({
                control: 'hue',
                defaultValue: defaultValue,
                format: 'hex',
                position: 'bottom left',
                letterCase: 'lowercase',
                swatches: swatches.split("|"),
                change: function (value, opacity) {
                },
                hide: function () {
                },
                theme: 'bootstrap'
            });
        },


        /**
         * 弹窗
         * @param html  弹窗内容html
         * @param title  标题
         * @param buttons 按钮配置
         * @param shownCallback 弹窗展示后的回调函数
         */
        popDialog: function (html, title, buttons, shownCallback) {
            return appModal.dialog({
                title: title,
                message: html,
                size: 'small',
                buttons: buttons,
                shown: function () {
                    if ($.isFunction(shownCallback)) {
                        shownCallback();
                    }
                }
            });
        },
    };


    var ClassifyMark = {
        options: null,
        classifyMarks: null,
        tableWidget: null,
        init: function (options) {
            this.options = options;
            this.classifyMarks = options.markDataDefine.classifyMarks;
            if (!this.classifyMarks || this.classifyMarks.length == 0) {
                return;
            }
            this.tableWidget = options.tableWidget;
            this.loadButtonGroup();
            this.setEvent();
        },

        loadButtonGroup: function () {
            var _self = this;
            for (var i = 0, len = this.classifyMarks.length; i < len; i++) {
                var callButtonDataService = function (mk) {

                    var renderButtonGrp = function (data, mk) {
                        var $ul, $targetBtnContainer, button = mk.targetButton;
                        if ($(".btn_class_" + button.buttonCode).length > 0) {
                            $targetBtnContainer = $(".btn_class_" + button.buttonCode);
                            $targetBtnContainer.addClass("dropdown-toggle");
                            $targetBtnContainer.attr("data-toggle", "dropdown");
                            $(".btn_class_" + button.buttonCode).append($("<span>", {"class": "caret"}));
                            $targetBtnContainer.wrap($("<div>", {"class": "btn-group"}));
                            $ul = $("<ul>", {
                                "class": "dropdown-menu classify_dropdown_ul",
                                "role": "menu"
                            });
                            $ul.insertAfter($targetBtnContainer);

                        } else {
                            $targetBtnContainer = $(".li_class_" + button.buttonCode);
                            $targetBtnContainer.parent('ul').addClass('open_label_ul');
                            $targetBtnContainer.find('a').append($("<span>", {
                                "class": "iconfont icon-more label_li_more_icon"
                            }));
                            var $div = $("<div>", {
                                "style": "display:none;",
                                "class": "li_label_div"
                            });
                            $ul = $("<ul>", {
                                "class": "dropdown-menu classify_dropdown_ul hide",
                                "role": "menu"
                            });
                            $div.append($ul);
                            $targetBtnContainer.append($div);
                        }
                        if (mk.classifyMarkTable) {
                            $ul.data('classifyMarkTable', JSON.stringify(mk.classifyMarkTable));
                        }
                        if (mk.classifyMarkEntity) {
                            $ul.data('classifyMarkEntity', mk.classifyMarkEntity.entityClassName);
                        }
                        $ul.attr('dataRelaType', mk.dataRelaType);

                        for (var d = 0, dLen = data.length; d < dLen; d++) {
                            _self.createButtonGrp(data[d], $ul, button.menuStyle, 0);
                        }
                    }
                    if (mk.targetButton.sourceType == 'classifyTableConfig') {//分类表配置
                        server.JDS.call({
                            service: "dmsDataClassifyFacadeService.listClassifyNodes",
                            data: [mk.classifyTable.classifyDbTable, mk.classifyTable.unqiueColumn
                                , mk.classifyTable.parentColumn, mk.classifyTable.displayColumn, null],
                            version: '',
                            async: true,
                            success: function (res) {
                                if (res.success) {
                                    renderButtonGrp(res.data, mk);
                                }
                            }
                        });

                    } else if (mk.targetButton.sourceType == 'interface') {
                        $.ajax({
                            type: "POST",
                            url: ctx + "/basicdata/treecomponent/loadTree",
                            data: {
                                dataProvider: mk.targetButton.buttonProvider,
                                intfParams: mk.targetButton.params
                            },
                            async: true,
                            dataType: "json",
                            success: function (data) {
                                if (data && data.length > 0) {
                                    renderButtonGrp(data, mk);
                                }
                            }
                        });
                    } else if (mk.targetButton.sourceType == 'datastore') {
                        server.JDS.call({
                            async: true,
                            service: "cdDataStoreService.loadTreeNodes",
                            data: [{
                                dataStoreId: mk.targetButton.datastoreId,
                                uniqueColumn: mk.targetButton.uniqueKeyColumn,
                                parentColumn: mk.targetButton.parentKeyColumn,
                                displayColumn: mk.targetButton.displayColumn,
                                defaultCondition: mk.targetButton.defaultCondition
                            }],
                            success: function (result) {
                                if (result.success) {
                                    renderButtonGrp(result.data, mk);
                                }
                            }
                        });
                    }


                };
                callButtonDataService(this.classifyMarks[i])
            }
        },

        createButtonGrp: function (data, $childContainer, menuStyle, level) {
            var uuid = data.id;
            if (data.data && data.data.uuid) {
                uuid = data.data.uuid;
            }
            var $li = $("<li>", {
                "class": "li_classify_data",
                "uuid": uuid,
                "menu-style": menuStyle,
            }).append(
                $("<a>").append($("<span>", {
                    "class": data.data && data.data.icon ? data.data.icon : '',
                    "style": data.data && data.data.iconStyle ? data.data.iconStyle : ''
                }), data.name)
            );
            if (data.data && data.data.unclickable) {//不可进行分类关系点击事件
                $li.attr('unclickable', true);
            }


            if (menuStyle == 'vertical_tree') {
                $li.css('padding-left', 5 * level + 'px');
            }

            $childContainer.append($li);
            if (data.children.length > 0) {
                $childContainer.addClass('open_label_ul');
                if (menuStyle == 'horizontal_tree') {
                    $li.find('a').append($("<span>", {
                        "class": "iconfont icon-more li_expand label_li_more_icon"
                    }));
                } else {
                    $li.find('a').append($("<span>", {
                        "class": "iconfont icon-more li_expand label_li_more_icon_down"
                    }));
                }

                $li.addClass('li_has_children');
                var $div = $("<div>", {
                    "style": "display:none;",
                    "class": "li_label_div"
                });
                var $container;
                if (menuStyle == 'horizontal_tree') {
                    var $ul = $("<ul>", {
                        "class": "dropdown-menu classify_dropdown_ul hide",
                        "role": "menu"
                    });
                    $div.append($ul);
                    $container = $ul;
                } else {
                    $div.addClass('li_label_div_vertical');
                    $container = $div;
                }
                $li.append($div);
                level++;
                for (var i = 0, len = data.children.length; i < len; i++) {
                    this.createButtonGrp(data.children[i], $container, menuStyle, level);
                }
            }
        },


        setEvent: function () {
            var _self = this;
            /**
             * 标签组按钮展示用户定义的所有标签
             */
            this.tableWidget.pageContainer.off('click', '.li_classify_data,.li_expand');
            this.tableWidget.pageContainer.on('click', '.li_classify_data', function (e) {
                var _this = this;
                if ($(e.target).is('.li_expand')) {//图标点击展开或者折叠
                    return false;
                }

                if ($(this).attr('unclickable') == 'true') {
                    return false;
                }

                var uuids = _self.tableWidget.getSelectionPrimaryKeys();
                if (uuids.length == 0) {
                    appModal.info('请选中需要操作的数据行');
                    return false;
                }
                var classifyMarkEntityName = $(this).parents('ul').data('classifyMarkEntity');
                var classifyMarkTable = $(this).parents('ul').data('classifyMarkTable');
                var dataRelaType = $(this).parents('ul').attr('dataRelaType');
                server.JDS.call({
                    service: "dmsDataClassifyFacadeService.addClassifyRelaByTable",
                    data: [uuids, $(this).attr('uuid'), classifyMarkTable, dataRelaType],
                    version: '',
                    async: true,
                    success: function (res) {
                        if (res.success) {
                            _self.tableWidget.refresh();
                            appModal.info('数据操作成功');
                            $('.btn-group.open').trigger('click');
                        }
                    }
                });


                return false;

            });
            this.tableWidget.pageContainer.on('click', '.li_expand', function (e) {
                var $li = $(this).parent().parent();
                var isShow = $li.attr('aria-expanded') != undefined && $li.attr('aria-expanded') == 'true';
                var $labelDiv = $li.children(".li_label_div");
                if ($labelDiv.length == 0) {
                    return;
                }
                //查找所有同级展开的按钮组，进行折叠
                var $expandedLi = $li.siblings('.li_has_children[aria-expanded=true]');
                $expandedLi.each(function (k) {
                    $expandedLi.trigger('unexpandAll')
                });

                if (isShow) {//折叠
                    $li.trigger('unexpandAll');
                } else {//展开
                    $labelDiv.show();
                    $labelDiv.children('.classify_dropdown_ul').removeClass('hide').addClass('show');
                    $li.attr('aria-expanded', true);
                    var treeType = $li.attr('menu-style');
                    $(this).css('transform', treeType == 'horizontal_tree' ? 'rotate(-180deg)' : 'rotate(-90deg)');
                }
                return false;


            });

            this.tableWidget.pageContainer.off('unexpandAll', '.li_has_children');
            this.tableWidget.pageContainer.on('unexpandAll', '.li_has_children', function (e) {
                var $childLabels = $(this).find('.li_label_div');
                $childLabels.hide();
                var $childLi = $(this).find('li[aria-expanded=true]');
                $childLi.attr('aria-expanded', false);
                var treeType = $(this).attr('menu-style');
                $(this).find('.li_expand').css('transform', treeType == 'horizontal_tree' ? 'rotate(0deg)' : 'rotate(90deg)');
                $(this).attr('aria-expanded', false);
                return false;

            });

            for (var i = 0, len = this.classifyMarks.length; i < len; i++) {
                var button = this.classifyMarks[i].targetButton;
                $(".li_class_" + button.buttonCode + ",.btn_class_" + button.buttonCode).on('click', function (e) {
                    if ($(this).attr('aria-expanded') == 'true') {
                        $(this).next().children('.li_has_children').trigger('unexpandAll');
                    }
                });
            }

        },


    };


    var StatusMark = {
        options: null,
        statusMarks: null,
        tableWidget: null,
        init: function (options) {
            this.options = options;
            this.statusMarks = options.markDataDefine.statusMarks;
            if (this.statusMarks && this.statusMarks.length > 0) {
                this.tableWidget = options.tableWidget;//表格组件
                this.setEvent();
            }

        },

        setEvent: function () {
            var _self = this;
            //表格数据加载完成后进行数据关联的标签加载
            this.options.tableWidget.$tableElement.on('post-body.bs.table', function (e) {
                _self.loadStatusMarks();
            });
            _self.loadStatusMarks();
        },

        loadStatusMarks: function () {
            var _self = this;
            for (var i = 0, len = _self.statusMarks.length; i < len; i++) {
                MarkUtil.renderMarkDataTr(_self.options, _self.statusMarks[i], function (param, mark) {
                    if (mark.markDataType.markType == 'table') {//数据库表字段状态标记
                        param.tableName = mark.markDataType.databaseTable;
                        param.statusColumn = mark.markDataType.statusColumn;
                        param.updateTimeColumn = mark.markDataType.updateTimeColumn;

                    } else if (mark.markDataType.markType == 'entity') {//实体类状态标记
                        param.entityClassName = mark.markDataType.entityClassName;
                    }
                });
            }
        }
    };


    BootstrapTable.prototype.initTableMarkable = function (opt) {
        if (opt.markDataDefine &&
            (opt.markDataDefine.enableLabel
                || (opt.markDataDefine.statusMarks && opt.markDataDefine.statusMarks.length > 0)
                || (opt.markDataDefine.classifyMarks && opt.markDataDefine.classifyMarks.length > 0))) {
            if (!opt.tableWidget.getPrimaryColumnName()) {
                throw new Error("表格组件的列定义未配置主键列");
            }
            options = $.extend({}, options, opt);

            StatusMark.init(options);

            Label.init(options);

            ClassifyMark.init(options);

        }
    };


    /**
     * 标记方法：提供二开js主动调用进行标记
     * @param uuids 主键ID集合
     * @param markType {isDeleteMark: ,entityClassName:'',tableName:'',statusColumn:'',updateTimeColumn:''}
     * @param successCallback 成功标记的回调函数
     */
    BootstrapTable.prototype.dmsMarkRows = function (uuids, markType, successCallback) {
        var _self = this;
        var param = {
            dataList: []
        };

        for (var k in uuids) {
            param.dataList.push({
                'dataUuid': uuids[k]
            });
        }
        $.extend(param, markType);

        server.JDS.call({
            service: "dmsDataMarkFacadeService.saveOrDeleteMark",
            data: [param],
            version: '',
            async: true,
            success: function (res) {
                if (res.success) {
                    if ($.isFunction(successCallback)) {
                        successCallback();
                    } else {
                        _self.refresh();
                        appModal.info('数据操作成功');
                    }
                }
            }
        });
    };


    $.fn.bootstrapTable.methods.push("initTableMarkable");//初始化标签加入bootstrap方法组内，否则无法调用
    $.fn.bootstrapTable.methods.push("dmsMarkRows");


}));