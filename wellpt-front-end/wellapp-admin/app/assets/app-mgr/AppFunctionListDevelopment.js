define([ "constant", "commons", "server", "appContext","appModal", "formBuilder", "ListViewWidgetDevelopment" ],
    function(constant, commons, server,appContext,appModal, formBuilder, ListViewWidgetDevelopment) {
        var StringUtils = commons.StringUtils;
        var StringBuilder = commons.StringBuilder;
        var JDS = server.JDS;

        // 平台应用_产品集成开发及管理_功能列表二开
        var AppFunctionListDevelopment = function() {
            ListViewWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppFunctionListDevelopment, ListViewWidgetDevelopment, {
            // 准备创建回调
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
            // 创建后回调
            create: function () {
                // 开发环境不可同步所有功能
                var env = SystemParams.getValue("spring.profiles.active");
                if (env === "dev") {
                    var widget = this.getWidget();
                    widget.element.find(".btn_class_btn_sync").hide();
                }
            },
            // 初始化回调
            init: function() {
                var _self = this;
                // 功能类型
                var items = SelectiveDatas.getItems("PT_APP_FUNCTION_TYPE");
                var functionTypes = $.map(items, function(item) {
                    return {
                        aceId: item.aceId,
                        id: item.value,
                        text: item.label
                    }
                });

                var $search = _self.getWidget().element.find('.keyword-search-wrap');
                var $typeSelect = $('<input>',{
                    id: 'queryFields',
                    "class": 'query-fields',
                    style: 'width: 140px'
                });
                var $typeSelectWrap = $('<div>',{
                    "class": 'pull-left',
                    style: 'width: 140px'
                });
                $typeSelectWrap.append($typeSelect);
                $search.before($typeSelectWrap);
                $typeSelect.wSelect2({
                    valueField: 'queryFields',
                    searchable: false,
                    defaultBlank: true,// 默认空选项
                    defaultBlankText: "全部",// 默认空选项的显示值
                    defaultBlankValue:'',
                    style: {'height':'34px'},
                    data: functionTypes
                });

                $typeSelect.on('change',function() {
                    var $this = $(this);
                    var type = $this.val();
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
                })
            },

            //同步本地系统功能
            btn_sync_local: function (event,opt) {
                var _self = this;
                var functionType = _self.getWidget().element.find('.queryFields').val();
                appModal.showMask();
                JDS.call({
                    service : "appFunctionService.syncAppFunctionSources",
                    data : [ functionType, false ],
                    version:"",
                    success : function(result) {
                        appModal.hideMask();
                        appModal.alert("系统功能同步成功!");
                    }
                });
            },
            //同步系统所有功能
            btn_sync: function (event,opt) {
                var _self = this;
                var functionType = _self.getWidget().element.find('.queryFields').val();
                appModal.showMask();
                JDS.call({
                    service : "appFunctionService.syncAppFunctionSources",
                    data : [ functionType, false ],
                    version:"",
                    success : function(result) {
                        appModal.hideMask();
                        appModal.alert("系统功能同步成功!");
                    }
                });
            },
            getWidgetParams : function() {
                var _self = this;
                var widget = _self.getWidget();
                var params = widget.options.widgetDefinition.params || {};
                return params;
            }
        });
        return AppFunctionListDevelopment;
    });

