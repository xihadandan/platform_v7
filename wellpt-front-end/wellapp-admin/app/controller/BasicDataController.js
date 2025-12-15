'use strict';

const Controller = require('wellapp-framework').Controller;

class BasicDataController extends Controller {
  async getSystemParam() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/basicdata/system/param/get', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('获取系统参数配置异常：%s', error);
    }
  }

  async selectiveDataGet() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/basicdata/selective/data/get', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('下拉选项数据获取异常：%s', error);
    }
  }

  async treeComponetLoadTree() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/basicdata/treecomponent/loadTree', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('加载树形数据异常：%s', error);
    }
  }

  async loadDataStoreTree() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/basicdata/treecomponent/loadDataStoreTree', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('加载树形数据异常：%s', error);
    }
  }

  async workhourDayList() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/basicdata/workhour/' + ctx.params.type, {
        method: 'POST',
        contentType: 'form',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('工作日数据异常：%s', error);
    }
  }

  async workhourDaySave() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/basicdata/workhour/save.action', {
        method: 'GET',
        contentType: 'json',
        dataType: 'text',
        dataAsQueryString: true,
        data: ctx.req.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('保存工作日数据异常：%s', error);
    }
  }

  async querySystemParam() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/basicdata/system/param/query', {
        method: 'GET',
        contentType: 'json',
        dataType: 'text',
        dataAsQueryString: true,
        data: ctx.req.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('系统参数查询异常：%s', error);
    }
  }
}

module.exports = BasicDataController;
