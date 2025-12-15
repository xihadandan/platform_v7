'use strict';

const Controller = require('wellapp-framework').Controller;

class TimeServiceController extends Controller {
  async getAllBySystemUnitIdsLikeName() {
    // 获取计时服务分类列表
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/timer/category/getAllBySystemUnitIdsLikeName', {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body,
        headers: {
          system_id: ctx.headers.system_id
        }
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async getCategory() {
    // 根据uuid获取计时服务分类
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/timer/category/get', {
        method: 'get',
        // contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async isUsedCategory() {
    // 判断计时服务分类是否被使用
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/timer/category/isUsed', {
        method: 'post',
        // contentType: 'application/x-www-form-urlencoded',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async saveCategory() {
    // 保存计时服务分类
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/timer/category/save', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('计时服务分类保存异常：%s', error);
    }
  }

  async deleteAllCategory() {
    // 删除计时服务分类
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/timer/category/deleteAll', {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('计时服务分类删除异常：%s', error);
    }
  }

  async getConfig() {
    // 获取计时器
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/timer/config/get', {
        method: 'get',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async isUsedConfig() {
    // 判断计时器配置是否被使用
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/timer/config/isUsed', {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async selectDataConfig() {
    // 获取计时器配置下拉选择数据
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/timer/config/selectdata', {
        method: 'get',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async saveConfig() {
    // 保存计时器配置
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/timer/config/save', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async deleteAllConfig() {
    // 删除计时器配置
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/timer/config/deleteAll', {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async getHoliday() {
    // 获取节假日管理
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/holiday/get', {
        method: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async getHolidayInstanceDate() {
    // 获取指定年份的节假日实例日期
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/holiday/getHolidayInstanceDate', {
        method: 'get',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async saveHoliday() {
    // 保存节假日管理
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/holiday/save', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async isUsedHoliday() {
    // 判断节假日是否被使用
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/holiday/isUsed', {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async deleteAllHoliday() {
    // 删除节假日管理
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/holiday/deleteAll', {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async getAllHolidayBySystemUnitIdsLikeName() {
    // 获取节假日管理列表
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/holiday/getAllBySystemUnitIdsLikeName', {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async getAllBySystemUnitIdsLikeFields() {
    // 获取节假日管理列表
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/holiday/getAllBySystemUnitIdsLikeFields', {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body,
        headers: {
          system_id: ctx.headers.system_id
        }
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async getHolidayScheduleListByYear() {
    // 获取节假日安排
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/holiday/schedule/listByYear', {
        method: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async getHolidayScheduleListAllYear() {
    // 获取节假日安排所有年份列表
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/holiday/schedule/listAllYear', {
        method: 'get',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async saveAllHolidaySchedule() {
    // 保存节假日安排
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/holiday/schedule/saveAll?year=' + ctx.query.year, {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async deleteAllAllHolidaySchedule() {
    // 删除节假日安排
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/holiday/schedule/deleteAll', {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async getWorkTimePlan() {
    // 获取工作时间方案
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/work/time/plan/get', {
        method: 'get',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async getWorkDate() {
    // 获取工作时间
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/work/time/plan/getWorkDate', {
        method: 'get',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async saveWorkTimePlan() {
    // 保存工作时间方案
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/work/time/plan/save', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async getSysDate() {
    // 获取当前服务器时间
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/work/time/plan/getSysDate', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async saveAsNewVersion() {
    // 保存工作时间方案为新版本
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/work/time/plan/saveAsNewVersion', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async getAllWorkPlan() {
    // 工作时间方案查询按系统单位及名称查询
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/work/time/plan/getAllBySystemUnitIdsLikeName', {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async listNewVersionTipByUuids() {
    // 根据工作时间方案UUID列表获取存在新版本的信息提示
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/work/time/plan/listNewVersionTipByUuids', {
        method: 'get',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }
  async setAsDefault() {
    // 工作时间方案设置为默认
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/work/time/plan/setAsDefault', {
        method: 'get',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query,
        headers: ctx.headers
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async isUsedWorkTimePlan() {
    // 判断工作时间方案是否被使用
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/work/time/plan/isUsed', {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async deleteAllWorkTimePlan() {
    // 删除工作时间方案
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/work/time/plan/deleteAll', {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async getMaxVersionByUuid() {
    // 获取工作时间方案的最高版本
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/work/time/plan/getMaxVersionByUuid', {
        method: 'get',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }

  async getPlanHistory() {
    // 获取工作时间方案的最高版本
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/ts/work/time/plan/history/get', {
        method: 'get',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('%s', error);
    }
  }
}

module.exports = TimeServiceController;
