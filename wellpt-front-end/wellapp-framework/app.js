'use strict';
const DataStore = require('./lib/nedb');
const path = require('path');
const lodash = require('lodash');
var events = require('events');
const Redis = require('ioredis');
const fs = require('fs');
const extend = require('extend');
const jwt = require('jsonwebtoken');
const { ref } = require('process');
class AppBootHook {
  constructor(app) {
    this.app = app;
    this.app.appEmitter = new events.EventEmitter();
  }

  configWillLoad() {
    this.app.config.coreMiddleware.unshift('dyformControlPlaceholderRedirect');
    this.app.config.appMiddleware.unshift('gzip');
    this.app.config.appMiddleware.unshift('authenticateInterceptor'); // 添加权限中间件
    this.app.config.coreMiddleware.unshift('staticRequestPathDecoder'); // 添加静态资源请求路径解密解析
    this.app.config.appMiddleware.unshift('sessionReturnTo');
    this.app.config.appMiddleware.unshift('isAnnoymousRequest');
    this.app.config.appMiddleware.unshift('themeResolver');
    this.app.config.appMiddleware.unshift('prodContextPathRequest');
    // this.app.config.appMiddleware.unshift('appSystemRequestResolver');

    if (!!!this.app.config.io.redis && this.app.config.redis && this.app.config.redis.client) {
      this.app.config.io.redis = {
        host: this.app.config.redis.client.host,
        port: this.app.config.redis.client.port,
        auth_pass: this.app.config.redis.client.password
      };
    }

    if ((this.app.config.publishSubscribeRedis && this.app.config.publishSubscribeRedis.client) || this.app.config.redis.client) {
      this.app.pubRedis = new Redis(
        this.app.config.publishSubscribeRedis ? this.app.config.publishSubscribeRedis.client : this.app.config.redis.client
      );
      this.app.subRedis = new Redis(
        this.app.config.publishSubscribeRedis ? this.app.config.publishSubscribeRedis.client : this.app.config.redis.client
      );
    }

    // 匿名请求地址
    this.app._ANNOYMOUSE_URLS = this.app.config.defaultAnnoymousUrls.concat(this.app.config.annoymousUrls || []);

    let authenticateOptions = this.app.config.authenticateOptions;
    for (let k in authenticateOptions) {
      for (let j in authenticateOptions[k]) {
        if (typeof authenticateOptions[k][j] === 'string' && authenticateOptions[k][j].indexOf('/') == 0) {
          //url地址
          this.app._ANNOYMOUSE_URLS.push(authenticateOptions[k][j]);
          if (authenticateOptions[k][j].mobile) {
            for (let z in authenticateOptions[k][j].mobile) {
              if (typeof authenticateOptions[k][j].mobile[z] === 'string' && authenticateOptions[k][j].mobile[z].indexOf('/') == 0) {
                this.app._ANNOYMOUSE_URLS.push(authenticateOptions[k][j].mobile[z]);
              }
            }
          }
        }
      }
    }
    this.app._ANNOYMOUSE_URLS = Array.from(new Set(this.app._ANNOYMOUSE_URLS));

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
    if (this.app.config.fileupload && this.app.config.fileupload.mode !== 'mongodb') {
      // 非mongobd的文件存储不支持分片上传
      this.app.config.fileupload.maxChunkSize = undefined;
      // 路径补全
      if (this.app.config.fileupload.uploadDir) {
        if (!lodash.startsWith(this.app.config.fileupload.uploadDir, '/')) {
          this.app.config.fileupload.uploadDir = '/' + this.app.config.fileupload.uploadDir;
        }
        if (!lodash.endsWith(this.app.config.fileupload.uploadDir, '/')) {
          this.app.config.fileupload.uploadDir += '/';
        }
      }
    }
    let secretKey = fs.readFileSync(`${__dirname}/client.key`, 'utf8');
    if (secretKey) {
      this.app.jwtSecretKey = secretKey.trim();
      const token = jwt.sign({ subject: this.app.config.keys, name: this.app.config.name }, secretKey.trim(), { algorithm: 'HS256' });
      this.app.CLIENT_TOKEN = token;
    }

    this.app.config.security.domainWhiteList.push(this.app.config.h5Server);
    if (this.app.config.domainWhiteList && this.app.config.domainWhiteList.length > 0) {
      this.app.config.domainWhiteList.push(...this.app.config.domainWhiteList);
    }
  }

