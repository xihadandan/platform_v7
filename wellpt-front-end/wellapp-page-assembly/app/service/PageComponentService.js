'use strict';

const Service = require('wellapp-framework').Service;

class PageComponentService extends Service {

  async getWidgetUsageFrequency() {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST', contentType: 'json', dataType: 'json',
        data: {
          serviceName: 'appWidgetDefinitionService', methodName: 'getWidgetUsageFrequency',
          version: '', args: JSON.stringify([]),
        },
      });
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('组件使用频率服务请求异常：%s', error);
    }
    return null;
  }
}

module.exports = PageComponentService;
