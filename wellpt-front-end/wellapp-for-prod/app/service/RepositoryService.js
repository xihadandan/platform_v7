'use strict';

const Service = require('wellapp-framework').Service;

class RepositoryService extends Service {
  async mongoFileUpload2Sftp(fileIDs) {
    const { app, ctx } = this;
    try {
      let result = await ctx.curl(app.config.backendURL + '/api/repository/file/mongo/mongoFileUpload2Sftp', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        dataAsQueryString: true,
        data: {
          fileIDs
        }
      });
      if (result.data.code == 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('通知mongodb上传文件到sftp接口异常：%s', error);
    }
    return null;
  }
}

module.exports = RepositoryService;
