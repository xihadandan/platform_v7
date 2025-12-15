'use strict';

const Controller = require('wellapp-framework').Controller;
const svgCaptcha = require('svg-captcha-smooth');
const lodash = require('lodash');

class LoginController extends Controller {
  async login() {
    const { ctx, app } = this;

    if (ctx.query.hasOwnProperty('error') && ctx.path === '/login') {
      let referer = ctx.req.headers.referer;
      if (referer) {
        if (referer.indexOf('/login/system') !== -1) {
          // 返回产品登录页
          ctx.redirect(referer.substring(referer.indexOf('/login')) + '?error');
          return;
        } else if (referer.indexOf('/superadmin/login') !== -1) {
          // 超管登录页验证失败的情况下
          ctx.redirect('/superadmin/login?error');
          return;
        }
      }
    }
    let error = null;
    if (ctx.query.hasOwnProperty('redirectOauth2GrantPasswordError')) {
      // oauth2 密码Post登录异常的重定向
      let _referer = ctx.req.headers.referer;
      if (_referer.indexOf('?') == -1) {
        _referer += '?';
      }
      if (_referer.indexOf('&error') == -1) {
        _referer += '&error';
      }
      ctx.redirect(_referer);
      return;
    }
    if (ctx.query.hasOwnProperty('success') && ctx.isAuthenticated()) {
      // 表示登录成功了
      if (ctx.session.returnTo) {
        ctx.redirect(ctx.session.returnTo);
        ctx.session.returnTo = null;
      } else if (ctx.user.admin) {
        // 管理员
        if (app.config.adminLoginSuccessUrl) {
          ctx.redirect(app.config.adminLoginSuccessUrl);
        } else {
          ctx.redirect('/web/app/pt-mgr.html?pageUuid=ac525dcd-50b7-42e9-95b7-658b117ac19b');
        }
      } else if (ctx.user.superAdmin) {
        ctx.redirect('/web/app/pt-mgr.html?pageUuid=2f852b9e-4564-4f5b-bc7e-bb57f639cbe3');
      } else if (this.config.loginRedirectUrl) {
        ctx.redirect(this.config.loginRedirectUrl);
      } else {
        ctx.redirect('/web/app/security_homepage');
      }
      //账号锁定 redis标记置空
      if (ctx.app.redis) {
        let logoutFlag = ctx.app.redis.get('logout_' + ctx.user.userId);
        if (logoutFlag != undefined) {
          ctx.app.redis.set('logout_' + ctx.user.userId, '');
          ctx.cookies.set('logout', 'login', {
            httpOnly: false
          });
        }
      }

      return;
    }
    let loginType = 1,
      id = 'T001';
    if (ctx.path === '/superadmin/login') {
      loginType = 3;
      id = 'S0000000000';
    } else if (ctx.path.startsWith('/login/unit/')) {
      let loginUrl = ctx.path.split('login/unit/')[1];
      loginUrl = loginUrl.split('?')[0];
      let loginSettings = await ctx.service.appLoginConfService.loginPageSettings(loginUrl);
      if (null == loginSettings || (loginSettings && loginSettings.unitLoginPageSwitch == '0')) {
        ctx.redirect('/error/404');
      } else if (loginSettings && loginSettings.unitLoginPageSwitch == '1') {
        id = loginSettings.systemUnitId;
      }
    } else if (ctx.query.hasOwnProperty('error') && ctx.req.headers.referer && ctx.req.headers.referer.indexOf('/login/unit/') !== -1) {
      let loginUrl = ctx.req.headers.referer.split('login/unit/')[1];
      loginUrl = loginUrl.split('?')[0];
      let loginSettings = await ctx.service.appLoginConfService.loginPageSettings(loginUrl);
      if (null == loginSettings) {
        ctx.redirect('/error/404');
      } else if (loginSettings.unitLoginPageSwitch == '1') {
        ctx.redirect('/login/unit/' + loginUrl + '?error');
      }
    }
    let loginConfig = await ctx.service.appLoginConfService.getLoginPageConfig(id);

    if (ctx.query.hasOwnProperty('tokenExpired') || ctx.query.hasOwnProperty('sessionExpired')) {
      ctx.set('tokenExpired', 'true');
      error = '登录失效，请重新登录!';
      if (ctx.helper.mobileDetect.mobile() != null) {
        this.logout();
      }
    }
    if (ctx.query.hasOwnProperty('forbidLoginedSameTime')) {
      ctx.set('forbidLoginedSameTime', 'true');
      error = '您的账号已登录其他设备，请重新登录!';
    }
    if (ctx.query.hasOwnProperty('error')) {
      error = ctx.req.session.messages ? ctx.req.session.messages[ctx.req.session.messages.length - 1] : '';
      error = ctx.__(error); //国际化语言
    }
    if (!loginConfig) {
      error = '登录服务异常';
    }
    if (loginConfig && 'SessionExpired' == loginConfig.errorCode) {
      loginConfig = await ctx.service.appLoginConfService.unitLoginPageSetting(id);
    }
    // 验证码
    if (loginConfig && loginConfig.data.loginBoxAccountCode === '0') {
      ctx.cookies.set('loginBoxAccountCodeType', loginConfig.data.loginBoxAccountCodeType, {
        httpOnly: false
      });
      let cap = await this.createCaptcha(loginConfig.data.loginBoxAccountCodeType);
      loginConfig.data.svgCaptcha = cap.data;
    }

    let notDevelopement = app.config.env !== 'local' && app.redis;
    if (loginConfig && loginConfig.data) {
      if (loginConfig.data.pageBackgroundImageBase64) {
        // 非开发环境下，图片base64不返回前端，已在web启动时候自动写为图片文件到工程内
        loginConfig.data.pageBackgroundImageBase64 = notDevelopement
          ? `url(/static/login_page_bg_${loginConfig.data.systemUnitId}.png)`
          : `url(data:image/png;base64,${loginConfig.data.pageBackgroundImageBase64.replace(/\r|\n/g, '')})`;
      }
      if (loginConfig.data.pageLogoBase64) {
        loginConfig.data.pageLogoBase64 = notDevelopement
          ? loginConfig.data.pageStyle !== '_right'
            ? `url(/static/login_page_logo_${loginConfig.data.systemUnitId}.png)`
            : `/static/login_page_logo_${loginConfig.data.systemUnitId}.png`
          : loginConfig.data.pageStyle !== '_right'
          ? `url(data:image/png;base64,${loginConfig.data.pageLogoBase64.replace(/\r|\n/g, '')})`
          : `data:image/png;base64,${loginConfig.data.pageLogoBase64.replace(/\r|\n/g, '')}`;
      }
    }

    await ctx.render(ctx.query.hasOwnProperty('i') ? 'internet_user_login.nj' : 'login.nj', {
      loginConfig,
      loginType,
      error
    });
  }

