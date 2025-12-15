'use strict';

const Controller = require('wellapp-framework').Controller;

class DataCleanupController extends Controller {
  async index() {
    const { ctx, app } = this;
    if (ctx.hasAnyUserRole('ROLE_TENANT_ADMIN', 'ROLE_ADMIN')) {
      await this.ctx.render('data-cleanup/index.js', {});
    } else {
      await ctx.renderError('403');
    }
  }
  async systemClean() {
    const { ctx, app } = this;
    if (ctx.hasAnyUserRole('ROLE_ADMIN')) {
      // 只允许超管登录清理
      await this.ctx.render('data-cleanup/system-clean.js', {});
    } else {
      await ctx.renderError('403');
    }
  }


}

module.exports = DataCleanupController;
