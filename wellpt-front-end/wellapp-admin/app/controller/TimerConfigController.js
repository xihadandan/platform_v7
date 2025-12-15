'use strict';

const Controller = require('wellapp-framework').Controller;

class TimerConfigController extends Controller {

  async get() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/timer/config/get', {
        method: 'get', 
		contentType: 'application/x-www-form-urlencoded', 
		dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s' , error);
    }
  }

  async selectdata() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/timer/config/selectdata', {
        method: 'get', 
		contentType: 'application/x-www-form-urlencoded', 
		dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s' , error);
    }
  }

  async save() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/timer/config/save', {
        method: 'post', 
		contentType: 'json', 
		dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('流程信息格式保存异常：%s' , error);
    }
  }

  async deleteAll() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/timer/config/deleteAll', {
        method: 'post', 
		contentType: 'application/x-www-form-urlencoded',
		dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('流程信息格式删除异常：%s' , error);
    }
  }
}

module.exports = TimerConfigController;
