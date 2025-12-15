'use strict';

const Service = require('wellapp-framework').Service;

class UserPreferenceService extends Service {
  async savePreferences(params) {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/user/preferences/save`, {
        method: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: params,
        dataAsQueryString: true
      });
      app.appEmitter.emit('event.UserPreferencesUpdate', ctx.user, params);
      return result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async getValuePreferences(params) {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/user/preferences/getValue`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: params,
        dataAsQueryString: true
      });
      return result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async getPreferences(params) {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/user/preferences/get`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: params,
        dataAsQueryString: true
      });
      return result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }
}

module.exports = UserPreferenceService;
