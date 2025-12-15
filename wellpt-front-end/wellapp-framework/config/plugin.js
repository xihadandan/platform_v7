'use strict';

// add you build-in plugin here, example:
// exports.nunjucks = {
//   enable: true,
//   package: 'egg-view-nunjucks',
// };

const path = require('path');
const fs = require('fs');

exports.nunjucks = {
  enable: true,
  package: 'egg-view-nunjucks'
};

exports.passport = {
  enable: true,
  package: 'egg-passport'
};

exports.wellappPassportLocal = {
  enable: true,
  path: path.join(__dirname, '../lib/plugin/wellapp-passport-local')
};

exports.wellappPassportFeishu = {
  enable: true,
  path: path.join(__dirname, '../lib/plugin/wellapp-passport-feishu')
};

exports.wellappPassportDingtalk = {
  enable: true,
  path: path.join(__dirname, '../lib/plugin/wellapp-passport-dingtalk')
};

exports.wellappPassportWeixin = {
  enable: true,
  path: path.join(__dirname, '../lib/plugin/wellapp-passport-weixin')
};

exports.wellappPassportOauth2 = {
  enable: true,
  path: path.join(__dirname, '../lib/plugin/wellapp-passport-oauth2')
};

exports.wellappPassportIdnumber = {
  enable: false,
  path: path.join(__dirname, '../lib/plugin/wellapp-passport-idnumber')
};

exports.wellappPassportServerJwt = {
  enable: false,
  path: path.join(__dirname, '../lib/plugin/wellapp-passport-server-jwt')
};

exports.redis = {
  enable: true,
  package: 'egg-redis'
};

exports.wellappRedisSession = {
  enable: true,
  path: path.join(__dirname, '../lib/plugin/wellapp-redis-session')
};

exports.wellappRequestFilter = {
  enable: false,
  path: path.join(__dirname, '../lib/plugin/wellapp-request-filter')
};

exports.security = {
  csrf: {
    headerName: 'x-csrf-token' // 通过 header 传递 CSRF token 的默认字段为 x-csrf-token
  }
  // xframe: {
  //   enable: true,
  //   // 'SAMEORIGIN', 'DENY' or 'ALLOW-FROM http://example.jp'
  //   value: 'ALLOW-FROM http://localhost:8080',
  // },
};

exports.httpProxy = {
  enable: true,
  package: '@eggjs/http-proxy'
};

exports.io = {
  enable: true,
  package: 'egg-socket.io'
};

exports.cors = {
  enable: true,
  package: 'egg-cors',
};

exports.vuessr = { enable: true, package: 'egg-view-vue-ssr' };

// 业务模块以插件形式自动启用
const AutoDiscover = require('../lib/auto-discover');
global.AUTO_DISCOVER = new AutoDiscover();
global.AUTO_DISCOVER.autoDiscoverPluginModule(exports);
