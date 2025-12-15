'use strict';
import { isEmpty, isArray, isFunction, assignIn, each as forEach } from 'lodash';
import ErrorHandler from './ErrorHanlder.js';
let unit = {};

export function getMsgI18ns(msg, callbackContext, prefixKey = 'WorkflowWork.message') {
  let msgI18n = msg;
  if (!msg) {
    return msgI18n;
  }

  let $widget;
  if (callbackContext) {
    $widget = callbackContext.$widget;
  } else {
    $widget = window.$app;
  }

  if (!$widget) {
    return msgI18n;
  }
  let msgMap = {
    notFindHanlerNextStep: '找不到办理人时，自动进入下一个环节为空！',
    taskNotNewSubflow: '环节实例没有发起任何子流程！',
    currentTaskSuspendNotOperated: '当前环节处于挂起状态，不能进行',
    collabNeedMultipe: '协作环节需多人办理！',
    openPrintNotTemplate: '流程配置了提交并套打，但没有配置套打模板！',
    printFileNotEmpty: '模板文件不能为空，请检查模板配置！',
    printTemplate: '套打',
    opinionWithdrawn: '（办理意见已撤回）',
    greaterThanCurrentTime: '办理时限必须大于当前时间！',
    notTimingNotChange: '流程没有配置计时系统，不能变更时限！',
    submitSubflowTask: '提交到子流程环节',
    serialNumberOccupy: '流水号已经被占用，系统已为您获取最新流水号',
    noJobSelect: '没有选择职位！',
    notSpecifyCopy: '无法指定抄送人！',
    notSpecifySupervise: '无法指定督办人！',
    notFindSpecifyTask: '找不到指定的提交环节！',
    taskNoSpecifyUser: '任务没有指定办理人！',
    workingMinutes: '工作分钟',
    mainFlowCancel: '主流程撤销',
    mainFlowCancelCompleted: '主流程撤销办结!',
    taskCurrentStateNotDel: '任务当前所在的状态不允许删除!',
    workDelegation: '工作委托',
    workDelegationExpiredCollection: '工作委托到期回收',
    workDelegationStopCollection: "工作委托终止回收",
    pleaseHurryUp: '请抓紧办理!',
    timeLimitChange: '变更时限',
    Redo: "重办",
    defaultWorkTimeNot: "默认工作时间方案没有配置",
    timerConfigurationError: "计时器配置出错！",
    pleaseEnterValidTimeLimit: "请输入有效的办理时限!",
    transferNotMe: "转办人员不能包括当前办理人!",
    countersignedNotMe: "会签人员不能包括当前办理人!",
    addSigningNotMe: "加签人员不能包括当前办理人!",
    entrustTo: "委托给",
    printFileNotSet: "打印模板类型未配置，请检查"
  };
  for (const key in msgMap) {
    if (msg.indexOf(msgMap[key]) !== -1) {
      let longMsg = '', longMsgKey
      for (const k in msgMap) {
        if (msg.indexOf(msgMap[k]) !== -1) {
          if (msgMap[k].length > longMsg.length) {
            longMsg = msgMap[k]
            longMsgKey = k
          }
        }
      }
      if (longMsgKey) {
        // 匹配到更长字符
        msgI18n = msg.replace(new RegExp(longMsg, 'g'), $widget.$t(`${prefixKey}.${longMsgKey}`, longMsg));
        break
      }
      if (key === msgMap.currentTaskSuspendNotOperated) {
        // 全部替换
        msgI18n = $widget.$t(`${prefixKey}.${key}`);
      } else {
        msgI18n = msg.replace(new RegExp(msgMap[key], 'g'), $widget.$t(`${prefixKey}.${key}`, msgMap[key]));
      }
    }
  }
  return msgI18n;
}

// 1、工作流异常
function WorkFlowException(faultData, options) {
  var _self = this;
  var eData = faultData.data;
  var callback = options.callback;
  var callbackContext = options.callbackContext;
  if (eData.hasOwnProperty('autoClose')) {
    if (eData.autoClose === true) {
      showInfo(callbackContext, eData.msg);
    } else {
      showError(callbackContext, eData.msg);
    }
  } else {
    showError(callbackContext, eData.msg, () => {
      // 重新触发回调事件
      if (isFunction(callback)) {
        callback.call(_self);
      }
    });
  }
}

// 2、任务没有指定参与者，弹出人员选择框选择参与人(人员、部门及群组)
function TaskNotAssignedUser(faultData, options) {
  const _this = this;
  let eData = faultData.data;
  let callback1 = options.callback;
  let callbackContext = options.callbackContext;
  let workFlow = options.workFlow;
  let workData = workFlow.getWorkData();
  let title = '';
  if (!isEmpty(eData.title)) {
    title = eData.title;
    if (title.indexOf('(') > -1) {
      title = title.substring(1, title.length - 1);
    }
    let titleArr = title.split(':');
    const taskNameI18n = callbackContext.$widget.$t(`WorkflowView.${eData.taskId}.taskName`, null);
    if (taskNameI18n) {
      titleArr[1] = taskNameI18n;
    }
    const workflowNameI18n = callbackContext.$widget.$t(`WorkflowView.workflowName`, null);
    if (taskNameI18n && workflowNameI18n) {
      titleArr[0] = workflowNameI18n;
    }
    title = titleArr.join(':');
  }
  let isCancel = true;
  // 新组织弹出框 zyguo

  openOrgSelect({
    valueField: '',
    labelField: '',
    title: callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseToUser', '选择承办人') + '【' + title + '】',
    type: 'all',
    multiple: true,
    selectTypes: 'all',
    valueFormat: 'justId',
    orgVersionId: eData.currentOrgVersionId,
    orgVersionIds: eData.orgVersionIds,
    bizOrgId: eData.bizOrgId,
    orgIdOptions: eData.orgIdBizOrgIdMap,
    locale: callbackContext && callbackContext.$widget && callbackContext.$widget.locale,
    callback: function (values, labels, treeNodes) {
      let taskUsers = {};
      let taskId = eData.taskId;
      let taskUserJobPaths = {};
      if (values && values.length > 0) {
        // 在原来的环节办理人上增加环节办理人
        taskUsers = workData.taskUsers;
        taskUsers[taskId] = values;
        taskUserJobPaths = workData.taskUserJobPaths;
        taskUserJobPaths[taskId] = gettaskUserJobPaths(treeNodes);
      } else {
        taskUsers[taskId] = null;
        workData.taskUsers = taskUsers;
      }
      workFlow.setTempData('taskUsers', workData.taskUsers);
      isCancel = false;
      // 重新触发回调事件
      if (isFunction(callback1)) {
        callback1.call(callbackContext);
      }
    },
    close: function () {
      options.cancel && options.cancel();
      if (isCancel) {
        workFlow.clearTempData();
      }
    }
  });
}

function openOrgSelect({
  title,
  type,
  multiple,
  selectTypes,
  valueFormat,
  viewStyles,
  moreOptList,
  orgVersionId,
  orgVersionIds,
  bizOrgId,
  orgIdOptions,
  params,
  checkableTypes,
  checkableTypesOfOrgType,
  footerDescriptionComponent,
  callback,
  close,
  locale = {}
}) {
  const _this = this;
  import('@admin/app/web/lib/org-select.vue').then(m => {
    Vue.component('OrgSelect', m.default);

    let orgSelectContainer = document.createElement('div');
    orgSelectContainer.classList.add('org-select-container');
    let orgSelectDiv = document.createElement('div');
    orgSelectContainer.appendChild(orgSelectDiv);
    document.body.appendChild(orgSelectContainer);
    let OrgSelect = Vue.extend({
      template: `<a-config-provider :locale="locale">
                  <OrgSelect ref="orgSelect" v-model="orgId" v-bind="orgOptions"  @change="onChange" @cancel="onCancel">
                  <template v-if="footerDescriptionComponent" slot="footerDescription"><footerDescriptionComponent/></template>
                  </OrgSelect>
                </a-config-provider>`, //
      data: function () {
        return {
          orgId: '',
          orgOptions: {
            title,
            orgType: type != 'all' ? (Array.isArray(type) ? type : [type]) : undefined,
            multiSelect: multiple,
            selectTypes,
            isPathValue: valueFormat != 'justId',
            viewStyles,
            moreOptList,
            orgVersionId,
            orgVersionIds,
            bizOrgId,
            orgIdOptions,
            params,
            checkableTypes,
            checkableTypesOfOrgType
          },
          footerDescriptionComponent,
          locale
        };
      },
      mounted() {
        let openOrg = (counter) => {
          if (this.$refs.orgSelect && this.$refs.orgSelect.orgSelectTreeLoading && counter < 10) {
            setTimeout(() => {
              openOrg(++counter);
            }, 50);
          } else {
            this.$refs.orgSelect.openModal();
          }
        }
        setTimeout(() => {
          openOrg(1);
        }, 100);
      },
      methods: {
        onChange({ value, label, nodes }) {
          if (callback) {
            callback.call(this, value.split(';'), label.split(';'), nodes);
          }
          this.$destroy();
          orgSelectContainer.remove();
        },
        onCancel() {
          if (close) {
            close.call(this);
          }
          this.$destroy();
          orgSelectContainer.remove();
        }
      }
    });
    let orgSelect = new OrgSelect({ i18n: $app && $app.$options && $app.$options.i18n });
    orgSelect.$mount(orgSelectDiv);
  });
}

