'use strict';

/**
 * @param {Wellsoft.Application} app - wellsoft application
 */
module.exports = app => {
  const { controller, router } = app;
  router.get('/message/queue/count', controller.messageController.queueCount);
  router.get('/message/classify/queryList', controller.messageController.queryList);
  router.get('/message/inbox/queryRecentTenLists', controller.messageController.queryRecentTenLists);
  router.get('/message/classify/facadeQueryList', controller.messageController.facadeQueryList);
  router.put('/message/inbox/updateToReadState', controller.messageController.updateToReadState);
  router.put('/message/inbox/updateToUnReadStateByclass', controller.messageController.updateToUnReadStateByclass);
  router.put('/message/inbox/updateToReadStateByclass', controller.messageController.updateToReadStateByclass);
  router.get('/userPersonalise/queryList', controller.userPersonaliseController.queryList);
  router.post('/userPersonalise/saveUserPersonalise', controller.userPersonaliseController.saveUserPersonalise);
  router.put('/userPersonalise/reset', controller.userPersonaliseController.reset);
  router.get('/personalinfo/modify/password', controller.userPersonaliseController.modifyPassword);
  router.get('/personalinfo/userImg', controller.userPersonaliseController.userHeadImg);
  router.post('/message/content/unread', controller.messageController.unread);
  router.post('/message/content/read', controller.messageController.read);
  router.post('/message/content/deleteInboxMessage', controller.messageController.deleteInboxMessage);
  router.post('/message/content/submitmessage', controller.messageController.contentSubmitmessage);
  router.post('/message/content/deleteOutboxMessage', controller.messageController.deleteOutboxMessage);

  // 附件下载重定向转发
  router.put('/api/repository/file/mongo/:fileId/fileName', controller.repositoryController.renameFileName);
  // 兼容文件上传下载旧地址
  router.all(
    /^\/repository\/file\/mongo\/(savefiles$|savefilesChunk$|getFileChunkInfoAndSave$|getFileChunkInfo$|saveTemps$|download$|downloadVideoSegment$|downAllFiles$|downAllFiles4ocx$|downloadBody|deleteFile$|updateSignature$|downloadSWF$|download4ocx$|download4bodytemplate$)/,
    controller.repositoryController.proxy
  );
  // 文件上传下载代理地址
  router.all('/proxy-repository/repository/file/**', controller.repositoryController.proxy);
  router.post('/repository/file/mongo/saveFilesByFileIds', controller.repositoryController.saveFilesByFileIds);
  // 静态目录重定向
  // router.get('/static/wps/oaassist/EtOAAssist/', controller.repositoryController.etOAAssistForward);
  // router.get('/static/wps/oaassist/WppOAAssist/', controller.repositoryController.wppOAAssistForward);
  // router.get('/static/wps/oaassist/WpsOAAssist/', controller.repositoryController.wpsOAAssistForward);

  router.get('/webmail/get', controller.wmWebmailController.getMail); // 获取邮件详情
  router.get('/webmail/getMailConfig', controller.wmWebmailController.getMailConfig); // 获取邮件配置
  router.post('/webmail/refush', controller.wmWebmailController.refush);

  router.get('/repository/file/mongo/queryFileHistory', controller.repositoryController.queryFileHistory); // 图标附件查询历史版本
  router.post('/repository/file/mongo/getNonioFiles', controller.repositoryController.getNonioFiles); // 获取文件信息
  router.get('/repository/file/mongo/getNonioFilesFromFolder', controller.repositoryController.getNonioFilesFromFolder);

  router.post('/basicdata/calendarcomponent/save', controller.calendarCompoentController.save);
  router.post('/basicdata/calendarcomponent/delete', controller.calendarCompoentController.delete);
  router.post('/basicdata/calendarcomponent/loadEvents', controller.calendarCompoentController.loadEvents);
  router.post('/basicdata/calendarcomponent/getProviderInfo', controller.calendarCompoentController.getProviderInfo);
  //日历分类
  router.post('/api/calendar/category/getAllBySystemUnitIdsLikeName', controller.calendarCategoryController.getAllBySystemUnitIdsLikeName);
  // 获取日历分类
  router.get('/api/calendar/category/get', controller.calendarCategoryController.get);
  // 保存日历分类
  router.post('/api/calendar/category/save', controller.calendarCategoryController.save);
  // 生成日历分类编号
  router.post('/api/calendar/category/generateCalendarCategoryCode', controller.calendarCategoryController.generateCalendarCategoryCode);
  // 删除日历分类
  router.post('/api/calendar/category/deleteAll', controller.calendarCategoryController.deleteAll);
  // 删除没用的日历分类
  router.post('/api/calendar/category/deleteWhenNotUsed', controller.calendarCategoryController.deleteWhenNotUsed);

  // su well ofd
  router.get('/suWellOfdOnlineEditView', controller.ptOfdViewerController.suWellOfdOnlineEditView);
};
