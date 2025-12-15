import { generateId } from '@framework/vue/utils/util';
import { isEmpty, forEach, findIndex, trim as stringTrim } from 'lodash';
import { addDbHeader } from '@framework/vue/utils/function';
import { getMsgI18ns } from './WorkFlowErrorHandler.js';
const TABLE_COLUMN_INDEX_OPTIONS = [
  { label: '办理环节', value: 'taskName' },
  { label: '办理人', value: 'assignee' },
  { label: '操作', value: 'actionName' },
  { label: '办理意见', value: 'opinion' },
  { label: '意见立场名称', value: 'opinionLabel' },
  { label: '意见立场值', value: 'opinionValue' },
  { label: '操作时间', value: 'endTime' },
  { label: '附件', value: 'opinionFiles' }
];

class WorkProcess {
  constructor(workData, workView) {
    this.workData = workData;
    this.workView = workView;
    this.emptyData = { opinionPositionConfig: {}, workProcesses: [], items: [] };
    this.promiseMap = {};
  }

  getWorkProcess(displayStyle = 'standard') {
    const _this = this;
    if (isEmpty(_this.workData) || isEmpty(_this.workData.flowInstUuid)) {
      return Promise.resolve();
    }

    let flowInstUuid = _this.workData.flowInstUuid;
    let promiseKey = flowInstUuid + '_' + displayStyle;
    if (_this.promiseMap[promiseKey]) {
      return _this.promiseMap[promiseKey];
    }
    let config = addDbHeader({});
    let workProcessPromise = new Promise((resolve, reject) => {
      $axios
        .get('/api/workflow/work/getWorkProcessAndOpinionPositionConfigs?flowInstUuid=' + flowInstUuid, config)
        .then(({ data: result }) => {
          let data = result.data;
          _this.data = data || _this.emptyData;
          if (data) {
            let opinionPositionConfig = data.opinionPositionConfig;
            let workProcesses = data.workProcesses;
            _this.translateWorkProcessSysOptionText(workProcesses).then(() => {
              let items = _this.splitData(workProcesses, displayStyle);
              _this.statisticsOpinionPosition(items, opinionPositionConfig);
              data.items = items;
              resolve(_this.data);
            });
          } else {
            resolve(_this.data);
          }
        });
    });

    _this.promiseMap[promiseKey] = workProcessPromise;
    return _this.promiseMap[promiseKey];
  }

  translateWorkProcessSysOptionText(workProcesses) {
    let _this = this;
    return new Promise((resolve, reject) => {
      if (workProcesses.length && _this.workView.$widget.$i18n.locale != 'zh_CN') {
        let labels = [],
          process = [];
        for (let p of workProcesses) {
          if (p.opinion && /^计时系统\[.+\]逾期处理/.test(p.opinion)) {
            labels.push(p.opinion);
            process.push(p);
          }
        }
        if (labels.length > 0) {
          _this.workView.$widget.$translate(labels, 'zh', _this.workView.$widget.$i18n.locale.split('_')[0]).then(map => {
            for (let p of process) {
              if (map[p.opinion]) {
                p.opinion = map[p.opinion];
              }
            }
            resolve();
          });
        }
      } else {
        resolve();
      }
    });
  }

  getWorkProcessByDataUuid(dataUuid, displayStyle = 'standard') {
    const _this = this;
    if (isEmpty(dataUuid)) {
      return Promise.resolve(_this.emptyData);
    }

    let promiseKey = dataUuid + '_' + displayStyle;
    if (_this.promiseMap[promiseKey]) {
      return _this.promiseMap[promiseKey];
    }

    let config = addDbHeader({});
    let workProcessPromise = $axios
      .get('/proxy/api/workflow/work/getWorkProcessAndOpinionPositionConfigsByFormDataUuid?dataUuid=' + dataUuid, config)
      .then(({ data: result }) => {
        let data = result.data;
        if (data) {
          let opinionPositionConfig = data.opinionPositionConfig;
          let workProcesses = data.workProcesses;
          if (workProcesses && workProcesses.length) {
            let rowData = workProcesses[workProcesses.length - 1];
            if (rowData.endTime) {
              rowData.markCompletedFlag = true;
            }
          }
          let items = _this.splitData(workProcesses, displayStyle);
          _this.statisticsOpinionPosition(items, opinionPositionConfig);
          data.items = items;
        }
        _this.data = data || _this.emptyData;
        return data;
      });

    _this.promiseMap[promiseKey] = workProcessPromise;
    return _this.promiseMap[promiseKey];
  }

