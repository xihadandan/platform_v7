define(['mui', 'commons', 'appContext'], function ($, commons, appContext) {
  // our public object; augmented after our private API
  var exports = {};

  // 提示框
  // 参数格式1：message, callback(result)
  // 参数格式2：{title:"",size:"large",message:"",callback:function(result){}}
  // 参数格式2：{title:"",size:"large",message:""},callback(result)
  var alertDefaults = {
    title: '提示框', // 标题
    message: null, // 消息内容
    type: 'info', // 消息类型:success、info、warning、error
    showConfirmButton: null, // 显示确认按钮
    confirmButtonText: '确定', // 确认按钮文本
    closeOnConfirm: true, // 确认后关闭
    callback: null, // 确认按钮事件回调
    size: 'small', // 大小
    timer: null, // 自动定时关闭
    position: null, // 位置:center、bottom-right
    modal: null, // 是否模式对话框
    show: $.noop, // 显示弹出框前事件
    shown: $.noop, // 显示弹出框后事件
    draggable: null, // 可拖动
    resultCode: null, // 应用窗口返回，当callback为空时有效，0关闭当前窗口，刷新父窗口；1、刷新当前窗口，其他不处理
    asyncResult: true, // 异步执行，当resultCode为0时有效
    container: '' // 容器
  };
  // 1、类型为success、info时在右下显示，不阻塞窗口，不可拖动，自动计时关闭
  // 2、类型为warning、error时在中间显示，阻塞窗口，可拖动，不自动计时关闭
  // 3、返回结果代码、否异步结果
  // 3.1、resultCode==0，asyncResult=true时，异步执行，关闭当前窗口，在父窗口进行弹框显示，同时刷新父窗口组件
  // 3.2、resultCode==0，asyncResult=false时，同步执行，在当前窗口进行弹框显示，关闭窗口，同时刷新父窗口组件
  // 3.3、resultCode==1，asyncResult=true时，asyncResult无效，窗口关闭时刷新窗口
  // 3.4、resultCode==1，asyncResult=false时，asyncResult无效，窗口关闭时刷新窗口
  // 4、callback在窗口关闭时执行
  // 5、timer有值时自动关闭，以毫秒为单位，不显示确认按钮
  exports.success = function () {
    var options = _options.apply(this, arguments);
    options.type = 'success';
    options.position = 'bottom-right';
    exports.alert(options);
  };
  exports.info = function () {
    var options = _options.apply(this, arguments);
    options.type = 'info';
    options.position = 'bottom-right';
    exports.alert(options);
  };
  exports.warning = function () {
    var options = _options.apply(this, arguments);
    options.type = 'warning';
    options.position = 'center';
    exports.alert(options);
  };
  exports.error = function () {
    var options = _options.apply(this, arguments);
    options.type = 'error';
    options.position = 'center';
    exports.alert(options);
  };
  exports.alert = function () {
    var options = _options.apply(this, arguments);
    var requestCode = commons.Browser.getQueryString('_requestCode');
    var callback = options.callback;
    var resultCode = options.resultCode;
    var asyncResult = options.asyncResult;
    // 3、返回结果代码、否异步结果
    if (callback == null) {
      if (resultCode == 0 && asyncResult == true) {
        // resultCode==2，刷新组件，若组件不存在刷新窗口
        options.resultCode = 2;
        options.requestCode = requestCode;
        appContext.getWindowManager().refresh();
        options.callback = function () {
          appContext.getWindowManager().refresh();
          return true;
        };
        exports.alert(options);
      } else if (resultCode == 0 && asyncResult == false) {
        options.callback = function () {
          appContext.getWindowManager().closeAndRefreshParent();
          return true;
        };
        _alert(options);
      } else if (resultCode == 1) {
        options.callback = function () {
          appContext.getWindowManager().refresh();
          return true;
        };
        _alert(options);
      } else {
        _alert(options);
      }
    } else {
      _alert(options);
    }
  };

  function _options() {
    var options = {};
    if (arguments.length == 1) {
      if (typeof arguments[0] == 'object') {
        options = arguments[0];
      } else {
        options.message = arguments[0];
        options.type = 'warning';
      }
    } else if (arguments.length == 2) {
      if (typeof arguments[0] == 'object') {
        options = arguments[0];
      } else {
        options.message = arguments[0];
        options.type = 'warning';
      }
      options.callback = arguments[1];
    } else {
      throw new error('无效的提示框参数!');
    }
    options = $.extend(_alertDefaults(), options);
    return options;
  }

  function _alertDefaults() {
    return $.extend({}, alertDefaults);
  }

  function _setOptionIfNull(options, key, value) {
    if (options[key] == null) {
      options[key] = value;
    }
  }

  function _alert(options) {
    // 1、类型为success、info时在右下显示，不阻塞窗口，不可拖动，自动计时关闭
    // 2、类型为warning、error时在中间显示，阻塞窗口，可拖动，不自动计时关闭
    var type = options.type;
    if (type === 'warning' || type === 'error') {
      _setOptionIfNull(options, 'position', 'center');
      _setOptionIfNull(options, 'modal', true);
      _setOptionIfNull(options, 'draggable', true);
      _setOptionIfNull(options, 'showConfirmButton', true);
    } else {
      _setOptionIfNull(options, 'position', 'bottom-right');
      _setOptionIfNull(options, 'modal', false);
      _setOptionIfNull(options, 'draggable', false);
      _setOptionIfNull(options, 'timer', 2000);
    }
    // 5、timer有值时自动关闭，以毫秒为单位，不显示确认按钮
    var timer = options.timer;
    if (timer != null) {
      _setOptionIfNull(options, 'showConfirmButton', false);
    } else if (options.showConfirmButton == false) {
      _setOptionIfNull(options, 'timer', 2000);
    }
    // 显示确认按钮
    if (options.showConfirmButton == null) {
      options.showConfirmButton = true;
    }

    // 弹框处理
    // 设置内容
    var requestCode = options.requestCode;
    if (commons.StringUtils.isBlank(requestCode)) {
      requestCode = commons.Browser.getQueryString('_requestCode');
    }
    var callback = options.callback;
    // 自动关闭
    if (options.showConfirmButton === false) {
      mui.toast(options.message);
      setTimeout(function () {
        if ($.isFunction(callback)) {
          callback();
        }
      }, options.timer);
    } else {
      $.alert(options.message, options.title, options.confirmButtonText, function () {
        if ($.isFunction(callback)) {
          callback();
        }
      });
    }
  }

  // 确认框
  // 参数格式1：message, callback(result)
  // 参数格式2：{title:"",size:"large",message:"",callback:function(result){}}
  // result : 按确定，则返回true，按取消，则返回false
  exports.confirm = function () {
    var options = _options.apply(this, arguments);
    var btnValue = ['取消', options.confirmButtonText];
    var callback = function (args) {
      if (args.index == 1 && $.isFunction(options.callback)) {
        options.callback(args.index);
      }
    };
    $.confirm(options.message, options.title, btnValue, callback);
  };

  // 弹出框模板ID
  var dialogTplId = 'mui-appModal-dialog';
  // 弹出框
  exports.dialog = function (options) {
    // 弹出框ID
    var dialogId = commons.UUID.createUUID();
    // 弹出框内容ID
    var contentId = commons.UUID.createUUID();
    var buttonCount = 0;
    var callbacks = {};
    $.each(options.buttons, function (index, button) {
      buttonCount++;
      button.index = index;
      callbacks[index] = button.callback;
    });
    options.dialogId = dialogId;
    options.contentId = contentId;
    options.btnColspan = 12;
    if (buttonCount !== 0) {
      options.btnColspan = 12 / buttonCount;
    }
    var templateEngine = appContext.getJavaScriptTemplateEngine();
    var html = templateEngine.renderById(dialogTplId, options);
    var container = options.container || 'body';
    var popover = document.createElement('div');
    popover.innerHTML = html;
    $(container)[0].appendChild(popover);
    var popoverContent = popover.querySelector('li.mui-popover-content');
    var contentElem = popover.querySelector('#' + contentId);
    contentElem.innerHTML = options.message;
    var $dialog = $('#' + dialogId);
    // 获取焦点
    $.focus($dialog[0]);
    // 显示前回调
    if ($.isFunction(options.show)) {
      options.show.call(this);
    }
    // 显示后回调
    if ($.isFunction(options.shown)) {
      $dialog[0].addEventListener('shown', options.shown);
    }
    // 退出回调
    $dialog[0].addEventListener('hidden', function () {
      if ($.isFunction(options.onEscape)) {
        options.onEscape.apply(this, arguments);
      }
      $dialog.popover('hide');
      if (popover && popover.parentNode) {
        popover.parentNode.removeChild(popover);
      }
    });
    $dialog.popover('toggle');
    // 设置可滚动(如果视图内没有可滚动元素)
    setTimeout(function (t) {
      if ($('div.mui-scroll-wrapper', popoverContent).length <= 0) {
        $.ui.scrollAble(popoverContent, {
          autoHeight: true
        });
      }
    }, 1000);
    // 按钮事件
    $dialog.on('tap', '.mui-popover-button', function (e) {
      var btnIndex = this.getAttribute('index');
      if (btnIndex != null && $.isFunction(callbacks[btnIndex])) {
        if (callbacks[btnIndex].apply($dialog, arguments) !== false) {
          $.trigger($dialog[0], 'hidden', arguments);
        }
      }
    });
    return $dialog;
  };

  // 弹出面板
  exports.panel = function (options) {
    // TODO
  };

  // 动态弹出框
  var oToast = $.toast || $.noop;
  var CLASS_ACTIVE = 'mui-active';
  exports.toast = function (message, type) {
    if (type === 'success' || type === 'warning' || type === 'error' || type === 'info') {
      var toast = document.createElement('div');
      toast.classList.add('mui-toast-container');
      toast.classList.add('mui-toast-' + type);
      toast.classList.add('mui-toast-' + type);
      var toastHTML = '<span class="mui-toast-message">';
      if (type === 'success') {
        toastHTML += '<i class="fa fa-check-circle" aria-hidden="true"></i>';
      } else if (type === 'warning') {
        toastHTML += '<i class="fa fa-exclamation-triangle" aria-hidden="true"></i>';
      } else if (type === 'error') {
        toastHTML += '<i class="fa fa-bolt" aria-hidden="true"></i>';
      } else if (type === 'info') {
        toastHTML += '<i class="fa fa-info" aria-hidden="true"></i>';
      }
      toastHTML += '' + message + '</span>';
      toast.innerHTML = toastHTML;
      toast.addEventListener('webkitTransitionEnd', function () {
        if (!toast.classList.contains(CLASS_ACTIVE)) {
          toast.parentNode.removeChild(toast);
        }
      });
      document.body.appendChild(toast);
      toast.classList.add(CLASS_ACTIVE);
      return setTimeout(function () {
        toast.classList.remove(CLASS_ACTIVE);
      }, 2000);
    }
    return oToast.apply(this, arguments);
  };
  exports.toast['info'] = 'info';
  exports.toast['error'] = 'error';
  exports.toast['warning'] = 'warning';
  exports.toast['success'] = 'success';

  // 遮罩层
  // 参数格式：message,container,timer,maskCls
  exports.showMask = function (message, container, timer, maskCls) {
    // 提示文本
    if (message == null || typeof message === 'undefined') {
      message = '数据加载中...';
    }
    // 容器
    if (container == null) {
      container = 'body';
    }
    // timer
    if (timer == null) {
      timer = 30000;
    }

    // 创建遮罩层
    var mask = exports.mask;
    if (!mask) {
      mask = $.createMask(function () {
        return exports.isHideMask;
      });
      exports.isHideMask = false;
      exports.mask = mask;
    }
    var sb = new commons.StringBuilder();
    sb.appendFormat('<div class="mui-pull mui-mask">');
    if (message) {
      sb.appendFormat('<div class="mui-pull-loading mui-icon mui-spinner"></div>');
      sb.appendFormat('<div class="mui-pull-caption">{0}</div>', message);
    }
    sb.appendFormat('</div>');
    $.each(mask, function () {
      this.innerHTML = sb.toString();
    });
    // 显示遮罩
    mask.show();
    // 超时移除遮罩层
    setTimeout(function () {
      // mask.close();// 关闭遮罩
      exports.hideMask();
    }, timer);
  };

  // 遮罩层
  // 参数格式：container
  exports.hideMask = function (container) {
    if (exports.mask) {
      exports.isHideMask = true;
      exports.mask.close();
      {
        // 立即移除元素
        var element = exports.mask[0];
        var body = document.body;
        element.parentNode === body && body.removeChild(element);
      }
      delete exports.mask;
    }
  };
  window.appModal = exports;
  return exports;
});
