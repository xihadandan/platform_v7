import { isFunction, isEmpty, trim, each, indexOf, map, assign } from "lodash";
import WorkFlow from "./uni-WorkFlow.js";
import WorkFlowErrorHandler from "./uni-WorkFlowErrorHandler.js";
import { workFlowUtils } from "wellapp-uni-framework";
const pageUrl = "/uni_modules/w-app/pages/workflow/work_view";

const toolbar = {
  DRAFT: ["click", "close", "save", "submit", "signOpinion", "viewFlowDesigner", "remove"],
  TODO: [
    "click",
    "close",
    "save",
    "submit",
    "rollback",
    "directRollback",
    "rollbackToMainFlow",
    "viewTheMainFlow",
    "printForm",
    "signOpinion",
    "transfer",
    "counterSign",
    "addSign",
    "attention",
    "print",
    "copyTo",
    "unfollow",
    "viewProcess",
    "suspend",
    "resume",
    "layoutDocumentProcess",
    "viewReadLog",
    "enterContinuousMode",
    "viewFlowDesigner",
    "remove",
    "complete",
  ],
  DONE: [
    "click",
    "close",
    "cancel",
    "attention",
    "print",
    "copyTo",
    "unfollow",
    "remind",
    "viewProcess",
    "viewTheMainFlow",
    "printForm",
    "viewReadLog",
    "viewFlowDesigner",
    "complete",
  ],
  OVER: [
    "click",
    "close",
    "attention",
    "print",
    "copyTo",
    "unfollow",
    "viewProcess",
    "viewTheMainFlow",
    "viewReadLog",
    "cancel",
    "viewFlowDesigner",
    "complete",
  ],
  FLAG_READ: [
    "click",
    "close",
    "attention",
    "print",
    "copyTo",
    "unfollow",
    "viewProcess",
    "viewTheMainFlow",
    "printForm",
    "viewReadLog",
    "viewFlowDesigner",
    "complete",
  ],
  UNREAD: [
    "click",
    "close",
    "attention",
    "print",
    "copyTo",
    "unfollow",
    "viewProcess",
    "viewTheMainFlow",
    "viewReadLog",
    "viewFlowDesigner",
    "complete",
  ],
  ATTENTION: [
    "click",
    "close",
    "print",
    "copyTo",
    "attention",
    "unfollow",
    "viewProcess",
    "viewTheMainFlow",
    "viewReadLog",
    "viewFlowDesigner",
    "complete",
  ],
  SUPERVISE: [
    "close",
    "attention",
    "print",
    "copyTo",
    "unfollow",
    "viewProcess",
    "remind",
    "suspend",
    "resume",
    "viewTheMainFlow",
    "viewReadLog",
    "viewFlowDesigner",
    "complete",
  ],
  MONITOR: [
    "close",
    "attention",
    "print",
    "copyTo",
    "unfollow",
    "viewProcess",
    "remind",
    "handOver",
    "gotoTask",
    "suspend",
    "resume",
    "viewReadLog",
    "viewTheMainFlow",
    "viewFlowDataSnapshot",
    "removeByAdmin",
    "viewFlowDesigner",
    "complete",
  ],
};
const rightMap = {
  B004000: "click", // 点击
  B004001: "save", // 保存
  B004002: "submit", // 提交
  B004003: "rollback", // 退回
  B004004: "directRollback", // 直接退回
  B004005: "cancel", // 撤回
  B004006: "transfer", // 转办
  B004007: "counterSign", // 会签
  B004008: "attention", // 关注
  // B004009: 'print', // 套打
  B004010: "copyTo", // 抄送
  B004011: "signOpinion", // 签署意见
  B004012: "unfollow", // 取消关注
  B004013: "viewProcess", // 查看办理过程
  B004014: "remind", // 催办
  B004015: "handOver", // 移交
  B004016: "gotoTask", // 跳转
  B004017: "suspend", // 挂起
  B004018: "resume", // 恢复
  B004019: "close", // 关闭
  B004020: "submitAndPrint", // 提交并套打
  B004023: "remove", // 删除
  B004024: "removeByAdmin", // 管理员删除
  B004025: "editable", // 可编辑文档
  B004026: "requiredSubmitOpinion", // 必须签署意见
  B004039: "requiredCancelOpinion", // 撤回必填意见
  B004029: "requiredTransferOpinion", // 转办必填意见
  B004030: "requiredCounterSignOpinion", // 会签必填意见
  B004031: "requiredRollbackOpinion", // 退回必填意见
  B004032: "requiredHandOverOpinion", // 特送个人必填意见
  B004033: "requiredGotoTaskOpinion", // 特送环节必填意见
  B004034: "requiredRemindOpinion", // 催办环节必填意见
  B004035: "rollbackToMainFlow", // 退回主流程
  B004095: "viewTheMainFlow", //查看主流程
  B004096: "printForm", //打印表单
  B004097: "layoutDocumentProcess", // 版式文档处理
  B004037: "viewReadLog", // 查看阅读记录
  B004038: "viewFlowDataSnapshot", // 查看流程数据快照
  B004040: "enterContinuousMode", //连续签批模式
  B004041: "viewFlowDesigner", // 查看流程图
  B004042: "addSign", // 加签
  B004044: "complete", // 完成
};
const signOpinionActionRightMap = {
  submit: "B004026", // 必须签署意见
  complete: "B004026", // 完成必填签署意见
  transfer: "B004029", // 转办必填意见
  counterSign: "B004030", // 会签必填意见
  addSign: "B004043", // 加签必填意见
  rollback: "B004031", // 退回必填意见
  directRollback: "B004031", // 退回必填意见
  rollbackToMainFlow: "B004031", // 退回必填意见
  handOver: "B004032", // 特送个人必填意见
  gotoTask: "B004033", // 特送环节必填意见
  remind: "B004034", // 催办环节必填意见
  cancel: "B004039", // 撤回必填意见
};
//流程意见校验设置_场景
var sceneEnum = {
  SUBMIT: "S001", //提交
  RETURN: "S002", //退回
  TURN_TO: "S003", //转办
  COUNTERSIGN: "S004", //会签
};
var WorkView = function (options) {
  this.options = options;
  this.$widget = options.$widget;
  this.loading = options.loading;
  this.methods = {};
  this.init();
  this.submitButtonCode = "B004002"; // 提交按钮代码
  this.submitAndPrintButtonCode = "B004020"; // 打印并套打按钮代码
  this.rollbackButtonCode = "B004003"; // 退回按钮代码
  this.cancelButtonCode = "B004005"; // 撤回按钮代码
  this.viewProcessButtonCode = "B004013"; // 查看办理过程按钮代码
  this.gotoTaskButtonCode = "B004016"; // 跳转按钮代码
};
// 初始化
WorkView.prototype.init = function () {
  var _self = this;
  var options = _self.options;
  var workData = options.workData;
  _self.workFlow = new WorkFlow(workData, _self);
  // _self.workProcess = new WorkProcess(workData, _self);
  // 异常处理器
  _self.errorHandler = new WorkFlowErrorHandler(_self);
  // 表单数据不存在，加载流程单据
  if (workData.dyFormData == null) {
    let ajaxUrl = "/api/workflow/work/getWorkDataByWorkBean";
    _self.workFlow.load(ajaxUrl, "POST", function (result) {
      workData.dyFormData = result.data.data.dyFormData;
      if (options.onLoad) {
        options.onLoad.call(_self);
      }
    });
  } else {
    if (options.onLoad) {
      options.onLoad.call(_self);
    }
  }
};
// 获取工作数据
WorkView.prototype.getWorkData = function () {
  if (this.workFlow) {
    return this.workFlow.getWorkData();
  }
  return { extraParams: {} };
};
// 表单数据是否可编辑
WorkView.prototype.isDyformEditable = function () {
  var _self = this;
  if (_self.options.dyformEditable != null) {
    return _self.options.dyformEditable;
  }
  var workData = _self.getWorkData();
  // 新版流程定义通过“环节属性->可编辑表单”选项控制是否可编辑文档
  if (workData.canEditForm != null) {
    return workData.canEditForm;
  }
  // 可编辑文档参数
  // var editableCode = _self.options.dyformEditableCode;
  // var btnSelector = ":button[name='" + editableCode + "']";
  // var dyformEditable = $(btnSelector, _self.options.toolbarPlaceholder).length > 0;
  // return dyformEditable;
  return false;
};
// 获取办理过程数据
WorkView.prototype.getWorkProcess = function () {
  return this.workProcess.getWorkProcess();
};
// 获取页面参数值
WorkView.prototype.getQueryString = function (name, defaultValue) {
  var values = this.$widget.options[name];
  if (values != null) {
    return values;
  }
  if (defaultValue != null) {
    return defaultValue;
  }
  return null;
};
// 是否新建工作
WorkView.prototype.isNewWork = function () {
  if (isEmpty(this.options.taskInstUuid) && isEmpty(this.options.flowInstUuid)) {
    return true;
  }
  return false;
};
// 是否草稿
WorkView.prototype.isDraft = function () {
  return this.isWorkDataMatchAclRole("DRAFT");
};
// 是否待办工作
WorkView.prototype.isTodo = function () {
  return this.isWorkDataMatchAclRole("TODO");
};
// 是否已办工作
WorkView.prototype.isDone = function () {
  return this.isWorkDataMatchAclRole("DONE");
};
// 是否办结工作
WorkView.prototype.isOver = function () {
  return this.isWorkDataMatchAclRole("OVER");
};
// 是否关注工作
WorkView.prototype.isAttention = function () {
  return this.isWorkDataMatchAclRole("ATTENTION");
};
// 是否督办工作
WorkView.prototype.isSupervise = function () {
  return this.isWorkDataMatchAclRole("SUPERVISE");
};
// 是否监控工作
WorkView.prototype.isMonitor = function () {
  return this.isWorkDataMatchAclRole("MONITOR");
};
WorkView.prototype.isWorkDataMatchAclRole = function (aclRole) {
  var _self = this;
  if (_self.workFlow == null) {
    return _self.options.aclRole == aclRole;
  }
  var workData = _self.workFlow.getWorkData();
  if (workData.aclRole === aclRole) {
    return true;
  }
  return false;
};
WorkView.prototype.hasActionByCode = function (code) {
  var _self = this;
  var workData = _self.getWorkData();
  var buttons = workData.buttons || [];
  for (var i = 0; i < buttons.length; i++) {
    if (buttons[i].code == code) {
      return true;
    }
  }
  return false;
};
WorkView.prototype.getActions = function () {
  var _self = this;
  if (_self.actions) {
    return _self.actions;
  }
  var workData = _self.getWorkData();
  var aclOpts = toolbar[workData.aclRole];
  var buttons = workData.buttons || [];
  var actions = (_self.actions = []);
  const isValidJSON = (text) => {
    try {
      if (!text) {
        return false;
      }
      JSON.parse(text);
      return true;
    } catch (e) {
      return false;
    }
  };
  // 是否多角色时流程操作按角色隔离
  let aclRoleIsolation = _self.isAclRoleIsolation();
  each(buttons, function (button) {
    // 按钮名称
    var name = button.name;
    // 按钮编号
    var code = button.code;
    // 按钮ID
    var btnId = button.id;
    // 操作名称
    var methodName = rightMap[code];
    // 按钮图标、按钮类型
    let icon, type;
    if (isValidJSON(button.className)) {
      // 新版
      const className = JSON.parse(button.className);
      icon = className.icon;
      type = className.type;
    } else if (button.btnIcon) {
      icon = button.btnIcon;
    }
    if ((indexOf(aclOpts, methodName) >= 0 || !aclRoleIsolation) && _self[methodName] != null) {
      actions.push({
        id: btnId,
        code,
        name,
        text: name,
        methodName,
        icon,
        type,
        eventHandler: undefined,
      });
    } else if (button.eventHandler) {
      actions.push({
        id: btnId,
        code,
        name,
        text: name,
        methodName,
        icon,
        type,
        eventHandler: JSON.parse(button.eventHandler),
      });
    } else {
      // 操作设置自定义操作按钮
      if (!_self.actionButtonMap) {
        let actionSetting = (_self.options.settings && _self.options.settings.ACTION) || {};
        let buttons = actionSetting.buttons || [];
        _self.customButtonMap = {};
        buttons
          .filter((button) => !button.buildIn)
          .forEach((button) => {
            _self.customButtonMap[button.code] = button;
          });
      }
      if (_self.customButtonMap[code]) {
        let button = _self.customButtonMap[code];
        actions.push({
          id: code,
          code,
          name,
          text: name,
          icon: button.style && button.style.icon,
          type: button.style && button.style.type,
          eventHandler: button.eventHandler,
        });
      }
    }
  });

  // 判断签署意见是否显示为按钮
  if (!_self.isShowSignOpinionInToolbar()) {
    let actionIndex = actions.findIndex((action) => action.methodName == "signOpinion");
    if (actionIndex != -1) {
      actions.splice(actionIndex, 1);
    }
  }
  // 判断查看办理过程是否显示为按钮
  if (!_self.isShowViewProcessInToolbar()) {
    let actionIndex = actions.findIndex((action) => action.methodName == "viewProcess");
    if (actionIndex != -1) {
      actions.splice(actionIndex, 1);
    }
  }
  // 判断查看流程图是否显示为按钮
  if (!_self.isShowViewFlowDesignerInToolbar()) {
    let actionIndex = actions.findIndex((action) => action.methodName == "viewFlowDesigner");
    if (actionIndex != -1) {
      actions.splice(actionIndex, 1);
    }
  }

  // 判断是否启用连续签批
  if (_self.isEnabledContinuousWorkView()) {
    let requestCode = _self.getQueryString("_requestCode");
    let cwvDataStoreParams = uni.getStorageSync(`cwvDataStoreParams_${requestCode}`);
    if (isEmpty(cwvDataStoreParams) || _self.$widget.options.continuousMode == "1") {
      let actionIndex = actions.findIndex((action) => action.methodName == "enterContinuousMode");
      if (actionIndex != -1) {
        actions.splice(actionIndex, 1);
      }
    }
  } else {
    let actionIndex = actions.findIndex((action) => action.methodName == "enterContinuousMode");
    if (actionIndex != -1) {
      actions.splice(actionIndex, 1);
    }
  }
  return actions;
};
// 是否多角色时流程操作按角色隔离
WorkView.prototype.isAclRoleIsolation = function () {
  let generalSetting = (this.options.settings && this.options.settings.GENERAL) || {};
  return generalSetting.aclRoleIsolation;
};
// 是否显示签署意见按钮
WorkView.prototype.isShowSignOpinionInToolbar = function () {
  let opinionEditorSetting = (this.options.settings && this.options.settings.OPINION_EDITOR) || {};
  return opinionEditorSetting.showMode == "modal" || opinionEditorSetting.showMode == "all";
};
// 判断查看办理过程是否显示为按钮
WorkView.prototype.isShowViewProcessInToolbar = function () {
  let processViewerSetting = (this.options.settings && this.options.settings.PROCESS_VIEWER) || {};
  return processViewerSetting.showMode == "modal" || processViewerSetting.showMode == "all";
};
// 判断查看流程图是否显示为按钮
WorkView.prototype.isShowViewFlowDesignerInToolbar = function () {
  let processViewerSetting = (this.options.settings && this.options.settings.PROCESS_VIEWER) || {};
  return processViewerSetting.designerShowMode == "modal" || processViewerSetting.designerShowMode == "all";
};
// 判断是否启用连续签批
WorkView.prototype.isEnabledContinuousWorkView = function () {
  let generalSetting = (this.options.settings && this.options.settings.GENERAL) || {};
  return generalSetting.enabledContinuousWorkView;
};
// 设置事件按钮名称
WorkView.prototype.setEventAction = function (action, actionType, workData) {
  workData.action = action;
  workData.actionType = actionType;
  let currentAction = this.currentAction;
  if (currentAction && currentAction.text) {
    workData.action = currentAction.text;
  }
};
// 存储签署意见
WorkView.prototype.storeOpinion = function () {
  let opinionEditor = this.getOpinionEditor();
  if (opinionEditor) {
    opinionEditor.store();
  }
};
// 还原签署意见
WorkView.prototype.restoreOpinion = function () {
  let opinionEditor = this.getOpinionEditor();
  if (opinionEditor) {
    opinionEditor.restore();
  }
};
// 设置是否必填意见立场
WorkView.prototype.setRequiredOpinionPosition = function (requiredOpinionPosition) {
  this.requiredOpinionPosition = requiredOpinionPosition;
};
// 是否必填意见立场
WorkView.prototype.isRequiredOpinionPosition = function () {
  return this.requiredOpinionPosition == null ? false : this.requiredOpinionPosition;
};
// 设置办理过程查看器
WorkView.prototype.setProcessViewer = function (processViewer) {
  this.processViewer = processViewer;
};
// 获取办理过程查看器
WorkView.prototype.getProcessViewer = function () {
  return this.processViewer;
};
// 设置意见编辑器
WorkView.prototype.setOpinionEditor = function (opinionEditor) {
  this.opinionEditor = opinionEditor;
};
// 获取意见编辑器
WorkView.prototype.getOpinionEditor = function () {
  return this.opinionEditor;
};
// 办理意见是否变更
WorkView.prototype.isOpinionChanged = function () {
  return this.opinionEditor && this.opinionEditor.isOpinionChanged();
};
// 打开提交签署意见
WorkView.prototype.openToSignIfRequiredSubmit = function () {
  return this.openToSignIfRequired("提交", "submit");
};
// 打开完成签署意见
WorkView.prototype.openToSignIfRequiredComplete = function () {
  return this.openToSignIfRequired("完成", "complete");
};
// 打开退回签署意见
WorkView.prototype.openToSignIfRequiredRollback = function () {
  return this.openToSignIfRequired("退回", "rollback");
};
// 打开直接退回签署意见
WorkView.prototype.openToSignIfRequiredDirectRollback = function () {
  return this.openToSignIfRequired("退回", "directRollback");
};
// 打开退回主流程签署意见
WorkView.prototype.openToSignIfRequiredRollbackToMainFlow = function () {
  return this.openToSignIfRequired("退回", "rollbackToMainFlow");
};
// 打开撤回签署意见
WorkView.prototype.openToSignIfRequiredCancel = function () {
  return this.openToSignIfRequired("撤回", "cancel");
};
// 打开转办签署意见
WorkView.prototype.openToSignIfRequiredTransfer = function () {
  return this.openToSignIfRequired("转办", "transfer");
};
// 打开会签签署意见
WorkView.prototype.openToSignIfRequiredCounterSign = function () {
  return this.openToSignIfRequired("会签", "counterSign");
};
// 打开加签签署意见
WorkView.prototype.openToSignIfRequiredAddSign = function () {
  return this.openToSignIfRequired("加签", "addSign");
};
// 打开催办签署意见
WorkView.prototype.openToSignIfRequiredRemind = function () {
  return this.openToSignIfRequired("催办", "remind");
};
// 打开特送个人签署意见
WorkView.prototype.openToSignIfRequiredHandOver = function () {
  return this.openToSignIfRequired("特送个人", "handOver");
};
// 打开特送环节签署意见
WorkView.prototype.openToSignIfRequiredGotoTask = function () {
  return this.openToSignIfRequired("特送环节", "gotoTask");
};
// 根据需要打开签署意见，需要签署意见返回true,否则返回false
WorkView.prototype.openToSignIfRequired = function (defaultLabel, action) {
  var _self = this;
  // 签署意见权限判断
  var rightCode = signOpinionActionRightMap[action];
  if (isEmpty(rightCode)) {
    return false;
  }
  if (!_self.hasActionByCode(rightCode)) {
    return false;
  }

  // 必须签署意见
  var workData = _self.getWorkData();
  var opinionText = workData.opinionText;
  if (!isEmpty(opinionText)) {
    if (!_self.isRequiredOpinionPosition()) {
      return false;
    } else if (!isEmpty(workData.opinionValue)) {
      return false;
    }
  }

  var label = _self.getButtonLabel(defaultLabel);
  var options = {};
  var callback = _self._getActionCallback(action);
  var buttons = {
    confirm: {
      label: label,
      className: "btn-primary",
      callback: callback,
      callbackScope: _self,
    },
  };
  options.buttons = buttons;
  options.action = action;
  // 打开签署意见
  _self.$widget.openToSignOpinion(options);
  return true;
};
// 意见收集到工作流数据
WorkView.prototype.opinionToWorkData = function (data) {
  var _self = this;
  if (data == null) {
    return;
  }
  var workData = _self.workFlow.getWorkData();
  workData.opinionLabel = data.label;
  workData.opinionValue = data.value;
  workData.opinionText = data.text;
  workData.opinionFiles = data.files;
};
// 按钮操作后回调
WorkView.prototype.callbackAfterAction = function (func, success, originalArgs) {
  func.apply(
    this,
    [
      {
        success: success,
        args: originalArgs,
      },
    ].concat(Array.prototype.slice.call(originalArgs))
  );
};
// 获取表单对象
WorkView.prototype.getDyform = function () {
  return this.dyform;
};
// 验证表单数据
WorkView.prototype.validateDyformData = function (workData, callback) {
  if (workData.todoType === 2) {
    callback.call(this, true);
  } else {
    this.dyform.validateFormData(callback);
  }
};
// 校验签署意见是否符合规则
WorkView.prototype._isOpinionRuleCheck = function (workData, scene, callback) {
  var _self = this;
  var opinionText = workData.opinionText;
  var flowId = workData.flowDefId;
  var taskId = workData.taskId;
  uni.request({
    service: "wfOpinionCheckSetFacadeService.isOpinionRuleCheck",
    data: [opinionText, flowId, taskId, scene],
    async: false,
    success: function (result) {
      var isOpinionRuleCheckDto = result.data.data;
      if (!isOpinionRuleCheckDto.success) {
        var options = {};
        if (isOpinionRuleCheckDto.alertAutoClose) {
          options = {
            title: "提示框", // 标题
            message: isOpinionRuleCheckDto.message, // 消息内容，不能为空
            type: "error", // 消息类型:success、info、warning、error、alert
            autoClose: true, // 是否自动关闭，true为自动关闭，不显示关闭按钮，false为手动关闭，显示关闭按钮
            timer: 3000,
          };
        } else {
          options = {
            title: "提示框", // 标题
            message: isOpinionRuleCheckDto.message, // 消息内容，不能为空
            type: "error", // 消息类型:success、info、warning、error、alert
            autoClose: false, // 是否自动关闭，true为自动关闭，不显示关闭按钮，false为手动关闭，显示关闭按钮
            timer: null,
          };
        }
        uni.showToast({ title: options.message });
      } else if (callback) {
        callback.call(_self);
      }
    },
    error: function () {
      uni.showToast({ title: "校验签署意见是否符合规则时出错！" });
    },
  });
};
// 添加附加参数
WorkView.prototype.addExtraParam = function (paramName, paramValue) {
  let workData = this.workFlow.getWorkData();
  workData.extraParams[paramName] = paramValue;
};
// 获取附加参数
WorkView.prototype.getExtraParam = function (paramName) {
  let workData = this.workFlow.getWorkData();
  return workData.extraParams[paramName];
};
// 获取当前事件
WorkView.prototype.getCurrentEvent = function () {
  return this.currentEvent;
};
// 设置当前事件
WorkView.prototype.setCurrentEvent = function (event) {
  this.currentEvent = event;
};
// 设置当前事件
WorkView.prototype.setCurrentAction = function (action) {
  this.currentAction = action;
};
// 自定义动态按钮提交
WorkView.prototype.collectIfUseCustomDynamicButton = function () {
  var _self = this;
  var currentAction = _self.currentAction;
  if (currentAction == null) {
    return;
  }
  var workData = _self.workFlow.getWorkData();
  var btnId = currentAction.id;
  var btn_submit = _self.submitButtonCode;
  // 处理自定义动态按钮
  var customDynamicButton = {};
  if (btnId !== btn_submit) {
    customDynamicButton.id = btnId;
    customDynamicButton.code = currentAction.name;
    customDynamicButton.newCode = btnId;
  } else {
    customDynamicButton = null;
  }
  workData.submitButtonId = btnId;
  workData.customDynamicButton = customDynamicButton;
};
// 自定义动态按钮退回
WorkView.prototype.collectIfUseCustomRollbackDynamicButton = function () {
  var _self = this;
  var currentAction = _self.currentAction;
  if (currentAction == null) {
    return;
  }
  var workData = _self.workFlow.getWorkData();
  var btnId = currentAction.id;
  var btn_rollback = _self.rollbackButtonCode;
  // 处理自定义动态按钮
  var customDynamicButton = {};
  if (btnId !== btn_rollback) {
    customDynamicButton.id = btnId;
    customDynamicButton.code = currentAction.name;
    customDynamicButton.newCode = btnId;
  } else {
    customDynamicButton = null;
  }
  workData.customDynamicButton = customDynamicButton;
};
// 自定义动态按钮跳转
WorkView.prototype.collectIfUseCustomGotoTaskDynamicButton = function () {
  var _self = this;
  var currentAction = _self.currentAction;
  if (currentAction == null) {
    return;
  }
  var workData = _self.workFlow.getWorkData();
  var btnId = currentAction.id;
  var btn_gotoTask = _self.gotoTaskButtonCode;
  // 处理自定义动态按钮
  var customDynamicButton = {};
  if (btnId !== btn_gotoTask) {
    customDynamicButton.id = btnId;
    customDynamicButton.code = currentAction.name;
    customDynamicButton.newCode = btnId;
  } else {
    customDynamicButton = null;
  }
  workData.customDynamicButton = customDynamicButton;
};
// 获取当前操作的按钮名称
WorkView.prototype.getButtonLabel = function (defaultLabel) {
  var _self = this;
  var currentAction = _self.currentAction;
  if (currentAction == null) {
    return defaultLabel;
  }
  var label = trim(currentAction.text);
  if (isEmpty(label)) {
    return defaultLabel;
  }
  return label;
};
WorkView.prototype._getActionCallback = function (action) {
  var _self = this;
  return function () {
    _self[action].call(_self);
  };
};
// 失败处理
WorkView.prototype.handlerError = function (jqXHR, callback) {
  var _self = this;
  var options = {};
  options.callback = callback;
  options.callbackContext = _self;
  options.workFlow = _self.workFlow;
  _self.errorHandler.handle(jqXHR, null, null, options);
};
WorkView.prototype.onError = function (type, options) {
  this.$widget[type](options);
};
WorkView.prototype.getMsgTips = function (todoUser, msgPre, nextTasks) {
  var msgName = "";
  var userName = [];
  if (todoUser && Object.keys(todoUser).length > 0) {
    for (var k in todoUser) {
      var users = todoUser[k];
      userName.push(users);
    }
  }

  if (userName.length > 0 || nextTasks.name != "") {
    msgName = msgPre + userName.join("，") + (nextTasks.name ? " | " + nextTasks.name : "") + "！";
  }
  return msgName;
};
// 重新加载容器
WorkView.prototype.reload = function () {
  var _self = this;
  _self.allowUnloadWorkData = true;
  var workData = _self.workFlow.getWorkData();
  var taskInstUuid = workData.taskInstUuid;
  var flowInstUuid = workData.flowInstUuid;
  if (!isEmpty(trim(taskInstUuid))) {
    _self.reloadCurrentPage();
  } else if (!isEmpty(trim(flowInstUuid))) {
    var refreshUrl = pageUrl + "?aclRole=DRAFT&flowInstUuid=" + flowInstUuid;
    // 关闭当前页面，跳转到应用内的某个页面。
    uni.redirectTo({
      url: _self.addSystemPrefix(refreshUrl),
    });
  } else {
    _self.reloadCurrentPage();
  }
};
WorkView.prototype.reloadCurrentPage = function (options = {}) {
  var _self = this;
  // 页面重载
  const pages = getCurrentPages();
  // 声明一个pages使用getCurrentPages方法
  const curPage = pages[pages.length - 1];
  options = assign({}, curPage.options, options);
  let params = [];
  for (const key in options) {
    if (Object.hasOwnProperty.call(options, key)) {
      params.push(key + "=" + options[key]);
    }
  }
  let refreshUrl = pageUrl + "?" + params.join("&");
  // 关闭当前页面，跳转到应用内的某个页面。
  uni.redirectTo({
    url: _self.addSystemPrefix(refreshUrl),
  });
  // 执行刷新
};
WorkView.prototype.reloadLater = function () {
  var _self = this;
  setTimeout(function () {
    _self.reload();
  }, 1000);
};
// 保存
WorkView.prototype.save = function () {
  var _self = this;
  var workData = _self.workFlow.getWorkData();
  // 操作动作及类型
  _self.setEventAction("保存", "Save", workData);
  // 获取表单数据
  workData.dyFormData = _self.dyform.collectFormData().dyFormData;
  var success = function () {
    // 还原签署意见
    _self.restoreOpinion();
    if (isFunction(_self.onAfterSave)) {
      _self.callbackAfterAction(_self.onAfterSave, true, arguments);
    } else {
      _self.onSaveSuccess.apply(_self, arguments);
    }
  };
  var failure = function () {
    if (isFunction(_self.onAfterSave)) {
      _self.callbackAfterAction(_self.onAfterSave, false, arguments);
    } else {
      _self.onSaveFailure.apply(_self, arguments);
    }
  };
  // 存储签署意见
  _self.storeOpinion();
  _self.workFlow.save(success, failure);
};
WorkView.prototype.onSaveSuccess = function (result) {
  var _self = this;
  let data = result.data && result.data.data;
  let workData = _self.getWorkData();
  if (!workData.flowInstUuid && data.flowInstUuid) {
    workData.flowInstUuid = data.flowInstUuid;
  }
  let message = "保存成功！";
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: "save",
    result,
    message,
  });
  // uni.showToast({ title: "保存成功！" });
  // _self.reloadLater();
};
WorkView.prototype.onSaveFailure = function (jqXHR) {
  var _self = this;
  var callback = _self._getActionCallback("save");
  this.handlerError.call(_self, jqXHR, callback);
};
// 提交
WorkView.prototype.submit = function () {
  var _self = this;
  // 签署意见
  if (_self.openToSignIfRequiredSubmit.apply(_self, arguments)) {
    return;
  }
  _self.opinionToWorkData();
  var workData = _self.getWorkData();
  // 操作动作及类型
  _self.setEventAction("提交", "Submit", workData);
  // 获取表单数据
  workData.dyFormData = _self.dyform.collectFormData().dyFormData;
  // 是否提交后进行套打
  workData.printAfterSubmit = false;
  var success = function () {
    if (isFunction(_self.onAfterSubmit)) {
      _self.callbackAfterAction(_self.onAfterSubmit, true, arguments);
    } else {
      _self.onSubmitSuccess.apply(_self, arguments);
    }
  };
  var failure = function () {
    if (isFunction(_self.onAfterSubmit)) {
      _self.callbackAfterAction(_self.onAfterSubmit, false, arguments);
    } else {
      _self.onSubmitFailure.apply(_self, arguments);
    }
  };
  try {
    // 会签待办提交不进行表单验证
    _self.validateDyformData(workData, function (validate) {
      if (!validate) {
        return;
      }
      // 签署意见校验
      var opinionRuleCheckCallback = function () {
        // 收集自定义按钮操作的信息
        _self.collectIfUseCustomDynamicButton();
        _self.workFlow.submit(success, failure);
      };
      _self._isOpinionRuleCheck(workData, sceneEnum.SUBMIT, opinionRuleCheckCallback);
    });
  } catch (e) {
    uni.showToast({ title: "表单数据验证出错" + e + "，无法提交数据！" });
    throw e;
  }
};
WorkView.prototype._saveUserSubmit = function (result) {
  let _self = this;
  let data = result.data.data;
  let signOpinionModel = _self.options.signOpinionModel || "1";
  let sameUserSubmitType = data.sameUserSubmitType;
  let submitTaskInstUuid = data.sameUserSubmitTaskInstUuid;
  let submitTaskOperationUuid = data.sameUserSubmitTaskOperationUuid;
  // 与前环节相同自动提交
  if (isEmpty(submitTaskInstUuid) && isEmpty(submitTaskOperationUuid)) {
    let refreshUrl =
      pageUrl +
      "?aclRole=TODO&flowInstUuid=" +
      data.flowInstUuid +
      "&submitTaskInstUuid=" +
      submitTaskInstUuid +
      "&sameUserSubmitType=" +
      sameUserSubmitType +
      "&submitTaskOperationUuid=" +
      submitTaskOperationUuid +
      "&ep_wf_sign_opinion_model=" +
      signOpinionModel;
    // 自动提交函数
    var autoSubmit = function (refreshUrl) {
      // 连续签批模式
      if (_self.$widget.options.lxqpMode) {
        refreshUrl += "&lxqpMode=true&_requestCode=" + _self.$widget.options._requestCode;
      }
      uni.redirectTo({
        url: _self.addSystemPrefix(refreshUrl),
      });
    };
    var nextTasks = result.data.nextTasks || [];
    if (nextTasks.length > 1) {
      nextTasks = [nextTasks[nextTasks.length - 1]];
    }
    if (nextTasks.length > 0) {
      $.each(nextTasks, function (tIndex, tItem) {
        var taskTodoUsers = result.data.taskTodoUsers[tItem.uuid];
        var msgToUser = _self.getMsgTips(taskTodoUsers, "已提交至", tItem);
        // 自动提交
        _self.emitActionSuccess({
          action: "submit",
          result,
          message: "提交成功！" + msgToUser,
          msgCallback: () => {
            autoSubmit(refreshUrl);
          },
        });
      });
    } else {
      // 自动提交
      _self.emitActionSuccess({
        action: "submit",
        result,
        message: "提交成功！",
        msgCallback: () => {
          autoSubmit(refreshUrl);
        },
      });
    }

    return true;
  }
  return false;
};
WorkView.prototype.emitRefreshAndGoBackLater = function (actionName) {
  uni.$emit("refresh", {
    msg: actionName + "success",
  });
  setTimeout(function () {
    uni.navigateBack({
      delta: 1,
    });
  }, 1000);
};
WorkView.prototype.emitActionSuccess = function (opt) {
  this.$widget.$emit("actionSuccess", opt);
};
// 提交成功
WorkView.prototype.onSubmitSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;
  // 下载打印结果
  // _self._downloadPrintResult(result);
  // 与前环节相同自动提交
  if (_self._saveUserSubmit(result)) {
    return;
  }

  var data = result.data.data;
  var nextTasks = data.nextTasks || [];
  if (nextTasks.length > 1) {
    nextTasks = [nextTasks[nextTasks.length - 1]];
  }
  if (nextTasks.length > 0) {
    each(nextTasks, function (tItem) {
      var taskTodoUsers = data.taskTodoUsers[tItem.uuid];
      var msgToUser = _self.getMsgTips(taskTodoUsers, "已提交至", tItem);
      var message = "提交成功！" + msgToUser;
      _self.emitActionSuccess({
        action: "submit",
        result,
        message,
      });
    });
  } else {
    var message = "提交成功！";
    // 触发操作成功事件
    _self.emitActionSuccess({
      action: "submit",
      result,
      message,
    });
  }
  // 触发刷新事件并返回上一页面
  // _self.emitRefreshAndGoBackLater("submit");
};
// 提交失败
WorkView.prototype.onSubmitFailure = function (jqXHR) {
  var _self = this;
  var callback = _self._getActionCallback("submit");
  _self.handlerError.call(_self, jqXHR, callback);
}; // 完成
WorkView.prototype.complete = function () {
  const _this = this;
  // 签署意见
  if (_this.openToSignIfRequiredComplete.apply(_this, arguments)) {
    return;
  }
  _this.opinionToWorkData();
  let workData = _this.getWorkData();
  // 操作动作及类型
  _this.setEventAction("完成", "Complete", workData);
  // 获取表单数据
  workData.dyFormData = _this.dyform.collectFormData().dyFormData;
  let success = function () {
    if (isFunction(_this.onAfterComplete)) {
      _this.callbackAfterAction(_this.onAfterComplete, true, arguments);
    } else {
      _this.onCompleteSuccess.apply(_this, arguments);
    }
  };
  let failure = function () {
    if (isFunction(_this.onAfterComplete)) {
      _this.callbackAfterAction(_this.onAfterComplete, false, arguments);
    } else {
      _this.onCompleteFailure.apply(_this, arguments);
    }
  };
  try {
    // 签署意见校验
    let opinionRuleCheckCallback = function () {
      _this.workFlow.complete(success, failure);
    };
    _this._isOpinionRuleCheck(workData, sceneEnum.SUBMIT, opinionRuleCheckCallback);
    // });
  } catch (e) {
    uni.showToast({ title: "表单数据验证出错" + e + "，无法提交数据！" });
    throw e;
  }
};
// 完成成功
WorkView.prototype.onCompleteSuccess = function (result) {
  this.onSubmitSuccess(result);
};
// 完成失败
WorkView.prototype.onCompleteFailure = function (jqXHR) {
  const _this = this;
  let callback = _this._getActionCallback("complete");
  _this.handlerError.call(_this, jqXHR, callback);
};
// 退回
WorkView.prototype.rollback = function () {
  var _self = this;
  // 签署意见
  if (_self.openToSignIfRequiredRollback.apply(_self, arguments)) {
    return;
  }
  _self.opinionToWorkData();
  var workData = _self.workFlow.getWorkData();
  // 签署意见校验
  var opinionRuleCheckCallback = function () {
    // 操作动作及类型
    _self.setEventAction("退回", "Rollback", workData);
    // 获取表单数据
    workData.dyFormData = _self.dyform.collectFormData().dyFormData;
    workData.rollbackToPreTask = false;
    var success = function () {
      if (isFunction(_self.onAfterRollback)) {
        _self.callbackAfterAction(_self.onAfterRollback, true, arguments);
      } else {
        _self.onRollbackSuccess.apply(_self, arguments);
      }
    };
    var failure = function () {
      if (isFunction(_self.onAfterRollback)) {
        _self.callbackAfterAction(_self.onAfterRollback, false, arguments);
      } else {
        _self.onRollbackFailure.apply(_self, arguments);
      }
    };
    // 收集自定义按钮操作的信息
    _self.collectIfUseCustomRollbackDynamicButton();
    _self.workFlow.rollback(success, failure);
  };
  _self._isOpinionRuleCheck(workData, sceneEnum.RETURN, opinionRuleCheckCallback);
};
// 是否退回到自己
WorkView.prototype.rollbackToSelf = function () {
  return false;
};
WorkView.prototype.onRollbackSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;
  if (_self.rollbackToSelf(result)) {
    return;
  }

  var msgToUser = "";
  var data = result.data.data;
  var nextTasks = data.nextTasks;
  if (nextTasks.length > 0) {
    var taskTodoUsers = data.taskTodoUsers[nextTasks[0].uuid];
    msgToUser = this.getMsgTips(taskTodoUsers, "已退回至", nextTasks[0]);
  }

  // 提示
  let message = "退回成功！" + msgToUser;
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: "rollback",
    result,
    message,
  });
  // uni.showToast({ title: "退回成功！" + msgToUser });
  // // 触发刷新事件并返回上一页面
  // _self.emitRefreshAndGoBackLater("rollback");
};
WorkView.prototype.onRollbackFailure = function (jqXHR) {
  var _self = this;
  var callback = _self._getActionCallback("rollback");
  _self.handlerError.call(_self, jqXHR, callback);
};
// 直接退回
WorkView.prototype.directRollback = function () {
  var _self = this;
  // 签署意见
  if (_self.openToSignIfRequiredDirectRollback.apply(_self, arguments)) {
    return;
  }
  _self.opinionToWorkData();
  var workData = _self.workFlow.getWorkData();
  // 签署意见校验
  var opinionRuleCheckCallback = function () {
    // 操作动作及类型
    _self.setEventAction("直接退回", "DirectRollback", workData);
    // 获取表单数据
    workData.dyFormData = _self.dyform.collectFormData().dyFormData;
    workData.rollbackToPreTask = true;
    var success = function () {
      if (isFunction(_self.onAfterDirectRollback)) {
        _self.callbackAfterAction(_self.onAfterDirectRollback, true, arguments);
      } else {
        _self.onDirectRollbackSuccess.apply(_self, arguments);
      }
    };
    var failure = function () {
      if (isFunction(_self.onAfterDirectRollback)) {
        _self.callbackAfterAction(_self.onAfterDirectRollback, false, arguments);
      } else {
        _self.onDirectRollbackFailure.apply(_self, arguments);
      }
    };
    _self.workFlow.rollback(success, failure);
  };
  _self._isOpinionRuleCheck(workData, sceneEnum.RETURN, opinionRuleCheckCallback);
};
WorkView.prototype.onDirectRollbackSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;
  var data = result.data.data;
  var nextTasks = data.nextTasks;
  var msgToUser = "";
  if (nextTasks.length > 0) {
    var taskTodoUsers = data.taskTodoUsers[nextTasks[0].uuid];
    msgToUser = _self.getMsgTips(taskTodoUsers, "已退回至", nextTasks[0]);
  }

  // 提示
  let message = "直接退回成功！" + msgToUser;
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: "directRollback",
    result,
    message,
  });
  // uni.showToast({ title: "直接退回成功！" + msgToUser });
  // // 触发刷新事件并返回上一页面
  // _self.emitRefreshAndGoBackLater("directRollback");
};
WorkView.prototype.onDirectRollbackFailure = function (jqXHR) {
  var _self = this;
  var callback = function () {
    _self.directRollback();
  };
  _self.handlerError.call(_self, jqXHR, callback);
};
// 退回主流程
WorkView.prototype.rollbackToMainFlow = function () {
  var _self = this;
  // 签署意见
  if (_self.openToSignIfRequiredRollbackToMainFlow.apply(_self, arguments)) {
    return;
  }
  _self.opinionToWorkData();
  var workData = _self.workFlow.getWorkData();
  // 操作动作及类型
  _self.setEventAction("退回主流程", "RollbackToMainFlow", workData);
  // 获取表单数据
  workData.dyFormData = _self.dyform.collectFormData().dyFormData;
  workData.rollbackToPreTask = true;
  var success = function () {
    if (isFunction(_self.onAfterRollbackToMainFlow)) {
      _self.callbackAfterAction(_self.onAfterRollbackToMainFlow, true, arguments);
    } else {
      _self.onRollbackToMainFlowSuccess.apply(_self, arguments);
    }
  };
  var failure = function () {
    if (isFunction(_self.onAfterRollbackToMainFlow)) {
      _self.callbackAfterAction(_self.onAfterRollbackToMainFlow, false, arguments);
    } else {
      _self.onRollbackToMainFlowFailure.apply(_self, arguments);
    }
  };
  _self.workFlow.rollbackToMainFlow(success, failure);
};
WorkView.prototype.onRollbackToMainFlowSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;

  var data = result.data.data;
  var nextTasks = data.nextTasks || [];
  var msgToUser = "";
  if (nextTasks.length > 0) {
    var taskTodoUsers = result.data.taskTodoUsers[nextTasks[0].uuid];
    msgToUser = _self.getMsgTips(taskTodoUsers, "已退回至", nextTasks[0]);
  }
  // 提示
  // _self.showSuccess({ title: '退回成功！' + msgToUser });
  let message = "退回成功！" + msgToUser;
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: "rollbackToMainFlow",
    result,
    message,
  });
};
WorkView.prototype.onRollbackToMainFlowFailure = function (jqXHR) {
  var _self = this;
  var callback = function () {
    _self.directRollback();
  };
  _self.handlerError.call(_self, jqXHR, callback);
};
// 查看主流程
WorkView.prototype.viewTheMainFlow = function () {
  const _self = this;
  if (_self.$widget.subTaskData) {
    let subTaskData = _self.$widget.subTaskData || _self.$widget.branchTaskData;
    let url = `${pageUrl}?taskInstUuid=${subTaskData.parentTaskInstUuid}&flowInstUuid=${subTaskData.parentFlowInstUuid}&viewTheMainFlow=true`;
    uni.navigateTo({
      url: _self.addSystemPrefix(url),
    });
  }
};
// 撤回
WorkView.prototype.cancel = function () {
  var _self = this;
  // 签署意见
  if (_self.openToSignIfRequiredCancel.apply(_self, arguments)) {
    return;
  }
  _self.opinionToWorkData();
  var workData = _self.workFlow.getWorkData();
  // 操作动作及类型
  _self.setEventAction("撤回", "Cancel", workData);
  // 获取表单数据
  workData.dyFormData = _self.dyform.collectFormData().dyFormData;
  var success = function () {
    if (isFunction(_self.onAfterCancel)) {
      _self.callbackAfterAction(_self.onAfterCancel, true, arguments);
    } else {
      _self.onCancelSuccess.apply(_self, arguments);
    }
  };
  var failure = function () {
    if (isFunction(_self.onAfterCancel)) {
      _self.callbackAfterAction(_self.onAfterCancel, false, arguments);
    } else {
      _self.onCancelFailure.apply(_self, arguments);
    }
  };
  _self.workFlow.cancel(success, failure);
};
WorkView.prototype.onCancelSuccess = function () {
  var _self = this;
  _self.allowUnloadWorkData = true;
  var callback = function () {
    // 打开待办工作界面
    var success = function (result) {
      let taskInstUuid = result.data && result.data.data && result.data.data.uuid;
      if (!isEmpty(taskInstUuid)) {
        _self.refreshTodoWork(taskInstUuid);
      } else {
        _self.handlerSuccess.call(_self, result);
      }
    };
    var failure = function (jqXHR) {
      // 处理流程操作返回的错误信息
    };
    _self.workFlow.getTodoTaskByFlowInstUuid(success, failure);
  };
  // 提示
  _self.emitActionSuccess({
    action: "cancel",
    message: "撤回成功！",
    msgCallback: callback,
  });
  // 触发刷新事件并返回上一页面
  // _self.emitRefreshAndGoBackLater("cancel");
};
WorkView.prototype.onCancelFailure = function () {
  this.handlerError.apply(this, arguments);
};
// 转办
WorkView.prototype.transfer = function () {
  var _self = this;
  // 签署意见
  if (_self.openToSignIfRequiredTransfer.apply(_self, arguments)) {
    return;
  }
  _self.opinionToWorkData();
  var workData = _self.workFlow.getWorkData();
  // 签署意见校验
  var opinionRuleCheckCallback = function () {
    // 操作动作及类型
    _self.setEventAction("转办", "Transfer", workData);
    // 获取表单数据
    workData.dyFormData = _self.dyform.collectFormData().dyFormData;
    var success = function () {
      if (isFunction(_self.onAfterTransfer)) {
        _self.callbackAfterAction(_self.onAfterTransfer, true, arguments);
      } else {
        _self.onTransferSuccess.apply(_self, arguments);
      }
    };
    var failure = function () {
      if (isFunction(_self.onAfterTransfer)) {
        _self.callbackAfterAction(_self.onAfterTransfer, false, arguments);
      } else {
        _self.onTransferFailure.apply(_self, arguments);
      }
    };
    _self.workFlow.transfer([], success, failure);
  };
  _self._isOpinionRuleCheck(workData, sceneEnum.TURN_TO, opinionRuleCheckCallback);
};
// 转办成功
WorkView.prototype.onTransferSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;
  var msgToUser = "已转办至" + _self.workFlow.transferUsers.join("，");

  // 提示
  let message = "转办成功！" + msgToUser;
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: "transfer",
    result,
    message,
  });
  // uni.showToast({ title: "转办成功！" + msgToUser });
  // // 触发刷新事件并返回上一页面
  // _self.emitRefreshAndGoBackLater("transfer");
};
// 转办失败
WorkView.prototype.onTransferFailure = function (jqXHR) {
  var _self = this;
  var callback = _self._getActionCallback("transfer");
  _self.handlerError.call(_self, jqXHR, callback);
};
// 查看阅读记录
WorkView.prototype.viewReadLog = function () {
  const _self = this;
  const workData = _self.getWorkData();
  let taskInstUuid = workData.taskInstUuid;
  workFlowUtils.jsonDataService("workService", "viewReadLog", [taskInstUuid]).then((result) => {
    var data = result.data || {};
    Date.prototype.format = function (fmt) {
      // author: meizz
      var o = {
        "M+": this.getMonth() + 1, // 月份
        "d+": this.getDate(), // 日
        "H+": this.getHours(), // 小时
        "m+": this.getMinutes(), // 分
        "s+": this.getSeconds(), // 秒
        "q+": Math.floor((this.getMonth() + 3) / 3), // 季度
        S: this.getMilliseconds(),
        // 毫秒
      };
      if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
      }
      for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
          fmt = fmt.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
      }
      return fmt;
    };
    var getReadTimeString = function (readTime) {
      if (!readTime) {
        return "";
      }
      var timestring = new Date(readTime);
      var now = new Date();
      if (now.getFullYear() != timestring.getFullYear()) {
        readTime = timestring.format("yyyy-MM-dd HH:mm"); //往年查阅的，显示年月日时分
      } else {
        readTime = timestring.format("MM-dd HH:mm"); //当年查阅的，仅显示月日时分
        if (now.format("MM-dd") == timestring.format("MM-dd")) {
          readTime = timestring.format("HH:mm"); //当天查阅的，仅显示时分
        }
      }
      return readTime;
    };
    each(data.readUser, function (record) {
      record.readTimeString = getReadTimeString(record.readTime);
    });

    _self.$widget.readLogVisible = true;
    _self.$widget.readLogData = data;
  });
};
// 会签
WorkView.prototype.counterSign = function () {
  var _self = this;
  // 签署意见
  if (_self.openToSignIfRequiredCounterSign.apply(_self, arguments)) {
    return;
  }
  _self.opinionToWorkData();
  var workData = _self.workFlow.getWorkData();
  // 签署意见校验
  var opinionRuleCheckCallback = function () {
    // 操作动作及类型
    _self.setEventAction("会签", "CounterSign", workData);
    // 获取表单数据
    workData.dyFormData = _self.dyform.collectFormData().dyFormData;
    var success = function () {
      if (isFunction(_self.onAfterCounterSign)) {
        _self.callbackAfterAction(_self.onAfterCounterSign, true, arguments);
      } else {
        _self.onCounterSignSuccess.apply(_self, arguments);
      }
    };
    var failure = function () {
      if (isFunction(_self.onAfterCounterSign)) {
        _self.callbackAfterAction(_self.onAfterCounterSign, false, arguments);
      } else {
        _self.onCounterSignFailure.apply(_self, arguments);
      }
    };

    _self.workFlow.counterSign([], success, failure);
  };
  _self._isOpinionRuleCheck(workData, sceneEnum.COUNTERSIGN, opinionRuleCheckCallback);
};
WorkView.prototype.onCounterSignSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;
  var workData = _self.workFlow.getWorkData();
  var action = workData.action || "会签";
  var msgToUser = "已" + action + "至" + this.signUsers.join("，") + "！";

  // 提示
  let message = action + "成功！" + msgToUser;
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: "counterSign",
    result,
    message,
  });
  // uni.showToast({ title: action + "成功！" + msgToUser });
  // // 触发刷新事件并返回上一页面
  // _self.emitRefreshAndGoBackLater("counterSign");
};
WorkView.prototype.onCounterSignFailure = function (jqXHR) {
  const _self = this;
  let callback = _self._getActionCallback("counterSign");
  _self.handlerError.call(_self, jqXHR, callback);
};
// 加签
WorkView.prototype.addSign = function () {
  const _self = this;
  // 签署意见
  if (_self.openToSignIfRequiredAddSign.apply(_self, arguments)) {
    return;
  }
  _self.opinionToWorkData();
  let workData = _self.workFlow.getWorkData();
  // 签署意见校验
  let opinionRuleCheckCallback = function () {
    // 操作动作及类型
    _self.setEventAction("加签", "AddSign", workData);
    // 获取表单数据
    workData.dyFormData = _self.dyform.collectFormData().dyFormData;
    let success = function () {
      if (isFunction(_self.onAfterAddSign)) {
        _self.callbackAfterAction(_self.onAfterAddSign, true, arguments);
      } else {
        _self.onAddSignSuccess.apply(_self, arguments);
      }
    };
    let failure = function () {
      if (isFunction(_self.onAfterAddSign)) {
        _self.callbackAfterAction(_self.onAfterAddSign, false, arguments);
      } else {
        _self.onAddSignFailure.apply(_self, arguments);
      }
    };
    let addSignUserIds = [];
    if (workData.taskAddSignUsers) {
      addSignUserIds = workData.taskAddSignUsers[workData.taskId] || [];
    }
    _self.workFlow.addSign(addSignUserIds, success, failure);
  };
  _self._isOpinionRuleCheck(workData, sceneEnum.COUNTERSIGN, opinionRuleCheckCallback);
};
WorkView.prototype.onAddSignSuccess = function (result) {
  const _self = this;
  _self.allowUnloadWorkData = true;
  let workData = _self.workFlow.getWorkData();
  let action = workData.action || "加签";
  let msgToUser = "已" + action + "至" + _self.workFlow.addSignUsers.join("，") + "！";

  // 提示
  // _self.showToast({ title: action + '成功！' + msgToUser });
  let message = action + "成功！" + msgToUser;
  if (_self.isTodo()) {
    uni.showToast({ title: message, duration: 3000, success: function () {} });
  } else {
    // 触发操作成功事件
    _self.emitActionSuccess({
      action: "addSign",
      result,
      message,
    });
  }
};
WorkView.prototype.onAddSignFailure = function (jqXHR) {
  const _self = this;
  let callback = _self._getActionCallback("addSign");
  _self.handlerError.call(_self, jqXHR, callback);
};
// 抄送
WorkView.prototype.copyTo = function () {
  var _self = this;
  var workData = _self.workFlow.getWorkData();
  var success = function () {
    if (isFunction(_self.onAfterCopyTo)) {
      _self.callbackAfterAction(_self.onAfterCopyTo, true, arguments);
    } else {
      _self.onCopyToSuccess.apply(_self, arguments);
    }
    workData.taskCopyUsers = {};
  };
  var failure = function () {
    if (isFunction(_self.onAfterCopyTo)) {
      _self.callbackAfterAction(_self.onAfterCopyTo, false, arguments);
    } else {
      _self.onCopyToFailure.apply(_self, arguments);
    }
  };
  let copyToUserIds = [];
  if (workData.taskCopyUsers) {
    copyToUserIds = workData.taskCopyUsers[workData.taskId] || [];
  }
  _self.workFlow.copyTo(copyToUserIds, success, failure);
};
WorkView.prototype.onCopyToSuccess = function (result) {
  const _this = this;
  let msgToUser = "已抄送至" + this.workFlow.copyToUsers.join("，") + "！";
  // 提示
  // _self.showToast({ title: action + '成功！' + msgToUser });
  let message = "抄送成功！" + msgToUser;
  // 触发操作成功事件
  _this.emitActionSuccess({
    action: "copyTo",
    result,
    message,
  });
};
WorkView.prototype.onCopyToFailure = function (jqXHR) {
  const _self = this;
  let callback = _self._getActionCallback("copyTo");
  _self.handlerError.call(_self, jqXHR, callback);
};
// 关注
WorkView.prototype.attention = function () {
  var _self = this;
  var workData = _self.workFlow.getWorkData();
  var taskInstUuids = [];
  taskInstUuids.push(workData.taskInstUuid);
  var success = function () {
    if (isFunction(_self.onAfterAttention)) {
      _self.callbackAfterAction(_self.onAfterAttention, true, arguments);
    } else {
      _self.onAttentionSuccess.apply(_self, arguments);
    }
  };
  var failure = function () {
    if (isFunction(_self.onAfterAttention)) {
      _self.callbackAfterAction(_self.onAfterAttention, false, arguments);
    } else {
      _self.onAttentionFailure.apply(_self, arguments);
    }
  };
  _self.workFlow.attention(taskInstUuids, success, failure);
};
WorkView.prototype.onAttentionSuccess = function (result) {
  // 提示
  var _self = this;
  let message = "已关注！";
  _self.emitActionSuccess({
    action: "attention",
    result,
    message,
  });
  // uni.showToast({ title: "已关注！" });
  // _self.reloadLater();
};
WorkView.prototype.onAttentionFailure = function () {
  this.handlerError.apply(this, arguments);
};
// 取消关注
WorkView.prototype.unfollow = function () {
  var _self = this;
  var workData = _self.workFlow.getWorkData();
  var taskInstUuids = [];
  taskInstUuids.push(workData.taskInstUuid);
  var success = function () {
    if (isFunction(_self.onAfterUnfollow)) {
      _self.callbackAfterAction(_self.onAfterUnfollow, true, arguments);
    } else {
      _self.onUnfollowSuccess.apply(_self, arguments);
    }
  };
  var failure = function () {
    if (isFunction(_self.onAfterUnfollow)) {
      _self.callbackAfterAction(_self.onAfterUnfollow, false, arguments);
    } else {
      _self.onUnfollowFailure.apply(_self, arguments);
    }
  };
  _self.workFlow.unfollow(taskInstUuids, success, failure);
};
WorkView.prototype.onUnfollowSuccess = function (result) {
  // 提示
  var _self = this;
  let message = "已取消关注！";
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: "unfollow",
    result,
    message,
  });
  // _self.reloadLater();
};
WorkView.prototype.onUnfollowFailure = function () {
  this.handlerError.apply(this, arguments);
}; // 套打
WorkView.prototype.print = function () {
  var _self = this;
  var printSuccess = function (result) {
    if (isFunction(_self.onAfterPrint)) {
      _self.callbackAfterAction(_self.onAfterPrint, true, arguments);
    } else {
      _self.onPrintSuccess.apply(_self, arguments);
    }
  };
  var printFailure = function () {
    if (isFunction(_self.onAfterPrint)) {
      _self.callbackAfterAction(_self.onAfterPrint, false, arguments);
    } else {
      _self.onPrintFailure.apply(_self, arguments);
    }
  };
  var success = function (result) {
    var templates = result.data.data;
    if (templates.length === 0) {
      _self.showToast({ title: "流程没有配置套打模板！" });
    } else if (templates.length === 1) {
      let template = templates[0];
      _self.workFlow.print(template.id, template.uuid, "zh-CN", printSuccess, printFailure);
    } else {
      // 选择套打模板
      _self.$widget.showPrintTemplates({
        templates,
        callback: function (template) {
          _self.workFlow.print(template.id, template.uuid, "zh-CN", printSuccess, printFailure);
        },
      });
    }
  };
  var failure = function () {
    _self.showToast({
      title: "获取套打模板报错！",
      msgIcon: "error",
    });
  };
  _self.workFlow.getPrintTemplates(success, failure);
};
WorkView.prototype.onPrintSuccess = function (result) {
  var _self = this;
  _self._downloadPrintResult(result);
  // 提示
  // _self.showSuccess({ title: '套打成功，开始下载套打结果文件！' });
  let message = "套打成功，开始下载套打结果文件！";
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: "print",
    result,
    message,
  });
};
WorkView.prototype.onPrintFailure = function (jqXHR) {
  this.handlerError.apply(this, arguments);
};
// 催办
WorkView.prototype.remind = function () {
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
    if (isFunction(_self.onAfterRemind)) {
      _self.callbackAfterAction(_self.onAfterRemind, true, arguments);
    } else {
      _self.onRemindSuccess.apply(_self, arguments);
    }
  };
  var failure = function () {
    if (isFunction(_self.onAfterRemind)) {
      _self.callbackAfterAction(_self.onAfterRemind, false, arguments);
    } else {
      _self.onRemindFailure.apply(_self, arguments);
    }
  };
  _self.workFlow.remind(taskInstUuids, opinionLabel, opinionValue, opinionText, success, failure);
};
WorkView.prototype.onRemindSuccess = function (result) {
  var _self = this;
  // 提示
  let message = "催办成功！";
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: "remind",
    result,
    message,
  });
};
WorkView.prototype.onRemindFailure = function () {
  this.handlerError.apply(this, arguments);
};
// 移交
WorkView.prototype.handOver = function () {
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
  workData.dyFormData = _self.dyform.collectFormData().dyFormData;
  var success = function () {
    if (isFunction(_self.onAfterHandOver)) {
      _self.callbackAfterAction(_self.onAfterHandOver, true, arguments);
    } else {
      _self.onHandOverSuccess.apply(_self, arguments);
    }
  };
  var failure = function () {
    if (isFunction(_self.onAfterHandOver)) {
      _self.callbackAfterAction(_self.onAfterHandOver, false, arguments);
    } else {
      _self.onHandOverFailure.apply(_self, arguments);
    }
  };
  WorkFlowErrorHandler.prototype.openOrgSelect({
    title: "选择特送的人员",
    type: "all",
    multiple: true,
    selectTypes: "all",
    valueFormat: "justId",
    locale: _self.$widget.locale,
    workData,
    workFlow: _self.workFlow,
    callback: function (values) {
      if (values && values.length > 0) {
        let handOverUserIds = values;
        _self.workFlow.handOver(handOverUserIds, success, failure);
      } else {
        _self.showToast({ title: "请选择移交人员！" });
        _self.handOver();
      }
    },
  });
};
// 检查环节锁
WorkView.prototype.checkTaskLock = function () {
  return true;
};
WorkView.prototype.onHandOverSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;
  // 提示
  let message = "特送个人成功！";
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: "handOver",
    result,
    message,
  });
  // uni.showToast({ title: "特送个人成功！" });
  // // 触发刷新事件并返回上一页面
  // _self.emitRefreshAndGoBackLater("handOver");
};
WorkView.prototype.onHandOverFailure = function () {
  this.handlerError.apply(this, arguments);
};
// 跳转
WorkView.prototype.gotoTask = function () {
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
  workData.dyFormData = _self.dyform.collectFormData().dyFormData;
  var success = function () {
    if (isFunction(_self.onAfterGotoTask)) {
      _self.callbackAfterAction(_self.onAfterGotoTask, true, arguments);
    } else {
      _self.onGotoTaskSuccess.apply(_self, arguments);
    }
  };
  var failure = function () {
    if (isFunction(_self.onAfterGotoTask)) {
      _self.callbackAfterAction(_self.onAfterGotoTask, false, arguments);
    } else {
      _self.onGotoTaskFailure.apply(_self, arguments);
    }
  };
  // 收集自定义按钮操作的信息
  _self.collectIfUseCustomGotoTaskDynamicButton();
  _self.workFlow.gotoTask(success, failure);
};
WorkView.prototype.onGotoTaskSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;
  // 提示
  let message = "特送环节成功！";
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: "gotoTask",
    result,
    message,
  });
  // uni.showToast({ title: "特送环节成功！" });
  // // 触发刷新事件并返回上一页面
  // _self.emitRefreshAndGoBackLater("gotoTask");
};
WorkView.prototype.onGotoTaskFailure = function (jqXHR) {
  var _self = this;
  var callback = function () {
    _self.gotoTask();
  };
  _self.handlerError.call(_self, jqXHR, callback);
};
// 挂起
WorkView.prototype.suspend = function () {
  var _self = this;
  var workData = _self.workFlow.getWorkData();
  var taskInstUuids = [];
  taskInstUuids.push(workData.taskInstUuid);
  var success = function () {
    if (isFunction(_self.onAfterSuspend)) {
      _self.callbackAfterAction(_self.onAfterSuspend, true, arguments);
    } else {
      _self.onSuspendSuccess.apply(_self, arguments);
    }
  };
  var failure = function () {
    if (isFunction(_self.onAfterSuspend)) {
      _self.callbackAfterAction(_self.onAfterSuspend, false, arguments);
    } else {
      _self.onSuspendFailure.apply(_self, arguments);
    }
  };
  _self.workFlow.suspend(taskInstUuids, success, failure);
};
WorkView.prototype.onSuspendSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;
  var actionName = _self.getButtonLabel("挂起");
  // 提示
  var message = actionName + "成功！";
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: "suspend",
    result,
    message,
  });
  // 触发刷新事件并返回上一页面
  // _self.emitRefreshAndGoBackLater("suspend");
};
WorkView.prototype.onSuspendFailure = function () {
  this.handlerError.apply(this, arguments);
};
// 恢复
WorkView.prototype.resume = function () {
  var _self = this;
  var workData = _self.workFlow.getWorkData();
  var taskInstUuids = [];
  taskInstUuids.push(workData.taskInstUuid);
  var success = function () {
    if (isFunction(_self.onAfterResume)) {
      _self.callbackAfterAction(_self.onAfterResume, true, arguments);
    } else {
      _self.onResumeSuccess.apply(_self, arguments);
    }
  };
  var failure = function () {
    if (isFunction(_self.onAfterResume)) {
      _self.callbackAfterAction(_self.onAfterResume, false, arguments);
    } else {
      _self.onResumeFailure.apply(_self, arguments);
    }
  };
  _self.workFlow.resume(taskInstUuids, success, failure);
};
WorkView.prototype.onResumeSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;
  var actionName = _self.getButtonLabel("恢复");
  // 提示
  let message = actionName + "成功！";
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: "resume",
    result,
    message,
  });
  // 触发刷新事件并返回上一页面
  // _self.emitRefreshAndGoBackLater("resume");
};
WorkView.prototype.onResumeFailure = function () {
  this.handlerError.apply(this, arguments);
};
// 删除
WorkView.prototype.remove = function () {
  var _self = this;
  var workData = _self.workFlow.getWorkData();
  var taskInstUuids = [];
  taskInstUuids.push(workData.taskInstUuid);
  var success = function () {
    if (isFunction(_self.onAfterRemove)) {
      _self.callbackAfterAction(_self.onAfterRemove, true, arguments);
    } else {
      _self.onRemoveSuccess.apply(_self, arguments);
    }
  };
  var failure = function () {
    if (isFunction(_self.onAfterRemove)) {
      _self.callbackAfterAction(_self.onAfterRemove, false, arguments);
    } else {
      _self.onRemoveFailure.apply(_self, arguments);
    }
  };
  _self.workFlow.remove(taskInstUuids, success, failure);
};
WorkView.prototype.onRemoveSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;
  // 提示
  let message = "删除成功！";
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: "remove",
    result,
    message,
  });
  // 触发刷新事件并返回上一页面
  // _self.emitRefreshAndGoBackLater("remove");
};
WorkView.prototype.onRemoveFailure = function () {
  this.handlerError.apply(this, arguments);
}; // 进入连续签批模式
WorkView.prototype.enterContinuousMode = function () {
  const _this = this;
  let callback = () => {
    _this.reloadCurrentPage({ continuousMode: "1" });
  };
  callback();
};
// 退出连续签批模式
WorkView.prototype.exitContinuousMode = function () {
  const _this = this;
  if (this.$widget.options.continuousMode == "1") {
    _this.reloadCurrentPage({ continuousMode: "0" });
  }
};
// 获取流程设置
WorkView.prototype.getSettings = function () {
  return this.workFlow.getSettings();
};
WorkView.prototype.getSubmitActionCode = function () {
  return this.workFlow.actionCode.submit;
};
// 是否允许提交
WorkView.prototype.isAllowSubmit = function () {
  return this.hasActionByCode(this.workFlow.actionCode.submit);
};
// 是否允许签署意见
WorkView.prototype.isAllowSignOpinion = function () {
  const _self = this;
  if (_self.hasActionByCode(_self.workFlow.actionCode.signOpinion)) {
    return true;
  }
  for (let key in signOpinionActionRightMap) {
    let actionCode = signOpinionActionRightMap[key];
    if (_self.hasActionByCode(actionCode)) {
      return true;
    }
  }
  return false;
};
// 是否允许查看办理过程
WorkView.prototype.isAllowViewProcess = function () {
  const _self = this;
  const workData = _self.getWorkData();
  const buttons = workData.buttons || [];
  // 送审批的流程实例uuid
  let approveFlowInstUuid = "";
  let split = location.href.split("&");
  each(split, function (urlParam) {
    let approveFlowInstUuidKey = "approveFlowInstUuid=";
    let indexOf = urlParam.indexOf(approveFlowInstUuidKey);
    if (indexOf > -1) {
      approveFlowInstUuid = urlParam.substring(indexOf + approveFlowInstUuidKey.length, urlParam.length);
    }
  });
  if (!isEmpty(trim(approveFlowInstUuid))) {
    return true;
  }

  // 草稿不可查看办理过程
  if (_self.isDraft()) {
    return false;
  }

  // 办理过程权限
  if (_self.hasActionByCode(_self.workFlow.actionCode.viewProcess)) {
    return true;
  }

  return false;
};
// 刷新待办工作
WorkView.prototype.refreshTodoWork = function (taskInstUuid) {
  var _self = this;
  this.allowUnloadWorkData = true;
  var workData = _self.workFlow.getWorkData();
  var flowInstUuid = workData.flowInstUuid;
  var refreshUrl = pageUrl + "?aclRole=TODO&flowInstUuid=" + flowInstUuid + "&taskInstUuid=" + taskInstUuid;
  // 关闭当前页面，跳转到应用内的某个页面。
  uni.redirectTo({
    url: _self.addSystemPrefix(refreshUrl),
  });
};
WorkView.prototype.addSystemPrefix = function (url) {
  const _this = this;
  if (_this.$widget._$SYSTEM_ID && url && url.indexOf("system=prod_") == -1) {
    url += `&system=${_this.$widget._$SYSTEM_ID}`;
  }
  return url;
};
// 加载遮罩
WorkView.prototype.showLoading = function (tip) {
  const _self = this;
  uni.showLoading({
    title: tip || "",
  });
};
// 隐藏遮罩
WorkView.prototype.hideLoading = function () {
  uni.hideLoading();
};
WorkView.prototype.showToast = function (opt) {
  uni.showToast({
    title: opt.title || "",
    duration: opt.timer,
    icon: opt.msgIcon || "success",
  });
  this._msgCallback(opt);
};
WorkView.prototype._msgCallback = function (opt) {
  const _self = this;
  if (opt.timer) {
    setTimeout(() => {
      if (isFunction(opt.callback)) {
        opt.callback.call(_self);
      }
    }, opt.timer);
  } else if (isFunction(opt.callback)) {
    opt.callback.call(_self);
  }
};

module.exports = WorkView;
