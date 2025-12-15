define([
  'jquery',
  'commons',
  'constant',
  'server',
  'appModal',
  'formBuilder',
  'ListViewWidgetDevelopment',
  'WorkFlowErrorHandler',
  'WorkFlow'
], function ($, commons, constant, server, appModal, formBuilder, ListViewWidgetDevelopment, WorkFlowErrorHandler, WorkFlow) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var UUID = commons.UUID;
  var JDS = server.JDS;
  var errorHandler = server.ErrorHandler.getInstance();
  var isAllowedTransferService = 'listWorkService.isAllowedTransfer';
  var isAllowedCancelService = 'listWorkService.isAllowedCancel';
  // 批量操作，需进行复杂交互
  // 转办
  var transferService = 'workV53Service.transfer';
  // 移交
  var handOverService = 'listWorkService.handOver';
  var isAllowedHandOverService = 'listWorkService.isAllowedHandOver';
  var isRequiredCancelOpinionService = 'listWorkService.isRequiredCancelOpinion';
  var isRequiredHandOverOpinionService = 'listWorkService.isRequiredHandOverOpinion';
  // 跳转环节
  var gotoTaskService = 'workService.gotoTask';
  var checkGotoTasksService = 'listWorkService.checkGotoTasks';
  var isRequiredGotoTaskOpinionService = 'listWorkService.isRequiredGotoTaskOpinion';

  // 批量操作，无需进行复杂交互
  // 已阅
  var markReadService = 'workV53Service.markRead';
  // 未阅
  var markUnreadService = 'workV53Service.markUnread';
  // 关注
  var attentionService = 'workV53Service.attention';
  // 取消关注
  var unfollowService = 'workV53Service.unfollow';
  // 撤回
  var cancelService = 'workV53Service.cancelWithOpinion';
  // 催办
  var remindService = 'workV53Service.remind';
  // 删除草搞
  var deleteDraftService = 'workV53Service.deleteDraft';
  // 删除工作
  var deleteService = 'workV53Service.delete';

  // 置顶
  var toppingService = 'workV53Service.topping';

  // 取消置顶
  var untoppingService = 'workV53Service.untopping';

  // 管理员删除工作
  var deleteByAdminService = 'workV53Service.deleteByAdmin';

  var aclRoleServices = {
    DRAFT: 'workV53Service.getDraft',
    TODO: 'workV53Service.getTodo',
    DONE: 'workV53Service.getDone',
    OVER: 'workV53Service.getOver',
    FLAG_READ: 'workV53Service.getRead',
    UNREAD: 'workV53Service.getUnread', // openToRead
    ATTENTION: 'workV53Service.getAttention',
    SUPERVISE: 'workV53Service.getSupervise',
    MONITOR: 'workV53Service.getMonitor'
  };

  // 平台应用_工作流程_二开基础_视图组件二开
  var WorkFlowListViewWidgetDevelopmentBase = function () {
    var _self = this;
    ListViewWidgetDevelopment.apply(this, arguments);

    // 工作流对像集合
    _self.workFlows = {};
    // _self.workFlow = new WorkFlow(options.workData);
    // _self.createOpinionEditor();
    // _self.createErrorHandler();
    _self.errorHandler = new WorkFlowErrorHandler(_self);
  };
  commons.inherit(WorkFlowListViewWidgetDevelopmentBase, ListViewWidgetDevelopment, {
    afterRender: function () {
      var self = this;
      self._superApply();
      $('body').on(constant.WIDGET_EVENT.WindowResultRefresh, function (event) {
        self.refresh(true);
      });
    },
    getWorkViewUrl: function (index, row) {
      var taskInstUuid = row.taskInstUuid ? row.taskInstUuid : row.uuid;
      var taskIdentityUuid = row.taskIdentityUuid || '';
      var flowInstUuid = row.flowInstUuid;
      var url = null;
      if (StringUtils.isNotBlank(taskIdentityUuid)) {
        url = ctx + '/workflow/work/view/todo?taskInstUuid={0}&taskIdentityUuid={1}&flowInstUuid={2}';
      } else {
        url = ctx + '/workflow/work/view/todo?taskInstUuid={0}&flowInstUuid={2}';
      }
      var sb = new StringBuilder();
      sb.appendFormat(url, taskInstUuid, taskIdentityUuid, flowInstUuid);
      return sb.toString();
    },
    onClickRow: function (index, row, $element, field) {
      var _self = this;
      var url = _self.getWorkViewUrl(index, row);
      var options = {};
      options.url = url;
      options.ui = _self.getWidget();
      options.size = 'large';
      var _requestId = UUID.createUUID();
      var dataStoreParams = $.extend({}, options.ui.getDataProvider().getParams());
      window.sessionStorage.setItem(_requestId + '_dataStoreParams', JSON.stringify(dataStoreParams));
      options.requestCode = _requestId;
      appContext.openWindow(options);
      // 去除未阅样式
      if ($element) {
        if ($element.find('tr').length > 0) {
          $element.find('tr').removeClass('unread');
        } else {
          $element.closest('tr').removeClass('unread');
        }
      }
    },
    refresh: function (force) {
      this.getWidget().refresh(force);
    },
    trigger: function (eventType, eventData) {
      this.getWidget().trigger(eventType, eventData);
    },
    // 获取流程数据
    getTodoWorkData: function (taskInstUuid) {
      var workData = {};
      JDS.restfulPost({
        url: ctx + '/api/workflow/work/getTodoWorkData',
        data: {
          taskInstUuid: taskInstUuid,
          loadDyFormData: false
        },
        async: false,
        version: '',
        success: function (result) {
          workData = result.data;
        },
        error: function (jqXHR) {
          appModal.error('获取流程数据失败!');
        }
      });
      return workData;
    },
    getSelectedRowIds: function (multiple, rowData) {
      var _self = this;
      var selection = _self.getWidget().getSelections();
      // 禁用选择时，操作行按钮
      if (selection.length === 0 && rowData != null && $.isPlainObject(rowData)) {
        selection = [rowData];
      }
      if (selection.length === 0) {
        appModal.error('请选择记录!');
        return;
      }
      if (!multiple && selection.length > 1) {
        appModal.error('只能选择一条记录!');
        return;
      }
      var rowIds = [];
      $.each(selection, function () {
        rowIds.push(this.uuid);
      });
      return rowIds;
    },
    // 送审批
    sendToApprove: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(false);
      if (!taskInstUuids) {
        return;
      }
      var selection = _self.getSelection();
      var rowData = selection[0];
      appContext.require(['WorkFlowApprove'], function (WorkFlowApprove) {
        var options = {
          ui: _self.getWidget(),
          formUuid: rowData.formUuid,
          dataUuid: rowData.dataUuid,
          linkUrl: _self.getLinkUrl(rowData),
          linkTitle: rowData.title,
          autoSubmit: true,
          successCallback: function () {
            appModal.success('送审批成功！');
          }
        };
        var workFlowApprove = new WorkFlowApprove(options);
        workFlowApprove.sendToApprove();
      });
    },
    getLinkUrl: function (rowData) {
      var url = ctx + '/workflow/work/v53/view/work?taskInstUuid={0}&flowInstUuid={1}';
      var taskInstUuid = rowData.uuid || '';
      var flowInstUuid = rowData.flowInstUuid;
      var sb = new StringBuilder();
      sb.appendFormat(url, taskInstUuid, flowInstUuid);
      return sb.toString();
    },
    // 转办
    transfer: function () {
      var _self = this;
      appContext.require(['multiOrg'], function () {
        _self._transfer();
      });
    },
    _transfer: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }

      // 转办权限检测
      if (_self.isAllowedTransfer(taskInstUuids) === false) {
        appModal.error('没有权限转办所选工作!');
        return;
      }

      // 判断是否转办必填意见
      // if (isRequiredTransferOpinion($element, taskInstUuids)) {
      // openToSignOpinion($element, taskInstUuids);
      // return;
      // }
      var opinionName = ''; // getOpinionName($element,
      // taskInstUuids);
      var opinionValue = ''; // getOpinionValue($element,
      // taskInstUuids);
      var opinionText = ''; // getOpinionText($element,
      // taskInstUuids);
      $.unit2.open({
        title: '选择转办人员',
        type: 'all',
        valueField: '',
        labelField: '',
        callback: function (values) {
          if (values && values.length > 0) {
            var transferUserIds = values;
            JDS.restfulPost({
              url: ctx + '/api/workflow/work/transfer',
              data: {
                taskInstUuids: taskInstUuids,
                userIds: transferUserIds,
                opinionLabel: opinionName,
                opinionValue: opinionValue,
                opinionText: opinionText
              },
              version: '',
              success: function (result) {
                appModal.success('转办成功!');
                _self.refresh(true);
                _self.trigger(constant.WIDGET_EVENT.BadgeRefresh);
              },
              error: function (jqXHR) {
                appModal.error('转办失败!');
              }
            });
          } else {
            appModal.error('转办人员不能为空!');
          }
        }
      });
    },
    // 移交
    handOver: function () {
      var _self = this;
      appContext.require(['multiOrg'], function () {
        _self._handOver();
      });
    },
    _handOver: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }
      // 检查环节锁
      if (!_self.checkTaskLock(taskInstUuids, '移交')) {
        return;
      }

      // 移交权限检测
      if (_self.isAllowedHandOver(taskInstUuids) === false) {
        appModal.error('工作已结束或已被处理或没有权限，无法进行操作!');
        return;
      }

      // 判断是否特送个人必填意见
      // if (isRequiredHandOverOpinion($element, taskInstUuids)) {
      // openToSignOpinion($element, taskInstUuids);
      // return;
      // }

      var opinionName = ''; // getOpinionName($element,
      // taskInstUuids);
      var opinionValue = ''; // getOpinionValue($element,
      // taskInstUuids);
      var opinionText = ''; // getOpinionText($element,
      // taskInstUuids);
      // 移交处理
      $.unit2.open({
        title: '选择移交人员',
        type: 'all',
        valueField: '',
        labelField: '',
        callback: function (values) {
          if (values && values.length > 0) {
            var handOverUserIds = values;
            JDS.restfulPost({
              url: ctx + '/api/workflow/work/handOver',
              data: {
                taskInstUuids: taskInstUuids,
                userIds: handOverUserIds,
                opinionLabel: opinionName,
                opinionValue: opinionValue,
                opinionText: opinionText
              },
              success: function (result) {
                appModal.success('移交办理人成功！');
                _self.refresh(true);
                _self.trigger(constant.WIDGET_EVENT.BadgeRefresh);
              },
              error: function (jqXHR) {
                appModal.error('移交失败!');
              }
            });
          } else {
            appModal.error('请选择移交人员!');
          }
        }
      });
    },
    // 跳转
    doGoto: function (_taskInstUuid) {
      var _self = this;
      var taskInstUuids = typeof _taskInstUuid != 'string' ? _self.getSelectedRowIds(false) : [].concat(_taskInstUuid);
      if (!taskInstUuids) {
        return;
      }
      // 检查环节锁
      if (!_self.checkTaskLock(taskInstUuids, '跳转')) {
        return;
      }

      // 跳转权限检测
      if (_self.isAllowedGotoTask(taskInstUuids) === false) {
        appModal.error('工作已结束或已被处理或没有权限，无法进行操作!');
        return;
      }

      $.each(taskInstUuids, function (i, taskInstUuid) {
        // 特送环节处理
        var gotoTaskCallback = function () {
          var success = function () {
            _self.deleteWorkFlow(taskInstUuid);
            _self.onGotoTaskSuccess.apply(_self, arguments);
          };
          var failure = function (jqXHR) {
            arguments[0].taskInstUuid = taskInstUuid;
            var msg = JSON.parse(jqXHR.responseText);
            if (_self.workFlow && msg && msg.data && msg.data.errorCode == 'WorkFlowException') {
              _self.workFlow.clearTempData();
            }
            _self.onGotoTaskFailure.apply(_self, arguments);
          };
          _self.workFlow = _self.getWorkFlow(taskInstUuid);
          if (_self.workFlow && _self.workFlow.workData) {
            _self.workFlow.workData.action = '环节跳转';
            _self.workFlow.workData.actionType = 'GotoTask';
          }
          _self.workFlow.gotoTask(success, failure);
        };
        if (!_self.isExistsWorkFlow(taskInstUuid)) {
          _self.workFlow = _self.ceateWorkFlow(taskInstUuid);
          _self.workFlow.load(function () {
            gotoTaskCallback();
          });
        } else {
          gotoTaskCallback();
        }
      });
    },
    // 检查环节锁
    checkTaskLock: function (taskInstUuids, actionName) {
      var _self = this;
      var taskLocks = null;
      if (taskInstUuids.length == 1) {
        JDS.restfulGet({
          url: ctx + '/api/workflow/work/listLockInfo',
          data: {
            taskInstUuid: taskInstUuids[0]
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
          var userNamesStr = '“' + userNames.join('；') + '”';
          appModal.error(userNamesStr + '正在编辑，无法进行' + actionName + '操作。');
          return false;
        }
      } else {
        JDS.restfulGet({
          url: ctx + '/api/workflow/work/listAllLockInfo',
          data: {
            taskInstUuids: taskInstUuids
          },
          traditional: true,
          contentType: 'application/x-www-form-urlencoded',
          async: false,
          success: function (result) {
            taskLocks = result.data;
          }
        });
        if (taskLocks != null) {
          var lockTaskInstUuids = [];
          for (var key in taskLocks) {
            if (taskLocks[key].length > 0) {
              lockTaskInstUuids.push(key);
            }
          }
          var titles = [];
          var dataList = _self.getWidget().getData();
          $.each(dataList, function (i, data) {
            for (var j = 0; j < lockTaskInstUuids.length; j++) {
              if (data.uuid == lockTaskInstUuids[j]) {
                titles.push(data.title);
                break;
              }
            }
          });
          if (titles.length > 0) {
            var message = '流程正在编辑，无法进行' + actionName + '操作。<br/>包括' + titles.join('；');

            appModal.error({
              message: message,
              limitLines: false
            });
            return false;
          }
        }
      }
      return true;
    },
    // 解锁
    releaseLock: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }
      JDS.restfulPost({
        url: ctx + '/api/workflow/work/releaseAllLock',
        data: {
          taskInstUuids: taskInstUuids
        },
        contentType: 'application/x-www-form-urlencoded',
        success: function (result) {
          appModal.success('解锁成功！');
        }
      });
    },
    isExistsWorkFlow: function (taskInstUuid) {
      return this.workFlows[taskInstUuid] != null;
    },
    ceateWorkFlow: function (taskInstUuid) {
      var _self = this;
      var workData = _self.getTodoWorkData(taskInstUuid);
      _self.workFlows[taskInstUuid] = new WorkFlow(workData);
      return _self.workFlows[taskInstUuid];
    },
    deleteWorkFlow: function (taskInstUuid) {
      delete this.workFlows[taskInstUuid];
    },
    getWorkFlow: function (taskInstUuid) {
      var _self = this;
      var workflow = _self.workFlows[taskInstUuid];
      if (workflow == null) {
        workflow = _self.ceateWorkFlow(taskInstUuid);
      }
      return workflow;
    },
    onGotoTaskSuccess: function (result) {
      var _self = this;
      // 提示
      appModal.success('跳转环节成功!');
      _self.refresh(true);
      _self.trigger(constant.WIDGET_EVENT.BadgeRefresh);
    },
    onGotoTaskFailure: function (jqXHR) {
      var _self = this;
      var _taskInstUuid = jqXHR.taskInstUuid;
      var callback = function () {
        _self.doGoto(_taskInstUuid);
      };
      _self.handlerError.call(_self, jqXHR, callback);
    },
    // 工作标记已阅
    markRead: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }

      JDS.restfulPost({
        url: ctx + '/api/workflow/work/markRead',
        data: {
          taskInstUuids: taskInstUuids
        },
        success: function (result) {
          appModal.success('标记已阅成功!');
          _self.refresh(true);
          _self.trigger(constant.WIDGET_EVENT.BadgeRefresh);
        },
        error: function (jqXHR) {
          appModal.error('标记已阅失败!');
        }
      });
    },
    // 工作标记未阅
    markUnread: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }

      JDS.restfulPost({
        url: ctx + '/api/workflow/work/markUnread',
        data: {
          taskInstUuids: taskInstUuids
        },
        success: function (result) {
          appModal.success('标记未阅成功!');
          _self.refresh(true);
          _self.trigger(constant.WIDGET_EVENT.BadgeRefresh);
        },
        error: function (jqXHR) {
          appModal.error('标记未阅失败!');
        }
      });
    },
    // 关注
    attention: function (e, options, rowData) {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true, rowData);
      if (!taskInstUuids) {
        return;
      }

      JDS.restfulPost({
        url: ctx + '/api/workflow/work/attention',
        data: {
          taskInstUuids: taskInstUuids
        },
        success: function (result) {
          appModal.success('已关注!');
          _self.refresh(true);
          _self.trigger(constant.WIDGET_EVENT.BadgeRefresh);
        },
        error: function (jqXHR) {
          appModal.error('关注失败!');
        }
      });
    },
    // 取消关注
    unfollow: function (e) {
      var _self = this;
      var taskInstUuids;
      var index = $(e.target).parents('tr').data('index');
      if (index !== undefined) {
        taskInstUuids = [_self.getData()[index].uuid];
      } else {
        taskInstUuids = _self.getSelectedRowIds(true);
      }
      if (!taskInstUuids) {
        return;
      }

      JDS.restfulPost({
        url: ctx + '/api/workflow/work/unfollow',
        data: {
          taskInstUuids: taskInstUuids
        },
        success: function (result) {
          appModal.success('已取消关注!');
          _self.refresh(true);
          _self.trigger(constant.WIDGET_EVENT.BadgeRefresh);
        },
        error: function (jqXHR) {
          appModal.error('取消关注失败!');
        }
      });
    },
    // 撤回
    cancel: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds();
      if (!taskInstUuids) {
        return;
      }
      // 撤回权限检测
      if (_self.isAllowedCancel(taskInstUuids) === false) {
        if (_self.isAllowedCancelCheckResult && _self.isAllowedCancelCheckResult.code == -5002) {
          appModal.error(_self.isAllowedCancelCheckResult.msg);
          delete _self.isAllowedCancelCheckResult;
        } else {
          appModal.error("撤回失败！<span style='color:#666;'>下一环节已办理，无法撤回！</span>");
        }
        return;
      }
      // 撤回处理
      var doCancel = function (opinionText) {
        appModal.confirm('确认撤回?', function (result) {
          if (result) {
            JDS.restfulPost({
              url: ctx + '/api/workflow/work/cancel',
              data: {
                taskInstUuids: taskInstUuids,
                opinionText: opinionText
              },
              mask: true,
              success: function (result) {
                appModal.success('撤回成功!');
                _self.refresh(true);
                _self.trigger(constant.WIDGET_EVENT.BadgeRefresh);
              },
              error: function (jqXHR) {
                _self.handlerError(jqXHR);
              }
            });
          }
        });
      };
      // 判断是否撤回必填意见
      if (_self.isRequiredCancelOpinion(taskInstUuids)) {
        _self.openToSignOpinion({
          title: '撤回流程',
          label: '撤回原因',
          onOk: doCancel
        });
      } else {
        doCancel('');
      }
    },
    // 催办
    remind: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }

      // 催办必填意见
      // var opinionName = _self.getOpinionName(taskInstUuids);
      // var opinionValue = _self.getOpinionValue(taskInstUuids);
      // var opinionText = _self.getOpinionText(taskInstUuids);
      // if (StringUtils.isBlank(opinionText)) {
      // _self.openToSignOpinion(taskInstUuids);
      // return;
      // }

      JDS.restfulPost({
        url: ctx + '/api/workflow/work/remind',
        data: {
          taskInstUuids: taskInstUuids
        },
        success: function (result) {
          appModal.success('催办成功!');
          _self.refresh();
        },
        error: function (jqXHR) {
          appModal.error('催办失败!');
        }
      });
    },
    // 删除草搞
    deleteDraft: function () {
      var _self = this;
      var flowInstUuids = _self.getSelectedRowIds(true);
      if (!flowInstUuids) {
        return;
      }
      appModal.confirm('确定删除工作草稿?', function (result) {
        if (result) {
          JDS.restfulPost({
            url: ctx + '/api/workflow/work/deleteDraft',
            data: {
              flowInstUuids: flowInstUuids
            },
            success: function (result) {
              appModal.success('删除成功!');
              _self.refresh(true);
              _self.trigger(constant.WIDGET_EVENT.BadgeRefresh);
            }
          });
        }
      });
    },
    // 删除工作
    deleteTask: function (e, options) {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }
      var confirmMsg = '确认删除工作(只有处于拟稿环节的待办工作才能删除)？';
      if (options && options.params) {
        if (StringUtils.isNotBlank(options.params.confirmMsg)) {
          confirmMsg = options.params.confirmMsg;
        }
      }
      appModal.confirm(confirmMsg, function (result) {
        if (result) {
          JDS.restfulPost({
            url: ctx + '/api/workflow/work/delete',
            data: {
              taskInstUuids: taskInstUuids
            },
            success: function (result) {
              appModal.success('删除成功!');
              _self.refresh(true);
              _self.trigger(constant.WIDGET_EVENT.BadgeRefresh);
            },
            error: function (jqXHR) {
              _self.handlerError(jqXHR);
            }
          });
        }
      });
    },
    // 置顶
    topping: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }

      appModal.confirm('确认要置顶?', function (result) {
        if (result) {
          JDS.restfulPost({
            url: ctx + '/api/workflow/work/topping',
            data: {
              taskInstUuids: taskInstUuids
            },
            success: function (result) {
              appModal.success('置顶成功!');
              _self.refresh(true);
              _self.trigger(constant.WIDGET_EVENT.BadgeRefresh);
            },
            error: function (jqXHR) {
              _self.handlerError(jqXHR);
            }
          });
        }
      });
    },

    // 取消置顶
    untopping: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }

      appModal.confirm('确认要取消置顶?', function (result) {
        if (result) {
          JDS.restfulPost({
            url: ctx + '/api/workflow/work/untopping',
            data: {
              taskInstUuids: taskInstUuids
            },
            success: function (result) {
              appModal.success('取消置顶成功!');
              _self.refresh(true);
              _self.trigger(constant.WIDGET_EVENT.BadgeRefresh);
            },
            error: function (jqXHR) {
              _self.handlerError(jqXHR);
            }
          });
        }
      });
    },
    // 管理员删除工作
    deleteTaskByAdmin: function (e, options) {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }

      var confirmMsg = '确认删除流程？';
      if (options && options.params) {
        if (StringUtils.isNotBlank(options.params.confirmMsg)) {
          confirmMsg = options.params.confirmMsg;
        }
      }

      appModal.confirm(confirmMsg, function (result) {
        if (result) {
          JDS.restfulPost({
            url: ctx + '/api/workflow/work/logicalDeleteByAdmin',
            data: {
              taskInstUuids: taskInstUuids
            },
            success: function (result) {
              appModal.success('删除成功!');
              _self.refresh(true);
              _self.trigger(constant.WIDGET_EVENT.BadgeRefresh);
            },
            error: function (jqXHR) {
              _self.handlerError(jqXHR);
            }
          });
        }
      });
    },
    isAllowedTransfer: function (taskInstUuids) {
      return this.checkPermission(taskInstUuids, isAllowedTransferService, 'isAllowedTransfer');
    },
    isAllowedHandOver: function (taskInstUuids) {
      return this.checkPermission(taskInstUuids, isAllowedHandOverService, 'isAllowedHandOver');
    },
    isAllowedGotoTask: function (taskInstUuids) {
      return this.checkPermission(taskInstUuids, '', 'isAllowedGotoTask');
    },
    isAllowedCancel: function (taskInstUuids) {
      return this.checkPermission(taskInstUuids, isAllowedCancelService, 'isAllowedCancel');
    },
    checkPermission: function (taskInstUuids, service, suffix) {
      var _self = this;
      var $element = $(this.getWidget().element);
      var key = taskInstUuids.join('_') + '_' + suffix;
      var isAllowedTransfer = $element.data(key);
      if (isAllowedTransfer === false) {
        return isAllowedTransfer;
      } else {
        isAllowedTransfer = false;
      }
      JDS.restfulPost({
        url: ctx + '/api/workflow/work/' + suffix,
        data: {
          taskInstUuids: taskInstUuids
        },
        async: false,
        success: function (result) {
          isAllowedTransfer = result.data;
          $element.data(key, isAllowedTransfer);
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
    // 判断是否需要撤回意见
    isRequiredCancelOpinion: function (taskInstUuids) {
      var _self = this;
      var key = taskInstUuids.join('_') + '_' + 'isRequiredCancelOpinion';
      var $element = $(_self.getWidget().element);
      var isRequiredCancelOpinion = $element.data(key);
      if (isRequiredCancelOpinion === true || isRequiredCancelOpinion === false) {
        return isRequiredCancelOpinion;
      } else {
        isRequiredCancelOpinion = false;
      }
      JDS.restfulPost({
        url: ctx + '/api/workflow/work/isRequiredCancelOpinion',
        data: {
          taskInstUuids: taskInstUuids
        },
        async: false,
        success: function (result) {
          isRequiredCancelOpinion = result.data;
          $element.data(key, isRequiredCancelOpinion);
        },
        error: function (jqXHR) {
          isRequiredCancelOpinion = false;
        }
      });
      return isRequiredCancelOpinion;
    },
    openToSignOpinion: function (options) {
      var dlgId = UUID.createUUID();
      var dlgSelector = '#' + dlgId;
      var title = options.title || '弹出框';
      var message = "<div id='" + dlgId + "'></div>";
      var dlgOptions = {
        title: title,
        message: message,
        size: 'middle',
        shown: function () {
          formBuilder.buildTextarea({
            label: options.label,
            name: 'opinionText',
            labelClass: 'required',
            placeholder: '',
            rows: 6,
            container: dlgSelector
          });
          $("textarea[name='opinionText']", dlgSelector).attr('maxlength', 2000);
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var opinionText = $("textarea[name='opinionText']", dlgSelector).val();
              if (StringUtils.isBlank(opinionText)) {
                appModal.error(options.label + '不能为空！');
                return false;
              }
              if ($.isFunction(options.onOk)) {
                options.onOk.call(this, opinionText);
              }
              return true;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default'
          }
        }
      };
      appModal.dialog(dlgOptions);
    },
    handlerError: function (jqXHR, callback) {
      // 默认的异常处理器处理
      // errorHandler.handle.apply(errorHandler, arguments);
      var _self = this;
      var msg = JSON.parse(jqXHR.responseText);
      var options = {};
      options.callback = callback;
      options.callbackContext = _self;
      options.workFlow = _self.workFlow;
      _self.errorHandler.handle(jqXHR, null, null, options);
    }
  });
  return WorkFlowListViewWidgetDevelopmentBase;
});
