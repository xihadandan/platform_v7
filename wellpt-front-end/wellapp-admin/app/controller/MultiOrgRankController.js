'use strict';

const Controller = require('wellapp-framework').Controller;

class MultiOrgRankController extends Controller {
  async hierarchySwitch() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/duty/hierarchy/switch`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async dutySeqUpdate() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/duty/hierarchy/dutySeqUpdate`, {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async dutySeqSave() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/duty/hierarchy/dutySeqSave`, {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async dutySeqTree() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/duty/hierarchy/dutySeqTree`, {
        method: 'get',
        dataType: 'json',
        data: ctx.query,
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async jobGradeList() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/duty/hierarchy/jobGradeList`, {
        method: 'get',
        dataType: 'json',
        data: ctx.query,
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async jobGradeSave() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/duty/hierarchy/jobGradeSave`, {
        method: 'post',
        dataType: 'json',
        contentType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async dutySeqDelete() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/duty/hierarchy/dutySeqDelete/${ctx.params.uuid}`, {
        method: 'delete',
        dataType: 'json',
        data: ctx.req
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async dutySeqInfo() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/duty/hierarchy/dutySeqInfo/${ctx.params.uuid}`, {
        method: 'get',
        dataType: 'json',
        data: ctx.query,
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async jobRankTree() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/duty/hierarchy/jobRankTree`, {
        method: 'get',
        dataType: 'json',
        data: ctx.query,
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async dutyHierarchyExport() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/duty/hierarchy/dutyHierarchyExport`, {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query,
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async dutyHierarchyView() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/duty/hierarchy/dutyHierarchyView`, {
        method: 'get',
        dataType: 'json',
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async listJobRankByDutySeqUuid() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/listJobRankByDutySeqUuid/${ctx.params.uuid}`, {
        method: 'get',
        dataType: 'json',
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async batchDeleteDuty() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/deleteDuty`, {
        method: 'post', 
		contentType: 'application/x-www-form-urlencoded',
		dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }
  async deleteDuty() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/deleteDuty/${ctx.params.uuid}`, {
        method: 'delete',
        dataType: 'json',
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async getJobRankByJobId() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/getJobRankByJobId/${ctx.params.id}/${ctx.params.jid}`, {
        method: 'get',
        dataType: 'json',
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async deleteJobRank() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/deleteJobRank/${ctx.params.uuid}`, {
        method: 'delete',
        dataType: 'json',
        data: ctx.req
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }
}

module.exports = MultiOrgRankController;
