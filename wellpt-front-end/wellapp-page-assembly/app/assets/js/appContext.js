define(['jquery', 'constant', 'commons', 'server', 'templateEngine'], function ($, constant, commons, server, templateEngine) {
  // 派发器
  var _prefix = 'ID_';
  var Dispatcher = function () {
    this._callbacks = {};
    this._isDispatching = false;
    this._isHandled = {};
    this._isPending = {};
    this._lastID = 1;
  };
  Dispatcher.prototype.register = function (callback) {
    var id = _prefix + this._lastID++;
    this._callbacks[id] = callback;
    return id;
  };
  Dispatcher.prototype.dispatch = function (payload, dispatchCallbacks) {
    this._startDispatching(payload, dispatchCallbacks);
    try {
      for (var id in this._callbacks) {
        if (this._isPending[id]) {
          continue;
        }
        // console.log("调用应用派发器:" + id);
        // 返回true结束派发
        if (this._invokeCallback(id) === true) {
          console.log('调用应用派发器:' + id + '，返回true结束派发.');
          break;
        }
      }
    } finally {
      this._stopDispatching(payload, dispatchCallbacks);
    }
  };
  Dispatcher.prototype._invokeCallback = function (id) {
    this._isPending[id] = true;
    this._callbacks[id](this._pendingPayload);
    this._isHandled[id] = true;
  };
  Dispatcher.prototype.isDispatching = function () {
    return this._isDispatching;
  };
  Dispatcher.prototype._invokeCallback = function (id) {
    this._isPending[id] = true;
    this._callbacks[id](this._pendingPayload);
    this._isHandled[id] = true;
  };
  Dispatcher.prototype._startDispatching = function (payload, dispatchCallbacks) {
    for (var id in this._callbacks) {
      this._isPending[id] = false;
      this._isHandled[id] = false;
    }
    this._pendingPayload = payload;
    this._isDispatching = true;
    if (dispatchCallbacks && $.isFunction(dispatchCallbacks.onStart)) {
      dispatchCallbacks.onStart.call(this, payload);
    }
  };
  Dispatcher.prototype._stopDispatching = function (payload, dispatchCallbacks) {
    delete this._pendingPayload;
    this._isDispatching = false;
    if (dispatchCallbacks && $.isFunction(dispatchCallbacks.onStop)) {
      dispatchCallbacks.onStop.call(this, payload);
    }
  };

  // 个性化配置数据
  var Preferences = function (preferences) {
    this.preferences = preferences || {};
  };
  // 设值
  Preferences.prototype.put = function (path, value, flush) {
    var self = this;
    self.preferences[path] = value;
    if (flush === true && self.flush()) {
      return true;
    }
  };
  // 取值
  Preferences.prototype.get = function (path, callback) {
    return this.preferences[path];
  };
  // 保存
  Preferences.prototype.flush = function () {
    var preferences = JSON.stringify(this.preferences);
    JDS.call({
      service: 'appProductIntegrationContextService.savePiPreferences',
      data: [preferences],
      async: false,
      version: '',
      success: function (result) {},
      error: function (jqXHR, statusText, error) {
        console.error('保存个性化数据失败');
      }
    });
  };
  // 当前用户的应用数据
  var CurrentUserAppData = function (appData) {
    this.appData = appData;
    if (this.appData.moduleApps) {
    }
  };
  // 1、获取当前用户使用的主题
  CurrentUserAppData.prototype.getTheme = function () {
    return this.appData.theme;
  };
  // 2、获取用户当前访问的系统
  CurrentUserAppData.prototype.getSystem = function () {
    return this.appData.system;
  };
  // 3、获取当前用户访问的模块
  CurrentUserAppData.prototype.getModule = function () {
    return this.appData.module;
  };
  // 4、获取当前模块加载的应用
  CurrentUserAppData.prototype.getModuleApps = function () {
    return this.appData.moduleApps;
  };
  // 5、获取当前用户访问的应用
  CurrentUserAppData.prototype.getApplication = function () {
    return this.appData.application;
  };
  // 6、获取当前应用加载的功能
  CurrentUserAppData.prototype.getAppFunctions = function () {
    return this.appData.appFunctions;
  };
  var SessionExpiredHanlder = function (faultData, options) {
    var logout = function () {
      if (top) {
        top.location.href = ctx + faultData.data;
      } else {
        location.href = ctx + faultData.data;
      }
    };
    appModal.alert(faultData.msg);
    logout();
  };
  var JDS = server.JDS;
  var getDefaultErrorHandler = function (defaultErrorHandler) {
    var errorHandler = server.ErrorHandler.getInstance(defaultErrorHandler);
    return errorHandler.registerSessionExpired(SessionExpiredHanlder);
  };
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var ArrayUtils = commons.ArrayUtils;
  var _theme = 'app.theme';
  var WebApp = window.WebApp || {};
  // 系统应用上下文
  var AppContext = function () {
    // 当前用户数据
    this.currentUserAppData = {};
    // // 当前系统选择的模块
    // this.selectedModule = {};
    // // 当前系统选择的应用
    // this.selectedApp = {};
    // 当前已加载的产品集成信息
    this.piItems = {};
    // 当前已加载的功能
    this.functions = {};
    // 当前已加载的应用
    this.applications = {};
    // 当前已加载的组件定义<id/uuid, json>
    this._widgetDefinitions = {};
    // 当前已加载的JS模板
    this.jsTemplates = {};
    // 所有主题
    this.themes = [];
    // 应用派发器
    this.dispatcher = new Dispatcher();
    // 环境变量
    this.environment = WebApp.environment || {};
    // 窗口管理器
    this._windowManager = {};
    // 本地数据存储
    this._storages = {};
    // 页面容器
    this.pageContainer = $('body');
    // 创建的组件
    // this.widgets = [];
    // 创建的组件<id, ui>
    this.widgetMap = {};
    // 创建的组件数量
    this._widgetCreatedCount = 0;
    // 异步创建widget的timeout
    this._timeout = 0;
    // 等待创建的widget
    this._waitForWidgets = {};
  };
  // 初如化数据
  AppContext.prototype.init = function (currentUserAppData) {
    var self = this;
    self.preferences = new Preferences(currentUserAppData.preferences);
    self.currentUserAppData = new CurrentUserAppData(currentUserAppData);
    if (currentUserAppData.appFunctions != null) {
      $.each(currentUserAppData.appFunctions, function () {
        self.functions[this.path] = JSON.parse(this.definitionJson);
      });
    }
  };

  // 当前用户上下文数据
  // 1、获取当前用户的应用数据
  AppContext.prototype.getCurrentUserAppData = function () {
    return this.currentUserAppData;
  };
  // 2、获取当前应用的发起都路径
  AppContext.prototype.getInitiatorPath = function () {
    var storage = commons.StorageUtils.getStorage();
    return storage.getItem(constant.KEY_APP_INITIATOR_PATH);
  };
  // 3、获取当前应用的发起都路径
  AppContext.prototype.setInitiatorPath = function (path) {
    var storage = commons.StorageUtils.getStorage();
    storage.setItem(constant.KEY_APP_INITIATOR_PATH, path);
  };
  // 4、获取当前用户个性化配置数据
  AppContext.prototype.getPreferences = function () {
    return this.preferences;
  };

  // 系统上下文数据
  // 1.1、获取系统主题
  AppContext.prototype.getTheme = function (id) {
    return this.getCurrentUserAppData().getTheme();
  };
  // 1.2、设置系统主题
  AppContext.prototype.setTheme = function (id) {
    // 设置页面主题
    var pageUuid = WebApp.pageDefinition.uuid || WebApp.currentUserAppData.pageUuid;
    JDS.call({
      service: 'appContextService.setThemeOfPage',
      data: [id, pageUuid],
      async: false
    });
    // 设置cookie主题
    this.require(['cookie'], function () {
      $.cookie(_theme, id, {
        path: constant.Separator.Slash
      });
      window.location.reload();
    });
  };
  // 1.3、获取系统所有主题
  AppContext.prototype.getAllThemes = function () {
    if (this.themes.length !== 0) {
      return this.themes;
    }
    var themes = this.themes;
    JDS.call({
      service: 'appContextService.getAllThemes',
      async: false,
      success: function (result) {
        $.each(result.data, function () {
          themes.push(this);
        });
      },
      error: function (jqXHR, statusText, error) {
        console.error('主题请求异常');
      }
    });
    return themes;
  };

  // 2.1、根据产品集成信息UUID获取集成信息
  AppContext.prototype.getPiItem = function (piUuid) {
    var _self = this;
    var piItem = _self.piItems[piUuid];
    if (piItem != null) {
      return piItem;
    }
    return _self._loadPiItem(piUuid);
  };
  // 2.2、从服务器加载产品集成信息
  AppContext.prototype._loadPiItem = function (piUuid) {
    var _self = this;
    if (_self.piItems[piUuid] != null) {
      return _self.piItems[piUuid];
    } else {
      JDS.call({
        service: 'appContextService.getPiItemByPiUuid',
        data: [piUuid],
        async: false,
        success: function (result) {
          _self.piItems[piUuid] = result.data;
        },
        error: function (jqXHR, statusText, error) {
          getDefaultErrorHandler(function () {
            console.error('功能请求异常');
            appModal.alert('产品集成信息请求异常，请确认存在集成信息UUID: ' + piUuid);
          }).handle(jqXHR, statusText, error);
        }
      });
    }
    return _self.piItems[piUuid];
  };

  // 4、根据功能ID信息获取功能
  AppContext.prototype.getFunction = function (functionId, functionName, asyncCallback) {
    var appFunction = this.functions[functionId];
    if (appFunction != null) {
      if (asyncCallback != undefined) {
        asyncCallback(appFunction);
        return;
      }
      return appFunction;
    }
    return this._loadFunction(functionId, functionName, asyncCallback);
  };
  // 4.1、从服务器加载功能
  AppContext.prototype._loadFunction = function (functionId, functionName, asyncCallback) {
    var self = this;
    if (self.functions[functionId] != null) {
      return this.functions[functionId];
    } else {
      JDS.call({
        service: 'appContextService.getFunctionByPath',
        data: [functionId],
        async: asyncCallback != undefined,
        mask: false,
        success: function (result) {
          if (result.data == null) {
            appModal.error('功能' + (functionName ? '[' + functionName + ']' : '') + '不存在!');
          } else if (!result.data.authenticated) {
            appModal.error('功能[' + result.data.name + ']无权限访问!');
          } else if (result.data.definitionJson != null) {
            self.functions[functionId] = JSON.parse(result.data.definitionJson);
            if (asyncCallback != undefined) {
              asyncCallback(self.functions[functionId]);
            }
          } else {
            console.error('功能' + (functionName ? '[' + functionName + ']' : '') + '请求返回数据异常: ' + JSON.stringify(result.data));
            appModal.error('功能' + (functionName ? '[' + functionName + ']' : '') + '请求返回数据异常!');
          }
        },
        error: function (jqXHR, statusText, error) {
          getDefaultErrorHandler(function () {
            console.error('功能' + (functionName ? '[' + functionName + ']' : '') + '请求异常，集成路径：' + functionId);
            appModal.error('功能' + (functionName ? '[' + functionName + ']' : '') + '请求异常');
          }).handle(jqXHR, statusText, error);
        }
      });
    }
    return self.functions[functionId];
  };
  // 5、根据应用ID信息获取应用
  AppContext.prototype.getApplication = function (appId) {
    var application = this.applications[appId];
    if (application != null) {
      return application;
    }
    return this._loadApplication(appId);
  };
  // 5.1、从服务器加载应用
  AppContext.prototype._loadApplication = function (appId) {
    var self = this;
    if (self.applications[appId] != null) {
      return this.applications[appId];
    } else {
      JDS.call({
        service: 'appContextService.getApplicationByPath',
        data: [appId],
        async: false,
        success: function (result) {
          if (result.data == null) {
            console.error('集成路径为[' + appId + ']的应用不存在或无权限访问!');
            appModal.error('应用不存在!');
          } else if (result.data != null) {
            if (!result.data.authenticated) {
              appModal.error('应用[' + result.data.name + ']无权限访问!');
              return;
            }
            self.applications[appId] = result.data;
            // 应用关联的功能
            var cfUuid = result.data.cfUuid;
            var cfFunction = result.data.cfFunction;
            if (StringUtils.isNotBlank(cfUuid) && cfFunction != null) {
              self.functions[cfUuid] = JSON.parse(cfFunction.definitionJson);
            }
          } else {
            console.error('应用请求返回数据异常: ' + JSON.stringify(result.data));
            appModal.error('应用请求异常');
          }
        },
        error: function (jqXHR, statusText, error) {
          getDefaultErrorHandler(function () {
            console.error('应用[' + appId + ']请求异常');
            appModal.error('应用请求异常');
          }).handle(jqXHR, statusText, error);
        }
      });
    }
    return self.applications[appId];
  };
  // 6、根据组件定义ID获取组件定义
  AppContext.prototype.getWidgetDefinition = function (widgetDefId, widgetCloneable, asyncCallback) {
    var widgetDefinition = this._widgetDefinitions[widgetDefId];
    if (widgetDefinition != null) {
      if (asyncCallback != undefined) {
        asyncCallback(widgetDefinition);
        return;
      }
      return widgetDefinition;
    }
    return this._loadWidgetDefinition(widgetDefId, widgetCloneable, asyncCallback);
  };
  // 6.1、从服务器加载组件定义
  AppContext.prototype._loadWidgetDefinition = function (widgetDefId, widgetCloneable, asyncCallback) {
    var self = this;
    var wdId = widgetDefId;
    if (self._widgetDefinitions[wdId] != null) {
      if (asyncCallback != undefined) {
        asyncCallback(this._widgetDefinitions[wdId]);
        return;
      }
      return this._widgetDefinitions[wdId];
    } else {
      JDS.call({
        service: 'appContextService.getAppWidgetDefinitionById',
        data: [wdId, widgetCloneable],
        async: asyncCallback != undefined,
        success: function (result) {
          var wd = result.data;
          if (wd == null) {
            console.log('ID为[' + wdId + ']的组件定义不存在或无权限访问!');
            appModal.alert('ID为[' + wdId + ']的组件定义不存在或无权限访问!');
          } else if (wd != null) {
            wdId = wd.id;
            self._widgetDefinitions[wdId] = wd;
            // 执行requirejs组件依赖配置脚本
            if (wd.configScript) {
              if (typeof wd.configScript == 'string') {
                eval(wd.configScript);
              } else {
                requirejs.config(wd.configScript);
              }
            }
            if (asyncCallback != undefined) {
              asyncCallback(wd);
            }
          } else {
            console.error('组件定义请求返回数据异常: ' + JSON.stringify(wd));
            appModal.alert('组件定义请求返回数据异常: ' + JSON.stringify(wd));
          }
        },
        error: function (jqXHR, statusText, error) {
          getDefaultErrorHandler(function () {
            console.error('组件定义请求异常');
            appModal.alert('组件定义请求异常，请确认存在组件定义ID: ' + wdId);
          }).handle(jqXHR, statusText, error);
        }
      });
    }
    return self._widgetDefinitions[wdId];
  };
  // 7、JS模块配置并加载
  AppContext.prototype.require = function (modules, callback, errback, optional) {
    // 加载并配置JS模块
    var unConfigs = [];
    $.each(modules, function (i) {
      var module = modules[i] + ''; // IE8兼容性问题，typeof
      // modules[i]为object,这里转换成string,否则JDS.call中转换成JSON会出现异常
      if (StringUtils.isBlank(WebApp.configJsModules[module])) {
        unConfigs.push(module);
      }
    });
    if (unConfigs.length > 0) {
      this._configJavaScriptModules(unConfigs);
    }
    return require(modules, callback, errback, optional);
  };
  // 7.1、从服务器加载JS模块配置脚本并执行
  AppContext.prototype._configJavaScriptModules = function (modules) {
    $.ajax({
      type: 'POST',
      data: { ids: modules },
      async: false,
      url: '/jsexplainer/getJavascriptRequirejsConfig?isMobileApp=' + this.isMobileApp(),
      success: function (result) {
        console.debug(result);
        $.extend(true, WebApp.configJsModules, result.paths);
        requirejs.config(result);
      }
    });
  };
  // 8、执行JS模块
  AppContext.prototype.executeJsModule = function (app, options) {
    if (app) {
      if (StringUtils.isBlank(options.action)) {
        app(options);
      } else if (options.action in app && $.isFunction(app[options.action])) {
        app[options.action](options);
      } else if ($.isFunction(app)) {
        var obj = new app(options);
        if (options.action in obj && $.isFunction(obj[options.action])) {
          obj[options.action](options);
        }
      }
    } else {
      console.error('JsModule is null, ignore execute');
    }
  };

  // 页面组件管理
  // 1、创建组件
  AppContext.prototype.createWidget = function (widgetDefinition, containerDefinition, callback, initiator) {
    var _self = this;
    var pageIdSelector = 'body';
    if (StringUtils.isNotBlank(containerDefinition.id)) {
      pageIdSelector = '#' + containerDefinition.id;
    }

    // 父容器如果有特定事件参数，则透传给子组件
    if (containerDefinition && containerDefinition.params) {
      // 合并父容器参数到子组件事件参数上，如果父容器事件参数key与子组件事件参数key相同，则会使用子组件参数覆盖掉父容器事件参数
      // 所以定义参数key时候要注意
      widgetDefinition.params = $.extend({}, containerDefinition.params, widgetDefinition.params);
    }

    //定义了组件的创建准备事件，由发起方定义
    if (containerDefinition && containerDefinition.onPrepare) {
      widgetDefinition.onPrepare = containerDefinition.onPrepare;
    }

    var wtype = widgetDefinition.wtype;
    var len = wtype.indexOf('_');
    if (len > 0) {
      wtype = wtype.substring(0, len);
    }
    var widgetSelector = '#' + widgetDefinition.id;
    // 每3个组件一组，非阻塞执行创建组件
    this._widgetCreatedCount++;
    var waitForWidgets = _self._waitForWidgets;
    waitForWidgets[widgetSelector] = widgetSelector;
    var _createWidget = function (widgetSelector, pageIdSelector, wtype, widgetDefinition, containerDefinition, callback, initiator) {
      try {
        var $widget = $(widgetSelector, pageIdSelector);
        if ($widget.length <= 0) {
          appModal.hideMask();
          throw Error('未找到元素widgetSelector：' + widgetSelector);
        }
        var widgetInitiator = initiator;
        if (widgetInitiator == null && containerDefinition.id) {
          var containerWidget = appContext.getWidgetById(containerDefinition.id);
          if (containerWidget != null && containerWidget.getInitiator) {
            widgetInitiator = containerWidget.getInitiator();
          }
        }
        $widget[wtype]({
          widgetDefinition: widgetDefinition,
          containerDefinition: containerDefinition,
          containerInstance: containerDefinition.instance,
          initiator: widgetInitiator,
          onCreate: function () {
            appModal.hideMask();
            // 创建后回调处理
            var widget = this;
            // 删除已存在的内存对像
            _self._removeWidgetById(widgetDefinition.id);
            _self.widgetMap[widgetDefinition.id] = widget;
            if ($.isFunction(callback)) {
              callback.call(widget, widget);
            }
            // 删除等待的组件标记
            delete waitForWidgets[widgetSelector];
            // 触发已创建事件
            _self.onWidgetCreated(widget);
          },

          onPrepare: function () {
            if (widgetDefinition.onPrepare && $.isFunction(widgetDefinition.onPrepare[widgetDefinition.id])) {
              widgetDefinition.onPrepare[widgetDefinition.id].call(this);
            }
          }
        });
      } catch (e) {
        console.error(e);
        console.error(widgetDefinition);
        // 删除等待的组件标记
        delete waitForWidgets[widgetSelector];
      } finally {
        // console.log("complete create widget: " + wtype + "——" + widgetSelector);
      }
    };
    // console.log("start create widget: " + wtype + "——" + widgetSelector);
    if (_self._widgetCreatedCount < 20 && _self._widgetCreatedCount % 2 === 0) {
      _self._timeout += 10;
      setTimeout(function () {
        if (waitForWidgets[widgetSelector]) {
          _createWidget(widgetSelector, pageIdSelector, wtype, widgetDefinition, containerDefinition, callback, initiator);
        }
      }, _self._timeout);
    } else {
      _createWidget(widgetSelector, pageIdSelector, wtype, widgetDefinition, containerDefinition, callback, initiator);
    }
  };
  // 1.1、组件创建成功回调
  AppContext.prototype.onWidgetCreated = function (widget) {
    var self = this,
      _dispatchEvent;
    if (self.isMobileApp()) {
      _dispatchEvent = function (type, eventData, ui) {
        if (ui) {
          $.trigger(ui.element[0] || ui.element, type, [eventData, ui]);
        } else {
          console.log("widget is null, cann't trigger event [" + type + ']');
        }
      };
    } else {
      _dispatchEvent = function (type, eventData, ui) {
        var e = new jQuery.Event(type, true, true);
        e.detail = eventData || {};
        if (ui) {
          $(ui.element).trigger(e, ui);
        } else {
          console.log("widget is null, cann't trigger event [" + type + ']');
        }
      };
    }

    // 判断是否需要等待
    self._waitForCreateWidgets(_dispatchEvent, constant.WIDGET_EVENT.WidgetCreated, {}, widget);
  };
  // 1.2、组件创建出错回调
  AppContext.prototype.onWidgetCreateError = function (widget) {
    var _self = this;
    var waitForWidgets = _self._waitForWidgets;
    var widgetId = widget.getId();
    var widgetSelector = '#' + widgetId;
    console.error('complete create widget with error: ' + widget.getWtype() + '——' + widgetSelector);

    // 删除已存在的内存对像
    _self._removeWidgetById(widgetId);
    _self.widgetMap[widgetId] = widget;
    // 删除等待的组件标记
    delete waitForWidgets[widgetSelector];
    // 触发已创建事件
    _self.onWidgetCreated(widget);
  };
  // 2.1、判断是否等待创建组件，回调函数起作用
  AppContext.prototype._waitForCreateWidgets = function (callback, type, eventData, ui) {
    var _self = this;
    var wait = false;
    for (var p in _self._waitForWidgets) {
      var widget = _self.widgetMap[p.substring(1)];
      if (widget == null || typeof widget === 'undefined') {
        wait = true;
      } else {
        delete _self._waitForWidgets[p];
      }
      // console.log("waitForCreateWidgets: " + p);
      // 存在加载错误的JS模块，删除当前所有组件的等待创建信息
      if ($.isArray(WebApp.errorRequireModules) && WebApp.errorRequireModules.length > 0) {
        _self._waitForWidgets = {};
      }
      break;
    }

    if (wait) {
      // console.log("waitForCreateWidgets millisecond: " + 50);
      setTimeout(function () {
        _self._waitForCreateWidgets(callback, type, eventData, ui);
      }, 50);
    } else {
      callback(type, eventData, ui);
      return;
    }
  };
  // 2.2、判断是否等待创建组件
  AppContext.prototype.isWaitForCreateWidgets = function () {
    return $.isEmptyObject(this._waitForWidgets);
  };
  // 3、通过组件ID获取对应的组件
  AppContext.prototype.getWidgetById = function (id) {
    if (typeof id != 'string') {
      return null;
    }
    var map = this.widgetMap;
    if (map[id] != null) {
      return map[id];
    }
    // $.each(this.widgets, function(index, widget) {
    // map[widget.getId()] = widget;
    // });
    var $element = id.indexOf('#') == 0 ? $(id) : $('#' + id);
    if ($element.length != 1) {
      return null;
    }
    var widgetFullName = $element.data('widgetFullName');
    if (StringUtils.isBlank(widgetFullName)) {
      return null;
    }
    var $widget = $element.data(widgetFullName);
    return $widget.getWtype ? $widget : null;
  };
  // 3、通过组件ID获取对应的组件
  AppContext.prototype.getWidgetByCssSelector = function (cssSelector) {
    var _self = this;
    // 样式选择器
    var $widgets = $(cssSelector);
    for (var i = 0; i < $widgets.length; i++) {
      var tmpWidgetId = $($widgets[i]).attr('id');
      var tmpWidget = _self.getWidgetById(tmpWidgetId);
      if (tmpWidget != null) {
        return tmpWidget;
      }
    }
    return null;
  };
  // 4、通过组件ID删除对应的组件
  AppContext.prototype.removeWidgetById = function (widgetId, removeChildren) {
    var _self = this;
    var map = _self.widgetMap;
    var widget = map[widgetId];
    if (widget != null) {
      // 删除子组件
      if (removeChildren === true) {
        _self.removeChildrenWidgetById(widgetId, removeChildren);
      }
      // 删除处理
      _self._removeWidgetById(widgetId);
    }
  };
  // 4.1、通过组件ID删除对应的组件处理
  AppContext.prototype._removeWidgetById = function (widgetId) {
    var _self = this;
    var map = _self.widgetMap;
    var widget = map[widgetId];
    if (widget) {
      // 解除事件监听
      widget.off();
      widget.destroy();
    }
    delete map[widgetId];
  };
  // 5、通过组件ID删除对应的子组件
  AppContext.prototype.removeChildrenWidgetById = function (widgetId, removeChildren) {
    var _self = this;
    var parentWidgetId = widgetId;
    if (parentWidgetId.substring(0, 1) !== '#') {
      parentWidgetId = '#' + parentWidgetId;
    }

    var map = _self.widgetMap;
    for (var p in map) {
      var widgetSelector = '#' + p;
      var childWidget = $(widgetSelector)[0];
      var parentWidget = $(parentWidgetId)[0];
      if (parentWidget && childWidget && $.contains(parentWidget, childWidget)) {
        _self.removeWidgetById(p, removeChildren);
      }
    }
    var items,
      parentWidget = _self.widgetMap[parentWidgetId.substring(1)];
    if (parentWidget && (items = parentWidget.options.widgetDefinition.items)) {
      $.each(items, function (idx, item) {
        _self.removeWidgetById(item.id, removeChildren);
      });
    }
  };
  // 6、渲染组件
  AppContext.prototype.renderWidget = function (options) {
    var _self = this;
    var renderTo = options.renderTo;
    var widgetDefId = options.widgetDefId;
    var widgetCloneable = options.widgetCloneable;

    appContext.getWidgetDefinition(widgetDefId, widgetCloneable, function (appWidgetDefinition) {
      widgetDefId = appWidgetDefinition.id;
      var refreshIfExists = options.refreshIfExists;
      var forceRenderIfConflict = options.forceRenderIfConflict;
      var onPrepare = options.onPrepare;
      var params = options.params || {};
      // 组件已存在，刷新组件
      if (refreshIfExists === true && $("div[id='" + widgetDefId + "']").length === 1) {
        var renderWidget = _self.getWidgetById(widgetDefId);
        if (renderWidget != null) {
          appModal.hideMask();
          renderWidget.refresh(params);
          return;
        }
      } else {
        // 移除已存在的组件
        if (options.target !== '_navTab' && options.renderTo.indexOf('wNavTab_') < 0) {
          //目标位置为导航标签时不移除组件
          _self.removeChildrenWidgetById(renderTo, true);
          _self.removeWidgetById(widgetDefId, true);
        }
      }
      var containerDefinition = options.containerDefinition;
      if (containerDefinition == null) {
        containerDefinition = {};
      }
      //html支持事件参数渲染
      var html = appWidgetDefinition.html;
      if (html && !$.isEmptyObject(params)) {
        html = appContext.resolveParams({ resolveHtml: html }, params).resolveHtml;
        if (html == undefined) {
          html = appWidgetDefinition.html;
          console.warn('解析渲染html字符串异常，未解析出html字符串，使用原始html渲染');
        }
      }
      var $renderPlaceholder = _self.getWidgetRenderPlaceholder(renderTo);
      if (options.renderNavTab) {
        appContext.getWidgetByCssSelector(renderTo)._addTab({
          options: options,
          html: html,
          type: 'html'
        });
      } else {
        $renderPlaceholder[0].innerHTML = html;
      }
      // 作为容器被渲染，就不要创建组件了
      delete _self._waitForWidgets[renderTo];
      var wtype = appWidgetDefinition.wtype;
      var requireModules = appWidgetDefinition.requireJavaScriptModules || wtype.split(',');
      _self.require(requireModules, function () {
        var widgetDefinition = JSON.parse(appWidgetDefinition.definitionJson);
        widgetDefinition.renderOptions = options;
        var widgetCount = $("div[id='" + widgetDefinition.id + "']").length;
        if (widgetCount > 1 && forceRenderIfConflict !== true) {
          appModal.alert('组件ID[' + widgetDefinition.id + ']与当前页面元素ID冲突，无法渲染组件!');
        } else {
          // 组件ID冲突时，强制渲染
          if (widgetCount > 1 && forceRenderIfConflict) {
            var newWidgetId = UUID.createUUID();
            $('#' + widgetDefId, $renderPlaceholder).attr('id', newWidgetId);
            widgetDefinition.id = newWidgetId;
          }
          if (options.defaultCondition) {
            widgetDefinition.configuration.defaultCondition = options.defaultCondition;
          }
          // 事件参数
          widgetDefinition.params = params;
          widgetDefinition.onPrepare = onPrepare;
          appContext.createWidget(widgetDefinition, containerDefinition, options.callback, options.initiator);
        }
      });
    });
  };
  // 7、获取渲染组件的占位符
  AppContext.prototype.getWidgetRenderPlaceholder = function (widgetId) {
    var renderWidgetId = widgetId;
    if (renderWidgetId.substring(0, 1) === '#') {
      renderWidgetId = renderWidgetId.substring(1);
    }
    var renderWidget = this.getWidgetById(renderWidgetId);
    if (renderWidget != null) {
      return $(renderWidget.getRenderPlaceholder());
    }
    //防止widgetMap无当前组件时增加判断是否面板，是的话返回panel-body
    if (renderWidgetId.indexOf('wPanel_') > -1) {
      return $('#' + renderWidgetId).children('.panel-body');
    } else {
      return $('#' + renderWidgetId);
    }
  };
  // 8、设置页面容器
  AppContext.prototype.setPageContainer = function (container) {
    this.pageContainer = container;
  };
  // 9、设置页面容器
  AppContext.prototype.getPageContainer = function () {
    return this.pageContainer;
  };
  // 10、根据组件ID获取组件的选择器
  AppContext.prototype.getWidgetSelector = function (widgetId) {
    var widget = this.getWidgetById(widgetId);
    if (widget != null) {
      return widgetId.indexOf('#') == 0 ? widgetId : '#' + widgetId;
    }
    return widgetId;
  };
  // 11、刷新页面所有组件
  AppContext.prototype.refreshAllWidgets = function () {
    var _self = this;
    var map = _self.widgetMap;
    for (var p in map) {
      var widget = map[p];
      if (widget != null) {
        widget.refresh();
      }
    }
  };

  // 11、刷新页面指定组件ID
  AppContext.prototype.refreshWidgetById = function (widgetId, refreshInnerFrames) {
    var _self = this;
    if (_self.widgetMap[widgetId]) {
      _self.widgetMap[widgetId].refresh();
      return true;
    }

    if (refreshInnerFrames) {
      $.each($('iframe'), function (idx, iframe) {
        if (iframe && iframe.contentWindow && iframe.contentWindow.appContext) {
          // 刷新 `iframe` 里的组件
          if (iframe.contentWindow.appContext.refreshWidgetById(widgetId, false)) {
            return;
          }

          // 如果 `iframe` 里的 `widgetMap` 没有找到组件，则刷新组件ID所在 `iframe` 的所有组件。
          var $iframe = $(iframe);
          if ($iframe.contents().find('#' + widgetId).length > 0) {
            iframe.contentWindow.appContext.refreshAllWidgets();
          }
        }
      });
    }
  };

  // 11、刷新页面指定组件标题
  AppContext.prototype.refreshWidgetByTitle = function (title) {
    var _self = this;
    var map = _self.widgetMap;
    for (var p in map) {
      var widget = map[p];
      if (widget && widget.options.widgetDefinition.title === title) {
        widget.refresh();
      }
    }
  };

  // 12、刷新页面指定类型组件
  AppContext.prototype.refreshWidgetByWtype = function (wWidgetType) {
    var _self = this;
    var map = _self.widgetMap;
    for (var p in map) {
      var widget = map[p];
      if (widget && p.indexOf(wWidgetType) == 0) {
        widget.refresh();
      }
    }
  };

  // 13、判断组件是否存在于应用上下文
  AppContext.prototype.isWidgetExists = function (widget) {
    if (widget == null || !$.isFunction(widget.getId)) {
      return false;
    }
    return this.getWidgetById(widget.getId()) != null;
  };
  AppContext.prototype.isWidgetExistsById = function (widgetId) {
    return this.getWidgetById(widgetId) != null;
  };

  // 应用派发器
  // 1、注册
  AppContext.prototype.registerAppDispatcher = function (callback) {
    this.dispatcher.register(callback);
  };
  // 2、启动应用
  AppContext.prototype.startApp = function (ui, options) {
    // var widgets = this.widgets;
    // console.log("widget count: " + widgets.length);
    // $.each(widgets, function(index, widget) {
    // if (widget != null) {
    // console.log(widget.getId());
    // } else {
    // console.log("widget " + index + " is " + widget);
    // }
    // });
    // console.log(ui);
    var _self = this;
    if (arguments.length === 1) {
      _self.dispatcher.dispatch(ui, appOnDispatchingCallbacks());
    } else {
      options.ui = ui;
      options.appContext = _self;
      // 原始参数
      if (options.rawParams == null) {
        options.rawParams = options.params;
      }
      var params = $.extend(true, {}, options.params, options.rawParams);
      // 解析后参数
      options.params = appContext.resolveParams(params, options);
      _self.dispatcher.dispatch(options, appOnDispatchingCallbacks());
    }
  };
  // 2.2，应用开始派发
  var appOnDispatchingCallbacks = function () {
    return {
      onStart: function (options) {
        // if(StringUtils.isBlank(options.appType)) {
        // 		return;
        // }
        // console.log("onStart, pageContainerCreationComplete: " + (appContext.pageContainerCreationComplete == true));
      },
      onStop: function (options) {
        if (StringUtils.isBlank(options.appType)) {
          return;
        }
        // 页面容器未创建完成、ui组件为空、执行JS模块、目标组件ID为空、不更新hash
        if (
          appContext.pageContainerCreationComplete !== true ||
          !options.ui ||
          !(options.selection || options.menuId) ||
          options.isJsModule ||
          options.target != constant.TARGET_POSITION.TARGET_WIDGET ||
          StringUtils.isBlank(options.targetWidgetId)
        ) {
          return;
        }

        // console.log("onStop, pageContainerCreationComplete: " + (appContext.pageContainerCreationComplete == true));
        var widgetId = options.ui.getId();
        var widget = appContext.getWidgetById(widgetId);
        if (widget != null) {
          appContext.updateCurrentHash(options);
        } else {
          (function () {
            $(document).one('WidgetCreated', '#' + options.ui.getId(), function (e, ui) {
              appContext.updateCurrentHash(options);
            });
          })();
        }
      }
    };
  };
  // 3、根据产品集成信息UUID启动应用
  AppContext.prototype.startAppByPiUuid = function (piUuid, options) {
    var _self = this;
    var appOptions = options || {};
    var piItem = _self.getPiItem(piUuid);
    appOptions.appType = piItem.type;
    appOptions.appPath = piItem.path;
    appOptions.appId = piItem.uuid;
    appOptions.appContext = _self;
    if (options.ui) {
      _self.startApp(options.ui, appOptions);
    } else {
      _self.startApp(appOptions);
    }
  };
  // 4、获取应用派发器, create为true创建一个新的派发器，否则返回默认的应用派发器
  AppContext.prototype.getDispatcher = function (create) {
    if (create === true) {
      return new Dispatcher();
    }
    return this.dispatcher;
  };
  // 5、解析参数
  AppContext.prototype.resolveParams = function (params, data, setting) {
    if (params == null) {
      return {};
    }
    var templateEngine = this.getJavaScriptTemplateEngine();
    for (var p in params) {
      params[p] = templateEngine.render(params[p], data, setting);
    }
    return params;
  };

  // 5、解析参数(为避免与freemarker冲突)
  AppContext.prototype.resolveParamsNoConflics = function (params, data) {
    if (params == null) {
      return {};
    }
    var templateEngine = this.getJavaScriptTemplateEngine();
    for (var p in params) {
      params[p] = templateEngine.render(params[p], data, {
        'tag::operationOpen': '{@',
        'tag::operationClose': '}',
        'tag::interpolateOpen': '&{',
        'tag::interpolateClose': '}',
        'tag::noneencodeOpen': '&&{',
        'tag::noneencodeClose': '}',
        'tag::commentOpen': '{#',
        'tag::commentClose': '}'
      });
    }
    return params;
  };

  // 组件自定义事件
  // 1、发布事件
  AppContext.prototype.dispatchEvent = function (type, eventData, ui) {
    var self = this,
      _dispatchEvent;
    if (self.isMobileApp()) {
      _dispatchEvent = function (type, eventData, ui) {
        if (ui) {
          $.trigger(ui.element[0] || ui.element, type, [eventData, ui]);
        } else {
          console.log("widget is null, cann't trigger event [" + type + ']');
        }
      };
    } else {
      _dispatchEvent = function (type, eventData, ui) {
        var e = new jQuery.Event(type, true, true);
        e.detail = eventData || {};
        $(ui.element).trigger(e, ui);
      };
    }

    // 判断是否需要等待
    this._waitForCreateWidgets(_dispatchEvent, type, eventData, ui);
  };
  // 2、添加事件监听
  AppContext.prototype.addEventListener = function (type, selector, data, eventHandle, ui) {
    var self = this;
    var widgetSelector = self.getWidgetSelector(selector);
    if (self.isMobileApp()) {
      var element = ui.element[0] || ui.element;
      eventHandle = $.isFunction(data) ? data : eventHandle;
      eventHandle = $.isFunction(selector) ? selector : eventHandle;
      element.addEventListener(type, eventHandle);
    } else {
      $(ui.element).on(type, widgetSelector, data, eventHandle);
    }
  };
  // 3、删除事件监听
  AppContext.prototype.removeEventListener = function (type, selector, fn, ui) {
    var widgetSelector = this.getWidgetSelector(selector);
    $(ui.element).off(type, widgetSelector, fn);
  };

  // 窗口操作
  // 1、打开新窗口
  AppContext.prototype.openWindow = function (options) {
    this._windowManager.open(options);
  };
  // 2、新窗口关闭，父窗口接收结果处理
  AppContext.prototype.onWindowResult = function (options) {
    this._windowManager.onResult(options);
  };
  // 3、设置窗口管理器
  AppContext.prototype.setWindowManager = function (appWindowManager) {
    this._windowManager = appWindowManager;
  };
  // 4、获取窗口管理器
  AppContext.prototype.getWindowManager = function () {
    return this._windowManager;
  };

  // JS模板引擎
  var JavaScriptTemplateEngine = function () {
    // 扩展参数到location对象上
    try {
      location.query = Browser.getAllQueryString();
    } catch (e) {}
  };
  // 1、根据模板及数据进行渲染，返回渲染结果
  JavaScriptTemplateEngine.prototype.render = function (template, data, setting) {
    if (setting) {
      templateEngine.set(setting);
    }
    var result = templateEngine(template, data);
    // 还原设置
    templateEngine.set({
      'tag::operationOpen': '{@',
      'tag::operationClose': '}',
      'tag::interpolateOpen': '${',
      'tag::interpolateClose': '}',
      'tag::noneencodeOpen': '$${',
      'tag::noneencodeClose': '}',
      'tag::commentOpen': '{#',
      'tag::commentClose': '}'
    });
    return result;
  };
  // 2、根据模板ID及数据进行渲染，返回渲染结果
  JavaScriptTemplateEngine.prototype.renderById = function (templateId, data) {
    return this.render(this.getTemplate(templateId), data);
  };
  // 3、根据模板ID返回模板
  JavaScriptTemplateEngine.prototype.getTemplate = function (templateId) {
    var template = appContext.getJavaScriptTemplateById(templateId);
    if (template != null) {
      return template.content;
    }
    return null;
  };
  // 4、返回模板引擎
  AppContext.prototype.getJavaScriptTemplateEngine = function () {
    if (this.jsTemplateEngine == null) {
      this.jsTemplateEngine = new JavaScriptTemplateEngine();
    }
    return this.jsTemplateEngine;
  };
  // 5、设置JS模板
  AppContext.prototype.setJavaScriptTemplates = function (jsTemplates) {
    this.jsTemplates = jsTemplates;
  };
  // 6、根据模板ID，获取JS模板
  AppContext.prototype.getJavaScriptTemplateById = function (templateId) {
    if (this.jsTemplates[templateId] != null) {
      return this.jsTemplates[templateId];
    }
    return this._loadJavaScriptTemplateById(templateId);
  };
  // 6.1、从服务器加载JS模板
  AppContext.prototype._loadJavaScriptTemplateById = function (templateId) {
    var self = this;
    if (self.jsTemplates[templateId] != null) {
      return this.jsTemplates[templateId];
    } else {
      JDS.call({
        service: 'appContextService.getJavaScriptTemplateById',
        data: [templateId],
        async: false,
        success: function (result) {
          var template = result.data;
          if (template == null) {
            console.log('ID为[' + templateId + ']的JS模板不存在!');
            appModal.alert('ID为[' + templateId + ']的JS模板不存在!');
          } else if (template != null) {
            self.jsTemplates[templateId] = template;
          } else {
            console.error('JS模板请求返回数据异常: ' + JSON.stringify(template));
            appModal.alert('JS模板请求返回数据异常: ' + JSON.stringify(template));
          }
        },
        error: function (jqXHR, statusText, error) {
          getDefaultErrorHandler(function () {
            console.error('JS模板请求异常');
            appModal.alert('JS模板请求异常，请确认存在ID为[' + templateId + ']的JS模板!');
          }).handle(jqXHR, statusText, error);
        }
      });
    }
    return self.jsTemplates[templateId];
  };

  // 7、浏览器hash处理
  var AppHash = function (hash) {
    this.updateCounter = 1;
    this.hashInfos = [];
    this.init(hash);
  };
  // 初始化浏览器hash
  AppHash.prototype.init = function (hash) {
    var _self = this;
    _self.reset(hash);
    if (window.location.hash != _self.hash) {
      _self._updateByHash(_self.hash);
    }
  };
  // 重置锚点信息
  AppHash.prototype.reset = function (hash) {
    var _self = this;
    _self.hash = hash;
    _self.hashInfos = [];
    if (StringUtils.isNotBlank(hash)) {
      var hashParts = hash.split(',');
      for (var i = 0; i < hashParts.length; i++) {
        _self.hashInfos.push(appContext.parseHashInfo(hashParts[i]));
      }
    }
  };
  // 根据锚点hash更新浏览器锚点
  AppHash.prototype._updateByHash = function (hash) {
    var _self = this;
    var timeout = _self.updateCounter * 200;
    _self.updateCounter++;
    // console.log("updateCounter: " + _self.updateCounter);
    // console.log("hash: " + _self.hash);
    appContext.disableHashPopStateListener();
    setTimeout(function () {
      _self.updateCounter--;
      if (_self.updateCounter <= 1) {
        // console.log("hash: " + _self.hash);
        // console.log("update window.location.hash: " + _self.hash);
        if (window.location.hash != _self.hash) {
          window.location.hash = _self.hash;
          // 重置更新锚点
          _self.reset(_self.hash);
        }
        appContext.enableHashPopStateListener();
      }
    }, timeout);
  };
  // 更新浏览器hash
  AppHash.prototype.update = function () {
    var _self = this;
    var hashInfos = _self.getHashInfos();
    var newHash = '#' + appContext.hashInfoToString(hashInfos, true);
    if (newHash != _self.hash) {
      _self.hash = newHash;
      _self._updateByHash(_self.hash);
    }
  };
  // 获取锚点信息
  AppHash.prototype.getHashInfos = function () {
    return this.hashInfos || [];
  };
  // 合并锚点信息
  AppHash.prototype._mergeHashInfo = function (hashInfo, newHashInfo) {
    hashInfo.selection = newHashInfo.selection;
    hashInfo.subHash = newHashInfo.subHash;
  };
  // 更新子锚点信息
  AppHash.prototype._updateSubHash = function (hashInfo, newHashInfo) {
    hashInfo.subHash = newHashInfo;
  };
  // 获取同一组件的锚点信息
  AppHash.prototype._getTheSameWidgetHashInfo = function (hashInfos, newHashInfo) {
    var _self = this;
    // 是否同一组件
    var theSameHashInfo = null;
    var isSameWidgetHash = false;
    for (var i = 0; i < hashInfos.length; i++) {
      var hashInfo = hashInfos[i];
      if (hashInfo.widgetId == newHashInfo.widgetId) {
        theSameHashInfo = hashInfo;
        isSameWidgetHash = true;
        break;
      } else if (hashInfo.subHash) {
        // 遍历子锚点信息
        var sameWidgetHash = _self._getTheSameWidgetHashInfo([hashInfo.subHash], newHashInfo);
        if (sameWidgetHash.isSameWidgetHash) {
          return sameWidgetHash;
        }
      }
    }
    return {
      theSameHashInfo: theSameHashInfo,
      isSameWidgetHash: isSameWidgetHash
    };
  };
  // 获取组件发起者锚点信息
  AppHash.prototype._getWidgetInitiatorHash = function (hashInfos, newHashInfo) {
    var _self = this;
    // 获取组件发起者ID
    var newWidgetId = newHashInfo.widgetId;
    var newWidget = appContext.getWidgetById(newWidgetId);
    if (newWidget == null) {
      return null;
    }
    var initiator = newWidget.getInitiator();
    var initiatorWidgetId = initiator && initiator.id ? initiator.id : null;
    if (initiatorWidgetId == null) {
      return null;
    }
    // 获取组件发起者对应的锚点信息
    return _self._getHashInfoByWidgetId(hashInfos, initiatorWidgetId);
  };
  // 根据组件ID获取锚点信息
  AppHash.prototype._getHashInfoByWidgetId = function (hashInfos, widgetId) {
    var _self = this;
    for (var i = 0; i < hashInfos.length; i++) {
      if (hashInfos[i].widgetId == widgetId) {
        return hashInfos[i];
      } else if (hashInfos[i].subHash) {
        var hashInfo = _self._getHashInfoByWidgetId([hashInfos[i].subHash], widgetId);
        if (hashInfo != null) {
          return hashInfo;
        }
      }
    }
    return null;
  };
  // 根据组件ID判断跟锚点信息是否在同一页面
  AppHash.prototype._isWidgetsInSamePage = function (hashInfos, widgetId) {
    if ($("div[id='" + widgetId + "']").length <= 0) {
      return false;
    }
    for (var i = 0; i < hashInfos.length; i++) {
      if ($("div[id='" + hashInfos[i].widgetId + "']").length > 0) {
        return true;
      }
    }
    return false;
  };
  // 判断widgetId是否为newWidgetId对应组件的组件发人者
  AppHash.prototype._isWidgetInitiator = function (widgetId, newWidgetId) {
    var newWidget = appContext.getWidgetById(newWidgetId);
    if (newWidget == null) {
      return false;
    }
    var initiator = newWidget.getInitiator();
    if (initiator && initiator.id == widgetId) {
      return true;
    }
    return false;
  };
  // 判断锚点是否为空
  AppHash.prototype.isEmpty = function () {
    return this.hashInfos.length == 0;
  };
  // 根据组件锚点更新应用锚点
  AppHash.prototype.updateByWidgetSelection = function (widgetId, selection) {
    var _self = this;
    // 初始化锚点
    if (_self.isEmpty()) {
      var initHash = '#/' + widgetId + '/' + selection;
      _self.init(initHash);
    } else {
      // 附加或更新锚点
      var newHash = '/' + widgetId + '/' + selection;
      _self.updateByNewHash(newHash);
    }
  };
  // 根据新锚点信息更新锚点信息
  AppHash.prototype.updateByNewHash = function (newHash) {
    var _self = this;
    var isNewHashInfo = false;
    var isSubHashInfo = false;
    var hashInfos = _self.getHashInfos();
    var newHashInfo = appContext.parseHashInfo(newHash);
    var sameWidgetHash = _self._getTheSameWidgetHashInfo(hashInfos, newHashInfo);
    // 同一组件，更新
    if (sameWidgetHash.isSameWidgetHash) {
      _self._mergeHashInfo(sameWidgetHash.theSameHashInfo, newHashInfo);
    } else if (_self._isWidgetsInSamePage(hashInfos, newHashInfo.widgetId)) {
      // 同一页面，获取组件发起者
      var initiatorHash = _self._getWidgetInitiatorHash(hashInfos, newHashInfo);
      if (initiatorHash) {
        _self._updateSubHash(initiatorHash, newHashInfo);
        isSubHashInfo = true;
      }
      if (!isSubHashInfo) {
        isNewHashInfo = true;
      }
    } else {
      isNewHashInfo = true;
    }
    if (isNewHashInfo) {
      hashInfos.push(newHashInfo);
    }
    // 更新
    _self.update();
  };
  // 根据组件锚点删除应用锚点
  AppHash.prototype.removeByWidgetSelection = function (widgetId, selection) {
    var _self = this;
    if (_self.isEmpty()) {
      return;
    }
    var hashInfos = _self.getHashInfos();
    // 删除对应锚点信息
    var arrays = ArrayUtils.removeElement(hashInfos, { widgetId: widgetId, selection: selection }, function (element, hashInfo) {
      return element.widgetId == hashInfo.widgetId && element.selection.join(',') == hashInfo.selection;
    });
    // 清空锚点信息
    hashInfos.length = 0;
    for (var i = 0; i < arrays; i++) {
      hashInfos.push(arrays[i]);
    }
    // 更新
    _self.update();
  };
  // 更新当前应用锚点——{ui:"组件", selection:"选择的内容——菜单id,子菜单id"}
  AppContext.prototype.updateCurrentHash = function (options) {
    var _self = this;
    if (appContext.pageContainerCreationComplete !== true) {
      return;
    }
    var widgetId = options.ui.getId();
    var selection = options.selection || options.menuId || '';
    var appHash = _self.parseCurrentHash();
    appHash.updateByWidgetSelection(widgetId, selection);
  };
  // 从当前锚点中删除指定锚点——{ui:"组件", selection:"选择的内容——菜单id,子菜单id"}
  AppContext.prototype.removeFromCurrentHash = function (options) {
    var _self = this;
    if (appContext.pageContainerCreationComplete !== true) {
      return;
    }
    var widgetId = options.ui.getId();
    var selection = options.selection || options.menuId || '';
    var appHash = _self.parseCurrentHash();
    appHash.removeByWidgetSelection(widgetId, selection);
  };
  // 解析当前锚点
  AppContext.prototype.parseCurrentHash = function (hash) {
    if (hash) {
      appContext.appHash = new AppHash(decodeURIComponent(hash));
    } else if (!appContext.appHash) {
      appContext.appHash = new AppHash(decodeURIComponent(window.location.hash));
    }
    return appContext.appHash;
  };
  // 锚点字符串解析成锚点对象
  AppContext.prototype.parseHashInfo = function (hash) {
    var _self = this;
    var isFirst = false;
    var widgetHash = hash;
    // 是否第一个锚点
    if (hash.startsWith('#')) {
      isFirst = true;
      widgetHash = hash.substring(1);
    }
    // 子层次的组件选择信息
    var subHash = null;
    var subPartIndex = widgetHash.indexOf(':');
    if (subPartIndex != -1) {
      // 解析子锚点信息
      subHash = _self.parseHashInfo(widgetHash.substring(subPartIndex + 1));
      widgetHash = widgetHash.substring(0, subPartIndex);
    }
    // 去掉开始的第一个"/"
    if (widgetHash.startsWith('/')) {
      widgetHash = widgetHash.substring(1);
    }
    var widgetHashes = widgetHash.split('/');
    var widgetId = widgetHashes.shift();
    var selection = widgetHashes;
    return {
      isFirst: isFirst,
      widgetId: widgetId,
      selection: selection,
      subHash: subHash
    };
  };
  // 解析事件处理锚点参数
  AppContext.prototype.parseEventHashParams = function (eventHashInfo, selectionKey, hashKey) {
    var params = {};
    if (eventHashInfo == null) {
      return params;
    }
    var eventHashType = eventHashInfo.eventHashType || eventHashInfo.hashType;
    var eventHash = eventHashInfo.eventHash || eventHashInfo.hash;
    if (StringUtils.isBlank(eventHashType) || StringUtils.isBlank(eventHash)) {
      return params;
    }
    var hashInfos = eventHash.split(',');
    var hashSelection = [];
    for (var i = 0; i < hashInfos.length; i++) {
      var hashInfo = hashInfos[i];
      var hashInfo = appContext.parseHashInfo(hashInfo);
      hashSelection = hashSelection.concat(hashInfo.selection);
    }
    params[selectionKey || 'selection'] = hashSelection.join(',');
    params[hashKey || 'hash'] = eventHash;
    return params;
  };
  // 锚点对象转为字符串
  AppContext.prototype.hashInfoToString = function (hashInfos, requiredWidgetExists) {
    var _self = this;
    var hashStrs = [];
    var infos = hashInfos;
    if (!$.isArray(hashInfos)) {
      infos = [hashInfos];
    }
    $.each(infos, function (i, hashInfo) {
      if (requiredWidgetExists === true && !appContext.isWidgetExistsById(hashInfo.widgetId)) {
        return;
      }
      var sb = new StringBuilder();
      sb.append('/');
      sb.append(hashInfo.widgetId);
      if (hashInfo.selection && hashInfo.selection.length > 0) {
        sb.append('/');
        sb.append(hashInfo.selection.join('/'));
      }
      if (hashInfo.subHash) {
        sb.append(':');
        sb.append(_self.hashInfoToString([hashInfo.subHash], requiredWidgetExists));
      }
      hashStrs.push(sb.toString());
    });
    return hashStrs.join(',');
  };
  // 根据锚点对象获取叶子锚点
  AppContext.prototype.getWidgetLeafHashInfo = function (hashInfo) {
    var leafHash = hashInfo;
    while (leafHash.subHash && appContext.isWidgetExistsById(leafHash.subHash.widgetId)) {
      leafHash = leafHash.subHash;
    }
    return leafHash;
  };
  // 禁用浏览器地址变更事件监听
  AppContext.prototype.disableHashPopStateListener = function () {
    $(window).off('popstate');
  };
  // 启用浏览器地址变更事件监听
  AppContext.prototype.enableHashPopStateListener = function () {
    $(window).on('popstate', function (e) {
      if (!appContext.pageContainerCreationComplete || appContext.dispatcher.isDispatching()) {
        return;
      }
      if (e.target && e.target.location && StringUtils.isBlank(e.target.location.hash)) {
        //window.location.href = window.location.href;
        return;
      }
      var appHash = appContext.parseCurrentHash(e.target.location.hash);
      if (appHash.isEmpty()) {
        return;
      }
      var hasWidgetSelection = false;
      var hashInfos = appHash.getHashInfos();
      $.each(hashInfos, function (i, hashInfo) {
        // 获取组件存在的叶子锚点
        var leafHash = appContext.getWidgetLeafHashInfo(hashInfo);
        var widget = appContext.getWidgetById(leafHash.widgetId);
        if (widget) {
          var selectionOptions = $.extend(true, leafHash, {});
          widget.trigger(constant.WIDGET_EVENT.HashSelection, selectionOptions);
          widget.onHashSelection(selectionOptions);
          hasWidgetSelection = true;
        }
      });
      // 不存在组件选择信息刷新页面
      if (!hasWidgetSelection) {
        window.location.reload();
      }
    });
  };
  // 页面容器创建完成时绑定当前
  AppContext.prototype.bindCurrentHashWhenPageContainerCreationComplete = function () {
    appContext.disableHashPopStateListener();
    appContext.enableHashPopStateListener();
    // 监听页面创建完成事件，只需监听一次
    $(window.document).one &&
      $(window.document).one(constant.WIDGET_EVENT.PageContainerCreationComplete, function () {
        appContext.pageContainerCreationComplete = true;
        if (appContext.dispatcher.isDispatching()) {
          return;
        }
        var appHash = appContext.parseCurrentHash();
        if (appHash.isEmpty()) {
          // 登录跳转的hash
          var loginRedirectHash = null;
          if (localStorage) {
            loginRedirectHash = localStorage.getItem('loginRedirectHash');
            localStorage.removeItem('loginRedirectHash');
          }
          if (StringUtils.isNotBlank(loginRedirectHash)) {
            appHash = appContext.parseCurrentHash(loginRedirectHash);
          }
          if (appHash.isEmpty()) {
            return;
          }
        }
        var hashInfos = appHash.getHashInfos();
        $.each(hashInfos, function (i, hashInfo) {
          var widget = appContext.getWidgetById(hashInfo.widgetId);
          if (widget) {
            var selectionOptions = $.extend(true, hashInfo, {});
            widget.trigger(constant.WIDGET_EVENT.HashSelection, selectionOptions);
            widget.onHashSelection(selectionOptions);
          }
        });
      });
  };

  // 8、应用上下文临时数据存储
  // 8.1、设置应用上下文的临时数据
  AppContext.prototype.setItem = function (key, value) {
    this._storages[key] = value;
  };
  // 8.2、获取应用上下文的临时数据
  AppContext.prototype.getItem = function (key) {
    return this._storages[key];
  };

  // 1、设置环境变量
  AppContext.prototype.setEnvironment = function (environment) {
    this.environment = environment;
  };
  // 2、获取环境变量
  AppContext.prototype.getEnvironment = function () {
    return this.environment;
  };
  // 3、是否手机APP
  AppContext.prototype.isMobileApp = function () {
    return this.environment.isMobileApp === true;
  };

  /**
   * 执行匿名函数代码
   * @param scriptCode 脚本代码
   * @param $this this对象
   * @param params 参数值对{key:value},key变量可提供脚本代码内使用
   * @param callback 执行匿名函数后的回调函数
   * @return 返回匿名函数体
   */
  AppContext.prototype.eval = function (scriptCode, $this, params, callback) {
    var paramNames = []; //参数键
    var paramValues = []; //参数值
    var _this = $this ? $this : this;
    if (params) {
      for (var k in params) {
        paramNames.push(k);
        paramValues.push(params[k]);
      }
    }
    var anonymousFunction = new Function(paramNames.join(','), scriptCode);
    var rt = anonymousFunction.apply(_this, paramValues);
    if ($.isFunction(callback)) {
      //处理执行结果
      callback(rt);
    }
    return anonymousFunction;
  };

  AppContext.prototype.getNavTabWidget = function () {
    var self = this;
    var widgetMap = self.widgetMap;
    var hasNavTab = false;
    var navTabWidget = null;
    $.each(widgetMap, function (i) {
      if (i.indexOf('wNavTab_') > -1) {
        hasNavTab = true;
        navTabWidget = widgetMap[i];
        return false;
      }
    });
    if (hasNavTab) {
      console.log(navTabWidget);
      return navTabWidget;
    }
    return self.getNavTabWidget.apply(parent.appContext);
  };

  // 初始化应用上下文
  var appContext = new AppContext();
  // 页面创建完成时绑定锚点
  appContext.bindCurrentHashWhenPageContainerCreationComplete();
  window.appContext = appContext;

  return appContext;
});
