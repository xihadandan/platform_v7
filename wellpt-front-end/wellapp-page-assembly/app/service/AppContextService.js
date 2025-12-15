'use strict';

const Service = require('wellapp-framework').Service;
const is = require('is-type-of');
class AppContextService extends Service {
  async getAppWidgetDefinitionById() {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/webapp/resolveWidgetView', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          id: arguments[0],
          isClone: arguments[1]
        }
      });
      if (result.data && result.data.definitionJson) {
        let _definitionJson = JSON.parse(result.data.definitionJson);
        if (app.component[_definitionJson.wtype] && is.function(app.component[_definitionJson.wtype].beforeExplainDefinitionJson)) {
          await app.component[_definitionJson.wtype].beforeExplainDefinitionJson(ctx, _definitionJson);
          result.data.definitionJson = JSON.stringify(_definitionJson);
        }
        const definitionRelaRes = await ctx.service.pageDefinitionService.explainJavascriptCssByDefinitonJson(_definitionJson);
        result.data.configScript = ctx.helper.createRequirejsConfig(definitionRelaRes.javascripts); // 解析组件依赖脚本
        result.data.requireJavaScriptModules = Array.from(definitionRelaRes.jsAdded);
        result.data.definitionJson = definitionRelaRes.definitionJson;
      }
      return result;
    } catch (error) {
      app.logger.error('JDS调用后端服务接口异常：%s', error);
    }
    return null;
  }

  async getJavaScriptTemplateById(id) {
    const { app, ctx } = this;
    if (app.jsTemplatePack[id]) {
      await ctx.render(app.jsTemplatePack[id].path, {});
      return { data: Object.assign({ content: ctx.body }, app.jsTemplatePack[id]) };
    }
    return null;
  }


}

module.exports = AppContextService;
