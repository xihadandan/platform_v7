'use strict';

const Controller = require('wellapp-framework').Controller;

class CalendarCompoentController extends Controller {
  async save() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/basicdata/calendarcomponent/save', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (e) {
      app.logger.error('保存日历组件数据异常：%s', error);
    }
  }

  async delete() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/basicdata/calendarcomponent/delete', {
        method: 'post',
        contentType: 'form',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (e) {
      app.logger.error('删除日历组件日程数据异常：%s', error);
    }
  }

  async loadEvents() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/basicdata/calendarcomponent/loadEvents', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('日历组件加载事件数据异常：%s', error);
    }
  }

  async getProviderInfo() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/basicdata/calendarcomponent/getProviderInfo', {
        method: 'POST',
        // contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('日历组件加载事件配置异常：%s', error);
    }
  }
}

module.exports = CalendarCompoentController;
