'use strict';

const Controller = require('wellapp-framework').Controller;
const is = require('is-type-of');

class DingtalkController extends Controller {
  async forwardDingtalkMethod() {
    const { ctx, app } = this;
    try {
      let contentType =
        ctx.req.headers['content-type'] && ctx.req.headers['content-type'].indexOf('application/x-www-form-urlencoded') != -1
          ? 'form'
          : 'json';
      const result = await ctx.curl(app.config.backendURL + '/pt/dingtalk/' + ctx.params.method, {
        method: ctx.req.method,
        contentType,
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('钉钉服务转发异常：%s', error);
    }
  }

  // app
  async dingtalkOauth2Start() {
    const { ctx, app } = this;
    const uri = ctx.query.uri || '/mobile/mui/login.jsp';
    if (ctx.user != null) {
      let result = await ctx.curl(app.config.backendURL + '/checkToken', {
        method: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        data: {
          token: ctx.user.token
        },
        dataAsQueryString: true
      });
      if (result.data.code == -3000) {
        //token失效
        ctx.logout();
      }
    }
    // 判断用户token是否有效
    if (null == ctx.user) {
      let appId = this.app.config.dingtalk.appId;
      if (is.function(appId)) {
        appId = appId(ctx);
      }
      let redirectUri = this.ctx.origin + '/mobile/pt/dingtalk/auth?uri=' + encodeURIComponent(uri);
      let url = this.app.config.dingtalk.oauth_sns_authorize_url + '?appid=' + appId + '&response_type=code&scope=snsapi_auth&state=snsapi_auth';
      url += '&redirect_uri=' + encodeURIComponent(redirectUri);
      ctx.redirect(url);
    } else {
      ctx.redirect(uri);
    }
  }

  async dingtalkOauth2Auth() {
    const { ctx, app } = this;
    const uri = ctx.query.uri || '/passport/user/login/success';
    if (ctx.user) {
      ctx.redirect(url);
    }
  }

  // pc扫码
  async dingtalkOauth2Contect() {
    const { ctx, app } = this;
    let appId = this.app.config.dingtalk.appId;
    if (is.function(appId)) {
      appId = appId(ctx);
    }
    let redirectUri = (this.app.config.dingtalk.corp_domain_uri || this.ctx.origin) + '/mobile/pt/dingtalk/auth?qrcode=1&uri=login?success';
    redirectUri = encodeURIComponent(redirectUri);
    let url = this.app.config.dingtalk.oauth_sns_authorize_url + '?appid=' + appId + '&response_type=code&scope=snsapi_login&state=STATE';
    if (this.ctx.query.loginTmpCode) {
      url += '&loginTmpCode=' + this.ctx.query.loginTmpCode;
    }
    url += '&redirect_uri=' + redirectUri;
    this.ctx.body = url;
  }

  async proxy() {
    const { ctx, app } = this;
    let url = new URL(app.config.backendURL);
    await ctx.proxyRequest(url.host, {
      withCredentials: true
    });
  }

  // 多部门人员审核
  async dingtalkGetMultiDeptUserAudit() {
    const { ctx, app } = this;

    try {
      const result = await ctx.curl(app.config.backendURL + '/pt/dingtalk/getMultiDeptUserAudit', {
        method: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('多部门人员详情获取异常：%s', error);
    }
  }
}

module.exports = DingtalkController;
