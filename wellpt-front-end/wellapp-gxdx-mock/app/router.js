'use strict';

/**
 * @param {Egg.Application} app - egg application
 */
module.exports = app => {
  const { router, controller } = app;
  router.get('/', controller.home.index);
  router.post('/exchange', controller.home.exchange);
  router.post('/ftpExchange', controller.home.ftpExchange);
};
