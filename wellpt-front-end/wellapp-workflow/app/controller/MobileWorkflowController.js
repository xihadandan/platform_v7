'use strict';

const Controller = require('wellapp-framework').Controller;
const lodash = require('lodash');
const constant = require('./constant');

class MobileWorkflowController extends Controller {
  /**
   * 待办
   */
  async todo() {
    const ctx = this.ctx;
    const userid = ctx.user.userId;
    let { taskInstUuid, flowInstUuid } = ctx.query;
    let urlParams = '&taskInstUuid=' + taskInstUuid + '&flowInstUuid=' + flowInstUuid;
    // 权限检查
    if (await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.TODO.mask])) {
      ctx.redirect('/web/app/pt-mobile/mobile-workflow/mobile-worktodo.html?dotype=done2' + urlParams);
      return;
    } else if (await ctx.service.workflowTaskService.hasPermission(taskInstUuid, [constant.AclPermission.DONE.mask])) {
      ctx.redirect('/web/app/pt-mobile/mobile-workflow/mobile-workdone.html?dotype=todo2' + urlParams);
      return;
    } else {
      ctx.redirect('/web/app/pt-mobile/mobile-workflow/mobile-workshare.html?dotype=todo2' + urlParams);
      return;
    }
  }
}

module.exports = MobileWorkflowController;