// 3、任务没有指定抄送人，弹出人员选择框选择抄送人(人员、部门及群组)
function TaskNotAssignedCopyUser(faultData, options) {
  const _this = this;
  let eData = faultData.data;
  let callback = options.callback;
  let callbackContext = options.callbackContext;
  let workFlow = options.workFlow;
  let workData = workFlow.getWorkData();
  let currentTaskName = callbackContext.$widget.$t(`WorkflowView.${eData.taskId}.taskName`, eData.taskName);
  let workflowName = callbackContext.$widget.$t(`WorkflowView.workflowName`, workData.name);
  let title = '';
  if (!isEmpty(eData.title)) {
    title = eData.title;
    title = title.indexOf('(') > -1 ? title.substring(1, title.length - 1) : title;
  }
  title = workflowName ? `${workflowName}:${currentTaskName}` : title ? title : currentTaskName;
  let isCancel = true;
  let users = eData.users;
  let okCallback = function (values, labels) {
    console.log(labels);
    var taskCopyUsers = workData.taskCopyUsers;
    var taskId = eData.taskId;
    if (values && values.length > 0) {
      taskCopyUsers[taskId] = values;
      workData.taskCopyUsers = taskCopyUsers;
    } else {
      taskCopyUsers[taskId] = null;
      workData.taskCopyUsers = taskCopyUsers;
    }
    workFlow.copyToUsers = labels;
    isCancel = false;
    // 重新触发回调事件
    if (isFunction(callback)) {
      callback.call(callbackContext);
    }
  };
  // 二次确认选择抄送人
  let label = callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseCcUser', '选择抄送人');
  if (users) {
    title = label + '【' + title + '】';
    eData.params = { loadEleUser: true };
    openUserByUnit(
      title,
      users,
      eData,
      workFlow,
      callbackContext.$widget.$t('WorkflowWork.modal.ccUserLabel', '抄送人'),
      true,
      okCallback,
      options.cancel,
      callbackContext
    );
  } else {
    // 新组织弹出框zyguo
    openOrgSelect({
      valueField: '',
      labelField: '',
      title: label + '【' + title + '】',
      type: 'all',
      multiple: true,
      selectTypes: 'all',
      valueFormat: 'justId',
      orgVersionId: eData.currentOrgVersionId,
      orgVersionIds: eData.orgVersionIds,
      bizOrgId: eData.bizOrgId,
      orgIdOptions: eData.orgIdBizOrgIdMap,
      locale: callbackContext && callbackContext.$widget && callbackContext.$widget.locale,
      callback: function (values, labels) {
        console.log(values);
        console.log(labels);
        okCallback.apply(this, arguments);
      },
      close: function () {
        options.cancel && options.cancel();
        if (isCancel) {
          workFlow.clearTempData();
        }
      }
    });
  }
}

