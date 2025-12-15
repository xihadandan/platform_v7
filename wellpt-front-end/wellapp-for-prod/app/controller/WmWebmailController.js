'use strict';

const Controller = require('wellapp-framework').Controller;

class WmWebMailController extends Controller {
  async getMail() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/webmail/get', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          mailboxUuid: ctx.query.mailboxUuid
        }
      });
      if (result.data) {
        ctx.body = result.data;
      }
    } catch (error) {
      app.logger.error('获取邮件详情异常：%s', error);
    }
  }

  async getMailConfig() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/webmail/getMailConfig', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json'
      });
      if (result.data) {
        ctx.body = result.data;
      }
    } catch (error) {
      app.logger.error('获取邮件详情异常：%s', error);
    }
  }

  async refush() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/webmail/refush', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json'
      });
      if (result.data) {
        ctx.body = result.data;
      }
    } catch (error) {
      app.logger.error('获取邮件更新数量异常：%s', error);
    }
  }
}

module.exports = WmWebMailController;
