"use strict";

import { isEmpty, template, isArray } from "lodash";
import dmsDataService from "../dms-data-services";
// const templateEngine = require("juicer");
import pageContext from "../page-context";
let jsAppModules = {};
let appFunctions = {};
let piItems = {};
let registerJsModules = function (jsModules) {
  jsModules.keys().forEach(function (fileName) {
    let appId = fileName
      .split("/")
      .pop()
      .replace(/\.\w+$/, "");
    let appModule = () => jsModules(fileName);
    // let appModule = jsModules(fileName);
    appModule = appModule.default || appModule;
    if (jsAppModules[appModule.id || appId]) {
      console.error("Duplicate functions", jsAppModules[appModule.id || appId], appModule, appModule.id || appId);
    }
    jsAppModules[appModule.id || appId] = appModule;
  });
};

export default {
  // 注册应用功能
  registerAppFunctions() {
    // 加载项目JS功能二开
    registerJsModules(require.context("@/development", true, /\w+\.js$/));

    // 加载平台应用JS功能二开
    // #ifdef H5 || MP
    registerJsModules(require.context("@/packages/_/development", true, /\w+\.js$/));
    // #endif
    // #ifdef APP-PLUS
    registerJsModules(require.context("@/packages/_/development", true, /\w+\.js$/));
    // #endif
  },

  jsModuleExist(id) {
    return jsAppModules[id] != null;
  },

  jsInstance(id, $widget) {
    if (jsAppModules[id]) {
      return new (jsAppModules[id]().default)($widget);
    }
    console.error("unknown js : " + id);
  },

  // 发起应用
  startApp: function (options) {
    const _self = this;
    let ui = options.ui;
    let pageParameters = options.pageParameters;
    let pageUrl = options.pageUrl || options.mobileUrl;
    let appFunction = options.appFunction;
    // 页面跳转
    if (!isEmpty(pageUrl)) {
      // 页面参数
      if (!isEmpty(pageParameters)) {
        pageUrl = _self.setPageUrlAndParams(pageUrl, pageParameters);
        for (let key in pageParameters) {
          ui.setPageParameter(key, pageParameters[key]);
        }
      }
      // 页面标题
      if (options.title) {
        pageUrl = _self.setPageUrlAndParams(pageUrl, { redirectPageTitle: encodeURI(options.title) });
      }
      if (pageUrl.startsWith("http")) {
        pageUrl = "/packages/_/pages/web-view/web-view?url=" + pageUrl;
      }
      uni.navigateTo({
        url: pageUrl,
      });
    } else if (appFunction) {
      _self.startAppByAppFunction(appFunction, options);
    } else if (!isEmpty(options.appPath)) {
      _self.startAppByAppPath(options.appPath, options);
    } else if (!isEmpty(options.piUuid)) {
      _self.startAppByPiUuid(options.piUuid, options);
    } else if (!isEmpty(options.pageId)) {
      _self.startAppByPageId(options.pageId, options);
    } else {
      console.log("unknown app options: ", options);
    }
  },
  setPageUrlAndParams(url, pageParameters) {
    for (let key in pageParameters) {
      let value = pageParameters[key];
      if (isArray(pageParameters)) {
        key = value["paramKey"];
        value = value["paramValue"];
      }
      if (url.indexOf("?") == -1) {
        url += "?";
        url += key + "=" + value;
      } else {
        url += "&" + key + "=" + value;
      }
    }
    return url;
  },
  dispatchEvent: function (options) {
    let _this = this;
    let { actionType, ui, $evtWidget } = options;
    if (!ui) {
      ui = $evtWidget;
      options.ui = $evtWidget;
    }
    if (actionType == "redirectPage") {
      // 打开页面
      if (options.pageType == "url") {
        options.pageUrl = options.uniModel && options.uniModel.url ? options.uniModel.url : options.url;
        // 解析URL上的动态参数
        let compiler = template(options.pageUrl);
        try {
          options.pageUrl = compiler(options.meta || {});
        } catch (error) {}
        let eventParams = this.resolveEventParams(options.eventParams, options.meta ? options.meta : {});
        if (!isEmpty(eventParams)) {
          options.pageParameters = eventParams;
        }
        this.startApp(options);
      } else {
        let eventParams = this.resolveEventParams(options.eventParams, options.meta ? options.meta : {});
        if (!isEmpty(eventParams)) {
          options.pageParameters = eventParams;
        }
        this.startAppByPageId(options.pageId, options);
      }
    } else if (actionType == "jsFunction") {
      let jsFunction = options.jsFunction;
      if (jsFunction) {
        let jsParts = jsFunction.split(".");
        if (ui) {
          // 调用组件上的二开
          if (ui.$developJsInstance != undefined) {
            if (ui.$developJsInstance[jsParts[0]] && ui.$developJsInstance[jsParts[0]][jsParts[1]]) {
              // 调用二开
              return ui.$developJsInstance[jsParts[0]][jsParts[1]].apply(ui.$developJsInstance[jsParts[0]], [options]);
            }
          }
        }
        this.invokeJsFunction(jsFunction, options);
      }
      let customScript = options.customScript;
      if (customScript) {
        pageContext.executeCodeSegment(customScript, options || {}, ui);
      }
    } else if (actionType === "widgetEvent") {
      let $event = {};
      let eventParams = this.resolveEventParams(options.eventParams, options.meta ? options.meta : {});
      if (!isEmpty(eventParams)) {
        $event.eventParams = eventParams;
      }
      $event.ui = options.ui;
      ui.pageContext.emitEvent(`${options.eventWid}:${options.eventId}`, $event);
    } else if (actionType === "dataManager") {
      let parts = options.action.split(".");
      let scriptName = parts[0],
        method = parts[1];
      let eventParams = this.resolveEventParams(options.eventParams, options.meta ? options.meta : {});
      if (!isEmpty(eventParams)) {
        options.eventParams = eventParams;
      }
      if (ui) {
        // 调用组件上的二开
        if (ui.$developJsInstance != undefined) {
          if (ui.$developJsInstance[scriptName] && ui.$developJsInstance[scriptName][method]) {
            // 调用二开
            return ui.$developJsInstance[scriptName][method].apply(ui.$developJsInstance[scriptName], [options]);
          }
        }
      }
      this.invokeJsFunction(options.action, options);
    } else if (actionType === "workflow") {
      // if (options.sendToApprove) {
      //   if (options.ui && options.ui.selectedRows && options.ui.selectedRows.length == 0) {
      //     uni.$ptToastShow({
      //       title: options.ui.$t("global.pleaseSelect", "请选择！"),
      //     });
      //     return;
      //   }
      //   import("@/packages/_/development/workflow/WorkflowSendToApprove").then((WorkflowSendToApprove) => {
      //     let links = [];
      //     let evetnParams = _this.resolveEventParams(options.eventParams, options.ui ? options.ui : {});
      //     if (evetnParams.linkTitle && evetnParams.linkUrl) {
      //       let linkTitles = evetnParams.linkTitle.split(";");
      //       let linkUrls = evetnParams.linkUrl.split(";");
      //       for (let index = 0; index < linkTitles.length; index++) {
      //         links.push({
      //           title: linkTitles[index],
      //           url: linkUrls[index],
      //         });
      //       }
      //     }
      //     let approveOptions = { ui: options.ui, flowDefId: options.workflowId, links, ...evetnParams };
      //     new WorkflowSendToApprove.default(approveOptions).sendToApprove();
      //   });
      // } else {
      let url = `/packages/_/pages/workflow/work_view?flowDefId=${options.workflowId}`;
      if (_this.SYSTEM_ID) {
        url += `system_id=${_this.SYSTEM_ID}`;
      }
      // if (options.broadcastChannel) {
      //   url += '?__bc=' + options.broadcastChannel;
      // }
      this.startApp({
        pageUrl: url,
      });
      // }
    }
  },
  resolveEventParams: function (eventParams, data = {}) {
    let params = {};
    if (eventParams) {
      if (Array.isArray(eventParams)) {
        eventParams.forEach((p) => {
          let compiler = template(p.paramValue);
          try {
            params[p.paramKey] = compiler(data);
          } catch (error) {
            console.error(error);
          }
        });
      } else {
        let params = Object.assign({}, eventParams);
        for (let p in params) {
          let compiler = template(params[p]);
          try {
            params[p] = compiler(data);
          } catch (error) {
            console.error(error);
          }
        }
      }
    }
    return params;
  },

  resolveParams: function (params, data) {
    if (params == null) {
      return {};
    }
    for (var p in params) {
      // params[p] = templateEngine(params[p], data);
    }
    return params;
  },

  invokeJsFunction: function (jsFunction, options) {
    // 执行二开脚本方法
    let parts = jsFunction.split(".");
    let script = jsAppModules[parts[0]];
    if (script) {
      script = script();
      // 异步加载的JS模块
      if (script.then) {
        script.then(function (module) {
          let inst = module();
          if (inst[parts[1]]) {
            inst[parts[1]](options);
          }
        });
      } else if (script.default) {
        let inst = new script.default();
        if (inst[parts[1]]) {
          return inst[parts[1]](options);
        }
      }
    } else {
      console.error("未加载的二开脚本: ", parts[0]);
    }
  },

  startAppByAppFunction: function (appFunction, options) {
    const _self = this;
    // 应用功能
    let appModule =
      jsAppModules[appFunction.id] || jsAppModules[appFunction.executeJsModule] || jsAppModules[appFunction.jsModule];
    if (appModule) {
      // 数据管理操作模块，确保dmsId、configuration参数
      if (appFunction.category == "DmsAction") {
        let pageParameters = options.pageParameters || {};
        let dmsWidgetDefinition = (options.ui && options.ui.parent) || {};
        if (!pageParameters["dmsId"] && dmsWidgetDefinition.id) {
          pageParameters["dmsId"] = dmsWidgetDefinition.id;
        }
        if (!pageParameters["configuration"] && dmsWidgetDefinition.configuration) {
          pageParameters["configuration"] = dmsWidgetDefinition.configuration;
        }
        options.pageParameters = pageParameters;
      }
      if (appModule.performed) {
        appModule.performed(options);
      } else if (typeof appModule == "function") {
        let appModuleResult = appModule();
        // 异步加载的JS模块
        if (appModuleResult.then) {
          appModuleResult.then(function (module) {
            module.performed(options);
          });
        } else if (appModuleResult.default) {
          appModuleResult = new appModuleResult.default();
          appModuleResult.performed(options);
        } else if (appModuleResult.performed) {
          appModuleResult.performed(options);
        } else {
          console.error("js module must implement method of performed", appModule);
        }
      } else {
        console.error("js module must implement method of performed", appModule);
      }
    } else if (appFunction.category == "DmsAction" && isEmpty(appFunction.executeJsModule)) {
      // 数据管理数据操作
      dmsDataService.performed(options);
    } else if (appFunction.category == "appWidgetDefinition") {
      // 组件定义
      let pageUrl = `/packages/_/pages/app/app?widgetDefId=${appFunction.id}`;
      options.pageUrl = pageUrl;
      _self.startApp(options);
    } else if (!isEmpty(appFunction.appPath)) {
      // 已从功能启动，不能循环启动
      if (options.startFromAppFunction) {
        console.error("app has started by app path", appFunction.appPath, appFunction);
      } else {
        _self.startAppByAppPath(appFunction.appPath, options);
      }
    } else {
      console.log("app module no performed", appFunction);
    }
  },
  startAppByAppPath: function (appPath, options) {
    const _self = this;
    const appType = options.appType;
    if (appType == "1" || appType == "2" || appType == "3") {
      let pageUrl = `/packages/_/pages/app/app?appPiPath=${appPath}`;
      options.pageUrl = pageUrl;
      _self.startApp(options);
    } else {
      _self.getAppFunctionByAppPath(appPath, function (appFunction) {
        options.appFunction = appFunction;
        // 标记从功能启动
        options.startFromAppFunction = true;
        _self.startApp(options);
      });
    }
  },
  startAppByPageId: function (pageId, options) {
    const _self = this;
    let pageUrl = `/packages/_/pages/app/app?pageId=${pageId}`;
    options.pageUrl = pageUrl;
    _self.startApp(options);
  },
  getAppFunctionByAppPath: function (appPath, callback) {
    const _self = this;
    if (appFunctions[appPath]) {
      callback.call(_self, appFunctions[appPath]);
      return;
    }

    uni.request({
      service: "appContextService.getFunctionByPath",
      method: "POST",
      data: [appPath],
      success: function (result) {
        let appFunction = result.data.data;
        if (appFunction && appFunction.definitionJson) {
          appFunctions[appPath] = JSON.parse(appFunction.definitionJson);
          callback.call(_self, appFunctions[appPath]);
        } else {
          console.log("app function no found", appPath, appFunction);
        }
      },
    });
  },
  startAppByPiUuid: function (piUuid, options) {
    const _self = this;
    _self.getPiItemByPiUuid(piUuid, function (piItem) {
      var appOptions = options || {};
      appOptions.appType = piItem.type;
      appOptions.appPath = piItem.path;
      appOptions.appId = piItem.uuid;
      _self.startApp(appOptions);
    });
  },
  getPiItemByPiUuid: function (piUuid, callback) {
    const _self = this;
    if (piItems[piUuid]) {
      callback.call(_self, piItems[piUuid]);
      return;
    }

    uni.request({
      service: "appContextService.getPiItemByPiUuid",
      method: "POST",
      data: [piUuid],
      success: function (result) {
        piItems[piUuid] = result.data.data;
        callback.call(_self, piItems[piUuid]);
      },
    });
  },
  getWidget($this, wgtId, callback) {
    if (wgtId != undefined) {
      let id = undefined,
        appPageId = undefined,
        appPageUuid = undefined;
      if (typeof wgtId == "string") {
        id = wgtId;
      } else {
        // 根据页面定位组件ID
        id = wgtId.id;
        appPageId = wgtId.pageId;
        appPageUuid = wgtId.pageUuid;
      }
      return new Promise((resolve, reject) => {
        $this.$axios
          .get("/api/app/widget/getWidgetById", { params: { id, appPageId, appPageUuid } })
          .then(({ data }) => {
            let wgt = data && data.data;
            uni.showLoading();
            if (!wgt) {
              console.warn("no found widget definition");
              reject();
            } else {
              resolve(JSON.parse(wgt.definitionJson));
            }
          })
          .catch(() => {});
      });
    }
  },
};

// 时间格式化
Date.prototype.format = function (fmt) {
  var o = {
    "M+": this.getMonth() + 1, //月份
    "DD+": this.getDate(), //日
    "HH+": this.getHours(), //小时
    "m+": this.getMinutes(), //分
    "s+": this.getSeconds(), //秒
    "q+": Math.floor((this.getMonth() + 3) / 3), //季度
    S: this.getMilliseconds(), //毫秒
  };

  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
  }

  for (var k in o) {
    if (new RegExp("(" + k + ")").test(fmt)) {
      fmt = fmt.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
    }
  }

  return fmt;
};
