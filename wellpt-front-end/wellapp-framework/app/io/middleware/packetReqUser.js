module.exports = app => {
  return async (ctx, next) => {
    if (ctx.session.passport && ctx.session.passport.user) {
      ctx.req.user = ctx.session.passport.user;
      await next();
    } else {
      ctx.socket.disconnect();
    }
  };
};
