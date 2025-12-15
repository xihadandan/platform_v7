'use strict';
import { isEmpty, isFunction, trim as stringTrim, each as forEach, indexOf, isArray } from 'lodash';
import { generateId, download, deepClone } from '@framework/vue/utils/util';
import WorkFlow from './WorkFlow.js';
import WorkFlowErrorHandler, { getMsgI18ns } from './WorkFlowErrorHandler.js';
import WorkProcess from './WorkProcess.js';

const toolbar = {
  DRAFT: ['click', 'close', 'save', 'submit', 'signOpinion', 'viewFlowDesigner', 'remove'],
  TODO: [
    'click',
    'close',
    'save',
    'submit',
    'rollback',
    'directRollback',
    'rollbackToMainFlow',
    'viewTheMainFlow',
    'printForm',
    'signOpinion',
    'transfer',
    'counterSign',
    'addSign',
    'attention',
    'print',
    'copyTo',
    'unfollow',
    'viewProcess',
    'suspend',
    'resume',
    'layoutDocumentProcess',
    'viewReadLog',
    'enterContinuousMode',
    'viewFlowDesigner',
    'remove',
    'recover',
    'complete',
    'startGroupChat'
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
    'printForm',
    'viewReadLog',
    'viewFlowDesigner',
    'complete'
  ],
  OVER: [
    'click',
    'close',
    'attention',
    'print',
    'copyTo',
    'unfollow',
    'viewProcess',
    'viewTheMainFlow',
    'viewReadLog',
    'cancel',
    'viewFlowDesigner',
    'complete'
  ],
  FLAG_READ: [
    'click',
    'close',
    'attention',
    'print',
    'copyTo',
    'unfollow',
    'viewProcess',
    'viewTheMainFlow',
    'printForm',
    'viewReadLog',
    'viewFlowDesigner',
    'complete'
  ],
  UNREAD: [
    'click',
    'close',
    'attention',
    'print',
    'copyTo',
    'unfollow',
    'viewProcess',
    'viewTheMainFlow',
    'viewReadLog',
    'viewFlowDesigner',
    'complete'
  ],
  ATTENTION: [
    'click',
    'close',
    'print',
    'copyTo',
    'attention',
    'unfollow',
    'viewProcess',
    'viewTheMainFlow',
    'viewReadLog',
    'viewFlowDesigner',
    'complete'
  ],
  SUPERVISE: [
    'close',
    'attention',
    'print',
    'copyTo',
    'unfollow',
    'viewProcess',
    'remind',
    'suspend',
    'resume',
    'viewTheMainFlow',
    'viewReadLog',
    'viewFlowDesigner',
    'complete'
  ],
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
    'suspend',
    'resume',
    'viewReadLog',
    'viewTheMainFlow',
    'viewFlowDataSnapshot',
    'removeByAdmin',
    'viewFlowDesigner',
    'complete'
  ],
  VIEWER: [
    'click',
    'close',
    'print',
    'copyTo',
    'attention',
    'unfollow',
    'viewProcess',
    'viewTheMainFlow',
    'viewReadLog',
    'viewFlowDesigner',
    'complete'
  ],
};
const rightMap = {
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
  B004024: 'removeByAdmin', // 管理员删除
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
  B004096: 'printForm', //打印表单
  B004097: 'layoutDocumentProcess', // 版式文档处理
  B004037: 'viewReadLog', // 查看阅读记录
  B004038: 'viewFlowDataSnapshot', // 查看流程数据快照
  B004040: 'enterContinuousMode', //连续签批模式
  B004041: 'viewFlowDesigner', // 查看流程图
  B004042: 'addSign', // 加签
  B004044: 'complete', // 完成
  B004046: 'recover', // 恢复逻辑删除
  B004050: 'startGroupChat' // 发起群聊
};
const signOpinionActionRightMap = {
  submit: 'B004026', // 必须签署意见
  complete: 'B004026', // 完成必填签署意见
  transfer: 'B004029', // 转办必填意见
  counterSign: 'B004030', // 会签必填意见
  addSign: 'B004043', // 加签必填意见
  rollback: 'B004031', // 退回必填意见
  directRollback: 'B004031', // 退回必填意见
  rollbackToMainFlow: 'B004031', // 退回必填意见
  handOver: 'B004032', // 特送个人必填意见
  gotoTask: 'B004033', // 特送环节必填意见
  remind: 'B004034', // 催办环节必填意见
  cancel: 'B004039' // 撤回必填意见
};
//流程意见校验设置_场景
var sceneEnum = {
  SUBMIT: 'S001', //提交
  RETURN: 'S002', //退回
  TURN_TO: 'S003', //转办
  COUNTERSIGN: 'S004' //会签
};

const DYFORM_SYSTEM_FIELD_SET = new Set([
  'uuid',
  'creator',
  'create_time',
  'modifier',
  'modify_time',
  'rec_ver',
  'form_uuid',
  'status',
  'signature_',
  'version',
  'system_unit_id',
  'tenant',
  'system'
]);

