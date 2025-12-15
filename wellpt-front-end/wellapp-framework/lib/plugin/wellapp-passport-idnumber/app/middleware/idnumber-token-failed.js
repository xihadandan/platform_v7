module.exports = options => {
  return async function idnumberTokenFailed(ctx, next) {
    if (
      !ctx.isAuthenticated() &&
      (ctx.req.url === ctx.app.config.authenticateOptions['idnumber-token'].failureRedirect ||
        (ctx.app.config.authenticateOptions['idnumber-token'].mobile &&
          ctx.req.url === ctx.app.config.authenticateOptions['idnumber-token'].mobile.failureRedirect))
    ) {
      let _idnumberOptions = ctx.app.config.authenticateOptions['idnumber-token'];
      if (_idnumberOptions.errorCallback && typeof _idnumberOptions.errorCallback === 'function') {
        _idnumberOptions.errorCallback(ctx, next);
        return;
      }
      let config = ctx.app.config.wellappPassportIdnumber;
      let error = ctx.req.session.messages.length ? ctx.req.session.messages[ctx.req.session.messages.length - 1] : '';
      let errorStrategy = config.errorStrategy;
      ctx.req.session.messages = []; //清空session登录反馈的信息
      if (error.indexOf('Not foud user by id number :') != -1) {
        let idnumber = error.split(':')[1].trim();
        if (ctx.helper.mobileDetect.mobile()) {
          _idnumberOptions = _idnumberOptions.mobile || _idnumberOptions;
        }
        idnumber = !!config.encodeIdNumber ? Buffer.from(idnumber, 'base64').toString() : idnumber;
        if (errorStrategy && errorStrategy.idNumberNotFound) {
          // 身份证无法找到
          if (typeof errorStrategy.idNumberNotFound === 'string') {
            //地址
            ctx.redirect(errorStrategy.idNumberNotFound);
          } else if (typeof errorStrategy.idNumberNotFound === 'function') {
            errorStrategy.idNumberNotFound(ctx, idnumber);
          }
          return;
        }
        //FIXME: 重定向到身份证绑定的地址，属于业务范畴的，由业务定义到选项
        ctx.redirect(
          '/api/org/user/idnumber/update/redirect?idNumber=' +
            idnumber +
            '&_provider=idnumber-token&redirect=' +
            _idnumberOptions.successRedirect
        );
        return;
      } else if (error.indexOf('Access token is invalid') != -1) {
        if (errorStrategy && errorStrategy.accessTokenInvalid) {
          if (typeof errorStrategy.accessTokenInvalid === 'string') {
            //地址
            ctx.redirect(errorStrategy.accessTokenInvalid);
          } else if (typeof errorStrategy.accessTokenInvalid === 'function') {
            errorStrategy.accessTokenInvalid(ctx, idnumber);
          }
        }
        ctx.redirect('/error?real_status=403');
        return;
      }
    }
    await next();
  };
};
