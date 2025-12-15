'use strict';

const Controller = require('wellapp-framework').Controller;

class ApiAppColorSettingController extends Controller {
  async saveBean() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/webapp/color/setting/saveBean', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('保存：%s', error);
    }
  }

  async deleteBean() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(
        app.config.backendURL + `/api/webapp/color/setting/deleteBean?moduleCode=${ctx.query.moduleCode}&type=${ctx.query.type}`,
        {
          method: 'POST',
          contentType: 'json',
          dataType: 'json',
          data: ctx.request.body
        }
      );
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('删除：%s', error);
    }
  }

  async getAllBean() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/webapp/color/setting/getAllBean', {
        method: 'get',
        dataType: 'json',
        data: ctx.query,
        dataAsQueryString: true
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('获取：%s', error);
    }
  }

  async getBeanByFilter() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/webapp/color/setting/getBeanByFilter', {
        method: 'get',
        dataType: 'json',
        data: ctx.query,
        dataAsQueryString: true
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('获取：%s', error);
    }
  }
}

module.exports = ApiAppColorSettingController;
