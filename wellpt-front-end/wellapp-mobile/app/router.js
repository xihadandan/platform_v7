'use strict';

/**
 * @param {Wellsoft.Application} app - wellsoft application
 */
module.exports = app => {
  const { router, controller } = app;

  router.get('/mobile/mui/login.jsp', controller.mobileUserController.login);
  router.get('/mobile/mui/login', controller.mobileUserController.login);
  router.get('/mobile/mui/userInfo.html', controller.mobileUserController.mUserInfoViewer);
  router.get('/mobile/mui/modifyPassword.html', controller.mobileUserController.mModifyPasswordViewer);
  // 代理登录操作
  router.all('/webservices/wellpt/rest/service', controller.mobileUserController.rest);

  router.get('/mobile/app/qrcode', controller.mobileAppController.qrcode);
  router.get('/mobile/app/download', controller.mobileAppController.download);


  router.post('/uni-app-design-json/set', controller.mobileAppController.setUniappTempDesignData);
  router.get('/uni-app-design-json/get', controller.mobileAppController.getUniappTempDesignData);
};
