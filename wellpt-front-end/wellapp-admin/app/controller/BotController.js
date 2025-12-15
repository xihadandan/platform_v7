'use strict';

const Controller = require('wellapp-framework').Controller;

class BotController extends Controller {
  async ruleConfigIsDebug() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/bot/ruleConfig/isDebug', {
        method: 'POST', contentType: 'json', dataType: 'json',
        data: ctx.req.body,
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('判断是否单据转换规则可调试异常：%s' , error);
    }
  }


}

module.exports = BotController;
