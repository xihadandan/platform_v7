define([ "constant", "commons", "server", "appContext", "appModal", "formBuilder", "ListViewWidgetDevelopment" ],
        function(constant, commons, server, appContext, appModal, formBuilder, ListViewWidgetDevelopment) {
            var StringUtils = commons.StringUtils;
            var JDS = server.JDS;
            var UUID = commons.UUID;
            var Validation = server.Validation;

            // 平台管理_产品集成_门户_我的门户_视图组件二开
            var AppMyPortalListViewWidgetDevelopment = function() {
                ListViewWidgetDevelopment.apply(this, arguments);
            };

            // 接口方法
            commons.inherit(AppMyPortalListViewWidgetDevelopment, ListViewWidgetDevelopment, {
                // 组件准备
                prepare : function() {
                    var _self = this;
                    var widget = _self.getWidget();
                    // 获取传给组件的参数
                    var params = _self.getWidgetParams();
                    var otherConditions = [];
                    // 产品信息信息UUID
                    if (StringUtils.isNotBlank(params.appPiUuid)) {
                        var condition = {
                            columnIndex : "appPiUuid",
                            value : params.appPiUuid,
                            type : "eq"
                        };
                        otherConditions.push(condition);
                    }
                    widget.addOtherConditions(otherConditions);
                },
                // 获取传给组件的参数
                getWidgetParams : function() {
                    var _self = this;
                    var widget = _self.getWidget();
                    var params = widget.options.widgetDefinition.params || {};
                    return params;
                },
                onPostBody : function(dataList) {
                    var _self = this;
                    var widget = _self.getWidget();
                    var $element = $(widget.element);
                    $.each(dataList, function(i, data) {
                        if (data.shared == true) {
                            var $row = $element.find("tr[data-index='" + i + "']");
                            $row.find(".btn_class_btn_set_as_default").remove();
                            $row.find(".btn_class_btn_config").remove();
                            $row.find(".btn_class_btn_delete").remove();
                        }
                    });
                },
                // 新增门户
                btn_add : function() {
                    var _self = this;
                    var sourcePageUuid = WebApp.pageDefinition.uuid || WebApp.currentUserAppData.pageUuid;
                    JDS.call({
                        service : "appPortalFacadeService.getPortalCorrelativePageUuidByPageUuid",
                        data : [ sourcePageUuid ],
                        async : false,
                        success : function(result) {
                            sourcePageUuid = result.data;
                        }
                    });
                    var configUrl = ctx + "/web/app/portal/config/page/" + sourcePageUuid;
                    appContext.getWindowManager().open({
                        url : configUrl,
                        ui : _self.getWidget()
                    });
                },
                // 设为默认
                btn_set_as_default : function(e, options, rowData) {
                    var _self = this;
                    JDS.call({
                        service : "appPortalFacadeService.updateUserDefaultPortalByPageUuid",
                        data : [ rowData.uuid ],
                        async : false,
                        success : function(result) {
                            appModal.success("设置成功！");
                            _self.refresh(true);
                        }
                    });
                },
                // 访问
                btn_visit : function(e, options, rowData) {
                    var piUuid = rowData.appPiUuid;
                    var pageUuid = rowData.uuid;
                    var previewUrl = ctx + "/web/app/portal/preview/" + piUuid + "?pageUuid=" + pageUuid;
                    appContext.getWindowManager().open(previewUrl);
                },
                // 配置
                btn_config : function(e, options, rowData) {
                    var piUuid = rowData.appPiUuid;
                    var pageUuid = rowData.uuid;
                    var configUrl = ctx + "/web/app/portal/config/page/" + pageUuid;
                    appContext.getWindowManager().open(configUrl);
                },
                // 删除
                btn_delete : function(e, options, rowData) {
                    var _self = this;
                    appModal.confirm("确认要删除个人门户[" + rowData.name + "]吗？", function(result) {
                        if (result) {
                            JDS.call({
                                service : "appPortalFacadeService.deleteUserPortalByPageUuid",
                                data : [ rowData.uuid ],
                                async : false,
                                success : function(result) {
                                    appModal.success("删除成功！");
                                    _self.refresh(true);
                                }
                            });
                        }
                    });
                }
            });
            return AppMyPortalListViewWidgetDevelopment;
        });