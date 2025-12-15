'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 面板组件的解析类
 */
class WPanel extends Component {
  constructor() {
    super();
    this.name = '面板组件';
    this.category = 'app';
    this.layoutType = 'specific'; // specific特定布局
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.defineJs = 'container_panel';
    this.previewHtml = '<div class="panel panel-default"><div class="panel-body ui-sortable"></div></div>';
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.scope = ['wPage', 'wLayoutit']; // 组件默认生效页面类型:wPage
  }

  async getFunctionElements(ctx, json) {
    if (!json || !json.configuration) {
      return [];
    }

    const elements = [];
    let navBadgeCountDataStores = [];
    let header = json.configuration.header,
      body = json.configuration.body;
    if (header && header.getBadgeCountDataStore) {
      elements.push(await this.getDataStoreFunctionElement(ctx, header.getBadgeCountDataStore));
    }

    if (body && body.contentType == 'contentTab' && body.tabs && body.tabs.length) {
      for (let i = 0, len = body.tabs.length; i < len; i++) {
        elements.push({
          uuid: body.tabs[i].uuid,
          id: body.tabs[i].uuid,
          name: '页签_' + body.tabs[i].name,
          code: 'tab_' + (i + 1),
          type: 'tab'
        });

        if (body.tabs[i].BadgeTypeCountDs) {
          navBadgeCountDataStores.push(body.tabs[i].BadgeTypeCountDs);
        }
      }
    }

    for (let i = 0, len = navBadgeCountDataStores.length; i < len; i++) {
      elements.push(await this.getDataStoreFunctionElement(ctx, navBadgeCountDataStores[i]));
    }

    return elements;
  }
}

module.exports = WPanel;
