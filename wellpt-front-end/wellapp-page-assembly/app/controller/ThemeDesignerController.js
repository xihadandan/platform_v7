'use strict';

const Controller = require('wellapp-framework').Controller;
const less = require('less');

/**
 * 主题设计
 */
class ThemeDesignerController extends Controller {
  async index() {
    const { ctx, app } = this;
    await ctx.render('theme-designer/index.js', {
    });
  }
  async design() {
    const { ctx, app } = this;
    await ctx.render('theme-designer/theme-designer.js', {
      uuid: ctx.query.uuid
    });
  }
  async themeSpecification() {
    const { ctx, app } = this;
    await ctx.render('theme-designer/theme-specification.js', {
    });
  }

  async exportThemePack() {
    const { ctx, app } = this;
    await ctx.service.themeService.exportTheme(ctx.params.uuid);
  }

  async preview() {
    const { ctx, app } = this;
    const baseClass = ['preview'];
    let packResult = await ctx.service.themeService.exportTheme(ctx.params.uuid, 'text', baseClass);
    let output = await less.render(packResult.body);
    await ctx.render('theme-designer/theme-preview.js', {
      INJECT_CSS: output.css,
      themeColors: packResult.themeColors,
      themeFontSize: packResult.themeFontSize,
      defaultThemeColor: packResult.defaultThemeColor,
      baseClass: baseClass.join(' ')
    });
  }

  async compileThemePackCss() {
    const { ctx, app } = this;
    let packResult = await ctx.service.themeService.exportTheme(ctx.params.uuid, 'text');
    try {
      let output = await less.render(packResult.originalBody);
      ctx.body = {
        css: output.css
      }
    } catch (error) {
      ctx.body = {
        originalBody: packResult.originalBody,
        css: undefined,
        error: '编译异常: ' + error.message
      }
    }

  }

  async themePackPublishNotify() {
    this.ctx.service.themeService.publishThemePackAsChunkContent(this.ctx.query.uuid);
    this.ctx.body = 'ok';
  }

  async queryPublishedThemePack() {
    this.ctx.body = await this.ctx.service.themeService.queryPublishedThemeOptions();
  }

}
module.exports = ThemeDesignerController;
