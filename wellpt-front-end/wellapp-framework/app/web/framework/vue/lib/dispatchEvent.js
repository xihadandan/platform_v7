import { template as stringTemplate, isEmpty, cloneDeepWith, cloneDeep, isSymbol, set } from 'lodash';
import md5 from '@framework/vue/utils/md5';
import {
  jsonataEvaluate,
  evaluateConvertJsonDataFromSchema,
  expressionCompare,
  queryString,
  queryStringify
} from '@framework/vue/utils/util';

const Event = function (option) {
  this.$evtWidget = option.$evtWidget; // 事件操作的主体组件
  this.action = option.action;
  this.dmsId = option.dmsId; // 数据管理ID
  this.dmsTableId = option.dmsTableId;
  this.actionType = option.actionType;
  this.pageUuid = option.pageUuid; // 派发页面
  this.pageId = option.pageId; // 派发页面ID
  this.targetPosition = option.targetPosition; // 目标位置
  this.pageContext = option.pageContext;
  this.containerWid = option.containerWid; // 目标容器ID
  this.eventWid = option.eventWid; // 事件组件ID
  this.eventId = option.eventId;
  this.layoutNewTabOpen = option.layoutNewTabOpen;
  this.meta = option.meta || {}; // 事件元数据
  this.eventParams = cloneDeep(option.eventParams);
  this.layoutNoTabsOpenType = option.layoutNoTabsOpenType; // 仅支持布局容器的打开方式备选渲染：当布局非多标签页模式时候自动切换为该指定方式渲染内容
  this.option = option;
  this.title = option.title; // 派发到模板位置的显示标题
  this.key = option.key;
  this.wEventParams = cloneDeep(option.wEventParams);
  this.broadcastChannel = option.broadcastChannel; // 广播通信ID
};

/**
 * 支持字符模板化 : ${var}
 * @see https://www.lodashjs.com/docs/lodash.template
 * @returns
 */
Event.prototype.resolveEventParams = function () {
  let params = {};
  if (this.eventParams) {
    if (Array.isArray(this.eventParams)) {
      this.eventParams.forEach(p => {
        if (typeof p.paramValue == 'string') {
          let compiler = stringTemplate(p.paramValue);
          try {
            params[p.paramKey] = compiler.call(this, this.meta || {});
          } catch (error) {
            console.error(error);
          }
        } else {
          params[p.paramKey] = p.paramValue;
        }
      });
    } else {
      let params = Object.assign({}, this.eventParams);
      for (let p in params) {
        if (typeof p.paramValue == 'string') {
          let compiler = stringTemplate(params[p]);
          try {
            params[p] = compiler(this.meta || {});
          } catch (error) {
            console.error(error);
          }
        } else {
          params[p] = params[p];
        }
      }
    }
  }
  return params;
};
Event.prototype.resolveEventParamsAsUrl = function () {
  let pair = [],
    params = {};
  if (this.eventParams) {
    if (Array.isArray(this.eventParams)) {
      this.eventParams.forEach(p => {
        let compiler = stringTemplate(p.paramValue);
        try {
          params[p.paramKey] = compiler(this.meta || {});
        } catch (error) {
          params[p.paramKey] = p.paramValue;
          console.error(error);
        }
      });
    } else {
      let params = Object.assign({}, this.eventParams);
      for (let p in params) {
        let compiler = stringTemplate(params[p]);
        try {
          params[p] = compiler(this.meta || {});
        } catch (error) { }
      }
    }
  }
  for (let p in params) {
    pair.push(`${p}=${params[p]}`);
  }
  return pair.length > 0 ? pair.join('&') : '';
};

Event.prototype.getWidget = function (wgtId, callback) {
  let _this = this;
  if (wgtId != undefined) {
    $tempStorage.getCache(
      `getWidget:${typeof wgtId == 'string' ? wgtId : md5(JSON.stringify(wgtId))}`,
      () => {
        window.$LOADING();
        let id = undefined,
          appPageId = undefined,
          appPageUuid = undefined;
        if (typeof wgtId == 'string') {
          id = wgtId;
        } else {
          // 根据页面定位组件ID
          id = wgtId.id;
          appPageId = wgtId.pageId;
          appPageUuid = wgtId.pageUuid;
        }
        return new Promise((resolve, reject) => {
          $axios
            .get('/api/widget/getWidgetById', { params: { id, appPageId, appPageUuid } })
            .then(({ data }) => {
              let wgt = data;
              window.$LOADING(false);
              if (!wgt) {
                console.warn('no found widget definition');
                reject();
              } else {
                resolve(wgt.definitionJson);
              }
            })
            .catch(() => {
              window.$LOADING(false);
            });
        });
      },
      json => {
        callback.call(_this, JSON.parse(json));
      }
    );
  }
};

