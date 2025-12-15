'use strict';

const Controller = require('wellapp-framework').Controller;

class OrgController extends Controller {
  async orgVersionExport() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/multi/org/version/export', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('组织版本导出异常：%s', error);
    }
  }

  async queryAllUserRoleInfoDtoListByUserIdAndRoleUuids() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/facade/queryAllUserRoleInfoDtoListByUserIdAndRoleUuids`, {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  // async queryAllUserRoleInfoDtoListByUserIdAndRoleUuids() {
  //   const {ctx, app} = this;
  //   try {
  //     const result = await ctx.curl(`${app.config.backendURL}/api/org/facade/queryAllUserRoleInfoDtoListByUserIdAndRoleUuids`, {
  //       method: 'get',
  //       contentType: 'application/json',
  //       dataType: 'json',
  //       data: ctx.query,
  //       dataAsQueryString: true
  //     });
  //     this.ctx.body = result.data;
  //   } catch (error) {
  //     this.app.logger.error(error);
  //   }
  // }

  async queryAllUserRoleInfoDtoListByUserId() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/facade/queryAllUserRoleInfoDtoListByUserId`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async orgManagerDetail() {
    // 获取组织归属的系统
    const result = await this.ctx.curl(`${this.app.config.backendURL}/api/org/organization/details/${this.ctx.query.uuid}`, {
      method: 'GET',
      contentType: 'json',
      dataType: 'json'
    });
    if (result.data.data == null) {
      await this.ctx.renderError('404');
      return;
    }
    // 非本组织内部成员不让访问
    let { system, id } = result.data.data;
    let systems = [];
    if (this.ctx.user && this.ctx.user.userSystemOrgDetails) {
      this.ctx.user.userSystemOrgDetails.details.forEach(d => {
        systems.push(d.system);
      });
    }
    if (systems.includes(system) || this.ctx.hasAnyUserRole('ROLE_TENANT_ADMIN', 'ROLE_ADMIN')) {
      await this.ctx.render('org-manager/detail.js', {
        SYSTEM_ID: system,
        uuid: this.ctx.query.uuid,
        id
      });
    } else {
      await this.ctx.renderError('403');
    }
  }

  async bizOrgManagerDetail() {
    // 获取组织归属的系统
    const result = await this.ctx.curl(`${this.app.config.backendURL}/api/org/biz/getBizOrgByUuid`, {
      method: 'GET',
      contentType: 'json',
      dataType: 'json',
      data: {
        uuid: this.ctx.query.uuid
      }
    });
    // 非本组织内部成员不让访问
    let { system, name } = result.data.data;
    let systems = [];
    if (this.ctx.user && this.ctx.user.userSystemOrgDetails) {
      this.ctx.user.userSystemOrgDetails.details.forEach(d => {
        systems.push(d.system);
      });
    }
    if (this.ctx.hasAnyUserRole('ROLE_TENANT_ADMIN', 'ROLE_ADMIN')) {
      await this.ctx.render('org-manager/biz-org-detail-manager.js', {
        SYSTEM_ID: system,
        uuid: this.ctx.query.uuid,
        title: name
      });
    } else {
      await this.ctx.renderError('403');
    }
  }

  async getNameByOrgEleIds() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/facade/getNameByOrgEleIds`, {
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

  async getCurrentUserProperty() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/facade/getCurrentUserProperty`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async getSystemParamsPwdTiming() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/pwd/setting/getSystemParamsPwdTiming`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async getMultiOrgPwdSetting() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/pwd/setting/getMultiOrgPwdSetting`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async saveMultiOrgPwdSetting() {
    const { ctx, app } = this;
    try {
      const isEnforceUpdatePwd = ctx.request.body.isEnforceUpdatePwd;
      const result = await ctx.curl(`${app.config.backendURL}/api/pwd/setting/saveMultiOrgPwdSetting`, {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async modifyCurrentUserPasswordEncrypt() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/personalinforest/modifyCurrentUserPasswordEncrypt`, {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.request.body
      });
      if (ctx.user.pwdRoleCheckObj) {
        ctx.user.pwdRoleCheckObj.openUpdatePwdPage = false;
      }
      this.ctx.body = result.data;
      // 账号锁定，redis记录 退出标记
      if (result.data.data.locked) {
        ctx.app.redis.set('logout_' + ctx.user.userId, ctx.user.userId);
      }
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async getPwdSettingTimerPageUrl() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/pwd/setting/getPwdSettingTimerPageUrl`, {
        method: 'GET',
        contentType: 'json',
        dataType: 'json'
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }
}

module.exports = OrgController;
