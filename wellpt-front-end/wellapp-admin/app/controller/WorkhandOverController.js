'use strict';

const Controller = require('wellapp-framework').Controller;

class WorkhandOverController extends Controller {
  async saveWorkHandover() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/wh/handover/saveWorkHandover', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('工作交接保存异常：%s', error);
    }
  }

  async deleteWorkHandover() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/wh/handover/deleteWorkHandover', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('工作交接删除异常：%s', error);
    }
  }

  async getWorkHandoverByUuid() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/wh/handover/getWorkHandoverByUuid', {
        method: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('工作交接获取异常：%s', error);
    }
  }

  async getWorkSettings() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/wh/handover/getWorkSettings', {
        method: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('执行时间获取异常：%s', error);
    }
  }

  async saveWorkSettings() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/wh/handover/saveWorkSettings', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('执行时间保存异常：%s', error);
    }
  }

  async getFlowDatasRecords() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/wh/handover/getFlowDatasRecords', {
        method: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('流程记录查询异常：%s', error);
    }
  }
}

module.exports = WorkhandOverController;
