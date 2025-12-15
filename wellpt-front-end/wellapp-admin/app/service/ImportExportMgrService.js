'use strict';

const Service = require('wellapp-framework').Service;

class ImportExportMgrService extends Service {
  async getRequire() {
    const { app, ctx } = this;
    var url = ctx.originalUrl.split('?')[0];
    const result = await ctx.curl(app.config.backendURL + url, {
      method: 'GET',
      dataType: 'json',
      contentType: 'json', //application/json
      data: ctx.req.query
    });
    return result.data;
  }
  //Post请求
  async postRequire() {
    const { app, ctx } = this;
    const result = await ctx.curl(app.config.backendURL + ctx.originalUrl, {
      type: 'POST',
      dataType: 'json',
      contentType: 'json', //'application/x-www-form-urlencoded',
      // headers: {
      //     'content-type': 'application/x-www-form-urlencoded',
      // },
      data: ctx.req.body
    });
    return result.data;
  }
}

module.exports = ImportExportMgrService;
