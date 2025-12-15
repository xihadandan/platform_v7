define(["jquery", "commons", "constant", "server", "appContext", "DmsListViewViewAction", "appModal"],
    function ($, commons, constant, server, appContext, DmsListViewViewAction, appModal) {
        // 数据标记_列表操作
        var DmsDataMarkListViewAction = function () {
            DmsListViewViewAction.apply(this, arguments);
        }
        commons.inherit(DmsDataMarkListViewAction, DmsListViewViewAction, {
            /**
             * 标记数据
             */
            btn_list_mark_data: function (options, otherParam) {
                var markDataDefine = appContext.getWidgetById($(options.ui.element).data('dms_id')).getConfiguration().markDataDefine;
                var _self = this;
                var callInvoke = function (options, mark, isDeleteMark) {
                    _self.invoke(options, (function () {
                        var param = $.extend({isDeleteMark: isDeleteMark}, otherParam);
                        if (mark.markDataType.markType == 'table') {//数据库表字段状态标记
                            param.tableName = mark.markDataType.databaseTable;
                            param.statusColumn = mark.markDataType.statusColumn;
                            param.updateTimeColumn = mark.markDataType.updateTimeColumn;
                        } else if (mark.markDataType.markType == 'entity') {//实体类状态标记
                            param.entityClassName = mark.markDataType.entityClassName;
                        }
                        return param;
                    })(), mark.text);
                }
                if (markDataDefine) {
                    var statusMarks = markDataDefine.statusMarks;
                    for (var i = 0, len = statusMarks.length; i < len; i++) {
                        var btnClass = otherParam && otherParam.isDeleteMark ? statusMarks[i].unmarkButton : statusMarks[i].markButton;
                        if (btnClass) {
                            if ($(options.event.target).is('.btn_class_' + btnClass)
                                || $(options.event.target).parent().is('.li_class_' + btnClass)) {
                                callInvoke(options, statusMarks[i]);
                                break;
                            }
                        }

                    }


                }
                return false;
            },

            /**
             * 取消标记数据
             */
            btn_list_unmark_data: function (options) {
                this.btn_list_mark_data(options, {isDeleteMark: true})
                return false;
            },

            invoke: function (options, params, actionName) {
                var _self = this;
                var ui = options.ui;
                if (options.rowData.length == 0) {
                    appModal.info('请选择行数据');
                    return false;
                }
                var markData = {
                    dataList: []
                };

                var pcn = ui.getPrimaryColumnName();
                $.extend(markData, params);

                markData.dataList = $.map(options.rowData, function (r) {
                    return r[pcn] ? {dataUuid: r[pcn]} : null;
                });
                if ($.isEmptyObject(markData.dataList)) {
                    return false;
                }

                options.data = markData;
                var urlParams = {
                    ac_id: options.appFunction.id
                };
                options.urlParams = urlParams;
                options.success = function (result) {
                    appModal.info((params.isDeleteMark ? '取消' : '') + actionName + '操作成功');
                    options.ui.refresh(true);
                };
                options.failure = function () {
                    appModal.error((params.isDeleteMark ? '取消' : '') + actionName + '操作成功');
                    appModal.hideMask();
                }
                appModal.showMask('数据操作中，请稍后...');
                _self.dmsDataServices.performed(options);
            }


        });

        return DmsDataMarkListViewAction;
    });