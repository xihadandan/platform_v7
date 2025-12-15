'use strict';

module.exports = (name, options) => {
  return async function jwtAccessUrl(ctx, next) {
    //判断是否为匿名请求路径
    if (
      !ctx.isAuthenticated() &&
      !ctx.isAnnoymousRequest &&
      isAllowAccessJwtUrls(ctx.app.config.allowAccessServerJwtUrls, ctx.req.url) &&
      ctx.req.query[ctx.app.config.wellappPassportServerJwt.passwordField]
    ) {
      let query = ctx.req.query, params = {};
      for (let key in query) {
        if (key != ctx.app.config.wellappPassportServerJwt.passwordField) {
          params[key] = query[key]
        }
      }
      ctx.session.returnTo = ctx.path + (Object.keys(params).length > 0 ? '?' + Object.keys(params).map(key => key + '=' + params[key]).join('&') : '');
      ctx.redirect(
        `/login/jwt?userName=sso&${ctx.app.config.wellappPassportServerJwt.passwordField}=${ctx.req.query[ctx.app.config.wellappPassportServerJwt.passwordField]
        }`
      );
      return;
    }

    await next();
  };
};

function isAllowAccessJwtUrls(urls, path) {
  if (urls) {
    for (let i = 0, len = urls.length; i < len; i++) {
      if (path === urls[i] || (path != '/' && urls[i] != '/' && new RegExp('^' + urls[i]).test(path))) {
        return true;
      }
    }
  }

  return false;
}
