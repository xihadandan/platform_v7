(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['mui', 'commons', 'mui-wWidget', 'appContext', 'server'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, commons, widget, appContext, server) {
  'use strict';
  var StringUtils = commons.StringUtils;
  $.widget('mui.wMobileGridView', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {}
    },
    _createView: function () {
      var self = this;
      var requires = [];
      var configuration = self.getConfiguration() || {};
      self.options.widgetDefinition.configuration = configuration;
      if (configuration.jsModule) {
        requires.push(configuration.jsModule);
      }
      appContext.require(requires, function (Development) {
        if (typeof Development === 'function') {
          self.development = new Development(self);
        }
        self._executeJsModule('beforeRender', [self.options, configuration]);
        // "生成布局"（flow）和"绘制"（paint）这两步，合称为"渲染"（render）
        self._renderView();
        self._executeJsModule('afterRender', [self.options, configuration]);
        self._bindEvents();
      });

      // wMobileGridDevelopment._changeStyle.apply(this);
    },
    _renderView: function () {
      // 生成页面组件
      var _self = this;
      var options = _self.options;
      _self.element[0].classList.add('w-mobile-grid-view');
      _self.eventHandlers = {};
      var configuration = options.widgetDefinition.configuration;
      var numColumns = Number(configuration.numColumns);
      // 返回分组
      var sliderGroups = _self._getSliderGroups(configuration.columns, numColumns, configuration);
      var StringBuilder = commons.StringBuilder;
      var sb = new StringBuilder();
      var sliderId = 'mui-slider_' + _self.getId();
      sb.append('<div id="' + sliderId + '" class="mui-slider">');
      sb.append('<div class="mui-slider-group">');
      for (var i = 0; i < sliderGroups.groupCount; i++) {
        sb.append('<div class="mui-slider-item">');
        if (configuration && configuration.logoFilePath) {
          sb.append('<div class="banner"><img src="' + ctx + configuration.logoFilePath + '"></div>');
        }
        sb.append('<ul class="mui-table-view mui-grid-view mui-grid-9">');
        var columns = sliderGroups[i];
        $.each(columns, function (i, column) {
          var text = column.text;
          var iconClass = column.iconClass;
          if (commons.StringUtils.isBlank(iconClass)) {
            iconClass = 'mui-icon-more';
          }
          var appId = '';
          if (column.eventHandler) {
            appId = column.eventHandler.id;
            column.eventHandler['target'] = column.target || {};
            column.eventHandler['eventParams'] = column.eventParams || {};
            _self.eventHandlers[appId] = column.eventHandler;
          }
          if (appId && column.badge) {
            setTimeout(function () {
              _self._getBadgeCount(appId, column.badge);
            }, 0);
          }
          var colspan = 12 / numColumns;
          var li = '<li appId="{0}" class="mui-table-view-cell mui-media mui-col-xs-{1}">';
          sb.appendFormat(li, appId, colspan);
          sb.append('<a href="#">');
          sb.append('<span class="mui-icon ' + iconClass + '"></span>');
          sb.append('<div class="mui-media-body">' + text + '</div>');
          sb.append('</a>');
          sb.append('</li>');
        });
        sb.append('</ul>');
        sb.append('</div>');
      }
      sb.append('</div>');

      // 滑块分组标记
      if (sliderGroups.groupCount > 1) {
        sb.append('<div class="mui-slider-indicator" style="bottom:0;">');
        for (var i = 0; i < sliderGroups.groupCount; i++) {
          if (i == 0) {
            sb.append('<div class="mui-indicator mui-active"></div>');
          } else {
            sb.append('<div class="mui-indicator"></div>');
          }
        }
        sb.append('</div>');
      }
      sb.append('</div>');
      this.element[0].innerHTML = sb.toString();
    },
    _bindEvents: function () {
      var _self = this;
      // 点击事件
      $('.mui-grid-view').on('tap', '.mui-table-view-cell', function (event) {
        // 获取id
        var appId = this.getAttribute('appId');
        if (commons.StringUtils.isBlank(appId)) {
          return;
        }
        var eventHandler = _self.eventHandlers[appId];
        var options = {
          appType: eventHandler.type,
          appPath: eventHandler.path,
          params: eventHandler.eventParams.params,
          target: eventHandler.target.position,
          targetDetail: eventHandler.target,
          targetWidgetId: eventHandler.target.widgetId,
          refreshIfExists: eventHandler.target.refreshIfExists
        };
        options.event = event;
        _self.startApp(options);
      });

      // 分组事件
      var sliderId = 'mui-slider_' + _self.getId();
      var slider = document.getElementById(sliderId);
      var group = slider.querySelector('.mui-slider-group');
      $(slider).slider();
    },
    _getSliderGroups: function (rawColumns, numColumns, config) {
      var columns = [];
      for (var i = 0; i < rawColumns.length; i++) {
        var column = rawColumns[i];
        if (column.hidden == '1') {
          continue;
        }
        columns.push(column);
      }
      var groups = {
        totalCount: 0,
        groupCount: 0
      };
      var groupColumns = [];
      var totalCount = columns.length;
      groups.totalCount = totalCount;
      if (config.showAsSingle == '1') {
        groups[0] = columns;
        groups.groupCount = 1;
      } else {
        for (var i = 0; i < columns.length; i++) {
          var column = columns[i];
          var groupIndex = parseInt(i / (3 * numColumns));
          if (groups[groupIndex] == null) {
            groups[groupIndex] = [];
          }
          groups.groupCount = groupIndex + 1;
          groups[groupIndex].push(column);
        }
      }
      return groups;
    },
    // 获取导航徽章数量
    _getBadgeCount: function (appId, badge) {
      var _self = this;
      if ($.isEmptyObject(badge) === false) {
        if (StringUtils.isNotBlank(badge.countJsModule)) {
          _self.startApp({
            isJsModule: true,
            jsModule: badge.countJsModule,
            action: 'getCount',
            data: badge,
            callback: function (count, realCount, options) {
              _self._setBadgeCount(appId, count, realCount);
            }
          });
        } else if (badge.countWay === 'dataStoreCount') {
          // server.JDS.call({
          //   async: true,
          //   service: 'cdDataStoreService.loadCount',
          //   data: [{ dataStoreId: badge.dataStoreCounter }],
          //   success: function (result) {
          server.JDS.restfulPost({
            url: '/proxy/api/datastore/loadCount',
            data: { dataStoreId: badge.dataStoreCounter },
            async: true,
            success: function (result) {
              _self._setBadgeCount(appId, result.data);
            }
          });
        }
      }
    },
    _setBadgeCount: function (appId, count, realCount) {
      var _self = this;
      var selector = 'li.mui-table-view-cell[appId="' + appId + '"] > a';
      var $nav = $(selector, _self.element[0]);
      if (!$nav || $nav.length <= 0) {
        return false;
      }
      var $badge = $nav[0].querySelector('span.mui-badge');
      if (typeof $badge === 'undefined' || $badge == null) {
        $nav[0]
          .querySelector('span.mui-icon')
          .appendChild($.dom('<span class="mui-badge mui-badge-danger" style="top:2px;left:auto;right:2px;">' + count + '</span>')[0]);
      } else {
        $badge.innerHTML = count;
      }
    }
  });
});
