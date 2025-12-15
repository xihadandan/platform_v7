(function (factory) {
  "use strict";
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'bootbox', 'server'], factory);
  } else {
    // Browser globals
    factory(jQuery, bootbox);
  }
}(function ($, bootbox, server) {
  "use strict";
  var JDS = null;
  if (server) {
    JDS = server.JDS;
  } else if (window.JDS) {
    JDS = window.JDS;
  }
  // 警告框
  // 参数格式1：message, callback(result)
  // 参数格式2：{title:"",size:"large",message:"",callback:function(result){}}
  $.WCommonAlert = function () {
      if (arguments.length >= 1 && arguments.length <= 2) {
        var options = {
          title: '提示',
          size: 'small',
          message: "",
          buttons: {
            ok: {
              label: "确定",
              className: "btn-sm well-btn w-btn-primary",
              callback: function () {}
            }
          },
          callback: $.noop,
          draggable: true,
          show: $.noop, // 显示弹出框前事件
          shown: $.noop, // 显示弹出框后事件
        };
        if (typeof (arguments[0]) == "Object") {
          var optJson = arguments[0];
          if (optJson.title) {
            options.title = optJson.title;
          }
          if (optJson.size) {
            options.size = optJson.size;
          }
          if (optJson.message) {
            options.message = optJson.message;
          }
          if (optJson.callback && typeof (options.callback) == "function") {
            options.callback = optJson.callback;
          }
          if (optJson.draggable === false) {
            options.draggable = false;
          }
          if (optJson.show && typeof (options.show) == "function") {
            options.show = optJson.show;
          }
          if (optJson.shown && typeof (options.shown) == "function") {
            options.shown = optJson.shown;
          }
          // $.extend(options, arguments[0]);
        } else {
          options.message = arguments[0];
          if (arguments[1]) {
            options.callback = arguments[1];
          }
        }

        options.buttons.ok.callback = options.onEscape = function () {
          if ($.isFunction(options.callback)) {
            return options.callback.call(this);
          }
          return true;
        };

        try {
          $.WCommonDialog(options);
        } catch (e) {
          console.error(e.message);
        }
      }
    },
    // 进度框
    $.WCommonProgress = function () {

      if (arguments.length >= 1 && arguments.length <= 2) {
        var options = {
          title: "进度",
          size: 'small',
          message: "",
          type: "progress",
          tipsCode: "",
          ignore: false,
          zIndex: 999999999,
          width: "",
          height: "",
          buttons: {
            confirm: {
              label: "确定",
              className: "well-btn w-btn-primary"
            },
            cancel: {
              label: "取消",
              className: "btn-default"
            }
          },
          callback: $.noop,
          draggable: true,
          show: $.noop, // 显示弹出框前事件
          shown: $.noop, // 显示弹出框后事件
        };

        if (arguments[0] instanceof Object) {
          var optJson = arguments[0];
          if (optJson.title) {
            options.title = optJson.title;
          }
          if (optJson.size) {
            options.size = optJson.size;
          }
          if (optJson.zIndex) {
            options.zIndex = optJson.zIndex;
          }
          if (optJson.message) {
            options.message = optJson.message;
          }
          if (optJson.callback && typeof (options.callback) == "function") {
            options.callback = optJson.callback;
          }
          if (optJson.draggable === false) {
            options.draggable = false;
          }
          if (optJson.show && typeof (options.show) == "function") {
            options.show = optJson.show;
          }
          if (optJson.shown && typeof (options.shown) == "function") {
            options.shown = optJson.shown;
          }
          if (optJson.ignore) {
            options.ignore = optJson.ignore
          }
          if (optJson.tipsCode) {
            options.tipsCode = optJson.tipsCode
          }
          if (optJson.width) {
            options.width = optJson.width
          }
          if (optJson.height) {
            options.height = optJson.height
          }
          if (optJson.buttons && Object.keys(optJson.buttons).length > 0) {
            options.buttons = optJson.buttons
          }
          // options = $.extend(options, arguments[0]);
        } else {
          options.message = arguments[0];
          if (arguments[1]) {
            options.callback = arguments[1];
            options.buttons.cancel.callback = options.callback
            options.buttons.confirm.callback = options.callback
          }
        }

        var body = '<div class="bootbox-body-progress"><i></i><span class="modal-progress-msg">' + options.message + '</span></div>';
        options.message = body
        options = $.extend({}, $.wCommonBaseDialog, options);
        try {
          $.WCommonDialog(options);
        } catch (e) {
          console.error(e.message);
        }
      }
    },
    // 确认框
    // 参数格式1：message, callback(result)
    // 参数格式2：{title:"",size:"large",message:"",callback:function(result){}}
    // result : 按确定，则返回true，按取消，则返回false
    $.WCommonConfirm = function () {
      if (arguments.length >= 1 && arguments.length <= 2) {
        var options = {
          title: "确认",
          size: 'small',
          message: "",
          type: "confirm",
          tipsCode: "",
          ignore: false,
          zIndex: 999999999,
          width: "",
          height: "",
          buttons: {
            confirm: {
              label: "确定",
              className: "well-btn w-btn-primary"
            },
            cancel: {
              label: "取消",
              className: "btn-default"
            }
          },
          callback: $.noop,
          draggable: true,
          show: $.noop, // 显示弹出框前事件
          shown: $.noop, // 显示弹出框后事件
        };

        if (arguments[0] instanceof Object) {
          var optJson = arguments[0];
          if (optJson.title) {
            options.title = optJson.title;
          }
          if (optJson.size) {
            options.size = optJson.size;
          }
          if (optJson.zIndex) {
            options.zIndex = optJson.zIndex;
          }
          if (optJson.message) {
            options.message = optJson.message;
          }
          if (optJson.callback && typeof (options.callback) == "function") {
            options.callback = optJson.callback;
          }
          if (optJson.draggable === false) {
            options.draggable = false;
          }
          if (optJson.show && typeof (options.show) == "function") {
            options.show = optJson.show;
          }
          if (optJson.shown && typeof (options.shown) == "function") {
            options.shown = optJson.shown;
          }
          if (optJson.ignore) {
            options.ignore = optJson.ignore
          }
          if (optJson.tipsCode) {
            options.tipsCode = optJson.tipsCode
          }
          if (optJson.width) {
            options.width = optJson.width
          }
          if (optJson.height) {
            options.height = optJson.height
          }
          // options = $.extend(options, arguments[0]);
        } else {
          options.message = arguments[0];
          if (arguments[1]) {
            options.callback = arguments[1];
          }
        }

        options.buttons.cancel.callback = function () {
          return options.callback.call(this, false);
        };

        options.buttons.confirm.callback = function () {
          return options.callback.call(this, true);
        };
        try {
          $.WCommonDialog(options);
        } catch (e) {
          console.error(e.message);
        }
      }
    };

  // 弹出框
  $.WCommonDialog = function (options) {
    var defaultOptions = {
      title: "提示",
      size: 'small',
      type: 'default',
      tipsCode: "",
      ignore: false,
      ignoreMessage: "<div class='modal-ignore'><i class='iconfont icon-ptkj-duoxuan-weixuan'></i><span>不再提醒</span></div>",
      ignoreMount: ".modal-footer",
      ignoreType: "1", // 1:弹窗不再显示，2：ignore消息不再提示
      draggable: true,
      className: "primary-bg",
      closeIcon: "icon-ptkj-dacha",
      zIndex: 999999999,
      width: "",
      height: "",
      timer: null,
      backdrop: true,
      buttons: {
        confirm: {
          label: "确定",
          className: "well-btn w-btn-primary"
        },
        cancel: {
          label: "取消",
          className: "btn-default"
        }
      },
      show: $.noop, // 显示弹出框前事件
      shown: $.noop, // 显示弹出框后事件
      callback: $.noop
    };
    if (options.show && typeof (options.show) == "function") {
      options.show.call(this, options);
    }

    _ignoreStatus(options);
    options = $.extend({}, $.wCommonBaseDialog, defaultOptions, options);
    var type = options.type || ''
    var $dialog = bootbox.dialog(options);
    if (options.closeIcon) {
      $(".bootbox-close-button").html("<i class='iconfont " + options.closeIcon + "'></i>")
    }
    if (options.ignore && options.tipsCode != "") {
      $dialog.find(options.ignoreMount).prepend($(options.ignoreMessage))
    }
    if (options.uiFront !== false) {
      // 参与jquery-ui中dailog的moveToTop的计算
      $dialog.addClass("ui-front");
    }
    var $modalDialog = $dialog.find(".modal-dialog");
    $dialog.css({
      "z-index": options.zIndex
    });
    $dialog.next(".modal-backdrop").css({
      "z-index": options.zIndex - 1
    });

    if (options.width) {
      $modalDialog.width(options.width).css('min-width', options.width);
    }

    if (options.headerType) {
      $modalDialog.find(".modal-header").addClass(options.headerType);
    }
    if (options.headerStyle) {
      $modalDialog.find(".modal-header").css(options.headerStyle);
    }
    if (options.titleStyle) {
      $modalDialog.find(".modal-title").css(options.titleStyle);
    }
    if (options.bodyStyle) {
      $modalDialog.find(".modal-body").css(options.bodyStyle);
      $modalDialog.find(".bootbox-body").css('max-height', 'unset');
    }

    if (options.height) {
      $modalDialog.find(".modal-body").css({
        height: parseInt(options.height) - 106 + "px",
        "overflow-y": "auto"
      });
    }
    if (options.maxHeight) {
      $modalDialog.find(".modal-body").css({
        maxHeight: parseInt(options.maxHeight) - 106 + "px",
        "overflow-y": "auto"
      });
    }

    if (options.dialogPosition) {
      switch (options.dialogPosition) {
        case 'center':
          $dialog.addClass('layout-center');
          break;
      }
    }

    if (type != "") {
      $modalDialog.addClass("modal-" + type);
      $modalDialog.find(".bootbox-body").addClass("bootbox-body-" + type)
    }
    if (options.size == "middle") {
      $modalDialog.removeClass("modal-lg");
      $modalDialog.removeClass("modal-sm");
      $modalDialog.addClass("modal-md");
    }
    if (options.draggable) {
      $modalDialog.draggable({
        handle: ".modal-header,.modal-footer",
        cursor: 'move',
        refreshPositions: false
      });
    }
    if (options.shown && typeof (options.shown) == "function") {
      window.setTimeout(function () {
        options.shown.call(this, $dialog);

      }, 100);
    }
    if (options.timer) {
      setTimeout(function () {
        $dialog.modal("hide");
      }, options.timer)
    }

    if (!options.backdrop) {
      $dialog.next(".modal-backdrop").hide();
    }

    setTimeout(function () {
      if ($modalDialog.find(".bootbox-body").height() > 110) {
        $modalDialog.find(".bootbox-body").removeClass("bootbox-body-" + type)
      }
    }, 150)
    $dialog.on("click", ".modal-ignore", function () { // 不再提醒状态切换
      if ($(this).find("i.iconfont").size() > 0) {
        if ($(this).find("i").hasClass("icon-ptkj-duoxuan-weixuan")) {
          $(this).find("i")
            .removeClass("icon-ptkj-duoxuan-weixuan")
            .addClass("icon-ptkj-duoxuan-xuanzhong")
            .css("color", "#488CEE")
        } else {
          $(this).find("i")
            .removeClass("icon-ptkj-duoxuan-xuanzhong")
            .addClass("icon-ptkj-duoxuan-weixuan")
            .css("color", "#999")
        }
      } else {
        _ignoredModal(options)
        $(this).hide()
      }
    })
    $dialog.on("hidden.bs.modal", function (e) {
      if ($dialog.find(".modal-ignore i").hasClass("icon-ptkj-duoxuan-xuanzhong")) {
        _ignoredModal(options)
      }
      if (options.callback && options.type != "confirm") {
        options.callback()
      }
    });
    return $dialog;
  };

  function _ignoreStatus(options) { // 不再提醒状态
    if (options.ignore && options.tipsCode == "") {
      console.error("tipsCode不能为空")
    } else if (options.ignore && options.tipsCode != "") {
      JDS.call({
        service: "appFacadeService.existAppTipNoLongerRemind",
        version: '',
        data: [options.tipsCode, null],
        async: false,
        success: function (result) {
          if (options.ignoreType == "1") {
            options.show = !result.data
          } else {
            if (result.data) {
              options.ignoreMessage = ""
            }
          }
        }
      });
    }
  }

  function _ignoredModal(options) { // 不再提醒操作
    JDS.call({
      service: "appFacadeService.appTipNoLongerRemind",
      version: '',
      data: [options.tipsCode, null],
      success: function (result) {
        console.log(result);
      }
    });
  }

}));
