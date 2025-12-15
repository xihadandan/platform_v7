'use strict';

const Controller = require('wellapp-framework').Controller;

class ValidationController extends Controller {
  async metadata() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/common/validation/metadata', {
        method: 'GET', contentType: 'json', dataType: 'json',
        data: ctx.query,
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('数据校验规则获取异常：%s' , error);
    }

  }

  async checkExist(){
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/common/validate/check/exists', {
        method: 'POST', contentType: 'json', dataType: 'json',
        data: ctx.req.body,dataAsQueryString:true
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('数据校验异常：%s' , error);
    }
  }
}

module.exports = ValidationController;
