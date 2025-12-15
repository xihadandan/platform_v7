'use strict';

const extend = require('extend');

module.exports = app => {
  const { controller, router } = app;

  // 账号密码登录校验
  router.post('/login', app.middleware.authenticate('local', app.config.authenticateOptions.local));

  // 切换账号
  router.post('/api/org/user/account/changingOver/:account', app.middleware.authenticate('local', app.config.authenticateOptions.local));

  // 飞书登录
  if (app.plugins.wellappPassportFeishu) {
    app.post('/login/feishuToken', app.middleware.authenticate('feishu-token', app.config.authenticateOptions.feishu));
  }

  if (app.plugins.wellappPassportDingtalk) {
    // 钉钉扫码登录校验
    router.post('/login/dingtalkAuth',
      app.middleware.authenticate('dingtalk-qr', Object.assign({}, app.config.dingtalk, app.config.authenticateOptions['dingtalk-qr']))
    );
    // router.get(
    //   '/mobile/pt/dingtalk/auth',
    //   app.middleware.authenticate('dingtalk-qr', Object.assign({}, app.config.dingtalk, app.config.authenticateOptions['dingtalk-qr']))
    // );
  }

  // 企业微信扫码登录
  if (app.plugins.wellappPassportWeixin) {
    router.post('/login/weixinAuth',
      app.middleware.authenticate('weixin-qr', Object.assign({}, app.config.dingtalk, app.config.authenticateOptions['weixin-qr']))
    );
  }

  if (app.plugins.wellappPassportOauth2) {
    // 统一认证oauth2登录
    app.get('/login/oauth2', app.middleware.authenticate('oauth2', app.config.authenticateOptions.oauth2));

    // 统一认证的账号密码登录
    app.post('/login/oauth2', app.middleware.authenticate('oauth2-password', app.config.authenticateOptions['oauth2-password']));

    app.get('/login/oauth2/callback', app.middleware.authenticate('oauth2', app.config.authenticateOptions.oauth2));
  }

  if (app.plugins.wellappPassportServerJwt) {
    app.get('/login/jwt', app.middleware.authenticate('server-jwt', app.config.authenticateOptions['server-jwt']));
  }

  if (app.plugins.wellappPassportIdnumber) {
    app.get('/login/idnumberToken', app.middleware.authenticate('idnumber-token', app.config.authenticateOptions['idnumber-token']));
  }

  router.all('/proxy/**', app.controller.proxyController.proxy);

  router.get(`/api/cache/deleteByKey`, app.controller.cacheController.deleteKey);

  router.get(`/api/cache/deleteByPattern`, app.controller.cacheController.deleteByPattern);


  router.get(`/api/getWebConfigByKey`, app.controller.configController.getWebConfigByKey);

  router.post(`/api/cache/set`, app.controller.cacheController.set);

  router.all('/dx-post-proxy/**', app.controller.proxyController.dxPostProxy);

  // OAuth2 统一认证路由
  router.get('/oauth/authorize', app.controller.oAuth2Controller.authorize);
  router.post('/oauth/authorize', app.controller.oAuth2Controller.authorize);
  router.get('/oauth/login', app.controller.oAuth2Controller.login);
  router.get('/oauth/test/client/callback', app.controller.oAuth2Controller.testClientCallback);
  router.post('/oauth/token', app.controller.oAuth2Controller.token);
};