  async systemLogin() {
    const { ctx, app } = this;
    let { system, tenant } = ctx.params;
    app.logger.info('loginUrl: %s , session.returnTo: %s', ctx.req.url, ctx.session.returnTo);
    if (ctx.query.hasOwnProperty('success') && ctx.isAuthenticated()) {
      // 表示登录成功了
      let system = ctx.query.system;

      // 非管理员不允许跳转到系统管理后台
      if (
        ctx.session.returnTo &&
        !ctx.hasAnyUserRole('ROLE_TENANT_ADMIN', 'ROLE_ADMIN') &&
        system != undefined &&
        new RegExp('/system_admin/[a-zA-Z_0-9-]+/index').test(ctx.session.returnTo)
      ) {
        ctx.session.returnTo = null;
      }

      if (ctx.session.returnTo) {
        ctx.redirect(ctx.session.returnTo);
        ctx.session.returnTo = null;
      } else if (ctx.hasAnyUserRole('ROLE_TENANT_ADMIN', 'ROLE_ADMIN') && system != undefined) {
        // 租户管理员，前往系统管理后台
        if (ctx.hasAnyUserRole('ROLE_TENANT_ADMIN')) {
          ctx.redirect('/system_admin/' + system + '/index');
          return;
        }
        // TODO: 超级管理员
        ctx.redirect('/system_admin/' + system + '/index');
      } else if (this.config.loginRedirectUrl) {
        ctx.redirect(this.config.loginRedirectUrl);
      } else if (system != undefined) {
        // 返回系统首页
        ctx.redirect('/sys/' + system + '/index');
      } else {
        ctx.redirect('/web/app/security_homepage');
      }
      //账号锁定 redis标记置空
      if (ctx.app.redis) {
        let logoutFlag = ctx.app.redis.get('logout_' + ctx.user.userId);
        if (logoutFlag != undefined) {
          ctx.app.redis.set('logout_' + ctx.user.userId, '');
          ctx.cookies.set('logout', 'login', {
            httpOnly: false
          });
        }
      }

      return;
    } else {
      // 已认证登录过，直接跳转到成功回调地址
      if (
        !(ctx.query.hasOwnProperty('tokenExpired') || ctx.query.hasOwnProperty('sessionExpired')) &&
        ctx.isAuthenticated() &&
        ctx.req.query.loginSuccessRedirectUrl
      ) {
        ctx.redirect(decodeURIComponent(ctx.req.query.loginSuccessRedirectUrl));
        return;
      }
      let error = undefined;
      if (ctx.query.hasOwnProperty('error')) {
        error = ctx.req.session.messages ? ctx.req.session.messages[ctx.req.session.messages.length - 1] : '';
        // error = ctx.__(error); //国际化语言
      }
      if (ctx.query.hasOwnProperty('tokenExpired') || ctx.query.hasOwnProperty('sessionExpired')) {
        ctx.set('tokenExpired', 'true');
        error = '登录失效，请重新登录!';
        ctx.logout();
      }
      // 渲染登录页
      // 获取系统的登录页配置
      let result = null;
      if (ctx.req.url.startsWith('/login')) {
        // 普通登录页：从【平台产品】的系统下获取登录页
        system = 'PRD_PT';
        tenant = 'T001';
      }
      result = await ctx.service.appLoginConfService.getEnableTenantSystemLoginPagePolicy(system, tenant);
      if (result == null && system != 'PRD_PT') {
        // 系统下无可用的登录配置，则使用平台默认登录页
        result = await ctx.service.appLoginConfService.getEnableTenantSystemLoginPagePolicy('PRD_PT', 'T001');
      }
      if (result) {
        if (system) {
          ctx._SET_REQUEST_CLIENT_TOKEN_ = true;
          let systemInfo = await ctx.service.appProdIntegrationService.getTenantSystemInfo(tenant, system);
          ctx.SYSTEM_ENABLE_LOCALE = systemInfo.enableLocale;
          if (systemInfo.enableLocale) {
            // 判断 cookies 中是否设置有语言
            let cookieLocale = ctx.cookies.get(`locale.${system}`, { signed: false });
            if (cookieLocale == undefined) {
              ctx.cookies.set(`locale.${system}`, systemInfo.defaultLocale, {
                httpOnly: false
              });
            }
          }
        }

        let { title, defJson, loginPolicy } = result;
        let referer = ctx.req.headers.referer;
        if (referer == undefined || referer.indexOf('/system_admin/') == -1) {
          if (ctx.query.hasOwnProperty('logout')) {
            // 非管理后台的预览地址打开
            let _sessionid = ctx.session._externalKey;
            if (app.sessionStore && ctx.session.returnTo == null) {
              app.sessionStore.destroy(_sessionid);
            }
            ctx.logout();
          }
        }
        // if (ctx.query.hasOwnProperty('redirectUrl')) {
        //   ctx.session.returnTo = decodeURIComponent(ctx.query.redirectUrl);
        // }
        let loginConfig = JSON.parse(defJson);
        if (loginConfig.config.loginWin && loginConfig.config.loginWin.enableI18nLanguage) {
          loginConfig.config.loginWin.enableI18nLanguage = ctx.SYSTEM_ENABLE_LOCALE === true;
        }
        await ctx.render('login/index.js', {
          loginConfig,
          loginPolicy,
          // SYSTEM_ID: system,
          errorMsg: error,
          title
        });
      } else {
        await ctx.renderError('404', '找不到相关配置');
      }
    }
  }

