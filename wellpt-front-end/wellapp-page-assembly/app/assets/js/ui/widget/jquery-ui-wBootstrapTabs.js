(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'commons', 'server', 'constant', 'appContext', 'dataStoreBase'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, commons, server, constant, appContext, DataStore) {
  'use strict';
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  $.widget('ui.wBootstrapTabs', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {},
      callback: {}
    },
    _createView: function () {
      var _self = this;
      _self._beforeRenderView();
      _self._renderView();
      var options = $.extend({}, _self.options.widgetDefinition);
      _self._setEvent(options);

      _self._afterRenderView();

      _self.tabsScrollHandle();
    },
    /**
     * 渲染视图前
     */
    _beforeRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
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
        console.log($("li[id='" + menuId + "']", _self.element));
        $("li[id='" + menuId + "']", _self.element)
          .find('a')
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

      // tab显示异步加载事件处理内容
      $element.on('show.bs.tab', '.js-nav-tabs>li>a[role=tab]', function (e) {
        _self._handleShowTab($(e.target));
        _self.tabsScrollHandle();
      });
      // tab显示异步加载事件处理内容
      $element.on('shown.bs.tab', '.js-nav-tabs>li>a[role=tab]', function (e) {
        _self._handleShownTab($(e.target));
        _self.tabsScrollHandle();
      });

      // 定义切换tab事件
      $element.find('a[role=tab]').each(function (i) {
        $(this).click(function (e) {
          e.preventDefault();
          var childWgtId = $(this).attr('data-child-widget-id');
          var clickRefresh = $(this).attr('click-refresh');
          if (childWgtId && clickRefresh == '1') {
            //刷新内部子组件
            appContext.refreshWidgetById(childWgtId);
          }
          $(this).tab('show');
          _self.invokeDevelopmentMethod('onShowTab', [i, _self.options, _self.getConfiguration()]);
          var tab = $(this).data('tab');
          if (tab) {
            // 更新当前组件锚点
            appContext.updateCurrentHash({
              ui: _self,
              selection: tab.uuid
            });
          }

          // 定位当前选中位置
          $element.find('.bootstrap-tabs-container .nav-tabs').attr('data-translatex-index', i);
          _self.tabTranslatexSetting(i);
        });
      });

      // 默认激活第一个页签或锚点选择的页签
      var params = _self.options.widgetDefinition.params || {};
      if (params.menuid && $('#' + params.menuid, $element).length > 0) {
        $('#' + params.menuid, $element)
          .find('a')
          .trigger('click');
      } else {
        $element.find('ul>li>a:first').trigger('click');
      }

      window.addEventListener('resize', function () {
        _self.tabsScrollHandle();
      });
    },
    //切换tab后事件处理
    _handleShownTab: function ($tab) {
      var _self = this;
      var options = $.extend({}, _self.options.widgetDefinition);
      var tab = $tab.data('tab');
      var childWgtId = $tab.attr('data-child-widget-id');
      if (tab && tab.eventHandler && !childWgtId) {
        var targetWidgetSelector = $tab.attr('href');
        var clickRefresh = $tab.attr('click-refresh');
        // 处理内容已加载，表格组件显示不正常问题 bug#59554
        if ($(targetWidgetSelector).children().length > 0 && clickRefresh == '0') {
          if ($(targetWidgetSelector).children().hasClass('ui-wBootstrapTable')) {
            var $tableId = $(targetWidgetSelector).children().attr('id') + '_table';
            $('#' + $tableId).css({
              width: '100%'
            });
          }
        }
      }
    },
    //切换tab前事件处理
    _handleShowTab: function ($tab) {
      var _self = this;
      var options = $.extend({}, _self.options.widgetDefinition);
      var tab = $tab.data('tab');
      var childWgtId = $tab.attr('data-child-widget-id');
      if (tab && tab.eventHandler && !childWgtId) {
        var targetWidgetSelector = $tab.attr('href');
        var clickRefresh = $tab.attr('click-refresh');
        // 内容已加载，不再处理
        if ($(targetWidgetSelector).children().length > 0 && clickRefresh == '0') {
          return;
        }
        var targetWidgetId = targetWidgetSelector.substring(1);
        // 事件处理来自其他页面
        var opt = {
          target: '_targetWidget',
          targetWidgetId: targetWidgetId,
          widgetCloneable: true,
          refreshIfExists: true,
          appId: tab.eventHandler.id,
          appType: tab.eventHandler.type,
          appPath: tab.eventHandler.path,
          params: $.extend({}, options.params, appContext.parseEventHashParams(tab.eventHandler, 'menuid')),
          selection: tab.uuid,
          ui: _self,
          view: _self,
          viewOptions: _self.options
        };
        _self.startApp(opt);
      }
    },
    _getTabBadgetCount: function (options, e, params) {
      var _self = this;
      $.each(options.configuration.tabs, function () {
        var tab = this;
        if (tab == null || tab.isShowBadge != '1') {
          // 判断是否统计徽章
          return true;
        }
        if (e.detail && e.detail.targetTabName) {
          //由于trigger事件中，将参数转到e.detail里了
          params.targetTabName = e.detail.targetTabName;
        }
        if (params && params.targetTabName != tab.name) {
          //刷新指定的tab项徽章
          return true;
        }
        if (tab.BadgeType == 'datastore') {
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
        } else if (tab.BadgeType == 'countJs') {
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
      var _self = this;
      var selector = 'li[id="' + uuid + '"]';
      var $tab = $(selector, _self.element).children('a');
      var $badge = $tab.children('span.badge');
      count = count > 99 ? '99+' : count;
      if ($badge.length == 0) {
        $badge = $('<span class="badge">' + count + '</span>');
        $tab.append($badge);
      } else {
        $badge.html(count);
      }
    },
    /**
     * 视图组件渲染结束调用
     */
    _afterRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('afterRender', [_self.options, _self.getConfiguration()]);
    },
    _renderView: function () {
      var _self = this;
      var $element = $(_self.element);
      var configuration = _self.getConfiguration();
      $element.addClass(configuration.customClass);
      var widgetDefinition = _self.options.widgetDefinition;
      // tabs组件嵌套了其他的widget，需要先实现
      if (widgetDefinition.items && widgetDefinition.items.length > 0) {
        $.each(widgetDefinition.items, function (index, childWidgetDefinition) {
          appContext.createWidget(childWidgetDefinition, widgetDefinition);
        });
      }

      // 创建tabs
      if (configuration.tabs) {
        _self._createTabs(configuration.tabs);
      }
    },
    // 创建tabs
    _createTabs: function (tabs) {
      var _self = this;
      var $element = $(_self.element);
      var $ul = $element.find('ul.js-nav-tabs').first();
      var $content = $element.find('div.js-tab-content').first();
      $.each(tabs, function (i, tab) {
        // 创建tab
        var $li = $("<li role='presentation'></li>").attr('id', tab.uuid);
        var $a = $('<a>', {
          href: '#' + $element.attr('id') + '-' + i,
          role: 'tab',
          'data-toggle': 'tab',
          'aria-controls': 'tab-' + i,
          'click-refresh': tab.clickTabToRefresh
        });
        if (tab.icon && tab.icon.className) {
          var $icon = $('<span>', {
            class: tab.icon.className
          });
          $a.append($icon);
        }
        $a.data('tab', tab);
        $a.append(tab.name);
        $li.append($a);
        $ul.append($li);

        // 创建对应的tab-content
        var $div = $('<div>', {
          role: 'tabpanel',
          class: 'tab-pane',
          id: $element.attr('id') + '-' + i
        });
        $content.append($div);
        if (tab.eventHandler && StringUtils.isNotBlank(tab.eventHandler.path)) {
          var paths = tab.eventHandler.path.split('/');
          var widgetId = paths[paths.length - 1];
          var $childWidget = $element.find('#' + widgetId);
          if ($childWidget.length > 0) {
            $a.attr('data-child-widget-id', widgetId); //内部的子组件
            // 事件处理来自本tabs组件中的子组件，则将该组件移到对应的位置
            $childWidget.appendTo($div);
          } else {
            // 事件处理来自其他页面，由事件绑定异步处理
          }
        }
      });
    },
    refresh: function () {
      var _self = this;
      var id = $(_self.element).find('.tab-pane.active > div').attr('id');
      appContext.refreshWidgetById(id);
    },
    tabsScrollHandle: function () {
      var _self = this;
      var $element = _self.element.find('.bootstrap-tabs-container');
      var $box = $element.find('.nav-tabs');
      var boxWidth = $element.find('.nav-tabs-box').outerWidth(true);
      var tabsWidth = $element.find('.nav-tabs').outerWidth(true);
      if ($box.length == 1) {
        if (tabsWidth < boxWidth + 30) {
          //tabs无滚动条
          if ($element.find('.nav-tabs-prev')[0]) {
            $element.find('.nav-tabs-prev').remove();
            $element.find('.nav-tabs-next').remove();
            $element.find('.nav-tabs-more').remove();
            $element.css({
              'padding-left': '0px',
              'padding-right': '0px'
            });
          }
          $box[0].style.transform = 'translateX(0px)';
        } else {
          //有滚动条
          // 已有就不需要添加
          if (!$element.find('.nav-tabs-prev')[0]) {
            var $prev = $('<div>', {
              class: 'nav-tabs-prev'
            }).html('<i class="iconfont icon-ptkj-xianmiaojiantou-zuo"></i>');
            var $next = $('<div>', {
              class: 'nav-tabs-next'
            }).html('<i class="iconfont icon-ptkj-xianmiaojiantou-you"></i>');
            $element.append($prev).append($next);
            $element.css({
              'padding-left': '20px',
              'padding-right': '55px'
            });
            _self.tabMoreBtnSetting($element);
          }
          _self.tabTranslatexEvent();
        }
      }
    },
    tabTranslatexEvent: function () {
      var _self = this;
      var $element = _self.element.find('.bootstrap-tabs-container');
      var $box = $element.find('.nav-tabs');
      var tabLength = $box.find('li[role="presentation"]').length;

      //左移
      $element
        .find('.nav-tabs-prev')
        .off()
        .on('click', function () {
          var _index = $box.attr('data-translatex-index') || 0; //记录移动到哪个位置
          _index = parseInt(_index);
          if (_index > 0) {
            _index--;
          }
          _self.tabTranslatexSetting(_index);
        });

      // 右移
      $element
        .find('.nav-tabs-next')
        .off()
        .on('click', function () {
          var _index = $box.attr('data-translatex-index') || 0; //记录移动到哪个位置
          _index = parseInt(_index);
          if (_index < tabLength) {
            _index++;
            _self.tabTranslatexSetting(_index);
          }
        });

      _self.tabTranslatexSetting();
    },
    // 设置tab的偏移量
    tabTranslatexSetting: function (_index) {
      var _self = this;
      var $element = _self.element.find('.bootstrap-tabs-container');
      var $box = $element.find('.nav-tabs');
      var boxWidth = $element.find('.nav-tabs-box').outerWidth(true);
      var tabsWidth = $element.find('.nav-tabs').outerWidth(true);
      var tabLength = $box.find('li[role="presentation"]').length;
      var differ = tabsWidth - boxWidth + 60; //tabs总宽度 - 显示宽度
      var width = 0; //记录tab总宽度
      if (_index == undefined) {
        //_index不存在时
        _index = $box.attr('data-translatex-index') || 0;
      }
      _index = parseInt(_index);
      $box.attr('data-translatex-index', _index);
      if ($element.find('.nav-tabs-prev')[0]) {
        // 左移右移按钮显示判断
        if (_index == 0) {
          $element.find('.nav-tabs-prev').hide();
        } else {
          $element.find('.nav-tabs-prev').show();
        }
        if (_index + 1 == tabLength) {
          $element.find('.nav-tabs-next').hide();
        } else {
          $element.find('.nav-tabs-next').show();
        }

        $box.find('li[role="presentation"]').each(function (index, item) {
          if (_index > index) {
            var _width = $(item).outerWidth(true); //tab的宽度
            width += _width;
            if (differ < width) {
              _index = index;
              $box.attr('data-translatex-index', _index);
            }
          }
        });
        if (_index != -1) {
          if (differ < width) {
            $box[0].style.transform = 'translateX(-' + differ + 'px)';
            $element.find('.nav-tabs-next').hide();
          } else {
            $box[0].style.transform = 'translateX(-' + width + 'px)';
            $element.find('.nav-tabs-next').show();
          }
        }
      }
    },
    tabMoreBtnSetting: function ($element) {
      var _self = this;
      var tabs = _self.getConfiguration().tabs;
      var $more_btn = $('<div>', {
        class: 'nav-tabs-more',
        style: 'overflow: visible'
      }).html('<i class="iconfont icon-ptkj-zhankai"></i>');
      var $ul = $('<div><ul class="nav-tabs-more-list"></ul></div>');
      _.each(tabs, function (item, index) {
        var $icon = item.icon.className ? '<i class="' + item.icon.className + '"></i>' : '';
        var $li = $('<li>', {
          class: 'ellipsis',
          title: item.name,
          'li-index': index
        })
          .append($icon)
          .append(item.name);
        $ul.find('.nav-tabs-more-list').append($li);
      });
      $element.append($more_btn);
      $('.nav-tabs-more>i', $element)
        .popover({
          trigger: 'manual',
          container: 'body',
          placement: 'bottom', //top, bottom, left or right
          title: '', //设置 弹出框 的标题
          html: 'true', // 为true的话，data-content里就能放html代码了
          content: $ul.html()
        })
        .on('mouseenter', function () {
          var _this = this;
          $(this).popover('show');
          $('body')
            .find('.popover.tab-more-popover')
            .on('mouseleave', function () {
              $(_this).popover('hide');
            });
        })
        .on('mouseleave', function () {
          var _this = this;
          setTimeout(function () {
            if (!$('.popover:hover').length) {
              $(_this).popover('hide');
            }
          }, 100);
        });
      $('.nav-tabs-more>i', $element).popover('show');
      $('.nav-tabs-more>i', $element).popover('hide');
      $('.nav-tabs-more>i', $element).on('shown.bs.popover', function () {
        var popoverId = $(this).attr('aria-describedby');
        if (popoverId) {
          $('#' + popoverId).addClass('tab-more-popover');
          $('ul', '#' + popoverId).niceScroll({
            height: '200px',
            oneaxismousemode: false,
            cursorcolor: '#ccc',
            cursorwidth: '8px'
          });
          $('ul', '#' + popoverId)
            .find('li')
            .on('click', function () {
              _self.tabTranslatexSetting($(this).attr('li-index'));
            });
        }
      });
    }
  });
});
