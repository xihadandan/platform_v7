(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'constant'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, constant) {
  'use strict';
  $.widget('ui.wFooter', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {}
    },
    _createView: function () {
      this._renderView();
      this._bindEvent();
    },
    _renderView: function () {
      // 生成页面组件
      var self = this;
      var options = self.options;
      var widgetDefinition = options.widgetDefinition;
      // 生成页面组件
      var toYear = new Date().getFullYear();
      var copyright = '<p class="text-center"><strong>Copyright</strong>©2003-${toYear} 厦门威尔信息技术有限公司 版权所有</p>';
      copyright = widgetDefinition.content || copyright;
      $(this.element).html(copyright.replace('${toYear}', toYear));
      //计算底部组件的高度，调整页面容器的底部padding
      window.setTimeout(function () {
        appContext.getPageContainer().element.css('padding-bottom', $(self.element).height());
      }, 10);
    },
    _bindEvent: function () {
      var self = this;

      // bug#51055: 底部组件配置后前端页面显示的位置不正确
      // this.pageContainer.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function () {
      //   // 容器
      //   // 调整掉底部的位置样式，随内容的高度多少，底部下移
      //   $(self.element).css('position', 'absolute');
      // });
    }
  });
});
