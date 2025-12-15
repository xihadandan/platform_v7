/* eslint valid-jsdoc: "off" */

'use strict';

/**
 * 生产环境配置
 * @param {Egg.EggAppInfo} appInfo app info
 */
module.exports = appInfo => {
  const config = (exports = {});

  config.redis = {
    client: {
      port: 6379, // Redis port
      host: '192.168.0.116', // Redis host
      password: 'wellsoft',
      db: 0,
      weakDependent: true
    }
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

  return {
    backendURL: global.process.env.wellapp_backend_url || 'http://localhost:8080',
    ...config
  };
};
