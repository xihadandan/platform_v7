'use strict';

/**
 * @param {Wellsoft.Application} app - wellsoft application
 */
module.exports = app => {
  const { router, controller } = app;

  router.post('/iuser', controller.testController.postIuser);
  router.get('/iuser/check', controller.testController.checkIuser);
  router.get('/iuser/register', controller.testController.registerInternetUser);
};
