'use strict';

const Controller = require('wellapp-framework').Controller;

class LayoutDocumentServiceConfController extends Controller {
  async getByUuid() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/basicdata/layoutDocumentServiceConf/getByUuid`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async saveBean() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/basicdata/layoutDocumentServiceConf/saveBean', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('保存版式文档服务配置异常：%s', error);
    }
  }

  async deleteByUuids() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/basicdata/layoutDocumentServiceConf/deleteByUuids', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        dataAsQueryString: true
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('删除版式文档服务配置异常：%s', error);
    }
  }

  async getEnableConfigList() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/basicdata/layoutDocumentServiceConf/getEnableConfigList`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json'
        // data: ctx.query,
        // dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async changeLayoutDocumentConfigStatus() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/basicdata/layoutDocumentServiceConf/changeLayoutDocumentConfigStatus', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        dataAsQueryString: true
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('启用版式文档配置：%s', error);
    }
  }

  async beforeEnableLayoutDocumentConfig() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/basicdata/layoutDocumentServiceConf/beforeEnableLayoutDocumentConfig`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json'
        // data: ctx.query,
        // dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }
}

module.exports = LayoutDocumentServiceConfController;
