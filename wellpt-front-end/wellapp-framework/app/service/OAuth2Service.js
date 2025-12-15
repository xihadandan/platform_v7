const { Service } = require('egg');
const OAuth2Server = require('oauth2-server');
const crypto = require('crypto');
const AuthorizeHandler = require('oauth2-server/lib/handlers/authorize-handler')
const jwt = require('jsonwebtoken');

class OAuth2Service extends Service {
  constructor(ctx) {
    super(ctx);
    this.oauthOption = {
      model: this, // 使用当前 service 作为 model
      accessTokenLifetime: 60 * 60 * 24, // 24小时 (单位: 秒)
      authorizationCodeLifetime: 5 * 60,  // 5 分钟 (单位: 秒)
      allowBearerTokensInQueryString: true,
      allowEmptyState: true
    };
    this.oauth = new OAuth2Server(this.oauthOption);
    this.authorizeHandler = new AuthorizeHandler(this.oauthOption);
  }

  // 以下是 oauth2-server 需要的 model 方法

  // 获取客户端信息
  async getClient(clientId, clientSecret) {
    // TODO: 从后端服务加载客户端信息
    const clients = [
      {
        id: 'test_client',
        secret: 'testsecret',
        grants: ['password', 'authorization_code', 'refresh_token'],
        redirectUris: ['http://localhost:7001/oauth/test/client/callback'],
      },
    ];

    const client = clients.find(c => c.id === clientId);

    if (!client) {
      return false;
    }

    if (clientSecret && client.secret !== clientSecret) {
      return false;
    }

    return {
      id: client.id,
      grants: client.grants,
      redirectUris: client.redirectUris,
    };
  }

  // 密码模式认证使用：获取用户信息
  async getUser(username, password) {
    // TODO: 从后端服务获取用户
    return {
    };
  }
  // 生成随机令牌
  generateToken(length = 32) {
    return crypto.randomBytes(length).toString('hex');
  }

  async generateAccessToken(client, user, scope) {
    const { app } = this;
    const code = this.ctx.req.body.code; // 授权码
    let data = await app.redis.get('oauth2:authorizationCode:' + code + ':user');
    data = data ? JSON.parse(data) : undefined;
    const token = jwt.sign({ subject: data ? data.loginName : code, user: data }, this.app.jwtSecretKey.trim(), {
      algorithm: 'HS256',
      expiresIn: this.oauthOption.accessTokenLifetime
    });
    return token;
  }

  // 保存授权码
  async saveAuthorizationCode(code, client, user) {
    let { ctx, app } = this, { authorizationCode, expiresAt, redirectUri, scope } = code;
    app.redis.set('oauth2:authorizationCode:' + authorizationCode, JSON.stringify(
      { user, expiresAt: expiresAt.getTime(), client, redirectUri, authorizationCode }
    ), 'EX', parseInt(expiresAt.getTime() / 1000));
    app.redis.set('oauth2:authorizationCode:' + authorizationCode + ':user', JSON.stringify(ctx.user), 'EX', parseInt(expiresAt.getTime() / 1000));
    return {
      ...code,
      user,
      client,
    };
  }

  // 获取（验证）授权码
  async getAuthorizationCode(authorizationCode) {
    let { ctx, app } = this;
    let data = await app.redis.get('oauth2:authorizationCode:' + authorizationCode);
    data = data ? JSON.parse(data) : undefined;
    if (data) {
      data.expiresAt = new Date(parseInt(data.expiresAt));
    }
    return data;
  }

  // 撤销授权码
  async revokeAuthorizationCode(code) {
    return true;
  }

  // 保存令牌
  async saveToken(token, client, user) {
    let { app } = this;
    let { accessToken, authorizationCode, accessTokenExpiresAt, refreshToken, refreshTokenExpiresAt, } = token;
    // app.redis.set('oauth2:accessToken:' + accessToken, JSON.stringify(
    //   { user, expiresAt: expiresAt.getTime(), client, redirectUri, authorizationCode, token: ctx.user.token }
    // ), 'EX', parseInt(expiresAt.getTime() / 1000));
    return {
      ...token,
      client,
      user,
    };
  }

  // 获取访问令牌
  async getAccessToken(accessToken) {
    return {
      accessToken,
      client: { id: 'testclient' },
      user: { id: '1' },
      expires: new Date(Date.now() + 86400000), // 24小时后过期
    };
  }

  // 验证范围
  async verifyScope(token, scope) {
    return true;
  }

  // 以下是封装给控制器使用的方法

  async token() {
    const { ctx } = this;
    const request = new OAuth2Server.Request(ctx.request);
    const response = new OAuth2Server.Response(ctx.response);

    try {
      return await this.oauth.token(request, response);
    } catch (err) {
      ctx.throw(err.status || 500, err.message);
    }
  }

  // async authenticate() {
  //   const { ctx } = this;
  //   const request = new OAuth2Server.Request(ctx.request);
  //   const response = new OAuth2Server.Response(ctx.response);

  //   try {
  //     return await this.oauth.authenticate(request, response);
  //   } catch (err) {
  //     ctx.throw(err.status || 401, err.message);
  //   }
  // }

  // async authorize() {
  //   const { ctx } = this;
  //   const request = new OAuth2Server.Request(ctx.request);
  //   const response = new OAuth2Server.Response(ctx.response);

  //   try {
  //     return await this.oauth.authorize(request, response);
  //   } catch (err) {
  //     ctx.throw(err.status || 500, err.message);
  //   }
  // }
}

module.exports = OAuth2Service;
