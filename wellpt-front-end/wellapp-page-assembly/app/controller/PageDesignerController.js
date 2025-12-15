'use strict';

const Controller = require('wellapp-framework').Controller;
const lodash = require('lodash');
const _constant = require('../constant');
const qs = require('qs');

/**
 * 页面设计器控制层
 */
class PageDesignController extends Controller {
  async index() {
    const piUuid = this.ctx.params.piUuid;
    let pageUuid = this.ctx.query.pageUuid;
    const appProd = await this.ctx.service.appProdIntegrationService.getByUuid(piUuid);
    if (appProd && appProd.appPageReference) {
      throw new Error('不能对引用的页面进行配置');
    }
    if (!pageUuid) {
      pageUuid = appProd.appPageUuid;
    }
    let pageType;
    let pageDefinition;
    if (pageUuid) {
      pageDefinition = await this.ctx.service.pageDefinitionService.getByUuid(pageUuid);
      if (pageDefinition) {
        pageType = pageDefinition.wtype || this.ctx.query.pageWtype;
      }
    }
    if (pageType == 'vPage') {
      await this.vuePageDesigner({
        pageDefinition,
        productUuid: appProd.appProductUuid
      });
      return;
    }

    if (pageType == 'vUniPage') {
      await this.vueUniPageDesigner({
        pageDefinition,
        productUuid: appProd.appProductUuid
      });
      return;
    }

    if (pageType === 'wLayoutit') {
      // TODO: layoutit
      await this.ctx.render(
        '/design/app_page_config_new.nj',
        await this.defaultDesignContainer({
          piUuid,
          pageUuid,
          pageType,
          appProd,
          pageDefinition
        })
      );
      return;
    }
    await this.ctx.render(
      '/design/app_page_config.nj',
      await this.defaultDesignContainer({
        piUuid,
        pageUuid,
        pageType,
        appProd,
        pageDefinition
      })
    );
  }

