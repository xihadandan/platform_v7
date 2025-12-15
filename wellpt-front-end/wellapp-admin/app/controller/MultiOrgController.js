'use strict';

const Controller = require('wellapp-framework').Controller;

class MultiOrgController extends Controller {

  async modifyOrgChildNode() {
    const {ctx, app} = this;
    try {
      const url = `${app.config.backendURL}/api/org/multi/modifyOrgChildNode?isUnbind=${this.ctx.query.isUnbind}`;
      const result = await ctx.curl(url, {
        method: 'PUT',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      console.error(error);
    }
  }

  async addOrgChildNode() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/addOrgChildNode`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('添加组织节点异常：%s', error);
    }
  }

  async deleteOrgChildNode() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/deleteOrgChildNode`, {
        method: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async getOrgNodeByTreeUuid() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/getOrgNodeByTreeUuid`, {
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

  async getOrgNodePrivilegeResultTree() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/getOrgNodePrivilegeResultTree`, {
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

  async getOrgAsTreeByVersionId() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/getOrgAsTreeByVersionId`, {
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

  async addSystemUnit() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/addSystemUnit`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('添加组织节点异常：%s', error);
    }
  }

  async modifySystemUnit() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/modifySystemUnit`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('添加组织节点异常：%s', error);
    }
  }

  async getSystemUnitVo() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/getSystemUnitVo`, {
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

  async modifyUnitAdmin() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/modifyUnitAdmin`, {
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

  async addUnitAdmin() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/addUnitAdmin`, {
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

  async getUser() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/getUser`, {
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

  async getUserPrivilegeResultTree() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/getUserPrivilegeResultTree`, {
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

  async modifyDuty() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/modifyDuty`, {
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

  async addDuty() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/addDuty`, {
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

  async getDuty() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/getDuty`, {
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

  async modifyJobRank() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/modifyJobRank`, {
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

  async addJobRank() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/addJobRank`, {
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

  async getJobRank() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/getJobRank`, {
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

  async modifyOrgOption() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/modifyOrgOption`, {
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

  async addOrgOption() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/addOrgOption`, {
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

  async getOrgOption() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/getOrgOption`, {
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

  async getOptionStyle() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/option/getOptionStyle`, {
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

  async getOrgOptionListByUnitIdAndOnlyShow() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/getOrgOptionListByUnitIdAndOnlyShow`, {
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

  async queryOrgOptionListBySystemUnitIdAndOptionOfPT() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/queryOrgOptionListBySystemUnitIdAndOptionOfPT`, {
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

  async queryAllSystemUnitList() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/queryAllSystemUnitList`, {
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

  async modifyOrgType() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/modifyOrgType`, {
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

  async addOrgType() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/addOrgType`, {
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

  async getOrgType() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/getOrgType`, {
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

  async modifyUser() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/modifyUser`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      //修改自己的用户信息才能修改用户会话
      if(this.ctx.user.userId == ctx.req.body.id){
        // 修改用户会话里面的中文名，前端需要根据cookie获取展示使用
        this.ctx.user.userName = ctx.req.body.userName;
      }
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async addUser() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/addUser`, {
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

  async clearAllUserOfUnit() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/clearAllUserOfUnit`, {
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

  async getUserAllPrivilegeResultTree() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/getUserAllPrivilegeResultTree`, {
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

  async deleteUsers() {
    const {ctx, app} = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/api/org/multi/deleteUsers`, {
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
}

module.exports = MultiOrgController;
