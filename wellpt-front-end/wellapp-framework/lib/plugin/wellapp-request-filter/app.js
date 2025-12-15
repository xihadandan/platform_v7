'use strict';

module.exports = app => {
  //拦截请求
  app.config.coreMiddleware.splice(0, 0, 'requestFilter');
};
