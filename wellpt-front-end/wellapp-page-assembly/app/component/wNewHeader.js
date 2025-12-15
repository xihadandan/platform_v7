'use strict';

const Component = require('wellapp-framework').Component;
const is = require('is-type-of');
/**
 * 头部组件的解析类
 */
class WNewHeader extends Component {
  constructor() {
    super();
    this.name = '头部组件';
    this.category = 'app';
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.appType = 'techComponent';
    this.componentType = 'unit';
    this.scope = ['wPage', 'wLayoutit']; // 组件默认生效页面类型:wPage
  }

  async filterGranted(ctx, navConfiguration) {
    if (!navConfiguration || !navConfiguration.menuItems || navConfiguration.menuItems.length == 0) {
      return;
    }

    let checkGrantedItems = [];
    for (let i = 0, len = navConfiguration.menuItems.length; i < len; i++) {
      let item = navConfiguration.menuItems[i];
      if (item.hidden == '1') {
        continue;
      }
      if (item.eventHandler) {
        checkGrantedItems.push({
          object: is.string(item.eventHandler)
            ? item.eventHandler
            : item.eventHandler.id
            ? item.eventHandler.id
            : item.eventHandler.toString(),
          functionType: 'appProductIntegration'
        });
      }
    }
    let result = await this.grantedFilter(ctx, checkGrantedItems);

    for (let i = navConfiguration.menuItems.length - 1; i >= 0; i--) {
      let item = navConfiguration.menuItems[i];
      if (item.hidden == '1') {
        navConfiguration.menuItems.splice(i, 1); // 隐藏则直接移除
        continue;
      }
      if (item.eventHandler) {
        if (!result[is.string(item.eventHandler) ? item.eventHandler : item.eventHandler.id]) {
          navConfiguration.menuItems.splice(i, 1);
        }
      }
    }
  }

  async explainDefinitionJson(ctx, json) {
    let configuration = json.configuration;

    await this.filterGranted(ctx, configuration && configuration.mainNav);
    await this.filterGranted(ctx, configuration && configuration.rightNav);
  }

  async getFunctionElements(ctx, json) {
    if (!json || !json.configuration) {
      return [];
    }
    let elements = [];
    if (json.configuration.mainNav && json.configuration.mainNav.menuItems) {
      let items = json.configuration.mainNav.menuItems;
      for (let i = 0, len = items.length; i < len; i++) {
        elements.push({
          uuid: items[i].uuid,
          id: items[i].uuid,
          name: '主导航_' + items[i].text,
          code: 'main_nav_' + (i + 1),
          type: 'menu'
        });
      }
    }
    if (json.configuration.rightNav && json.configuration.rightNav.menuItems) {
      let items = json.configuration.rightNav.menuItems;
      for (let i = 0, len = items.length; i < len; i++) {
        elements.push({
          uuid: items[i].uuid,
          id: items[i].uuid,
          name: '右侧导航_' + items[i].text,
          code: 'right_nav_' + (i + 1),
          type: 'menu'
        });
        if (items[i].badge && items[i].badge.dataStoreCounter) {
          let _element = await this.getDataStoreFunctionElement(ctx, items[i].badge.dataStoreCounter);
          if (_element) {
            elements = this.pushElements(elements, _element);
          }
        }
      }
    }

    return elements;
  }
}

module.exports = WNewHeader;
