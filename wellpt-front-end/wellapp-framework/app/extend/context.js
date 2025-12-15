const Timing = require('../../lib/timing');

const LOCALEVAR = Symbol('Context#localeVariable');

module.exports = {
  // this 就是 ctx 对象，在其中可以调用 ctx 上的其他方法，或访问属性

  /**
   * 重定向到后端的地址
   * @param {} url
   */
  redirect2backend(url) {
    if (url.indexOf('?') == -1) {
      url += '?';
    }
    if (this.user) {
      url += '&' + this.user._AUTH + '=' + this.user.token;
    }
    this.redirect((global.process.env.wellapp_backend_url_outer || this.app.config.backendURL) + url);
  },

  get nedb() {
    return this.app.nedb;
  },

  /**
   * 渲染错误页面
   * @param {错误码} errorCode
   * @param {标题} title
   * @param {子标题} subTitle
   */
  async renderError(errorCode, title, subTitle) {
    await this.render('error/index.js', {
      title: title || errorCode,
      errorCode,
      subTitle
    });
  },

  /**
   * 判断是否存在任一角色
   * @param {角色ID} roles
   * @returns
   */
  hasAnyUserRole(roles) {
    if (this.user && this.user.roles) {
      let _roles = Array.isArray(roles) ? roles : Array.from(arguments);
      for (let i = 0, len = _roles.length; i < len; i++) {
        if (this.user.roles.includes(_roles[i])) {
          return true;
        }
      }
    }
    return false;
  },

  get newTiming() {
    return new Timing();
  },

  get localeVariable() {
    if (!this[LOCALEVAR]) {
      let cookieField = 'locale';
      let queryField = 'locale';
      const cookieLocale = this.cookies.get(cookieField, { signed: false });
      const systemCookieLocale = this.cookies.get(cookieField + '.' + this.SYSTEM_ID, { signed: false });
      if (this.SYSTEM_ENABLE_LOCALE === false) {
        return 'zh_CN';
      }
      // 1. Query
      locale = this.query[queryField];
      let localeOrigin = 'query';

      // 2. Cookie
      if (!locale) {
        locale = systemCookieLocale || cookieLocale;
        localeOrigin = 'cookie';
      }

      // 3. Header
      if (!locale) {
        // Accept-Language: zh-CN,zh;q=0.5
        // Accept-Language: zh-CN
        let languages = this.acceptsLanguages();
        if (Array.isArray(languages)) {
          locale = languages[0];
          localeOrigin = 'header';
        } else if (languages) {
          locale = languages;
          localeOrigin = 'header';
        }

        // all missing, set it to defaultLocale
        if (!locale) {
          locale = this.app.config.i18n.defaultLocale;
          localeOrigin = 'default';
        }
      }

      locale = formatLocale(locale);
      this[LOCALEVAR] = locale;
      if (cookieLocale !== locale) {
        this.cookies.set(cookieField, locale, {
          httpOnly: false
        });
      }
    }
    return this[LOCALEVAR];
  },

  mergeContextLocals(locals) {
    // 获取服务端相关数据回填到 locals 里 ( 服务端渲染会在 server.js 里面增加相关数据)
    const _CONTEXT_STATE_ = {
      USER_ROLES: this.USER_ROLES,
      USER: this.user,
      SERVER_TIMESTAMP: new Date().getTime(),
      SYSTEM_ID: this.SYSTEM_ID, // 返回系统ID
      FORCE_SET_REQUEST_CLIENT_TOKEN: this._SET_REQUEST_CLIENT_TOKEN_,
      ENV: this.app.config.env,
      SSR_THEME: {}
    };

    if (locals.PAGE_ID) {
      _CONTEXT_STATE_.PAGE_ID = locals.PAGE_ID;
    }
    if (locals.SYSTEM_ID) {
      // 返回系统ID
      _CONTEXT_STATE_.SYSTEM_ID = locals.SYSTEM_ID;
    }
    if (locals.PAGE_EFFECTIVE_ROLES) {
      // 返回使当前页面生效的角色（用户可能由 N 个角色，但是实际上使用户能访问到该页面的有效角色只是其中的子集）
      _CONTEXT_STATE_.PAGE_EFFECTIVE_ROLES = locals.PAGE_EFFECTIVE_ROLES;
    }

    // 系统标题
    if (this.TITLE && locals.title == undefined) {
      locals.title = this.TITLE;
    }
    // 系统图标
    if (this.FAVICON && locals.favicon == undefined) {
      locals.favicon = this.FAVICON;
    }
    let currentTheme = undefined;
    if (!this.req.url.startsWith('/theme/theme-design') && !this.req.url.startsWith('/theme/theme-specify')) {
      if (this.THEME) {
        // 中间件根据用户个性化决定的主题
        currentTheme = this.THEME;
      }
      if (locals.THEME) {
        // 控制层指定主题返回
        currentTheme = locals.THEME;
      }
    }
    if (currentTheme) {
      // 返回前端主题数据
      let theme = {},
        bodyClass = [];
      if (currentTheme.class || currentTheme.themeClass) {
        theme.themeClass = currentTheme.class || THcurrentThemeEME.themeClass;
        bodyClass.push(theme.themeClass);
      }
      if (currentTheme.color || currentTheme.colorClass) {
        theme.themeColor = currentTheme.color;
        theme.colorClass = currentTheme.colorClass;
        bodyClass.push(theme.colorClass);
      }
      if (currentTheme.fontSize || currentTheme.fontSizeClass) {
        theme.fontSize = currentTheme.fontSize;
        theme.fontSizeClass = currentTheme.fontSizeClass;
        if (theme.fontSizeClass != undefined && theme.fontSizeClass !== 'font-size-1') {
          bodyClass.push(theme.fontSizeClass);
        }
      }
      if (currentTheme.fixed) {
        theme.fixed = true;
      }
      if (currentTheme.classScope) {
        theme.classScope = currentTheme.classScope;
      }
      _CONTEXT_STATE_.SSR_THEME = theme;
      _CONTEXT_STATE_.SSR_BODY_CLASS = bodyClass.length > 0 ? bodyClass.join(' ') : '';
    }
    if (this.SYSTEM_ENABLE_LOCALE != undefined) {
      _CONTEXT_STATE_.LOCALE_ENABLE = this.SYSTEM_ENABLE_LOCALE;
    }
    locals._CONTEXT_STATE_ = _CONTEXT_STATE_;
    return locals;
  },

  renderView(...args) {
    // 重写 egg-view-vue-ssr / lib / view.js , 实现项目定制化诉求
    this.view.viewManager.get('vue').prototype.render = function (name, locals, options = {}) {
      locals = this.app.vue.normalizeLocals(this.ctx, locals, options);
      this.ctx.mergeContextLocals(locals || {});
      const context = { state: locals };
      let fetchThemeStyles = undefined,
        themeStyles = undefined,
        currentTheme = locals._CONTEXT_STATE_.SSR_THEME;
      if (currentTheme) {
        let classScope = currentTheme.classScope || [currentTheme.themeClass];
        if (classScope) {
          // 按需加载主题类
          let getCssPromise = [];
          for (let i = 0, len = classScope.length; i < len; i++) {
            getCssPromise.push(this.app.redis.get(`THEME_DESIGN_CLASS_CSS:${classScope[i]}`));
          }
          fetchThemeStyles = new Promise((resolve, reject) => {
            Promise.all(getCssPromise)
              .then(ans => {
                themeStyles = ans.join(' ');
                resolve();
              })
              .catch(() => {
                // 不管样式是否加载成功都返回
                resolve();
              });
          });
        }
      }
      this.config.afterRender = function (html, data) {
        // 注入主题
        if (themeStyles) {
          return html.replace(/\/\* vue-ssr-inject-style \*\//i, match => {
            return match + themeStyles;
          });
        }
        return html;
      };

      let webpackTargetWeb = process.env.WELLAPP_WEBPACK_TARGET === 'web';
      if (webpackTargetWeb || this.app.config.vuessr.ssr === false) {
        this.app.logger.info(
          `${
            webpackTargetWeb ? 'Webpack build target is web' : 'vuessr configuration ssr disabled'
          }. Proceeding with client-side rendering only.`
        );
        // 服务端渲染关闭，则直接进行客户端渲染
        return this.app.vue.renderClient(options.name, locals, options);
      }
      let renderPromises = [this.app.vue.render(name, context, options)];
      if (fetchThemeStyles != undefined) {
        renderPromises.push(fetchThemeStyles);
      }
      return Promise.allSettled(renderPromises)
        .then(results => {
          if (results[0].status === 'fulfilled') {
            let html = results[0].value;
            if (this.app.vue.resource) {
              return this.app.vue.resource.inject(html, options.name, context, options);
            }
            return html;
          } else {
            if (this.config.fallbackToClient) {
              this.app.logger.warn('[%s] server render bundle error, try client render, the server render error', name, results[0].reason);
              return this.app.vue.renderClient(options.name, locals, options);
            }
            throw new Error(results[0].reason);
          }
        })
        .catch(err => {
          throw err;
        });
    };

    return this.view.render(...args);
  }
  /**
   * 重写 egg-view-vue-ssr / lib / view.js , 实现项目定制化诉求
   * @param {*} name
   * @param {*} locals
   * @param {*} options
   * @returns
   */
  // render(name, locals, options = {}) {
  //   locals = this.app.vue.normalizeLocals(this, locals, options);
  //   const context = { state: locals };
  //   return this.app.vue.render(name, context, options).then(html => {
  //     if (this.app.vue.resource) {
  //       return this.app.vue.resource.inject(html, options.name, context, options);
  //     }
  //     return html;
  //   }).catch(err => {
  //     if (this.app.config.vuessr.fallbackToClient) {
  //       this.app.logger.error('[%s] server render bundle error, try client render, the server render error', name, err);
  //       return this.app.vue.renderClient(options.name, this.mergeContextLocals(locals || {}), options);
  //     }
  //     throw err;
  //   });
  // },
};

function formatLocale(locale) {
  let _l = locale.split(/-|_/);
  // 转为： zh_CN 、en_US 格式
  return _l[0].toLowerCase() + (_l.length == 2 ? '_' + _l[1].toUpperCase() : '');
}
