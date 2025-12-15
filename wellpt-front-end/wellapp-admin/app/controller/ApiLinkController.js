'use strict';

const Controller = require('wellapp-framework').Controller;
const lodash = require('lodash');
const jwt = require('jsonwebtoken');
const qs = require('qs');
const FormData = require('form-data');
const axios = require('axios');
const soap = require('soap');

class ApiLinkController extends Controller {
  constructor(ctx) {
    super(ctx);
  }
  async postProxy() {
    const { ctx, app } = this;
    const { apiOperationUuid } = ctx.req.body;
    console.log('请求入参', ctx.req.body);
    const result = await ctx.curl(app.config.backendURL + '/api/apiLink/operationDetails/' + apiOperationUuid, {
      method: 'GET',
      contentType: 'json',
      dataType: 'json'
    });
    if (result.data) {
      let apiOperation = result.data.data,
        { apiLink } = apiOperation,
        invokeLog = {
          apiOperationUuid: apiOperationUuid,
          apiLinkUuid: apiLink.uuid,
          reqTime: new Date().getTime(),
          protocol: apiLink.protocol
        };
      try {
        let endpoint = undefined;
        if (app.config.env == 'local') {
          endpoint = apiLink.devEndpoint;
        } else if (app.config.env == 'unittest') {
          endpoint = apiLink.testEndpoint;
        } else if (app.config.env == 'stag') {
          endpoint = apiLink.stagEndpoint;
        } else if (app.config.env == 'prod') {
          endpoint = apiLink.prodEndpoint;
        }
        if (endpoint == undefined) {
          throw new Error('未配置 API 集成端点');
        }
        let authConfig = JSON.parse(apiLink.authConfig || '{}');
        if (app.config.env == 'local') {
          authConfig = authConfig.dev || {};
        } else if (app.config.env == 'unittest') {
          authConfig = authConfig.test || {};
        } else if (app.config.env == 'stag') {
          authConfig = authConfig.stag || {};
        } else if (app.config.env == 'prod') {
          authConfig = authConfig.prod || {};
        }

        let faultToleranceConfig = JSON.parse(apiLink.faultToleranceConfig || '{}');
        if (app.config.env == 'local') {
          faultToleranceConfig = faultToleranceConfig.dev || {};
        } else if (app.config.env == 'unittest') {
          faultToleranceConfig = faultToleranceConfig.test || {};
        } else if (app.config.env == 'stag') {
          faultToleranceConfig = faultToleranceConfig.stag || {};
        } else if (app.config.env == 'prod') {
          faultToleranceConfig = faultToleranceConfig.prod || {};
        }
        faultToleranceConfig.timeout = apiOperation.timeout ? parseInt(apiOperation.timeout) : faultToleranceConfig.timeout;

        if (apiLink.protocol == 'REST') {
          await this.invokeHttpRestApi(apiOperation, { endpoint, authConfig, faultToleranceConfig, invokeLog });
        } else if (apiLink.protocol == 'SOAP') {
          await this.invokeHttpSoapApi(apiOperation, { endpoint, authConfig, faultToleranceConfig, invokeLog });
        }
      } catch (error) {
        app.logger.error('api 集成接口调用异常: %s', error.message);
      }

      invokeLog.resTime = new Date().getTime();
      invokeLog.latency = invokeLog.resTime - invokeLog.reqTime;

      ctx.curl(app.config.backendURL + '/api/apiLink/commitInvokeLog', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: invokeLog
      });
      return;
    }
    ctx.body = undefined;
  }

  async invokeHttpRestApi(apiOperation, { endpoint, authConfig, faultToleranceConfig, invokeLog }) {
    const { ctx, app } = this;
    let { pathParams, queryParams, headers, body } = ctx.req.body;
    let { path, method, reqFormatType, resFormatType } = apiOperation;
    if (authConfig.authType == 'BearerToken') {
      headers[authConfig.tokenHeaderName || 'Authorization'] = 'Bearer ' + (authConfig.useUserToken ? ctx.user.token : authConfig.token);
    } else if (authConfig.authType == 'OAuth2') {
      let accessToken = await this.getOauth2AssessToken(authConfig.oauth2);
      if (accessToken) {
        headers.Authorization = 'Bearer ' + accessToken;
      } else {
        throw new Error(403);
      }
    }
    if (Object.keys(pathParams).length > 0) {
      try {
        path = lodash.template(path, {
          interpolate: /\{([^{}]+)\}/g
        })(pathParams);
      } catch (error) {}
    }
    let url = (endpoint.endsWith('/') ? endpoint.substring(0, endpoint.length - 1) : endpoint) + path;
    // if (method == 'POST' && queryParams && Object.keys(queryParams).length > 0) {
    //   url = url + (path.indexOf('?') == -1 ? '?' : '&') + qs.stringify(queryParams);
    // }
    invokeLog.invokeUrl = url;
    invokeLog.reqMethod = method;
    invokeLog.reqQueryParams = queryParams != undefined && Object.keys(queryParams).length > 0 ? JSON.stringify(queryParams) : undefined;
    invokeLog.reqHeaders = headers != undefined && Object.keys(headers).length > 0 ? JSON.stringify(headers) : undefined;
    invokeLog.reqBody = body != undefined && Object.keys(body).length > 0 ? JSON.stringify(body) : undefined;
    let contentType = { 'x-www-form-urlencoded': 'form', 'form-data': 'form-data', json: 'json' }[reqFormatType.toLowerCase()];

    let isPostFormData = method == 'POST' && ['form-data', 'x-www-form-urlencoded'].includes(reqFormatType.toLowerCase()),
      parameterMap = {};
    if (apiOperation.parameters.length > 0 && (method == 'GET' || isPostFormData)) {
      for (let p of apiOperation.parameters) {
        if (p.paramType == 'path' || (isPostFormData && p.paramType == 'body') || (method == 'GET' && p.paramType == 'query')) {
          parameterMap[p.name] = p;
          //TODO: 是否做参数校验
        }
      }
    }

    try {
      if (method == 'POST' && body != undefined && Object.keys(body).length > 0 && reqFormatType.toLowerCase() == 'form-data') {
        let formData = new FormData();
        let remoteFilePromise = [];
        for (let key in body) {
          if (typeof body[key] == 'object' && parameterMap[key] && parameterMap[key].dataType == 'file') {
            // 附件类型
            if (Array.isArray(body[key])) {
              for (let item of body[key]) {
                if (item.fileID) {
                  remoteFilePromise.push(
                    this.addRemoteFileToFormData(
                      formData,
                      `${app.config.backendURL}/repository/file/mongo/download?fileID=${item.fileID}&jwt=${ctx.user.token}`,
                      key,
                      item.filename
                    )
                  );
                }
              }
            }
          } else {
            formData.append(key, body[key]);
          }
        }
        if (remoteFilePromise.length > 0) {
          await Promise.all(remoteFilePromise);
        }
        body = formData;
        contentType = formData.getHeaders()['content-type'];
        Object.assign(headers, formData.getHeaders());
      }
      const response = await axios({
        method: method.toLowerCase(),
        url,
        headers,
        params: queryParams,
        contentType,
        data: body,
        timeout: faultToleranceConfig.timeout || 10000,
        responseType: resFormatType.toLowerCase()
      });
      // console.log('响应结果', response)
      invokeLog.resStatus = response.status;
      invokeLog.resBody =
        response.data != undefined ? (resFormatType.toLowerCase() == 'json' ? JSON.stringify(response.data) : response.data) : undefined;
      invokeLog.resHeaders =
        response.headers != undefined && Object.keys(response.headers).length > 0 ? JSON.stringify(response.headers) : undefined;
      ctx.body = response.data;
    } catch (error) {
      invokeLog.errorMessage = error.message;
      invokeLog.resStatus = 500;
      throw new Error(error.message);
    }
  }

  async addRemoteFileToFormData(formData, url, fieldName, fileName) {
    return new Promise((resolve, reject) => {
      axios({
        method: 'get',
        url: url,
        responseType: 'stream'
      })
        .then(response => {
          // 将文件流添加到FormData
          formData.append(fieldName, response.data, fileName);
          resolve();
        })
        .catch(() => {
          reject();
        });
    });
  }

  async invokeHttpSoapApi(apiOperation, { endpoint, authConfig, faultToleranceConfig, invokeLog }) {
    const { ctx, app } = this;
    let { queryParams, headers, body } = ctx.req.body;
    let { path, method } = apiOperation;
    invokeLog.reqMethod = method;
    invokeLog.reqBody = body != undefined && Object.keys(body).length > 0 ? JSON.stringify(body) : undefined;

    // 创建客户端
    const client = await soap.createClientAsync(endpoint);
    // 调用服务方法
    const response = await client[path + 'Async'](body, {
      extraHeaders: headers,
      timeout: faultToleranceConfig.timeout || 10000
    });
    invokeLog.invokeUrl = endpoint;
    invokeLog.path = path;
    invokeLog.resBody = JSON.stringify(Array.isArray(response) ? response[0] : response);
    invokeLog.resStatus = 200;
    // invokeLog.resHeaders =
    //   response.headers != undefined && Object.keys(response.headers).length > 0 ? JSON.stringify(response.headers) : undefined;
    ctx.body = Array.isArray(response) ? response[0] : response;
  }

  async getOauth2AssessToken(oauth2) {
    let { ctx, app } = this;
    let { grantType, clientId, clientSecret, accessTokenUrl, callbackUrl, username, password, state, scope, algorithm, secretKey } = oauth2;
    let body = {
      grant_type: grantType,
      client_id: clientId,
      client_secret: clientSecret,
      scope: scope
    };
    if (grantType == 'password') {
      body.username = username;
      body.password = password;
    } else if (grantType == 'jwt-bearer') {
      body = {
        grant_type: `urn:ietf:params:oauth:grant-type:${grantType}`,
        assertion: await this.generateAssertion(oauth2)
      };
    }

    let accessToken = await app.redis.get(`apiLink:oauth2:${grantType}:${clientId}:accessToken`);
    if (accessToken) {
      return accessToken;
    }
    // 获取accessToken
    const response = await ctx.curl.post(accessTokenUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      dataAsQueryString: true,
      contentType: 'json',
      data: body
    });
    if (response.data) {
      app.redis.set(`apiLink:oauth2:${grantType}:${clientId}:access_token`, response.data.access_token, 'EX', response.data.expires_in);
      if (response.data.refresh_token) {
        app.redis.set(`apiLink:oauth2:${grantType}:${clientId}:refresh_token`, response.data.refresh_token);
      }
      return response.data.access_token;
    }
  }

  async generateAssertion(oauth2) {
    const now = Math.floor(Date.now() / 1000);
    let oauth2Payload = {};
    try {
      if (oauth2.payload) {
        oauth2Payload = JSON.parse(oauth2.payload);
      }
    } catch (error) {}
    const payload = {
      ...oauth2Payload,
      iss: oauth2.clientId,
      sub: oauth2.clientId,
      aud: oauth2.accessTokenUrl,
      exp: now + 300, // 5分钟有效
      iat: now,
      jti: `jwt-${now}-${Math.random().toString(36).substr(2, 9)}`
    };

    return jwt.sign(payload, oauth2.secretKey, {
      algorithm: oauth2.algorithm,
      header: {
        typ: 'JWT',
        alg: oauth2.algorithm
      }
    });
  }
}

module.exports = ApiLinkController;
