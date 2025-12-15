'use strict';
const qs = require('qs');
module.exports = (name, options) => {
  return async function sessionReturnTo(ctx, next) {
    let tokenExpired = ctx.session && ctx.session.messages && ctx.session.messages.indexOf('ApiTokenExpired') != -1; //是否是访问后端接口时候token失效的情况
    if (!tokenExpired && !ctx.isAuthenticated() && !ctx.isAnnoymousRequest) {
      let returnTo;
      // 未授权的情况下，通过页面直接跳转登录页的情况下，要记录来源页，等待登录后进行重定向
      if (ctx.req.method === 'GET' && ctx.req.url.endsWith('/login')) {
        let _referer = ctx.request.headers['referer'];
        if (_referer && !_referer.endsWith('/login')) {
          returnTo = _referer;
        }
      } else if (
        ctx.request.method === 'GET' &&
        ctx.request.headers['x-requested-with'] != 'XMLHttpRequest' &&
        ctx.request.url != '/' &&
        ctx.request.url.indexOf(ctx.app.config.static.prefix) != 0 &&
        ctx.request.headers['accept'] &&
        ctx.request.headers['accept'].indexOf('text/html') != -1
        //&& ctx.request.headers['sec-fetch-dest'] === 'document'
      ) {
        //FIXME: ip 访问没有sec-feth-dest属性
        returnTo = ctx.request.originalUrl || ctx.request.url;
      }
      // 通过ajax请求，但是无权限的情况下，会记录当前发起请求的页面，以便用户登录后重定向回该页面
      else if (ctx.request.headers['x-requested-with'] === 'XMLHttpRequest') {
        returnTo = ctx.request.headers['loginSuccessRedirect'] || ctx.request.headers['referer'];
      }
      if (ctx.session.returnTo) {
        ctx._perRequestReturnTo = returnTo;
      } else {
        ctx.session.returnTo = returnTo;
      }
    }

    if (ctx.isAuthenticated() && ctx.session.returnTo && ctx.app.config.authenticateOptions.local.successRedirect == ctx.req.url) {
      ctx.redirect(ctx.session.returnTo);
      ctx.session.returnTo = null;
      return;
    }

    await next();
  };
};
