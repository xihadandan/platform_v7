define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment
) {
  // 页面组件二开基础
  var AppPtMgrDetailsWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppPtMgrDetailsWidgetDevelopment, HtmlWidgetDevelopment, {
    // 获取传给组件的参数
    getWidgetParams: function () {
      var _self = this;
      var widget = _self.getWidget();
      var params = widget.options.widgetDefinition.params || {};
      return params;
    },
    // 刷新组件
    refresh: function (widget) {
      if (widget) {
        widget.refresh(true);
        widget.trigger('RefreshView');
      }
    }
  });
  return AppPtMgrDetailsWidgetDevelopment;
});
