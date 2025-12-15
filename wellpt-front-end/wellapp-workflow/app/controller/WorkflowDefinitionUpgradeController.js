'use strict';

const Controller = require('wellapp-framework').Controller;

class WorkflowDefinitionUpgradeController extends Controller {
  async upgrade2v6_2_12() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/workflow/define/flow/upgrade/2v6_2_12', {
        method: 'GET',
        contentType: 'json',
        dataType: 'text',
        data: ctx.query
      });
      ctx.set('content-type', 'application/json; charset=utf-8');
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }
  async upgrade2v6_2_9_1() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/workflow/define/flow/upgrade/2v6_2_9_1', {
        method: 'GET',
        contentType: 'json',
        dataType: 'text',
        data: ctx.query
      });
      ctx.set('content-type', 'application/json; charset=utf-8');
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }
  async upgrade2v6_2_7() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/workflow/define/flow/upgrade/2v6_2_7', {
        method: 'GET',
        contentType: 'json',
        dataType: 'text',
        data: ctx.query
      });
      ctx.set('content-type', 'application/json; charset=utf-8');
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }

  async upgrade2v6_2_5() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/workflow/define/flow/upgrade/2v6_2_5', {
        method: 'GET',
        contentType: 'json',
        dataType: 'text',
        data: ctx.query
      });
      ctx.set('content-type', 'application/json; charset=utf-8');
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }
}

module.exports = WorkflowDefinitionUpgradeController;
