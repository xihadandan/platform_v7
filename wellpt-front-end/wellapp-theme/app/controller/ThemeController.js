'use strict';

const Controller = require('wellapp-framework').Controller;
const lodash = require('lodash');
const fs = require('fs-extra');
const path = require('path');

class ThemeController extends Controller {
  themesCache = [];

  async getAllThemes() {
    const { ctx, app } = this;

    try {
      if (this.themesCache && this.themesCache.length) {
        ctx.body = this.themesCache;
        return;
      }

      const themesSettingJsonPath = path.join(__dirname, '..', '..', 'src', 'styles', 'themes.json');
      const themes = fs.readJsonSync(themesSettingJsonPath);
      for (const theme of themes) {
        const id = theme.id;
        const name = theme.name;

        const custom = { colors: [], fontSize: [] };

        const customized = !!theme.custom;
        if (customized) {
          const colors = theme.custom['@primary-color'];
          for (const colorName in colors) {
            custom.colors.push({ name: colorName, value: colors[colorName] });
          }

          const fontSizes = theme.custom['@font-size-base'];
          for (const fontSize in fontSizes) {
            custom.fontSize.push({ name: fontSize, value: fontSizes[fontSize] });
          }
        }

        this.themesCache.push(customized ? { name, custom, theme: id } : { name, theme: id });
      }

      ctx.body = this.themesCache;
      return;
    } catch (error) {
      app.logger.error('主题信息获取异常：%s', error);
    }
  }

  async getUserCustomTheme() {
    const { ctx } = this;
    const theme = await ctx.service.userThemeService.getUserTheme();
    theme.themeName = ctx.service.userThemeService.getThemeName(theme);
    theme.themeFiles = await ctx.service.userThemeService.getThemeFiles(theme);
    ctx.body = theme;
  }

  async saveTheme() {
    const { ctx, app } = this;
    try {
      const theme = ctx.req.body;
      const result = await ctx.service.userThemeService.saveUserTheme(theme);
      ctx.body = true;
      ctx.service.pageDefinitionService.clearUserPageCacheData();
    } catch (error) {
      this.app.logger.error('主题配置保存异常：%s', error);
    }
  }
}

module.exports = ThemeController;
