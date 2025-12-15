'use strict';
module.exports = (name, options) => {
  return async function themeResolver(ctx, next) {
    if (ctx.PROD_VERSION_UUID) {
      // 基于产品版本下的页面，根据获取产品版本设置的系统默认主题
      let themeJson = await ctx.service.appProdIntegrationService.getProdVersionDefaultTheme(ctx.PROD_VERSION_UUID);
      let classScope = [];
      if (themeJson) {
        ctx.THEME = themeJson;
        classScope = themeJson.classScope;
      }
      let userProdTheme = await ctx.service.appProdIntegrationService.getUserPreferenceProdTheme();
      if (userProdTheme && userProdTheme.class != undefined) {
        // 用户设置了主题，且主题包有效
        if (userProdTheme.class == '' || (themeJson && themeJson.classScope && themeJson.classScope.includes(userProdTheme.class))) {
          ctx.THEME = { ...userProdTheme, byUser: true, classScope };
        }
      }
    } else if (ctx.SYSTEM_ID) {
      // 获取系统设置的主题风格
      if (ctx.SYSTEM_INFO && ctx.SYSTEM_INFO.themeStyle) {
        let themeStyle = JSON.parse(ctx.SYSTEM_INFO.themeStyle).pc;
        let { theme, defaultTheme } = themeStyle;
        let themeJson = {
          class: undefined,
          colorClass: undefined,
          classScope: []
        };
        for (let i = 0, len = theme.length; i < len; i++) {
          themeJson.classScope.push(theme[i].themeClass);
          if (defaultTheme == theme[i].themeClass) {
            themeJson.class = defaultTheme;
            themeJson.colorClass = theme[i].colorClass;
          }
        }
        ctx.THEME = themeJson;
        // 登录页不读取用户主题
        if (!(ctx.path.startsWith('/sys/') && ctx.path.endsWith('/login')) && ctx.isAuthenticated()) {
          let userTheme = await ctx.service.appProdIntegrationService.getUserPreferenceSystemTheme();
          if (userTheme && userTheme.class != undefined) {
            // 用户设置了主题，且主题包有效
            if (userTheme.class == '' || (themeJson && themeJson.classScope && themeJson.classScope.includes(userTheme.class))) {
              ctx.THEME = { ...userTheme, byUser: true, classScope: themeJson.classScope };
            }
          }
        }
      }
    }
    await next();
  };
};
