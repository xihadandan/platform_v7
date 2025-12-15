'use strict';

const Controller = require('wellapp-framework').Controller;

class ModuleController extends Controller {
  async index() {
    await this.ctx.render('module-center/index.js', {
      selectedKey: this.ctx.query.selectedKey
    });
  }

  async assemble() {
    let result = await this.ctx.curl(this.app.config.backendURL + '/api/app/module/details/' + this.ctx.params.uuid, {
      method: 'get',
      contentType: 'json',
      dataType: 'json'
    });
    if (!result.data.data) {
      result = await this.ctx.curl(this.app.config.backendURL + '/api/app/module/detailsById/' + this.ctx.params.uuid, {
        method: 'get',
        contentType: 'json',
        dataType: 'json'
      });
    }
    await this.ctx.render('module-center/assemble.js', {
      domain: this.ctx.app.config.domain,
      h5Server: this.ctx.app.config.h5Server,
      moduleDetail: result.data.data
      // moduleUuid: this.ctx.params.uuid
    });
  }
}

module.exports = ModuleController;
