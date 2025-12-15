define(['jquery', 'server', 'commons', 'constant', 'appContext', 'appModal', 'formBuilder'], function (
  $,
  server,
  commons,
  constant,
  appContext,
  appModal,
  formBuilder
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  // var SystemParams = server.SystemParams;
  // 1、工作流异常
  function WorkFlowException(faultData, options) {
    var _self = this;
    var eData = faultData.data;
    var callback = options.callback;
    if (eData.hasOwnProperty('autoClose')) {
      if (eData.autoClose === true) {
        appModal.info(eData.msg);
      } else {
        appModal.error(eData.msg);
      }
    } else {
      appModal.error(eData.msg, function () {
        // 重新触发回调事件
        if ($.isFunction(callback)) {
          callback.call(_self);
        }
      });
    }
  }

  // 2、任务没有指定参与者，弹出人员选择框选择参与人(人员、部门及群组)
  function TaskNotAssignedUser(faultData, options) {
    var _self = this;
    var eData = faultData.data;
    var callback1 = options.callback;
    var callbackContext = options.callbackContext;
    var workFlow = options.workFlow;
    var workData = workFlow.getWorkData();
    var title = '';
    if (StringUtils.isNotBlank(eData.title)) {
      title = eData.title;
      title = title.indexOf('(') > -1 ? title.substring(1, title.length - 1) : title;
    }
    var isCancel = true;
    // 新组织弹出框 zyguo
    $.unit2.open({
      valueField: '',
      labelField: '',
      title: '选择承办人<span class="taskFontWeight">【' + title + '】</span>',
      type: 'all',
      multiple: true,
      selectTypes: 'all',
      valueFormat: 'justId',
      callback: function (values, labels, treeNodes) {
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
        isCancel = false;
        // 重新触发回调事件
        if ($.isFunction(callback1)) {
          callback1.call(callbackContext);
        }
      },
      close: function () {
        if (isCancel) {
          workFlow.clearTempData();
        }
      }
    });

    // $.unit.open({
    // title : "选择承办人" + title,
    // close : function() {
    // if (isCancel) {
    // workFlow.clearTempData();
    // }
    // },
    // afterSelect : function(laReturn) {
    // var taskUsers = {};
    // var taskId = eData.taskId;
    // if (StringUtils.isNotBlank(laReturn.id)) {
    // // 在原来的环节办理人上增加环节办理人
    // taskUsers = workData.taskUsers;
    // var userIds = laReturn.id.split(";");
    // taskUsers[taskId] = userIds;
    // } else {
    // taskUsers[taskId] = null;
    // workData.taskUsers = taskUsers;
    // }
    // workFlow.setTempData("taskUsers", workData.taskUsers);
    // isCancel = false;
    // // 重新触发回调事件
    // if ($.isFunction(callback)) {
    // callback.call(callbackContext);
    // }
    // }
    // });
  }

  // 3、任务没有指定抄送人，弹出人员选择框选择抄送人(人员、部门及群组)
  function TaskNotAssignedCopyUser(faultData, options) {
    var _self = this;
    var eData = faultData.data;
    var callback = options.callback;
    var callbackContext = options.callbackContext;
    var workFlow = options.workFlow;
    var workData = workFlow.getWorkData();
    var taskName = eData.taskName;
    var title = '';
    if (StringUtils.isNotBlank(eData.title)) {
      title = eData.title;
      title = title.indexOf('(') > -1 ? title.substring(1, title.length - 1) : title;
    }
    var isCancel = true;
    var users = eData.users;
    var okCallback = function (values, labels) {
      var taskCopyUsers = workData.taskCopyUsers;
      var taskId = eData.taskId;
      if (values && values.length > 0) {
        taskCopyUsers[taskId] = values;
        workData.taskCopyUsers = taskCopyUsers;
      } else {
        taskCopyUsers[taskId] = null;
        workData.taskCopyUsers = taskCopyUsers;
      }
      isCancel = false;
      // 重新触发回调事件
      if ($.isFunction(callback)) {
        callback.call(callbackContext);
      }
    };
    // 二次确认选择抄送人
    if (users) {
      title = '选择抄送人<span class="taskFontWeight">【' + taskName + '】</span>';
      openUserByUnit(title, users, eData, workFlow, '抄送人', true, okCallback);
    } else {
      // 新组织弹出框zyguo
      $.unit2.open({
        valueField: '',
        labelField: '',
        title: '选择抄送人<span class="taskFontWeight">【' + title + '】</span>',
        type: 'all',
        multiple: true,
        selectTypes: 'all',
        valueFormat: 'justId',
        callback: function (values, labels) {
          okCallback.apply(this, arguments);
        },
        close: function () {
          if (isCancel) {
            workFlow.clearTempData();
          }
        }
      });
    }
    // $.unit.open({
    // title : "选择抄送人" + title,
    // close : function() {
    // if (isCancel) {
    // workFlow.clearTempData();
    // }
    // },
    // afterSelect : function(laReturn) {
    // var taskCopyUsers = workData.taskCopyUsers;
    // var taskId = eData.taskId;
    // if (StringUtils.isNotBlank(laReturn.id)) {
    // var userIds = laReturn.id.split(";");
    // taskCopyUsers[taskId] = userIds;
    // workData.taskCopyUsers = taskCopyUsers;
    // } else {
    // taskCopyUsers[taskId] = null;
    // workData.taskCopyUsers = taskCopyUsers;
    // }
    // isCancel = false;
    // // 重新触发回调事件
    // if ($.isFunction(callback)) {
    // callback.call(callbackContext);
    // }
    // }
    // });
  }

  function TaskNotAssignedTransferUser(faultData, options) {
    var _self = this;
    var eData = faultData.data;
    var callback = options.callback;
    var callbackContext = options.callbackContext;
    var workFlow = options.workFlow;
    var workData = workFlow.getWorkData();
    var taskName = eData.taskName;
    var title = '';
    if (StringUtils.isNotBlank(eData.title)) {
      title = eData.title;
      title = title.indexOf('(') > -1 ? title.substring(1, title.length - 1) : title;
    }
    var users = eData.users;
    var okCallback = function (values, labels) {
      var taskTransferUsers = workData.taskTransferUsers;
      var taskId = eData.taskId;
      if (values && values.length > 0) {
        taskTransferUsers[taskId] = values;
        workData.taskTransferUsers = taskTransferUsers;
      } else {
        taskTransferUsers[taskId] = null;
        workData.taskTransferUsers = taskTransferUsers;
      }
      workFlow.transferUsers = labels;
      isCancel = true;
      // 重新触发回调事件
      if ($.isFunction(callback)) {
        callback.call(callbackContext);
      }
    };
    // 二次确认选择转办人
    if (users && users.length > 0) {
      title = '选择转办人员<span class="taskFontWeight">【' + taskName + '】</span>';
      openUserByUnit(title, users, eData, workFlow, '转办人员', true, okCallback);
    } else {
      // 新组织弹出框zyguo
      $.unit2.open({
        valueField: '',
        labelField: '',
        title: '选择转办人员<span class="taskFontWeight">【' + taskName + '】</span>',
        type: 'all',
        multiple: true,
        selectTypes: 'all',
        valueFormat: 'justId',
        callback: function (values, labels) {
          if (values && values.length > 0) {
            okCallback.apply(this, arguments);
          } else {
            appModal.warning('转办人员不能为空！');
          }
        },
        close: function () {
          if (isCancel) {
            workFlow.clearTempData();
          }
        }
      });
    }
  }

  // 4、任务没有指定督办人，弹出人员选择框选择督办人(人员和部门及群组)
  function TaskNotAssignedMonitor(faultData, options) {
    var _self = this;
    var eData = faultData.data;
    var callback = options.callback;
    var callbackContext = options.callbackContext;
    var workFlow = options.workFlow;
    var workData = workFlow.getWorkData();
    var title = '';
    if (StringUtils.isNotBlank(eData.title)) {
      title = eData.title;
      title = title.indexOf('(') > -1 ? title.substring(1, title.length - 1) : title;
    }
    var isCancel = true;
    // 新组织弹出框 zyguo
    $.unit2.open({
      valueField: '',
      labelField: '',
      title: '选择督办人<span class="taskFontWeight">【' + title + '】</span>',
      type: 'all',
      multiple: true,
      selectTypes: 'all',
      valueFormat: 'justId',
      callback: function (values, labels) {
        var taskMonitors = workData.taskMonitors;
        var taskId = eData.taskId;
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
        if ($.isFunction(callback)) {
          callback.call(callbackContext);
        }
      },
      close: function () {
        if (isCancel) {
          workFlow.clearTempData();
        }
      }
    });

    // $.unit.open({
    // title : "选择督办人" + title,
    // close : function() {
    // if (isCancel) {
    // workFlow.clearTempData();
    // }
    // },
    // afterSelect : function(laReturn) {
    // var taskMonitors = workData.taskMonitors;
    // var taskId = eData.taskId;
    // if (StringUtils.isNotBlank(laReturn.id)) {
    // var userIds = laReturn.id.split(";");
    // taskMonitors[taskId] = userIds;
    // workData.taskMonitors = taskMonitors;
    // } else {
    // taskMonitors[taskId] = null;
    // workData.taskMonitors = taskMonitors;
    // }
    // workFlow.setTempData("taskMonitors", workData.taskMonitors);
    // isCancel = false;
    // // 重新触发回调事件
    // if ($.isFunction(callback)) {
    // callback.call(callbackContext);
    // }
    // }
    // });
  }

  // 选择组织选择框人员
  function ChooseUnitUser(faultData, options) {
    var _self = this;
    var eData = faultData.data;
    var callback = options.callback;
    var callbackContext = options.callbackContext;
    var workFlow = options.workFlow;
    var workData = workFlow.getWorkData();
    var unitUser = eData.unitUser;
    var multiple = unitUser.multiple;
    var selectType = unitUser.selectType == 4 ? 'U' : 'all';
    var type = unitUser.type || 'all';
    var taskName = eData.taskName;
    var eleIdPaths = unitUser.initIds.join(';');
    // for (var i = 0; i < eData.unitUser.initIds.length; i++) {
    // var val = eData.unitUser.initIds[i];
    // if (filterCondition !== "") {
    // filterCondition += ",";
    // }
    // filterCondition += val;
    // }
    // filterCondition = encodeURI(filterCondition);
    var isCancel = true;
    // 选择新的组织弹出框 zyguo
    $.unit2.open({
      valueField: '',
      labelField: '',
      title: '选择承办人<span class="taskFontWeight">【' + taskName + '】</span>',
      type: type,
      multiple: multiple,
      selectTypes: selectType,
      valueFormat: 'justId',
      otherParams: {
        eleIdPath: eleIdPaths
      },
      callback: function (values, labels, treeNodes) {
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
        isCancel = false;
        // 重新触发回调事件
        if ($.isFunction(callback)) {
          callback.call(callbackContext);
        }
      },
      close: function () {
        if (isCancel) {
          workFlow.clearTempData();
        }
      }
    });

    // $.unit.open({
    // initNames : "",
    // initIDs : "",
    // title : "选择承办人(" + taskName + ")",
    // multiple : multiple,
    // selectType : selectType,
    // nameType : "21",
    // type : "Mixture",
    // loginStatus : false,
    // excludeValues : null,
    // showType : false,
    // filterCondition : filterCondition,
    // close : function() {
    // if (isCancel) {
    // workFlow.clearTempData();
    // }
    // },
    // afterSelect : function(laReturn) {
    // isCancel = false;
    // var taskUsers = {};
    // var taskId = eData.taskId;
    // if (StringUtils.isNotBlank(laReturn.id)) {
    // // 在原来的环节办理人上增加环节办理人
    // taskUsers = workData.taskUsers;
    // var userIds = laReturn.id.split(";");
    // taskUsers[taskId] = userIds;
    // } else {
    // taskUsers[taskId] = null;
    // workData.taskUsers = taskUsers;
    // }
    // workFlow.setTempData("taskUsers", workData.taskUsers);
    // isCancel = false;
    // // 重新触发回调事件
    // if ($.isFunction(callback)) {
    // callback.call(callbackContext);
    // }
    // }
    // });
  }

  //5、选择具体办理人
  function ChooseSpecificUser(faultData, options) {
    showUserByUnit(faultData, options, true);
  }

  //	 5、选择具体办理人
  //	function ChooseSpecificUser(faultData, options) {
  //		if (faultData.data.unitUser) {
  //			ChooseUnitUser(faultData, options);
  //			return;
  //		}
  //		var _self = this;
  //		var eData = faultData.data;
  //		var callback = options.callback;
  //		var callbackContext = options.callbackContext;
  //		var workFlow = options.workFlow;
  //		var workData = workFlow.getWorkData();
  //		var taskName = eData.taskName;
  //		var taskId = eData.taskId;
  //		var message, isCancel = true;
  //		message = "<div class='choose-specific-user'></div>";
  //		var dialogOptions = {
  //			title : "选择具体办理人" + "(" + taskName + ")",
  //			size : "middle",
  //			message : message,
  //			onEscape : function() {
  //				if (isCancel) {
  //					workFlow.clearTempData();
  //				}
  //			},
  //			shown : function() {
  //				var data = [];
  //				$.each(eData.users, function(index, object) {
  //					data.push({
  //						"id" : object.id,
  //						"text" : object.name
  //					});
  //				});
  //				var $container = $(".choose-specific-user");
  //				// 手机端滚动处理
  //				$container = scrollForMobileApp(data, $container);
  //				formBuilder.buildSelect2({
  //					select2 : {
  //						data : data,
  //						multiple : true
  //					},
  //					container : $container,
  //					name : "chooseSpecificUser",
  //					diaplay : "chooseSpecificUserName",
  //					labelColSpan : "0",
  //					controlColSpan : "12"
  //				});
  //			},
  //			buttons : {
  //				confirm : {
  //					label : "确定",
  //					className : "btn-primary",
  //					callback : function() {
  //						var $container = $(".choose-specific-user");
  //						var userId = "";
  //						// 手机端
  //						if (appContext.isMobileApp()) {
  //							var checkedSelector = "input[name=chooseSpecificUser]:checked";
  //							var $checkbox = $container[0].querySelectorAll(checkedSelector);
  //							if ($checkbox.length === 0) {
  //								appModal.error("请选择具体办理人!");
  //								return false;
  //							}
  //							$.each($checkbox, function(i) {
  //								userId += this.value;
  //								if (i !== $checkbox.length - 1) {
  //									userId += ";";
  //								}
  //							});
  //						} else {
  //							// PC端
  //							userId = $("input[name=chooseSpecificUser]", $container).val();
  //						}
  //
  //						if (StringUtils.isBlank(userId)) {
  //							appModal.error("请选择具体办理人!");
  //							return false;
  //						}
  //
  //						var userIds = userId.split(";");
  //						workData.taskUsers[taskId] = [ userId ];
  //						workFlow.setTempData("taskUsers", workData.taskUsers);
  //						isCancel = false;
  //						// 重新触发回调事件
  //						if ($.isFunction(callback)) {
  //							callback.call(callbackContext);
  //						}
  //						return true;
  //					}
  //				},
  //				cancel : {
  //					label : "取消",
  //					className : "btn-default",
  //					callback : function() {
  //						if (isCancel) {
  //							workFlow.clearTempData();
  //						}
  //					}
  //				}
  //			}
  //		};
  //		appModal.dialog(dialogOptions);
  //	}

  // 通过组织框，显示所有的本地用户
  function showUserByUnit(faultData, options, multiple) {
    var _self = this;
    var eData = faultData.data;
    var callback = options.callback;
    var callbackContext = options.callbackContext;
    var workFlow = options.workFlow;
    var workData = workFlow.getWorkData();
    var users = eData.users;
    var taskName = eData.taskName;
    var title = '选择承办人<span class="taskFontWeight">【' + taskName + '】</span>';
    //    $.each(users, function (i) {
    //      var user = users[i];
    //      var node = {
    //        id: user['id'],
    //        name: user['name'],
    //        mainJobName: user['mainJobName'],
    //        mainDepartmentName: user['mainDepartmentName'],
    //        mainJobNamePath: user['mainJobNamePath'],
    //        type: 'U',
    //        iconSkin: 'U'
    //      };
    //      treeData.push(node);
    //    });
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
      isCancel = false;
      // 重新触发回调事件
      if ($.isFunction(callback)) {
        callback.call(callbackContext);
      }
    };
    openUserByUnit(title, users, eData, workFlow, '办理人', multiple, okCallback);
  }

  function openUserByUnit(title, users, eData, workFlow, optionName, multiple, okCallback) {
    var eleOrderMap = eData.eleOrderMap || {};
    var treeData = convertUnitTreeData(users, eleOrderMap);
    var isCancel = true;
    var defaultViewStyle = SystemParams.getValue('unit.dialog.task.users.defaultViewStyle');
    var num = SystemParams.getValue('unit.dialog.task.users.autoViewStyle.num');
    var userNum = 0;
    if (defaultViewStyle == 'auto') {
      $.each(users, function (uIndex, uItem) {
        if (uItem.showJobNamePath) {
          var len = uItem.showJobNamePath.split(';').length;
          userNum = userNum - 0 + len;
        }
      });
    }
    var viewStyles = defaultViewStyle == 'auto' ? (userNum <= num ? 'list' : 'tree') : defaultViewStyle;
    var selectTypes = '';
    if (treeData.length > 0 && treeData[0].id.startsWith('J') && typeof treeData[0].children == 'undefined') {
      selectTypes = 'J';
    } else {
      selectTypes = 'U';
    }
    $.unit2.open({
      valueField: '',
      labelField: '',
      title: title,
      type: 'TaskUsers',
      multiple: multiple,
      selectTypes: selectTypes,
      valueFormat: 'justId',
      viewStyles: {
        TaskUsers: viewStyles
      },
      moreOptList: [
        {
          id: 'TaskUsers',
          name: optionName, //'办理人',
          treeData: treeData,
          attach: 'list;tree'
        }
      ],
      callback: function (values, labels, treeNodes) {
        isCancel = false;
        okCallback.apply(this, arguments);
      },
      close: function () {
        if (isCancel) {
          workFlow.clearTempData();
        }
      }
    });
  }

  //解析选中的树节点，解析出职位路径数组
  function gettaskUserJobPaths(treeNodes) {
    var taskUserJobPaths = [];
    $.each(treeNodes, function (i, treeNode) {
      var jobPaths = '';
      if (!treeNode.extValues) {
        return;
      }
      var VersionId = treeNode.extValues.VersionId;
      jobPaths = VersionId;
      var paths = '';
      if (treeNode.idPath != undefined) {
        paths = treeNode.idPath.split('/');
        $.each(paths, function (i, path) {
          if (!path.startsWith('U')) {
            jobPaths += '/' + path.id;
          }
        });
      } else if (treeNode.allPath != undefined) {
        paths = treeNode.getPath();
        $.each(paths, function (i, path) {
          if (!path.id.startsWith('U')) {
            jobPaths += '/' + path.id;
          }
        });
      }
      if (StringUtils.isBlank(paths)) {
        return taskUserJobPaths;
      }

      taskUserJobPaths.push(jobPaths);
    });
    return taskUserJobPaths;
  }

  function convertUnitTreeData(users, eleOrderMap) {
    var treeNodes = [];
    var showOrgVersion = isShowOrgVersion(users);
    var showUnit = {};
    $.each(users, function (i, user) {
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
            for (var i = 0; i < more_ids.length; i++) {
              if (more_ids[i].endsWith('/')) {
                more_ids[i] = more_ids[i].substring(0, more_ids[i].length - 1);
              }
              if (more_names[i].endsWith('/')) {
                more_names[i] = more_names[i].substring(0, more_names[i].length - 1);
              }
              var ids = handleEleIds(more_ids[i], user.groupType, user.memberObjId);
              var names = handleEleNames(more_ids[i], more_names[i], user.groupType, user.memberObjId);

              var jobName = more_jobNames[i];
              var eleIds = [];
              var parentNode = null;
              for (var j = 0; j < ids.length; j++) {
                var id = ids[j].substring(ids[j].lastIndexOf('/') + 1, ids[j].length);
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
            if (user.showJobIdPath.endsWith('/')) {
              user.showJobIdPath = user.showJobIdPath.substring(0, user.showJobIdPath.length - 1);
            }
            if (user.showJobNamePath.endsWith('/')) {
              user.showJobNamePath = user.showJobNamePath.substring(0, user.showJobNamePath.length - 1);
            }
            var ids = handleEleIds(user.showJobIdPath, user.groupType, user.memberObjId);
            var names = handleEleNames(user.showJobIdPath, user.showJobNamePath, user.groupType, user.memberObjId);

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

  // 处理树形展示的id层级
  function handleEleIds(showJobIdPath, groupTypes, memberObjIds) {
    var ids = [];
    if (showJobIdPath.split('/').length >= 2) {
      ids.push(showJobIdPath.replace('/', '||||').split('||||')[0]);

      var groupType = null;
      if (showJobIdPath.startsWith('DU')) {
        groupType = 'DU';
      } else if (showJobIdPath.startsWith('G')) {
        groupType = 'G';
      }

      // 如果群组的层级不是到职位，则需要对显示的节点进行处理
      if (groupType != null) {
        var idx = groupTypes.split(';').indexOf(groupType);
        var memberObjId = memberObjIds.split(';')[idx];
        if (showJobIdPath.indexOf(memberObjId) == -1) {
          // 匹配错误，该用户可能在2个及以上的公共群组内或者2个及以上的职务群组内
          $.each(memberObjIds.split(';'), function (i, obj) {
            if (showJobIdPath.indexOf(obj) > -1) {
              idx = i;
              memberObjId = memberObjIds.split(';')[idx];
              return;
            }
          });
        }
        if (showJobIdPath.endsWith(memberObjId)) {
          ids.push(showJobIdPath.substring(showJobIdPath.lastIndexOf('/') + 1, showJobIdPath.length));
        } else {
          ids.push(memberObjId.substring(memberObjId.lastIndexOf('/') + 1, memberObjId.length));
          var childIdPath = showJobIdPath.split('/' + memberObjId + '/')[1];
          if (childIdPath) {
            $.each(childIdPath.split('/'), function (index, obj) {
              ids.push(obj);
            });
          }
        }
      } else {
        ids = showJobIdPath.split('/');
        // ids.push(showJobIdPath.substring(showJobIdPath.lastIndexOf('/') + 1, showJobIdPath.length));
      }
    } else {
      ids = showJobIdPath.split('||||');
    }
    return ids;
  }

  // 处理树形转型的name层级
  function handleEleNames(showJobIdPath, showJobNamePath, groupTypes, memberObjIds) {
    var names = [];
    if (showJobNamePath.split('/').length >= 2) {
      names.push(showJobNamePath.replace('/', '||||').split('||||')[0]);

      // 如果群组的层级不是到职位，则需要对显示的节点进行处理
      if (showJobIdPath.startsWith('DU')) {
        var idx = groupTypes.split(';').indexOf('DU');
        var memberObjId = memberObjIds.split(';')[idx];
        if (showJobIdPath.indexOf(memberObjId) == -1) {
          // 匹配错误，该用户可能在2个及以上的公共群组内或者2个及以上的职务群组内
          $.each(memberObjIds.split(';'), function (i, obj) {
            if (showJobIdPath.indexOf(obj) > -1) {
              idx = i;
              memberObjId = memberObjIds.split(';')[idx];
              return;
            }
          });
        }
        if (showJobIdPath.endsWith(memberObjId)) {
          names.push(handleNamePath(showJobIdPath, showJobNamePath));
        } else {
          var jobId = memberObjId.substring(memberObjId.lastIndexOf('/') + 1, memberObjId.length);
          idx = showJobIdPath.split('/').indexOf(jobId);
          names.push(
            handleNamePath(showJobIdPath, showJobNamePath.split(showJobNamePath.split('/')[idx] + '/')[0] + showJobNamePath.split('/')[idx])
          );
          var childNamePath = showJobNamePath.split('/' + showJobNamePath.split('/')[idx] + '/')[1];
          $.each(childNamePath.split('/'), function (index, obj) {
            names.push(obj);
          });
        }
      } else if (showJobIdPath.startsWith('G')) {
        var idx = groupTypes.split(';').indexOf('G');
        var memberObjId = memberObjIds.split(';')[idx];
        if (showJobIdPath.indexOf(memberObjId) == -1) {
          // 匹配错误，该用户可能在2个及以上的公共群组内或者2个及以上的职务群组内
          $.each(memberObjIds.split(';'), function (i, obj) {
            if (showJobIdPath.indexOf(obj) > -1) {
              idx = i;
              memberObjId = memberObjIds.split(';')[idx];
              return;
            }
          });
        }
        if (showJobIdPath.endsWith(memberObjId)) {
          names.push(handleNamePath(showJobIdPath, showJobNamePath));
        } else {
          idx = showJobIdPath.split('/').indexOf(memberObjId);
          names.push(
            handleNamePath(showJobIdPath, showJobNamePath.split(showJobNamePath.split('/')[idx] + '/')[0] + showJobNamePath.split('/')[idx])
          );
          var childNamePath = showJobNamePath.split('/' + showJobNamePath.split('/')[idx] + '/')[1];
          if (childNamePath) {
            $.each(childNamePath.split('/'), function (index, obj) {
              names.push(obj);
            });
          }
        }
      } else {
        names = showJobNamePath.split('/');
        // names.push(handleNamePath(showJobIdPath, showJobNamePath));
      }
    } else {
      names = showJobNamePath.split('||||');
    }
    return names;
  }

  // 去掉组织版本和根节点的显示
  function handleNamePath(showJobIdPath, showJobNamePath) {
    if (showJobIdPath.startsWith('DU') || showJobIdPath.startsWith('G')) {
      var namePath = showJobNamePath.replace('/', '||||').split('||||')[1];
      if (namePath.split('/').length > 2) {
        namePath = namePath.substring(namePath.indexOf('/') + 1, namePath.length);
        namePath = namePath.substring(namePath.indexOf('/') + 1, namePath.length);
      }
      return namePath;
    } else {
      var namePath = showJobNamePath.substring(showJobNamePath.indexOf('/') + 1, showJobNamePath.length);
      namePath = namePath.substring(namePath.indexOf('/') + 1, namePath.length);
      return namePath;
    }
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
    for (var key in orgVersion) {
      orgVersionCount++;
    }
    return orgVersionCount > 1;
  }

  // 是否显示单位结点
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
    var userNode = getTreeNodeById(node.id, treeNodes, user.showJobIdPath);

    if (parentNode != null) {
      var children = parentNode.children || [];
      children.push(node);
      parentNode.children = children;
    } else {
      treeNodes.push(node);
    }
  }

  function createVersionTreeNodeIfRequired(id, name, eleIdPath, parentNode, treeNodes) {
    var node = getTreeNodeById(id, treeNodes, eleIdPath);
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
    var node = getTreeNodeById(id, treeNodes, eleIdPath);
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
    var node = getTreeNodeById(id, treeNodes, eleIdPath);
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
    var node = getTreeNodeById(id, treeNodes, eleIdPath);
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
    var node = getTreeNodeById(id, treeNodes, eleIdPath);
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
    var node = getTreeNodeById(id, treeNodes, eleIdPath);
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

  function getTreeNodeById(id, treeNodes, eleIdPath) {
    for (var i = 0; i < treeNodes.length; i++) {
      var treeNode = treeNodes[i];
      if (eleIdPath && treeNode.eleIdPath == eleIdPath) {
        return treeNode;
      }
      if (!eleIdPath && treeNode.id == id) {
        return treeNode;
      } else if (treeNode.children) {
        var node = getTreeNodeById(id, treeNode.children, eleIdPath);
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

  //	// 6、只能选择一个人办理,
  //	function OnlyChooseOneUser(faultData, options) {
  //		if (faultData.data.unitUser) {
  //			ChooseUnitUser(faultData, options);
  //			return;
  //		}
  //		var _self = this;
  //		var eData = faultData.data;
  //		var callback = options.callback;
  //		var callbackContext = options.callbackContext;
  //		var workFlow = options.workFlow;
  //		var workData = workFlow.getWorkData();
  //		var taskName = eData.taskName;
  //		var taskId = eData.taskId;
  //		var message = "<div class='only-choose-one-user'></div>";
  //		var isCancel = true;
  //		var dialogOptions = {
  //			title : "选择一个办理人" + "(" + taskName + ")",
  //			size : "middle",
  //			message : message,
  //			onEscape : function() {
  //				if (isCancel) {
  //					workFlow.clearTempData();
  //				}
  //			},
  //			shown : function() {
  //				var data = [];
  //				$.each(eData.users, function(index, object) {
  //					data.push({
  //						"id" : object.id,
  //						"text" : object.name
  //					});
  //				});
  //				var $container = $(".only-choose-one-user");
  //				// 手机端滚动处理
  //				$container = scrollForMobileApp(data, $container);
  //				formBuilder.buildSelect2({
  //					select2 : {
  //						data : data
  //					},
  //					container : $container,
  //					name : "onlyOneUser",
  //					display : "onlyOneUserName",
  //					labelColSpan : "0",
  //					controlColSpan : "12"
  //				});
  //			},
  //			buttons : {
  //				confirm : {
  //					label : "确定",
  //					className : "btn-primary",
  //					callback : function() {
  //						var $container = $(".only-choose-one-user");
  //						var userId = "";
  //						if (appContext.isMobileApp()) {
  //							var checkedSelector = "input[name=onlyOneUser]:checked";
  //							var $radio = $container[0].querySelectorAll(checkedSelector);
  //							if ($radio.length === 0 || $radio.length > 1) {
  //								appModal.error("请选择一个办理人!");
  //								return false;
  //							}
  //
  //							var userId = $radio[0].value;
  //							if (StringUtils.isBlank(userId)) {
  //								appModal.error("请选择一个办理人!");
  //								return false;
  //							}
  //						} else {
  //							userId = $("input[name=onlyOneUser]", $container).val();
  //							if (StringUtils.isBlank(userId)) {
  //								appModal.error("请选择一个办理人!");
  //								return false;
  //							}
  //						}
  //
  //						workData.taskUsers[taskId] = [ userId ];
  //						workFlow.setTempData("taskUsers", workData.taskUsers);
  //						isCancel = false;
  //						// 重新触发回调事件
  //						if ($.isFunction(callback)) {
  //							callback.call(callbackContext);
  //						}
  //						return true;
  //					}
  //				},
  //				cancel : {
  //					label : "取消",
  //					className : "btn-default",
  //					callback : function() {
  //						if (isCancel) {
  //							workFlow.clearTempData();
  //						}
  //					}
  //				}
  //			}
  //		};
  //		appModal.dialog(dialogOptions);
  //	}
  // 7、弹出环节选择框选择下一流程环节
  function JudgmentBranchFlowNotFound(faultData, options) {
    var _self = this;
    var eData = faultData.data;
    var callback = options.callback;
    var callbackContext = options.callbackContext;
    var workFlow = options.workFlow;
    var workData = workFlow.getWorkData();
    var toTasks = eData.toTasks;
    var multiselect = eData.multiselect;
    var message = "<div class='choose-next-task'></div>";
    var isCancel = true;
    var title = eData.useDirection ? '请选择流向' : '请选择环节';
    if (StringUtils.isNotBlank(eData.flowName)) {
      title += '(' + eData.flowName + ')';
    }
    var $nextTaskDialog = appModal.dialog({
      title: title,
      headerType: 'white',
      headerStyle: {
        height: '50px',
        textAlign: 'center'
      },
      titleStyle: {
        fontSize: '16px',
        fontWeight: 'bold'
      },
      width: '380px',
      bodyStyle: {
        padding: '0 15px',
        minHeight: 'unset'
      },
      draggable: false,
      dialogPosition: 'center',
      message: message,
      shown: function () {
        var data = [];
        $.each(toTasks, function (index, object) {
          // 使用流向
          if (eData.useDirection === true) {
            // 退回后允许提交退回环节
            if (object.returnAfterRollback == 'true') {
              data.push({
                id: 'returnAfterRollback_' + object.id,
                text: object.name,
                returnAfterRollback: object.returnAfterRollback
              });
            } else {
              data.push({
                id: object.directionId,
                text: object.name,
                returnAfterRollback: object.returnAfterRollback
              });
            }
          } else {
            data.push({
              id: object.id,
              text: object.name,
              returnAfterRollback: object.returnAfterRollback
            });
          }
        });
        var $container = $('.choose-next-task');
        // 手机端滚动处理
        $container = scrollForMobileApp(data, $container);
        var chooseType = 'radio';
        if (multiselect) {
          chooseType = 'checkbox';
        } else {
          // 流向单选隐藏确定按钮
          $('.modal-footer .btn-primary', $nextTaskDialog).hide();
        }
        if (appContext.isMobileApp()) {
          formBuilder.buildContent({
            container: $container,
            contentItems: [
              {
                type: chooseType,
                items: data,
                name: 'chooseNextTask',
                display: 'chooseNextTaskName',
                labelColSpan: '0',
                controlColSpan: '12'
              }
            ]
          });
        } else {
          $container.append('<input type="hidden" id="nextTaskValue">');
          $.each(data, function (i, item) {
            $container.append(
              '<div title="' +
                item.text +
                '" class="next-task-item well-btn well-btn-lg ' +
                (multiselect ? 'w-btn-checkbox inactive' : 'w-btn-radio') +
                ' w-btn-block w-btn-primary w-line-btn mb10 ellipsis" data-id="' +
                item.id +
                '">' +
                item.text +
                (item.returnAfterRollback == 'true' ? "<span class='rollback-task-tag'>退回环节</span>" : '') +
                '</div>'
            );
          });
          $('.next-task-item', $container).on('click', function () {
            if (multiselect) {
              $(this).toggleClass('hover');
              var _value = [];
              $('.next-task-item.hover', $container).each(function (i, item) {
                _value.push($(this).data('id'));
              });
              $nextTaskDialog.find('#nextTaskValue').val(_value.join(';'));
            } else {
              $nextTaskDialog.find('#nextTaskValue').val($(this).data('id'));
              $nextTaskDialog.find('.modal-footer .btn-primary').trigger('click');
            }
          });
        }
      },
      buttons: {
        confirm: {
          label: '确定',
          className: 'btn-primary',
          callback: function () {
            var $container = $('.choose-next-task');
            var checkedSelector = 'input[name=chooseNextTask]:checked';
            var $checkbox = null;
            if (appContext.isMobileApp()) {
              $checkbox = $container[0].querySelectorAll(checkedSelector);
            } else {
              $checkbox = $(checkedSelector, $container);
            }
            var toTaskId = '';
            if (appContext.isMobileApp()) {
              if ($checkbox.length === 0) {
                appModal.error('请选择环节!');
                return false;
              } else if (multiselect === false && $checkbox.length !== 1) {
                appModal.error('只能选择一个环节!');
                return false;
              }
              $.each($checkbox, function (i) {
                toTaskId += this.value;
                if (i !== $checkbox.length - 1) {
                  toTaskId += ';';
                }
              });
            } else {
              toTaskId = $nextTaskDialog.find('#nextTaskValue').val();
              if (!toTaskId) {
                appModal.error('请选择环节!');
                return false;
              }
            }
            workFlow.setTempData('fromTaskId', eData.fromTaskId);
            // 使用流向
            if (eData.useDirection === true) {
              // 退回后允许提交退回环节
              if (StringUtils.contains(toTaskId, 'returnAfterRollback_')) {
                toTaskId = toTaskId.substring('returnAfterRollback_'.length);
                workFlow.setTempData('toTaskId', toTaskId);
                var toTaskIds = workData.toTaskIds || {};
                toTaskIds[eData.fromTaskId] = toTaskId;
                workFlow.setTempData('toTaskIds', toTaskIds);
              } else {
                workFlow.setTempData('toDirectionId', toTaskId);
                var toDirectionIds = workData.toDirectionIds || {};
                toDirectionIds[eData.fromTaskId] = toTaskId;
                workFlow.setTempData('toDirectionIds', toDirectionIds);
              }
            } else {
              workFlow.setTempData('toTaskId', toTaskId);
              var toTaskIds = workData.toTaskIds || {};
              toTaskIds[eData.fromTaskId] = toTaskId;
              workFlow.setTempData('toTaskIds', toTaskIds);
            }
            isCancel = false;
            // 重新触发回调事件
            if ($.isFunction(callback)) {
              callback.call(callbackContext);
            }
            return true;
          }
        },
        cancel: {
          label: '取消',
          className: 'btn-default',
          callback: function () {
            if (isCancel) {
              workFlow.clearTempData();
            }
          }
        }
      }
    });
    // appModal.dialog(dialogOptions);
  }

  // 8、找到多个判断分支流向
  function MultiJudgmentBranch(faultData, options) {
    JudgmentBranchFlowNotFound(faultData, options);
  }

  // 9、弹出环节选择框选择下一子流程
  function SubFlowNotFound(faultData, options) {
    appModal.error('流程没有配置要发起的子流程!');
  }

  // 10、子流程合并等待异常类
  function SubFlowMerge(faultData, options) {
    var _self = this;
    var eData = faultData.data;
    var callback = options.callback;
    var callbackContext = options.callbackContext;
    var workFlow = options.workFlow;
    var workData = workFlow.getWorkData();
    var message = '<div>是否等待其他子流程合并!</div>';
    var isCancel = true;
    var dialogOptions = {
      title: '子流程合并等待',
      size: 'middle',
      message: message,
      buttons: {
        confirm: {
          label: '不等待',
          className: 'btn-primary',
          callback: function () {
            var waitForMerge = {};
            waitForMerge[eData.subFlowInstUuid] = false;
            workData.waitForMerge = waitForMerge;
            workFlow.setTempData('waitForMerge', workData.waitForMerge);
            isCancel = false;
            // 重新触发回调事件
            if ($.isFunction(callback)) {
              callback.call(callbackContext);
            }
            return true;
          }
        },
        cancel: {
          label: '等待',
          className: 'btn-default',
          callback: function () {
            if (isCancel) {
              workFlow.clearTempData();
            }
          }
        }
      }
    };
    appModal.dialog(dialogOptions);
  }

  // 11、用户没有权限访问流程
  function IdentityNotFlowPermission(faultData, options) {
    var eData = faultData.data;
    appModal.error(eData.msg);
  }

  // 12、找不到退回操作的退回环节异常类
  function RollbackTaskNotFound(faultData, options) {
    var _self = this;
    var eData = faultData.data;
    var callback = options.callback;
    var callbackContext = options.callbackContext;
    var workFlow = options.workFlow;
    var workData = workFlow.getWorkData();
    var toTasks = eData.rollbackTasks;
    var message = "<div class='choose-rollback-task'></div>";
    var isCancel = true;
    var dialogOptions = {
      title: '选择退回环节',
      size: 'middle',
      message: message,
      shown: function () {
        var data = [];
        $.each(toTasks, function (index, object) {
          data.push({
            id: object.id,
            text: object.name
          });
        });
        var $container = $('.choose-rollback-task');
        // 手机端滚动处理
        $container = scrollForMobileApp(data, $container);
        formBuilder.buildRadio({
          container: $container,
          items: data,
          name: 'chooseRollbackTask',
          display: 'chooseRollbackTaskName',
          labelColSpan: '0',
          controlColSpan: '12',
          isBlock: true
        });
      },
      buttons: {
        confirm: {
          label: '确定',
          className: 'btn-primary',
          callback: function () {
            var $container = $('.choose-rollback-task');
            var checkedSelector = 'input[name=chooseRollbackTask]:checked';
            var $checkbox = $container[0].querySelector(checkedSelector);
            if ($checkbox == null) {
              appModal.error('请选择环节!');
              return false;
            }
            var rollbackToTaskId = $checkbox.value;
            if (StringUtils.isBlank(rollbackToTaskId)) {
              appModal.error('请选择环节!');
              return false;
            }
            workData.rollbackToTaskId = rollbackToTaskId;
            // 设置对应的taskInstUuid
            for (var i = 0; i < toTasks.length; i++) {
              var toTask = toTasks[i];
              if (toTask.id === rollbackToTaskId) {
                workData.rollbackToTaskInstUuid = toTask.taskInstUuid;
                break;
              }
            }
            workFlow.setTempData('rollbackToTaskId', workData.rollbackToTaskId);
            workFlow.setTempData('rollbackToTaskInstUuid', workData.rollbackToTaskInstUuid);
            isCancel = false;
            // 重新触发回调事件
            if ($.isFunction(callback)) {
              callback.call(callbackContext);
            }
            return true;
          }
        },
        cancel: {
          label: '取消',
          className: 'btn-default',
          callback: function () {
            if (isCancel) {
              workFlow.clearTempData();
            }
          }
        }
      }
    };
    appModal.dialog(dialogOptions);
  }

  // 13、找不到特送环节操作的环节异常类
  function GotoTaskNotFound(faultData, options) {
    var _self = this;
    var eData = faultData.data;
    var callback = options.callback;
    var callbackContext = options.callbackContext;
    var workFlow = options.workFlow;
    var workData = workFlow.getWorkData();
    var toTasks = eData.toTasks;
    var message = "<div class='choose-goto-task'></div>";
    var isCancel = true;
    var dialogOptions = {
      title: '选择特送环节',
      size: 'middle',
      message: message,
      shown: function () {
        var data = [];
        $.each(toTasks, function (index, object) {
          data.push({
            id: object.id,
            text: object.name
          });
        });
        var $container = $('.choose-goto-task');
        // 手机端滚动处理
        $container = scrollForMobileApp(data, $container);
        formBuilder.buildRadio({
          container: $container,
          items: data,
          name: 'chooseGotoTask',
          display: 'chooseGotoTaskName',
          labelColSpan: '0',
          controlColSpan: '12',
          isBlock: true
        });
      },
      buttons: {
        confirm: {
          label: '确定',
          className: 'btn-primary',
          callback: function () {
            var $container = $('.choose-goto-task');
            var checkedSelector = 'input[name=chooseGotoTask]:checked';
            var $checkbox = $container[0].querySelector(checkedSelector);
            if ($checkbox == null) {
              appModal.error('请选择环节!');
              return false;
            }
            var gotoTaskId = $checkbox.value;
            if (StringUtils.isBlank(gotoTaskId)) {
              appModal.error('请选择环节!');
              return false;
            }
            workData.fromTaskId = eData.fromTaskId;
            workData.gotoTaskId = gotoTaskId;
            workFlow.setTempData('fromTaskId', workData.fromTaskId);
            workFlow.setTempData('gotoTaskId', workData.gotoTaskId);
            isCancel = false;
            // 重新触发回调事件
            if ($.isFunction(callback)) {
              callback.call(callbackContext);
            }
            return true;
          }
        },
        cancel: {
          label: '取消',
          className: 'btn-default',
          callback: function () {
            if (isCancel) {
              workFlow.clearTempData();
            }
          }
        }
      }
    };
    appModal.dialog(dialogOptions);
  }

  // 14、表单数据保存失败
  function SaveData(faultData, options) {
    var workView = options.callbackContext;
    if (workView && workView.getDyform && $.isFunction(workView.getDyform)) {
      var dyform = workView.getDyform();
      $.extend(options, {
        dyformDataOptions: function () {
          return dyform.dyformDataOptions();
        }
      });
      dyform.handleException(faultData.data, options);
    } else {
      appModal.error('表单数据保存失败！');
    }
  }

  // 15、必填域为空
  function RequiredFieldIsBlank(faultData, options) {
    if (faultData.data) {
      var dyformSelector = workView.getDyformSelector();
      $.each(faultData.data, function () {
        $(dyformSelector).dyform('setFieldEditableByFieldName', this.fieldName, this.dataUuid);
        $(dyformSelector).dyform('setFieldRequiredByFormUuid', this.fieldName, true, this.formUuid);
      });
      $(dyformSelector).dyform('validateForm');
    }
  }

  // 16、选择归档夹
  function ChooseArchiveFolder(faultData, options) {
    var callback = options.callback;
    var callbackContext = options.callbackContext;
    var workFlow = options.workFlow;
    var workData = workFlow.getWorkData();
    appContext.require(['DmsFileServices'], function (DmsFileServices) {
      var dmsFileServices = new DmsFileServices();
      var rootFolderUuids = faultData.data.rootFolderUuids;
      dmsFileServices.showChooseFolderDialog({
        title: '选择归档夹',
        rootFolderUuid: rootFolderUuids.join(';'),
        treeSetting: {
          view: {
            selectedMulti: true
          }
        },
        onOk: function (treeId, selectNodes) {
          var archiveFolderUuids = [];
          $.each(selectNodes, function (i, selectNode) {
            archiveFolderUuids.push(selectNode.id);
          });
          workData.archiveFolderUuid = archiveFolderUuids.join(';');
          // 重新触发回调事件
          if ($.isFunction(callback)) {
            callback.call(callbackContext);
          }
        }
      });
    });
  }

  // 一人多职未选择职位的情况下
  function MultiJobNotSelected(faultData, options) {
    var workFlow = options.workFlow;
    var callback1 = options.callback;
    var callbackContext = options.callbackContext;
    // 弹出职位选择
    appModal.dialog({
      title: '请选择职位发起流程',
      size: 'middle',
      draggable: true,
      buttons: {},
      backdrop: 'static',
      message: function () {
        var $div = $('<div>');
        var $ul = $('<ul>');
        for (var i = 0; i < faultData.data.jobs.length; i++) {
          var job = faultData.data.jobs[i];
          var text = '';
          if (job.parent) {
            // if(job.parent.parent){
            // 	text+=job.parent.parent.name +' - ';
            // }
            if (job.parent.id.indexOf('D') == 0) {
              text += job.parent.name + ' - ';
            }
          }
          text += job.name;
          $ul.append(
            $('<li>', {
              class: 'workflow-job-li-select',
              'data-jobid': job.id
            }).html('<label>' + text + '</label><i class="iconfont icon-ptkj-xiayigeqianjin"></i>')
          );
        }
        $div.append($ul);
        return $div[0].outerHTML;
      },
      // 显示弹出框后事件
      shown: function ($dialog) {
        $dialog.on('click', '.workflow-job-li-select', function () {
          workFlow.setTempData('jobSelected', $(this).data('jobid'));
          appModal.hide();
          // 重新触发回调事件
          if ($.isFunction(callback1)) {
            callback1.call(callbackContext);
          }
        });
        var i = 0;
        $dialog.on('keydown', function (e) {
          if (e.key === 'Tab' || e.which === 9 || e.keyCode === 9) {
            $dialog.find('.workflow-job-li-select-active').removeClass('workflow-job-li-select-active');
            $dialog.find('.workflow-job-li-select:eq(' + (i++ % 3) + ')').addClass('workflow-job-li-select-active');
            $dialog.focus();
          }
          if (e.key === 'Enter' || e.which === 13 || e.keyCode === 13) {
            $dialog.find('.workflow-job-li-select-active').click();
          }
        });
        $('.workflow-job-li-select', $dialog).hover(function () {
          $dialog.find('.workflow-job-li-select-active').removeClass('workflow-job-li-select-active');
        });
      }
    });
  }

  // 18、表单数据验证失败
  function FormDataValidateException(faultData, options) {
    if (typeof faultData.data == 'string') {
      appModal.error(faultData.data);
    } else if (typeof faultData.data == 'object') {
      var msg = new StringBuilder();
      msg.append(faultData.data.msg);
      if (faultData.data.errors) {
        var errorStrings = [];
        $.each(faultData.data.errors, function (i, error) {
          errorStrings.push(error.displayName + '(' + error.fieldName + ')' + error.msg);
        });
        msg.append(': ' + errorStrings.join('、'));
      }
      appModal.error(msg.toString());
    } else {
      Default(faultData, options);
    }
  }

  // 19、流程无法处理的异常
  function Default(faultData, options) {
    var msg = null;
    if (StringUtils.isNotBlank(faultData.msg)) {
      msg = faultData.msg;
    } else {
      msg = JSON.stringify(faultData);
    }
    appModal.error('工作流无法处理的未知异常：' + msg);
  }

  // 手机端radio、checkbox列表滚动
  function scrollForMobileApp(data, $container) {
    if (appContext.isMobileApp()) {
      var height = data.length * 37; // 40
      if (height > 500) {
        height = 500;
      }
      $container[0].innerHTML = '<div class="mui-scroll-wrapper"><div class="mui-scroll"></div></div>';
      if ($container[0].parentNode && $container[0].parentNode.parentNode) {
        $container[0].parentNode.parentNode.style.minHeight = height + 'px';
      } else if ($container[0].parentNode) {
        $container[0].parentNode.style.minHeight = height + 'px';
      } else {
        $container[0].style.minHeight = height + 'px';
      }
      $container[0].style.marginBottom = '-40px';
      $container[0].style.posotion = 'relative';
      var wrapper = $container[0].querySelector('.mui-scroll-wrapper');
      if (wrapper) {
        $(wrapper)
          .scroll({
            deceleration: 0.0005
          })
          .refresh();
        return $(wrapper.querySelector('.mui-scroll'));
      }
    }
    return $container;
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
    SubFlowMerge: SubFlowMerge, // 10、子流程合并等待
    IdentityNotFlowPermission: IdentityNotFlowPermission, // 11、用户没有权限访问流程
    RollbackTaskNotFound: RollbackTaskNotFound, // 12、找不到退回操作的退回环节异常类
    GotoTaskNotFound: GotoTaskNotFound, // 13、找不到特送环节操作的环节异常类
    SaveData: SaveData, // 14、表单数据保存失败
    RequiredFieldIsBlank: RequiredFieldIsBlank, // 15、必填域为空
    ChooseArchiveFolder: ChooseArchiveFolder, // 16、选择归档夹
    MultiJobNotSelected: MultiJobNotSelected, // 17、一人多职时候未选择职位
    FormDataValidateException: FormDataValidateException, // 18、表单数据验证失败
    TaskNotAssignedTransferUser: TaskNotAssignedTransferUser // 19、任务没有指定转办人
  };
  // 异常处理
  var WorkFlowErrorHandler = function (workView) {
    this.workView = workView;
    this.errorHandler = server.ErrorHandler.getInstance();
    this._init();
  };
  // 内部初始化默认的异常处理代码
  WorkFlowErrorHandler.prototype._init = function () {
    var _self = this;
    $.each(WorkFlowErrorCode, function (prop, value) {
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
  return WorkFlowErrorHandler;
});
