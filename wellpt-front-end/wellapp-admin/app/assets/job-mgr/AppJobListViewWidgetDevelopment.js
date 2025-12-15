define(["constant", "commons", "server", "appContext", "appModal", "AppPtMgrListViewWidgetDevelopment",
    "AppPtMgrCommons"
], function(constant, commons, server, appContext, appModal,
    AppPtMgrListViewWidgetDevelopment, AppPtMgrCommons) {
    var StringUtils = commons.StringUtils;
    var JDS = server.JDS;

    // 平台管理_产品集成_模块_定时任务列表_视图组件二开
    var AppJobListViewWidgetDevelopment = function() {
        AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
    };

    // 接口方法
    commons.inherit(AppJobListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
        // 组件准备
        prepare: function() {},

        beforeRender: function(options, configuration) {
            // 归属模块ID
            this.widget.addParam('moduleId', this._moduleId())
        },


        afterRender: function(options, configuration) {
            var _self = this;
            this.widget.on('AppJobListView.refresh', function() {
                _self.refresh();
            });

            this.showJobStateOperationButtons();
        },

        refresh: function() {
            //刷新徽章
            var tabpanel = this.widget.element.parents('.active');
            if (tabpanel.length > 0) {
                var id = tabpanel.attr('id');
                id = id.substring(0, id.indexOf('-'));
                $("#" + id).trigger(constant.WIDGET_EVENT.BadgeRefresh, {
                    targetTabName: '定时任务',
                });
            }
            return this.getWidget().refresh(this.options);
        },


        _moduleId: function() {
            return this.getWidgetParams().moduleId
        },

        onLoadSuccess: function(data) {
            var rows = data.rows;
            //查询其他信息


        },

        // 删除
        btn_delete: function() {
            var _self = this;
            var rowData = _self.getSelectRowData();
            if (rowData.length > 0) {
                var name = rowData[0].name;
                appModal.confirm("确认要删除定时任务吗?", function(result) {
                    if (result) {
                        server.JDS.call({
                            service: "jobDetailsService.removeAll",
                            version: '',
                            data: [(function() {
                                var uuids = [];
                                for (var i = 0, len = rowData.length; i < len; i++) {
                                    uuids.push(rowData[i].uuid);
                                }
                                return uuids;
                            })()],
                            success: function(result) {
                                appModal.info("刪除成功");
                                _self.refresh(); //刷新表格
                            },
                            error: function(jqXHR) {
                                var faultData = JSON.parse(jqXHR.responseText);
                                appModal.alert(faultData.msg);
                            }
                        });
                    }
                });
            } else {
                appModal.error("请选择记录！");
                return false;
            }
        },

        getSelectRowData: function() {
            var _self = this;
            var $toolbarDiv = $(event.target).closest("div");
            var rowData = [];
            if ($toolbarDiv.is(".div_lineEnd_toolbar")) { //行级点击操作
                var index = $toolbarDiv.attr("index");
                var allData = _self.getData();
                rowData = [allData[index]];
            } else {
                if (_self.getSelectionIndexes().lengthh == 0) {
                    return [];
                }
                rowData = _self.getSelections();
            }
            return rowData;
        },


        btn_add: function() {
            this.showJobStateOperationButtons();
            this.widget.trigger("AppTaskJobListView.editRow", {
                ui: this.widget
            });
        },

        // 行点击查看详情
        onClickRow: function(rowNum, rowData, $element, field) {

            //查看状态，显示/隐藏相关按钮
            this.showJobStateOperationButtons(rowData.stateRenderValue);

            this.widget.trigger("AppTaskJobListView.editRow", {
                rowData: rowData,
                ui: this.widget
            });
        },

        definition_import: function() {
            var _self = this;
            // 定义导入
            $.iexportData["import"]({
                callback: function() {
                    _self.refresh();
                }
            });
        },

        definition_export: function() {
            // 定义导出
            var rowData = this.getSelectRowData();
            if (rowData.length > 0) {
                $.iexportData["export"]({
                    uuid: rowData[0].uuid,
                    type: 'jobDetails'
                });
            } else {
                appModal.alert('请选择导出的定时任务！');
            }

        },

        btn_start: function() {
            var rows = this.getSelectRowData();
            if (rows.length > 0) {
                this.jobStateToggle(rows[0].uuid, 'start');
            }
        },

        btn_stop: function() {
            var rows = this.getSelectRowData();
            if (rows.length > 0) {
                this.jobStateToggle(rows[0].uuid, 'stop');
            }
        },

        btn_pause: function() {
            var rows = this.getSelectRowData();
            if (rows.length > 0) {
                this.jobStateToggle(rows[0].uuid, 'pause');
            }
        },

        btn_recover: function() {
            var rows = this.getSelectRowData();
            if (rows.length > 0) {
                this.jobStateToggle(rows[0].uuid, 'resume');
            }
        },

        jobStateToggle: function(uuid, method) {
            var _self = this;
            server.JDS.call({
                service: "jobDetailsService." + method,
                version: '',
                data: [uuid],
                success: function(result) {
                    if (result.success) {
                        appModal.info('操作成功');
                        _self.refresh();
                    }
                },
                async: false
            });
        },

        showJobStateOperationButtons: function(state) {
            $(".btn_class_btn_start,.btn_class_btn_stop,.btn_class_btn_pause,.btn_class_btn_recover", this.widget.element).addClass('hide');
            if (state == '已停止' || state == '已完成' || state == 'STOPED' || state == 'FINISHED') {
                $(".btn_class_btn_start", this.widget.element).removeClass('hide');
            } else if (state == '运行中' || state == '阻塞中' || state == '错误' || state == 'RUNNING' || state == 'BLOCKING' || state == 'ERROR') {
                $(".btn_class_btn_stop", this.widget.element).removeClass('hide');
                $(".btn_class_btn_pause", this.widget.element).removeClass('hide');
            } else if (state == '暂停中' || state == 'PAUSED') {
                $(".btn_class_btn_recover", this.widget.element).removeClass('hide');
            }
        },


    });
    return AppJobListViewWidgetDevelopment;
});