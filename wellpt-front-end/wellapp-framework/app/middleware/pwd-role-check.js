'use strict';

module.exports = options => {
  return async function pwdRoleCheck(ctx, next) {
    const isAjax = !!ctx.req.headers['x-requested-with']; //判断请求来源是否是ajax请求
    const reqUrl = ctx.req.url;
    const openUpdatePwdPageUrl = '/web/app/pt-mgr/pt-usr-mgr/app_20210326173601.html?pageUuid=4f4dddab-2349-4eda-bd45-e16f14379790&tag=1';
    const isPwdPage = reqUrl.indexOf(openUpdatePwdPageUrl) > -1; //判断当前页面是否修改密码页面
    const isStatic = reqUrl.indexOf('/static') === 0; //是否静态资源请求
    const isLoginPage = reqUrl === '/login' || reqUrl === '/superadmin/login';
    if (!isAjax && !isStatic && !isPwdPage && !isLoginPage) {
      if (ctx.user && ctx.user.pwdRoleCheckObj && ctx.user.pwdRoleCheckObj.openUpdatePwdPage) {
        let msg = ctx.user.pwdRoleCheckObj.message || '';
        ctx.cookies.set('pwdMsg', escape(msg), { httpOnly: false });
        ctx.redirect(openUpdatePwdPageUrl);
        return;
      }
    }
    await next();
  };
};
