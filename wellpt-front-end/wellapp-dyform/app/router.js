'use strict';

/**
 * @param {Wellsoft.Application} app - wellsoft application
/**
 *
 *
 * @param {*} app
 */
module.exports = app => {
  const { router, controller } = app;

  router.get('/dyform-designer/preview/:id', controller.dyformDefinitionController.vuePageDesignPreview);
  router.get('/dyform-designer/index', controller.dyformDefinitionController.vueDyformDefinitionDesign);
  router.get('/dyform-viewer/detail', controller.dyformDefinitionController.vueDyformDefinitionDesign);

  router.get('/data-model-form/:dataModelUuid', controller.dyformDefinitionController.dataModelDyform);

  router.get('/dyform-viewer/data', controller.dyformExplainController.dataViewer);
  router.get('/dyform-viewer/just-dyform', controller.dyformExplainController.justDyform);

  router.get('/dms/data/manager', controller.dyformExplainController.dmsManager);

  /**
   * 表单类控制地址
   */
  router.post('/dyform/explain/explainDyformRequirejs', controller.dyformExplainController.explainDyformRequirejs); // 解析表单依赖的脚本配置
  router.get('/pt/dyform/definition/form-copy', controller.dyformDefinitionController.formCopy);
  router.get('/pt/dyform/definition/preview', controller.dyformDefinitionController.preview);
  router.get('/pt/dyform/definition/mobile/preview', controller.dyformDefinitionController.mobilePreview);
  router.get('/pt/dyform/definition/open', controller.dyformDefinitionController.preview);
  router.post('/pt/dyform/definition/open', controller.dyformDefinitionController.preview);
  router.post('/pt/dyform/definition/update', controller.dyformDefinitionController.save); // 表单定义更新
  router.post('/pt/dyform/definition/save', controller.dyformDefinitionController.save); // 表单定义更新
  router.post('/pt/dyform/definition/getFormDefinition', controller.dyformDefinitionController.getFormDefinition); // 获取表单定义
  router.get('/pt/dyform/definition/getFormDefinitionByUuid', controller.dyformDefinitionController.getFormDefinitionByUuid); // 获取表单定义
  router.post('/pt/dyform/data/validate/exists', controller.dyformValidateController.existsFormData); // 表单数据校验
  router.post('/pt/dyform/definition/list', controller.dyformDefinitionController.list);
  router.get('/pt/dyform/definition/form-designer', controller.dyformDefinitionController.design); // 表单设计器
  router.get('/pt/dyform/definition/pform-designer', controller.dyformDefinitionController.design); // 表单设计器
  router.get('/pt/dyform/definition/vform-designer', controller.dyformDefinitionController.design); // 表单设计器
  router.get('/pt/dyform/definition/cform-designer', controller.dyformDefinitionController.design); // 表单设计器
  router.get('/pt/dyform/definition/mform-designer', controller.dyformDefinitionController.design); // 表单设计器
  router.get('/pt/dyform/definition/mstform-designer', controller.dyformDefinitionController.design); // 表单设计器
  router.post('/pt/dyform/field/get/:uuid', controller.dyformFieldController.getField);
  router.post('/pt/dyform/field/save', controller.dyformFieldController.saveField);
  router.post('/pt/dyform/field/deleteAll', controller.dyformFieldController.deleteAll);
  router.post('/pt/dyform/data/getFormDefinitionData', controller.dyformDataController.getFormDefinitionData);
  router.post('/pt/dyform/data/validateFormData', controller.dyformDataController.validateFormData);
  router.post('/pt/dyform/data/saveFormData', controller.dyformDataController.saveFormData);
  router.post('/pt/dyform/data/uploadFile', controller.dyformDataController.uploadFile);
  router.get('/pt/dyform/data/downloadFile/:uuid', controller.dyformDataController.downloadFile);
  router.post('/pt/dyform/data/uploadImage', controller.dyformDataController.uploadImage);
  router.get('/pt/dyform/data/downloadImage', controller.dyformDataController.downloadImage);
  router.post('/pt/dyform/data/getDyformTitle', controller.dyformDataController.getDyformTitle);
  router.post('/pt/dyform/data/dynamicQuery', controller.dyformDataController.dynamicQuery);

  // =========================================================================

  router.get('/pt/dyform/app_dyform_data_viewer', controller.dyformDataController.dyformDataViewer);

  router.get(
    '/resources/pt/js/dyform/definition/ckeditor/plugins/:controlType/images/placeHolder.jpg',
    controller.dyformDefinitionController.placeHolderJpg
  );

  router.get('/fileListConfig/list', controller.dyformDefinitionController.fileListConfigList);

  // =========================================================================

  router.delete('/dict/:uuid', controller.dyformFieldController.deleteDictByUuid);

  // 表单定义升级
  router.get('/pt/dyform/definition/upgrade/2v6_2_5', controller.dyformDefinitionUpgradeController.upgrade2v6_2_5);
  router.get('/pt/dyform/definition/upgrade/2v6_2_3_repair_json', controller.dyformDefinitionUpgradeController.upgrade_v6_2_3_repair_json);
  router.get(
    '/pt/dyform/definition/upgrade/2v6_2_5_repair_readonly_style',
    controller.dyformDefinitionUpgradeController.upgrade_v6_2_5_repair_readonly_style
  );

  router.post('/pt/dyform/data/updateFieldValue', controller.dyformDataController.updateFieldValue); // 更新字段值
  router.get('/basicdata/datadict/type/:type', controller.dyformDataController.getDictByType); // 更新字段值

  router.post('/pt/dyform/data/autoComplete', controller.dyformDataController.dialogAutoComplete); // 弹窗组件自动匹配

  router.post('/pt/dyform/definition/getFieldDictionaryOptionSet', controller.dyformDataController.getFieldDictionaryOptionSet); // 获取表单字段选项

  //流程签批 表单详情显示
  router.get('/pt/dyform/flow_dyform_data_viewer', controller.flowVisaDyformDataController.dyformDataViewer);
};
