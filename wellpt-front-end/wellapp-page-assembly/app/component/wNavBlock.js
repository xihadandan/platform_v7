'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 导航块组件的解析类
 */
class WNavBlock extends Component {
  constructor() {
    super();
    this.name = '导航块组件';
    this.category = 'app';
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.appType = 'techComponent';
    this.componentType = 'unit';
    this.scope = ['wPage', 'wLayoutit']; // 组件默认生效页面类型:wPage
  }

  async getFunctionElements(ctx, json) {
    if (!json || !json.configuration) {
      return [];
    }

    let nav = json.configuration.nav;
    let badgeTypeCountDsArr = [];
    const elements = [];
    if (nav && nav.length) {
      let navElements = function (list, index) {
        for (let i = 0, len = list.length; i < len; i++) {
          // 忽略根结点
          if (index != 0) {
            elements.push({
              uuid: list[i].id,
              id: list[i].id,
              name: '自定义导航块_' + list[i].name,
              code: 'nav_block_' + ++index,
              type: 'nav'
            });
          } else {
            index++;
          }
          if (list[i].data.badgeTypeCountDs) {
            badgeTypeCountDsArr.push(list[i].data.badgeTypeCountDs);
          }
          if (list[i].children && list[i].children.length) {
            navElements(list[i].children, index);
          }
        }
      };
      navElements(nav, 0);
    }

    for (let i = 0, len = badgeTypeCountDsArr.length; i < len; i++) {
      elements.push(await this.getDataStoreFunctionElement(ctx, badgeTypeCountDsArr[i]));
    }

    return elements;
  }
}

module.exports = WNavBlock;
