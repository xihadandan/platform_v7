'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 数据管理查看器组件的解析类
 */
class WDataManagementViewer extends Component {
  constructor() {
    super();
    this.name = '数据管理查看器组件';
    this.category = 'app';
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.previewHtml = '<div class="panel panel-default"><div class="panel-body ui-sortable"></div></div>';
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.scope = ['wPage']; // 组件默认生效页面类型:wPage
  }

  async getFunctionElements(ctx, json) {
    if (!json || !json.configuration) {
      return [];
    }

    const elements = [];
    if (json.configuration.belongToFolderUuid) {
      elements.push({
        uuid: json.configuration.belongToFolderUuid,
        functionType: 'dmsFolder'
      });
    }

    if (json.configuration.store && json.configuration.store.formUuid) {
      let _element = await this.getDyformFunctionElement(ctx, json.configuration.store.formUuid);
      elements.push(_element);
    }

    return elements;
  }
}

module.exports = WDataManagementViewer;
