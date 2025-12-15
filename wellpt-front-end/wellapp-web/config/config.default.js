/* eslint valid-jsdoc: "off" */

'use strict';

var path = require('path');
/**
 * 默认配置
 *
 * @param {Egg.EggAppInfo}
 *            appInfo app info
 */
module.exports = appInfo => {
  const config = (exports = {});

  config.keys = appInfo.name + '_1585538455047_6407';

  config.middleware = [];

  const userConfig = {
    backendURL: global.process.env.wellapp_backend_url || 'http://192.168.0.116:11060',
    // backendURL: global.process.env.wellapp_backend_url || 'http://localhost:9527',
    // backendURL: global.process.env.wellapp_backend_url || 'http://localhost:8080',
    dingtalk: {
      // appId: function (ctx) {
      //   console.log(ctx);
      //   return "dingoatqql5whogyedauzj";
      // },
      appId: 'dingoatqql5whogyedauzj',
      oauth_sns_authorize_url: 'http://oapi.dingtalk.com/connect/oauth2/sns_authorize',
      corp_domain_uri: 'http://oa.well-soft.com:18098'
    },
    xxlJobAdminAddress: global.process.env.xxlJobAdminAddress || 'http://192.168.0.116:8082'
  };
  config.httpclient = {
    request: {
      // 默认 request 超时时间
      timeout: [10000, 100000]
    }
  };
  config.httpProxy = {
    timeout: [10000, 100000]
  };
  config.aMapConfig = {
    securityJsCode: 'e6d8fe301f4731423b984ea33469e300', // 高德地图密钥
    key: 'cedd4b05eeabf22c9bc057a9e6f0c044', // 高德地图key
    version: '2.0'
  }

  return {
    ...config,
    ...userConfig
  };
};
