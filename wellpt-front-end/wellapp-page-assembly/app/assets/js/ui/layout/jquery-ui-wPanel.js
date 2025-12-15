(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'constant', 'commons', 'appContext', 'dataStoreBase', 'server'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function (jquery, constant, commons, appContext, DataStore, server) {
  'use strict';
  var $ = jquery;
  var StringUtils = commons.StringUtils;
  var Browser = commons.Browser;
  var StringBuilder = commons.StringBuilder;
  var JDS = server.JDS;
  var BootstrapTableViewGetCount = 'BootstrapTableViewGetCount';
  var domloaded = constant.getValueByKey(constant.EVENT_TYPE, 'DOM_LOADED');
  var mouseover = constant.getValueByKey(constant.EVENT_TYPE, 'MONSE_OVER');
  $.widget('ui.wPanel', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {}
    },
    _createView: function () {
      // "生成布局"（flow）和"绘制"（paint）这两步，合称为"渲染"（render）
      this._renderView();
    },
    _init: function () {},
    _renderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
      // 生成布局
      _self._createLayout();
      // 生成头部
      _self._createHeader();
      // 生成页面组件
      _self._createWidgets();
      // 生成底部
      _self._createFooter();

      // 绑定事件
      _self._bindEvent();

      // 默认激活第一个页签或锚点选择的页签
      _self._activeFirst();

      _self.invokeDevelopmentMethod('afterRender', [_self.options, _self.getConfiguration()]);
    },
    _activeFirst: function () {
      var _self = this;
      var $element = $(_self.element);
      var params = _self.options.widgetDefinition.params || {};
      var firstPanel = $('.panel-tab-content', $element).eq(0);
      if (params.menuid) {
        var $li = _self._getPanelTabNavElementById(params.menuid);
        if ($li && $li.length > 0) {
          $li.find('a').trigger('click');
        } else {
          _self._initPanelTabContent(firstPanel);
          var configuration = _self.getConfiguration();
          if (configuration.body && configuration.body.tabs) {
            var tabs = configuration.body.tabs;
            var tab = tabs[firstPanel.attr('data-index')];
            if (tab) {
              // 更新当前组件锚点
              appContext.updateCurrentHash({
                ui: _self,
                selection: tab.uuid
              });
            }
          }
        }
      } else {
        _self._initPanelTabContent(firstPanel);
      }
    },
    getRenderPlaceholder: function () {
      var _self = this;
      // 获取渲染组件的占位符
      if (_self.renderPlaceholder == null) {
        _self.renderPlaceholder = _self.element.children('.panel-body');
      }
      return _self.renderPlaceholder;
    },
    _createLayout: function () {
      var _self = this;
      var configuration = _self.getConfiguration();
      if (configuration.panelContentHeight) {
        _self.element.children('.panel-body').css({
          height: configuration.panelContentHeight + 'px',
          'overflow-y': 'auto'
        });
      }

      if (configuration.noPadding) {
        _self.element.children('.panel-body').css('padding', 0);
      }
      if (configuration.noMarginBottom) {
        _self.element.css('margin-bottom', 0);
      }
      if (configuration.bgTransparent) {
        _self.element.css('background', 'transparent').children('.panel-body').css('background', 'transparent');
      }
    },
    _createHeader: function () {
      var _self = this;
      var configuration = _self.getConfiguration();

      if (configuration.hideHeader || !configuration.header) {
        return;
      }

      // if(configuration.body.contentType === 'contentTab') {
      //     $(_self.element.find('.panel-heading').hide());
      //     return;
      // }

      // 面板头部
      var $panelHeader = _self.element.children('.panel-heading');
      $panelHeader.append("<div class='panel-header-right'></div>");
      _self.$panelHeader = $panelHeader;

      if (!configuration.header.titleIcon) {
        //如果面板头部未选择图标则添加默认图标
        $panelHeader.find('.panel-icon').addClass('iconfont icon-ptkj-qukuaibiaotitubiao');
      }

      // 显示微章数量
      var header = configuration.header;
      if (header.showBadgeCount === true) {
        // 监听容器创建完成事件
        var pageContainer = _self.pageContainer;
        var getBadgeCountWay = header.getBadgeCountWay || (header.getBadgeCountListViewId ? 'tableWidgetCount' : null);
        if (getBadgeCountWay === 'tableWidgetCount') {
          var getBadgeCountListViewId = header.getBadgeCountListViewId;
          pageContainer.onWidgetCreated(getBadgeCountListViewId, function (e, listViewWidget) {
            var changeCount = function (e, ui) {
              if (ui.getDataProvider()) {
                var totalCount = ui.getDataProvider().getCount();
                if (totalCount != 0) {
                  $panelHeader.find('.badge').text(totalCount);
                }
              }
            };
            changeCount(e, listViewWidget);
            var freshCallBack = function (e, ui) {
              changeCount(e, ui);
              ui.off(constant.WIDGET_EVENT.Refresh, freshCallBack);
            };
            listViewWidget.on(constant.WIDGET_EVENT.Refresh, freshCallBack);
            listViewWidget.on(constant.WIDGET_EVENT.Change, changeCount);
          });
          // 页面加载完成后，如果页面不存在组件，调用获取数量的模块取数量
          pageContainer.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function () {
            var widget = appContext.getWidgetById(getBadgeCountListViewId);
            if (widget != null) {
              return;
            }
            var data = {
              bootstrapView: getBadgeCountListViewId,
              widgetDefId: getBadgeCountListViewId
            };
            _self.startApp({
              isJsModule: true,
              jsModule: BootstrapTableViewGetCount,
              action: 'getCount',
              data: data,
              callback: function (count, realCount, options) {
                $panelHeader.find('.badge').text(count);
              }
            });
          });
        } else if (getBadgeCountWay === 'dataStoreCount' && header.getBadgeCountDataStore) {
          //按数据仓库的数据量计算徽章数量
          _self._badgeCountDataProvider = new DataStore({
            dataStoreId: header.getBadgeCountDataStore,
            onDataChange: function (data, count) {
              if (count != 0) {
                $panelHeader.find('.badge').text(count);
              }
            }
          });
          _self._badgeCountDataProvider.getCount(true);
        }
      }

      // 创建头部查看更多
      _self._createViewMore(header);

      // 创建头部查询信息
      _self._createPanelQuery(header);
    },

    _containerComputed: function () {
      // todo
      var _self = this;
      var configuration = _self.getConfiguration();

      var height = document.documentElement.clientHeight;
      var headerHeight = $('.ui-wHeader').height();
      if ($('.ui-wNavbar').length != 0) {
        var navHeight = $('.ui-wNavbar').height();
        headerHeight = headerHeight + navHeight;
      }
    },
    _createViewMore: function (header, type) {
      var _self = this;
      var configuration = _self.getConfiguration();
      if (header.enableViewMore !== true) {
        return;
      }
      var sb = new StringBuilder();
      sb.append('<div class="pull-right panel-view-more ml-20">');
      sb.append('<a>更多<i class="iconfont icon-ptkj-xianmiaojiantou-you"></i></a>');
      sb.append('</div>');
      // todo
      if (type && type === 'tab') {
        var $tabHeader = _self.element.find('.panel-tab-header');
        var $panelHeaderRight = $tabHeader.find('.panel-header-right');
        $panelHeaderRight.append(sb.toString());
        $tabHeader.find('.panel-nav-tabs-container')[0].style.width = 'calc(100% - ' + $panelHeaderRight.outerWidth(true) + 'px)';
      } else {
        var $panelHeader = _self.$panelHeader;
        var $panelHeaderRight = _self.$panelHeader.find('.panel-header-right');
        $panelHeaderRight.append(sb.toString());
      }
    },

    _createPanelQuery: function (header, type) {
      var _self = this;
      var $element = _self.element;
      var queryCorrelativeListViewId = header.queryCorrelativeListViewId;
      if (!header.enableQuery) {
        return;
      }
      // if (StringUtils.isBlank(queryCorrelativeListViewId)) {
      //     return;
      // }
      var queryHint = header.queryHint;
      if (StringUtils.isBlank(queryHint)) {
        queryHint = '请输入关键字';
      }
      var sb = new StringBuilder();
      sb.appendFormat(
        '<div class="pull-right search-wrapper">' +
          '<div class="input-group panel-heading-query animated">' +
          '<input type="search" class="form-control" placeholder="{0}">' +
          '<div class="search-clear-icon">' +
          '<i class="iconfont icon-ptkj-dacha-xiao"></i>' +
          '</div>' +
          '</div>' +
          '<div class="panel-search-icon">' +
          '<i class="iconfont icon-ptkj-sousuochaxun"></i>' +
          '</div>' +
          '</div>',
        queryHint
      );

      var $panelHeader = _self.$panelHeader;
      var $tabHeader = $element.find('.panel-tab-header');
      if (type && type === 'tab') {
        var $panelHeaderRight = $tabHeader.find('.panel-header-right');
        $panelHeaderRight.append(sb.toString());
        $tabHeader.find('.panel-heading-query').css('marginTop', 0);
        $tabHeader.find('.panel-nav-tabs-container')[0].style.width = 'calc(100% - ' + $panelHeaderRight.outerWidth(true) + 'px)';
      } else {
        var $panelHeaderRight = _self.$panelHeader.find('.panel-header-right');
        $panelHeaderRight.append(sb.toString());
      }
      // IE8的placeholder效果处理
      if ($.fn && $.fn.placeholder && Browser.isIE8OrLower()) {
        $('input', $panelHeader).placeholder();
        $('input', $tabHeader).placeholder();
      }

      /** 搜索框 */
      var searchWrap = '.search-wrapper:first';

      /** 包含删除按钮的搜索输入框 */
      var queryInputWrapSelector = '.panel-heading-query:first';

      /** 搜索输入框的input元素 */
      var queryInputSelector = '.panel-heading-query > input[type="search"]:first';

      /** 搜索按钮，鼠标进入搜索按钮时，显示搜索输入框 */
      var queryIconSelector = '.search-wrapper .panel-search-icon:first';

      var queryCallback = function () {
        var configuration = _self.getConfiguration();
        var listView = '';
        if (configuration.body.contentType === 'contentTab') {
          var currTab = $element.find('.panel-tab-content.active').data('tab');
          listView = appContext.getWidgetById($element.find('.panel-tab-content.active .ui-wBootstrapTable').attr('id'));
        } else {
          listView = appContext.getWidgetById(queryCorrelativeListViewId);
        }
        if (listView == null) {
          return;
        }

        var queryText = $(queryInputSelector, type && type === 'tab' ? $tabHeader : $panelHeader).val();

        if (listView.query) {
          listView.query(queryText, 'keyword');
        } else {
          listView.refresh();
        }
      };
      var $queryInputWrapper = $(queryInputWrapSelector, $element);

      if (header.inputQueryShow) {
        $queryInputWrapper.show();
      }

      function setQueryInputVisibility(visible) {
        var $searchWrap = $(searchWrap, $element);
        var $queryInput = $(queryInputSelector, $element);

        // 当前搜索框是否显示
        var isCurrentVisible = $queryInputWrapper.hasClass('input-visible');

        // 当前搜索框输入值
        var queryValue = $queryInput.val();

        // 当前搜索框是否处于focus状态
        var isInputFocus = $searchWrap.hasClass('search-wrap-focus');

        if (header.inputQueryShow) {
          $queryInputWrapper.show();
          return;
        }

        if ((isCurrentVisible && visible) || (!isCurrentVisible && !visible)) {
          return;
        }

        if (!isCurrentVisible && visible) {
          $queryInputWrapper.show().removeClass('fadeOutRight').addClass('fadeInRight input-visible');
          return;
        }

        if (isCurrentVisible && !visible && !queryValue && !isInputFocus) {
          // input为focus状态或者有输入搜素字符时不关闭
          $queryInputWrapper.removeClass('fadeInRight input-visible').addClass('fadeOutRight');
        }
      }

      $element
        .on('mouseenter', queryIconSelector, function () {
          setQueryInputVisibility(true);
        })
        .on('mouseleave', queryIconSelector, function (e) {
          var $target = $(e.relatedTarget);
          if ($target.closest('.panel-heading-query').length || $target.hasClass('panel-heading-query')) {
            return;
          }

          setQueryInputVisibility(false);
        })
        .on('click', queryIconSelector, function () {
          if ($element.find(queryInputWrapSelector).hasClass('fadeInRight')) {
            queryCallback();
          }
        });
      $element
        .on('keypress', queryInputSelector, function (event) {
          if (event.keyCode === 13) {
            queryCallback();
          }
        })
        .on('focus', queryInputSelector, function (event) {
          setQueryInputVisibility(true);
          $element.find(searchWrap).addClass('search-wrap-focus');
        })
        .on('blur', queryInputSelector, function (event) {
          setQueryInputVisibility(false);
          $element.find(searchWrap).removeClass('search-wrap-focus');
        })
        .on('mouseenter', queryInputWrapSelector, function () {
          setQueryInputVisibility(true);
        })
        .on('mouseleave', queryInputWrapSelector, function (event) {
          setQueryInputVisibility(false);
        });

      $element.on('input propertychange', queryInputSelector, function () {
        var $this = $(this);
        var _val = $.trim($this.val());
        $('.panel-tab.active', _self.element).attr('searchValue', _val);
        if (_val) {
          $element.find('.search-clear-icon').show();
        } else {
          $element.find('.search-clear-icon').hide();
        }
      });

      $element.on('click', '.search-clear-icon', function (e) {
        $element.find(queryInputSelector).val('').focus();
        $(this).hide();
        queryCallback();
      });

      $(document).on('click', function (e) {
        if (!$(e.target).closest('.search-wrapper').length) {
          $element.find(searchWrap).removeClass('search-wrap-focus');
          $element.find(queryInputSelector).removeClass('search-focus');
          setQueryInputVisibility(false);
        }
      });
    },

    _createWidgets: function () {
      var _self = this;
      var configuration = _self.getConfiguration();
      if (configuration.body) {
        if (configuration.body.pageUrl) {
          _self._renderFrameContent();
          return;
        } else if (configuration.body.tabs && configuration.body.tabs.length) {
          _self._renderTabContent(configuration.body.tabs);
          return;
        }
      }
      var widgetDefinition = _self.options.widgetDefinition;
      $.each(widgetDefinition.items, function (index, childWidgetDefinition) {
        appContext.createWidget(childWidgetDefinition, widgetDefinition);
      });
    },

    _renderFrameContent: function () {
      var _self = this;
      var configuration = this.getConfiguration();
      //解析pageUlr的可替换参数
      var user = server.SpringSecurityUtils.getUserDetails();
      var param = {
        currentUserName: user.userName,
        currentLoginName: user.loginName,
        currentUserId: user.id,
        currentUserDepartmentId: user.mainDepartmentId,
        currentUserDepartmentName: user.mainDepartmentName,
        currentUserUnitId: user.systemUnitId
      };
      if (_.startsWith(configuration.body.pageUrl, '/')) {
        configuration.body.pageUrl = ctx + configuration.body.pageUrl;
      }
      var iframeId = commons.UUID.createUUID();
      _self.panelInnerFrameId = iframeId;
      var url = appContext.resolveParams(
        {
          url: configuration.body.pageUrl
        },
        param
      ).url;
      var iframe =
        '<div class="embed-responsive embed-responsive-4by3"><iframe src="' +
        url +
        '" class="embed-responsive-item" id="' +
        iframeId +
        '"></iframe></div>';
      appContext.getWidgetRenderPlaceholder(_self.options.widgetDefinition.id).html(iframe);

      var publishFucntion = function () {
        var frameWindow = window.frames[iframeId].contentWindow || window.frames[iframeId];
        frameWindow.$panelContainer = $(_self.element); //面板元素赋予子窗口
        frameWindow.publish = function () {
          _self.invokeDevelopmentMethod('framePublishEventInvoke', arguments);
        };
      };
      if ($('#' + iframeId)[0].attachEvent) {
        $('#' + iframeId)[0].attachEvent('onload', function () {
          publishFucntion();
        });
      } else {
        $('#' + iframeId)[0].onload = function () {
          publishFucntion();
        };
      }
    },

    // 渲染tab头部
    _renderTabHeader: function (configuration, $element, tabs) {
      var _self = this;
      var tabHeaderHtml = new StringBuilder();
      var $tabs = new StringBuilder();
      var header = configuration.header;
      var moreHtml = '';
      var searchHtml = '';
      $.each(tabs, function (i, v) {
        _self.renderTabBadgeCount(v);

        $tabs.appendFormat(
          '<li role="presentation" class="panel-tab {0}" data-index="{1}">' +
            '<a id="panelTab_{2}" href="#{2}" aria-controls="{2}" role="tab" click-refresh="{5}" data-toggle="tab"><i class="{4}"></i>{3}</a>' +
            '</li>',
          i === 0 ? 'active' : '',
          i,
          v.uuid,
          v.name,
          v.icon ? v.icon.className : '',
          v.clickTabToRefresh || '0'
        );
      });

      tabHeaderHtml.appendFormat(
        '<div class="panel-tab-header {0} clearfix"><div class="panel-nav-tabs-container"><div class="nav-tabs-box"><ul class="nav nav-tabs pull-left" role="tablist">{1}</ul></div></div><div class="panel-header-right"></div></div>',
        configuration.hideHeader ? '' : 'has-panel-header',
        $tabs
      );
      $element.find('.panel-body').append(tabHeaderHtml.toString());
      if (configuration.hideHeader) {
        // 创建头部查看更多
        _self._createViewMore(header, 'tab');

        // 创建头部查询信息
        _self._createPanelQuery(header, 'tab');
      }

      setTimeout(function () {
        _self.tabsScrollHandle();
      }, 1000);
    },

    renderTabBadgeCount: function (tab) {
      var _self = this;
      if (tab == null || tab.isShowBadge !== '1') {
        // 判断是否统计徽章
        return;
      }
      if (tab.BadgeType === 'datastore') {
        //按数据仓库统计
        //按数据仓库的数据量计算徽章数量
        _self._badgeCountDataProvider = new DataStore({
          dataStoreId: tab.BadgeTypeCountDs,
          onDataChange: function (data, count) {
            _self.setTabBadgetCount(tab, count);
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
            _self.setTabBadgetCount(tab, count);
          }
        });
      }
    },

    setTabBadgetCount: function (tab, count) {
      var _self = this;
      var tabId = '#panelTab_' + tab.uuid;
      var $curTab = _self.element.find(tabId);
      $curTab.html(tab.name + '<span>(' + count + ')</span>');
    },

    _renderTabContent: function (tabs) {
      var _self = this;
      var configuration = _self.getConfiguration();
      var $element = _self.element;
      var options = $.extend({}, _self.options.widgetDefinition, _self.options.widgetDefinition.configuration);
      var $content = new StringBuilder();
      var $tabContent = new StringBuilder();
      _self._renderTabHeader(configuration, $element, tabs);

      $.each(tabs, function (i, v) {
        $tabContent.appendFormat(
          '<div role="tabpanel" class="panel-tab-content tab-pane {0}" id="{1}" data-index="{2}"></div>',
          i === 0 ? 'active' : '',
          v.uuid,
          i
        );
      });
      var tabContentStyle = '';
      if (configuration.body.contentTabHeight) {
        tabContentStyle = 'width: 100%;height: ' + configuration.body.contentTabHeight + 'px;overflow-y: auto;';
      }
      $content.appendFormat('<div class="tab-content" style="{1}">{0}</div>', $tabContent, tabContentStyle);
      $element.find('.panel-body').append($content.toString());

      // $('.panel-tab-content', $element).each(function () {
      //     var $this = $(this);
      //     var tab = tabs[$this.attr('data-index')];
      //     $this.attr('data-tab', tab);
      //     if (tab && tab.eventHandler.id) {
      //         var opt = {
      //             target: "_targetWidget",
      //             targetWidgetId: $this.attr('id'),
      //             widgetCloneable: true,
      //             refreshIfExists: false,
      //             appId: tab.eventHandler.id,
      //             appType: tab.eventHandler.type,
      //             appPath: tab.eventHandler.path,
      //             params: options.params || {},
      //             ui: _self,
      //             view: _self,
      //             viewOptions: _self.options
      //         };
      //         _self.startApp(opt);
      //     }
      // })
    },

    _initPanelTabContent: function ($this) {
      var _self = this;
      var configuration = _self.getConfiguration();
      var options = $.extend({}, _self.options.widgetDefinition, _self.options.widgetDefinition.configuration);
      if (!configuration.body || !configuration.body.tabs) {
        return;
      }
      var tabs = configuration.body.tabs;
      var tab = tabs[$this.attr('data-index')];
      $this.attr('data-tab', tab);
      if (tab && tab.eventHandler.id) {
        var opt = {
          target: '_targetWidget',
          targetWidgetId: $this.attr('id'),
          widgetCloneable: true,
          refreshIfExists: false,
          appId: tab.eventHandler.id,
          appType: tab.eventHandler.type,
          appPath: tab.eventHandler.path,
          params: options.params || {},
          ui: _self,
          view: _self,
          viewOptions: _self.options
        };
        _self.startApp(opt);
      }
    },
    // 锚点选择回调处理
    onHashSelection: function (options) {
      var _self = this;
      // 调用父类方法
      _self._superApply(arguments);
      var selection = options.selection || [];
      var configuration = _self.getConfiguration();
      $.each(selection, function (i, tabId) {
        $('.panel-tab', _self.element).each(function () {
          var $this = $(this);
          if ($this.hasClass('active')) {
            return;
          }
          var tabs = configuration.body.tabs;
          var tab = tabs[$this.attr('data-index')];
          if (tab && tab.uuid == tabId) {
            $this.find('a').trigger('click');
          }
        });
      });
    },
    _getPanelTabNavElementById: function (tabId, widgetConfiguration) {
      var _self = this;
      var configuration = widgetConfiguration || _self.getConfiguration();
      var $li = null;
      $('li.panel-tab', _self.element).each(function () {
        var $this = $(this);
        if ($this.hasClass('active')) {
          return;
        }
        var tabs = configuration.body.tabs;
        var tab = tabs[$this.attr('data-index')];
        if (tab && tab.uuid == tabId) {
          $li = $this;
        }
      });
      return $li;
    },
    _createFooter: function () {},
    _bindEvent: function () {
      var _self = this;
      var $element = _self.element;
      var configuration = _self.getConfiguration();
      var header = configuration.header;
      if (header && $.isArray(header.events)) {
        // 添加鼠标形状样式
        // 事件绑定
        $.each(header.events, function (i, eventInfo) {
          var type = eventInfo.type;
          var selector = eventInfo.source;
          $element.find(selector).addClass('event-handler');
          var eventCallback = function (event) {
            var handler = eventInfo.eventManager ? eventInfo.eventManager.eventHandler : eventInfo.handler;
            var params = eventInfo.eventManager ? eventInfo.eventManager.eventParams : eventInfo.params || {};
            var target = eventInfo.target;
            var opt = {
              target: target.position,
              targetWidgetId: target.widgetId,
              refreshIfExists: target.refreshIfExists,
              appType: handler.type,
              appPath: handler.path,
              params: $.extend({}, params.params, appContext.parseEventHashParams(handler, 'menuid')),
              event: event
            };
            _self.startApp(opt);
          };
          // 延迟0.2秒
          if (mouseover === type) {
            $(selector, $element).hoverDelay({
              hoverEvent: eventCallback
            });
          } else {
            $element.on(type, selector, eventCallback);
          }
          if (domloaded === type) {
            $(selector, $element).trigger(type);
          }
        });
      }

      $($element).on('click', '.panel-tab', function () {
        var $this = $(this);
        if (!configuration.body || !configuration.body.tabs) {
          return;
        }
        var tabs = configuration.body.tabs;
        var tab = tabs[$this.attr('data-index')];
        var $content = $('#' + $this.find('a').attr('aria-controls'));
        if (tab) {
          $this.attr('data-tab', tab);
          var searchValue = $this.attr('searchValue') || '';
          $('.search-wrapper input', _self.element).val(searchValue);
          if (searchValue) {
            $('.search-wrapper .input-group', _self.element).removeClass('fadeOutRight').addClass('input-visible fadeInRight');
          }

          _self._bindTabMoreEvent(tab);
          // 更新当前组件锚点
          appContext.updateCurrentHash({
            ui: _self,
            selection: tab.uuid
          });

          if (tab.clickTabToRefresh === '1') {
            var paths = tab.eventHandler.path.split('/');
            var widgetId = paths[paths.length - 1];

            //刷新内部子组件
            appContext.refreshWidgetById(widgetId, true);
          } else {
          }

          // 定位当前选中位置
          $element.find('.nav-tabs').attr('data-translatex-index', $this.attr('data-index'));
          _self.tabTranslatexSetting($this.attr('data-index'));
        }
        if ($content.html() === '') {
          // appModal.showMask();
          _self._initPanelTabContent($content);
        }
      });
      $($element).find('.panel-tab.active').trigger('click');

      // tab内容页显示后触发
      $($element)
        .find('a[data-toggle="tab"]')
        .on('shown.bs.tab', function (e) {
          var tabPanelId = $(e.target).attr('aria-controls');
          if ($('#' + tabPanelId + ' .ui-wBootstrapTable')[0]) {
            // 6149 视图列表的翻页导航支持配置显示位置在页面底部
            $('#' + tabPanelId + ' .ui-wBootstrapTable').trigger('fixedPagination');
          }
        });

      _self.pageContainer.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function () {
        //如果是主页面容器
        if (configuration.showAsContainer) {
          if (!_self.element.hasClass('container-box')) {
            _self.element.addClass('container-box');
          }

          _self._panelBodyResize();
          window.addEventListener('resize', function () {
            _self._panelBodyResize();
          });
        }
      });
      // 解决有时候不进入PageContainerCreationComplete，不设置主面板高度的问题
      setTimeout(function () {
        //如果是主页面容器
        if (configuration.showAsContainer) {
          if (!_self.element.hasClass('container-box')) {
            _self.element.addClass('container-box');
          }

          _self._panelBodyResize();
          window.addEventListener('resize', function () {
            _self._panelBodyResize();
          });
        }
      }, 500);

      window.addEventListener('resize', function () {
        _self.tabsScrollHandle();
      });
    },

    _panelBodyResize: function () {
      var _self = this;
      var $element = _self.element;
      var $panelbody = $element.find('.panel-body:eq(0)');
      $panelbody.css({
        height: 'auto'
      });
      //兄弟节点的高度
      var pageContainer = appContext.getPageContainer().element;
      var height = document.body.clientHeight; //浏览器高度
      var panelBody0 = $element.parents('.panel-body');
      if (panelBody0[0] && panelBody0[0].style.height) {
        height = panelBody0.height();
        if (panelBody0.find('.content')[0]) {
          var padding = panelBody0.find('.content').css('padding');
          padding = padding ? parseFloat(padding) : 0;
          height = height - padding * 2;
        }
      }
      var scrollHeight = document.documentElement.scrollHeight; //滚动的高度

      var siblingsHeight = 0;
      var hasFooter = false;
      $element.siblings().each(function () {
        siblingsHeight += $(this).outerHeight();
        if ($(this).is('.ui-wFooter')) {
          //有底部控件的时候，底部控件会占据容器高度
          hasFooter = true;
        }
      });

      if (!hasFooter) {
        //没有底部控件，则需要扣掉容器的底部padding
        siblingsHeight += parseInt($(pageContainer).css('padding-bottom'));
      }

      //设置内容面板的最小可视高度
      $panelbody.css({
        height: height - siblingsHeight,
        overflow: 'auto'
      });

      if (height == scrollHeight || height >= $(pageContainer).height()) {
        //无滚动高度时候，内容一屏显示
        //作为主页的内容容器（不要套在布局容器内），需要计算兄弟节点的高度，该容器的高度=客户端高度-兄弟节点的高度
        $panelbody.css({
          height: height - siblingsHeight
        });
      }
    },

    _bindTabMoreEvent: function (tab) {
      var _self = this;
      var $element = _self.element;
      if (tab.moreEvents.length) {
        $element.find('.panel-view-more').show();
        $.each(tab.moreEvents, function (i, eventInfo) {
          var type = eventInfo.type;
          var selector = eventInfo.source;
          $element.find(selector).addClass('event-handler');
          var eventCallback = function (event) {
            var handler = eventInfo.eventManager ? eventInfo.eventManager.eventHandler : eventInfo.handler;
            var params = eventInfo.eventManager ? eventInfo.eventManager.eventParams : eventInfo.params || {};
            var target = eventInfo.target;
            var opt = {
              target: target.position,
              targetWidgetId: target.widgetId,
              refreshIfExists: target.refreshIfExists,
              appType: handler.type,
              appPath: handler.path,
              params: $.extend({}, params.params, appContext.parseEventHashParams(handler, 'menuid')),
              event: event
            };
            _self.startApp(opt);
          };
          $(selector, $element).off();
          // 延迟0.2秒
          if (mouseover === type) {
            $(selector, $element).hoverDelay({
              hoverEvent: eventCallback
            });
          } else {
            $element.off(type, selector).on(type, selector, eventCallback);
          }
          if (domloaded === type) {
            $(selector, $element).trigger(type);
          }
        });
      } else {
        $element.find('.panel-view-more').hide();
      }
    },

    refreshBadge: function () {
      var _self = this;
      var configuration = _self.getConfiguration();
      if (configuration.body) {
        if (configuration.body.tabs && configuration.body.tabs.length) {
          $.each(configuration.body.tabs, function (i, item) {
            _self.renderTabBadgeCount(item);
          });
        }
      }
    },

    tabsScrollHandle: function () {
      var _self = this;
      if (!_self.element.hasClass('no-sroll-tabs')) {
        var $element = _self.element.find('.panel-nav-tabs-container');
        var $box = $element.find('.nav-tabs');
        var boxWidth = $element.find('.nav-tabs-box').outerWidth(true);
        var tabsWidth = $element.find('.nav-tabs').outerWidth(true);
        if ($box.length == 1) {
          if (tabsWidth < boxWidth) {
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
                'padding-right': '45px'
              });
              _self.tabMoreBtnSetting($element);
            }
            _self.tabTranslatexEvent();
          }
        }
      }
    },
    tabTranslatexEvent: function () {
      var _self = this;
      if (!_self.element.hasClass('no-sroll-tabs')) {
        var $element = _self.element.find('.panel-nav-tabs-container');
        var $box = $element.find('.nav-tabs');
        var tabLength = $box.find('.panel-tab').length;

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
      }
    },
    // 设置tab的偏移量
    tabTranslatexSetting: function (_index) {
      var _self = this;
      if (!_self.element.hasClass('no-sroll-tabs')) {
        var $element = _self.element.find('.panel-nav-tabs-container');
        var $box = $element.find('.nav-tabs');
        var boxWidth = $element.find('.nav-tabs-box').outerWidth(true);
        var tabsWidth = $element.find('.nav-tabs').outerWidth(true);
        var tabLength = $box.find('.panel-tab').length;
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

          $box.find('.panel-tab').each(function (index, item) {
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
      }
    },
    tabMoreBtnSetting: function ($element) {
      var _self = this;
      if ($element.parent().hasClass('has-panel-header')) {
        var tabs = _self.getConfiguration().body.tabs;
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
            $(_this).popover('show');
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
    }
  });
});
