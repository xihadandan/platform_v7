'use strict';

const Controller = require('wellapp-framework').Controller;

class PictureLibCategoryController extends Controller {
  async getCategoryByUuid() {
    const { ctx, app } = this;
    try {
      const url = `${app.config.backendURL}/api/basicdata/img/category/${ctx.params.uuid}`;

      const result = await ctx.curl(url, {
        method: 'GET',
        contentType: 'json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('图片库分类获取异常：%s', error);
    }
  }

  async queryAllCategory() {
    const { ctx, app } = this;
    try {
      const url = `${app.config.backendURL}/api/basicdata/img/category/queryAllCategory`;

      const result = await ctx.curl(url, {
        method: 'GET',
        contentType: 'json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('图片库所有分类查询异常：%s', error);
    }
  }

  async saveCategory() {
    const { ctx, app } = this;
    try {
      const url = `${app.config.backendURL}/api/basicdata/img/category/save`;

      const result = await ctx.curl(url, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('图片库分类保存异常：%s', error);
    }
  }

  async addImagesToCategory() {
    const { ctx, app } = this;
    try {
      const url = `${app.config.backendURL}/api/basicdata/img/category/addImgs/${ctx.params.uuid}`;

      const result = await ctx.curl(url, {
        method: 'POST',
        // contentType: 'application/json',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('图片库保存到分类异常：%s', error);
    }
  }

  async getImagesByCategory() {
    const { ctx, app } = this;
    try {
      const url = `${app.config.backendURL}/api/basicdata/img/category/queryImgs/${ctx.params.uuid}`;

      const result = await ctx.curl(url, {
        method: 'GET',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('图片库分类图片查询异常：%s', error);
    }
  }

  async deleteCategoryByUuid() {
    const { ctx, app } = this;
    try {
      const url = `${app.config.backendURL}/api/basicdata/img/category/${ctx.params.uuid}`;

      const result = await ctx.curl(url, {
        method: 'DELETE',
        contentType: 'json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('图片库分类删除异常：%s', error);
    }
  }

  async deleteImageFromCategoryByUuid() {
    const { ctx, app } = this;
    try {
      const url = `${app.config.backendURL}/api/basicdata/img/category/delImgs/${ctx.params.uuid}`;

      const result = await ctx.curl(url, {
        method: 'POST',
        contentType: 'form',
        dataType: 'json',
        data: ctx.req.body
      });
      ctx.body = result.data;
    } catch (error) {
      this.app.logger.error('图片库删除图片异常：%s', error);
    }
  }
}

module.exports = PictureLibCategoryController;
