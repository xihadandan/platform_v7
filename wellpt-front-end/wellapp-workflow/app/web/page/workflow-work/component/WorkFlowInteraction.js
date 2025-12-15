/**
 * 流程交互模块
 */
class WorkFlowInteraction {
  constructor(workData) {
    const _self = this;
    _self.workData = workData || {};
    // 临时数据存储
    _self.tempData = {
      fromTaskId: _self.workData.fromTaskId,
      toDirectionId: null,
      toDirectionIds: null,
      toTaskId: null,
      toTaskIds: null,
      taskUsers: {},
      taskUserJobPaths: {},
      taskCopyUsers: {},
      taskTransferUsers: {},
      taskMonitors: {},
      taskDecisionMakers: {},
      jobSelected: null,
      toSubFlowId: null,
      waitForMerge: {},
      rollbackToTaskId: null,
      rollbackToTaskInstUuid: null,
      gotoTaskId: null,
      archiveFolderUuid: null
    };
    // 保持初始化的临时数据
    _self.initTempData = JSON.parse(JSON.stringify(_self.tempData));

    // 操作代码
    _self.actionCode = {
      submit: 'B004002', // 提交按钮代码
      submitAndPrint: 'B004020', // 打印并套打按钮代码
      signOpinion: 'B004011', // 签署意见代码
      rollback: 'B004003', // 退回按钮代码
      directRollback: 'B004004', // 直接退回按钮代码
      cancel: 'B004005', // 撤回按钮代码
      viewProcess: 'B004013', // 查看办理过程按钮代码
      gotoTask: 'B004016' // 跳转按钮代码
    };
  }
}

Object.assign(WorkFlowInteraction.prototype, {
  // 获取流程数据
  getWorkData: function () {
    return this.workData;
  },
  // 获取交互性的数据
  getInteractionTaskData: function () {
    return this.tempData;
  },
  // 设置临时数据
  setTempData: function (key, value) {
    if (Object.prototype.hasOwnProperty.call(this.tempData, key)) {
      this.tempData[key] = value;
    } else {
      console.log('value of key[' + key + '] is not temp data');
      console.log(value);
    }
  },
  // 清理临时数据
  clearTempData: function () {
    this.tempData = JSON.parse(JSON.stringify(this.initTempData));
    delete this.workData.customDynamicButton;
  },
  // 工作流数据存储到临时数据
  _workData2TempData: function () {
    for (var key in this.tempData) {
      this.tempData[key] = this.workData[key];
    }
    this.initTempData = JSON.parse(JSON.stringify(this.tempData));
  },
  // 临时数据合并到工作流数据
  _tempData2WorkData: function () {
    for (var key in this.tempData) {
      this.workData[key] = this.tempData[key];
    }
  },
  getDataProperty: function (key) {
    var self = this;
    if (Object.prototype.hasOwnProperty.call(self.tempData, key)) {
      return self.tempData[key];
    }
    return self.workData[key];
  },
  setDataProperty: function (key, value) {
    var self = this;
    if (Object.prototype.hasOwnProperty.call(self.tempData, key)) {
      return (self.tempData[key] = value);
    }
    return (self.workData[key] = value);
  },
  getFromTaskId: function () {
    return this.getDataProperty('fromTaskId');
  },
  setFromTaskId: function (fromTaskId) {
    return this.setDataProperty('fromTaskId', fromTaskId);
  },
  getToDirectionId: function () {
    return this.getDataProperty('toDirectionId');
  },
  setToDirectionId: function (toDirectionId) {
    return this.setDataProperty('toDirectionId', toDirectionId);
  },
  getToDirectionIds: function () {
    return this.getDataProperty('toDirectionIds');
  },
  setToDirectionIds: function (toDirectionIds) {
    return this.setDataProperty('toDirectionIds', toDirectionIds);
  },
  getToTaskId: function () {
    return this.getDataProperty('toTaskId');
  },
  setToTaskId: function (toTaskId) {
    return this.setDataProperty('toTaskId', toTaskId);
  },
  getToTaskIds: function () {
    return this.getDataProperty('toTaskIds');
  },
  setToTaskIds: function (toTaskIds) {
    return this.setDataProperty('toTaskIds', toTaskIds);
  },
  getTaskUsers: function () {
    return this.getDataProperty('taskUsers');
  },
  setTaskUsers: function (taskUsers) {
    return this.setDataProperty('taskUsers', taskUsers);
  },
  getTaskCopyUsers: function () {
    return this.getDataProperty('taskCopyUsers');
  },
  setTaskCopyUsers: function (taskCopyUsers) {
    return this.setDataProperty('taskCopyUsers', taskCopyUsers);
  },
  getTaskTransferUsers: function () {
    return this.getDataProperty('taskTransferUsers');
  },
  setTaskTransferUsers: function (taskTransferUsers) {
    return this.setDataProperty('taskTransferUsers', taskTransferUsers);
  },
  getTaskMonitors: function () {
    return this.getDataProperty('taskMonitors');
  },
  setTaskMonitors: function (taskMonitors) {
    return this.setDataProperty('taskMonitors', taskMonitors);
  },
  getToSubFlowId: function () {
    return this.getDataProperty('toSubFlowId');
  },
  setToSubFlowId: function (toSubFlowId) {
    return this.setDataProperty('toSubFlowId', toSubFlowId);
  },
  getRollbackToTaskId: function () {
    return this.getDataProperty('rollbackToTaskId');
  },
  setRollbackToTaskId: function (rollbackToTaskId) {
    return this.setDataProperty('rollbackToTaskId', rollbackToTaskId);
  },
  getRollbackToTaskInstUuid: function () {
    return this.getDataProperty('rollbackToTaskInstUuid');
  },
  setRollbackToTaskInstUuid: function (rollbackToTaskInstUuid) {
    return this.setDataProperty('rollbackToTaskInstUuid', rollbackToTaskInstUuid);
  },
  getWaitForMerge: function () {
    return this.getDataProperty('waitForMerge');
  },
  setWaitForMerge: function (waitForMerge) {
    return this.setDataProperty('waitForMerge', waitForMerge);
  },
  getGotoTaskId: function () {
    return this.getDataProperty('gotoTaskId');
  },
  setGotoTaskId: function (gotoTaskId) {
    return this.setDataProperty('gotoTaskId', gotoTaskId);
  }
});

export default WorkFlowInteraction;
