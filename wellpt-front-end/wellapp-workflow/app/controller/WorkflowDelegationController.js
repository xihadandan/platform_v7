'use strict';

const Controller = require('wellapp-framework').Controller;

class WorkflowDelegationController extends Controller {
  async vueDelegationView() {
    const { ctx, app } = this;
    let uuid = ctx.query.uuid || ctx.params.uuid || null;
    let settings = {
      uuid
    };
    if (uuid) {
      settings = await ctx.service.jsonDataService._call('workflowDelegationSettiongsService', 'getBean', [uuid]);
    }
    await ctx.render('workflow-work/delegation-settings.js', { settings });
  }

  async delegationView() {
    const { ctx, app } = this;
    let uuid = ctx.query.uuid || ctx.params.uuid || null;
    const data = {
      uuid
    };
    if (ctx.req.url.indexOf('/consult/') != -1 || ctx.req.url.indexOf('/view/') != -1) {
      data.dutyAgent = await ctx.service.jsonDataService._call('workflowDelegationSettiongsService', 'getBean', [uuid]);
      if (data.dutyAgent != null && data.dutyAgent.status == 2 && ctx.req.url.indexOf('/consult/') != -1) {
        data.duty_agent_consult = true;
      } else {
        data.duty_agent_view = true;
      }
    }
    data.themeFiles = await ctx.service.userThemeService.getThemeFiles();
    await ctx.render('/workflow/wf_delegation_settings.nj', data);
  }

  async consult() { }

  async get() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/delegation/settiongs/get', {
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

  async save() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/delegation/settiongs/save', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async activeAll() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/delegation/settiongs/activeAll', {
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

  async deactiveAll() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/delegation/settiongs/deactiveAll', {
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

  async deleteAll() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/delegation/settiongs/deleteAll', {
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

  async delegationActive() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/delegation/settiongs/delegationActive', {
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

  async delegationRefuse() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/delegation/settiongs/delegationRefuse', {
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
}

module.exports = WorkflowDelegationController;
