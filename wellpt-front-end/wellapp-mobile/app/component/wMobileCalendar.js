'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 手机日历组件的解析类
 */
class WMobileCalendar extends Component {
  constructor() {
    super();
    this.name = '手机资源日历';
    this.css = []; // ['select2', 'bootstrap-datetimepicker', 'fullcalendar-resourceview', 'fullcalendar'];
    this.category = 'app';
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.scope = ['wMobilePage']; // 组件默认生效页面类型:wPage
  }

  async explainDefinitionJson(ctx, json) {
    let configuration = json.configuration;
    if (!configuration.buttons || configuration.buttons.length == 0) {
      return;
    }

    let checkGrantedButtons = [];
    for (let i = 0, len = configuration.buttons.length; i < len; i++) {
      let btn = configuration.buttons[i];
      if (btn.resource && btn.resource.id) {
        checkGrantedButtons.push({
          object: btn.resource.id,
          functionType: 'appProductIntegration'
        });
      }
      if (btn.eventHandler && btn.eventHandler.id) {
        checkGrantedButtons.push({
          object: btn.eventHandler.id,
          functionType: 'appProductIntegration'
        });
      }
    }
    let result = await this.grantedFilter(ctx, checkGrantedButtons);

    for (let i = configuration.buttons.length - 1; i >= 0; i--) {
      let btn = configuration.buttons[i];
      let hasCheckGranted = false;
      if (btn.resource && btn.resource.id) {
        hasCheckGranted = true;
        if (!result[btn.resource.id]) {
          configuration.buttons.splice(i, 1);
          continue;
        }
      }
      if (btn.eventHandler && btn.eventHandler.id) {
        hasCheckGranted = true;
        if (!result[btn.eventHandler.id]) {
          configuration.buttons.splice(i, 1);
          continue;
        }
      }
    }
  }
}

module.exports = WMobileCalendar;
