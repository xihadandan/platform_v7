module.exports = options => {
  return async function idnumberTokenUrl(ctx, next) {
    if (!ctx.isAnnoymouseRequest && !ctx.isAuthenticated() && ctx.req.url.indexOf('/login/idnumberToken') != 0) {
      const { idNumber, accessToken, _provider, appId } = ctx.req.query;
      if (idNumber && accessToken && _provider === 'idnumber-token') {
        ctx.session.returnTo = ctx.req.url;
        // 重定向
        ctx.redirect(`/login/idnumberToken?idNumber=${idNumber}&appId=${appId}&accessToken=${accessToken}`);
        return;
      }
    }
    await next();
  };
};
