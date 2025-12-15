'use strict';
const lodash = require('lodash');
module.exports = options => {
  return async function staticRequestPathDecoder(ctx, next) {
    ctx.cookies.set('resourceVersionHash', ctx.app._RESOURCE_VERSION_HASH, { httpOnly: false });
    if (ctx.app.config.confuseStaticPath && lodash.startsWith(ctx.path, ctx.app.config.static.prefix) && lodash.endsWith(ctx.path, '.js')) {
      let _p = ctx.path.substring(ctx.app.config.static.prefix.length + 1);
      if (ctx.app.md5StaticPathEncoder[_p]) {
        // 是否有加密路径
        ctx.path = ctx.app.config.static.prefix + ctx.app.md5StaticPathEncoder[_p];
      }
    }
    await next();
  };
};
