'use strict';

const Service = require('wellapp-framework').Service;
// let Client = require('ssh2-sftp-client');
const path = require('path');
const fs = require('fs');
// const ftp = require('basic-ftp');
const compressing = require('compressing');
const { v4: uuidv4 } = require('uuid');
const Ftp = require('wellapp-framework').Ftp;
const crypto = require('crypto');
class SftpService extends Service {
  async exec(protocols) {
    const { app, ctx } = this;

    let { host, port, username, password, uploadDir, debug, secure } = app.config.fileupload;
    let options = { host, port, username, password, protocols, logger: app.logger, debug, secure };
    const ftp = new Ftp(options);
    await ftp.connect();
    if (ctx.req.headers['content-type'] && ctx.req.headers['content-type'].indexOf('multipart/') != -1) {
      const stream = await ctx.getFileStream();
      let { filename, mimeType } = stream;
      let { md5, localFileSourceIcon, bsMode, origUuid, source, size } = stream.fields;
      if (md5 === undefined) {
        //计算文件md5
        const fsHash = crypto.createHash('md5');
        let _size = 0;
        stream.on('data', d => {
          fsHash.update(d);
          _size += d.length;
        });
        stream.on('end', () => {
          md5 = fsHash.digest('hex');
          app.logger.info('%s文件的MD5值为: %s', filename, md5);
          if (size == undefined) {
            size = _size;
          }
        });
      }
      let filePath = uploadDir + md5,
        _fileuuid;
      if (md5 === undefined) {
        _fileuuid = uuidv4(); //临时文件id
        filePath = uploadDir + _fileuuid;
      }
      console.log('upload file : %s -> %s', filename, filePath);
      await ftp.upload(stream, filePath);
      if (_fileuuid) {
        await (function (time) {
          // 休眠一会，等待md5计算值
          return new Promise((resolve, reject) => setTimeout(resolve, time));
        })(100);
        try {
          // 文件md5计算完后，更新临时文件id
          await ftp.rename(filePath, uploadDir + md5);
        } catch (error) {
          app.logger.error(error.message);
          // 已存在的重名文件
          if (await ftp.exists(uploadDir + md5)) {
            await ftp.remove(filePath);
          }
        }
      }
      ftp.close();
      const result = await ctx.curl(app.config.backendURL + '/api/repository/file/mongo/uploadNoStreamFileMD5', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: { md5, filename, contentType: mimeType, source, localFileSourceIcon, bsMode, origUuid }
      });
      let uploadResult = result.data.data;
      let fileID = uploadResult.fileID;
      ctx.body = {
        code: 0,
        success: true,
        data: [
          {
            fileID: uploadResult.fileID,
            digestValue: uploadResult.digestValue,
            contentType: uploadResult.contentType,
            filename,
            fileSize: size,
            creator: uploadResult.userId,
            userId: uploadResult.userId,
            userName: uploadResult.userName,
            createTime: uploadResult.createTime,
            uploadTime: uploadResult.createTime
          }
        ]
      };

      if (typeof app.config.fileupload.fileUploadComplete === 'function') {
        // 执行上传后续业务
        app.config.fileupload.fileUploadComplete({ ctx, md5, filePath, fileID, fileName: filename });
      }
    } else if (ctx.req.url.indexOf('file/mongo/down') != -1) {
      //下载
      let tempdir = app.config.multipart.tmpdir;
      let params = {};
      if (ctx.req.query.fileID) {
        params.fileID = ctx.req.query.fileID;
      }
      if (ctx.req.query.fileIDs) {
        let _fileids = JSON.parse(ctx.req.query.fileIDs);
        params.fileID = [];
        for (let i = 0, len = _fileids.length; i < len; i++) {
          params.fileID.push(_fileids[i].fileID);
        }
        params.fileID = params.fileID.join(';');
      }
      // fileIDs
      const result = await ctx.curl(app.config.backendURL + '/repository/file/mongo/getNonioFiles', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        dataAsQueryString: true,
        data: params
      });
      if (result.data.data) {
        if (ctx.req.query.fileIDs) {
          //多文件下载要压缩成统一的文件
          let _zipFileName = [];
          let _folder = uuidv4();
          fs.mkdirSync(path.resolve(tempdir, _folder), { recursive: true });
          let _fileIDs = [];
          let srcs = [],
            dests = [];
          for (let i = 0; i < result.data.data.length; i++) {
            let _file = result.data.data[i];
            _fileIDs.push(_file.uuid);
            srcs.push(uploadDir + _file.digestValue);
            dests.push(path.resolve(tempdir, _folder, _file.fileName));
            _zipFileName.push(_file.fileName.substring(0, _file.fileName.lastIndexOf('.')));
          }
          let res = await ftp.downloadAll(srcs, dests);
          ftp.close();
          for (let i = 0; i < res.length; i) {
            if (res[i] !== -1) {
              _fileIDs.splice(i, 1);
              res.splice(i, 1);
              continue;
            }
            i++;
          }
          if (_fileIDs.length) {
            ctx.body = 'No such file, try later.';
            //存在文件未找到，派发文件丢失事件
            if (typeof app.config.fileupload.fileNotFound === 'function') {
              app.config.fileupload.fileNotFound({ ctx, fileIDs: _fileIDs });
            }
            return;
          }
          let _filename =
            ctx.req.query.fileName || result.data.data[0].fileName.substring(0, result.data.data[0].fileName.lastIndexOf('.'));
          const zipStream = new compressing.zip.Stream();
          for (let i = 0; i < result.data.data.length; i++) {
            let _file = result.data.data[i];
            zipStream.addEntry(path.resolve(tempdir, _folder, _file.fileName));
          }
          ctx.attachment(_filename + '.zip');
          ctx.set('Content-Type', 'application/octet-stream; charset=UTF-8');
          ctx.body = zipStream;
        } else {
          let _file = result.data.data[0];
          let digestValue = _file.digestValue;
          let filepath = path.resolve(tempdir, digestValue);
          let code = await ftp.download(uploadDir + digestValue, filepath);
          if (code === -1) {
            ctx.body = 'No such file, try later.';
            // 派发文件丢失事件
            if (typeof app.config.fileupload.fileNotFound === 'function') {
              app.config.fileupload.fileNotFound({ ctx, fileIDs: [_file.uuid] });
            }
            return;
          }
          ctx.attachment(_file.fileName);
          ctx.set('Content-Type', _file.contentType);
          ctx.body = fs.createReadStream(filepath);
        }
      }
    }
  }
}

