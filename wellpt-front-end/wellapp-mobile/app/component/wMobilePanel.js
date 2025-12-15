'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 手机面板解析的解析类
 */
class WMobilePanel extends Component {
  constructor() {
    super();
    this.name = '手机面板';
    this.category = 'basic';
    this.previewHtml = '<div class="panel panel-default"><div class="panel-body ui-sortable"></div></div>'; // 预览html
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.scope = ['wMobilePage']; // 组件默认生效页面类型:wPage
  }

  async explainDefinitionJson(ctx, json) {
    let configuration = json.configuration;
    if (!configuration || !configuration.menuItems || configuration.menuItems.length == 0) {
      return;
    }
    const items = [];
    if (configuration.eventHanlderId && configuration.eventHanlderPath) {
      items.push({
        object: configuration.eventHanlderId,
        functionType: 'appProductIntegration'
      });
    }
    for (let i = 0, len = configuration.menuItems.length; i < len; i++) {
      let n = configuration.menuItems[i];
      if (n.eventHandler) {
        items.push({
          object: n.eventHandler.appUuid || n.eventHandler.id,
          functionType: 'appProductIntegration'
        });
      }
    }

    let result = await this.grantedFilter(ctx, items);
    if (configuration.eventHanlderId && configuration.eventHanlderPath) {
      if (!result[configuration.eventHanlderId]) {
        delete configuration.eventHanlderId;
        delete configuration.eventHanlderPath;
      }
    }
    for (let i = configuration.menuItems.length - 1; i >= 0; i--) {
      let n = configuration.menuItems[i];
      if (n.eventHandler && !result[n.eventHandler.appUuid || n.eventHandler.id]) {
        configuration.menuItems.splice(i, 1);
      }
    }
  }
}

module.exports = WMobilePanel;
