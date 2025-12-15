'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 磁贴组件的解析类
 */
class WTiles extends Component {
  constructor() {
    super();
    this.name = '磁贴组件';
    this.category = 'app';
    this.css = ['wTiles', 'minicolors'];
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.appType = 'techComponent';
    this.componentType = 'unit';
    this.scope = ['wPage', 'wLayoutit']; // 组件默认生效页面类型:wPage
  }

  async explainDefinitionJson(ctx, json) {
    let configuration = json.configuration;
    if (!configuration || !configuration.tiles || configuration.tiles.length == 0) {
      return;
    }
    // let timing = new Timing();
    // timing.start('wTiles explainDefinitionJson');
    let checkGrantedTiles = [];
    for (let i = 0, len = configuration.tiles.length; i < len; i++) {
      let tile = configuration.tiles[i];
      if (tile.eventHandler && tile.eventHandler.id) {
        checkGrantedTiles.push({
          object: tile.eventHandler,
          functionType: 'appProductIntegration'
        });
      }
    }
    let result = await this.grantedFilter(ctx, checkGrantedTiles);

    for (let i = configuration.tiles.length - 1; i >= 0; i--) {
      let tile = configuration.tiles[i];
      if (tile.eventHandler && tile.eventHandler.id) {
        if (!result[tile.eventHandler.id]) {
          // 事件处理没有权限
          configuration.tiles.splice(i, 1);
          continue;
        }
      }
    }
  }
}

module.exports = WTiles;
