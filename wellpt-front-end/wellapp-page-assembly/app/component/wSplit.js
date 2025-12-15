'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 分割线布局的解析类
 */
class WSplit extends Component {
  constructor() {
    super();
    this.name = '分割线布局';
    this.category = 'app';
    this.layoutType = 'grid'; // specific特定布局
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.defineJs = 'container_panel';
    this.previewHtml = '<div class="panel panel-default"><div class="panel-body ui-sortable"></div></div>';
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.scope = ['wLayoutit']; // 组件默认生效页面类型:wPage
  }

  async getFunctionElements(ctx, json) {
    if (!json || !json.configuration) {
      return [];
    }
    const elements = [];
    return elements;
  }
}

module.exports = WSplit;
