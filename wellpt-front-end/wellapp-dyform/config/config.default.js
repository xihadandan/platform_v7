'use strict';

/**
 * wellapp-wellapp-dyform default config
 * @member Config#wellappDyform
 * @property {String} SOME_KEY - some description
 */
exports.wellappDyform = {};

exports.io = {
  namespace: {
    '/wellapp-dyform': {
      connectionMiddleware: ['authSocketConnect', 'dyformDataJoin'],
      packetMiddleware: ['packetReqUser']
    }
  }
};

// exports.DyformDevelopment = "DyformExplainDevelopment"; // 表单解析二开，针对全局
