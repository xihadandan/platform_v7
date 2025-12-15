'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 九宫格组件的解析类
 */
class WMobileTiles extends Component {
  constructor() {
    super();
    this.name = '磁贴';
    this.css = ['select2', 'bootstrap-datetimepicker'];
    this.category = 'app';
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.scope = ['wMobilePage']; // 组件默认生效页面类型:wPage
  }
}

module.exports = WMobileTiles;
