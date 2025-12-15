'use strict';

const Controller = require('wellapp-framework').Controller;

class HelloWorldController extends Controller {
  async index() {
    await this.ctx.render('hello/index.js', {});
  }
}

module.exports = HelloWorldController;
