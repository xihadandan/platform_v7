define(["commons", "constant", "server", "appContext", "GetCountBase"],
    function (commons, constant, server, appContext, GetCountBase) {
        // 平台管理_产品集成_获取模块化管理下的各Tab视图数据总数
        var StringUtils = commons.StringUtils;
        var GetModuleMgrTabBadgeCountDevelopment = function () {
            GetCountBase.apply(this, arguments);
        };
        commons.inherit(GetModuleMgrTabBadgeCountDevelopment, GetCountBase, {
            getCount: function (options) {
                var _self = this;
                if (StringUtils.isBlank(options.data.widgetDefId)) {
                    console.error("GetModuleMgrTabBadgeCountDevelopment widgetDefId is null");
                    return;
                }
                var configuration = _self.getConfiguration(options.data.widgetDefId);
                if (StringUtils.isBlank(configuration.dataStoreId)) {
                    console.error("GetModuleMgrTabBadgeCountDevelopment dataStoreId is null");
                    return;
                }
                var params = {};
                if (options.ui && options.ui.options.widgetDefinition.params) {
                    params = $.extend(true, {}, options.ui.options.widgetDefinition.params);
                }
                var tab = options.data.tab;

                appContext.require(["dataStoreBase"], function (DataStore) {
                    var _dataProvider = new DataStore({
                        dataStoreId: configuration.dataStoreId,
                        defaultCriterions: [].concat(_self.getDefaultConditions(configuration))
                            .concat(function () {
                                var otherConditions = [];
                                if (tab.name == '模块' || tab.name == '应用') {
                                    if (StringUtils.isNotBlank(params.appProductUuid)) {
                                        var condition = {
                                            columnIndex: "appProductUuid",
                                            value: params.appProductUuid,
                                            type: "eq"
                                        };
                                        otherConditions.push(condition);
                                    }
                                    // 归属系统UUID
                                    if (StringUtils.isNotBlank(params.appSystemUuid)) {
                                        var condition = {
                                            columnIndex: "appSystemUuid",
                                            value: params.appSystemUuid,
                                            type: "eq"
                                        };
                                        otherConditions.push(condition);
                                    }
                                    // 上级产品信息信息UUID
                                    if (StringUtils.isNotBlank(params.appPiUuid)) {
                                        var condition = {
                                            columnIndex: "parentAppPiUuid",
                                            value: params.appPiUuid,
                                            type: "eq"
                                        };
                                        otherConditions.push(condition);
                                    }
                                } else if (tab.name == '模块主页' || tab.name == '工作台'
                                    || tab.name == '页面') {
                                    if (StringUtils.isNotBlank(params.appPiUuid)) {
                                        var condition = {
                                            columnIndex: "appPiUuid",
                                            value: params.appPiUuid,
                                            type: "eq"
                                        };
                                        otherConditions.push(condition);
                                    }
                                } else if (tab.name == '系统') {
                                    var condition = {
                                        columnIndex: "appProductUuid",
                                        value: params.appProductUuid,
                                        type: "eq"
                                    };
                                    otherConditions.push(condition);
                                } else if (tab.name == '功能') {
                                    if (StringUtils.isNotBlank(params.appPiUuid)) {
                                        var condition = {
                                            columnIndex: "parentAppPiUuid",
                                            value: params.appPiUuid,
                                            type: "eq"
                                        };
                                        otherConditions.push(condition);
                                    }
                                }
                                return otherConditions;
                            }()),
                        params: params,//添加提供数据仓库查询的参数
                        onDataChange: function (data, count) {
                            _self.applyCallback(options, count);
                        }
                    });
                    _dataProvider.getCount(true);

                });
            },
            getConfiguration: function (widgetDefUuid) {
                return this.getWidgetDefinition(widgetDefUuid).configuration;
            },
            getDefaultConditions: function (configuration) {
                var defaultConditions = [];
                if (commons.StringUtils.isNotBlank(configuration.defaultCondition)) {
                    var criterion = {};
                    criterion.sql = configuration.defaultCondition;
                    defaultConditions.push(criterion);
                }
                return defaultConditions;
            },
            getWidgetDefinition: function (widgetDefId) {
                var widgetDefinition = appContext.getWidgetDefinition(widgetDefId);
                var definitionJson = null;
                if (widgetDefinition) {
                    definitionJson = JSON.parse(widgetDefinition.definitionJson);
                } else {
                    definitionJson = {
                        configuration: {}
                    };
                }
                return definitionJson;
            }
        });
        return GetModuleMgrTabBadgeCountDevelopment;
    });