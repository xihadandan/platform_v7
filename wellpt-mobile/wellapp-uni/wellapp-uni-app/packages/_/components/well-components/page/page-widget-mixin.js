"use strict";
import { isEmpty, get, debounce, merge } from "lodash";
import {
  TableViewGetCount,
  dataStoreCount,
  dataModelCount,
  appContext,
  storage,
  utils,
  pageContext,
} from "wellapp-uni-framework";

export default {
  inject: ["namespace", "unauthorizedResource", "$pageJsInstance", "dyform", "workviewContext", "widgetDyformContext"],
  provide() {
    return {
      widgetContext: this,
    };
  },
  props: {
    id: String,
    widget: Object, // 组件定义
    widgetsOfParent: Array, // 父组件的所有子组件
    parent: Object, // 父组件
    index: Number, //当前组件在父组件的子组件列表的序号
  },
  created() {
    if (!this.deviceVisible) {
      return;
    }
    if (this.hiddenByRule !== undefined) {
      this.calculateWidgetVisible = !this.hiddenByRule;
    }

    let widget = this.$options.propsData.widget;
    this.$developJsInstance = {};
    if (widget.configuration.jsModules && widget.configuration.jsModules.length > 0) {
      for (let i = 0, len = widget.configuration.jsModules.length; i < len; i++) {
        let js = widget.configuration.jsModules[i].key;
        try {
          this.$developJsInstance[js] = appContext.jsInstance(js, this);
        } catch (error) {
          console.error(error);
        }
      }
    }
    pageContext.registerComponent(this.widget.id, this);
    this.refreshWidgetVisibleByCondition = debounce(this.refreshWidgetVisibleByCondition.bind(this), 200);
    this.afterChangeableDependDataChanged = debounce(this.afterChangeableDependDataChanged.bind(this), 200);
    this.invokeDevelopmentMethod("created");
  },
  computed: {
    _vShowByWorkflowData() {
      let data = {};
      if (this.workviewContext != undefined) {
        data = {
          taskId: this.workviewContext.workData.taskId,
          taskName: this.workviewContext.workData.taskName,
          flowDefId: this.workviewContext.workData.flowDefId,
          version: parseFloat(this.workviewContext.workData.version),
        };
      }
      return {
        _WORKFLOW_: data,
      };
    },
    _vShowByDateTime() {
      let now = new Date();
      return {
        _DATETIME_: {
          currentDateString: parseInt(now.format("YYYYMMDD")),
          currentFullDateTimeString: parseInt(now.format("YYYYMMDDHHmmss")),
          currentWeekDay: now.getDay(),
          currentMonth: now.getMonth() + 1,
          currentQuarter: now.getQuarter(),
          currentYear: now.getFullYear(),
          currentDay: now.getDate(),
          currentHour: now.getHours(),
        },
      };
    },
    _vShowByUserData() {
      let data = {};
      if (this._$USER) {
        data.userId = this._$USER.userId;
        data.userName = this._$USER.userName;
        data.loginName = this._$USER.loginName;
        data.roles = this._$USER.roles;
        // 解析出默认组织中的单位ID、部门ID、职位ID以及名称的信息
        let userSystemOrgDetails = this._$USER.userSystemOrgDetails;
        if (userSystemOrgDetails) {
          let details = userSystemOrgDetails.details;
          if (details.length) {
            let d = details[0]; // 只取默认组织上的
            let { mainJob, otherJobs, mainDept, otherDepts, unit } = d;
            data.mainJobId = mainJob ? mainJob.eleId : undefined;
            data.mainJobIdPath = mainJob ? mainJob.eleIdPath : undefined;
            data.mainJobName = mainJob ? mainJob.name : undefined;
            data.mainJobNamePath = mainJob ? mainJob.eleNamePath : undefined;
            data.mainDeptId = mainDept ? mainDept.eleId : undefined;
            data.mainDeptIdPath = mainDept ? mainDept.eleIdPath : undefined;
            data.mainDeptName = mainDept ? mainDept.name : undefined;
            data.mainDeptNamePath = mainDept ? mainDept.eleNamePath : undefined;
            data.unitId = unit ? unit.eleId : undefined;
            data.unitName = unit ? unit.name : undefined;
            data.jobIds = [];
            if (data.mainJobId) {
              data.jobIds.push(data.mainJobId);
            }
            if (otherJobs && otherJobs.length) {
              otherJobs.forEach((job) => {
                data.jobIds.push(job.eleId);
              });
            }
            data.jobNames = [];
            if (data.mainJobName) {
              data.jobNames.push(data.mainJobName);
            }
            if (otherJobs && otherJobs.length) {
              otherJobs.forEach((job) => {
                data.jobNames.push(job.name);
              });
            }
            data.deptIds = [];
            if (data.mainDeptId) {
              data.deptIds.push(data.mainDeptId);
            }
            if (otherDepts && otherDepts.length) {
              otherDepts.forEach((dept) => {
                data.deptIds.push(dept.eleId);
              });
            }
            data.deptNames = [];
            if (data.mainDeptName) {
              data.deptNames.push(data.mainDeptName);
            }
            if (otherDepts && otherDepts.length) {
              otherDepts.forEach((dept) => {
                data.deptNames.push(dept.name);
              });
            }
          }
        }
      }
      return {
        _USER_: data,
      };
    },

    vShow() {
      return this.calculateWidgetVisible;
    },
    vWatchChangeableDependDataMD5Changed() {
      let obj = {};
      let dyform = this.widgetDyformContext != undefined ? this.widgetDyformContext.dyform : this.dyform;
      if (dyform) {
        // 主表
        obj.subformDataMD5 = dyform.subformDataMD5;
        obj.formData = dyform.formData;
      } else if (this.form != undefined) {
        obj.formData = this.form.formData;
      }
      // if (this.$attrs.isSubformCell && this.form != undefined) {
      //   // 获取当前从表行数据
      //   obj.rowFormData = {
      //     ...this.form.formData
      //   };
      // }
      return utils.md5(JSON.stringify(obj));
    },
    vDefineEvents() {
      let events = {};
      if (this.widget.configuration.defineEvents) {
        for (let i = 0, len = this.widget.configuration.defineEvents.length; i < len; i++) {
          events[this.widget.configuration.defineEvents[i].id] = this.widget.configuration.defineEvents[i];
        }
      }
      if (this.supperDefaultEvents) {
        for (let i = 0, len = this.supperDefaultEvents.length; i < len; i++) {
          this.supperDefaultEvents[i].default = true;
          events[this.supperDefaultEvents[i].id] = this.supperDefaultEvents[i];
        }
      }

      // 默认事件
      if (this.defaultEvents) {
        for (let i = 0, len = this.defaultEvents.length; i < len; i++) {
          this.defaultEvents[i].default = true;
          events[this.defaultEvents[i].id] = this.defaultEvents[i];
        }
      }

      return events;
    },
    customClassCom() {
      let _self = this;
      let com = [];
      if (this.customClass) {
        com.push(this.customClass);
      }
      let configuration = _self.widget.configuration || {};
      let customClass = configuration.customClass;
      if (customClass) {
        com.push(customClass);
      }
      console.log(configuration);
      com.push("widget_" + _self.widget.id);
      return com.join(" ");
    },
  },
  data() {
    const _self = this;
    let configuration = _self.widget.configuration || {};
    let isEmptyUniJsModule = isEmpty(configuration.uniJsModule);
    let deviceVisible = true;
    if (this.widget.configuration.deviceVisible && !this.widget.configuration.deviceVisible.includes("mobile")) {
      deviceVisible = false;
    }
    return {
      isMounted: false,
      deviceVisible,
      calculateWidgetVisible: deviceVisible === false ? false : true,
      isEmptyUniJsModule,
      isUniApp: true, // 用于标记移动端
    };
  },
  methods: {
    refreshWidgetVisibleByCondition() {
      if (this.widget.configuration.defaultVisible != undefined && this.hiddenByRule == undefined) {
        let _this = this;
        this.calculateVisibleByCondition(
          this.widget.configuration.defaultVisibleVar,
          this.widget.configuration.defaultVisible,
          (visible) => {
            _this.calculateWidgetVisible = visible;
          }
        );
      }
    },
    widgetDependentVariableDataSource(includeSubform = true) {
      let dyform = this.widgetDyformContext != undefined ? this.widgetDyformContext.dyform : this.dyform;
      let source = {
        ...(dyform != undefined ? dyform.formData : {}), // 兼容旧配置，旧配置的表字段数据在顶层
        _FORM_DATA_:
          typeof this.getDyformData == "function" ? this.getDyformData() : dyform != undefined ? dyform.formData : {}, // 主表数据
        ...this._vShowByUserData,
        ...this._vShowByDateTime,
        ...this._vShowByWorkflowData,
        // _URL_PARAM_: this.vUrlParams,
        __DYFORM__:
          dyform != undefined
            ? {
                editable: dyform.displayState == "edit",
              }
            : {},
      };
      // if (this.$attrs.isSubformCell && this.form != undefined) {
      //   // 获取当前从表行数据
      //   source.__CURRENT_ROW_FORM_DATA__ = {
      //     ...this.form.formData,
      //   };
      // }
      // if (dyform && dyform.subform) {
      //   let subformData = {};
      //   for (let subformUuid in dyform.subform) {
      //     let subforms = dyform.subform[subformUuid];
      //     if (subforms) {
      //       if (subforms.length > 0) {
      //         subformData[subformUuid] = subforms.map((item) => item.formData);
      //       }
      //     }
      //   }
      //   source._SUBFORM_DATA_ = subformData;
      // }
      // console.log("widgetDependentVariableDataSource", source);
      return source;
    },
    calculateDataConditionControlResult(controlConfig) {
      return new Promise((resolve, reject) => {
        // 根据数据决定是否控制
        if (controlConfig && controlConfig.enable) {
          let promise = [];
          let _compareData = this.widgetDependentVariableDataSource();
          if (controlConfig.match != undefined && controlConfig.conditions != undefined) {
            // 多组条件判断
            let match = controlConfig.match == "all";
            for (let i = 0, len = controlConfig.conditions.length; i < len; i++) {
              let { code, codeValue, operator, value, valueType } = controlConfig.conditions[i];
              if (!code) {
                continue;
              }
              if (code.endsWith(".") && codeValue) {
                code = `${code}${codeValue}`;
              }
              if (valueType == "variable") {
                try {
                  value = get(_compareData, value);
                } catch (error) {
                  console.error("无法解析变量值", value);
                }
              }
              let result = utils.expressionCompare(_compareData, code, operator, value, true);
              promise.push(result);
            }
            Promise.all(promise)
              .then((results) => {
                if (results != undefined) {
                  for (let result of results) {
                    if (controlConfig.match == "all" && !result) {
                      match = false;
                      break;
                    }
                    if (controlConfig.match == "any" && result) {
                      match = true;
                      break;
                    }
                  }
                }

                resolve(match);
              })
              .catch((error) => {
                console.error("calculateDataConditionControlResult error", error);
                reject();
              });
          } else {
            let { code, codeValue, value, operator, valueType } = controlConfig;
            if (code.endsWith(".") && codeValue) {
              code = `${code}${codeValue}`;
            }
            if (valueType == "variable") {
              try {
                value = get(_compareData, value);
              } catch (error) {
                console.error("无法解析变量值", value);
              }
            }
            utils
              .expressionCompare(_compareData, code, operator, value, true)
              .then((result) => {
                resolve(result);
              })
              .catch(() => {
                reject();
              });
          }
        } else {
          // 无启用则直接返回true
          resolve(true);
        }
      });
    },
    calculateVisibleByCondition(defaultVisibleVar, defaultVisible, callback) {
      return new Promise((resolve, reject) => {
        this.calculateDataConditionControlResult(defaultVisibleVar).then((match) => {
          if (typeof callback == "function") {
            callback(match ? defaultVisible : !defaultVisible);
          }
          resolve(match ? defaultVisible : !defaultVisible);
        });
      });
    },
    /**
     * 重写翻译方法：主要针对组件实例上的国际化信息进行解析，支持返回默认值，针对未做国际化的相关配置
     * @returns
     */
    $t() {
      let params = {},
        defaultValue = undefined;
      if (arguments.length > 1) {
        if (arguments[1] != undefined) {
          if (typeof arguments[1] !== "string") {
            // 参数化翻译
            params = arguments[1];
          } else {
            // 默认值
            defaultValue = arguments[1];
          }
        }
        if (arguments[2] != undefined && typeof arguments[2] == "string") {
          // 默认值
          defaultValue = arguments[2];
        }
      }
      let wKey = `Widget.${this.widget.id}.${arguments[0]}`;
      if (this.$te(wKey)) {
        // 组件内的国际化信息，按照国际化信息嵌套路径优先解析: Widget.组件ID.key
        return this.$i18n.t.apply(this.$i18n, [wKey, params]);
      } else if (this.$i18n._root && this.$i18n._root.$te(wKey)) {
        // 从顶级国际化信息获取
        return this.$i18n._root.$t.apply(this.$i18n._root, [wKey, params]);
      } else if (this.$te(arguments[0])) {
        return this.$i18n.t.apply(this.$i18n, [arguments[0], params]);
      } else if (this.$i18n._root && this.$i18n._root.$te(arguments[0])) {
        // 从顶级国际化信息获取
        return this.$i18n._root.$t.apply(this.$i18n._root, [arguments[0], params]);
      } else if (
        this.$i18n.fallbackLocale &&
        this.$i18n._te(arguments[0], this.$i18n.fallbackLocale, this.$i18n.messages)
      ) {
        return this.$i18n._t(arguments[0], this.$i18n.fallbackLocale, this.$i18n.messages, params);
      }
      return defaultValue || "";
    },
    mergeWidgetI18nMessages(i18n, id) {
      let allMessage = this.$i18n._getMessages();
      if (!id) {
        id = this.widget.id;
      }
      if (i18n != undefined) {
        for (let m in i18n) {
          if (m == this.$i18n.locale) {
            let widgetI18n = allMessage[m] && allMessage[m].Widget;
            if (!widgetI18n) {
              widgetI18n = {};
            }
            if (!widgetI18n[id]) {
              widgetI18n[id] = {};
            }
            merge(widgetI18n[id], i18n[m]);
            this.$i18n.mergeLocaleMessage(m, {
              Widget: widgetI18n,
            });
          }
        }
      }
    },
    // 将i18ns格式的i18n，实例：fileSourceOptions
    i18nsToI18n(i18ns, code = "code") {
      let i18n = {};
      for (let m in i18ns) {
        i18ns.forEach((item) => {
          if (!i18n[item.locale]) {
            i18n[item.locale] = {};
          }
          if (item.elementId && item[code].indexOf(item.elementId) > -1) {
            item[code] = item[code].split(".");
            item[code].splice(item[code].indexOf(item.elementId), 1);
            item[code].join(".");
          }
          i18n[item.locale][item[code]] = item.content;
        });
      }
      return i18n;
    },
    /**
     * 向上通知显示变更
     */
    emitVisibleChange(visible = true) {
      this.$emit("visibleChange", {
        visible,
      });
      // 发布组件显示变更通知
      this.pageContext.emitEvent(`Widget:${this.widget.id}:VisibleChange`, visible);
      // 向上通知子元素的显示变更通知，由各自组件处理子元素的显示状态对应逻辑
      if (this.parent && this.parent.id) {
        this.pageContext.emitEvent(`Widget:${this.parent.id}:Child:VisibleChange`, {
          id: this.widget.id,
          visible,
        });
      }
    },
    // 获取徽章数量
    getBadgeCount: function (item, callback) {
      let _self = this;
      let badge = item.badge;
      if (badge && badge.enable) {
        let { badgeSourceType, dataSourceId, dataModelUuid, jsFunction } = badge;
        if (badgeSourceType === "dataSource") {
          dataStoreCount(dataSourceId, callback || _self.setBadgeCount, item);
        } else if (badgeSourceType == "dataModel") {
          dataModelCount(dataModelUuid, callback || _self.setBadgeCount, item);
        } else if (badgeSourceType == "jsFunction") {
          let result = appContext.invokeJsFunction(jsFunction);
          if (result.then) {
            result.then((data) => {
              if (typeof callback == "function") {
                callback(item, data);
              } else {
                _self.setBadgeCount(item, data);
              }
            });
          } else {
            if (typeof callback == "function") {
              callback(item, result);
            } else {
              _self.setBadgeCount(item, result);
            }
          }
        }
      }
    },
    setBadgeCount: function (item, data) {
      let _self = this;
      _self.$set(item, "badgeNum", data);
    },
    invokeDevelopmentMethod() {
      try {
        for (let k in this.$developJsInstance) {
          let args = Array.from(arguments),
            method = args.shift(0); // 第一个参数是访问方法，移出后剩余的参数组就是方法入参
          if (this.$developJsInstance[k] && typeof this.$developJsInstance[k][method] === "function") {
            let returnObj = this.$developJsInstance[k][method].apply(this.$developJsInstance[k], args);
            if (returnObj != undefined) {
              // 二开有返回对象，直接返回
              return returnObj;
            }
            // 无则继续执行其他二开脚本的方法
          }
        }
      } catch (error) {
        console.error("调用二开脚本方法失败：", error);
      }
    },

    executeEvent() {
      // 非默认事件为开发自定义
      let args = Array.from(arguments);
      let e = args.shift(0);
      try {
        if (e.codeSource && (e.codeSource === "codeEditor" || e.codeSource.includes("codeEditor"))) {
          // 执行自定义代码块
          if (e.customScript) {
            this.pageContext.executeCodeSegment(e.customScript, args[0], this);
          }
        }
      } catch (error) {
        console.error("执行自定义代码块异常", error);
      }
      try {
        if (
          e.codeSource &&
          (e.codeSource === "developJsFileCode" || e.codeSource.includes("developJsFileCode")) &&
          e.jsFunction
        ) {
          // 执行二开脚本方法
          let parts = e.jsFunction.split(".");
          if (this.$developJsInstance != undefined) {
            if (this.$developJsInstance[parts[0]] && this.$developJsInstance[parts[0]][parts[1]]) {
              // 调用二开
              this.$developJsInstance[parts[0]][parts[1]].apply(this.$developJsInstance[parts[0]], args);
            }
          }

          if (this.$pageJsInstance && this.$pageJsInstance._JS_META_ === parts[0] && this.$pageJsInstance[parts[1]]) {
            this.$pageJsInstance[parts[1]].apply(this.$pageJsInstance, args);
          }
        }
      } catch (error) {
        console.error("执行二开脚本异常", error);
      }
      try {
        if (
          e.codeSource &&
          (e.codeSource === "widgetMethod" || e.codeSource.includes("widgetMethod")) &&
          e.widgetMethod
        ) {
          // 执行组件内部方法
          if (this.widget.wtype === "WidgetTemplate") {
            // 模板组件执行的是模板内容的组件方法
            this.wTemplate[e.widgetMethod].apply(this.wTemplate, args);
          } else {
            this[e.widgetMethod].apply(this, args);
          }
        }
      } catch (error) {
        console.log("执行组件内部方法异常", error);
      }
      try {
        if (e.codeSource && (e.codeSource == "pageEvent" || e.codeSource.includes("pageEvent"))) {
          this.dispatchEventHandler({
            actionType: "redirectPage",
            meta: e.meta,
            $evtWidget: this,
            key: e.pageEvent.pageId || generateId(),
            pageContext: this.pageContext,
            ...e.pageEvent,
          });
        }
      } catch (error) {
        console.log("执行页面派发事件异常", error);
      }
      try {
        if (e.codeSource && (e.codeSource == "widgetEvent" || e.codeSource.includes("widgetEvent"))) {
          this.dispatchConditionWidgetEvent(e.widgetEvent, this.vPageState);
        }
      } catch (error) {
        console.log("执行组件事件异常", error);
      }
    },
    /**
     * 执行事件设置的配置
     * @param {事件设置} eventHandler
     */
    dispatchEventHandler(eventHandler) {
      if (eventHandler.pageContext == undefined) {
        eventHandler.pageContext = this.pageContext;
      }

      this.appContext.dispatchEvent(eventHandler);
    },

    dispatchConditionWidgetEvent(widgetEvent, dependentOnData) {
      if (widgetEvent) {
        let events = Array.isArray(widgetEvent) ? widgetEvent : [widgetEvent];
        events.forEach((e) => {
          let executeEvent = true;
          if (e.condition && e.condition.enable && e.condition.conditions.length > 0) {
            // 判断条件是否成立
            let { conditions, match } = e.condition;
            executeEvent = match == "all";
            for (let c = 0, clen = conditions.length; c < clen; c++) {
              let { code, operator, value } = conditions[c];
              let isTrue = utils.expressionCompare(dependentOnData, code, operator, value);
              if (match == "any") {
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
            for (let item of Array.isArray(e.event) ? e.event : [e]) {
              let { eventId, eventParams, eventWid, wEventParams } = item;
              let handler = {
                actionType: "widgetEvent",
                $evtWidget: this,
                meta: {},
                eventWid,
                eventId,
                eventParams,
                wEventParams,
              };

              this.dispatchEventHandler(handler);
            }
          }
        });
      }
    },

    fetchDataByApiLinkInvocation(apiInvocationConfig) {
      // api接口移动端暂时无法使用
      return new Promise((resolve, reject) => {
        resolve([]);
      });
      let _this = this;
      let logicPromise = [];
      let data = this.widgetDependentVariableDataSource();
      // 计算参数
      let pathParams = {},
        queryParams = {},
        headers = {},
        body = {};
      for (let i = 0, len = apiInvocationConfig.parameters.length; i < len; i++) {
        let { valueOption, name, paramType, defaultValue, isRequired } = apiInvocationConfig.parameters[i];
        let p = { name, paramType, defaultValue, isRequired };
        if (valueOption.valueType == "constant") {
          if (p.paramType == "path") {
            pathParams[p.name] = valueOption.value;
          } else if (p.paramType == "query") {
            queryParams[p.name] = valueOption.value;
          } else if (p.paramType == "header") {
            headers[p.name] = valueOption.value;
          } else if (p.paramType == "body") {
            body[p.name] = valueOption.value;
          }
        } else {
          logicPromise.push(
            new Promise((resolve, reject) => {
              utils.jsonataEvaluate(valueOption.value, data).then((result) => {
                if (p.paramType == "path") {
                  pathParams[p.name] = result;
                } else if (p.paramType == "query") {
                  queryParams[p.name] = result;
                } else if (p.paramType == "header") {
                  headers[p.name] = result;
                } else if (p.paramType == "body") {
                  body[p.name] = result;
                }
                resolve();
              });
            })
          );
        }
      }
      if (apiInvocationConfig.reqSchema && apiInvocationConfig.reqFormatType == "json") {
        logicPromise.push(
          new Promise((resolve, reject) => {
            utils.evaluateConvertJsonDataFromSchema(apiInvocationConfig.reqSchema, data).then((result) => {
              body = result;
              resolve();
            });
          })
        );
      }

      if (apiInvocationConfig.beforeInvokeScript && apiInvocationConfig.beforeInvokeScript.trim().length > 0) {
        var rt = new Function("request", apiInvocationConfig.beforeInvokeScript).apply(_this, [
          {
            pathParams,
            queryParams,
            headers,
            body,
          },
        ]);
        if (rt != undefined && typeof rt.then === "function") {
          logicPromise.push(rt);
        }
      }
      return new Promise((resolve, reject) => {
        Promise.all(logicPromise).then(() => {
          this.$axios
            .post(`/api-link-proxy/post`, {
              apiOperationUuid: apiInvocationConfig.apiOperationUuid,
              pathParams,
              queryParams,
              headers,
              body,
            })
            .then(({ data }) => {
              let end = (result) => {
                if (apiInvocationConfig.endAction && apiInvocationConfig.endAction.actionType) {
                  try {
                    if (apiInvocationConfig.endAction.actionType == "widgetEvent") {
                      _this.dispatchConditionWidgetEvent(apiInvocationConfig.endAction.widgetEvent, result);
                    } else {
                      _this.dispatchEventHandler({
                        ...deepClone(apiInvocationConfig.endAction),
                        meta: result,
                        $evtWidget: _this,
                      });
                    }
                  } catch (error) {
                    console.error("endAction error", error);
                  }
                  resolve(result);
                } else {
                  resolve(result);
                }
              };
              if (apiInvocationConfig.dataTransformMethod == "setSchemaValue") {
                if (apiInvocationConfig.resTransformSchema) {
                  utils
                    .evaluateConvertJsonDataFromSchema(apiInvocationConfig.resTransformSchema, data)
                    .then((result) => {
                      end(result);
                    });
                }
              } else if (
                apiInvocationConfig.dataTransformMethod == "function" &&
                apiInvocationConfig.dataTransformFunction
              ) {
                var rt = new Function("response", apiInvocationConfig.dataTransformFunction).apply(_this, [data]);
                if (rt != undefined && typeof rt.then === "function") {
                  rt.then((rs) => {
                    end(rs);
                  });
                } else {
                  end(rt);
                }
              } else {
                end(data);
              }
            })
            .catch((error) => {});
        });
      });
    },
    afterChangeableDependDataChanged() {},
  },

  mounted() {
    this.isMounted = true;
    if (!this.deviceVisible) {
      return;
    }
    this.refreshWidgetVisibleByCondition();
    this.afterChangeableDependDataChanged();
    this.invokeDevelopmentMethod("mounted");
  },

  beforeMount() {
    if (!this.deviceVisible) {
      return;
    }
    this.invokeDevelopmentMethod("beforeMount");
    // 初始化自定义事件处理
    let _this = this;
    for (let k in this.vDefineEvents) {
      let e = this.vDefineEvents[k];
      if (!["created", "beforeMount", "mounted"].includes(e.id)) {
        if (this.$props.isSubformCell) {
          return;
        }
        // 对外开放的事件
        _this.pageContext.offEvent(`${this.widget.id}:${e.id}`).handleEvent(`${this.widget.id}:${e.id}`, function () {
          if (!e.default) {
            let args = Array.from(arguments);
            args = [e].concat(args); // 参数合并
            _this.executeEvent.apply(_this, args);
          } else if (_this[e.id]) {
            // 默认的事件，存在对应的方法函数，执行该方法函数
            _this[e.id].apply(_this, arguments);
          }
        });
      }
    }
  },

  unmounted() {
    this.pageContext.unregisterComponent(this.widget.id);
  },

  watch: {
    vWatchChangeableDependDataMD5Changed: {
      handler(v, o) {
        this.refreshWidgetVisibleByCondition();
        this.afterChangeableDependDataChanged();
      },
    },
    vShow: {
      // 显示或者隐藏切换后执行事件
      handler(v, o) {
        if (this.widget.configuration.visibleToggle) {
          this.executeEvent(this.widget.configuration.visibleToggle, { $widget: this, visible: v });
        }
      },
    },
  },
};
