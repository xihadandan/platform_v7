import { handlerFilterList } from '@workflow/app/web/page/workflow-designer/component/designer/constant.js';
import { isEmpty } from 'lodash';

export const isUserFilterOption = function (item) {
  if (item.type == 8) {
    let isFilter = false;
    handlerFilterList.forEach(group => {
      if (isFilter) {
        return;
      }
      group.list.forEach(gItem => {
        if (gItem.value == item.value) {
          isFilter = true;
          return;
        }
      })
    })
    return isFilter;
  } else {
    return false;
  }
}

export const getTaskUserLabel = function (taskConfig) {
  let label = '';
  if (taskConfig.isSetUser == '2') {
    label = '由前一环节办理人指定';
  } else {
    let taskUsers = taskConfig.users || [];
    let configUsers = taskUsers.filter(item => !isUserFilterOption(item));
    let filterUsers = taskUsers.filter(item => isUserFilterOption(item));
    if (configUsers.length) {
      label += `办理人(${configUsers
        .map(user => {
          if (user.argValue && user.argValue.includes('|')) {
            return user.argValue.split('|')[0];
          } else if (user.userOptions && user.userOptions.length) {
            return user.userOptions.map(userOption => userOption.argValue);
          }
          return user.argValue;
        }).filter(displayName => !isEmpty(displayName)).join(';')})`;
    }
    if (filterUsers.length) {
      label += `，办理人过滤条件(${filterUsers.map(user => (user.userOptions && user.userOptions.length) ? user.userOptions.map(userOption => userOption.argValue) : user.argValue)})`;
    }
  }
  return label;
}

export const getTaskDecisionMakerLabel = function (taskConfig) {
  let label = '由前一环节办理人指定(决策人员)';
  let decisionMakers = taskConfig.decisionMakers || [];
  if (decisionMakers && decisionMakers.length) {
    label = '';
    let configUsers = decisionMakers.filter(item => !isUserFilterOption(item));
    let filterUsers = decisionMakers.filter(item => isUserFilterOption(item));
    if (configUsers.length) {
      label += `决策人(${configUsers
        .map(user => {
          if (user.argValue && user.argValue.includes('|')) {
            return user.argValue.split('|')[0];
          }
          return user.argValue;
        }).filter(displayName => !isEmpty(displayName)).join(';')})`;
    }
    if (filterUsers.length) {
      label += `，决策人过滤条件(${filterUsers.map(user => user.argValue)})`;
    }
  }
  return label;
}

export const getTaskCopyUserLabel = function (taskConfig) {
  let label = '';
  if (taskConfig.isSetCopyUser == "1") {
    let copyUsers = taskConfig.copyUsers || [];
    let configUsers = copyUsers.filter(item => !isUserFilterOption(item));
    let filterUsers = copyUsers.filter(item => isUserFilterOption(item));
    if (configUsers.length) {
      label += `抄送人(${configUsers
        .map(user => {
          if (user.argValue && user.argValue.includes('|')) {
            return user.argValue.split('|')[0];
          }
          return user.argValue;
        }).filter(displayName => !isEmpty(displayName)).join(';')})`;
    }
    if (filterUsers.length) {
      label += `，抄送人过滤条件(${filterUsers.map(user => user.argValue)})`;
    }
  } else if (taskConfig.isSetCopyUser == "2") {
    label = "由前一环节办理人指定"
  } else {
    label = '不设置';
  }
  let copyUserCondition = taskConfig.copyUserCondition;
  if (taskConfig.isSetCopyUser && taskConfig.isSetCopyUser != "2" && copyUserCondition) {
    label = `抄送前置条件(${copyUserCondition})，` + label;
  }
  return label;
}

export const getTaskMonitorUserLabel = function (taskConfig) {
  let label = '';
  if (taskConfig.isSetMonitor == "1") {
    let monitors = taskConfig.monitors || [];
    let configUsers = monitors.filter(item => !isUserFilterOption(item));
    let filterUsers = monitors.filter(item => isUserFilterOption(item));
    if (configUsers.length) {
      label += `督办人(${configUsers
        .map(user => {
          if (user.argValue && user.argValue.includes('|')) {
            return user.argValue.split('|')[0];
          }
          return user.argValue;
        }).filter(displayName => !isEmpty(displayName)).join(';')})`;
    }
    if (filterUsers.length) {
      label += `，督办人过滤条件(${filterUsers.map(user => user.argValue)})`;
    }
  } else if (taskConfig.isSetMonitor == "2") {
    label = '由前一环节办理人指定';
  } else {
    label = '不设置';
  }
  return label;
}

export const addSystemPrefix = function (url, $widget) {
  let newUrl = url;
  if ($widget._$SYSTEM_ID && url && !url.startsWith('/sys/')) {
    newUrl = `/sys/${$widget._$SYSTEM_ID}/_${url}`;
  }
  return newUrl;
}
