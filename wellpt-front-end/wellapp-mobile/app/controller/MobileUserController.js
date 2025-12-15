'use strict';

const Controller = require('wellapp-framework').Controller;

class MobileUserController extends Controller {
  async login() {
    const { ctx, app } = this;
    if (ctx.user) {
      ctx.redirect('/web/app/pt-mobile/pt-mobile-base.html');
      return;
    }
    await this.ctx.render('/mobile/mui/login.nj');
  }

  async rest() {
    const { ctx, app } = this;
    let { json } = ctx.request.body;
    if (typeof json === 'string') {
      json = JSON.parse(json);
    }
    if (json && json.apiServiceName === 'security.login') {
      if (ctx.user) {
        // ctx.redirect('/web/app/pt-mobile/pt-mobile-base.html');
        ctx.body = {
          msg: 'logined',
          success: true,
          access_token: ctx.user.token
        };
        return;
      }
      let result;
      let { username, password } = ctx.request.body;
      try {
        result = await ctx.curl(app.config.backendURL + '/login/1', {
          method: 'POST',
          contentType: 'json',
          dataType: 'json',
          data: {
            username,
            password
          }
        });
      } catch (error) {
        throw new Error('登录超时');
      }
      let success = false,
        access_token,
        msg;
      if (result.status === 200) {
        if (result.data.code !== 0) {
          msg = result.data.msg;
        } else if (result.data) {
          ctx.req.session.messages = []; // 登录成功则清掉失败的消息
          result.data.data._AUTH = 'jwt'; // 标记为jwt认证
          success = true;
          access_token = result.data.data.token;
          ctx.req.login(result.data.data, { session: true }, function (err) {});
        }
      }
      ctx.body = {
        msg,
        success,
        access_token
      };
      return;
    }
    let url = new URL(app.config.backendURL);
    await ctx.proxyRequest(url.host, {
      withCredentials: true
    });
  }

  async mUserInfoViewer() {
    await this.ctx.render('/mobile/mui/userInfo.html');
  }

  async mModifyPasswordViewer() {
    await this.ctx.render('/mobile/mui/modifyPassword.html');
  }
}

module.exports = MobileUserController;
