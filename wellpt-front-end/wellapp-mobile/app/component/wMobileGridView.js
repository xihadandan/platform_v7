'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 九宫格组件的解析类
 */
class WMobileGridView extends Component {
  constructor() {
    super();
    this.name = '九宫图';
    this.css = ['select2', 'bootstrap-datetimepicker', 'fullcalendar-resourceview', 'fullcalendar'];
    this.category = 'app';
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.scope = ['wMobilePage']; // 组件默认生效页面类型:wPage
  }

  async explainDefinitionJson(ctx, json) {
    let configuration = json.configuration;
    if (!configuration.columns || configuration.columns.length == 0) {
      return;
    }

    let checkGrantedColumns = [];
    for (let i = 0, len = configuration.columns.length; i < len; i++) {
      let cln = configuration.columns[i];
      if (cln.appUuid || (cln.eventHandler && cln.eventHandler.id)) {
        checkGrantedColumns.push({
          object: cln.appUuid || cln.eventHandler.id,
          functionType: 'appProductIntegration'
        });
      }
    }
    let result = await this.grantedFilter(ctx, checkGrantedColumns);

    for (let i = configuration.columns.length - 1; i >= 0; i--) {
      let cln = configuration.columns[i];
      if (cln.appUuid || (cln.eventHandler && cln.eventHandler.id)) {
        if (!result[cln.appUuid || cln.eventHandler.id]) {
          configuration.columns.splice(i, 1);
          continue;
        }
      }
    }
  }
}

module.exports = WMobileGridView;