Event.prototype.getPage = function (params, callback) {
  return new Promise((resolve, reject) => {
    $axios
      .get('/proxy/webapp/authenticatePage', {
        params
      })
      .then(({ data }) => {
        if (data.code == 0) {
          resolve(data.data || '404');
        } else if (data.code == -7000) {
          // 无权限
          resolve('403');
        }
      })
      .catch(() => {
        reject();
      });
  });
};

Event.prototype.render = function (renderOption) {
  let { url, widgets, title, key, targetPosition, containerWid, eventHandler, meta, eventParams, forceUpdate } = renderOption;
  if (url) {
    if (url.startsWith('/')) {
      if (window.__INITIAL_STATE__.SYSTEM_ID && !window.__INITIAL_STATE__.ADMIN) {
        url = '/sys/' + window.__INITIAL_STATE__.SYSTEM_ID + '/_' + url;
      } else if (window.__INITIAL_STATE__.PROD_CONTEXT_PATH) {
        url = '/webapp' + window.__INITIAL_STATE__.PROD_CONTEXT_PATH + '/_' + url;
      }
    }
    if (targetPosition == 'widgetLayout') {
      if (this.layoutNewTabOpen) {
        key = key + '_' + new Date().getTime();
      }
      this.pageContext.emitEvent(`WidgetLayoutContent:Update:${containerWid}`, {
        title,
        key,
        url
      });
    } else if (targetPosition === 'currentWindow') {
      window.$LOADING();
      location.href = url;
    } else if (targetPosition == 'widgetTab') {
      this.pageContext.emitEvent(`${containerWid}:addTab`, {
        id: md5(url),
        title,
        url
      });
    } else if (targetPosition === 'widgetModal') {
      this.pageContext.emitEvent(`${containerWid}:showModal`, undefined, url, title);
    } else {
      let newWindow = window.open(url, md5(url)); //window.open(url,name)相同name的窗口只能创建一个
      if (title) {
        title = this.$evtWidget.$t('Widget.' + this.option.key + '.redirectPageTitle', title)
        newWindow.onload = function () {
          setTimeout(() => {
            // 跳转新页面，修改新页面标题
            newWindow.document.title = title;
          }, 0);
        }
      }
    }
  } else if (widgets) {
    if (targetPosition === 'widgetLayout') {
      if (containerWid == undefined && this.$evtWidget && this.$evtWidget.parentLayContentId != undefined) {
        // 获取上一级的布局容器进行渲染
        if (typeof this.$evtWidget.parentLayContentId == 'function') {
          containerWid = this.$evtWidget.parentLayContentId();
        } else {
          containerWid = this.$evtWidget.parentLayContentId;
        }
      }
      //  不存在布局的处理情况
      if (containerWid == undefined) {
        // 以新页面打开内容
        let pageEvent = new PageEvent(this.option);
        pageEvent.targetPosition = 'newWindow';
        pageEvent.trigger();
        return;
      }
      if (this.layoutNewTabOpen) {
        key = key + '_' + new Date().getTime();
      }
      this.pageContext.emitEvent(`WidgetLayoutContent:Update:${containerWid}`, {
        title,
        key,
        widgets,
        layoutNoTabsOpenType: this.layoutNoTabsOpenType,
        forceUpdate,
        $event: {
          $evtWidget: this.$evtWidget,
          ...JSON.parse(JSON.stringify({ meta, eventParams }))
        },

        locateNavigation: this.option.locateNavigation // 派发到布局的页面，可能需要把页面导航定位出来
      });
    } else if (targetPosition === 'widgetModal') {
      this.pageContext.emitEvent(`${containerWid}:showModal`, undefined, widgets, title);
    } else if (targetPosition === 'widgetTab') {
      // 服务端渲染，直接赋予组件配置
      if (EASY_ENV_IS_NODE) {
        if (this.pageContext.ssrEvent[`${containerWid}:addTab`] == undefined) {
          this.pageContext.ssrEvent[`${containerWid}:addTab`] = [];
        }
        this.pageContext.ssrEvent[`${containerWid}:addTab`].push({
          title,
          id: key,
          widgets,
          eventHandler,
          $event: {
            $evtWidget: this.$evtWidget,
            ...JSON.parse(JSON.stringify({ meta, eventParams }))
          }
        });
      } else {
        this.pageContext.emitEvent(`${containerWid}:addTab`, {
          title,
          id: key,
          widgets,
          eventHandler,
          $event: {
            $evtWidget: this.$evtWidget,
            ...JSON.parse(JSON.stringify({ meta, eventParams }))
          }
        });
      }
    }
  }
};
const PageEvent = function (option) {
  Event.call(this, option);
};

