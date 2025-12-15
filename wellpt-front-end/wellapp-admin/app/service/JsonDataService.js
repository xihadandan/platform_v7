'use strict';

const Service = require('wellapp-framework').Service;

class JsonDataService extends Service {
  async invoke(requestBody) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: requestBody,
        headers: {
          ...ctx.headers,
          debug: app.options.isDebug // 是否调试模式
        }
      });

      if (result.status == 417) {
        // 业务封装的异常
        ctx.status = 417;
      } else if (result.data) {
        if (result.data.success === false || result.data.errorCode) {
          // 业务封装的异常
          ctx.status = 417;
        }
      }

      return result.data;
    } catch (error) {
      app.logger.error('JDS调用后端服务接口异常：%s', error);
    }
    return null;
  }

  async _call(serviceName, methodName, args) {
    const { app, ctx } = this;
    try {
      var config = {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          serviceName,
          methodName,
          version: '',
          args: JSON.stringify(args)
        }
      };
      config = ctx.service.workflowTaskService.addDbHeader(config);
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', config);
      if (result.data && result.data.code === 0) {
        return result.data.data;
      } else {
        app.logger.error('jds调用异常：%s', result.data);
      }
    } catch (error) {
      app.logger.error('JDS调用后端服务接口异常：%s', error);
    }
    return null;
  }
}

module.exports = JsonDataService;
