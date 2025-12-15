'use strict';

const Service = require('wellapp-framework').Service;
const FormStream = require('formstream');

class AppLoginConfService extends Service {
  async save(files, data, loginType) {
    const { app, ctx } = this;
    try {
      const form = new FormStream();
      for (let i = 0; i < files.length; i++) {
        form.file(files[i].field, files[i].filepath, files[i].filename);
      }
      form.field('data', data);
      form.field('loginType', loginType);
      const result = await ctx.curl(app.config.backendURL + '/web/login/config/save', {
        method: 'POST',
        contentType: 'multipart/form-data',
        stream: form,
        headers: form.headers(),
        dataType: 'json'
      });
      this.notifyReloadLoginPageSetting(JSON.parse(data).systemUnitId);
      return result.data;
    } catch (error) {
      app.logger.error('保存登录页配置接口异常：%s', error);
    }
    return null;
  }

  async notifyReloadLoginPageSetting(systemUnitId) {
    // 通知重新获取登录页配置
    if (this.app.config.env !== 'local' && this.app.redis) {
      this.app.pubRedis.publish(
        'wellappLoginPageSetting',
        JSON.stringify({
          systemUnitId
        })
      );
    }
  }

  async saveUnitConf(files, data, loginType) {
    const { app, ctx } = this;
    try {
      const form = new FormStream();
      for (let i = 0; i < files.length; i++) {
        form.file(files[i].field, files[i].filepath, files[i].filename);
      }
      form.field('data', data);
      form.field('loginType', loginType);
      const result = await ctx.curl(app.config.backendURL + '/web/login/config/saveUnitConf', {
        method: 'POST',
        contentType: 'multipart/form-data',
        stream: form,
        headers: form.headers(),
        dataType: 'json'
      });
      this.notifyReloadLoginPageSetting(JSON.parse(data).systemUnitId);
      return result.data;
    } catch (error) {
      app.logger.error('保存登录页配置接口异常：%s', error);
    }
    return null;
  }

  async getLoginPageConfig(id, nocache) {
    const { app, ctx } = this;
    try {
      if (app.config.env !== 'local' && app.redis && nocache !== true) {
        let data = await app.redis.get(app.redis.keyWrapper(`wellapp:loginPageSetting:${id}`));
        if (data) {
          return { data: JSON.parse(data) };
        }
      }
      const result = await ctx.curl(app.config.backendURL + '/web/login/config/getLoginPageConfig', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          systemUnitId: id || ''
        }
      });
      return result.data;
    } catch (error) {
      app.logger.error('查询登录页配置接口异常：%s', error);
    }
    return null;
  }
  async getEnableTenantSystemLoginPagePolicy(system, tenant) {
    const { app, ctx } = this;
    let cacheKey = `${system}:${tenant || 'T001'}:systemLoginPagePolicy`;
    let data = await this.app.redis.get(cacheKey);
    if (data) {
      return JSON.parse(data);
    }
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/system/getEnableTenantSystemLoginPagePolicy', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          system, tenant
        }
      });
      await this.app.redis.set(cacheKey, JSON.stringify(result.data.data))
      return result.data.data;
    } catch (error) {
      app.logger.error('查询登录页配置接口异常：%s', error);
    }
    return null;
  }

  async loginPageSettings(loginUrl) {
    const { app, ctx } = this;
    try {
      if (app.config.env !== 'local' && app.redis) {
        let data = await app.redis.get(app.redis.keyWrapper(`wellapp:loginPageSetting:${loginUrl}`));
        if (data) {
          return JSON.parse(data);
        }
      }
      const result = await ctx.curl(app.config.backendURL + '/web/login/config/loginPageSettings', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          loginUrl: loginUrl || ''
        }
      });
      return result.data.data;
    } catch (error) {
      app.logger.error('查询登录页配置接口异常：%s', error);
    }
    return null;
  }

  async getAllLoginPageSettings() {
    const { app, ctx } = this;
    try {

      const result = await ctx.curl(app.config.backendURL + '/web/login/config/getAllLoginPageSettings', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json'
      });
      return result.data.data;
    } catch (error) {
      app.logger.error('查询登录页配置接口异常：%s', error);
      throw error;
    }
  }

  async unitLoginPageSetting(systemUnitId) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/web/login/config/unitLoginPageSetting', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          systemUnitId: systemUnitId || ''
        }
      });
      return result;
    } catch (error) {
      app.logger.error('查询登录页配置接口异常：%s', error);
    }
    return null;
  }
}

module.exports = AppLoginConfService;
