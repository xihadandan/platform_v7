'use strict';

const Controller = require('wellapp-framework').Controller;
// const cheerio = require('cheerio');
const lodash = require('lodash');
const qs = require('qs');
/**
 * 页面渲染解析控制层
 */
class PageRenderController extends Controller {
  async index() {
    const { ctx, app } = this;
    if (lodash.startsWith(ctx.request.url, '/web/app/page/config/')) {
      // 配置页面
      ctx.redirect(ctx.request.url.replace('/web/app/page/config/', '/web/design/config/'));
    } else if (lodash.startsWith(ctx.request.url, '/web/app/page/preview/')) {
      // 预览页面
      const piUuid = ctx.request.url.replace('/web/app/page/preview/', '').split('?')[0];
      await this.preview(piUuid, ctx.query.pageUuid);
    } else if (lodash.startsWith(ctx.request.url, '/web/app/security_homepage')) {
      // 跳转到有权限的页面
      if (ctx.user.admin || ctx.hasAnyUserRole('ROLE_TENANT_ADMIN')) {
        // 管理员
        if (app.config.adminLoginSuccessUrl) {
          ctx.redirect(app.config.adminLoginSuccessUrl);
        } else {
          ctx.redirect('/welldo/index');
          // ctx.redirect('/web/app/pt-mgr.html?pageUuid=ac525dcd-50b7-42e9-95b7-658b117ac19b');
        }
        return;
      } else if (ctx.user.superAdmin) {
        ctx.redirect('/web/app/pt-mgr.html?pageUuid=2f852b9e-4564-4f5b-bc7e-bb57f639cbe3');
        return;
      }

      let specifyWebappPageUrl = '';
      if (this.config.specifyWebappPageUrl != undefined) {
        //指定的有权限工作台地址
        specifyWebappPageUrl =
          typeof this.config.specifyWebappPageUrl == 'function' ? this.config.specifyWebappPageUrl(ctx) : this.config.specifyWebappPageUrl;
      }
      if (specifyWebappPageUrl) {
        ctx.redirect(specifyWebappPageUrl);
        return;
      }
      if (ctx.user.userSystemOrgDetails && ctx.user.userSystemOrgDetails.details && ctx.user.userSystemOrgDetails.details.length) {
        ctx.redirect('/sys/' + ctx.user.userSystemOrgDetails.details[0].system + '/index');
        return;
      }
      let authenticated = await ctx.service.pageDefinitionService.getLoginUserGrantedPageUrl();
      if (authenticated) {
        authenticated =
          lodash.startsWith(authenticated, '/webapp') || lodash.startsWith(authenticated, '/sys/')
            ? authenticated
            : (lodash.startsWith(authenticated, '/web/app/') ? '' : '/web/app/') + authenticated;
      }
      ctx.redirect(authenticated || '/error/403');
      return;
    } else {
      let appPiPath = ctx.request.url.replace('/web/app', '').split('?')[0];
      if (lodash.endsWith(appPiPath, '.html')) {
        // 渲染页面
        await this.page(appPiPath.replace('.html', ''), ctx.query.pageUuid);
        return;
      }
      ctx.redirect('/error?real_status=404');
    }
  }

