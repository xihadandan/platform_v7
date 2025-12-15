'use strict';

const Service = require('wellapp-framework').Service;
const is = require('is-type-of');
const cheerio = require('cheerio');
const {
  v1: uuidv1
} = require('uuid');
class PageDefinitionService extends Service {
  async getByUuid(uuid) {
    const {
      app,
      ctx
    } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          serviceName: 'appPageDefinitionMgr',
          methodName: 'getBean',
          version: '',
          args: JSON.stringify([uuid])
        }
      });
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('页面定义请求异常：%s', error);
    }
    return null;
  }


  async getById(id) {
    const {
      app,
      ctx
    } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          serviceName: 'appPageDefinitionService',
          methodName: 'getLatestPageDefinition',
          version: '',
          args: JSON.stringify([id])
        }
      });
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('页面定义请求异常：%s', error);
    }
    return null;
  }

  async getPageUuidVersionById(id) {
    const {
      app,
      ctx
    } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          serviceName: 'appPageDefinitionService',
          methodName: 'getLatestUuidAndVersion',
          version: '',
          args: JSON.stringify([id])
        }
      });
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('页面定义请求异常：%s', error);
    }
    return null;
  }

  async authenticatedPageDefinition(uuid, id) {
    const {
      app,
      ctx
    } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/webapp/authenticatePage?uuid=${uuid || ''}&id=${id}`, {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        headers: {
          debug: ctx.isDebug // 是否调试模式
        }
      });

      if (result.data.code === 0) {
        return result.data.data;
      } else if (result.data.code == -7000) {
        throw new Error('403');
      }
      return result.data.data;

    } catch (error) {
      app.logger.error('页面定义请求异常：%s', error);
      throw error;
    }
    return null;
  }


  async prodVersionAuthenticatePage(prodVersionUuid, prodId) {
    const {
      app,
      ctx
    } = this;
    try {
      const result = await ctx.curl(`${app.config.backendURL}/webapp/prodVersionAuthenticatePage`, {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        dataAsQueryString: true,
        data: {
          prodVersionUuid, prodId
        }
      });

      if (result.data.code === 0) {
        return result.data.data;
      } else if (result.data.code == -7000) {
        throw new Error('403');
      }
      return result.data.data;

    } catch (error) {
      app.logger.error('页面定义请求异常：%s', error);
      throw error;
    }
    return null;
  }


  async getPageDefinition(appPiPath, puuid) {
    const {
      app,
      ctx
    } = this;
    let result = null;
    try {
      result = await ctx.curl(app.config.backendURL + '/webapp/get', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          appPiPath: appPiPath,
          pageUuid: puuid
        },
        headers: {
          debug: ctx.isDebug // 是否调试模式
        }
      });
    } catch (error) {
      throw error;
    }
    if (result.data) {
      if (result.data.code === 200) {
        return result.data.data;
      }
      throw new Error(result.data.code);
    }
  }

  async getAuthenticatedPageDefintions() {
    const {
      app,
      ctx
    } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/json/data/services', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          serviceName: 'appPageDefinitionMgr',
          methodName: 'listFacade'
        }
      });
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('授权页面请求异常：%s', error);
    }
    return null;
  }

  async getUserAppData(piPath) {
    const {
      app,
      ctx
    } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/webapp/getUserAppData', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          appPiPath: piPath
        }
      });
      if (result.data && result.data.code === 200) {
        return result.data.data.currentUserAppData;
      }
    } catch (error) {
      app.logger.error('%s', error);
    }
  }

  async explainJavascriptCssByDefinitonJson(definition, extraJs, extraCss) {
    const {
      app,
      ctx
    } = this;
    const widgetTypes = new Set();
    const widgetDefJSON = {};
    this.getWidgetTypes(definition, widgetTypes, widgetDefJSON); // 解析出里面的组件类型
    let isMobile = this.ctx.req.query.isMobileApp === 'true';
    if (widgetTypes.has('wMobilePage')) {
      // 移动版本页面
      isMobile = true;
    }

    const cssAdded = new Set(),
      jsAdded = new Set(),
      requireJsLoad = new Set(),
      widgetDone = new Set();
    const css = [],
      javascripts = [];
    if (!widgetTypes || widgetTypes.length === 0) {
      return {
        css,
        javascripts,
        cssAdded,
        jsAdded
      };
    }
    const _this = this;
    let types = Array.from(widgetTypes);

    const beforeExplainDefJsonPromise = [];
    for (let i = 0, len = types.length; i < len; i++) {
      if (app.component[types[i]] && is.function(app.component[types[i]].beforeExplainDefinitionJson) &&
        widgetDefJSON[types[i]]) {
        for (let j = 0, jlen = widgetDefJSON[types[i]].length; j < jlen; j++) {
          beforeExplainDefJsonPromise.push(app.component[types[i]].beforeExplainDefinitionJson(ctx, widgetDefJSON[
            types[i]][j]));
        }
      }
    }
    await Promise.all(beforeExplainDefJsonPromise);

    const explainDefJsonPromise = [];
    for (let i = 0, len = types.length; i < len; i++) {
      if (
        !ctx.isDebug && // 非调试模式下，需要解析json的权限
        app.component[types[i]] &&
        is.function(app.component[types[i]].explainDefinitionJson) &&
        widgetDefJSON[types[i]]
      ) {
        // 定义解析
        for (let j = 0, jlen = widgetDefJSON[types[i]].length; j < jlen; j++) {
          explainDefJsonPromise.push(app.component[types[i]].explainDefinitionJson(ctx, widgetDefJSON[types[i]][j]));
        }
      }
    }

    widgetTypes.forEach(function (id) {
      const _component = app.component[id]; // 是否是组件

      if (_component) {
        widgetDefJSON[id];
        if (widgetDone.has(id)) {
          return;
        }
        widgetDone.add(id);
        // 添加组件关联的样式文件
        let compcss = null;
        if (_component.explainCss) {
          if (is.function(_component.explainCss)) {
            compcss = [];
            for (let w = 0; widgetDefJSON[id] && w < widgetDefJSON[id].length; w++) {
              compcss = compcss.concat(_component.explainCss(ctx, widgetDefJSON[id][w]));
            }
          } else {
            compcss = _component.explainCss;
          }
        } else if (_component.css && _component.css.length) {
          compcss = _component.css;
        }
        if (compcss) {
          compcss.forEach(function (v) {
            if (!cssAdded.has(v)) {
              css.push(app.cssPack[v]);
              cssAdded.add(v);
            }
          });
        }

        // 添加组件的关联的js文件
        if (_component.explainJs) {
          let expJs = null;
          if (is.function(_component.explainJs)) {
            expJs = [];
            for (let w = 0; widgetDefJSON[id] && w < widgetDefJSON[id].length; w++) {
              expJs = expJs.concat(_component.explainJs(ctx, widgetDefJSON[id][w].configuration));
            }
          } else {
            expJs = _component.explainJs;
          }
          [].concat(expJs).forEach(function (v) {
            _this.explainJs(v, jsAdded, javascripts, isMobile, requireJsLoad, false);
          });
        }
        _this.explainJs(id, jsAdded, javascripts, isMobile, requireJsLoad, false);
      }
    });

    if (isMobile && definition.javascriptModules) {
      definition.javascriptModules.forEach(function (v) {
        _this.explainJs(v, jsAdded, javascripts, isMobile, null, false);
      });
    }

    await Promise.all(explainDefJsonPromise);
    let definitionJson = JSON.stringify(definition);
    return {
      css,
      javascripts,
      cssAdded,
      jsAdded,
      requireJsLoad,
      isMobile,
      definitionJson
    };
  }

  explainJs(id, jsAdded, javascripts, isMobile, requireJsLoad, resolveDep) {
    const {
      app
    } = this;
    const _this = this;
    if (!jsAdded.has(id) && (app.mobileJsPack[id] || this.app.jsPack[id])) {
      const targetPack = isMobile && app.mobileJsPack[id] ? app.mobileJsPack[id] : app.jsPack[id];
      javascripts.push(targetPack);
      jsAdded.add(id);
      if (requireJsLoad) {
        requireJsLoad.add(id);
      }
      if (isMobile && app.mobileJsPack[id] && app.mobileJsPack[id].alias) {
        javascripts.push(app.mobileJsPack[app.mobileJsPack[id].alias]);
        jsAdded.add(app.mobileJsPack[id].alias);
        if (requireJsLoad) {
          requireJsLoad.add(app.mobileJsPack[id].alias);
          requireJsLoad.delete(id);
        }
      }
      if (targetPack.dependencies) {
        for (let t = 0, tlen = targetPack.dependencies.length; t < tlen; t++) {
          if (resolveDep) {
            this.explainJs(targetPack.dependencies[t], jsAdded, javascripts, isMobile, requireJsLoad, resolveDep);
          } else {
            let _id = targetPack.dependencies[t];
            const _targetPack = isMobile && app.mobileJsPack[_id] ? app.mobileJsPack[_id] : app.jsPack[_id];
            javascripts.push(_targetPack);
            jsAdded.add(_id);
            if (requireJsLoad) {
              requireJsLoad.add(_id);
            }
          }
        }
      }
    }
  }

  getWidgetTypes(definition, widgetTypes, widgetDefJson) {
    widgetTypes.add(definition.wtype);
    if (definition.items && definition.items.length) {
      for (let i = 0; i < definition.items.length; i++) {
        widgetTypes.add(definition.items[i].wtype);
        if (!widgetDefJson[definition.items[i].wtype]) {
          widgetDefJson[definition.items[i].wtype] = [];
        }
        widgetDefJson[definition.items[i].wtype].push(definition.items[i]);
        this.getWidgetTypes(definition.items[i], widgetTypes, widgetDefJson);
      }
    }
  }

  async getLoginUserGrantedPageUrl() {
    const {
      app,
      ctx
    } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/webapp/getLoginUserGrantedPageUrl', {
        method: 'GET',
        contentType: 'json',
        dataType: 'text'
      });
      if (result.data) {
        return result.data;
      }
    } catch (error) {
      throw new Error(error);
    }
    return null;
  }

  async getAppHashTreeByAppPath(appPath) {
    const {
      app,
      ctx
    } = this;
    let widgetDefJson = await ctx.service.jsonDataService._call('appPageDefinitionMgr',
      'getWidgetDefinitionByAppPath', [appPath]);
    let widgetJSON = JSON.parse(widgetDefJson);

    const treeNode = {};
    let buildTree = async function (widget, node) {
      if (widget) {
        node.name = widget.title;
        node.id = widget.id;
        node.nocheck = true;
        node.children = [];

        if (widget.items && widget.items.length) {
          for (let i = 0, len = widget.items.length; i < len; i++) {
            let childNode = {};
            await buildTree(widget.items[i], childNode);
            if (childNode.children && childNode.children.length) {
              node.children.push(childNode);
            }
          }
        }

        if (app.component[widget.wtype] && is.function(app.component[widget.wtype].getFunctionElements)) {
          let elements = await app.component[widget.wtype].getFunctionElements(ctx, widget);
          for (let i = 0, len = elements.length; i < len; i++) {
            if (['menu', 'nav', 'tab'].indexOf(elements[i].type) != -1) {
              node.children.push({
                name: elements[i].name,
                id: '/' + widget.id + '/' + elements[i].id
              });
            }
          }
        }
      }
    };

    await buildTree(widgetJSON, treeNode);
    return {
      code: 0,
      data: treeNode
    };
  }

  async getWidgetFunctionElements(json) {
    let widgetJson = is.string(json) ? JSON.parse(json) : json;
    const {
      ctx,
      app
    } = this;
    const widgetFunctionElements = {};
    let getWidgetFunElements = async function (widget) {
      if (app.component[widget.wtype] && is.function(app.component[widget.wtype].getFunctionElements)) {
        widgetFunctionElements[widget.id] = await app.component[widget.wtype].getFunctionElements(ctx, widget);
      }
      if (widget.items && widget.items.length) {
        for (let i = 0, len = widget.items.length; i < len; i++) {
          await getWidgetFunElements(widget.items[i]);
        }
      }
    };

    await getWidgetFunElements(widgetJson);
    return widgetFunctionElements;
  }

  async copyPageDefinition(appPiUuid, copyPageUuid, copyPageId) {
    const {
      ctx,
      app
    } = this;
    try {
      let sourcePageDefintion = await ctx.service.jsonDataService._call('appPageDefinitionService', 'get', [
        copyPageUuid
      ]);
      let {
        name,
        title,
        code,
        wtype,
        theme,
        userId,
        isDefault,
        remark,
        definitionJson,
        html
      } = sourcePageDefintion;
      if (definitionJson && html) {
        // 修改定义属性
        const $ = cheerio.load(html);
        let widgetJson = JSON.parse(definitionJson);
        let getChildWidgets = function (_parentWidget) {
          let items = [_parentWidget];
          if (_parentWidget.items && _parentWidget.items.length) {
            for (let i = 0, len = _parentWidget.items.length; i < len; i++) {
              items = items.concat(getChildWidgets(_parentWidget.items[i]));
            }
          }
          return items;
        };
        const allWidgets = getChildWidgets(widgetJson);
        let ifRefrenceWidget = function (_w) {
          for (let i = 0, len = allWidgets.length; i < len; i++) {
            if (allWidgets[i].refWidgetDefUuid && allWidgets[i].id != _w.id) {
              let _childW = getChildWidgets(allWidgets[i]);
              for (let j = 0, jlen = _childW.length; j < jlen; j++) {
                if (_childW[j].id == _w.id) {
                  return true;
                }
              }
            }
          }
          return false;
        };
        var oldIdMap = new Map();
        let widgetIdRenew = function (widget) {
          if (ifRefrenceWidget(widget)) {
            return;
          }
          let _id = widget.id,
            newid = widget.wtype + '_' + uuidv1().replace(/-/g, '').toUpperCase();
          oldIdMap.set(_id, newid);
          $('#' + _id).attr('id', newid);
          widget.id = newid;
          delete widget.uuid;
          if (widget.items && widget.items.length) {
            for (let i = 0, len = widget.items.length; i < len; i++) {
              widgetIdRenew(widget.items[i]);
            }
          }
        };
        widgetIdRenew(widgetJson);

        let oldIdRenew = function (widget) {
          if (widget.configuration && widget.configuration.view) {
            for (let item in widget.configuration.view) {
              let item_val = widget.configuration.view[item];
              if (item_val && oldIdMap.has(item_val)) {
                widget.configuration.view[item] = oldIdMap.get(item_val);
              }
            }
          }
          if (widget.items && widget.items.length) {
            for (let i = 0, len = widget.items.length; i < len; i++) {
              oldIdRenew(widget.items[i]);
            }
          }
        };
        oldIdRenew(widgetJson);
        html = $.html();
        widgetJson.html = html;
        definitionJson = JSON.stringify(widgetJson);
      }
      return await this.saveDefinitionJson(appPiUuid, definitionJson, false, name + '(复制)', wtype, copyPageId);
    } catch (error) {
      app.logger.error('复制页面请求异常：%s', error);
    }
  }

  async savePageDefinition(pageDefinition, functionElements, appWidgetDefinitionElements) {
    const {
      ctx,
      app
    } = this;
    try {
      pageDefinition.functionElements = functionElements || (await this.getWidgetFunctionElements(pageDefinition
        .definitionJson));
      pageDefinition.appWidgetDefinitionElements = appWidgetDefinitionElements;
      const result = await ctx.curl(app.config.backendURL + '/api/webapp/page/definition/savePageDefinition', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: pageDefinition
      });
      if (pageDefinition.piUuid) {
        let appProd = await ctx.service.appProdIntegrationService.getByUuid(pageDefinition.piUuid);
        this.clearPageCacheData(appProd.dataPath, result.data.data);
      }
      return result.data || null;
    } catch (error) {
      app.logger.error('授权页面请求异常：%s', error);
    }
  }

  async saveDefinitionJson(uuid, piUuid, definitionJson, newVersion, name, type, id, functionElements,
    appWidgetDefinitionElements) {
    const {
      ctx,
      app
    } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/webapp/page/definition/savePageDefinition', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: {
          uuid,
          piUuid,
          definitionJson,
          newVersion,
          functionElements: functionElements || (await this.getWidgetFunctionElements(definitionJson)),
          appWidgetDefinitionElements,
          name,
          id
        }
      });
      if (piUuid) {
        let appProd = await ctx.service.appProdIntegrationService.getByUuid(piUuid);
        this.clearPageCacheData(appProd.dataPath, result.data.data);
      }
      return result.data || null;
    } catch (error) {
      app.logger.error('授权页面请求异常：%s', error);
    }
  }

  async getPageCacheData(appPiPath, pageUuid) {
    const {
      ctx,
      app
    } = this;
    if (app.config.env === 'prod' && ctx.user && app.redis) {
      //生产环境开启页面缓存
      let data = await app.redis.get(`webapp:page:${ctx.user.userId}:${appPiPath}:${pageUuid}`);
      return JSON.parse(data);
    }
    return null;
  }

  async clearPageCacheData(appPiPath, pageUuid, expireSeconds) {
    const {
      ctx,
      app
    } = this;
    if (app.config.env === 'prod' && ctx.user && app.redis) {
      let keyPattern = 'webapp:page:*';
      if (appPiPath) {
        keyPattern += appPiPath + '*';
      }
      if (pageUuid) {
        keyPattern += pageUuid;
      }
      app.redis.keys(keyPattern, (err, keys) => {
        if (keys.length) {
          if (expireSeconds != undefined) {
            for (let i = 0, len = keys.length; i < len; i++) {
              app.redis.expire(keys[i], expireSeconds);
            }
          } else {
            app.redis.del(keys);
          }
        }
      });
    }
  }

  async clearUserPageCacheData() {
    const {
      ctx,
      app
    } = this;
    if (app.config.env === 'prod' && ctx.user && app.redis) {
      let keyPattern = `webapp:page:${ctx.user.userId}:*`;
      app.redis.keys(keyPattern, (err, keys) => {
        if (keys.length) {
          app.redis.del(keys);
        }
      });
    }
  }

  async setPageCacheData(appPiPath, pageUuid, data) {
    const {
      ctx,
      app
    } = this;
    if (app.config.env === 'prod' && ctx.user && app.redis) {
      //生产环境开启页面缓存
      app.redis.set(`webapp:page:${ctx.user.userId}:${appPiPath}:${pageUuid}`, JSON.stringify(data));
    }
  }

  async getWidgetById(id, from) {
    const {
      app,
      ctx
    } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/app/widget/getWidgetById', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          id,
          appPageId: from,
          appPageUuid: from
        }
      });
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('组件请求异常：%s', error);
    }
    return null;
  }


  async getProdVersionPageUuid(pageId, prodVersionUuid) {
    const {
      app,
      ctx
    } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/app/prod/version/pageUuid', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          pageId, prodVersionUuid
        }
      });
      if (result.data && result.data.code === 0) {
        return result.data.data;
      }
    } catch (error) {
      app.logger.error('组件请求异常：%s', error);
    }
    return null;
  }
}

module.exports = PageDefinitionService;
