define([ "constant", "commons", "server", "appContext", "appModal", "ListViewWidgetDevelopment" ], function(constant,
        commons, server, appContext, appModal, ListViewWidgetDevelopment) {
    // 页面组件二开基础
    var AppPtMgrListViewWidgetDevelopment = function() {
        ListViewWidgetDevelopment.apply(this, arguments);
    };
    // 接口方法
    commons.inherit(AppPtMgrListViewWidgetDevelopment, ListViewWidgetDevelopment, {
        // 触发组件事件
        trigger : function(type, eventData) {
            var _self = this;
            var widget = _self.getWidget();
            widget.trigger(type, eventData);
        },
        // 获取传给组件的参数
        getWidgetParams : function() {
            var _self = this;
            var widget = _self.getWidget();
            var params = widget.options.widgetDefinition.params || {};
            return params;
        },
        // 检查选择的记录
        checkSelection : function(multiple) {
            var _self = this;
            var indexes = _self.getSelectionIndexes();
            if (indexes.length == 0) {
                appModal.error("请选择记录！");
                return false;
            }
            if (multiple !== true && indexes.length > 1) {
                appModal.error("请选择一条记录！");
                return false;
            }
            return true;
        },

        refresh: function () {
            //刷新徽章
            var tabpanel = this.widget.element.parents('.active');
            if (tabpanel.length > 0) {
                var id = tabpanel.attr('id');
                id = id.substring(0, id.indexOf('-'));
                var $tabWidgetElement = $("#" + id);
                var activeTabName = $tabWidgetElement.find('.nav:eq(0) > li.active > a')[0].childNodes[0].nodeValue;
                $tabWidgetElement.trigger(constant.WIDGET_EVENT.BadgeRefresh, {
                    targetTabName: activeTabName,
                });
            }
            return this.getWidget().refresh(this.options);
        },
    });
    return AppPtMgrListViewWidgetDevelopment;
});