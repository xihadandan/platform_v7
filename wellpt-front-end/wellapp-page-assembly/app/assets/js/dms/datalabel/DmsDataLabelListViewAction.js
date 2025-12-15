define(["jquery", "commons", "constant", "server", "appContext", "DmsListViewViewAction", "appModal", "minicolors"],
    function ($, commons, constant, server, appContext, DmsListViewViewAction, appModal, minicolors) {
        // 数据管理标签_视图列表操作
        var DmsDataLabelListViewAction = function () {
            DmsListViewViewAction.apply(this, arguments);
        };

        commons.inherit(DmsDataLabelListViewAction, DmsListViewViewAction, {

            //新建标签
            btn_list_add_dms_data_label: function (options, isEdit) {
                var _self = this;
                if (!isEdit && (options.params == undefined || options.params.moduleId == undefined)) {
                    // appModal.error('未配置事件参数moduleId');
                    // throw new Error('未配置事件参数moduleId');
                    options.params.moduleId = appContext.getCurrentUserAppData().appData.module ? appContext.getCurrentUserAppData().appData.module.id
                        : null;
                    if (options.params.moduleId == null && appContext.getCurrentUserAppData().appData.dispatchAppPath) {
                        options.params.moduleId = appContext.getCurrentUserAppData().appData.dispatchAppPath.split("/")[2];
                    }
                }
                var labelRow = isEdit ? options.rowData[0] : null;
                var actionText = isEdit ? '编辑标签' : '新建标签';
                var $dialog = _self.popDialog(_self.labelDialogHtml(labelRow), actionText, {
                    confirm: {
                        label: "确定", className: "btn-primary", callback: function () {
                            var labelData = {
                                "moduleId": _self.options.params.moduleId,
                                "labelName": $("#labelName").val(),
                                "labelColor": $("#labelColor").val()
                            };
                            if (labelRow) {
                                labelData.uuid = labelData.uuid || labelRow.UUID;
                            }
                            if ($.trim(labelData.labelName).length == 0) {
                                appModal.info('请输入标签名称');
                                return false;
                            }
                            options.data = labelData;
                            var urlParams = {
                                ac_id: options.appFunction.id
                            };
                            options.urlParams = urlParams;
                            options.success = function (result) {
                                if (result.success) {
                                    $dialog.find(".bootbox-close-button").trigger("click");
                                }
                                _self.dmsDataServices.onPerformedResult(result, options);
                                options.ui.invokeDevelopmentMethod('refreshLeftSideMenus');
                            };
                            options.failure = function () {
                                appModal.hideMask();
                            }
                            appModal.showMask(actionText + '中，请稍后...');
                            _self.dmsDataServices.performed(options);
                            return false;
                        }
                    },
                    cancel: {
                        label: "取消", className: "btn-default", callback: function () {

                        }
                    }
                }, function () {
                    _self.miniColorsPluginInit($("#labelColor"), isEdit && options.rowData ? options.rowData[0].LABEL_COLOR : null);
                });

                return false;
            },

            /**
             * 编辑标签
             * @param options
             */
            btn_list_edit_dms_data_label: function (options) {
                this.btn_list_add_dms_data_label(options, true);
            },

            /**
             * 删除标签
             * @param options
             */
            btn_list_delete_dms_data_label: function (options) {
                var _self = this;
                if (!options.rowData || options.rowData.length == 0) {
                    appModal.info('请选中要删除的标签行');
                    return false;
                }

                var $dialog = _self.popDialog(_self.labelDeleteTip(), '删除标签', {
                    confirm: {
                        label: "确定", className: "btn-primary", callback: function () {
                            options.data = $.map(options.rowData, function (r, i) {
                                return r.uuid || r.UUID;
                            });
                            var urlParams = {
                                ac_id: options.appFunction.id
                            };
                            options.urlParams = urlParams;
                            options.success = function (result) {
                                if (result.success) {
                                    $dialog.find(".bootbox-close-button").trigger("click");
                                }
                                _self.dmsDataServices.onPerformedResult(result, options);
                                options.ui.invokeDevelopmentMethod('refreshLeftSideMenus');
                            };
                            options.failure = function () {
                                appModal.hideMask();
                            }
                            appModal.showMask('删除标签中，请稍后...');
                            _self.dmsDataServices.performed(options);
                            return false;
                        }
                    },
                    cancel: {
                        label: "取消", className: "btn-default", callback: function () {

                        }
                    }
                }, function () {
                });

                return false;
            },

            labelDeleteTip: function () {
                var $table = $("<table>", {
                    "class": "table js-deleteConfirmTable"
                });
                var $labelTr = $("<tr>").append($("<td>").html('是否确定删除标签'));
                var $labelTr2 = $("<tr>").append($("<td>").html('已经标记了该标签的数据也将移除此标签(数据不会被删除)'));
                $table.append($labelTr, $labelTr2);
                return $table[0].outerHTML;
            },


            labelDialogHtml: function (data) {
                var $table = $("<table>", {
                    "class": "table table-hover table-striped"
                });
                var $labelTr = $("<tr>").append($("<td>").html('请输入标签名称'));
                var $inputTr = $("<tr>");
                $inputTr.append($("<td>").append(
                    $("<div>", {"class": "label_color_select_div"}).append($("<input>", {
                        "type": "hidden", "id": "labelColor",
                        "value": data ? data.LABEL_COLOR : ""
                    })),
                    $("<input>", {
                        "type": "text", "id": "labelName", "maxlength": 32,
                        "class": "label_name_input", "value": data ? data.LABEL_NAME : ""
                    })
                ));
                $table.append($labelTr, $inputTr);
                return $table[0].outerHTML;
            },

            miniColorsPluginInit: function ($target, defaultColor) {
                var _self = this;
                var swatches = "#90caf9|#ef9a9a|#a5d6a7|#fff59d|#ffcc80|#bcaaa4|#eeeeee|#f44336|#2196f3|#4caf50|#ffeb3b|#ff9800|#795548|#9e9e9e";
                var defaultValue = defaultColor ? defaultColor : '#90caf9';
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


        });

        return DmsDataLabelListViewAction;
    });