'use strict';

const Controller = require('wellapp-framework').Controller;

class CalendarCategoryController extends Controller {
  async getAllBySystemUnitIdsLikeName() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/calendar/category/getAllBySystemUnitIdsLikeName', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('日程分类按系统单位及名称查询异常：%s', error);
    }
  }

  async get() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/calendar/category/get', {
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

  async save() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/calendar/category/save', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('日程信息格式保存异常：%s', error);
    }
  }

  async generateCalendarCategoryCode() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/calendar/category/generateCalendarCategoryCode', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('日程信息格式删除异常：%s', error);
    }
  }

  async deleteAll() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/calendar/category/deleteAll', {
        method: 'post',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('日程信息格式删除异常：%s', error);
    }
  }

  async deleteWhenNotUsed() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/calendar/category/deleteWhenNotUsed', {
        method: 'post',
        contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('日程信息格式删除异常：%s', error);
    }
  }
}

module.exports = CalendarCategoryController;
