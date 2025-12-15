'use strict';

const Controller = require('wellapp-framework').Controller;

class RepositoryController extends Controller {
  async proxy() {
    const { ctx, app } = this;
    // 校验文件白名单
    if (ctx.req.url.endsWith('savefilesChunk') || ctx.req.url.endsWith('savefiles')) {
      let filename = this.extractFilename();
      if (filename) {
        let result = ctx.app.config.multipartParseOptions.checkFile(null, true, filename);
        if (result) {
          app.logger.error('校验上传文件格式结果: %s', result);
          throw result;
        }
      }
    }
    if (app.config.fileupload.mode === 'ftp') {
      await ctx.service.sftpService.exec('ftp');
      return;
    } else if (app.config.fileupload.mode === 'sftp') {
      await ctx.service.sftpService.exec('sftp');
      return;
    }

    let url = new URL(app.config.backendURL);
    await ctx.proxyRequest(url.host, {
      rewrite(urlObj) {
        urlObj.protocol = url.protocol;
        if (url.protocol === 'http:' && url.port === '') {
          urlObj.port = 80;
        } else if (url.protocol === 'https:' && url.port === '') {
          urlObj.port = 443;
        }
        urlObj.pathname = urlObj.pathname.replace(/^\/proxy-repository/, '');
        return urlObj;
      },
      withCredentials: true
    });
  }

  extractFilename() {
    let contentDispositionHeader = this.ctx.req.headers['content-disposition'];
    if (contentDispositionHeader) {
      const match = contentDispositionHeader.match(/filename\*?=["']?([^"'\s;]+)["']?/i);

      if (!match || !match[1]) {
        return null; // 未找到 filename
      }

      let filename = match[1];

      // 尝试去除可能的引号（双引号或单引号）
      filename = filename.replace(/^['"]|['"]$/g, '');

      // 处理编码（比如 %20 是空格）
      filename = decodeURIComponent(filename);

      return filename;
    }
  }

  async saveFilesByFileIds() {
    try {
      const { app, ctx } = this;
      const { helper } = ctx;
      const result = await ctx.curl(app.config.backendURL + '/repository/file/mongo/saveFilesByFileIds', {
        headers: {
          'content-type': 'application/x-www-form-urlencoded'
        },
        method: 'POST',
        dataType: 'json',
        content: helper.jQueryParam(ctx.req.body)
      });
      if (result.data) {
        this.ctx.body = result.data;
      }
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async downloadFileForward() {
    this.ctx.redirect2backend(this.ctx.req.url.substring(this.ctx.req.url.indexOf('/repository/file')));
  }

  async queryFileHistory() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/repository/file/mongo/queryFileHistory', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        dataAsQueryString: true,
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (e) {
      app.logger.error('获取文件历史异常：%s', e);
    }
  }

  async getNonioFiles() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/repository/file/mongo/getNonioFiles', {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (e) {
      app.logger.error('获取文件异常：%s', e);
    }
  }

  async getNonioFiles() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/repository/file/mongo/getNonioFiles', {
        method: 'post',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (e) {
      app.logger.error('获取文件异常：%s', e);
    }
  }

  async etOAAssistForward() {
    const { ctx, app } = this;
    ctx.redirect('/static/wps/oaassist/EtOAAssist/index.html');
  }

  async wppOAAssistForward() {
    const { ctx, app } = this;
    ctx.redirect('/static/wps/oaassist/WppOAAssist/index.html');
  }

  async wpsOAAssistForward() {
    const { ctx, app } = this;
    ctx.redirect('/static/wps/oaassist/WpsOAAssist/index.html');
  }

  async renameFileName() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/repository/file/mongo/' + ctx.params.fileId + '/fileName', {
        method: 'PUT',
        contentType: 'json',
        dataType: 'application/json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      app.logger.error('重命名文件命名异常：%s', error);
    }
  }

  async getNonioFilesFromFolder() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/repository/file/mongo/getNonioFilesFromFolder', {
        method: 'get',
        contentType: 'application/json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (e) {
      app.logger.error('获取文件异常：%s', e);
    }
  }
}

module.exports = RepositoryController;
