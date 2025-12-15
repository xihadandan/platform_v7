'use strict';

const Service = require('wellapp-framework').Service;
const lodash = require('lodash');

class WorkflowTaskService extends Service {
  async newWork(flowDefUuid) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          serviceName: 'workService',
          methodName: 'newWork',
          version: '',
          args: JSON.stringify([flowDefUuid])
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

  async newWorkById(id) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          serviceName: 'workService',
          methodName: 'newWorkById',
          version: '',
          args: JSON.stringify([id])
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

  async hasPermission(taskInstUuid, masks) {
    const { app, ctx } = this;
    var config = {
      method: 'POST',
      contentType: 'json',
      dataType: 'json',
      data: {
        serviceName: 'taskService',
        methodName: 'hasPermissionCurrentUser',
        version: '',
        args: JSON.stringify([taskInstUuid, masks])
      }
    };
    config = this.addDbHeader(config);
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', config);
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('%s', error);
    }
    return false;
  }

  async hasViewPermission(taskInstUuid) {
    const { app, ctx } = this;
    var config = {
      method: 'POST',
      contentType: 'json',
      dataType: 'json',
      data: {
        serviceName: 'taskService',
        methodName: 'hasViewPermissionCurrentUser',
        version: '',
        args: JSON.stringify([taskInstUuid])
      }
    };
    config = this.addDbHeader(config);
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', config);
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('%s', error);
    }
    return false;
  }

  //获取get请求配置
  getReqGetConfig() {
    var config = {
      method: 'get',
      contentType: 'json',
      dataType: 'json'
    };
    config = this.addDbHeader(config);
    return config;
  }

  addDbHeader(config) {
    const { ctx } = this;
    var dbLinkConfUuid = ctx.query.dbLinkConfUuid;
    if (dbLinkConfUuid) {
      if (!config.hasOwnProperty('headers')) {
        config['headers'] = {};
      }
      config.headers.dbLinkConfUuid = dbLinkConfUuid;
    }
    return config;
  }

  async getWorkBean(taskInstUuid, flowInstUuid, methodName) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          serviceName: 'workService',
          methodName,
          version: '',
          args: JSON.stringify([taskInstUuid, flowInstUuid])
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

  async getDoneWorkData(taskInstUuid, flowInstUuid, taskIdentityUuid = '') {
    const { app, ctx } = this;
    var config = this.getReqGetConfig();
    const result = await ctx.curl(
      app.config.backendURL +
      `/api/workflow/work/getDoneWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}&taskIdentityUuid=${taskIdentityUuid}`,
      config
    );
    const workBean = result.data && result.data.data;
    return workBean;
  }

  async getReadWorkData(taskInstUuid, flowInstUuid) {
    const { app, ctx } = this;
    var config = this.getReqGetConfig();
    const result = await ctx.curl(
      app.config.backendURL + `/api/workflow/work/getReadWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`,
      config
    );
    const workBean = result.data && result.data.data;
    return workBean;
  }

  async getAttentionWorkData(taskInstUuid, flowInstUuid) {
    const { app, ctx } = this;
    var config = this.getReqGetConfig();
    const result = await ctx.curl(
      app.config.backendURL + `/api/workflow/work/getAttentionWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`,
      config
    );
    const workBean = result.data && result.data.data;
    return workBean;
  }

  async getMonitorWorkData(taskInstUuid, flowInstUuid) {
    const { app, ctx } = this;
    var config = this.getReqGetConfig();
    const result = await ctx.curl(
      app.config.backendURL + `/api/workflow/work/getMonitorWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`,
      config
    );
    const workBean = result.data && result.data.data;
    return workBean;
  }

  async getSuperviseWorkData(taskInstUuid, flowInstUuid) {
    const { app, ctx } = this;
    var config = this.getReqGetConfig();
    const result = await ctx.curl(
      app.config.backendURL + `/api/workflow/work/getSuperviseWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`,
      config
    );
    const workBean = result.data && result.data.data;
    return workBean;
  }

  async getViewerWorkData(taskInstUuid, flowInstUuid) {
    const { app, ctx } = this;
    var config = this.getReqGetConfig();
    const result = await ctx.curl(
      app.config.backendURL + `/api/workflow/work/getViewerWorkData?taskInstUuid=${taskInstUuid}&flowInstUuid=${flowInstUuid}`,
      config
    );
    const workBean = result.data && result.data.data;
    return workBean;
  }

  async getTaskOperation(taskOperationUuid) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          serviceName: 'workService',
          methodName: 'getTaskOperation',
          version: '',
          args: JSON.stringify([taskOperationUuid])
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

  async taskMultiUserSubmitType(flowDefinitionUuid, taskId) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/taskMultiUserSubmitType', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          taskId,
          flowDefinitionUuid
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

  async getFlowSettings(systemId = '') {
    const { ctx, app } = this;
    let settings = await app.redis.get(`FLOW_SETTING:${systemId}`);
    if (!settings) {
      try {
        const result = await ctx.curl(app.config.backendURL + '/api/workflow/setting/list', {
          method: 'GET',
          contentType: 'json',
          dataType: 'json'
        });
        if (result.data && result.data.code === 0) {
          settings = {};
          lodash.forEach(result.data.data || [], item => {
            settings[item.attrKey] = JSON.parse(item.attrVal);
            settings[item.attrKey].enabled = item.enabled;
            settings[item.attrKey].category = item.category;
          });
          await app.redis.set(`FLOW_SETTING:${systemId}`, JSON.stringify(settings));
          return settings;
        }
      } catch (error) {
        app.logger.error('获取流程设置异常：%s', error);
      }
    } else {
      settings = JSON.parse(settings);
    }
    return settings;
  }
}

module.exports = WorkflowTaskService;
