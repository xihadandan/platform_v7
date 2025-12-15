'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 标签页组件的解析类
 */
class WBootstrapTabs extends Component {
  constructor() {
    super();
    this.name = '标签页组件';
    this.category = 'app';
    this.layoutType = 'specific'; // specific特定布局
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.previewHtml = '<div class="panel panel-default"><div class="panel-body ui-sortable"></div></div>'; // 预览html
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.scope = ['wPage', 'wLayoutit']; // 组件默认生效页面类型:wPage
  }

  async explainDefinitionJson(ctx, json) {
    let configuration = json.configuration;
    if (!configuration || !configuration.tabs || configuration.tabs.length == 0) {
      return;
    }

    let checkGrantedTabs = [];
    for (let i = 0, len = configuration.tabs.length; i < len; i++) {
      let tab = configuration.tabs[i];
      if (tab.uuid) {
        if (tab.eventHandler && tab.eventHandler.id) {
          checkGrantedTabs.push({
            object: tab.eventHandler.id,
            functionType: 'appProductIntegration'
          });
        }
      }

      if (tab.uuid) {
        checkGrantedTabs.push({
          object: tab.uuid + json.id,
          functionType: 'AppWidgetFunctionElement',
          md5hex: true
        });
      }
    }
    let result = await this.grantedFilter(ctx, checkGrantedTabs);

    for (let i = configuration.tabs.length - 1; i >= 0; i--) {
      let tab = configuration.tabs[i];
      let hasEventGranted = true;
      if (tab.eventHandler && tab.eventHandler.id) {
        if (!result[tab.eventHandler.id]) {
          hasEventGranted = false;
          // 事件处理没有权限
          configuration.tabs.splice(i, 1);
          continue;
        }
      }

      if (hasEventGranted && tab.uuid && !result[tab.uuid + json.id]) {
        // tab元素没有权限
        configuration.tabs.splice(i, 1);
      }
    }
  }

  async getFunctionElements(ctx, json) {
    if (!json || !json.configuration) {
      return [];
    }
    let tabs = json.configuration.tabs,
      elements = [];
    if (tabs) {
      for (let i = 0, len = tabs.length; i < len; i++) {
        elements.push({
          uuid: tabs[i].uuid,
          id: tabs[i].uuid,
          name: '页签_' + tabs[i].name,
          code: 'tab_' + (i + 1),
          type: 'tab'
        });

        if (tabs[i].BadgeTypeCountDs) {
          elements.push(await this.getDataStoreFunctionElement(ctx, tabs[i].BadgeTypeCountDs));
        }
      }
    }
    return elements;
  }
}

module.exports = WBootstrapTabs;
