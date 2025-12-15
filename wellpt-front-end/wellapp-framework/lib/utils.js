'use strict';
const crypto = require('crypto');
module.exports = {
  confuseUrlPath(path, app) {
    let parts = path.split('/');
    let encodeParts = [];
    for (let i = 0; i < parts.length; i++) {
      if (parts[i] !== '') {
        let md5 = crypto.createHash('md5');
        md5.update(new Buffer(parts[i], 'binary'));
        let encodeStr = md5.digest('hex');
        encodeParts.push(encodeStr);
        app.staticPackage[encodeStr] = parts[i];
      }
    }
    return '/' + encodeParts.join('/');
  },

  hashMd5(seed) {
    const md5 = crypto.createHash('md5');
    md5.update(seed || Buffer.from(Math.random() + new Date().getTime() + ''));
    return md5.digest('hex');
  }
};