  async didReady() {
    var _this = this;
    global.AUTO_DISCOVER.autoDiscoverController({
      app: this.app
    });

    global.AUTO_DISCOVER.autoDiscoverComponent({
      app: this.app
    });
    global.AUTO_DISCOVER.autoDiscoverViewFilePath({
      app: this.app
    });
    global.AUTO_DISCOVER.autodIscoverImages({
      app: this.app
    });
    // global.AUTO_DISCOVER.autoDiscoverStaticResourceImages({
    //   app: this.app
    // });

    // curl 请求全局设置
    this.globalRequestSetting();

    // http 服务创建成功后执行相关代码
    let serverConfig = this.app.config.serverConfig || {};
    this.app.once('server', server => {
      if (serverConfig.keepAliveTimeout) {
        server.keepAliveTimeout = serverConfig.keepAliveTimeout;
      }
    });

    // 非linux操作系统存在视图解析问题：修改视图解析的名称解析，确保原有的基于nunjucks的模板解析与新版vue的视图解析正常使用
    if (process.platform !== 'linux' && this.app.view) {
      this.app.view.resolve = function (name) {
        if (name.endsWith('.nj') || name.endsWith('.html')) {
          let config = this.config;
          let filename = this.fileMap.get(name);
          if (config.cache && filename) return filename;
          filename = resolvePath([name, name + config.defaultExtension], config.root);
          this.fileMap.set(name, filename);
          return filename;
        } else {
          return Promise.resolve(name);
        }
      };
    }

    // redis 键包装，提供同一个redis情况下的不同应用使用
    this.app.redis.keyWrapper = function (key) {
      if (_this.app.config.redisKeyPrefix) {
        return `${_this.app.config.redisKeyPrefix}:${key}`;
      }
      return key;
    };

    this.app.messenger.on('egg-ready', () => {
      _this.app.messenger.sendToAgent('getAssetsJsMeta', process.pid);
      _this.app.messenger.on('pushAssetsJsMeta', meta => {
        _this.app.nedb.removeSync({}, { multi: true });
        _this.app.nedb.insertSync(meta.nedbDatas);
        delete meta.nedbDatas;
        for (let k in meta) {
          _this.app[k] = meta[k]; // 扩展静态资源变量值到app上
        }
      });
    });
  }