  splitData(dataList, displayStyle) {
    if (isEmpty(dataList)) {
      return [];
    }

    let groups = [];
    let groupMap = {};
    let toUsersMap = {};
    let pretaskInstUuid = null;
    let canceledGroup = null;
    dataList.forEach((item, index) => {
      if (!item.id) {
        item.id = generateId();
      }
      let taskInstUuid = item.taskInstUuid;
      let group = groupMap[taskInstUuid];
      if (!group) {
        toUsersMap[taskInstUuid] = item.toUser ? item.toUser.split(';') : [];
        if (
          canceledGroup &&
          canceledGroup.assigneeId == item.assigneeId &&
          (item.actionCode == 6 || (item.actionCode == 1 && canceledGroup.taskId == item.taskId))
        ) {
          canceledGroup.handleDetail.push(item);
        } else {
          group = {
            id: generateId(),
            taskName: item.taskName,
            arriveTime: item.submitTime,
            taskStatus: item.status,
            handleDetail: [item],
            taskId: item.taskId,
            taskInstUuid,
            assigneeId: item.assigneeId
          };
          groupMap[taskInstUuid] = group;
          groups.push(group);

          if (item.canceled && displayStyle == 'standard') {
            canceledGroup = group;
          } else {
            canceledGroup = null;
          }
        }
      } else {
        item.assignee = getMsgI18ns(item.assignee, this.workView, 'WorkflowWork.opinionManager');
        group.handleDetail.push(item);
      }

      // 是否补审补办
      if (item.supplemented) {
        group.supplemented = item.supplemented;
      }

      pretaskInstUuid = groups.length > 1 ? groups[groups.length - 2].taskInstUuid : null;
      // 移交的办理人计算
      if (pretaskInstUuid && pretaskInstUuid != taskInstUuid) {
        let toUsers = toUsersMap[pretaskInstUuid];
        if (item.actionType == 'HandOver') {
          item.formerHandler = toUsers.join(';');
          toUsers = item.toUser ? item.toUser.split(';') : [];
          toUsersMap[pretaskInstUuid] = toUsers;
        } else if (!['CopyTo', 'Remind'].includes(item.actionType) && item.status != '未完成') {
          let userIndex = toUsers.indexOf(item.assignee);
          if (userIndex != -1) {
            toUsers.splice(userIndex, 1);
          }

          // 提交到下一环节
          if (item.actionType == 'Submit' && item.toUser && dataList[index + 1] && dataList[index + 1].taskInstUuid != item.taskInstUuid) {
            let toUsers = item.toUser.split(';');
            toUsers.forEach(toUser => {
              if (!toUsersMap[item.taskInstUuid].includes(toUser)) {
                toUsersMap[item.taskInstUuid].push(toUser);
              }
            });
          } else if (['Transfer', 'CounterSign', 'AddSign'].includes(item.actionType)) {
            let appendUsers = item.toUser ? item.toUser.split(';') : [];
            appendUsers.forEach(appendUser => {
              if (!toUsers.includes(appendUser)) {
                toUsers.push(appendUser);
              }
            });
          } else if (item.actionType == 'Cancel') {
            if (item.assignee && !toUsers.includes(item.assignee)) {
              toUsers.push(item.assignee);
            }
          } else if (item.actionType == 'Delegation') {
            if (item.toUser && !toUsers.includes(item.toUser)) {
              toUsers.push(item.toUser);
            }
          } else if (item.actionType == 'GotoTask') {
            toUsers = toUsersMap[taskInstUuid] || [];
            if (item.toUser) {
              toUsers.length = 0;
              toUsers.push(item.toUser);
            }
            toUsersMap[taskInstUuid] = toUsers;
          }
        }
      }

      item.expandFiles = 'fileList';
      // 跳转环节名称
      if (item.gotoTaskId) {
        if (item.gotoTaskId == '<EndFlow>') {
          item.gotoTaskName = '结束';
        } else {
          let operation = dataList.find(operation => operation.taskId == item.gotoTaskId);
          if (operation) {
            item.gotoTaskName = operation.taskName;
          }
        }
      }

      // 办理人身份名称
      item.jobNames = this.extractJobNames(item);
      item.opinion = getMsgI18ns(item.opinion, this.workView, 'WorkflowWork.opinionManager');
      if (item.opinionValue) {
        item.opinionLabel = this.workView.$widget.$t(
          'WorkflowView.' + item.taskId + '.opinionPosition.' + item.opinionValue,
          item.opinionLabel
        );
      }
    });

    // var newData = new Array();
    // var first = dataList[0];
    // first.formerHandler = first.toUser;
    // newData.push({
    //   taskName: first.taskName,
    //   arriveTime: first.submitTime,
    //   taskStatus: first.status,
    //   handleDetail: [first],
    //   taskId: first.taskId
    // });
    // var formerHandler = first.toUser;

    // var index = 0;
    // for (let j = 1; j < dataList.length; j++) {
    //   let item = dataList[j];
    //   // 跳转环节名称
    //   if (item.gotoTaskId) {
    //     let operation = dataList.find(operation => operation.taskId == item.gotoTaskId);
    //     if (operation) {
    //       item.gotoTaskName = operation.taskName;
    //     }
    //   }

    //   var handleDetail = newData[index].handleDetail;
    //   if (item.actionType == 'Cancel') {
    //     var allDelegation = true;
    //     handleDetail.forEach(hItem => {
    //       if (hItem.actionType != 'Delegation') {
    //         allDelegation = false;
    //         return;
    //       }
    //     });
    //     if (allDelegation) {
    //       newData.pop();
    //       index -= 1;
    //     }
    //     item.toUser = item.assignee;
    //   }

    //   handleDetail.forEach(hItem => {
    //     hItem.expandFiles = 'fileList';
    //     if (hItem.toUser) {
    //       formerHandler = hItem.toUser.split(';');
    //     } else {
    //       var uIndex = formerHandler.indexOf(hItem.assignee);
    //       if (uIndex > -1 && hItem.actionType != 'CopyTo') {
    //         formerHandler.splice(uIndex, 1);
    //       }
    //     }
    //   });
    //   item.formerHandler = formerHandler == '' ? formerHandler : formerHandler.join(';');

    //   if (item.taskName == newData[index].taskName || item.actionType == 'Cancel') {
    //     newData[index].taskStatus = item.status;
    //     newData[index].handleDetail.push(item);
    //   } else {
    //     index++;
    //     newData.push({
    //       taskName: item.taskName,
    //       arriveTime: item.submitTime,
    //       taskStatus: item.status,
    //       handleDetail: [item],
    //       taskId: item.taskId
    //     });
    //   }
    // }
    // return newData;

    // 是否跳过环节
    groups.forEach(group => {
      if (group.handleDetail && group.handleDetail.length) {
        let noSkipSubmitItem = group.handleDetail.find(item => item.actionCode != 36);
        group.skipSubmit = noSkipSubmitItem == null;
      }
    });

    return groups;
  }

