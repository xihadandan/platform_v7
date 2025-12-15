'use strict';

const Controller = require('wellapp-framework').Controller;

class ProductController extends Controller {

  async index() {
    await this.ctx.render('product-center/index.js', {
      selectedKey: this.ctx.query.selectedKey
    });
  }

  async assemble() {
    let result = await this.ctx.curl(this.app.config.backendURL + '/api/app/prod/getDetail', {
      method: 'get',
      contentType: 'json', dataType: 'json',
      dataAsQueryString: true,
      data: {
        uuid: this.ctx.params.uuid,
      }
    });
    if (!result.data.data) {
      result = await this.ctx.curl(this.app.config.backendURL + '/api/app/prod/getDetail', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: {
          id: this.ctx.params.uuid,
        }
      });
    }
    if (!this.ctx.hasAnyUserRole('ROLE_ADMIN') && result.data.data && (['PRD_PT', 'system_manager'].includes(result.data.data.id) || (result.data.data.id.startsWith('pt_')
      || result.data.data.id.startsWith('pt-')))) {
      // 内置的平台级产品，设置为仅超管人员才能访问
      await this.ctx.renderError('403', '无权限');
      return;

    }
    await this.ctx.render('product-center/assemble.js', {
      product: result.data.data,
      domain: this.ctx.app.config.domain,
      h5Server: this.ctx.app.config.h5Server,
    });
  }

  async latestPublishedIndexUrl() {
    let result = await this.ctx.curl(this.app.config.backendURL + '/api/app/prod/version/latestPublishedIndexUrl', {
      method: 'get',
      contentType: 'json', dataType: 'json',
      dataAsQueryString: true,
      data: this.ctx.query
    });
    this.ctx.body = result.data.data;

  }



}

module.exports = ProductController
