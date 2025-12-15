'use strict';

const Controller = require('wellapp-framework').Controller;

class WorkflowSimulationController extends Controller {

  async listTaskByFlowDefId() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/simulation/listTaskByFlowDefId', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getSimulationData() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/simulation/getSimulationData', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async simulationSubmit() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/simulation/simulationSubmit', {
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

  async cleanSimulationData() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/simulation/cleanSimulationData', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

}

module.exports = WorkflowSimulationController;
