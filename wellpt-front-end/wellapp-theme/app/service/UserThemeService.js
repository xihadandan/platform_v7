'use strict';

const Service = require('wellapp-framework').Service;

class UserThemeService extends Service {
  defaultTheme = this.config.defaultTheme || {
    theme: 'wellpt-default',
    color: 'red1',
    fontSize: 'default',
    order: ['color', 'fontSize'],
    colorValue: '#c7000b'
  };

  getThemeName(theme) {
    if (!theme) {
      theme = this.defaultTheme;
    }

    let themeName = theme.theme;
    if (theme.order) {
      for (const key of theme.order) {
        themeName += `-${theme[key]}`;
      }
    }
    return themeName;
  }

  async getThemeCss(themeName) {
    if (!themeName) {
      const theme = await this.getUserTheme();
      themeName = this.getThemeName(theme);
    }

    const independentLoadedCssFilesArr = ['iconfont', 'pt-iconfont', 'summernote'];

    return this.app.name === 'wellapp-web'
      ? [`${themeName}`, ...independentLoadedCssFilesArr]
      : [`${themeName}`, `${themeName}-dev`, ...independentLoadedCssFilesArr];
  }

  async getThemeFiles(theme) {
    if (!theme) {
      theme = await this.getUserTheme();
    }

    const themeName = this.getThemeName(theme);
    const appName = this.app.name;
    const appEnv = this.app.config.env;

    const independentLoadedFilesArr = [
      '/static/iconfont/iconfont.css',
      '/static/pt-iconfont/iconfont.css',
      '/static/js/bootstrap-wysiwyg/summernote/summernote.css'
    ];

    if (appName === 'wellapp-web') {
      if (appEnv === 'prod' || appEnv === 'unittest') {
        // 平台生产环境
        return [`/static/themes/${themeName}.css`, ...independentLoadedFilesArr];
      } else {
        // 平台开发环境
        return [`/static/themes/${themeName}.css`, ...independentLoadedFilesArr];
      }
    } else {
      if (appEnv === 'prod' || appEnv === 'unittest') {
        // 业务线生产环境
        return [`/static/themes/${themeName}.min.css`, `/static/themes/${themeName}-dev.min.css`, ...independentLoadedFilesArr];
      } else {
        // 业务线开发环境
        return [`/static/themes/${themeName}.min.css`, `/static/themes/${themeName}-dev.css`, ...independentLoadedFilesArr];
      }
    }
  }

  async getUserTheme() {
    // 1. 先读取 redis
    let theme = await this.getUserThemeFromRedis();

    // 2. 不存在时读取 后端
    if (!theme) {
      theme = await this.getUserThemeFromServer();
    }

    // 开发环境重新生成通知 agent 在必要时候重新生成主题文件
    if (this.app.config.env === 'local') {
      const themeName = this.getThemeName(theme);
      this.app.messenger.sendToAgent('onThemeRequest', {
        theme,
        themeName
      });
    }

    this.ctx.cookies.set('themeColor', (theme && theme.color) || 'default');

    return theme;
  }

  async getUserThemeFromRedis() {
    const { app, ctx } = this;

    if (app.redis && app.redis.get && ctx.user && ctx.user.userUuid) {
      const theme = await app.redis.get(`theme:${ctx.user.userUuid}`);
      return theme && JSON.parse(theme);
    }

    return null;
  }

  async getUserThemeFromServer() {
    const { app, ctx } = this;

    try {
      const result = await ctx.curl(app.config.backendURL + '/api/user/preferences/getThemeDataValue', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json'
      });

      const theme = result.data.data ? JSON.parse(result.data.data) : this.defaultTheme;

      // 保存到 redis 中
      await this.saveUserThemeToRedis(theme);
      return theme;
    } catch (error) {
      app.logger.error('用户主题信息请求异常：%s', error);
    }
    return null;
  }

  async saveUserThemeToRedis(theme) {
    const { app, ctx } = this;

    if (app.redis && app.redis.set && ctx.user.userUuid) {
      const maxAge = app.config.wellappTheme.redisCacheMaxAge;
      await app.redis.set(`theme:${ctx.user.userUuid}`, JSON.stringify(theme), 'EX', maxAge);
      app.logger.log(`保存用户主题到redis，key为"theme:${ctx.user.userUuid}"`);
    }
  }

  async saveUserTheme(theme) {
    const { app, ctx } = this;

    try {
      const result = await ctx.curl(app.config.backendURL + '/api/user/preferences/saveTheme', {
        method: 'POST',
        contentType: 'form',
        dataType: 'json',
        data: {
          dataValue: JSON.stringify(theme)
        }
      });

      if (result.data.code === 0) {
        await this.saveUserThemeToRedis(theme);
      }
    } catch (error) {
      app.logger.error('用户主题信息保存异常：%s', error);
    }
    return null;
  }
}

module.exports = UserThemeService;
