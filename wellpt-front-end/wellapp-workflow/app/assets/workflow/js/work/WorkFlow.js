define(['jquery', 'constant', 'commons', 'server', 'appContext', 'appModal', 'WorkFlowInteraction'], function (
  $,
  constant,
  commons,
  server,
  appContext,
  appModal,
  WorkFlowInteraction
) {
  var UrlUtils = commons.UrlUtils;
  // 操作服务
  var serviceName = 'workV53Service';
  var printUrl = '/workflow/work/v53/print';
  var dot = constant.Separator.Dot;
  var services = {
    getWorkData: serviceName + dot + 'getWorkData',
    save: serviceName + dot + 'save',
    submit: serviceName + dot + 'submit',
    rollback: serviceName + dot + 'rollbackWithWorkData',
    rollbackToMainFlow: serviceName + dot + 'rollbackToMainFlowWithWorkData',
    cancel: serviceName + dot + 'cancelWithWorkData',
    getTodoTaskByFlowInstUuid: serviceName + dot + 'getTodoTaskByFlowInstUuid',
    transfer: serviceName + dot + 'transferWithWorkData',
    counterSign: serviceName + dot + 'counterSignWithWorkData',
    copyTo: serviceName + dot + 'copyTo',
    attention: serviceName + dot + 'attention',
    unfollow: serviceName + dot + 'unfollow',
    remind: serviceName + dot + 'remind',
    getPrintTemplates: serviceName + dot + 'getPrintTemplates',
    print: serviceName + dot + 'print',
    getDyformFileFieldDefinitions: serviceName + dot + 'getDyformFileFieldDefinitions',
    handOver: serviceName + dot + 'handOver',
    gotoTask: serviceName + dot + 'gotoTask',
    suspend: serviceName + dot + 'suspend',
    resume: serviceName + dot + 'resume',
    remove: serviceName + dot + 'delete'
  };

  var WorkDataApi = function () { };
  $.extend(WorkDataApi.prototype, {
    getBusinessTypeId: function () {
      return this.businessTypeId;
    },
    setBusinessTypeId: function (businessTypeId) {
      return (this['businessTypeId'] = businessTypeId);
    },
    getAclRole: function () {
      return this.aclRole;
    },
    getTaskInstUuid: function () {
      return this.taskInstUuid;
    },
    getTaskId: function () {
      return this.taskId;
    },
    getTaskName: function () {
      return this.taskName;
    },
    getTaskStartTime: function () {
      return this.taskStartTime;
    },
    getTaskIdentityUuid: function () {
      return this.taskIdentityUuid;
    },
    getFlowInstUuid: function () {
      return this.flowInstUuid;
    },
    getFlowDefUuid: function () {
      return this.flowDefUuid;
    },
    getFlowDefId: function () {
      return this.flowDefId;
    },
    getTitle: function () {
      return this.title;
    },
    getName: function () {
      return this.name;
    },
    getSuspensionState: function () {
      return this.suspensionState;
    },
    isNormalState: function () {
      return this.getSuspensionState() === 0;
    },
    isSuspendState: function () {
      return this.getSuspensionState() === 1;
    },
    isDeletedState: function () {
      return this.getSuspensionState() === 2;
    },
    getSerialNoDefId: function () {
      return this.serialNoDefId;
    },
    getUserId: function () {
      return this.userId;
    },
    getUserName: function () {
      return this.userName;
    },
    getFormUuid: function () {
      return this.formUuid;
    },
    getDataUuid: function () {
      return this.dataUuid;
    },
    setDataUuid: function (dataUuid) {
      return (this['dataUuid'] = dataUuid);
    },
    getDyFormData: function () {
      return this.dyFormData;
    },
    setDyFormData: function (dyFormData) {
      return (this['dyFormData'] = dyFormData);
    },
    getOwnerId: function () {
      return this.ownerId;
    },
    getStartDepartmentId: function () {
      return this.startDepartmentId;
    },
    getOwnerDepartmentId: function () {
      return this.ownerDepartmentId;
    },
    getStartUnitId: function () {
      return this.startUnitId;
    },
    setStartUnitId: function (startUnitId) {
      return (this['startUnitId'] = startUnitId);
    },
    getOwnerUnitId: function () {
      return this.ownerUnitId;
    },
    setOwnerUnitId: function (ownerUnitId) {
      return (this['ownerUnitId'] = ownerUnitId);
    },
    getCreateTime: function () {
      return this.createTime;
    },
    getTodoType: function () {
      return this.todoType;
    },
    // 办理类型工作待办(1)
    isTodoSubmit: function () {
      return this.getTodoType() === 1;
    },
    // 办理类型会签待办(2)
    isTodoCounterSign: function () {
      return this.getTodoType() === 2;
    },
    // 办理类型转办待办(3)
    isTodoTransfer: function () {
      return this.getTodoType() === 3;
    },
    // 办理类型移交人员待办(4)
    isTodoHandOver: function () {
      return this.getTodoType() === 4;
    },
    // 办理类型委托待办(5)
    isTodoDelegation: function () {
      return this.getTodoType() === 5;
    },
    getGotoTask: function () {
      return this.gotoTask;
    },
    getAction: function () {
      return this.action;
    },
    setAction: function (action) {
      return (this['action'] = action);
    },
    getActionType: function () {
      return this.actionType;
    },
    setActionType: function (actionType) {
      return (this['actionType'] = actionType);
    },
    getRollbackToPreTask: function () {
      return this.rollbackToPreTask;
    },
    getPrintTemplateId: function () {
      return this.printTemplateId;
    },
    getPrintTemplateUuid: function () {
      return this.printTemplateUuid;
    },
    getPrintTemplateLang: function () {
      return this.printTemplateLang;
    },
    getOpinionValue: function () {
      return this.opinionValue;
    },
    getOpinionLabel: function () {
      return this.opinionLabel;
    },
    getOpinionText: function () {
      return this.opinionText;
    },
    getCustomJsModule: function () {
      return this.customJsModule;
    },
    getCustomJsFragmentModule: function () {
      return this.customJsFragmentModule;
    },
    getIsFirstTaskNode: function () {
      return this.isFirstTaskNode;
    }
  });

  // JSON服务
  var jds = server.JDS;
  var StringUtils = commons.StringUtils;
  var WorkFlow = function (options) {
    var args = arguments;
    args[0] = $.extend(options || {}, WorkDataApi.prototype);
    WorkDataApi.prototype;
    WorkFlowInteraction.apply(this, args);
    this.beforeServiceCallback = $.noop;
    this.afterServiceCallback = $.noop;
  };
  commons.inherit(WorkFlow, WorkFlowInteraction, {
    // 加载数据
    load: function (successCallback, failureCallback) {
      var _self = this;
      var success = function (result) {
        _self.workData = $.extend(result.data, WorkDataApi.prototype);
        // 工作流数据存储到临时数据
        _self._workData2TempData();
        if ($.isFunction(successCallback)) {
          successCallback.call(_self, result);
        }
      };
      var workData = $.extend({}, _self.workData);
      //			if(appContext.isMobileApp() && StringUtils.isNotBlank(workData.defaultMFormUuid)){
      //				workData.formUuid = workData.defaultMFormUuid;
      //			}else if(StringUtils.isNotBlank(workData.defaultVFormUuid)){
      //				workData.formUuid = workData.defaultVFormUuid;
      //			}
      // _self._service(services.getWorkData, [workData], success, failureCallback);
      var ajaxUrl = ctx + '/api/workflow/work/getWorkDataByWorkBean';
      _self._ajaxPostService(ajaxUrl, workData, success, failureCallback);
    },
    // 获取表单数据
    getDyformData: function () {
      return this.workData.dyFormData;
    },
    // 服务前处理
    setBeforeServiceCallback: function (callback) {
      this.beforeServiceCallback = callback;
    },
    // 服务后处理
    setAfterServiceCallback: function (callback) {
      this.afterServiceCallback = callback;
    },
    // 服务处理
    _service: function (service, data, successCallback, failureCallback, isLxqpMode, refresh) {
      var _self = this;
      if ($.isFunction(_self.beforeServiceCallback)) {
        _self.beforeServiceCallback.apply(_self, arguments);
      }
      var urlParams = {
        isMobileApp: appContext.isMobileApp()
      };
      jds.call({
        service: service,
        data: data,
        urlParams: urlParams,
        success: function (result) {
          // 清理临时数据
          _self.clearTempData();
          if ($.isFunction(_self.afterServiceCallback)) {
            _self.afterServiceCallback.apply(_self, arguments);
          }
          if ($.isFunction(successCallback)) {
            successCallback.apply(_self, arguments);
          }
          if (isLxqpMode) {
            if (refresh) {
              _self.refreshTodoList();
            } else if (result.data.sameUserSubmitType !== '1' && !result.data.sameUserSubmitTaskInstUuid) {
              _self.triggerLXQPNextRecord();
            }
          }
        },
        error: function (jqXHR) {
          if (isLxqpMode && JSON.parse(jqXHR.responseText).data.msg) {
            if (refresh) {
              _self.refreshTodoList();
            } else {
              _self.triggerLXQPNextRecord(true);
            }
          }
          if ($.isFunction(_self.afterServiceCallback)) {
            _self.afterServiceCallback.apply(_self, arguments);
          }
          if ($.isFunction(failureCallback)) {
            failureCallback.apply(_self, arguments);
          }
        }
      });
    },
    _ajaxGetService: function (ajaxUrl, data, successCallback, failureCallback, isLxqpMode, refresh) {
      this._ajaxService(ajaxUrl, 'GET', data, successCallback, failureCallback, isLxqpMode, refresh);
    },
    _ajaxPostService: function (ajaxUrl, data, successCallback, failureCallback, isLxqpMode, refresh) {
      this._ajaxService(ajaxUrl, 'POST', data, successCallback, failureCallback, isLxqpMode, refresh);
    },
    _ajaxService: function (ajaxUrl, type, data, successCallback, failureCallback, isLxqpMode, refresh) {
      var _self = this;
      if ($.isFunction(_self.beforeServiceCallback)) {
        _self.beforeServiceCallback.apply(_self, arguments);
      }
      var urlParams = {
        isMobileApp: appContext.isMobileApp()
      };
      var _action = data.action;
      if (!_action && data.workBean && data.workBean.action) {
        _action = data.workBean.action;
      }
      if (_action) {
        appModal.showMask(_action ? _action + '中' : '提交中');
      }
      jds.restfulRequest({
        type: type,
        url: ajaxUrl,
        urlParams: urlParams,
        data: JSON.stringify(data),
        contentType: 'application/json',
        dataType: 'json',
        success: function (result) {
          if (_action) {
            appModal.hideMask();
          }
          if (result.code == -5002) {
            if ($.isFunction(_self.afterServiceCallback)) {
              _self.afterServiceCallback.apply(_self, arguments);
            }
            if ($.isFunction(failureCallback)) {
              failureCallback.apply(_self, arguments);
            }
          } else {
            // 清理临时数据
            _self.clearTempData();
            if ($.isFunction(_self.afterServiceCallback)) {
              _self.afterServiceCallback.apply(_self, arguments);
            }
            if ($.isFunction(successCallback)) {
              successCallback.apply(_self, arguments);
            }
          }

          if (isLxqpMode) {
            setTimeout(function () {
              if (refresh) {
                _self.refreshTodoList();
              } else {
                _self.triggerLXQPNextRecord();
              }
            }, 4000);
          }
        },
        error: function (jqXHR) {
          if (_action) {
            appModal.hideMask();
          }
          if (isLxqpMode && JSON.parse(jqXHR.responseText).data && JSON.parse(jqXHR.responseText).data.msg) {
            if (refresh) {
              _self.refreshTodoList();
            } else if (
              // 去除选择办理人和选择退回环节
              JSON.parse(jqXHR.responseText).data.errorCode != 'TaskNotAssignedUser' &&
              JSON.parse(jqXHR.responseText).data.errorCode != 'RollbackTaskNotFound' &&
              JSON.parse(jqXHR.responseText).data.errorCode != 'JudgmentBranchFlowNotFound' &&
              JSON.parse(jqXHR.responseText).data.errorCode != 'TaskNotAssignedMonitor' &&
              JSON.parse(jqXHR.responseText).data.errorCode != 'ChooseSpecificUser'
            ) {
              _self.triggerLXQPNextRecord(true);
            }
          }
          if ($.isFunction(_self.afterServiceCallback)) {
            _self.afterServiceCallback.apply(_self, arguments);
          }
          if ($.isFunction(failureCallback)) {
            failureCallback.apply(_self, arguments);
          }
        }
      });
    },
    // 保存
    save: function (successCallback, failureCallback) {
      var _self = this;
      var isLxqpMode = _self.isLxqpMode();
      var success = function (result) {
        // 更新流程实例UUID
        var data = result.data;
        _self.workData.flowInstUuid = data.flowInstUuid;
        successCallback.apply(_self, arguments);
      };
      // _self._service(services.save, [_self.workData], success, failureCallback, isLxqpMode, true);
      var ajaxUrl = ctx + '/api/workflow/work/save';
      _self._ajaxPostService(ajaxUrl, _self.workData, success, failureCallback, isLxqpMode, true);
    },
    // 提交
    submit: function (successCallback, failureCallback) {
      var _self = this;
      var isLxqpMode = _self.isLxqpMode();
      // 临时数据合并到工作流数据
      _self._tempData2WorkData();
      // _self._service(services.submit, [_self.workData], successCallback, failureCallback, isLxqpMode);
      var ajaxUrl = ctx + '/api/workflow/work/submit';
      _self._ajaxPostService(ajaxUrl, _self.workData, successCallback, failureCallback, isLxqpMode);
    },
    // 退回
    rollback: function (successCallback, failureCallback) {
      var _self = this;
      var isLxqpMode = _self.isLxqpMode();
      // 临时数据合并到工作流数据
      _self._tempData2WorkData();
      // _self._service(services.rollback, [_self.workData], successCallback, failureCallback, isLxqpMode);
      var ajaxUrl = ctx + '/api/workflow/work/rollback';
      _self._ajaxPostService(ajaxUrl, _self.workData, successCallback, failureCallback, isLxqpMode);
    },
    // 退回主流程
    rollbackToMainFlow: function (successCallback, failureCallback) {
      var _self = this;
      var isLxqpMode = _self.isLxqpMode();
      // 临时数据合并到工作流数据
      _self._tempData2WorkData();
      // _self._service(services.rollbackToMainFlow, [_self.workData], successCallback, failureCallback, isLxqpMode);
      var ajaxUrl = ctx + '/api/workflow/work/rollbackToMainFlow';
      _self._ajaxPostService(ajaxUrl, _self.workData, successCallback, failureCallback, isLxqpMode);
    },
    // 撤回
    cancel: function (successCallback, failureCallback) {
      var _self = this;
      var isLxqpMode = _self.isLxqpMode();
      // 临时数据合并到工作流数据
      _self._tempData2WorkData();
      // _self._service(services.cancel, [_self.workData], successCallback, failureCallback, isLxqpMode);
      var ajaxUrl = ctx + '/api/workflow/work/cancelWithWorkData';
      _self._ajaxPostService(ajaxUrl, _self.workData, successCallback, failureCallback, isLxqpMode);
    },
    // 根据流程实例获取待办任务
    getTodoTaskByFlowInstUuid: function (successCallback, failureCallback) {
      var _self = this;
      // _self._service(services.getTodoTaskByFlowInstUuid, [_self.workData.flowInstUuid], successCallback, failureCallback);
      var ajaxUrl = ctx + '/api/workflow/work/getTodoTaskByFlowInstUuid?flowInstUuid=' + _self.workData.flowInstUuid;
      _self._ajaxGetService(ajaxUrl, {}, successCallback, failureCallback);
    },
    // 转办
    transfer: function (transferUserIds, successCallback, failureCallback) {
      var _self = this;
      var isLxqpMode = _self.isLxqpMode();
      // 临时数据合并到工作流数据
      _self._tempData2WorkData();
      // _self._service(services.transfer, [_self.workData, transferUserIds], successCallback, failureCallback, isLxqpMode);
      var ajaxUrl = ctx + '/api/workflow/work/transfer';
      var data = {
        workBean: _self.workData,
        taskInstUuids: [_self.workData.taskInstUuid],
        userIds: transferUserIds,
        transferUsers: _self.transferUsers,
        opinionText: _self.workData.opinionText
      };
      _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback, isLxqpMode);
    },
    // 会签
    counterSign: function (counterSignUserIds, successCallback, failureCallback) {
      var _self = this;
      var isLxqpMode = _self.isLxqpMode();
      // _self._service(services.counterSign, [_self.workData, counterSignUserIds], successCallback, failureCallback, isLxqpMode);
      var ajaxUrl = ctx + '/api/workflow/work/counterSign';
      var data = {
        workBean: _self.workData,
        taskInstUuids: [_self.workData.taskInstUuid],
        userIds: counterSignUserIds,
        opinionText: _self.workData.opinionText,
        signUsers: _self.signUsers
      };
      _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback, isLxqpMode);
    },
    // 抄送
    copyTo: function (taskInstUuids, copyToUserIds, successCallback, failureCallback) {
      var _self = this;
      var isLxqpMode = _self.isLxqpMode();
      // _self._service(services.copyTo, [taskInstUuids, copyToUserIds], successCallback, failureCallback, isLxqpMode, true);
      var ajaxUrl = ctx + '/api/workflow/work/copyTo';
      var data = {
        taskInstUuids: taskInstUuids,
        userIds: copyToUserIds
      };
      _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback, isLxqpMode, true);
    },
    // 关注
    attention: function (taskInstUuids, successCallback, failureCallback) {
      var _self = this;
      var isLxqpMode = _self.isLxqpMode();
      // _self._service(services.attention, [taskInstUuids], successCallback, failureCallback, isLxqpMode, true);
      var ajaxUrl = ctx + '/api/workflow/work/attention';
      var data = {
        taskInstUuids: taskInstUuids
      };
      _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback, isLxqpMode, true);
    },
    // 取消关注
    unfollow: function (taskInstUuids, successCallback, failureCallback) {
      var _self = this;
      var isLxqpMode = _self.isLxqpMode();
      // _self._service(services.unfollow, [taskInstUuids], successCallback, failureCallback, isLxqpMode, true);
      var ajaxUrl = ctx + '/api/workflow/work/unfollow';
      var data = {
        taskInstUuids: taskInstUuids
      };
      _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback, isLxqpMode, true);
    },
    // 催办
    remind: function (taskInstUuids, opinionLabel, opinionValue, opinionText, successCallback, failureCallback) {
      var _self = this;
      // _self._service(services.remind, [taskInstUuids, opinionLabel, opinionValue, opinionText], successCallback, failureCallback);
      var ajaxUrl = ctx + '/api/workflow/work/remind';
      var data = {
        taskInstUuids: taskInstUuids,
        opinionLabel: opinionLabel,
        opinionValue: opinionValue,
        opinionText: opinionText
      };
      _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback);
    },
    // 获取套打模板
    getPrintTemplates: function (successCallback, failureCallback) {
      var _self = this;
      // _self._service(services.getPrintTemplates, [_self.workData.taskInstUuid, _self.workData.flowInstUuid], successCallback, failureCallback);
      var ajaxUrl = ctx + '/api/workflow/work/getPrintTemplateTree';
      var data = {
        taskInstUuid: _self.workData.taskInstUuid,
        flowInstUuid: _self.workData.flowInstUuid
      };
      _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback);
    },
    // 套打
    print: function (printTemplateId, printTemplateUuid, printTemplateLang, successCallback, failureCallback) {
      var _self = this;
      if (typeof printTemplateUuid === 'function') {
        successCallback = printTemplateUuid;
        printTemplateUuid = null;
      }
      if (typeof printTemplateLang === 'function') {
        failureCallback = printTemplateLang;
        printTemplateLang = null;
      }
      var isLxqpMode = _self.isLxqpMode();
      // _self._service(services.print, [_self.workData.taskInstUuid, printTemplateId, printTemplateUuid, printTemplateLang], successCallback, failureCallback, isLxqpMode, true);
      var ajaxUrl = ctx + '/api/workflow/work/print';
      var data = {
        taskInstUuid: _self.workData.taskInstUuid,
        printTemplateId: printTemplateId,
        printTemplateUuid: printTemplateUuid,
        printTemplateLang: printTemplateLang
      };
      _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback, isLxqpMode, true);
      // var _self = this;
      // var StringBuilder = commons.StringBuilder;
      // var sb = new StringBuilder();
      // sb.append(ctx + printUrl);
      // sb.append("?taskInstUuid=" + _self.workData.taskInstUuid);
      // sb.append("&flowInstUuid=" + _self.workData.flowInstUuid);
      // sb.append("&printTemplateId=" + printTemplate.id);
      // sb.append("&printTemplateName=" + printTemplate.name);
      // sb.append("&filename=" + _self.workData.title);
      // appContext.getWindowManager().open(sb.toString(), "_self");
    },
    // 获取表单附件字段的定义信息
    getDyformFileFieldDefinitions: function (successCallback, failureCallback) {
      var _self = this;
      _self._service(services.getDyformFileFieldDefinitions, [_self.workData.taskInstUuid], successCallback, failureCallback);
      var ajaxUrl = ctx + '/api/workflow/work/getDyformFileFieldDefinitions?taskInstUuid=' + _self.workData.taskInstUuid;
      _self._ajaxGetService(ajaxUrl, {}, successCallback, failureCallback);
    },
    // 移交
    handOver: function (handOverUserIds, successCallback, failureCallback) {
      var _self = this;
      // _self._service(services.handOver, [_self.workData, handOverUserIds], successCallback, failureCallback);
      var ajaxUrl = ctx + '/api/workflow/work/handOver';
      var data = {
        taskInstUuids: [_self.workData.taskInstUuid],
        userIds: handOverUserIds
      };
      _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback);
    },
    // 跳转
    gotoTask: function (successCallback, failureCallback) {
      var _self = this;
      // 临时数据合并到工作流数据
      _self._tempData2WorkData();
      // _self._service(services.gotoTask, [_self.workData], successCallback, failureCallback);
      var ajaxUrl = ctx + '/api/workflow/work/gotoTaskWithWorkData';
      _self.workData.dyFormData = null;
      _self._ajaxPostService(ajaxUrl, _self.workData, successCallback, failureCallback);
    },
    // 挂起
    suspend: function (taskInstUuids, successCallback, failureCallback) {
      var _self = this;
      var isLxqpMode = _self.isLxqpMode();
      // _self._service(services.suspend, [taskInstUuids], successCallback, failureCallback, isLxqpMode);
      var ajaxUrl = ctx + '/api/workflow/work/suspend';
      var data = {
        taskInstUuids: taskInstUuids
      };
      _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback, isLxqpMode);
    },
    // 恢复
    resume: function (taskInstUuids, successCallback, failureCallback) {
      var _self = this;
      var isLxqpMode = _self.isLxqpMode();
      // _self._service(services.resume, [taskInstUuids], successCallback, failureCallback, isLxqpMode, true);
      var ajaxUrl = ctx + '/api/workflow/work/resume';
      var data = {
        taskInstUuids: taskInstUuids
      };
      _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback, isLxqpMode, true);
    },
    // 删除
    remove: function (taskInstUuids, successCallback, failureCallback) {
      var _self = this;
      // _self._service(services.remove, [taskInstUuids], successCallback, failureCallback);
      var confirmMsg = '确认删除流程？';
      appModal.confirm(confirmMsg, function (result) {
        if (result) {
          var ajaxUrl = ctx + '/api/workflow/work/logicalDeleteByAdmin';
          var data = {
            taskInstUuids: taskInstUuids
          };
          _self._ajaxPostService(ajaxUrl, data, successCallback, failureCallback);
        }
      });
    },
    isLxqpMode: function () {
      return !!(window != top && top.WorkFlowLXQP);
    },
    //连续签批模式触发下一条
    triggerLXQPNextRecord: function (isError) {
      if (isError) {
        top.appModal.info({
          message: '工作不存在或者已被办理，将为您自动加载下一条待办',
          timer: 1000,
          callback: function () {
            top.WorkFlowLXQP.parentNextRecord();
          }
        });
      } else {
        top.WorkFlowLXQP.parentNextRecord();
      }
    },
    //刷新待办列表
    refreshTodoList: function () {
      top.WorkFlowLXQP.VueObj.refreshTodoList();
    }
  });

  return WorkFlow;
});
