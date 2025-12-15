'use strict';

const Controller = require('egg').Controller;
let Client = require('ssh2-sftp-client');
const PATH = require('path');
const fs = require('fs');
const ftp = require('basic-ftp');
class HomeController extends Controller {
  async index() {
    const { ctx, app } = this;
    ctx.body = 'hi, egg';
  }
  async exchange() {
    const { ctx, app } = this;
    app.logger.info('body: %s', ctx.request.body);
    const result = await ctx.curl(app.config.backendURL + ctx.request.body.targetMethod, {
      method: 'POST',
      dataType: 'text',
      content: ctx.request.body.param
    });
    ctx.body = {
      reason: 'success',
      resCode: '10000',
      resData: result.data
    };
  }

  async ftpExchange() {
    const { ctx, app } = this;
    const { path, toPath, fileName, transtype } = ctx.request.body;
    let frontTempdir = PATH.resolve(app.config.multipart.tmpdir, 'front2backend');
    let backendTempdir = PATH.resolve(app.config.multipart.tmpdir, 'backend2front');
    fs.mkdirSync(frontTempdir, { recursive: true });
    fs.mkdirSync(backendTempdir, { recursive: true });

    const frontFtp = new Ftp(Object.assign({ logger: app.logger, debug: true, secure: false }, app.config.frontFtp));
    const backendFtp = new Ftp(Object.assign({ logger: app.logger, debug: true, secure: false }, app.config.backendFtp));

    let _file = null,
      _downloadFtp = null,
      _uploadFtp = null;
    if (transtype === 1) {
      _file = PATH.resolve(frontTempdir, fileName);
      _downloadFtp = frontFtp;
      _uploadFtp = backendFtp;
    } else if (transtype === 2) {
      _file = PATH.resolve(backendTempdir, fileName);
      _downloadFtp = backendFtp;
      _uploadFtp = frontFtp;
    }
    if (_file) {
      await _downloadFtp.connect();
      await _downloadFtp.download(path + '/' + fileName, _file); //下载本地
      await _uploadFtp.connect();
      await _uploadFtp.upload(fs.createReadStream(_file), toPath + '/' + fileName);
      fs.unlink(_file, () => {});
    }
    ctx.body = 'success';
  }
}

function Ftp(options) {
  this.protocols = options.protocols;
  this.logger = options.logger;
  if (options.protocols === 'ftp') {
    const client = new ftp.Client();
    let { host, port, username, password, secure, debug } = options;
    this._options = { host, port, user: username, password, secure: secure };
    if (debug) {
      client.ftp.verbose = true; //是否开启调试级别日志
      client.ftp.log = msg => {
        options.logger.info(msg);
      };
    }
    this.client = client;
  } else if (options.protocols === 'sftp') {
    const sftp = new Client();
    let { host, port, username, password, debug } = options;
    this._options = { host, port, username, password };
    if (debug) {
      this._options.debug = msg => {
        options.logger.info(msg);
      };
    }
    this.client = sftp;
  }
  return this;
}

Ftp.prototype.connect = async function () {
  if (this.protocols === 'sftp') {
    await this.client.connect(this._options);
  } else if (this.protocols === 'ftp') {
    await this.client.access(this._options);
  }
};

Ftp.prototype.upload = async function (stream, dest) {
  if (this.protocols === 'sftp') {
    await this.client.put(stream, dest);
  } else if (this.protocols === 'ftp') {
    await this.client.uploadFrom(stream, dest);
  }
};

Ftp.prototype.close = function () {
  if (this.protocols === 'ftp') {
    this.client.close();
  }
};

Ftp.prototype.downloadAll = async function (srcs, dests) {
  if (this.protocols === 'sftp') {
    let _promise = [];
    for (let i = 0, len = srcs.length; i < len; i++) {
      _promise.push(this.download(srcs[i], dests[i]));
    }
    return await Promise.all(_promise);
  } else if (this.protocols === 'ftp') {
    //ftp 不支持并行请求
    let res = [];
    for (let i = 0, len = srcs.length; i < len; i++) {
      res.push(await this.download(srcs[i], dests[i]));
    }
    return res;
  }
};

Ftp.prototype.download = async function (src, dest) {
  const writer = fs.createWriteStream(dest, {
    flags: 'w'
  });
  if (this.protocols === 'sftp') {
    // 从sftp获取文件
    try {
      await this.client.get(src, writer);
    } catch (error) {
      this.logger.error(error.message);
      if (error.message.indexOf('No such file') != -1) {
        return -1;
      }
      return 0;
    }
    return 1;
  } else if (this.protocols === 'ftp') {
    try {
      await this.client.downloadTo(writer, src);
    } catch (error) {
      this.logger.error(error.message);
      if (error.message.indexOf('cannot find the file') != -1) {
        return -1;
      }
      return 0;
    }
    return 1;
  }
};

module.exports = HomeController;
