'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 统一门户组件的解析类
 */
class WUnifiedPortal extends Component {
  constructor() {
    super();
    this.name = '统一门户组件';
    this.category = 'app';
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.scope = ['wPage']; // 组件默认生效页面类型:wPage
  }

  async getFunctionElements(ctx, json) {
    if (!json || !json.configuration) {
      return [];
    }

    const elements = [];
    return elements;
  }
}

module.exports = WUnifiedPortal;
