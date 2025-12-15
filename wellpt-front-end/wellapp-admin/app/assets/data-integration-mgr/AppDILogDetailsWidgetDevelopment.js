define(["constant", "commons", "server", "appModal", "AppPtMgrDetailsWidgetDevelopment", "AppPtMgrCommons"],
    function (constant, commons, server, appModal, AppPtMgrDetailsWidgetDevelopment, AppPtMgrCommons) {
        var listView;

        var AppDILogDetailsWidgetDevelopment = function () {
            AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
        };

        // 接口方法
        commons.inherit(AppDILogDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
            // 组件初始化
            init: function () {
                var _self = this;

                _self.initProcessorLogTable();

                _self.setEvent();
            },

            setEvent: function () {
                var _self = this;
                var widget = _self.getWidget();
                var $container = $(widget.element);
                var pageContainer = _self.getPageContainer();
                // 新增
                pageContainer.off('AppDIProcessorLog.showDetails');
                pageContainer.on("AppDIProcessorLog.showDetails", function (e) {

                    server.JDS.call({
                        service: "diDataProcessorLogFacadeService.getInterationLogDto",
                        data: [e.detail.rowData.UUID],
                        version: '',
                        success: function (result) {
                            $container.find('#integrationException').val(result.data ? result.data.exception : '');
                        }
                    });


                    server.JDS.call({
                        service: "diDataProcessorLogFacadeService.listLogsByExchangeId",
                        data: [e.detail.rowData.EXCHANGE_ID],
                        version: '',
                        success: function (result) {
                            if (result.data) {
                                result.data.splice(0, 1);//排除“开始的处理器”
                            }
                            _self.$table.bootstrapTable('load', result.data || []);
                        }
                    });

                    // 显示第一个tab内容
                    $(".nav-tabs>li>a:first", $container).tab("show");

                    listView = e.detail.ui;

                });
            },

            initProcessorLogTable: function () {
                var _self = this;
                var widget = _self.getWidget();
                var $container = $(widget.element);
                var $processorLogTable = $("#table_processor_log_info", $container);
                _self.$table = $processorLogTable;
                $processorLogTable.bootstrapTable("destroy").bootstrapTable({
                    data: [],
                    idField: "uuid",
                    uniqueId: "uuid",
                    pagination: true,
                    striped: false,
                    showColumns: false,
                    width: 500,
                    pageSize: 25,
                    search: true,
                    columns: [{
                        field: "checked",
                        checkbox: true
                    }, {
                        field: "uuid",
                        title: "UUID",
                        visible: false
                    }, {
                        field: "processorName",
                        title: "过程名称",
                    }, {
                        field: "createTime",
                        title: "执行时间",
                    }, {
                        field: "timeConsuming",
                        title: "耗时",
                        formatter: function (value, row, index) {
                            if (value) {
                                return _self.timeValueReadable(parseInt(value));
                            }
                        }
                    }, {
                        field: 'operation',
                        title: '操作',
                        formatter: function (value, row, index) {
                            var $btn = $("<button>", {
                                "class": "btn btn-sm btn-primary btn-p-log-details",
                                "data-uuid": row.uuid
                            }).text('交换数据详情');
                            return $btn[0].outerHTML;
                        }
                    }]
                });

                $processorLogTable.on('click', '.btn-p-log-details', function () {
                    var uuid = $(this).attr('data-uuid');
                    var $dialog, dialogOpts = {
                        title: '详情',
                        message: '<div style="max-height:500px;overflow-y: auto;overflow-x: hidden;" ' +
                        'class="form-horizontal">' + _self.processorDealReportHtml() + '</div>',
                        shown: function () {
                            server.JDS.call({
                                service: "diDataProcessorLogFacadeService.getProcessorLogDetails",
                                version: '',
                                data: [uuid],
                                success: function (result) {
                                    if (result.data) {
                                        $dialog.find('#beforeProcess textarea').val(result.data.inMessage);
                                        $dialog.find('#afterProcess textarea').val(result.data.outMessage);
                                    }
                                },
                                error: function (jqXHR) {

                                }
                            });
                        },
                        size: "large",

                    };
                    $dialog = appModal.dialog(dialogOpts);
                    return false;
                });

            },


            processorDealReportHtml: function () {
                return "<ul class=\"nav nav-tabs\" role=\"tablist\">\n" +
                    "        <li role=\"presentation\" class=\"active\">\n" +
                    "            <a href=\"#beforeProcess\" aria-controls=\"beforeProcess\" role=\"tab\" data-toggle=\"tab\" aria-expanded=\"true\">处理前</a>\n" +
                    "        </li>\n" +
                    "\t\t<li role=\"presentation\" class=\"\">\n" +
                    "            <a href=\"#afterProcess\" aria-controls=\"afterProcess\" role=\"tab\" data-toggle=\"tab\" aria-expanded=\"true\">处理后</a>\n" +
                    "        </li>\n" +
                    "    </ul>\n" +
                    "    <div class=\"tab-content\">\n" +
                    "        <div role=\"tabpanel\" class=\"tab-pane active\" id=\"beforeProcess\" style=\"padding-top: 10px;\">\n" +
                    "            <textarea rows=30 class=\"form-control\"></textarea>\n" +
                    "        </div>\n" +
                    "\n" +
                    "\t\t <div role=\"tabpanel\" class=\"tab-pane\" id=\"afterProcess\" style=\"padding-top: 10px;\">\n" +
                    "            <textarea rows=30 class=\"form-control\"></textarea>\n" +
                    "        </div>\n" +
                    "    </div>";
            },

            timeValueReadable: function (v) {
                if (v < 1000) {
                    return v + "毫秒";
                } else if (v >= 1000 && v < 1000 * 60) {
                    return Math.floor(v / 1000) + "秒" + this.timeValueReadable(v % 1000);
                } else if (v >= 1000 * 60 && v < 1000 * 60 * 60) {
                    return Math.floor(v / 60000) + "分" + this.timeValueReadable(v % 60000);
                }
                return "";
            }


        });
        return AppDILogDetailsWidgetDevelopment;
    });