  async defaultDesignContainer(options) {
    const { app, ctx } = this;
    const pageComponent = app.component[options.pageType];
    if (!pageComponent) {
      throw new Error('不识别的页面类型');
    }
    let javascripts = [];
    let css = [];
    let jsAdded = new Set();
    let cssAdded = new Set();

    // 添加页面设计器脚本依赖
    jsAdded.add('AppPageDesigner');
    jsAdded.add(pageComponent.defineJs);
    javascripts.push(app.jsPack.AppPageDesigner);
    javascripts.push(app.jsPack[pageComponent.defineJs]);
    []
      .concat(app.jsPack.AppPageDesigner.dependencies)
      .concat(app.jsPack[pageComponent.defineJs].dependencies)
      .forEach(function (d) {
        if (app.jsPack[d] && !jsAdded.has(d)) {
          javascripts.push(app.jsPack[d]);
          jsAdded.add(d);
        }
      });

    // 加载组件的定义配置脚本依赖
    for (const a in app.component) {
      if (
        app.component[a].scope &&
        (app.component[a].scope === options.pageType || app.component[a].scope.indexOf(options.pageType) !== -1)
      ) {
        if (!jsAdded.has(app.component[a].defineJs)) {
          jsAdded.add(app.component[a].defineJs);
          const defineJs = app.jsPack[app.component[a].defineJs];
          if (defineJs) {
            javascripts.push(defineJs);
            if (defineJs.dependencies && defineJs.dependencies.length) {
              for (let i = 0; i < defineJs.dependencies.length; i++) {
                if (app.jsPack[defineJs.dependencies[i]] && !jsAdded.has(defineJs.dependencies[i])) {
                  javascripts.push(app.jsPack[defineJs.dependencies[i]]);
                  jsAdded.add(defineJs.dependencies[i]);
                }
              }
            }
          }
        }
      }
    }

    // 加载组件解析的脚本依赖
    if (options.pageDefinition && options.pageDefinition.definitionJson && pageComponent.supportsWysiwyg) {
      const definition = JSON.parse(options.pageDefinition.definitionJson);
      const definitionRelaRes = await ctx.service.pageDefinitionService.explainJavascriptCssByDefinitonJson(definition);
      javascripts = javascripts.concat(definitionRelaRes.javascripts);
      css = css.concat(definitionRelaRes.css);
      jsAdded = new Set(Array.from(jsAdded).concat(Array.from(definitionRelaRes.jsAdded))); // 合并去重
      cssAdded = new Set(Array.from(cssAdded).concat(Array.from(definitionRelaRes.cssAdded))); // 合并去重
    }

    // 组件排序
    const usageFrequency = await ctx.service.pageComponentService.getWidgetUsageFrequency();
    const allComponent = (function () {
      const temp = {};
      for (const c in _constant.componentCategory) {
        temp[_constant.componentCategory[c].name] = [];
      }
      return temp;
    })();
    const newAllComponent = {
      layout: {
        grid: {
          name: '栅格布局',
          list: []
        },
        specific: {
          name: '特定布局',
          list: []
        }
      },
      component: {
        techComponent: {
          name: '技术中台组件',
          unit: {
            name: '元组件',
            list: []
          },
          report: {
            name: '报表组件',
            list: []
          }
        },
        businessComponent: {
          name: '业务中台组件'
        },
        dataComponent: {
          name: '数据中台组件'
        }
      }
    };
    const componentDefaultOptions = {};

    const allowComponentTypes = [options.pageType];
    // 组件分类/排序
    for (const c in app.component) {
      const component = app.component[c];
      const category = app.component[c].category;
      if (
        (app.component[c].scope === options.pageType || app.component[c].scope.indexOf(options.pageType) !== -1) &&
        _constant.componentCategory[category]
      ) {
        //新版布局/组件分类
        if (options.pageType === 'wLayoutit') {
          //代表是布局
          if (component.layoutType) {
            newAllComponent.layout[component.layoutType].list.push({
              name: app.component[c].name,
              defineJs: app.component[c].defineJs,
              useTimes: usageFrequency[c] || 0,
              id: c,
              wtype: c,
              items: [],
              configurable: app.component[c].configurable,
              previewHtml: app.component[c].previewHtml,
              layout: true
            });
          } else if (component.appType) {
            newAllComponent.component[component.appType][component.componentType].list.push({
              name: app.component[c].name,
              defineJs: app.component[c].defineJs,
              useTimes: usageFrequency[c] || 0,
              id: c,
              wtype: c,
              configurable: app.component[c].configurable,
              previewHtml: app.component[c].previewHtml
            });
          }
        }

        componentDefaultOptions[c] = app.component[c].defaultOptions;
        allComponent[_constant.componentCategory[category].name].push({
          name: app.component[c].name,
          defineJs: app.component[c].defineJs,
          useTimes: usageFrequency[c] || 0,
          id: c,
          configurable: app.component[c].configurable,
          previewHtml: app.component[c].previewHtml
        });
        allowComponentTypes.push(c);
      }
    }

    // 页面容器组件的样式加载
    allowComponentTypes.forEach(c => {
      // 加载组件定义的css
      if (app.component[c].css && app.component[c].css.length) {
        app.component[c].css.forEach(c => {
          if (app.cssPack[c] && !cssAdded.has(c)) {
            css.push(app.cssPack[c]);
            cssAdded.add(c);
          }
        });
      }
    });

    for (const ac in allComponent) {
      // 按使用频率排序
      allComponent[ac] = lodash.orderBy(allComponent[ac], 'useTimes', 'desc');
      //组件列表为空时不显示类别
      if (!allComponent[ac].length) {
        delete allComponent[ac];
      }
    }

    newAllComponent.layout.grid.list = lodash.orderBy(newAllComponent.layout.grid.list, 'useTimes', 'desc');
    newAllComponent.layout.specific.list = lodash.orderBy(newAllComponent.layout.specific.list, 'useTimes', 'desc');
    newAllComponent.component.techComponent.unit.list = lodash.orderBy(
      newAllComponent.component.techComponent.unit.list,
      'useTimes',
      'desc'
    );
    newAllComponent.component.techComponent.report.list = lodash.orderBy(
      newAllComponent.component.techComponent.report.list,
      'useTimes',
      'desc'
    );

    const userAppData = await ctx.service.pageDefinitionService.getUserAppData(options.appProd.dataPath);
    const javascriptsJson = JSON.stringify(Array.from(jsAdded));

    const themeCss = await ctx.service.webResourceService.getThemeCss(css, true);

    return {
      requirejsConfigJson: JSON.stringify(ctx.helper.createRequirejsConfig(javascripts)),
      title: options.appProd.dataName,
      pageUuid: options.pageUuid,
      piUuid: options.piUuid,
      componentMap: allComponent,
      componentMapJson: JSON.stringify(allComponent),
      newAllComponentMapJson: JSON.stringify(newAllComponent),
      containerSupportsWysiwyg: false, //pageComponent.supportsWysiwyg,
      cssOrderd: lodash.orderBy(themeCss, ['order'], ['asc']),
      userAppDataJson: JSON.stringify(userAppData),
      systemUnitId: ctx.user.systemUnitId,
      javascriptsJson,
      containerJsModule: pageComponent.defineJs,
      containerDefaultOptionsJson: JSON.stringify(pageComponent.defaultOptions),
      componentDefaultOptionsJson: JSON.stringify(componentDefaultOptions)
    };
  }