  /**
   * @description 渲染CMS页面
   */
  async page(appPiPath, pageUuid) {
    const { ctx, app } = this;

    let _cachePage = await ctx.service.pageDefinitionService.getPageCacheData(appPiPath, pageUuid);
    if (_cachePage) {
      await ctx.render(!_cachePage.isMobile ? '/app.nj' : '/app_mobile.nj', _cachePage);
      return;
    }

    let timing = ctx.newTiming;
    timing.start('TotalPageRender');
    let pageDefiniton = null;
    try {
      timing.start('GetPageDefinition');
      pageDefiniton = await ctx.service.pageDefinitionService.getPageDefinition(appPiPath, pageUuid);
      app.logger.info('%s , 请求页面定义耗时: %d ms', ctx.req.url, timing.end('GetPageDefinition').duration);
      if (pageDefiniton == null) {
        await ctx.render('/error/404');
        return;
      }
    } catch (error) {
      if (error.message === '404') {
        await ctx.render('/error/404.html');
      } else if (error.message === '403') {
        await ctx.render('/error/403.html');
      } else {
        await ctx.render('/error/500.html');
      }
      return;
    }

    const { html, definitionJson, currentUserAppData, title, wtype, system } = pageDefiniton;
    const definition = JSON.parse(definitionJson);
    if (wtype === 'vPage') {
      //vue页面
      await this.ctx.render('page-render/index.js', { title, widget: definition, pageUuid, APP_SYSTEM_ID: system });
      return;
    }

    const definitionRelaRes = await ctx.service.pageDefinitionService.explainJavascriptCssByDefinitonJson(definition);

    let css = await ctx.service.webResourceService.getThemeCss(definitionRelaRes.cssAdded, !definitionRelaRes.isMobile);

    const userAppDataJson = JSON.stringify(currentUserAppData);
    const javascriptsJson = JSON.stringify(Array.from(definitionRelaRes.requireJsLoad));
    const requestInfoJson = JSON.stringify({
      scheme: ctx.protocol,
      contextPath: '',
      basePath: ctx.host,
      serverName: ctx.name
    });
    let isMobile = definition.wtype === 'wMobilePage';
    if (definition.javaScriptTemplates) {
    }

    // systemId, moduleId, appId, pageUuid 缓存
    let __data = {
      html,
      isMobile,
      javascriptsJson,
      title,
      css,
      definitionJson: JSON.stringify(definition),
      currentUserAppData,
      userAppDataJson,
      requestInfoJson
    };
    if (isMobile) {
      //手机端按需加载
      __data.requirejsConfigJSON = JSON.stringify(ctx.helper.createRequirejsConfig(definitionRelaRes.javascripts));
    }
    ctx.service.pageDefinitionService.setPageCacheData(appPiPath, pageUuid, __data);
    app.logger.info('%s , 渲染总计耗时: %d ms', ctx.req.url, timing.end('TotalPageRender').duration);
    //app.redis.set(systemId + moduleId + appId + pageUuid, JSON.stringify(__data));

    await ctx.render(!isMobile ? '/app.nj' : '/app_mobile.nj', __data);
  }

  async superadminIndexV6() {
    if (this.ctx.user.superAdmin) {
      await this.page('/pt-mgr', '2f852b9e-4564-4f5b-bc7e-bb57f639cbe3');
    } else {
      await this.ctx.render('page-render/index.js', { errorCode: '403', title: '无权限' });
    }
  }

  async vuePageRender() {
    let pageDefinition = null;
    if (this.ctx.req.headers.referer && this.ctx.req.headers.referer.indexOf('module/assemble')) {
      // 来自模块装配内的页面预览
      this.ctx.isDebug = true;
    }
    try {
      pageDefinition = await this.ctx.service.pageDefinitionService.authenticatedPageDefinition(this.ctx.params.uuid, this.ctx.params.id);
    } catch (error) {
      await this.ctx.render('page-render/index.js', { errorCode: error.message, title: '无权限' });
      return;
    }
    if (pageDefinition == null) {
      await this.ctx.render('page-render/index.js', { errorCode: '404', title: '404' });
      return;
    }

    if (pageDefinition.wtype === 'vPage' || pageDefinition.wtype === 'vBigscreen' || pageDefinition.wtype == 'vUniPage') {
      let definitionJson = pageDefinition.definitionJson ? JSON.parse(pageDefinition.definitionJson) : {};
      delete definitionJson.appWidgetDefinitionElements;
      delete definitionJson.functionElements;
      // let urlParams = {},
      //   _URL_STATE_ = { ...definitionJson.vars };
      // if (Object.keys(this.ctx.query).length) {
      //   // 解析地址的参数为对象：支持 user[code]=1&user[name]=测试
      //   urlParams = qs.parse(this.ctx.req.url.substr(this.ctx.req.url.indexOf('?') + 1), {
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
      //   Object.assign(_URL_STATE_, urlParams); // 地址参数优先
      // }

      // let urlParams = qs.parse(this.ctx.req.url.substring(this.ctx.req.url.indexOf('?') + 1), { allowDots: true });
      // let subPage = {};
      // if (urlParams._sub_page_ && urlParams._sub_page_.uuid) {
      //   let subpage = await this.ctx.service.pageDefinitionService.getByUuid(urlParams._sub_page_.uuid);
      //   if (subpage && subpage.wtype === 'vPage') {
      //     let subpageJson = JSON.parse(subpage.definitionJson);
      //     (subPage.uuid = urlParams._sub_page_.uuid), (subPage.widget = subpageJson);
      //   }
      // }
      let widgetI18ns = undefined,
        locale = this.ctx.localeVariable || 'zh_CN',
        camelLocale = lodash.camelCase(locale);
      if (pageDefinition.i18ns && pageDefinition.i18ns.length) {
        widgetI18ns = { [locale]: { Widget: {} } };
        for (let item of pageDefinition.i18ns) {
          lodash.set(widgetI18ns[locale].Widget, item.code, item.content);
        }
      }

      await this.ctx.render('page-render/index.js', {
        title: pageDefinition.title,
        widget: definitionJson,
        widgetI18ns,
        errorCode: null,
        // subPage,
        // _URL_STATE_, // 页面变量
        pageUuid: this.ctx.params.uuid,
        pageId: this.ctx.params.id,
        unauthorizedResource: pageDefinition.unauthorizedResource,
        APP_SYSTEM_ID: pageDefinition.system // 返回前端标识归属哪个系统
      });
    } else {
      await this.ctx.renderError('404', '找不到相关页面');
    }
  }

