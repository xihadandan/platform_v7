'use strict';

const Controller = require('wellapp-framework').Controller;

class FlowOpinionController extends Controller {
  async saveOpinionRule() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/opinion/rule/saveOpinionRule', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('流程信息格式保存异常：%s', error);
    }
  }

  async delete() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/opinion/rule/delete', {
        method: 'post',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('流程签署意见规则删除异常：%s', error);
    }
  }

  async getOpinionRuleDetail() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/opinion/rule/getOpinionRuleDetail', {
        method: 'get',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async isReferencedByOpinionRuleUuids() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/opinion/rule/isReferencedByOpinionRuleUuids', {
        method: 'post',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }
}

module.exports = FlowOpinionController;
