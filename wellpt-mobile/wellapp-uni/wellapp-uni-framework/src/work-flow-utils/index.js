"use strict";
import { get, isEmpty, assign, startsWith, lowerCase, isFunction, camelCase } from "lodash";
import constant from "./constant";
import utils from "../utils/index";
const pageUrl = "/packages/_/pages/workflow/work_view";
export default {
  pageUrl,
  options: {},
  initWorkDataSuccessCallBack: undefined,
  initWorkData: function (options, callback) {
    let _self = this;
    this.options = options;
    this.initWorkDataSuccessCallBack = callback;
    // 是否新建工作
    if (_self.isNewWork()) {
      this._new();
    } else if (_self.isDraft()) {
      // 草稿
      this.draft();
    } else if (_self.isTodo()) {
      this.todo();
    } else if (_self.isDone()) {
      this.done();
    } else if (_self.isOver()) {
      this.viewOver();
    } else if (_self.isAttention()) {
      this.attention();
    } else if (_self.isSupervise()) {
      this.viewSupervise();
    } else if (_self.isMonitor()) {
      this.viewMonitor();
    } else if (
      _self.options.aclRole &&
      _self.isWorkDataMatchAclRole(_self.options.aclRole) &&
      isFunction(_self[camelCase(_self.options.aclRole)])
    ) {
      _self[camelCase(_self.options.aclRole)]();
    } else {
      this.work();
    }
  },
  // 是否新建工作
  isNewWork() {
    if (isEmpty(this.options.taskInstUuid) && isEmpty(this.options.flowInstUuid)) {
      return true;
    }
    return false;
  },
  // 是否草稿
  isDraft() {
    return this.isWorkDataMatchAclRole("DRAFT");
  },
  // 是否待办工作
  isTodo() {
    return this.isWorkDataMatchAclRole("TODO");
  },
  // 是否已办工作
  isDone() {
    return this.isWorkDataMatchAclRole("DONE");
  },
  // 是否办结工作
  isOver() {
    return this.isWorkDataMatchAclRole("OVER");
  },
  // 是否关注工作
  isAttention() {
    return this.isWorkDataMatchAclRole("ATTENTION");
  },
  // 是否督办工作
  isSupervise() {
    return this.isWorkDataMatchAclRole("SUPERVISE");
  },
  // 是否监控工作
  isMonitor() {
    return this.isWorkDataMatchAclRole("MONITOR");
  },
  isWorkDataMatchAclRole(aclRole) {
    var _self = this;
    if (_self.workFlow == null) {
      return _self.options.aclRole == aclRole;
    }
    var workData = _self.workFlow.getWorkData();
    if (workData.aclRole === aclRole) {
      return true;
    }
    return false;
  },
  jsonDataService(serviceName, methodName, args) {
    return new Promise((resolve, reject) => {
      this.options.viewVue.$axios
        .post("/json/data/services", {
          serviceName: serviceName,
          methodName: methodName,
          args: JSON.stringify(args),
        })
        .then(({ data }) => {
          if (data.code == 403 || data.code == -7000) {
            uni.showModal({
              content: utils.$t("global.noPermission", "您无权限访问该页面，请联系管理员！"),
              showCancel: false,
              confirmText: utils.$t("global.confirm", "确认"),
              success: function (res) {
                if (res.confirm) {
                  uni.navigateBack({ delta: 1 });
                }
              },
            });
          } else if (data.errorCode === "SessionExpired") {
            this.redirectToLogin();
            // uni.setStorageSync("loginSuccessRedirect", "/packages/_/pages/welldo/index");
            // uni.reLaunch({
            //   url: "/packages/_/pages/login/login",
            // });
          } else if (data.code == 0) {
            resolve(data);
          }
        });
    });
  },
  axiosRequire(url, method = "get", params) {
    return new Promise((resolve, reject) => {
      if (method == "post") {
        this.options.viewVue.$axios
          .post(url, params)
          .then(({ data }) => {
            if (data.code == 403 || data.code == -7000) {
              uni.showModal({
                content: utils.$t("global.noPermission", "您无权限访问该页面，请联系管理员！"),
                showCancel: false,
                confirmText: utils.$t("global.confirm", "确认"),
                success: function (res) {
                  if (res.confirm) {
                    uni.navigateBack({ delta: 1 });
                  }
                },
              });
            } else if (data.errorCode === "SessionExpired") {
              this.redirectToLogin();
              // uni.setStorageSync("loginSuccessRedirect", "/packages/_/pages/welldo/index");
              // uni.reLaunch({
              //   url: "/packages/_/pages/login/login",
              // });
            } else if (data.code == 0) {
              resolve(data);
            } else {
              reject(data);
            }
          })
          .catch((error) => {
            reject(error);
          });
      } else {
        this.options.viewVue.$axios
          .get(url, {
            params,
          })
          .then(({ data }) => {
            if (data.code == 403 || data.code == -7000) {
              uni.showModal({
                content: utils.$t("global.noPermission", "您无权限访问该页面，请联系管理员！"),
                showCancel: false,
                confirmText: utils.$t("global.confirm", "确认"),
                success: function (res) {
                  if (res.confirm) {
                    uni.navigateBack({ delta: 1 });
                  }
                },
              });
            } else if (data.errorCode === "SessionExpired") {
              this.redirectToLogin();
              // uni.setStorageSync("loginSuccessRedirect", "/packages/_/pages/welldo/index");
              // uni.reLaunch({
              //   url: "/packages/_/pages/login/login",
              // });
            } else if (data.code == 0) {
              resolve(data);
            } else {
              reject(data);
            }
          })
          .catch((error) => {
            reject(error);
            if (error && error.response.status == "403") {
              uni.reLaunch({
                url: LOGIN_PAGE_PATH,
              });
            }
          });
      }
    });
  },
  redirectToLogin() {
    let loginSuccessRedirect = "/pages/welldo/index";
    if (window.location.href && window.location.href.includes("/packages")) {
      loginSuccessRedirect = window.location.href.substring(window.location.href.indexOf("/packages"));
    }
    if (loginSuccessRedirect && loginSuccessRedirect.includes("system")) {
      let systemPart = loginSuccessRedirect.substring(loginSuccessRedirect.indexOf("system"));
      let system = systemPart.split("=")[1];
      if (system && system.includes("&")) {
        system = system.substring(0, system.indexOf("&"));
      }
      uni.setStorageSync("system", system);
    }
    let source = "";
    if (loginSuccessRedirect && loginSuccessRedirect.includes("source")) {
      let sourcePart = loginSuccessRedirect.substring(loginSuccessRedirect.indexOf("source"));
      source = sourcePart.split("=")[1];
      if (source && source.includes("&")) {
        source = source.substring(0, source.indexOf("&"));
      }
      uni.setStorageSync("login_provider", source + "_sso");
    }
    uni.setStorageSync("loginSuccessRedirect", loginSuccessRedirect);
    uni.reLaunch({
      url: `${LOGIN_PAGE_PATH}?${source}_sso=true`,
    });
  },
  // 获取所有流程设置数据
  getSetting() {
    return this.options.viewVue.$axios.get(`/api/workflow/setting/list`).then(({ data: result }) => {
      let settings = {};
      result.data.forEach((item) => {
        let attrVal = JSON.parse(item.attrVal);
        attrVal.enabled = item.enabled;
        settings[item.attrKey] = attrVal;
      });
      return settings;
    });
  },
  /**
   * 发起工作流
   */
  async _new() {
    try {
      let newWorkUrl = "/api/workflow/work/getWorkData?flowDefId=" + this.options.flowDefId;
      if (this.options.formUuid) {
        newWorkUrl += "&formUuid=" + this.options.formUuid;
        if (this.options.dataUuid) {
          newWorkUrl += "&dataUuid=" + this.options.dataUuid;
        }
      }
      const result = await this.axiosRequire(newWorkUrl).catch((res) => {});
      const workBean = result.data;
      if (this.options.formUuid) {
        workBean.formUuid = this.options.formUuid;
        if (this.options.dataUuid) {
          workBean.dataUuid = this.options.dataUuid;
        }
      }
      for (const q in this.options) {
        if (startsWith(q, "ep_")) {
          workBean.extraParams[q] = this.options[q];
        }
      }
      this.workView(workBean);
    } catch (error) {
      console.error("%s", error);
    }
  },
  /**
   * 待办
   */
  async todo() {
    const userid = this.options.userId;
    let {
      taskUuid,
      taskInstUuid,
      taskIdentityUuid,
      flowInstUuid,
      openToRead,
      custom_script_url,
      auto_submit,
      sameUserSubmitTaskOperationUuid,
      supportsContinuousWorkView,
    } = this.options;
    openToRead = openToRead == undefined || openToRead === "" || openToRead === "true";
    if (taskUuid) {
      taskInstUuid = taskUuid;
      this.options.taskInstUuid = taskInstUuid;
    }

    // 权限检查
    if (taskInstUuid && !(await this.hasPermission(taskInstUuid, [constant.AclPermission.TODO.mask]))) {
      if (await this.hasPermission(taskInstUuid, [constant.AclPermission.UNREAD.mask])) {
        this.options.aclRole = "UNREAD";
        this.options.openToRead = openToRead;
        let url = pageUrl + "?aclRole=UNREAD&taskInstUuid=" + taskInstUuid + "&openToRead=" + openToRead;
        this.workView(undefined, {
          isReLoad: true,
          url: url,
        });
        return;
      } else if (await this.hasPermission(taskInstUuid, [constant.AclPermission.FLAG_READ.mask])) {
        this.options.aclRole = "FLAG_READ";
        let url = pageUrl + "?aclRole=FLAG_READ&taskInstUuid=" + taskInstUuid;
        this.workView(undefined, {
          isReLoad: true,
          url: url,
        });
        return;
      }
      this.options.UnauthenticateText = constant.UnauthenticateText;
      this.workView();
      return;
    }
    const result = await this.axiosRequire("/api/workflow/work/getTodoWorkData", "post", {
      taskInstUuid,
      flowInstUuid,
      loadDyFormData: true,
    });
    const workBean = result.data;
    // 当前任务办理人标识
    if (taskIdentityUuid) {
      workBean.taskIdentityUuid = taskIdentityUuid;
    }
    if (taskInstUuid) {
      this.markRead(taskInstUuid, userid);
    }
    for (const q in this.options) {
      if (startsWith(q, "ep_")) {
        workBean.extraParams[q] = this.options[q];
      }
    }
    workBean && workBean.extraParams && (workBean.extraParams.auto_submit = auto_submit);
    if (sameUserSubmitTaskOperationUuid) {
      const taskOperation = await this.getTaskOperation(sameUserSubmitTaskOperationUuid);
      if (taskOperation) {
        workBean.opinionLabel = taskOperation.opinionLabel;
        workBean.opinionValue = taskOperation.opinionValue;
        workBean.opinionText = taskOperation.opinionText;
        workBean.opinionFileIds = taskOperation.opinionFileIds;
      }
    }
    // 多人办理的情况下，判断提交方式
    workBean.multiSubmitType = await this.taskMultiUserSubmitType(workBean.flowDefUuid, workBean.taskId);
    await this.workView(workBean);
  },

  /**
   * 草稿
   */
  async draft() {
    const { flowInstUuid } = this.options;
    if (
      flowInstUuid &&
      !(await this.jsonDataService("flowService", "hasDraftPermission", [this.options.userId, flowInstUuid])).data
    ) {
      this.options.UnauthenticateText = constant.UnauthenticateText;
      this.workView();
      return;
    }

    const result = await this.axiosRequire(`/api/workflow/work/getWorkData?flowInstUuid=${flowInstUuid}`);
    const workBean = result.data;
    for (const q in this.options) {
      if (startsWith(q, "ep_")) {
        workBean.extraParams[q] = this.options[q];
      }
    }
    await this.workView(workBean);
  },

  /**
   * 已办
   */
  async done() {
    let { taskUuid, taskInstUuid, flowInstUuid } = this.options;
    if (taskUuid) {
      taskInstUuid = taskUuid;
      this.options.taskInstUuid = taskInstUuid;
    }
    if (taskInstUuid) {
      this.markRead(taskInstUuid, this.options.userId);
    }

    if (taskInstUuid && !(await this.hasPermission(taskInstUuid, [constant.AclPermission.DONE.mask]))) {
      this.options.UnauthenticateText = constant.UnauthenticateText;
      this.workView();
      return;
    }

    const workBean = await this.getDoneWorkData(taskInstUuid, flowInstUuid);
    await this.workView(workBean);
  },

  /**
   * 关注
   */
  async attention() {
    const { taskInstUuid, flowInstUuid } = this.options;
    if (taskInstUuid && !(await this.hasPermission(taskInstUuid, [constant.AclPermission.ATTENTION.mask]))) {
      this.work();
    } else {
      const result = await this.axiosRequire(
        `/api/workflow/work/getAttentionWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`
      );
      const workBean = result.data;
      this.workView(workBean);
    }
  },
  /**
   * 未阅
   */
  async unread() {
    let { taskInstUuid, taskUuid, flowInstUuid, openToRead } = this.options;
    openToRead = openToRead == undefined || openToRead === "" || openToRead === "true";
    if (taskUuid) {
      taskInstUuid = taskUuid;
      this.options.taskInstUuid = taskInstUuid;
    }
    if (taskInstUuid && !(await this.hasPermission(taskInstUuid, [constant.AclPermission.UNREAD.mask]))) {
      if (await this.hasPermission(taskInstUuid, [constant.AclPermission.FLAG_READ.mask])) {
        this.options.aclRole = "FLAG_READ";
        let url = pageUrl + "?aclRole=FLAG_READ&taskInstUuid=" + taskInstUuid;
        this.workView(undefined, {
          isReLoad: true,
          url: url,
        });
        return;
      }
      this.options.UnauthenticateText = constant.UnauthenticateText;
      this.workView();
      return;
    }
    const result = await this.axiosRequire(
      `/api/workflow/work/getUnreadWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}&openToRead=${openToRead}`
    );
    const workBean = result.data;
    if (taskInstUuid) {
      this.markRead(taskInstUuid, this.options.userId);
    }
    await this.workView(workBean);
  },

  /**
   * 已阅
   */
  async flagRead() {
    let { taskInstUuid, taskUuid, flowInstUuid } = this.options;
    if (taskUuid) {
      taskInstUuid = taskUuid;
      this.options.taskInstUuid = taskInstUuid;
    }
    if (taskInstUuid) {
      if (!(await this.hasPermission(taskInstUuid, [constant.AclPermission.FLAG_READ.mask]))) {
        if (await this.hasPermission(taskInstUuid, [constant.AclPermission.UNREAD.mask])) {
          this.options.aclRole = "UNREAD";
          let url = pageUrl + "?aclRole=UNREAD&taskInstUuid=" + taskInstUuid;
          this.workView(undefined, {
            isReLoad: true,
            url: url,
          });
          return;
        }
        this.options.UnauthenticateText = constant.UnauthenticateText;
        this.workView();
        return;
      }
      const userid = this.options.userId;
      this.jsonDataService("aclService", "removePermission", [
        taskInstUuid,
        constant.AclPermission.UNREAD.mask,
        userid,
      ]);
      this.jsonDataService("aclService", "addPermission", [
        "com.wellsoft.pt.bpm.engine.entity.TaskInstance",
        taskInstUuid,
        constant.AclPermission.FLAG_READ.mask,
        userid,
      ]);
      this.markRead(taskInstUuid, userid);
    }

    const result = await this.axiosRequire(
      `/api/workflow/work/getReadWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`
    );
    const workBean = result.data;
    await this.workView(workBean);
  },

  async viewOver() {
    let { taskInstUuid, taskUuid, flowInstUuid } = this.options;
    if (taskUuid) {
      taskInstUuid = taskUuid;
      this.options.taskInstUuid = taskInstUuid;
    }
    if (taskInstUuid && !(await this.hasPermission(taskInstUuid, [constant.AclPermission.DONE.mask]))) {
      this.options.UnauthenticateText = constant.UnauthenticateText;
      this.workView();
      return;
    }

    const result = await this.axiosRequire(
      `/api/workflow/work/getOverWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`
    );
    const workBean = result.data;
    await this.workView(workBean);
  },

  async viewSupervise() {
    let { taskInstUuid, taskUuid, flowInstUuid } = this.options;
    if (taskUuid) {
      taskInstUuid = taskUuid;
      this.options.taskInstUuid = taskInstUuid;
    }
    if (
      taskInstUuid &&
      !(await this.jsonDataService("taskService", "hasSupervisePermissionCurrentUser", [taskInstUuid])).data
    ) {
      this.options.UnauthenticateText = constant.UnauthenticateText;
      this.workView();
      return;
    }

    const result = await this.axiosRequire(
      `/api/workflow/work/getSuperviseWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`
    );
    const workBean = result.data;
    await this.workView(workBean);
  },

  async viewMonitor() {
    let { taskInstUuid, taskUuid, flowInstUuid } = this.options;
    if (taskUuid) {
      taskInstUuid = taskUuid;
      this.options.taskInstUuid = taskInstUuid;
    }
    if (
      taskInstUuid &&
      !(await this.jsonDataService("taskService", "hasMonitorPermissionCurrentUser", [taskInstUuid])).data
    ) {
      this.options.UnauthenticateText = constant.UnauthenticateText;
      this.workView();
      return;
    }
    const result = await this.axiosRequire(
      `/api/workflow/work/getMonitorWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`
    );
    const workBean = result.data;
    for (const q in this.options) {
      if (startsWith(q, "ep_")) {
        workBean.extraParams[q] = this.options[q];
      }
    }
    await this.workView(workBean);
  },

  async workView(workBean, reload) {
    let _self = this;
    if (!workBean) {
      if (reload) {
        this.initWorkDataSuccessCallBack(reload);
        return false;
      }
      this.initWorkDataSuccessCallBack(_self.options);
    } else {
      let system = this._$SYSTEM_ID;
      _self.getSetting().then((settings) => {
        let defaultContinuousWorkView = settings && settings.GENERAL && settings.GENERAL.defaultContinuousWorkView;
        let options = assign(
          {},
          {
            system,
            workBean,
            settings,
          },
          _self.options
        );
        if (defaultContinuousWorkView && this.options.continuousMode != "0" && workBean.aclRole === "TODO") {
          options.continuousMode = "1";
        }
        this.initWorkDataSuccessCallBack(options);
      });
    }
  },

  async subflowShare() {
    const { taskInstUuid, flowInstUuid, belongToFlowInstUuid } = this.options;
    const taskInstanceUuid = (
      await this.jsonDataService("taskService", "getLastTaskInstanceUuidByFlowInstUuid", [flowInstUuid])
    ).data;
    if (taskInstanceUuid) {
      if ((await this.jsonDataService("taskService", "hasViewPermissionCurrentUser", [taskInstanceUuid])).data) {
        this.work();
      } else {
        if ((await this.jsonDataService("taskSubFlowService", "isShare", [belongToFlowInstUuid, flowInstUuid])).data) {
          const workBean = await this.getReadWorkData(taskInstUuid, flowInstUuid);
          await this.workView(workBean);
        } else {
          this.options.UnauthenticateText = constant.UnauthenticateText;
          this.workView();
        }
      }
    }
  },

  async workflowSimulation() {
    let uuid = this.options.uuid;
    let flowDefinition = {};
    if (uuid) {
      const flowUrl = `/api/workflow/scheme/flow/json.action?uuid=${uuid}`;
      try {
        const result = await this.axiosRequire(flowUrl);
        if (result.data) {
          flowDefinition = result.data;
        }
      } catch (error) {
        console.error("%s", error);
      }
    }
    // await ctx.render("workflow-simulation/index.js", {
    //   uuid,
    //   flowDefinition,
    // });
  },
  async work() {
    let _this = this;
    let {
      viewTheMainFlow,
      taskInstUuid,
      flowInstUuid,
      taskIdentityUuid,
      approveFlowInstUuid,
      openToRead,
      custom_script_url,
      allowOperate,
      auto_submit,
      _requestCode,
      supportsContinuousWorkView,
    } = this.options;
    openToRead = openToRead == undefined || openToRead === "" || openToRead === "true";
    allowOperate = allowOperate == undefined || allowOperate === "" || allowOperate === "true";
    _this.options.aclRole = _this.options.aclRole || "DRAFT";
    const requestCodeParamUri = "&_requestCode=" + (_requestCode || utils.generateId());
    let draftUrl = pageUrl + "?aclRole=DRAFT&flowInstUuid=" + flowInstUuid + requestCodeParamUri;
    for (const q in this.options) {
      if (startsWith(q, "ep_")) {
        draftUrl += "&" + q + "=" + this.options[q];
      }
    }
    if (taskInstUuid) {
      const taskInstanceUuid = (
        await _this.jsonDataService("taskService", "getLastTaskInstanceUuidByTaskInstUuidAndFlowInstUuid", [
          taskInstUuid,
          flowInstUuid,
        ])
      ).data;
      if (!taskInstanceUuid) {
        this.workView(undefined, {
          isReLoad: true,
          url: draftUrl,
        });
        return;
      }
      taskInstUuid = taskInstanceUuid;
    }
    if (!taskInstUuid && flowInstUuid) {
      const unfinishedTaskUuidsData = await _this.jsonDataService("taskService", "getUnfinishedTaskUuids", [
        flowInstUuid,
      ]);
      const unfinishedTaskUuids = unfinishedTaskUuidsData.data;
      let taskInstanceUuid = null;
      if (unfinishedTaskUuids && unfinishedTaskUuids.length !== 0) {
        taskInstanceUuid = unfinishedTaskUuids[0];
      } else {
        taskInstanceUuid = (
          await _this.jsonDataService("taskService", "getLastTaskInstanceUuidByFlowInstUuid", [flowInstUuid])
        ).data;
        taskInstanceUuid = taskInstanceUuid;
      }

      if (taskInstanceUuid == null) {
        this.workView(undefined, {
          isReLoad: true,
          url: draftUrl,
        });
        return;
      }

      taskInstUuid = taskInstanceUuid;
    }

    let permissions = await _this.jsonDataService("taskService", "getCurrentUserPermissions", [taskInstUuid, null]);
    let permissionMasks = (permissions.data && permissions.data.map((permission) => permission.mask)) || [];

    if (permissionMasks.includes(constant.AclPermission.TODO.mask)) {
      _this.options.aclRole = "TODO";
      this.options.allowOperate = allowOperate || "";
      let todoUrl = pageUrl + "?aclRole=TODO&flowInstUuid=" + flowInstUuid + requestCodeParamUri;
      todoUrl += "&taskInstUuid=" + (taskInstUuid || "");
      todoUrl += "&taskIdentityUuid=" + (taskIdentityUuid || "");
      // todoUrl += '&custom_script_url=' + (custom_script_url || '');
      todoUrl += "&allowOperate=" + (allowOperate || "");
      if (this.options.isXGWD) {
        todoUrl += "&isXGWD=" + this.options.isXGWD;
      }
      for (const q in this.options) {
        if (startsWith(q, "ep_")) {
          todoUrl += "&" + q + "=" + this.options[q];
        }
      }
      this.workView(undefined, {
        isReLoad: true,
        url: todoUrl,
      });
      return;
    }
    let workBean = null;
    let sendContent = null;
    if (viewTheMainFlow === "true") {
      workBean = await this.getReadWorkData(taskInstUuid, flowInstUuid);
    } else {
      if (permissionMasks.includes(constant.AclPermission.SUPERVISE.mask)) {
        workBean = await this.getSuperviseWorkData(taskInstUuid, flowInstUuid);
      } else if (permissionMasks.includes(constant.AclPermission.MONITOR.mask)) {
        workBean = await this.getMonitorWorkData(taskInstUuid, flowInstUuid);
      } else if (permissionMasks.includes(constant.AclPermission.DONE.mask)) {
        workBean = await this.getDoneWorkData(taskInstUuid, flowInstUuid, taskIdentityUuid);
      } else if (
        permissionMasks.includes(constant.AclPermission.UNREAD.mask) ||
        permissionMasks.includes(constant.AclPermission.FLAG_READ.mask)
      ) {
        workBean = await this.getReadWorkData(taskInstUuid, flowInstUuid, taskIdentityUuid);
      } else if (
        permissionMasks.includes(constant.AclPermission.ATTENTION.mask) ||
        permissionMasks.includes(constant.AclPermission.READ.mask)
      ) {
        workBean = await this.getAttentionWorkData(taskInstUuid, flowInstUuid);
      } else {
        if (approveFlowInstUuid) {
          const flowInstanceParameter = {
            flowInstUuid: approveFlowInstUuid,
            name: "custom_rt_sentContent",
          };
          const flowInstanceParameters = (
            await _this.jsonDataService("flowService", "findFlowInstanceParameter", [flowInstanceParameter])
          ).data;
          if (flowInstanceParameters && flowInstanceParameters.length === 1) {
            sendContent = flowInstanceParameters[0].value;
            if (sendContent.indexOf(flowInstUuid) != -1) {
              if (
                (
                  await _this.jsonDataService("flowService", "hasDraftPermission", [
                    this.options.userId,
                    approveFlowInstUuid,
                  ])
                ).data
              ) {
                workBean = await this.getReadWorkData(taskInstUuid, flowInstUuid);
              } else {
                const taskInstanceUuid = (
                  await _this.jsonDataService("taskService", "getLastTaskInstanceUuidByFlowInstUuid", [
                    approveFlowInstUuid,
                  ])
                ).data;
                const hasViewPermissionCurrentUser = (
                  await _this.jsonDataService("taskService", "hasViewPermissionCurrentUser", [taskInstanceUuid])
                ).data;
                if (taskInstanceUuid && hasViewPermissionCurrentUser) {
                  workBean = await this.getReadWorkData(taskInstUuid, flowInstUuid);
                }
              }
            }
          }
        }
      }

      if (!workBean) {
        this.options.UnauthenticateText = constant.UnauthenticateText;
        this.workView();
        return;
      }
    }

    workBean.taskIdentityUuid = taskIdentityUuid;
    this.markRead(taskInstUuid, this.options.userId);
    for (const q in this.options) {
      if (startsWith(q, "ep_")) {
        workBean.extraParams[q] = this.options[q];
      }
    }
    if (!allowOperate) {
      workBean.buttons = [];
    }
    await this.workView(workBean);
  },
  async hasPermission(taskInstUuid, masks) {
    try {
      const result = await this.jsonDataService("taskService", "hasPermissionCurrentUser", [taskInstUuid, masks]);
      if (result.data && result.code === 0) {
        return result.data;
      }
    } catch (error) {
      console.error("%s", error);
    }
    return false;
  },

  async hasViewPermission(taskInstUuid) {
    try {
      const result = await this.jsonDataService("taskService", "hasViewPermissionCurrentUser", [taskInstUuid]);
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      console.error("%s", error);
    }
    return false;
  },

  async getWorkBean(taskInstUuid, flowInstUuid, methodName) {
    try {
      const result = await this.jsonDataService("workService", methodName, [taskInstUuid, flowInstUuid]);
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      console.error("%s", error);
    }
    return null;
  },

  async getDoneWorkData(taskInstUuid, flowInstUuid, taskIdentityUuid = "") {
    const result = await this.axiosRequire(
      `/api/workflow/work/getDoneWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}&taskIdentityUuid=${taskIdentityUuid}`
    ).catch((res) => {});
    const workBean = result ? result.data : null;
    return workBean;
  },

  async getReadWorkData(taskInstUuid, flowInstUuid) {
    const result = await this.axiosRequire(
      `/api/workflow/work/getReadWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`
    ).catch((res) => {});
    const workBean = result.data;
    return workBean;
  },

  async getAttentionWorkData(taskInstUuid, flowInstUuid) {
    const result = await this.axiosRequire(
      `/api/workflow/work/getAttentionWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`
    ).catch((res) => {});
    const workBean = result ? result.data : null;
    return workBean;
  },

  async getMonitorWorkData(taskInstUuid, flowInstUuid) {
    const result = await this.axiosRequire(
      `/api/workflow/work/getMonitorWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`
    ).catch((res) => {});
    const workBean = result ? result.data : null;
    return workBean;
  },

  async getSuperviseWorkData(taskInstUuid, flowInstUuid) {
    const result = await this.axiosRequire(
      `/api/workflow/work/getSuperviseWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`
    ).catch((res) => {});
    const workBean = result ? result.data : null;
    return workBean;
  },

  async getTaskOperation(taskOperationUuid) {
    try {
      const result = await this.jsonDataService("workService", "getTaskOperation", [taskOperationUuid]);
      if (result.data && result.code === 0) {
        return result.data;
      }
    } catch (error) {
      console.error("%s", error);
    }
    return null;
  },

  async taskMultiUserSubmitType(flowDefinitionUuid, taskId) {
    try {
      const result = await this.axiosRequire("/api/workflow/definition/taskMultiUserSubmitType", "get", {
        taskId,
        flowDefinitionUuid,
      }).catch((res) => {});
      if (result.data && result.code === 0) {
        return result.data;
      }
      return null;
    } catch (error) {
      console.error("%s", error);
    }
    return null;
  },
  markRead(dataUuid, userid) {
    this.jsonDataService("readMarkerService", "markRead", [dataUuid, userid]);
  },
  onPerformedResult: function (result) {
    const _self = this;
    // 关闭窗口
    let close = result.close;
    // 刷新窗口
    let refresh = result.refresh;
    // 刷新父窗口
    let refreshParent = result.refreshParent;
    // 返回的数据
    let resultData = result.data;
    // 要附加的URL参数，存在替换，不存在附加
    let appendUrlParams = result.appendUrlParams;
    // 操作结果提示
    let message = result.message;
    // 操作结果提示类型
    // let msgType = result.msgType;
    // 触发的事件
    let triggerEvents = result.triggerEvents;

    // 提示信息
    if (message || result.showMsg) {
      uni.hideLoading();
      setTimeout(() => {
        let duration = result.duration ? result.duration : 2000;
        const title =
          message || utils.$t("global.successText", { name: utils.$t("global.operation", "操作") }, "操作成功");
        // #ifndef APP-PLUS
        uni.showToast({
          title,
          duration: duration,
          icon: result.msgIcon || "success",
        });
        setTimeout(() => {
          if (isFunction(result.msgCallback)) {
            result.msgCallback();
          }
        }, duration);
        // #endif
        // #ifdef APP-PLUS
        uni.$emit("nToast", title);
        // #endif
      }, 10);
    }
    // 刷新父窗口
    if (refreshParent) {
      // 刷新视图列表
      uni.$emit("refresh", result);
    }
    // 关闭当前窗口
    if (close) {
      // 显示提示信息时，延时2秒关闭当前窗口
      setTimeout(
        function () {
          uni.navigateBack({
            delta: 1,
          });
        },
        message || result.showMsg ? (result.duration ? result.duration : 2000) : 0
      );
    } else {
      // 刷新当前窗口
      if (refresh && _self.refresh) {
        _self.refresh(appendUrlParams);
      }
    }
    // 触发事件
    if (!isEmpty(triggerEvents)) {
      each(triggerEvents, function (triggerEvent) {
        _self.$emit(triggerEvent, resultData);
      });
    }
  },
};
