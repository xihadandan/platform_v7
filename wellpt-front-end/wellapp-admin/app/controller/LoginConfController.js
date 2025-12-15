'use strict';

const Controller = require('wellapp-framework').Controller;

/**
 * 登录配置控制层
 */
class LoginConfController extends Controller {
  async index() {
    const { ctx, app } = this;
    try {
      const themeFiles = await ctx.service.userThemeService.getThemeFiles();

      const result = await ctx.curl(app.config.backendURL + '/web/login/config/appLoginPageConfigSetting', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          systemUnitId: ctx.query.u
        }
      });

      await ctx.render('/app_login_page_config.nj', {
        ...result.data.data,
        themeFiles
      });
    } catch (error) {
      this.app.logger.error('登录页配置异常：%s', error);
    }
  }

  async save() {
    const ctx = this.ctx;
    try {
      const result = await this.ctx.service.appLoginConfService.save(ctx.request.files, ctx.req.body.data, ctx.req.body.loginType);
      ctx.body = result;
    } catch (error) {
      this.app.logger.error('登录配置保存异常：%s', error);
    }
  }

  async saveUnitConf() {
    const ctx = this.ctx;
    try {
      const result = await this.ctx.service.appLoginConfService.saveUnitConf(ctx.request.files, ctx.req.body.data, ctx.req.body.loginType);
      ctx.body = result;
    } catch (error) {
      this.app.logger.error('登录配置保存异常：%s', error);
    }
  }

  async saveLoginSecurityConfig() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/security/login/save', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('登录安全配置保存异常：%s', error);
    }
  }

  async getLoginSecurityConfig() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/security/login/getSystemUnitLoginSecurityConfig', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          systemUnitId: ctx.user.systemUnitId
        }
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('登录安全配置查询异常：%s', error);
    }
  }

  async loginPageSettings() {
    const { ctx, app } = this;
    try {
      const result = await ctx.service.appLoginConfService.loginPageSettings(ctx.query.loginUrl);
      ctx.body = result;
    } catch (error) {
      this.app.logger.error('登录安全配置查询异常：%s', error);
    }
  }

  async unitLoginPageSetting() {
    const { ctx, app } = this;
    try {
      const result = await ctx.service.appLoginConfService.unitLoginPageSetting(ctx.query.systemUnitId);
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('登录安全配置查询异常：%s', error);
    }
  }

  async preview() {
    const ctx = this.ctx;
    const data = {
      loginConfig: ctx.request.body,
      isPreview: true
    };
    //const { app } = this;

    if (data.loginConfig.data.pageBackgroundImageBase64) {
      data.loginConfig.data.pageBackgroundImageBase64 = 'url(' + data.loginConfig.data.pageBackgroundImageBase64 + ')';
    }

    if (data.loginConfig.data.pageLogoBase64) {
      if ('_right' === data.loginConfig.data.pageStyle) {
        data.loginConfig.data.pageLogoBase64 = data.loginConfig.data.pageLogoBase64;
      } else {
        data.loginConfig.data.pageLogoBase64 = 'url(' + data.loginConfig.data.pageLogoBase64 + ')';
      }
    }
    //app.logger.info('值为：%s , 看下pageBackgroundImageBase64内容', data.loginConfig.data.pageBackgroundImageBase64);
    await ctx.render('login.nj', data);
  }
}

module.exports = LoginConfController;
