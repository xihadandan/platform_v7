define(['jquery', 'server', 'commons', 'constant', 'appContext', 'appModal', 'WorkFlow'], function (
  $,
  server,
  commons,
  constant,
  appContext,
  appModal,
  WorkFlow
) {
  var JDS = server.JDS;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var Browser = commons.Browser;
  var SystemParams = server.SystemParams;
  var FileDownloadUtils = server.FileDownloadUtils;
  var JDS = server.JDS;
  var WorkFlowDevelopment = require('WorkFlowDevelopment');
  var WorkView = function ($element) {
    this.$element = this.element = $element;
  };
  //流程意见校验设置_场景
  var sceneEnum = {
    SUBMIT: 'S001', //提交
    RETURN: 'S002', //退回
    TURN_TO: 'S003', //转办
    COUNTERSIGN: 'S004' //会签
  };
  commons.inherit(WorkView, WorkFlowDevelopment, {
    // 初始化
    init: function (options) {
      var _self = this;
      _self.options = options;
      _self.dyformSelector = options.dyformSelector;
      _self.submitButtonCode = options.submitButtonCode;
      _self.submitAndPrintButtonCode = options.submitAndPrintButtonCode;
      _self.rollbackButtonCode = options.rollbackButtonCode;
      _self.cancelButtonCode = options.cancelButtonCode;
      _self.viewProcessButtonCode = options.viewProcessButtonCode;
      _self.gotoTaskButtonCode = options.gotoTaskButtonCode;
      _self.workFlow = new WorkFlow(options.workData);
      // 服务前后回调事件
      _self.workFlow.setBeforeServiceCallback(options.beforeServiceCallback);
      _self.workFlow.setAfterServiceCallback(options.afterServiceCallback);
      // 创建相关组件
      _self.createToolbar();
      _self.createErrorHandler();
      if (_self.workFlow.workData.aclRole) {
        _self.createLXQPComponent();
      }
      // 注册到appContext
      _self.registerWorkView2AppContext();
      // 监听窗口关闭事件
      _self.registerWindowCloseEventListerner();
      window.WorkView = _self;
    },
    getWorkFlow: function () {
      return this.workFlow;
    },
    // 创建工具栏
    createToolbar: function () {
      var _self = this;
      var WorkFlowToolbar = _self.options.toolbarModule;
      if ($.isFunction(WorkFlowToolbar)) {
        this.toolbar = new WorkFlowToolbar(_self, _self.options.toolbarPlaceholder);
      }
    },
    // 返回工具栏
    getToolbar: function () {
      return this.toolbar;
    },
    // 创建右边栏
    createSidebar: function () {
      var _self = this;
      var signOpinionModel = _self.options.signOpinionModel;
      var defaultShowTab = '';
      var tabShowType = 'float';
      if (_self.isAllowSignOpinion()) {
        defaultShowTab = '签署意见';
        // tabShowType = "extrusion";
      }
      if (signOpinionModel == '1') {
        $('.right-sidebar-open').hide();
        var tabs = _self.getSidebarTabData();
        if (tabs && tabs.length > 0) {
          // 右侧标签页
          $.fn.appTabs({
            siblings: $('#dyform').parent(),
            tabs: (function () {
              return tabs;
            })(), // tab数据项
            tabShowType: tabShowType, // 展现
            defaultShowTab: defaultShowTab, // 默认展示的标签页
            dragable: true, // 是否可拖动
            showStickPin: true, // 显示固定按钮
            tabPrefix: 'wf_right-sidebar_tab_',
            top: $('.widget-header').height(), // 距离顶部距离
            bottom: $('.footer').outerHeight(), // 距离底部距离
            tabWidth: '450px',
            successCallback: function () {
              $('#dyform').parent().css('padding-right', 11);
              $(document).on('siblingContainerResize', function () {
                var _dyform = _self.getDyform();
                if (_dyform) {
                  _dyform.autoWidth();
                }
              });
            }
          });
          $('#rightMenuTabNavContainer').on('show.bs.tab', '.pull-right-nav>li>a[role=tab]', function (e) {
            var $tab = $(e.target);
            var tabName = $tab.text();
            _self.showSidebarTab(tabName, $($tab.attr('href') + '_content'));
          });
          // tab打开时触发激活的tab显示
          if ($('button.open-tab', '#rightMenuContainer').hasClass('open-tab-active')) {
            $('ul>li.active', '#rightMenuTabNavContainer').find('a').trigger('show.bs.tab');
          }

          $('#wf_sign_opinion_content').slimScroll({
            height: $(document).height() - 80 + 'px',
            wheelStep: navigator.userAgent.indexOf('Firefox') > -1 ? 1 : 10
          });
          $('#wf_work_flow').click(function () {
            var workData = _self.workFlow.getWorkData();
            // var url = ctx + "/workflow/show?open=true&id="+workData.flowDefUuid+"&flowInstUuid="+workData.flowInstUuid;
            var url =
              ctx +
              '/web/app/pt-mgr/pt-wf-mgr/pt-wf-viewer.html?open=true&onlyView=true&id=' +
              workData.flowDefUuid +
              '&flowInstUuid=' +
              workData.flowInstUuid;
            window.open(url, '_blank');
          });
          // 签署意见占位符
          _self.options.opinionEditor.editorPlaceholder = $('#wf_sign_opinion_content');
          // 办理过程占位符
          var $viewerPlaceholder = $('#wf_work_process_content');
          _self.options.processViewer.viewerPlaceholder = $viewerPlaceholder;
          $viewerPlaceholder.append($('.sidebar-container', '.work-process-viewer'));
          $viewerPlaceholder.addClass('work-process-viewer');
          // 默认显示签署意见
          if (_self.isAllowSignOpinion()) {
            _self.createOpinionEditor();
          }
        }
      } else {
        _self.createProcessViewer();
        _self.createOpinionEditor();
      }
    },
    // 获取侧边tab数据
    getSidebarTabData: function () {
      var _self = this;
      var tabs = [];
      if (
        _self.isAllowSignOpinion() &&
        !_self.isOver() //办结工作不显示签署意见
      ) {
        tabs.push({
          name: '签署意见',
          id: 'wf_sign_opinion'
        });
      }

      //送审批的流程实例uuid
      var approveFlowInstUuid = '';
      var split = location.href.split('&');
      $.each(split, function (index, urlParam) {
        var approveFlowInstUuidKey = 'approveFlowInstUuid=';
        var indexOf = urlParam.indexOf(approveFlowInstUuidKey);
        if (indexOf > -1) {
          approveFlowInstUuid = urlParam.substring(indexOf + approveFlowInstUuidKey.length, urlParam.length);
        }
      });

      if (_self.isAllowViewProcess() || approveFlowInstUuid) {
        if (!_self.isDraft()) {
          tabs.push({
            name: '办理过程',
            id: 'wf_work_process'
          });
        }
        tabs.push({
          name: '查阅流程',
          id: 'wf_work_flow'
        });
      }
      return tabs;
    },

    // 显示侧边tab
    showSidebarTab: function (tabName, $tabContentContainer) {
      var _self = this;
      if (tabName == '签署意见') {
        if (_self.opinionEditor == null) {
          // 签署意见占位符
          _self.options.opinionEditor.editorPlaceholder = $('#wf_sign_opinion_content');
          _self.getOpinionEditor();
        }
      } else if (tabName == '办理过程') {
        if (_self.processViewer == null) {
          _self.createProcessViewer();
          // 初始化办理过程
          _self.initWorkProcess();
        }
      }
    },
    // 是否允许查看办理过程
    isAllowViewProcess: function () {
      var btnSelector = ":button[name='" + this.viewProcessButtonCode + "']";
      return $(btnSelector).length > 0;
    },
    // 创建过程查看器
    createProcessViewer: function () {
      var _self = this;
      var WorkFlowWorkProcessViewer = _self.options.processViewerModule;
      if ($.isFunction(WorkFlowWorkProcessViewer)) {
        this.processViewer = new WorkFlowWorkProcessViewer(_self, _self.options.processViewer);
      }
    },
    // 是否允许签署意见
    isAllowSignOpinion: function () {
      return this.options.opinionEditor.signOpinion === true || this.isAllowCancel();
    },
    // 是否允许撤回
    isAllowCancel: function () {
      var _self = this;
      // 撤回
      var cancelButtonCode = _self.cancelButtonCode;
      var btnSelector = ":button[name='" + cancelButtonCode + "']";
      var cancel = $(btnSelector, _self.options.toolbarPlaceholder).length > 0;
      return this.options.opinionEditor.required.cancel === true && cancel;
    },
    isAllowedCancel: function (taskInstUuid) {
      return this.checkPermission(taskInstUuid, 'isAllowedCancel');
    },
    checkPermission: function (taskInstUuid, suffix) {
      var _self = this;
      JDS.restfulPost({
        url: ctx + '/api/workflow/work/' + suffix,
        data: {
          taskInstUuids: [taskInstUuid]
        },
        async: false,
        success: function (result) {
          isAllowedTransfer = result.data;
          _self[suffix + 'CheckResult'] = result;
        },
        error: function (jqXHR) {
          isAllowedTransfer = false;
          try {
            _self[suffix + 'CheckResult'] = JSON.parse(jqXHR.responseText);
          } catch (e) {}
        }
      });
      return isAllowedTransfer;
    },
    // 创建意见编辑器
    createOpinionEditor: function () {
      var _self = this;
      var WorkFlowOpinionEditor = _self.options.opinionEditorModule;
      if ($.isFunction(WorkFlowOpinionEditor) && this.opinionEditor == null) {
        var workData = _self.workFlow.getWorkData();
        var flowInstUuid = workData.flowInstUuid || Browser.getQueryString('flowInstUuid');
        // 撤回后,要能带出之前提交填写的意见 待办才执行
        if (StringUtils.isNotBlank(flowInstUuid) && workData.aclRole == 'TODO') {
          var cancelOpinionText = _self._getLastTaskOpinionCancelAfter(flowInstUuid);
          if (StringUtils.isNotBlank(cancelOpinionText)) {
            _self.options.opinionEditor.opinionText = cancelOpinionText;
          }
        }
        this.opinionEditor = new WorkFlowOpinionEditor(_self, _self.options.opinionEditor);
      }
    },
    // 获取意见编辑器
    getOpinionEditor: function () {
      var _self = this;
      if (_self.opinionEditor == null) {
        _self.createOpinionEditor();
        // 初始化意见编辑器
        _self.initOpinionEditor();
      }
      return _self.opinionEditor;
    },
    // 创建异常处理器
    createErrorHandler: function () {
      var _self = this;
      var WorkFlowErrorHandler = _self.options.errorHandlerModule;
      if ($.isFunction(WorkFlowErrorHandler)) {
        this.errorHandler = new WorkFlowErrorHandler(_self);
      }
    },
    // 注册流程单据二开对象到appContext
    registerWorkView2AppContext: function () {
      var _self = this;
      appContext.getDocument = function () {
        return _self;
      };
    },
    // 加载流程数据
    load: function () {
      var _self = this;
      //如果是连续签批模式则不初始化父页面表单
      if (_self.isLXQPMode() && window == top) {
        return;
      }
      var success = function () {
        _self.onLoadSuccess.apply(_self, arguments);
        _self.initDyform();
      };
      var failure = function () {
        _self.onLoadFailure.apply(_self, arguments);
      };
      _self.workFlow.load(success, failure);
    },
    // 成功加载流程数据
    onLoadSuccess: function () {
      // 子流程共享数据信息
      this.createShareDataViewer();
    },
    // 关闭
    close: function () {
      var _self = this;
      var _superApply = _self._superApply;
      if (_self.isTodo()) {
        var workData = _self.workFlow.getWorkData();
        JDS.restfulPost({
          url: ctx + '/api/workflow/work/unlockWork',
          data: {
            taskInstUuid: workData.taskInstUuid
          },
          contentType: 'application/x-www-form-urlencoded',
          success: function () {
            // 调用父类方法
            _superApply.apply(_self, arguments);
          },
          error: function () {
            // 调用父类方法
            _superApply.apply(_self, arguments);
          }
        });
      } else {
        // 调用父类方法
        _self._superApply(arguments);
      }
    },
    // 是否需要确认重新加载流程数据
    isNeedConfirmUnloadWorkData: function () {
      var _self = this;
      // 附件下载标记不卸载窗口内容
      if (window.unloadContent === false) {
        delete window.unloadContent;
        return false;
      }
      // 允许重新加载流程数据
      if (_self.allowUnloadWorkData == true) {
        return false;
      }
      // 用户确认关闭
      if (_self.userConfirmClose == true) {
        return false;
      }
      // 检测办理意见、表单数据是否变更
      return _self.isOpinionChanged() || _self.isDyformDataChanged();
    },
    // 办理意见是否变更
    isOpinionChanged: function () {
      var _self = this;
      var opinionEditor = _self.opinionEditor;
      if (opinionEditor == null) {
        return false;
      }
      var initOptions = opinionEditor.options;
      var opinion = opinionEditor.getOpinion();
      var opinionChanged = opinion.text != (opinionEditor.restoreText || initOptions.opinionText); // || opinion.label != initOptions.opinionLabel || opinion.value != initOptions.opinionValue;
      return opinionChanged;
    },
    // 表单数据是否变更
    isDyformDataChanged: function (formData) {
      return this.getDyform().isDyformDataChanged(formData);
    },
    // 锁定工作
    lockWorkIfRequired: function () {
      var _self = this;
      if (_self.isTodo()) {
        var workData = _self.workFlow.getWorkData();
        JDS.restfulPost({
          url: ctx + '/api/workflow/work/lockWork',
          data: {
            taskInstUuid: workData.taskInstUuid
          },
          error: function () {}
        });
      }
    },
    // 监听窗口关闭事件
    registerWindowCloseEventListerner: function () {
      var _self = this;
      // 窗口关闭工作解锁
      $(window).on('unload', function () {
        if (_self.isTodo()) {
          var workData = _self.workFlow.getWorkData();
          if (workData.actionType != 'Save') {
            JDS.restfulPost({
              url: ctx + '/api/workflow/work/unlockWork',
              data: {
                taskInstUuid: workData.taskInstUuid
              },
              contentType: 'application/x-www-form-urlencoded',
              error: function () {}
            });
          }
        }
      });
      // 关闭前表单数据变更判断
      window.onbeforeunload = function (e) {
        if (_self.isNeedConfirmUnloadWorkData()) {
          e.preventDefault();
          e = e || window.event;
          if (e) {
            e.returnValue = '内容已修改，请先保存后再操作，否则修改内容将丢失！';
          }
          // Chrome, Safari, Firefox 4+, Opera 12+ , IE 9+
          return e.returnValue;
        }
      };
    },
    // 创建子流程共享数据查看器
    createShareDataViewer: function () {
      var _self = this;
      var workData = _self.workFlow.getWorkData();
      if (workData.branchTaskData != null || workData.subTaskData != null) {
        appContext.require(['WorkFlowShareDataViewer'], function (WorkFlowShareDataViewer) {
          var shareDataViewer = new WorkFlowShareDataViewer(_self, workData.branchTaskData, workData.subTaskData);
          _self.shareDataViewer = shareDataViewer;
          // 确保表单已初始化成功
          if (_self.getDyform().getOption() && _self.getDyform().getOption().complete === true) {
            shareDataViewer.init();
          } else {
            var ti = setInterval(function () {
              if (_self.getDyform().getOption() && _self.getDyform().getOption().complete === true) {
                shareDataViewer.init();
                window.clearInterval(ti);
              }
            }, 100);
          }
        });
      }
    },
    // 共享数据查看器初始化
    onShareDataViewerInit: function () {},
    // 保存前后事件处理
    saveEventHandler: function (event_type, args) {
      var _self = this;
      var formDefinition = _self.getDyform().formDefinition;
      if (formDefinition.events && formDefinition.events != '') {
        var dyformEvents = eval('(' + formDefinition.events + ')');
        if (dyformEvents[event_type]) {
          appContext.eval(dyformEvents[event_type], this, args);
        }
      }
    },
    // 按钮操作后回调
    callbackAfterAction: function (func, success, originalArgs) {
      func.apply(
        this,
        [
          {
            success: success,
            args: originalArgs
          }
        ].concat(Array.prototype.slice.call(originalArgs))
      );
    },
    // 保存
    save: function () {
      var _self = this;
      _self.opinionToWorkData();
      var workData = _self.workFlow.getWorkData();
      // 操作动作及类型
      _self.setEventAction('保存', 'Save', workData);
      // 获取表单数据
      _self.getDyformData(function (dyFormData) {
        workData.dyFormData = dyFormData;
        // 保存前事件执行
        _self.saveEventHandler('beforeSave_event', {
          dyFormData: dyFormData
        });
        var success = function () {
          // 还原签署意见
          _self.restoreOpinion();
          if ($.isFunction(_self.onAfterSave)) {
            _self.callbackAfterAction(_self.onAfterSave, true, arguments);
          } else {
            _self.onSaveSuccess.apply(_self, arguments);
          }
          _self.saveEventHandler('afterSave_event', {
            dyFormData: dyFormData,
            result: arguments[0]
          });
          $('.btn', '.widget-toolbar').attr('disabled', 'disabled');
        };
        var failure = function () {
          if ($.isFunction(_self.onAfterSave)) {
            _self.callbackAfterAction(_self.onAfterSave, false, arguments);
          } else {
            _self.onSaveFailure.apply(_self, arguments);
          }
        };
        // 存储签署意见
        _self.storeOpinion();
        _self.workFlow.save(success, failure);
      });
    },
    // 提交
    submit: function () {
      var _self = this;
      // 签署意见
      if (_self.openToSignIfRequiredSubmit.apply(_self, arguments)) {
        return;
      }
      _self.opinionToWorkData();
      var workData = _self.workFlow.getWorkData();
      // 操作动作及类型
      _self.setEventAction('提交', 'Submit', workData);
      // 获取表单数据
      _self.getDyformData(function (dyformData) {
        workData.dyFormData = dyformData;
        // 是否提交后进行套打
        workData.printAfterSubmit = _self.isPrintAfterSubmit();
        var success = function () {
          if ($.isFunction(_self.onAfterSubmit)) {
            _self.callbackAfterAction(_self.onAfterSubmit, true, arguments);
          } else {
            _self.onSubmitSuccess.apply(_self, arguments);
          }
          $('.btn', '.widget-toolbar').attr('disabled', 'disabled');
        };
        var failure = function () {
          if ($.isFunction(_self.onAfterSubmit)) {
            _self.callbackAfterAction(_self.onAfterSubmit, false, arguments);
          } else {
            _self.onSubmitFailure.apply(_self, arguments);
          }
        };
        try {
          // 会签待办提交不进行表单验证
          if (workData.todoType === 2 || _self.validateDyformData() === true) {
            // 签署意见校验
            var isOpinionRuleCheck = _self._isOpinionRuleCheck(workData, sceneEnum.SUBMIT);
            if (!isOpinionRuleCheck) {
              return;
            }
            // 收集自定义按钮操作的信息
            _self.collectIfUseCustomDynamicButton();
            _self.workFlow.submit(success, failure);
            var storage = commons.StorageUtils.getStorage();
            storage.removeItem('opinionvalue');
          }
        } catch (e) {
          appModal.error('表单数据验证出错' + e + '，无法提交数据！');
          throw e;
        }
      });
    },
    // 设置事件按钮名称
    setEventAction: function (action, actionType, workData) {
      var _self = this,
        target;
      workData.action = action;
      workData.actionType = actionType;
      if (_self.currentEvent && (target = _self.currentEvent.target)) {
        var btnText = $.fn.text ? $(target).text() : target.innerText;
        var btnText = StringUtils.trim(btnText);
        if (StringUtils.isNotBlank(btnText)) {
          workData.action = btnText;
        }
      }
    },
    // 自定义动态按钮提交
    collectIfUseCustomDynamicButton: function () {
      var _self = this;
      if (_self.currentEvent == null) {
        return;
      }
      var workData = _self.workFlow.getWorkData();
      var $btn = $(_self.currentEvent.target);
      var btnId = $btn.attr('btnId');
      var btn_submit = _self.submitButtonCode;
      // 处理自定义动态按钮
      var customDynamicButton = {};
      if (btnId !== btn_submit) {
        customDynamicButton.id = btnId;
        customDynamicButton.code = $btn.attr('name');
        customDynamicButton.newCode = btnId;
      } else {
        customDynamicButton = null;
      }
      workData.customDynamicButton = customDynamicButton;
    },
    // 自定义动态按钮退回
    collectIfUseCustomRollbackDynamicButton: function () {
      var _self = this;
      if (_self.currentEvent == null) {
        return;
      }
      var workData = _self.workFlow.getWorkData();
      var $btn = $(_self.currentEvent.target);
      var btnId = $btn.attr('btnId');
      var btn_rollback = _self.rollbackButtonCode;
      // 处理自定义动态按钮
      var customDynamicButton = {};
      if (btnId !== btn_rollback) {
        customDynamicButton.id = btnId;
        customDynamicButton.code = $btn.attr('name');
        customDynamicButton.newCode = btnId;
      } else {
        customDynamicButton = null;
      }
      workData.customDynamicButton = customDynamicButton;
    },
    // 自定义动态按钮跳转
    collectIfUseCustomGotoTaskDynamicButton: function () {
      var _self = this;
      if (_self.currentEvent == null) {
        return;
      }
      var workData = _self.workFlow.getWorkData();
      var $btn = $(_self.currentEvent.target);
      var btnId = $btn.attr('btnId');
      var btn_gotoTask = _self.gotoTaskButtonCode;
      // 处理自定义动态按钮
      var customDynamicButton = {};
      if (btnId !== btn_gotoTask) {
        customDynamicButton.id = btnId;
        customDynamicButton.code = $btn.attr('name');
        customDynamicButton.newCode = btnId;
      } else {
        customDynamicButton = null;
      }
      workData.customDynamicButton = customDynamicButton;
    },
    // 退回
    rollback: function () {
      var _self = this;
      // 签署意见
      if (_self.openToSignIfRequiredRollback.apply(_self, arguments)) {
        return;
      }
      _self.opinionToWorkData();
      var workData = _self.workFlow.getWorkData();
      // 签署意见校验
      var isOpinionRuleCheck = _self._isOpinionRuleCheck(workData, sceneEnum.RETURN);
      if (!isOpinionRuleCheck) {
        return;
      }
      // 操作动作及类型
      _self.setEventAction('退回', 'Rollback', workData);
      // 获取表单数据
      _self.getDyformData(function (dyformData) {
        workData.dyFormData = dyformData;
        workData.rollbackToPreTask = false;
        var success = function () {
          if ($.isFunction(_self.onAfterRollback)) {
            _self.callbackAfterAction(_self.onAfterRollback, true, arguments);
          } else {
            _self.onRollbackSuccess.apply(_self, arguments);
          }
          $('.btn', '.widget-toolbar').attr('disabled', 'disabled');
        };
        var failure = function () {
          if ($.isFunction(_self.onAfterRollback)) {
            _self.callbackAfterAction(_self.onAfterRollback, false, arguments);
          } else {
            _self.onRollbackFailure.apply(_self, arguments);
          }
        };
        // 收集自定义按钮操作的信息
        _self.collectIfUseCustomRollbackDynamicButton();
        var storage = commons.StorageUtils.getStorage();
        storage.removeItem('opinionvalue');
        _self.workFlow.rollback(success, failure);
      });
    },
    // 直接退回
    directRollback: function () {
      var _self = this;
      // 签署意见
      if (_self.openToSignIfRequiredRollback.apply(_self, arguments)) {
        return;
      }
      _self.opinionToWorkData();
      var workData = _self.workFlow.getWorkData();
      // 签署意见校验
      var isOpinionRuleCheck = _self._isOpinionRuleCheck(workData, sceneEnum.RETURN);
      if (!isOpinionRuleCheck) {
        return;
      }
      // 操作动作及类型
      _self.setEventAction('直接退回', 'DirectRollback', workData);
      // 获取表单数据
      _self.getDyformData(function (dyFormData) {
        workData.dyFormData = dyFormData;
        workData.rollbackToPreTask = true;
        var success = function () {
          if ($.isFunction(_self.onAfterDirectRollback)) {
            _self.callbackAfterAction(_self.onAfterDirectRollback, true, arguments);
          } else {
            _self.onDirectRollbackSuccess.apply(_self, arguments);
          }
          $('.btn', '.widget-toolbar').attr('disabled', 'disabled');
        };
        var failure = function () {
          if ($.isFunction(_self.onAfterDirectRollback)) {
            _self.callbackAfterAction(_self.onAfterDirectRollback, false, arguments);
          } else {
            _self.onDirectRollbackFailure.apply(_self, arguments);
          }
        };
        _self.workFlow.rollback(success, failure);
      });
    },
    // 退回主流程
    rollbackToMainFlow: function () {
      var _self = this;
      // 签署意见
      if (_self.openToSignIfRequiredRollback.apply(_self, arguments)) {
        return;
      }
      _self.opinionToWorkData();
      var workData = _self.workFlow.getWorkData();
      // 操作动作及类型
      _self.setEventAction('退回主流程', 'RollbackToMainFlow', workData);
      // 获取表单数据
      _self.getDyformData(function (dyFormData) {
        workData.dyFormData = dyFormData;
        workData.rollbackToPreTask = true;
        var success = function () {
          if ($.isFunction(_self.onAfterRollbackToMainFlow)) {
            _self.callbackAfterAction(_self.onAfterRollbackToMainFlow, true, arguments);
          } else {
            _self.onRollbackToMainFlowSuccess.apply(_self, arguments);
          }
          $('.btn', '.widget-toolbar').attr('disabled', 'disabled');
        };
        var failure = function () {
          if ($.isFunction(_self.onAfterRollbackToMainFlow)) {
            _self.callbackAfterAction(_self.onAfterRollbackToMainFlow, false, arguments);
          } else {
            _self.onRollbackToMainFlowFailure.apply(_self, arguments);
          }
        };
        _self.workFlow.rollbackToMainFlow(success, failure);
      });
    },
    // 查看主流程
    viewTheMainFlow: function () {
      var _self = this;
      var subTaskData = _self.shareDataViewer.subTaskData;
      var url = ctx + '/workflow/work/v53/view/work?taskInstUuid={0}&flowInstUuid={1}&viewTheMainFlow=true';
      var flowInstUuid = subTaskData.parentFlowInstUuid;
      var taskInstUuid = subTaskData.parentTaskInstUuid;
      var sb = new StringBuilder();
      sb.appendFormat(url, taskInstUuid, flowInstUuid);
      appContext.openWindow(sb.toString());
    },
    // 打印表单
    PrintForm: function () {
      var _self = this;
      console.log(_self);
      _self.dyform.$dyform.find('textarea').each(function () {
        if (this.value) {
          this.innerHTML = this.value;
        }
        console.log(this);
      });
      _self.dyform.$dyform.printArea();
    },
    // 撤回
    cancel: function () {
      var _self = this;
      // 签署意见
      if (_self.openToSignIfRequiredCancel.apply(_self, arguments)) {
        return;
      }
      _self.opinionToWorkData();
      var workData = _self.workFlow.getWorkData();
      // 操作动作及类型
      _self.setEventAction('撤回', 'Cancel', workData);
      // 获取表单数据
      _self.getDyformData(function (dyFormData) {
        workData.dyFormData = dyFormData;
        var success = function () {
          if ($.isFunction(_self.onAfterCancel)) {
            _self.callbackAfterAction(_self.onAfterCancel, true, arguments);
          } else {
            _self.onCancelSuccess.apply(_self, arguments);
          }
          $('.btn', '.widget-toolbar').attr('disabled', 'disabled');
        };
        var failure = function () {
          if ($.isFunction(_self.onAfterCancel)) {
            _self.callbackAfterAction(_self.onAfterCancel, false, arguments);
          } else {
            _self.onCancelFailure.apply(_self, arguments);
          }
        };
        // 撤回权限检测
        if (_self.isAllowedCancel(workData.taskInstUuid) === false) {
          if (_self.isAllowedCancelCheckResult && _self.isAllowedCancelCheckResult.code == -5002) {
            appModal.error(_self.isAllowedCancelCheckResult.msg);
            delete _self.isAllowedCancelCheckResult;
          } else {
            appModal.error("撤回失败！<span style='color:#666;'>下一环节已办理，无法撤回！</span>");
          }
          return;
        }
        _self.workFlow.cancel(success, failure);
      });
    },
    // 转办
    transfer: function () {
      var _self = this;
      // 签署意见
      if (_self.openToSignIfRequiredTransfer.apply(_self, arguments)) {
        return;
      }
      _self.opinionToWorkData();
      var workData = _self.workFlow.getWorkData();
      // 签署意见校验
      var isOpinionRuleCheck = _self._isOpinionRuleCheck(workData, sceneEnum.TURN_TO);
      if (!isOpinionRuleCheck) {
        return;
      }
      // 操作动作及类型
      _self.setEventAction('转办', 'Transfer', workData);
      // 获取表单数据
      _self.getDyformData(function (dyFormData) {
        workData.dyFormData = dyFormData;
        var success = function () {
          if ($.isFunction(_self.onAfterTransfer)) {
            _self.callbackAfterAction(_self.onAfterTransfer, true, arguments);
          } else {
            _self.onTransferSuccess.apply(_self, arguments);
          }
          $('.btn', '.widget-toolbar').attr('disabled', 'disabled');
        };
        var failure = function () {
          if ($.isFunction(_self.onAfterTransfer)) {
            _self.callbackAfterAction(_self.onAfterTransfer, false, arguments);
          } else {
            _self.onTransferFailure.apply(_self, arguments);
          }
        };
        _self.workFlow.transfer([], success, failure);
        /**
        $.unit2.open({
          title: '选择转办人员<span class="taskFontWeight">【' + workData.taskName + '】</span>',
          type: 'all',
          valueField: '',
          labelField: '',
          callback: function (values, labels) {
            if (values && values.length > 0) {
              var transferUserIds = values;
              _self.transferUsers = labels;
              _self.workFlow.transferUsers = labels;
              _self.workFlow.transfer(transferUserIds, success, failure);
            } else {
              appModal.warning('转办人员不能为空！');
            }
          }
        });
        */
      });
    },
    // 查看阅读记录
    viewReadLog: function () {
      var _self = this;

      var options = {
        title: '阅读记录',
        message: "<div class='view-read-record-content'></div>",
        dialogPosition: 'center',
        width: 840,
        height: 600,
        show: function () {
          $.ajax({
            type: 'GET',
            url: ctx + '/workflow/work/v53/view/viewReadLog?taskInstUuid=' + _self.getWorkData().taskInstUuid,
            dataType: 'json',
            success: function (data) {
              // 获取已阅人员字符串
              var getReadString = function (records) {
                if (records == null) {
                  return '';
                }
                var sb = new StringBuilder();
                $.each(records, function (i, record) {
                  sb.append('<li>');
                  sb.append("<div class='record-item'>");
                  sb.append("<div class='gray-11'>");
                  sb.append(record.userName);
                  sb.append('</div>');
                  sb.append("<div class='text-right gray-10'>");
                  sb.append(getReadTimeString(record.readTime));
                  sb.append('</div>');
                  sb.append('</div>');
                  sb.append('</li>');
                });
                return sb.toString();
              };

              var getReadTimeString = function (readTime) {
                if (!readTime) {
                  return '';
                }
                var timestring = new Date(readTime);
                var now = new Date();
                if (now.getFullYear() != timestring.getFullYear()) {
                  readTime = timestring.format('yyyy-MM-dd HH:mm'); //往年查阅的，显示年月日时分
                } else {
                  readTime = timestring.format('MM-dd HH:mm'); //当年查阅的，仅显示月日时分
                  if (now.format('MM-dd') == timestring.format('MM-dd')) {
                    readTime = timestring.format('HH:mm'); //当天查阅的，仅显示时分
                  }
                }
                return readTime;
              };

              var tpl = '<div class="record-box"><div class="record-header">已阅人员（{0}人）</div><ul>{1}</ul></div>';
              tpl += '<div class="record-box"><div class="record-header">未阅人员（{2}人）</div><ul>{3}</ul></div>';

              var sb = new commons.StringBuilder();
              var readString = getReadString(data.readUser);
              var unreadRecords = _.map(data.unReadUser, function (item) {
                return {
                  userName: item
                };
              });
              var unreadString = getReadString(unreadRecords);
              sb.appendFormat(tpl, data.readUser.length, readString, unreadRecords.length, unreadString);
              $('.view-read-record-content').html(sb.toString());

              $('.view-read-record-content').parent().css({
                'max-height': '500px'
              });
            },
            error: function (jqXHR, statusText, error) {}
          });
        },
        buttons: [
          {
            className: 'well-btn w-btn-light w-line-btn',
            label: '取消',
            callback: $.noop
          }
        ]
      };
      appModal.dialog(options);
    },

    // 会签
    counterSign: function () {
      var _self = this;
      // 签署意见
      if (_self.openToSignIfRequiredCounterSign.apply(_self, arguments)) {
        return;
      }
      _self.opinionToWorkData();
      var workData = _self.workFlow.getWorkData();
      // 签署意见校验
      var isOpinionRuleCheck = _self._isOpinionRuleCheck(workData, sceneEnum.COUNTERSIGN);
      if (!isOpinionRuleCheck) {
        return;
      }
      // 操作动作及类型
      _self.setEventAction('会签', 'CounterSign', workData);
      // 获取表单数据
      _self.getDyformData(function (dyFormData) {
        workData.dyFormData = dyFormData;
        var success = function () {
          if ($.isFunction(_self.onAfterCounterSign)) {
            _self.callbackAfterAction(_self.onAfterCounterSign, true, arguments);
          } else {
            _self.onCounterSignSuccess.apply(_self, arguments);
          }
          $('.btn', '.widget-toolbar').attr('disabled', 'disabled');
        };
        var failure = function () {
          if ($.isFunction(_self.onAfterCounterSign)) {
            _self.callbackAfterAction(_self.onAfterCounterSign, false, arguments);
          } else {
            _self.onCounterSignFailure.apply(_self, arguments);
          }
        };
        $.unit2.open({
          title: '选择' + workData.action + '人员<span class="taskFontWeight">【' + workData.taskName + '】</span>',
          type: 'all',
          valueField: '',
          labelField: '',
          callback: function (values, labels) {
            if (values && values.length > 0) {
              var counterSignUserIds = values;
              _self.signUsers = labels;
              _self.workFlow.signUsers = labels;
              _self.workFlow.counterSign(counterSignUserIds, success, failure);
            } else {
              appModal.warning(workData.action + '人员不能为空！');
            }
          }
        });
      });
    },
    // 抄送
    copyTo: function () {
      var _self = this;
      var workData = _self.workFlow.getWorkData();
      var taskInstUuids = [];
      taskInstUuids.push(workData.taskInstUuid);
      var success = function () {
        if ($.isFunction(_self.onAfterCopyTo)) {
          _self.callbackAfterAction(_self.onAfterCopyTo, true, arguments);
        } else {
          _self.onCopyToSuccess.apply(_self, arguments);
        }
      };
      var failure = function () {
        if ($.isFunction(_self.onAfterCopyTo)) {
          _self.callbackAfterAction(_self.onAfterCopyTo, false, arguments);
        } else {
          _self.onCopyToFailure.apply(_self, arguments);
        }
      };
      $.unit2.open({
        title: '选择抄送人员<span class="taskFontWeight">【' + workData.taskName + '】</span>',
        type: 'all',
        valueField: '',
        labelField: '',
        callback: function (values, labels) {
          if (values && values.length > 0) {
            var copyToUserIds = values;
            _self.copyToUsers = labels;
            _self.workFlow.copyTo(taskInstUuids, copyToUserIds, success, failure);
          } else {
            appModal.warning('抄送人员不能为空！');
          }
        }
      });
    },
    // 关注
    attention: function () {
      var _self = this;
      var workData = _self.workFlow.getWorkData();
      var taskInstUuids = [];
      taskInstUuids.push(workData.taskInstUuid);
      var success = function () {
        if ($.isFunction(_self.onAfterAttention)) {
          _self.callbackAfterAction(_self.onAfterAttention, true, arguments);
        } else {
          _self.onAttentionSuccess.apply(_self, arguments);
        }
      };
      var failure = function () {
        if ($.isFunction(_self.onAfterAttention)) {
          _self.callbackAfterAction(_self.onAfterAttention, false, arguments);
        } else {
          _self.onAttentionFailure.apply(_self, arguments);
        }
      };
      _self.workFlow.attention(taskInstUuids, success, failure);
    },
    // 取消关注
    unfollow: function () {
      var _self = this;
      var workData = _self.workFlow.getWorkData();
      var taskInstUuids = [];
      taskInstUuids.push(workData.taskInstUuid);
      var success = function () {
        if ($.isFunction(_self.onAfterUnfollow)) {
          _self.callbackAfterAction(_self.onAfterUnfollow, true, arguments);
        } else {
          _self.onUnfollowSuccess.apply(_self, arguments);
        }
      };
      var failure = function () {
        if ($.isFunction(_self.onAfterUnfollow)) {
          _self.callbackAfterAction(_self.onAfterUnfollow, false, arguments);
        } else {
          _self.onUnfollowFailure.apply(_self, arguments);
        }
      };
      _self.workFlow.unfollow(taskInstUuids, success, failure);
    },
    // 套打
    print: function () {
      var _self = this;
      var workData = _self.workFlow.getWorkData();
      var templateTreeList;
      var printSuccess = function (result) {
        if ($.isFunction(_self.onAfterPrint)) {
          _self.callbackAfterAction(_self.onAfterPrint, true, arguments);
        } else {
          var logicFileInfo = result.data;
          if (_self.isTodo()) {
            // setTimeout(function () {        // 2020/11/26 注释掉
            //     // 流程-套打-打结果设置到表单的附件字段
            //     var pr2fs = SystemParams.getValue("workflow.work.printresult.to.filefields.enable", "true");
            //     if (pr2fs == "true") {
            //         _self.setPrintResult2DyformData(logicFileInfo);
            //     }
            // }, 1);
          }
          FileDownloadUtils.downloadMongoFile({
            fileId: logicFileInfo.fileID
          });
        }
      };
      var printFailure = function () {
        if ($.isFunction(_self.onAfterPrint)) {
          _self.callbackAfterAction(_self.onAfterPrint, false, arguments);
        } else {
          _self.onPrintFailure.apply(_self, arguments);
        }
      };
      var success = function (result) {
        var templates = result.data;
        var ifOpendialog = true;
        templateTreeList = templates;
        if (templates.length === 0) {
          appModal.info('流程没有配置套打模板！');
        } else if (templates.length === 1) {
          var cateTepList = getCateTemplate(templates) || [];
          if (cateTepList.length == 1) {
            ifOpendialog = false;
          }
          if (!ifOpendialog) {
            appContext.require(['printtemplateUtils'], function (Printtemplate) {
              Printtemplate.showLangsDialog({
                printtemplateId: cateTepList[0].data.id,
                printtemplateUuid: cateTepList[0].data.uuid,
                printtemplateLang: cateTepList[0].lang || '',
                printCallback: function (printtemplateLang, printtemplate) {
                  _self.workFlow.print(cateTepList[0].data.id, cateTepList[0].data.uuid, printtemplateLang, printSuccess, printFailure);
                }
              });
            });
          } else {
            setDialogOption(templates);
          }
        } else if (ifOpendialog) {
          setDialogOption(templates);
        }
      };
      var failure = function () {
        appModal.error('获取套打模板报错！');
      };
      var ttList = []; // 存放模板集合
      var getCateTemplate = function (list) {
        for (var i = 0; i < list.length; i++) {
          if (list[i].type === 'PrintTemplate') {
            ttList.push(list[i]);
          }
          if (list[i].children.length > 0) {
            getCateTemplate(list[i].children);
          }
        }
        console.log(ttList);
        return ttList;
      };
      var setDialogOption = function (templates) {
        // 多个套打模板，弹框选择打印
        var message =
          "<div class='form-widget choose-print-template-box'>" +
          "<div class='input-box'>" +
          "<input id='fullFlowName' type='text' value='' placeholder='搜索' />" +
          "<i id='searchFlowCategory' class='iconfont icon-ptkj-sousuochaxun'></i>" +
          "<i id='deleteFlowCategory' class='iconfont icon-ptkj-dacha-xiao'></i>" +
          '</div>' +
          "<div class='choose-print-template msg-category-content print-category-content' id='choose_print_template' style='padding-top:0;'><div class='msg-category-list' style='height:304px;margin-top:0;'><ul id='flow_category_tree'>";
        var lis = '';
        lis += buildHtml(templates, -1);
        message +=
          lis +
          '</ul></div>' +
          "<div class='form_operate'><button type='button' id='ID_LExpandAll' class='fl well-btn w-btn-line' style='background-color:transparent;'><i class='icon iconfont icon-ptkj-zhankai'></i>展开</button><button type='button' id='ID_LCollapseAll' class='fl well-btn w-btn-line' style='background-color:transparent;' value='折叠'><i class='icon iconfont icon-ptkj-zhedie'></i>折叠</button></div>" +
          '</div></div>';
        var options = {
          title: '选择套打模板',
          size: 'middle',
          message: message,
          shown: function () {
            // var data = $.map(templates, function (object, index) {
            //   return {
            //     id: object.id,
            //     text: object.name
            //   };
            // });
            // var formBuilder = require('formBuilder');
            // var $container = $('.choose-print-template');
            // var chooseType = 'radio';
            // formBuilder.buildRadio({
            //   container: $container,
            //   items: data,
            //   name: 'choosePrintTemplate',
            //   display: 'choosePrintTemplateName',
            //   labelColSpan: '0',
            //   controlColSpan: '12'
            // });
            // $('.radio-inline').css({
            //   display: 'block'
            // });

            /* 绑定事件 */
            var $container = $('.choose-print-template-box');
            // 类型下模板收缩/展开
            $('#choose_print_template', $container)
              .off()
              .on('click', '.hasList', function () {
                var liCode = $(this).closest('li').data('uuid');
                if ($(this).hasClass('icon-ptkj-shixinjiantou-you')) {
                  $(this).removeClass('icon-ptkj-shixinjiantou-you').addClass('icon-ptkj-shixinjiantou-xia');
                  $('.children-' + liCode, $container).show();
                } else {
                  $(this).removeClass('icon-ptkj-shixinjiantou-xia').addClass('icon-ptkj-shixinjiantou-you');
                  $('.children-' + liCode, $container).hide();
                }
                // 每次点击展开都resize一下滚动条
                $('.msg-category-list', $container).getNiceScroll().resize();
              });
            // 全部展开
            $('#ID_LExpandAll', $container)
              .off('click')
              .on('click', function () {
                $('.hasList', $container).each(function () {
                  var liCode = $(this).closest('li').data('uuid');
                  $(this).removeClass('icon-ptkj-shixinjiantou-you').addClass('icon-ptkj-shixinjiantou-xia');
                  $('.children-' + liCode, $container).show();
                });
                $('.msg-category-list', $container).getNiceScroll().resize();
              });
            // 全部折叠
            $('#ID_LCollapseAll', $container)
              .off('click')
              .on('click', function () {
                $('.hasList', $container).each(function () {
                  var liCode = $(this).closest('li').data('uuid');
                  $(this).removeClass('icon-ptkj-shixinjiantou-xia').addClass('icon-ptkj-shixinjiantou-you');
                  $('.children-' + liCode, $container).hide();
                });
              });
            // 滚动条
            setTimeout(function () {
              $('.msg-category-list', $container).niceScroll({
                height: '304',
                oneaxismousemode: false,
                cursorcolor: '#ccc',
                cursorwidth: '8px'
              });
            }, 200);
            // 查询模板
            $('#searchFlowCategory', $container)
              .off()
              .on('click', function () {
                var val = $('#fullFlowName', $container).val();
                getList(val, $container);
              });
            // 输入按enter键查询
            $('#fullFlowName', $container)
              .off()
              .on('keyup', function () {
                if ($(this).val() != '') {
                  $('#deleteFlowCategory', $container).show();
                } else {
                  $('#deleteFlowCategory', $container).hide();
                }
              })
              .on('keypress', function (e) {
                if (e.keyCode == 13) {
                  getList($(this).val(), $container);
                }
              });
            // 去掉查询
            $('#deleteFlowCategory', $container)
              .off()
              .on('click', function () {
                $('#fullFlowName', $container).val('');
                $(this).hide();
                getList('', $container);
              });

            // 没有模板，隐藏 展开/折叠
            if ($('.hasList', '#choose_print_template').length == 0) {
              $('.form_operate', $container).hide();
            }
            if ($('#flow_category_tree li', $container).length > 0) {
              // 一屏放得下 隐藏搜索框
              setTimeout(function () {
                var lisHeight = 0;
                $('#flow_category_tree li', $container).each(function () {
                  lisHeight += $(this).outerHeight();
                });
                console.log(lisHeight);
                if (lisHeight >= 304) {
                  $('.input-box', '.choose-print-template-box').show();
                } else {
                  $('.input-box', '.choose-print-template-box').hide();
                }
              }, 100);
            }

            // 默认只展开第一级分类模板
            var hasListArray = $('.hasList', '#choose_print_template');
            // console.log(hasListArray);
            hasListArray.each(function (index, item) {
              if (index > 0) {
                $(item).trigger('click');
              }
            });

            /* 绑定事件结束 */
          },
          buttons: {
            confirm: {
              label: '确定',
              className: 'btn-primary',
              callback: function () {
                var $container = $('.choose-print-template');
                var $checked = $('input[name=choosePrintTemplate]:checked', $container);
                var templateId = $checked.val(),
                  templateUuid,
                  templateLang;
                if (StringUtils.isBlank(templateId)) {
                  appModal.error('请选择套打模板！');
                  return false;
                }
                var allprintTemplateList = [];

                function getPrintTemplateList(list) {
                  for (var i = 0; i < list.length; i++) {
                    if (list[i].type === 'PrintTemplate') {
                      allprintTemplateList.push({
                        uuid: list[i].data.uuid,
                        id: list[i].data.id,
                        lang: ''
                      });
                    } else if (list[i].children.length > 0) {
                      getPrintTemplateList(list[i].children);
                    }
                  }
                }

                getPrintTemplateList(templates);

                for (var i = 0; i < allprintTemplateList.length; i++) {
                  if (allprintTemplateList[i].id === templateId) {
                    templateUuid = allprintTemplateList[i].uuid;
                    templateLang = allprintTemplateList[i].lang;
                    break;
                  }
                }
                appContext.require(['printtemplateUtils'], function (Printtemplate) {
                  Printtemplate.showLangsDialog({
                    printtemplateId: templateId,
                    printtemplateUuid: templateUuid,
                    printtemplateLang: templateLang,
                    printCallback: function (printtemplateLang, printtemplate) {
                      _self.workFlow.print(templateId, templateUuid, printtemplateLang, printSuccess, printFailure);
                    }
                  });
                });
                return true;
              }
            },
            cancel: {
              label: '取消',
              className: 'btn-default'
            }
          }
        };
        appModal.dialog(options);
      };
      var getList = function (val, $container) {
        val = val ? val : '';

        $('#flow_category_tree', $container).detach();
        $('.form_operate', $container).hide();

        var templateTreeListCopy = JSON.parse(JSON.stringify(templateTreeList));
        var templateTreeListTemp = {
          children: templateTreeListCopy
        };
        recTemplateTreeFilterName(val, templateTreeListTemp);
        $('.msg-category-list', $container).append('<ul id="flow_category_tree">' + buildHtml(templateTreeListTemp.children, -1) + '</ul>');

        if ($('.hasList', '#choose_print_template').length == 0) {
          $('.form_operate', $container).hide();
        } else {
          $('.form_operate', $container).show();
        }
      };
      var recTemplateTreeFilterName = function (searchValue, templateTreeFilterName) {
        var result = false;
        if (templateTreeFilterName.name && templateTreeFilterName.name.indexOf(searchValue) > -1) {
          return true;
        }
        if (templateTreeFilterName.children && templateTreeFilterName.children.length > 0) {
          var rmTreeNodeList = [];
          for (var i = 0; i < templateTreeFilterName.children.length; i++) {
            var child = templateTreeFilterName.children[i];
            if (recTemplateTreeFilterName(searchValue, child)) {
              result = true;
            } else {
              rmTreeNodeList.push(child);
            }
          }
          for (var i = 0; i < rmTreeNodeList.length; i++) {
            var rmTreeNodeListElement = rmTreeNodeList[i];
            var indexOf = templateTreeFilterName.children.indexOf(rmTreeNodeListElement);
            if (indexOf > -1) {
              templateTreeFilterName.children.splice(indexOf, 1);
            }
          }
        }
        return result;
      };
      var buildHtml = function (serviceData, count, code) {
        var html = '';
        count++;
        var value = $('#fullFlowName', '.choose-print-template-box').val() || '';
        for (var i = 0; i < serviceData.length; i++) {
          var data = serviceData[i].data;
          var icon = data.icon ? data.icon : 'iconfont icon-ptkj-fenlei2';
          var background = data.iconColor ? data.iconColor : '#64B3EA';
          var remark = data.remark || '';
          var title = '';
          var className = 'msg-category-item-' + count;
          var $li = '';
          if (count == 0) {
            code = serviceData[i].id;
            if (serviceData[i].type == 'PrintTemplate') {
              $li =
                "<li class='msg-category-item " +
                className +
                "' data-code='" +
                data.code +
                "' data-uuid='" +
                data.uuid +
                "' title='" +
                title +
                "'>" +
                "<span class='slide_icon'></span>" +
                "<input type='radio' name='choosePrintTemplate' id='" +
                data.id +
                "' value='" +
                data.id +
                "' />" +
                "<label for='" +
                data.id +
                "' style='margin-right:0;'></label>" +
                setKeyRed(serviceData[i].name, value);
            } else {
              $li =
                "<li class='msg-category-item " +
                className +
                "' data-code='" +
                data.code +
                "' data-uuid='" +
                serviceData[i].id +
                "' title='" +
                title +
                "'>" +
                "<span class='slide_icon'><i class='iconfont icon-folders hasList icon-ptkj-shixinjiantou-xia'></i></span>" +
                "<span class='button ico_close'" +
                '></span>' +
                setKeyRed(serviceData[i].name, value);
            }
          } else {
            if (serviceData[i].type == 'PrintTemplateCategory') {
              $li =
                "<li class='msg-category-item " +
                className +
                ' children-' +
                code +
                "' data-code='" +
                data.code +
                "' data-uuid='" +
                serviceData[i].id +
                "' title='" +
                title +
                "'>" +
                "<span class='button ico_close'" +
                '></span>' +
                setKeyRed(serviceData[i].name, value);
            } else {
              $li =
                "<li class='msg-category-item " +
                className +
                ' children-' +
                code +
                "' data-code='" +
                data.code +
                "' data-uuid='" +
                data.uuid +
                "' title='" +
                title +
                "'>" +
                "<input type='radio' name='choosePrintTemplate' id='" +
                data.id +
                "' value='" +
                data.id +
                "' />" +
                "<label for='" +
                data.id +
                "' style='margin-right:0;'></label>" +
                setKeyRed(serviceData[i].name, value);
            }
          }

          html += $li;
          if (serviceData[i].children && serviceData[i].children.length > 0) {
            html += buildHtml(serviceData[i].children, count, code);
          }
        }
        return html;
      };

      var setKeyRed = function (name, value) {
        var html = '';
        var position, subEnd;
        if (value === '') {
          html = "<span class='msg-category-text'>" + name + '</span>' + '</li>';
        } else {
          if (name.indexOf(value) == -1) {
            html = "<span class='msg-category-text'>" + name + '</span>' + '</li>';
          } else {
            position = name.indexOf(value);
            subEnd = position + value.length;
            html =
              "<span class='msg-category-text'>" +
              name.substring(0, position) +
              "<span style='color:red;margin-right:0;line-height:22px;width:auto;'>" +
              name.substring(position, subEnd) +
              '</span>' +
              name.substring(subEnd, name.length) +
              '</span>' +
              '</li>';
          }
        }
        return html;
      };
      _self.workFlow.getPrintTemplates(success, failure);
    },

    // 将打结果设置到表单的附件字段
    setPrintResult2DyformData: function (logicFileInfo) {
      var _self = this;
      var success = function (result) {
        var fileFields = result.data;
        if (fileFields.length == 0) {
          return;
        }
        // 选择附件字段
        var message = "<div class='choose-file-field'></div>";
        var options = {
          title: '打印结果设置到表单——选择附件字段',
          size: 'middle',
          message: message,
          shown: function () {
            var data = $.map(fileFields, function (object, index) {
              return {
                id: object.name,
                text: object.displayName
              };
            });
            var formBuilder = require('formBuilder');
            var $container = $('.choose-file-field');
            var chooseType = 'radio';
            formBuilder.buildCheckbox({
              container: $container,
              items: data,
              name: 'chooseFileField',
              display: 'chooseFileFieldName',
              labelColSpan: '0',
              controlColSpan: '12'
            });
          },
          buttons: {
            confirm: {
              label: '确定',
              className: 'btn-primary',
              callback: function () {
                var $container = $('.choose-file-field');
                var $checkboxes = $('input[name=chooseFileField]:checked', $container);
                if ($checkboxes.length == 0) {
                  appModal.error('请选择表单附件字段！');
                  return false;
                }
                var dyform = _self.getDyform();
                $.each($checkboxes, function () {
                  var fileField = $(this).val();
                  var fileControl = dyform.getControl(fileField);
                  if (fileControl) {
                    $.ajax({
                      type: 'post',
                      url: '/repository/file/mongo/saveFilesByFileIds', // 上传文件的地址
                      data: {
                        fileIds: [logicFileInfo.fileID]
                      },
                      success: function (data) {
                        var dataFiles = JSON.parse(data);
                        if (dataFiles.success) {
                          var files = dataFiles.data;
                          fileControl.getValue(function (valueFiles) {
                            for (var i = 0; i < files.length; i++) {
                              var fileElement = files[i];
                              fileElement.fileName = fileElement.filename;
                              fileElement.isNew = true;
                              valueFiles.push(fileElement);
                            }
                            fileControl.setValue(valueFiles);
                          });
                        } else {
                          appModal.error(dataFiles.msg);
                        }
                      },
                      error: function (data) {
                        appModal.error(data);
                      }
                    });
                  }
                });
                return true;
              }
            },
            cancel: {
              label: '取消',
              className: 'btn-default'
            }
          }
        };
        appModal.dialog(options);
      };
      var failure = function () {};
      _self.workFlow.getDyformFileFieldDefinitions(success, failure);
    },
    // 催办
    remind: function () {
      var _self = this;
      // 签署意见
      if (_self.openToSignIfRequiredRemind.apply(_self, arguments)) {
        return;
      }
      _self.opinionToWorkData();
      var workData = _self.workFlow.getWorkData();
      var taskInstUuids = [];
      taskInstUuids.push(workData.taskInstUuid);
      var opinionLabel = workData.opinionLabel;
      var opinionValue = workData.opinionValue;
      var opinionText = workData.opinionText;
      var success = function () {
        if ($.isFunction(_self.onAfterRemind)) {
          _self.callbackAfterAction(_self.onAfterRemind, true, arguments);
        } else {
          _self.onRemindSuccess.apply(_self, arguments);
        }
      };
      var failure = function () {
        if ($.isFunction(_self.onAfterRemind)) {
          _self.callbackAfterAction(_self.onAfterRemind, false, arguments);
        } else {
          _self.onRemindFailure.apply(_self, arguments);
        }
      };
      _self.workFlow.remind(taskInstUuids, opinionLabel, opinionValue, opinionText, success, failure);
    },
    // 特送个人
    handOver: function () {
      var _self = this;
      // 检查环节锁
      if (!_self.checkTaskLock()) {
        return;
      }
      // 签署意见
      if (_self.openToSignIfRequiredHandOver.apply(_self, arguments)) {
        return;
      }
      _self.opinionToWorkData();
      var workData = _self.workFlow.getWorkData();
      // 获取表单数据
      _self.getDyformData(function (dyFormData) {
        workData.dyFormData = dyFormData;
        var success = function () {
          if ($.isFunction(_self.onAfterHandOver)) {
            _self.callbackAfterAction(_self.onAfterHandOver, true, arguments);
          } else {
            _self.onHandOverSuccess.apply(_self, arguments);
          }
        };
        var failure = function () {
          if ($.isFunction(_self.onAfterHandOver)) {
            _self.callbackAfterAction(_self.onAfterHandOver, false, arguments);
          } else {
            _self.onHandOverFailure.apply(_self, arguments);
          }
        };
        $.unit2.open({
          title: '选择特送的人员',
          type: 'all',
          valueField: '',
          labelField: '',
          callback: function (values) {
            if (values && values.length > 0) {
              var handOverUserIds = values;
              _self.workFlow.handOver(handOverUserIds, success, failure);
            } else {
              appModal.warning('请选择移交人员！');
            }
          }
        });
      });
    },
    // 检查环节锁
    checkTaskLock: function () {
      var _self = this;
      var workData = _self.workFlow.getWorkData();
      var taskLocks = null;
      JDS.restfulGet({
        url: ctx + '/api/workflow/work/listLockInfo',
        data: {
          taskInstUuid: workData.taskInstUuid
        },
        contentType: 'application/x-www-form-urlencoded',
        async: false,
        success: function (result) {
          taskLocks = result.data;
        }
      });
      if (taskLocks != null && taskLocks.length > 0) {
        var userNames = [];
        $.each(taskLocks, function (i, lock) {
          userNames.push(lock.userName);
        });
        var btnText = $.trim($(_self.currentEvent.target).text());

        var userNamesStr = '“' + userNames.join('；') + '”';
        appModal.error(userNamesStr + '正在编辑，无法进行' + btnText + '操作。');

        return false;
      }
      return true;
    },
    // 特送环节
    gotoTask: function () {
      var _self = this;
      // 检查环节锁
      if (!_self.checkTaskLock()) {
        return;
      }
      // 签署意见
      if (_self.openToSignIfRequiredGotoTask.apply(_self, arguments)) {
        return;
      }
      _self.opinionToWorkData();
      var workData = _self.workFlow.getWorkData();
      // 获取表单数据
      _self.getDyformData(function (dyFormData) {
        workData.dyFormData = dyFormData;
        var success = function () {
          if ($.isFunction(_self.onAfterGotoTask)) {
            _self.callbackAfterAction(_self.onAfterGotoTask, true, arguments);
          } else {
            _self.onGotoTaskSuccess.apply(_self, arguments);
          }
        };
        var failure = function () {
          if ($.isFunction(_self.onAfterGotoTask)) {
            _self.callbackAfterAction(_self.onAfterGotoTask, false, arguments);
          } else {
            _self.onGotoTaskFailure.apply(_self, arguments);
          }
        };
        // 收集自定义按钮操作的信息
        _self.collectIfUseCustomGotoTaskDynamicButton();
        _self.workFlow.gotoTask(success, failure);
      });
    },
    // 挂起
    suspend: function () {
      var _self = this;
      var workData = _self.workFlow.getWorkData();
      var taskInstUuids = [];
      taskInstUuids.push(workData.taskInstUuid);
      var success = function () {
        if ($.isFunction(_self.onAfterSuspend)) {
          _self.callbackAfterAction(_self.onAfterSuspend, true, arguments);
        } else {
          _self.onSuspendSuccess.apply(_self, arguments);
        }
      };
      var failure = function () {
        if ($.isFunction(_self.onAfterSuspend)) {
          _self.callbackAfterAction(_self.onAfterSuspend, false, arguments);
        } else {
          _self.onSuspendFailure.apply(_self, arguments);
        }
      };
      _self.workFlow.suspend(taskInstUuids, success, failure);
    },
    // 恢复
    resume: function () {
      var _self = this;
      var workData = _self.workFlow.getWorkData();
      var taskInstUuids = [];
      taskInstUuids.push(workData.taskInstUuid);
      var success = function () {
        if ($.isFunction(_self.onAfterResume)) {
          _self.callbackAfterAction(_self.onAfterResume, true, arguments);
        } else {
          _self.onResumeSuccess.apply(_self, arguments);
        }
      };
      var failure = function () {
        if ($.isFunction(_self.onAfterResume)) {
          _self.callbackAfterAction(_self.onAfterResume, false, arguments);
        } else {
          _self.onResumeFailure.apply(_self, arguments);
        }
      };
      _self.workFlow.resume(taskInstUuids, success, failure);
    },
    // 删除
    remove: function () {
      var _self = this;
      var workData = _self.workFlow.getWorkData();
      var taskInstUuids = [];
      taskInstUuids.push(workData.taskInstUuid);
      var success = function () {
        if ($.isFunction(_self.onAfterRemove)) {
          _self.callbackAfterAction(_self.onAfterRemove, true, arguments);
        } else {
          _self.onRemoveSuccess.apply(_self, arguments);
        }
      };
      var failure = function () {
        if ($.isFunction(_self.onAfterRemove)) {
          _self.callbackAfterAction(_self.onAfterRemove, false, arguments);
        } else {
          _self.onRemoveFailure.apply(_self, arguments);
        }
      };
      _self.workFlow.remove(taskInstUuids, success, failure);
    },
    //创建连续签批模式组件
    createLXQPComponent: function () {
      var _self = this;
      var lxqpModule = _self.options.lxqpModule;
      var _requestCode = Browser.getQueryString('_requestCode');
      //从流程表单相关文档链接进入不显示连续签批模式
      if (Browser.getQueryString('isXGWD')) {
        _self.$element.find('#lxqpMode').remove();
        return false;
      }
      if ($.isFunction(lxqpModule)) {
        this.lxqpModule = new lxqpModule(_self);
      }
      if (window != top) {
        _self.$element.find('.widget-header.fixed').css('box-shadow', '0 1px 0 0 #E8E8E8');
      }
      var dataStoreId;
      var isTodoList;
      var sessionStorage_dataStoreParams = sessionStorage[_requestCode + '_dataStoreParams'];
      //无数据仓库参数判定为不显示连续签批按钮 或 窗口未命名
      if ((!sessionStorage_dataStoreParams && !sessionStorage['lxqp_dataStoreParams'] && !sessionStorage[window.name]) || !window.name) {
        _self.$element.find('#lxqpMode').remove();
        return;
      }
      //有数据仓库参数则判断是否待办列表进来
      var _dataStoreParams = sessionStorage_dataStoreParams || sessionStorage[window.name];
      if (_dataStoreParams) {
        sessionStorage.setItem(window.name, _dataStoreParams);
        dataStoreId = JSON.parse(_dataStoreParams).dataStoreId;
        isTodoList = _self.checkIsTodoList(dataStoreId);
        !isTodoList && _self.$element.find('#lxqpMode').remove();
      }
      if (sessionStorage.lxqpUrl && (!Browser.getQueryString('lxqpMode') || window == top)) {
        if (sessionStorage.lxqpUrl.indexOf(Browser.getQueryString('taskInstUuid')) > -1 || sessionStorage['lxqp_dataStoreParams']) {
          _self.enterLxqpMode(true);
        } else {
          location.href = sessionStorage.lxqpUrl;
        }
      }
    },
    //是否连续签批模式
    isLXQPMode: function () {
      if (sessionStorage.lxqpUrl && (!Browser.getQueryString('lxqpMode') || window == top) && !Browser.getQueryString('isXGWD')) {
        if (sessionStorage.lxqpUrl.indexOf(Browser.getQueryString('taskInstUuid')) > -1 || sessionStorage['lxqp_dataStoreParams']) {
          return true;
        }
      }
      return false;
    },
    //连续签批模式
    enterLxqpMode: function (hideConfirm) {
      var _self = this;
      var workData = _self.workFlow.getWorkData();
      var needShowSavePopup = _self.needShowSavePopup();
      if ((typeof hideConfirm === 'boolean' && hideConfirm) || !needShowSavePopup) {
        _self.lxqpModule.init(workData);
        return;
      }
      _self.saveDataPopup(function () {
        _self.lxqpModule.init(workData);
      });
    },
    saveDataPopup: function (callback) {
      var _self = this;
      var workData = _self.workFlow.getWorkData();
      //是否保存当前编辑的内容
      top.appModal.confirm('是否保存当前编辑的内容？', function (result) {
        if (result) {
          // 操作动作及类型
          _self.setEventAction('保存', 'Save', workData);
          // 获取表单数据
          _self.getDyformData(function (dyFormData) {
            workData.dyFormData = dyFormData;
            // 保存前事件执行
            _self.saveEventHandler('beforeSave_event', {
              dyFormData: dyFormData
            });
            var success = function () {
              callback();
              _self.saveEventHandler('afterSave_event', {
                dyFormData: dyFormData,
                result: arguments[0]
              });
              $('.btn', '.widget-toolbar').attr('disabled', 'disabled');
            };
            var failure = function () {
              if ($.isFunction(_self.onAfterSave)) {
                _self.callbackAfterAction(_self.onAfterSave, false, arguments);
              } else {
                _self.onSaveFailure.apply(_self, arguments);
              }
            };
            // 存储签署意见
            _self.storeOpinion();
            _self.workFlow.save(success, failure);
          });
        } else {
          callback();
        }
      });
    },
    needShowSavePopup: function () {
      var _self = this;

      // 签署意见更改
      var opinionText = _self.getOpinionEditor().opinion.text;
      if (opinionText && opinionText !== _self.initSignValue) {
        return true;
      }

      // 表单数据更改
      if (!_self.dyform) {
        return false;
      }

      var workData = _self.workFlow.getWorkData();
      var _dyFormData = workData.dyFormData;
      var updatedFormDatas;

      _self.getDyformData(function (dyFormData) {
        updatedFormDatas = dyFormData.updatedFormDatas[_dyFormData.formUuid][_dyFormData.dataUuid];
      });

      if (updatedFormDatas && updatedFormDatas.length) {
        // 字段映射字段和真实值字段不参与检查
        _self.dyform.setMappingFields(_dyFormData.formUuid);
        var mappingFields = _dyFormData.formDefinition.mappingFields;
        for (var i = 0; i < updatedFormDatas.length; i++) {
          var updateField = updatedFormDatas[i];
          if ($.inArray(updateField, mappingFields) === -1) {
            return true;
          }
        }
      }
      return false;
    },
    //进入下一条环节
    nextRecord: function (tag) {
      this.lxqpModule.parentNextRecord(tag);
    },
    //提交到相同办里人时刷新连续签批模式iframe
    refreshSameFlow: function (url) {
      this.lxqpModule.refreshSameFlow(url);
    },
    //判断流程列表是否待办列表
    checkIsTodoList: function (dataStoreId) {
      var isTodoList = sessionStorage['isTodoList' + dataStoreId];
      if (isTodoList != null) {
        return isTodoList == 'true' ? true : false;
      }
      JDS.call({
        service: 'cdDataStoreDefinitionService.getBeanById',
        data: [dataStoreId],
        async: false,
        success: function (result) {
          if (result.success) {
            if (result.data.dataInterfaceName.indexOf('WorkFlowTodoDataStore') > -1) {
              isTodoList = true;
            } else {
              isTodoList = false;
            }
          }
        }
      });
      sessionStorage['isTodoList' + dataStoreId] = isTodoList;
      return isTodoList;
    },
    // 版式文档处理
    layoutDocumentProcess: function () {
      var layoutDocumentUtils = require('layoutDocumentUtils');
      var _self = this;

      if (!require('layoutDocumentUtils').dialogNoConfigError()) {
        return false;
      }

      var dyform = _self.getDyform();

      var fileList = [];

      for (var field in dyform.formDefinition.fields) {
        var fieldControl = dyform.getControl(field);
        var fileuploadobj = fieldControl.fileuploadobj;
        if (fileuploadobj instanceof WellFileUpload && !(fileuploadobj instanceof WellFileUpload4Icon)) {
          // 列表式附件
          if (fileuploadobj && fileuploadobj.secDevBtns && fileuploadobj.secDevBtns.length) {
            fieldControl.getValue(function (files) {
              for (var index = 0; index < fileuploadobj.secDevBtns.length; index++) {
                var secDevBtn = fileuploadobj.secDevBtns[index];
                if (fileuploadobj.readOnly && fileuploadobj.editBtns.indexOf(secDevBtn.uuid) == -1) {
                  continue;
                }

                if (/*secDevBtn.code === 'look_up_btn' || */ secDevBtn.code === 'seal_file_btn') {
                  var fileExtensions = secDevBtn.fileExtensions;

                  for (var i = 0; i < files.length; i++) {
                    var fileExtensionFilter = false;
                    var file = files[i];

                    if (fileExtensions) {
                      var fileExtensionsSplit = fileExtensions.split(';');
                      var fileExtensionsSplitStr = fileExtensionsSplit.join('|');
                      var regSecDevBtnFileType = eval('/\\.(' + fileExtensionsSplitStr + ')(\\?.*)?$/');
                      ///\.(png|jpe?g|gif|bmp|tif|tiff|zip|rar|7z|rtf|dot|doc|docx|ppt|pptx|xls|xlsx|vsd|vsdx|pdf|mp3|ogg|mp4|webm|text|txt|mthml|html|htm|xml|css|js|json|sql|log|properties|yaml|yml|Zat|sh|md|java|mine|groovy|jsp)(\?.*)?/;
                      if (regSecDevBtnFileType.test(file.fileName)) {
                        fileExtensionFilter = true;
                      }
                    } else {
                      fileExtensionFilter = true;
                    }

                    if (fileExtensionFilter) {
                      if (secDevBtn.code === 'look_up_btn') {
                        // file
                        file.layoutDocumentBtnType = 'look_up_btn';
                      }
                      if (secDevBtn.code === 'seal_file_btn') {
                        file.layoutDocumentBtnType = 'seal_file_btn';
                      }
                      fileList.push(file);
                    }
                  }
                }
              }
            });
          }
        } else if (fileuploadobj instanceof WellFileUpload4Icon) {
          if (fileuploadobj && fileuploadobj.operateBtns && fileuploadobj.operateBtns.length) {
            // 图标式附件
            fieldControl.getValue(function (files) {
              if (/*fileuploadobj.operateBtns.indexOf('22') > -1 || */ fileuploadobj.operateBtns.indexOf('23') > -1) {
                for (var i = 0; i < files.length; i++) {
                  var file = files[i];

                  if (require('layoutDocumentUtils').checkFileType(file.fileName)) {
                    if (fileuploadobj.operateBtns.indexOf('22')) {
                      file.layoutDocumentBtnType = 'look_up_btn';
                    }
                    if (fileuploadobj.operateBtns.indexOf('23')) {
                      file.layoutDocumentBtnType = 'seal_file_btn';
                    }
                    fileList.push(file);
                  }
                }
              }
            });
          }
        }
      }

      var signBtnName = $('[btnid="B004097"]').html().trim();
      if (fileList.length === 0) {
        var noFileTip = '没有可处理的文件。';
        if (signBtnName !== '版式文档处理') {
          noFileTip = `没有可${signBtnName}的文件`;
        }
        appModal.alert({
          message: noFileTip
        });
        return false;
      }

      if (fileList.length === 1) {
        // 只有一个文件
        var selectFileToSignForce = window.SystemParams.getValue('pt.layoutdocument.selectfile.force');
        if (selectFileToSignForce !== 'true') {
          var file = fileList[0];
          if (file.layoutDocumentBtnType === 'look_up_btn') {
            layoutDocumentUtils.openFile(file.fileID, file.fileName);
          } else if (file.layoutDocumentBtnType === 'seal_file_btn') {
            layoutDocumentUtils.sealFile(file.fileID, file.fileName);
          }
          return false;
        }
      }
      // 职等弹窗html
      function getLayoutDocumentDialogContent() {
        var html = '';
        html +=
          '<div class="dyform" id="layoutDocumentForm">' +
          '<div class="layout-document-header">' +
          '</div>' +
          '<div class="file-table"><table id="fileTable"></table></div>' +
          '</div>';
        return html;
      }

      var html = getLayoutDocumentDialogContent();

      var title = '选择要处理文件';

      if (signBtnName !== '版式文档处理') {
        title = `选择要${signBtnName}文件`;
      }
      var $layoutDocumentDialog = appModal.dialog({
        message: html,
        title: title,
        width: 800,
        maxHeight: 497,
        shown: function ($dialog) {
          $dialog.find('.bootbox-body').css({
            'overflow-y': 'unset'
          });

          var data = fileList;
          // initTable($layoutDocumentDialog, data);
          var $fileTable = $('#fileTable', $layoutDocumentDialog);
          $fileTable.bootstrapTable('destroy');
          $fileTable.bootstrapTable({
            data: data,
            idField: 'id',
            striped: false,
            showColumns: false,
            sortable: true,
            undefinedText: '',
            columns: [
              {
                field: 'checked',
                radio: true
              },
              {
                field: 'fileID',
                title: 'fileID',
                visible: false
              },
              {
                field: 'fileName',
                title: '文件名称'
              }
            ]
          });

          $fileTable.parents('.bootstrap-table').addClass('ui-wBootstrapTable');
          if (data.length > 0) {
            $fileTable.bootstrapTable('checkBy', {
              field: 'fileID',
              values: [data[0].fileID]
            });
          }
        },
        buttons: {
          save: {
            label: '确认',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var $fileTable = $('#fileTable', $layoutDocumentDialog);
              var rows = $fileTable.bootstrapTable('getSelections');
              if (rows && rows.length && rows.length > 0) {
                var file = rows[0];
                if (file.layoutDocumentBtnType === 'look_up_btn') {
                  require('layoutDocumentUtils').openFile(file.fileID, file.fileName);
                } else if (file.layoutDocumentBtnType === 'seal_file_btn') {
                  require('layoutDocumentUtils').sealFile(file.fileID, file.fileName);
                }
              } else {
                appModal.error('请选择文件');
                return false;
              }
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default'
          }
        }
      });
    },

    //撤回时要能带出之前提交填写的意见
    _getLastTaskOpinionCancelAfter: function (flowInstUuid) {
      var opinionText = '';
      // var dyform = _self.getDyform();
      JDS.restfulGet({
        url: ctx + '/proxy/api/task/operation/getLastestCancelAfterByFlowInstUuid/' + flowInstUuid,
        contentType: 'application/x-www-form-urlencoded',
        mask: false,
        async: false,
        success: function (result) {
          if (result.data != null) {
            opinionText = result.data.opinionText;
          }
        }
      });
      return opinionText;
    },
    //校验签署意见是否符合规则
    _isOpinionRuleCheck: function (workData, scene) {
      var opinionText = workData.opinionText;
      var flowId = workData.flowDefId;
      var taskId = workData.taskId;
      var isOpinionRuleCheck = true;
      JDS.call({
        service: 'wfOpinionCheckSetFacadeService.isOpinionRuleCheck',
        data: [opinionText, flowId, taskId, scene],
        async: false,
        success: function (result) {
          var isOpinionRuleCheckDto = result.data;
          if (!isOpinionRuleCheckDto.success) {
            var options = {};
            if (isOpinionRuleCheckDto.alertAutoClose) {
              options = {
                title: '提示框', // 标题
                message: isOpinionRuleCheckDto.message, // 消息内容，不能为空
                type: 'error', // 消息类型:success、info、warning、error、alert
                autoClose: true, // 是否自动关闭，true为自动关闭，不显示关闭按钮，false为手动关闭，显示关闭按钮
                timer: 3000
              };
            } else {
              options = {
                title: '提示框', // 标题
                message: isOpinionRuleCheckDto.message, // 消息内容，不能为空
                type: 'error', // 消息类型:success、info、warning、error、alert
                autoClose: false, // 是否自动关闭，true为自动关闭，不显示关闭按钮，false为手动关闭，显示关闭按钮
                timer: null
              };
            }
            appModal.error(options);
            isOpinionRuleCheck = false;
          }
        },
        error: function () {}
      });
      return isOpinionRuleCheck;
    }
  });
  return WorkView;
});
