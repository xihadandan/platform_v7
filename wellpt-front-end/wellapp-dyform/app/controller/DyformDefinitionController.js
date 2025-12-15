'use strict';

const Controller = require('wellapp-framework').Controller;

class DyformDefinitionController extends Controller {
  async design(iscopy) {
    const { ctx, app } = this;
    var preservedFields = await ctx.service.dyformDefinitionService.getPreservedField();
    let moduleId = ctx.query.moduleId;
    let formType = ctx.query.formType || 'P';
    if (ctx.query.uuid) {
      const dyformDefinition = await ctx.service.dyformDefinitionService.getFormDefinition(ctx.query.uuid, false);
      if (dyformDefinition) {
        moduleId = dyformDefinition.moduleId;
        formType = dyformDefinition.formType;
      }
    } else {
      //解析地址上的formType
      let urlparts = ctx.req.url.split('/');
      let index = urlparts[urlparts.length - 1].indexOf('form-designer');
      if (index !== 0) {
        formType = urlparts[urlparts.length - 1].substring(0, index).toUpperCase();
      }
    }
    let data = {
      formType,
      formUuid: ctx.query.uuid,
      moduleId,
      preservedFieldsJSON: JSON.stringify(preservedFields)
    };
    if (ctx.query.iscopy) {
      data.formCopy = data.formUuid;
    }

    data.themeFiles = await ctx.service.userThemeService.getThemeFiles();

    await this.ctx.render('/dyform/dyform_form_designer.nj', data);
  }

  async formCopy() {
    this.ctx.query.iscopy = true;
    await this.design();
  }
  async save() {
    const { ctx, app } = this;
    ctx.body = await ctx.service.dyformDefinitionService.save(
      ctx.request.body,
      ctx.request.url.indexOf('/pt/dyform/definition/update') !== -1
    );
  }

  async getFormDefinition() {
    const { ctx, app } = this;
    let justDataAndDef = ctx.request.body.justDataAndDef;
    justDataAndDef = !(justDataAndDef === false || justDataAndDef === 'false');
    ctx.body = await ctx.service.dyformDefinitionService.getFormDefinition(ctx.request.body.formUuid, justDataAndDef);
  }

  // 重定向旧的控件图片地址到新路径
  async placeHolderJpg() {
    this.ctx.redirect(
      global.STATIC_PREFIX + '/dyform/definition/ckeditor/plugins/' + this.ctx.params.controlType + '/images/placeHolder.jpg'
    );
  }