PageEvent.prototype = Object.create(Event.prototype);

PageEvent.prototype.trigger = function () {
  /**
   *  pageId 指定的页面，渲染的是最新版本的
   */
  let url = undefined;
  if (this.option.pageType == 'url') {
    url = this.option.url;
  } else if (this.pageId) {
    url = `/webpage/${this.pageId}`;
  }
  if (url) {
    let urlParams = this.resolveEventParamsAsUrl();
    if (url.indexOf('?') === -1 && urlParams) {
      url += `?${urlParams}`;
    }
    // 解析URL上的动态参数
    let compiler = stringTemplate(url);
    url = compiler(this.meta);
  }
  let urlHashParams = undefined;
  if (this.option.pageLoadedSelectElementId && this.option.pageLoadedSelectElementId.length) {
    urlHashParams = { selectKeys: this.option.pageLoadedSelectElementId };
    let state = queryStringify(urlHashParams, {
      allowDots: true,
      arrayFormat: 'repeat'
    });
    url += '#' + state;
  }

  if (this.targetPosition === 'newWindow' || this.targetPosition === 'currentWindow') {
    this.render({ url, title: this.title, targetPosition: this.targetPosition, containerWid: this.containerWid });
  } else if (this.targetPosition === 'widgetLayout' || this.targetPosition === 'widgetModal') {
    if (this.option.pageType != 'url') {
      if (this.targetPosition === 'widgetLayout') {
        this.pageContext.emitEvent(`WidgetLayoutContent:Loading:${this.containerWid}`, {
          title: this.title,
          key: this.key
        });
      }
      let _this = this;
      this.render({
        targetPosition: this.targetPosition,
        containerWid: this.containerWid,
        title: this.title,
        key: this.key,
        meta: this.meta,
        eventParams: this.resolveEventParams(),
        widgets: new Promise((resolve, reject) => {
          _this
            .getPage({ id: _this.pageId })
            .then(data => {
              let definitionJson = typeof data != 'string' ? JSON.parse(data.definitionJson) : {};
              definitionJson.wtype = 'WidgetVpage';
              definitionJson.props = {
                urlHashParams,
                pageId: _this.pageId,
                pageTitle: definitionJson.title
              };
              if (typeof data == 'string') {
                definitionJson.props.errorCode = data;
              } else {
                definitionJson.props.eventParams = this.resolveEventParams();
                definitionJson.props._unauthorizedResource = data.unauthorizedResource;
              }
              // 页面国际化参数配置
              if (this.pageContext.vueInstance) {
                let widgetI18ns = undefined;
                if (data.i18ns.length) {
                  widgetI18ns = { [this.pageContext.vueInstance.$i18n.locale]: { Widget: {} } };
                  for (let item of data.i18ns) {
                    set(widgetI18ns[this.pageContext.vueInstance.$i18n.locale].Widget, item.code, item.content);
                  }
                }
                this.pageContext.vueInstance.$nextTick(() => {
                  if (widgetI18ns) {
                    for (let l in widgetI18ns) {
                      this.pageContext.vueInstance.$i18n.mergeLocaleMessage(l, widgetI18ns[l]);
                    }
                  }
                });
              }
              resolve({
                widgets: [definitionJson],
                page: data
              });
            })
            .catch(() => {
              reject();
            });
        })
      });
    } else {
      this.render({ key: this.key, url, title: this.title, targetPosition: this.targetPosition, containerWid: this.containerWid });
    }
  } else if (this.targetPosition === 'widgetTab') {
    if (this.option.pageType != 'url') {
      this.render({
        widgets: [],
        eventHandler: { pageId: this.pageId, pageType: this.option.pageType, urlHashParams },
        meta: this.meta,
        eventParams: this.resolveEventParams(),
        title: this.title,
        key: this.pageId,
        targetPosition: this.targetPosition,
        containerWid: this.containerWid
      });
    } else {
      this.render({ key: this.key, url, title: this.title, targetPosition: this.targetPosition, containerWid: this.containerWid });
    }
  }
};

