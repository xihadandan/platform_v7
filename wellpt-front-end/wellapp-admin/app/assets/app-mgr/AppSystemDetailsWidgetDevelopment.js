define([ "constant", "commons", "server", "appModal", "AppPtMgrDetailsWidgetDevelopment", "AppPtMgrCommons" ],
        function(constant, commons, server, appModal, AppPtMgrDetailsWidgetDevelopment, AppPtMgrCommons) {
            var JDS = server.JDS;
            var Browser = commons.Browser;
            var StringUtils = commons.StringUtils;
            var Validation = server.Validation;
            var SecurityUtils = server.SpringSecurityUtils;

            // 系统视图列表
            var listView = null;
            // 表单验证器
            var validator = null;
            // 主题
            // var themes = {};

            // 平台管理_产品集成_系统详情_HTML组件二开
            var AppSystemDetailsWidgetDevelopment = function() {
                AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
                // AppSystem的VO类
                this.bean = {
                    uuid : null, // UUID
                    recVer : null, // 版本号
                    code : null, // 编号
                    enabled : null, // 是否启用
                    id : null, // ID
                    name : null, // 名称
                    title : null, // 标题
                    remark : null, // 备注
                    theme : null, // 系统默认主题
                    jsModule : null,// 加载自定义JavaScript模块，以模块化的形式开发JavaScript，多个以逗号隔开
                    systemUnitId : null,// 归属系统单位ID
                    appProductUuid : null,// 产品UUID
                    appPiUuid : null
                // 集成信息UUID
                };
            };

            // 接口方法
            commons.inherit(AppSystemDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
                // 组件初始化
                init : function() {
                    var _self = this;
                    var widget = _self.getWidget();
                    var $container = $(widget.element);
                    // 验证器
                    validator = Validation.validate({
                        beanName : "appSystem",
                        container : widget.element,
                        wrapperForm : true
                    });

                    // 主题
                    $("#themeName",$container).wSelect2({
                        serviceName : "appThemeMgr",
                        labelField : "themeName",
                        valueField : "theme",
                        defaultBlank : false,
                        remoteSearch : false,
                        width : "100%",
                        height : 250
                    });
                    // 归属产品
                    $("#appProductName").wSelect2({
                        serviceName : "appProductMgr",
                        labelField : "appProductName",
                        valueField : "appProductUuid",
                        defaultBlank : true,
                        remoteSearch : false,
                        width : "100%",
                        height : 250
                    });
                    // 绑定事件
                    _self._bindEvents();
                },
                _bindEvents : function() {
                    var _self = this;
                    var widget = _self.getWidget();
                    // 获取传给组件的参数
                    var params = _self.getWidgetParams();
                    var $container = $(widget.element);
                    var parentWidget = _self.getParentWidget();
                    // 监听系统列表事件
                    // 新增
                    parentWidget.on("AppSystemListView.addRow", function(e, ui) {
                        // 清空表单
                        AppPtMgrCommons.clearForm({
                            container : $container,
                            includeHidden : true
                        });
                        $("#theme",$container).val('default');// 主题：默认
                        $("#themeName",$container).trigger('change');
                        listView=e.detail.ui;
                        // 归属产品——从全部系统导航新增的可为空，从产品系统配置新增的不可编辑
                        if (StringUtils.isNotBlank(params.appProductUuid)) {
                            $("#appProductUuid", $container).val(params.appProductUuid);
                            $("#appProductName", $container).select2("disable")
                        } else {
                            $("#appProductUuid", $container).val(_self.bean.appProductUuid);
                        }
                        $("#appProductName", $container).trigger("change");
                        // 生成ID
                        AppPtMgrCommons.idGenerator.generate($("#id", $container), "sys_");
                        // ID可编辑
                        $("#id", $container).prop("readonly", "");
                        // 显示第一个tab内容
                        $(".nav-tabs>li>a:first", $container).tab("show");
                    });
                    // 删除行
                    parentWidget.on("AppSystemListView.deleteRow", function(e, ui) {
                        AppPtMgrCommons.clearForm({
                            includeHidden : true,
                            container : $container
                        });
                    });
                    // 行点击
                    parentWidget.on("AppSystemListView.clickRow", function(e, ui) {
                        _self.bean = $.extend(true, {}, e.detail.rowData);
                        AppPtMgrCommons.json2form({
                            json : _self.bean,
                            container : $container
                        });
                        listView = ui;
                        $("#themeName", $container).trigger("change");
                        $("#appProductName", $container).trigger("change");
                        $("#appProductName", $container).select2("enable");
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
                            json : _self.bean,
                            container : $container
                        });
                        JDS.call({
                            service : "appSystemManager.saveDto",
                            data : [ _self.bean ],
                            version : "",
                            success : function(result) {
                                appModal.success("保存成功！");
                                // 保存成功刷新列表
                                _self.refresh(listView);
                            }
                        });
                    });
                }
            });
            return AppSystemDetailsWidgetDevelopment;
        });