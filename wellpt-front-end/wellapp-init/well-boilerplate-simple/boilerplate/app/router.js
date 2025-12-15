'use strict';

/**
 * @param {Wellsoft.Application} app - wellsoft application
 */
module.exports = app => {
  const { router, controller } = app;
  router.get('/hello/index', controller.helloWorldController.index);
};
