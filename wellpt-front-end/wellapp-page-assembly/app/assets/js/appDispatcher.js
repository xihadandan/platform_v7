define(['jquery', 'commons', 'constant'], function ($, commons, constant) {
  // 平台默认的应用派发器
  var callbacks = [];
  var UrlUtils = commons.UrlUtils;
  var StringUtils = commons.StringUtils;
  var urlPrefix = ctx + '/web/app';
  var urlSuffix = '.html';
  var _self = '_self';
  var _blank = '_blank';
  var _widget = '_targetWidget';
  var _dialog = '_dialog';

  // 获取派发器的目标
  var getTarget = function (options) {
    var target = options.target;
    if (StringUtils.isBlank(target)) {
      target = _self;
    }
    return target;
  };
  // 获取派发器的目标组件ID
  var getTargetWidgetId = function (options) {
    var targetWidgetId = options.targetWidgetId;
    if (options.eventTarget) {
      var eventTarget = options.eventTarget;
      // 样式选择器
      if (eventTarget.widgetSelectorType == '2') {
        var widgetCssClass = eventTarget.widgetCssClass;
        var targetWidget = appContext.getWidgetByCssSelector('.' + widgetCssClass);
        if (targetWidget != null) {
          targetWidgetId = targetWidget.getId();
        }
      }
    }
    if (StringUtils.isBlank(targetWidgetId)) {
      targetWidgetId = appContext.getPageContainer().getId();
    }
    return targetWidgetId;
  };

  // 1、系统派发器
  var sysCallback = function (options) {
    var appType = options.appType;
    var appPath = options.appPath;
    if (constant.getValueByKey(constant.APP_TYPE, 'SYSTEM', 'englishName') == appType) {
      var target = getTarget(options);
      var url = urlPrefix + appPath + urlSuffix;
      windowOpen(appPath, appPath, url, target, options, false);
      return true;
    }
  };

  // 2、模块派发器
  var moduleCallback = function (options) {
    var appType = options.appType;
    var appPath = options.appPath;
    if (constant.getValueByKey(constant.APP_TYPE, 'MODULE', 'englishName') == appType) {
      var target = getTarget(options);
      var appContext = options.appContext;
      var userAppData = appContext.getCurrentUserAppData();
      var url = urlPrefix + appPath + urlSuffix;
      windowOpen(appPath, appPath, url, target, options, false);
      return true;
    }
  };

  // 3、应用派发器
  var appCallback = function (options) {
    var appId = options.appId;
    var appType = options.appType;
    var appPath = options.appPath;
    if (constant.getValueByKey(constant.APP_TYPE, 'APPLICATION', 'englishName') == appType) {
      var isJsModule = options.isJsModule;
      // 发起JS模块的应用
      if (isJsModule === true) {
        console.log('start js app: ' + appPath);
        options.appContext.require([appPath], function (app) {
          app(options);
        });
      } else {
        var appContext = options.appContext;
        var app = appContext.getApplication(appId || appPath);
        var appFunction = null;
        // 获取应用关联的功能，若存在关联功能派发该功能
        if (app && StringUtils.isNotBlank(app.cfUuid)) {
          appFunction = appContext.getFunction(app.cfUuid);
        }
        if (appFunction != null) {
          _dispachFunction(options, appFunction);
        } else if (app) {
          var target = getTarget(options);
          var userAppData = appContext.getCurrentUserAppData();
          var url = urlPrefix + appPath + urlSuffix;
          windowOpen(app.id, app.name, url, target, options, false);
        } else {
          return false;
        }
      }
      return true;
    }
  };

  // 4、功能派发器
  var funcCallback = function (options) {
    var appId = options.appId;
    var appType = options.appType;
    var appPath = options.appPath;
    var appName = options.appName;
    if (constant.getValueByKey(constant.APP_TYPE, 'FUNCTION', 'englishName') == appType) {
      var appContext = options.appContext;
      // console.log("功能派发器处理中");
      var appFunction = options.appFunction;
      // 功能派发参数存在功能信息直接使用
      if (!(appFunction && appFunction.appPath === appPath && appFunction.appType == appType)) {
        // appFunction = appContext.getFunction(appId || appPath, appName);
        appContext.getFunction(appId || appPath, appName, function (_appFuc) {
          _dispachFunction(options, _appFuc);
          appModal.hideMask();
        });
      }
      if (appFunction == null) {
        return;
      }
      return _dispachFunction(options, appFunction);
    }
  };
  // 派发功能处理
  function _dispachFunction(options, appFunction) {
    var isJsModule = appFunction.isJsModule;
    // 发起JS模块的功能
    if (isJsModule === true) {
      var jsPath = ctx + appFunction.path + '.js';
      console.log('start js app: ' + jsPath);
      var jsModules = [appFunction.jsModule];
      jsModules = jsModules.concat(appFunction.deps);
      appContext.require(jsModules, function (app) {
        appContext.executeJsModule(app, options);
      });
      return true;
    }

    var type = appFunction.category;
    var target = getTarget(options);
    // URL功能处理
    if (type === 'URL' || type === 'MENU') {
      if (appFunction.url.indexOf('/security_logout') >= 0) {
        try {
          // 退出时清理cookie TODO 移出到appAdapter
          var plus = window.plus;
          if (plus && plus.storage && plus.storage.getItem) {
            var setting = plus.storage.getItem('setting') || '{}';
            var setting = JSON.parse(setting);
            setting.autoLogin = false;
            plus.storage.setItem('setting', JSON.stringify(setting));
          }
          var localStorage = window.localStorage;
          if (localStorage && localStorage.removeItem) {
            localStorage.removeItem('autoLogin');
          }
          var wx = window.wx;
          if (wx && wx.getStorageSync && wx.setStorageSync) {
            var userInfo = wx.getStorageSync('userInfo') || {};
            userInfo.autoLogin = false;
            wx.setStorageSync('userInfo', userInfo);
          }
        } catch (ex) {
          $.toast('清理自动登录状态：' + ex.message);
        }
      }
      var url = ctx + appFunction.url;
      windowOpen(appFunction.id, appFunction.name, url, target, options, false);
      return true;
    }
    // Web控制器功能处理
    if (type === 'Controller') {
      var patterns = appFunction.patterns || [];
      // bug#59785，多个url默认只打开第一个
      if(patterns.length > 1) {
		if(options.params && options.params.openAll == "true") {
		} else {
			patterns = [patterns[0]];
		}
      }
      $.each(patterns, function (i) {
        var url = ctx + this;
        var useRequestCode = false;
        if (options.params && options.params.useRequestCode == 'true') {
          useRequestCode = true;
        }
        windowOpen(appFunction.id, appFunction.name, url, target, options, useRequestCode);
      });
      return true;
    }

    // 组件
    if (type === 'appWidgetDefinition') {
      var targetWidgetId = options.targetWidgetId;
      // 当前窗口组件没有指定渲染的目标组件，使用当前页面容器
      if (target === _widget) {
        targetWidgetId = getTargetWidgetId(options);
      } else if (target === _self) {
        // 当前窗口，使用当前页面容器
        targetWidgetId = appContext.getPageContainer().getId();
      } else if (target === _blank) {
        // 新窗口，暂时使用当前页面容器
        // TODO
        targetWidgetId = appContext.getPageContainer().getId();
        $(document.body).removeClass('fixed-header fixed-navbar');
      } else if (target === _dialog) {
        targetWidgetId = '_dialog_inner';
        appModal.dialog({
          message: '<div id="_dialog_inner"></div>',
          size: 'large',
          title: options.eventTarget.widgetDialogTitle || '',
          width: options.eventTarget.widgetDialogWidth || '1200',
          height: options.eventTarget.widgetDialogHeight || ''
        });
      }

      var renderOptions = {
        renderTo: '#' + targetWidgetId,
        widgetDefId: appFunction.id,
        widgetCloneable: options.widgetCloneable,
        refreshIfExists: options.refreshIfExists,
        params: options.params,
        onPrepare: options.onPrepare, //onPrepare事件可由派发源决定
        initiator: {
          type: 'widget',
          id: options.ui && options.ui.getId ? options.ui.getId() : ''
        }
      };
      if (options.appContext.currentUserAppData && options.appContext.currentUserAppData.appData) {
        options.appContext.currentUserAppData.appData.dispatchAppPath = options.appPath; //派发的应用路径
      }
      if (options.renderNavTab) {
        renderOptions.renderNavTab = options.renderNavTab;
        renderOptions.text = options.text;
        renderOptions.menuId = options.menuId;
        renderOptions.buttonEventId = options.buttonEventId;
        renderOptions.buttonEvent = options.buttonEvent;
        renderOptions.pNavTabId = options.pNavTabId;
        renderOptions.target = options.target;
      }
      options.appContext.renderWidget(renderOptions);
      return true;
    }

    // 发起流程
    if (type === 'flowDefinition') {
      if (appContext.isMobileApp()) {
        var menuItem = options.menuItem || {};
        var draftTitle = menuItem.text || '';
        appContext.require(['mobile_new_work'], function (startNewWork) {
          var flowDefId = appFunction.flowId;
          if (!flowDefId && appFunction.id) {
            flowDefId = appFunction.id.substr(0, appFunction.id.lastIndexOf('_'));
          }
          startNewWork({
            title: draftTitle,
            action: 'newWorkById',
            flowDefId: flowDefId
          });
        });
      } else {
        var fullNewWorkUrl = ctx + appFunction.newWorkUrl;
        options.useUniqueName = false;
        windowOpen(appFunction.id, appFunction.name, fullNewWorkUrl, appFunction.target, options, true);
      }
      return true;
    }

    // 数据管理操作
    if (type === 'DmsAction') {
      options.appFunction = appFunction;
      options.originalAction = options.action;
      options.params = options.params;
      options.action = appFunction.id;
      appContext.require(['DmsActionDispatcher'], function (app) {
        appContext.executeJsModule(app, options);
      });
      return true;
    }
  }

  // 5、js模块派发器
  var jsModuleCallback = function (options) {
    if (options.isJsModule === true) {
      var jsModule = options.jsModule;
      var devJsModules = options.jsModule.split(constant.Separator.Comma);
      $.each(devJsModules, function (i, devJsModule) {
        appContext.require([devJsModule], function (app) {
          appContext.executeJsModule(app, options);
        });
      });
      return true;
    }
  };

  // 打开窗口
  var windowOpen = function (id, text, url, target, options, useRequestCode) {
    var warpperUrl = url;
    var urlParams = options.params || {};
    // 选中参数
    if (options && options.selection) {
      urlParams = $.extend(true, urlParams, {
        selection: options.selection
      });
    }
    // 锚点
    var hash = urlParams.hash || '';
    if (hash) {
      if (!hash.startsWith('#')) {
        hash = '#' + hash;
      }
      delete urlParams.hash;
    }
    warpperUrl = UrlUtils.appendUrlParams(warpperUrl, urlParams) + hash;
    // 存储源appId
    // var storage = commons.StorageUtils.getStorage();
    // if (options && options.appPath && options.appContext) {
    // options.appContext.setInitiatorPath(options.appPath);
    // } else {
    // reuqire([ "appContext" ], function(appContext) {
    // appContext.setInitiatorPath("");
    // });
    // }
    // 替换当前窗口组件
    if (target === constant.TARGET_POSITION.TARGET_WIDGET) {
      // $.get(url, function(content) {
      // $("#" + options.targetWidgetId).html(content);
      // })

      //需要显示在多标签导航上
      if (options.renderNavTab) {
        appContext.getWidgetByCssSelector('#' + options.targetWidgetId)._addTab({
          options: options,
          warpperUrl: warpperUrl,
          type: 'iframe',
          single: true
        });
      } else {
        var iframe =
          '<div class="embed-responsive embed-responsive-4by3"><iframe src="' +
          warpperUrl +
          '" class="embed-responsive-item" style="width:100%;"></iframe></div>';
        var targetWidgetId = getTargetWidgetId(options);
        // 删除目标组件下的组件
        appContext.removeChildrenWidgetById(targetWidgetId, true);
        appContext.getWidgetRenderPlaceholder(targetWidgetId).html(iframe);
      }
    } else if (target === '_navTab') {
      appContext.getWidgetByCssSelector('#' + options.targetWidgetId)._addTab({
        options: options,
        warpperUrl: warpperUrl,
        type: 'iframe'
      });
    } else if (target === '_dialog') {
      var iframe =
        '<div class="embed-responsive embed-responsive-4by3"><iframe src="' +
        warpperUrl +
        '" class="embed-responsive-item" style="width:100%;"></iframe></div>';
      var widgetDialogTitle = '';
      var widgetDialogWidth = '';
      var widgetDialogHeight = '';
      if (options.targetDetail) {
        widgetDialogTitle = options.targetDetail.widgetDialogTitle || '';
        widgetDialogWidth = options.targetDetail.widgetDialogWidth || '';
        widgetDialogHeight = options.targetDetail.widgetDialogHeight || '';
      } else if (options.eventTarget) {
        widgetDialogTitle = options.eventTarget.widgetDialogTitle || '';
        widgetDialogWidth = options.eventTarget.widgetDialogWidth || '';
        widgetDialogHeight = options.eventTarget.widgetDialogHeight || '';
      }
      appModal.dialog({
        message: iframe,
        size: 'large',
        title: widgetDialogTitle,
        noHeader: widgetDialogTitle == '' ? true : false,
        width: widgetDialogWidth || '1200',
        height: widgetDialogHeight || '',
        buttons: {}
      });
    } else {
      var windowOptions = {};
      windowOptions.id = id;
      windowOptions.text = text;
      windowOptions.url = warpperUrl;
      windowOptions.target = target;
      windowOptions.useRequestCode = urlParams.useRequestCode || useRequestCode;
      windowOptions.useUniqueName = options.useUniqueName;
      windowOptions.locationReplace = options.locationReplace;
      windowOptions.ui = options.ui;
      windowOptions.size = 'large';
      appContext.openWindow(windowOptions);
    }
  };

  callbacks.push(sysCallback);
  callbacks.push(appCallback);
  callbacks.push(moduleCallback);
  callbacks.push(funcCallback);
  callbacks.push(jsModuleCallback);
  return callbacks;
});
