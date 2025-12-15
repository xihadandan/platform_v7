define([
  'jquery',
  'server',
  'commons',
  'constant',
  'appContext',
  'appModal',
  'WorkFlowErrorHandler',
  'WorkFlowInteraction',
  'formBuilder'
], function ($, server, commons, constant, appContext, appModal, WorkFlowErrorHandler, WorkFlowInteraction, formBuilder) {
  var JDS = server.JDS;
  var UUID = commons.UUID;
  var UrlUtils = commons.UrlUtils;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var startNewWorkModule = 'app-workflow-start-new-work';
  var workViewApproveFragment = 'WorkViewApproveFragment';
  var candidateContentOptions = [
    {
      id: '1',
      text: '源文送审批'
    },
    {
      id: '2',
      text: '复制源文送审批'
    },
    {
      id: '3',
      text: '原文作为链接送审批'
    }
  ];
  var WorkFlowApprove = function (options) {
    var _self = this;
    _self.options = options;
    // 指定送审内容
    var contentType = options.contentType;
    if (StringUtils.isNotBlank(contentType)) {
      // 配置多个进行选择
      if (StringUtils.contains(contentType, ';')) {
        _self.initContentOptions(contentType.split(';'));
      } else {
        _self.sendContent = {
          type: contentType
        };
      }
    } else {
      _self.initContentOptions();
    }
    // 指定流程ID
    var flowDefId = options.flowDefId;
    if (StringUtils.isNotBlank(flowDefId)) {
      _self.approveFlow = {
        flowDefId: flowDefId,
        newWorkUrl: ctx + '/workflow/work/new/' + flowDefId
      };
    }
    _self.workFlow = new WorkFlowInteraction();
    _self.errorHandler = new WorkFlowErrorHandler(_self);
  };
  $.extend(WorkFlowApprove.prototype, {
    // 初始化送审批内容
    initContentOptions: function (types) {
      var _self = this;
      if (types != null) {
        _self.contentOptions = [];
        $.each(candidateContentOptions, function (i, contentOption) {
          if ($.inArray(contentOption.id, types) != -1) {
            _self.contentOptions.push(contentOption);
          }
        });
      } else {
        _self.contentOptions = $.extend(true, candidateContentOptions, {});
      }
    },
    // 初始化
    sendToApprove: function () {
      var _self = this;
      var sendContent = _self.sendContent;
      var approveFlow = _self.approveFlow;
      var autoSubmit = _self.options.autoSubmit;

      // 发送内容为空，选择内容
      if (sendContent == null) {
        _self.showSelectSendContentDialog();
      } else if (approveFlow == null) {
        // 检验是否允许进行单据转换
        if (!_self.checkAllowedConvertDyformDataByBotRuleIdIfRequired()) {
          return;
        }
        // 审批流程为空，选择流程
        _self.selectApproveFlow();
      } else {
        // 检验是否允许进行单据转换
        if (!_self.checkAllowedConvertDyformDataByBotRuleIdIfRequired()) {
          return;
        }
        // 自动提交，不打开发起流程页面进行提交
        if (autoSubmit == true || autoSubmit == 'true') {
          _self.autoSubmit2Approve();
        } else {
          // 打开发起流程页面进行提交
          _self.open2Approve();
        }
      }
    },
    // 检验是否允许进行单据转换
    checkAllowedConvertDyformDataByBotRuleIdIfRequired: function () {
      var _self = this;
      var formUuid = _self.options.formUuid;
      var botRuleId = _self.options.botRuleId;
      var approveFlow = _self.approveFlow || {};
      var flowDefId = approveFlow.flowDefId;
      if (StringUtils.isBlank(botRuleId)) {
        return true;
      }
      $.get({
        url: ctx + "/api/workflow/approve/isAllowedConvertDyformDataByBotRuleId",
        data: {
          sourceFormUuid: formUuid,
          botRuleId: botRuleId,
          flowDefId: flowDefId
        },
        async: false,
        success: function (result) {
          _self.allowedConvertDyformDataByBotRuleId = result.data;
        }
      });
      if (!_self.allowedConvertDyformDataByBotRuleId) {
        //        if (StringUtils.isBlank(flowDefId)) {
        appModal.error('源表单或者目标单据和单据转换规则' + botRuleId + '中配置的不一致');
        //        } else {
        //          appModal.error('复制源文送审批的数据或发起的流程数据无法通过单据转换规则[' + botRuleId + ']进行转换');
        //        }
      }
      return _self.allowedConvertDyformDataByBotRuleId;
    },
    // 不打开窗口自动提交进行送审批
    autoSubmit2Approve: function () {
      var _self = this;
      var sendType = parseInt(_self.sendContent.type);
      switch (sendType) {
        case 1:
          // 源文送审批
          _self.sendSource2Approve(sendType);
          break;
        case 2:
          // 复制源文送审批
          _self.sendCopySource2Approve(sendType);
          break;
        case 3:
          // 原文作为链接送审批
          _self.sendLink2Approve(sendType);
          break;
        default:
          appModal.error('无法识别选择送审的内容');
      }
    },
    // 源文送审批
    sendSource2Approve: function (type) {
      var _self = this;
      var approveContent = {
        formUuid: _self.options.formUuid,
        dataUuid: _self.options.dataUuid,
        linkUrl: _self.options.linkUrl,
        linkTitle: _self.options.linkTitle
      };
      _self._send2Approve(approveContent);
    },
    // 复制源文送审批
    sendCopySource2Approve: function (type) {
      var _self = this;
      var approveContent = {
        formUuid: _self.options.formUuid,
        dataUuid: _self.options.dataUuid,
        botRuleId: _self.options.botRuleId,
        linkUrl: _self.options.linkUrl,
        linkTitle: _self.options.linkTitle
      };
      _self._send2Approve(approveContent);
    },
    // 原文作为链接送审批
    sendLink2Approve: function (type) {
      var _self = this;
      var approveContent = {
        linkUrl: _self.options.linkUrl,
        linkTitle: _self.options.linkTitle
      };
      _self._send2Approve(approveContent);
    },
    // 自动提交后台送审批
    _send2Approve: function (approveContent) {
      var _self = this;
      approveContent.type = _self.sendContent.type;
      approveContent.flowDefId = _self.approveFlow.flowDefId;
      _self.workFlow._tempData2WorkData();
      var interactionTaskData = _self.workFlow.getWorkData();
      JDS.restfulPost({
        url: ctx + '/api/workflow/approve/sendToApprove',
        data: { approveContent: approveContent, interactionTaskData: interactionTaskData },
        mask: true,
        success: function (result) {
          if (result.code == -5002) {
            var options = {};
            options.callback = _self.sendToApprove;
            options.callbackContext = _self;
            options.workFlow = _self.workFlow;
            _self.errorHandler.handle(result, null, null, options);
          } else {
            _self.onSubmitSuccess.apply(_self, arguments);
          }
        },
        error: function (jqXHR, statusText, error) {
          var options = {};
          options.callback = _self.sendToApprove;
          options.callbackContext = _self;
          options.workFlow = _self.workFlow;
          _self.errorHandler.handle(jqXHR, null, null, options);
        }
      });
    },
    // 提交成功处理
    onSubmitSuccess: function (result) {
      var _self = this;
      var options = _self.options;
      var successCallback = options.successCallback;
      var autoSubmitResult = options.autoSubmitResult || {};
      if (StringUtils.isBlank(autoSubmitResult.msg)) {
        autoSubmitResult.msg = '送审批成功！' + _self.getMsgTips(result.data.taskTodoUsers, '已提交至');
      }
      if ($.isFunction(successCallback)) {
        successCallback.apply(_self, arguments);
      } else {
        // 刷新父窗口
        if (autoSubmitResult.refreshParent) {
          appContext.getWindowManager().refreshParent();
        }
        // 提示信息
        appModal.success(autoSubmitResult.msg, function () {
          // 关闭当前窗口
          if (autoSubmitResult.close) {
            appContext.getWindowManager().close();
          }
          // 刷新当前窗口/组件
          if (autoSubmitResult.refresh) {
            if (options.ui && options.ui.refresh) {
              options.ui.refresh();
            } else {
              appContext.getWindowManager().refresh();
            }
          }
        });
      }
    },
    getMsgTips: function (todoUser, msgPre) {
      var msgName = '';
      if (todoUser && Object.keys(todoUser).length > 0) {
        var userName = [];
        for (var k in todoUser) {
          var users = todoUser[k];
          for (var i in users) {
            userName.push(users[i]);
          }
        }

        if (userName.length > 0) {
          msgName =
            "<span style='color:#666;'>" +
            msgPre +
            "<span style='color:#333;font-weight: bold;'>&nbsp;" +
            userName.join('，') +
            '</span>！</span>';
        }
      }
      return msgName;
    },
    // 打开窗口进行送审批
    open2Approve: function () {
      var _self = this;
      var sendType = parseInt(_self.sendContent.type);
      switch (sendType) {
        case 1:
          // 源文送审批
          _self.openSource2Approve(sendType);
          break;
        case 2:
          // 复制源文送审批
          _self.openCopySource2Approve(sendType);
          break;
        case 3:
          // 原文作为链接送审批
          _self.openLink2Approve(sendType);
          break;
        default:
          appModal.error('无法识别选择送审的内容');
      }
    },
    // 源文送审批
    openSource2Approve: function (type) {
      var _self = this;
      var contents = {
        linkUrl: _self.options.linkUrl,
        linkTitle: encodeURIComponent(_self.options.linkTitle)
      };
      var urlParams = {
        formUuid: _self.options.formUuid,
        dataUuid: _self.options.dataUuid,
        ep_workViewFragment: workViewApproveFragment,
        ep_sendContent: JSON.stringify(contents)
      };
      _self._openApproveFlow(urlParams);
    },
    // 复制源文送审批
    openCopySource2Approve: function (type) {
      var _self = this;
      var contents = {
        formUuid: _self.options.formUuid,
        dataUuid: _self.options.dataUuid,
        botRuleId: _self.options.botRuleId,
        onlyFillEditableField: _self.options.onlyFillEditableField,
        linkUrl: _self.options.linkUrl,
        linkTitle: encodeURIComponent(_self.options.linkTitle),
        type: type
      };
      var urlParams = {
        ep_workViewFragment: workViewApproveFragment,
        ep_sendContent: JSON.stringify(contents)
      };
      _self._openApproveFlow(urlParams);
    },
    // 原文作为链接送审批
    openLink2Approve: function (type) {
      var _self = this;
      var contents = {
        linkUrl: _self.options.linkUrl,
        linkTitle: encodeURIComponent(_self.options.linkTitle),
        type: type
      };
      var urlParams = {
        ep_workViewFragment: workViewApproveFragment,
        ep_sendContent: JSON.stringify(contents)
      };
      _self._openApproveFlow(urlParams);
    },
    // 打开发起审批流程
    _openApproveFlow: function (urlParams) {
      var _self = this;
      var newWorkUrl = _self.approveFlow.newWorkUrl;
      newWorkUrl = UrlUtils.appendUrlParams(newWorkUrl, urlParams);
      // 默认发起流程
      var newWorkOptions = {};
      newWorkOptions.url = newWorkUrl;
      newWorkOptions.ui = _self.options.ui;
      newWorkOptions.size = 'large';
      appContext.openWindow(newWorkOptions);
    },
    // 显示选择发送内容弹出框
    showSelectSendContentDialog: function () {
      var _self = this;
      var dlgId = UUID.createUUID();
      var dlgIdSelector = '#' + dlgId;
      var message = new StringBuilder();
      message.appendFormat('<div id="{0}" class="wf-approve-choose-send-content">', dlgId);
      message.appendFormat('</div">');
      var contentTitle = _self.options.contentTitle || '选择送审批内容';
      var options = {
        title: contentTitle,
        size: 'middle',
        message: message.toString(),
        onEscape: function () { },
        // 显示弹出框后事件
        shown: function () {
          var $container = $(dlgIdSelector);
          formBuilder.buildRadio({
            container: $container,
            items: _self.contentOptions,
            name: 'chooseRollbackTask',
            display: 'chooseRollbackTaskName',
            labelColSpan: '0',
            controlColSpan: '12',
            isBlock: true
          });

          if (_self.options.selectionLength > 1) {
            var $chooseRollbackTaskInputs = $container.find('input[name="chooseRollbackTask"]');
            $.each($chooseRollbackTaskInputs, function (index, chooseRollbackTaskInput) {
              if ($(chooseRollbackTaskInput).attr('value') === '1' || $(chooseRollbackTaskInput).attr('value') === '2') {
                $(chooseRollbackTaskInput).attr('disabled', 'disabled').hide();
                $(chooseRollbackTaskInput).next().css('cursor', 'not-allowed').hide();
              }
              if ($(chooseRollbackTaskInput).attr('value') === '3') {
                $(chooseRollbackTaskInput).attr('checked', 'checked');
              }
            });
          }
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var $container = $(dlgIdSelector);
              var checkedSelector = 'input[name=chooseRollbackTask]:checked';
              var $radio = $(checkedSelector, $container);
              var sendContentType = $radio.val();
              if (StringUtils.isBlank(sendContentType)) {
                appModal.error('请选择送审批内容');
                return false;
              }
              // 设置发送内容的类型
              _self.sendContent = {
                type: sendContentType
              };
              // 重新送审批
              setTimeout(function () {
                _self.sendToApprove();
              }, 1);
              return true;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () { }
          }
        }
      };
      appModal.dialog(options);
    },
    // 选择审批流程
    selectApproveFlow: function () {
      var _self = this;
      var options = _self.options;
      appContext.require([startNewWorkModule], function (startNewWorkModule) {
        // 设置发起流程标题
        var title = options.flowTitle || '选择送审批流程';
        var categoryCode = options.flowCategoryCode;
        var startWorkOptions = {
          ui: options.ui,
          callbackContext: _self,
          callback: function (retVal) {
            _self.approveFlow = retVal;
            // 重新送审批
            setTimeout(function () {
              _self.sendToApprove();
            }, 1);
          },
          params: $.extend(_self.options, _self.options.params, {
            title: title,
            categoryCode: categoryCode
          })
        };
        appContext.executeJsModule(startNewWorkModule, startWorkOptions);
      });
    }
  });
  return WorkFlowApprove;
});