  async prodVuePageRender() {
    let { prodVersionUuid, pageId, prodId, pageUuid } = this.ctx.params;
    let pageDefinition = null;
    if (pageUuid != undefined || pageId != undefined) {
      pageUuid = pageUuid || (await this.ctx.service.pageDefinitionService.getProdVersionPageUuid(pageId, prodVersionUuid));
      try {
        pageDefinition = await this.ctx.service.pageDefinitionService.authenticatedPageDefinition(pageUuid, pageId);
      } catch (error) {
        await this.ctx.render('page-render/index.js', { errorCode: error.message, title: '无权限' });
        return;
      }
    }
    let versionSetting = await this.ctx.service.appProdIntegrationService.getProdVersionSetting(prodVersionUuid);
    let allPages = await this.ctx.service.appProdIntegrationService.getPageInfosUnderProdVersion(prodVersionUuid);

    if (pageDefinition == null) {
      if (allPages.length == 0) {
        await this.ctx.render('page-render/index.js', { errorCode: error.message, title: '无权限' });
        return;
      }
      for (let i = 0, len = allPages.length; i < len; i++) {
        allPages[i].pageId = allPages[i].id;
        allPages[i].pageName = allPages[i].name;
        allPages[i].pageUuid = allPages[i].uuid;
      }
      pageId = allPages[0].id;
      pageUuid = allPages[0].uuid;
      pageDefinition = await this.ctx.service.pageDefinitionService.authenticatedPageDefinition(pageUuid, pageId);
    }

    if (pageDefinition.wtype === 'vPage') {
      const layoutFixed = pageDefinition.layoutFixed;
      let fulltextSearchData, fullSearchDefinition;
      fulltextSearchData = await this.getSearchSetting(prodId);
      if (fulltextSearchData && fulltextSearchData.definitionJson) {
        fullSearchDefinition = JSON.parse(fulltextSearchData.definitionJson);
      }

      let definitionJson = pageDefinition.definitionJson ? JSON.parse(pageDefinition.definitionJson) : {};
      let theme = await this.ctx.service.appProdIntegrationService.getProdVersionPageTheme(prodVersionUuid, pageId);
      // 获取产品版本设置
      if (theme) {
        if (this.ctx.THEME.byUser && this.ctx.THEME.class == theme.class) {
          theme.color = this.ctx.THEME.color;
          theme.colorClass = this.ctx.THEME.colorClass;
        }
        theme.classScope = [theme.class];
        theme.fixed = true;
      }
      // 产品上下文
      // / prodId / prodVid
      await this.ctx.render('page-render/index.js', {
        THEME: theme,
        widget: definitionJson,
        ENVIRONMENT: {
          layoutConf: versionSetting.layoutConf ? JSON.parse(versionSetting.layoutConf) : undefined,
          defaultLayoutConf: versionSetting.layoutConf ? JSON.parse(versionSetting.layoutConf) : undefined,
          themeStyle: versionSetting.theme ? JSON.parse(versionSetting.theme) : undefined,
          userLayoutDefinable: true,
          userThemeDefinable: true,
          pageId: pageDefinition.id,
          pageUuid: pageDefinition.uuid,
          allAuthPages: allPages
        },
        errorCode: null,

        // subPage,
        // _URL_STATE_, // 页面变量
        pageId,
        SYSTEM_ID: prodId,
        layoutFixed,
        fullSearchDefinition,
        unauthorizedResource: pageDefinition.unauthorizedResource
      });
    } else {
      await this.ctx.renderError('404', '找不到相关页面');
    }
  }