function TaskNotAssignedTransferUser(faultData, options) {
  taskNotAssignedActionUser(
    faultData,
    options,
    'taskTransferUsers',
    'transferUsers',
    options.callbackContext.$widget.$t('WorkflowWork.modal.transfer', '转办')
  );
  // const _this = this;
  // let eData = faultData.data;
  // let callback = options.callback;
  // let callbackContext = options.callbackContext;
  // let workFlow = options.workFlow;
  // let workData = workFlow.getWorkData();
  // let taskName = eData.taskName;
  // let title = '';
  // if (!isEmpty(eData.title)) {
  //   title = eData.title;
  //   title = title.indexOf('(') > -1 ? title.substring(1, title.length - 1) : title;
  // }
  // let isCancel = true;
  // let users = eData.users;
  // let okCallback = function (values, labels) {
  //   let taskTransferUsers = workData.taskTransferUsers;
  //   let taskId = eData.taskId;
  //   if (values && values.length > 0) {
  //     taskTransferUsers[taskId] = values;
  //     workData.taskTransferUsers = taskTransferUsers;
  //   } else {
  //     taskTransferUsers[taskId] = null;
  //     workData.taskTransferUsers = taskTransferUsers;
  //   }
  //   workFlow.transferUsers = labels;
  //   isCancel = true;
  //   // 重新触发回调事件
  //   if (isFunction(callback)) {
  //     callback.call(callbackContext);
  //   }
  // };
  // // 二次确认选择转办人
  // if (users && users.length > 0) {
  //   title = '选择转办人员【' + taskName + '】';
  //   openUserByUnit(title, users, eData, workFlow, '转办人员', true, okCallback, callbackContext);
  // } else {
  //   // 新组织弹出框zyguo
  //   openOrgSelect({
  //     valueField: '',
  //     labelField: '',
  //     title: '选择转办人员【' + taskName + '】',
  //     type: 'all',
  //     multiple: true,
  //     selectTypes: 'all',
  //     valueFormat: 'justId',
  //     orgVersionId: eData.currentOrgVersionId,
  //     orgVersionIds: eData.orgVersionIds,
  //     locale: callbackContext && callbackContext.$widget && callbackContext.$widget.locale,
  //     callback: function (values, labels) {
  //       okCallback.apply(this, arguments);
  //     },
  //     close: function () {
  //       if (isCancel) {
  //         workFlow.clearTempData();
  //       }
  //     }
  //   });
  // }
}
function TaskNotAssignedCounterSignUser(faultData, options) {
  taskNotAssignedActionUser(
    faultData,
    options,
    'taskCounterSignUsers',
    'signUsers',
    options.callbackContext.$widget.$t('WorkflowWork.modal.signUser', '会签')
  );
  // const _this = this;
  // let eData = faultData.data;
  // let callback = options.callback;
  // let callbackContext = options.callbackContext;
  // let workFlow = options.workFlow;
  // let workData = workFlow.getWorkData();
  // let taskName = eData.taskName;
  // let title = '';
  // if (!isEmpty(eData.title)) {
  //   title = eData.title;
  //   title = title.indexOf('(') > -1 ? title.substring(1, title.length - 1) : title;
  // }
  // let isCancel = true;
  // let users = eData.users;
  // let okCallback = function (values, labels) {
  //   let taskCounterSignUsers = workData.taskCounterSignUsers;
  //   let taskId = eData.taskId;
  //   if (values && values.length > 0) {
  //     taskCounterSignUsers[taskId] = values;
  //     workData.taskCounterSignUsers = taskCounterSignUsers;
  //   } else {
  //     taskCounterSignUsers[taskId] = null;
  //     workData.taskCounterSignUsers = taskCounterSignUsers;
  //   }
  //   workFlow.signUsers = labels;
  //   isCancel = true;
  //   // 重新触发回调事件
  //   if (isFunction(callback)) {
  //     callback.call(callbackContext);
  //   }
  // };
  // // 二次确认选择会签人
  // if (users && users.length > 0) {
  //   title = '选择会签人员【' + taskName + '】';
  //   openUserByUnit(title, users, eData, workFlow, '会签人员', true, okCallback, callbackContext);
  // } else {
  //   // 新组织弹出框zyguo
  //   openOrgSelect({
  //     valueField: '',
  //     labelField: '',
  //     title: '选择会签人员【' + taskName + '】',
  //     type: 'all',
  //     multiple: true,
  //     selectTypes: 'all',
  //     valueFormat: 'justId',
  //     orgVersionId: eData.currentOrgVersionId,
  //     orgVersionIds: eData.orgVersionIds,
  //     locale: callbackContext && callbackContext.$widget && callbackContext.$widget.locale,
  //     callback: function (values, labels) {
  //       okCallback.apply(this, arguments);
  //     },
  //     close: function () {
  //       if (isCancel) {
  //         workFlow.clearTempData();
  //       }
  //     }
  //   });
  // }
}
function TaskNotAssignedAddSignUser(faultData, options) {
  taskNotAssignedActionUser(
    faultData,
    options,
    'taskAddSignUsers',
    'addSignUsers',
    options.callbackContext.$widget.$t('WorkflowWork.modal.addSignUser', '加签')
  );
}
function taskNotAssignedActionUser(faultData, options, userProp, labelProp, actionName) {
  const _this = this;
  let eData = faultData.data;
  let callback = options.callback;
  let callbackContext = options.callbackContext;
  let workFlow = options.workFlow;
  let workData = workFlow.getWorkData();
  let currentTaskName = callbackContext.$widget.$t(`WorkflowView.${eData.taskId}.taskName`, workData.taskName);
  let workflowName = callbackContext.$widget.$t(`WorkflowView.workflowName`, workData.name);
  let taskName = `${workflowName}:${currentTaskName}`;
  let title = '';
  if (!isEmpty(eData.title)) {
    title = eData.title;
    title = title.indexOf('(') > -1 ? title.substring(1, title.length - 1) : title;
  }
  let isCancel = true;
  let users = eData.users;
  let okCallback = function (values, labels, treeNodes) {
    let taskActionUsers = workData[userProp] || {};
    let taskUserJobPaths = workData.taskUserJobPaths || {};
    let taskId = eData.taskId;
    if (values && values.length > 0) {
      taskActionUsers[taskId] = values;
      workData[userProp] = taskActionUsers;
      taskUserJobPaths[taskId] = gettaskUserJobPaths(treeNodes);
      workData.taskUserJobPaths = taskUserJobPaths;
    } else {
      taskActionUsers[taskId] = null;
      workData[userProp] = taskActionUsers;
    }
    workFlow[labelProp] = labels;
    isCancel = true;
    // 重新触发回调事件
    if (isFunction(callback)) {
      callback.call(callbackContext);
    }
  };
  let footerDescriptionComponent = null;
  let label = callbackContext.$widget.$t('WorkflowWork.modal.actionFormPrivilegeLabel', { actionName }, `${actionName}表单权限`),
    label2 = callbackContext.$widget.$t('WorkflowWork.modal.sameActionFromPrivilegeLabel', { actionName }, `同${actionName}人表单权限`),
    label3 = callbackContext.$widget.$t('WorkflowWork.modal.readPrivilege', '只读权限');
  if (eData.viewFormMode == 'custom') {
    footerDescriptionComponent = Vue.extend({
      template: `<div style="position:absolute"><label>${label}</label>
        <a-radio-group v-model="workData.viewFormMode" size="small">
          <a-radio-button value="default">${label2}</a-radio-button>
          <a-radio-button value="readonly">${label3}</a-radio-button>
        </a-radio-group>
      </div>`,
      data() {
        this.$set(workData, 'viewFormMode', 'default');
        return {
          workData
        };
      }
    });
    Vue.component('footerDescriptionComponent', footerDescriptionComponent);
  }
  // 二次确认选择操作人
  if (users && users.length > 0) {
    title =
      callbackContext.$widget.$t('WorkflowWork.modal.chooseActionNameUser', { actionName }, `选择${actionName}人员`) + `【${taskName}】`;
    eData.params = { loadEleUser: true };
    openUserByUnit(
      title,
      users,
      eData,
      workFlow,
      `${actionName}${callbackContext.$widget.$t('WorkflowWork.modal.user', '人员')}`,
      true,
      okCallback,
      options.cancel,
      callbackContext,
      footerDescriptionComponent
    );
  } else {
    // 新组织弹出框zyguo
    let type = ['MyOrg', 'MyLeader'];
    let moreOptList = [];
    if (eData.taskInstUuid) {
      type.push('TaskDoneUsers');
      moreOptList.push({
        id: 'TaskDoneUsers',
        name: callbackContext.$widget.$t('WorkflowWork.modal.taskDoneUser', '已办人员'), //'已办人员',
        attach: 'list;tree'
      });
    }
    openOrgSelect({
      valueField: '',
      labelField: '',
      title:
        callbackContext.$widget.$t('WorkflowWork.modal.chooseActionNameUser', { actionName }, `选择${actionName}人员`) + `【${taskName}】`,
      type,
      multiple: true,
      selectTypes: 'all',
      valueFormat: 'justId',
      viewStyles: { TaskDoneUsers: 'list' },
      moreOptList,
      orgVersionId: eData.currentOrgVersionId,
      orgVersionIds: eData.orgVersionIds,
      bizOrgId: eData.bizOrgId,
      orgIdOptions: eData.orgIdBizOrgIdMap,
      params: {
        taskInstUuid: eData.taskInstUuid
      },
      checkableTypesOfOrgType: {
        TaskDoneUsers: 'user'
      },
      locale: callbackContext && callbackContext.$widget && callbackContext.$widget.locale,
      footerDescriptionComponent,
      callback: function (values, labels) {
        okCallback.apply(this, arguments);
      },
      close: function () {
        options.cancel && options.cancel();
        if (isCancel) {
          workFlow.clearTempData();
        }
      }
    });
  }
}
function TaskNotAssignedDecisionMaker(faultData, options) {
  const _this = this;
  let eData = faultData.data;
  let callback = options.callback;
  let callbackContext = options.callbackContext;
  let workFlow = options.workFlow;
  let workData = workFlow.getWorkData();
  let title = '';
  if (!isEmpty(eData.title)) {
    title = eData.title;
    title = title.indexOf('(') > -1 ? title.substring(1, title.length - 1) : title;
  }
  let isCancel = true;
  // 新组织弹出框 zyguo
  openOrgSelect({
    valueField: '',
    labelField: '',
    title: callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseDecisionMaker', '选择决策人') + '【' + title + '】',
    type: 'all',
    multiple: true,
    selectTypes: 'all',
    valueFormat: 'justId',
    orgVersionId: eData.currentOrgVersionId,
    orgVersionIds: eData.orgVersionIds,
    bizOrgId: eData.bizOrgId,
    orgIdOptions: eData.orgIdBizOrgIdMap,
    locale: callbackContext && callbackContext.$widget && callbackContext.$widget.locale,
    callback: function (values, labels, treeNodes) {
      console.log(labels);
      let taskDecisionMakers = workData.taskDecisionMakers;
      let taskUserJobPaths = workData.taskUserJobPaths || {};
      let taskId = eData.taskId;
      if (values && values.length > 0) {
        taskDecisionMakers[taskId] = values;
        workData.taskDecisionMakers = taskDecisionMakers;
        taskUserJobPaths['decisionMakers_' + taskId] = gettaskUserJobPaths(treeNodes);
        workData.taskUserJobPaths = taskUserJobPaths;
      } else {
        taskDecisionMakers[taskId] = null;
        workData.taskDecisionMakers = taskDecisionMakers;
      }
      workFlow.setTempData('taskDecisionMakers', workData.taskDecisionMakers);
      isCancel = false;
      // 重新触发回调事件
      if (isFunction(callback)) {
        callback.call(callbackContext);
      }
    },
    close: function () {
      options.cancel && options.cancel();
      if (isCancel) {
        workFlow.clearTempData();
      }
    }
  });
}
// 4、任务没有指定督办人，弹出人员选择框选择督办人(人员和部门及群组)
function TaskNotAssignedMonitor(faultData, options) {
  const _this = this;
  let eData = faultData.data;
  let callback = options.callback;
  let callbackContext = options.callbackContext;
  let workFlow = options.workFlow;
  let workData = workFlow.getWorkData();
  let title = '';
  if (!isEmpty(eData.title)) {
    title = eData.title;
    title = title.indexOf('(') > -1 ? title.substring(1, title.length - 1) : title;
  }
  let isCancel = true;
  // 新组织弹出框 zyguo
  openOrgSelect({
    valueField: '',
    labelField: '',
    title: callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseSupervisor', '选择督办人') + '【' + title + '】',
    type: 'all',
    multiple: true,
    selectTypes: 'all',
    valueFormat: 'justId',
    orgVersionId: eData.currentOrgVersionId,
    orgVersionIds: eData.orgVersionIds,
    bizOrgId: eData.bizOrgId,
    orgIdOptions: eData.orgIdBizOrgIdMap,
    locale: callbackContext && callbackContext.$widget && callbackContext.$widget.locale,
    callback: function (values, labels) {
      console.log(labels);
      let taskMonitors = workData.taskMonitors;
      let taskId = eData.taskId;
      if (values && values.length > 0) {
        taskMonitors[taskId] = values;
        workData.taskMonitors = taskMonitors;
      } else {
        taskMonitors[taskId] = null;
        workData.taskMonitors = taskMonitors;
      }
      workFlow.setTempData('taskMonitors', workData.taskMonitors);
      isCancel = false;
      // 重新触发回调事件
      if (isFunction(callback)) {
        callback.call(callbackContext);
      }
    },
    close: function () {
      options.cancel && options.cancel();
      if (isCancel) {
        workFlow.clearTempData();
      }
    }
  });
}

