define([ "server", "commons", "constant", "jquery-ui", "bootbox", "appContext" ], function(server, commons, constant,
        ui, bootbox, appContext) {
    var JDS = server.JDS;
    var UUID = commons.UUID;
    var UrlUtils = commons.UrlUtils;
    var StringBuilder = commons.StringBuilder;

    // 显示门户列表，默认为下拉列表，通过事件参数配置(list_style)配置1、下拉列表，2、弹框列表
    var showAppPortalList = function(options) {
        var params = options.params || {};
        if (params.list_style == "2") {
            // 弹框门户列表
            showDialogPortalList(options);
        } else {
            // 下拉门户列表
            showDropdownProtalList(options);
        }
    }

    function getPortalListHtml(options, callback) {
        var pageInfo2Html = function(dataList) {
            var sb = new StringBuilder();
            sb.append('<ul class="menu portal-menu" role="menu">');
            $.each(dataList, function(i, pageInfo) {
                // 当前门户的页面不需要显示在切换列表中
                var pageUuid = WebApp.pageDefinition.uuid || WebApp.currentUserAppData.pageUuid;
                if (pageInfo.uuid == pageUuid) {
                    return;
                }
                var uuid = pageInfo.uuid;
                var name = pageInfo.name;
                sb.append('<li role="presentation" class="portal-menu-item">');
                sb.appendFormat('<a pageUuid="{0}" href="#">{1}</a>', uuid, name);
                sb.append('</li>');
            });
            // 分隔线
            if (dataList.length > 0) {
                sb.append('<li role="presentation" class="divider"></li>');
            }
            // 门户管理
            sb.append('<li role="presentation">');
            sb.append('<a class="portal-mgr" href="#">门户管理</a>');
            sb.append('</li>');
            sb.append('</ul>');
            return sb.toString();
        }

        // 获取用户可访问的页面信息
        var dataList = [];
        var piUuid = WebApp.currentUserAppData.piUuid;
        JDS.call({
            service : "appPortalFacadeService.getUserAppPageDefinitionByPiUuid",
            data : [ piUuid ],
            async : false,
            version : "",
            success : function(result) {
                dataList = result.data;
            }
        });
        return pageInfo2Html(dataList);
    }

    // 显示下拉门户列表
    function showDropdownProtalList(options) {
        var $target = $(options.event.target);
        if (!$target.is("li") && $target.find("li").length == 0 && $target.closest("li").length > 0) {
            $target = $target.closest("li");
        }
        if ($target.is("[portal=true]") === false) {
            var portalListHtml = getPortalListHtml(options);
            $target.attr("portal", "true");
            $target.popover({
                html : true,
                content : portalListHtml,
                placement : "auto",
                trigger : "manual"
            });
            $target.popover("show");
            bindDropdownProtalEvents($target);
        }
        $target.popover("show");
    }

    // 显示弹出框门户列表
    function showDialogPortalList(options) {
        showPortalManagerDialog();
    }

    // 绑定下拉门户列表事件
    function bindDropdownProtalEvents($target) {
        // 隐藏弹出框
        $("body").on("mousedown", function(event) {
            if ($(event.target).parents("*[portal='true']").length <= 0) {
                $target.popover('hide');
            }
        });
        $target.parent().on("click", ".portal-menu-item", function() {
            $target.popover('hide');
            var pageUuid = $(this).children("a").attr("pageUuid");
            var appPath = WebApp.currentUserAppData.appPath;
            var previewUrl = ctx + "/web/app" + appPath + ".html?pageUuid=" + pageUuid;
            appContext.getWindowManager().refresh(previewUrl);
        });
        // 门户管理
        $target.parent().on("click", ".portal-mgr", function() {
            $target.popover('hide');
            showPortalManagerDialog();
        });
    }
    // 显示门户管理弹出框
    function showPortalManagerDialog() {
        var dlgId = UUID.createUUID();
        var title = "我的门户";
        var message = "<div id='" + dlgId + "'></div>";
        var dlgOptions = {
            title : title,
            message : message,
            templateId : "",
            size : "large",
            className : "dlg-my-portal-list",
            shown : function() {
                appContext.renderWidget({
                    renderTo : "#" + dlgId,
                    widgetDefId : "wBootstrapTable_C884234372B00001C330FEA033431A4B",
                    forceRenderIfConflict : true,
                    params : {
                        appPiUuid : WebApp.currentUserAppData.piUuid
                    }
                });
            }
        };
        appModal.dialog(dlgOptions);
    }
    return showAppPortalList;
});