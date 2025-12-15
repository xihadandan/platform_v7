'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 手风琴组件的解析类
 */
class WAccordion extends Component {
  constructor() {
    super();
    this.name = '手风琴组件';
    this.category = 'app';
    this.layoutType = 'specific'; // specific特定布局
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
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
      if (tab.eventHandler && tab.eventHandler.id) {
        checkGrantedTabs.push({
          object: tab.eventHandler.id,
          functionType: 'appProductIntegration'
        });
      }

      if (tab.uuid) {
        checkGrantedTabs.push({
          object: tab.uuid,
          functionType: 'AppWidgetFunctionElement',
          md5hex: true
        });
      }
    }
    let result = await this.grantedFilter(ctx, checkGrantedTabs);

    for (let i = configuration.tabs.length - 1; i >= 0; i--) {
      let tab = configuration.tabs[i];
      //let hasEventGranted = true;
      if (tab.eventHandler && tab.eventHandler.id) {
        if (!result[tab.eventHandler.id]) {
          // hasEventGranted = false;
          // 事件处理没有权限
          configuration.tabs.splice(i, 1);
          continue;
        }
      }

      // if (hasEventGranted && tab.uuid && result[tab.uuid] != undefined && !result[tab.uuid]) {
      //   // tab元素没有权限
      //   configuration.tabs.splice(i, 1);
      // }
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
          name: '手风琴子项_' + tabs[i].name,
          code: 'accordion_item_' + (i + 1),
          type: 'tab'
        });

        if (tabs[i].BadgeTypeCountDs) {
          let _element = await this.getDataStoreFunctionElement(ctx, tabs[i].BadgeTypeCountDs);
          if (_element) {
            elements.push(_element);
          }
        }
      }
    }
    return elements;
  }
}

module.exports = WAccordion;