//5、选择具体办理人
function ChooseSpecificUser(faultData, options) {
  showUserByUnit(faultData, options, true);
}

function getOrgSelectTitle(eData, callbackContext, defaultTitle = '选择承办人') {
  let title = '';
  if (!isEmpty(eData.title)) {
    title = eData.title;
    if (title.indexOf('(') > -1) {
      title = title.substring(1, title.length - 1);
    }
    let titleArr = title.split(':');
    const taskNameI18n = callbackContext.$widget.$t(`WorkflowView.${eData.taskId}.taskName`, null);
    if (taskNameI18n) {
      titleArr[1] = taskNameI18n;
    }
    const workflowNameI18n = callbackContext.$widget.$t(`WorkflowView.workflowName`, null);
    if (workflowNameI18n) {
      titleArr[0] = workflowNameI18n;
    }
    title = callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseToUser', defaultTitle) + '【' + titleArr.join(':') + '】'
  } else {
    var taskName = callbackContext.$widget.$t(`WorkflowView.${eData.taskId}.taskName`, eData.taskName);
    title = callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseUndertaker', defaultTitle) + '【' + taskName + '】';
  }
  return title;
}

// 通过组织框，显示所有的本地用户
function showUserByUnit(faultData, options, multiple) {
  // var _self = this;
  var eData = faultData.data;
  var callback = options.callback;
  var callbackContext = options.callbackContext;
  var workFlow = options.workFlow;
  var workData = workFlow.getWorkData();
  var users = eData.users;
  // var taskName = callbackContext.$widget.$t(`WorkflowView.${eData.taskId}.taskName`, eData.taskName);

  var title = getOrgSelectTitle(eData, callbackContext, '选择承办人');// callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseUndertaker', '选择承办人') + '【' + taskName + '】';
  // 选择新的组织弹出框 zyguo
  var okCallback = function (values, labels, treeNodes) {
    var taskUsers = {};
    var taskId = eData.taskId;
    var taskUserJobPaths = {};
    if (values && values.length > 0) {
      // 在原来的环节办理人上增加环节办理人
      taskUsers = workData.taskUsers;
      taskUsers[taskId] = values;
      taskUserJobPaths = workData.taskUserJobPaths;
      taskUserJobPaths[taskId] = gettaskUserJobPaths(treeNodes);
    } else {
      taskUsers[taskId] = null;
      workData.taskUsers = taskUsers;
    }
    workFlow.setTempData('taskUsers', workData.taskUsers);
    // isCancel = false;
    // 重新触发回调事件
    if (isFunction(callback)) {
      callback.call(callbackContext);
    }
  };
  openUserByUnit(
    title,
    users,
    eData,
    workFlow,
    callbackContext.$widget.$t('WorkflowWork.modal.taskUser', '办理人'),
    multiple,
    okCallback,
    options.cancel,
    callbackContext
  );
}

function openUserByUnit(
  title,
  users,
  eData,
  workFlow,
  optionName,
  multiple,
  okCallback,
  cancelCallback,
  callbackContext,
  footerDescriptionComponent
) {
  let eleOrderMap = eData.eleOrderMap || {};
  let treeData = []; //convertUnitTreeData(users, eleOrderMap);
  let isCancel = true;
  let sidGranularity = eData.sidGranularity;
  let defaultViewStyle = eData.defaultViewStyle || 'list'; // SystemParams.getValue('unit.dialog.task.users.defaultViewStyle');
  let num = parseInt(eData.autoViewStyleNum || 20); // SystemParams.getValue('unit.dialog.task.users.autoViewStyle.num');
  let userNum = (eData.userIds && eData.userIds.length) || 0;
  // if (defaultViewStyle == 'auto') {
  //   forEach(users, function (uItem) {
  //     let len = uItem.showJobNamePath.split(';').length;
  //     userNum = userNum - 0 + len;
  //   });
  // }
  let viewStyles = defaultViewStyle == 'auto' ? (userNum <= num ? 'list' : 'tree') : defaultViewStyle;
  let selectTypes = new Set();
  let checkableTypes = new Set();
  let hasUser = false;
  if (sidGranularity) {
    if (sidGranularity == 'U') {
      checkableTypes.add('user');
    } else if (sidGranularity == 'J') {
      checkableTypes.add('job');
      checkableTypes.add('user');
    } else {
      checkableTypes.add('job');
      checkableTypes.add('user');
      checkableTypes.add('dept');
      checkableTypes.add('unit');
    }
  }
  eData.userIds &&
    eData.userIds.forEach(userId => {
      if (!userId) {
        return;
      }
      if (userId.startsWith('U')) {
        selectTypes.add('U');
        checkableTypes.add('user');
        hasUser = true;
      } else if (userId.startsWith('J')) {
        selectTypes.add('J');
        checkableTypes.add('job');
      } else if (userId.startsWith('D')) {
        selectTypes.add('D');
        checkableTypes.add('dept');
      } else if (userId.startsWith('S')) {
        selectTypes.add('S');
        checkableTypes.add('unit');
      }
    });
  // if (treeData.length > 0 && treeData[0].id.startsWith('J')) {
  //   selectTypes = 'J';
  //   checkableTypes.push('job');
  // } else {
  //   selectTypes = 'U';
  //   checkableTypes.push('user');
  // }
  openOrgSelect({
    valueField: '',
    labelField: '',
    title: title,
    type: 'TaskUsers',
    multiple: multiple,
    selectTypes: [...selectTypes.values()],
    valueFormat: 'justId',
    viewStyles: {
      TaskUsers: hasUser ? viewStyles : 'tree'
    },
    moreOptList: [
      {
        id: 'TaskUsers',
        name: optionName, //'办理人',
        treeData: treeData,
        attach: 'list;tree'
      }
    ],
    orgVersionId: eData.currentOrgVersionId,
    orgVersionIds: eData.orgVersionIds || [eData.currentOrgVersionId],
    bizOrgId: eData.bizOrgId,
    orgIdOptions: eData.orgIdBizOrgIdMap,
    params: {
      userIds: eData.userIds || [],
      userJobIdentityMap: eData.userJobIdentityMap || {},
      orgVersionIds: eData.orgVersionIds || [eData.currentOrgVersionId],
      bizOrgId: eData.bizOrgId,
      ...(eData.params || {})
    },
    checkableTypes: [...checkableTypes.values()],
    locale: callbackContext && callbackContext.$widget && callbackContext.$widget.locale,
    footerDescriptionComponent,
    callback: function () {
      isCancel = false;
      okCallback.apply(this, arguments);
    },
    close: function () {
      cancelCallback && cancelCallback();
      if (isCancel) {
        workFlow.clearTempData();
      }
    }
  });
}

//解析选中的树节点，解析出职位路径数组
function gettaskUserJobPaths(treeNodes) {
  return treeNodes.map(node => node.keyPath);
  // var taskUserJobPaths = [];
  // forEach(treeNodes, function (treeNode) {
  //   var jobPaths = '';
  //   if (!treeNode.extValues) {
  //     return;
  //   }
  //   var VersionId = treeNode.extValues.VersionId;
  //   jobPaths = VersionId;
  //   var paths = '';
  //   if (treeNode.idPath != undefined) {
  //     paths = treeNode.idPath.split('/');
  //     forEach(paths, function (path) {
  //       if (!path.startsWith('U')) {
  //         jobPaths += '/' + path.id;
  //       }
  //     });
  //   } else if (treeNode.allPath != undefined) {
  //     paths = treeNode.getPath();
  //     forEach(paths, function (path) {
  //       if (!path.id.startsWith('U')) {
  //         jobPaths += '/' + path.id;
  //       }
  //     });
  //   }
  //   if (isEmpty(paths)) {
  //     return taskUserJobPaths;
  //   }

  //   taskUserJobPaths.push(jobPaths);
  // });
  // return taskUserJobPaths;
}