  async componentConfigPage() {
    await this.ctx.render('/design/component/' + this.ctx.params.page + '.nj');
  }

  async saveDefinitionJson() {
    let body = this.ctx.req.body;
    await this.ctx.service.pageDefinitionService.saveDefinitionJson(
      body.uuid,
      body.piUuid,
      body.definitionJson,
      body.newVersion === true,
      null,
      null,
      null,
      body.functionElements,
      body.appWidgetDefinitionElements
    );
    this.ctx.body = 'success';
  }

  async savePageDefinition() {
    let body = this.ctx.req.body;
    let result = await this.ctx.service.pageDefinitionService.savePageDefinition(
      body,
      body.functionElements,
      body.appWidgetDefinitionElements
    );
    this.ctx.body = result;
  }

  async vuePageDesigner(options) {
    const { ctx, app } = this;
    let pageDefinition = options.pageDefinition || {},
      prodVersionUuid = null;
    if (ctx.query.uuid) {
      pageDefinition = await this.ctx.service.pageDefinitionService.getByUuid(ctx.query.uuid);
    } else if (ctx.query.id) {
      pageDefinition = await this.ctx.service.pageDefinitionService.getPageUuidVersionById(ctx.query.id);
      ctx.redirect('/page-designer/index?uuid=' + pageDefinition.uuid);
      return;
    }
    if (pageDefinition == null) {
      await ctx.renderError('404', '页面设计不存在');
      return;
    }
    if (pageDefinition.system == pageDefinition.appId) {
      if (pageDefinition.tenant) {
        // 是系统的首页，要重定向到首页地址进行设计
        ctx.redirect(`/index-designer/${pageDefinition.appId}?uuid=${pageDefinition.uuid}`);
        return;
      }
      const result = await ctx.curl(app.config.backendURL + '/api/app/prod/version/queryLatestCreate', {
        method: 'POST',
        contentType: 'json',
        dataType: 'json',
        data: [pageDefinition.appId]
      });
      if (result.data && result.data.code === 0 && result.data.data && result.data.data.length) {
        // 是产品版本设计的首页，要重定向到首页地址进行设计
        ctx.redirect(`/index-designer/${pageDefinition.appId}/${result.data.data[0].uuid}?uuid=${pageDefinition.uuid}`);
        return;
      }
    }
    await ctx.render('page-designer/index.js', {
      prodVersionUuid,
      pageDefinition,
      functionWidgetDesign: false,
      domain: this.ctx.app.config.domain,
      h5Server: this.ctx.app.config.h5Server,
      uniDesign: ctx.path.startsWith('/uni-page-designer'),
      productUuid: options.productUuid || ctx.query.productUuid
    });
  }
  async vueBigScreenPageDesigner() {
    const { ctx, app } = this;
    await ctx.render('page-designer/big-screen-designer.js', {
      pageDefinition: {}
    });
  }

