'use strict';

const Controller = require('wellapp-framework').Controller;

class UserController extends Controller {
  async queryDetails() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/security/user/details/', {
        method: 'GET',
        contentType: 'application/json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('查询用户详情异常：%s', error);
    }
  }

  async orgUserDialogPage() {
    const { ctx, app } = this;
    try {
      const themeFiles = await ctx.service.userThemeService.getThemeFiles();

      const result = await ctx.curl(app.config.backendURL + '/multi/org/orgTypesOptions', {
        method: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        data: { unitId: ctx.query.unitId }
      });

      await ctx.render('/organization/org_tree_dialog.nj', {
        orgOptionList: JSON.stringify(result.data.orgOptionsList),
        orgTypes: result.data.orgTypes,
        themeFiles
      });
    } catch (error) {
      this.app.logger.error('组织弹出框异常：%s', error);
    }
  }

  // async userImg() {
  //   this.ctx.redirect('/proxy/org/user/view/photo/' + this.ctx.params.uuid);
  // }

  async personalinfo() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/personalinfo/show/infoBody', {
        method: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('用户信息查询异常：%s', error);
    }
  }

  async savePreferences() {
    this.ctx.body = await this.ctx.service.userPreferenceService.savePreferences(this.ctx.req.body);
  }

  async getValuePreferences() {
    this.ctx.body = await this.ctx.service.userPreferenceService.getValuePreferences(this.ctx.query);
  }

  async getPreferences() {
    this.ctx.body = await this.ctx.service.userPreferenceService.getPreferences(this.ctx.query);
  }

  async resetUserDefinedPwd() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/user/account/resetUserDefinedPwd`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async resetAllUserDefinedPwd() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/user/account/resetAllUserDefinedPwd`, {
        method: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async commitUserBehaviorLog() {
    await this.ctx.service.userBehaviorLogService.commitBehaviorLog(this.ctx.user, this.ctx.req.body);
    this.ctx.body = {
      code: 0,
      msg: 'success'
    };
  }
}

module.exports = UserController;
