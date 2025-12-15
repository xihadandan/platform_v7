'use strict';

const Controller = require('wellapp-framework').Controller;

class WorkflowApproveController extends Controller {

  async isAllowedConvertDyformDataByBotRuleId() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/approve/isAllowedConvertDyformDataByBotRuleId', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async convertDyformDataByBotRuleId() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/approve/convertDyformDataByBotRuleId', {
        method: 'post',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async sendToApprove() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/approve/sendToApprove', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

}

module.exports = WorkflowApproveController;
