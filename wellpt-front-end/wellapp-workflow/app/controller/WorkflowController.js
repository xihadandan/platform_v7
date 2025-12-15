'use strict';

const Controller = require('wellapp-framework').Controller;
const lodash = require('lodash');
const constant = require('./constant');

class WorkflowController extends Controller {
  /**
   * 发起工作流
   */
  async _new() {
    const { ctx, app } = this;
    try {
      const workBean = await ctx.service.workflowTaskService.newWorkById(ctx.params.id);
      if (ctx.query.formUuid) {
        workBean.formUuid = ctx.query.formUuid;
        if (ctx.query.dataUuid) {
          workBean.dataUuid = ctx.query.dataUuid;
        }
      }
      if (ctx.query.customScriptUrl) {
        workBean.extraParams.custom_script_url = ctx.query.customScriptUrl;
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

  async workView(workBean) {
    const { ctx, app } = this;
    const extraJs = [
      'cmsWindown',
      'bootstrap',
      'appContext',
      'select2',
      'select2_locale_zh-cn',
      'wSelect2',
      'summernote-zh-CN',
      'summernote',
      'appModal',
      'slimScroll',
      'server',
      workBean.customJsModule,
      'WorkView',
      'jquery-workView',
      'niceScroll',
      'printArea'
    ];
    var signOpinion = this.config.signOpinion; // 获取签署意见的二开文件id
    var processViewer = this.config.processViewer; // 获取办理过程的二开文件id

    const definitionJs = await ctx.service.dyformDefinitionService.explainJavascripts(workBean.formUuid, extraJs);
    if (signOpinion && signOpinion != '') {
      var jsIndex = lodash.findIndex(definitionJs.javascripts, function (o) {
        // 获取旧的签署意见id所在的index
        return o.id == 'WorkFlowSidebarTabOpinionEditor';
      });
      definitionJs.javascripts[jsIndex] = lodash.cloneDeep(app.jsPack[signOpinion]); // 替换签署意见的文件路径和依赖
      definitionJs.javascripts[jsIndex].id = 'WorkFlowSidebarTabOpinionEditor'; // 签署意见id不变
    }

    if (processViewer && processViewer != '') {
      var processJsIndex = lodash.findIndex(definitionJs.javascripts, function (o) {
        // 获取旧的办理过程id所在的index
        return o.id == 'WorkFlowSidebarTabWorkProcessViewer';
      });
      definitionJs.javascripts[processJsIndex] = lodash.cloneDeep(app.jsPack[processViewer]); // 替换办理过程的文件路径和依赖
      definitionJs.javascripts[processJsIndex].id = 'WorkFlowSidebarTabWorkProcessViewer'; // 办理过程id不变
    }

    var DyformDevelopment = this.config.DyformDevelopment; // 获取表单的二开文件id
    if (DyformDevelopment && DyformDevelopment != '') {
      var jsIndex = lodash.findIndex(definitionJs.javascripts, function (o) {
        // 获取表单id所在的index
        return o.id == 'DyformExplainDevelopment';
      });
      definitionJs.javascripts[jsIndex] = lodash.cloneDeep(app.jsPack[DyformDevelopment]); // 替换签署意见的文件路径和依赖
      definitionJs.javascripts[jsIndex].id = 'DyformExplainDevelopment'; // 签署意见id不变
    }

    const requirejsConfig = ctx.helper.createRequirejsConfig(definitionJs.javascripts);

    // 样式
    const css = await ctx.service.webResourceService.getThemeCss([], true);
    await ctx.render('/workflow/work_view.nj', {
      requirejsConfigJSON: JSON.stringify(requirejsConfig),
      javascriptsJson: JSON.stringify(Array.from(definitionJs.jsAdded)),
      css,
      workBean,
      paceDisabled: app.config.paceDisabled === true //pace加载进度条，默认开启；存在部分项目组不想要的情况
    });
  }

  /**
   * 待办
   */
  async todo() {
    const ctx = this.ctx;
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
        ctx.redirect('/workflow/work/view/unrend?taskInstUuid' + taskInstUuid + '&openToRead=' + openToRead);
        return;
      } else if (await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.FLAG_READ.mask])) {
        ctx.redirect('/workflow/work/view/read?taskInstUuid' + taskInstUuid);
        return;
      }
      ctx.body = constant.UnauthenticateText;
      return;
    }

