'use strict';

const Controller = require('wellapp-framework').Controller;
const lodash = require('lodash');
const DMS = require('../constant').DMS;

class DmsController extends Controller {
  async dmsDataService() {
    const { ctx, app } = this;
    try {
      let url = app.config.backendURL + '/dms/data/services';
      if (ctx.request.method === 'POST') {
        url += '?';
        // POST 为数据操作类的数据返回请求，需要拼接查询参数到Url上提供后端服务解析
        for (const k in ctx.query) {
          url += k + '=' + ctx.query[k] + '&';
        }
      }
      let result;
      try {
        result = await ctx.curl(url, {
          method: ctx.request.method,
          contentType: 'json',
          dataType: 'json',
          data: ctx.request.method === 'GET' ? ctx.query : ctx.request.body,
          headers: {
            Host_From_NodeServer: true // 提供后端识别请求标记
          }
        });
      } catch (error) {
        app.logger.error('数据管理服务请求异常：%s', error);
        if (ctx.request.method === 'GET') {
          await ctx.render('/error/500');
          return;
        }
        ctx.body = {};
        return;
      }

      if (ctx.request.method === 'GET') {
        let dmsWidgetType = ctx.query.dms_id ? ctx.query.dms_id.substring(0, ctx.query.dms_id.indexOf('_')) : 'wDataManagementViewer';
        if (dmsWidgetType === 'wFileManager') {
          dmsWidgetType = 'wDataManagementViewer';
        }
        // GET 为数据操作类的页面返回请求
        const loadjs = [result.data.documentViewModule].concat(DMS[dmsWidgetType].js);
        if (app.config.paceDisabled === true) {
          //pace加载进度条，默认开启；存在部分项目组不想要的情况
          let _i = loadjs.indexOf('pace');
          if (_i != -1) {
            loadjs.splice(_i, 1);
          }
        }
        const definitionJs = await ctx.service.dyformDefinitionService.explainJavascripts(result.data.formUuid, loadjs);
        const requirejsConfig = ctx.helper.createRequirejsConfig(definitionJs.javascripts);
        // 样式
        const css = await ctx.service.webResourceService.getThemeCss(
          DMS[dmsWidgetType].css.concat(result.data.documentViewCss ? [result.data.documentViewCss] : []),
          true
        );

        const defaultValue = ctx.query.default;

        await ctx.render(
          DMS[dmsWidgetType].viewPath,
          Object.assign(
            {
              requirejsConfigJSON: JSON.stringify(requirejsConfig),
              javascriptsJson: JSON.stringify(Array.from(definitionJs.jsAdded)),
              css,
              defaultValue,
              paceDisabled: app.config.paceDisabled === true //pace加载进度条，默认开启；存在部分项目组不想要的情况
            },
            result.data
          )
        );
      } else {
        ctx.body = result.data;
        ctx.status = result.status;
      }
    } catch (error) {
      app.logger.error('数据管理服务请求异常：%s', error);
    }
    // Host_From_NodeServer
  }

  async getFolderTree() {
    const { ctx, app } = this;
    try {
      ctx.body = await ctx.curl(app.config.backendURL + '/dms/file/manager/component/load/folder/tree', {
        method: 'POST',
        contentType: 'form',
        dataType: 'json',
        data: ctx.request.body
      });
    } catch (error) {
      app.logger.error('文件树数据管理服务请求异常：%s', error);
    }
  }

  async fileServices() {
    const { ctx, app } = this;
    try {
      ctx.body = await ctx.curl(app.config.backendURL + '/dms/file/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
    } catch (error) {
      app.logger.error('文件数据管理服务请求异常：%s', error);
    }
  }

  async fileUpload() {
    const { ctx, app } = this;
    try {
      ctx.body = await ctx.curl(app.config.backendURL + '/api/dms/file/upload', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
    } catch (error) {
      app.logger.error('文件数据管理服务请求异常：%s', error);
    }
  }

  async dmsFileViewer() {
    const { ctx, app } = this;

    var data = {
      fileUuid: ctx.params.id,
      viewFileUrl: ctx.originalUrl.split('viewFileUrl=')[1]
    };

    await ctx.render('file-manager/file-viewer.js', { fileData: data });
  }

  async saveOrUpdateConfig() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/dms/doc/exc/config/saveOrUpdate', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('业务交换弹窗数据保存请求异常：%s', error);
    }
  }

  async queryListConfig() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/dms/doc/exc/config/queryList', {
        method: 'get',
        contentType: 'json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('业务交换列表数据请求异常：%s', error);
    }
  }

  async sequenceConfig() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/dms/doc/exc/config/sequence', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.status = result.status;
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('业务交换列表数据排序请求异常：%s', error);
    }
  }

  async delConfig() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/dms/doc/exc/config/del', {
        method: 'post',
        // contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('业务交换列表删除请求异常：%s', error);
    }
  }

  async getOneConfig() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/dms/doc/exc/config/getOne', {
        method: 'get',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('业务交换获取单条数据请求异常：%s', error);
    }
  }

  async queryBusinessCategorys() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/dms/doc/exc/config/queryBusinessCategorys', {
        method: 'get',
        contentType: 'json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('业务交换获取单条数据请求异常：%s', error);
    }
  }

  async queryBusinessRoles() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/dms/doc/exc/config/queryBusinessRoles', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('业务交换获取单条数据请求异常：%s', error);
    }
  }

  async queryEvents() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/dms/doc/exc/config/queryEvents', {
        method: 'get',
        contentType: 'json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('业务交换获取单条数据请求异常：%s', error);
    }
  }

  async dyformGetOne() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/dms/doc/exc/dyform/getOne', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('业务交换获取单条数据请求异常：%s', error);
    }
  }

  async dyformSaveOrUpdate() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/dms/doc/exc/dyform/saveOrUpdate', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('业务交换获取单条数据请求异常：%s', error);
    }
  }

  async configIsRead() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/dms/doc/exc/config/isRead', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('业务交换获取单条数据请求异常：%s', error);
    }
  }

  async configRecordRead() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/dms/doc/exc/config/recordRead', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('业务交换获取单条数据请求异常：%s', error);
    }
  }

  async queryBusinessCategorOrgs() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/dms/doc/exc/config/queryBusinessCategorOrgs', {
        method: 'get',
        contentType: 'json',
        dataType: 'json',
        data: ctx.query
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('业务交换获取单条数据请求异常：%s', error);
    }
  }

  async getPrintTemplateTreeByFolderUuids() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/dms/printtemplate/getPrintTemplateTreeByFolderUuids', {
        method: 'post',
        contentType: 'json',
        dataType: 'json',
        data: ctx.request.body
      });
      ctx.body = result.data;
    } catch (error) {
      app.logger.error('业务交换获取单条数据请求异常：%s', error);
    }
  }

  async dataModelDesign() {
    let { uuid, id } = this.ctx.query;
    await this.ctx.render('module-center/data-model-design.js', { uuid, id });
  }
}

module.exports = DmsController;
