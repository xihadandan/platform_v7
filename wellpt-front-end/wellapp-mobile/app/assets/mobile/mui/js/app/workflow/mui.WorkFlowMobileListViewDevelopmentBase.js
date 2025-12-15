define(["mui", "commons", "constant", "server", "formBuilder", "MobileListDevelopmentBase", "WorkView",
  "WorkViewProxy", "formBuilder", "appModal"
], function ($, commons, constant, server, formBuilder,
  MobileListDevelopmentBase, WorkView, workView, formBuilder, appModal) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var JDS = server.JDS;
  var errorHandler = server.ErrorHandler.getInstance();
  var isAllowedTransferService = "listWorkService.isAllowedTransfer";
  var isAllowedCancelService = "listWorkService.isAllowedCancel";
  // 批量操作，需进行复杂交互
  // 转办
  var transferService = "workV53Service.transfer";
  // 移交
  var handOverService = "listWorkService.handOver";
  var isAllowedHandOverService = "listWorkService.isAllowedHandOver";
  var isRequiredHandOverOpinionService = "listWorkService.isRequiredHandOverOpinion";
  // 跳转环节
  var gotoTaskService = "workService.gotoTask";
  var checkGotoTasksService = "listWorkService.checkGotoTasks";
  var isRequiredGotoTaskOpinionService = "listWorkService.isRequiredGotoTaskOpinion";

  // 批量操作，无需进行复杂交互
  // 已阅
  var markReadService = "workV53Service.markRead";
  // 未阅
  var markUnreadService = "workV53Service.markUnread";
  // 关注
  var attentionService = "workV53Service.attention";
  // 取消关注
  var unfollowService = "workV53Service.unfollow";
  // 撤回
  var cancelService = "workV53Service.cancel";
  // 催办
  var remindService = "workV53Service.remind";
  // 删除草搞
  var deleteDraftService = "workV53Service.deleteDraft";
  // 删除工作
  var deleteService = "workV53Service.delete";
  // 管理员删除工作
  var deleteByAdminService = "workV53Service.deleteByAdmin";

  var WorkFlowMobileListViewDevelopmentBase = function () {
    MobileListDevelopmentBase.apply(this, arguments);
  };
  commons.inherit(WorkFlowMobileListViewDevelopmentBase, MobileListDevelopmentBase, {
    getWorkService: function () {
      return "mobileWorkService.getTodo";
    },
    getWorkServiceParams: function (data) {
      var taskInstUuid = data.uuid;
      var flowInstUuid = data.flowInstUuid;
      return [taskInstUuid, flowInstUuid];
    },
    onClickRow: function (index, data, $element, event) {
      var _self = this;
      var options = {};
      options.workData = {};

      var service = _self.getWorkService();
      var params = _self.getWorkServiceParams(data);
      server.JDS.call({
        service: service,
        data: params,
        async: false,
        success: function (result) {
          options.workData = result.data;
        }
      });

      var wrapper = document.createElement("div");
      wrapper.id = "wf_work_view";
      var pageContainer = appContext.getPageContainer();
      var renderPlaceholder = pageContainer.getRenderPlaceholder();
      renderPlaceholder[0].appendChild(wrapper);
      formBuilder.buildPanel({
        title: "",
        container: "#wf_work_view"
      });
      $.ui.loadContent("#wf_work_view");
      $("#wf_work_view").workView(options);
    },
    getSelectedRowIds: function (multiple) {
      var _self = this;
      var selection = _self.getWidget().getSelections();
      if (selection.length === 0) {
        appModal.error("请选择记录！");
        return;
      }
      if (!multiple && selection.length > 1) {
        appModal.error("只能选择一条记录！");
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
      var selection = _self.getWidget().getSelections();
      var rowData = selection[0];
      appContext.require(["WorkFlowApprove"], function (WorkFlowApprove) {
        var options = {
          ui: _self.getWidget(),
          formUuid: rowData.formUuid,
          dataUuid: rowData.dataUuid,
          linkUrl: _self.getLinkUrl(rowData),
          linkTitle: rowData.title,
          autoSubmit: false,
          successCallback: function () {
            appModal.success("送审批成功！");
          }
        };
        var workFlowApprove = new WorkFlowApprove(options);
        workFlowApprove.sendToApprove();
      });
    },
    getLinkUrl: function (rowData) {
      var url = ctx + "/workflow/work/v53/view/work?taskInstUuid={0}&flowInstUuid={1}";
      var taskInstUuid = rowData.uuid || "";
      var flowInstUuid = rowData.flowInstUuid;
      var sb = new StringBuilder();
      sb.appendFormat(url, taskInstUuid, flowInstUuid);
      return sb.toString();
    },
    // 转办
    transfer: function () {
      var _self = this;
      appContext.require(["unit"], function () {
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
        appModal.error("没有权限转办所选工作！");
        return;
      }

      // 判断是否转办必填意见
      // if (isRequiredTransferOpinion($element, taskInstUuids)) {
      // openToSignOpinion($element, taskInstUuids);
      // return;
      // }
      var opinionName = ""; // getOpinionName($element,
      // taskInstUuids);
      var opinionValue = ""; // getOpinionValue($element,
      // taskInstUuids);
      var opinionText = ""; // getOpinionText($element,
      // taskInstUuids);
      $.unit.open({
        title: "选择转办人员",
        selectType: 4,
        afterSelect: function (laReturn) {
          if (StringUtils.isNotBlank(laReturn.id)) {
            var transferUserIds = laReturn.id.split(";");
            JDS.call({
              service: transferService,
              data: [taskInstUuids, transferUserIds, opinionName, opinionValue, opinionText],
              version: "",
              success: function (result) {
                appModal.info("转办成功！");
                _self.refresh(true);
                _self.wWidget.trigger(constant.WIDGET_EVENT.BadgeRefresh);
              },
              error: function (jqXHR) {
                appModal.error("转办失败！");
              }
            });
          } else {
            appModal.error("转办人员不能为空！");
          }
        }
      });
    },
    // 移交
    handOver: function () {
      var _self = this;
      appContext.require(["unit"], function () {
        _self._handOver();
      });
    },
    _handOver: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }

      // 移交权限检测
      if (_self.isAllowedHandOver(taskInstUuids) === false) {
        appModal.error("工作已结束或已被处理或没有权限，无法进行操作！");
        return;
      }

      // 判断是否特送个人必填意见
      // if (isRequiredHandOverOpinion($element, taskInstUuids)) {
      // openToSignOpinion($element, taskInstUuids);
      // return;
      // }

      var opinionName = ""; // getOpinionName($element,
      // taskInstUuids);
      var opinionValue = ""; // getOpinionValue($element,
      // taskInstUuids);
      var opinionText = ""; // getOpinionText($element,
      // taskInstUuids);
      // 移交处理
      $.unit.open({
        title: "选择移交人员",
        selectType: 4,
        afterSelect: function (laReturn) {
          if (StringUtils.isNotBlank(laReturn.id)) {
            var handOverUserIds = laReturn.id.split(";");
            JDS.call({
              service: handOverService,
              data: [taskInstUuids, handOverUserIds, opinionName, opinionValue, opinionText],
              version: "",
              success: function (result) {
                appModal.info("移交成功!");
                _self.refresh(true);
                _self.wWidget.trigger(constant.WIDGET_EVENT.BadgeRefresh);
              },
              error: function (jqXHR) {
                appModal.error("移交失败！");
              }
            });
          } else {
            appModal.error("请选择移交人员！");
          }
        }
      });
    },
    doGotoRow: function (event, options, row) {
      var _self = this;
      var taskInstUuids = [row["uuid"]];
      _self._doGoto(taskInstUuids);
    },
    doGoto: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(false);
      if (!taskInstUuids) {
        return;
      }
      _self._doGoto(taskInstUuids);
    },
    // 跳转
    _doGoto: function (taskInstUuids) {
      var _self = this;
      $.each(taskInstUuids, function (i, taskInstUuid) {
        // 特送环节处理
        var gotoTaskCallback = function () {
          var success = function () {
            _self.deleteWorkFlow(taskInstUuid);
            _self.onGotoTaskSuccess.apply(_self, arguments);
          };
          var failure = function () {
            _self.onGotoTaskFailure.apply(_self, arguments);
          };
          _self.workFlow = _self.getWorkFlow(taskInstUuid);
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
      })
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
      appModal.success("特送环节成功！");
      _self.refresh(true);
      _self.wWidget.trigger(constant.WIDGET_EVENT.BadgeRefresh);
    },
    onGotoTaskFailure: function (jqXHR) {
      var _self = this;
      var callback = function () {
        _self.doGoto();
      };
      _self.handlerError.call(_self, jqXHR, callback);
    },
    markReadRow: function (event, options, row) {
      var _self = this;
      var taskInstUuids = [row["uuid"]];
      _self._markRead(taskInstUuids);
    },
    markRead: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }
      _self._markRead(taskInstUuids);
    },
    // 工作标记已阅
    _markRead: function (taskInstUuids) {
      var _self = this;

      JDS.call({
        service: markReadService,
        data: [taskInstUuids],
        success: function (result) {
          appModal.info("标记已阅成功！");
          _self.refresh(true);
          _self.refreshMessageTabContainer();
          _self.wWidget.trigger(constant.WIDGET_EVENT.BadgeRefresh);
        },
        error: function (jqXHR) {
          appModal.error("标记已阅失败！");
        }
      });
    },
    markUnreadRow: function (event, options, row) {
      var _self = this;
      var taskInstUuids = [row["uuid"]];
      _self._markUnread(taskInstUuids);
    },
    markUnread: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }
      _self._markUnread(taskInstUuids);
    },
    // 工作标记未阅
    _markUnread: function (taskInstUuids) {
      var _self = this;

      JDS.call({
        service: markUnreadService,
        data: [taskInstUuids],
        success: function (result) {
          appModal.info("标记未阅成功！");
          _self.refresh(true);
          _self.refreshMessageTabContainer();
          _self.wWidget.trigger(constant.WIDGET_EVENT.BadgeRefresh);
        },
        error: function (jqXHR) {
          appModal.error("标记未阅失败！");
        }
      });
    },
    attentionRow: function (event, options, row) {
      var _self = this;
      var taskInstUuids = [row["uuid"]];
      _self._attention(taskInstUuids);
    },
    attention: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }
      _self._attention(taskInstUuids);
    },
    // 关注
    _attention: function (taskInstUuids) {
      var _self = this;
      JDS.call({
        service: attentionService,
        data: [taskInstUuids],
        success: function (result) {
          appModal.info("关注成功！");
          _self.refresh(true);
          _self.wWidget.trigger(constant.WIDGET_EVENT.BadgeRefresh);
        },
        error: function (jqXHR) {
          appModal.error("关注失败！");
        }
      });
    },
    unfollowRow: function (event, options, row) {
      var _self = this;
      var taskInstUuids = [row["uuid"]];
      _self._unfollow(taskInstUuids);
    },
    unfollow: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }
      _self._unfollow(taskInstUuids);
    },
    // 取消关注
    _unfollow: function (taskInstUuids) {
      var _self = this;
      JDS.call({
        service: unfollowService,
        data: [taskInstUuids],
        success: function (result) {
          appModal.info("取消关注成功！");
          _self.refresh(true);
          _self.wWidget.trigger(constant.WIDGET_EVENT.BadgeRefresh);
        },
        error: function (jqXHR) {
          appModal.error("取消关注失败！");
        }
      });
    },
    cancelRow: function (event, options, row) {
      var _self = this;
      var taskInstUuids = [row["uuid"]];
      _self._cancel(taskInstUuids);
    },
    cancel: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds();
      if (!taskInstUuids) {
        return;
      }
      _self._cancel(taskInstUuids);
    },
    // 撤回
    _cancel: function (taskInstUuids) {
      var _self = this;
      // 撤回权限检测
      if (_self.isAllowedCancel(taskInstUuids) === false) {
        appModal.error("没有权限或不能撤回所选工作！");
        return;
      }
      appModal.confirm("确认撤回？", function (result) {
        if (result) {
          JDS.call({
            service: cancelService,
            data: [taskInstUuids],
            success: function (result) {
              appModal.info("撤回成功！");
              _self.refresh(true);
              _self.wWidget.trigger(constant.WIDGET_EVENT.BadgeRefresh);
            },
            error: function (jqXHR) {
              _self.handlerError(jqXHR);
            }
          });
        }
      });
    },
    remindRow: function (event, options, row) {
      var _self = this;
      var taskInstUuids = [row["uuid"]];
      _self._remind(taskInstUuids);
    },
    remind: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }
      _self._remind(taskInstUuids);
    },
    // 催办
    _remind: function (taskInstUuids) {
      var _self = this;

      // 催办必填意见
      // var opinionName = _self.getOpinionName(taskInstUuids);
      // var opinionValue = _self.getOpinionValue(taskInstUuids);
      // var opinionText = _self.getOpinionText(taskInstUuids);
      // if (StringUtils.isBlank(opinionText)) {
      // _self.openToSignOpinion(taskInstUuids);
      // return;
      // }

      JDS.call({
        service: remindService,
        data: [taskInstUuids, null, null, null],
        success: function (result) {
          appModal.info("催办成功！");
          _self.refresh();
        },
        error: function (jqXHR) {
          appModal.error("催办失败！");
        }
      });
    },
    deleteDraftRow: function (event, options, row) {
      var _self = this;
      var flowInstUuids = [row["uuid"]];
      _self._deleteDraft(flowInstUuids);
    },
    deleteDraft: function () {
      var _self = this;
      var flowInstUuids = _self.getSelectedRowIds(true);
      if (!flowInstUuids) {
        return;
      }
      _self._deleteDraft(flowInstUuids);
    },
    // 删除草搞
    _deleteDraft: function (flowInstUuids) {
      var _self = this;
      appModal.confirm("确定删除工作草稿？", function (result) {
        if (result) {
          JDS.call({
            service: deleteDraftService,
            data: [flowInstUuids],
            success: function (result) {
              appModal.success("删除成功！");
              _self.refresh(true);
              _self.wWidget.trigger(constant.WIDGET_EVENT.BadgeRefresh);
            }
          });
        }
      });
    },
    deleteTaskRow: function (event, options, row) {
      var _self = this;
      var taskInstUuids = [row["uuid"]];
      _self._deleteTask(taskInstUuids);
    },
    deleteTask: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }
      _self._deleteTask(taskInstUuids);
    },
    // 删除工作
    _deleteTask: function (taskInstUuids) {
      var _self = this;

      appModal.confirm("确认删除工作(只有处于拟稿环节的待办工作才能删除)？", function (result) {
        if (result) {
          JDS.call({
            service: deleteService,
            data: [taskInstUuids],
            success: function (result) {
              appModal.success("删除成功！");
              _self.refresh(true);
              _self.wWidget.trigger(constant.WIDGET_EVENT.BadgeRefresh);
            },
            error: function (jqXHR) {
              _self.handlerError(jqXHR);
            }
          });
        }
      });
    },
    deleteTaskByAdminRow: function (event, options, row) {
      var _self = this;
      var taskInstUuids = [row["uuid"]];
      _self._deleteTaskByAdmin(taskInstUuids);
    },
    deleteTaskByAdmin: function () {
      var _self = this;
      var taskInstUuids = _self.getSelectedRowIds(true);
      if (!taskInstUuids) {
        return;
      }
      _self._deleteTaskByAdmin(taskInstUuids);
    },
    // 管理员删除工作
    _deleteTaskByAdmin: function (taskInstUuids) {
      var _self = this;

      appModal.confirm("确认删除工作(只有处于拟稿环节的工作才能删除)？", function (result) {
        if (result) {
          JDS.call({
            service: deleteByAdminService,
            data: [taskInstUuids],
            success: function (result) {
              appModal.success("删除成功！");
              _self.refresh(true);
              _self.wWidget.trigger(constant.WIDGET_EVENT.BadgeRefresh);
            },
            error: function (jqXHR) {
              _self.handlerError(jqXHR);
            }
          });
        }
      });
    },
    isAllowedTransfer: function (taskInstUuids) {
      return this.checkPermission(taskInstUuids, isAllowedTransferService, "isAllowedTransfer");
    },
    isAllowedHandOver: function (taskInstUuids) {
      return this.checkPermission(taskInstUuids, isAllowedHandOverService, "isAllowedHandOver");
    },
    isAllowedCancel: function (taskInstUuids) {
      return this.checkPermission(taskInstUuids, isAllowedCancelService, "isAllowedCancel");
    },
    checkPermission: function (taskInstUuids, service, suffix) {
      var $element = $(this.getWidget().element);
      var key = taskInstUuids.join("_") + "_" + suffix;
      var isAllowedTransfer = $element.data(key);
      if (isAllowedTransfer === true || isAllowedTransfer === false) {
        return isAllowedTransfer;
      } else {
        isAllowedTransfer = false;
      }
      JDS.call({
        service: service,
        data: [taskInstUuids],
        async: false,
        version: "",
        success: function (result) {
          isAllowedTransfer = result.data;
          $element.data(key, isAllowedTransfer);
        },
        error: function (jqXHR) {
          isAllowedTransfer = false;
        }
      });
      return isAllowedTransfer;
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
      var handler = _self.errorHandler || errorHandler;
      handler.handle(jqXHR, null, null, options);
    },
    //更新当前页的其他tab
    refreshMessageTabContainer: function () {
      var _self = this;
      var widgetId = _self.wWidget.element.attr("id");
      var $tab = _self.wWidget.pageContainer.element.find(".ui-wMobileTabs");
      if ($tab[0]) {
        var id = $tab.attr("id");
        var tabWidget = appContext.getWidgetById(id)
        if (id && tabWidget) {
          var tabs = tabWidget.getConfiguration().tabs || [];
          tabs.forEach(function (item, index) {
            var $id = item.id;
            var contentId = $tab.find("#" + $id + ">div").attr("id");
            if (contentId && appContext.getWidgetById(contentId)) {
              appContext.getWidgetById(contentId).refresh();
            }
          })
        }
      }
    }
  });

  return WorkFlowMobileListViewDevelopmentBase;
});
