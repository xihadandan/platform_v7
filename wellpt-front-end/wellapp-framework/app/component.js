'use strict';
const lodash = require('lodash');

class Component {
  constructor() {
    let _class = this.__proto__.constructor.name;

    this.category = 'app'; // 组件分类
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.previewHtml = '<div></div>'; // 预览html
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.scope = ['wPage']; // 组件默认生效页面类型:wPage
    this.css = [];
    this.defineJs = lodash.snakeCase(_class).replace('w_', 'widget_'); // 默认命名格式的组件定义脚本: widget_**_**
    this.defaultOptions = {
      // 组件配置的默认选项
      wtype: lodash.lowerFirst(_class), //组件类型
      configuration: {} // 组件默认配置
    };
    this.dependencies = []; // 组件依赖
  }

  /**
   * 组件解析脚本
   * @param {组件定义配置configuration} configuration
   */
  explainJs(configuration) {
    return [];
  }

  explainCss(configuration) {
    return this.css;
  }

  async grantedFilter(ctx, resources) {
    return await ctx.service.jsonDataService._call('securityAuditFacadeService', 'isGranted', [resources]);
  }

  /**
   * 解析定义json
   * @param {*} ctx
   * @param {*} json
   */
  async explainDefinitionJson(ctx, json) { }

  async getDataStoreFunctionElement(ctx, dataStoreId) {
    let dataStoreDefinition = await ctx.service.jsonDataService._call('cdDataStoreDefinitionService', 'getBeanById', [dataStoreId]);
    if (dataStoreDefinition) {
      return {
        uuid: dataStoreDefinition.uuid,
        functionType: 'dataStoreDefinition',
        ref: true
      };
    }
  }

  async getDataDictionaryFunctionElement(ctx, dataDicUuid) {
    return {
      uuid: dataDicUuid,
      functionType: 'dataDictionaryParent',
      ref: true
    };
  }

  async getDyformFunctionElement(ctx, formUuid) {
    return {
      uuid: formUuid,
      functionType: 'formDefinition',
      ref: true
    };
  }

  async getFunctionElements(ctx, json) {
    return [];
  }

  pushElements(elements, _element) {
    let isExit = false;
    for (var element of elements) {
      if (element.uuid === _element.uuid) {
        isExit = true;
        return elements;
      }
    }
    elements.push(_element);
    return elements;
  }
}

module.exports = Component;
