'use strict';

exports.wellappPassportIdnumber = {
  usernameField: 'idNumber',
  passwordField: 'accessToken',
  // encodeIdNumber: true,
  errorStrategy: {
    // idNumberNotFound: '/error?real_status=404', //身份证无法找到对应用户
    accessTokenInvalid: '/error?real_status=403' // token不合法
  }
};
