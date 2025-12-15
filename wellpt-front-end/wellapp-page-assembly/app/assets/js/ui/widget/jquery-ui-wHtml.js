(function (factory) {
    "use strict";
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define(['jquery', 'commons', 'server', 'constant'], factory);
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function ($, commons, server, constant) {
    "use strict";
    $.widget("ui.wHtml", $.ui.wWidget, {
        options: {
            // 组件定义
            widgetDefinition: {},
            // 上级容器定义
            containerDefinition: {}
        },
        _createView: function () {
            this._renderView();
        },
        _renderView: function () {
            // 生成页面组件
            var options = this.options;
            var configuration = options.widgetDefinition.configuration || {};
            var _self = this;
            if (configuration.htmlSourceType === 'html_from_project_code_page') {
                $.ajax({
                    type: 'GET',
                    url: ctx + "/html"+configuration.selectPage,
                    dataType: 'html',
                    context: _self.element,
                    async: false,
                    success: function (html) {
                        $(this).html(html);
                    }
                });
            }


            // 监听容器创建完成事件
            this.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function (e, ui) {
            });

            // 监听左导航事件
            this.on(constant.WIDGET_EVENT.LeftSidebarItemClick, function (e, ui) {
            });

            // 滚动条
            // $(_self.element).slimScroll();
        },
        refresh: function () {
            var _self = this;
            if(_self.develops) {
            	$.each(_self.develops,function (i,item) {
                    item.refresh && item.refresh();
               })
            }
        }
    });
}));