  async systemUserReg() {
    const { ctx, app } = this;
    let { system, tenant } = ctx.params;
    let result = await ctx.service.appLoginConfService.getEnableTenantSystemLoginPagePolicy(system, tenant);

    if (result) {
      let { title, defJson, loginPolicy } = result;
      await ctx.render('login/user-reg.js', {
        loginPolicy,
        SYSTEM_ID: system,
        title: '注册'
      });
    } else {
      await ctx.renderError('404', '找不到相关配置');
    }
  }

  async systemUserForgetPwd() {
    const { ctx, app } = this;
    let { system, tenant } = ctx.params;
    let result = await ctx.service.appLoginConfService.getEnableTenantSystemLoginPagePolicy(system, tenant);

    if (result) {
      let { title, defJson, loginPolicy } = result;
      await ctx.render('login/user-forget-pwd.js', {
        loginPolicy,
        SYSTEM_ID: system,
        title: '忘记密码'
      });
    } else {
      await ctx.renderError('404', '找不到相关配置');
    }
  }

  async systemUserModifyPwd() {
    const { ctx, app } = this;
    await ctx.render('user-manager/modify-account-password.js', {
      title: '修改密码',
      modifyCode: ctx.req.session.messages ? ctx.req.session.messages[ctx.req.session.messages.length - 1] : undefined
    });
  }

