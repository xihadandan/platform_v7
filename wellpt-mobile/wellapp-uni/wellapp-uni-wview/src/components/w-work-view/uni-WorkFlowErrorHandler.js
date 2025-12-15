import { isEmpty, isArray, isFunction, assignIn, each } from "lodash";
import { errorHandler as ErrorHandler } from "wellapp-uni-framework";
// 1、工作流异常
function WorkFlowException(faultData) {
  var _self = this;
  var eData = faultData.data;
  // var callback = options.callback;
  uni.showToast({ title: eData.msg, icon: "error" });
}

// 2、任务没有指定参与者，弹出人员选择框选择参与人(人员、部门及群组)
function TaskNotAssignedUser(faultData, options) {
  var _self = this;
  var eData = faultData.data;
  var callback1 = options.callback;
  var callbackContext = options.callbackContext;
  var workFlow = options.workFlow;
  var workData = workFlow.getWorkData();
  var title = "";
  if (!isEmpty(eData.title)) {
    title = eData.title;
    title = title.indexOf("(") > -1 ? title.substring(1, title.length - 1) : title;
  }
  var isCancel = true;
  // 新组织弹出框 zyguo
  openOrgSelect({
    valueField: "",
    labelField: "",
    title: "选择承办人【" + title + "】",
    type: "all",
    multiple: true,
    selectTypes: "all",
    valueFormat: "justId",
    orgVersionId: eData.currentOrgVersionId,
    orgVersionIds: eData.orgVersionIds,
    locale: callbackContext && callbackContext.$widget && callbackContext.$widget.locale,
    workData,
    workFlow,
    callback: function (values, labels, treeNodes) {
      //点击确认后的回调函数， 传参为 values, labels， 均为数组格式
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
      workFlow.setTempData("taskUsers", workData.taskUsers);
      isCancel = false;
      // 重新触发回调事件
      if (isFunction(callback1)) {
        callback1.call(callbackContext);
      }
    },
    close: function () {
      //弹出框的关闭事件
      if (isCancel) {
        workFlow.clearTempData();
      }
    },
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
  let taskName = eData.taskName;
  let title = "";
  if (!isEmpty(eData.title)) {
    title = eData.title;
    title = title.indexOf("(") > -1 ? title.substring(1, title.length - 1) : title;
  }
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
  if (users) {
    title = "选择抄送人【" + taskName + "】";
    openUserByUnit(title, users, eData, workFlow, "抄送人", true, okCallback, callbackContext);
  } else {
    // 新组织弹出框zyguo
    openOrgSelect({
      valueField: "",
      labelField: "",
      title: "选择抄送人【" + title + "】",
      type: "all",
      multiple: true,
      selectTypes: "all",
      valueFormat: "justId",
      orgVersionId: eData.currentOrgVersionId,
      orgVersionIds: eData.orgVersionIds,
      locale: callbackContext && callbackContext.$widget && callbackContext.$widget.locale,
      workData,
      workFlow,
      callback: function (values, labels) {
        console.log(values);
        console.log(labels);
        okCallback.apply(this, arguments);
      },
      close: function () {
        if (isCancel) {
          workFlow.clearTempData();
        }
      },
    });
  }
}
// 19、任务没有指定转办人
function TaskNotAssignedTransferUser(faultData, options) {
  taskNotAssignedActionUser(faultData, options, "taskTransferUsers", "transferUsers", "转办");
}
// 4、任务没有指定督办人，弹出人员选择框选择督办人(人员和部门及群组)
function TaskNotAssignedMonitor(faultData, options) {
  const _this = this;
  let eData = faultData.data;
  let callback = options.callback;
  let callbackContext = options.callbackContext;
  let workFlow = options.workFlow;
  let workData = workFlow.getWorkData();
  let title = "";
  if (!isEmpty(eData.title)) {
    title = eData.title;
    title = title.indexOf("(") > -1 ? title.substring(1, title.length - 1) : title;
  }
  let isCancel = true;
  // 新组织弹出框 zyguo
  openOrgSelect({
    valueField: "",
    labelField: "",
    title: "选择督办人【" + title + "】",
    type: "all",
    multiple: true,
    selectTypes: "all",
    valueFormat: "justId",
    orgVersionId: eData.currentOrgVersionId,
    orgVersionIds: eData.orgVersionIds,
    locale: callbackContext && callbackContext.$widget && callbackContext.$widget.locale,
    workData,
    workFlow,
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
      workFlow.setTempData("taskMonitors", workData.taskMonitors);
      isCancel = false;
      // 重新触发回调事件
      if (isFunction(callback)) {
        callback.call(callbackContext);
      }
    },
    close: function () {
      if (isCancel) {
        workFlow.clearTempData();
      }
    },
  });
}
// 22、任务没有指定决定人
function TaskNotAssignedDecisionMaker(faultData, options) {
  const _this = this;
  let eData = faultData.data;
  let callback = options.callback;
  let callbackContext = options.callbackContext;
  let workFlow = options.workFlow;
  let workData = workFlow.getWorkData();
  let title = "";
  if (!isEmpty(eData.title)) {
    title = eData.title;
    title = title.indexOf("(") > -1 ? title.substring(1, title.length - 1) : title;
  }
  let isCancel = true;
  // 新组织弹出框 zyguo
  openOrgSelect({
    valueField: "",
    labelField: "",
    title: "选择决策人【" + title + "】",
    type: "all",
    multiple: true,
    selectTypes: "all",
    valueFormat: "justId",
    orgVersionId: eData.currentOrgVersionId,
    orgVersionIds: eData.orgVersionIds,
    locale: callbackContext && callbackContext.$widget && callbackContext.$widget.locale,
    workData,
    workFlow,
    callback: function (values, labels) {
      console.log(labels);
      let taskDecisionMakers = workData.taskDecisionMakers;
      let taskId = eData.taskId;
      if (values && values.length > 0) {
        taskDecisionMakers[taskId] = values;
        workData.taskDecisionMakers = taskDecisionMakers;
      } else {
        taskDecisionMakers[taskId] = null;
        workData.taskDecisionMakers = taskDecisionMakers;
      }
      workFlow.setTempData("taskDecisionMakers", workData.taskDecisionMakers);
      isCancel = false;
      // 重新触发回调事件
      if (isFunction(callback)) {
        callback.call(callbackContext);
      }
    },
    close: function () {
      if (isCancel) {
        workFlow.clearTempData();
      }
    },
  });
}
// 20、任务没有指定会签人
function TaskNotAssignedCounterSignUser(faultData, options) {
  taskNotAssignedActionUser(faultData, options, "taskCounterSignUsers", "signUsers", "会签");
}
// 21、任务没有指定会签人
function TaskNotAssignedAddSignUser(faultData, options) {
  taskNotAssignedActionUser(faultData, options, "taskAddSignUsers", "addSignUsers", "加签");
}
function taskNotAssignedActionUser(faultData, options, userProp, labelProp, actionName) {
  const _this = this;
  let eData = faultData.data;
  let callback = options.callback;
  let callbackContext = options.callbackContext;
  let workFlow = options.workFlow;
  let workData = workFlow.getWorkData();
  let taskName = eData.taskName;
  let title = "";
  if (!isEmpty(eData.title)) {
    title = eData.title;
    title = title.indexOf("(") > -1 ? title.substring(1, title.length - 1) : title;
  }
  let isCancel = true;
  let users = eData.users;
  let okCallback = function (values, labels) {
    let taskActionUsers = workData[userProp] || {};
    let taskId = eData.taskId;
    if (values && values.length > 0) {
      taskActionUsers[taskId] = values;
      workData[userProp] = taskActionUsers;
    } else {
      taskActionUsers[taskId] = null;
      workData[userProp] = taskActionUsers;
    }
    workFlow[labelProp] = labels;
    isCancel = false;
    // 重新触发回调事件
    if (isFunction(callback)) {
      callback.call(callbackContext);
    }
  };
  // 二次确认选择操作人
  if (users && users.length > 0) {
    title = `选择${actionName}人员【${taskName}】`;
    eData.actionNameLabel = actionName;
    openUserByUnit(title, users, eData, workFlow, `${actionName}人员`, true, okCallback, callbackContext);
  } else {
    // 新组织弹出框zyguo
    let type = ["MyOrg", "MyLeader"];
    let moreOptList = [];
    if (eData.taskInstUuid) {
      type.push("TaskDoneUsers");
      moreOptList.push({
        id: "TaskDoneUsers",
        name: "已办人员", //'已办人员',
        attach: "list;tree",
      });
    }
    openOrgSelect({
      valueField: "",
      labelField: "",
      title: `选择${actionName}人员【${taskName}】`,
      type,
      multiple: true,
      selectTypes: "all",
      valueFormat: "justId",
      viewStyles: { TaskDoneUsers: "list" },
      moreOptList,
      orgVersionId: eData.currentOrgVersionId,
      orgVersionIds: eData.orgVersionIds,
      params: {
        taskInstUuid: eData.taskInstUuid,
      },
      checkableTypesOfOrgType: {
        TaskDoneUsers: "user",
      },
      locale: callbackContext && callbackContext.$widget && callbackContext.$widget.locale,
      viewFormMode: eData.viewFormMode,
      actionName,
      workData,
      workFlow,
      callback: function (values, labels) {
        okCallback.apply(this, arguments);
      },
      close: function () {
        if (isCancel) {
          workFlow.clearTempData();
        }
      },
    });
  }
}

//5、选择具体办理人
function ChooseSpecificUser(faultData, options) {
  showUserByUnit(faultData, options, true);
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
  var taskName = eData.taskName;
  var title = "选择承办人【" + taskName + "】";
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
    workFlow.setTempData("taskUsers", workData.taskUsers);
    // isCancel = false;
    // 重新触发回调事件
    if (isFunction(callback)) {
      callback.call(callbackContext);
    }
  };
  openUserByUnit(title, users, eData, workFlow, "办理人", multiple, okCallback, callbackContext);
}
function openUserByUnit(
  title,
  users,
  eData,
  workFlow,
  optionName,
  multiple,
  okCallback,
  callbackContext,
  footerDescriptionComponent
) {
  let eleOrderMap = eData.eleOrderMap || {};
  let treeData = []; //convertUnitTreeData(users, eleOrderMap);
  let isCancel = true;
  let workData = workFlow.workData;
  let sidGranularity = eData.sidGranularity;
  let defaultViewStyle = eData.defaultViewStyle || "list"; // SystemParams.getValue('unit.dialog.task.users.defaultViewStyle');
  let num = parseInt(eData.autoViewStyleNum || 20); // SystemParams.getValue('unit.dialog.task.users.autoViewStyle.num');
  let userNum = (eData.userIds && eData.userIds.length) || 0;
  let viewStyles = defaultViewStyle == "auto" ? (userNum <= num ? "list" : "tree") : defaultViewStyle;
  let selectTypes = new Set();
  let checkableTypes = new Set();
  let hasUser = false;
  if (sidGranularity) {
    if (sidGranularity == "U") {
      checkableTypes.add("user");
    } else if (sidGranularity == "J") {
      checkableTypes.add("job");
      checkableTypes.add("user");
    } else {
      checkableTypes.add("job");
      checkableTypes.add("user");
      checkableTypes.add("dept");
      checkableTypes.add("unit");
    }
  }
  eData.userIds &&
    eData.userIds.forEach((userId) => {
      if (!userId) {
        return;
      }
      if (userId.startsWith("U")) {
        selectTypes.add("U");
        checkableTypes.add("user");
        hasUser = true;
      } else if (userId.startsWith("J")) {
        selectTypes.add("J");
        checkableTypes.add("job");
      } else if (userId.startsWith("D")) {
        selectTypes.add("D");
        checkableTypes.add("dept");
      } else if (userId.startsWith("S")) {
        selectTypes.add("S");
        checkableTypes.add("unit");
      }
    });
  openOrgSelect({
    valueField: "",
    labelField: "",
    title: title,
    type: "TaskUsers",
    multiple: multiple,
    selectTypes: [...selectTypes.values()],
    valueFormat: "justId",
    viewStyles: {
      TaskUsers: hasUser ? viewStyles : "tree",
    },
    moreOptList: [
      {
        id: "TaskUsers",
        name: optionName, //'办理人',
        treeData: treeData,
        attach: "list;tree",
      },
    ],
    orgVersionId: eData.currentOrgVersionId,
    orgVersionIds: eData.orgVersionIds || [eData.currentOrgVersionId],
    params: {
      userIds: eData.userIds || [],
      orgVersionIds: eData.orgVersionIds || [eData.currentOrgVersionId],
    },
    checkableTypes: [...checkableTypes.values()],
    locale: callbackContext && callbackContext.$widget && callbackContext.$widget.locale,
    viewFormMode: eData.viewFormMode,
    actionName: eData.actionNameLabel,
    workData,
    workFlow,
    callback: function () {
      isCancel = false;
      okCallback.apply(this, arguments);
    },
    close: function () {
      if (isCancel) {
        workFlow.clearTempData();
      }
    },
  });
}

