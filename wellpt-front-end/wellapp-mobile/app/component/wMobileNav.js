'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 手机导航的解析类
 */
class WMobileNav extends Component {
  constructor() {
    super();
    this.name = '手机导航';
    this.category = 'app';
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.scope = ['wMobilePage']; // 组件默认生效页面类型:wPage
  }

  async explainDefinitionJson(ctx, json) {
    let configuration = json.configuration;
    if (!configuration || !configuration.nav || configuration.nav.length == 0) {
      return;
    }

    const navs = [];
    let getNavs = function (navlist) {
      for (let i = 0, len = navlist.length; i < len; i++) {
        let n = navlist[i];
        if (n.data && (n.data.appUuid || n.data.eventHanlderId)) {
          navs.push({
            object: n.data.appUuid || n.data.eventHanlderId,
            functionType: 'appProductIntegration'
          });
        }
      }
    };

    for (let i = 0, len = configuration.nav.length; i < len; i++) {
      let n = configuration.nav[i];
      if (n.data && (n.data.appUuid || n.data.eventHanlderId)) {
        navs.push({
          object: n.data.appUuid || n.data.eventHanlderId,
          functionType: 'appProductIntegration'
        });
      }

      if (n.children && n.children.length) {
        getNavs(n.children);
      }
    }
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
}

module.exports = WMobileNav;
