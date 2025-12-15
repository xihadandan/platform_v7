'use strict';

const Controller = require('wellapp-framework').Controller;

class PrivilegeController extends Controller {
  async getOtherResourceTreeOnlyCheck() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/security/privilege/getOtherResourceTreeOnlyCheck`, {
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

  async publishPrivilegeUpdatedEvent() {
    await this.ctx.service.jsonDataService._call('privilegeFacadeService', 'publishPrivilegeUpdatedEvent', [this.ctx.query.uuid]);
    this.ctx.service.pageDefinitionService.clearPageCacheData(null, null, 20);
  }
}

module.exports = PrivilegeController;
