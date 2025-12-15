'use strict';

const Controller = require('egg').Controller;
const querystring = require('querystring');
class ProxyController extends Controller {
  async proxy() {
    const { ctx, app } = this;
    let url = new URL(app.config.backendURL);
    if (app.config.backendURL.indexOf('/dx-post-proxy') != -1) {
      // 后端接口基于数据交换
      await this.dxPostProxy();
      return;
    }
    let timeout = ctx.req.headers.timeout || 1000 * 600;
    await ctx.proxyRequest(url.host, {
      rewrite(urlObj) {
        urlObj.protocol = url.protocol;
        if (url.protocol === 'http:' && url.port === '') {
          urlObj.port = 80;
        } else if (url.protocol === 'https:' && url.port === '') {
          urlObj.port = 443;
        }
        urlObj.pathname = urlObj.pathname.replace(/^\/proxy/, '');
        return urlObj;
      },
      withCredentials: true,
      timeout
    });
  }

  async dxPostProxy() {
    const { ctx, app } = this;
    let dxProxyOptions = app.config.dxPostProxy;
    // 参数转换
    const data = {
      method: ctx.req.method,
      contentType: ctx.req.headers['content-type'] || ctx.req.headers['accept'],
      requestUrl: decodeURIComponent(ctx.req.url).replace('/dx-post-proxy', '')
    };
    if (data.requestUrl.indexOf('/proxy') == 0) {
      //从代理控制层来
      data.requestUrl = data.requestUrl.replace('/proxy', '');
    }

    if (data.method === 'POST' || data.method == 'PUT') {
      if (data.contentType == 'application/x-www-form-urlencoded') {
        let _querystring = querystring.stringify(ctx.req.body);
        data.requestUrl += '?' + _querystring;
      }
      data.content = JSON.stringify(ctx.req.body);
      //FIXME: 文件转base64
      if (data.contentType && data.contentType.indexOf('multipart/') != -1) {
        //带文件流的
        let files = ctx.request.files;
        data.base64files = {};
        for (let i = 0; i < files.length; i++) {
          // files[i].field, files[i].filepath,files[i].filename
        }
      }
    }

    if (typeof dxProxyOptions.request === 'function') {
      await dxProxyOptions.request(ctx, data);
      return;
    }
    ctx.body = 404;
  }
}

module.exports = ProxyController;
