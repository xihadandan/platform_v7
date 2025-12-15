define(["constant", "commons", "server", "appModal", "AppPtMgrDetailsWidgetDevelopment", "AppPtMgrCommons"],
    function(constant, commons, server, appModal, AppPtMgrDetailsWidgetDevelopment, AppPtMgrCommons) {
        var JDS = server.JDS;
        var Browser = commons.Browser;
        var StringUtils = commons.StringUtils;
        var Validation = server.Validation;
        var SecurityUtils = server.SpringSecurityUtils;

        // 应用视图列表
        var listView = null;
        // 表单验证器
        var validator = null;

        // 平台管理_产品集成_应用详情_HTML组件二开
        var AppApplicationDetailsWidgetDevelopment = function() {
            AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
            // AppApplication的VO类
            this.bean = {
                uuid: null, // UUID
                recVer: null, // 版本号
                name: null, // 名称
                title: null, // 标题
                id: null, // ID
                code: null, // 编号
                enabled: null, // 是否启用
                configurable: null, // 标识该应用对于普通用户是否可配置
                type: null, // 应用类别 1(UI交互)、2 (服务)
                jsModule: null, // 加载自定义JavaScript模块，以模块化的形式开发JavaScript，多个以逗号隔开
                correlativeFunction: null, // 关联的功能ID
                remark: null, // 备注
                systemUnitId: null, // 归属系统单位ID
                belongToAppPiUuid: null, // 归属集成信息UUID
                appPiUuid: null
                    // 集成信息UUID
            };
        };

        // 接口方法
        commons.inherit(AppApplicationDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
            // 组件初始化
            init: function() {
                var _self = this;
                var widget = _self.getWidget();
                // 获取传给组件的参数
                var params = _self.getWidgetParams();
                // 验证器
                validator = Validation.validate({
                    beanName: "appApplication",
                    container: widget.element,
                    wrapperForm: true
                });
                // 关联的功能
                $("#correlativeFunctionName", widget.element).wSelect2({
                    serviceName: "appFunctionMgr",
                    queryMethod: "loadSelectDataForUuid",
                    selectionMethod: "loadSelectDataByUuids",
                    labelField: "correlativeFunctionName",
                    valueField: "correlativeFunction",
                    defaultBlank: true,
                    width: "100%",
                    height: 250
                });
                // 归属模块
                $("#belongToAppPiName", widget.element).wSelect2({
                    serviceName: "appModuleManager",
                    labelField: "belongToAppPiName",
                    valueField: "belongToAppPiUuid",
                    params: {
                        appPiUuid: params.appPiUuid,
                    },
                    defaultBlank: true,
                    remoteSearch: false,
                    width: "100%",
                    height: 250
                });
                // 绑定事件
                _self._bindEvents();
            },
            _bindEvents: function() {
                var _self = this;
                var widget = _self.getWidget();
                // 获取传给组件的参数
                var params = _self.getWidgetParams();
                var $container = $(widget.element);
                var parentWidget = _self.getParentWidget();
                // 监听应用列表事件
                // 新增
                parentWidget.on("AppApplicationListView.addRow", function(e, ui) {
                    // 清空表单
                    AppPtMgrCommons.clearForm({
                        container: $container,
                        includeHidden: true
                    });
                    listView = e.detail.ui;
                    // 归属模块集成信息不可变更
                    // 归属模块——从全部应用导航新增的可为空，从产品应用配置新增的应用不可编辑
                    if (StringUtils.isNotBlank(params.appPiUuid)) {
                        $("#belongToAppPiUuid", $container).val(params.appPiUuid);
                        $("#belongToAppPiName", $container).select2("disable")
                    } else {
                        $("#belongToAppSystemUuid", $container).val(_self.bean.parentAppPiUuid);
                    }
                    $("#belongToAppPiName", $container).trigger("change");
                    // 生成ID
                    AppPtMgrCommons.idGenerator.generate($("#id", $container), "app_");
                    $("#correlativeFunctionName", $container).trigger("change");
                    // ID可编辑
                    $("#id", $container).prop("readonly", "");
                    // 显示第一个tab内容
                    $(".nav-tabs>li>a:first", $container).tab("show");
                });
                // 删除行
                parentWidget.on("AppApplicationListView.deleteRow", function(e, ui) {
                    AppPtMgrCommons.clearForm({
                        includeHidden: true,
                        container: $container
                    });
                });
                // 行点击
                parentWidget.on("AppApplicationListView.clickRow", function(e, ui) {
                    _self.bean = $.extend(true, {}, e.detail.rowData);
                    // 归属集成信息UUID
                    _self.bean.belongToAppPiUuid = _self.bean.parentAppPiUuid;
                    AppPtMgrCommons.json2form({
                        json: _self.bean,
                        container: $container
                    });
                    listView = ui;
                    $("#belongToAppPiName", $container).select2("enable");
                    $("#belongToAppPiName", $container).trigger("change");
                    $("#correlativeFunctionName", $container).val(_self.bean.correlativeFunction);
                    $("#correlativeFunctionName", $container).trigger("change");
                    // ID只读
                    $("#id", $container).prop("readonly", "readonly");
                    validator.form();
                });

                // 保存
                $("#btn_save", $container).on("click", function() {
                    if (!validator.form()) {
                        return false;
                    }
                    AppPtMgrCommons.form2json({
                        json: _self.bean,
                        container: $container
                    });
                    _self.bean.parentAppPiUuid = _self.bean.parentAppPiUuid || params.appPiUuid;
                    JDS.call({
                        service: "appApplicationManager.saveDto",
                        data: [_self.bean],
                        version: "",
                        success: function(result) {
                            appModal.success("保存成功！");
                            // 保存成功刷新列表
                            _self.refresh(listView);
                        }
                    });
                });
            }
        });
        return AppApplicationDetailsWidgetDevelopment;
    });