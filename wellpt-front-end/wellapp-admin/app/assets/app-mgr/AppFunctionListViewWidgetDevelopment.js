define([ "constant", "commons", "server", "appContext", "appModal", "formBuilder", "AppPtMgrListViewWidgetDevelopment",
        "AppPtMgrCommons" ], function(constant, commons, server, appContext, appModal, formBuilder,
        AppPtMgrListViewWidgetDevelopment, AppPtMgrCommons) {
    var UUID = commons.UUID;
    var StringUtils = commons.StringUtils;
    var JDS = server.JDS;
    var Validation = server.Validation;
    var SelectiveDatas = server.SelectiveDatas;

    // 平台管理_产品集成_功能列表_视图组件二开
    var AppFunctionListViewWidgetDevelopment = function() {
        AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
    };

    // 接口方法
    commons.inherit(AppFunctionListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
        // 组件准备
        prepare : function() {
            var _self = this;
            _self.addWidgetParamCondition();
        },
        addWidgetParamCondition : function() {
            var _self = this;
            var widget = _self.getWidget();
            // 获取传给组件的参数
            var params = _self.getWidgetParams();
            var otherConditions = [];
            // 归属产品集成信息UUID
            if (StringUtils.isNotBlank(params.appPiUuid)) {
                var condition = {
                    columnIndex : "parentAppPiUuid",
                    value : params.appPiUuid,
                    type : "eq"
                };
                otherConditions.push(condition);
            }
            widget.addOtherConditions(otherConditions);
        },
        afterRender : function() {
            var _self = this;
            var $element = $(_self.widget.element);
            var $searchInput = $element.find("input[type='search']");
            var $select = $("<select/>");
            $select.append($("<option>", {
                "value" : ""
            }).text("全部"));
            var items = SelectiveDatas.getItems("PT_APP_FUNCTION_TYPE");
            $.each(items, function(i, item) {
                var option = "<option value='" + item.value + "'>" + item.label + "</option>";
                $select.append(option);
            });
            var $div = $("<div>", {
                "class" : "pull-right search"
            }).append($select);
            $div.insertAfter($searchInput.parent());
            // 类型变更选择
            $select.on("change", function() {
                var type = $(this).val();
                var functionTypeCondition = {
                    columnIndex : "type",
                    value : type,
                    type : "eq"
                };
                _self.widget.clearOtherConditions();
                // 添加组件参数的查询条件
                _self.addWidgetParamCondition();
                if (StringUtils.isNotBlank(type)) {
                    _self.widget.addOtherConditions([ functionTypeCondition ]);
                }
                _self.widget.refresh(true);
            });
        },
        // 行点击的时候回调方法，子类可覆盖
        onClickRow : function(rowNum, row, $element, field, event) {
            if (field !== 'isProtected' || event.target.className !== 'protected-checkbox') return;

            JDS.call({
                service : "appFunctionManager.updatePiFunction",
                data : [row.appPiUuid, event.target.checked],
                async : false,
                version : "",
                success : function(result) {
                    appModal.success("权限更改成功！");
                }
            });
            
        },
        // 添加
        btn_add : function() {
            var _self = this;
            var widget = _self.getWidget();
            // 获取传给组件的参数
            var params = _self.getWidgetParams();
            _self.showAddFunctionDialog(function(data) {
                JDS.call({
                    service : "appFunctionManager.addPiFunction",
                    data : [ params.appPiUuid, data ],
                    async : false,
                    version : "",
                    success : function(result) {
                        appModal.success("添加成功！");
                        _self.refresh(true);
                    }
                });
            });
        },
        showAddFunctionDialog : function(callback) {
            var _self = this;
            var dataList = _self.getData();
            var excludeDataUuids = $.map(dataList, function(data) {
                return data.uuid;
            });
            appContext.require([ "AppFunctionDialog" ], function(AppFunctionDialog) {
                AppFunctionDialog.show({
                    excludeDataUuids : excludeDataUuids,
                    callback : callback
                });
            });
        },
        // 取消引用
        btn_cancel_ref : function(e, options, rowData) {
            var _self = this;
            appModal.confirm("确认取消引用？", function(result) {
                if (result) {
                    JDS.call({
                        service : "appFunctionManager.removePiFunction",
                        data : [ rowData.appPiUuid ],
                        async : false,
                        success : function(result) {
                            appModal.success("取消成功！");
                            _self.refresh(true);
                        }
                    });
                }
            });
        },

    });
    return AppFunctionListViewWidgetDevelopment;
});