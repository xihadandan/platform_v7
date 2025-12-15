'use strict';

const Controller = require('wellapp-framework').Controller;

class ImportExportMgrController extends Controller {
  async getRequire() {
    const { app, ctx } = this;
    const result = await ctx.service.importExportMgrService.getRequire();
    ctx.body = result;
  }
  async postRequire() {
    const { app, ctx } = this;
    const result = await ctx.service.importExportMgrService.postRequire();
    ctx.body = result;
  }
}

module.exports = ImportExportMgrController;
