(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'commons', 'server', 'constant', 'appContext', 'appModal', 'dataStoreBase'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, commons, server, constant, appContext, appModal, DataStore) {
  'use strict';
  var Browser = commons.Browser;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var SpringSecurityUtils = server.SpringSecurityUtils;

  $.widget('ui.wCarousel', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {}
    },
    _createView: function () {
      var _self = this;
      var options = $.extend({}, _self.options.widgetDefinition, _self.options.widgetDefinition.configuration);
      _self.$element = $(_self.element);
      _self.$elementParentWidth = _self.$element.parent().width() - 30;
      _self._beforeRenderView();
      // 渲染组件内容
      _self._renderView(options);
      // 绑定组件事件
      _self._bindEvents(options);
      _self._setEvent(options);
    },
    /**
     * 渲染视图前
     */
    _beforeRenderView: function () {
      var _self = this;
      // _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
    },
    _renderView: function (options) {
      // 渲染组件内容
      var _self = this;
      var html = new StringBuilder();
      var _indicators = new StringBuilder();
      var _items = new StringBuilder();
      var _len = options.tabs.length;

      _self.$element.addClass(options.customClass);

      $.each(options.tabs, function (i, v) {
        _indicators.appendFormat('<li data-target="#carousel" data-slide-to="{0}" class="{1}"></li>', i, i === 0 ? 'active' : '');
        _items.appendFormat(
          '<div class="item {0}">' +
            '<img src="{1}" alt="{2}" style="height: {3}px">' +
            '<div class="carousel-caption">{4}' +
            '</div>' +
            '</div>',
          i === 0 ? 'active' : '',
          v.filePath,
          v.name,
          options.carousel_height,
          v.caption
        );
      });

      html.appendFormat(
        '<div id="carousel" class="carousel slide" data-ride="carousel" style="height: {0}">' +
          '  <ol class="carousel-indicators">' +
          _indicators +
          '  </ol>' +
          '  <div class="carousel-inner" role="listbox">' +
          _items +
          '  </div>' +
          '  <a class="left carousel-control" href="#carousel" role="button" data-slide="prev">' +
          '    <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>' +
          '    <span class="sr-only">Previous</span>' +
          '  </a>' +
          '  <a class="right carousel-control" href="#carousel" role="button" data-slide="next">' +
          '    <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>' +
          '    <span class="sr-only">Next</span>' +
          '  </a>' +
          '</div>',
        options.carousel_height + 'px'
      );
      _self.$element.html(html + '');
    },
    _bindEvents: function (options) {
      var _self = this;
      var _len = options.tabs.length;
      // 绑定组件事件
      $('.carousel', _self.$element).carousel({
        interval: options.carousel_interval ? parseInt(options.carousel_interval) * 1000 : 5000
      });

      // 绑定点击事件
      $('.carousel', _self.$element).on('click', '.item', function () {
        var index = $('.carousel-inner .item', _self.$element).index(this);
        var tab = options.tabs[index];
        var handler = tab.eventHandler;
        var opt = {
          target: '_blank',
          appType: handler.type,
          appPath: handler.path
        };
        _self.startApp(opt);
      });
    },
    _setEvent: function (options) {
      var _self = this;
      var $element = $(_self.element);
      var pageContainer = _self.getPageContainer();
    }
  });
});
