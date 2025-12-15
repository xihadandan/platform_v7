'use strict';

const Controller = require('wellapp-framework').Controller;

class DocumentLinkController extends Controller {
  async view() {
    const { ctx, app } = this;
    try {
      const uriParts = ctx.request.originalUrl.split('/');
      const uuid = uriParts[uriParts.length - 1];
      const result = await ctx.curl(app.config.backendURL + '/api/document/link/checkAndGetTargetUrl/' + uuid, {
        method: ctx.req.method,
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      var targetUrl = result.data.data;
      if (targetUrl != null) {
        if (targetUrl.indexOf('?') > 0) {
          targetUrl += '&docLinkUuid=' + uuid.replace('?', '&');
        } else {
          targetUrl += '?docLinkUuid=' + uuid.replace('?', '&');
        }
        ctx.redirect(targetUrl);
      } else {
        ctx.body = '你当前登录的账号无权限访问该数据，请用有权限的账号访问!';
      }
    } catch (error) {
      this.app.logger.error('查看文档链接异常：%s', error);
    }
  }
}

module.exports = DocumentLinkController;
