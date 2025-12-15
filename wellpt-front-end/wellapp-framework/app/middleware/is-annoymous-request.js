'use strict';

module.exports = (name, options) => {
  const loginReg = new RegExp('/sys/[a-zA-Z_0-9-]+/(login|user_reg|user_forget_pwd)|/oauth/authorize|/login$|/login\\?');
  const annoySysIndexReg = new RegExp('/sys-public/[a-zA-Z_0-9-]+/index');
  return async function isAnnoymousRequest(ctx, next) {
    //判断是否为匿名请求路径
    if (ctx.isAnnoymousRequest === undefined) {
      ctx.isAnnoymousRequest = isAnnoymousUrl(ctx.app._ANNOYMOUSE_URLS, ctx.req.url);
    }

    /**
     * 来自匿名系统主页上或者系统登录页上的非匿名接口请求，标记为匿名请求，提供客户端token给后端服务认证
     */

    if (
      !ctx.isAnnoymousRequest &&
      !ctx.isAuthenticated() &&
      ctx.req.headers &&
      ctx.req.headers.referer &&
      !ctx.req.headers.referer.endsWith(ctx.req.url) &&
      (ctx.req.headers['x-set-req-client-token'] ||
        loginReg.test(ctx.req.headers.referer) ||
        annoySysIndexReg.test(ctx.req.headers.referer))
    ) {
      try {
        if (!(ctx.session.messages && ctx.session.messages.includes('ApiTokenExpired'))) {
          ctx.assertCsrf();
        }
        ctx.isAnnoymousRequest = true;
        ctx._SET_REQUEST_CLIENT_TOKEN_ = true;
      } catch (error) {
        ctx.app.logger.error('%s', error);
      }
    }

    await next();
  };
};

function isAnnoymousUrl(urls, path) {
  for (let i = 0, len = urls.length; i < len; i++) {
    if (path === urls[i] || (path != '/' && urls[i] != '/' && new RegExp('^' + urls[i]).test(path))) {
      return true;
    }
  }

  return false;
}
