'use strict';
const LocalStrategy = require('passport-local').Strategy;
const crypto = require('crypto');
var events = require('events');
module.exports = app => {
  const config = app.config.wellappPassportLocal;
  config.passReqToCallback = true;

  // 重写 eggPassport校验方法， cache捕捉异常反馈
  app.passport.doVerify = function (req, user, done) {
    const hooks = this._verifyHooks;
    if (hooks.length === 0) return done(null, user);
    (async () => {
      const ctx = req.ctx;
      let loginUser = null;
      for (const handler of hooks) {
        loginUser = await handler(ctx, user);
        if (loginUser) {
          break;
        }
      }
      done(null, loginUser);
    })().catch(e => {
      done(null, null, e.message);
    });
  };

  const localStrategy = new LocalStrategy(config, (req, username, password, done) => {
    const user = {
      provider: 'local',
      username,
      password,
      passwordEncryptKey: req.body.j_pwd_encrypt_key,
      passwordEncryptType: req.body.j_pwd_encrypt_type || '1',
      loginType: req.body.loginType || '1',
      loginUrl: req.body.loginUrl,
      systemId: req.body.systemId
    };
    if ((app.config.env == 'unittest' || app.config.env == 'local') && req.body.testTokenExpiration != undefined) {
      // 仅在测试环境或者开发环境，允许临时添加token超时参数
      user.tokenExpiration = req.body.testTokenExpiration;
    }
    // 这里不处理应用层逻辑，传给 app.passport.verify 统一处理
    app.passport.doVerify(req, user, done);
  });
  app.passport.use(localStrategy);

  // 处理用户信息
  app.passport.verify(async (ctx, user) => {
    let result;
    if (user && user.provider !== 'local') {
      return null;
    }

    // 支持切换账号请求URL处理
    var url = ctx.request.url;
    if (url == '/login') {
      url = url + '/' + user.loginType;
    }
    ctx.SYSTEM_ID = user.systemId;
    try {
      result = await ctx.curl(app.config.backendURL + url, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: user
      });
    } catch (error) {
      throw new Error('登录超时');
    }

    if (result.status === 200) {
      if (result.data.code !== 0) {
        ctx.app.logger.error('登录后端反馈异常：%j', result.data);
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
        const md5 = crypto.createHash('md5');
        md5.update(new Buffer(Math.random() + new Date().getTime() + result.data.data.loginName, 'binary'));
        const encodeStr = md5.digest('hex');
        ctx.cookies.set('_loginid', encodeStr, {
          httpOnly: false
        });
        result.data.data._PROVIDER = 'local';
        return result.data.data;
      }
    }
    throw new Error('登录异常');
  });
  app.passport.serializeUser(async (ctx, _user) => {
    // app.logger.info('登录返回用户信息: %j', _user);
    // console.log(ctx.session.externalKey);
    let {
      _AUTH,
      _PROVIDER,
      loginName,
      admin,
      superAdmin,
      systemUnitId,
      token,
      userId,
      userName,
      localUserName,
      userUuid,
      isAllowMultiDeviceLogin,
      tenantId,
      photoUuid,
      userNamePy,
      userSystemOrgDetails,
      loginNextAction,
      userIp
    } = _user;
    // 权限
    let roles = [];
    if (_user.authorities) {
      roles = [];
      for (let i = 0, len = _user.authorities.length; i < len; i++) {
        let authority = _user.authorities[i].authority;
        if (authority.indexOf('ROLE_page_sysdef') == -1) {
          roles.push(authority);
        }
      }
    }
    let mainJobPath = '',
      mainJobName,
      mainDeptName;
    if (_user.mainJob) {
      mainJobPath = _user.mainJob.deptNamePath + '/' + _user.mainJob.name;
      mainJobName = _user.mainJob.name;
      mainDeptName = _user.mainJob.deptName;
    }
    app.appEmitter.emit('event.UserLoginSuccess', _user); //触发用户登录成功事件
    let user = {
      _AUTH,
      _PROVIDER,
      loginName,
      admin,
      superAdmin,
      systemUnitId,
      token,
      userId,
      userName,
      localUserName,
      userUuid,
      isAllowMultiDeviceLogin,
      photoUuid,
      tenantId,
      roles,
      userNamePy,
      userSystemOrgDetails,
      userIp
    };
    if (loginNextAction) {
      // ctx.req.session.messages = [loginNextAction.code];
      if (loginNextAction.code.startsWith('forceModifyPassword')) {
        ctx.cookies.set('force_modify_password_code', loginNextAction.code, {
          httpOnly: false
        });
      }
    }

    user = JSON.parse(
      JSON.stringify(user, (key, value) => {
        return value == null || value == '' ? undefined : value;
      })
    );
    // 修正部分账号没有组织用户信息
    if (user.userSystemOrgDetails.details == undefined) {
      user.userSystemOrgDetails.details = [];
    }
    ctx.service.loginLogService.commitLoginLog(user);
    return user;
  });
  app.passport.deserializeUser(async (ctx, user) => {
    return user;
  });
};
