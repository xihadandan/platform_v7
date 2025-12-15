'use strict';

const Service = require('wellapp-framework').Service;
const UAParser = require('ua-parser-js');

class LoginLogService extends Service {
  async commitLoginLog(user) {
    const { app, ctx } = this;
    const userAgent = ctx.req.headers['user-agent'];
    const parser = new UAParser(userAgent);
    const userAgentInfo = parser.getResult();
    app.logger.info('当前登录用户客户端信息: %s ', JSON.stringify(userAgentInfo));
    let loginIp =
      user.userIp ||
      ctx.req.headers['x-forwarded-for'] ||
      ctx.req.connection.remoteAddress ||
      ctx.req.socket.remoteAddress ||
      (ctx.req.connection.socket ? ctx.req.connection.socket.remoteAddress : null);
    if (loginIp === '::1') {
      loginIp = '127.0.0.1';
    } else if (loginIp && loginIp.startsWith('::ffff:')) {
      loginIp = loginIp.substring(7); // 处理 ::ffff:127.0.0.1 这种情况
    }
    try {
      ctx.curl(app.config.backendURL + '/user/loginLog', {
        method: 'post',
        dataType: 'json',
        contentType: 'json',
        data: {
          loginIp,
          loginSource: user._PROVIDER,
          userOs: userAgentInfo.os.name,
          loginTime: new Date().getTime(),
          loginLocale: ctx.localeVariable,
          userUuid: user.userUuid,
          userName: user.userName,
          tenant: user.tenantId,
          system:
            ctx.SYSTEM_ID ||
            (user.userSystemOrgDetails && user.userSystemOrgDetails.details && user.userSystemOrgDetails.details.length > 0
              ? user.userSystemOrgDetails.details[0].system
              : ctx.SYSTEM_ID),
          browser: userAgentInfo.browser.name
            ? userAgentInfo.browser.name + (userAgentInfo.browser.version ? `/${userAgentInfo.browser.version}` : '')
            : undefined
        }
      });
    } catch (err) {
      app.logger.error('当前登录用户保存登录日志异常: %s ', err);
    }
  }
}

module.exports = LoginLogService;
