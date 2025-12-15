'use strict';

const Controller = require('wellapp-framework').Controller;

class WorkflowOpinionController extends Controller {
  async saveFlowOpinionCategories() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/opinion/saveFlowOpinionCategories', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }

  async deleteRecentOpinion() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/opinion/deleteRecentOpinion', {
        method: 'delete',
        contentType: 'application/json',
        dataType: 'json',
        dataAsQueryString: true,
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }
}

module.exports = WorkflowOpinionController;
