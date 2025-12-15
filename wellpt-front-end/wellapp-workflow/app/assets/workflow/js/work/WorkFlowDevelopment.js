define(['jquery', 'server', 'commons', 'constant', 'appContext', 'appModal', 'DyformExplain'], function (
  $,
  server,
  commons,
  constant,
  appContext,
  appModal,
  DyformExplain
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var FileDownloadUtils = server.FileDownloadUtils;
  var WorkFlowDevelopment = function () {};
  $.extend(WorkFlowDevelopment.prototype, {
    // 流程二开ID
    getId: function () {
      var _self = this;
      if (_self._id) {
        _self._id = new Date().getTime();
      }
      return _self._id;
    },
    // 初始化
    init: $.noop,
    onInitSuccess: function () {},
    onInitFailure: function () {
      this.handlerError.apply(this, arguments);
    },
    // 加载流程数据
    load: $.noop,
    onLoadSuccess: function () {},
    onLoadFailure: function () {
      this.handlerError.apply(this, arguments);
    },
    getMsgTips: function (todoUser, msgPre, nextTasks) {
      var msgName = '';
      var userName = [];
      if (todoUser && Object.keys(todoUser).length > 0) {
        for (var k in todoUser) {
          var users = todoUser[k];
          userName.push(users);
        }
      }

      if (userName.length > 0 || nextTasks.name != '') {
        if (appContext.isMobileApp()) {
          msgName = msgPre + userName.join('，') + (nextTasks.name ? ' | ' + nextTasks.name : '') + '！';
        } else {
          var msgStyle = userName.length == 0 ? 'font-weight: bold;' : '';
          msgName =
            "<span style='color:#666;'>" +
            msgPre +
            "<span class='toDoUserName' style='color:#333;font-weight: bold;'>&nbsp;" +
            userName.join('，') +
            '</span>' +
            (nextTasks.name
              ? (userName.length > 0 ? '&nbsp;|&nbsp;' : '') +
                ("<span class='toDoTaskName' style='color:#333;" + msgStyle + "'>" + nextTasks.name + '</span>')
              : '') +
            '！</span>';
        }
      }

      return msgName;
    },
    // 关闭
    close: function () {
      var _self = this;
      _self.getDyformData(function (dyFormData) {
        if (_self.isDyformDataChanged(dyFormData)) {
          appModal.confirm('内容已修改，请先保存后再操作，否则修改内容将丢失！', function (result) {
            if (result) {
              _self.userConfirmClose = true;
              appContext.getWindowManager().close();
            }
          });
        } else {
          appContext.getWindowManager().close();
        }
      });
    },
    // 保存
    save: $.noop,
    onSaveSuccess: function (result) {
      var _self = this;
      appContext.getWindowManager().refreshParent();
      appModal.success({
        message: '保存成功！',
        callback: function () {
          _self.refresh();
        }
      });
    },
    onSaveFailure: function (jqXHR) {
      var _self = this;
      var callback = _self._getActionCallback('save');
      this.handlerError.call(_self, jqXHR, callback);
    },
    // 提交
    submit: $.noop,
    // 下载打印结果
    _downloadPrintResult: function (result) {
      if (result && result.data && StringUtils.isNotBlank(result.data.printResultFileId)) {
        FileDownloadUtils.downloadMongoFile({
          fileId: [result.data.printResultFileId]
        });
      }
    },
    // 与前环节相同自动提交
    _saveUserSubmit: function (result) {
      var _self = this;
      var data = result.data;
      var signOpinionModel = _self.options.signOpinionModel || '1';
      var sameUserSubmitType = data.sameUserSubmitType;
      var submitTaskInstUuid = data.sameUserSubmitTaskInstUuid;
      var submitTaskOperationUuid = data.sameUserSubmitTaskOperationUuid;
      // 与前环节相同自动提交
      if (StringUtils.isNotBlank(submitTaskInstUuid) && StringUtils.isNotBlank(submitTaskOperationUuid)) {
        var refreshUrl = new StringBuilder();
        refreshUrl.append(ctx);
        refreshUrl.append('/workflow/work/v53/view/todo?taskInstUuid=');
        refreshUrl.append(submitTaskInstUuid);
        refreshUrl.append('&flowInstUuid=');
        refreshUrl.append(data.flowInstUuid);
        refreshUrl.append('&sameUserSubmitType=');
        refreshUrl.append(sameUserSubmitType);
        refreshUrl.append('&auto_submit=true&sameUserSubmitTaskOperationUuid=');
        refreshUrl.append(submitTaskOperationUuid);
        refreshUrl.append('&ep_wf_sign_opinion_model=');
        refreshUrl.append(signOpinionModel);
        // 自动提交函数
        var autoSubmit = function (refreshUrl) {
          //在iframe里则判定为连续签批模式
          if (window != top) {
            refreshUrl.append('&_requestCode=' + Browser.getQueryString('_requestCode'));
            refreshUrl.append('&lxqpMode=true');
            _self.refreshSameFlow(refreshUrl.toString());
          } else {
            _self.allowUnloadWorkData = true;
            appContext.getWindowManager().refresh(refreshUrl.toString());
          }
        };
        var nextTasks = result.data.nextTasks || [];
        if (nextTasks.length > 1) {
          nextTasks = [nextTasks[nextTasks.length - 1]];
        }
        if (nextTasks.length > 0) {
          $.each(nextTasks, function (tIndex, tItem) {
            var taskTodoUsers = result.data.taskTodoUsers[tItem.uuid];
            var msgToUser = _self.getMsgTips(taskTodoUsers, '已提交至', tItem);

            // 自动提交
            appModal.success({
              message: '提交成功！' + msgToUser,
              timer: 3000,
              callback: function () {
                autoSubmit(refreshUrl);
              }
            });
          });
        } else {
          // 自动提交
          appModal.success({
            message: '提交成功！',
            timer: 3000,
            callback: function () {
              autoSubmit(refreshUrl);
            }
          });
        }

        return true;
      }
      return false;
    },
    onSubmitSuccess: function (result) {
      var _self = this;
      _self.allowUnloadWorkData = true;
      // 下载打印结果
      _self._downloadPrintResult(result);
      // 与前环节相同自动提交
      if (_self._saveUserSubmit(result)) {
        return;
      }
      var nextTasks = result.data.nextTasks || [];
      if (nextTasks.length > 1) {
        nextTasks = [nextTasks[nextTasks.length - 1]];
      }
      if (nextTasks.length > 0) {
        $.each(nextTasks, function (tIndex, tItem) {
          var taskTodoUsers = result.data.taskTodoUsers[tItem.uuid];
          var msgToUser = _self.getMsgTips(taskTodoUsers, '已提交至', tItem);

          //在iframe中
          if (window != top) {
            top.appModal.success({
              message: '提交成功！' + msgToUser
            });
            _self.nextRecord('lastOne');
          } else {
            appModal.success({
              message: '提交成功！' + msgToUser,
              resultCode: 0
            });
          }
        });
      } else {
        if (window != top) {
          top.appModal.success({
            message: '提交成功！'
          });
          _self.nextRecord('lastOne');
        } else {
          appModal.success({
            message: '提交成功！',
            resultCode: 0
          });
        }
      }
    },
    onSubmitFailure: function (jqXHR) {
      var _self = this;
      var callback = _self._getActionCallback('submit');
      _self.handlerError.call(_self, jqXHR, callback);
    },
    // 退回
    rollbackToSelf: function (result) {
      var _self = this;
      var data = result.data;

      var toTaskInstUuid = Object.keys(data.taskTodoUsers)[0];
      var currUserId = SpringSecurityUtils.getCurrentUserId();
      var taskTodoUsers = data.taskTodoUsers[toTaskInstUuid];
      var isSameUser = false;
      for (var i in taskTodoUsers) {
        if (i == currUserId) {
          isSameUser = true;
          break;
        }
      }
      // 与前环节相同自动提交
      if (isSameUser) {
        var refreshUrl = new StringBuilder();
        refreshUrl.append(ctx);
        refreshUrl.append('/workflow/work/v53/view/todo?taskInstUuid=');
        refreshUrl.append(toTaskInstUuid);
        refreshUrl.append('&flowInstUuid=');
        refreshUrl.append(data.flowInstUuid);

        // 自动提交函数
        var autoSubmit = function (refreshUrl) {
          //在iframe里则判定为连续签批模式
          if (window != top) {
            refreshUrl.append('&_requestCode=' + Browser.getQueryString('_requestCode'));
            refreshUrl.append('&lxqpMode=true');
            _self.refreshSameFlow(refreshUrl.toString());
          } else {
            _self.allowUnloadWorkData = true;
            appContext.getWindowManager().refresh(refreshUrl.toString());
          }
        };
        var nextTasks = result.data.nextTasks;
        var msgToUser = '';
        if (nextTasks.length > 0) {
          var taskTodoUsers = result.data.taskTodoUsers[nextTasks[0].uuid];
          msgToUser = _self.getMsgTips(taskTodoUsers, '已退回至', nextTasks[0]);
        }
        // 自动提交
        appModal.success('退回成功！' + msgToUser, function () {
          autoSubmit(refreshUrl);
        });
        return true;
      } else {
        return false;
      }
    },
    rollback: function () {},
    onRollbackSuccess: function (result) {
      var _self = this;
      _self.allowUnloadWorkData = true;
      if (_self.rollbackToSelf(result)) {
        return;
      }

      var msgToUser = '';
      var nextTasks = result.data.nextTasks;
      if (nextTasks.length > 0) {
        var taskTodoUsers = result.data.taskTodoUsers[nextTasks[0].uuid];
        msgToUser = this.getMsgTips(taskTodoUsers, '已退回至', nextTasks[0]);
      }

      // 提示
      // bug#50364
      if (window.opener && window.opener.appModal && window.opener.appContext) {
        if (window == top) {
          appModal.success({
            message: '退回成功！' + msgToUser,
            resultCode: 0
          });
          _self.nextRecord('lastOne');
        } else {
          window.opener.appModal.success('退回成功！' + msgToUser);
          appContext.getWindowManager().refreshParent();
          appContext.getWindowManager().close();
        }
      } else {
        if (top.WorkFlowLXQP) {
          appModal.success({
            message: '退回成功！' + msgToUser,
            callback: function () {
              _self.nextRecord('lastOne');
              appContext.getWindowManager().closeAndRefreshParent();
            }
          });
        } else {
          appModal.success({
            message: '退回成功！' + msgToUser,
            resultCode: 0
          });
        }
      }
    },
    onRollbackFailure: function (jqXHR) {
      var _self = this;
      var callback = _self._getActionCallback('rollback');
      _self.handlerError.call(_self, jqXHR, callback);
    },
    // 直接退回
    directRollback: function () {},
    onDirectRollbackSuccess: function (result) {
      var _self = this;
      _self.allowUnloadWorkData = true;
      var nextTasks = result.data.nextTasks;
      var msgToUser = '';
      if (nextTasks.length > 0) {
        var taskTodoUsers = result.data.taskTodoUsers[nextTasks[0].uuid];
        msgToUser = _self.getMsgTips(taskTodoUsers, '已退回至', nextTasks[0]);
      }
      // 提示
      appModal.success({
        message: '直接退回成功！' + msgToUser,
        resultCode: window == top ? 0 : null
      });
    },
    onDirectRollbackFailure: function (jqXHR) {
      var _self = this;
      var callback = function () {
        _self.directRollback();
      };
      _self.handlerError.call(_self, jqXHR, callback);
    },
    // 退回主流程
    rollbackToMainFlow: function () {},
    onRollbackToMainFlowSuccess: function (result) {
      var _self = this;
      _self.allowUnloadWorkData = true;

      var nextTasks = result.data.nextTasks;
      var msgToUser = '';
      if (nextTasks.length > 0) {
        var taskTodoUsers = result.data.taskTodoUsers[nextTasks[0].uuid];
        msgToUser = _self.getMsgTips(taskTodoUsers, '已退回至', nextTasks[0]);
      }
      // 提示
      appModal.success({
        message: '退回成功！' + msgToUser,
        resultCode: window == top ? 0 : null
      });
    },
    onRollbackToMainFlowFailure: function (jqXHR) {
      var _self = this;
      var callback = function () {
        _self.directRollback();
      };
      _self.handlerError.call(_self, jqXHR, callback);
    },
    // 撤回
    cancel: function () {},
    onCancelSuccess: function (result) {
      var _self = this;
      _self.allowUnloadWorkData = true;
      var callback = function () {
        // 打开待办工作界面
        var success = function (result) {
          if (StringUtils.isNotBlank(result.data.uuid)) {
            // 刷新父窗口
            appContext.getWindowManager().refreshParent();
            var taskInstUuid = result.data.uuid;
            _self.refreshTodoWork(taskInstUuid);
          } else {
            _self.handlerSuccess.call(_self, result);
          }
        };
        var failure = function (jqXHR) {
          // 处理流程操作返回 的错误信息
          appContext.getWindowManager().closeAndRefreshParent();
        };
        _self.workFlow.getTodoTaskByFlowInstUuid(success, failure);
      };
      // 提示
      appModal.success({
        message: '撤回成功！',
        callback: callback
      });
    },
    onCancelFailure: function (jqXHR) {
      this.handlerError.apply(this, arguments);
    },
    // 转办
    transfer: $.noop,
    onTransferSuccess: function (result) {
      var _self = this;
      _self.allowUnloadWorkData = true;
      var msgToUser =
        "<span style='color:#666;'>已转办至<span style='color:#333;font-weight: bold;'>&nbsp;" +
        _self.workFlow.transferUsers.join('，') +
        '</span>！</span>';
      // 提示
      appModal.success({
        message: '转办成功！' + msgToUser,
        resultCode: window == top ? 0 : null
      });
      if (window != top) {
        _self.nextRecord('lastOne');
      }
    },
    onTransferFailure: function (jqXHR) {
      var _self = this;
      var callback = _self._getActionCallback('transfer');
      _self.handlerError.call(_self, jqXHR, callback);
    },
    // 会签
    counterSign: $.noop,
    onCounterSignSuccess: function (result) {
      var _self = this;
      _self.allowUnloadWorkData = true;
      var workData = _self.workFlow.getWorkData();
      var action = workData.action || '会签';
      var msgToUser =
        "<span style='color:#666;'>已" +
        action +
        "至<span style='color:#333;font-weight: bold;'>&nbsp;" +
        this.signUsers.join('，') +
        '</span>！</span>';
      // 提示
      appModal.success({
        message: action + '成功！' + msgToUser,
        resultCode: window == top ? 0 : null
      });
      if (window != top) {
        _self.nextRecord('lastOne');
      }
    },
    onCounterSignFailure: function (jqXHR) {
      this.handlerError.apply(this, arguments);
    },
    // 抄送
    copyTo: $.noop,
    onCopyToSuccess: function (result) {
      var msgToUser =
        "<span style='color:#666;'>已抄送至<span style='color:#333;font-weight: bold;'>&nbsp;" +
        this.copyToUsers.join('，') +
        '</span>！</span>';
      // 提示
      appModal.success({
        message: '抄送成功！' + msgToUser
      });
    },
    onCopyToFailure: function (jqXHR) {
      this.handlerError.apply(this, arguments);
    },
    // 签署意见
    signOpinion: $.noop,
    // 关注
    attention: $.noop,
    onAttentionSuccess: function (result) {
      // 提示
      var _self = this;
      appModal.success({
        message: '已关注！',
        callback: function () {
          _self.reload();
        }
      });
    },
    onAttentionFailure: function (jqXHR) {
      this.handlerError.apply(this, arguments);
    },
    // 取消关注
    unfollow: $.noop,
    onUnfollowSuccess: function (result) {
      // 提示
      var _self = this;
      appModal.success({
        message: '已取消关注！',
        callback: function () {
          _self.reload();
        }
      });
    },
    onUnfollowFailure: function (jqXHR) {
      this.handlerError.apply(this, arguments);
    },
    // 套打
    print: $.noop,
    onPrintFailure: function (jqXHR) {
      this.handlerError.apply(this, arguments);
    },
    // 催办
    remind: $.noop,
    onRemindSuccess: function (result) {
      // 提示
      appModal.success({
        message: '催办成功！'
      });
    },
    onRemindFailure: function (jqXHR) {
      this.handlerError.apply(this, arguments);
    },
    // 特送个人
    handOver: $.noop,
    onHandOverSuccess: function (result) {
      var _self = this;
      _self.allowUnloadWorkData = true;
      // 提示
      appModal.success({
        message: '特送个人成功！',
        resultCode: window == top ? 0 : null
      });
    },
    onHandOverFailure: function (jqXHR) {
      this.handlerError.apply(this, arguments);
    },
    // 特送环节
    gotoTask: $.noop,
    onGotoTaskSuccess: function (result) {
      var _self = this;
      _self.allowUnloadWorkData = true;
      // 提示
      appModal.success({
        message: '特送环节成功！',
        resultCode: window == top ? 0 : null
      });
    },
    onGotoTaskFailure: function (jqXHR) {
      var _self = this;
      var callback = function () {
        _self.gotoTask();
      };
      _self.handlerError.call(_self, jqXHR, callback);
    },
    // 挂起
    suspend: $.noop,
    onSuspendSuccess: function (result) {
      // 提示
      var _self = this;
      _self.allowUnloadWorkData = true;
      appContext.getWindowManager().refreshParent();
      var actionName = _self.getButtonLabel('挂起');
      var successMsg = actionName + '成功！';
      appModal.success({
        message: successMsg,
        resultCode: window == top ? 0 : null
      });
      //			appModal.success({
      //				message : successMsg,
      //				callback : function() {
      //					_self.reload();
      //				}
      //			});
    },
    onSuspendFailure: function (jqXHR) {
      this.handlerError.apply(this, arguments);
    },
    // 恢复
    resume: $.noop,
    onResumeSuccess: function (result) {
      // 提示
      var _self = this;
      _self.allowUnloadWorkData = true;
      appContext.getWindowManager().refreshParent();
      var actionName = _self.getButtonLabel('恢复');
      var successMsg = actionName + '成功！';
      appModal.success({
        message: successMsg,
        callback: function () {
          _self.reload();
        }
      });
    },
    onResumeFailure: function (jqXHR) {
      this.handlerError.apply(this, arguments);
    },
    // 删除
    remove: $.noop,
    onRemoveSuccess: function (result) {
      var _self = this;
      _self.allowUnloadWorkData = true;
      // 提示
      appModal.success({
        message: '删除成功！',
        resultCode: 0
      });
    },
    onRemoveFailure: function (jqXHR) {
      this.handlerError.apply(this, arguments);
    },
    viewFlowDataSnapshot: function () {
      var _self = this;
      appContext.require(['WorkFlowSnapshotViewer'], function (WorkFlowSnapshotViewer) {
        var snapshot = new WorkFlowSnapshotViewer(_self);
        snapshot.show();
      });
    },
    // 返回工作数据
    getWorkData: function () {
      return this.workFlow.getWorkData();
    },
    getWorkDataApi: function () {
      return this.workFlow.getWorkDataApi();
    },
    // 返回工作流程
    getWorkFlow: function () {
      var _self = this;
      return _self.workFlow;
    },
    // 添加附加参数
    addExtraParam: function (paramName, paramValue) {
      var workData = this.workFlow.getWorkData();
      workData.extraParams[paramName] = paramValue;
    },
    // 获取附加参数
    getExtraParam: function (paramName) {
      var workData = this.workFlow.getWorkData();
      return workData.extraParams[paramName];
    },
    // 是否办结工作
    isOver: function () {
      return this._isWorkDataMatchAclRole('OVER');
    },
    // 是否草稿工作
    isDraft: function () {
      return this._isWorkDataMatchAclRole('DRAFT');
    },
    // 是否待办工作
    isTodo: function () {
      return this._isWorkDataMatchAclRole('TODO');
    },
    // 是否已办工作
    isDone: function () {
      return this._isWorkDataMatchAclRole('DONE');
    },
    // 是否督办工作
    isSupervise: function () {
      return this._isWorkDataMatchAclRole('SUPERVISE');
    },
    // 是否监控工作
    isMonitor: function () {
      return this._isWorkDataMatchAclRole('MONITOR');
    },
    // 获取ACL角色
    getAclRole: function () {
      var _self = this;
      var workData = _self.workFlow.getWorkData();
      return workData.aclRole;
    },
    _isWorkDataMatchAclRole: function (aclRole) {
      var _self = this;
      var workData = _self.workFlow.getWorkData();
      if (workData.aclRole === aclRole) {
        return true;
      }
      return false;
    },
    // 是否提交后进行打印
    isPrintAfterSubmit: function () {
      var _self = this;
      if (_self.submitAndPrint != null) {
        return _self.submitAndPrint;
      }
      // 提交并打印
      var submitAndPrintButtonCode = _self.submitAndPrintButtonCode;
      var btnSelector = ":button[name='" + submitAndPrintButtonCode + "']";
      var submitAndPrint = $(btnSelector, _self.options.toolbarPlaceholder).length > 0;
      _self.submitAndPrint = submitAndPrint;
      return _self.submitAndPrint;
    },
    // 是否可编辑文档
    isDyformEditable: function () {
      var _self = this;
      if (_self.options.dyformEditable != null) {
        return _self.options.dyformEditable;
      }
      // 新版流程定义通过“环节属性->可编辑表单”选项控制是否可编辑文档
      if (this.workFlow.workData.canEditForm != null) {
        return this.workFlow.workData.canEditForm;
      }
      // 可编辑文档参数
      var editableCode = _self.options.dyformEditableCode;
      var btnSelector = ":button[name='" + editableCode + "']";
      var dyformEditable = $(btnSelector, _self.options.toolbarPlaceholder).length > 0;
      return dyformEditable;
    },
    // 初始化表单数据
    initDyform: function () {
      var _self = this;
      var editDyform = _self.isDyformEditable();
      var workDataDisplayAsLabel = false;
      var workData = _self.workFlow.getWorkData();
      var dyFormData = workData.dyFormData;
      var isFirst = StringUtils.isBlank(workData.flowInstUuid);
      // 如果是开始环节可编辑，若设置可编辑，则可编辑，否则不可编辑
      if (StringUtils.isBlank(workData.taskInstUuid) || workData.isFirstTaskNode === 'true' || workData.isFirstTaskNode === true) {
        if (_self.isDraft() || _self.isTodo()) {
          //解决第一环节已办的情况下被退回到第一环节，应该是文本状态
          workDataDisplayAsLabel = false;
        } else {
          workDataDisplayAsLabel = true;
        }
      } else if (!_self.isTodo()) {
        workDataDisplayAsLabel = true;
      } else if (!editDyform) {
        workDataDisplayAsLabel = true;
      }
      try {
        var dyformOptions = {
          renderTo: _self.dyformSelector,
          formData: dyFormData,
          recordInitFormDatas: true,
          displayAsLabel: workDataDisplayAsLabel,
          optional: {
            isFirst: isFirst,
            title: workData.title
          },
          success: function () {
            _self.dyform = this;
            _self.createSidebar();
            _self.initWorkProcess();
            _self.initOpinionEditor();

            // 锁定工作
            _self.lockWorkIfRequired();
            _self.showButtons();
            _self.onDyformInitSuccess.apply(_self, arguments);
            _self.afterDyformInitSuccess();
          },
          error: function () {
            _self.dyform = this;
            _self.onDyformInitFailure.apply(_self, arguments);
          }
        };
        // 准备初始化表单
        _self.prepareInitDyform(dyformOptions);
        var $dyform = null;
        var placeholder = _self.$element[0];
        if (placeholder && placeholder.querySelector) {
          $dyform = $(placeholder.querySelector(_self.dyformSelector));
        }
        if (!$dyform || !$dyform.length) {
          $dyform = $(_self.dyformSelector);
        }
        _self.dyform = new DyformExplain($dyform, dyformOptions);
      } catch (e) {
        _self.hideButtons();
        appModal.error('表单解析失败： ' + e);
        throw e;
      }
    },
    createSidebar: function () {},
    // 锁定工作
    lockWorkIfRequired: function () {},
    // 表单初始化成功，设置浏览器地址输入的值
    onDyformInitSuccess: function () {},
    afterDyformInitSuccess: function () {
      var _self = this;
      // 输入的动态表单值
      var $dyfields = $('input[name^=ep_dyfs_]');
      $dyfields.each(function () {
        var pName = $(this).attr('name');
        var fieldName = pName.substring(8);
        var fieldVal = $(this).val();
        _self.dyform.setFieldValue(fieldName, fieldVal);
      });
      // 表单初始化成功，自动提交
      var autoSubmit = _self.options.autoSubmit;
      var submitButtonCode = _self.options.submitButtonCode;
      var $submitBtn = $("button[btnId='" + submitButtonCode + "']", _self.options.toolbarPlaceholder);
      var sameUserSubmitType = Browser.getQueryString('sameUserSubmitType');
      if (autoSubmit === 'true' && $submitBtn.length > 0) {
        if (sameUserSubmitType == '3') {
          // 不自动提交并刷新页面
          appContext.getWindowManager().refreshParent();
        } else if (sameUserSubmitType == '1') {
          // 自动提交
          setTimeout(function () {
            $submitBtn.trigger('click');
          }, 0);
        } else {
          var workData = _self.getWorkData();
          var data = _self.getOpinionEditor().getOpinion();
          appModal.dialog({
            title: '即将连续提交[' + workData.taskName + ']',
            message: '<div style="word-break: break-all;">自动使用上一环节意见进行提交<br>' + data.text + '</div>',
            buttons: {
              confirm: {
                label: '确定',
                className: 'btn-primary',
                callback: function () {
                  // 自动提交
                  $submitBtn.trigger('click');
                  return true;
                }
              },
              cancel: {
                label: '取消',
                className: 'btn-primary',
                callback: function () {
                  return true;
                }
              }
            }
          });
        }
      }
    },
    // 表单初始化失败
    onDyformInitFailure: function () {
      appModal.error('表单解析失败！');
    },
    // 准备初始化表单
    prepareInitDyform: function (dyformOptions) {},
    // 获取表单选择器
    getDyformSelector: function () {
      return this.dyformSelector;
    },
    // 获取表单实例对象
    getDyform: function () {
      return this.dyform;
    },
    // 获取表单数据
    getDyformData: function (fnCallback) {
      this.dyform.collectFormData(fnCallback, function (errorInfo) {
        // 收集表单数据失败
        appModal.error('表单数据收集失败[ + ' + JSON.stringify(errorInfo) + ']，无法提交数据！');
        // throw Error("表单数据收集失败[ + " + JSON.stringify(errorInfo) + "]，无法提交数据！");
      });
    },
    // 验证表单数据
    validateDyformData: function () {
      return this.dyform.validateForm();
    },
    // 初始化办理过程
    initWorkProcess: function () {
      var _self = this;
      if (_self.processViewer) {
        _self.processViewer.init();
      }
    },
    // 初始化意见编辑器
    initOpinionEditor: function () {
      var _self = this;
      if (_self.opinionEditor) {
        _self.opinionEditor.init();
      }
    },
    // 获取意见编辑器
    getOpinionEditor: function () {
      return this.opinionEditor;
    },
    // 显示操作按钮
    showButtons: function () {
      $('.wf_operate').show();
    },
    // 隐藏操作按钮
    hideButtons: function () {
      $('.wf_operate').hide();
    },
    // 重新加载容器
    reload: function () {
      this.allowUnloadWorkData = true;
      appContext.getWindowManager().refresh();
    },
    // 刷新窗口
    refresh: function () {
      var _self = this;
      this.allowUnloadWorkData = true;
      var workData = _self.workFlow.getWorkData();
      var taskInstUuid = workData.taskInstUuid;
      var flowInstUuid = workData.flowInstUuid;
      var windowManager = appContext.getWindowManager();
      if (StringUtils.isNotBlank(taskInstUuid)) {
        windowManager.refresh();
      } else if (StringUtils.isNotBlank(flowInstUuid)) {
        var refreshUrl = ctx + '/workflow/work/v53/view/draft?flowInstUuid=' + flowInstUuid;
        windowManager.refresh(refreshUrl);
      } else {
        windowManager.refresh();
      }
    },
    // 刷新待办工作
    refreshTodoWork: function (taskInstUuid) {
      var _self = this;
      this.allowUnloadWorkData = true;
      var workData = _self.workFlow.getWorkData();
      var signOpinionModel = _self.options.signOpinionModel || '1';
      var flowInstUuid = workData.flowInstUuid;
      var refreshUrl = new StringBuilder();
      refreshUrl.append(ctx);
      refreshUrl.append('/workflow/work/v53/view/todo?taskInstUuid=');
      refreshUrl.append(taskInstUuid);
      refreshUrl.append('&flowInstUuid=');
      refreshUrl.append(flowInstUuid);
      refreshUrl.append('&ep_wf_sign_opinion_model=');
      refreshUrl.append(signOpinionModel);
      appContext.getWindowManager().refresh(refreshUrl.toString());
    },
    // 成功处理
    handlerSuccess: function (result) {
      this.allowUnloadWorkData = true;
      appContext.getWindowManager().closeAndRefreshParent();
    },
    // 失败处理
    handlerError: function (jqXHR, callback) {
      var _self = this;
      var options = {};
      options.callback = callback;
      options.callbackContext = _self;
      options.workFlow = _self.workFlow;
      this.errorHandler.handle(jqXHR, null, null, options);
    },
    // 获取当前事件
    getCurrentEvent: function () {
      return this.currentEvent;
    },
    // 设置当前事件
    setCurrentEvent: function (event) {
      this.currentEvent = event;
    },
    // 获取当前操作的按钮名称
    getButtonLabel: function (defaultLabel) {
      if (this.currentEvent == null) {
        return defaultLabel;
      }
      var $btn = $(this.currentEvent.target);
      var label = $btn.text();
      if (StringUtils.isBlank(label)) {
        return defaultLabel;
      }
      return label;
    },
    _getActionCallback: function (action) {
      var _self = this;
      return function () {
        _self[action].call(_self);
      };
    },
    // 存储签署意见
    storeOpinion: function () {
      this.getOpinionEditor().store();
    },
    // 还原签署意见
    restoreOpinion: function () {
      this.getOpinionEditor().restore();
    },
    // 打开提交签署意见
    openToSignIfRequiredSubmit: function () {
      return this.openToSignIfRequired('提交', 'submit');
    },
    // 打开退回签署意见
    openToSignIfRequiredRollback: function () {
      return this.openToSignIfRequired('退回', 'rollback');
    },
    // 打开撤回签署意见
    openToSignIfRequiredCancel: function () {
      return this.openToSignIfRequired('撤回', 'cancel');
    },
    // 打开转办签署意见
    openToSignIfRequiredTransfer: function () {
      return this.openToSignIfRequired('转办', 'transfer');
    },
    // 打开会签签署意见
    openToSignIfRequiredCounterSign: function () {
      return this.openToSignIfRequired('会签', 'counterSign');
    },
    // 打开催办签署意见
    openToSignIfRequiredRemind: function () {
      return this.openToSignIfRequired('催办', 'remind');
    },
    // 打开特送个人签署意见
    openToSignIfRequiredHandOver: function () {
      return this.openToSignIfRequired('特送个人', 'handOver');
    },
    // 打开特送环节签署意见
    openToSignIfRequiredGotoTask: function () {
      return this.openToSignIfRequired('特送环节', 'gotoTask');
    },
    setRequiredSignOpinion: function (requiredSignOpinion) {
      var _self = this;
      _self.getOpinionEditor().requiredSignOpinion = requiredSignOpinion;
    },
    // 根据需要打开签署意见，需要签署意见返回true,否则返回false
    openToSignIfRequired: function (defaultLabel, action) {
      var _self = this;
      var label = _self.getButtonLabel(defaultLabel);
      var options = {};
      var callback = _self._getActionCallback(action);
      var buttons = {
        confirm: {
          label: label,
          className: 'btn-primary',
          callback: callback,
          callbackScope: _self
        }
      };
      options.buttons = buttons;
      options.action = action;
      return _self.getOpinionEditor().openToSignIfRequired(options);
    },
    // 意见收集到工作流数据
    opinionToWorkData: function () {
      var _self = this;
      var data = _self.getOpinionEditor().getOpinion();
      var workData = _self.workFlow.getWorkData();
      workData.opinionLabel = data.label;
      workData.opinionValue = data.value;
      workData.opinionText = data.text;
    },
    // 清空签署意见
    clearOpinion: function () {
      this.opinionEditor.clear();
    }
  });
  return WorkFlowDevelopment;
});
