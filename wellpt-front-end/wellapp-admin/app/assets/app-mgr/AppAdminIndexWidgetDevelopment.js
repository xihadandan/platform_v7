define([ "constant", "commons", "server", "appContext", "HtmlWidgetDevelopment","niceScroll" ],
    function(constant, commons, server,appContext,HtmlWidgetDevelopment,niceScroll) {
        var JDS = server.JDS;

        var AppAdminIndexWidgetDevelopment = function() {
            HtmlWidgetDevelopment.apply(this, arguments);
        };
        // 接口方法
        commons.inherit(AppAdminIndexWidgetDevelopment, HtmlWidgetDevelopment, {
            init: function() {
                var $element = this.widget.element;
                getDatas();

                $(".admin-home-item,.admin-home-detail-num",$element).off().on("click", function(e){
                    var menuid = $(this).data("menuid");
                    if(menuid){
                        jumpTo(menuid,e);
                    }
                })

                $.fn.niceScroll && $(".admin-home-exception",$element).niceScroll({
                    cursorcolor: "#ccc"
                })

                function jumpTo(menuid,e){
                    var leftNav = appContext.getWidgetByCssSelector(".ui-wLeftSidebar");
                    var event = leftNav.menuItemMap[menuid];
                    var opt = {
                        appPath: event.eventHanlderPath,
                        appType: event.eventHanlderType,
                        menuId: event.uuid,
                        menuItem: event,
                        onPrepare: {},
                        params: event.eventParams,
                        refreshIfExists: event.refreshIfExists,
                        renderNavTab: true,
                        target: event.targetPosition,
                        targetWidgetId: event.targetWidgetId,
                        text: event.name,
                        value: event.uuid,
                        event: e
                    }

                    opt.eventTarget = {
                        position: event.targetPosition,
                        widgetSelectorTypeName: event.targetWidgetSelectorTypeName,
                        widgetSelectorType: event.targetWidgetSelectorType || '1',
                        widgetName: event.targetWidgetName,
                        widgetId: event.targetWidgetId,
                        widgetCssClass: event.targetWidgetCssClass,
                        refreshIfExists: event.refreshIfExists
                    }
                    leftNav.startApp(opt);
                }

                function getDatas(){
                    JDS.call({
                        service: "adminHomeService.dataOverview",
                        version:"",
                        success: function (result) {
                            if(result.success){
                                var data = result.data;
                                var domList = $(".admin-show-data .admin-home-data-item",$element);
                                for(var i = 0; i < domList.length; i ++){
                                    var lis = $(domList[i]).find(".admin-home-data-detail .admin-home-detail-item");
                                    for(var j = 0; j < lis.length; j ++){
                                        $(lis[j]).find(".admin-home-detail-num").text(data[i].items[j].count).css({
                                            cursor:"pointer"
                                        });
                                    }
                                }
                            }
                        }
                    })
                }
            },
            refresh: function () {
                this.init()
            }
        })
        return AppAdminIndexWidgetDevelopment;
    })
