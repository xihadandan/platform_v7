'use strict';

const Controller = require('wellapp-framework').Controller;

class MessageController extends Controller {
  async queryList() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/message/classify/queryList', {
        method: 'GET',
        contentType: 'application/json',
        dataType: 'text',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }

  async queryRecentTenLists() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/message/inbox/queryRecentTenLists', {
        method: 'GET',
        contentType: 'application/json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }

  async facadeQueryList() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/message/classify/facadeQueryList', {
        method: 'GET',
        contentType: 'application/json',
        dataType: 'text',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }

  async updateToReadState() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/message/inbox/updateToReadState', {
        method: 'put',
        contentType: 'application/json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }

  async updateToUnReadStateByclass() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/message/inbox/updateToUnReadStateByclass', {
        method: 'put',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }

  async updateToReadStateByclass() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/message/inbox/updateToReadStateByclass', {
        method: 'put',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }
  async unread() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/message/content/unread', {
        method: 'post',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'text',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }

  async read() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/message/content/read', {
        method: 'post',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'text',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }

  async deleteInboxMessage() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/message/content/deleteInboxMessage', {
        method: 'post',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'text',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }

  async queueCount() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/message/queue/count', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }

  async contentSubmitmessage() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/message/content/submitmessage', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }

  async deleteOutboxMessage() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/message/content/deleteOutboxMessage', {
        method: 'post',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'text',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }
}

module.exports = MessageController;
