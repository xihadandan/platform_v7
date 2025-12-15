/* eslint valid-jsdoc: "off" */

'use strict';

/**
 * 测试环境配置
 * @param {Egg.EggAppInfo} appInfo app info
 */
module.exports = appInfo => {
  const config = (exports = {});

  config.redis = {
    client: {
      db: process.env.WELLAPP_REDIS_DB ? parseInt(process.env.WELLAPP_REDIS_DB) : 1
    }
  };

  return {
    ...config
  };
};
