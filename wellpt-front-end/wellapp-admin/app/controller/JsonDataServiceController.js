'use strict';

const Controller = require('wellapp-framework').Controller;
/**
 * jds服务接口转发
 */
class JsonDataServiceController extends Controller {
  async forward() {
    const { ctx } = this;
    // const result = { code: 0, data: null, success: true, msg: 'success' };
    if (ctx.service[ctx.request.body.serviceName] && ctx.service[ctx.request.body.serviceName][ctx.request.body.methodName]) {
      ctx.body = await ctx.service[ctx.request.body.serviceName][ctx.request.body.methodName].apply(ctx.service[ctx.request.body.serviceName],
        ctx.request.body.args ? JSON.parse(ctx.request.body.args) : []);
    } else { // 转发后端服务返回
      ctx.body = await ctx.service.jsonDataService.invoke(ctx.request.body);
    }

  }

  async pinYin() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services/pinYin', {
        method: 'POST', contentType: 'text', dataType: 'text',
        data: ctx.req.body,
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('获取拼音异常：%s' , error);
    }
  }
}

module.exports = JsonDataServiceController;
