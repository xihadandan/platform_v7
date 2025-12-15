'use strict';

const Service = require('wellapp-framework').Service;
const UAParser = require('ua-parser-js');

class UserBehaviorLogService extends Service {
  async commitBehaviorLog(user, behavior) {
    const { app, ctx } = this;
    const userAgent = ctx.req.headers['user-agent'];
    const parser = new UAParser(userAgent);
    const userAgentInfo = parser.getResult();
    let ipAddress =
      (user ? user.userIp : undefined) ||
      ctx.req.headers['x-forwarded-for'] ||
      ctx.req.connection.remoteAddress ||
      ctx.req.socket.remoteAddress ||
      (ctx.req.connection.socket ? ctx.req.connection.socket.remoteAddress : null);
    if (ipAddress === '::1') {
      ipAddress = '127.0.0.1';
    } else if (ipAddress.startsWith('::ffff:')) {
      ipAddress = ipAddress.substring(7); // 处理 ::ffff:127.0.0.1 这种情况
    }
    try {
      ctx.curl(app.config.backendURL + '/api/log/manage/operation/saveUserBehaviorLog', {
        method: 'post',
        dataType: 'json',
        contentType: 'json',
        data: {
          userId: user ? user.userId : undefined,
          userName: user ? user.userName : undefined,
          behaviorType: behavior.type,
          behaviorDesc: behavior.description,
          elementTag: behavior.element && behavior.element.tag ? behavior.element.tag.toUpperCase() : undefined,
          elementText: behavior.element ? behavior.element.text : undefined,
          elementXpath: behavior.element ? behavior.element.xpath : undefined,
          pageUrl: behavior.page ? behavior.page.url : undefined,
          pageTitle: behavior.page ? behavior.page.title : undefined,
          pageId: behavior.page ? behavior.page.id : undefined,
          referrer: behavior.page && behavior.page.referrer ? behavior.page.referrer : ctx.req.headers.referer,
          ipAddress,
          extraInfo: behavior.extraInfo
            ? typeof behavior.extraInfo === 'string'
              ? behavior.extraInfo
              : JSON.stringify(behavior.extraInfo)
            : undefined,
          businessCode: behavior.businessCode,
          moduleId: behavior.moduleId,
          moduleName: behavior.moduleName,
          userOs: userAgentInfo.os.name,
          tenant: user ? user.tenantId : undefined,
          system:
            user && user.userSystemOrgDetails && user.userSystemOrgDetails.details && user.userSystemOrgDetails.details.length > 0
              ? user.userSystemOrgDetails.details[0].system
              : undefined,
          browser: userAgentInfo.browser.name
            ? userAgentInfo.browser.name + (userAgentInfo.browser.version ? `/${userAgentInfo.browser.version}` : '')
            : undefined
        }
      });
    } catch (err) {
      app.logger.error('当前用户保存行为操作日志异常: %s ', err);
      throw new Error(err);
    }
  }
}

module.exports = UserBehaviorLogService;
