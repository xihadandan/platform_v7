'use strict';
const LocalStrategy = require('passport-local').Strategy;

module.exports = app => {
  //拦截token异常
  const index = app.config.appMiddleware.indexOf('authenticateInterceptor');
  app.config.appMiddleware.splice(index + 1, 0, 'idnumberTokenFailed');
  app.config.appMiddleware.splice(app.config.appMiddleware.indexOf('isAnnoymousRequest') + 1, 0, 'idnumberTokenUrl');

  const config = app.config.wellappPassportIdnumber;
  config.passReqToCallback = true;

  const localStrategy = new LocalStrategy(config, (req, idNumber, accessToken, done) => {
    const user = {
      provider: 'idnumber-token',
      idNumber,
      accessToken,
      appId: req.query.appId,
      loginType: req.query.loginType || '1',
      encodeIdNumber: !!config.encodeIdNumber ? 1 : 0 // 是否base64编码身份证号
    };
    // 这里不处理应用层逻辑，传给 app.passport.verify 统一处理
    app.passport.doVerify(req, user, done);
  });

  app.passport.use('idnumber-token', localStrategy);

  // 处理用户信息
  app.passport.verify(async (ctx, user) => {
    let result;
    if (user && user.provider !== 'idnumber-token') {
      return null;
    }
    try {
      result = await ctx.curl(app.config.backendURL + '/loginByIdNumberToken', {
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
        ctx.app.logger.error('登录后端反馈异常：%j', result.data);
        if (result.data.code === -3001 || result.data.code === -3000) {
          //token无效
          throw new Error('Access token is invalid');
        } else if (result.data.code === -6000) {
          throw new Error('Not foud user by id number : ' + user.idNumber); // 无用户关联此身份证号，需要跳转到用户
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
        ctx.req.session.messages = []; // 登录成功则清掉失败的消息
        result.data.data._AUTH = 'jwt'; // 标记为jwt认证
        result.data.data._PROVIDER = 'idnumber-token';
        return result.data.data;
      }
    }
    throw new Error('登录异常');
  });
};
