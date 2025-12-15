'use strict';

const Controller = require('wellapp-framework').Controller;

class DyformValidateController extends Controller {


  async existsFormData() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/data/validate/exists', {
        method: 'POST', contentType: 'json', dataType: 'json',
        data: ctx.request.body,
      });
      ctx.body = result.data;
    } catch (e) {
      app.logger.error('查询表单数据是否存在异常：%s', e);
    }
  }

}


module.exports = DyformValidateController;
