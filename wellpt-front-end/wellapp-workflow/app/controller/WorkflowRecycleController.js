'use strict';

const Controller = require('wellapp-framework').Controller;

class workflowRecycleController extends Controller {
  /** 单个逻辑删除流程定义 */
  async logicalDelete() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/logical/delete', {
        method: 'post',
        contentType: 'json',
        dataAsQueryString: true,
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }

  /** 批量逻辑删除流程定义 */
  async logicalDeleteAll() {
    const { ctx, app } = this;

    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/logical/deleteAll', {
        method: 'post',
        contentType: 'json',
        dataAsQueryString: true,
        data: ctx.req.body
      });

      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }

  /** 单个恢复流程定义 */
  async recovery() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/recovery', {
        method: 'post',
        contentType: 'json',
        dataAsQueryString: true,
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }

  /** 批量恢复流程定义 */
  async recoveryAll() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/recoveryAll', {
        method: 'post',
        contentType: 'json',
        dataAsQueryString: true,
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }

  /** 单个物理删除流程定义 */
  async physicalDelete() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/physical/delete', {
        method: 'post',
        contentType: 'json',
        dataAsQueryString: true,
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }

  /** 批量物理删除流程定义 */
  async physicalDeleteAll() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/physical/deleteAll', {
        method: 'post',
        contentType: 'json',
        dataAsQueryString: true,
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }

  /** 获取流程定义定时清除设置 */
  async getCleanupConfig() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/cleanup/config/get', {
        method: 'post',
        contentType: 'json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }

  /** 保存流程定义定时清除设置 */
  async saveCleanupConfig() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/cleanup/config/save', {
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
}

module.exports = workflowRecycleController;
