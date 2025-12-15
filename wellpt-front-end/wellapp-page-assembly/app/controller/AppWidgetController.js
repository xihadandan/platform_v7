'use strict';

const Controller = require('wellapp-framework').Controller;



class AppWidgetController extends Controller {

  async getWidgetById() {
    let { app, ctx } = this;
    let { id, appPageId, appPageUuid } = ctx.query, widgetJson = null, widget = null;
    if (app.env != 'local') {
      let key = `page:${appPageUuid || appPageId}:widget:${id}`;
      widgetJson = await app.redis.get(key);
    }
    if (widgetJson && app.env != 'local') {
      widget = JSON.parse(widgetJson);
    } else {
      const result = await ctx.curl(app.config.backendURL + '/api/app/widget/getWidgetById', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          id,
          appPageId,
          appPageUuid
        }
      });
      if (result.data && result.data.code === 0) {
        widget = result.data.data;
        if (app.env != 'local') {
          let json = JSON.stringify(widget);
          if (widget.appPageUuid) {
            app.redis.set(`page:${widget.appPageUuid}:widget:${id}`, json);
          }
          if (widget.appPageId) {
            app.redis.set(`page:${widget.appPageId}:widget:${id}`, json);
          }
        }
      }
    }
    ctx.body = widget;
  }


  /**
   * 获取应用的主布局和导航组件
   */
  async getUserAppIndexLayoutMenuWidget() {
    let { app, ctx } = this;
    let appId = ctx.query.appId, userId = ctx.user.userId;
    const result = await ctx.curl(app.config.backendURL + '/api/app/widget/getAuthorizedWidgetsByAppId', {
      method: 'GET',
      contentType: 'json',
      dataType: 'json',
      data: {
        appId,
        wtype: ['WidgetLayout', 'WidgetMenu'].join(','),
        main: true
      }
    });
    if (result.data && result.data.code === 0) {
      ctx.body = result
    } else {
      ctx.body = {}
    }

  }



}
module.exports = AppWidgetController;
