'use strict';

const Controller = require('wellapp-framework').Controller;

class UserPersonaliseController extends Controller {
  async queryList() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/userPersonalise/queryList', {
        method: 'GET',
        contentType: 'application/json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('查询用户信息列表异常：%s', error);
    }
  }
  async saveUserPersonalise() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/userPersonalise/saveUserPersonalise', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('保存用户信息异常：%s', error);
    }
  }

  async reset() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/userPersonalise/reset', {
        method: 'put',
        contentType: 'application/json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('用户信息重置异常：%s', error);
    }
  }

  async userHeadImg() {
    this.ctx.redirect2backend('/personalinfo/userImg?id=' + (this.ctx.req.query.id || ''));
  }

  async modifyPassword() {
    await this.ctx.render('/personalinfo/modify-personal-password', { userUuid: this.ctx.user.userUuid });
  }
}

module.exports = UserPersonaliseController;
