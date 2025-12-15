'use strict';

const Controller = require('wellapp-framework').Controller;
const constant = require('./constant');

class WorkflowFileViewerController extends Controller {
  async viewArchive() {
    const ctx = this.ctx;
    const { flowInstUuid, fileUuid, folderUuid, _requestCode } = ctx.query;

    const taskInstUuid = await this.getTaskInstUuid(flowInstUuid);

    // 流程权限判断
    const hasTaskViewPermission = await ctx.service.workflowTaskService.hasViewPermission(taskInstUuid);
    if (hasTaskViewPermission) {
      const redirectUrl = `/workflow/work/view/work?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}&_requestCode=${_requestCode}`;
      ctx.redirect(redirectUrl);
      return;
    }

    // 文件读取权限判断
    const hasFilePermission = await this.hasFilePermission(fileUuid);
    if (!hasFilePermission) {
      ctx.redirect('/error?real_status=404');
      return;
    }

    const workBean = await ctx.service.workflowTaskService.getReadWorkData(taskInstUuid, flowInstUuid);
    // const workBean = await ctx.service.jsonDataService._call('workService', 'getRead', [taskInstUuid, flowInstUuid]);
    if (workBean) {
      await this.workView(workBean);
    } else {
      ctx.redirect('/error?real_status=404');
    }
  }

  async viewDyformArchive() {
    const ctx = this.ctx;
    const { dataUuid, fileUuid, folderUuid, _requestCode } = ctx.query;

    const flowInstUuid = await this.getArchiveFlowInstUuid(fileUuid, folderUuid);
    if (!flowInstUuid) {
      ctx.redirect('/error?real_status=404');
      return;
    }

    const taskInstUuid = await this.getTaskInstUuid(flowInstUuid);

    // 流程权限判断
    const hasTaskViewPermission = await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [
      constant.AclPermission.FLAG_READ.mask
    ]);
    if (hasTaskViewPermission) {
      const redirectUrl = `/workflow/work/view/work?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}&_requestCode=${_requestCode}`;
      ctx.redirect(redirectUrl);
      return;
    }

    // 文件读取权限判断
    const hasFilePermission = await this.hasFilePermission(fileUuid);
    if (!hasFilePermission) {
      ctx.redirect('/error?real_status=404');
      return;
    }
    const workBean = await ctx.service.workflowTaskService.getReadWorkData(taskInstUuid, flowInstUuid);
    // const workBean = await ctx.service.jsonDataService._call('workService', 'getRead', [taskInstUuid, flowInstUuid]);
    if (workBean) {
      // 表单归档只能查看表单数据及打印表单
      workBean.buttons = workBean.buttons && workBean.buttons.filter(button => button.id == 'B004096');
      await this.workView(workBean);
    } else {
      ctx.redirect('/error?real_status=404');
    }
  }

  async getTaskInstUuid(flowInstUuid) {
    return await this.ctx.service.jsonDataService._call('taskService', 'getLastTaskInstanceUuidByFlowInstUuid', [flowInstUuid]);
  }

  async hasFilePermission(fileUuid) {
    return await this.ctx.service.jsonDataService._call('dmsFileActionService', 'hasFilePermission', [fileUuid, ['readFile']]);
  }

  async getArchiveFlowInstUuid(fileUuid, folderUuid) {
    const flowInstanceParameter = { name: folderUuid + '/' + fileUuid };

    const flowInstanceParameters = await this.ctx.service.jsonDataService._call('flowService', 'findFlowInstanceParameter', [
      flowInstanceParameter
    ]);

    if (Array.isArray(flowInstanceParameters) && flowInstanceParameters.length) {
      return flowInstanceParameters[0].flowInstUuid;
    }

    return null;
  }

  async workView(workBean) {
    const ctx = this.ctx;
    let SYSTEM_ID = ctx.SYSTEM_ID && ctx.SYSTEM_ID !== 'undefined' ? ctx.SYSTEM_ID : undefined;

    let settings = await ctx.service.workflowTaskService.getFlowSettings(SYSTEM_ID);

    await ctx.render('workflow-work/work_view.js', {
      workBean,
      SYSTEM_ID,
      settings
    });
  }
}

module.exports = WorkflowFileViewerController;
