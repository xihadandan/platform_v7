(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['mui', 'commons', 'server', 'constant', 'mui-wWidget', 'appContext', 'formBuilder', 'appModal'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, commons, server, constant, widget, appContext, formBuilder, appModal) {
  'use strict';
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var StorageUtils = commons.StorageUtils;
  var locationStorage = StorageUtils.getStorage(3);
  var scrollOptions = {
    bounce: false,
    indicators: true, // 是否显示滚动条
    deceleration: $.os.ios ? 0.0003 : 0.0009
  };

  var getTabId = function (id) {
    return 'tab-id-' + id;
  };

  $.widget('mui.wMobileTabs', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {},
      // 当前查询的类型
      jsModules: {}
    },

    /**
     * 创建组件
     */
    _createView: function () {
      // 创建js数据源对象
      var _self = this;
      _self._beforeRenderView();
      _self._renderView();
      _self._afterRenderView();
    },
    /**
     * 渲染视图前
     */
    _beforeRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
    },

    /**
     * 视图组件渲染结束调用
     */
    _afterRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('afterRender', [this.options, this.getConfiguration()]);
    },

    /**
     * 视图组件渲染主体方法
     */
    _renderView: function () {
      var _self = this;
      var options = _self.options;
      var $element = $(this.element);
      var placeholder = $element[0];
      var configuration = _self.getConfiguration();
      var locationStorageKey = 'tabs:' + _self.getId();
      // tabs组件嵌套了其他的widget，需要先实现
      var widgetDefinition = options.widgetDefinition;
      if (widgetDefinition.items && widgetDefinition.items.length > 0) {
        $.each(widgetDefinition.items, function (index, childWidgetDefinition) {
          var childWidgetId = childWidgetDefinition.id;
          if (childWidgetId && placeholder.querySelector('#' + childWidgetId)) {
            // 触发默认页签时初始化
            return;
          }
          appContext.createWidget(childWidgetDefinition, widgetDefinition);
        });
      }

      // 创建tab
      if (configuration.tabs && configuration.tabType) {
        var firstTabSelector;
        if (configuration.tabType === '4') {
          // TODO 显示
          var html = new StringBuilder();
          var parentPanel = $(placeholder).closest('.panel');
          // 1、panel>tab，2、tab>panel
          if (parentPanel != null && typeof parentPanel !== 'undefined') {
            // 全屏并插入pageContainer
            placeholder.classList.add('mui-fullscreen');
            var pageContainer = appContext.getPageContainer();
            var pagePlaceholder = pageContainer.getRenderPlaceholder()[0];
            pagePlaceholder.appendChild(placeholder);
            // 拼接HTML(menu-move:主界面不动、菜单移动、mui-scalable:缩放式侧滑)
            var tab4style = '';
            if (configuration.tab4style === 'menu-move') {
              tab4style = 'mui-slide-in';
            } else if (configuration.tab4style === 'main-move-scalable') {
              tab4style = 'mui-scalable';
            }
            html.appendFormat('<div class="wui-off-canvas mui-off-canvas-wrap mui-draggable {0}">', tab4style);
            html.appendFormat('<!--侧滑菜单部分--><aside class="mui-off-canvas-left">');
            html.appendFormat('<div class="mui-scroll-wrapper">');
            html.appendFormat('<div class="mui-scroll">');
            html.appendFormat('<div class="title" style="margin-bottom: 25px;">{0}</div>', configuration.name);
            html.appendFormat('<ul class="mui-table-view mui-table-view-chevron mui-table-view-inverted">');
            $.each(configuration.tabs, function (idx, tab) {
              tab.id = getTabId(tab.id || idx);
              html.appendFormat(
                '<li class="mui-table-view-cell"><a class="mui-navigate-right mui-control-item" href="#{0}"><i class="{2}"></i>{1}</a></li>',
                tab.id,
                tab.name,
                (tab.icon && tab.icon.className) || ''
              );
            });
            html.append('</ul>');
            if (StringUtils.isNotBlank(configuration.tab4help)) {
              html.appendFormat('<div class="content">{0}</div>', configuration.tab4help);
            }
            html.append('</div>');
            html.append('</div>');
            html.append('</aside>');
            html.append('<!--主界面部分--><div class="mui-inner-wrap">');
            html.append('<!-- off-canvas backdrop --><div class="mui-off-canvas-backdrop"></div>');
            html.append('</div>');
            html.append('</div>');
            placeholder.appendChild($.dom(html.toString())[0]); // placeholder.innerHTML = html.toString();
            var pageContent = pagePlaceholder.firstChild;
            // 面板隐藏时,mui-fullscreen的页签组件也隐藏
            if (pageContent.classList.contains('panel')) {
              pageContent.addEventListener('panel.shown', function () {
                // console.log("panel.shown");
                placeholder.classList.remove('mui-hidden');
              });
              pageContent.addEventListener('panel.hidden', function () {
                // console.log("panel.hidden");
                placeholder.classList.add('mui-hidden');
              });
            }
            // 侧滑菜单界面均支持区域滚动；
            $('.mui-off-canvas-wrap .mui-scroll-wrapper', placeholder).scroll(scrollOptions);
            // 生成内容主体放到mui-content
            var tabHtml = new StringBuilder();
            $.each(configuration.tabs, function (idx, tab) {
              tabHtml.appendFormat('<div id="{0}" class="mui-control-content"></div>', tab.id);
            });
            var panelContent = $('.mui-content', pageContent)[0];
            panelContent.classList.add('wui-off-canvas-content');
            panelContent.innerHTML = tabHtml.toString();
            // 头部添加引导菜单项
            var menuBar = $.dom('<a class="mui-icon mui-action-menu mui-icon-location mui-pull-left"></a>')[0];
            var panelHeader = $('header.mui-bar-nav', pageContent)[0];
            panelHeader.appendChild(menuBar);
            $('.mui-off-canvas-wrap>.mui-inner-wrap', placeholder)[0].appendChild(pageContent);
            // 初始化侧滑菜单
            var offCanvasApi = $('.mui-off-canvas-wrap', placeholder).offCanvas({
              opacity: 0 // 根据wui-action-dragging处理侧滑背景透视问题
            });
            // debugger
            // 绑定打开侧滑菜单事件
            menuBar.addEventListener('tap', function () {
              offCanvasApi.show();
            });
            // 设置标题和绑定关闭侧滑菜单事件
            $element.on('tap', 'aside.mui-off-canvas-left a.mui-control-item', function (event) {
              var aItem = this;
              $.ui.setTitle(aItem.textContent || aItem.innerText);
              if (event.detail && event.detail.customTrigger === true) {
                return; // 自定义事件不触发关闭
              }
              offCanvasApi.close();
            });
            firstTabSelector = 'aside.mui-off-canvas-left a.mui-control-item';
          } else {
            // TODO tab>panel
          }
        } else if (configuration.tabType === '3') {
          var html = new StringBuilder();
          html.append('<nav class="mui-bar mui-bar-tab">');
          $.each(configuration.tabs, function (idx, tab) {
            tab.id = getTabId(tab.id || idx);
            html.appendFormat('<a class="mui-tab-item" href="#{0}">', tab.id);
            if (tab.icon && tab.icon.className) {
              html.appendFormat('<i class="{0}"></i>', tab.icon.className);
            }
            html.appendFormat('<span class="mui-tab-label">{0}</span>', tab.name);
            html.appendFormat('</a>');
          });
          html.append('</nav>');
          html.append('<div class="wui-bar-tab-content">');
          $.each(configuration.tabs, function (idx, tab) {
            html.appendFormat('<div id="{0}" class="mui-control-content"></div>', tab.id);
          });
          html.append('</div>');
          placeholder.innerHTML = html.toString();
          firstTabSelector = '.mui-bar-tab>a.mui-tab-item';
        } else if (configuration.tabType === '2') {
          var html = new StringBuilder();
          html.append('<div class="wui-tab-segmented">');
          html.appendFormat('<div class="mui-segmented-control {0}">', ' mui-segmented-control-' + configuration.tab2style);
          $.each(configuration.tabs, function (idx, tab) {
            tab.id = getTabId(tab.id || idx);
            html.appendFormat(
              '<a class="mui-control-item" href="#{0}"><i class="{2}"></i>{1}</a>',
              tab.id,
              tab.name,
              (tab.icon && tab.icon.className) || 'mui-icon'
            );
          });
          html.append('</div>');
          html.append('</div>');
          html.append('<div class="wui-tab-segmented-content">');
          $.each(configuration.tabs, function (idx, tab) {
            html.appendFormat('<div id="{0}" class="mui-control-content"></div>', tab.id);
          });
          html.append('</div>');
          placeholder.innerHTML = html.toString();
          firstTabSelector = '.mui-segmented-control>a.mui-control-item';
        } else if (configuration.tabType === '1') {
          var html = new StringBuilder();
          html.append('<div class="wui-tab-segmented">');
          html.appendFormat('<div class="mui-scroll-wrapper mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">');
          html.appendFormat('<div class="mui-scroll">');
          $.each(configuration.tabs, function (idx, tab) {
            tab.id = getTabId(tab.id || idx);
            html.appendFormat('<a class="mui-control-item" href="#{0}">{1}</a>', tab.id, tab.name, (tab.icon && tab.icon.className) || '');
          });
          html.appendFormat('</div>');
          html.append('</div>');
          html.append('</div>');
          html.append('<div class="wui-tab-segmented-content">');
          $.each(configuration.tabs, function (idx, tab) {
            html.appendFormat('<div id="{0}" class="mui-control-content"></div>', tab.id);
          });
          html.append('</div>');
          placeholder.innerHTML = html.toString();
          firstTabSelector = '.mui-segmented-control>.mui-scroll>a.mui-control-item';
          // 定义tab滚动
          $('.wui-tab-segmented>.mui-scroll-wrapper', placeholder).scroll(scrollOptions);
        }

        // 定义tab处理事件
        if (StringUtils.isNotBlank(firstTabSelector)) {
          // 定义切换tab事件
          $element.on('shown.mui.tab', '.mui-control-content', function (event) {
            var tabContent = this;
            var tabNumber = event.detail.tabNumber;
            var tabItem = configuration.tabs[tabNumber];
            _self.currentTab = tabItem;
            if (tabItem.eventHandler) {
              event.stopPropagation();
              event.preventDefault();
              var paths = tabItem.eventHandler.path.split('/');
              var widgetId = paths[paths.length - 1];
              var $widget = $('#' + widgetId, placeholder);
              if ($widget && $widget.length > 0) {
                // 保存选择项
                locationStorage.setItem(locationStorageKey, event.target.id);
                // 事件处理来自本tabs组件中的子组件，则将该组件移到对应的位置
                if ($widget[0].parentNode === tabContent) {
                  // 已经存在
                } else {
                  tabContent.appendChild($widget[0]);
                }
              } else {
                var target = tabItem.target || {};
                var eventParams = tabItem.eventParams || {};
                var optTarget, optTargetWidgetId, optRefreshIfExists;
                if (
                  (target.position === '_targetWidget' && target.widgetId) ||
                  target.position === '_blank' ||
                  target.position === '_self' ||
                  target.position === '_dialog'
                ) {
                  optTarget = target.position;
                  optTargetWidgetId = target.widgetId;
                  optRefreshIfExists = target.refreshIfExists;
                } else {
                  optTarget = '_targetWidget';
                  optTargetWidgetId = tabItem.id;
                  optRefreshIfExists = false;
                  // 保存选择项
                  locationStorage.setItem(locationStorageKey, event.target.id);
                }
                // 事件处理来自其他页面
                var opt = {
                  view: _self,
                  viewOptions: _self.options,
                  params: eventParams.params,
                  target: optTarget,
                  targetWidgetId: optTargetWidgetId,
                  refreshIfExists: optRefreshIfExists,
                  appType: tabItem.eventHandler.type,
                  appPath: tabItem.eventHandler.path
                };
                _self.startApp(opt);
              }
            }
            _self.invokeDevelopmentMethod('onShowTab', [tabNumber, _self.options, _self.getConfiguration(), tabItem]);
          });

          // 默认激活第一个
          var defaultTabItem = null;
          var locationHash = locationStorage.getItem(locationStorageKey);
          if (StringUtils.isNotBlank(locationHash)) {
            var preLocationSelector = 'a[href$=' + locationHash + ']';
            defaultTabItem = $(preLocationSelector, placeholder)[0];
          }
          defaultTabItem = defaultTabItem || $(firstTabSelector, placeholder)[0];
          var eventDetail = {
            gesture: {
              // fix e.detail && e.detail.gesture.preventDefault();
              preventDefault: $.noop
            },
            customTrigger: true
          };
          if (defaultTabItem) {
            // 触发targetHandle
            $.trigger(defaultTabItem, $.EVENT_START, eventDetail);
            // 触发选中
            $.trigger(defaultTabItem, 'tap', eventDetail);
          }

          /*  // 浏览器退回跳转
					window.addEventListener('hashchange',function(event) {
						var locationHash = window.location.hash;
						if(locationHash && locationHash.charAt(0) === "#") {
							var preLocationSelector = "a[href$="+(locationHash.substring(1))+"]";
							var defaultTabItem = $(preLocationSelector, placeholder)[0];
							if(defaultTabItem != null && typeof locationHash !== "undefined") {
								// 触发targetHandle
								$.trigger(defaultTabItem, $.EVENT_START, eventDetail);
								// 触发选中
								$.trigger(defaultTabItem, "tap", eventDetail);
							}
						}
					});
					*/
        }
      }
    },
    getChildWidgetByType: function (wTypes) {
      var self = this;
      // String or Array
      wTypes = wTypes || [];
      var widgetDefinitions = self._superApply(arguments);
      // 获取页签子节点
      if (self.currentTab && self.currentTab.eventHandler) {
        var paths = self.currentTab.eventHandler.path.split('/');
        var childWidget = appContext.getWidgetById(paths[paths.length - 1]);
        if (childWidget && childWidget.getChildWidgetByType) {
          widgetDefinitions = widgetDefinitions.concat(childWidget.getChildWidgetByType(wTypes));
        }
      }
      return widgetDefinitions;
    }
  });
  // 获取tabId(加前缀)
  $.ui.wMobileTabs.tabId = getTabId;
});
