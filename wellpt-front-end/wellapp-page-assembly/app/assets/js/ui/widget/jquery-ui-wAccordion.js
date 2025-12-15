(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define([
      'jquery',
      'commons',
      'server',
      'constant',
      'appContext',
      'appModal',
      'dataStoreBase'
      // 'css!/static/css/widget/jquery-ui-wAccordion'
    ], factory);
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

  $.widget('ui.wAccordion', $.ui.wWidget, {
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
      // 锚点选择
      if (options.params && options.params.hash) {
        var hashInfo = appContext.parseHashInfo(options.params.hash);
        if (hashInfo && hashInfo.widgetId == _self.getId()) {
          _self.onHashSelection(hashInfo);
        }
      }
    },
    /**
     * 渲染视图前
     */
    _beforeRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
    },
    _renderView: function (options) {
      // 渲染组件内容
      var _self = this;
      var html = new StringBuilder();
      var _len = options.tabs.length;
      console.log('options->', options);

      _self.$element.addClass(options.customClass);

      $.each(options.tabs, function (i, v) {
        if (options.accordion_type === 'horizontal') {
          html.appendFormat(
            '<div id="{5}" class="accordion-item {0}">' +
              '            <div class="accordion-label">' +
              '                <div class="accordion-label-content" style="height: {8};background: {1}">' +
              '                    <div class="badge-wrap"></div>' +
              '                    <p>{2}</p>' +
              '                    <div class="icon">》</div>' +
              '                </div>' +
              '            </div>' +
              '            <div id="accordionItem_{7}" class="accordion-content" data-id="{6}" style="width:{3};border: {4};border-left:none">' +
              '            </div>' +
              '        </div>',
            i === 0 ? 'active' : '',
            v.backgroundColor,
            v.name,
            _self.$elementParentWidth - 110 * _len + 'px',
            i === 0 ? '2px solid ' + v.backgroundColor : 'none',
            v.uuid,
            v.BadgeTypeCountJsWBootstrapTable,
            i,
            options.accordion_height + 'px'
          );
        } else {
          html.appendFormat(
            '<div id="{4}" class="accordion-item {0}">' +
              '            <div class="accordion-label">' +
              '                <div class="accordion-label-content" style="background: {1}">' +
              '                    <p>{2}</p>' +
              '                    <div class="badge-wrap"></div>' +
              '                    <div class="icon">》</div>' +
              '                </div>' +
              '            </div>' +
              '            <div id="accordionItem_{6}" class="accordion-content" data-id="{5}" style="height: {7};border: {3};border-top: none">' +
              '            </div>' +
              '        </div>',
            i === 0 ? 'active' : '',
            v.backgroundColor,
            v.name,
            i === 0 ? '2px solid ' + v.backgroundColor : 'none',
            v.uuid,
            v.BadgeTypeCountJsWBootstrapTable,
            i,
            options.accordion_height - 50 * (_len - 1) + 'px'
          );
        }
      });

      if (options.accordion_type === 'horizontal') {
        _self.$element.html('<div class="accordion clearfix" style="height: ' + options.accordion_height + 'px">' + html + '</div>');
      } else {
        _self.$element.html('<div class="accordion">' + html + '</div>');
      }

      $('.accordion').addClass(options.accordion_type);
      $('.accordion-label').each(function (i) {
        $(this).data('tab', options.tabs[i]);
      });
    },
    _bindEvents: function (options) {
      var _self = this;
      var _len = options.tabs.length;
      // 绑定组件事件
      $('.accordion-label').on('click', function (e) {
        e.preventDefault();
        var $this = $(this);
        var _parent = $this.parent();
        if (!_parent.hasClass('active')) {
          var _borderColor = $this.find('.accordion-label-content')[0].style.background;
          if (options.accordion_type === 'horizontal') {
            _parent
              .stop()
              .animate(
                {
                  width: _self.$elementParentWidth - 110 * (_len - 1)
                },
                500
              )
              .find('.accordion-content')
              .css({ border: '2px solid ' + _borderColor, borderLeft: 'none' });
            _parent
              .siblings()
              .stop()
              .animate(
                {
                  width: 110
                },
                500
              )
              .find('.accordion-content')
              .css('border', 'none');
          } else {
            _parent
              .stop()
              .animate(
                {
                  height: options.accordion_height - 50 * (_len - 1)
                },
                500
              )
              .find('.accordion-content')
              .css({ border: '2px solid ' + _borderColor, borderTop: 'none' });
            _parent
              .siblings()
              .stop()
              .animate(
                {
                  height: 50
                },
                500
              )
              .find('.accordion-content')
              .css('border', 'none');
          }
          _parent.addClass('active').siblings().removeClass('active');
        }
        if (_parent.find('.accordion-content .ui-wBootstrapTable').children().length > 0) {
          return;
        } else {
          var _renderTo = _parent.find('.accordion-content');
          var tab = $this.data('tab');
          if (tab && tab.eventHandler.id) {
            if (!StringUtils.isBlank(_renderTo.html())) {
              appContext.updateCurrentHash({
                ui: _self,
                selection: tab.uuid
              });
              return;
            }
            var opt = {
              target: '_targetWidget',
              targetWidgetId: _renderTo.attr('id'),
              widgetCloneable: true,
              refreshIfExists: false,
              appId: tab.eventHandler.id,
              appType: tab.eventHandler.type,
              appPath: tab.eventHandler.path,
              params: options.params || {},
              selection: tab.uuid,
              ui: _self,
              view: _self,
              viewOptions: _self.options
            };
            _self.startApp(opt);
          } else {
            appContext.renderWidget({
              renderTo: _renderTo.attr('id'),
              widgetDefId: _renderTo.data('id'),
              callback: function () {
                appModal.hideLoading({});
              }
            });
          }
        }
      });

      $('.accordion-label:first').trigger('click');
    },
    // 锚点选择回调处理
    onHashSelection: function (options) {
      var _self = this;
      // 调用父类方法
      _self._superApply(arguments);
      var selection = options.selection || [];
      $.each(selection, function (i, menuId) {
        var params = {};
        if (options.subHash) {
          params.menuid = options.subHash.selection.join(',');
          params.hash = appContext.hashInfoToString(options.subHash);
        }
        // $("li[id='" + menuId + "']", _self.element).data("selectionParams", params);
        $("div[id='" + menuId + "']", _self.element)
          .find('.accordion-label')
          .trigger('click');
        // $("li[id='" + menuId + "']", _self.element).data("selectionParams", null);
      });
    },
    _setEvent: function (options) {
      var _self = this;
      var $element = $(_self.element);
      var pageContainer = _self.getPageContainer();
      // 页面容器创建完成，加载徽章数量
      pageContainer.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function (e) {
        _self._getTabBadgetCount(options, e);
      });
      pageContainer.on(constant.WIDGET_EVENT.BadgeRefresh, function (e, param) {
        _self._getTabBadgetCount(options, e, param);
      });
    },
    _getTabBadgetCount: function (options, e, params) {
      var _self = this;
      $.each(options.tabs, function () {
        var tab = this;
        if (tab == null || tab.isShowBadge !== '1') {
          // 判断是否统计徽章
          return true;
        }
        if (e.detail && e.detail.targetTabName) {
          //由于trigger事件中，将参数转到e.detail里了
          params.targetTabName = e.detail.targetTabName;
        }
        if (params && params.targetTabName !== tab.name) {
          //刷新指定的tab项徽章
          return true;
        }
        if (tab.BadgeType === 'datastore') {
          //按数据仓库统计
          //按数据仓库的数据量计算徽章数量
          _self._badgeCountDataProvider = new DataStore({
            dataStoreId: tab.BadgeTypeCountDs,
            params: options.params || {},
            onDataChange: function (data, count) {
              if (count != -1) {
                _self._setBadgetCount(tab.uuid, count);
              }
            }
          });
          _self._badgeCountDataProvider.getCount(true);
        } else if (tab.BadgeType === 'countJs') {
          //执行统计js脚本
          // 获取导航徽章数量
          var badge = {
            countJsModule: tab.BadgeTypeCountJs,
            widgetDefId: tab.BadgeTypeCountJsWBootstrapTable || tab.viewId,
            tab: tab
          };
          _self.startApp({
            isJsModule: true,
            jsModule: tab.BadgeTypeCountJs,
            action: 'getCount',
            data: badge,
            ui: _self,
            callback: function (count, realCount, options) {
              _self._setBadgetCount(tab.uuid, count);
            }
          });
        }
      });
    },

    _setBadgetCount: function (uuid, count) {
      var selector = '.accordion-item[id="' + uuid + '"]';
      var $badge = $(selector).find('.badge-wrap');
      count = count > 99 ? '99+' : count;
      $badge.html(count);
    }
  });
});
