(function (root, factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    define(['jquery', 'commons', 'bootbox', 'bodymovin', 'appContext', 'appToast', 'ckeditor'], factory);
  } else if (typeof exports === 'object') {
    module.exports = factory(require('jquery'));
  } else {
    root.appModal = factory(root.jQuery, root.commons, root.bootbox, root.bodymovin);
  }
})(this, function init($, commons, bootbox, bodymovin, appContext, appToast, ckeditor) {
  'use strict';
  var StringUtils = (commons || window.commons).StringUtils;
  var exports = {};

  // 提示框
  // 参数格式1：message, callback(result)
  // 参数格式2：{title:"",size:"large",message:"",callback:function(result){}}
  // 参数格式2：{title:"",size:"large",message:""},callback(result)
  var alertDefaults = {
    ignore: false,
    tipsCode: '',
    title: '提示框', // 标题
    message: null, // 消息内容，不能为空
    type: 'info', // 消息类型:success、info、warning、error
    showConfirmButton: null, // 显示确认按钮
    confirmButtonText: '确定', // 确认按钮文本
    closeOnConfirm: true, // 确认后关闭
    callback: null, // 确认按钮事件回调
    size: 'small', // 大小large、middle、small
    className: '', // 添加到弹出框顶层的CSS样式
    timer: null, // 自动定时关闭
    position: null, // 位置:center、bottom-right
    modal: null, // 是否模式对话框
    show: $.noop, // 显示弹出框前事件
    shown: $.noop, // 显示弹出框后事件
    draggable: null, // 可拖动
    resultCode: null, // 应用窗口返回，当callback为空时有效，0关闭当前窗口，刷新父窗口；1、刷新当前窗口，其他不处理
    asyncResult: true, // 异步执行，当resultCode为0时有效
    container: '', // 容器
    modalTimer: null,
    zIndex: 999999999,
    width: '',
    height: '',
    subTitle: '', // 副标题
    autoClose: true // 是否自动关闭，true:默认3秒关闭，false:显示打叉按钮
  };
  var icons = {
    success: 'icon-ptkj-zhengquechenggongtishi',
    info: 'icon-ptkj-xinxiwenxintishi',
    error: 'icon-ptkj-cuowushibaitishi',
    warning: 'icon-ptkj-weixianjinggaotishiyuqi'
  };
  var newIcons = {
    success: 'icon-ptkj-mianxingchenggongtishi',
    info: 'icon-ptkj-mianxingwenxintishi',
    error: 'icon-ptkj-mianxingshibaitishi',
    warning: 'icon-ptkj-mianxingjinggaotishi'
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
  exports.dialogSuccess = function () {
    var options = _options.apply(this, arguments);
    options.type = 'success';
    options.timer = options.timer ? options.timer : 3000;
    _modalStatus(options);
  };
  exports.dialogInfo = function () {
    var options = _options.apply(this, arguments);
    options.type = 'info';
    options.timer = options.timer ? options.timer : 3000;
    options.removeTimer = options.removeTimer;
    _modalStatus(options);
  };
  exports.dialogWarning = function () {
    var options = _options.apply(this, arguments);
    options.type = 'warning';
    _modalStatus(options);
  };
  exports.dialogError = function () {
    var options = _options.apply(this, arguments);
    options.type = 'error';
    _modalStatus(options);
  };
  exports.alert = function () {
    var options = _options.apply(this, arguments);
    options.type = 'alert';
    _modalStatus(options);
  };

  function _modalStatus() {
    var options = _options.apply(this, arguments);
    var requestCode = commons.Browser.getQueryString('_requestCode');
    var callback = options.callback;
    var resultCode = options.resultCode;
    var asyncResult = options.asyncResult;
    var modalType = options.type;
    var iconType = options.type == 'alert' ? '' : icons[modalType];
    options.title = options.type == 'alert' ? options.title : '';
    options.closeButton = options.type == 'alert' ? (options.closeButton == true ? true : options.closeButton) : false;
    options.confirmButtonText = '立即关闭';
    var messages = options.message;
    if (!commons.StringUtils.contains(options.message)) {
      var body = '<div class="bootbox-body-tips bootbox-body-tips-' + modalType + '" style="text-align: left">';
      if (iconType != '') {
        body += '<i class="iconfont ' + iconType + ' "></i>';
      }
      body +=
        '<span class="bootbox-body-tips-msg" style="text-align: left">' + options.message.replace(/(\r\n)|(\n)/g, '<br/>') + '</span>';
      body += "<span class='show-more-msg'>显示更多</span>";
      if (options.timer && !options.removeTimer) {
        body += '<span class="bootbox-body-tips-time">' + options.timer / 1000 + 's后自动关闭</span>';
      }
      body += '</div>';
      options.message = body;
    }
    // 3、返回结果代码、否异步结果
    if (callback == null) {
      if (resultCode == 0 && asyncResult == true) {
        // resultCode==2，刷新组件，若组件不存在刷新窗口
        options.resultCode = 2;
        options.requestCode = requestCode;
        options.message = messages;
        if (window.opener && window.opener.appModal) {
          window.opener.appModal[modalType](options);
        } else {
          appContext.require(['appModal'], function (appModal) {
            options.callback = function () {
              appContext.getWindowManager().close();
              return true;
            };
            appModal[modalType](options);
          });
        }
        appContext.getWindowManager().closeAndRefreshParent();
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
  }

  function _options() {
    var options = {};
    if (arguments.length == 1) {
      if (typeof arguments[0] == 'object') {
        options = arguments[0];
      } else {
        options.message = arguments[0];
      }
    } else if (arguments.length == 2) {
      if (typeof arguments[0] == 'object') {
        options = arguments[0];
      } else {
        options.message = arguments[0];
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
    _setOptionIfNull(options, 'modal', true);
    _setOptionIfNull(options, 'draggable', true);
    _setOptionIfNull(options, 'showConfirmButton', true);

    // 5、timer有值时自动关闭，以毫秒为单位，同时也显示确认按钮
    var requestCode = options.requestCode;
    if (commons.StringUtils.isBlank(requestCode)) {
      requestCode = commons.Browser.getQueryString('_requestCode');
    }
    var callback = options.callback;
    // 确认按钮
    if (options.showConfirmButton === true) {
      var buttons = {
        ok: {
          label: options.confirmButtonText,
          className: 'btn-line',
          callback: function () {
            if (options.modalTimer) {
              clearInterval(options.modalTimer);
            }

            if ($.isFunction(callback)) {
              callback.apply(this, arguments);
              callback = $.noop;
            }

            return options.closeOnConfirm;
          }
        }
      };
      options.buttons = buttons;
    }
    // 是否锁住弹出层
    if (options.modal === false) {
      options.backdrop = null;
    }
    // 初始化弹出框
    var $dialog = bootbox.dialog(options);
    var $modalDialog = $dialog.find('.modal-dialog');
    $modalDialog.addClass('modal-' + type);
    $dialog.css({
      'z-index': options.zIndex
    });
    $dialog.next('.modal-backdrop').css({
      'z-index': options.zIndex - 1
    });
    $dialog.find('.bootbox-close-button').html("<i class='iconfont icon-ptkj-dacha'></i>");
    if (options.width) {
      $modalDialog.width(options.width);
    }

    if (options.height) {
      $modalDialog.find('.modal-body').css({
        height: parseInt(options.height) - 66 + 'px',
        'overflow-y': 'auto'
      });
    }

    setTimeout(function () {
      if ($('.bootbox-body-tips').height() > 110) {
        $('.bootbox-body-tips').addClass('bootbox-body-overflow');
        $('.bootbox-body-tips').removeClass('bootbox-body-tips-' + type);
      }
      if ($('.bootbox-body-tips-msg').height() > 300) {
        $('.bootbox-body-tips-msg').addClass('bootbox-body-msg-overflow');
        $('.show-more-msg').show();
      }
    }, 150);
    $('.show-more-msg').live('click', function () {
      $('.bootbox-body-tips-msg').css({
        overflow: 'auto'
      });
      $(this).hide();
    });
    if (options.uiFront !== false) {
      // 参与jquery-ui中dailog的moveToTop的计算
      $dialog.addClass('ui-front');
    }
    // 可拖动
    if (options.draggable === true) {
      $modalDialog.draggable({
        handle: '.modal-header,.modal-footer',
        cursor: 'move',
        refreshPositions: false
      });
    }
    // 自动关闭

    if (options.timer && !options.removeTimer) {
      var times = options.timer / 1000;
      options.modalTimer = setInterval(function () {
        times--;
        $('.bootbox-body-tips-time').html(times + 's后自动关闭');
        if (times <= 0) {
          clearInterval(options.modalTimer);
          if ($.isFunction(callback)) {
            callback();
            callback = $.noop;
          }

          if (options.closeOnConfirm === true) {
            $modalDialog.parent().removeClass('fadeInUp').addClass('fadeOutDown'); //消失换动画形式消失
            setTimeout(function () {
              $modalDialog.find('.btn-line').trigger('click');
            }, 100);
          }
        }
      }, 1000);
    }

    if (options.shown && typeof options.shown == 'function') {
      window.setTimeout(function () {
        options.shown.call(this, $dialog);
      }, 100);
    }
  }

  // 确认框
  // 参数格式1：message, callback(result)
  // 参数格式2：{title:"",size:"large",message:"",callback:function(result){}}
  // result : 按确定，则返回true，按取消，则返回false
  exports.confirm = function () {
    $.WCommonConfirm.apply(this, arguments);
  };

  // 弹出框
  exports.dialog = function (options) {
    if (StringUtils.isNotBlank(options.templateId)) {
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      options.message = templateEngine.renderById(options.templateId, options);
    }
    return $.WCommonDialog.apply(this, arguments);
  };

  exports.progress = function (options) {
    return $.WCommonProgress.apply(this, arguments);
  };

  exports.showSimpleLoading = function (message, container, timer) {
    // 提示文本
    if (commons.StringUtils.isBlank(message)) {
      message = '数据加载中...';
    }
    if (container == null) {
      container = 'body';
    }
    // timer
    if (timer == null) {
      timer = 60000;
    }
    // 容器定位
    var $box = $(container);
    $box.append(
      `<div class="widget-box-overlay simple-line-loading-body">
      <div class="simple-line-loading"><div><span></span><span></span><span></span><span></span><span></span>
      </div><label>${message}</label></div></div>`
    );
    $box.one('simpleLoadingRemove', function () {
      $box.find('.simple-line-loading-body').remove();
    });
  };

  exports.hideSimpleLoading = function (container) {
    if (container == null) {
      container = 'body';
    }

    var $box = $(container);
    $box.trigger('simpleLoadingRemove');
  };

  exports.showSkeleton = function (container, rows) {
    if (container && $(container).find('.ui-skeleton').length == 0) {
      var doms = ['<div class="ui-skeleton ui-skeleton-active"><div class="ui-skeleton-content"><ul class="ui-skeleton-paragraph">'];
      rows = rows || 4;
      doms = doms.concat(new Array(rows).join('<li></li>'));
      doms.push('<li style="width: 61%;"></li></ul></div></div>');
      $(container).append(doms.join(''));
    }
  };
  // 遮罩层
  // 参数格式：message,container,timer,maskCls
  exports.showMask = function (message, container, timer, hasMask) {
    var f = function () {
      if ($('#appModalLoading').size() > 0) {
        return;
      }
      // 提示文本
      if (commons.StringUtils.isBlank(message)) {
        message = '数据加载中...';
      }
      // 容器
      if (container == null) {
        container = 'body';
      }
      // timer
      if (timer == null) {
        timer = 600000;
      }
      if (hasMask == null) {
        hasMask = true;
      }

      // 容器定位
      var $box = $(container);

      // 遮罩层
      var overlay =
        '<div class="widget-box-overlay" id="appModalLoading"><div class="loading-overlay">' +
        '<div id="loadingGif" class="loading-overlay-gif"></div>' +
        '<div class="rectangle-bounce">' +
        '<div class="rect1"></div>' +
        '<div class="rect2"></div>' +
        '<div class="rect3"></div>' +
        '<div class="rect4"></div>' +
        '<div class="rect5"></div>' +
        '</div>' +
        '<div class="loading-text">' +
        message +
        '</div></div></div>';
      $box.append(overlay);
      if (!hasMask) {
        $box
          .find('.widget-box-overlay')
          .css({
            background: 'initial'
          })
          .find('.loading-text')
          .css({
            color: '#999'
          });
      }
      // 自定义事件移除遮罩层
      $box.one('reloaded.mask.widget', function () {
        $box.find('.widget-box-overlay').remove();
        if (!hasMask) {
          $box.find('.widget-box-overlay').css({
            background: 'initial'
          });
        }
      });
      // // 超时移除遮罩层
      var maskTimeout = setTimeout(function () {
        $box.trigger('reloaded.mask.widget');
      }, timer);
      $box.data('maskTimeout', maskTimeout);
    };
    var args = arguments;
    // 防抖
    (function (func, wait) {
      window.showMaskTimer && clearTimeout(window.showMaskTimer);
      !window.showMaskTimer && func.apply(this, args);
      window.showMaskTimer = setTimeout(function () {
        window.showMaskTimer = null;
      }, wait);
    })(f, 500);
  };

  function _loadingGif() {
    bodymovin.loadAnimation({
      container: document.getElementById('loadingGif'), // the dom element that will contain the animation
      renderer: 'svg',
      loop: true,
      autoplay: true,
      path: ctx + '/resources/pt/js/loading.json' // the path to the animation json
    });
  }
  // 遮罩层
  // 参数格式：container
  exports.hideMask = function (container, immediate) {
    var f = function () {
      // 容器
      if (container == null) {
        container = 'body';
      }

      var $box = $(container);
      $box.trigger('reloaded.mask.widget');
      window.clearTimeout($box.data('maskTimeout'));
    };
    if (immediate === true) {
      f();
      return;
    }
    var args = arguments;
    (function (func, wait) {
      window.hideMaskTimer && clearTimeout(window.hideMaskTimer);
      window.hideMaskTimer = setTimeout(function () {
        func.apply(this, args);
      }, wait);
    })(f, 500);
  };

  // 参数格式：container
  exports.hide = function (container) {
    if (container == null || typeof container === 'undefined') {
      return bootbox.hideAll();
    }
    return $(container).modal('hide');
  };

  exports.toast = function () {
    // 传参：1、"message"   2、{type,container,message,timer}
    var options = _options.apply(this, arguments);
    options.name = 'toast';
    _initDom(options);
  };
  exports.tips = function () {
    // 1、"message"  2、{type,container,message,callback}
    var options = _options.apply(this, arguments);
    options.name = 'tips';
    _initDom(options);
  };

  exports.success = function () {
    var options = _options.apply(this, arguments);
    options.name = 'newToast';
    options.type = 'success';
    _initToastDom(options, this);
  };
  exports.info = function () {
    var options = _options.apply(this, arguments);
    options.name = 'newToast';
    options.type = 'info';
    _initToastDom(options, this);
  };
  exports.warning = function () {
    var options = _options.apply(this, arguments);
    options.name = 'newToast';
    options.type = 'warning';
    _initToastDom(options, this);
  };
  exports.error = function () {
    var options = _options.apply(this, arguments);
    options.name = 'newToast';
    options.type = 'error';
    console.log(options.message);
    _initToastDom(options, this);
  };

  function _initDom(options) {
    var body = '';
    var iconType = icons[options.type];
    var name = options.name;
    options.container = options.container || 'body';
    if (name == 'toast') {
      // toast

      body = '<div class="pt-toast">';
      if (iconType) {
        body += '<i class="iconfont ' + iconType + '"></i>';
      }
      body += '<span class="pt-toast-msg">' + options.message + '</span>' + '</div>';
    } else if (name == 'tips') {
      // 提示

      body = '<div class="pt-tips ' + options.type + '">';
      if (iconType) {
        body += '<i class="iconfont icon-pre ' + iconType + '"></i>';
      }
      body += '<span class="pt-tips-msg">' + options.message + '</span>' + '<i class="iconfont icon-del icon-ptkj-dacha"></i>' + '</div>';
    }
    // else if(name == "newToast"){
    //     body = '<div class="pt-newToast ' + options.type + '">'
    //         + '<i class="iconfont icon-pre ' + iconType + '"></i>'
    //         + '<div class="pt-newToast-msg">'
    //         + '<span class="pt-tips-msg">' + options.message + '</span>';
    //     if(options.subTitle){
    //         body += '<span class="pt-tips-msg-sub">' + options.subTitle + '</span>';
    //     }
    //     body += "</div>";
    //     if(!options.autoClose){
    //         body += '<i class="iconfont icon-del icon-ptkj-dacha"></i>';
    //     }
    //     body += '</div>';
    // }
    options.message = body;
    _showPage(options);
  }

  function _showPage(options) {
    if (
      $(options.container)
        .find('.pt-' + options.name)
        .size() > 0 &&
      options.name != 'newToast'
    ) {
      $(options.container)
        .find('.pt-' + options.name)
        .remove();
    }
    if (options.name == 'tips') {
      $(options.container).append($(options.message));
      $(options.container).find('.pt-tips').animate(
        {
          top: 0,
          left: 0,
          opacity: 1,
          width: '100%'
        },
        600
      );
    } else {
      $(options.container).append(options.message);
    }

    if ($(options.message).hasClass('pt-toast')) {
      // toast自动关闭
      setTimeout(function () {
        $('.pt-toast').remove();
      }, options.timer || 3000);
    }

    $('.icon-del', '.pt-tips').on('click', function () {
      // 关闭tips提示
      $(this).parents('.pt-tips').remove();
    });
  }

  function _initToastDom(options, _this) {
    if (_this) {
      _this.hideMask(null, true);
    }
    var iconType = newIcons[options.type];
    options.container = options.container || 'body';
    var modalType = options.type;
    var requestCode = commons.Browser.getQueryString('_requestCode');
    var callback = options.callback;
    var resultCode = options.resultCode;
    var asyncResult = options.asyncResult;
    var message = options.message;
    var body =
      '<div class="pt-newToast-box"><div class="pt-newToast ' +
      options.type +
      '">' +
      '<i class="iconfont icon-pre ' +
      iconType +
      '"></i>' +
      '<div class="pt-newToast-msg">' +
      '<span class="pt-tips-msg">' +
      options.message +
      '</span>';
    if (options.subTitle) {
      body += '<span class="pt-tips-msg-sub">' + options.subTitle + '</span>';
    }
    body += '</div>';
    if (!options.autoClose) {
      body += '<i class="iconfont icon-del icon-ptkj-dacha"></i>';
    }
    body += '</div></div>';
    options.message = body;
    if (callback == null) {
      if (resultCode == 0 && asyncResult == true) {
        // resultCode==2，刷新组件，若组件不存在刷新窗口
        options.resultCode = 2;
        options.requestCode = requestCode;
        options.message = message;
        if (window.opener && window.opener.appModal) {
          window.opener.appModal[modalType](options);
          appContext.getWindowManager().closeAndRefreshParent();
        } else {
          appContext.require(['appModal'], function (appModal) {
            options.callback = function () {
              appContext.getWindowManager().close();
              return true;
            };
            appModal[modalType](options);
          });
        }
      } else if (resultCode == 0 && asyncResult == false) {
        options.callback = function () {
          appContext.getWindowManager().closeAndRefreshParent();
          return true;
        };
        _showToastPage(options);
      } else if (resultCode == 1) {
        options.callback = function () {
          appContext.getWindowManager().refresh();
          return true;
        };
        _showToastPage(options);
      } else {
        _showToastPage(options);
      }
    } else {
      _showToastPage(options);
    }
  }

  function _showToastPage(options) {
    if ($('.dialog-newToast').size() > 0) {
      $('.dialog-newToast').append(options.message);
    } else {
      $(options.container).append("<div class='dialog-newToast'>" + options.message + '</div>');
    }
    $('.icon-del', '.pt-newToast-box').on('click', function () {
      // 关闭tips提示
      if ($.isFunction(options.callback)) {
        options.callback();
      }
      $(this).parents('.pt-newToast-box').remove();
      if ($('.pt-newToast-box').size() <= 0) {
        $('.dialog-newToast').remove();
      }
    });
    $(document).keypress(function (e) {
      if (e.keyCode == 13 && $('.pt-newToast-box').find('.icon-del')) {
        $('.pt-newToast-box').find('.icon-del').trigger('click');
      }
    });

    if ($(options.message).hasClass('pt-newToast-box') && (options.timer || options.autoClose)) {
      // toast自动关闭
      var toast = $('.dialog-newToast').find('.pt-newToast-box');
      $.each(toast, function (index, item) {
        setTimeout(function () {
          if ($.isFunction(options.callback)) {
            options.callback();
          }
          $(item).remove();
          if ($('.pt-newToast-box').size() <= 0) {
            $('.dialog-newToast').remove();
          }
        }, options.timer || 3000);
      });
    }
  }

  // 遮罩隐藏事件处理，修复会左移问题
  $('body').on('hidden.bs.modal', function () {
    $(this).css({
      paddingLeft: '',
      paddingRight: ''
    });
    // 弹出层嵌套时，当前窗口还存在弹出层，保持打开样式
    if ($('body>div.bootbox.modal:visible').length > 0) {
      $(this).addClass('modal-open');
    } else {
      $(this).removeClass('modal-open');
    }
  });

  window.appModal = exports;
  return exports;
});
