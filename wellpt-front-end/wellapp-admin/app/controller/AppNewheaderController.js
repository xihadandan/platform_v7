'use strict';

const Controller = require('wellapp-framework').Controller;

class AppNewheaderController extends Controller {
  // 获取在线人员列表
  async getLoginingUser() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/getLoginingUser', {
        method: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('查询登录用户列表异常：%s', error);
    }
  }
}

module.exports = AppNewheaderController;
