'use strict';

const Controller = require('wellapp-framework').Controller;

class MultiOrgUserAccountController extends Controller {
  async resetUserPwd() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/user/account/resetUserPwd`, {
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

  async resetAllUserPwd() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/user/account/resetAllUserPwd`, {
        method: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async unlockAccount() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/user/account/unlockAccount?uuid=${this.ctx.req.body.uuid}`, {
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

  async lockAccount() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/user/account/lockAccount?uuid=${this.ctx.req.body.uuid}`, {
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

  async forbidAccount() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/user/account/forbidAccount?uuid=${this.ctx.req.body.uuid}`, {
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

  async unforbidAccount() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/user/account/unforbidAccount?uuid=${this.ctx.req.body.uuid}`, {
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
  async pwdUnlockAccount() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/user/account/pwdUnlockAccount?uuid=${this.ctx.req.body.uuid}`, {
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

  async saveRelationAccount() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/user/account/saveRelationAccount`, {
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
  async getRelationAccounts() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/user/account/getRelationAccounts`, {
        method: 'GET',
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
  async unboundAccount() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}` + ctx.request.url, {
        method: 'GET',
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
}

module.exports = MultiOrgUserAccountController;