    const workBean = await ctx.service.workflowTaskService.getWorkBean(taskInstUuid, flowInstUuid, 'getTodo');
    // 当前任务办理人标识
    if (taskIdentityUuid) {
      workBean.taskIdentityUuid = taskIdentityUuid;
    }
    if (taskInstUuid) {
      ctx.service.readMarkerService.markRead(taskInstUuid, userid);
    }
    if (custom_script_url) {
      workBean.extraParams.custom_script_url = custom_script_url;
    }
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
    workBean.multiSubmitType = await ctx.service.workflowTaskService.taskMultiUserSubmitType(workBean.flowDefUuid, workBean.taskId);
    await this.workView(workBean);
  }

  /**
   * 已办
   */
  async done() {
    const ctx = this.ctx;
    let { taskUuid, taskInstUuid, flowInstUuid } = ctx.query;
    if (taskUuid) {
      taskInstUuid = taskUuid;
    }
    if (taskInstUuid) {
      ctx.service.readMarkerService.markRead(taskInstUuid, ctx.user.userId);
    }

    if (taskInstUuid && !(await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.DONE.mask]))) {
      ctx.body = constant.UnauthenticateText;
      return;
    }

    const workBean = await ctx.service.workflowTaskService.getWorkBean(taskInstUuid, flowInstUuid, 'getDone');
    await this.workView(workBean);
  }

  /**
   * 关注
   */
  async attention() {
    const ctx = this.ctx;
    const { taskUuid, taskInstUuid, flowInstUuid } = ctx.query;
    if (taskInstUuid && !(await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.ATTENTION.mask]))) {
      ctx.redirect('/workflow/work/v53/view/work?taskInstUuid' + taskInstUuid + '&flowInstUuid=' + flowInstUuid);
    } else {
      const workBean = await ctx.service.workflowTaskService.getWorkBean(taskInstUuid, flowInstUuid, 'getAttention');
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
      docLinkUuid,
      openToRead,
      custom_script_url,
      allowOperate,
      auto_submit,
      _requestCode
    } = ctx.query;
    openToRead = openToRead == undefined || openToRead === '' || openToRead === 'true';
    allowOperate = allowOperate == undefined || allowOperate === '' || allowOperate === 'true';
    const requestCodeParamUri = '&_requestCode=' + _requestCode;
    let draftUrl = '/workflow/work/v53/view/draft?flowInstUuid=' + flowInstUuid + requestCodeParamUri;
    for (const q in ctx.query) {
      if (lodash.startsWith(q, 'ep_')) {
        draftUrl += '&' + q + '=' + ctx.query[q];
      }
    }
    let taskInstance = null;
    if (taskInstUuid) {
      taskInstance = await ctx.service.jsonDataService._call('taskService', 'getTask', [taskInstUuid]);
      if (taskInstance && taskInstance.endTime) {
        const taskInstanceUuid = await ctx.service.jsonDataService._call('taskService', 'getLastTaskInstanceUuidByFlowInstUuid', [
          flowInstUuid
        ]);
        if (!taskInstanceUuid) {
          ctx.redirect(draftUrl);
          return;
        }
        taskInstUuid = taskInstanceUuid;
      }
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
        ctx.redirect(draftUrl);
        return;
      }

      taskInstUuid = taskInstanceUuid;
    }

    if (await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.TODO.mask])) {
      let todoUrl = '/workflow/work/v53/view/todo?flowInstUuid=' + flowInstUuid + requestCodeParamUri;
      todoUrl += '&taskInstUuid=' + (taskInstUuid || '');
      todoUrl += '&taskIdentityUuid=' + (taskIdentityUuid || '');
      todoUrl += '&custom_script_url=' + (custom_script_url || '');
      todoUrl += '&allowOperate=' + (allowOperate || '');
      if (ctx.query.isXGWD) {
        todoUrl += '&isXGWD=' + ctx.query.isXGWD;
      }
      for (const q in ctx.query) {
        if (lodash.startsWith(q, 'ep_')) {
          draftUrl += '&' + q + '=' + ctx.query[q];
        }
      }
      ctx.redirect(todoUrl);
      return;
    }
    let workBean = null;
    taskInstance = {
      uuid: taskInstUuid
    };
    let sendContent = null;
    if (viewTheMainFlow === 'true') {
      workBean = await ctx.service.jsonDataService._call('workService', 'getRead', [taskInstUuid, flowInstUuid]);
    } else {
      if (await ctx.service.jsonDataService._call('taskService', 'hasSupervisePermissionCurrentUser', [taskInstUuid])) {
        workBean = await ctx.service.jsonDataService._call('workService', 'getSupervise', [taskInstUuid, flowInstUuid]);
      } else if (await ctx.service.jsonDataService._call('taskService', 'hasMonitorPermissionCurrentUser', [taskInstUuid])) {
        workBean = await ctx.service.jsonDataService._call('workService', 'getMonitor', [taskInstUuid, flowInstUuid]);
      } else if (await ctx.service.jsonDataService._call('taskService', 'hasViewPermissionCurrentUser', [taskInstUuid])) {
        workBean = await ctx.service.jsonDataService._call('workService', 'getDone', [taskInstUuid, taskIdentityUuid, flowInstUuid]);
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
                workBean = await ctx.service.jsonDataService._call('workService', 'getRead', [taskInstUuid, flowInstUuid]);
              } else {
                const taskInstanceUuid = await ctx.service.jsonDataService._call('taskService', 'getLastTaskInstanceUuidByFlowInstUuid', [
                  approveFlowInstUuid
                ]);
                if (
                  taskInstanceUuid &&
                  (await ctx.service.jsonDataService._call('taskService', 'hasViewPermissionCurrentUser', [taskInstanceUuid]))
                ) {
                  workBean = await ctx.service.jsonDataService._call('workService', 'getRead', [taskInstUuid, flowInstUuid]);
                }
              }
            }
          }
        } else if (docLinkUuid) {
          // 文档链接
          if (await ctx.service.documentLinkService.checkSourceDataByUuid(docLinkUuid)) {
            workBean = await ctx.service.jsonDataService._call('workService', 'getRead', [taskInstUuid, flowInstUuid]);
          }
        }
      }

      if (!workBean) {
        ctx.body = constant.UnauthenticateText;
        return;
      }
    }

    workBean.taskIdentityUuid = taskIdentityUuid;
    ctx.service.readMarkerService.markRead(taskInstUuid, ctx.user.userId);
    if (custom_script_url) {
      workBean.extraParams.custom_script_url = custom_script_url;
    }
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
    let { taskInstUuid, taskUuid, flowInstUuid, openToRead } = ctx.query;
    openToRead = openToRead == undefined || openToRead === '' || openToRead === 'true';
    if (taskUuid) {
      taskInstUuid = taskUuid;
    }
    if (taskInstUuid && !(await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.UNREAD.mask]))) {
      if (await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.FLAG_READ.mask])) {
        ctx.redirect('/workflow/work/v53/view/read?taskInstUuid=' + taskInstUuid);
        return;
      }
      ctx.body = constant.UnauthenticateText;
      return;
    }
    const workBean = await ctx.service.jsonDataService._call('workService', 'getUnread', [taskInstUuid, flowInstUuid, openToRead]);
    if (taskInstUuid) {
      ctx.service.readMarkerService.markRead(taskInstUuid, ctx.user.userId);
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
      ctx.body = constant.UnauthenticateText;
      return;
    }

    const workBean = await ctx.service.jsonDataService._call('workService', 'getDraft', [flowInstUuid]);
    for (const q in ctx.query) {
      if (lodash.startsWith(q, 'ep_')) {
        workBean.extraParams[q] = ctx.query[q];
      }
    }
    await this.workView(workBean);
  }

  /**
   * 已阅
   */
  async read() {
    const ctx = this.ctx;
    let { taskInstUuid, taskUuid, flowInstUuid } = ctx.query;
    if (taskUuid) {
      taskInstUuid = taskUuid;
    }
    if (taskInstUuid) {
      if (!(await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.FLAG_READ.mask]))) {
        if (await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.UNREAD.mask])) {
          ctx.redirect('/workflow/work/v53/view/unread?taskInstUuid=' + taskInstUuid);
          return;
        }
        ctx.body = constant.UnauthenticateText;
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

    await this.workView(await ctx.service.jsonDataService._call('workService', 'getRead', [taskInstUuid, flowInstUuid]));
  }

  async viewReadLog() {
    this.ctx.body = await this.ctx.service.jsonDataService._call('workService', 'viewReadLog', [this.ctx.query.taskInstUuid]);
  }

  async subflowShare() {
    const ctx = this.ctx;
    const { taskInstUuid, flowInstUuid, belongToFlowInstUuid } = ctx.query;
    const taskInstanceUuid = await ctx.service.jsonDataService._call('taskService', 'getLastTaskInstanceUuidByFlowInstUuid', [
      flowInstUuid
    ]);
    if (taskInstanceUuid) {
      if (await ctx.service.jsonDataService._call('taskService', 'hasViewPermissionCurrentUser', [taskInstanceUuid])) {
        let url = '/workflow/work/v53/view/work?taskInstUuid=' + taskInstanceUuid + '&flowInstUuid=' + flowInstUuid;
        for (const q in ctx.query) {
          if (lodash.startsWith(q, 'ep_')) {
            url += '&' + q + '=' + ctx.query[q];
          }
        }
        ctx.redirect(url);
      } else {
        if (await ctx.service.jsonDataService._call('taskSubFlowService', 'isShare', [belongToFlowInstUuid, flowInstUuid])) {
          const workBean = await ctx.service.jsonDataService._call('workService', 'getRead', [taskInstanceUuid, flowInstUuid]);
          await this.workView(workBean);
        } else {
          ctx.body = constant.UnauthenticateText;
        }
      }
    }
  }

  async viewFlowProcess() {
    const query = this.ctx.query;

    const themeFiles = await ctx.service.userThemeService.getThemeFiles();

    await this.ctx.render('/workflow/work_view_process_v53.nj', {
      flowInstUuid: query.flowInstUuid,
      workProcesses: await this.ctx.service.jsonDataService._call('workService', 'getWorkProcess', [query.flowInstUuid]),
      themeFiles
    });
  }

  async viewFlowMobileProcess() {
    const { app } = this;
    try {
      const query = this.ctx.query;

      await this.ctx.render('/workflow/work_view_process_mobile_v53.nj', {
        isMobileApp: query.isMobileApp === 'true',
        flowInstUuid: query.flowInstUuid,
        workProcesses: await this.ctx.service.jsonDataService._call('workService', 'getWorkProcess', [query.flowInstUuid])
      });
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async viewOver() {
    const ctx = this.ctx;
    let { taskInstUuid, taskUuid, flowInstUuid } = ctx.query;
    if (taskUuid) {
      taskInstUuid = taskUuid;
    }
    if (taskInstUuid && !(await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.DONE.mask]))) {
      ctx.body = constant.UnauthenticateText;
      return;
    }

    await this.workView(await ctx.service.jsonDataService._call('workService', 'getOver', [taskInstUuid, flowInstUuid]));
  }

  async viewSupervise() {
    const ctx = this.ctx;
    let { taskInstUuid, taskUuid, flowInstUuid } = ctx.query;
    if (taskUuid) {
      taskInstUuid = taskUuid;
    }
    if (taskInstUuid && !(await ctx.service.jsonDataService._call('taskService', 'hasSupervisePermissionCurrentUser', [taskInstUuid]))) {
      ctx.body = constant.UnauthenticateText;
      return;
    }

    await this.workView(await ctx.service.jsonDataService._call('workService', 'getSupervise', [taskInstUuid, flowInstUuid]));
  }

  async viewMonitor() {
    const ctx = this.ctx;
    let { taskInstUuid, taskUuid, flowInstUuid } = ctx.query;
    if (taskUuid) {
      taskInstUuid = taskUuid;
    }
    if (taskInstUuid && !(await ctx.service.jsonDataService._call('taskService', 'hasMonitorPermissionCurrentUser', [taskInstUuid]))) {
      ctx.body = constant.UnauthenticateText;
      return;
    }
    const workBean = await ctx.service.jsonDataService._call('workService', 'getMonitor', [taskInstUuid, flowInstUuid]);
    for (const q in ctx.query) {
      if (lodash.startsWith(q, 'ep_')) {
        workBean.extraParams[q] = ctx.query[q];
      }
    }
    await this.workView(workBean);
  }

  async getWorkProcess() {
    //获取流程办理过程
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getWorkProcessAndOpinionPositionConfigs', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getWorkProcesses() {
    //获取流程办理过程
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getWorkProcesses', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getOldWorkProcess() {
    //获取流程办理过程
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getWorkProcess', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getTaskProcess() {
    //获取环节办理信息
    const { ctx, app } = this;
    ctx.headers.authorization = ctx.headers['authorization-jwt'];
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getTaskProcess', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async listFlowDataSnapshotWithoutDyformDataByFlowInstUuid() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/listFlowDataSnapshotWithoutDyformDataByFlowInstUuid', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getFlowDataSnapshotByIds() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getFlowDataSnapshotByIds', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getCurrentUserOpinion2Sign() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getCurrentUserOpinion2Sign', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  //保存加载规则
  async saveLoadingRules() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/user/preferences/saveLoadingRules', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  //获取加载规则
  async getLoadingRules() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/user/preferences/getLoadingRules', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  //流程分类下拉数据查询接口
  async categoryQuery() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/category/query', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  //流程定义下拉数据查询接口
  async definitionQuery() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/query', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  //待办工作数据查询接口
  async todoQuery() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/todo/query', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  //下一条
  async nextRecord() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/todo/next/record?dataIndex=' + ctx.query.dataIndex, {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  //稍后处理接口
  async todoDealLater() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/todo/dealLater', {
        method: 'post',
        contentType: 'form',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  //待办工作数据按计时状态分组取总数
  async groupAndCountByTimingState() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/todo/groupAndCountByTimingState', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  //待办工作数据按流程定义ID分组取总数
  async groupAndCountByFlowDefId() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/todo/groupAndCountByFlowDefId', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async todoCount() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/todo/count', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getWorkDataByWorkBean() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getWorkDataByWorkBean', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getTodoWorkData() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getTodoWorkData', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async save() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/save', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      if (ctx.req.body.taskInstUuid) {
        this.sendSocketMsg({
          taskInstUuid: ctx.req.body.taskInstUuid,
          flowDefUuid: ctx.req.body.flowDefUuid,
          taskId: ctx.req.body.taskId,
          anyOneTip: `<label class="user-name-label">${ctx.user.userName}</label>已${ctx.req.body.action}数据，可能存在冲突，请在备份后更新数据`,
          multiSubmitTip: result.data.data && result.data.data.formDataUpdated == 'true' // 数据存在变更才通知其他办理人
            ? `<label class="user-name-label">${ctx.user.userName}</label>已${ctx.req.body.action}数据，可能存在冲突，请在备份后更新数据`
            : '',
          success: result.data.code == 0
        });
        await this.dealDataChangedResult(result, ctx.req.body.taskInstUuid, ctx.req.body.action);
      }
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async dealDataChangedResult(result, taskInstUuid, action) {
    const { ctx, app } = this;
    if (app.config.redis && app.config.redis.client) {
      if (result.data.code == 0 || result.data.success) {
        app.redis.set(
          'workflow:submit:' + taskInstUuid,
          JSON.stringify({
            user: ctx.user.userName,
            action: action
          }),
          'EX',
          60 * 60 * 3
        );
      } else if (result.data.data && result.data.data.errorCode === 'SaveData') {
        if (result.data.data.data instanceof String) {
          let _result = JSON.parse(result.data.data.data);
          if (_result.code === 'DATA_OUT_OF_DATE') {
            //数据过时
            let lastOperator = await app.redis.get('workflow:submit:' + taskInstUuid);
            if (lastOperator) {
              lastOperator = JSON.parse(lastOperator);
              _result.msg = `与${lastOperator.user}已${lastOperator.action}的数据冲突，请在备份后更新数据`;
              result.data.data.data = JSON.stringify(_result);
            }
          }
        }
      }
    }
  }

  async isAllowedSubmit() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/isAllowedSubmit', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async isRequiredSubmitOpinion() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/isRequiredSubmitOpinion', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async submit() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/submit', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      // let success = result.data.success || result.data.code == 0;
      if (ctx.req.body.taskInstUuid) {
        this.sendSocketMsg({
          taskInstUuid: ctx.req.body.taskInstUuid,
          flowDefUuid: ctx.req.body.flowDefUuid,
          taskId: ctx.req.body.taskId,
          anyOneTip: `当前环节只需要一人办理，<label class="user-name-label">${ctx.user.userName}</label>已${ctx.req.body.action}`,
          multiSubmitTip: result.data.data && result.data.data.formDataUpdated // 数据存在变更才通知其他办理人
            ? `<label class="user-name-label">${ctx.user.userName}</label>已${ctx.req.body.action}数据，可能存在冲突，请在备份后更新数据`
            : '',
          success: result.data.success || result.data.code == 0
        });
        await this.dealDataChangedResult(result, ctx.req.body.taskInstUuid, ctx.req.body.action);
      }

      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async sendSocketMsg({ taskInstUuid, flowDefUuid, taskId, anyOneTip, multiSubmitTip, success, tipType }) {
    const { ctx, app } = this;
    if (success) {
      let operationTip = anyOneTip;
      if (taskId) {
        let submitType = await ctx.service.workflowTaskService.taskMultiUserSubmitType(flowDefUuid, taskId);
        if (submitType !== 'isAnyone') {
          operationTip = multiSubmitTip;
        }
      }
      // 流程提交成功要通知其他人
      if (operationTip) {
        app.io
          .of('/wellapp-dyform')
          .to(taskInstUuid)
          .emit('dyformDataChanged', {
            userId: ctx.user.userId,
            userName: ctx.user.userName,
            operationTip,
            socketId: ctx.cookies.get('io', {
              signed: false
            }),
            tipType
          });
      }
    }
  }

  async isAllowedDirectRollback() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/isAllowedDirectRollback', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async isRequiredDirectRollbackOpinion() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/isRequiredRollbackOpinion', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async rollback() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/rollback', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      this.sendSocketMsg({
        taskInstUuid: ctx.req.body.taskInstUuid,
        flowDefUuid: ctx.req.body.flowDefUuid,
        taskId: ctx.req.body.taskId,
        anyOneTip: `${ctx.user.userName} 已${ctx.req.body.action}`,
        multiSubmitTip: `${ctx.user.userName} 已${ctx.req.body.action}`,
        success: result.data.success
      });
      await this.dealDataChangedResult(result, ctx.req.body.taskInstUuid, ctx.req.body.action);
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async rollbackToMainFlow() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/rollbackToMainFlow', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      this.sendSocketMsg({
        taskInstUuid: ctx.req.body.taskInstUuid,
        flowDefUuid: ctx.req.body.flowDefUuid,
        taskId: ctx.req.body.taskId,
        anyOneTip: `<label class="user-name-label">${ctx.user.userName}</label>已${ctx.req.body.action}`,
        multiSubmitTip: `<label class="user-name-label">${ctx.user.userName}</label>已${ctx.req.body.action}`,
        success: result.data.success
      });
      await this.dealDataChangedResult(result, ctx.req.body.taskInstUuid, ctx.req.body.action);
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async isAllowedCancel() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/isAllowedCancel', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async isRequiredCancelOpinion() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/isRequiredCancelOpinion', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async cancel() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/cancel', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      // 撤回成功要判断是否还有相关权限
      let hasTodoPermission = await ctx.service.workflowTaskService.hasPermission(ctx.req.body.taskInstUuid, [
        constant.AclPermission.TODO.mask
      ]);
      if (!hasTodoPermission) {
        this.sendSocketMsg({
          taskInstUuid: ctx.req.body.taskInstUuid,
          flowDefUuid: null,
          taskId: null,
          anyOneTip: `${ctx.user.userName} 已${ctx.req.body.action}`,
          multiSubmitTip: `${ctx.user.userName} 已${ctx.req.body.action}`,
          success: result.data.code == 0,
          tipType: 'info'
        });
      }
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async cancelWithWorkData() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/cancelWithWorkData', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      // 撤回成功要判断是否还有相关权限
      let hasTodoPermission = await ctx.service.workflowTaskService.hasPermission(ctx.req.body.taskInstUuid, [
        constant.AclPermission.TODO.mask
      ]);
      if (!hasTodoPermission) {
        this.sendSocketMsg({
          taskInstUuid: ctx.req.body.taskInstUuid,
          flowDefUuid: null,
          taskId: null,
          anyOneTip: `${ctx.user.userName} 已${ctx.req.body.action}`,
          multiSubmitTip: `${ctx.user.userName} 已${ctx.req.body.action}`,
          success: result.data.code == 0,
          tipType: 'alert'
        });
      }
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getTodoTaskByFlowInstUuid() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getTodoTaskByFlowInstUuid', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async isAllowedTransfer() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/isAllowedTransfer', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async isRequiredTransferOpinion() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/isRequiredTransferOpinion', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async transfer() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/transfer', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      let success = result.data.success || result.data.code == 0;
      if (success && ctx.req.body.workBean) {
        this.sendSocketMsg({
          taskInstUuid: ctx.req.body.workBean.taskInstUuid,
          flowDefUuid: ctx.req.body.workBean.flowDefUuid,
          taskId: ctx.req.body.workBean.taskId,
          anyOneTip: `<label class="user-name-label">${ctx.user.userName}</label>已${ctx.req.body.workBean.action
            }给<label class="user-name-label-inner">${ctx.req.body.transferUsers.join(' ')}</label>`,
          multiSubmitTip: result.data.data && result.data.data.formDataUpdated // 数据存在变更才通知其他办理人
            ? `<label class="user-name-label">${ctx.user.userName}</label>已${ctx.req.body.workBean.action}数据，可能存在冲突，请在备份后更新数据`
            : '',
          success: result.data.code == 0
        });
        await this.dealDataChangedResult(result, ctx.req.body.workBean.taskInstUuid, ctx.req.body.workBean.action);
      }

      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async isRequiredCounterSignOpinion() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/isRequiredCounterSignOpinion', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async isAllowedCounterSign() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/isAllowedCounterSign', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async counterSign() {
    const { ctx, app } = this;
    try {
      let signUsers = ctx.req.body.signUsers ? ctx.req.body.signUsers.join(' ') : '';
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/counterSign', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      let success = result.data.success || result.data.code == 0;
      if (success && ctx.req.body.workBean) {
        this.sendSocketMsg({
          taskInstUuid: ctx.req.body.workBean.taskInstUuid,
          flowDefUuid: ctx.req.body.workBean.flowDefUuid,
          taskId: ctx.req.body.workBean.taskId,
          anyOneTip: `当前环节只需一人办理，<label class="user-name-label">${ctx.user.userName}</label>已${ctx.req.body.workBean.action
            }给<label class="user-name-label-inner">${ctx.req.body.signUsers.join(' ')}</label>`,
          multiSubmitTip: `<label class="user-name-label">${ctx.user.userName}</label>已${ctx.req.body.workBean.action}数据，可能存在冲突，请在备份后更新数据`,
          success: result.data.code == 0
        });
      }
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async addSign() {
    const { ctx, app } = this;
    try {
      let signUsers = ctx.req.body.signUsers ? ctx.req.body.signUsers.join(' ') : '';
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/addSign', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      let success = result.data.success || result.data.code == 0;
      if (success && ctx.req.body.workBean) {
        this.sendSocketMsg({
          taskInstUuid: ctx.req.body.workBean.taskInstUuid,
          flowDefUuid: ctx.req.body.workBean.flowDefUuid,
          taskId: ctx.req.body.workBean.taskId,
          anyOneTip: `当前环节只需一人办理，<label class="user-name-label">${ctx.user.userName}</label>已${ctx.req.body.workBean.action
            }给<label class="user-name-label-inner">${signUsers}</label>`,
          multiSubmitTip: `<label class="user-name-label">${ctx.user.userName}</label>已${ctx.req.body.workBean.action}数据，可能存在冲突，请在备份后更新数据`,
          success: result.data.code == 0
        });
      }
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async copyTo() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/copyTo', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async markRead() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/markRead', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async markUnread() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/markUnread', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async attentionTask() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/attention', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async unfollow() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/unfollow', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async setViewMainFlow() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/setViewMainFlow', {
        method: 'post',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async remind() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/remind', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getPrintTemplates() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getPrintTemplates', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getPrintTemplateTree() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getPrintTemplateTree', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getPrintTemplateByIds() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/basicdata/printtemplate/listByIds', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async print() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/print', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getDyformFileFieldDefinitions() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getDyformFileFieldDefinitions', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async isAllowedHandOver() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/isAllowedHandOver', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async isRequiredHandOverOpinion() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/isRequiredHandOverOpinion', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async handOver() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/handOver', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async isAllowedGotoTask() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/isAllowedGotoTask', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async isRequiredGotoTaskOpinion() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/isRequiredGotoTaskOpinion', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async gotoTaskWithWorkData() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/gotoTaskWithWorkData', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async suspend() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/suspend', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async resume() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/resume', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async topping() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/topping', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async untopping() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/untopping', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async delete() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/delete', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async deleteDraft() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/deleteDraft', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async deleteByAdmin() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/deleteByAdmin', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async logicalDeleteByAdmin() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/logicalDeleteByAdmin', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async loadSubTaskData() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/loadSubTaskData', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async loadBranchTaskData() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/loadBranchTaskData', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getBranchTaskProcesses() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getBranchTaskProcesses', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getBusinessApplicationConfig() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getBusinessApplicationConfig', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async startSubFlow() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/startSubFlow', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async startBranchTask() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/startBranchTask', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getNewFlowLabelInfos() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getNewFlowLabelInfos', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async resendSubFlow() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/resendSubFlow', {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async redoFlow() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/redoFlow', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async redoBranchTask() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/redoBranchTask', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async stopFlow() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/stopFlow', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async stopBranchTask() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/stopBranchTask', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async distributeInfo() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/distributeInfo', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async changeTaskDueTime() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/changeTaskDueTime', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async changeFlowDueTime() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/changeFlowDueTime', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async lockWork() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/lockWork', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async unlockWork() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/unlockWork', {
        method: 'post',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async listLockInfo() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/listLockInfo', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async listAllLockInfo() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + ctx.request.url, {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async releaseAllLock() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/releaseAllLock', {
        method: 'post',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getLastestCancelAfterByFlowInstUuid() {
    // 获取最新的环节操作
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/task/operation/getLastestCancelAfterByFlowInstUuid', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async checkRecordPreCondition() {
    // 检查信息记录前置条件
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/checkRecordPreCondition', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getOldWorkProcess() {
    //获取流程办理过程
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/getWorkProcess', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async loadShareDatasByPage() {
    //分页查询承办信息
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/loadShareDatasByPage', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async loadDistributeInfosByPage() {
    //分页查询信息分发
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/loadDistributeInfosByPage', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async loadRelateOperationByPage() {
    //分页查询操作记录
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/loadRelateOperationByPage', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }
}

module.exports = WorkflowController;
