/* eslint valid-jsdoc: "off" */

'use strict';

module.exports = appInfo => {
  const config = (exports = {});

  config.keys = appInfo.name + '_{{keys}}';

  config.middleware = [];

  const userConfig = {
    backendURL: global.process.env.wellapp_backend_url || 'http://192.168.0.116:38080', // 后端服务地址
    confuseStaticPath: false, // 混淆静态资源请求路径
    annoymousUrls: ['/hello/index'] // 匿名访问地址
  };

  config.httpclient = {
    request: {
      // 默认 request 超时时间
      timeout: 20000
    }
  };

  return {
    ...config,
    ...userConfig
  };
};