  async indexPageDesigner() {
    const { ctx, app } = this;
    let { prodId, prodVersionUuid } = ctx.params;
    let pageDefinition = {};
    if (ctx.query.uuid) {
      pageDefinition = await this.ctx.service.pageDefinitionService.getByUuid(ctx.query.uuid);
    }
    let systemInfo = null;
    if (prodVersionUuid == undefined) {
      // 系统首页设计
      systemInfo = await ctx.service.appProdIntegrationService.getTenantSystemInfo(null, prodId);
      if (systemInfo) {
        prodVersionUuid = systemInfo.prodVersionUuid;
      }
    }

    await ctx.render('page-designer/index.js', {
      title: '首页设计',
      systemInfo,
      isIndexDesign: true,
      pageDefinition,
      functionWidgetDesign: false,
      h5Server: ctx.app.config.h5Server,
      prodVersionUuid
    });
  }

  async indexPageDesignerPreview() {
    const { ctx, app } = this;
    let versionSetting = {},
      themeJson = undefined,
      layoutConf = undefined;
    let { prodId, prodVersionUuid, id } = ctx.params;
    if (prodVersionUuid != undefined) {
      versionSetting = await this.ctx.service.appProdIntegrationService.getProdVersionSetting(ctx.params.prodVersionUuid);
      if (versionSetting.uuid) {
        if (versionSetting.theme) {
          let themeData = JSON.parse(versionSetting.theme).pc,
            defaultTheme = themeData.defaultTheme,
            defaultColor = undefined,
            colorClass = undefined,
            classScope = [];
          for (let t of themeData.theme) {
            classScope.push(t.themeClass);
            if (t.themeClass == defaultTheme) {
              for (let color of t.color) {
                if (color.default) {
                  defaultColor = color.value;
                  colorClass = color.colorClass;
                  break;
                }
              }
            }
          }
          themeJson = { class: defaultTheme, color: defaultColor, colorClass, classScope };
        }
        layoutConf = versionSetting.layoutConf ? JSON.parse(versionSetting.layoutConf) : undefined;
      }
    } else {
      ctx.SYSTEM_ID = prodId;
      // 系统首页预览
      let result = await ctx.service.appProdIntegrationService.getTenantSystemPageSetting(ctx.user.tenantId, prodId);
      layoutConf = result.layoutConf ? JSON.parse(result.layoutConf) : undefined;
      if (result.themeStyle) {
        let themeStyle = JSON.parse(result.themeStyle).pc,
          _theme = themeStyle.theme,
          defaultTheme = themeStyle.defaultTheme;
        themeJson = { class: defaultTheme, classScope: [], colorClass: undefined };
        for (let i = 0, len = _theme.length; i < len; i++) {
          themeJson.classScope.push(_theme[i].themeClass);
          if (_theme[i].themeClass == defaultTheme) {
            themeJson.colorClass = _theme[i].colorClass;
          }
        }
        // 预览不加载用户设置的主题效果
        // let userTheme = await ctx.service.appProdIntegrationService.getUserPreferenceSystemTheme();
        // if (userTheme && userTheme.class != undefined) {
        //   // 用户设置了主题，且主题包有效
        //   if (userTheme.class == '' || (themeJson && themeJson.classScope && themeJson.classScope.includes(userTheme.class))) {
        //     themeJson = { ...userTheme, byUser: true, classScope: themeJson.classScope };
        //   }
        // }
      }
    }

    await ctx.render('page-designer/preview.js', {
      title: '预览',
      [prodVersionUuid == undefined ? 'SYSTEM_ID' : 'PROD_CONTEXT_PATH']:
        prodVersionUuid == undefined ? prodId : `/${prodId}/${prodVersionUuid}`,
      THEME: themeJson,
      ENVIRONMENT: { layoutConf, userThemeDefinable: true, defaultLayoutConf: layoutConf, userLayoutDefinable: true },
      domain: this.ctx.app.config.domain,
      h5Server: this.ctx.app.config.h5Server,
      // prodVersionUuid: ctx.params.prodVersionUuid,
      id: this.ctx.params.id
    });
  }

