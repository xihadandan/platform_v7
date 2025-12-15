'use strict';

const Service = require('wellapp-framework').Service;

class DocumentLinkService extends Service {
  async checkSourceDataByUuid(uuid) {
    const { app, ctx } = this;
    const result = await ctx.curl(app.config.backendURL + '/api/document/link/checkSourceDataByUuid/' + uuid, {
      method: 'GET',
      contentType: 'json',
      dataType: 'json'
    });
    return result.data.data;
  }
}

module.exports = DocumentLinkService;
