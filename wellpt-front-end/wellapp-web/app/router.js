'use strict';

/**
 * @param {Egg.Application} app - egg application
 */
module.exports = app => {
  const { router, controller } = app;
  router.get('/error', controller.indexController.error);
  router.get('/error/:status', controller.indexController.error);

  router.get('/', controller.indexController._index);

  router.get(/^\/resources\/pt\/images\/.*$/, controller.indexController.images);

  router.get('/test/sample/vue', controller.indexController.helloVue);

  router.get('/excelweb', controller.indexController.excelweb);

  router.get('/webConfig/getAmapConfig', controller.indexController.getAmapConfig);
};
