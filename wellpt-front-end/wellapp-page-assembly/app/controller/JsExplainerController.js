'use strict';

const Controller = require('wellapp-framework').Controller;

/**
 * 解析js相关业务配置
 */
class JsExplainerController extends Controller {
  /**
   *  获取指定javascript id的requirejs配置
   */
  async getJavascriptRequirejsConfig() {
    this.ctx.body = await this.ctx.service.webResourceService.getJavascriptRequirejsConfig(
      this.ctx.request.body.ids,
      this.ctx.query.isMobileApp === 'true'
    );
  }

  async requirejsConfigScript() {
    const cf = await this.ctx.service.webResourceService.getJavascriptRequirejsConfig(this.ctx.query.ids.split(','));
    cf.baseUrl = global.STATIC_PREFIX;
    cf.waitSeconds = 0;
    (cf.urlArgs = 'v=' + this.ctx.helper.resVer()),
      (cf.map = {
        '*': {
          css: global.STATIC_PREFIX + '/js/requirejs/css.min.js'
        }
      });
    this.ctx.set('content-type', 'application/javascript; charset=utf-8');
    this.ctx.body = 'requirejs.config(' + JSON.stringify(cf) + ');';
  }

  async getJavaScriptPath() {
    const { ctx, app } = this;

    const jsArr = ctx.req.query.js.split(';');
    const pathArr = [];
    jsArr.forEach(js => pathArr.push(`/static${app.jsPack[js].path}.js`));

    this.ctx.body = pathArr;
  }

  /**
   *  注册javascript功能到后端服务
   */
  async registerJavascriptFunction() {
    const { ctx, app } = this;
    try {
      ctx.body = await ctx.curl(app.config.backendURL + '/webapp/registerJavascriptFunction', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: app.jsPack[ctx.params.jsid]
      });
    } catch (error) {
      app.logger.error('注册js功能到后端服务异常：%s', error);
    }
  }
}

module.exports = JsExplainerController;
