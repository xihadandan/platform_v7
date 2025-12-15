'use strict';

/**
 * wellapp-wellapp-for-prod default config
 * @member Config#wellappForProd
 * @property {String} SOME_KEY - some description
 */
exports.wellappForProd = {};

// 文件上传配置
exports.fileupload = {
  mode: 'mongodb', // ftp / sftp / mongodb
  maxChunkSize: 1 * 1024 * 1024

  // host: '192.168.0.116',
  // port: '2294',
  // username: 'foo',
  // password: 'pass',
  // secure: false,
  // uploadDir: '/upload',
  // debug: false,
  // fileUploadComplete: async options => {
  //   //TODO: 上传完文件后续处理逻辑
  //   console.log(options.md5);
  // },
  // fileNotFound: async options => {
  //   console.log('文件丢失：', options.fileIDs);
  // }
};
