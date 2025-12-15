'use strict';

const Controller = require('wellapp-framework').Controller;

class FlowOpinionController extends Controller {

  async get() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/opinion/get', {
        method: 'get', 
		contentType: 'application/x-www-form-urlencoded', 
		dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s' , error);
    }
  }

  async getByOpinionCategory() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/opinion/getByOpinionCategory', {
        method: 'get', 
		contentType: 'application/x-www-form-urlencoded', 
		dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s' , error);
    }
  }

  async save() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/opinion/save', {
        method: 'post', 
		contentType: 'json', 
		dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('流程信息格式保存异常：%s' , error);
    }
  }

  async deleteAll() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/opinion/deleteAll', {
        method: 'post', 
		contentType: 'application/x-www-form-urlencoded',
		dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('流程信息格式删除异常：%s' , error);
    }
  }

  async getCategory() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/opinion/category/get', {
        method: 'get', 
		contentType: 'application/x-www-form-urlencoded', 
		dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s' , error);
    }
  }

  async saveCategory() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/opinion/category/save', {
        method: 'post', 
		contentType: 'json', 
		dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('流程信息格式保存异常：%s' , error);
    }
  }

  async deleteCategory() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/opinion/category/delete', {
        method: 'post', 
		contentType: 'application/x-www-form-urlencoded',
		dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('流程信息格式删除异常：%s' , error);
    }
  }

  async deleteCategoryAndOpinion() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/opinion/categoryAndOpinion/delete', {
        method: 'post', 
		contentType: 'application/x-www-form-urlencoded',
		dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('流程信息格式删除异常：%s' , error);
    }
  }

  async getFlowOpinionCategoryTreeByBusinessAppDataDic() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/opinion/getFlowOpinionCategoryTreeByBusinessAppDataDic', {
        method: 'get', 
		contentType: 'application/x-www-form-urlencoded', 
		dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s' , error);
    }
  }
}

module.exports = FlowOpinionController;