const WidgetRender = function (option) {
  Event.call(this, option);
};
WidgetRender.prototype = Object.create(Event.prototype);
WidgetRender.prototype.trigger = function () {
  if (this.option.widgets != undefined) {
    if (this.targetPosition === 'widgetLayout' || this.targetPosition === 'widgetModal' || this.targetPosition === 'widgetTab') {
      this.render({
        widgets: this.option.widgets,
        meta: this.meta,
        eventParams: this.resolveEventParams(),
        title: this.title,
        key: this.key,
        targetPosition: this.targetPosition,
        containerWid: this.containerWid
      });
    }
  }
};

// const DialogEvent = function (option) {
//   Event.call(this, option);
// };
// DialogEvent.prototype = Object.create(Event.prototype);
// DialogEvent.prototype.trigger = function () {
//   this.pageContext.emitEvent(`WidgetModal:${this.containerWid}`, {
//     action: 'open'
//   });
// };

const WorkflowEvent = function (option) {
  Event.call(this, option);
};
WorkflowEvent.prototype.trigger = function () { };

const DataManagerEvent = function (option) {
  Event.call(this, option);
};

DataManagerEvent.prototype = Object.create(Event.prototype);
DataManagerEvent.prototype.trigger = function () {
  let parts = this.action.split('.');
  let scriptName = parts[0],
    method = parts[1];
  let _this = this;
  let $script = new this.$evtWidget.__developScript[scriptName].default(this.$targetWidget || this.$evtWidget);
  let eventParams = this.resolveEventParams();
  _this.eventParams = {};
  if (!isEmpty(eventParams)) {
    _this.eventParams = eventParams;
  }
  if (this.broadcastChannel) {
    _this.eventParams.__bc = this.broadcastChannel;
  }
  $script[method](_this);
};

const WidgetEventEmit = function (option) {
  Event.call(this, option);
};
WidgetEventEmit.prototype = Object.create(Event.prototype);
WidgetEventEmit.prototype.trigger = function () {
  let $event = {};
  if (!isEmpty(this.meta)) {
    $event.meta = cloneDeepWith(this.meta, v => {
      if (isSymbol(v)) {
        return undefined;
      }
      if (v && v._isVue === true) {
        return v;
      }
    });
  }
  let eventParams = this.resolveEventParams();
  if (!isEmpty(eventParams)) {
    $event.eventParams = eventParams;
  }
  if (!isEmpty(this.wEventParams)) {
    let str = JSON.stringify(this.wEventParams);
    let compiler = stringTemplate(str);
    try {
      str = compiler(this.meta || {});
    } catch (error) {
      console.error('解析模板字符串错误: ', error);
    }
    $event.wEventParams = JSON.parse(str);
  }

  $event.$evtWidget = this.$evtWidget;
  this.pageContext.emitEvent(`${this.option.eventWid}:${this.option.eventId}`, $event);
};

const JsFunction = function (option) {
  Event.call(this, option);
  // 事件派发没有设置二开脚本时从事件主体中获取
  this.initDevelopJsInstanceFromEventWidgetIfRequired();
};

JsFunction.prototype = Object.create(Event.prototype);
JsFunction.prototype.initDevelopJsInstanceFromEventWidgetIfRequired = function () {
  const _this = this;
  let $evtWidget = _this.$evtWidget;
  if (!$evtWidget || _this.option.$developJsInstance) {
    return;
  }

  let developJs = typeof $evtWidget.developJsInstance === 'function' ? $evtWidget.developJsInstance() : $evtWidget.developJsInstance;
  if (developJs == undefined) {
    developJs = {};
  }
  if ($evtWidget.$pageJsInstance != undefined) {
    developJs[$evtWidget.$pageJsInstance._JS_META_] = $evtWidget.$pageJsInstance;
  }
  _this.option.$developJsInstance = developJs;
};
JsFunction.prototype.trigger = function () {
  let option = this.option;
  let eventParams = this.resolveEventParams();
  try {
    if (option.jsFunction) {
      let parts = option.jsFunction.split('.');
      let developJsInstance = option.$developJsInstance;
      if (developJsInstance != undefined) {
        let jsInstance = typeof developJsInstance === 'function' ? developJsInstance() : developJsInstance;
        if (jsInstance[parts[0]] && jsInstance[parts[0]][parts[1]]) {
          // 执行二开函数前
          if (typeof option.before === 'function') {
            option.before();
          }
          // 调用二开
          jsInstance[parts[0]][parts[1]](
            {
              $evt: option.$evt,
              $evtWidget: option.$evtWidget,
              meta: option.meta,
              eventParams
            },
            success => {
              if (typeof option.after == 'function') {
                option.after(success);
              }
            }
          );
        }
      }
    }

    if (option.customScript) {
      this.pageContext.executeCodeSegment(
        option.customScript,
        {
          meta: option.meta,
          eventParams,
          callback: success => {
            if (typeof option.after == 'function') {
              option.after(success);
            }
          }
        },
        option.$evtWidget,
        success => {
          // 直接返回结果
          if (typeof option.after == 'function') {
            option.after(success);
          }
        }
      );
    }
  } catch (error) {
    console.error('执行二开函数错误: ', error);
  }
};

