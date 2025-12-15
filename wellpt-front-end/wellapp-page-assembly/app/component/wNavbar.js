'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 导航条组件的解析类
 */
class WNavbar extends Component {
  constructor() {
    super();
    this.name = '导航条组件';
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

    const navs = [];
    const getNavs = function (navlist) {
      for (const nav of navlist) {
        const data = nav.data;

        if (data && (data.appUuid || data.eventHanlderId)) {
          navs.push({
            object: data.appUuid || data.eventHanlderId,
            functionType: 'appProductIntegration'
          });
        }

        if (nav.children && nav.children.length) {
          getNavs(nav.children);
        }
      }
    };

    getNavs(configuration.nav || []);

    let result = await this.grantedFilter(ctx, navs);

    let filterNavChildrenGranted = function (children, parent) {
      for (let i = children.length - 1; i >= 0; i--) {
        let c = children[i];
        if (c.data && (c.data.appUuid || c.data.eventHanlderId)) {
          if (!result[c.data.appUuid || c.data.eventHanlderId]) {
            children.splice(i, 1);
            continue;
          }
        }
        if (c.children && c.children.length) {
          filterNavChildrenGranted(c.children, c);
          if (c.children === undefined || c.children.length == 0) {
            delete c.children;
          }
        }
      }
      if (children.length == 0) {
        delete parent.children;
      }
    };

    for (let i = configuration.nav.length - 1; i >= 0; i--) {
      let n = configuration.nav[i];
      if (n.data && (n.data.appUuid || n.data.eventHanlderId)) {
        if (!result[n.data.appUuid || n.data.eventHanlderId]) {
          // 无权限 ，则移除
          configuration.nav.splice(i, 1);
          continue;
        }
      }
      if (n.children && n.children.length) {
        filterNavChildrenGranted(n.children, n);
        if (n.children === undefined || n.children.length == 0) {
          // 所有子导航都没有权限，则移除父导航
          configuration.nav.splice(i, 1);
        }
      }
    }
  }

  async getFunctionElements(ctx, json) {
    if (!json || !json.configuration) {
      return [];
    }

    let nav = json.configuration.nav,
      subNav = json.configuration.subNav;
    const elements = [];
    if (json.configuration.isShowCustomNavbar == 'true' && nav && nav.length) {
      let navElements = function (list, index) {
        for (let i = 0, len = list.length; i < len; i++) {
          elements.push({
            uuid: list[i].id,
            id: list[i].id,
            name: '自定义主导航_' + list[i].name,
            code: 'nav_' + index++,
            type: 'nav'
          });
          if (list[i].children && list[i].children.length) {
            navElements(list[i].children, index);
          }
        }
      };
      navElements(nav, 1);
    } else if (subNav && subNav.menuItems) {
      for (let i = 0, len = subNav.menuItems.length; i < len; i++) {
        elements.push({
          uuid: subNav.menuItems[i].uuid,
          id: subNav.menuItems[i].uuid,
          name: '主导航_' + subNav.menuItems[i].text,
          code: 'sub_nav_' + (i + 1),
          type: 'nav'
        });
      }
    }

    return elements;
  }
}

module.exports = WNavbar;
