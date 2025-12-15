'use strict';

let Component = require('wellapp-framework').Component;
/**
 * 表格组件的解析类
 */
class WBootstrapTable extends Component {
  constructor() {
    super();
    this.name = '表格组件';
    this.css = [
      'select2',
      'bootstrapTable',
      'bootstrap-editable',
      'bootstrap-datetimepicker',
      'bootstrap-table-label-mark',
      'bootstrap-table-fixed-columns',
      'wellBtnLib',
      'minicolors'
    ];
    this.category = 'app';
    this.supportsWysiwyg = true; // 默认组件支持设计所见即所得
    this.previewHtml = '<div></div>'; // 预览html
    this.configurable = true; // 默认可编辑
    this.enable = true; // 是否启用
    this.appType = 'techComponent';
    this.componentType = 'unit';
    this.scope = ['wPage', 'wLayoutit']; // 组件默认生效页面类型:wPage
  }

  /**
   * 解析依赖脚本
   * @param {请求上下文} ctx
   * @param {组件定义configuration} configuration
   */
  explainJs(ctx, configuration) {
    let js = [];
    if (configuration) {
      let ismsie = ctx.helper.isUserAgentOfMsie();
      if (ismsie || (configuration.treeGrid && configuration.treeGrid.enableTreegrid)) {
        js.push('bootstrap-table-treegrid');
      }
      if (ismsie || (configuration.cardGrid && configuration.cardGrid.enableCardgrid)) {
        js.push('bootstrap-table-cardgrid');
      }
    }
    return js;
  }

  explainCss(ctx, configuration) {
    return this.css;
  }

  async beforeExplainDefinitionJson(ctx, json) {
    let configuration = json.configuration;
    // 加载用户自定义的配置数据
    let tablePref = await ctx.service.userPreferenceService.getPreferences({ dataKey: json.id, moduleId: 'BOOTSTRAPTABLE' });
    if (tablePref && tablePref.data && tablePref.data.dataValue) {
      configuration.customConfiguration = JSON.parse(tablePref.data.dataValue);
    }
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
      if (btn.uuid) {
        checkGrantedButtons.push({
          object: btn.uuid + json.id,
          functionType: 'AppWidgetFunctionElement',
          md5hex: true
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
      if (!hasCheckGranted && btn.uuid) {
        if (!result[btn.uuid + json.id]) {
          configuration.buttons.splice(i, 1);
          continue;
        }
      }
    }
  }

  async getFunctionElements(ctx, json) {
    if (!json || !json.configuration) {
      return [];
    }
    let elements = [];

    // 数据仓库
    if (json.configuration.dataStoreId) {
      elements.push(await this.getDataStoreFunctionElement(ctx, json.configuration.dataStoreId));
    }

    // 按钮
    let buttons = json.configuration.buttons;
    if (buttons) {
      for (let i = 0, len = buttons.length; i < len; i++) {
        elements.push({
          uuid: buttons[i].uuid,
          id: buttons[i].code,
          name: '按钮_' + buttons[i].text,
          code: 'button_' + (i + 1)
        });
      }
    }

    // 筛选出查询条件内数据仓库、数据字典
    if (json.configuration.query && json.configuration.query.fields) {
      let dataStores = [],
        dataDics = [];
      for (let i = 0, len = json.configuration.query.fields.length; i < len; i++) {
        let f = json.configuration.query.fields[i];
        if (f.queryOptions) {
          if (f.queryOptions.dataStore) {
            dataStores.push(f.queryOptions.dataStore);
          }
          if (f.queryOptions.dataDic) {
            dataDics.push(f.queryOptions.dataDic);
          }
        }
      }
      for (let i = 0, len = dataStores.length; i < len; i++) {
        let _element = await this.getDataStoreFunctionElement(ctx, dataStores[i]);
        if (_element) {
          elements = this.pushElements(elements, _element);
        }
      }
      for (let i = 0, len = dataDics.length; i < len; i++) {
        let _element = await this.getDataDictionaryFunctionElement(ctx, dataDics[i]);
        if (_element) {
          elements = this.pushElements(elements, _element);
        }
      }
    }

    return elements;
  }
}

module.exports = WBootstrapTable;