const Workflow = function (option) {
  Event.call(this, option);
};
Workflow.prototype.trigger = function () {
  const _this = this;
  if (_this.option.sendToApprove) {
    if (_this.meta && _this.meta.selectedRows && _this.meta.selectedRows.length == 0) {
      _this.$evtWidget.$message.error(_this.$evtWidget.$t('WidgetTable.pleaseSelectRecord', '请选择记录！'));
      return;
    }
    import('@workflow/app/web/widget/@develop/WorkflowSendToApprove').then(WorkflowSendToApprove => {
      let links = [];
      let evetnParams = Event.prototype.resolveEventParams.call(_this);
      if (evetnParams.linkTitle && evetnParams.linkUrl) {
        let linkTitles = evetnParams.linkTitle.split(';');
        let linkUrls = evetnParams.linkUrl.split(';');
        for (let index = 0; index < linkTitles.length; index++) {
          links.push({
            title: linkTitles[index],
            url: linkUrls[index]
          });
        }
      }
      let approveOptions = { ui: _this.$evtWidget, flowDefId: _this.option.workflowId, links, ...evetnParams };
      new WorkflowSendToApprove.default(approveOptions).sendToApprove();
    });
  } else {
    let url = `/workflow/work/new/${_this.option.workflowId}`;
    if (window.__INITIAL_STATE__.SYSTEM_ID) {
      url = '/sys/' + window.__INITIAL_STATE__.SYSTEM_ID + '/_' + url;
    }
    if (this.option.broadcastChannel) {
      url += '?__bc=' + this.option.broadcastChannel;
    }
    window.open(url, '_blank');
  }
};

const ApiLinkServiceInvokeEvent = function (option) {
  Event.call(this, option);
};

ApiLinkServiceInvokeEvent.prototype = Object.create(Event.prototype);

