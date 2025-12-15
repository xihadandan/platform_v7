'use strict';

const Controller = require('wellapp-framework').Controller;

class FlowDefinitionController extends Controller {
  async copy() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/copy', {
        method: 'POST',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('流程定义删除异常：%s', error);
    }
  }

  async delete() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/delete', {
        method: 'POST',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('流程定义删除异常：%s', error);
    }
  }

  async listLogs() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/listLogs', {
        method: 'get',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async getLogCompareXml() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getLogCompareXml', {
        method: 'get',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async getLogManageOperation() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/log/manage/operation/getLogManageOperation', {
        method: 'get',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async saveFlowListExportLogManageOperation() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/log/manage/operation/saveFlowListExportLogManageOperation', {
        method: 'get',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }
}

module.exports = FlowDefinitionController;
