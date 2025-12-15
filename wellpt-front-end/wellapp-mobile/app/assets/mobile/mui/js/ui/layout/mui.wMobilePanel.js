(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['mui', 'constant', 'commons', 'appContext', 'app-adapter'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, constant, commons, appContext) {
  'use strict';
  var actionBackTemplate = '<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>';
  var navMenuTemplate = '<a class="mui-action-menu mui-icon mui-icon-bars mui-pull-right" href="#{0}"></a>';
  var navMenuTplId = 'wMobilePanel-navMenu';
  var StringBuilder = commons.StringBuilder;
  var StringUtils = commons.StringUtils;
  var ObjectUtils = commons.ObjectUtils;
  var Browser = commons.Browser;
  $.widget('mui.wMobilePanel', $.ui.wWidget, {
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
      });
    },
    _init: function () {},
    _renderView: function () {
      // 生成布局
      this._createLayout();
      // 生成页面组件
      this._createWidgets();

      $.ui.triggerMenuChange();
      $.ui.triggerTitleChange();
    },
    getRenderPlaceholder: function () {
      // 获取渲染组件的占位符
      return $(this.element).children('.mui-content');
    },
    _createLayout: function () {
      var _self = this;
      // 设置面板头部标题
      var configuration = _self.getConfiguration();
      var appData = appContext.getCurrentUserAppData();
      var system = appData.getSystem();
      var module = appData.getModule();
      var app = appData.getApplication();
      var appId = system.id;
      var headerName = system.name;
      if (app) {
        appId = app.id;
        headerName = app.title || app.name;
      } else if (module) {
        appId = module.id;
        headerName = module.title || module.name;
      }
      _self.element[0].classList.add(appId);
      _self.element[0].setAttribute('data-appid', appId);
      var barNarEl = _self.element[0].querySelector('.mui-bar-nav');
      var sb = new StringBuilder();
      // 访问模块或应用，添加返回按钮
      if (/*(module || app) &&*/ _self.isHideBack() === false) {
        sb.append(actionBackTemplate);
      } else if (configuration.eventHanlderIcon && configuration.eventHanlderPath) {
        sb.appendFormat('<a class="mui-icon {0} mui-pull-left"></a>', configuration.eventHanlderIcon);
      }
      // 创建左导航
      if (_self.isShowNavMenu()) {
        if (_self.menuItems.length == 1) {
          var rightBtn = _self._createNavMenu();
          sb.append(rightBtn);
        } else {
          _self.menuPopoverId = commons.UUID.createUUID();
          sb.appendFormat(navMenuTemplate, _self.menuPopoverId);
          _self._createNavMenu();
          _self._bindNavMenuEvents();
        }
      }
      // 添加标题部分
      var requestNavCode = Browser.getQueryString('_requestNavCode');
      var requestNavLevel = Browser.getQueryString('_requestNavLevel') || '1';
      if (requestNavCode && _self.isShowDropDownNav()) {
        sb.appendFormat(
          '<h1 class="mui-title"><span class="mui-title-text">{0}</span><i class="mui-icon mui-icon-arrowdown"></></h1>',
          headerName
        );
        sb.appendFormat('<div id="menu-wrapper" class="menu-wrapper fade-out-up animated hidden">');
        sb.appendFormat('<div id="menu" class="menu bounce-out-up animated">');
        sb.appendFormat('<ul class="mui-table-view"></ul>');
        sb.appendFormat('<div id="menu-backdrop" class="menu-backdrop mui-hidden" style="opacity: 0;"></div>');
        // 延迟加载
        setTimeout(function () {
          _self.renderDropDownNav(requestNavCode, requestNavLevel);
        }, 0);
      } else {
        sb.appendFormat('<h1 class="mui-title">{0}</h1>', headerName);
      }
      // sb.append('<a class="mui-icon mui-action-menu mui-icon-bars mui-pull-right mui-header-menu mui-hidden"></a>');
      barNarEl.innerHTML = sb.toString();
      // 右菜单
      if (_self.isHideBack()) {
        $(barNarEl).on('tap', '.mui-pull-left', function (event) {
          event.preventDefault();
          event.stopPropagation();
          var options = {
            menuItem: this,
            appId: configuration.eventHanlderId,
            appType: configuration.eventHanlderType,
            appPath: configuration.eventHanlderPath
          };
          options.event = event;
          _self.startApp(options);
        });
      }

      // 右菜单
      $(barNarEl).on('tap', '.mui-pull-right', function (event) {
        event.preventDefault();
        event.stopPropagation();
        if (_self.menuItems.length == 1) {
          var appId = this.getAttribute('appId');
          if (StringUtils.isBlank(appId)) {
            return;
          }
          var target = _self.menuItems[0].target || {};
          var eventHandler = _self.menuItems[0].eventHandler;
          var eventParams = _self.menuItems[0].eventParams || {};
          var options = {
            menuItem: this,
            target: target.position,
            targetWidgetId: target.widgetId,
            refreshIfExists: target.refreshIfExists,
            params: eventParams.params,
            appType: eventHandler.type,
            appPath: eventHandler.path
          };
          options.event = event;
          _self.startApp(options);
        } else {
          $('#' + _self.menuPopoverId).popover('toggle');
          return false;
        }
      });
    },
    isShowDropDownNav: function () {
      var _self = this;
      var configuration = _self.getConfiguration();
      return configuration && configuration.showDropDownNav !== false;
    },
    isShowNavMenu: function () {
      var _self = this;
      var configuration = _self.getConfiguration();
      if (configuration == null || typeof configuration === 'undefined') {
        return false;
      }
      _self.showNavMenu = configuration.showNavMenu;
      _self.menuItems = configuration.menuItems;
      if (_self.showNavMenu == null || _self.showNavMenu !== true) {
        return false;
      }
      if (_self.menuItems == null || _self.menuItems.length == 0) {
        return false;
      }
      return true;
    },
    isHideBack: function () {
      var _self = this;
      var configuration = _self.getConfiguration();
      if (configuration == null || typeof configuration === 'undefined') {
        return false;
      }
      return configuration.hideBack === true;
    },
    _createNavMenu: function () {
      var _self = this;
      var configuration = _self.getConfiguration();
      if (configuration.menuItems.length == 1) {
        var button = configuration.menuItems[0];
        if (button.hidden === 'false' || button.hidden === '0' || button.hidden === false) {
          var navBtn = '<a class="mui-icon ' + button.iconClass + ' mui-pull-right mui-header-menu" appId=' + button.uuid + '></a>';
        }
        return navBtn;
      } else {
        configuration.oMenuItems = configuration.menuItems;
        configuration.menuItems = [];
        $.each(configuration.oMenuItems, function (i, menu) {
          if (menu.hidden === 'false' || menu.hidden === '0' || menu.hidden === false) {
            configuration.menuItems.push(menu);
          }
        });
        var templateEngine = appContext.getJavaScriptTemplateEngine();
        configuration.menuPopoverId = _self.menuPopoverId;
        var text = templateEngine.renderById(navMenuTplId, configuration);
        var wrapper = document.createElement('div');
        wrapper.innerHTML = text;
        $(_self.element)[0].appendChild(wrapper);
      }
    },
    _bindNavMenuEvents: function () {
      var _self = this;
      _self.eventHandlers = {};
      $.each(_self.menuItems, function (i, menuItem) {
        if (menuItem.eventHandler) {
          var appId = menuItem.eventHandler.id;
          menuItem.eventHandler['target'] = menuItem.target || {};
          menuItem.eventHandler['eventParams'] = menuItem.eventParams || {};
          _self.eventHandlers[appId] = menuItem.eventHandler;
        }
      });
      // 点击事件
      $('#' + _self.menuPopoverId).on('tap', '.mui-table-view-cell', function (event) {
        // 获取id
        var appId = this.getAttribute('appId');
        if (StringUtils.isBlank(appId)) {
          return;
        }
        var menuItem = _self.getMenuItemByAppId(appId);
        var eventHandler = _self.eventHandlers[appId];
        var options = {
          menuItem: menuItem,
          appType: eventHandler.type,
          appPath: eventHandler.path,
          params: eventHandler.eventParams.params,
          target: eventHandler.target.position,
          targetWidgetId: eventHandler.target.widgetId,
          refreshIfExists: eventHandler.target.refreshIfExists
        };
        options.event = event;
        _self.startApp(options);
        // 隐藏ActionSheet操作按钮
        $('#' + _self.menuPopoverId).popover('toggle');
        return true;
      });
    },
    getMenuItemByAppId: function (appId) {
      var self = this;
      var menuItems = self.menuItems;
      for (var i = 0; i < menuItems.length; i++) {
        var menuItem = menuItems[i];
        if (menuItem.eventHandler && menuItem.eventHandler.id === appId) {
          return menuItem;
        }
      }
    },
    _loadTree: function (params, isShowRoot) {
      var nav = [];
      $.ajax({
        type: 'POST',
        url: ctx + '/basicdata/treecomponent/loadTree',
        data: params,
        async: false,
        dataType: 'json',
        success: function (data) {
          if (isShowRoot === false) {
            $.each(data, function (idx, item) {
              nav = nav.concat(item.children);
            });
          } else {
            nav = data;
          }
        }
      });
      return nav;
    },
    _getNavArray: function (origNav, level, requestNavLevel, navEvent, navOptions, finalNav) {
      var self = this;
      var tempNav = [],
        menuIds = [];
      $.each(origNav, function (idx, item) {
        if (item.data && item.data.hidden) {
          return;
        }
        var navData = item.data;
        var menuId = navData.uuid || navData.id;
        if (StringUtils.isBlank(menuId)) {
          menuId = commons.UUID.createUUID();
          navData.uuid = menuId;
        } else {
          menuIds.push(menuId);
        }
        navData.menuId = menuId; // 统一ID
        navData.menuName = navOptions.navType == 1 ? navData.name : item.name; // 统一名称
        navData.navType = navOptions.navType;
        navData.navEvent = item.isParent ? null : navEvent;
        var appType = navData.eventHanlderType || navData.type || navData.appType;
        var appPath = navData.eventHanlderPath || navData.path || navData.appPath;
        var hasEventHandler = $.trim(appType).length > 0 && $.trim(appPath).length > 0;
        if (level == requestNavLevel && hasEventHandler) {
          self.menuItemMap[menuId] = navData;
          tempNav.push(navData);
        } else {
          self._getNavArray(item.children, level + 1, requestNavLevel, navEvent, navOptions, finalNav);
        }
      });
      if (navOptions.requestAllLevel || $.inArray(navOptions.requestMenuId, menuIds) > -1) {
        Array.prototype.push.apply(finalNav, tempNav);
      }
    },
    renderDropDownNav: function (requestNavCode, requestNavLevel) {
      var self = this;
      var navWidgetDefinition = appContext.getWidgetDefinition(requestNavCode);
      if (!navWidgetDefinition || !navWidgetDefinition.definitionJson) {
        return false;
      }
      var requestMenuId = Browser.getQueryString('_requestMenuId');
      var requestMenuName = Browser.getQueryString('_requestMenuName');
      var requestAllLevel = Browser.getQueryString('_requestAllLevel');
      if (StringUtils.isNotBlank(requestMenuName)) {
        // 设置自定义标题，如果存在
        $.ui.setTitle(requestMenuName);
      }
      self.menuItemMap = self.menuItemMap || {};
      var navDefinitionJson = JSON.parse(navWidgetDefinition.definitionJson);
      var navOptions = navDefinitionJson.configuration;
      // NAV数据
      var nav = [];
      var navEvent; //导航事件统一配置，用于动态生成的导航节点事件处理
      if (navOptions.navType == 1) {
        if (navOptions.navResource) {
          server.JDS.call({
            async: false,
            service: 'appProductIntegrationMgr.getTreeByUuid',
            data: [navOptions.navResource, ['1', '2', '3']],
            success: function (result) {
              if (navOptions.isShowRoot) {
                nav.push(result.data);
              } else {
                nav = result.data.children;
              }
            }
          });
        } else {
          var userAppData = appContext.getCurrentUserAppData();
          var moduleApps = userAppData.getModuleApps();
          for (var i = 0; i < moduleApps.length; i++) {
            var moduleApp = moduleApps[i];
            var mApp = {};
            mApp.id = moduleApp.piUuid;
            mApp.data = {};
            mApp.data.name = moduleApp.name;
            mApp.data.path = moduleApp.path;
            mApp.data.type = 3;
            nav.push(mApp);
          }
        }
      } else if (navOptions.navType == 2) {
        if (navOptions.isShowRoot === true) {
          nav = navOptions.nav;
        } else {
          $.each(navOptions.nav, function (i, navData) {
            nav = nav.concat(navData.children);
          });
        }
      } else if (navOptions.navType == 3) {
        var params = {};
        if (StringUtils.isNotBlank(navOptions.parentId)) {
          params.parentId = navOptions.parentId;
        }
        (params.dataProvider = navOptions.navInterface), (params.nodeTypeInfo = JSON.stringify(navOptions.nodeTypeInfo));
        nav = self._loadTree(params, navOptions.isShowRoot);
        navEvent = {
          eventParams: navOptions.eventParams,
          targetWidgetId: navOptions.targetWidgetId,
          targetPosition: navOptions.targetPosition,
          refreshIfExists: navOptions.refreshIfExists,
          eventHanlderPath: navOptions.eventHanlderPath,
          eventHanlderType: navOptions.eventHanlderType
        };
      }
      var finalNav = [];
      navOptions.requestMenuId = requestMenuId;
      navOptions.requestAllLevel = requestAllLevel == 'true';
      self._getNavArray(nav, 1, requestNavLevel, navEvent, navOptions, finalNav);
      var barNarEl = self.element[0].querySelector('.mui-bar-nav');
      var barTableView = barNarEl.querySelector('.menu-wrapper .mui-table-view');
      var sb = new StringBuilder();
      var arrowdown = barNarEl.querySelector('.mui-icon-arrowdown');
      if (finalNav.length == 0 && arrowdown) {
        arrowdown.parentNode.removeChild(arrowdown);
        return false;
      }
      $.each(finalNav, function (idx, itemNav) {
        sb.appendFormat('<li class="mui-table-view-cell" menuid="{0}">', itemNav.menuId);
        sb.appendFormat('<a href="javascript:;">{0}</a>', itemNav.menuName);
        sb.appendFormat('</li>');
      });
      barTableView.innerHTML = sb.toString();
      function toggleDropDownNav() {
        var navWrapper = barNarEl.querySelector('#menu-wrapper');
        var backdrop = barNarEl.querySelector('#menu-backdrop');
        var navTitle = barNarEl.querySelector('h1.mui-title');
        var nav = barNarEl.querySelector('#menu');
        var navWrapperClassList = navWrapper.classList;
        if (self.busying) {
          return;
        }
        self.busying = true;
        if (navWrapperClassList.contains('mui-active')) {
          self.element[0].classList.remove('menu-open');
          navWrapper.className = 'menu-wrapper fade-out-up animated';
          nav.className = 'menu bounce-out-up animated';
          setTimeout(function () {
            backdrop.style.opacity = 0;
            navWrapper.classList.add('hidden');
          }, 200);
        } else {
          self.element[0].classList.add('menu-open');
          navWrapper.className = 'menu-wrapper fade-in-down animated mui-active';
          menu.className = 'menu bounce-in-down animated';
          backdrop.style.opacity = 1;
        }
        setTimeout(function () {
          self.busying = false;
        }, 200);
      }
      //下拉导航菜单事件绑定
      $(barNarEl).on('tap', '.mui-title', toggleDropDownNav);
      $(barNarEl).on('tap', '.menu-backdrop', toggleDropDownNav);
      $(barNarEl).on('tap', '.menu-wrapper .mui-table-view-cell[menuid]', function (event) {
        var menuCell = this;
        var menuId = menuCell.getAttribute('menuid');
        var menuItem = self.menuItemMap[menuId];
        if (menuItem == null || typeof menuItem === 'undefined') {
          return;
        }
        event.preventDefault();
        event.stopPropagation();
        var appType = menuItem.eventHanlderType || menuItem.type || menuItem.appType;
        var appPath = menuItem.eventHanlderPath || menuItem.path || menuItem.appPath;
        var targetPosition = menuItem.targetPosition;
        var targetWidgetId = menuItem.targetWidgetId;
        var refreshIfExists = menuItem.refreshIfExists;
        var eventParams = menuItem.eventParams; //|| {}
        //使用动态导航的事件配置
        if (!appType && menuItem.navEvent) {
          appType = menuItem.navEvent.eventHanlderType;
        }
        if (!appPath && menuItem.navEvent) {
          appPath = menuItem.navEvent.eventHanlderPath;
        }
        if (!targetPosition && menuItem.navEvent) {
          targetPosition = menuItem.navEvent.targetPosition;
        }
        if (!targetWidgetId && menuItem.navEvent) {
          targetWidgetId = menuItem.navEvent.targetWidgetId;
        }
        if (!refreshIfExists && menuItem.navEvent) {
          refreshIfExists = menuItem.navEvent.refreshIfExists;
        }
        if (!eventParams && menuItem.navEvent) {
          eventParams = menuItem.navEvent.eventParams;
        }
        eventParams = eventParams || {};
        var opt = {
          event: event,
          value: menuId,
          menuId: menuId,
          menuItem: menuItem,
          locationReplace: true,
          name: menuCell.innerText,
          target: targetPosition || '',
          targetWidgetId: targetWidgetId || '',
          refreshIfExists: refreshIfExists,
          appType: appType,
          appPath: appPath,
          params: eventParams
        };
        for (var pKey in eventParams) {
          if (pKey.indexOf('opt_') === 0) {
            var pValue = eventParams[pKey];
            delete eventParams[pKey]; // 删除取值表达式
            pKey = pKey.replace('opt_', '');
            pValue = ObjectUtils.expValue(opt, pValue) || '';
            eventParams[pKey] = pValue;
          }
        }
        eventParams['_requestMenuId'] = opt.menuId;
        eventParams['_requestMenuName'] = opt.name;
        eventParams['_requestNavCode'] = requestNavCode;
        eventParams['_requestNavLevel'] = requestNavLevel;
        self.startApp(opt);
      });
    },
    _createWidgets: function () {
      var self = this;
      var widgetDefinition = self.options.widgetDefinition;
      widgetDefinition.instance = self;
      $.each(widgetDefinition.items, function (index, childWidgetDefinition) {
        appContext.createWidget(childWidgetDefinition, widgetDefinition);
      });
    },
    /**
     * 执行JS模块化方法
     */
    _executeJsModule: function (methodName, args) {
      var self = this;
      var development = self.development;
      var configuration = self.options.widgetDefinition.configuration;
      if (development && typeof development[methodName] === 'function') {
        development[methodName].apply(development, args);
      }
    }
  });
});
