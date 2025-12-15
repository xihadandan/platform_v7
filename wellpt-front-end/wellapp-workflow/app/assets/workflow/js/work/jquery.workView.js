(function (factory) {
  /* global define */
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'appContext', 'commons'], factory);
  } else if (typeof module === 'object' && module.exports) {
    // Node/CommonJS
    module.exports = factory(require('jquery'));
  } else {
    // Browser globals
    factory(window.jQuery);
  }
})(function ($, appContext, commons) {
  'use strict';
  var StringUtils = commons.StringUtils;
  var btnLengthDefalut =
    SystemParams.getValue('workflow.toolbarPlaceholder.button.max') == 'null'
      ? 3
      : SystemParams.getValue('workflow.toolbarPlaceholder.button.max');
  var WorkViewProxy = function (element, options) {
    this.options = options;
    this.$body = $(document.body);
    this.$element = $(element);
    this._createWorkView();
  };
  // 创建流程二开
  WorkViewProxy.prototype._createWorkView = function () {
    this._create();
    this._init();
    this._loadData();
  };
  // 创建并绑定事件
  WorkViewProxy.prototype._create = function () {
    var workViewModule = this.options.workViewModule;
    this.workView = new workViewModule(this.$element);
    this.workView.$element = this.$element;
    this._displayToolbar();
  };
  //  顶部按钮分组折叠
  WorkViewProxy.prototype._displayToolbar = function () {
    var _self = this;
    var toolbarPlaceholder = _self.options.toolbarPlaceholder;
    var rightMap = _self.options.rightMap;
    // ACL角色对应的操作
    var aclRoleOpts = _self.options.toolbar[_self.options.workData.aclRole];
    if (Browser.getQueryString('lxqpMode')) {
      $('#lxqpMode').remove();
    }
    $('.btn', toolbarPlaceholder).each(function () {
      // 按钮类型
      var btnType = $(this).attr('btnType');
      // 按钮编号
      var name = $(this).attr('name');
      // 操作名称
      var optName = rightMap[name];
      if (optName != null && _contains(aclRoleOpts, optName) && _self.workView[optName] != null) {
        $(this).addClass('isShow'); //增加显示样式，用于判断是否为显示的按钮
      } else if (btnType == '2') {
        $(this).addClass('isShow'); //增加显示样式，用于判断是否为显示的按钮
      }
    });
    var showLength = $('.btn.isShow', toolbarPlaceholder).length;
    if (showLength > btnLengthDefalut) {
      var $btn_group = $('<div>', {
        class: 'btn-group',
        style: 'overflow: visible'
      });
      var $btn_more = $('<button>', {
        class: 'well-btn w-btn-primary w-line-btn dropdown-toggle',
        'data-toggle': 'dropdown',
        'aria-expanded': 'false'
      }).html('<span>更多</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia"></i>');
      var $ul = $('<ul>', {
        class: 'dropdown-menu w-btn-dropMenu',
        role: 'menu'
      });
      $('.btn.isShow', toolbarPlaceholder).each(function (index) {
        if (index >= btnLengthDefalut) {
          var $li = $('<li>').append($(this).show());
          var $html = $li.html();
          $html = $html.replace(/button/g, 'div'); //将button标签改成div标签
          $li.empty().append($html);
          $ul.append($li);
        } else {
          $(this).show();
        }
      });
      $btn_group.append($btn_more).append($ul);
      $(toolbarPlaceholder).append($btn_group);
    } else {
      $('.btn.isShow', toolbarPlaceholder).each(function (index) {
        $(this).show();
      });
    }
    _self._bindEvent();
  };
  // 绑定按钮操作事件
  WorkViewProxy.prototype._bindEvent = function () {
    var _self = this;
    var toolbarPlaceholder = _self.options.toolbarPlaceholder;
    var rightMap = _self.options.rightMap;
    $('.isShow', toolbarPlaceholder).each(function () {
      // 按钮类型
      var btnType = $(this).attr('btnType');
      // 按钮编号
      var name = $(this).attr('name');
      // 操作名称
      var optName = rightMap[name];
      if (btnType != '2') {
        // 获取派发器
        var dispatcher = appContext.getDispatcher(true);
        // 派处理函数
        var callback = function (options) {
          // 绑定当前事件
          _self.workView.setCurrentEvent(options.event);
          _self.workView[options.optName].call(_self.workView, options.event);
        };
        // 注册派发器处理
        dispatcher.register(callback);
        $(this).on(
          'click',
          $.proxy(
            _.debounce(
              function (e) {
                e.stopPropagation();
                // 按钮点击事件中发起派发
                var options = {};
                options.optName = optName;
                options.event = e;
                var beforeActionFunction = _self.getBeforeActionFunction(optName);
                if ($.isFunction(beforeActionFunction)) {
                  if (beforeActionFunction.call(_self.workView, options.event) != false) {
                    dispatcher.dispatch(options);
                  } else {
                    console.log('按钮操作事件' + optName + '前处理返回false，终止事件处理！');
                  }
                } else {
                  if (options.optName === 'close') {
                    if (top.WorkFlowLXQP) {
                      top.WorkFlowLXQP.parentNextRecord();
                    }
                  }
                  dispatcher.dispatch(options);
                }
              },
              500,
              {
                leading: true,
                trailing: false
              }
            ),
            this
          )
        );
      } else if (btnType == '2') {
        // 事件处理按钮
        $(this).on(
          'click',
          $.proxy(
            _.debounce(
              function (e) {
                e.stopPropagation();
                var piUuid = $(this).attr('name');
                var hash = $(this).attr('hash');
                var btnParams = $(this).attr('btnParams');
                var targetPosition = $(this).attr('targetPosition');
                var eventParams = JSON.parse(btnParams || '[]');
                var params = {
                  hash: hash
                };
                $.each(eventParams, function (i, eventParam) {
                  if (eventParam.name) {
                    params[eventParam.name] = eventParam.value;
                  }
                });
                var appOptions = {
                  workView: _self.workView,
                  ui: _self.workView,
                  target: targetPosition,
                  params: params
                };
                appContext.startAppByPiUuid(piUuid, appOptions);
              },
              500,
              {
                leading: true,
                trailing: false
              }
            ),
            this
          )
        );
      }
    });
    //  监听移除
    $('.btn-group', toolbarPlaceholder).bind('DOMNodeRemoved', function (e) {
      var $this = $(this);
      setTimeout(function () {
        // 用于处理"更多"下拉框内无元素时，隐藏"更多"按钮
        if (!$this.find('ul li').html()) {
          $this.hide();
        }
      }, 100);
    });
    //  监听添加
    $('.btn-group', toolbarPlaceholder).bind('DOMNodeInserted', function (e) {
      var $this = $(this);
      setTimeout(function () {
        // 用于处理"更多"下拉框内有元素时，显示"更多"按钮
        if ($this.find('ul li').html() && $this[0].style.display == 'none') {
          $this.show();
        }
      }, 100);
    });
    $('.btn-group', toolbarPlaceholder)
      .off('click')
      .on('click', function () {
        var $this = $(this);
        setTimeout(function () {
          var expanded = $this.find('.dropdown-toggle').attr('aria-expanded');
          if (expanded) {
            $('.dropdown-menu', '.btn-group', toolbarPlaceholder).getNiceScroll().resize();
          } else {
            $('.dropdown-menu', '.btn-group', toolbarPlaceholder).getNiceScroll().hide();
          }
        }, 500);
      });
    $('.dropdown-menu', '.btn-group', toolbarPlaceholder).niceScroll({
      cursorcolor: '#ccc'
    });
  };
  WorkViewProxy.prototype.getBeforeActionFunction = function (action) {
    var _self = this;
    var beforeAction = 'onBefore' + StringUtils.capitalise(action);
    return _self.workView[beforeAction];
  };
  // 初始化
  WorkViewProxy.prototype._init = function () {
    this._mergeOpinionEditorOptions();
    this.workView.init(this.options);
  };
  // 收集签署意见的权限信息
  WorkViewProxy.prototype._mergeOpinionEditorOptions = function () {
    var _self = this;
    var toolbarPlaceholder = _self.options.toolbarPlaceholder;
    var requiredMap = _self.options.opinionEditor.requiredMap;
    var rightMap = _self.options.rightMap;
    $('.btn', toolbarPlaceholder).each(function () {
      // 按钮编号
      var name = $(this).attr('name');
      // 操作名称
      var optName = rightMap[name];
      var required = requiredMap[optName];
      if (optName != null && required != null) {
        _self.options.opinionEditor.required[required] = true;
      }
      // 签署意见，显示签署意见弹出框
      if (_self.options.opinionEditor.signOpinionCode === name) {
        _self.options.opinionEditor.signOpinion = true;
      }
    });
  };
  // 加载数据
  WorkViewProxy.prototype._loadData = function () {
    this.workView.load();
  };

  // 判断数据是否包含指定值
  function _contains(array, value) {
    if (array == null) {
      return false;
    }
    for (var i = 0; i < array.length; i++) {
      if (array[i] === value) {
        return true;
      }
    }
    return false;
  }

  $.fn.workView = function (options) {
    return this.each(function () {
      var $this = $(this);
      if (!$this.data('WorkView')) {
        options = $.extend(true, {}, $.fn.workView.defaults, options);
        $this.data('WorkView', new WorkViewProxy($this, options));
      }
    });
  };

  $.fn.workView.defaults = {
    workViewModule: null,
    processViewerModule: null,
    opinionEditorModule: null,
    errorHandlerModule: null,
    opinionEditor: {
      opinionLabel: '',
      opinionValue: '',
      opinionText: '',
      signOpinionModel: 1, // 签署意见模式
      editorPlaceholder: '.row-summernote', // 编辑器点位符
      snapshotPlaceholder: '.row-sign-opinion-text', // 签署意见快照点位符
      signOpinionCode: 'B004011', // 是否显示签署意见框，对应按钮权限B004011
      clickButtonToShow: false, // 点击显示签署意见框，与签署意见模式配合使用
      required: {
        submit: false,
        cancel: false,
        transfer: false,
        counterSign: false,
        rollback: false,
        remind: false,
        handOver: false,
        gotoTask: false
      },
      requiredMap: {
        requiredSubmitOpinion: 'submit',
        requiredCancelOpinion: 'cancel',
        requiredTransferOpinion: 'transfer',
        requiredCounterSignOpinion: 'counterSign',
        requiredRollbackOpinion: 'rollback',
        requiredRemindOpinion: 'remind',
        requiredHandOverOpinion: 'handOver',
        requiredGotoTaskOpinion: 'gotoTask'
      }
    },
    processViewer: {
      buttonCode: 'B004013', // 是否显示签署意见框，对应按钮权限B004011
      clickButtonToShow: true, // 点击显示签署意见框，与签署意见模式配合使用
      viewerPlaceholder: '.work-process-viewer' //
    },
    dyformSelector: '#dyform', // 表单选择器
    dyformEditableCode: 'B004025', // 可编辑文档代码
    toolbarPlaceholder: '.wf_operate', // 操作按钮占位符
    submitButtonCode: 'B004002', // 提交按钮代码
    submitAndPrintButtonCode: 'B004020', // 打印并套打按钮代码
    rollbackButtonCode: 'B004003', // 退回按钮代码
    cancelButtonCode: 'B004005', // 撤回按钮代码
    viewProcessButtonCode: 'B004013', // 查看办理过程按钮代码
    gotoTaskButtonCode: 'B004016', // 跳转按钮代码
    // 角色操作草稿(DRAFT)、待办(TODO)、已办(DONE)、办结(OVER)、已阅(FLAG_READ)、未阅(UNREAD)、关注(ATTENTION)、督办(SUPERVISE)、监控(MONITOR)
    toolbar: {
      DRAFT: ['click', 'close', 'save', 'submit'],
      TODO: [
        'click',
        'close',
        'save',
        'submit',
        'rollback',
        'directRollback',
        'rollbackToMainFlow',
        'viewTheMainFlow',
        'PrintForm',
        'transfer',
        'counterSign',
        'attention',
        'print',
        'copyTo',
        'unfollow',
        'viewProcess',
        'suspend',
        'resume',
        'layoutDocumentProcess',
        'viewReadLog',
        'enterLxqpMode'
      ],
      DONE: [
        'click',
        'close',
        'cancel',
        'attention',
        'print',
        'copyTo',
        'unfollow',
        'remind',
        'viewProcess',
        'viewTheMainFlow',
        'PrintForm',
        'viewReadLog'
      ],
      OVER: ['click', 'close', 'attention', 'print', 'copyTo', 'unfollow', 'viewProcess', 'viewTheMainFlow', 'viewReadLog', 'cancel'],
      FLAG_READ: [
        'click',
        'close',
        'attention',
        'print',
        'copyTo',
        'unfollow',
        'viewProcess',
        'viewTheMainFlow',
        'PrintForm',
        'viewReadLog'
      ],
      UNREAD: ['click', 'close', 'attention', 'print', 'copyTo', 'unfollow', 'viewProcess', 'viewTheMainFlow', 'viewReadLog'],
      ATTENTION: ['click', 'close', 'print', 'copyTo', 'unfollow', 'viewProcess', 'viewTheMainFlow', 'viewReadLog'],
      SUPERVISE: ['close', 'attention', 'print', 'copyTo', 'unfollow', 'viewProcess', 'remind', 'viewTheMainFlow', 'viewReadLog'],
      MONITOR: [
        'close',
        'attention',
        'print',
        'copyTo',
        'unfollow',
        'viewProcess',
        'remind',
        'handOver',
        'gotoTask',
        'viewReadLog',
        'viewTheMainFlow',
        'viewFlowDataSnapshot',
        'remove'
      ]
    },
    rightMap: {
      B004000: 'click', // 点击
      B004001: 'save', // 保存
      B004002: 'submit', // 提交
      B004003: 'rollback', // 退回
      B004004: 'directRollback', // 直接退回
      B004005: 'cancel', // 撤回
      B004006: 'transfer', // 转办
      B004007: 'counterSign', // 会签
      B004008: 'attention', // 关注
      B004009: 'print', // 套打
      B004010: 'copyTo', // 抄送
      B004011: 'signOpinion', // 签署意见
      B004012: 'unfollow', // 取消关注
      B004013: 'viewProcess', // 查看办理过程
      B004014: 'remind', // 催办
      B004015: 'handOver', // 移交
      B004016: 'gotoTask', // 跳转
      B004017: 'suspend', // 挂起
      B004018: 'resume', // 恢复
      B004019: 'close', // 关闭
      B004020: 'submitAndPrint', // 提交并套打
      B004023: 'remove', // 删除
      B004025: 'editable', // 可编辑文档
      B004026: 'requiredSubmitOpinion', // 必须签署意见
      B004039: 'requiredCancelOpinion', // 撤回必填意见
      B004029: 'requiredTransferOpinion', // 转办必填意见
      B004030: 'requiredCounterSignOpinion', // 会签必填意见
      B004031: 'requiredRollbackOpinion', // 退回必填意见
      B004032: 'requiredHandOverOpinion', // 特送个人必填意见
      B004033: 'requiredGotoTaskOpinion', // 特送环节必填意见
      B004034: 'requiredRemindOpinion', // 催办环节必填意见
      B004035: 'rollbackToMainFlow', // 退回主流程
      B004095: 'viewTheMainFlow', //查看主流程
      B004096: 'PrintForm', //打印表单
      B004097: 'layoutDocumentProcess', // 版式文档处理
      B004037: 'viewReadLog', // 查看阅读记录
      B004038: 'viewFlowDataSnapshot', // 查看流程数据快照
      B004040: 'enterLxqpMode' //连续签批模式
    }
  };
});
