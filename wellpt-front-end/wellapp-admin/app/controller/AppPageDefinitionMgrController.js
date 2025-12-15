'use strict';

const Controller = require('wellapp-framework').Controller;

class AppPageDefinitionMgrController extends Controller {

  async listPath() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/page/definition/listPath`, {
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

}

module.exports = AppPageDefinitionMgrController;
