'use strict';

const Controller = require('wellapp-framework').Controller;
const fs = require('fs');
const path = require('path');

class IndexController extends Controller {
  async error() {
    const ctx = this.ctx;
    // 当后端接口token过期时候，数据处理异常会导致500错误，这里统一进行跳转登录页，不再显示500页面
    if (ctx.session.messages && ctx.session.messages.indexOf('ApiTokenExpired') != -1) {
      ctx.redirect('/login?tokenExpired');
      return;
    }
    const status = ctx.params.status || ctx.query.real_status;
    await ctx.renderError(status);
  }

  async _index() {
    this.ctx.redirect(this.app.config.index.page);
  }

  async spa() {
    await this.ctx.render('app/app.js', { url: this.ctx.url.replace('/app', '') });
  }

  async images() {
    this.ctx.redirect(this.ctx.req.url.replace('/resources/pt/images/', global.STATIC_PREFIX + '/images/'));
  }

  async helloVue() {
    await this.ctx.render('sample/index.js', { message: 'from server !!!' });
  }

  async excelweb() {
    await this.ctx.render('/excelweb.nj', {});
  }

  async getAmapConfig() {
    this.ctx.body = this.app.config.aMapConfig;
  }



}

module.exports = IndexController;
