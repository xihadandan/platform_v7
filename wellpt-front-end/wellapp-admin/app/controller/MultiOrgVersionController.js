'use strict';

const Controller = require('wellapp-framework').Controller;

class MultiOrgVersionController extends Controller {

  async addMultiOrgVersion() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/version/addMultiOrgVersion`, {
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

  async modifyOrgVersionBaseInfo() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/version/modifyOrgVersionBaseInfo`, {
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

  async getOrgVersionVo() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/version/getOrgVersionVo`, {
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

  async addNewOrgVersionForUpgrade() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/version/addNewOrgVersionForUpgrade?sourceVersionUuid=${this.ctx.req.body.sourceVersionUuid}&isSyncName=${this.ctx.req.body.isSyncName}`, {
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

  async activeOrgVersion() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/version/activeOrgVersion?orgVersionUuid=${this.ctx.req.body.orgVersionUuid}`, {
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

  async unactiveOrgVersion() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/version/unactiveOrgVersion?orgVersionUuid=${this.ctx.req.body.orgVersionUuid}`, {
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

  async getVersionById() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/version/getVersionById`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async queryVersionTreeOfMySubUnit() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/version/queryVersionTreeOfMySubUnit`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async getDefaultVersionBySystemUnitId() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/version/getDefaultVersionBySystemUnitId`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }
}

module.exports = MultiOrgVersionController;
