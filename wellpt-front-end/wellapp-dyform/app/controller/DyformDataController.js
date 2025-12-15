'use strict';

const Controller = require('wellapp-framework').Controller;
const FormStream = require('formstream');

const DYFORMDATA = {
  wDataViewer: {
    js: [
      'pace',
      'cmsWindown',
      'bootstrap',
      'appContext',
      'select2',
      'select2_locale_zh-cn',
      'appModal',
      'dataStoreBase',
      'slimScroll',
      'niceScroll',
      'server',
      'summernote-zh-CN',
      'summernote',
      'appModal',
      'wWidget',
      'uuid'
    ],
    css: []
  }
};
class DyformDataController extends Controller {
  async getFormDefinitionData() {
    try {
      const { app, ctx } = this;
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/data/getFormDefinitionData', {
        method: 'POST',
        contentType: 'form',
        dataType: 'json',
        data: ctx.req.body
      });
      if (result.data) {
        this.ctx.body = result.data;
      }
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async validateFormData() {
    try {
      const { app, ctx } = this;
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/data/validateFormData', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      if (result.data) {
        this.ctx.body = result.data;
      }
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async saveFormData() {
    try {
      const { app, ctx } = this;
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/data/saveFormData', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      if (result.data) {
        this.ctx.body = result.data;
      }
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async uploadImage() {
    const { ctx, app } = this;
    try {
      const form = new FormStream();
      let files = ctx.request.files;
      for (let i = 0; i < files.length; i++) {
        form.file(files[i].field, files[i].filepath, files[i].filename);
      }
      let url = app.config.backendURL + '/pt/dyform/data/uploadImage?CKEditorFuncNum=';
      let { CKEditorFuncNum, folderId } = ctx.query;
      if (CKEditorFuncNum != undefined) {
        url += CKEditorFuncNum;
      }
      if (folderId != undefined) {
        url += '&folderId=' + folderId;
      }
      const result = await ctx.curl(url, {
        method: 'POST',
        contentType: 'multipart/form-data',
        stream: form,
        headers: form.headers(),
        dataType: 'json'
      });
      let filePath = '/pt/dyform/data/downloadImage?uuid=' + result.data.fileid;
      let alterMsg = '上传成功';
      ctx.body =
        "<script type='text/javascript'>window.parent.CKEDITOR.tools.callFunction(" +
        CKEditorFuncNum +
        ", '" +
        filePath +
        "', '" +
        alterMsg +
        "');</script>";
    } catch (error) {
      app.logger.error('保存登录页配置接口异常：%s', error);
    }
  }

  async downloadImage() {
    this.ctx.redirect2backend(this.ctx.req.url);
  }

  async uploadFile() {
    const { ctx, app } = this;
    try {
      const form = new FormStream();
      let files = ctx.request.files;
      for (let i = 0; i < files.length; i++) {
        form.file(files[i].field, files[i].filepath, files[i].filename);
      }
      let url = app.config.backendURL + '/pt/dyform/data/uploadFile?CKEditorFuncNum=';
      let { CKEditorFuncNum, folderId } = ctx.query;
      if (CKEditorFuncNum != undefined) {
        url += CKEditorFuncNum;
      }
      if (folderId != undefined) {
        url += '&folderId=' + folderId;
      }
      const result = await ctx.curl(url, {
        method: 'POST',
        contentType: 'multipart/form-data',
        stream: form,
        headers: form.headers(),
        dataType: 'json'
      });
      let file_ext = result.data.fileName.substring(result.data.fileName.lastIndexOf('.'));
      let filePath = '/pt/dyform/data/downloadFile/' + result.data.fileid + file_ext;
      let alterMsg = '上传成功';
      ctx.body =
        "<script type='text/javascript'>window.parent.CKEDITOR.tools.callFunction(" +
        CKEditorFuncNum +
        ", '" +
        filePath +
        "', '" +
        alterMsg +
        "');</script>";
    } catch (error) {
      app.logger.error('保存登录页配置接口异常：%s', error);
    }
  }

  async downloadFile() {
    this.ctx.redirect2backend(this.ctx.req.url);
  }
  async getDyformTitle() {
    try {
      const { app, ctx } = this;
      let { formUuid, dataUuid } = ctx.query;
      const result = await ctx.curl(
        app.config.backendURL + '/pt/dyform/data/getDyformTitle?formUuid=' + formUuid + '&dataUuid=' + dataUuid,
        {
          method: 'POST',
          contentType: 'json',
          dataType: 'json',
          data: ctx.req.body
        }
      );
      if (result.data) {
        this.ctx.body = result.data;
      }
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async dynamicQuery() {
    try {
      const { app, ctx } = this;
      const { helper } = ctx;
      const result = await ctx.curl(app.config.backendURL + '/pt/dyform/data/dynamicQuery', {
        headers: {
          'content-type': 'application/x-www-form-urlencoded'
        },
        method: 'POST',
        dataType: 'json',
        content: helper.jQueryParam(ctx.req.body)
      });
      if (result.data) {
        this.ctx.body = result.data;
      }
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async dyformDataViewer() {
    const { ctx, app } = this;
    let dataUuid = ctx.query.dataUuid;
    let isEdit = ctx.query.isEdit;
    if (!dataUuid || dataUuid == '') {
      const dyformDefinition = await ctx.service.dyformDefinitionService.getFormDefinition(ctx.query.uuid);
      if (dyformDefinition) {
        moduleId = dyformDefinition.moduleId;
        formType = dyformDefinition.formType;
      }
    }
    if (!isEdit || isEdit == '') {
      isEdit = false;
    }
    let data = {
      formUuid: ctx.query.formUuid,
      isEdit: isEdit,
      dataUuid: dataUuid,
      title: '表单数据查看器',
      isSubForm: ctx.query.isSubForm
    };
    const loadjs = DYFORMDATA['wDataViewer'].js;
    const definitionJs = await ctx.service.dyformDefinitionService.explainJavascripts(ctx.query.formUuid, loadjs);
    const requirejsConfig = ctx.helper.createRequirejsConfig(definitionJs.javascripts);

    // 样式
    const css = await ctx.service.webResourceService.getThemeCss(DYFORMDATA['wDataViewer'].css, true);

    await this.ctx.render(
      '/dyform/app_dyform_data_viewer.nj',
      Object.assign(
        {
          requirejsConfigJSON: JSON.stringify(requirejsConfig),
          javascriptsJson: JSON.stringify(Array.from(definitionJs.jsAdded)),
          css
        },
        data
      )
    );
  }

  async updateFieldValue() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/pt/dyform/data/updateFieldValue`, {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async getDictByType() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/basicdata/datadict/type/` + ctx.params.type, {
        method: 'get',
        contentType: 'application/json',
        dataType: 'json',
        dataAsQueryString: true
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async dialogAutoComplete() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/pt/dyform/data/autoComplete`, {
        method: 'post',
        dataType: 'json',
        data: ctx.req.body
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }

  async getFieldDictionaryOptionSet() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/pt/dyform/definition/getFieldDictionaryOptionSet`, {
        method: 'post',
        dataType: 'json',
        data: ctx.req.body,
        contentType: 'json'
      });
      this.ctx.body = result.data;
    } catch (error) {
      this.app.logger.error(error);
    }
  }
}

module.exports = DyformDataController;
