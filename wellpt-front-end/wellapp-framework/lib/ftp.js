const ftp = require('basic-ftp');
const Client = require('ssh2-sftp-client');
const fs = require('fs');
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

Ftp.prototype.rename = async function (src, dest) {
  if (this.protocols === 'sftp') {
    await this.client.rename(src, dest);
  } else if (this.protocols === 'ftp') {
    await this.client.rename(src, dest);
  }
};

Ftp.prototype.remove = async function (src) {
  if (this.protocols === 'sftp') {
    await this.client.delete(src);
  } else if (this.protocols === 'ftp') {
    await this.client.remove(src);
  }
};

Ftp.prototype.exists = async function (src) {
  if (this.protocols === 'sftp') {
    return await this.client.exists(src);
  } else if (this.protocols === 'ftp') {
    try {
      let date = await this.client.lastMod(src);
      return date != null;
    } catch (error) {
      return false;
    }
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

module.exports = Ftp;
