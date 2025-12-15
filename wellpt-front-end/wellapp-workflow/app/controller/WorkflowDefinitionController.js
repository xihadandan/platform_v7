'use strict';

const Controller = require('wellapp-framework').Controller;

class WorkflowDefinitionController extends Controller {
  async schemeDirectionXml() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/workflow/scheme/diction/xml.action', {
        method: 'GET',
        contentType: 'json',
        dataType: 'text',
        data: ctx.query
      });
      ctx.set('content-type', 'application/xml; charset=utf-8');
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }

  async schemeFlowXml() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/workflow/scheme/flow/xml.action', {
        method: 'GET',
        contentType: 'json',
        dataType: 'text',
        data: ctx.query
      });
      ctx.set('content-type', 'application/xml; charset=utf-8');
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }

  async checkFlowXmlForUpdate() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/scheme/checkFlowXmlForUpdate', {
        method: 'POST',
        contentType: 'json',
        dataType: 'text',
        data: ctx.req.body.xmlString
      });
      ctx.set('content-type', 'application/xml; charset=utf-8');
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }

  async schemeSaveXml() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/workflow/scheme/save.action?pbNew=' + ctx.query.pbNew, {
        method: 'POST',
        contentType: 'json',
        dataType: 'text',
        data: ctx.req.body.xmlString,
        headers: ctx.headers
      });
      ctx.set('content-type', 'application/xml; charset=utf-8');
      ctx.body = result.data;
    } catch (error) {
      app.logger.error(error);
    }
  }

  async getMessageTemplates() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getMessageTemplates', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getSelectFlowMessageTemplateType() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getSelectFlowMessageTemplateType', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getPrintTemplates() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getPrintTemplates', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getVformsByPformUuid() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getVformsByPformUuid', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getFormFields() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getFormFields', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getFormBlocks() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getFormBlocks', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getFormBlocksByFlowDefId() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getFormBlocksByFlowDefId', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getFormSubtabs() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getFormSubtabs', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getSubForms() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getSubForms', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async listFlowTimerByFlowId() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/listFlowTimerByFlowId', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getBusinessTypes() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getBusinessTypes', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getBusinessRoles() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getBusinessRoles', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getSubtaskDispatcherCustomInterfaces() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getSubtaskDispatcherCustomInterfaces', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getCustomDispatcherBranchTaskInterfaces() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getCustomDispatcherBranchTaskInterfaces', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async countById() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/countById', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async isExistsUnfinishedFlowInstanceByFlowDefUuid() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/isExistsUnfinishedFlowInstanceByFlowDefUuid', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getFlowHandingStateInfo() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getFlowHandingStateInfo', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getFlowTasks() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getFlowTasks', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async isAutoSubmitForkTask() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/isAutoSubmitForkTask', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getFormFieldsByFlowDefId() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getFormFieldsByFlowDefId', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getUserCustomExpression() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getUserCustomExpression', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async checkUserCustomExpression() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/checkUserCustomExpression', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async getCurrentUserUnitOrgVersions() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/definition/getCurrentUserUnitOrgVersions', {
        method: 'get',
        contentType: 'json',
        dataAsQueryString: true,
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }
  async getSortFields() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/workflow/work/sortFields', {
        method: 'get',
        contentType: 'application/json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('%s', error);
    }
  }
}

module.exports = WorkflowDefinitionController;