function convertUnitTreeData(users, eleOrderMap) {
  var treeNodes = [];
  var showOrgVersion = isShowOrgVersion(users);
  // var showUnit = {};
  forEach(users, function (user) {
    var node_id = user.id;
    if (node_id.startsWith('J')) {
      createJobTreeNodeIfRequired(node_id, user.name, null, null, treeNodes);
    } else {
      if (user.showJobIdPath && user.showJobNamePath) {
        if (user.showJobIdPath.indexOf(';') > 0) {
          //同时命中多个情况
          var more_ids = user.showJobIdPath.split(';');
          var more_names = user.showJobNamePath.split(';');
          var more_jobNames = user.showJobName.split(';');
          for (let i = 0; i < more_ids.length; i++) {
            var ids = more_ids[i].split('/');
            var names = more_names[i].split('/');
            var jobName = more_jobNames[i];
            var eleIds = [];
            var parentNode = null;
            for (var j = 0; j < ids.length; j++) {
              var id = ids[j];
              eleIds.push(id);
              if (showOrgVersion && id.startsWith('V')) {
                parentNode = createVersionTreeNodeIfRequired(id, names[j], eleIds.join('/'), parentNode, treeNodes);
              } else if (id.startsWith('O')) {
                parentNode = createOrgTreeNodeIfRequired(id, names[j], eleIds.join('/'), parentNode, treeNodes);
              } else if (id.startsWith('B')) {
                //2021-09-27 不在控制是否有不同的单位节点 直接显示单位节点
                parentNode = createBusinessTreeNodeIfRequired(id, names[j], eleIds.join('/'), parentNode, treeNodes);
              } else if (id.startsWith('J')) {
                parentNode = createJobTreeNodeIfRequired(id, names[j], eleIds.join('/'), parentNode, treeNodes);
              } else if (id.startsWith('D')) {
                parentNode = createDepartmentTreeNodeIfRequired(id, names[j], eleIds.join('/'), parentNode, treeNodes);
              } else if (id.startsWith('G')) {
                parentNode = createGroupTreeNodeIfRequired(id, names[j], eleIds.join('/'), parentNode, treeNodes);
              }
            }
            user.showJobName = jobName;
            createUserTreeNode(user, parentNode, treeNodes);
          }
        } else {
          let ids = user.showJobIdPath.split('/');
          let names = user.showJobNamePath.split('/');
          let eleIds = [];
          let parentNode = null;
          for (let j = 0; j < ids.length; j++) {
            let id = ids[j];
            eleIds.push(id);
            if (showOrgVersion && id.startsWith('V')) {
              parentNode = createVersionTreeNodeIfRequired(id, names[j], eleIds.join('/'), parentNode, treeNodes);
            } else if (id.startsWith('O')) {
              parentNode = createOrgTreeNodeIfRequired(id, names[j], eleIds.join('/'), parentNode, treeNodes);
            } else if (id.startsWith('B')) {
              //2021-09-27 不在控制是否有不同的单位节点 直接显示单位节点
              parentNode = createBusinessTreeNodeIfRequired(id, names[j], eleIds.join('/'), parentNode, treeNodes);
            } else if (id.startsWith('J')) {
              parentNode = createJobTreeNodeIfRequired(id, names[j], eleIds.join('/'), parentNode, treeNodes);
            } else if (id.startsWith('D')) {
              parentNode = createDepartmentTreeNodeIfRequired(id, names[j], eleIds.join('/'), parentNode, treeNodes);
            } else if (id.startsWith('G')) {
              parentNode = createGroupTreeNodeIfRequired(id, names[j], eleIds.join('/'), parentNode, treeNodes);
            }
          }
          createUserTreeNode(user, parentNode, treeNodes);
        }
      } else {
        createUserTreeNode(user, null, treeNodes);
      }
    }
  });
  var isOpen = treeNodes.length > 1 ? false : true;
  sortUnitTreeData(treeNodes, eleOrderMap, isOpen);
  // console.log(treeNodes);
  return treeNodes;
}

// 是否显示组织版本
function isShowOrgVersion(users) {
  var orgVersion = {};
  var orgVersionCount = 0;
  for (var i = 0; i < users.length; i++) {
    var user = users[i];
    if (user.showJobIdPath) {
      var ids = user.showJobIdPath.split('/');
      orgVersion[ids[0]] = ids[0];
    }
  }
  /* eslint-disable */
  for (var key in orgVersion) {
    orgVersionCount++;
  }
  /* eslint-enable */
  return orgVersionCount > 1;
}

// 是否显示单位结点
/* eslint-disable */
function isShowUnit(unitId, eleIds, users, showUnit) {
  if (showUnit[unitId] != null) {
    return showUnit[unitId];
  }
  var fullPath = eleIds.join('/');
  var prifix = fullPath.substring(0, fullPath.length - unitId.length - 1);
  for (var i = 0; i < users.length; i++) {
    var user = users[i];
    var idPath = user.mainJobIdPath;
    if (idPath) {
      if (idPath.startsWith(prifix + '/B') && !idPath.startsWith(prifix + '/' + unitId)) {
        showUnit[unitId] = false;
        break;
      }
    }
  }
  if (showUnit[unitId] == null) {
    showUnit[unitId] = false;
  }
  return showUnit[unitId];
}
/* eslint-enable */

function sortUnitTreeData(treeNodes, eleOrderMap, isOpen) {
  var sortFunc = function (node1, node2) {
    var eleIdPath1 = node1.eleIdPath;
    var eleIdPath2 = node2.eleIdPath;
    var eleOrder1 = eleOrderMap[eleIdPath1];
    var eleOrder2 = eleOrderMap[eleIdPath2];
    if (eleOrder1 == null || eleOrder2 == null) {
      return 0;
    }
    return eleOrder1 - eleOrder2;
  };
  treeNodes.sort(sortFunc);
  for (var i = 0; i < treeNodes.length; i++) {
    var treeNode = treeNodes[i];
    if (treeNode.children) {
      if (isOpen) {
        treeNode.open = isOpen; // 当前层级是否存在多个子级，存在多个只展开到当前层级，不存在同理展开子级
      }
      var newIsOpen = isOpen && treeNode.children.length <= 1 ? true : false;

      sortUnitTreeData(treeNode.children, eleOrderMap, newIsOpen);
    }
  }
}

function createUserTreeNode(user, parentNode, treeNodes) {
  var dataListJobName = [];
  if (user['mainDepartmentName'] != null) {
    dataListJobName.push(user['mainDepartmentName']);
  }
  if (user['showJobName'] != null) {
    if (
      user.showJobNamePath != '' &&
      dataListJobName.length == 1 &&
      user.showJobNamePath.split('/').indexOf(user['mainDepartmentName']) == -1
    ) {
      var dept = user.showJobNamePath.split('/');
      dataListJobName.splice(0, 1, dept[dept.length - 2]);
    }
    dataListJobName.push(user['showJobName']);
  }
  var node = {
    id: user['id'],
    name: user['name'],
    namePy: user['namePy'],
    //mainJobName: user['showJobName'],
    dataListJobName: user['showJobName'] == ' ' ? '' : dataListJobName.length == 1 ? dataListJobName[0] : dataListJobName.join('/'),
    // mainDepartmentName: user['mainDepartmentName'],
    // mainJobNamePath: user['mainJobNamePath'],
    type: 'U',
    iconSkin: 'U'
  };
  // var userNode = getTreeNodeById(node.id, treeNodes);

  if (parentNode != null) {
    var children = parentNode.children || [];
    children.push(node);
    parentNode.children = children;
  } else {
    treeNodes.push(node);
  }
}

function createVersionTreeNodeIfRequired(id, name, eleIdPath, parentNode, treeNodes) {
  var node = getTreeNodeById(id, treeNodes);
  if (node == null) {
    node = {
      id: id,
      name: name,
      type: 'V',
      iconSkin: 'V',
      eleIdPath: eleIdPath
    };
    if (parentNode) {
      var children = parentNode.children || [];
      children.push(node);
      parentNode.children = children;
    } else {
      treeNodes.push(node);
    }
  }
  return node;
}

function createOrgTreeNodeIfRequired(id, name, eleIdPath, parentNode, treeNodes) {
  var node = getTreeNodeById(id, treeNodes);
  if (node == null) {
    node = {
      id: id,
      name: name,
      type: 'O',
      iconSkin: 'O',
      eleIdPath: eleIdPath
    };
    if (parentNode) {
      var children = parentNode.children || [];
      children.push(node);
      parentNode.children = children;
    } else {
      treeNodes.push(node);
    }
  }
  return node;
}

function createBusinessTreeNodeIfRequired(id, name, eleIdPath, parentNode, treeNodes) {
  var node = getTreeNodeById(id, treeNodes);
  if (node == null) {
    node = {
      id: id,
      name: name,
      type: 'B',
      iconSkin: 'B',
      eleIdPath: eleIdPath
    };
    if (parentNode) {
      var children = parentNode.children || [];
      children.push(node);
      parentNode.children = children;
    } else {
      treeNodes.push(node);
    }
  }
  return node;
}

function createJobTreeNodeIfRequired(id, name, eleIdPath, parentNode, treeNodes) {
  var node = getTreeNodeById(id, treeNodes);
  if (node == null) {
    node = {
      id: id,
      name: name,
      type: 'J',
      iconSkin: 'J',
      eleIdPath: eleIdPath
    };
    if (parentNode) {
      var children = parentNode.children || [];
      children.push(node);
      parentNode.children = children;
    } else {
      treeNodes.push(node);
    }
  }
  return node;
}

