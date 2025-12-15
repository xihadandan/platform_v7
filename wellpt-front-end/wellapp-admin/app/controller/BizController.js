'use strict';

const Controller = require('wellapp-framework').Controller;

class BizController extends Controller {
  /**
   * 发起业务办件
   */
  async _newItem() {
    const { ctx, app } = this;
    const formUuid = ctx.query.formUuid;
    const dataUuid = ctx.query.dataUuid;
    try {
      const processDefId = ctx.params.id;
      const processItemIds = ctx.query.processItemIds;
      const itemBean = await ctx.service.bizProcessService.newItemById(processDefId, processItemIds);

      // 新增时传入的表单定义及数据UUID
      if (formUuid && dataUuid) {
        itemBean.formUuid = formUuid;
        itemBean.dataUuid = dataUuid;
      }
      // 流程定义ID不为空，进入流程发起页面
      if (itemBean.flowDefId) {
        this.redirectNewWorkView(itemBean);
      } else {
        await this.redirectNewItemView(itemBean);
      }
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  /**
   * 进入工作流页面
   */
  async redirectNewWorkView(itemBean) {
    const { ctx } = this;
    const processDefId = ctx.params.id;
    const processItemIds = ctx.query.processItemIds;
    // const flowBizDefId = itemBean.flowBizDefId;
    const flowDefId = itemBean.flowDefId;
    const formUuid = ctx.query.formUuid || '';
    const dataUuid = ctx.query.dataUuid || '';
    const _requestCode = ctx.query._requestCode;
    const workViewFragment = 'BizWorkViewIntegrationFragment';
    let url = `/workflow/work/new/${flowDefId}?formUuid=${formUuid}&dataUuid=${dataUuid}&ep_processDefId=${processDefId}&ep_processItemIds=${processItemIds}&ep_workViewFragment=${workViewFragment}`;
    // 附加参数
    for (const key in ctx.query) {
      if (key.startsWith('ep_')) {
        url += '&' + key + '=' + ctx.query[key];
      }
    }
    url += `&_requestCode=${_requestCode}`;
    ctx.redirect(this.addSystemPrefix(url));
  }

  addSystemPrefix(url) {
    const { ctx } = this;
    let SYSTEM_ID = ctx.SYSTEM_ID && ctx.SYSTEM_ID !== 'undefined' ? ctx.SYSTEM_ID : undefined;
    if (SYSTEM_ID && url && !url.startsWith('/sys/')) {
      return `/sys/${SYSTEM_ID}/_${url}`;
    }
    return url;
  }

  async redirectNewItemView(itemBean) {
    const { ctx } = this;
    const processDefId = ctx.params.id;
    const processItemIds = ctx.query.processItemIds;
    const formUuid = itemBean.formUuid || '';
    const dataUuid = itemBean.dataUuid || '';
    const _requestCode = ctx.query._requestCode;
    let url = '/web/app/pt-app/pt-app-biz/app_biz_item_instance.html?pageUuid=59608020677558272';
    url += `&processDefId=${processDefId}&processItemIds=${processItemIds}&formUuid=${formUuid}&dataUuid=${dataUuid}`;
    // 附加参数
    for (const key in ctx.query) {
      if (key.startsWith('ep_')) {
        url += '&' + key + '=' + ctx.query[key];
      }
    }
    url += `&_requestCode=${_requestCode}`;
    // ctx.redirect(this.addSystemPrefix(url));

    await this.itemView(itemBean);
  }

  /**
   * 查看业务办件
   */
  async viewItem() {
    const { ctx, app } = this;
    try {
      const itemInstUuid = ctx.query.itemInstUuid;
      const itemBean = await ctx.service.bizProcessService.getItemByUuid(itemInstUuid);

      await this.itemView(itemBean);
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async itemView(itemBean) {
    const { ctx, app } = this;
    await ctx.render('biz-process/item-instance-view.js', { itemData: itemBean });
  }

  /**
   * 查看过程节点办件
   */
  async viewProcessNode() {
    const { ctx, app } = this;
    try {
      const processNodeInstUuid = ctx.query.processNodeInstUuid;
      const processNodeBean = await ctx.service.bizProcessService.getProcessNodeByUuid(processNodeInstUuid);

      await this.processNodeView(processNodeBean);
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async processNodeView(processNodeBean) {
    const { ctx, app } = this;
    // const extraJs = [
    //   'jquery-dmsDocumentView',
    //   'BizProcessNodeView',
    //   'cmsWindown',
    //   'bootstrap',
    //   'appContext',
    //   'select2',
    //   'select2_locale_zh-cn',
    //   'wSelect2',
    //   'summernote-zh-CN',
    //   'summernote',
    //   'appModal',
    //   'slimScroll',
    //   'server',
    //   'niceScroll',
    //   'printArea'
    // ];

    // return this.view(processNodeBean, extraJs, '/biz/biz_process_node_view.nj');
    await ctx.render('biz-process/node-instance-view.js', { nodeData: processNodeBean });
  }

  /**
   * 查看业务流程办件
   */
  async viewProcess() {
    const { ctx, app } = this;
    try {
      const processInstUuid = ctx.query.processInstUuid;
      const processBean = await ctx.service.bizProcessService.getProcessByUuid(processInstUuid);

      await this.processView(processBean);
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async processView(processBean) {
    const { ctx, app } = this;
    // const extraJs = [
    //   'jquery-dmsDocumentView',
    //   'BizProcessView',
    //   'cmsWindown',
    //   'bootstrap',
    //   'appContext',
    //   'select2',
    //   'select2_locale_zh-cn',
    //   'wSelect2',
    //   'summernote-zh-CN',
    //   'summernote',
    //   'appModal',
    //   'slimScroll',
    //   'server',
    //   'niceScroll',
    //   'printArea'
    // ];

    // return this.view(processBean, extraJs, '/biz/biz_process_view.nj');
    await ctx.render('biz-process/process-instance-view.js', { processData: processBean });
  }

  async view(dataBean, extraJs, viewName) {
    const { ctx, app } = this;
    const definitionJs = await ctx.service.dyformDefinitionService.explainJavascripts(dataBean.formUuid, extraJs);

    const requirejsConfig = ctx.helper.createRequirejsConfig(definitionJs.javascripts);

    const skeletonJson = {
      hasRightDiv: true,
      show: true
    };

    // 样式
    const css = await ctx.service.webResourceService.getThemeCss([], true);
    await ctx.render(viewName, {
      requirejsConfigJSON: JSON.stringify(requirejsConfig),
      javascriptsJson: JSON.stringify(Array.from(definitionJs.jsAdded)),
      css,
      dataBean,
      skeletonJson,
      paceDisabled: app.config.paceDisabled === true //pace加载进度条，默认开启；存在部分项目组不想要的情况
    });
  }

  async assemble() {
    let result = await this.ctx.curl(this.app.config.backendURL + '/api/biz/process/definition/get/' + this.ctx.params.uuid, {
      method: 'get',
      contentType: 'json',
      dataType: 'json'
    });
    let processDefinition = result.data.data || {};
    let processAssemble = {};
    if (processDefinition.uuid) {
      result = await this.ctx.curl(
        this.app.config.backendURL + '/api/biz/process/assemble/getByProcessDefUuid?processDefUuid=' + this.ctx.params.uuid,
        {
          method: 'get',
          contentType: 'json',
          dataType: 'json'
        }
      );
      processAssemble = result.data.data || {};
    }
    await this.ctx.render('biz-process/assemble.js', {
      title: processDefinition.name,
      processDefUuid: this.ctx.params.uuid,
      processDefinition,
      processAssemble
    });
  }

  async desinger() {
    let result = await this.ctx.curl(this.app.config.backendURL + '/api/biz/process/definition/get/' + this.ctx.query.uuid, {
      method: 'get',
      contentType: 'json',
      dataType: 'json'
    });
    let processDefinition = result.data.data || {};
    await this.ctx.render('process-designer/index.js', {
      title: processDefinition.name,
      initProcessDefinition: processDefinition
    });
  }
}

module.exports = BizController;
