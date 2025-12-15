'use strict';

const Controller = require('wellapp-framework').Controller;

class ReadMarkController extends Controller {
  async readMarkerAdd() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/readMarker/add', {
        method: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }

  async readMarkerUnReadList() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/readMarker/unReadList', {
        method: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }

  async readMarkerDel() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/readMarker/del', {
        method: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }
}

module.exports = ReadMarkController;
