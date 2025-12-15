'use strict';

const Service = require('wellapp-framework').Service;

class AppProdIntegrationService extends Service {
  async getByUuid(uuid) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          serviceName: 'appProductIntegrationService',
          methodName: 'get',
          version: '',
          args: JSON.stringify([uuid])
        }
      });
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('获取产品集成树异常：%s', error);
    }
    return null;
  }

  async getLatestPublishProdVersionSetting(prodId) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/app/prod/version/latestPublishedVersionSetting', {
        method: 'GET',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: {
          prodId
        }
      });
      if (result.data && result.data.code === 0) {
        // await this.app.redis.set(`LATEST_PUBLISH_APP_PROD_VERSION_SETTING:${prodId}`, JSON.stringify(result.data.data))
        return result.data.data;
      }
    } catch (err) {}
    return null;
  }

  async getSystemAuthenticatePage(tenant, system) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/webapp/systemAuthenticatePage', {
        method: 'GET',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: {
          tenant,
          system
        }
      });
      if (result.data && result.data.code === 0) {
        // await this.app.redis.set(`TENANT_SYSTEM_INFO:${tenant || ctx.user.tenantId}:${system}`, JSON.stringify(result.data.data))
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('获取系统下授权页面异常：%s', error);
    }

    return null;
  }

  async getTenantSystemInfo(tenant, system) {
    const { app, ctx } = this;
    let cacheKey = `TENANT_SYSTEM_INFO:${tenant || (ctx.user ? ctx.user.tenantId : 'T001')}:${system}`;
    let systemInfo = await this.app.redis.get(cacheKey);
    if (systemInfo) {
      return JSON.parse(systemInfo);
    }
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/system/getTenantSystemInfo', {
        method: 'GET',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: {
          tenant,
          system
        }
      });
      if (result.data && result.data.code === 0 && result.data.data) {
        await this.app.redis.set(cacheKey, JSON.stringify(result.data.data));
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('获取产品版本设置异常：%s', error);
    }

    return null;
  }
  async getTenantSystemPageSetting(tenant, system) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/system/getTenantSystemPageSetting', {
        method: 'GET',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: {
          tenant,
          system
        }
      });
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('获取产品版本设置异常：%s', error);
    }
    return null;
  }

  async getPageInfosUnderProdVersion(prodVersionUuid) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/app/prod/version/getPageInfosIgnoreDefinitionUnderProdVersion', {
        method: 'GET',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: {
          prodVersionUuid
        }
      });
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('获取产品版本设置异常：%s', error);
    }
  }

  async getProdVersionSetting(versionUuid) {
    const { app, ctx } = this;
    let setting = await this.app.redis.get(`APP_PROD_VERSION_SETTING:${versionUuid}`);
    if (!setting) {
      try {
        const result = await ctx.curl(app.config.backendURL + '/api/app/prod/version/getSetting', {
          method: 'GET',
          contentType: 'json',
          dataAsQueryString: true,
          dataType: 'json',
          data: {
            versionUuid
          }
        });
        if (result.data && result.data.code === 0) {
          await this.app.redis.set(`APP_PROD_VERSION_SETTING:${versionUuid}`, JSON.stringify(result.data.data));
          return result.data.data;
        }
      } catch (error) {
        app.logger.error('获取产品版本设置异常：%s', error);
      }
    } else {
      return JSON.parse(setting);
    }

    return null;
  }

  async getProdVersionDefaultTheme(versionUuid) {
    let themeJson = await this.app.redis.get(`THEME:${versionUuid}`);
    if (!themeJson) {
      let setting = await this.getProdVersionSetting(versionUuid);
      if (setting && setting.theme) {
        let themeData = JSON.parse(setting.theme).pc,
          defaultTheme = themeData.defaultTheme,
          colorClass = undefined,
          classScope = [];
        for (let t of themeData.theme) {
          classScope.push(t.themeClass);
          if (t.themeClass == defaultTheme) {
            colorClass = t.colorClass;
          }
        }
        themeJson = { class: defaultTheme, colorClass, classScope };
        await this.app.redis.set(`THEME:${versionUuid}`, JSON.stringify(themeJson));
      }
    } else {
      themeJson = JSON.parse(themeJson);
    }
    return themeJson;
  }

  async getProdVersionPageTheme(prodVersionUuid, pageId) {
    const { app, ctx } = this;
    let themeJson = await this.app.redis.get(`PROD_VERSION_PAGE_BIND_THEME:${prodVersionUuid}:${pageId}`);
    if (!themeJson) {
      const result = await ctx.curl(app.config.backendURL + '/api/app/prod/version/getProdVersionPageTheme', {
        method: 'GET',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: {
          prodVersionUuid,
          pageId
        }
      });
      if (result.data && result.data.code === 0) {
        if (result.data.data) {
          themeJson = result.data.data;
          await this.app.redis.set(`PROD_VERSION_PAGE_BIND_THEME:${prodVersionUuid}:${pageId}`, themeJson);
          themeJson = JSON.parse(themeJson);
        }
      }
    } else {
      themeJson = JSON.parse(themeJson);
    }
    return themeJson;
  }

  async clearProdVersionBindPageThemeCache(prodVersionUuid) {
    const { app, ctx } = this;

    const result = await ctx.curl(app.config.backendURL + '/api/app/prod/version/getProdVersionRelaPage', {
      method: 'GET',
      contentType: 'json',
      dataAsQueryString: true,
      dataType: 'json',
      data: {
        prodVersionUuid
      }
    });
    if (result.data && result.data.code === 0 && result.data.data) {
      for (let d of result.data.data) {
        this.app.redis.del(`PROD_VERSION_PAGE_BIND_THEME:${prodVersionUuid}:${d.pageId}`);
      }
    }
  }

  async getUserPreferenceProdTheme() {
    const { app, ctx } = this;
    let key = `user:preference:${ctx.PROD_VERSION_UUID}:${ctx.user.userId}:theme`;
    let themeJSON = await app.redis.get(key);
    if (themeJSON) {
      return JSON.parse(themeJSON);
    } else {
      let value = await ctx.service.userPreferenceService.getValuePreferences({
        userId: ctx.user.userId,
        moduleId: ctx.PROD_VERSION_UUID,
        dataKey: 'USER_THEME'
      });
      if (value.data) {
        app.redis.set(key, value.data);
        return JSON.parse(value.data);
      }
    }
    return null;
  }

  async getUserPreferenceSystemLayout(system) {
    const { app, ctx } = this;
    let key = `user:preference:${system || ctx.SYSTEM_ID}:${ctx.user.userId}:systemLayout`;
    let json = await app.redis.get(key);
    if (json) {
      return JSON.parse(json);
    } else {
      let value = await ctx.service.userPreferenceService.getValuePreferences({
        userId: ctx.user.userId,
        moduleId: system || ctx.SYSTEM_ID,
        dataKey: 'USER_SYSTEM_LAYOUT'
      });
      if (value.data) {
        app.redis.set(key, value.data);
        return JSON.parse(value.data);
      }
    }
    return null;
  }

  async getUserPreferenceSystemTheme(system) {
    const { app, ctx } = this;
    let key = `user:preference:${system || ctx.SYSTEM_ID}:${ctx.user.userId}:theme`;
    let themeJSON = await app.redis.get(key);
    if (themeJSON) {
      return JSON.parse(themeJSON);
    } else {
      let value = await ctx.service.userPreferenceService.getValuePreferences({
        userId: ctx.user.userId,
        moduleId: system || ctx.SYSTEM_ID,
        dataKey: 'USER_THEME'
      });
      if (value.data) {
        // SYSTEM_USER_THEME
        app.redis.set(key, value.data);
        return JSON.parse(value.data);
      }
    }
    return null;
  }
}

module.exports = AppProdIntegrationService;
