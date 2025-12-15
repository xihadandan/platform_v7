'use strict';

const Controller = require('wellapp-framework').Controller;

class TaskMaintainController extends Controller {
  async taskAssignmentCenter() {
    const { app } = this;
    this.ctx.redirect(app.config.xxlJobAdminAddress + '/xxl-job-admin/well/jobinfo?token=758233f351645f45415a7fe3dd340bde');
  }
}

module.exports = TaskMaintainController;