var WorkView = function (options) {
  this.options = options;
  this.$widget = options.$widget;
  this.loading = options.loading;
  this.methods = {};
  this.init();
};
// 初始化
WorkView.prototype.init = function () {
  var _self = this;
  var options = _self.options;
  var workData = options.workData;
  _self.version = workData ? workData.version || '1.0' : '1.0';
  _self.workFlow = new WorkFlow(workData, _self);
  _self.workProcess = new WorkProcess(workData, _self);
  // 异常处理器
  if (!EASY_ENV_IS_NODE) {
    _self.errorHandler = new WorkFlowErrorHandler(_self);
    // 表单数据不存在，加载流程单据
    if (workData.dyFormData == null) {
      let ajaxUrl = '/api/workflow/work/getWorkDataByWorkBean';
      _self.workFlow.load(ajaxUrl, 'POST', function (result) {
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
  }
};
WorkView.prototype.getMsgI18ns = function (key, msg, prefixKey) {
  if (!key) {
    return getMsgI18ns(msg, this, prefixKey);
  }
  let msgI18n = this.$widget.$t(key, null);
  if (!msgI18n) {
    msgI18n = getMsgI18ns(msg, this, prefixKey);
  }
  return msgI18n;
};
// 获取工作数据
WorkView.prototype.getWorkData = function () {
  if (this.workFlow) {
    return this.workFlow.getWorkData();
  }
  return { extraParams: {} };
};
// 获取办理过程数据
WorkView.prototype.getWorkProcess = function (displayStyle = 'standard') {
  return this.workProcess.getWorkProcess(displayStyle);
};
// 获取浏览器参数值
WorkView.prototype.getQueryString = function (name, defaultValue) {
  var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
  var values = window.location.search.substr(1).match(reg);
  if (values != null) {
    return decodeURIComponent(values[2]);
  }
  if (defaultValue != null) {
    return defaultValue;
  }
  return null;
};
WorkView.prototype.getCookie = function (name) {
  var reg = new RegExp('(^| )' + name + '=([^;]*)(;|$)');
  var arr = document.cookie.match(reg);
  if (arr) {
    return unescape(arr[2]);
  }
  return null;
};
// 是否新建工作
WorkView.prototype.isNewWork = function () {
  if (this.options.workData && isEmpty(this.options.workData.taskInstUuid)) {
    return true;
  }
  return false;
};
// 是否草稿
WorkView.prototype.isDraft = function () {
  return this.isWorkDataMatchAclRole('DRAFT');
};
// 是否待办工作
WorkView.prototype.isTodo = function () {
  return this.isWorkDataMatchAclRole('TODO');
};
// 是否已办工作
WorkView.prototype.isDone = function () {
  return this.isWorkDataMatchAclRole('DONE');
};
// 是否办结工作
WorkView.prototype.isOver = function () {
  let workData = this.workFlow.getWorkData();
  return workData.endTime != null || this.isWorkDataMatchAclRole('OVER');
};
// 是否关注工作
WorkView.prototype.isAttention = function () {
  return this.isWorkDataMatchAclRole('ATTENTION');
};
// 是否督办工作
WorkView.prototype.isSupervise = function () {
  return this.isWorkDataMatchAclRole('SUPERVISE');
};
// 是否监控工作
WorkView.prototype.isMonitor = function () {
  return this.isWorkDataMatchAclRole('MONITOR');
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
// 获取操作按钮
WorkView.prototype.getActions = function () {
  var _self = this;
  if (_self.actions) {
    return _self.actions;
  }
  var workData = _self.getWorkData();
  var aclOpts = toolbar[workData.aclRole];
  var buttons = workData.buttons || [];
  var actions = (_self.actions = []);
  const isValidJSON = text => {
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
  forEach(buttons, function (button) {
    // 按钮名称
    var name = button.name;
    // 按钮编号
    var code = button.code;
    // 按钮ID
    var btnId = button.id;
    // 操作名称
    var methodName = rightMap[code];
    // 国际化文本信息
    var i18n = button.i18n;
    var uuid = button.uuid;
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
    if (_self.isMonitor() && code === 'B004023') {
      // 兼容旧数据-管理员删除
      methodName = 'removeByAdmin';
    }
    if ((indexOf(aclOpts, methodName) >= 0 || !aclRoleIsolation) && _self[methodName] != null) {
      actions.push({
        uuid,
        id: btnId,
        code,
        name,
        text: name,
        methodName,
        icon,
        type,
        eventHandler: undefined,
        i18n,
        configuration: JSON.parse(button.configuration)
      });
    } else if (button.eventHandler || button.configuration) {
      actions.push({
        uuid,
        id: btnId,
        code,
        name,
        text: name,
        methodName,
        icon,
        type,
        eventHandler: JSON.parse(button.eventHandler),
        i18n,
        configuration: JSON.parse(button.configuration)
      });
    } else {
      // 操作设置自定义操作按钮
      if (!_self.actionButtonMap) {
        let actionSetting = (_self.options.settings && _self.options.settings.ACTION) || {};
        let buttons = actionSetting.buttons || [];
        _self.customButtonMap = {};
        buttons
          .filter(button => !button.buildIn)
          .forEach(button => {
            _self.customButtonMap[button.code] = button;
          });
      }
      if (_self.customButtonMap[code]) {
        let button = _self.customButtonMap[code];
        actions.push({
          uuid,
          id: code,
          code,
          name,
          text: name,
          icon: button.style && button.style.icon,
          type: button.style && button.style.type,
          eventHandler: button.eventHandler,
          i18n
        });
      }
    }
  });

  // 判断签署意见是否显示为按钮
  if (!_self.isShowSignOpinionInToolbar()) {
    let actionIndex = actions.findIndex(action => action.methodName == 'signOpinion');
    if (actionIndex != -1) {
      actions.splice(actionIndex, 1);
    }
  }
  // 判断查看办理过程是否显示为按钮
  if (!_self.isShowViewProcessInToolbar()) {
    let actionIndex = actions.findIndex(action => action.methodName == 'viewProcess');
    if (actionIndex != -1) {
      actions.splice(actionIndex, 1);
    }
  }
  // 判断查看流程图是否显示为按钮
  if (!_self.isShowViewFlowDesignerInToolbar()) {
    let actionIndex = actions.findIndex(action => action.methodName == 'viewFlowDesigner');
    if (actionIndex != -1) {
      actions.splice(actionIndex, 1);
    }
  }

  // 判断是否启用连续签批
  if (_self.isEnabledContinuousWorkView()) {
    let requestCode = _self.getQueryString('_requestCode');
    let cwvDataStoreParams = sessionStorage.getItem(`cwvDataStoreParams_${requestCode}`);
    if (isEmpty(cwvDataStoreParams) || _self.$widget.continuousMode) {
      let actionIndex = actions.findIndex(action => action.methodName == 'enterContinuousMode');
      if (actionIndex != -1) {
        actions.splice(actionIndex, 1);
      }
    }
  } else {
    let actionIndex = actions.findIndex(action => action.methodName == 'enterContinuousMode');
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
  return opinionEditorSetting.showMode == 'modal' || opinionEditorSetting.showMode == 'all';
};
// 判断查看办理过程是否显示为按钮
WorkView.prototype.isShowViewProcessInToolbar = function () {
  let processViewerSetting = (this.options.settings && this.options.settings.PROCESS_VIEWER) || {};
  return processViewerSetting.showMode == 'modal' || processViewerSetting.showMode == 'all';
};
// 判断查看流程图是否显示为按钮
WorkView.prototype.isShowViewFlowDesignerInToolbar = function () {
  let processViewerSetting = (this.options.settings && this.options.settings.PROCESS_VIEWER) || {};
  return processViewerSetting.designerShowMode == 'modal' || processViewerSetting.designerShowMode == 'all';
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
  if (currentAction && (currentAction.text || currentAction.originalText)) {
    workData.action = currentAction.originalText || currentAction.text;
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
WorkView.prototype._getActionCallback = function (action) {
  var _self = this;
  return function () {
    _self[action].call(_self);
  };
};
// 自定义动态按钮提交
WorkView.prototype.collectIfUseCustomDynamicButton = function () {
  var _self = this;
  var currentAction = _self.currentAction;
  if (currentAction == null || currentAction.eventHandler) {
    return;
  }
  var workData = _self.workFlow.getWorkData();
  var btnId = currentAction.id;
  var btn_submit = _self.workFlow.actionCode.submit;
  // 处理自定义动态按钮
  var customDynamicButton = {};
  if (btnId !== btn_submit) {
    customDynamicButton.id = btnId;
    customDynamicButton.code = currentAction.code;
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
  var btn_rollback = _self.workFlow.actionCode.rollback;
  // 处理自定义动态按钮
  var customDynamicButton = {};
  if (btnId !== btn_rollback) {
    customDynamicButton.id = btnId;
    customDynamicButton.code = currentAction.code;
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
  var btn_gotoTask = _self.workFlow.actionCode.gotoTask;
  // 处理自定义动态按钮
  var customDynamicButton = {};
  if (btnId !== btn_gotoTask) {
    customDynamicButton.id = btnId;
    customDynamicButton.code = currentAction.code;
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
  var label = _.trim(currentAction.text);
  if (_.isEmpty(label)) {
    return defaultLabel;
  }
  return label;
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
  return this.openToSignIfRequired('提交', 'submit');
};
// 打开完成签署意见
WorkView.prototype.openToSignIfRequiredComplete = function () {
  return this.openToSignIfRequired('完成', 'complete');
};
// 打开退回签署意见
WorkView.prototype.openToSignIfRequiredRollback = function () {
  return this.openToSignIfRequired('退回', 'rollback');
};
// 打开直接退回签署意见
WorkView.prototype.openToSignIfRequiredDirectRollback = function () {
  return this.openToSignIfRequired('退回', 'directRollback');
};
// 打开退回主流程签署意见
WorkView.prototype.openToSignIfRequiredRollbackToMainFlow = function () {
  return this.openToSignIfRequired('退回', 'rollbackToMainFlow');
};
// 打开撤回签署意见
WorkView.prototype.openToSignIfRequiredCancel = function () {
  return this.openToSignIfRequired('撤回', 'cancel');
};
// 打开转办签署意见
WorkView.prototype.openToSignIfRequiredTransfer = function () {
  return this.openToSignIfRequired('转办', 'transfer');
};
// 打开会签签署意见
WorkView.prototype.openToSignIfRequiredCounterSign = function () {
  return this.openToSignIfRequired('会签', 'counterSign');
};
// 打开加签签署意见
WorkView.prototype.openToSignIfRequiredAddSign = function () {
  return this.openToSignIfRequired('加签', 'addSign');
};
// 打开催办签署意见
WorkView.prototype.openToSignIfRequiredRemind = function () {
  return this.openToSignIfRequired('催办', 'remind');
};
// 打开特送个人签署意见
WorkView.prototype.openToSignIfRequiredHandOver = function () {
  return this.openToSignIfRequired('特送个人', 'handOver');
};
// 打开特送环节签署意见
WorkView.prototype.openToSignIfRequiredGotoTask = function () {
  return this.openToSignIfRequired('特送环节', 'gotoTask');
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
  const opinionEditor = _self.getOpinionEditor();
  if (opinionEditor == null) {
    return false;
  }

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
  // 打开签署意见
  return opinionEditor.openToSignIfRequired(options);
};
// 是否必须提交签署意见
WorkView.prototype.isRequiredSubmitOpinion = function () {
  const _this = this;
  // 必须签署意见权限判断
  let rightCode = signOpinionActionRightMap['submit'];
  return _this.hasActionByCode(rightCode);
};
// 是否必须操作签署意见
WorkView.prototype.isRequiredActionOpinion = function (action) {
  const _this = this;
  // 必须签署意见权限判断
  let rightCode = signOpinionActionRightMap[action];
  return _this.hasActionByCode(rightCode);
};
// 意见收集到工作流数据
WorkView.prototype.opinionToWorkData = function (data) {
  var _self = this;
  let opinionEditor = _self.getOpinionEditor();
  if (opinionEditor == null) {
    return;
  }
  var data = opinionEditor.getOpinion();
  var workData = _self.workFlow.getWorkData();
  workData.opinionLabel = data.label;
  workData.opinionValue = data.value;
  workData.opinionText = data.text;
  workData.opinionFiles = data.files || [];
};
// 按钮操作后回调
WorkView.prototype.callbackAfterAction = function (func, success, originalArgs) {
  func.apply(
    this,
    [
      {
        success: success,
        args: originalArgs
      }
    ].concat(Array.prototype.slice.call(originalArgs))
  );
};
// 收集表单数据
WorkView.prototype.collectFormData = function () {
  let dyformWidget = this.$widget.$refs['dyform'];
  let workData = this.getWorkData();
  // 信息记录意见有意见即时显示时，恢复原有值
  let recordPreviewValues = {};
  (workData.records || []).forEach(record => {
    if (record.field && record.keepInitFieldValue) {
      recordPreviewValues[record.field] = dyformWidget.dyform.getFieldValue(record.field);
      dyformWidget.dyform.setFieldValue(record.field, record.initFieldValue);
    }
  });
  let data = dyformWidget.collectFormData();
  if (data.dataUuid && !(data.dyFormData && data.dyFormData.dataUuid)) {
    workData.dataUuid = data.dataUuid;
  }
  // 信息记录意见有意见即时显示时，恢复预览值
  if (!isEmpty(recordPreviewValues)) {
    for (let key in recordPreviewValues) {
      dyformWidget.dyform.setFieldValue(key, recordPreviewValues[key]);
    }
  }
  return data.dyFormData ? data.dyFormData : data;
};
// 验证表单数据
WorkView.prototype.validateDyformData = function (workData, callback) {
  if (workData.todoType === 2) {
    callback.call(this, true);
  } else {
    this.$widget.$refs['dyform'].validateFormData(callback);
  }
};
// 表单数据是否变更
WorkView.prototype.isDyformDataChanged = function () {
  const _this = this;
  let dyformWidget = _this.$widget.$refs['dyform'];
  if (_this.$widget.tempFormData) {
    let originFormData = dyformWidget.originFormData;
    let formDatas = deepClone(_this.$widget.tempFormData.dyFormData.formDatas);
    let tempOriginFormData = {};
    for (let key in formDatas) {
      let datas = formDatas[key] || [];
      tempOriginFormData[key] = datas;
      datas.forEach(data => {
        tempOriginFormData[data.uuid] = data;
      });
    }
    dyformWidget.originFormData = tempOriginFormData;
    let formData = dyformWidget.collectFormData();
    if (_this.$widget.tempFormData.dyFormData.addedFormDatas && formData.dyFormData.addedFormDatas) {
      for (let key in _this.$widget.tempFormData.dyFormData.addedFormDatas) {
        let uuids = _this.$widget.tempFormData.dyFormData.addedFormDatas[key] || [];
        let addedUuids = formData.dyFormData.addedFormDatas[key];
        addedUuids && uuids.forEach(uuid => {
          let index = addedUuids.indexOf(uuid);
          if (index != -1) {
            let tempRow = formDatas[key].find(item => item.uuid === uuid);
            let currentRow = formData.dyFormData.formDatas[key].find(item => item.uuid === uuid);
            if (!tempRow || !currentRow || !_this.isRowDataChanged(tempRow, currentRow)) {
              addedUuids.splice(index, 1);
            }
          }
        });
      }
    }
    let isChanged = dyformWidget.isDyformDataChanged(formData);
    dyformWidget.originFormData = originFormData;
    return isChanged;
  }
  return dyformWidget.isDyformDataChanged();
};
WorkView.prototype.getDyformWidget = function () {
  return this.$widget.$refs['dyform'];
};
// 校验签署意见是否符合规则
WorkView.prototype._isOpinionRuleCheck = function (workData, scene, callback) {
  var _self = this;
  var opinionText = workData.opinionText;
  var flowId = workData.flowDefId;
  var taskId = workData.taskId;
  $axios
    .post('/proxy/api/workflow/opinion/isOpinionRuleCheck', { opinionText, flowId, taskId, scene })
    .then(result => {
      var isOpinionRuleCheckDto = result.data.data;
      if (!isOpinionRuleCheckDto.success) {
        if (isOpinionRuleCheckDto.alertAutoClose) {
          _self.showError({ content: isOpinionRuleCheckDto.message });
        } else {
          _self.$widget.$error({
            content: isOpinionRuleCheckDto.message,
            okText: '知道了'
          });
        }
      } else if (callback) {
        callback.call(_self);
      }
    })
    .catch(error => {
      console.error(error);
      _self.showError({ content: '校验签署意见是否符合规则时出错！' });
    });
};
// 加载遮罩
WorkView.prototype.showLoading = function (opt) {
  const _self = this;
  _self.loading.visible = true;
  _self.loading.tip = getMsgI18ns(opt.tip || opt, this);
};
// 隐藏遮罩
WorkView.prototype.hideLoading = function () {
  this.loading.visible = false;
};
WorkView.prototype.showToast = function (opt) {
  this.$widget.$message.info(opt.content || opt.message);
  this._msgCallback(opt);
};
WorkView.prototype.showInfo = function (opt) {
  this.$widget.$message.info(opt.content || opt.message);
  this._msgCallback(opt);
};
WorkView.prototype.showSuccess = function (opt) {
  this.$widget.$message.success(opt.content || opt.message);
  this._msgCallback(opt);
};
WorkView.prototype.showWarning = function (opt) {
  this.$widget.$message.warning(opt.content || opt.message);
  this._msgCallback(opt);
};
WorkView.prototype.showError = function (opt) {
  this.$widget.$message.error(opt.content || opt.message);
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
// 刷新待办工作
WorkView.prototype.refreshTodoWork = function (taskInstUuid) {
  var _self = this;
  this.allowUnloadWorkData = true;
  var workData = _self.workFlow.getWorkData();
  var flowInstUuid = workData.flowInstUuid;
  var refreshUrl = `/workflow/work/view/todo?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`;
  window.location = _self.addSystemPrefix(refreshUrl);
};
WorkView.prototype.refreshCurrentWork = function () {
  const workData = this.workFlow.getWorkData();
  const flowInstUuid = workData.flowInstUuid;
  let refreshUrl = `/workflow/work/view/work/?flowInstUuid=${flowInstUuid}`
  window.location = this.addSystemPrefix(refreshUrl);
}

WorkView.prototype.addSystemPrefix = function (url) {
  const _this = this;
  if (_this.$widget._$SYSTEM_ID && url && !url.startsWith('/sys/')) {
    url = `/sys/${_this.$widget._$SYSTEM_ID}/_${url}`;
  }
  return url;
};
// 成功处理
WorkView.prototype.handlerSuccess = function (jqXHR, callback) {
  this.allowUnloadWorkData = true;
  window.close();
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
  var msgName = '';
  var userName = [];
  if (todoUser && Object.keys(todoUser).length > 0) {
    for (var k in todoUser) {
      var users = todoUser[k];
      userName.push(users);
    }
  }

  if (userName.length > 0 || nextTasks.id) {
    msgName =
      msgPre +
      userName.join('，') +
      (nextTasks.id ? ' | ' + this.$widget.$t('WorkflowView.' + nextTasks.id + '.taskName', nextTasks.name) : '') +
      '！';
  }
  return msgName;
};
// 获取环节办理过程
WorkView.prototype.getTaskProcess = function () {
  return this.taskProcess;
};
// 设置环节办理过程
WorkView.prototype.setTaskProcess = function (taskProcess) {
  this.taskProcess = taskProcess;
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
  if (EASY_ENV_IS_NODE) {
    return false;
  }
  const _self = this;
  const workData = _self.getWorkData();
  const buttons = workData.buttons || [];
  // 送审批的流程实例uuid
  let approveFlowInstUuid = '';
  let split = location.href.split('&');
  forEach(split, function (urlParam) {
    let approveFlowInstUuidKey = 'approveFlowInstUuid=';
    let indexOf = urlParam.indexOf(approveFlowInstUuidKey);
    if (indexOf > -1) {
      approveFlowInstUuid = urlParam.substring(indexOf + approveFlowInstUuidKey.length, urlParam.length);
    }
  });
  if (!isEmpty(stringTrim(approveFlowInstUuid))) {
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
// 是否允许查看流程设计器
WorkView.prototype.isAllowViewFlowDesigner = function () {
  return this.hasActionByCode('B004041');
};
// 是否提交后进行打印
WorkView.prototype.isPrintAfterSubmit = function () {
  var _self = this;
  if (_self.submitAndPrint != null) {
    return _self.submitAndPrint;
  }
  // 提交并打印
  _self.submitAndPrint = _self.hasActionByCode(_self.workFlow.actionCode.submitAndPrint);
  return _self.submitAndPrint;
};
// 重新加载容器
WorkView.prototype.reload = function () {
  var _self = this;
  _self.allowUnloadWorkData = true;
  var workData = _self.workFlow.getWorkData();
  var taskInstUuid = workData.taskInstUuid;
  var flowInstUuid = workData.flowInstUuid;
  if (!isEmpty(stringTrim(taskInstUuid))) {
    window.location.reload();
  } else if (!isEmpty(stringTrim(flowInstUuid))) {
    var refreshUrl = '/workflow/work/view/draft?flowInstUuid=' + flowInstUuid;
    window.location = _self.addSystemPrefix(refreshUrl);
  } else {
    window.location.reload();
  }
};
WorkView.prototype.reloadLater = function () {
  var _self = this;
  setTimeout(function () {
    _self.reload();
  }, 1000);
};
// 浏览器窗口关闭事件监听
WorkView.prototype.registerWindowCloseEventListerner = function () {
  var _self = this;
  // 窗口关闭工作解锁
  window.onunload = function () {
    _self.unlockWorkIfRequired();
  };
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
};
// 是否需要确认重新加载流程数据
WorkView.prototype.isNeedConfirmUnloadWorkData = function () {
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
  // 显示为文本
  if (_self.$widget.displayState == 'label') {
    return _self.isOpinionChanged();
  }
  // 检测办理意见、表单数据是否变更
  return _self.isOpinionChanged() || _self.isDyformDataChanged();
};
// 加锁数据
WorkView.prototype.lockWorkIfRequired = function () {
  const _self = this;
  if (_self.isTodo() && !(_self.hasActionByCode('B004015') || _self.hasActionByCode('B004016'))) {
    var workData = _self.workFlow.getWorkData();
    $axios.post(`/api/workflow/work/lockWork`, { taskInstUuid: workData.taskInstUuid });
  }
};
// 解锁数据
WorkView.prototype.unlockWorkIfRequired = function () {
  const _self = this;
  if (_self.isTodo()) {
    var workData = _self.workFlow.getWorkData();
    $axios.post(
      `/api/workflow/work/unlockWork`,
      { taskInstUuid: workData.taskInstUuid },
      {
        headers: {
          contentType: 'application/x-www-form-urlencoded'
        }
      }
    );
  }
};
// 关闭
WorkView.prototype.close = function () {
  const _self = this;
  if (_self.isDyformDataChanged()) {
    _self.$widget.$confirm({
      title: this.getMsgI18ns('WorkflowWork.message.confirmModal', '确认框'),
      content: this.getMsgI18ns('WorkflowWork.message.dyformDataChangedClose', '内容已修改，请先保存后再操作，是否放弃修改！'),
      okText: this.$widget.locale && this.$widget.locale.Modal && this.$widget.locale.Modal.okText,
      cancelText: this.$widget.locale && this.$widget.locale.Modal && this.$widget.locale.Modal.cancelText,
      onOk() {
        _self.unlockWorkIfRequired();
        _self.userConfirmClose = true;
        window.close();
      }
    });
  } else {
    _self.unlockWorkIfRequired();
    _self.userConfirmClose = true;
    window.close();
  }
};
// 保存
WorkView.prototype.save = function () {
  var _self = this;
  _self.opinionToWorkData();
  var workData = _self.workFlow.getWorkData();
  // 操作动作及类型
  _self.setEventAction('保存', 'Save', workData);
  // 获取表单数据
  workData.dyFormData = _self.collectFormData();
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
  const _self = this;
  // 提示
  let message = _self.getMsgI18ns('WorkflowWork.message.saveSuccess', '保存成功！');
  // _self.reloadLater();
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'save',
    result,
    message
  });
};
WorkView.prototype.onSaveFailure = function (jqXHR) {
  var _self = this;
  var callback = _self._getActionCallback('save');
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
  _self.setEventAction('提交', 'Submit', workData);
  // 获取表单数据
  workData.dyFormData = _self.collectFormData();
  // 是否提交后进行套打
  workData.printAfterSubmit = _self.isPrintAfterSubmit();
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
    _self.showError({ content: '表单数据验证出错' + e + '，无法提交数据！' });
    throw e;
  }
};
// 下载打印结果
WorkView.prototype._downloadPrintResult = function (result) {
  if (result && result.data && result.data.data) {
    let fileID = result.data.data.printResultFileId || result.data.data.fileID;
    if (fileID) {
      let url = `/proxy-repository/repository/file/mongo/download`;
      download({
        method: 'GET',
        url,
        data: { fileID }
      });
      result.data.downloadFile = true;
    }
  }
};
WorkView.prototype.downloadFilesByFileIds = function (fileIds) {
  let files = [];
  forEach(fileIds, function (fileId) {
    files.push({ fileID: fileId });
  });
  let fileIdString = JSON.stringify(files); // encodeURIComponent(JSON.stringify(files));
  let date = new Date();
  let fileName = '' + date.getFullYear() + (date.getMonth() + 1) + date.getDate() + date.getHours() + date.getMinutes() + date.getSeconds();
  let url = `/proxy-repository/repository/file/mongo/downAllFiles`;
  download({
    method: 'GET',
    url,
    data: {
      fileIDs: fileIdString,
      includeFolder: false,
      fileName
    }
  });
};
// WorkView.prototype.downloadFilesByUrl = function (url) {
//   var _self = this;
//   var hiddenIFrameID = 'hiddenDownloader' + generateId();
//   var iframe = document.createElement('iframe');
//   iframe.id = hiddenIFrameID;
//   iframe.style.display = 'none';
//   document.body.appendChild(iframe);
//   iframe.src = url;

//   var cnt = 0;
//   var timer = setInterval(function () {
//     if (cnt++ == 100) {
//       clearInterval(timer);
//       iframe.remove();
//       return;
//     }
//     var iframeDoc = iframe.contentDocument || iframe.contentWindow.document;
//     if (iframeDoc.readyState == 'complete' || iframeDoc.readyState == 'interactive') {
//       var _text = iframeDoc.body.innerText;
//       if (_text && _text.indexOf('No such file') != -1) {
//         // 需要等待后端响应无文件的异常
//         clearInterval(timer);
//         iframe.remove();
//         if (_text.indexOf('try later') != -1) {
//           setTimeout(function () {
//             _self.downloadURL(url); //重新下载
//           }, 2000);
//         }
//       }
//       return;
//     }
//   }, 1000);
// };
WorkView.prototype.emitActionSuccess = function (opt) {
  this.$widget.$emit('actionSuccess', opt);
};
// 提交成功
WorkView.prototype.onSubmitSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;
  // 下载打印结果
  _self._downloadPrintResult(result);

  let message = '';
  var data = result.data.data;
  var nextTasks = data.nextTasks || [];
  // if (nextTasks.length > 1) {
  //   nextTasks = [nextTasks[nextTasks.length - 1]];
  // }
  if (nextTasks.length > 0) {
    _.each(nextTasks, function (tItem) {
      var taskTodoUsers = data.taskTodoUsers[tItem.uuid];
      var msgToUser = _self.getMsgTips(taskTodoUsers, _self.getMsgI18ns('WorkflowWork.message.submittedTo', '已提交至'), tItem);
      message = _self.getMsgI18ns('WorkflowWork.message.submitSuccess', '提交成功') + '！' + msgToUser;
      _self.emitActionSuccess({
        action: 'submit',
        result,
        message
      });
    });
  } else {
    message = _self.getMsgI18ns('WorkflowWork.message.submitSuccess', '提交成功') + '！';
    // 触发操作成功事件
    _self.emitActionSuccess({
      action: 'submit',
      result,
      message
    });
  }
};
// 提交失败
WorkView.prototype.onSubmitFailure = function (jqXHR) {
  var _self = this;
  var callback = _self._getActionCallback('submit');
  _self.handlerError.call(_self, jqXHR, callback);
};
// 完成
WorkView.prototype.complete = function () {
  const _this = this;
  // 签署意见
  if (_this.openToSignIfRequiredComplete.apply(_this, arguments)) {
    return;
  }
  _this.opinionToWorkData();
  let workData = _this.getWorkData();
  // 操作动作及类型
  _this.setEventAction('完成', 'Complete', workData);
  // 获取表单数据
  workData.dyFormData = _this.collectFormData();
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
    // _this.validateDyformData(workData, function (validate) {
    //   if (!validate) {
    //     return;
    //   }
    // 签署意见校验
    let opinionRuleCheckCallback = function () {
      _this.workFlow.complete(success, failure);
    };
    _this._isOpinionRuleCheck(workData, sceneEnum.SUBMIT, opinionRuleCheckCallback);
    // });
  } catch (e) {
    _this.showError({ content: '表单数据验证出错' + e + '，无法提交数据！' });
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
  let callback = _this._getActionCallback('complete');
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
    _self.setEventAction('退回', 'Rollback', workData);
    // 获取表单数据
    workData.dyFormData = _self.collectFormData();
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
WorkView.prototype.onRollbackSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;

  var msgToUser = '';
  var data = result.data.data;
  var nextTasks = data.nextTasks || [];
  if (nextTasks.length > 0) {
    var taskTodoUsers = data.taskTodoUsers[nextTasks[0].uuid];
    msgToUser = _self.getMsgTips(taskTodoUsers, _self.getMsgI18ns('WorkflowWork.message.rollbackTo', '已退回至'), nextTasks[0]);
  }

  // 提示
  let message = _self.getMsgI18ns('WorkflowWork.message.rollbackSuccess', '退回成功') + '！' + msgToUser;

  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'rollback',
    result,
    message
  });
};
WorkView.prototype.onRollbackFailure = function (jqXHR) {
  var _self = this;
  var callback = _self._getActionCallback('rollback');
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
    _self.setEventAction('退回前办理人', 'DirectRollback', workData);
    // 获取表单数据
    workData.dyFormData = _self.collectFormData();
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
  var nextTasks = data.nextTasks || [];
  var msgToUser = '';
  if (nextTasks.length > 0) {
    var taskTodoUsers = data.taskTodoUsers[nextTasks[0].uuid];
    msgToUser = _self.getMsgTips(taskTodoUsers, _self.getMsgI18ns('WorkflowWork.message.rollbackTo', '已退回至'), nextTasks[0]);
  }

  // 提示
  let message = _self.getMsgI18ns('WorkflowWork.message.directRollbackSuccess', '退回前办理人成功') + '！' + msgToUser;

  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'directRollback',
    result,
    message
  });
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
  _self.setEventAction('退回主流程', 'RollbackToMainFlow', workData);
  // 获取表单数据
  workData.dyFormData = _self.collectFormData();
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
  var msgToUser = '';
  if (nextTasks.length > 0) {
    var taskTodoUsers = result.data.taskTodoUsers[nextTasks[0].uuid];
    msgToUser = _self.getMsgTips(taskTodoUsers, _self.getMsgI18ns('WorkflowWork.message.rollbackTo', '已退回至'), nextTasks[0]);
  }

  // 提示
  let message = _self.getMsgI18ns('WorkflowWork.message.rollbackSuccess', '退回成功') + '！' + msgToUser;

  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'rollbackToMainFlow',
    result,
    message
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
  let subflowViewer = _self.$widget.$refs.subflowViewer;
  if (subflowViewer) {
    let subTaskData = subflowViewer.subTaskData || subflowViewer.branchTaskData;
    let url = `/workflow/work/view/work?taskInstUuid=${subTaskData.parentTaskInstUuid}&flowInstUuid=${subTaskData.parentFlowInstUuid}&viewTheMainFlow=true`;
    window.open(_self.addSystemPrefix(url), 'viewTheMainFlow_' + subTaskData.parentFlowInstUuid);
  }
};
// 打印表单
WorkView.prototype.printForm = function () {
  this.$widget.$refs.dyform.print();
};
// 签署意见
WorkView.prototype.signOpinion = function () {
  const _this = this;
  const opinionEditor = _this.getOpinionEditor();
  if (opinionEditor == null) {
    console.error('opinion editor is null');
    return;
  }
  opinionEditor.openToSign({});
};
// 查看办理过程
WorkView.prototype.viewProcess = function () {
  const _this = this;
  const processViewer = _this.getProcessViewer();
  if (processViewer == null) {
    console.error('process viewer is null');
    return;
  }
  processViewer.show();
};
WorkView.prototype.getFlowDesignerData = function () {
  const workData = this.getWorkData();

  if (this.flowDesignerData) {
    return Promise.resolve(this.flowDesignerData);
  }
  return new Promise((resolve, reject) => {
    require('../../workflow-designer/component/api')
      .fetchFlowData(workData.flowDefUuid)
      .then(res => {
        this.flowDesignerData = res;
        resolve(res);
      });
  });
};
WorkView.prototype.getFlowViewerUrl = function () {
  const _self = this;
  const workData = _self.getWorkData();

  // const url = `/web/app/pt-mgr/pt-wf-mgr/pt-wf-viewer.html?open=true&onlyView=true&id=${workData.flowDefUuid}&flowInstUuid=${workData.flowInstUuid || ''
  //   }`;

  const url = `/html/newWorkFlowDesignerView.html?onlyGraphics=true&id=${workData.flowDefUuid}`;
  const newUrl = `/workflow-viewer/index?uuid=${workData.flowDefUuid}&flowInstUuid=${workData.flowInstUuid || ''}`;

  return new Promise((resolve, reject) => {
    _self.getFlowDesignerData().then(res => {
      if (res.graphData) {
        resolve(_self.addSystemPrefix(newUrl));
      } else {
        resolve(_self.addSystemPrefix(url));
      }
    });
  });
};

WorkView.prototype.viewFlowDesigner = function () {
  this.getFlowViewerUrl().then(viewUrl => {
    window.open(viewUrl, '_blank');
  });
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
  _self.setEventAction('撤回', 'Cancel', workData);
  // 获取表单数据
  workData.dyFormData = _self.collectFormData();
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
WorkView.prototype.onCancelSuccess = function (result) {
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
  _self.showSuccess({ content: _self.getMsgI18ns('WorkflowWork.message.cancelSuccess', '撤回成功！'), callback, timer: 2000 });
  _self.emitActionSuccess({
    action: 'cancel'
  });
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
    _self.setEventAction('转办', 'Transfer', workData);
    // 获取表单数据
    workData.dyFormData = _self.collectFormData();
    var success = function () {
      if (_.isFunction(_self.onAfterTransfer)) {
        _self.callbackAfterAction(_self.onAfterTransfer, true, arguments);
      } else {
        _self.onTransferSuccess.apply(_self, arguments);
      }
    };
    var failure = function () {
      if (_.isFunction(_self.onAfterTransfer)) {
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
  var msgToUser = _self.getMsgI18ns('WorkflowWork.message.transferredTo', '已转办至') + _self.workFlow.transferUsers.join('，');

  // 提示
  let message = _self.getMsgI18ns('WorkflowWork.message.transferSucces', '转办成功！') + msgToUser;
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'transfer',
    result,
    message,
    workView: this
  });
};
// 转办失败
WorkView.prototype.onTransferFailure = function (jqXHR) {
  var _self = this;
  var callback = _self._getActionCallback('transfer');
  _self.handlerError.call(_self, jqXHR, callback);
};
// 查看阅读记录
WorkView.prototype.viewReadLog = function () {
  const _self = this;
  const workData = _self.getWorkData();
  let taskInstUuid = workData.taskInstUuid;
  $axios.get(`/workflow/work/view/viewReadLog?taskInstUuid=${taskInstUuid}`).then(result => {
    var data = result.data || {};
    Date.prototype.format = function (fmt) {
      // author: meizz
      var o = {
        'M+': this.getMonth() + 1, // 月份
        'd+': this.getDate(), // 日
        'H+': this.getHours(), // 小时
        'm+': this.getMinutes(), // 分
        's+': this.getSeconds(), // 秒
        'q+': Math.floor((this.getMonth() + 3) / 3), // 季度
        S: this.getMilliseconds()
        // 毫秒
      };
      if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
      }
      for (var k in o) {
        if (new RegExp('(' + k + ')').test(fmt)) {
          fmt = fmt.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length));
        }
      }
      return fmt;
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
    forEach(data.readUser, function (record) {
      record.readTimeString = getReadTimeString(record.readTime);
    });

    _self.$widget.readLogVisible = true;
    _self.$widget.readLogData = data;
  });
};
// 会签
WorkView.prototype.counterSign = function () {
  const _self = this;
  // 签署意见
  if (_self.openToSignIfRequiredCounterSign.apply(_self, arguments)) {
    return;
  }
  _self.opinionToWorkData();
  let workData = _self.workFlow.getWorkData();
  // 签署意见校验
  let opinionRuleCheckCallback = function () {
    // 操作动作及类型
    _self.setEventAction('会签', 'CounterSign', workData);
    // 获取表单数据
    workData.dyFormData = _self.collectFormData();
    let success = function () {
      if (_.isFunction(_self.onAfterCounterSign)) {
        _self.callbackAfterAction(_self.onAfterCounterSign, true, arguments);
      } else {
        _self.onCounterSignSuccess.apply(_self, arguments);
      }
    };
    let failure = function () {
      if (_.isFunction(_self.onAfterCounterSign)) {
        _self.callbackAfterAction(_self.onAfterCounterSign, false, arguments);
      } else {
        _self.onCounterSignFailure.apply(_self, arguments);
      }
    };

    _self.workFlow.counterSign([], success, failure);
    // WorkFlowErrorHandler.openOrgSelect({
    //   title: '选择' + workData.action + '人员【' + workData.taskName + '】',
    //   type: 'all',
    //   multiple: true,
    //   selectTypes: 'all',
    //   valueFormat: 'justId',
    //   locale: _self.$widget.locale,
    //   callback: function (values, labels) {
    //     if (values && values.length > 0) {
    //       let counterSignUserIds = values;
    //       _self.signUsers = labels;
    //       _self.workFlow.signUsers = labels;
    //       _self.workFlow.counterSign(counterSignUserIds, success, failure);
    //     } else {
    //       _self.showInfo({ content: workData.action + '人员不能为空！' });
    //       _self.counterSign();
    //     }
    //   }
    // });
  };
  _self._isOpinionRuleCheck(workData, sceneEnum.COUNTERSIGN, opinionRuleCheckCallback);
};
WorkView.prototype.onCounterSignSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;
  var workData = _self.workFlow.getWorkData();
  var action = workData.action || '会签';
  var msgToUser = '已' + action + '至' + _self.workFlow.signUsers.join('，') + '！';

  // 提示
  // _self.showToast({ title: action + '成功！' + msgToUser });
  let message = action + '成功！' + msgToUser;
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'counterSign',
    result,
    message,
    workView: this
  });
};
WorkView.prototype.onCounterSignFailure = function (jqXHR) {
  const _self = this;
  let callback = _self._getActionCallback('counterSign');
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
    _self.setEventAction('加签', 'AddSign', workData);
    // 获取表单数据
    workData.dyFormData = _self.collectFormData();
    let success = function () {
      if (_.isFunction(_self.onAfterAddSign)) {
        _self.callbackAfterAction(_self.onAfterAddSign, true, arguments);
      } else {
        _self.onAddSignSuccess.apply(_self, arguments);
      }
    };
    let failure = function () {
      if (_.isFunction(_self.onAfterAddSign)) {
        _self.callbackAfterAction(_self.onAfterAddSign, false, arguments);
      } else {
        _self.onAddSignFailure.apply(_self, arguments);
      }
    };
    _self.workFlow.addSign([], success, failure);
  };
  _self._isOpinionRuleCheck(workData, sceneEnum.COUNTERSIGN, opinionRuleCheckCallback);
};
WorkView.prototype.onAddSignSuccess = function (result) {
  const _self = this;
  _self.allowUnloadWorkData = true;
  let workData = _self.workFlow.getWorkData();
  let action = workData.action || '加签';
  let msgToUser = '已' + action + '至' + _self.workFlow.addSignUsers.join('，') + '！';

  // 提示
  // _self.showToast({ title: action + '成功！' + msgToUser });
  let message = action + '成功！' + msgToUser;
  if (_self.isTodo()) {
    _self.showSuccess({
      message,
      timer: 3,
      callback() {
        window.location.reload();
      }
    });
  } else {
    // 触发操作成功事件
    _self.emitActionSuccess({
      action: 'addSign',
      result,
      message
    });
  }
};
WorkView.prototype.onAddSignFailure = function (jqXHR) {
  const _self = this;
  let callback = _self._getActionCallback('addSign');
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
  // WorkFlowErrorHandler.openOrgSelect({
  //   title: '选择抄送人员【' + workData.taskName + '】',
  //   type: 'all',
  //   multiple: true,
  //   selectTypes: 'all',
  //   valueFormat: 'justId',
  //   locale: _self.$widget.locale,
  //   callback: function (values, labels) {
  //     if (values && values.length > 0) {
  //       let copyToUserIds = values;
  //       _self.copyToUsers = labels;
  //       _self.workFlow.copyTo(taskInstUuids, copyToUserIds, success, failure);
  //     } else {
  //       _self.showInfo({ content: workData.action + '人员不能为空！' });
  //     }
  //   }
  // });
};
WorkView.prototype.onCopyToSuccess = function (result) {
  const _this = this;
  let msgToUser = this.getMsgI18ns('WorkflowWork.message.copiedTo', '已抄送至') + this.workFlow.copyToUsers.join('，') + '！';
  // 提示
  let message = this.getMsgI18ns('WorkflowWork.message.copySuccess', '抄送成功') + '！' + msgToUser;
  // 触发操作成功事件
  _this.emitActionSuccess({
    action: 'copyTo',
    result,
    message
  });
};
WorkView.prototype.onCopyToFailure = function (jqXHR) {
  const _self = this;
  let callback = _self._getActionCallback('copyTo');
  _self.handlerError.call(_self, jqXHR, callback);
};
// 关注
WorkView.prototype.attention = function () {
  var _self = this;
  var workData = _self.workFlow.getWorkData();
  var taskInstUuids = [];
  taskInstUuids.push(workData.taskInstUuid);
  var success = function () {
    if (_.isFunction(_self.onAfterAttention)) {
      _self.callbackAfterAction(_self.onAfterAttention, true, arguments);
    } else {
      _self.onAttentionSuccess.apply(_self, arguments);
    }
  };
  var failure = function () {
    if (_.isFunction(_self.onAfterAttention)) {
      _self.callbackAfterAction(_self.onAfterAttention, false, arguments);
    } else {
      _self.onAttentionFailure.apply(_self, arguments);
    }
  };
  _self.workFlow.attention(taskInstUuids, success, failure);
};
WorkView.prototype.onAttentionSuccess = function (result) {
  var _self = this;
  // 提示
  let message = this.$widget.$t('WorkflowWork.hasAttention', '已关注！');
  // _self.reloadLater();
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'attention',
    result,
    message
  });
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
    if (_.isFunction(_self.onAfterUnfollow)) {
      _self.callbackAfterAction(_self.onAfterUnfollow, true, arguments);
    } else {
      _self.onUnfollowSuccess.apply(_self, arguments);
    }
  };
  var failure = function () {
    if (_.isFunction(_self.onAfterUnfollow)) {
      _self.callbackAfterAction(_self.onAfterUnfollow, false, arguments);
    } else {
      _self.onUnfollowFailure.apply(_self, arguments);
    }
  };
  _self.workFlow.unfollow(taskInstUuids, success, failure);
};
WorkView.prototype.onUnfollowSuccess = function (result) {
  var _self = this;
  // 提示
  let message = this.$widget.$t('WorkflowWork.hasUnfollow', '已取消关注！');
  // _self.reloadLater();
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'unfollow',
    result,
    message
  });
};
WorkView.prototype.onUnfollowFailure = function () {
  this.handlerError.apply(this, arguments);
};
// 套打
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
      _self.showInfo({ message: _self.getMsgI18ns('WorkflowWork.message.flowNotTemplate', '流程没有配置套打模板！') });
    } else if (templates.length === 1) {
      let template = templates[0];
      _self.workFlow.print(template.id, template.uuid, 'zh-CN', printSuccess, printFailure);
    } else {
      // 选择套打模板
      _self.$widget.showPrintTemplates({
        templates,
        callback: function (template) {
          _self.workFlow.print(template.id, template.uuid, 'zh-CN', printSuccess, printFailure);
        }
      });
    }
  };
  var failure = function () {
    appModal.error('获取套打模板报错！');
  };
  _self.workFlow.getPrintTemplates(success, failure);
};
WorkView.prototype.onPrintSuccess = function (result) {
  var _self = this;
  _self._downloadPrintResult(result);
  // 提示
  let message = _self.getMsgI18ns('WorkflowWork.message.printSuccessAndDownload', '套打成功，开始下载套打结果文件！');
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'print',
    result,
    message
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
  var opinionFiles = workData.opinionFiles;
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
  _self.workFlow.remind(taskInstUuids, opinionLabel, opinionValue, opinionText, opinionFiles, success, failure);
};
WorkView.prototype.onRemindSuccess = function (result) {
  var _self = this;
  // 提示
  let message = this.$widget.$t('WorkflowWork.subFlowShareData.message.urgeSuccess', '催办成功！');
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'remind',
    result,
    message
  });
};
WorkView.prototype.onRemindFailure = function () {
  this.handlerError.apply(this, arguments);
};
// 移交
WorkView.prototype.handOver = function () {
  const _self = this;
  let callback = function () {
    // 签署意见
    if (_self.openToSignIfRequiredHandOver.apply(_self, arguments)) {
      return;
    }
    _self.opinionToWorkData();
    let workData = _self.workFlow.getWorkData();
    // 操作动作及类型
    _self.setEventAction('工作移交', 'HandOver', workData);
    // 获取表单数据
    workData.dyFormData = _self.collectFormData();
    let success = function () {
      if (isFunction(_self.onAfterHandOver)) {
        _self.callbackAfterAction(_self.onAfterHandOver, true, arguments);
      } else {
        _self.onHandOverSuccess.apply(_self, arguments);
      }
    };
    let failure = function () {
      if (isFunction(_self.onAfterHandOver)) {
        _self.callbackAfterAction(_self.onAfterHandOver, false, arguments);
      } else {
        _self.onHandOverFailure.apply(_self, arguments);
      }
    };
    const title = this.$widget.$t('WorkflowWork.selectSpecialUser', '选择特送的人员');
    WorkFlowErrorHandler.openOrgSelect({
      title,
      type: 'all',
      multiple: true,
      selectTypes: 'all',
      valueFormat: 'all', //'justId',
      locale: _self.$widget.locale,
      callback: function (values) {
        if (values && values.length > 0) {
          let handOverUserIds = values;
          _self.workFlow.handOver(handOverUserIds, success, failure);
        } else {
          _self.showInfo({ content: _self.$widget.$t('WorkflowWork.selectHandOver', '请选择移交人员！') });
          _self.handOver();
        }
      }
    });
  };

  // 检查环节锁
  _self.checkTaskLock(callback);
};
// 检查环节锁
WorkView.prototype.checkTaskLock = function (callback) {
  var _self = this;
  var workData = _self.workFlow.getWorkData();
  $axios
    .get('/api/workflow/work/listLockInfo', {
      params: {
        taskInstUuid: workData.taskInstUuid
      }
    })
    .then(result => {
      var taskLocks = result.data.data;
      if (taskLocks != null && taskLocks.length > 0) {
        var userNames = [];
        forEach(taskLocks, function (lock) {
          userNames.push(lock.userName);
        });
        var btnText = stringTrim(_self.currentAction.text);

        var userNamesStr = '“' + userNames.join('；') + '”';
        _self.showError({
          content:
            userNamesStr +
            _self.getMsgI18ns('WorkflowWork.message.editingNotProceed', '正在编辑，无法进行') +
            btnText +
            _self.getMsgI18ns('WorkflowWork.workProcess.actionName', '操作') +
            '。'
        });
      } else {
        if (isFunction(callback)) {
          callback.call(_self);
        }
      }
    });
};
WorkView.prototype.onHandOverSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;
  // 提示
  let message = this.$widget.$t('WorkflowWork.specialUserSuccess', '特送个人成功！');
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'handOver',
    result,
    message
  });
};
WorkView.prototype.onHandOverFailure = function () {
  this.handlerError.apply(this, arguments);
};

