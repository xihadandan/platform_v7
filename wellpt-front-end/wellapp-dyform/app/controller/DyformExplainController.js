'use strict';

const Controller = require('wellapp-framework').Controller;
const _contant = require('../constant')._constant;
const qs = require('qs');

class DyformExplainController extends Controller {
  async explainDyformRequirejs() {
    const { ctx, app } = this;
    const formUuids = ctx.request.body.formUuids;
    const requireJsModules = ctx.request.body.requireJsModules;
    const inputMode2Js = _contant.dyformControlInputModeJs;
    let javascripts = [];
    const jsAdded = new Set();
    if (formUuids.length) {
      for (let i = 0; i < formUuids.length; i++) {
        const fields = await ctx.service.dyformDefinitionService.getFormFieldDefinition(formUuids[i]);
        // eslint-disable-next-line no-loop-func
        fields.forEach(f => {
          if (inputMode2Js[f.inputMode] && !jsAdded.has(f.inputMode) && app.jsPack[inputMode2Js[f.inputMode]]) {
            javascripts = javascripts.concat(ctx.helper.explainOutJsAndDeps(inputMode2Js[f.inputMode], jsAdded, app.jsPack));
          }
        });
      }
    }
    if (requireJsModules) {
      requireJsModules.forEach(r => {
        if (!jsAdded.has(r) && app.jsPack[r]) {
          javascripts = javascripts.concat(ctx.helper.explainOutJsAndDeps(r, jsAdded, app.jsPack));
        }
      });
    }

    [
      'pace',
      'cmsWindown',
      'bootstrap',
      'appContext',
      'select2',
      'select2_locale_zh-cn',
      'layer',
      'appModal',
      'dataStoreBase',
      'slimScroll',
      'AppTabs',
      'server',
      'DmsDocumentViewDevelopment',
      'jquery-dmsDocumentView'
    ].forEach(i => {
      javascripts = javascripts.concat(ctx.helper.explainOutJsAndDeps(i, jsAdded, app.jsPack));
    });

    [
      'cmsWindown',
      'select2',
      'select2_locale_zh-cn',
      'appModal',
      'slimScroll',
      'AppTabs',
      'server',
      'DmsDocumentViewDevelopment',
      'jquery-dmsDocumentView',
      'dataStoreBase'
    ].forEach(i => {
      javascripts = javascripts.concat(ctx.helper.explainOutJsAndDeps(i, jsAdded, app.jsPack));
    });

    const requirejsConfig = ctx.helper.createRequirejsConfig(javascripts);

    ctx.body = requirejsConfig;
  }

  async justDyform() {
    const { ctx, app } = this;
    await ctx.render('dyform-viewer/just-dyform.js', { uuid: ctx.req.query.formUuid });
  }

  async dataViewer() {
    const { ctx, app } = this;
    await ctx.render('dyform-viewer/data-viewer.js', {
      formUuid: ctx.req.query.formUuid,
      dataUuid: ctx.req.query.dataUuid,
      displayState: ctx.req.query.displayState,
      showDataVersion: ctx.req.query.showDataVersion === 'true'
    });
  }

