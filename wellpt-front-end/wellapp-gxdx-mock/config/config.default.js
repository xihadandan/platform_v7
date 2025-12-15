/* eslint valid-jsdoc: "off" */

'use strict';
const path = require('path');
const os = require('os');
/**
 * @param {Egg.EggAppInfo} appInfo app info
 */
module.exports = appInfo => {
  /**
   * built-in config
   * @type {Egg.EggAppConfig}
   **/
  const config = (exports = {});

  // use for cookie sign key, should change to your own and keep security
  config.keys = appInfo.name + '_1635935408692_674';

  // add your middleware config here
  config.middleware = [];

  config.security = {
    csrf: {
      enable: false
    }
  };
  config.multipart = {
    mode: 'stream', //默认为流
    fileSize: '2048mb', //文件上传限制  富文本图片+视频上传有用到，其他未知
    tmpdir: path.join(os.tmpdir(), 'egg-multipart-tmp', appInfo.name),
    cleanSchedule: {
      cron: '0 30 4 * * *',
      disable: false
    }
  };
  // add your user config here
  const userConfig = {
    // myAppName: 'egg',
    backendURL: 'http://localhost:8080',

    frontFtp: {
      host: '192.168.0.116',
      port: 2294,
      username: 'foo',
      password: 'pass',
      protocols: 'sftp'
    },

    backendFtp: {
      host: '192.168.0.116',
      port: 2295,
      username: 'foo',
      password: 'pass',
      protocols: 'sftp'
    }
  };

  return {
    ...config,
    ...userConfig
  };
};