// function Ftp(options) {
//   this.protocols = options.protocols;
//   this.logger = options.logger;
//   if (options.protocols === 'ftp') {
//     const client = new ftp.Client();
//     let { host, port, username, password, secure, debug } = options;
//     this._options = { host, port, user: username, password, secure: secure };
//     if (debug) {
//       client.ftp.verbose = true; //是否开启调试级别日志
//       client.ftp.log = msg => {
//         options.logger.info(msg);
//       };
//     }
//     this.client = client;
//   } else if (options.protocols === 'sftp') {
//     const sftp = new Client();
//     let { host, port, username, password, debug } = options;
//     this._options = { host, port, username, password };
//     if (debug) {
//       this._options.debug = msg => {
//         options.logger.info(msg);
//       };
//     }
//     this.client = sftp;
//   }
//   return this;
// }

// Ftp.prototype.connect = async function () {
//   if (this.protocols === 'sftp') {
//     await this.client.connect(this._options);
//   } else if (this.protocols === 'ftp') {
//     await this.client.access(this._options);
//   }
// };

// Ftp.prototype.upload = async function (stream, dest) {
//   if (this.protocols === 'sftp') {
//     await this.client.put(stream, dest);
//   } else if (this.protocols === 'ftp') {
//     await this.client.uploadFrom(stream, dest);
//   }
// };

// Ftp.prototype.close = function () {
//   if (this.protocols === 'ftp') {
//     this.client.close();
//   }
// };

// Ftp.prototype.downloadAll = async function (srcs, dests) {
//   if (this.protocols === 'sftp') {
//     let _promise = [];
//     for (let i = 0, len = srcs.length; i < len; i++) {
//       _promise.push(this.download(srcs[i], dests[i]));
//     }
//     return await Promise.all(_promise);
//   } else if (this.protocols === 'ftp') {
//     //ftp 不支持并行请求
//     let res = [];
//     for (let i = 0, len = srcs.length; i < len; i++) {
//       res.push(await this.download(srcs[i], dests[i]));
//     }
//     return res;
//   }
// };

// Ftp.prototype.download = async function (src, dest) {
//   const writer = fs.createWriteStream(dest, {
//     flags: 'w'
//   });
//   if (this.protocols === 'sftp') {
//     // 从sftp获取文件
//     try {
//       await this.client.get(src, writer);
//     } catch (error) {
//       this.logger.error(error.message);
//       if (error.message.indexOf('No such file') != -1) {
//         return -1;
//       }
//       return 0;
//     }
//     return 1;
//   } else if (this.protocols === 'ftp') {
//     try {
//       await this.client.downloadTo(writer, src);
//     } catch (error) {
//       this.logger.error(error.message);
//       if (error.message.indexOf('cannot find the file') != -1) {
//         return -1;
//       }
//       return 0;
//     }
//     return 1;
//   }
// };

module.exports = SftpService;
