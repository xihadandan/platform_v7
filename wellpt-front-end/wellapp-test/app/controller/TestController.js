'use strict';

const Controller = require('wellapp-framework').Controller;

class TestController extends Controller {
  // 测试互联网用户注册
  async registerInternetUser() {
    await this.ctx.render('/iuser/register');
  }

  async checkIuser() {
    const result = await this.ctx.curl(this.app.config.backendURL + '/iuser/check', {
      method: 'GET',
      contentType: 'json',
      dataType: 'json',
      dataAsQueryString: true,
      data: {
        loginName: this.ctx.query.loginName
      }
    });
    this.ctx.body = result.data;
  }

  async postIuser() {
    const result = await this.ctx.curl(this.app.config.backendURL + '/iuser', {
      method: 'POST',
      contentType: 'json',
      dataType: 'json',
      data: this.ctx.req.body
    });
    this.ctx.body = result.data;
  }
}

module.exports = TestController;
