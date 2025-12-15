/* eslint-disable no-bitwise */
'use strict';

module.exports = {

  AclPermission: {
    READ: { code: 'D', mask: 1 },
    DRAFT: { code: 'D', mask: 1 << 5 },
    TODO: { code: 'T', mask: 1 << 6 },
    DONE: { code: 'D', mask: 1 << 7 },
    ATTENTION: { code: 'A', mask: 1 << 8 },
    UNREAD: { code: 'U', mask: 1 << 9 },
    FLAG_READ: { code: 'R', mask: 1 << 10 },
    SUPERVISE: { code: 'S', mask: 1 << 11 },
    MONITOR: { code: 'M', mask: 1 << 12 },
    DELEGATION: { code: 'D', mask: 1 << 13 },
    INTBOX: { code: 'I', mask: 1 << 14 },
    OUTBOX: { code: 'O', mask: 1 << 15 },
    REFUSE: { code: 'R', mask: 1 << 28 },
  },

  UnauthenticateText: '你当前登录的账号无权限访问该数据，请用有权限的账号访问!',


};
