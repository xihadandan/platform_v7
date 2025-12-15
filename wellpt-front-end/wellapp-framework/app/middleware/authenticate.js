'use strict';
const qs = require('qs');

module.exports = (name, options) => {
  return async function authenticate(ctx, next) {
    ctx.AUTHENTICATE_PASSPORT = true;
    // if (ctx.helper.mobileDetect.mobile()) {
    //   //手机端，需要切换对应的手机配置
    //   let _options = options.mobile || ctx.app.config.authenticateOptions.local.mobile;
    //   return ctx.app.passport.authenticate(name, _options)(ctx, next);
    // }
    let _options = Object.assign({}, options);
    if (name === 'local') {
      let loginType = ctx.req.query.loginType || ctx.req.body.loginType;
      if (loginType === '3') {
        //超级管理员账号的配置
        return ctx.app.passport.authenticate(name, ctx.app.config.authenticateOptions.superadminLogin)(ctx, next);
      }

      let systemId = ctx.req.query.systemId || ctx.req.body.systemId;
      if (systemId) {
        ctx.SYSTEM_ID = systemId;
        // 指定系统登录
        _options.successRedirect = '/sys/' + systemId + '/login?success&system=' + systemId;
        _options.failureRedirect = '/sys/' + systemId + '/login?error';
        _options.logoutRedirect = '/sys/' + systemId + '/login?logout';
      }

      let loginSuccessRedirectUrl;
      if (ctx.req.headers.referer) {
        let referer = new URL(ctx.req.headers.referer);
        if (referer.search) {
          let refererQuery = qs.parse(decodeURIComponent(referer.search.substring(1)));
          if (refererQuery && refererQuery.loginSuccessRedirectUrl) {
            loginSuccessRedirectUrl = refererQuery.loginSuccessRedirectUrl;
          }
        }
      }
      if (ctx.req.body.loginSuccessRedirectUrl) {
        loginSuccessRedirectUrl = ctx.req.body.loginSuccessRedirectUrl;
      }
      if (
        !ctx.hasAnyUserRole('ROLE_TENANT_ADMIN', 'ROLE_ADMIN') &&
        new RegExp('/system_admin/[a-zA-Z_0-9-]+/index').test(loginSuccessRedirectUrl)
      ) {
        // 非管理员不允许登录成功后跳转到系统管理后台
        loginSuccessRedirectUrl = undefined;
      }
      if (loginSuccessRedirectUrl) {
        ctx.app.logger.info('当前登录成功后, 回调地址: ', loginSuccessRedirectUrl);
      }
      if (loginSuccessRedirectUrl) {
        _options.successRedirect = loginSuccessRedirectUrl;
      }
    }

    return ctx.app.passport.authenticate(name, _options)(ctx, next);
  };
};
