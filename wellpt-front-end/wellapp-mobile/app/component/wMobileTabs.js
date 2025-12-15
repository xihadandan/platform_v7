'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 手机标签页组件解析的解析类
 */
class WMobileTabs extends Component {
  constructor() {
    super();
    this.name = '手机标签页';
    this.category = 'layout';
    this.previewHtml = '<div class="panel panel-default"><div class="panel-body ui-sortable"></div></div>'; // 预览html
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.scope = ['wMobilePage']; // 组件默认生效页面类型:wPage
  }

  async explainDefinitionJson(ctx, json) {
    let configuration = json.configuration;
    if (!configuration || !configuration.tabs || configuration.tabs.length == 0) {
      return;
    }
    const items = [];

    for (let i = 0, len = configuration.tabs.length; i < len; i++) {
      let n = configuration.tabs[i];
      if (n.eventHandler) {
        items.push({
          object: n.eventHandler.appUuid || n.eventHandler.id,
          functionType: 'appProductIntegration'
        });
      }
    }
    let result = await this.grantedFilter(ctx, items);

    for (let i = configuration.tabs.length - 1; i >= 0; i--) {
      let n = configuration.tabs[i];
      if (n.eventHandler && !result[n.eventHandler.appUuid || n.eventHandler.id]) {
        configuration.tabs.splice(i, 1);
      }
    }
  }
}

module.exports = WMobileTabs;
