'use strict';

const Controller = require('wellapp-framework').Controller;
const lodash = require('lodash');
const constant = require('./constant');

class WorkflowVueController extends Controller {
  /*
  工作流程设计器
  */
  async workflowDesigner() {
    const { ctx, app } = this;
    let uuid = '',
      userDetails = null,
      workFlowData = null;
    if (ctx.query.uuid == undefined && ctx.query.id) {
      const flowDefinition = await ctx.service.jsonDataService._call('flowService', 'getFlowDefinitionById', [ctx.query.id]);

      ctx.redirect('/workflow-designer/index?uuid=' + flowDefinition.uuid);
      return;
    }

    if (ctx.query.uuid) {
      uuid = ctx.query.uuid;
      const flowUrl = `/api/workflow/scheme/flow/json.action?uuid=${uuid}`;
      try {
        const result = await ctx.curl(app.config.backendURL + flowUrl, {
          method: 'get',
          contentType: 'json',
          dataType: 'json'
        });
        if (result.data) {
          workFlowData = result.data;
        }
      } catch (error) {
        app.logger.error('%s', error);
      }
    }
    if (uuid && !workFlowData) {
      await ctx.renderError('404')
      return
    }
    if (ctx.user) {
      userDetails = ctx.user;
    }
    await ctx.render('workflow-designer/index.js', {
      uuid,
      userDetails,
      workFlowData
    });
  }
  /* 工作流程查阅 */
  async workflowViewer() {
    const { ctx, app } = this;
    let uuid = '',
      workFlowData = null;
    if (ctx.query.uuid) {
      uuid = ctx.query.uuid;
      const flowUrl = `/api/workflow/scheme/flow/json.action?uuid=${uuid}`;
      try {
        const result = await ctx.curl(app.config.backendURL + flowUrl, {
          method: 'get',
          contentType: 'json',
          dataType: 'json'
        });
        if (result.data) {
          workFlowData = result.data;
        }
      } catch (error) {
        app.logger.error('%s', error);
      }
    }
    const showHeader = ctx.query.showHeader === 'false' ? false : true;
    const flowInstUuid = ctx.query.flowInstUuid ? ctx.query.flowInstUuid : '';

    await ctx.render('workflow-designer/viewer.js', {
      uuid,
      workFlowData,
      showHeader,
      flowInstUuid
    });
  }
  /**
   * 发起工作流
   */
  async _new() {
    const { ctx, app } = this;
    try {
      let newWorkUrl = '/api/workflow/work/getWorkData?flowDefId=' + ctx.params.id;
      if (ctx.query.formUuid) {
        newWorkUrl += '&formUuid=' + ctx.query.formUuid;
        if (ctx.query.dataUuid) {
          newWorkUrl += '&dataUuid=' + ctx.query.dataUuid;
        }
      }
      const result = await ctx.curl(app.config.backendURL + newWorkUrl, {
        method: 'get',
        contentType: 'json',
        dataType: 'json'
      });
      const workBean = result.data && result.data.data;
      if (ctx.query.formUuid) {
        workBean.formUuid = ctx.query.formUuid;
        if (ctx.query.dataUuid) {
          workBean.dataUuid = ctx.query.dataUuid;
        }
      }

      for (const q in ctx.query) {
        if (lodash.startsWith(q, 'ep_')) {
          workBean.extraParams[q] = ctx.query[q];
        }
      }

      await this.workView(workBean);
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  /**
   * 待办
   */
  async todo() {
    const ctx = this.ctx;
    const app = this.app;
    const userid = ctx.user.userId;
    let {
      taskUuid,
      taskInstUuid,
      taskIdentityUuid,
      flowInstUuid,
      openToRead,
      custom_script_url,
      auto_submit,
      sameUserSubmitTaskOperationUuid
    } = ctx.query;
    openToRead = openToRead == undefined || openToRead === '' || openToRead === 'true';
    if (taskUuid) {
      taskInstUuid = taskUuid;
    }

    // 权限检查
    if (taskInstUuid && !(await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.TODO.mask]))) {
      if (await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.UNREAD.mask])) {
        ctx.redirect(this.addSystemPrefix('/workflow/work/view/unrend?taskInstUuid' + taskInstUuid + '&openToRead=' + openToRead));
        return;
      } else if (await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.FLAG_READ.mask])) {
        ctx.redirect(this.addSystemPrefix('/workflow/work/view/read?taskInstUuid' + taskInstUuid));
        return;
      }
      await ctx.renderError('403', 'SystemErrorCode.UnauthorizedAccessToData', 'SystemErrorCode.PleaseUseAuthorizedUserToAccessData');;
      return;
    }
    const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getTodoWorkData', {
      method: 'post',
      contentType: 'json',
      dataType: 'json',
      data: { taskInstUuid, flowInstUuid, loadDyFormData: true }
    });
    const workBean = result.data && result.data.data; // await ctx.service.workflowTaskService.getWorkBean(taskInstUuid, flowInstUuid, 'getTodo');
    // 当前任务办理人标识
    if (taskIdentityUuid) {
      workBean.taskIdentityUuid = taskIdentityUuid;
    }
    if (taskInstUuid) {
      ctx.service.readMarkerService.markRead(taskInstUuid, userid);
    }
    // if (custom_script_url) {
    //   workBean.extraParams.custom_script_url = custom_script_url;
    // }
    for (const q in ctx.query) {
      if (lodash.startsWith(q, 'ep_')) {
        workBean.extraParams[q] = ctx.query[q];
      }
    }
    workBean && workBean.extraParams && (workBean.extraParams.auto_submit = auto_submit);
    if (sameUserSubmitTaskOperationUuid) {
      const taskOperation = await ctx.service.workflowTaskService.getTaskOperation(sameUserSubmitTaskOperationUuid);
      if (taskOperation) {
        workBean.opinionLabel = taskOperation.opinionLabel;
        workBean.opinionValue = taskOperation.opinionValue;
        workBean.opinionText = taskOperation.opinionText;
        workBean.opinionFileIds = taskOperation.opinionFileIds;
      }
    }
    // 多人办理的情况下，判断提交方式
    if (!workBean.multiSubmitType) {
      workBean.multiSubmitType = await ctx.service.workflowTaskService.taskMultiUserSubmitType(workBean.flowDefUuid, workBean.taskId);
    }
    await this.workView(workBean);
  }

  /**
   * 草稿
   */
  async draft() {
    const { ctx, app } = this;
    const { flowInstUuid } = ctx.query;
    if (flowInstUuid && !(await ctx.service.jsonDataService._call('flowService', 'hasDraftPermission', [ctx.user.userId, flowInstUuid]))) {
      await ctx.renderError('403', 'SystemErrorCode.UnauthorizedAccessToData', 'SystemErrorCode.PleaseUseAuthorizedUserToAccessData');;
      return;
    }

    const result = await ctx.curl(app.config.backendURL + `/api/workflow/work/getWorkData?flowInstUuid=${flowInstUuid}`, {
      method: 'get',
      contentType: 'json',
      dataType: 'json'
    });
    const workBean = result.data && result.data.data;
    // const workBean = await ctx.service.jsonDataService._call('workService', 'getDraft', [flowInstUuid]);
    for (const q in ctx.query) {
      if (lodash.startsWith(q, 'ep_')) {
        workBean.extraParams[q] = ctx.query[q];
      }
    }
    await this.workView(workBean);
  }

  /**
   * 已办
   */
  async done() {
    const ctx = this.ctx;
    const app = this.app;
    let { taskUuid, taskInstUuid, flowInstUuid } = ctx.query;
    if (taskUuid) {
      taskInstUuid = taskUuid;
    }
    if (taskInstUuid) {
      ctx.service.readMarkerService.markRead(taskInstUuid, ctx.user.userId);
    }

    if (taskInstUuid && !(await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.DONE.mask]))) {
      await ctx.renderError('403', 'SystemErrorCode.UnauthorizedAccessToData', 'SystemErrorCode.PleaseUseAuthorizedUserToAccessData');
      return;
    }

    const workBean = await ctx.service.workflowTaskService.getDoneWorkData(taskInstUuid, flowInstUuid);
    // const workBean = await ctx.service.workflowTaskService.getWorkBean(taskInstUuid, flowInstUuid, 'getDone');
    await this.workView(workBean);
  }

  /**
   * 关注
   */
  async attention() {
    const ctx = this.ctx;
    const app = this.app;
    const { taskInstUuid, flowInstUuid } = ctx.query;
    if (taskInstUuid && !(await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.ATTENTION.mask]))) {
      ctx.redirect(this.addSystemPrefix('/workflow/work/view/work?taskInstUuid' + taskInstUuid + '&flowInstUuid=' + flowInstUuid));
    } else {
      const config = ctx.service.workflowTaskService.getReqGetConfig();
      const result = await ctx.curl(
        app.config.backendURL + `/api/workflow/work/getAttentionWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`,
        config
      );
      const workBean = result.data && result.data.data;
      // const workBean = await ctx.service.workflowTaskService.getWorkBean(taskInstUuid, flowInstUuid, 'getAttention');
      await this.workView(workBean);
    }
  }

  async work() {
    const ctx = this.ctx;
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
      _requestCode
    } = ctx.query;
    openToRead = openToRead == undefined || openToRead === '' || openToRead === 'true';
    allowOperate = allowOperate == undefined || allowOperate === '' || allowOperate === 'true';
    const requestCodeParamUri = '&_requestCode=' + _requestCode;
    let draftUrl = '/workflow/work/view/draft?flowInstUuid=' + flowInstUuid + requestCodeParamUri;
    for (const q in ctx.query) {
      if (lodash.startsWith(q, 'ep_')) {
        draftUrl += '&' + q + '=' + ctx.query[q];
      }
    }
    if (taskInstUuid) {
      const taskInstanceUuid = await ctx.service.jsonDataService._call(
        'taskService',
        'getLastTaskInstanceUuidByTaskInstUuidAndFlowInstUuid',
        [taskInstUuid, flowInstUuid]
      );
      if (!taskInstanceUuid) {
        ctx.redirect(this.addSystemPrefix(draftUrl));
        return;
      }
      taskInstUuid = taskInstanceUuid;
    }
    if (!taskInstUuid && flowInstUuid) {
      const unfinishedTaskUuids = await ctx.service.jsonDataService._call('taskService', 'getUnfinishedTaskUuids', [flowInstUuid]);
      let taskInstanceUuid = null;
      if (unfinishedTaskUuids && unfinishedTaskUuids.length !== 0) {
        taskInstanceUuid = unfinishedTaskUuids[0];
      } else {
        taskInstanceUuid = await ctx.service.jsonDataService._call('taskService', 'getLastTaskInstanceUuidByFlowInstUuid', [flowInstUuid]);
      }

      if (taskInstanceUuid == null) {
        ctx.redirect(this.addSystemPrefix(draftUrl));
        return;
      }

      taskInstUuid = taskInstanceUuid;
    }

    let permissions = await ctx.service.jsonDataService._call('taskService', 'getCurrentUserPermissions', [taskInstUuid, null]);
    let permissionMasks = (permissions && permissions.map(permission => permission.mask)) || [];
    // this.app.logger.error("permissions: " + permissionMasks);

    // if (await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.TODO.mask])) {
    if (permissionMasks.includes(constant.AclPermission.TODO.mask)) {
      let todoUrl = '/workflow/work/view/todo?flowInstUuid=' + flowInstUuid + requestCodeParamUri;
      todoUrl += '&taskInstUuid=' + (taskInstUuid || '');
      todoUrl += '&taskIdentityUuid=' + (taskIdentityUuid || '');
      // todoUrl += '&custom_script_url=' + (custom_script_url || '');
      todoUrl += '&allowOperate=' + (allowOperate || '');
      if (ctx.query.isXGWD) {
        todoUrl += '&isXGWD=' + ctx.query.isXGWD;
      }
      for (const q in ctx.query) {
        if (lodash.startsWith(q, 'ep_')) {
          draftUrl += '&' + q + '=' + ctx.query[q];
        }
      }
      ctx.redirect(this.addSystemPrefix(todoUrl));
      return;
    }
    let workBean = null;
    let sendContent = null;
    if (viewTheMainFlow === 'true') {
      workBean = await ctx.service.workflowTaskService.getReadWorkData(taskInstUuid, flowInstUuid);
      // workBean = await ctx.service.jsonDataService._call('workService', 'getRead', [taskInstUuid, flowInstUuid]);
    } else {
      if (permissionMasks.includes(constant.AclPermission.SUPERVISE.mask)) {
        workBean = await ctx.service.workflowTaskService.getSuperviseWorkData(taskInstUuid, flowInstUuid);
        // workBean = await ctx.service.jsonDataService._call('workService', 'getSupervise', [taskInstUuid, flowInstUuid]);
        // } else if (await ctx.service.jsonDataService._call('taskService', 'hasMonitorPermissionCurrentUser', [taskInstUuid])) {
      } else if (permissionMasks.includes(constant.AclPermission.MONITOR.mask)) {
        workBean = await ctx.service.workflowTaskService.getMonitorWorkData(taskInstUuid, flowInstUuid);
        // workBean = await ctx.service.jsonDataService._call('workService', 'getMonitor', [taskInstUuid, flowInstUuid]);
        // } else if (await ctx.service.jsonDataService._call('taskService', 'hasViewPermissionCurrentUser', [taskInstUuid])) {
        // else if (await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.DONE.mask])) {
      } else if (permissionMasks.includes(constant.AclPermission.DONE.mask)) {
        workBean = await ctx.service.workflowTaskService.getDoneWorkData(taskInstUuid, flowInstUuid, taskIdentityUuid);
        // } else if (await ctx.service.jsonDataService._call('taskService', 'hasSupervisePermissionCurrentUser', [taskInstUuid])) {
      } else if (
        permissionMasks.includes(constant.AclPermission.UNREAD.mask) ||
        permissionMasks.includes(constant.AclPermission.FLAG_READ.mask)
      ) {
        workBean = await ctx.service.workflowTaskService.getReadWorkData(taskInstUuid, flowInstUuid, taskIdentityUuid);
        // workBean = await ctx.service.jsonDataService._call('workService', 'getDone', [taskInstUuid, taskIdentityUuid, flowInstUuid]);
      } else if (
        permissionMasks.includes(constant.AclPermission.ATTENTION.mask) ||
        permissionMasks.includes(constant.AclPermission.READ.mask)
      ) {
        workBean = await ctx.service.workflowTaskService.getAttentionWorkData(taskInstUuid, flowInstUuid);
        // workBean = await ctx.service.jsonDataService._call('workService', 'getDone', [taskInstUuid, taskIdentityUuid, flowInstUuid]);
      } else {
        if (approveFlowInstUuid) {
          const flowInstanceParameter = {
            flowInstUuid: approveFlowInstUuid,
            name: 'custom_rt_sentContent'
          };
          const flowInstanceParameters = await ctx.service.jsonDataService._call('flowService', 'findFlowInstanceParameter', [
            flowInstanceParameter
          ]);
          if (flowInstanceParameters && flowInstanceParameters.length === 1) {
            sendContent = flowInstanceParameters[0].value;
            if (sendContent.indexOf(flowInstUuid) != -1) {
              if (await ctx.service.jsonDataService._call('flowService', 'hasDraftPermission', [ctx.user.userId, approveFlowInstUuid])) {
                workBean = await ctx.service.workflowTaskService.getReadWorkData(taskInstUuid, flowInstUuid);
                // workBean = await ctx.service.jsonDataService._call('workService', 'getRead', [taskInstUuid, flowInstUuid]);
              } else {
                const taskInstanceUuid = await ctx.service.jsonDataService._call('taskService', 'getLastTaskInstanceUuidByFlowInstUuid', [
                  approveFlowInstUuid
                ]);
                if (
                  taskInstanceUuid &&
                  (await ctx.service.jsonDataService._call('taskService', 'hasViewPermissionCurrentUser', [taskInstanceUuid]))
                ) {
                  workBean = await ctx.service.workflowTaskService.getReadWorkData(taskInstUuid, flowInstUuid);
                  // workBean = await ctx.service.jsonDataService._call('workService', 'getRead', [taskInstUuid, flowInstUuid]);
                }
              }
            }
          }
        } else if (await ctx.service.jsonDataService._call('taskSubFlowService', 'isShare', [null, flowInstUuid])) {
          workBean = await ctx.service.workflowTaskService.getViewerWorkData(taskInstUuid, flowInstUuid);
        }
      }

      if (!workBean) {
        await ctx.renderError('403', 'SystemErrorCode.UnauthorizedAccessToData', 'SystemErrorCode.PleaseUseAuthorizedUserToAccessData');;
        return;
      }
    }

    workBean.taskIdentityUuid = taskIdentityUuid;
    ctx.service.readMarkerService.markRead(taskInstUuid, ctx.user.userId);
    // if (custom_script_url) {
    //   workBean.extraParams.custom_script_url = custom_script_url;
    // }
    for (const q in ctx.query) {
      if (lodash.startsWith(q, 'ep_')) {
        workBean.extraParams[q] = ctx.query[q];
      }
    }
    if (!allowOperate) {
      workBean.buttons = [];
    }
    await this.workView(workBean);
  }

  /**
   * 未阅
   */
  async unread() {
    const ctx = this.ctx;
    const app = this.app;
    let { taskInstUuid, taskUuid, flowInstUuid, openToRead } = ctx.query;
    openToRead = openToRead == undefined || openToRead === '' || openToRead === 'true';
    if (taskUuid) {
      taskInstUuid = taskUuid;
    }
    if (taskInstUuid && !(await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.UNREAD.mask]))) {
      if (await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.FLAG_READ.mask])) {
        ctx.redirect(this.addSystemPrefix('/workflow/work/view/read?taskInstUuid=' + taskInstUuid));
        return;
      }
      await ctx.renderError('403', 'SystemErrorCode.UnauthorizedAccessToData', 'SystemErrorCode.PleaseUseAuthorizedUserToAccessData');;
      return;
    }
    const config = ctx.service.workflowTaskService.getReqGetConfig();
    const result = await ctx.curl(
      app.config.backendURL +
      `/api/workflow/work/getUnreadWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}&openToRead=${openToRead}`,
      config
    );
    const workBean = result.data && result.data.data;
    // const workBean = await ctx.service.jsonDataService._call('workService', 'getUnread', [taskInstUuid, flowInstUuid, openToRead]);
    if (taskInstUuid) {
      ctx.service.readMarkerService.markRead(taskInstUuid, ctx.user.userId);
    }
    await this.workView(workBean);
  }

  /**
   * 已阅
   */
  async read() {
    const ctx = this.ctx;
    const app = this.app;
    let { taskInstUuid, taskUuid, flowInstUuid } = ctx.query;
    if (taskUuid) {
      taskInstUuid = taskUuid;
    }
    if (taskInstUuid) {
      if (!(await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.FLAG_READ.mask]))) {
        if (await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.UNREAD.mask])) {
          ctx.redirect(this.addSystemPrefix('/workflow/work/view/unread?taskInstUuid=' + taskInstUuid));
          return;
        }
        await ctx.renderError('403', 'SystemErrorCode.UnauthorizedAccessToData', 'SystemErrorCode.PleaseUseAuthorizedUserToAccessData');;
        return;
      }

      ctx.service.jsonDataService._call('aclService', 'removePermission', [
        taskInstUuid,
        constant.AclPermission.UNREAD.mask,
        ctx.user.userId
      ]);
      ctx.service.jsonDataService._call('aclService', 'addPermission', [
        'com.wellsoft.pt.bpm.engine.entity.TaskInstance',
        taskInstUuid,
        constant.AclPermission.FLAG_READ.mask,
        ctx.user.userId
      ]);
      ctx.service.readMarkerService.markRead(taskInstUuid, ctx.user.userId);
    }
    const config = ctx.service.workflowTaskService.getReqGetConfig();
    const result = await ctx.curl(
      app.config.backendURL + `/api/workflow/work/getReadWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`,
      config
    );
    const workBean = result.data && result.data.data;
    // const workBean = await ctx.service.jsonDataService._call('workService', 'getRead', [taskInstUuid, flowInstUuid]);
    await this.workView(workBean);
  }

  async viewOver() {
    const ctx = this.ctx;
    const app = this.app;
    let { taskInstUuid, taskUuid, flowInstUuid } = ctx.query;
    if (taskUuid) {
      taskInstUuid = taskUuid;
    }
    if (taskInstUuid && !(await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.DONE.mask]))) {
      await ctx.renderError('403', 'SystemErrorCode.UnauthorizedAccessToData', 'SystemErrorCode.PleaseUseAuthorizedUserToAccessData');;
      return;
    }
    const config = ctx.service.workflowTaskService.getReqGetConfig();
    const result = await ctx.curl(
      app.config.backendURL + `/api/workflow/work/getOverWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`,
      config
    );
    const workBean = result.data && result.data.data;
    // const workBean = await ctx.service.jsonDataService._call('workService', 'getOver', [taskInstUuid, flowInstUuid]);
    await this.workView(workBean);
  }

  async viewSupervise() {
    const ctx = this.ctx;
    const app = this.app;
    let { taskInstUuid, taskUuid, flowInstUuid } = ctx.query;
    if (taskUuid) {
      taskInstUuid = taskUuid;
    }
    if (taskInstUuid && !(await ctx.service.jsonDataService._call('taskService', 'hasSupervisePermissionCurrentUser', [taskInstUuid]))) {
      await ctx.renderError('403', 'SystemErrorCode.UnauthorizedAccessToData', 'SystemErrorCode.PleaseUseAuthorizedUserToAccessData');;
      return;
    }
    const config = ctx.service.workflowTaskService.getReqGetConfig();
    const result = await ctx.curl(
      app.config.backendURL + `/api/workflow/work/getSuperviseWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`,
      config
    );
    const workBean = result.data && result.data.data;
    // const workBean = await ctx.service.jsonDataService._call('workService', 'getSupervise', [taskInstUuid, flowInstUuid]);
    await this.workView(workBean);
  }

  async viewMonitor() {
    const ctx = this.ctx;
    const app = this.app;
    let { taskInstUuid, taskUuid, flowInstUuid } = ctx.query;
    if (taskUuid) {
      taskInstUuid = taskUuid;
    }
    if (taskInstUuid && !(await ctx.service.jsonDataService._call('taskService', 'hasMonitorPermissionCurrentUser', [taskInstUuid]))) {
      await ctx.renderError('403', 'SystemErrorCode.UnauthorizedAccessToData', 'SystemErrorCode.PleaseUseAuthorizedUserToAccessData');;
      return;
    }
    const config = ctx.service.workflowTaskService.getReqGetConfig();
    const result = await ctx.curl(
      app.config.backendURL + `/api/workflow/work/getMonitorWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`,
      config
    );
    const workBean = result.data && result.data.data;
    // const workBean = await ctx.service.jsonDataService._call('workService', 'getMonitor', [taskInstUuid, flowInstUuid]);
    for (const q in ctx.query) {
      if (lodash.startsWith(q, 'ep_')) {
        workBean.extraParams[q] = ctx.query[q];
      }
    }
    await this.workView(workBean);
  }

  async workView(workBean) {
    const { ctx, app } = this;
    let SYSTEM_ID = ctx.SYSTEM_ID && ctx.SYSTEM_ID !== 'undefined' ? ctx.SYSTEM_ID : undefined;
    let settings = await ctx.service.workflowTaskService.getFlowSettings(SYSTEM_ID);
    let defaultContinuousWorkView = false;// settings && settings.GENERAL && settings.GENERAL.defaultContinuousWorkView;
    // 进入连续签批
    if (
      (ctx.query.continuousMode === '1' || (defaultContinuousWorkView && ctx.query.continuousMode != '0')) &&
      workBean.aclRole === 'TODO'
    ) {
      await ctx.render('workflow-work/continuous_work_view.js', { workBean, SYSTEM_ID, settings });
    } else {
      // 进入流程单据
      await ctx.render('workflow-work/work_view.js', { workBean, SYSTEM_ID, settings });
    }
  }

  addSystemPrefix(url) {
    const { ctx } = this;
    let SYSTEM_ID = ctx.SYSTEM_ID && ctx.SYSTEM_ID !== 'undefined' ? ctx.SYSTEM_ID : undefined;
    if (SYSTEM_ID && url && !url.startsWith('/sys/')) {
      return `/sys/${SYSTEM_ID}/_${url}`;
    }
    return url;
  }

  async subflowShare() {
    const ctx = this.ctx;
    const { taskInstUuid, flowInstUuid, belongToFlowInstUuid } = ctx.query;
    const taskInstanceUuid = await ctx.service.jsonDataService._call('taskService', 'getLastTaskInstanceUuidByFlowInstUuid', [
      flowInstUuid
    ]);
    if (taskInstanceUuid) {
      if (await ctx.service.jsonDataService._call('taskService', 'hasViewPermissionCurrentUser', [taskInstanceUuid])) {
        let url = '/workflow/work/view/work?taskInstUuid=' + taskInstanceUuid + '&flowInstUuid=' + flowInstUuid;
        for (const q in ctx.query) {
          if (lodash.startsWith(q, 'ep_')) {
            url += '&' + q + '=' + ctx.query[q];
          }
        }
        ctx.redirect(this.addSystemPrefix(url));
      } else {
        if (await ctx.service.jsonDataService._call('taskSubFlowService', 'isShare', [belongToFlowInstUuid, flowInstUuid])) {
          const workBean = await ctx.service.workflowTaskService.getViewerWorkData(taskInstanceUuid, flowInstUuid);
          // const workBean = await ctx.service.jsonDataService._call('workService', 'getRead', [taskInstanceUuid, flowInstUuid]);
          await this.workView(workBean);
        } else {
          await ctx.renderError('403', 'SystemErrorCode.UnauthorizedAccessToData', 'SystemErrorCode.PleaseUseAuthorizedUserToAccessData');;
        }
      }
    }
  }

  async workflowSimulation() {
    const { ctx, app } = this;
    let uuid = ctx.query.uuid;
    let flowDefinition = {};
    if (uuid) {
      const flowUrl = `/api/workflow/scheme/flow/json.action?uuid=${uuid}`;
      try {
        const result = await ctx.curl(app.config.backendURL + flowUrl, {
          method: 'get',
          contentType: 'json',
          dataType: 'json'
        });
        if (result.data) {
          flowDefinition = result.data;
        }
      } catch (error) {
        app.logger.error('%s', error);
      }
    }
    await ctx.render('workflow-simulation/index.js', {
      uuid,
      flowDefinition
    });
  }
}

module.exports = WorkflowVueController;
