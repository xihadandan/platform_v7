'use strict';

const Controller = require('wellapp-framework').Controller;
// const lodash = require('lodash');
// const constant = require('./constant');

class PtOfdViewerController extends Controller {
  /**
   * 数科ofd页面
   **/
  async suWellOfdOnlineEditView() {
    const { app, ctx } = this;
    let query = this.ctx.query;

    var backendURL = app.config.backendURL;
    var ofdParam = {
      ...query,
      backendURL: backendURL
    };
    await ctx.render('/ofd/suwellofdviewer.nj', {
      ofdParam
    });
  }
}

module.exports = PtOfdViewerController;