// 跳转
WorkView.prototype.gotoTask = function () {
  var _self = this;

  let callback = function () {
    // 签署意见
    if (_self.openToSignIfRequiredGotoTask.apply(_self, arguments)) {
      return;
    }
    _self.opinionToWorkData();
    var workData = _self.workFlow.getWorkData();
    // 操作动作及类型
    _self.setEventAction('环节跳转', 'GotoTask', workData);
    // 获取表单数据
    workData.dyFormData = _self.collectFormData();
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

  // 检查环节锁
  _self.checkTaskLock(callback);
};
WorkView.prototype.onGotoTaskSuccess = function (result) {
  var _self = this;
  _self.allowUnloadWorkData = true;
  // 提示
  let message = _self.getMsgI18ns('WorkflowWork.message.gotoTaskSuccess', '特送环节成功！');
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'gotoTask',
    result,
    message
  });
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
  // 操作动作及类型
  _self.setEventAction('挂起', 'Suspend', workData);
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
  var actionName = _self.getButtonLabel('挂起');
  // 提示
  var message = actionName + ' ' + this.$widget.$t('WorkflowWork.success', '成功！');
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'suspend',
    result,
    message
  });
};
WorkView.prototype.onSuspendFailure = function () {
  this.handlerError.apply(this, arguments);
};
// 恢复
WorkView.prototype.resume = function () {
  var _self = this;
  var workData = _self.workFlow.getWorkData();
  // 操作动作及类型
  _self.setEventAction('恢复', 'Resume', workData);
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
  const _self = this;
  _self.allowUnloadWorkData = true;
  let actionName = _self.getButtonLabel('恢复');
  // 提示
  let message = actionName + ' ' + this.$widget.$t('WorkflowWork.success', '成功！');
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'resume',
    result,
    message
  });
};
WorkView.prototype.onResumeFailure = function () {
  this.handlerError.apply(this, arguments);
};
// 删除
WorkView.prototype.remove = function () {
  const _this = this;
  var workData = _this.workFlow.getWorkData();
  // 操作动作及类型
  _this.setEventAction('删除', 'Remove', workData);
  if (_this.isDraft()) {
    var flowInstUuids = [];
    flowInstUuids.push(workData.flowInstUuid);
    _this.doRemove('removeDraft', flowInstUuids);
  } else {
    var taskInstUuids = [];
    taskInstUuids.push(workData.taskInstUuid);
    _this.doRemove('remove', taskInstUuids);
  }
};
// 管理员删除
WorkView.prototype.removeByAdmin = function () {
  const _this = this;
  var workData = _this.workFlow.getWorkData();
  // 操作动作及类型
  _this.setEventAction('删除', 'Remove', workData);
  var taskInstUuids = [];
  taskInstUuids.push(workData.taskInstUuid);
  _this.doRemove('removeByAdmin', taskInstUuids);
};
// 删除处理
WorkView.prototype.doRemove = function (removeMethod, uuids) {
  var _self = this;
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
  this.$widget.$confirm({
    title: this.getMsgI18ns('WorkflowWork.message.confirmModal', '确认框'),
    content: this.getMsgI18ns('WorkflowWork.message.confirmDelFlow', '确认删除流程？'),
    okText: this.$widget.locale && this.$widget.locale.Modal && this.$widget.locale.Modal.okText,
    cancelText: this.$widget.locale && this.$widget.locale.Modal && this.$widget.locale.Modal.cancelText,
    onOk() {
      _self.workFlow[removeMethod](uuids, success, failure);
    }
  });
};
WorkView.prototype.onRemoveSuccess = function (result) {
  const _self = this;
  _self.allowUnloadWorkData = true;
  // 提示
  let message = _self.getMsgI18ns('WorkflowWork.message.removeSuccess', '删除成功！');
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'remove',
    result,
    message
  });
};
WorkView.prototype.onRemoveFailure = function () {
  this.handlerError.apply(this, arguments);
};
// 恢复
WorkView.prototype.recover = function () {
  const _this = this;
  let workData = _this.workFlow.getWorkData();
  // 操作动作及类型
  _this.setEventAction('恢复', 'Recover', workData);
  let taskInstUuids = [workData.taskInstUuid];
  let success = function () {
    if (isFunction(_this.onAfterRecover)) {
      _this.callbackAfterAction(_self.onAfterRecover, true, arguments);
    } else {
      _this.onRecoverSuccess.apply(_this, arguments);
    }
  };
  let failure = function () {
    if (isFunction(_this.onAfterRecover)) {
      _this.callbackAfterAction(_this.onAfterRecover, false, arguments);
    } else {
      _this.onRecoverFailure.apply(_this, arguments);
    }
  };
  _this.$widget.$confirm({
    title: this.getMsgI18ns('WorkflowWork.message.confirmModal', '确认框'),
    content: this.getMsgI18ns('WorkflowWork.message.confirmRecover', '确认恢复流程？'),
    okText: this.$widget.locale && this.$widget.locale.Modal && this.$widget.locale.Modal.okText,
    cancelText: this.$widget.locale && this.$widget.locale.Modal && this.$widget.locale.Modal.cancelText,
    onOk() {
      _this.workFlow.recover(taskInstUuids, success, failure);
    }
  });
};
WorkView.prototype.onRecoverSuccess = function (result) {
  const _self = this;
  _self.allowUnloadWorkData = true;
  // 提示
  let message = _self.getMsgI18ns('WorkflowWork.message.recoverSuccess', '恢复成功！');
  // 触发操作成功事件
  _self.emitActionSuccess({
    action: 'recover',
    result,
    message
  });
};
WorkView.prototype.onRecoverFailure = function () {
  this.handlerError.apply(this, arguments);
};
// 进入连续签批模式
WorkView.prototype.enterContinuousMode = function () {
  const _this = this;

  let callback = () => {
    localStorage.setItem('continuousMode', 'true');
    let href = window.location.href;
    if (href.indexOf('continuousMode') == -1) {
      window.location = href + '&continuousMode=1';
    } else {
      let urlPars = href.split('?');
      let params = urlPars[1].split('&');
      let url = urlPars[0] + '?';
      for (let i = 0; i < params.length; i++) {
        let param = params[i];
        let connector = i > 0 ? '&' : '';
        if (param.indexOf('continuousMode') != -1) {
          url += connector + 'continuousMode=1';
        } else {
          url += connector + param;
        }
      }
      window.location = url;
    }
  };

  if (_this.isDyformDataChanged()) {
    _this.$widget.$confirm({
      title: this.getMsgI18ns('WorkflowWork.message.confirmModal', '确认框'),
      content: this.getMsgI18ns('WorkflowWork.message.dyformDataChanged', '表单内容已变更，是否放弃更改？'),
      okText: this.$widget.locale && this.$widget.locale.Modal && this.$widget.locale.Modal.okText,
      cancelText: this.$widget.locale && this.$widget.locale.Modal && this.$widget.locale.Modal.cancelText,
      onOk() {
        _this.allowUnloadWorkData = true;
        callback();
      }
    });
  } else {
    callback();
  }
};
// 退出连续签批模式
WorkView.prototype.exitContinuousMode = function () {
  localStorage.setItem('continuousMode', 'false');
  let href = window.location.href;
  if (this.$widget.continuousMode) {
    if (href.indexOf('continuousMode=1') != -1) {
      window.location = href.replace('continuousMode=1', 'continuousMode=0');
    } else {
      let urlPars = href.split('?');
      let params = urlPars[1].split('&');
      let url = urlPars[0] + '?continuousMode=0&';
      for (let i = 0; i < params.length; i++) {
        let param = params[i];
        let connector = i > 0 ? '&' : '';
        if (param.indexOf('continuousMode') != -1) {
          continue;
        } else {
          url += connector + param;
        }
      }
      window.location = url;
    }
  }
};
// 获取流程设置
WorkView.prototype.getSettings = function () {
  return this.workFlow.getSettings();
};
// 获取流程使用的组织版本ID
WorkView.prototype.getOrgVersionIds = function () {
  const _this = this;
  if (_this.getOrgVersionIdsPromise) {
    return _this.getOrgVersionIdsPromise;
  }
  let workData = _this.getWorkData();
  _this.getOrgVersionIdsPromise = _this.$widget.$axios
    .get(`/proxy/api/workflow/work/getOrgVersionIds?flowInstUuid=${workData.flowInstUuid}&flowDefUuid=${workData.flowDefUuid}`)
    .then(({ data: result }) => {
      return result.data;
    });
  return _this.getOrgVersionIdsPromise;
};
WorkView.prototype.listGroupChatProvider = function () {
  const _this = this;
  if (_this.listGroupChatProviderPromise) {
    return _this.listGroupChatProviderPromise;
  }
  _this.listGroupChatProviderPromise = _this.$widget.$axios
    .get('/proxy/api/workflow/work/listGroupChatProvider')
    .then(({ data: result }) => {
      if (result.data) {
        return result.data
          .filter(item => item.enabled)
          .map(item => {
            return { label: item.name, value: item.id };
          });
      } else {
        return [];
      }
    });
  return _this.listGroupChatProviderPromise;
};
// 发起群聊
WorkView.prototype.startGroupChat = function () {
  const _this = this;
  const title = _this.$widget.$t('WorkflowWork.startGroupChat', '发起群聊');
  let locale = _this.$widget.locale || {};

  let Modal = Vue.extend({
    template: `<a-config-provider :locale="locale">
        <a-modal dialogClass="pt-modal wf-error-hander-modal" :title="title" :visible="visible" width="600px" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
        <div style="height: 220px; overflow:auto">
          <WorkflowStartGroupChat ref="startGroupChat" :workView="workView"></WorkflowStartGroupChat>
        </div>
        </a-modal>
      </a-config-provider>`,
    components: { WorkflowStartGroupChat: () => import('./workflow-start-group-chat.vue') },
    data: function () {
      return {
        title,
        visible: true,
        locale,
        workView: _this
      };
    },
    created() { },
    methods: {
      handleCancel() {
        this.visible = false;
        this.$destroy();
      },
      handleOk() {
        this.$refs.startGroupChat.collect().then(data => {
          this.startGroupChat(data).then(chatId => {
            if (chatId) {
              setTimeout(() => {
                let iframe = document.createElement('iframe');
                if (data.provider === 'feishu') {
                  iframe.src = `https://applink.feishu.cn/client/chat/open?openChatId=${chatId}`;
                  iframe.style.display = 'none';
                  document.body.appendChild(iframe);
                } else if (data.provider === 'dingtalk') {
                  window.open(`https://applink.dingtalk.com/page/conversation?corpId=${data.corpId}&chatId=${chatId}`);
                }
              }, 2000);
            }
            this.visible = false;
            this.$destroy();
          });
        });
      },
      startGroupChat(data) {
        _this.$widget.$loading(_this.$widget.$t('WorkflowWork.startGroupChat', '发起群聊') + '...');
        return $axios
          .post('/proxy/api/workflow/work/startGroupChat', data)
          .then(({ data: result }) => {
            _this.$widget.$loading(false);
            if (result.code === 0) {
              _this.showSuccess({ content: _this.$widget.$t('WorkflowWork.startGroupChatSuccess', '发起群聊成功') });
              return Promise.resolve(result.data);
            } else {
              _this.showError({ content: result.msg || _this.$widget.$t('WorkflowWork.startGroupChatFailed', '发起群聊失败') });
              return Promise.reject();
            }
          })
          .catch(({ response }) => {
            _this.$widget.$loading(false);
            _this.showError({ content: (response && response.data && response.data.msg) || '服务异常！' });
          });
      }
    }
  });
  let modal = new Modal({
    i18n: _this.$widget.$i18n
  });
  modal.$mount();
};

