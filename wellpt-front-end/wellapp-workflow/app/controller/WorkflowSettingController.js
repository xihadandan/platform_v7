'use strict';

const Controller = require('wellapp-framework').Controller;

class WorkflowSettingController extends Controller {
  async save() {
    const { ctx, app } = this;
    try {
      app.redis.del(`FLOW_SETTING:${ctx.headers.system_id || ''}`);
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/setting/save', {
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

  async saveAll() {
    const { ctx, app } = this;
    try {
      app.redis.del(`FLOW_SETTING:${ctx.headers.system_id || ''}`);
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/setting/saveAll', {
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
}

module.exports = WorkflowSettingController;