  extractJobNames(detail) {
    let identityNamePath = stringTrim(detail.identityNamePath);
    if (isEmpty(identityNamePath)) {
      return [];
    }
    let jobNamePaths = identityNamePath.split(';');
    let jobNames = [];
    jobNamePaths.forEach(jobNamePath => {
      let paths = jobNamePath.split('/');
      if (paths.length > 1) {
        jobNames.push(paths[paths.length - 2] + ' - ' + paths[paths.length - 1]);
      } else {
        jobNames.push(jobNamePath);
      }
    });
    return jobNames;
  }

  statisticsOpinionPosition(items, opinionPositionConfig) {
    const _this = this;
    if (isEmpty(opinionPositionConfig)) {
      return;
    }
    let filterCancel = details => {
      let retDetails = [];
      let cancelAssigneeIdMap = {};
      for (let index = details.length - 1; index >= 0; index--) {
        let item = details[index];
        if (cancelAssigneeIdMap[item.assigneeId]) {
          delete cancelAssigneeIdMap[item.assigneeId];
          continue;
        }

        if (item.actionType == 'Cancel' || item.canceled) {
          cancelAssigneeIdMap[item.assigneeId] = item.assigneeId;
          continue;
        }

        retDetails.push(details[index]);
      }
      return retDetails;
    };
    forEach(items, function (item) {
      let config = _this.getOpinionPositionConfigByTaskId(item.taskId, opinionPositionConfig);
      item.showUserOpinionPosition = (config && config.showUserOpinionPosition) || false;
      if (item.handleDetail) {
        item.handleDetail.forEach(detail => {
          if (detail.opinionLabel) {
            detail.showUserOpinionPosition = item.showUserOpinionPosition;
          }
        });
      }

      if (config == null || !config.showOpinionPositionStatistics || isEmpty(item.handleDetail)) {
        return;
      }

      let positionStatistics = [];
      let details = filterCancel(item.handleDetail);
      for (let index = 0; index < details.length; index++) {
        let detail = details[index];
        if (isEmpty(detail.opinionLabel)) {
          continue;
        }
        let statisticIndex = findIndex(positionStatistics, function (o) {
          return o.value == detail.opinionValue;
        });
        if (statisticIndex == -1) {
          positionStatistics.push({
            label: detail.opinionLabel,
            value: detail.opinionValue,
            count: 1
          });
        } else {
          positionStatistics[statisticIndex].count++;
        }
      }
      if (!isEmpty(positionStatistics)) {
        item.positionStatistics = positionStatistics;
      }
    });
  }

  getOpinionPositionConfigByTaskId(taskId, opinionPositionConfig) {
    return opinionPositionConfig.find(config => config.taskId == taskId);
  }

