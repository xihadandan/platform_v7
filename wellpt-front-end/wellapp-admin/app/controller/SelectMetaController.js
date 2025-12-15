'use strict';

const Controller = require('wellapp-framework').Controller;
const lodash = require('lodash');

class SelectController extends Controller {
  async query() {
    const { ctx, app } = this;
    // 如果是javascript下拉数据查询，则查询node端的，否则转发后端服务查询
    if (ctx.request.body.serviceName === 'appJavaScriptModuleMgr') {
      let queryMethod = ctx.request.body.queryMethod || 'listJavascriptContainsDependency';
      ctx.body = {
        results: lodash.transform(
          await ctx.service.webResourceService[queryMethod](
            ctx.request.body.dependencyFilter,
            ctx.request.body.searchValue,
            ctx.request.body.pageSize,
            ctx.request.body.ids
          ),
          function (result, n) {
            result.push({ id: n.id, text: n.name });
          },
          []
        )
      };
    } else if (
      ctx.request.body.serviceName === 'appFunctionMgr' &&
      ['JavaScript', 'JavaScriptTemplate'].indexOf(ctx.request.body.functionType) !== -1
    ) {
      // javascript的功能资源请求
      let type = 'js';
      if (ctx.request.body.functionType === 'JavaScriptTemplate') {
        type = 'jsTemlate';
      }
      ctx.body = {
        results: lodash.transform(
          await ctx.service.webResourceService.listAllByIdLikeAndNameLikeAndType(ctx.request.body.searchValue, type),
          function (result, n) {
            result.push({ id: n.id, text: n.name });
          },
          []
        )
      };
    } else {
      // 转发到后端的下拉查询服务接口
      try {
        let selectBody = ctx.request.body;
        if (selectBody.params) {
          selectBody.paramsJSONString = JSON.stringify(selectBody.params);
          delete selectBody.params; //避免传递到后端服务参数转换异常
        }
        if (selectBody.selectIds != undefined) {
          selectBody['selectIds[]'] = selectBody.selectIds;
        }
        let curlPath = '/common/select2/';
        if (ctx.routerPath.indexOf('group') > -1) {
          curlPath = '/common/select2/group/';
        }
        const result = await ctx.curl(app.config.backendURL + curlPath + ctx.params.method, {
          method: ctx.request.method,
          dataType: 'json',
          data: selectBody
        });
        ctx.body = result.data;
      } catch (error) {
        this.app.logger.error('下拉选项数据异常：%s', error);
      }
    }
  }
}

module.exports = SelectController;
