module.exports = options => {
  return async function requestFilter(ctx, next) {
    if (typeof ctx.app.config.wellappRequestFilter.ifNext === 'function') {
      if (!ctx.app.config.wellappRequestFilter.ifNext(ctx)) {
        return;
      }
    }
    await next();
  };
};