  async list() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/definition/list', {
        method: 'POST',
        contentType: 'form',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (e) {
      app.logger.error('查询表单定义列表异常：%s', e);
    }
  }

  async fileListConfigList() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/fileListConfig/list', {
        method: 'GET',
        contentType: 'form',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (e) {
      app.logger.error('查询表单附件控件配置异常：%s', e);
    }
  }

  async preview() {
    let { dataUuid, formUuid } = this.ctx.query;
    let formDefinition = this.ctx.req.body.formDefinition;
    let formDefinitionJSON = formDefinition || 'undefined';

    let js = [
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
      'niceScroll',
      'AppTabs',
      'server',
      'DmsDocumentViewDevelopment',
      'jquery-dmsDocumentView',
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
      'uuid'
    ];

    let dyformDefinition = formDefinition
      ? JSON.parse(formDefinition)
      : await this.ctx.service.dyformDefinitionService.getFormDefinition(formUuid);
    if (dyformDefinition && dyformDefinition.customJsModule) {
      js.push(dyformDefinition.customJsModule);
    }
    const definitionJs = await this.ctx.service.dyformDefinitionService.explainJavascripts(formUuid, js, dyformDefinition);
    const requirejsConfig = this.ctx.helper.createRequirejsConfig(definitionJs.javascripts);

    // 样式
    const css = await this.ctx.service.webResourceService.getThemeCss([], true);

    await this.ctx.render('/dyform/dyform_form_preivew.nj', {
      dataUuid,
      formUuid,
      formDefinitionJSON,
      requirejsConfigJSON: JSON.stringify(requirejsConfig),
      javascriptsJson: JSON.stringify(Array.from(definitionJs.jsAdded)),
      css
    });
  }

  async mobilePreview() {
    let css = ['mui', 'mui-dtpicker', 'mui-listpicker', 'mui-app', 'mui-app-uxd'];
    let js = ['mui-DyformPreview', 'mui', 'mui-listpicker', 'app-adapter', 'i18n'];
    // 样式
    css = await this.ctx.service.webResourceService.getThemeCss(css, true);
    const requirejsConfig = await this.ctx.service.webResourceService.getJavascriptRequirejsConfig(js, true);
    let { uuid, flag } = this.ctx.query;
    await this.ctx.render('/dyform/mobile_dyform_designer_preview.nj', {
      uuid,
      flag,
      requirejsConfigJSON: JSON.stringify(requirejsConfig),
      css
    });
  }

  async vueDyformDefinitionDesign() {
    const { ctx, app } = this;
    const jsModule = {};
    // const designType = ctx.req.url.startsWith('/uni-dyform-designer') ? 'mobileDyform' : 'dyform';
    let activeTabKey = '1'; // 激活第一个tab项
    let definition = {
      id: null,
      uuid: null,
      formType: 'P'
    };

    if (ctx.req.query.uuid) {
      // TODO: 获取表单定义
      let formDefinition = await ctx.service.dyformDefinitionService.getFormDefinitionByUuid(ctx.req.query.uuid);
      definition = formDefinition;
      if (definition == undefined) {
        await ctx.renderError('404')
        return;
      }
      if (definition.customJsModule) {
        jsModule.key = definition.customJsModule;
      }
      activeTabKey = '2';

      // 复制表单定义
      if (definition && ctx.req.query.copy === 'true') {
        delete definition.uuid;
        delete definition.id;
        delete definition.tableName;
      }
    }
    // 保留字
    var preservedFields = await ctx.service.dyformDefinitionService.getPreservedField();

    await ctx.render('dyform-designer/definition.js', {
      dataModelUuid: ctx.params.dataModelUuid || null,
      definition,
      activeTabKey,
      jsModule,
      h5Server: ctx.app.config.h5Server,
      preservedFields: JSON.parse(JSON.stringify(preservedFields || []).toLowerCase())
    });
  }

  async dataModelDyform() {
    const { ctx, app } = this;
    const jsModule = {};
    let activeTabKey = '1'; // 激活第一个tab项
    let definition = {
      id: null,
      uuid: null,
      formType: 'V'
    };

    if (ctx.req.query.uuid) {
      // TODO: 获取表单定义
      let formDefinition = await ctx.service.dyformDefinitionService.getFormDefinitionByUuid(ctx.req.query.uuid);
      definition = formDefinition;
      if (definition.customJsModule) {
        jsModule.key = definition.customJsModule;
      }
      activeTabKey = '2';
    }
    // 保留字
    var preservedFields = await ctx.service.dyformDefinitionService.getPreservedField();

    await ctx.render('dyform-designer/definition.js', {
      dataModelUuid: ctx.params.dataModelUuid,
      definition,
      activeTabKey,
      jsModule,
      preservedFields: JSON.parse(JSON.stringify(preservedFields || []).toLowerCase())
    });
  }

  async getFormDefinitionByUuid() {
    this.ctx.body = await this.ctx.service.dyformDefinitionService.getFormDefinitionByUuid(this.ctx.req.query.uuid);
  }

  async vuePageDesignPreview() {
    let { uuid, dataUuid } = this.ctx.query,
      definitionVjson = null;
    if (uuid) {
      let formDefinition = await this.ctx.service.dyformDefinitionService.getFormDefinitionByUuid(uuid);
      definitionVjson = JSON.parse(formDefinition.definitionVjson);
    }
    // widgetLocale
    // let locale = require('.widgetLocale/' + this.ctx.localeVariable + ".json");
    await this.ctx.render('dyform-designer/preview.js', {
      domain: this.ctx.app.config.domain,
      h5Server: this.ctx.app.config.h5Server, definitionVjson, dataUuid, uuid
      // widgetLocale: locale
    });
  }
}

module.exports = DyformDefinitionController;
