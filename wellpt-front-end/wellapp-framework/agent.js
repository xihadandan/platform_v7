// eslint-disable-next-line strict
'use strict';
const path = require('path');
const fs = require('fs');
const utils = require('./lib/utils');
const lodash = require('lodash');
const { loader } = require('./lib/static-resource-loader.js');
class AppBootHook {
  constructor(app) {
    this.app = app;
    this.app.ASSETS_JS_META = { assetsVersion: undefined };
    this.wellappNodeModules = [];
    let _this = this;
    fs.readdirSync('./node_modules').forEach(function (dirPath) {
      if (!lodash.startsWith(dirPath, 'wellapp-')) {
        return;
      }
      _this.wellappNodeModules.push(dirPath);
    });
  }

  configWillLoad() {
    if (!!!this.app.config.io.redis && this.app.config.redis && this.app.config.redis.client) {
      this.app.config.io.redis = {
        host: this.app.config.redis.client.host,
        port: this.app.config.redis.client.port,
        auth_pass: this.app.config.redis.client.password
      };
    }

    // 匿名请求地址
    this.app._ANNOYMOUSE_URLS = this.app.config.defaultAnnoymousUrls.concat(this.app.config.annoymousUrls || []);
    // 首页是否匿名
    if (!this.app.config.index.security) {
      this.app._ANNOYMOUSE_URLS.push(this.app.config.index.page);
      this.app._ANNOYMOUSE_URLS.push('/');
    }

    // 静态资源请求alias
    if (this.app.config.staticAlias) {
      for (let k in this.app.config.staticAlias) {
        let _k = path.normalize(k);
        this.app.config.static.alias[_k] = path.normalize(this.app.config.staticAlias[k]);
      }
    }

    // 忽略csrf校验
    if (this.app.config.csrfIgnore) {
      let ignores = this.app.config.security.csrf.ignore;
      for (let k of this.app.config.csrfIgnore) {
        if (ignores.indexOf(k) < 0) {
          ignores.push(k);
        }
      }
    }
  }

  async didReady() {
    let _this = this;
    this.app.timing.start('Static Resource Load Start');
    loader(this.app, this.wellappNodeModules);
    this.app.logger.info('静态资源加载解析完成，耗时：%d ms', this.app.timing.end('Static Resource Load Start').duration);

    this.app.messenger.on('egg-ready', () => {
      _this.app.messenger.on('getAssetsJsMeta', pid => {
        _this.app.messenger.sendTo(pid, 'pushAssetsJsMeta', _this.app.ASSETS_JS_META);
      });
    });

    // let watchStaticResPaths = [];
    // ['ROOT'].concat(this.wellappNodeModules).forEach(p => {
    //   let resourcePath = p === 'ROOT' ? path.join('config', 'resource') : path.join('node_modules', p, 'config', 'resource');
    //   watchStaticResPaths.push(resourcePath);
    //   let developJsPath =
    //     p === 'ROOT' ? path.join('app', 'assets', 'develop-js') : path.join('node_modules', p, 'app', 'assets', 'develop-js');
    //   watchStaticResPaths.push(developJsPath);
    // });

    // this.app.watcher.watch(watchStaticResPaths, result => {
    //   loader(_this.app, _this.wellappNodeModules);
    //   _this.app.messenger.sendToApp('pushAssetsJsMeta', _this.app.ASSETS_JS_META);
    // });
  }

  async serverDidReady() {}

  /**
   * 全局请求设置
   */
  async globalRequestSetting() {}
}

module.exports = AppBootHook;
