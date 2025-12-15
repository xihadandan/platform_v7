'use strict';

const Controller = require('wellapp-framework').Controller;

class RoleController extends Controller {
  async getRoleTree() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/security/role/getRoleTree`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async queryRoleByCurrentUserUnitId() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/security/role/queryRoleByCurrentUserUnitId`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async publishRoleUpdatedEvent() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/security/role/publishRoleUpdatedEvent`, {
        method: 'POST',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.service.pageDefinitionService.clearPageCacheData(null, null, 20);
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }
}

module.exports = RoleController;
