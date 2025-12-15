'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 左导航组件的解析类
 */
class WLeftSidebar extends Component {
  constructor() {
    super();
    this.name = '左导航组件';
    this.category = 'app';
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.appType = 'techComponent';
    this.componentType = 'unit';
    this.scope = ['wPage', 'wLayoutit']; // 组件默认生效页面类型:wPage
  }

  async explainDefinitionJson(ctx, json) {
    let configuration = json.configuration;
    if (!configuration || !configuration.nav || configuration.nav.length == 0) {
      return;
    }
    // let timing = new Timing();
    // timing.start('wLeftSidebar explainDefinitionJson');

    const navs = [];
    const getNavs = function (navList) {
      for (const nav of navList) {
        const { data, children } = nav;

        if (data && (data.appUuid || data.eventHanlderId)) {
          navs.push({ object: data.appUuid || data.eventHanlderId, functionType: 'appProductIntegration', name: data.name });
        }

        if (children && children.length) {
          getNavs(children);
        }

        // 导航元素
        if (data.uuid) {
          navs.push({ object: data.uuid + json.id, functionType: 'AppWidgetFunctionElement', md5hex: true, name: data.name });
        }
      }
    };

    getNavs(configuration.nav);

    const result = await this.grantedFilter(ctx, navs);
    const filterNavChildrenGranted = function (parent, childrenField = 'children') {
      // 移除无权限的导航
      parent[childrenField] = parent[childrenField].filter(nav => {
        const data = nav.data;
        if (!data) {
          return true;
        }

        // 事件处理优先
        if (data.appUuid || data.eventHanlderId) {
          if (result[data.appUuid || data.eventHanlderId] != undefined) {
            // 事件处理有权限
            return result[data.appUuid || data.eventHanlderId];
          }
        }

        // 元素权限次之
        if (data.uuid && !result[data.uuid + json.id]) {
          // 有子元素集合，以子元素判断结果为准
          return nav.children && nav.children.length;
        }

        return true;
      });

      for (const child of parent[childrenField]) {
        if (child.children) {
          if (child.children.length > 0) {
            filterNavChildrenGranted(child);
          } else {
            delete child.children;
          }
        }
      }

      // 子导航都无权限，则移除父导航
      parent[childrenField] = parent[childrenField].filter(nav => !nav.children || nav.children.length > 0);
    };

    filterNavChildrenGranted(configuration, 'nav');
  }

  async getFunctionElements(ctx, json) {
    if (!json || !json.configuration) {
      return [];
    }
    let nav = json.configuration.nav;
    let navBadgeCountDataStores = [];
    const elements = [];
    if (json.configuration.navType == '2' && nav && nav.length > 0) {
      let navElements = function (list, index) {
        for (let i = 0, len = list.length; i < len; i++) {
          elements.push({
            uuid: list[i].id,
            id: list[i].id,
            name: '导航_' + list[i].name,
            code: 'sidebar_' + index++,
            type: 'nav'
          });
          if (list[i].data.getBadgeCountDataStore) {
            navBadgeCountDataStores.push(list[i].data.getBadgeCountDataStore);
          }
          if (list[i].children && list[i].children.length) {
            navElements(list[i].children, index);
          }
        }
      };
      navElements(nav, 1);
    }

    for (let i = 0, len = navBadgeCountDataStores.length; i < len; i++) {
      elements.push(await this.getDataStoreFunctionElement(ctx, navBadgeCountDataStores[i]));
    }

    if ('4' == json.configuration.navType && json.configuration.navDataStoreId) {
      elements.push(await this.getDataStoreFunctionElement(ctx, json.configuration.navDataStoreId));
    }

    return elements;
  }
}

module.exports = WLeftSidebar;
