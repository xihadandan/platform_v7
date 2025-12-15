'use strict';

/**
 * @param {Wellsoft.Application} app - wellsoft application
 */
module.exports = app => {
  const { router, controller } = app;

  router.get('/api/getAllThemes', controller.themeController.getAllThemes);
  router.get('/api/getUserTheme', controller.themeController.getUserCustomTheme);
  router.post('/api/saveTheme', controller.themeController.saveTheme);
};
