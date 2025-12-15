define(['jquery', 'constant', 'commons', 'appContext'], function ($, constant, commons, appContext) {
  var UrlUtils = commons.UrlUtils;
  var StringUtils = commons.StringUtils;
  var StorageUtils = commons.StorageUtils;
  var Browser = commons.Browser;
  // window.open方法中，ie8仅支持的name参数
  var ie8SupportTarges = {
    _blank: '_blank',
    _media: '_media',
    _parent: '_parent',
    _search: '_search',
    _self: '_self',
    _top: '_top'
  };
  // 1、应用窗口管理器,1、浏览器窗口，2、模态窗口，3、手机窗口
  var AppWindowManager = function (appContext) {
    this.appContext = appContext;
    this.window = window;
    this.jQuery = $ || window.$ || window.jQuery;
    this._resultHandlers = {};
  };
  // 1.1、打开窗口
  AppWindowManager.prototype.open = function (options) {
    var _self = this;
    if (typeof options === 'string') {
      _self.window.open.apply(this.window, arguments);
    } else if (typeof options === 'object') {
      var storage = StorageUtils.getStorage();
      if (!options.ui) {
        options.ui = appContext.getPageContainer();
      }
      var newUrl = options.url;
      if ($.isPlainObject(options.urlParams)) {
        newUrl = UrlUtils.appendUrlParams(newUrl, options.urlParams);
      }
      if (options.useRequestCode !== false) {
        var requestCode = options.requestCode || new Date().getTime();
        if (options.ui) {
          storage.setItem(requestCode, options.ui.getId());
        }
        newUrl = UrlUtils.appendUrlParams(newUrl, {
          _requestCode: requestCode
        });
      }
      // 生成窗口名称
      var name = options.target || '_blank';
      var useUniqueName = options.useUniqueName;
      if (useUniqueName !== false && name === '_blank') {
        name = options.url;
      }
      // tabs模式打开tab
      if (_self.window.top && _self.window.top.navTabs) {
        var navTabs = _self.window.top.navTabs;
        var tabid = options.id;
        navTabs.removeTabItem(tabid);
        navTabs.addTabItem({
          tabid: tabid,
          text: options.text,
          url: newUrl,
          icon: options.icon
        });
      } else {
        if (options.locationReplace === true) {
          // 手机头部导航
          location.replace(newUrl);
        } else if (Browser.isIE8OrLower()) {
          if (ie8SupportTarges[name]) {
            _self.window.open(newUrl, name);
          } else {
            if (options.useUniqueName) {
              _self.window.open(newUrl, '_self');
            } else {
              _self.window.open(newUrl, '_blank');
            }
          }
        } else if (Browser.isIEOfVersion(9)) {
          if (ie8SupportTarges[name]) {
            _self.window.open(newUrl, name);
          } else {
            // IE9处理，name参数不能为特殊字符
            appContext.require(['md5'], function () {
              var targetName = md5().fromUTF8(name);
              _self.window.open(newUrl, targetName);
            });
          }
        } else {
          _self.window.open(newUrl, name);
        }
      }
    }
  };
  // 1.2、窗口结果处理
  AppWindowManager.prototype.onResult = function (options) {
    var resultCode = options.resultCode;
    this._resultHandlers[resultCode].call(this, options);
  };
  // 1.3、窗口结果处理器注册
  AppWindowManager.prototype.registerWindowResultHandler = function (resultCode, callback) {
    if (this._resultHandlers[resultCode] != null) {
      alert('结果代码为[' + resultCode + ']已经存在，不能再次注册!');
      return;
    }
    this._resultHandlers[resultCode] = callback;
  };
  // 1.4、刷新父窗口
  AppWindowManager.prototype.refreshParent = function (options) {
    // tabs模式刷新tab
    var _self = this;
    if (_self.window.top && _self.window.top.navTabs) {
      var navTabs = _self.window.top.navTabs;
      var parentId = options.parentId;
      // 多个父窗口
      var ids = parentId;
      var jq = _self.jQuery;
      if (!jq.isArray(ids)) {
        ids = parentId.split(';');
      }
      for (var i = 0; i < ids.length; i++) {
        // 刷新iframe
        var iframe = window.parent[ids[i]];
        if (iframe && iframe.appContext && iframe.appContext) {
          iframe.appContext.getPageContainer().refresh(true);
        } else {
          navTabs.reload(ids[i]);
        }
        // 选中tab
        navTabs.selectTabItem(ids[0]);
      }
    } else {
      try {
        if (window.opener && window.opener.appContext) {
          var requestCode = Browser.getQueryString('_requestCode');
          var resultOptions = {
            resultCode: 2,
            requestCode: requestCode
          };
          window.opener.appContext.onWindowResult(resultOptions);
        }
      } catch (e) {
        console.error(e);
      }
    }
  };

  AppWindowManager.prototype.triggerParentWidgetEvent = function (widgetId, event, options) {
    try {
      if (window.opener && window.opener.appContext && window.opener.appContext.widgetMap[widgetId]) {
        window.opener.appContext.widgetMap[widgetId].trigger(event, options);
      } else if (window.parent && window.parent.appContext && window.parent.appContext.widgetMap[widgetId]) {
        window.parent.appContext.widgetMap[widgetId].trigger(event, options);
      }
    } catch (e) {
      console.error(e);
    }
  };

  // 1.5、刷新窗口
  AppWindowManager.prototype.refresh = function (url) {
    if (url && url.ui && $.isFunction(url.ui.refresh)) {
      url.ui.refresh(true);
    } else if (StringUtils.isNotBlank(url)) {
      var requestCode = Browser.getQueryString('_requestCode');
      if (StringUtils.isNotBlank(requestCode) && !StringUtils.contains(url, '_requestCode')) {
        var newUrl = UrlUtils.appendUrlParams(url, {
          _requestCode: requestCode
        });
        this.window.location = newUrl;
      } else {
        this.window.location = url;
      }
    } else {
      this.window.location.reload();
    }
  };
  // 1.6、关闭当前窗口并刷新父窗口
  AppWindowManager.prototype.closeAndRefreshParent = function () {
    var _self = this;
    var timeout = 100;
    var args = [];
    args.push(timeout);
    for (var i = 0; i < arguments.length; i++) {
      args.push(arguments[i]);
    }
    _self.closeLater.apply(_self, args);
    _self.refreshParent.apply(_self, arguments);
  };
  // 1.7、关闭当前窗口
  AppWindowManager.prototype.close = function (options) {
    var _self = this;
    // tabs模式关闭tab
    if (options && _self.window.top && _self.window.top.navTabs) {
      var navTabs = _self.window.top.navTabs;
      // 多个窗口
      var ids = options.id;
      if (!$.isArray(ids)) {
        ids = ids.split(';');
      }
      for (var i = 0; i < ids.length; i++) {
        navTabs.removeTabItem(ids[i]);
      }
    } else {
      _self.window.close();
      _self.window.location.replace('about:blank'); //fix: chrome 无法关闭
    }
  };
  // 1.8、延时关闭当前窗口
  AppWindowManager.prototype.closeLater = function (timeout, options) {
    var _self = this;
    setTimeout(function () {
      _self.close.call(_self, options);
    }, timeout);
  };

  // 2、窗口结果处理器
  // 2.1 resultCode=-1，不处理
  var _wrNoopHandler = $.noop;
  // 2.2 resultCode==0，关闭窗口，appModal中实现在父窗口刷新组件并调用回调函数，若组件不存在刷新窗口，不执行回调
  var _wrCloseWindowHandler = function (options) {
    this.window.close();
  };
  // 2.3 resultCode==1，刷新窗口
  var _wrRefreshWindowHandler = function (options) {
    this.window.location.reload();
  };
  // 2.4 resultCode==2，刷新组件，若组件不存在刷新窗口
  var _wrRefreshWidgetHandler = function (options) {
    var requestCode = options.requestCode;
    var storage = StorageUtils.getStorage();
    var widgetId = storage.getItem(requestCode);
    var requestWidget = appContext.getWidgetById(widgetId);
    if (requestWidget != null) {
      requestWidget.refresh(true);
      requestWidget.trigger(constant.WIDGET_EVENT.WindowResultRefresh, options);
    } else {
      this.window.location.reload();
    }
  };
  // 2.5 resultCode==3，调用回调函数
  var _wrCallbackHandler = function (options) {
    var callback = options.callback;
    callback.call(this, options);
  };
  // 2.6 resultCode==4，刷新组件并调用回调函数，若组件不存在刷新窗口，不执行回调
  var _wrRefreshCallbackHandler = function (options) {
    var requestCode = options.requestCode;
    var storage = StorageUtils.getStorage();
    var widgetId = storage.getItem(requestCode);
    var requestWidget = appContext.getWidgetById(widgetId);
    if (requestWidget != null) {
      requestWidget.refresh(true);
      requestWidget.trigger(constant.WIDGET_EVENT.WindowResultRefresh, options);
    } else {
      this.window.location.reload();
    }
    var callback = options.callback;
    if ($.isFunction(callback)) {
      callback.call(this, options);
    }
  };
  // 2.7 resultCode==5，派发组件事件
  var _wrDispatchHandler = function (options) {
    var requestCode = options.requestCode;
    var storage = StorageUtils.getStorage();
    var widgetId = storage.getItem(requestCode);
    var type = options.eventType;
    var eventData = options.eventData;
    var requestWidget = appContext.getWidgetById(widgetId);
    appContext.dispatchEvent(type, eventData, requestWidget);
  };

  // 3、子类
  // 3.1、浏览器窗口
  var BrowserWindowManager = function (appContext) {
    AppWindowManager.call(this, appContext);
  };
  commons.inherit(BrowserWindowManager, AppWindowManager);
  // 3.2、模态窗口
  var ModalWindowManager = function (appContext) {
    AppWindowManager.call(this, appContext);
  };
  commons.inherit(ModalWindowManager, AppWindowManager);
  // 3.2.1 重写打开方法
  ModalWindowManager.prototype.open = function (options) {
    var requestCode = new Date().getTime();
    var storage = StorageUtils.getStorage();
    if (!options.ui) {
      options.ui = appContext.getPageContainer();
    }
    storage.setItem(requestCode, options.ui.getId());
    options.requestCode = requestCode;
    options.message = '<iframe src="' + options.url + '" width="100%" height="500px" frameborder="0"></iframe>';
    require(['appModal'], function (appModal) {
      appModal.dialog(options);
      $('.bootbox').draggable();
    });
  };
  // 3.3 手机窗口
  var MobileWindowManager = function (appContext) {
    AppWindowManager.call(this, appContext);
  };
  commons.inherit(MobileWindowManager, AppWindowManager, {
    open: function (options) {
      var _self = this;
      if (typeof options === 'string') {
        if (options.indexOf('http') < 0) {
          options = location.origin + ctx + options;
        }
        location.href = options;
      } else if (typeof options === 'object') {
        var storage = StorageUtils.getStorage();
        if (!options.ui) {
          options.ui = appContext.getPageContainer();
        }
        var newUrl = options.url;
        if (options.useRequestCode !== false) {
          var requestCode = new Date().getTime();
          if (options.ui) {
            storage.setItem(requestCode, options.ui.getId());
          }
          newUrl = UrlUtils.appendUrlParams(newUrl, {
            _requestCode: requestCode
          });
        }
        if (newUrl.indexOf('http') != 0) {
          newUrl = location.origin + ctx + newUrl;
        }
        if (options.locationReplace) {
          // 手机头部导航
          location.replace(newUrl);
        } else {
          location.href = newUrl;
        }
      }
    },
    refresh: function (url) {
      var _self = this;
      if (StringUtils.isNotBlank(url)) {
        // 调用父类提交方法
        _self._superApply(arguments);
      } else {
        // 调用父类提交方法
        _self._superApply(arguments);
        // appContext.refreshAllWidgets();
      }
    },
    closeLater: function (timeout) {
      var _self = this;
      setTimeout(function () {
        $.ui.goBack();
      }, timeout);
    }
  });
  // 3.4 手机窗口
  var PlusWindowManager = function (appContext) {
    AppWindowManager.call(this, appContext);
  };
  commons.inherit(PlusWindowManager, MobileWindowManager, {
    open: function (options) {
      var _self = this;
      if (typeof options === 'string') {
        if (options.indexOf('http') != 0) {
          options = location.origin + ctx + options;
        }
        $.openWindow.apply(this.window, arguments);
      } else if (typeof options === 'object') {
        var storage = StorageUtils.getStorage();
        if (!options.ui) {
          options.ui = appContext.getPageContainer();
        }
        var newUrl = options.url;
        if (options.useRequestCode !== false) {
          var requestCode = new Date().getTime();
          if (options.ui) {
            storage.setItem(requestCode, options.ui.getId());
          }
          newUrl = UrlUtils.appendUrlParams(newUrl, {
            _requestCode: requestCode
          });
        }
        // 生成窗口名称
        var name = options.target;
        var useUniqueName = options.useUniqueName;
        if (useUniqueName !== false) {
          if (StringUtils.isBlank(name) || name === '_blank') {
            name = options.url;
          }
        }
        if (newUrl.indexOf('http') != 0) {
          newUrl = location.origin + ctx + newUrl;
        }
        $.openWindow(newUrl, name);
      }
    }
  });

  // 4、返回方法模块，实例化对象
  return function (appContext) {
    var appWindowManager = null;
    var env = appContext.getEnvironment();
    /*
     * if(appContext.isMobileApp() && $.openWindow){ appWindowManager = new
     * PlusWindowManager(appContext); }else
     */
    if (env.windowModel === 3) {
      appWindowManager = new MobileWindowManager(appContext);
    } else if (env.windowModel === 2) {
      appWindowManager = new ModalWindowManager(appContext);
    } else {
      appWindowManager = new BrowserWindowManager(appContext);
    }
    // 注册浏览器窗口结果处理器
    appWindowManager.registerWindowResultHandler(-1, _wrNoopHandler);
    appWindowManager.registerWindowResultHandler(0, _wrCloseWindowHandler);
    appWindowManager.registerWindowResultHandler(1, _wrRefreshWindowHandler);
    appWindowManager.registerWindowResultHandler(2, _wrRefreshWidgetHandler);
    appWindowManager.registerWindowResultHandler(3, _wrCallbackHandler);
    appWindowManager.registerWindowResultHandler(4, _wrRefreshCallbackHandler);
    appWindowManager.registerWindowResultHandler(5, _wrDispatchHandler);
    return appWindowManager;
  };
});
