define([ "jquery", "jquery-ui", "bootstrap", "server", "commons", "appContext", "appWindowManager", "appModal",
        "AppPortalConfigurer" ], function($, ui, bootstrap, server, commons, appContext, appWindowManager, appModal,
        AppPortalConfigurer) {
    var UUID = commons.UUID;
    var Browser = commons.Browser;
    var StringUtils = commons.StringUtils;
    var StringBuilder = commons.StringBuilder;

    // 门户信息
    var portalInfo = getSourcePagePortalInfo();
    // 1、创建页面设计器对象并初始化
    var pageDesigner = new AppPortalConfigurer({
        widgetTitleEditable : false,// 组件标题不可变更
        widgetConfigable : false,// 组件不可配置
        isJsonViewer : false,// 关闭JSON查看器
        isWysiwyg : true,// 开启所见即所得
        portal : portalInfo,// 门户信息
        onCreate : function() {
            var _self = this;
            init(_self, portalInfo);
            bindEvents(_self);
            _self.initPortal();
        }
    });

    // 2、初始化
    function init(pageDesigner, portalInfo) {
        var $container = $(".designer-sidebar-nav");
        // 初始化基本信息
        initBaseInfo(portalInfo, pageDesigner, $container);
        // 初始化布局
        initLayoutsInfo(portalInfo, pageDesigner, $container);
        // 初始化组件应用
        initAppsInfo(portalInfo, pageDesigner, $container);
    }
    // 2.1、初始化基本信息
    function initBaseInfo(portalInfo, pageDesigner, $container) {
        var initDefinitionJson = JSON.parse(pageDesigner.initDefinitionJson);
        // 门户名称
        if (getPageUuid() == getSourcePageUuid()) {
            $("input[name='portal_name']", $container).val(portalInfo.name);
        } else {
            $("input[name='portal_name']", $container).val(initDefinitionJson.title);
        }
        // 门户主题
        if (StringUtils.isNotBlank(portalInfo.themes)) {
            var themeNames = portalInfo.themeNames.split(";");
            var themes = portalInfo.themes.split(";");
            for (var i = 0; i < themes.length; i++) {
                var $option = $("<option>", {
                    value : themes[i]
                }).text(themeNames[i]);
                $("select[name='portal_theme']", $container).append($option);
            }
            if (pageDesigner.appPageDefinition) {
                $("select[name='portal_theme']", $container).val(pageDesigner.appPageDefinition.theme);
            }
        }
    }
    // 2.2、初始化布局
    function initLayoutsInfo(portalInfo, pageDesigner, $container) {
        var $layoutsInfo = $(".portal-layouts-info", $container);
        var layouts = portalInfo.layouts || [];
        var sb = new StringBuilder();
        sb.append('<ul class="list-group">');
        $.each(layouts, function(i, layout) {
            var layoutId = layout.uuid;
            var title = layout.remark || layout.text;
            var imgPath = null;
            var li = '<li class="list-group-item" layoutId="{0}" title="{1}">{1}</li>';
            if (layout.icon && StringUtils.isNotBlank(layout.icon.iconPath)) {
                imgPath = ctx + layout.icon.iconPath;
                li = '<li class="list-group-item" layoutId="{0}" title="{1}">';
                li += '<div><img src="{2}" title="{1}"></img></div>';
                li += '</li>';
            }
            sb.appendFormat(li, layoutId, title, imgPath);
        });
        sb.append('</ul>');
        $layoutsInfo.append(sb.toString());
    }
    // 2.3、初始化组件应用
    function initAppsInfo(portalInfo, pageDesigner, $container) {
        var $appsInfo = $(".portal-apps-info", $container);
        var apps = portalInfo.apps || [];
        var sb = new StringBuilder();
        sb.append('<ul class="list-group">');
        $.each(apps, function(i, app) {
            var li = '<li class="list-group-item" appId={0}><i class="{2}"></i>{1}</li>';
            var iconClass = null;
            if (app.icon && StringUtils.isNotBlank(app.icon.className)) {
                iconClass = app.icon.className;
            }
            sb.appendFormat(li, app.uuid, app.text, iconClass);
        });
        sb.append('</ul>');
        $appsInfo.append(sb.toString());
    }

    // 3、绑定事件
    function bindEvents(pageDesigner) {
        var $designerContainer = $(".web-app-container");
        var $sidebarContainer = $(".designer-sidebar-nav");
        var container = pageDesigner.getPageContainer();
        // 保存门户
        $("#btn_save_portal", $sidebarContainer).on("click", function() {
            if (pageDesigner.isInEditState()) {
                appModal.error("门户配置处于编辑状态，不能进行保存，请先确定门户配置！");
                return;
            }
            // 门户名称判空
            var portalName = $("input[name='portal_name']", $sidebarContainer).val();
            if (StringUtils.isBlank(portalName)) {
                appModal.error("门户名称不能为空！");
                return;
            }
            // 门户主题
            var portalTheme = $("select[name='portal_theme']", $sidebarContainer).val();
            // 源页面UUID
            var sourcePageUuid = getSourcePageUuid();
            var portalPageUuid = null;
            // 页面定义JSON
            var definitionJson = container.getDefinitionJson.call(container, $designerContainer);
            // 页面定义HTML
            var html = container.toHtml.call(container, $designerContainer);
            definitionJson.html = html;
            // 门户页面UUID
            var pageUuid = getPageUuid();
            definitionJson.uuid = pageUuid;
            // 删除门户基础数据信息
            if (definitionJson.configuration) {
                delete definitionJson.configuration.portal;
            }
            JDS.call({
                service : "appPortalFacadeService.saveUserPageDefinitionJson",
                data : [ sourcePageUuid, portalName, portalTheme, JSON.stringify(definitionJson) ],
                async : false,
                success : function(result) {
                    portalPageUuid = result.data;
                    appModal.success("保存成功！", function() {
                        var url = ctx + "/web/app/portal/config/page/" + portalPageUuid;
                        appContext.getWindowManager().refreshParent();
                        appContext.getWindowManager().refresh(url);
                    });
                }
            });
        });
        // 放弃修改
        $("#btn_abandon_modify", $sidebarContainer).on("click", function() {
            if (pageDesigner.isWidgetDefinitionChanged()) {
                appModal.confirm("确定要放弃门户的更改？", function(result) {
                    if (result) {
                        appContext.getWindowManager().refresh();
                    }
                });
            } else {
                appContext.getWindowManager().refresh();
            }
        });
        // 返回主页
        $("#btn_return_homepage", $sidebarContainer).on("click", function() {
            var piUuid = pageDesigner.getPiUuid();
            var homePageUuid = $("#page_uuid").val();
            var previewUrl = ctx + "/web/app/portal/preview/" + piUuid + "?pageUuid=" + homePageUuid;
            if (pageDesigner.isWidgetDefinitionChanged()) {
                appModal.confirm("确定要放弃门户的更改并返回主页？", function(result) {
                    if (result) {
                        appContext.getWindowManager().refresh(previewUrl);
                    }
                });
            } else {
                appContext.getWindowManager().refresh(previewUrl);
            }
        });
    }

    // 获取源页面的门户配置信息
    function getSourcePagePortalInfo() {
        var pagePortalInfo = {};
        // 源页面UUID
        var sourcePageUuid = getSourcePageUuid();
        JDS.call({
            service : "appPortalFacadeService.getPagePortalInfoByPageUuid",
            data : [ sourcePageUuid ],
            async : false,
            success : function(result) {
                pagePortalInfo = JSON.parse(result.data);
            }
        });
        return pagePortalInfo;
    }

    // 获取源页面UUID
    function getSourcePageUuid() {
        return $("input[id='source_page_uuid']").val();
    }
    // 获取当前页面UUID
    function getPageUuid() {
        return $("input[id='page_uuid']").val();
    }
});
