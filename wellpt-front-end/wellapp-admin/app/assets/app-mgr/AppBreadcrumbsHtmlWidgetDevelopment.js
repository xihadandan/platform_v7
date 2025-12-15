define([ "constant", "commons", "server", "appContext", "appModal", "HtmlWidgetDevelopment" ], function(constant,
        commons, server, appContext, appModal, HtmlWidgetDevelopment) {
    var JDS = server.JDS;
    var StringBuilder = commons.StringBuilder;
    var widgetParams = {};
    var navDataMap = {};
    // 产品集成开发及管理页面组件ID
    var appTypeWidgetIdMap = {
        0 : "wBootgrid_12_C876657A7F100001B83665C05D001336",// 产品集成_产品管理_一行一列布局
        1 : "wBootgrid_12_C876732E2760000129391D10575D13A4",// 产品集成_系统管理_一行一列布局
        2 : "wBootgrid_12_C8769E0A5C200001A7EB44401053F9F0"// 产品集成_模块管理_一行一列布局
    }
    // 页面组件二开基础
    var AppBreadcrumbsHtmlWidgetDevelopment = function() {
        HtmlWidgetDevelopment.apply(this, arguments);
    };
    // 接口方法
    commons.inherit(AppBreadcrumbsHtmlWidgetDevelopment, HtmlWidgetDevelopment, {
        // 初始化
        init : function() {
            var _self = this;
            var pageContainer = _self.getPageContainer();
            pageContainer.on("AppListView.prepare", function(e, ui) {
                widgetParams = e.detail;
                $("ul.breadcrumb").parents(".ui-wPanel").css({"margin-bottom":"0"});
                _self.loadBreadcrumbs(widgetParams);
            });
        },
        // 加载面包屑导航
        loadBreadcrumbs : function(params) {
            var _self = this;
            JDS.call({
                service : "appBreadcrumbsFacadeService.getAppBreadcrumbsDataList",
                data : [ params ],
                async : false,
                version : "",
                success : function(result) {
                    console.log(result.data);
                    _self.showBreadcrumbs(result.data, params);
                }
            });
        },
        // 显示面包屑导航
        showBreadcrumbs : function(dataList, params) {
            var _self = this;
            var widget = _self.getWidget();
            console.log(111,_self.element)
            var length = dataList.length;
            if (length == 0) {
                return;
            }
            // 过滤导航数据
            dataList = _self.filterNavData(dataList, params);
            length = dataList.unshift(_self.getPageTypeNavInfo());
            var sb = new StringBuilder();
            navDataMap = {};
            $.each(dataList, function(i, nav) {
                navDataMap[nav.navId] = nav;
                if (i == length - 1) {
                    sb.appendFormat('<li class="active">{0}</li>', nav.label);
                } else {
                    sb.appendFormat('<li><a href="#" navId="{0}" navType="{1}">{2}</a></li>', nav.navId, nav.appType,
                            nav.label);
                }
            });
            $(".row-header>ul.breadcrumb", widget.element).html(sb.toString());

            // 绑定导航事件
            _self.bindBreadcrumbsEvents();
        },
        // 过滤导航数据
        filterNavData : function(dataList, params) {
            var _self = this;
            var pageType = $("input[name='pageType']").val();
            var navs = [];
            if (pageType == "0") {
                navs = dataList;
            } else {
                for (var i = 0; i < dataList.length; i++) {
                    var data = dataList[i];
                    if ((_self.rootAppPiUuid != null && _self.rootAppPiUuid == data.appPiUuid)
                            || data.appPiUuid == params.appPiUuid) {
                        navs = dataList.slice(i);
                        // 初始化产品集成根结点数据
                        if (_self.rootAppPiUuid == null) {
                            _self.rootAppPiUuid = data.appPiUuid;
                        }
                        break;
                    }
                }
            }
            return navs;
        },
        getPageTypeNavInfo : function() {
            var pageType = $("input[name='pageType']").val();
            var pageRootNav = {}
            if (pageType == "0") {
                pageRootNav = {
                    label : "产品",
                    navId : "product"
                };
            } else if (pageType == "1") {
                pageRootNav = {
                    label : "系统",
                    navId : "system"
                };
            } else if (pageType == "2") {
                pageRootNav = {
                    label : "模块",
                    navId : "module"
                };
            } else if (pageType == "3") {
                pageRootNav = {
                    label : "应用",
                    navId : "application"
                };
            }
            return pageRootNav;
        },
        bindBreadcrumbsEvents : function() {
            var _self = this;
            var widget = _self.getWidget();
            $(widget.element).on("click", ".row-header>ul.breadcrumb>li>a", function(e) {
                e.preventDefault();
                var navType = $(this).attr("navType");
                var navId = $(this).attr("navId");
                if (navId == "product") {
                    _self.openProductListPage();
                } else if (navId == "system") {
                    _self.openSystemListPage();
                } else if (navId == "module") {
                    _self.openModuleListPage();
                } else if (navId == "application") {
                    _self.openApplicationListPage();
                } else if (appTypeWidgetIdMap[navType] != null) {
                    _self.renderWidget(navDataMap[navId], appTypeWidgetIdMap[navType]);
                }
            });
        },
        // 打开产品列表页面
        openProductListPage : function() {
            var _self = this;
            var winOptions = {
                id : "product",
                name : "产品",
                target : "_self",
                url : ctx + "/web/app/pt-mgr/pt-pi-mgr/pt-pi-product-mgr.html"
            };
            appContext.getWindowManager().open(winOptions);
        },
        // 打开系统列表页面
        openSystemListPage : function() {
            var _self = this;
            var winOptions = {
                id : "system",
                name : "系统",
                target : "_self",
                url : ctx + "/web/app/pt-mgr/pt-pi-mgr/pt-pi-system-mgr.html"
            };
            appContext.getWindowManager().open(winOptions);
        },
        // 打开模块列表页面
        openModuleListPage : function() {
            var _self = this;
            var winOptions = {
                id : "module",
                name : "模块",
                target : "_self",
                url : ctx + "/web/app/pt-mgr/pt-pi-mgr/pt-pi-module-mgr.html"
            };
            appContext.getWindowManager().open(winOptions);
        },
        // 打开应用列表页面
        openApplicationListPage : function() {
            var _self = this;
            var winOptions = {
                id : "application",
                name : "应用",
                target : "_self",
                url : ctx + "/web/app/pt-mgr/pt-pi-mgr/pt-pi-app-mgr.html"
            };
            appContext.getWindowManager().open(winOptions);
        },
        renderWidget : function(nav, widgetId) {
            var _self = this;
            var widgetDefinition = appContext.getWidgetDefinition(widgetId);
            var targetWidget = appContext.getWidgetByCssSelector(".app-pi-mgr-container");
            var params = {
                appProductUuid : nav.appProductUuid,
                appSystemUuid : nav.appSystemUuid || "",
                appPiUuid : nav.appPiUuid || "",
                parentAppPiUuid : nav.parentAppPiUuid || ""
            };
            if (nav.appType == 1) {
                // 系统级
                params.systemId = nav.value;
                params.appId = nav.value;
                params.appProdIntegrateType = '系统';
            } else if (nav.appType == 2) {
                // 模块级
                params.moduleId = nav.value;
                params.appId = nav.value;
                params.appProdIntegrateType = '模块';
            } else if (nav.appType == 3) {
                // 应用级
                params.appId = nav.value;
                params.appProdIntegrateType = '应用';
            }
            if (widgetDefinition != null && targetWidget != null) {
                var renderOptions = {
                    renderTo : "#" + targetWidget.getId(),
                    widgetDefId : widgetDefinition.id,
                    refreshIfExists : false,
                    params : params,
                };
                appContext.renderWidget(renderOptions);
            } else {
                console.error("render widget has error by widget id " + widgetId);
            }
        }
    });
    return AppBreadcrumbsHtmlWidgetDevelopment;
});