'use strict';
const jwt = require('jsonwebtoken');

module.exports = options => {
  return async function authenticateInterceptor(ctx, next) {
    ctx.cookies.set('language', ctx.app.config.i18n.defaultLocale, { httpOnly: false });
    ctx.cookies.set('ctx', '', { httpOnly: false });
    ctx.cookies.set('backend.url', global.process.env.wellapp_backend_url_outer || ctx.app.config.backendURL || '', { httpOnly: false });
    ctx.cookies.set('staticPrefix', ctx.app.config.static.prefix, { httpOnly: false });
    ctx.cookies.set('fileupload.maxChunkSize', ctx.app.config.fileupload.maxChunkSize, { httpOnly: false });
    if (!ctx.isAuthenticated() && !ctx.isAnnoymousRequest) {
      try {
        // 拦截 token 访问
        let accessToken = undefined;
        if (ctx.req.headers.authorization) {
          var matches = token.match(/Bearer\s(\S+)/);
          accessToken = matches[1];
        }
        // else if (ctx.req.query.accessToken) {
        //   accessToken = ctx.req.query.accessToken;
        // }
        if (accessToken) {
          jwt.verify(
            accessToken,
            ctx.app.jwtSecretKey,
            {
              algorithm: 'HS256'
            },
            function (err, decoded) {
              if (err) {
                ctx.app.logger.error('accessToken 访问异常: %s', err.message);
              }
              if (!err && decoded && decoded.user) {
                ctx.login(decoded.user);
              }
            }
          );
        }
      } catch (error) {
        ctx.app.logger.error('accessToken 访问异常 ', error);
      }
    }
    if (ctx.isAuthenticated() || ctx.isAnnoymousRequest) {
      if (ctx.isAnnoymousRequest) {
        //匿名地址 但有传token 表单详情查看
        if (ctx.query.jwt != null && ctx.query.jwt != undefined && ctx.query.jwt.length > 0) {
          ctx.cookies.set('_auth', 'jwt', { httpOnly: false });
          ctx.cookies.set('jwt', ctx.query.jwt, { httpOnly: false });
          ctx.cookies.set('access_token', ctx.query.jwt, { httpOnly: false });
        }
      }
      if (ctx.isAuthenticated()) {
        ctx.USER_ROLES = ctx.user.roles || [];
        // ctx.cookies.set('USER_ROLES', JSON.stringify(ctx.USER_ROLES), { httpOnly: false });
        ctx.cookies.set('cookie.current.userName', encodeURIComponent(ctx.user.userName), { httpOnly: false });
        ctx.cookies.set('cookie.current.userId', ctx.user.userId || '', { httpOnly: false });
        ctx.cookies.set('cookie.current.forbidMultiDeviceLogin', ctx.user.isAllowMultiDeviceLogin === false, { httpOnly: false });
        ctx.cookies.set('_auth', ctx.user._AUTH, { httpOnly: false });
        if (ctx.user._AUTH === 'jwt') {
          ctx.cookies.set('jwt', ctx.user.token, { httpOnly: false });
        } else {
          ctx.cookies.set('access_token', ctx.user.token, { httpOnly: false });
        }
        let logout_cookie = ctx.cookies.get('logout');
        if (logout_cookie != undefined && logout_cookie != null && logout_cookie == 'login') {
          //账号锁定退出，多端登录情况下刷新 强制退出
          if (ctx.app.redis && ctx.user != undefined) {
            let logoutFlag = await ctx.app.redis.get('logout_' + ctx.user.userId);
            if (logoutFlag != null && logoutFlag != undefined && logoutFlag.length > 0) {
              ctx.req.session.messages.push('因账号锁定，当前登录已被强制退出，请重新登录！');
              let _options = ctx.app.config.authenticateOptions[ctx.user._PROVIDER || 'local'];
              let redirectUrl = _options.logoutSuccessUrl || '/login?logout';
              if (ctx.helper.mobileDetect.mobile() && _options.mobile && _options.mobile.logoutRedirect) {
                redirectUrl = _options.mobile.logoutRedirect;
              }
              await ctx.redirect(redirectUrl.indexOf('?') != -1 ? redirectUrl + '&error' : redirectUrl + '?error');
              ctx.cookies.set('logout', 'logout', { httpOnly: false });
              ctx.logout();
              return;
            }
          }
        }
      }

      await next();
    } else {
      let param = '';
      if (ctx.request.headers['cookie'] && ctx.request.headers['cookie'].indexOf(ctx.app.config.session.key) != -1 && ctx.app.redis) {
        // 禁止多设备同时登录的情况下
        let sessionid = ctx.cookies.get(ctx.app.config.session.key, { encrypt: true });
        let ans = await ctx.app.redis.get('session:kick:ip' + sessionid);
        // debugger;
        if (ans) {
          let username = ctx.cookies.get('cookie.current.username', { signed: false });
          ctx.app.logger.info('用户[%s], 由于其他IP=[%s]设备登录，强制下线!', username, ans);
          ctx.app.redis.del('session:kick:ip' + sessionid);
          param = '?forbidLoginedSameTime';
        } else {
          let logout = ctx.cookies.get('logout');
          if (logout != undefined && logout == 'logout') {
            // lockedMsg /账号锁定退出，多端登录情况下刷新 强制退出
            param = '?lockedMsg';
            ctx.cookies.set('lockedMsg', 'lockedMsg');
          } else {
            // session 过期被清理
            param = '?sessionExpired';
          }
        }
      }
      let _options = ctx.app.config.authenticateOptions[ctx.req.query._provider || 'local'];
      let redirectUrl = _options.logoutSuccessUrl || (ctx.SYSTEM_ID ? '/sys/' + ctx.SYSTEM_ID + '/login?logout' : '/login?logout');
      if (ctx.helper.mobileDetect.mobile() && _options.mobile && _options.mobile.logoutRedirect) {
        redirectUrl = _options.mobile.logoutRedirect;
      }
      await ctx.redirect(
        (redirectUrl.indexOf('?') != -1 ? redirectUrl + param.replace('?', '&') : redirectUrl + param) +
          (ctx._perRequestReturnTo ? '&loginSuccessRedirectUrl=' + encodeURIComponent(ctx._perRequestReturnTo) : '')
      );
    }
  };
};
