'use strict';
const LocalStrategy = require('passport-local').Strategy;

module.exports = app => {
  //拦截jwt url
  app.config.appMiddleware.splice(app.config.appMiddleware.indexOf('isAnnoymousRequest') + 1, 0, 'serverJwtAccessUrl');

  const config = app.config.wellappPassportServerJwt;
  config.passReqToCallback = true;

  const localStrategy = new LocalStrategy(config, (req, userName, jwt, done) => {
    const user = {
      provider: 'server-jwt',
      userName
    };
    user[config.passwordField] = jwt;
    // 这里不处理应用层逻辑，传给 app.passport.verify 统一处理
    app.passport.doVerify(req, user, done);
  });

  app.passport.use('server-jwt', localStrategy);

  // 处理用户信息
  app.passport.verify(async (ctx, user) => {
    let result;
    if (user && user.provider !== 'server-jwt') {
      return null;
    }
    try {
      result = await ctx.curl(`${app.config.backendURL}${config.checkTokenUrl}`, {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        dataAsQueryString: true,
        data: user
      });
    } catch (error) {
      throw new Error('登录超时');
    }

    if (result.status === 200) {
      if (result.data.code !== 0) {
        ctx.app.logger.error('JWT登录后端反馈异常：%j', result.data);
        if (result.data.code === -3001 || result.data.code === -3000) {
          //token无效
          throw new Error('Access token is invalid');
        }
        var msg = result.data.msg;
        if (msg.indexOf('isLocked:') > -1) {
          //密码错误多次被锁定
          var userId = msg.split('isLocked:')[1];
          msg = msg.split('isLocked:')[0];
          if (ctx.app.redis) {
            ctx.app.redis.set('logout_' + userId, userId);
          }
        }
        throw new Error(msg);
      } else if (result.data) {
        if (result.data.errorCode === 'SessionExpired') {
          //token无效
          throw new Error('Access token is invalid');
        }
        ctx.req.session.messages = []; // 登录成功则清掉失败的消息
        result.data.data._AUTH = 'jwt'; // 标记为jwt认证
        result.data.data._PROVIDER = 'server-jwt';
        return result.data.data;
      }
    }
    throw new Error('登录异常');
  });
};
