'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 文件管理的解析类
 */
class WFileManager extends Component {
  constructor() {
    super();
    this.name = '文件管理组件';
    this.category = 'app';
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.previewHtml = '<div class="panel panel-default"><div class="panel-body ui-sortable"></div></div>';
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

    return elements;
  }
}

module.exports = WFileManager;
