'use strict';

const Controller = require('wellapp-framework').Controller;

class MultiOrgTreeDialogController extends Controller {

  async queryUnitTreeDialogDataByType() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/tree/dialog/queryUnitTreeDialogDataByType`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async children() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/tree/dialog/children`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async search() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/tree/dialog/search`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async full() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/tree/dialog/full`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async allUserSearch() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/tree/dialog/allUserSearch`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async smartName() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/tree/dialog/smartName`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async fullNamePath() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/tree/dialog/fullNamePath`, {
        method: 'POST',
        contentType: 'form',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }
}

module.exports = MultiOrgTreeDialogController;
