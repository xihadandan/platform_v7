const Controller = require('egg').Controller;
const OAuth2Server = require('oauth2-server');
const jwt = require('jsonwebtoken');

class OAuth2Controller extends Controller {
  // 授权端点
  async authorize() {
    const { ctx, app } = this;
    let request = new OAuth2Server.Request(ctx.request), response = new OAuth2Server.Response(ctx.response);
    try {
      // 验证客户端
      const client = await ctx.service.oAuth2Service.authorizeHandler.getClient(request)
    } catch (error) {
      app.logger.error('授权客户端校验异常: %s', error.message)
      await ctx.renderError(403, '认证客户端错误', '认证失败: 无效的应用配置或应用配置错误, 请联系管理员');
      return;
    }

    // 检查用户是否登录
    if (!ctx.user) {
      ctx.session.returnTo = ctx.request.url;
      return ctx.redirect(`/oauth/login`);
    }
    const rst = await ctx.service.oAuth2Service.oauth.authorize(request, response, {
      authenticateHandler: {
        handle() {
          let user = {
            userId: ctx.user.userId,
            userName: ctx.user.userName,
            loginName: ctx.user.loginName
          };
          if (ctx.user.photoUuid) {
            user.avatar = `${ctx.origin}/proxy/org/user/view/photo/${ctx.user.photoUuid}`
          }
          return user;
        }
      }
    });
    ctx.redirect(response.headers.location)
  }

  async login() {
    const { ctx, app } = this;
    let result = await ctx.service.appLoginConfService.getEnableTenantSystemLoginPagePolicy('PRD_PT', 'T001');
    if (result) {
      let { title, defJson, loginPolicy } = result;
      await ctx.render('login/index.js', {
        loginConfig: JSON.parse(defJson),
        loginPolicy,
        title: '统一认证登录'
      })
    }
  }

  // 令牌端点
  async token() {
    const { ctx } = this;
    try {
      const token = await ctx.service.oAuth2Service.token();
      ctx.body = token;
    } catch (err) {
      ctx.body = { error: err.message };
      ctx.status = err.status || 500;
    }
  }


  // 模拟测试客户端回调获取token
  async testClientCallback() {
    const { ctx } = this;
    const result = await this.ctx.curl('http://localhost:7001/oauth/token', {
      method: 'post',
      contentType: 'application/x-www-form-urlencoded',
      dataType: 'json',
      data: {
        grant_type: 'authorization_code', code: this.ctx.req.query.code
        , redirect_uri: 'http://localhost:7001/oauth/test/client/callback'
        , client_id: 'test_client'
        , client_secret: 'testsecret'
      }
    });
    if (result.data && result.data.accessToken) {
      var decoded = jwt.verify(result.data.accessToken, this.app.jwtSecretKey, {
        algorithm: 'HS256'
      });

      console.log('解密token', decoded)
    }
    this.ctx.body = result;
    // ctx.body = 'client callback'
  }
}

module.exports = OAuth2Controller;