function createDepartmentTreeNodeIfRequired(id, name, eleIdPath, parentNode, treeNodes) {
  var node = getTreeNodeById(id, treeNodes);
  if (node == null) {
    node = {
      id: id,
      name: name,
      type: 'D',
      iconSkin: 'D',
      eleIdPath: eleIdPath
    };
    if (parentNode) {
      var children = parentNode.children || [];
      children.push(node);
      parentNode.children = children;
    } else {
      treeNodes.push(node);
    }
  }
  return node;
}

function createGroupTreeNodeIfRequired(id, name, eleIdPath, parentNode, treeNodes) {
  var node = getTreeNodeById(id, treeNodes);
  if (node == null) {
    node = {
      id: id,
      name: name,
      type: 'G',
      iconSkin: 'G',
      eleIdPath: eleIdPath
    };
    if (parentNode) {
      var children = parentNode.children || [];
      children.push(node);
      parentNode.children = children;
    } else {
      treeNodes.push(node);
    }
  }
  return node;
}

function getTreeNodeById(id, treeNodes) {
  for (var i = 0; i < treeNodes.length; i++) {
    var treeNode = treeNodes[i];
    if (treeNode.id == id) {
      return treeNode;
    } else if (treeNode.children) {
      var node = getTreeNodeById(id, treeNode.children);
      if (node != null) {
        return node;
      }
    }
  }
  return null;
}

// 6、只能选择一个人办理,
function OnlyChooseOneUser(faultData, options) {
  showUserByUnit(faultData, options, false);
}

// 7、弹出环节选择框选择下一流程环节
function JudgmentBranchFlowNotFound(faultData, options) {
  // var _self = this;
  const eData = faultData.data;
  let callback = options.callback;
  let callbackContext = options.callbackContext;
  let workFlow = options.workFlow;
  let workData = workFlow.getWorkData();
  let title = eData.useDirection
    ? callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseDirection', '请选择流向')
    : callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseTask', '请选择环节');
  let fromTaskId = eData.fromTaskId;
  let useDirection = eData.useDirection;
  let multiselect = eData.multiselect;
  let toTasks = eData.toTasks || [];

  let showActionTipPromise = workFlow.isShowActionTip ? workFlow.isShowActionTip() : Promise.resolve(false);
  showActionTipPromise.then(showActionTip => {
    // 选择下一环节、流向
    let Modal = Vue.extend({
      template: `<a-config-provider :locale="locale"><a-modal :title="title" dialogClass="pt-modal wf-error-hander-modal" :visible="visible" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
      <a-checkbox-group v-if="multiselect" v-model="toTaskId" class="wf-next-select-group">
        <template v-for="(item, index) in toTasks">
          <WorkflowActionPopover :workFlow="workFlow" :action="{ id: 'B004002', code: 'B004002', toTaskId: item.id, remark: item.remark }"
                :showActionTip="showActionTip" placement="right">
            <a-checkbox
              :key="index"
              :title="item.i18Label"
              :value="item.value">
            {{ item.i18Label }}
            </a-checkbox>
          </WorkflowActionPopover>
        </template>
      </a-checkbox-group>
      <a-radio-group v-else v-model="toTaskId" class="wf-next-select-group">
        <template v-for="(item, index) in toTasks">
          <WorkflowActionPopover :workFlow="workFlow" :action="{ id: 'B004002', code: 'B004002', toTaskId: item.id, remark: item.remark }"
            :showActionTip="showActionTip" placement="top">
            <a-radio
              :key="index"
              :title="item.i18Label"
              :value="item.value">
                {{ item.i18Label }}
            </a-radio>
          </WorkflowActionPopover>
        </template>
      </a-radio-group>
    </a-modal></a-config-provider>`,
      provide() {
        return {
          $workView: {
            workView: callbackContext
          }
        };
      },
      components: {
        WorkflowActionPopover: () => import('./workflow-action-popover.vue')
      },
      data: function () {
        toTasks.forEach(item => {
          if (useDirection) {
            item.value = item.directionId;
            item.remark = this.$t('WorkflowView.' + item.directionId + '.remark', item.remark);
            item.i18Label = this.$t('WorkflowView.' + item.directionId + '.directionName', item.name);
          } else {
            item.value = item.id;
            item.i18Label = this.$t('WorkflowView.' + item.id + '.taskName', item.name);
          }
        });
        return {
          locale: callbackContext.$widget.locale,
          title,
          workFlow,
          showActionTip,
          visible: true,
          fromTaskId,
          useDirection, // 是否使用流向
          multiselect, // 是否多选
          toTasks, // 下一环节
          toTaskId: undefined // 选择的下一环节、流向
        };
      },
      methods: {
        handleCancel() {
          options.cancel && options.cancel();
          workFlow.clearTempData();
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          let selectedToTaskId = this.toTaskId;
          if (isEmpty(selectedToTaskId)) {
            showError(callbackContext, title);
            return;
          }
          let taskIds = isArray(selectedToTaskId) ? selectedToTaskId : selectedToTaskId.split(';');
          let checkedValues = taskIds;
          // toTasks.forEach(item => {
          //   let indexInArray = taskIds.findIndex(o => {
          //     return o == item.id;
          //   });
          //   if (indexInArray != -1) {
          //     if (useDirection) {
          //       checkedValues.push(item.directionId);
          //     } else {
          //       checkedValues.push(item.id);
          //     }
          //   }
          // });
          if (checkedValues.length === 0) {
            showError(callbackContext, title);
            return;
          }

          let toTaskId = checkedValues.join(';');
          if (useDirection) {
            workFlow.setTempData('toDirectionId', toTaskId);
            let toDirectionIds = workData.toDirectionIds || {};
            toDirectionIds[fromTaskId] = toTaskId;
            workFlow.setTempData('toDirectionIds', toDirectionIds);
          } else {
            workFlow.setTempData('toTaskId', toTaskId);
            let toTaskIds = workData.toTaskIds || {};
            toTaskIds[fromTaskId] = toTaskId;
            workFlow.setTempData('toTaskIds', toTaskIds);
          }

          // 重新触发回调事件
          if (isFunction(callback)) {
            callback.call(callbackContext);
          }

          this.visible = false;
          this.$destroy();
        }
      }
    });

    let modal = new Modal({
      i18n: callbackContext.$widget.$i18n
    });
    modal.$mount();
  });
}

// 8、找到多个判断分支流向
function MultiJudgmentBranch(faultData, options) {
  JudgmentBranchFlowNotFound(faultData, options);
}

// 9、弹出环节选择框选择下一子流程
function SubFlowNotFound(faultData, options) {
  console.log(faultData);
  showInfo(
    options.callbackContext,
    options.callbackContext.$widget.$t('WorkflowWork.message.noSubWorkflowSetTip', '流程没有配置要发起的子流程!')
  );
}

// 11、用户没有权限访问流程
function IdentityNotFlowPermission(faultData, options) {
  var eData = faultData.data;
  if (eData && eData.msg) {
    const userName = eData.msg.match(/\[(.+?)\]/g);
    eData.msg = options.callbackContext.$widget.$t('WorkflowWork.message.notFlowPermission', { userName }, eData.msg);
  }
  // userNotFlowPermission
  showError(options.callbackContext, eData.msg);
}