  async moduleWgtDesigner() {
    const { ctx, app } = this;
    const designWidgets = {};
    for (let k in app.vuePageDesignWidgets) {
      let category = app.vuePageDesignWidgets[k].category;
      if (designWidgets[category] == undefined) {
        designWidgets[category] = [];
      }
      if (designWidgets[category]) {
        designWidgets[category].push(
          Object.assign(
            {
              wtype: k
            },
            app.vuePageDesignWidgets[k]
          )
        );
      }
    }
    await ctx.render('page-designer/index.js', {
      title: '功能组件设计',
      designWidgets,
      pageDefinition: {},
      functionWidgetDesign: true,
      widgetUuid: ctx.params.uuid
    });
  }

  async loginDesigner() {
    await this.ctx.render('login-designer/index.js', {
      uuid: this.ctx.params.uuid
    });
  }

  async loginPreview() {
    await this.ctx.render('login-designer/preview.js', {
      uuid: this.ctx.params.uuid
    });
  }

  async vuePageDesignPreview() {
    // let _URL_STATE_ = {};
    // if (Object.keys(this.ctx.query).length) {
    //   // 解析地址的参数为对象：支持 user[code]=1&user[name]=测试
    //   _URL_STATE_ = qs.parse(this.ctx.req.url.substr(this.ctx.req.url.indexOf('?') + 1), {
    //     allowDots: true,
    //     decoder(value) {
    //       if (/^(\d+|\d*\.\d+)$/.test(value)) {
    //         // 转数字
    //         return Number(value);
    //       }
    //       let keywords = {
    //         true: true,
    //         false: false
    //       };
    //       if (value in keywords) {
    //         // 转boolean
    //         return keywords[value];
    //       }
    //       return value;
    //     }
    //   });
    // }
    await this.ctx.render('page-designer/preview.js', {
      id: this.ctx.params.id,
      domain: this.ctx.app.config.domain,
      h5Server: this.ctx.app.config.h5Server
      // _URL_STATE_
    });
  }

  async vueUniPageDesigner(options) {
    const { ctx, app } = this;
    const designWidgets = {
      // container: [],
      // basic: []
    };
    let pageDefinition = options.pageDefinition || {};
    if (ctx.query.uuid) {
      pageDefinition = await this.ctx.service.pageDefinitionService.getByUuid(ctx.query.uuid);
    }

    for (let k in app.vueUniPageDesignWidgets) {
      let category = app.vueUniPageDesignWidgets[k].category;
      if (designWidgets[category] == undefined) {
        designWidgets[category] = [];
      }
      if (designWidgets[category]) {
        designWidgets[category].push(
          Object.assign(
            {
              wtype: k
            },
            app.vueUniPageDesignWidgets[k]
          )
        );
      }
    }
    await ctx.render('uni-page-designer/index.js', {
      designWidgets,
      pageDefinition: options.pageDefinition,
      productUuid: options.productUuid || ctx.query.productUuid
    });
  }

  async pageDesignerType() {
    this.ctx.body = _constant.pageDesingerTypes;
  }
}

module.exports = PageDesignController;
