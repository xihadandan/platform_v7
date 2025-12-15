'use strict';

const Controller = require('wellapp-framework').Controller;

class GroupOrgController extends Controller {
  async addOrUpdate() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/multi/group/addOrUpdate', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('集团组织添加/更新异常：%s', error);
    }
  }

  async del() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/multi/group/del', {
        method: 'POST',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('集团组织删除异常：%s', error);
    }
  }
  async get() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/multi/group/get', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('集团组织获取异常：%s', error);
    }
  }

  async isEnable() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/multi/group/isEnable', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('集团组织启用禁用异常：%s', error);
    }
  }
}

module.exports = GroupOrgController;