  async prodVersionLogin() {
    let { prodVersionUuid, prodId } = this.ctx.params;
    let { ctx, app } = this,
      loginConfig = {},
      title = '登录',
      favicon = undefined;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/app/prod/version/getDefaultLoginDef', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          prodVersionUuid,
          isPc: true
        }
      });
      if (result.data && result.data.data) {
        loginConfig = JSON.parse(result.data.data.defJson);
      }

      let theme = await this.ctx.service.appProdIntegrationService.getProdVersionDefaultTheme(prodVersionUuid);
      // 获取产品版本设置
      if (theme) {
        theme.classScope = [theme.class];
        this.ctx.THEME = theme;
        this.ctx.THEME.fixed = true;
      }

      let versionSetting = await this.ctx.service.appProdIntegrationService.getProdVersionSetting(prodVersionUuid);
      if (versionSetting) {
        title = versionSetting.title;
        favicon = versionSetting.icon;
      }
    } catch (error) {
      throw new Error(error);
    }
    if (ctx.query.hasOwnProperty('tokenExpired') || ctx.query.hasOwnProperty('sessionExpired')) {
      ctx.set('tokenExpired', 'true');
      error = '登录失效，请重新登录!';
      if (ctx.helper.mobileDetect.mobile() != null) {
        this.logout();
      }
    }
    let errorMsg = undefined;
    if (ctx.query.hasOwnProperty('error')) {
      errorMsg = ctx.req.session.messages ? ctx.req.session.messages[ctx.req.session.messages.length - 1] : '';
      errorMsg = ctx.__(errorMsg); //国际化语言
    }

    await this.ctx.render('login/index.js', {
      title,
      favicon,
      prodVersionUuid,
      loginConfig,
      errorMsg
    });
  }

  async requirejsConfig() {
    // this.ctx.body = this.app.requirejsConfig;
  }

  async templatePathRender() {
    await this.ctx.render(
      this.ctx.query.path.indexOf('/') === 0 ? this.ctx.query.path : '/' + this.ctx.query.path,
      this.ctx.request.body || {}
    );
  }

  // /html/?/?/? 兼容旧模式
  async htmlTemplateRender() {
    const ctx = this.ctx;
    const themeFiles = await ctx.service.userThemeService.getThemeFiles();

    const data = ctx.request.body
      ? {
          ...ctx.request.body,
          themeFiles
        }
      : {
          themeFiles
        };

    await this.ctx.render(ctx.request.path.replace('/html', ''), data);
  }

  async viewTemplateQuery() {
    this.ctx.body = this.app.viewPaths;
  }

  async renderSecurityHomepage() {
    let pages = await this.ctx.service.pageDefinitionService.getAuthenticatedPageDefintions();
    if (pages) {
      if (pages.length > 0) {
        this.ctx.redirect(pages[0].url);
      } else {
        await this.ctx.render('/error/401');
      }
    } else {
      await this.ctx.render('/error/404');
    }
  }

  async preview(piUuid, pageUuid) {
    const appProdItg = await this.ctx.service.appProdIntegrationService.getByUuid(piUuid);
    this.ctx.isDebug = this.app.options.isDebug; // 预览地址进来，且调试模式下
    if (appProdItg && appProdItg.dataPath) {
      await this.page(appProdItg.dataPath, pageUuid);
    }
  }

  resolveJavascriptDependencies(id, javascripts, added, resourceDefPack) {
    if (!added.has(id)) {
      javascripts.push(resourceDefPack[id]);
    }
  }

  async getWidgetDef() {
    const { ctx, app } = this;
    try {
      const url = `${app.config.backendURL}/api/user/widgetDef/get?widgetId=${ctx.query.widgetId}`;
      const result = await ctx.curl(url, {
        method: 'GET',
        contentType: 'json',
        dataType: 'json'
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }

  async saveWidgetDef() {
    const { ctx, app } = this;
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/user/widgetDef/save', {
        method: 'POST',
        contentType: 'form',
        dataType: 'json',
        data: {
          widgetId: ctx.req.body.widgetId,
          configuraion: JSON.stringify(ctx.req.body.configuration)
        }
      });
      ctx.body = result.data;
    } catch (error) {
      ctx.logger.error(error);
    }
  }

  async getAppHashTreeByAppPath() {
    this.ctx.body = await ctx.service.pageDefinitionService.getAppHashTreeByAppPath(this.ctx.req.body.appPath);
  }

  async clearProdVersionBindPageThemeCache() {
    this.ctx.service.appProdIntegrationService.clearProdVersionBindPageThemeCache(this.ctx.query.prodVersionUuid);
  }

  async getSearchSetting(systemId) {
    const { ctx, app } = this;
    const tenantId = ctx.user.tenantId;
    let cacheKey = `SYSTEM_FULL_SEARCH_SETTING:${tenantId}:${systemId}`;
    let searchData = await app.redis.get(cacheKey);
    if (searchData) {
      return JSON.parse(searchData);
    }
    try {
      const result = await ctx.curl(app.config.backendURL + '/api/fulltext/setting/getSearch', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        headers: {
          system_id: systemId
        }
      });
      if (result.data) {
        if (result.data.code === 0) {
          await app.redis.set(cacheKey, JSON.stringify(result.data.data));
          return result.data.data;
        }
      }
    } catch (error) {
      app.logger.error('请求全文检索设置异常：%s', error);
    }
    return null;
  }

  async sysIndex() {
    const { ctx, app } = this;
    let systemId = ctx.params.system,
      tenantId = ctx.user.tenantId;
    let result = await ctx.service.appProdIntegrationService.getSystemAuthenticatePage(tenantId, systemId);
    if (result == null) {
      await ctx.renderError('403', '无权限');
      return;
    }
    let { definitionJson, title, theme, pageSetting, allAuthPages, pageId, pageUuid, unauthorizedResource, effectiveRoles } = result;
    if (pageId == null) {
      await ctx.renderError('403', '无权限');
      return;
    }

    let layoutFixed = false,
      pageDefinition = null,
      widgetI18ns = undefined;
    try {
      pageDefinition = await this.ctx.service.pageDefinitionService.authenticatedPageDefinition(pageUuid, pageId);
    } catch (error) {
      await this.ctx.render('page-render/index.js', { errorCode: error.message, title: '无权限' });
      return;
    }
    if (pageDefinition) {
      layoutFixed = pageDefinition.layoutFixed;
      let locale = ctx.cookies.get(`locale.${systemId}`, { signed: false }) || 'zh_CN';
      if (pageDefinition.i18ns && pageDefinition.i18ns.length) {
        widgetI18ns = { [locale]: { Widget: {} } };
        for (let item of pageDefinition.i18ns) {
          lodash.set(widgetI18ns[locale].Widget, item.code, item.content);
        }
      }
    }

    let fulltextSearchData, fullSearchDefinition;
    fulltextSearchData = await this.getSearchSetting(systemId);
    if (fulltextSearchData && fulltextSearchData.definitionJson) {
      fullSearchDefinition = JSON.parse(fulltextSearchData.definitionJson);
    }

    let layoutConf = pageSetting.layoutConf ? JSON.parse(pageSetting.layoutConf) : undefined;
    let userLayoutConf = await ctx.service.appProdIntegrationService.getUserPreferenceSystemLayout();
    await this.ctx.render('page-render/index.js', {
      THEME: theme ? JSON.parse(theme) : undefined,
      widget: JSON.parse(definitionJson),
      widgetI18ns,
      ENVIRONMENT: {
        layoutConf: userLayoutConf || layoutConf,
        defaultLayoutConf: layoutConf,
        themeStyle: pageSetting.themeStyle ? JSON.parse(pageSetting.themeStyle) : undefined,
        userLayoutDefinable: pageSetting.userLayoutDefinable,
        userThemeDefinable: pageSetting.userThemeDefinable,
        pageId,
        pageUuid,
        allAuthPages
      },
      PAGE_EFFECTIVE_ROLES: effectiveRoles,
      PAGE_ID: pageId,
      // errorCode: null, title: pageDefinition.title,
      title,
      SYSTEM_ID: systemId, // 返回系统ID
      // pageId: pageDefinition.id,
      unauthorizedResource,
      layoutFixed,
      fullSearchDefinition
    });
  }

  async sysPublicIndex() {
    const { ctx, app } = this;
    let systemId = ctx.params.system,
      pageId = ctx.query.pageId;
    if (pageId) {
      const result = await ctx.curl(app.config.backendURL + '/webapp/getAnonymousPage', {
        method: 'GET',
        contentType: 'json',
        dataType: 'json',
        data: {
          id: pageId
        },
        headers: {
          system_id: systemId,
          'client-token': app.CLIENT_TOKEN
        }
      });
      if (result.data) {
        if (result.data.code === 0) {
          let { definitionJson, title, theme, pageUuid, system, pageSetting } = result.data.data;
          if (system != systemId) {
            await ctx.renderError('404');
            return;
          }
          let layoutConf = pageSetting.layoutConf ? JSON.parse(pageSetting.layoutConf) : undefined;
          let themeJson = {
            class: undefined,
            colorClass: undefined,
            classScope: []
          };
          if (theme != undefined) {
            themeJson = JSON.parse(theme);
          } else if (pageSetting.themeStyle) {
            let themeStyle = JSON.parse(pageSetting.themeStyle);
            if (themeStyle.pc) {
              // 由系统信息提供的主题
              let themes = themeStyle.pc.theme,
                defaultTheme = themeStyle.pc.defaultTheme;
              for (let i = 0, len = themes.length; i < len; i++) {
                if (defaultTheme == themes[i].themeClass) {
                  themeJson.class = defaultTheme;
                  themeJson.colorClass = themes[i].colorClass;
                  themeJson.classScope.push(defaultTheme);
                  break;
                }
              }
            }
          }

          await this.ctx.render('page-render/index.js', {
            THEME: themeJson,

            widget: JSON.parse(definitionJson),
            ENVIRONMENT: {
              layoutConf,
              userLayoutDefinable: false,
              userThemeDefinable: false,
              pageId,
              pageUuid
            },
            title,
            SYSTEM_ID: system // 返回系统ID
          });
          return;
        }
        // await ctx.renderError(result.data.code === -7000 ? '403' : (result.data.code === -6000 ? '404' : '500'));
        // return;
      }
    }
    await ctx.renderError('404');
  }

  async systemAdminIndex() {
    const { ctx, app } = this;
    // 超管与租户管理员可访问
    if (ctx.hasAnyUserRole('ROLE_TENANT_ADMIN', 'ROLE_ADMIN')) {
      let system = ctx.params.system,
        tenantId = ctx.user.tenantId;
      if (['system_manager', 'PRD_PT'].includes(system) && !ctx.hasAnyUserRole('ROLE_ADMIN')) {
        await ctx.renderError('403', '无权限');
        return;
      }
      let tenantSystemInfo = await this.ctx.service.appProdIntegrationService.getTenantSystemInfo(tenantId, system);
      if (tenantSystemInfo == null) {
        if (tenantId == 'T001') {
          // 默认租户情况下，先自动生成系统管理后台相关数据，其他租户需要通过授权方式授权相关产品
          await this.ctx.render('product-center/system-admin-init.js', {
            SYSTEM_ID: system
          });
          return;
        }
        // 未授权的产品
        await ctx.renderError('403', '无权限');
        return;
      }
      // 访问"系统管理后台"产品的最新发布产品版本下的授权页面
      let results = await Promise.all([
        this.ctx.service.pageDefinitionService.prodVersionAuthenticatePage(undefined, 'system_manager'),
        this.ctx.service.appProdIntegrationService.getLatestPublishProdVersionSetting('system_manager')
      ]);
      let pageUuids = results[0];
      let versionSetting = results[1];
      let pageDefinition = await this.ctx.service.pageDefinitionService.authenticatedPageDefinition(pageUuids[0], undefined);
      if (pageDefinition == null) {
        await ctx.renderError('403', '无权限');
        return;
      }

      if (pageDefinition.wtype === 'vPage') {
        let definitionJson = pageDefinition.definitionJson ? JSON.parse(pageDefinition.definitionJson) : {};

        let themeData = JSON.parse(versionSetting.theme).pc,
          defaultTheme = themeData.defaultTheme,
          colorClass = undefined,
          classScope = [];
        for (let t of themeData.theme) {
          classScope.push(t.themeClass);
          if (t.themeClass == defaultTheme) {
            colorClass = t.colorClass;
          }
        }
        let theme = { class: defaultTheme, colorClass, classScope };
        let pageTheme = await this.ctx.service.appProdIntegrationService.getProdVersionPageTheme(
          versionSetting.prodVersionUuid,
          pageDefinition.id
        );
        // 获取产品版本设置
        if (pageTheme) {
          if (this.ctx.THEME.byUser && this.ctx.THEME.class == pageTheme.class) {
            theme.color = this.ctx.THEME.color;
            theme.colorClass = this.ctx.THEME.colorClass;
          }
          theme.classScope = [pageTheme.class];
          theme.fixed = true;
        }

        let userTheme = await ctx.service.appProdIntegrationService.getUserPreferenceSystemTheme(system + ':system_manager');
        if (userTheme && userTheme.class != undefined) {
          // 用户设置了主题，且主题包有效
          if (userTheme.class == '' || (theme && theme.classScope.includes(userTheme.class))) {
            theme = { ...userTheme, byUser: true, classScope: theme ? theme.classScope : [] };
          }
        }

        let favicon = undefined;
        if (tenantSystemInfo) {
          favicon = tenantSystemInfo.favicon;
          if (tenantSystemInfo.adminTitle || tenantSystemInfo.adminLogo) {
            // 替换布局里面的 logo 和 头部标题
            let header = definitionJson.items[0].configuration.header;
            if (tenantSystemInfo.adminLogo) {
              header.configuration.logo = tenantSystemInfo.adminLogo;
            }
            if (tenantSystemInfo.adminTitle) {
              header.configuration.title = tenantSystemInfo.adminTitle;
            }
          }
        }

        let layoutConf = versionSetting.layoutConf ? JSON.parse(versionSetting.layoutConf) : undefined;
        let userLayoutConf = await ctx.service.appProdIntegrationService.getUserPreferenceSystemLayout(system + ':system_manager');

        // let result = await ctx.service.appProdIntegrationService.getSystemAuthenticatePage(tenantId, system),
        //   allAuthPages = [];
        // if (result) {
        //   allAuthPages = result.allAuthPages;
        // }
        await this.ctx.render('page-render/index.js', {
          THEME: theme,
          widget: definitionJson,
          SYSTEM_ADMIN_MANAGER_WORKBENCHES: true,
          ENVIRONMENT: {
            // allAuthPages,
            layoutConf: userLayoutConf || layoutConf,
            defaultLayoutConf: layoutConf,
            userThemeDefinable: true,
            userLayoutDefinable: true
          },
          errorCode: null,
          title: pageDefinition.title,
          title: tenantSystemInfo.title || pageDefinition.title,
          favicon,
          tenantProdVersionUuid: tenantSystemInfo.prodVersionUuid,
          SYSTEM_ID: system, // 返回系统ID
          // subPage,
          // _URL_STATE_, // 页面变量
          pageId: pageDefinition.id,
          unauthorizedResource: pageDefinition.unauthorizedResource
        });
        return;
      }
    }
    await ctx.renderError('403', '无权限');
  }

  async wellBuilderCenter() {
    const { ctx, app } = this;
    // 超管与租户管理员可访问
    if (ctx.hasAnyUserRole('ROLE_TENANT_ADMIN', 'ROLE_ADMIN')) {
      let system = 'PRD_PT',
        tenantId = ctx.user.tenantId;
      let tenantSystemInfo = await this.ctx.service.appProdIntegrationService.getTenantSystemInfo(tenantId, system);
      if (tenantSystemInfo == null) {
        // 未授权的产品
        await ctx.renderError('403', '无权限');
        return;
      }
      // 访问"平台产品"产品的最新发布产品版本下的授权页面
      let results = await Promise.all([this.ctx.service.appProdIntegrationService.getLatestPublishProdVersionSetting('PRD_PT')]);

      let versionSetting = results[0];
      let pageDefinition = await this.ctx.service.pageDefinitionService.authenticatedPageDefinition(undefined, 'well_builder');
      if (pageDefinition == null) {
        await ctx.renderError('403', '无权限');
        return;
      }

      if (pageDefinition.wtype === 'vPage') {
        let definitionJson = pageDefinition.definitionJson ? JSON.parse(pageDefinition.definitionJson) : {};

        let themeData = JSON.parse(versionSetting.theme).pc,
          defaultTheme = themeData.defaultTheme,
          colorClass = undefined,
          classScope = [];
        for (let t of themeData.theme) {
          classScope.push(t.themeClass);
          if (t.themeClass == defaultTheme) {
            colorClass = t.colorClass;
          }
        }
        let theme = { class: defaultTheme, colorClass, classScope };
        let pageTheme = await this.ctx.service.appProdIntegrationService.getProdVersionPageTheme(
          versionSetting.prodVersionUuid,
          pageDefinition.id
        );
        // 获取产品版本设置
        if (pageTheme) {
          if (this.ctx.THEME.byUser && this.ctx.THEME.class == pageTheme.class) {
            theme.color = this.ctx.THEME.color;
            theme.colorClass = this.ctx.THEME.colorClass;
          }
          theme.classScope = [pageTheme.class];
          theme.fixed = true;
        }

        let userTheme = await ctx.service.appProdIntegrationService.getUserPreferenceSystemTheme(system + ':well_builder');
        if (userTheme && userTheme.class != undefined) {
          // 用户设置了主题，且主题包有效
          if (userTheme.class == '' || (theme && theme.classScope.includes(userTheme.class))) {
            theme = { ...userTheme, byUser: true, classScope: theme ? theme.classScope : [] };
          }
        }

        let favicon = undefined;
        let layoutConf = versionSetting.layoutConf ? JSON.parse(versionSetting.layoutConf) : undefined;
        let userLayoutConf = await ctx.service.appProdIntegrationService.getUserPreferenceSystemLayout(system);
        // let result = await ctx.service.appProdIntegrationService.getSystemAuthenticatePage(tenantId, system),
        //   allAuthPages = [];
        // if (result) {
        //   allAuthPages = result.allAuthPages;
        // }
        await this.ctx.render('page-render/index.js', {
          THEME: theme,
          widget: definitionJson,
          ENVIRONMENT: {
            // allAuthPages,
            layoutConf: userLayoutConf || layoutConf,
            defaultLayoutConf: layoutConf,
            userThemeDefinable: true,
            userLayoutDefinable: true
          },
          errorCode: null,
          title: pageDefinition.title,
          title: pageDefinition.title,
          favicon,
          // SYSTEM_ID: system, // 返回系统ID
          // subPage,
          // _URL_STATE_, // 页面变量
          pageId: pageDefinition.id,
          unauthorizedResource: pageDefinition.unauthorizedResource
        });
        return;
      }
    }
    await ctx.renderError('403', '无权限');
  }
}

module.exports = PageRenderController;
