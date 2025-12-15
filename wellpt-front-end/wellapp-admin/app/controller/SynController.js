'use strict';

const Controller = require('wellapp-framework').Controller;

class SynController extends Controller {
  async synTriggerDrop() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/syn/trigger/drop', {
        method: 'POST', contentType: 'json', dataType: 'json',
        data: ctx.req.body,
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('同步触发器删除异常：%s' , error);
    }
  }

  async synTriggerCreate() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/syn/trigger/create', {
        method: 'POST', contentType: 'json', dataType: 'json',
        data: ctx.req.body,
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('同步触发器创建异常：%s' , error);
    }
  }
  async synTriggerDisable() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/syn/trigger/disable', {
        method: 'POST', contentType: 'json', dataType: 'json',
        data: ctx.req.body,
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('同步触发器停用异常：%s' , error);
    }
  }

  async synTriggerEnable() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/syn/trigger/enable', {
        method: 'POST', contentType: 'json', dataType: 'json',
        data: ctx.req.body,
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('同步触发器启用异常：%s' , error);
    }
  }
  async synTriggerReGenerate() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/syn/trigger/reGenerate', {
        method: 'POST', contentType: 'json', dataType: 'json',
        data: ctx.req.body,
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('同步触发器重新生成异常：%s' , error);
    }
  }

}

module.exports = SynController;
