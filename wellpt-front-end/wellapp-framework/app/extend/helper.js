'use strict';
const constant = require('./constant');
const jQueryParam = require('jquery-param');
const MobileDetect = require('mobile-detect');
const SYMBOLMOBILEDETECT = Symbol('Application#mobileDetect');
module.exports = {
  // 创建requirejs的脚本依赖配置
  createRequirejsConfig(javascripts) {
    const cf = {
      paths: {},
      shim: {},
      baseUrl: global.STATIC_PREFIX,
      urlArgs: 'v=' + this.resVer(),
      waitSeconds: 0,
      map: {
        '*': {
          css: global.STATIC_PREFIX + '/js/requirejs/css.min.js'
        }
      }
    };
    javascripts.forEach(element => {
      if (element && !element.abstract) {
        cf.paths[element.id] = global.STATIC_PREFIX + element.path;
        cf.shim[element.id] = {
          deps: element.dependencies,
          exports: element.id
        };
      }
    });

    return cf;
  },

  explainOutJsAndDeps(js, jsAdded, jsPack) {
    const out = [];
    if (!jsAdded.has(js) && jsPack[js]) {
      if (!jsPack[js].abstract) {
        out.push(jsPack[js]);
        jsAdded.add(js);
      }
      if (jsPack[js].dependencies && jsPack[js].dependencies.length) {
        jsPack[js].dependencies.forEach(d => {
          if (jsPack[d] && !jsAdded.has(d)) {
            out.push(jsPack[d]);
            jsAdded.add(d);
          }
        });
      }
    }
    return out;
  },

  // 静态资源地址前缀
  staticPrefix() {
    return global.STATIC_PREFIX;
  },

  jQueryParam: jQueryParam,

  isUserAgentOfMsie() {
    let useragent = this.ctx.request.headers['user-agent'];
    return !(useragent || !(useragent.indexOf('msie') != -1 || useragent.indexOf('"rv:11') != -1));
  },

  // 后端服务地址
  backendUrl() {
    // 浏览器端要使用外网地址
    return global.process.env.wellapp_backend_url_outer || this.app.config.backendURL;
  },

  resVer() {
    return this.app._RESOURCE_VERSION_HASH || '';
  },

  requirejs(all, initedJs, extras, callbackScript) {
    const { ctx, app } = this;
    // app.jsPack
    const explainjs = [];
    const jsAdded = new Set();
    all
      .concat(initedJs)
      .concat(extras)
      .forEach(id => {
        if (app.jsPack[id]) {
          if (!jsAdded.has(id)) {
            if (!app.jsPack[id].abstract) {
              explainjs.push(app.jsPack[id]);
              jsAdded.add(id);
            }
            if (app.jsPack[id].dependencies && app.jsPack[id].dependencies.length) {
              app.jsPack[id].dependencies.forEach(function (d) {
                if (!jsAdded.has(d)) {
                  explainjs.push(app.jsPack[d]);
                }
              });
            }
          }
        }
      });
    const conf = this.createRequirejsConfig(explainjs);
    const scripts =
      '<script type="text/javascript" src="' +
      global.STATIC_PREFIX +
      `/js/requirejs/require.js"></script>
    <script>
    var ctx = '';
    var WebApp = WebApp || {};
    WebApp.pageDefinition = {};
    WebApp.configJsModules = {};
    requirejs.onError = function (error) {
        throw error;
    }
    requirejs.config(` +
      JSON.stringify(conf) +
      `);
    require(` +
      JSON.stringify(Array.from(jsAdded)) +
      `,function(){
      ` +
      callbackScript +
      `
    });
    </script>
    `;

    return scripts;
  },

  /**
   * 判断是否是手机设备
   */
  isMobileDevice() {
    return this.mobileDetect.mobile();
  },

  get mobileDetect() {
    if (!this[SYMBOLMOBILEDETECT]) {
      let { ctx } = this;
      this[SYMBOLMOBILEDETECT] = new MobileDetect(ctx.request.headers['user-agent']);
    }
    return this[SYMBOLMOBILEDETECT];
  }
};
