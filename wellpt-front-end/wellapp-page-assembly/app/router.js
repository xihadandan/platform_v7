'use strict';

/**
 * @param {Wellsoft.Application} app - wellsoft application
 */
module.exports = app => {
  const { router, controller } = app;

  /**
   * 页面设计器、渲染相关请求地址映射
   */
  router.get(/^\/web\/app([\/\w-.]+)$/, controller.pageRenderController.index); // 配置/预览/展示页面
  router.get('/page/:uuid', controller.pageRenderController.vuePageRender);

  router.get('/webapp/:prodId/:prodVersionUuid', controller.pageRenderController.prodVuePageRender);
  router.get('/webapp/:prodId/:prodVersionUuid/:pageId', controller.pageRenderController.prodVuePageRender);
  router.get('/webapp/:prodId/:prodVersionUuid/:pageId/:pageUuid', controller.pageRenderController.prodVuePageRender);

  // 系统登录
  router.get('/login/system/:prodId/:prodVersionUuid', controller.pageRenderController.prodVersionLogin);


  router.get('/webpage/:id', controller.pageRenderController.vuePageRender);
  router.get('/webpage/:id/:uuid', controller.pageRenderController.vuePageRender);
  router.get('/web/design/config/:piUuid', controller.pageDesignerController.index); // 可视化页面配置
  router.get('/web/design/component_config/:page', controller.pageDesignerController.componentConfigPage); // 组件配置页
  router.get('/page-designer/index', controller.pageDesignerController.vuePageDesigner);
  router.get('/uni-page-designer/index', controller.pageDesignerController.vuePageDesigner);
  router.get('/bigscreen-designer/index', controller.pageDesignerController.vueBigScreenPageDesigner);

  router.get('/index-designer/:prodId', controller.pageDesignerController.indexPageDesigner);
  router.get('/index-designer/:prodId/:prodVersionUuid', controller.pageDesignerController.indexPageDesigner);

  router.get('/index-designer/preview/:prodId/:id', controller.pageDesignerController.indexPageDesignerPreview);
  router.get('/index-designer/preview/:prodId/:prodVersionUuid/:id', controller.pageDesignerController.indexPageDesignerPreview);


  router.get('/module-widget-design/:uuid', controller.pageDesignerController.moduleWgtDesigner);
  router.get('/login-design/:uuid', controller.pageDesignerController.loginDesigner);
  router.get('/login-design-preview/:uuid', controller.pageDesignerController.loginPreview);

  router.get('/page-designer/preview/:id', controller.pageDesignerController.vuePageDesignPreview);
  router.get('/page-designer/pageDesignerType', controller.pageDesignerController.pageDesignerType);
  router.post('/web/design/saveDefinitionJson', controller.pageDesignerController.saveDefinitionJson); // 页面保存
  router.post('/web/design/savePageDefinition', controller.pageDesignerController.savePageDefinition); // 页面保存
  router.get('/theme/theme-specify', controller.themeDesignerController.themeSpecification);
  router.get('/theme/theme-design', controller.themeDesignerController.design);
  router.get('/theme/index', controller.themeDesignerController.index);
  router.get('/theme/pack/export/:uuid', controller.themeDesignerController.exportThemePack);
  router.get('/theme/pack/preview/:uuid', controller.themeDesignerController.preview);
  router.get('/theme/pack/pub/notify', controller.themeDesignerController.themePackPublishNotify);
  router.get('/theme/pack/compile/:uuid', controller.themeDesignerController.compileThemePackCss);


  router.get('/theme/pack/queryPublishedThemeOptions', controller.themeDesignerController.queryPublishedThemePack);
  router.get('/product/version/clearProdVersionBindPageThemeCache', controller.pageRenderController.clearProdVersionBindPageThemeCache);



  router.get('/module/center/index', controller.moduleController.index); // 模块装配中心首页
  router.get('/module/assemble/:uuid', controller.moduleController.assemble); // 模块装配

  router.get('/product/center/index', controller.productController.index); // 产品管理中心首页
  router.get('/product/assemble/:uuid', controller.productController.assemble); // 产品管理中心首页
  router.get('/product/latestPublishedIndexUrl', controller.productController.latestPublishedIndexUrl); // 产品管理中心首页

  // 系统管理后台地址
  router.get('/system_admin/:system/index', controller.pageRenderController.systemAdminIndex);

  // 访问系统首页
  router.get('/sys/:system/index', controller.pageRenderController.sysIndex);
  // 访问系统的匿名公共页
  router.get('/sys-public/:system/index', controller.pageRenderController.sysPublicIndex);

  router.get('/welldo/index', controller.pageRenderController.wellBuilderCenter);
  router.get('/superadmin_v6/index', controller.pageRenderController.superadminIndexV6);


  router.post('/jsexplainer/getJavascriptRequirejsConfig', controller.jsExplainerController.getJavascriptRequirejsConfig);
  router.get('/jsexplainer/registerJavascriptFunction/:jsid', controller.jsExplainerController.registerJavascriptFunction);
  router.get('/pagerender/viewTemplateQuery', controller.pageRenderController.viewTemplateQuery);
  router.get('/pagerender/templatePathRender', controller.pageRenderController.templatePathRender); // 模板类视图路径渲染

  router.post('/jsexplainer/getJavaScriptPath', controller.jsExplainerController.getJavaScriptPath);

  router.get(/^\/html([\/\w-.]+)$/, controller.pageRenderController.htmlTemplateRender); // html视图路径渲染
  router.get('/security_homepage', controller.pageRenderController.renderSecurityHomepage); // 有权限的主页
  router.get('/jsexplainer/requirejsConfigScript.js', controller.jsExplainerController.requirejsConfigScript);
  // =========================================================================

  /**
   * 数据服务类控制地址
   */
  router.get('/dms/data/services', controller.dmsController.dmsDataService); // 数据管理服务Get
  router.post('/dms/data/services', controller.dmsController.dmsDataService); // 数据管理服务Post
  // =========================================================================

  router.get('/web/resource/getProjectImagFolders', controller.webResourceController.getProjectImageFolder);
  router.get('/web/resource/getProjectImages', controller.webResourceController.getProjectImages);
  router.post('/web/resource/queryJavascriptContainsDependency', controller.webResourceController.queryJavascriptContainsDependency);
  router.post('/web/resource/queryJavascriptByIds', controller.webResourceController.queryJavascriptByIds);
  router.get('/web/resource/viewCodeSource', controller.webResourceController.viewCodeSource);

  router.post('/dms/file/manager/component/load/folder/tree', controller.dmsController.getFolderTree);
  router.post('/dms/file/services', controller.dmsController.fileServices);
  router.post('/dms/file/upload', controller.dmsController.fileUpload);

  // 组件自定义配置
  router.get('/api/user/widgetDef/get', controller.pageRenderController.getWidgetDef);
  router.post('/api/user/widgetDef/save', controller.pageRenderController.saveWidgetDef);

  router.post('/api/readMarker/add', controller.readMarkController.readMarkerAdd); // 添加阅读记录
  router.post('/api/readMarker/unReadList', controller.readMarkController.readMarkerUnReadList); // 查询未读记录
  router.post('/api/readMarker/del', controller.readMarkController.readMarkerDel); // 标记为未读

  router.get('/dms/file/viewer/:id', controller.dmsController.dmsFileViewer);
  // 业务管理
  router.post('/api/dms/doc/exc/config/saveOrUpdate', controller.dmsController.saveOrUpdateConfig); // 保存
  router.get('/api/dms/doc/exc/config/queryList', controller.dmsController.queryListConfig); //获取列表
  router.post('/api/dms/doc/exc/config/sequence', controller.dmsController.sequenceConfig); // 列表排序
  router.post('/api/dms/doc/exc/config/del', controller.dmsController.delConfig); // 删除
  router.get('/api/dms/doc/exc/config/getOne', controller.dmsController.getOneConfig); // 获取详情
  router.get('/api/dms/doc/exc/config/queryBusinessCategorys', controller.dmsController.queryBusinessCategorys); // 获取业务分类
  router.get('/api/dms/doc/exc/config/queryBusinessRoles', controller.dmsController.queryBusinessRoles); // 获取业务角色
  router.get('/api/dms/doc/exc/config/queryEvents', controller.dmsController.queryEvents); // 获取事件列表
  router.get('/api/dms/doc/exc/dyform/getOne', controller.dmsController.dyformGetOne); // 获取文档定义
  router.post('/api/dms/doc/exc/dyform/saveOrUpdate', controller.dmsController.dyformSaveOrUpdate); // 保存文档定义
  router.post('/api/dms/doc/exc/config/isRead', controller.dmsController.configIsRead); // 是否已读
  router.post('/api/dms/doc/exc/config/recordRead', controller.dmsController.configRecordRead); // 记录已读
  router.get('/api/dms/doc/exc/config/queryBusinessCategorOrgs', controller.dmsController.queryBusinessCategorOrgs); // 查询业务单位
  //根据文件夹Uuid获取对应的套打模板（树）
  router.post('/api/dms/printtemplate/getPrintTemplateTreeByFolderUuids', controller.dmsController.getPrintTemplateTreeByFolderUuids);

  // -----------------------------------------
  router.post('/api/webapp/color/setting/saveBean', controller.apiAppColorSettingController.saveBean);
  router.post('/api/webapp/color/setting/deleteBean', controller.apiAppColorSettingController.deleteBean);
  router.get('/api/webapp/color/setting/getAllBean', controller.apiAppColorSettingController.getAllBean);
  router.get('/api/webapp/color/setting/getBeanByFilter', controller.apiAppColorSettingController.getBeanByFilter);


  router.get('/data-model-design/index', controller.dmsController.dataModelDesign)

  router.get('/api/widget/getWidgetById', controller.appWidgetController.getWidgetById);
  router.get('/api/widget/getUserAppIndexLayoutMenuWidget', controller.appWidgetController.getUserAppIndexLayoutMenuWidget);
};
