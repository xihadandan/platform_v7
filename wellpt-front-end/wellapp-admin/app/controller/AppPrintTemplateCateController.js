'use strict';

const Controller = require('wellapp-framework').Controller;

class AppPrintTemplateCateController extends Controller {
  async saveCate() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/printTemplate/category/save`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async queryCate() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/printTemplate/category/getTreeAllBySystemUnitIdsLikeName`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async deleteCate() {
    const { ctx, app } = this;
    try {
      const url = `${app.config.backendURL}/api/printTemplate/category/deleteWhenNotUsed`;

      const result = await ctx.curl(url, {
        method: 'post',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async getCateDetail() {
    const { ctx, app } = this;
    try {
      const url = `${app.config.backendURL}/api/printTemplate/category/get`;

      const result = await ctx.curl(url, {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }
}

module.exports = AppPrintTemplateCateController;
