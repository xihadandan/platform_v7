'use strict';

const Service = require('wellapp-framework').Service;

class BizProcessService extends Service {
  async newItemById(processDefId, processItemIds) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          serviceName: 'bizProcessItemFacadeService',
          methodName: 'newItemById',
          version: '',
          args: JSON.stringify([processDefId, processItemIds])
        }
      });
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('%s', error);
    }
    return null;
  }

  async getItemByUuid(itemInstUuid) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          serviceName: 'bizProcessItemFacadeService',
          methodName: 'getItemByUuid',
          version: '',
          args: JSON.stringify([itemInstUuid])
        }
      });
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('%s', error);
    }
    return null;
  }

  async getProcessNodeByUuid(processNodeInstUuid) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          serviceName: 'bizProcessNodeFacadeService',
          methodName: 'getProcessNodeByUuid',
          version: '',
          args: JSON.stringify([processNodeInstUuid])
        }
      });
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('%s', error);
    }
    return null;
  }

  async getProcessByUuid(processInstUuid) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          serviceName: 'bizProcessFacadeService',
          methodName: 'getProcessByUuid',
          version: '',
          args: JSON.stringify([processInstUuid])
        }
      });
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('%s', error);
    }
    return null;
  }
}

module.exports = BizProcessService;