  /**
   * 全局请求设置
   */
  async globalRequestSetting() {
    this.app.httpclient.on('request', req => {
      // 访问后端服务的时候，设置请求后端的头部token
      let header = {};
      header['Accept-Language'] = req.ctx.localeVariable.toLowerCase().replace('_', '-') || req.ctx.app.config.i18n.defaultLocale;
      if (req.ctx.headers['accept-locale']) {
        header['Accept-Language'] = req.ctx.headers['accept-locale'].replace('_', '-');
      }
      if (req.ctx && req.ctx.user && req.url.indexOf(req.ctx.app.config.backendURL) === 0) {
        let headKey = 'Authorization';
        if (req.ctx.user._AUTH === 'jwt') {
          headKey += '-JWT';
        }
        header[headKey] = 'Bearer ' + req.ctx.user.token;
      }
      if (req.ctx._SET_REQUEST_CLIENT_TOKEN_) {
        header['client-token'] = req.ctx.app.CLIENT_TOKEN;
      }

      if (req.ctx.isAnnoymousRequest) {
        let ctx = req.ctx;
        //匿名地址 但有传token 表单详情查看
        if (ctx.query.jwt != null && ctx.query.jwt != undefined && ctx.query.jwt.length > 0) {
          let headKey = 'Authorization';
          headKey += '-JWT';
          header[headKey] = 'Bearer ' + ctx.query.jwt;
        }
      }
      if (req.ctx && req.ctx.originalUrl.indexOf('isMobileApp=true') > -1) {
        header['isMobileApp'] = 'true';
      }
      if (req.url.indexOf('/dx-post-proxy') != -1 || req.ctx.app.config.cookieTransfer2Backend) {
        //重定向访问web自身服务或者支持cookie传递给后端服务
        header.cookie = req.ctx.headers.cookie;
      }
      req.args.headers = Object.assign({}, req.args.headers, header);
      // 传递给后端标明归属系统
      if (req.args.headers.system_id == undefined && req.ctx.SYSTEM_ID != undefined) {
        req.args.headers.system_id = req.ctx.SYSTEM_ID;
      }
      // 前端接口请求有传递归属系统
      if (req.args.headers.system_id == undefined && req.ctx.req.headers.system_id != undefined) {
        req.args.headers.system_id = req.ctx.req.headers.system_id;
      }
    });

    this.app.httpclient.on('response', result => {
      if (result.error && result.error.name === 'ResponseTimeoutError') {
        result.ctx.set('error', 'ResponseTimeoutError');
        result.ctx.body = { message: '服务响应超时' };
        result.ctx.status = 500;
        return;
      }
      let url = result.ctx.request.url;
      url = url.substring(0, url.indexOf('?'));
      if (result.res.headers.refreshtoken) {
        result.ctx.user.token = result.res.headers.refreshtoken;
      }
      // 前端提交的主题设置变更
      if (result.ctx.req.headers.user_theme_update) {
        result.ctx.user.theme = JSON.parse(result.ctx.req.headers.user_theme_update);
        result.ctx.session.commit({ save: true });
      }

      if (
        (result.res.status == 203 ||
          result.res.status == 403 ||
          (typeof result.res.data === 'string' && result.res.data.indexOf('"errorCode":"SessionExpired"') != -1) ||
          (result.res.data && result.res.data.errorCode === 'SessionExpired')) &&
        url != '/login'
      ) {
        // // 获取请求的来源地址
        let referer = result.ctx.req.headers.referer,
          origin = result.ctx.req.headers.origin,
          loginSuccessRedirectUrl;
        if (referer && origin) {
          // 来源页的请求地址
          loginSuccessRedirectUrl = referer.replace(origin, '');
        } else {
          loginSuccessRedirectUrl = result.ctx.req.url;
        }
        if (!result.ctx.session.returnTo) {
          result.ctx.session.returnTo = loginSuccessRedirectUrl;
          loginSuccessRedirectUrl = undefined;
        }

        result.ctx.app.logger.info(
          'token过期: referer -> %s , origin -> %s , url -> %s , returnTo -> %s',
          referer,
          origin,
          result.req.url,
          result.ctx.session.returnTo
        );
        // if (result.res.status == 403 && typeof result.res.data == 'string' && result.res.data.indexOf('403') !== -1) {
        //   // 后端返回的 403 提示页面代码
        //   result.res.data = undefined;// 避免 json 序列化异常
        // }
        result.ctx.session.messages = ['ApiTokenExpired'];
        if (typeof result.ctx.app.config.tokenExpiredStrategy === 'function') {
          result.ctx.app.config.tokenExpiredStrategy(result.ctx);
          return;
        }
        if (result.ctx.app.config.tokenExpiredRedirectUrl != undefined) {
          result.ctx.redirect(result.ctx.app.config.tokenExpiredRedirectUrl);
          return;
        }

        result.ctx.logout();
        // 无效登录
        result.ctx.redirect(
          (result.ctx.SYSTEM_ID ? '/sys/' + result.ctx.SYSTEM_ID : '') +
            '/login?tokenExpired' +
            (result.ctx.session.returnTo == undefined && loginSuccessRedirectUrl
              ? '&loginSuccessRedirectUrl=' + encodeURIComponent(loginSuccessRedirectUrl)
              : '')
        );
      }
    });
  }
}

function resolvePath(names, root) {
  for (const name of names) {
    for (const dir of root) {
      const filename = path.join(dir, name);
      if (fs.existsSync(filename)) {
        if (filename.indexOf(dir) > -1) {
          return filename;
        }
      }
    }
  }
}

module.exports = AppBootHook;
