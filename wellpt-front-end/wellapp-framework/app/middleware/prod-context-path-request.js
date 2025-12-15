'use strict';
module.exports = (name, options) => {
  return async function prodContextPathRequest(ctx, next) {
    if (ctx.req.url.startsWith('/webapp/') && ctx.req.method == 'GET') {
      let parts = ctx.req.url.split('/');
      if (parts.length >= 3) {
        let prodId = parts[2],
          prodVersion = parts[3];
        ctx.PROD_CONTEXT_PATH = `/${prodId}/${prodVersion}`;
        ctx.PROD_VERSION_UUID = prodVersion;
        ctx.PROD_ID = prodId;
        let setting = await ctx.service.appProdIntegrationService.getProdVersionSetting(prodVersion);
        if (setting) {
          ctx.TITLE = setting.title;
          ctx.FAVICON = setting.icon;
        }
      }
      if (parts.length >= 4) {
        if (parts[4] == '_') {
          // 访问分隔符后的实际业务地址
          ctx.path = '/' + ctx.path.substr(ctx.path.indexOf('/_/') + 3);
        }
      }
    } else if ((ctx.req.url.startsWith('/sys/') || ctx.req.url.startsWith('/system_admin/')) && ctx.req.method == 'GET') {
      // 获取系统信息
      let parts = ctx.req.url.split('/');
      ctx.SYSTEM_ID = parts[2];
      let systemInfo = await ctx.service.appProdIntegrationService.getTenantSystemInfo(
        ctx.user ? ctx.user.tenantId : 'T001',
        ctx.SYSTEM_ID
      );
      if (systemInfo) {
        ctx.TITLE = systemInfo.title;
        ctx.FAVICON = systemInfo.favicon;
        ctx.SYSTEM_INFO = systemInfo;
        ctx.SYSTEM_ENABLE_LOCALE = systemInfo.enableLocale;
        ctx.SYSTEM_DEFAULT_LOCALE = systemInfo.defaultLocale;
      }

      if (parts.length >= 5 && parts[3] == '_') {
        // 系统上下文
        ctx.path = '/' + ctx.path.substr(ctx.path.indexOf('/_/') + 3);
      }
    }
    await next();
  };
};
