'use strict';

const Service = require('wellapp-framework').Service;
const lodash = require('lodash');
const _constant = require('../constant')._constant;

class DyformDefinitionService extends Service {
  async getPreservedField() {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/definition/getPreservedField', {
        method: 'GET',
        contentType: 'application/json',
        dataType: 'json'
      });
      if (result.data) {
        return result.data;
      }
    } catch (error) {
      app.logger.error('查询表单保留字段异常：%s', error);
    }
  }

  async getFormDefinition(uuid, justDataAndDef) {
    try {
      const { app, ctx } = this;
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/definition/getFormDefinition', {
        method: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: { formUuid: uuid, justDataAndDef: justDataAndDef }
      });
      if (result.data) {
        return result.data;
      }
    } catch (error) {
      app.logger.error('查询表单定义异常：%s', error);
    }
  }

  async getFormDefinitionByUuid(uuid) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/definition/getFormDefinitionByUuid', {
        method: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: { formUuid: uuid }
      });
      if (result.data) {
        return result.data;
      }
    } catch (error) {
      app.logger.error('查询表单定义异常：%s', error);
    }
  }

  async getFormDefinitionJSON(uuid) {
    const result = await this.ctx.curl(this.app.config.backendURL + '/json/data/services', {
      method: 'POST',
      contentType: 'json',
      dataType: 'json',
      data: {
        serviceName: 'formDefinitionService',
        methodName: 'getFormDefinitionJSONByUuid',
        version: '',
        args: JSON.stringify([uuid])
      }
    });
    if (result.data && result.data.data) {
      return JSON.parse(result.data.data);
    }
    return null;
  }

  async getFormFieldDefinition(uuid) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          serviceName: 'dyFormFacade',
          methodName: 'getFormFieldDefintion',
          version: '',
          args: JSON.stringify([uuid])
        }
      });
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('查询表单字段定义异常：%s', error);
    }
    return null;
  }

  async save(data, isUpdate) {
    const { app, ctx } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/definition/' + (isUpdate ? 'update' : 'save'), {
        method: 'POST',
        headers: {
          'content-type': 'application/x-www-form-urlencoded;charset=utf-8',
        },
        dataType: 'json',
        data
      });
      if (result.data) {
        return result.data;
      }
    } catch (error) {
      app.logger.error('保存更新表单定义异常：%s', error);
    }
  }

  async deleteAll(ids) {
    try {
      const { app, ctx } = this;
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/field/deleteAll', {
        method: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: { ids }
      });
      if (result.data) {
        return result.data;
      }
    } catch (error) {
      app.logger.error('删除表单定义异常：%s', error);
    }
  }

  async explainJavascripts(formUuid, extras, formDefinitionJSON) {
    const app = this.app;
    const javascripts = [];
    const jsAdded = new Set();
    const inputMode2Js = _constant.dyformControlInputModeJs;
    const formDefinition = formDefinitionJSON ? formDefinitionJSON : await this.getFormDefinitionJSON(formUuid);
    const pushJs = function (i) {
      if (!jsAdded.has(i) && app.jsPack[i]) {
        if (!app.jsPack[i].abstract) {
          jsAdded.add(i);
          javascripts.push(app.jsPack[i]);
        }
        if (app.jsPack[i].dependencies && app.jsPack[i].dependencies.length) {
          app.jsPack[i].dependencies.forEach(function (d) {
            if (!jsAdded.has(d)) {
              javascripts.push(app.jsPack[d]);
              jsAdded.add(d);
            }
          });
        }
      }
    };

    if (formDefinition) {
      if (formDefinition.fields) {
        // 解析表单字段依赖的脚本定义
        lodash.forEach(formDefinition.fields, function (f, key) {
          if (inputMode2Js[f.inputMode]) {
            inputMode2Js[f.inputMode].split(',').forEach(i => {
              pushJs(i);
            });
          }
        });
      }

      if (formDefinition.fileLibrary) {
        pushJs('wFormFileLibrary');
      }
      if (formDefinition.tableView) {
        pushJs('wTableView');
      }

      // 从表字段解析
      if (formDefinition.subforms) {
        pushJs('wSubForm');
        lodash.forEach(formDefinition.subforms, function (s, k) {
          if (s.isGroupShowTitle === '1') {
            pushJs('wSubForm4Group');
          }
          lodash.forEach(s.fields, function (f, key) {
            if (inputMode2Js[f.inputMode]) {
              inputMode2Js[f.inputMode].split(',').forEach(i => {
                pushJs(i);
              });
            }
          });
        });
      }

      // 加载页签
      if (!lodash.isEmpty(formDefinition.layouts) || !lodash.isEmpty(formDefinition.blocks)) {
        pushJs('wContainerManager');
      }

      // 表单二开脚本
      if (formDefinition.customJsModule) {
        pushJs(formDefinition.customJsModule);
      }
    }

    // 其他依赖脚本添加
    (extras || [])
      .concat([
        'formDefinitionMethod',
        'DyformFunction',
        'DyformDevelopment',
        'wControlManager',
        'DyformExplain',
        'dyform_explain',
        'bootstrap',
        'jquery-ui',
        'appContext',
        'appWindowManager',
        'wCommonDialog',
        'appModal',
        'appDispatcher',
        'wWidget',
        'dependencyJQueryUIModule',
        'uuid'
      ])
      .forEach(i => {
        pushJs(i);
      });

    return {
      jsAdded,
      javascripts
    };
  }

  async explainJsIdsByDyformUuid(formUuid) {
    const { ctx, app } = this;
    const inputMode2Js = _constant.dyformControlInputModeJs;
    const formDef = await this.getFormDefinition(formUuid);
    const customJsModule = await formDef.customJsModule;
    const js = [
      'wTableView',
      'wFormFileLibrary',
      'wSubForm4Group',
      'wSubForm',
      'wContainerManager',
      'formDefinitionMethod',
      'DyformFunction',
      'DyformDevelopment',
      'wControlManager',
      'DyformExplain',
      'dyform_explain',
      'bootstrap',
      'jquery-ui',
      'appContext',
      'appWindowManager',
      'wCommonDialog',
      'appModal',
      'appDispatcher',
      'wWidget',
      'dependencyJQueryUIModule',
      'uuid'
    ];
    if (customJsModule) {
      js.push(customJsModule);
    }
    // const fields = await this.getFormFieldDefinition(formUuid);
    // const js = new Set();
    // if (fields) {
    //   fields && fields.forEach(f => {
    //     if (!js.has(inputMode2Js[f.inputMode]) && inputMode2Js[f.inputMode] && app.jsPack[inputMode2Js[f.inputMode]]) {
    //       js.add(inputMode2Js[f.inputMode]);
    //     }
    //   });
    // } else {
    //   for (const k in inputMode2Js) {
    //     js.add(inputMode2Js[k]);
    //   }
    // }

    for (const k in inputMode2Js) {
      inputMode2Js[k].split(',').forEach(element => {
        js.push(element);
      });
    }

    return new Set(js);
  }
}

module.exports = DyformDefinitionService;
