'use strict';

const Service = require('wellapp-framework').Service;

class MobileAppService extends Service {

  async getFormDefinitionByUuid(uuid) {
    try {
      const { app, ctx } = this;
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/definition/getFormDefinitionByUuid', {
        method: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: { formUuid: uuid }
      });
      if (result.data) {
        return result.data;
      }
    } catch (error) {
      app.logger.error('查询表单定义异常：%s', error);
    }
  }
}

module.exports = MobileAppService;
