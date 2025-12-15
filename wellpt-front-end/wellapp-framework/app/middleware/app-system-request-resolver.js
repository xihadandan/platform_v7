'use strict';
const qs = require('qs');
const typeis = require('type-is');
module.exports = (name, options) => {
  // 请求解析系统ID
  return async function appSystemRequestResolver(ctx, next) {
    // 解析header
    if (!ctx.APP_SYSTEM_ID) {
      if (ctx.headers.app_system_id) {
        ctx.APP_SYSTEM_ID = ctx.headers.app_system_id;
      } else {
        // 从地址解析
        let url = ctx.req.url,
          urlParams = {};
        urlParams = qs.parse(url.substr(url.indexOf('?') + 1));
        ctx.APP_SYSTEM_ID = urlParams.app_system_id || undefined;

        //尝试从Referer头解析
        if (!ctx.APP_SYSTEM_ID && ctx.headers.referer) {
          url = ctx.headers.referer;
          urlParams = qs.parse(url.substr(url.indexOf('?') + 1));
          ctx.APP_SYSTEM_ID = urlParams.app_system_id || undefined;
        }
      }
    }
    await next();
  };
};