// 12、找不到退回操作的退回环节异常类
function RollbackTaskNotFound(faultData, options) {
  // var _self = this;
  const eData = faultData.data;
  let callback = options.callback;
  let callbackContext = options.callbackContext;
  let workFlow = options.workFlow;
  // var workData = workFlow.getWorkData();
  let title = callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseRollbackTask', '选择退回环节');
  let toTasks = eData.rollbackTasks;

  setTaskI18s(toTasks, callbackContext.$widget);
  let showActionTipPromise = workFlow.isShowActionTip ? workFlow.isShowActionTip() : Promise.resolve(false);
  showActionTipPromise.then(showActionTip => {
    // 选择退回环节
    let Modal = Vue.extend({
      template: `<a-config-provider :locale="locale"><a-modal :title="title" dialogClass="pt-modal wf-error-hander-modal" :visible="visible" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
    <a-radio-group v-model="toTaskId" class="wf-next-select-group">
      <template v-for="(item, index) in toTasks">
          <WorkflowActionPopover :workFlow="workFlow" :action="{ id: 'B004003', code: 'B004003', toTaskId: item.id, remark: item.remark }"
            :showActionTip="showActionTip" :rollbackTaskInstUuid="item.taskInstUuid" placement="right">
            <a-radio
              :key="index"
              :value="item.id">
              {{ item.i18Name }}
            </a-radio>
          </WorkflowActionPopover>
      </template>
    </a-radio-group>
  </a-modal></a-config-provider>`,
      components: {
        WorkflowActionPopover: () => import('./workflow-action-popover.vue')
      },
      data: function () {
        return {
          locale: callbackContext.$widget.locale,
          title,
          visible: true,
          toTasks, // 退回环节
          toTaskId: undefined, // 选择的退回环节
          workFlow,
          showActionTip
        };
      },
      methods: {
        handleCancel() {
          options.cancel && options.cancel();
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          let toTaskId = this.toTaskId;
          if (isEmpty(toTaskId)) {
            showError(callbackContext, this.$t('WorkflowWork.message.pleaseChooseTask', '请选择环节!'));
            return;
          }
          let checkedItems = [];
          forEach(toTasks, function (item) {
            if (item.id === toTaskId) {
              checkedItems.push(item);
            }
          });
          if (checkedItems.length !== 1) {
            showError(callbackContext, this.$t('WorkflowWork.message.pleaseChooseTask', '请选择环节!'));
            return;
          }

          let item = checkedItems[0];
          let rollbackToTaskId = item.id;
          let rollbackToTaskInstUuid = item.taskInstUuid;
          workFlow.setTempData('rollbackToTaskId', rollbackToTaskId);
          workFlow.setTempData('rollbackToTaskInstUuid', rollbackToTaskInstUuid);

          // 重新触发回调事件
          if (isFunction(callback)) {
            callback.call(callbackContext);
          }

          this.visible = false;
          this.$destroy();
        }
      }
    });
    let modal = new Modal({
      i18n: callbackContext.$widget.$i18n
    });
    modal.$mount();
  });
}
function setTaskI18s(tasks, $widget) {
  tasks.map(task => {
    if (task.id === '<StartFlow>') {
      task.i18Name = $widget.$t(`WorkflowWork.startTaskName`, task.name);
    } else if (task.id === '<EndFlow>') {
      task.i18Name = $widget.$t(`WorkflowWork.endTaskName`, task.name);
    } else {
      task.i18Name = $widget.$t(`WorkflowView.${task.id}.taskName`, task.name);
    }
  });
  return tasks;
}

// 13、找不到特送环节操作的环节异常类
function GotoTaskNotFound(faultData, options) {
  // var _self = this;
  const eData = faultData.data;
  let callback = options.callback;
  let callbackContext = options.callbackContext;
  let workFlow = options.workFlow;
  // var workData = workFlow.getWorkData();
  let title = callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseGotoTask', '选择特送环节');
  let fromTaskId = eData.fromTaskId;
  let toTasks = eData.toTasks;
  setTaskI18s(toTasks, callbackContext.$widget);
  // 选择特送环节
  let Modal = Vue.extend({
    template: `<a-config-provider :locale="locale"><a-modal dialogClass="pt-modal wf-error-hander-modal" :title="title" :visible="visible" :maskClosable="false"  @ok="handleOk" @cancel="handleCancel">
        <a-radio-group v-model="toTaskId" class="wf-next-select-group">
          <a-radio v-for="(item, index) in toTasks"
            :key="index" :value="item.id">
            {{ item.i18Name }}
          </a-radio>
        </a-radio-group>
      </a-modal></a-config-provider>`,
    data: function () {
      return {
        locale: callbackContext.$widget.locale,
        title,
        visible: true,
        fromTaskId,
        toTasks, // 特送环节
        toTaskId: undefined // 选择的特送环节
      };
    },
    methods: {
      handleCancel() {
        options.cancel && options.cancel();
        this.visible = false;
        this.$destroy();
      },
      handleOk() {
        let toTaskId = this.toTaskId;
        if (isEmpty(toTaskId)) {
          showError(callbackContext, this.$t('WorkflowWork.message.pleaseChooseGotoTask', '选择特送环节!'));
          return;
        }

        workFlow.setTempData('fromTaskId', fromTaskId);
        workFlow.setTempData('gotoTaskId', toTaskId);

        // 重新触发回调事件
        if (isFunction(callback)) {
          callback.call(callbackContext);
        }

        this.visible = false;
        this.$destroy();
      }
    }
  });
  let modal = new Modal({
    i18n: callbackContext.$widget.$i18n
  });
  modal.$mount();
}

// 14、表单数据保存失败
function SaveData(faultData, options) {
  console.log(faultData);
  let workView = options.callbackContext;
  if (workView && workView.getDyformWidget && isFunction(workView.getDyformWidget)) {
    let dyform = workView.getDyformWidget();
    assignIn(options, {
      dyformDataOptions: function () {
        return dyform.dyformDataOptions(dyform);
      }
    });
    if (typeof faultData.data == 'string' && faultData.data.startsWith('{')) {
      faultData.data = JSON.parse(faultData.data);
    }
    faultData.data.title = getMsgI18ns(faultData.data.title, workView);
    dyform.handleException(faultData.data, options);
  } else {
    showError(options.callbackContext, options.callbackContext.$widget.$t('WorkflowWork.message.saveFormDataError', '表单数据保存失败!'));
  }
}

// 15、必填域为空
function RequiredFieldIsBlank(faultData, options) {
  console.log(faultData);
  showError(options.callbackContext, options.callbackContext.$widget.$t('WorkflowWork.message.formDataRequiredMiss', '表单必填域为空!'));
}

// 16、选择归档夹
function ChooseArchiveFolder(faultData, options) {
  let callback = options.callback;
  let callbackContext = options.callbackContext;
  let workFlow = options.workFlow;
  // let workData = workFlow.getWorkData();
  let rootFolderUuids = faultData.data.rootFolderUuids;
  let title = options.callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseArchiveFolder', '选择归档夹');
  // 选择职位发起流程
  let Modal = Vue.extend({
    template: `<a-config-provider :locale="locale">
      <a-modal :title="title" dialogClass="pt-modal wf-error-hander-folder-tree-modal" :visible="visible" width="550px" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
        <FolderTreeSelect ref="folderTree" mode="multiple" :folderUuid="folderUuid" :showSearch="false"></FolderTreeSelect>
      </a-modal>
    </a-config-provider>`,
    components: { FolderTreeSelect: () => import('@pageAssembly/app/web/widget/widget-file-manager/folder-tree-select.vue') },
    data: function () {
      return {
        locale: callbackContext.$widget.locale,
        title,
        visible: true,
        locale: callbackContext && callbackContext.$widget && callbackContext.$widget.locale,
        folderUuid: rootFolderUuids.join(';')
      };
    },
    methods: {
      handleCancel() {
        options.cancel && options.cancel();
        this.visible = false;
        this.$destroy();
      },
      handleOk() {
        this.$refs.folderTree.collect().then(folders => {
          let archiveFolderUuids = folders.map(folder => folder.uuid);
          workFlow.setTempData('archiveFolderUuid', archiveFolderUuids.join(';'));

          // 重新触发回调事件
          if (isFunction(callback)) {
            callback.call(callbackContext);
          }

          this.visible = false;
          this.$destroy();
        });
      }
    }
  });
  let modal = new Modal({
    i18n: callbackContext.$widget.$i18n
  });
  modal.$mount();
}

