'use strict';

const Controller = require('wellapp-framework').Controller;

class MultiOrgGroupController extends Controller {

  async getGroupVo() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/group/getGroupVo`, {
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

  async addGroup() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/group/addGroup`, {
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

  async modifyGroup() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/group/modifyGroup`, {
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

  async deleteGroup() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/group/deleteGroup`, {
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

  async deleteGroups() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/group/deleteGroups`, {
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

  async getGroupPrivilegeResultTree() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/group/getGroupPrivilegeResultTree`, {
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

}

module.exports = MultiOrgGroupController;
