'use strict';

const Controller = require('wellapp-framework').Controller;

class DyformFieldController extends Controller {
  async getField() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/field/get/' + ctx.params.uuid, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (e) {
      app.logger.error('查询表单字段异常：%s', e);
    }
  }

  async saveField() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/field/save', {
        method: 'POST',
        contentType: 'json',
        dataType: 'text',
        data: ctx.req.body
      });
      ctx.body = 'success';
    } catch (e) {
      app.logger.error('保存表单字段异常：%s', e);
    }
  }

  async deleteAll() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/field/deleteAll', {
        method: 'POST',
        contentType: 'form',
        dataType: 'text',
        data: ctx.req.body
      });
      ctx.body = 'success';
    } catch (e) {
      app.logger.error('查询表单字段异常：%s', e);
    }
  }

  async deleteDictByUuid() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/dict/' + ctx.params.uuid, {
        method: 'DELETE',
        contentType: 'json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (e) {
      app.logger.error('删除字典异常：%s', e);
    }
  }
}

module.exports = DyformFieldController;