ApiLinkServiceInvokeEvent.prototype.trigger = function () {
  let _this = this,
    apiInvocationConfig = this.option.apiInvocationConfig;
  let logicPromise = [];
  let data = this.meta || {};
  // 计算参数
  let pathParams = {},
    queryParams = {},
    headers = {},
    body = {};
  for (let i = 0, len = apiInvocationConfig.parameters.length; i < len; i++) {
    let { valueOption, name, paramType, defaultValue, isRequired } = apiInvocationConfig.parameters[i];
    let p = { name, paramType, defaultValue, isRequired };
    if (valueOption.valueType == 'constant') {
      if (p.paramType == 'path') {
        pathParams[p.name] = valueOption.value;
      } else if (p.paramType == 'query') {
        queryParams[p.name] = valueOption.value;
      } else if (p.paramType == 'header') {
        headers[p.name] = valueOption.value;
      } else if (p.paramType == 'body') {
        body[p.name] = valueOption.value;
      }
    } else {
      logicPromise.push(
        new Promise((resolve, reject) => {
          jsonataEvaluate(valueOption.value, data).then(result => {
            if (p.paramType == 'path') {
              pathParams[p.name] = result;
            } else if (p.paramType == 'query') {
              queryParams[p.name] = result;
            } else if (p.paramType == 'header') {
              headers[p.name] = result;
            } else if (p.paramType == 'body') {
              body[p.name] = result;
            }
            resolve();
          });
        })
      );
    }
  }
  if (apiInvocationConfig.reqSchema && apiInvocationConfig.reqFormatType == 'json') {
    logicPromise.push(
      new Promise((resolve, reject) => {
        evaluateConvertJsonDataFromSchema(apiInvocationConfig.reqSchema, data).then(result => {
          body = result;
          resolve();
        });
      })
    );
  }

  if (apiInvocationConfig.beforeInvokeScript && apiInvocationConfig.beforeInvokeScript.trim().length > 0) {
    var rt = new Function('request', apiInvocationConfig.beforeInvokeScript).apply(_this, [
      {
        pathParams,
        queryParams,
        headers,
        body
      }
    ]);
    if (rt != undefined && typeof rt.then === 'function') {
      logicPromise.push(rt);
    }
  }
  return new Promise((resolve, reject) => {
    Promise.all(logicPromise).then(() => {
      $axios
        .post(`/api-link-proxy/post`, {
          apiOperationUuid: apiInvocationConfig.apiOperationUuid,
          pathParams,
          queryParams,
          headers,
          body
        })
        .then(({ data }) => {
          let end = result => {
            if (apiInvocationConfig.endAction && apiInvocationConfig.endAction.actionType) {
              try {
                if (apiInvocationConfig.endAction.actionType == 'widgetEvent') {
                  if (apiInvocationConfig.endAction.widgetEvent) {
                    let events = Array.isArray(apiInvocationConfig.endAction.widgetEvent)
                      ? apiInvocationConfig.endAction.widgetEvent
                      : [apiInvocationConfig.endAction.widgetEvent];
                    events.forEach(e => {
                      let executeEvent = true;
                      if (e.condition && e.condition.enable && e.condition.conditions.length > 0) {
                        // 判断条件是否成立
                        let { conditions, match } = e.condition;
                        executeEvent = match == 'all';
                        for (let c = 0, clen = conditions.length; c < clen; c++) {
                          let { code, operator, value } = conditions[c];
                          let isTrue = expressionCompare(result, code, operator, value);
                          if (match == 'any') {
                            // 满足任一条件就执行
                            if (isTrue) {
                              executeEvent = true;
                              break;
                            }
                          } else {
                            // 全部情况下，只要一个条件不满足就不执行
                            if (!isTrue) {
                              executeEvent = false;
                              break;
                            }
                          }
                        }
                      }

                      if (executeEvent) {
                        let { eventId, eventParams, eventWid, wEventParams } = e;
                        new DispatchEvent({
                          actionType: 'widgetEvent',
                          $evtWidget: _this.$evtWidget,
                          pageContext: _this.pageContext,
                          meta: {},
                          eventWid,
                          eventId,
                          eventParams,
                          wEventParams
                        }).dispatch();
                      }
                    });
                  }
                } else {
                  new DispatchEvent({
                    ...deepClone(apiInvocationConfig.endAction),
                    meta: result,
                    pageContext: _this.pageContext,
                    $evtWidget: _this.$evtWidget
                  }).dispatch();
                }
              } catch (error) {
                console.error('endAction error', error);
              }
              resolve(result);
            } else {
              resolve(result);
            }
          };
          if (apiInvocationConfig.dataTransformMethod == 'setSchemaValue') {
            if (apiInvocationConfig.resTransformSchema) {
              evaluateConvertJsonDataFromSchema(apiInvocationConfig.resTransformSchema, data).then(result => {
                end(result);
              });
            }
          } else if (apiInvocationConfig.dataTransformMethod == 'function' && apiInvocationConfig.dataTransformFunction) {
            var rt = new Function('response', apiInvocationConfig.dataTransformFunction).apply(_this, [data]);
            if (rt != undefined && typeof rt.then === 'function') {
              rt.then(rs => {
                end(rs);
              });
            } else {
              end(rt);
            }
          } else {
            end(data);
          }
        })
        .catch(error => { });
    });
  });
};

export const DispatchEvent = function (option) {
  if (option.actionType === 'redirectPage') {
    this.event = new PageEvent(option);
  }
  // else if (option.actionType === 'openModalDialog') {
  //   this.event = new DialogEvent(option);
  // }
  else if (option.actionType === 'workflow') {
    this.event = new Workflow(option);
  } else if (option.actionType === 'dataManager') {
    this.event = new DataManagerEvent(option);
  } else if (option.actionType === 'widgetEvent') {
    this.event = new WidgetEventEmit(option);
  } else if (option.actionType === 'jsFunction') {
    this.event = new JsFunction(option);
  } else if (option.actionType == 'widgetRender') {
    this.event = new WidgetRender(option);
  } else if (option.actionType == 'apiLinkService') {
    this.event = new ApiLinkServiceInvokeEvent(option);
  } else {
    throw new Error('未知事件处理');
  }
};

DispatchEvent.prototype.dispatch = function () {
  this.event.trigger();
};
