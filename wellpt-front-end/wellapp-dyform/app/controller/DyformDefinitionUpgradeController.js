'use strict';

const Controller = require('wellapp-framework').Controller;

class DyformDefinitionUpgradeController extends Controller {
  async upgrade2v6_2_5() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/definition/upgrade/2v6_2_5', {
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
  async upgrade_v6_2_3_repair_json() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/definition/upgrade/2v6_2_3_repair_json', {
        timeout: 60 * 60 * 1000,
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
  async upgrade_v6_2_5_repair_readonly_style() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/definition/upgrade/2v6_2_5_repair_readonly_style', {
        timeout: 60 * 60 * 1000,
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

module.exports = DyformDefinitionUpgradeController;
