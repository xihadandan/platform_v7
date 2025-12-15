define(['jquery', 'commons', 'bootstrap', 'jquery-ui', 'appContext', 'appWindowManager', 'appModal', 'appDispatcher', 'wWidget'], function (
  $,
  commons,
  bootstrap,
  ui,
  appContext,
  appWindowManager,
  appModal,
  appDispatcher,
  wWidget
) {
  // 1、控制台
  if (!window.console) {
    window.console = commons.Console;
  }

  // 2、配置应用上下文
  (function configureAppContext(appContext) {
    var env = WebApp.environment || {};
    // 2.1、设置系统参数
    appContext.setEnvironment(env);

    // 2.2、设置JS模板
    appContext.setJavaScriptTemplates(WebApp.jsTemplates || {});

    // 2.3、设置窗口管理器
    appContext.setWindowManager(appWindowManager(appContext));

    // 2.4、注册默认的应用派发器
    for (var i = 0; i < appDispatcher.length; i++) {
      appContext.registerAppDispatcher(appDispatcher[i]);
    }
  })(appContext);

  // 3、渲染页面
  var wtype = WebApp.pageDefinition.wtype;
  if (wtype != null) {
    var renderTo = WebApp.pageDefinition.id || 'body';
    var pageSelector = renderTo === 'body' ? renderTo : '#' + renderTo;
    // 初始化页面
    $(pageSelector)[wtype]({
      initiator: { type: 'window', id: renderTo },
      widgetDefinition: WebApp.pageDefinition,
      onPrepare: function () {
        appContext.setPageContainer(this);
      }
    });
  }
});
