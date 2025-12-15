'use strict';

const Controller = require('egg').Controller;

class ConfigController extends Controller {
  async getWebConfigByKey() {
    let keys = this.ctx.queries.key;
    let map = {};
    for (let k of keys) {
      map[k] = this.ctx.app.config[k];
    }
    this.ctx.body = map;
  }


}

module.exports = ConfigController;
