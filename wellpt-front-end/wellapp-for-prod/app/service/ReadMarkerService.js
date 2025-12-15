'use strict';

const Service = require('wellapp-framework').Service;

class ReadMarkerService extends Service {

  async markRead(dataUuid, userid) {
    const { app, ctx } = this;
    try {
      ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST', contentType: 'json', dataType: 'json',
        data: {
          serviceName: 'readMarkerService', methodName: 'markRead',
          version: '', args: JSON.stringify([ dataUuid, userid ]),
        },
      });
    } catch (error) {
      app.logger.error('标记已读服务接口异常：%s', error);
    }
  }
}

module.exports = ReadMarkerService;
