'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 标签树的解析类
 */
class wUnitTree extends Component {
  constructor() {
    super();
    this.name = '通用树组件';
    this.category = 'app';
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.previewHtml = '<div></div>';
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.appType = 'techComponent';
    this.componentType = 'unit';
    this.scope = ['wPage', 'wLayoutit']; // 组件默认生效页面类型:wPage
  }

  async getFunctionElements(ctx, json) {
    if (!json || !json.configuration) {
      return [];
    }

    const elements = [];
    if (json.configuration.treeDataStoreId) {
      elements.push(await this.getDataStoreFunctionElement(ctx, json.configuration.treeDataStoreId));
    }

    return elements;
  }
}

module.exports = wUnitTree;
