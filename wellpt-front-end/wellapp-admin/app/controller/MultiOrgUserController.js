'use strict';

const Controller = require('wellapp-framework').Controller;

class MultiOrgUserController extends Controller {
  async modifyUser() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/user/modifyUser`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async getUserById() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/user/getUserById`, {
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

  async recomputeUserWorkInfoByEleId() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/user/recomputeUserWorkInfoByEleId`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async recomputeUserWorkInfoByVersions() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/user/recomputeUserWorkInfoByVersions`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async redirectUpdateUserIdnumber() {
    const { ctx, app } = this;
    try {
      await ctx.curl(`${app.config.backendURL}/api/org/user/updateIdnumber/${ctx.user.userId}`, {
        method: 'GET',
        contentType: 'json',
        dataAsQueryString: true,
        data: { idNumber: ctx.req.query.idNumber }
      });
      ctx.redirect(ctx.req.query.redirect || '/login?success');
    } catch (error) {
      this.app.logger.error(error);
    }
  }
}

module.exports = MultiOrgUserController;
