'use strict';
const DingtalkQRStrategy = require('./dingtalk-qr-strategy');
module.exports = app => {
  const config = app.config.wellappPassportDingtalk;
  config.passReqToCallback = true;

  const dingtalkQRStrategy = new DingtalkQRStrategy(config, (req, code, sso, done) => {
    const user = {
      provider: 'dingtalkQR',
      code,
      sso
    };
    // 这里不处理应用层逻辑，传给 app.passport.verify 统一处理
    app.passport.doVerify(req, user, done);
  });
  app.passport.use('dingtalk-qr', dingtalkQRStrategy);

  // 处理用户信息
  app.passport.verify(async (ctx, user) => {
    if (user && user.provider !== 'dingtalkQR') {
      return null;
    }
    let result;
    // 后端通过编码登录
    try {
      result = await ctx.curl(app.config.backendURL + `/api/dingtalk/auth?code=${user.code}&sso=${user.sso}`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json'
      });
    } catch (error) {
      throw new Error('登录超时');
    }
    if (result.status === 200) {
      if (result.data.code !== 0) {
        throw new Error(result.data.msg);
      } else if (result.data) {
        ctx.req.session.messages = []; // 登录成功则清掉失败的消息
        const uri = ctx.query.uri;
        uri && (ctx.req.session.returnTo = uri);
        result.data.data._AUTH = 'jwt';
        result.data.data._PROVIDER = 'dingtalk-qr';
        result.data.data.loginSource = 5;
        if (ctx.query.state === 'snsapi_auth') {
          result.data.data.loginSource = 6;
        }
        return result.data.data;
      }
    }
    throw new Error('登录异常');
  });
};