  async dmsManager() {
    const { ctx } = this;
    let { dmsId, displayState, dataUuid, formUuid: requestFormUuid, wTableId, from } = ctx.query,
      definitionVjson = null,
      formElementRules = {},
      wgt = null,
      urlParams = {};
    let formUuid = requestFormUuid;
    if (Object.keys(this.ctx.query).length) {
      // 解析地址的参数为对象：支持 user[code]=1&user[name]=测试
      urlParams = qs.parse(this.ctx.req.url.substr(this.ctx.req.url.indexOf('?') + 1), {
        allowDots: true,
        decoder(value) {
          if (/^(\d+|\d*\.\d+)$/.test(value)) {
            // 转数字
            return Number(value);
          }
          let keywords = {
            true: true,
            false: false
          };
          if (value in keywords) {
            // 转boolean
            return keywords[value];
          }
          return value;
        }
      });
    }
    // console.log('解析到地址参数: ', urlParams)

    if (dmsId) {
      let widgetDefinition = await ctx.service.pageDefinitionService.getWidgetById(dmsId, from);
      if (widgetDefinition == null) {
        await ctx.renderError('404');
        return;
      }
      wgt = JSON.parse(widgetDefinition.definitionJson);
      let { enableStateForm, labelStateFormUuid, editStateFormUuid } = wgt.configuration;
      formUuid = wgt.configuration.useRequestForm ? requestFormUuid || wgt.configuration.formUuid : wgt.configuration.formUuid;
      if (enableStateForm) {
        if (dataUuid) {
          if (displayState == undefined || displayState == 'label') {
            formUuid = labelStateFormUuid || formUuid;
          } else if (displayState == 'edit') {
            formUuid = editStateFormUuid || formUuid;
          }
        }
      }

      let ruleKey = 'formElementRules';

      if (dataUuid) {
        ruleKey = displayState === 'edit' ? 'editStateFormElementRules' : 'labelStateFormElementRules';
      }
      if (wgt.configuration[ruleKey]) {
        for (let i = 0, len = wgt.configuration[ruleKey].length; i < len; i++) {
          formElementRules[wgt.configuration[ruleKey][i].id] = {
            readonly: wgt.configuration[ruleKey][i].readonly,
            editable: wgt.configuration[ruleKey][i].editable,
            disable: wgt.configuration[ruleKey][i].disable,
            hidden: wgt.configuration[ruleKey][i].hidden,
            displayAsLabel: wgt.configuration[ruleKey][i].displayAsLabel,
            required: wgt.configuration[ruleKey][i].required === true
          };
          let children = wgt.configuration[ruleKey][i].children;
          if (children) {
            let childrenRules = {};
            formElementRules[wgt.configuration[ruleKey][i].id].children = childrenRules;
            for (let j = 0, jlen = children.length; j < jlen; j++) {
              childrenRules[children[j].id] = {
                readonly: children[j].readonly,
                editable: children[j].editable,
                disable: children[j].disable,
                hidden: children[j].hidden,
                displayAsLabel: children[j].displayAsLabel,
                required: children[j].required === true
              };
            }
          }
        }
      }
    }
    if (formUuid) {
      let formDefinition = await ctx.service.dyformDefinitionService.getFormDefinitionByUuid(formUuid);
      definitionVjson = JSON.parse(formDefinition.definitionVjson);
      if (dmsId == undefined) {
        // 当未传递 dmsId 时候，设置默认的组件配置用于展示表单数据
        wgt = {
          wtype: 'WidgetDyformSetting',
          id: 'dms_server_created' + new Date().getTime(),
          configuration: {}
        };
        if (displayState == 'edit') {
          //FIXME: 编辑的话，要考虑编辑按钮/可编辑字段等配置，需要通过确定的数据管理组件设计
        }
      }
      wgt.props = {
        // 传递属性参数
        namespace: from, // 表单设置的组件定义查询，要通过namespace(来源页面)准确定位
        dataUuid,
        formUuid,
        definitionVjson,
        displayState,
        wTableId,
        formElementRules
      }; //组件属性
      let initFormData = undefined;
      if (urlParams.eventParams && urlParams.eventParams.formData) {
        initFormData = { [formUuid]: [urlParams.eventParams.formData] };
      }
      await ctx.render('dyform-viewer/dyform-viewer.js', {
        initFormData,
        title: '表单',
        definitionVjson,
        displayState,
        dataUuid,
        widget: wgt,
        dmsId,
        wTableId,
        domain: ctx.app.config.domain,
        h5Server: ctx.app.config.h5Server
      });
      return;
    }

    await ctx.renderError('404');
  }
}

module.exports = DyformExplainController;