//解析选中的树节点，解析出职位路径数组
function gettaskUserJobPaths(treeNodes) {
  var taskUserJobPaths = [];
  each(treeNodes, function (treeNode) {
    var jobPaths = "";
    if (!treeNode.extValues) {
      return;
    }
    var VersionId = treeNode.extValues.VersionId;
    jobPaths = VersionId;
    var paths = "";
    if (treeNode.idPath != undefined) {
      paths = treeNode.idPath.split("/");
      each(paths, function (path) {
        if (!path.startsWith("U")) {
          jobPaths += "/" + path.id;
        }
      });
    } else if (treeNode.allPath != undefined) {
      paths = treeNode.getPath();
      each(paths, function (path) {
        if (!path.id.startsWith("U")) {
          jobPaths += "/" + path.id;
        }
      });
    }
    if (isEmpty(paths)) {
      return taskUserJobPaths;
    }

    taskUserJobPaths.push(jobPaths);
  });
  return taskUserJobPaths;
}

function convertUnitTreeData(users, eleOrderMap) {
  var treeNodes = [];
  var showOrgVersion = isShowOrgVersion(users);
  // var showUnit = {};
  each(users, function (user) {
    var node_id = user.id;
    if (node_id.startsWith("J")) {
      createJobTreeNodeIfRequired(node_id, user.name, null, null, treeNodes);
    } else {
      if (user.showJobIdPath && user.showJobNamePath) {
        if (user.showJobIdPath.indexOf(";") > 0) {
          //同时命中多个情况
          var more_ids = user.showJobIdPath.split(";");
          var more_names = user.showJobNamePath.split(";");
          var more_jobNames = user.showJobName.split(";");
          for (let i = 0; i < more_ids.length; i++) {
            var ids = more_ids[i].split("/");
            var names = more_names[i].split("/");
            var jobName = more_jobNames[i];
            var eleIds = [];
            var parentNode = null;
            for (var j = 0; j < ids.length; j++) {
              var id = ids[j];
              eleIds.push(id);
              if (showOrgVersion && id.startsWith("V")) {
                parentNode = createVersionTreeNodeIfRequired(id, names[j], eleIds.join("/"), parentNode, treeNodes);
              } else if (id.startsWith("O")) {
                parentNode = createOrgTreeNodeIfRequired(id, names[j], eleIds.join("/"), parentNode, treeNodes);
              } else if (id.startsWith("B")) {
                //2021-09-27 不在控制是否有不同的单位节点 直接显示单位节点
                parentNode = createBusinessTreeNodeIfRequired(id, names[j], eleIds.join("/"), parentNode, treeNodes);
              } else if (id.startsWith("J")) {
                parentNode = createJobTreeNodeIfRequired(id, names[j], eleIds.join("/"), parentNode, treeNodes);
              } else if (id.startsWith("D")) {
                parentNode = createDepartmentTreeNodeIfRequired(id, names[j], eleIds.join("/"), parentNode, treeNodes);
              } else if (id.startsWith("G")) {
                parentNode = createGroupTreeNodeIfRequired(id, names[j], eleIds.join("/"), parentNode, treeNodes);
              }
            }
            user.showJobName = jobName;
            createUserTreeNode(user, parentNode, treeNodes);
          }
        } else {
          let ids = user.showJobIdPath.split("/");
          let names = user.showJobNamePath.split("/");
          let eleIds = [];
          let parentNode = null;
          for (let j = 0; j < ids.length; j++) {
            let id = ids[j];
            eleIds.push(id);
            if (showOrgVersion && id.startsWith("V")) {
              parentNode = createVersionTreeNodeIfRequired(id, names[j], eleIds.join("/"), parentNode, treeNodes);
            } else if (id.startsWith("O")) {
              parentNode = createOrgTreeNodeIfRequired(id, names[j], eleIds.join("/"), parentNode, treeNodes);
            } else if (id.startsWith("B")) {
              //2021-09-27 不在控制是否有不同的单位节点 直接显示单位节点
              parentNode = createBusinessTreeNodeIfRequired(id, names[j], eleIds.join("/"), parentNode, treeNodes);
            } else if (id.startsWith("J")) {
              parentNode = createJobTreeNodeIfRequired(id, names[j], eleIds.join("/"), parentNode, treeNodes);
            } else if (id.startsWith("D")) {
              parentNode = createDepartmentTreeNodeIfRequired(id, names[j], eleIds.join("/"), parentNode, treeNodes);
            } else if (id.startsWith("G")) {
              parentNode = createGroupTreeNodeIfRequired(id, names[j], eleIds.join("/"), parentNode, treeNodes);
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
      var ids = user.showJobIdPath.split("/");
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

// 打开组织选择框
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
  params,
  checkableTypes,
  checkableTypesOfOrgType,
  viewFormMode,
  actionName,
  callback,
  close,
  locale = {},
  workData,
  workFlow,
}) {
  const $widget = workFlow && workFlow.workView && workFlow.workView.$widget;
  const Props = {
    data: {
      orgId: "",
      orgOptions: {
        title,
        orgType: type != "all" ? (Array.isArray(type) ? type : [type]) : undefined,
        multiSelect: multiple,
        selectTypes,
        isPathValue: valueFormat != "justId",
        viewStyles,
        moreOptList,
        orgVersionId,
        orgVersionIds,
        params,
        checkableTypes,
        checkableTypesOfOrgType,
      },
      viewFormMode,
      actionName,
      locale,
    },
    methods: {
      onConfirm({ value, label, nodes }) {
        if (callback) {
          callback.call(this, value, label, nodes);
        }
      },
      onClose() {
        if (close) {
          close.call(this);
        }
        $widget.clearErrorOrgSelectOptions();
      },
      viewFormModeChange(data) {
        this.$set(workData, "viewFormMode", data || "default");
      },
    },
  };
  if ($widget) {
    $widget.setErrorOrgSelectOptions({ ...Props });
  }
}

// 是否显示单位结点
/* eslint-disable */
function isShowUnit(unitId, eleIds, users, showUnit) {
  if (showUnit[unitId] != null) {
    return showUnit[unitId];
  }
  var fullPath = eleIds.join("/");
  var prifix = fullPath.substring(0, fullPath.length - unitId.length - 1);
  for (var i = 0; i < users.length; i++) {
    var user = users[i];
    var idPath = user.mainJobIdPath;
    if (idPath) {
      if (idPath.startsWith(prifix + "/B") && !idPath.startsWith(prifix + "/" + unitId)) {
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
  if (user["mainDepartmentName"] != null) {
    dataListJobName.push(user["mainDepartmentName"]);
  }
  if (user["showJobName"] != null) {
    if (
      user.showJobNamePath != "" &&
      dataListJobName.length == 1 &&
      user.showJobNamePath.split("/").indexOf(user["mainDepartmentName"]) == -1
    ) {
      var dept = user.showJobNamePath.split("/");
      dataListJobName.splice(0, 1, dept[dept.length - 2]);
    }
    dataListJobName.push(user["showJobName"]);
  }
  var node = {
    id: user["id"],
    name: user["name"],
    namePy: user["namePy"],
    //mainJobName: user['showJobName'],
    dataListJobName:
      user["showJobName"] == " " ? "" : dataListJobName.length == 1 ? dataListJobName[0] : dataListJobName.join("/"),
    // mainDepartmentName: user['mainDepartmentName'],
    // mainJobNamePath: user['mainJobNamePath'],
    type: "U",
    iconSkin: "U",
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
      type: "V",
      iconSkin: "V",
      eleIdPath: eleIdPath,
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
      type: "O",
      iconSkin: "O",
      eleIdPath: eleIdPath,
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
      type: "B",
      iconSkin: "B",
      eleIdPath: eleIdPath,
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
      type: "J",
      iconSkin: "J",
      eleIdPath: eleIdPath,
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
      type: "D",
      iconSkin: "D",
      eleIdPath: eleIdPath,
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
      type: "G",
      iconSkin: "G",
      eleIdPath: eleIdPath,
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
  var eData = faultData.data;
  // var callback = options.callback;
  // var callbackContext = options.callbackContext;
  // var workFlow = options.workFlow;
  // var workData = workFlow.getWorkData();
  var title = eData.useDirection ? "请选择流向" : "请选择环节";
  var useDirection = eData.useDirection;
  var multiselect = eData.multiselect;
  var toTasks = eData.toTasks;

  options.callbackContext.onError("judgmentBranchFlowNotFound", {
    title: title,
    fromTaskId: eData.fromTaskId,
    useDirection: useDirection,
    multiselect: multiselect,
    toTasks: toTasks,
    options: options,
  });
}

// 8、找到多个判断分支流向
function MultiJudgmentBranch(faultData, options) {
  JudgmentBranchFlowNotFound(faultData, options);
}

// 9、弹出环节选择框选择下一子流程
function SubFlowNotFound(faultData, options) {
  console.log(faultData);
  console.log(options);
  uni.showToast({ title: "流程没有配置要发起的子流程!" });
}

// 11、用户没有权限访问流程
function IdentityNotFlowPermission(faultData, options) {
  console.log(options);
  var eData = faultData.data;
  uni.showToast({ title: eData.msg });
}

// 12、找不到退回操作的退回环节异常类
function RollbackTaskNotFound(faultData, options) {
  // var _self = this;
  var eData = faultData.data;
  // var callback = options.callback;
  // var callbackContext = options.callbackContext;
  // var workFlow = options.workFlow;
  // var workData = workFlow.getWorkData();
  var toTasks = eData.rollbackTasks;

  options.callbackContext.onError("rollbackTaskNotFound", {
    title: "选择退回环节",
    toTasks: toTasks,
    options: options,
  });
}

// 13、找不到特送环节操作的环节异常类
function GotoTaskNotFound(faultData, options) {
  // var _self = this;
  var eData = faultData.data;
  // var callback = options.callback;
  // var callbackContext = options.callbackContext;
  // var workFlow = options.workFlow;
  // var workData = workFlow.getWorkData();
  var toTasks = eData.toTasks;

  options.callbackContext.onError("gotoTaskNotFound", {
    title: "选择特送环节",
    fromTaskId: eData.fromTaskId,
    toTasks: toTasks,
    options: options,
  });
}

// 14、表单数据保存失败
function SaveData(faultData, options) {
  console.log(faultData);
  console.log(options);
  uni.showToast({ title: "表单数据保存失败！" });
}

// 15、必填域为空
function RequiredFieldIsBlank(faultData, options) {
  console.log(faultData);
  console.log(options);
  uni.showToast({ title: "表单必填域为空！" });
}

// 16、选择归档夹
function ChooseArchiveFolder(faultData, options) {
  console.log(faultData);
  console.log(options);
  uni.showToast({ title: "不支持选择归档夹，请在电脑端操作！" });
}

// 一人多职未选择职位的情况下
function MultiJobNotSelected(faultData, options) {
  // var workFlow = options.workFlow;
  // var callback1 = options.callback;
  // var callbackContext = options.callbackContext;
  var jobs = [];
  for (var i = 0; i < faultData.data.jobs.length; i++) {
    var job = faultData.data.jobs[i];
    var text = "";
    if (job.parent && job.parent.id) {
      if (job.parent.id.indexOf("D") == 0) {
        text += job.parent.name + " - ";
      }
    }
    text += job.name;
    job.text = text;
    jobs.push(job);
  }

  options.callbackContext.onError("multiJobNotSelected", {
    title: "请选择职位发起流程",
    jobs: jobs,
    options: options,
  });
}

// 18、表单数据验证失败
function FormDataValidateException(faultData, options) {
  if (typeof faultData.data == "string") {
    uni.showToast({ title: faultData.data });
  } else if (typeof faultData.data == "object") {
    var msg = "";
    msg += faultData.data.msg;
    if (faultData.data.errors) {
      var errorStrings = [];
      each(faultData.data.errors, function (error) {
        errorStrings.push(error.displayName + "(" + error.fieldName + ")" + error.msg);
      });
      msg += ": " + errorStrings.join("、");
    }
    uni.showToast({ title: msg.toString() });
  } else {
    Default(faultData, options);
  }
}

// 19、流程无法处理的异常
function Default(faultData) {
  var msg = null;
  if (!isEmpty(faultData.msg)) {
    msg = faultData.msg;
  } else {
    msg = JSON.stringify(faultData);
  }
  uni.showToast({ title: "工作流无法处理的未知异常：" + msg });
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
  TaskNotAssignedAddSignUser: TaskNotAssignedAddSignUser, // 21、任务没有指定加签人
  TaskNotAssignedDecisionMaker: TaskNotAssignedDecisionMaker, // 22、任务没有指定决定人
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
  for (const key in WorkFlowErrorCode) {
    if (Object.hasOwnProperty.call(WorkFlowErrorCode, key)) {
      _self.errorHandler.register(key, WorkFlowErrorCode[key]);
    }
  }
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
WorkFlowErrorHandler.prototype.openOrgSelect = openOrgSelect;
export default WorkFlowErrorHandler;
