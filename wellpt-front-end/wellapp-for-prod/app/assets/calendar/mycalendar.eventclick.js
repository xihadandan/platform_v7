define(['server', 'commons', 'constant', 'appContext'], function (server, commons, constant, appContext) {
  var callback = function (options) {
    debugger;
    var $widget = options.view; // 视图组件
    var params = options.params || {}; // 视图配置参数
    var eventArgs = options.eventArgs; // 事件参数
    var eventName = options.eventName; // 事件名称
    $widget.invokeDevelopmentMethod(eventName, eventArgs);
  };
  return callback;
});