// 一人多职未选择职位的情况下
function MultiJobNotSelected(faultData, options) {
  let workFlow = options.workFlow;
  let callback = options.callback;
  let callbackContext = options.callbackContext;
  let title = callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseJobToStartFlow', '请选择身份发起流程');
  let eData = faultData.data;
  if (eData.taskInstUuid) {
    let workData = workFlow.getWorkData();
    if (workData.actionType == 'Transfer') {
      title = callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseJobToTransferFlow', '请选择身份转办流程');
    } else if (workData.actionType == 'CounterSign') {
      title = callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseJobToCounterSignFlow', '请选择身份会签流程');
    } else if (workData.actionType == 'AddSign') {
      title = callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseJobToAddSignFlow', '请选择身份加签流程');
    } else if (workData.actionType == 'Rollback' || workData.actionType == 'DirectRollback') {
      title = callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseJobToRollbackFlow', '请选择身份退回流程');
    } else {
      title = callbackContext.$widget.$t('WorkflowWork.modal.pleaseChooseJobToSubmitFlow', '请选择身份提交流程');
    }
  }
  let multiselect = eData.multiselect;
  let jobs = []; // [{ id: eData.jobs.map(job => job.jobId).join(';'), text: '全部身份', title: '全部身份' }];
  eData.jobs.forEach(job => {
    let text = '';
    if (job.jobNamePath) {
      let paths = job.jobNamePath.split('/');
      if (paths.length > 1) {
        text = paths[paths.length - 2] + ' - ';
      }
    }
    // if (job.parent && job.parent.id) {
    //   if (job.parent.id.indexOf('D') == 0) {
    //     text += job.parent.name + ' - ';
    //   }
    // }
    text += job.jobName;
    job.text = text;
    job.id = job.jobId;
    job.title = job.jobNamePath;
    jobs.push(job);
    // 职位路径显示组织名称
    if (job.orgVersionUuid) {
      $axios.get(`/proxy/api/org/organization/version/getOrgNameByVersionUuid?orgVersionUuid=${job.orgVersionUuid}`).then(({ data }) => {
        if (data.code == 0) {
          job.title = data.data + '/' + job.title;
        }
      });
    }
  });
  let allJobOptionLabel = callbackContext.$widget.$t('WorkflowWork.modal.allOrgJobOptionLabel', '全部身份');
  // 选择职位发起流程
  let Modal = Vue.extend({
    template: `<a-config-provider :locale="locale"><a-modal dialogClass="pt-modal wf-error-hander-modal" :title="title" :visible="visible" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
    <a-checkbox-group v-if="multiselect" v-model="jobId" class="wf-next-select-group">
      <a-button style="width: 100%; text-align: left; margin-bottom: 12px;" @click="selectAllBtnClick">{{allJobOptionLabel}}</a-button>
      <a-checkbox
        v-for="(item, index) in jobs"
        :key="index"
        :value="item.id"
        :title="item.title"
        @mouseenter="onMouseEnterCheckbox($event, item)">
        {{ item.text || item.name }}
      </a-checkbox>
    </a-checkbox-group>
    <a-radio-group v-else v-model="jobId" class="wf-next-select-group">
      <a-radio
        v-for="(item, index) in jobs"
        :key="index"
        :value="item.id"
        :title="item.title">
        {{ item.text || item.name }}
      </a-radio>
    </a-radio-group>
  </a-modal></a-config-provider>`,
    data: function () {
      return {
        locale: callbackContext.$widget.locale,
        allJobOptionLabel,
        title,
        visible: true,
        multiselect, // 是否多选
        selectAll: false,
        jobs, // 选择职位
        jobId: undefined, // 选择的职位
        bodyStyle: {
          maxHeight: '350px',
          overflow: 'auto'
        }
      };
    },
    methods: {
      onMouseEnterCheckbox(e, item) {
        let currentTarget = e.currentTarget;
        let title = currentTarget.getAttribute('title');
        if (isEmpty(title) && !isEmpty(item.title)) {
          currentTarget.setAttribute('title', item.title);
        }
      },
      selectAllBtnClick() {
        if (!this.selectAll) {
          this.jobId = this.jobs.map(job => job.jobId);
          this.selectAll = true;
        } else {
          this.jobId = [];
          this.selectAll = false;
        }
      },
      handleCancel() {
        options.cancel && options.cancel();
        this.visible = false;
        this.$destroy();
      },
      handleOk() {
        let selectedJobId = this.jobId;
        if (isEmpty(selectedJobId)) {
          showError(callbackContext, title + '！');
          return;
        }

        if (Array.isArray(selectedJobId)) {
          workFlow.setTempData('jobSelected', selectedJobId.join(';'));
        } else {
          workFlow.setTempData('jobSelected', selectedJobId);
        }

        // 重新触发回调事件
        if (isFunction(callback)) {
          callback.call(callbackContext);
        }

        this.visible = false;
        this.$destroy();
      }
    }
  });
  let modal = new Modal({
    i18n: callbackContext.$widget.$i18n
  });
  modal.$mount();
}

// 环节数据变更
function TaskDataChanged(faultData, options) {
  let callbackContext = options.callbackContext;
  let mergeOptions = Object.assign({}, faultData.data);
  if (options.callback) {
    let workFlow = options.workFlow;
    let workData = workFlow.getWorkData();
    let confirmLabel = callbackContext.$widget.$t('WorkflowWork.opinionManager.operation.ok', '确定') + workData.action;
    mergeOptions.buttons = {
      confirm: {
        label: confirmLabel,
        className: 'btn-primary',
        callback: options.callback,
        callbackScope: callbackContext
      }
    }
  }
  callbackContext.showMergeChangedFormDataModal(mergeOptions);
}

// 18、表单数据验证失败
function FormDataValidateException(faultData, options) {
  if (typeof faultData.data == 'string') {
    showError(options.callbackContext, faultData.data);
  } else if (typeof faultData.data == 'object') {
    var msg = '';
    msg += faultData.data.msg;
    if (faultData.data.errors) {
      var errorStrings = [];
      forEach(faultData.data.errors, function (error) {
        errorStrings.push(error.displayName + '(' + error.fieldName + ')' + error.msg);
      });
      msg += ': ' + errorStrings.join('、');
    }
    showError(options.callbackContext, msg.toString());
  } else {
    Default(faultData, options);
  }
}

// 19、流程无法处理的异常
function Default(faultData, options) {
  var msg = null;
  if (!isEmpty(faultData.msg)) {
    msg = faultData.msg;
  } else {
    msg = JSON.stringify(faultData);
  }

  showError(
    options.callbackContext,
    options.callbackContext.$widget.$t('WorkflowWork.message.unknownWorkflowException', '工作流无法处理的未知异常') + ': ' + msg
  );
}

function showInfo(callbackContext, content, callback) {
  if (callbackContext && callbackContext.showInfo) {
    callbackContext.showInfo({ content, callback });
  } else {
    window.$app.$message.info(content, 3, callback);
  }
}

function showError(callbackContext, content, callback) {
  content = getMsgI18ns(content, callbackContext);
  if (callbackContext && callbackContext.showError) {
    callbackContext.showError({ content, callback });
  } else {
    window.$app.$message.error(content, 3, callback);
  }
}

// 流程错误代码
var WorkFlowErrorCode = {
  WorkFlowException: WorkFlowException, // 1、工作流异常
  TaskNotAssignedUser: TaskNotAssignedUser, // 2、任务没有指定参与者
  TaskNotAssignedCopyUser: TaskNotAssignedCopyUser, // 3、任务没有指定抄送人
  TaskNotAssignedMonitor: TaskNotAssignedMonitor, // 4、任务没有指定督办人
  ChooseSpecificUser: ChooseSpecificUser, // 5、选择具体办理人
  OnlyChooseOneUser: OnlyChooseOneUser, // 6、只能选择一个办理人
  JudgmentBranchFlowNotFound: JudgmentBranchFlowNotFound, // 7、无法找到可用的判断分支流向
  MultiJudgmentBranch: MultiJudgmentBranch, // 8、找到多个判断分支流向
  SubFlowNotFound: SubFlowNotFound, // 9、没有指定子流程
  IdentityNotFlowPermission: IdentityNotFlowPermission, // 11、用户没有权限访问流程
  RollbackTaskNotFound: RollbackTaskNotFound, // 12、找不到退回操作的退回环节异常类
  GotoTaskNotFound: GotoTaskNotFound, // 13、找不到特送环节操作的环节异常类
  SaveData: SaveData, // 14、表单数据保存失败
  RequiredFieldIsBlank: RequiredFieldIsBlank, // 15、必填域为空
  ChooseArchiveFolder: ChooseArchiveFolder, // 16、选择归档夹
  MultiJobNotSelected: MultiJobNotSelected, // 17、一人多职时候未选择职位
  FormDataValidateException: FormDataValidateException, // 18、表单数据验证失败
  TaskNotAssignedTransferUser: TaskNotAssignedTransferUser, // 19、任务没有指定转办人
  TaskNotAssignedCounterSignUser: TaskNotAssignedCounterSignUser, // 20、任务没有指定会签人
  TaskNotAssignedAddSignUser: TaskNotAssignedAddSignUser, // 21、任务没有指定会签人
  TaskNotAssignedDecisionMaker: TaskNotAssignedDecisionMaker, // 22、任务没有指定决定人
  TaskDataChanged: TaskDataChanged // 23、环节数据变更
};
// 异常处理
var WorkFlowErrorHandler = function (workView) {
  this.workView = workView;
  this.errorHandler = ErrorHandler.getInstance();
  this._init();
};
// 内部初始化默认的异常处理代码
WorkFlowErrorHandler.prototype._init = function () {
  var _self = this;
  forEach(WorkFlowErrorCode, function (value, prop) {
    _self.errorHandler.register(prop, value);
  });
  _self.errorHandler.registerDefault(Default);
};
// 注册异常代码处理函数
WorkFlowErrorHandler.prototype.register = function (errorCode, callback) {
  var _self = this;
  _self.errorHandler.register(errorCode, callback);
};
// 异常处理
WorkFlowErrorHandler.prototype.handle = function (jqXHR, statusText, error, options) {
  this.errorHandler.handle(jqXHR, statusText, error, options);
};
// 打开组织选择框
WorkFlowErrorHandler.openOrgSelect = openOrgSelect;
export default WorkFlowErrorHandler;