  getWidgetConfiguration() {
    let opinionFilesRender = {
      options: {
        excludeFileTypes: [],
        fileButton: {
          enable: true,
          buttons: [
            {
              id: 'download',
              code: 'download',
              unDeleted: true,
              title: '下载',
              style: {
                type: 'link',
                icon: 'download'
              }
            },
            {
              id: 'previewFile',
              code: 'previewFile',
              unDeleted: true,
              title: '预览',
              style: {
                type: 'link',
                icon: 'search'
              }
            }
          ],
          buttonGroup: { groups: [], style: {} }
        },
        fileUuidField: 'opinionFiles'
      },
      type: 'CellDataFileRender'
    };
    let getColumns = () => {
      let columns = [];
      TABLE_COLUMN_INDEX_OPTIONS.forEach(item => {
        if (item.value == 'opinionLabel' || item.value == 'opinionValue') {
          return;
        }
        let column = {
          id: generateId(),
          title: item.label,
          dataIndex: item.value,
          primaryKey: false,
          hidden: false,
          sortable: false,
          showTip: false,
          titleAlign: 'left',
          contentAlign: 'left',
          renderFunction: item.value == 'opinionFiles' ? opinionFilesRender : { options: {} },
          clickEvent: { enable: false, eventHandler: {} },
          exportFunction: { options: {} }
        };
        // 办理环节
        if (column.dataIndex == 'taskName') {
          column.renderFunction = {
            type: 'vueTemplateDataRender',
            options: {
              i18n: {},
              templateFrom: 'codeEditor',
              template: `<span v-html="row.matchTaskName || row.taskName"></span> <a-tag v-if="!row.endTime" color="blue">{{$t('WorkflowWork.currentTask','当前环节')}}</a-tag>
                <a-tag v-else-if="row.markCompletedFlag" color="blue">{{$t('WorkflowWork.completed','已办结')}}</a-tag>`
            }
          };
        }
        // 办理人
        if (column.dataIndex == 'assignee') {
          column.renderFunction = {
            type: 'vueTemplateDataRender',
            options: {
              i18n: {},
              templateFrom: 'codeEditor',
              template: `<span v-html="row.assignee"></span> <a-tag v-if="row.endTime && _$USER && _$USER.userId == row.assigneeId" color="var(--w-primary-color)" class="current-user-tag">{{$t('WorkflowWork.opinionManager.Me','我')}}</a-tag>`
            }
          };
        }
        // 操作
        if (column.dataIndex == 'actionName') {
          column.renderFunction = {
            type: 'vueTemplateDataRender',
            options: {
              i18n: {},
              templateFrom: 'codeEditor',
              template: `<span v-html="row.matchActionName || row.actionName"></span>`
            }
          };
        }
        // 办理意见
        if (column.dataIndex == 'opinion') {
          column.renderFunction = {
            type: 'vueTemplateDataRender',
            options: {
              i18n: {},
              templateFrom: 'codeEditor',
              template: `<template v-if="row.actionInfo">
                            <span v-html="row.actionInfo"></span>
                            <template v-if="text">, </template>
                        </template>
                        <span v-html="row.opinion"></span>`
            }
          };
        }
        columns.push(column);
      });
      return columns;
    };
    return {
      wtype: 'WidgetWorkProcess',
      name: '办理过程组件',
      id: generateId(),
      configuration: {
        title: '办理过程',
        code: undefined,
        hideTitle: true,
        titleIcon: undefined,
        isDatabaseField: false,
        displayStyle: 'standard',
        displayDataTypes: ['Handle', 'CopyTo', 'HandOver', 'GotoTask'],
        columns: getColumns(),
        addSerialNumber: true,
        pagination: {
          type: 'default',
          pageSizeOptions: ['10', '20', '50', '100', '200'],
          showTotalPage: true,
          showSizeChanger: true,
          pageSize: 10,
          hideOnSinglePage: false
        },
        search: {
          keywordSearchEnable: true,
          keywordSearchColumns: ['taskName', 'assignee', 'actionName', 'opinion', 'opinionLabel', 'fileName'],
          locateCurrentUserRecord: true,
          locateCurrentTaskRecord: true,
          type: 'keywordAdvanceSearch',
          columnSearchGroupEnable: false,
          advanceSearchEnable: false,
          columnSearchGroup: [],
          columnAdvanceSearchGroup: [],
          advanceSearchPerRowColNumber: 3
        },
        enabledSort: true,
        unlinkDataDisplayState: 'hidden',
        height: 'auto', // 默认高度自动
        style: {
          defaultHeightPX: 400,
          defaultHeightVH: 40,
          margin: [0, 0, 0, 0],
          padding: [10, 10, 10, 10]
        }
      }
    };
  }
}

export default WorkProcess;