  async createCaptcha(loginBoxAccountCodeType, expiredTime) {
    let cap = null,
      height = this.ctx.query.height || 30;
    cap = svgCaptcha.create({
      size: 4, // 验证码长度
      width: 75,
      height,
      fontSize: 30,
      ignoreChars:
        loginBoxAccountCodeType == 'letterAndNumber' || loginBoxAccountCodeType == 'numberLetter'
          ? '0oO1ilI'
          : '0oO1ilIabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ', // 验证码字符中排除 0o1i有26个英文字母
      noise: 1, // 干扰线条的数量
      color: true, // 验证码的字符是否有颜色，默认没有，如果设定了背景，则默认有
      background: '#eee' // 验证码图片背景颜色
    });

    this.ctx.session.captcha = cap.text;
    if (expiredTime == undefined) {
      this.ctx.session.captcha_expired_time = new Date().getTime() + 1000 * 60 * 5; // 默认5分钟
    } else {
      this.ctx.session.captcha_expired_time = new Date().getTime() + 1000 * parseInt(expiredTime); // 默认5分钟
    }

    return cap;
  }

  async captcha() {
    if (this.ctx.query.hasOwnProperty('verify')) {
      let code = this.ctx.query.verify;
      if (new Date().getTime() > this.ctx.session.captcha_expired_time) {
        this.ctx.body = {
          success: false,
          msg: '验证码已失效',
          code: 'captchaExpired'
        };
        return;
      }
      let equals =
        this.ctx.query.ignorecase == '1' // 不区分大小写
          ? code.toLowerCase() === this.ctx.session.captcha.toLowerCase()
          : code === this.ctx.session.captcha;
      if (!equals) {
        this.ctx.body = {
          success: false,
          msg: '验证码错误',
          code: 'captchaError'
        };
        return;
      }
      this.ctx.body = {
        success: true
      };
      return;
    }
    let loginBoxAccountCodeType = this.ctx.query.loginBoxAccountCodeType || this.ctx.cookies.get('loginBoxAccountCodeType');
    let cap = await this.createCaptcha(loginBoxAccountCodeType, this.ctx.query.expiredTime);
    this.ctx.body = cap.data;
  }

  async feishuUserTokenInfo() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/feishu/getUserTokenInfo', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async logout() {
    const { app, ctx } = this;
    const user = ctx.user || {};
    let _sessionid = ctx.session._externalKey;
    let _options = app.config.authenticateOptions[user._PROVIDER || 'local'];
    var logOutPath = '/login?logout';
    if (ctx.query.system) {
      logOutPath = '/sys/' + ctx.query.system + '/login?logout';
    }

    let redirectUrl = _options ? _options.logoutSuccessUrl || logOutPath : logOutPath;
    if (_options && ctx.helper.mobileDetect.mobile() && _options.mobile && _options.mobile.logoutRedirect) {
      redirectUrl = _options.mobile.logoutRedirect;
    }

    // 调用后端退出事件
    await ctx.curl(app.config.backendURL + '/security_logout');
    ctx.session.returnTo = null;
    if (app.sessionStore) {
      app.sessionStore.destroy(_sessionid);
    }
    ctx.redirect(redirectUrl);
    ctx.logout();

    // 端口socket连接
    let sockets = app.io.of('/wellapp-dyform').sockets;
    for (let key in sockets) {
      if (sockets[key].handshake.query._sessionid === _sessionid) {
        sockets[key].disconnect(true);
      }
    }
  }

  async getOnlineUser() {
    const { ctx, app } = this;
    ctx.body = await ctx.service.loginService.listOnlineUser();
  }

  async destroySession() {
    // console.log('=======================> destroySession')
    // const { ctx, app } = this;
    // let _sessionid = ctx.session._externalKey;
    // if (app.sessionStore) {
    //   app.sessionStore.destroy(_sessionid);
    // }
  }
  async heartbeat() {
    const { ctx, app } = this;
    try {
      if (ctx.req.body && ctx.req.body.reason == 'user focus') {
        app.redis.set(`session:online:${ctx.user.loginName}`, '1', 'EX', 60 * (app.config.userOfflineTime || 10));
      }
      app.redis.set(
        `session:key:${ctx.session._externalKey}`,
        JSON.stringify(
          lodash.pick(ctx.user, ['loginName', 'userId', 'userName', 'userUuid', 'userSystemOrgDetails', 'loginTime', 'roles', 'userNamePy'])
        ),
        'EX',
        60 * 3
        // 60 * (app.config.sessionKeyKeepaliveMinutes || 1)
      ); // 指定分钟内没有活动会被认为失效session，用户不在线情况，（实际session对应的缓存并没有失效），此处缓存时用于处理在线用户的数据
    } catch (error) {}

    this.ctx.body = 'ok';
  }
}

module.exports = LoginController;