// 显示合并表单数据变更弹出框
WorkView.prototype.showMergeChangedFormDataModal = function ({ taskInstRecVer = 0, action, actionType, buttons = {}, watchValue = false }) {
  const _this = this;
  import('./workflow-merge-changed-modal.vue').then(module => {
    const MergeChangedModal = Vue.extend(module.default);
    let modal = new MergeChangedModal({
      propsData: { workView: _this, expectTaskInstRecVer: taskInstRecVer, action, actionType, buttons, watchValue },
      i18n: _this.$widget.$i18n,
      provide() {
        let provided = { ..._this.$widget._provided };
        provided.locale = _this.$widget.locale;
        return { provided }
      }
    });
    modal.$mount();
    modal.show();
  });
}

// 是否表单内置字段
WorkView.prototype.isDyformSystemField = function (fieldName) {
  return DYFORM_SYSTEM_FIELD_SET.has(fieldName);
}
WorkView.prototype.isRowDataChanged = function (newRow, oldRow) {
  for (let key in newRow) {
    if (this.isDyformSystemField(key)) {
      continue;
    }
    if (this.isDiff(newRow[key], oldRow[key])) {
      return true;
    }
  }
  return false;
}
WorkView.prototype.isDiff = function (newValue, oldValue) {
  if (newValue == oldValue) {
    return false;
  }
  if (newValue == null && oldValue == '') {
    return false;
  }
  if (isArray(newValue) || isArray(oldValue)) {
    return this.isDiffOfFile(newValue, oldValue);
  }
  return true;
}
WorkView.prototype.isDiffOfFile = function (newFiles = [], oldFiles = []) {
  let oldFileIds = (oldFiles && oldFiles.map(file => file.fileID)) || [];
  let newFileIds = (newFiles && newFiles.map(file => file.fileID)) || [];
  if (oldFileIds.length != newFileIds.length) {
    return true;
  }
  oldFileIds.forEach(oldFileId => {
    let index = newFileIds.indexOf(oldFileId);
    if (index != -1) {
      let oldFile = oldFiles.find(file => file.fileID == oldFileId);
      let newFile = newFiles.find(file => file.fileID == oldFileId);
      if (oldFile.fileName == newFile.fileName) {
        newFileIds.splice(index, 1);
      }
    }
  });
  return newFileIds.length != 0;
}
export default WorkView;
