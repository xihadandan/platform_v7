import {
  executeCodeSegment,
  jsonValue,
  jsonataEvaluate,
  expressionCompare,
  queryString,
  queryStringify,
  generateId,
  evaluateConvertJsonDataFromSchema,
  deepClone
} from '../utils/util';
import { DispatchEvent } from '../lib/dispatchEvent';
import md5 from '@framework/vue/utils/md5';
import { kebabCase, merge, debounce, throttle, get } from 'lodash';
import moment from 'moment';

export default {
  inject: [
    'pageContext',
    'namespace',
    'vPageState',
    'dyform',
    '$pageJsInstance',
    'designMode',
    'currentWindow',
    'parentLayContentId',
    '$tempStorage',
    '$localStorage',
    '$workView',
    'urlHashParams',
    'widgetContext',
    'USER'
  ],

  props: {
    widget: Object, // 组件定义
    widgetsOfParent: Array, // 父组件的所有子组件
    designer: Object, // 设计器实例
    parent: Object, // 父组件
    index: Number, //当前组件在父组件的子组件列表的序号
    vStyle: Object // 样式提供
  },

  provide() {
    return {
      widgetContext: this
    };
  },
  // i18n: {
  //   messages: {}
  // },
  computed: {
    configuration() {
      return this.widget.configuration;
    },
    deviceVisible() {
      if (this.widget.configuration.deviceVisible && !this.widget.configuration.deviceVisible.includes('pc')) {
        return false;
      }
      return true;
    },
    _vShowByWorkflowData() {
      let data = {};
      if (this.$workView != undefined) {
        data = {
          taskId: this.$workView.workData.taskId,
          taskName: this.$workView.workData.taskName,
          flowDefId: this.$workView.workData.flowDefId,
          version: parseFloat(this.$workView.workData.version)
        };
      }
      return {
        _WORKFLOW_: data
      };
    },
    _vShowByDateTime() {
      let now = moment(this._$SERVER_TIMESTAMP);
      return {
        _DATETIME_: {
          currentDateString: parseInt(now.format('YYYYMMDD')),
          currentFullDateTimeString: parseInt(now.format('YYYYMMDDHHmmss')),
          currentWeekDay: now.day() == 0 ? 7 : now.day(),
          currentMonth: now.month() + 1,
          currentQuarter: now.quarter(),
          currentYear: now.year(),
          currentDay: now.date(),
          currentHour: now.hour()
        }
      };
    },
    _vShowByUserData() {
      let data = {};
      let user = this.USER || this._$USER;
      if (user) {
        data.userId = user.userId;
        data.userName = user.userName;
        data.loginName = user.loginName;
        data.roles = user.roles;
        data.systemId = this._$SYSTEM_ID;
        // 解析出默认组织中的单位ID、部门ID、职位ID以及名称的信息
        let userSystemOrgDetails = user.userSystemOrgDetails;
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
              otherJobs.forEach(job => {
                data.jobIds.push(job.eleId);
              });
            }
            data.jobNames = [];
            if (data.mainJobName) {
              data.jobNames.push(data.mainJobName);
            }
            if (otherJobs && otherJobs.length) {
              otherJobs.forEach(job => {
                data.jobNames.push(job.name);
              });
            }
            data.deptIds = [];
            if (data.mainDeptId) {
              data.deptIds.push(data.mainDeptId);
            }
            if (otherDepts && otherDepts.length) {
              otherDepts.forEach(dept => {
                data.deptIds.push(dept.eleId);
              });
            }
            data.deptNames = [];
            if (data.mainDeptName) {
              data.deptNames.push(data.mainDeptName);
            }
            if (otherDepts && otherDepts.length) {
              otherDepts.forEach(dept => {
                data.deptNames.push(dept.name);
              });
            }
          }
        }
      }
      return {
        _USER_: data
      };
    },
    vShow() {
      return this.calculateWidgetVisible;
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
    defaultLifecycleEvents() {},
    vMargin() {
      if (this.widget.configuration.style && this.widget.configuration.style.margin) {
        let p = [];
        this.widget.configuration.style.margin.forEach(v => {
          if (v && v.indexOf('var') > -1) {
            p.push(v);
          } else {
            p.push((v || 0) + 'px');
          }
        });
        return p.join(' ');
      }
      return '0px';
    },
    vPadding() {
      if (this.widget.configuration.style && this.widget.configuration.style.padding) {
        let p = [];
        this.widget.configuration.style.padding.forEach(v => {
          if (v && v.toString().indexOf('var') > -1) {
            p.push(v);
          } else {
            p.push((v || 0) + 'px');
          }
        });
        return p.join(' ');
      }
      return '0px';
    },
    vBackground() {
      if (this.widget.configuration.style && this.widget.configuration.style.background) {
        let background = this.widget.configuration.style.background;
        if (typeof background == 'string') {
          return { background: background };
        } else if (typeof background == 'object') {
          return background;
        }
      }
      return { background: 'var(--w-fill-color-base)' }; //默认白底
    },
    widgetClass() {
      let className = [];
      if (this.isMounted && this.$el && this.$el.nodeName !== '#comment') {
        let wClass = kebabCase(this.widget.wtype);
        className.push('widget');
        className.push(`${wClass}`);
        if (this.widget.subtype) {
          wClass = kebabCase(this.widget.subtype);
          className.push(wClass);
        }
        if (this.widget.configuration.isDatabaseField) {
          className.push('field-widget');
          if (this.widget.configuration.labelPosition == 'top') {
            className.push('label-top');
          }
        }
      }
      return className;
    },
    vUrlParams() {
      if (!EASY_ENV_IS_NODE) {
        return queryString(location.search.substr(1));
      }
      return {};
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
      if (this.widgetFormContext != undefined) {
        obj.widgetFormData = this.widgetFormContext.form;
      }

      if (this.$attrs.isSubformCell && this.form != undefined) {
        // 获取当前从表行数据
        obj.rowFormData = {
          ...this.form.formData
        };
      }
      return md5(JSON.stringify(obj));
    }
  },
  data() {
    let calculateWidgetVisible = true;
    if (!this.designMode) {
      if (this.deviceVisible === false) {
        calculateWidgetVisible = false;
      }
    }

    if (
      this.widget.configuration.defaultVisible != undefined &&
      (this.widget.configuration.defaultVisibleVar == undefined ||
        this.widget.configuration.defaultVisibleVar.enable === false ||
        this.widget.configuration.defaultVisibleVar.conditions.length == 0)
    ) {
      calculateWidgetVisible = this.widget.configuration.defaultVisible;
    }

    return {
      isMounted: false,
      calculateWidgetVisible
    };
  },

  beforeCreate() {
    if (this.$attrs.stopEvent) {
      return;
    }
    let widget = this.$options.propsData.widget;
    // beforeCreate 数据未初始化完，无法访问倒属性、方法等实例对象
    if (EASY_ENV_IS_BROWSER) {
      // 浏览器环境: 加载二开脚本
      this.$developJsInstance = {};
      if (widget.configuration.jsModules && widget.configuration.jsModules.length > 0) {
        for (let i = 0, len = widget.configuration.jsModules.length; i < len; i++) {
          let js = widget.configuration.jsModules[i].key;
          if (!this.__developScript) {
            console.error(`widget develop script not imported!`, this.__developScript, js);
          } else if (this.__developScript[js]) {
            this.$developJsInstance[js] = new this.__developScript[js].default(this);
          } else {
            console.error(`widget develop module [${js}] not loaded!`, this.__developScript);
          }
        }
      }
    }
  },
  created() {
    if (!this.deviceVisible) {
      this.calculateWidgetVisible = false;
      return;
    }
    if (this.$attrs.stopEvent) {
      return;
    }
    if (this.hiddenByRule !== undefined) {
      this.calculateWidgetVisible = !this.hiddenByRule;
    }

    // if (!this.$store.hasModule(this.widget.id)) {
    //   console.log(`${this.widget.title} -> 注册组件级状态管理: ${this.widget.id}`);
    //   let _title = this.widget.title;
    //   this.$store.registerModule(this.widget.id, {
    //     state: { $event: undefined },
    //     namespaced: true,
    //     mutations: {
    //       set(state, payload) {
    //         if (typeof payload === 'function') {
    //           payload.call(this, state);
    //         } else if (typeof payload === 'object') {
    //           extend(true, state, payload);
    //           if (payload.$event != undefined) {
    //             state.$event = payload.$event;
    //           }
    //           console.log(`${_title} -> 状态管理更新: `, state);
    //         }
    //       }
    //     }
    //   });
    // }

    // if (EASY_ENV_IS_NODE) {
    //   // 服务端渲染，需要返回二开脚本ID给前端
    //   if (this.widget.configuration.jsModules && this.widget.configuration.jsModules.length) {
    //     if (this.$root._data.__developmentJsMeta === undefined) {
    //       this.$root._data.__developmentJsMeta = [];
    //     }
    //     this.widget.configuration.jsModules.forEach(j => {
    //       this.$root._data.__developmentJsMeta.push(j.key);
    //     });
    //   }
    // }
    this.refreshWidgetVisibleByCondition = debounce(this.refreshWidgetVisibleByCondition.bind(this), 200);
    this.afterChangeableDependDataChanged = debounce(this.afterChangeableDependDataChanged.bind(this), 200);
    this.executeLifecycleHook('created');
    this.invokeDevelopmentMethod('created');
    //FIXME: 对组件配置项的权限判断
    if (typeof this.filterConfigurationSecurity === 'function') {
      this.filterConfigurationSecurity();
    }
  },
  beforeMount() {
    if (!this.deviceVisible) {
      return;
    }
    if (this.$attrs.stopEvent) {
      return;
    }
    this.refreshWidgetVisibleByCondition(() => {});
    this.afterChangeableDependDataChanged();
    this.executeLifecycleHook('beforeMount');
    // 初始化自定义事件处理
    let _this = this;
    for (let k in this.vDefineEvents) {
      let e = this.vDefineEvents[k];
      if (!['created', 'beforeMount', 'mounted'].includes(e.id)) {
        // 对外开放的事件
        _this.pageContext.offEvent(`${this.widget.id}:${e.id}`).handleEvent(`${this.widget.id}:${e.id}`, function () {
          if (!e.default) {
            let args = Array.from(arguments);
            args = [e].concat(args); // 参数合并
            _this.executeEvent.apply(_this, args);
          } else if (_this[e.id]) {
            // 默认的事件，存在对应的方法函数，执行该方法函数
            _this[e.id].apply(_this, arguments);
          } else if (e.codeSource == 'widgetEvent') {
            _this.executeEvent.apply(_this, [e]);
          }
        });
      }
    }

    this.invokeDevelopmentMethod('beforeMount');
  },
  mounted() {
    this.$emit('mounted', { $vue: this });
    this.isMounted = true;
    if (!this.deviceVisible) {
      return;
    }
    this.updateWidgetAttrAndStyleContent();
    if (this.$attrs.stopEvent) {
      return;
    }
    this.pageContext.handleEvent(`Widget:${this.widget.id}:Child:VisibleChange`, ({ id, visible }) => {
      if (typeof this.handleChildVisibleChange == 'function') {
        this.handleChildVisibleChange(id, visible);
      }
    });

    this.executeLifecycleHook('mounted');
    this.invokeDevelopmentMethod('mounted');
    this.pageContext.emitEvent(`widget:mounted:${this.widget.id}`, { widget: this.widget, $: this });
    this.pageContext.emitEvent(`widget:mounted`, { widget: this.widget, $: this });
  },
  beforeUpdate() {
    if (this.$attrs.stopEvent) {
      return;
    }
    this.executeLifecycleHook('beforeUpdate');
  },
  updated() {
    this.updateWidgetAttrAndStyleContent();
    if (this.$attrs.stopEvent) {
      return;
    }
    this.executeLifecycleHook('updated');
  },
  beforeDestroy() {
    if (this.$attrs.stopEvent) {
      return;
    }
    this.executeLifecycleHook('beforeDestroy');
    this.invokeDevelopmentMethod('beforeDestroy');
  },
  destroyed() {
    if (this.$attrs.stopEvent) {
      return;
    }
    this.executeLifecycleHook('destroyed');

    // 销毁事件
    // for (let k in this.vDefineEvents) {
    //   let e = this.vDefineEvents[k];
    //   this.pageContext.offEvent(`${this.widget.id}:${e.id}`);
    // }
  },

  methods: {
    refreshWidgetVisibleByCondition(callback) {
      if (!this.designMode && this.widget.configuration.defaultVisible != undefined && this.hiddenByRule == undefined) {
        let _this = this;
        this.calculateVisibleByCondition(this.widget.configuration.defaultVisibleVar, this.widget.configuration.defaultVisible, visible => {
          _this.calculateWidgetVisible = visible;
          if (typeof callback === 'function') {
            callback();
          }
        });
      } else {
        if (typeof callback === 'function') {
          callback();
        }
      }
    },
    calculateDataConditionControlResult(controlConfig) {
      return new Promise((resolve, reject) => {
        // 根据数据决定是否控制
        if (controlConfig && controlConfig.enable) {
          let promise = [];
          let _compareData = this.widgetDependentVariableDataSource();
          if (controlConfig.match != undefined && controlConfig.conditions != undefined) {
            // 多组条件判断
            let match = controlConfig.match == 'all';
            for (let i = 0, len = controlConfig.conditions.length; i < len; i++) {
              let { code, codeValue, operator, value, valueType } = controlConfig.conditions[i];
              if (code.endsWith('.') && codeValue) {
                code = `${code}${codeValue}`;
              }
              if (valueType == 'variable') {
                try {
                  value = get(_compareData, value);
                } catch (error) {
                  console.error('无法解析变量值', value);
                }
              }
              let result = expressionCompare(_compareData, code, operator, value, true);
              promise.push(result);
            }
            Promise.all(promise)
              .then(results => {
                for (let result of results) {
                  if (controlConfig.match == 'all' && !result) {
                    match = false;
                    break;
                  }
                  if (controlConfig.match == 'any' && result) {
                    match = true;
                    break;
                  }
                }

                resolve(match);
              })
              .catch(() => {
                reject();
              });
          } else {
            let { code, codeValue, value, operator, valueType } = controlConfig;
            if (code.endsWith('.') && codeValue) {
              code = `${code}${codeValue}`;
            }
            if (valueType == 'variable') {
              try {
                value = get(_compareData, value);
              } catch (error) {
                console.error('无法解析变量值', value);
              }
            }
            expressionCompare(_compareData, code, operator, value, true)
              .then(result => {
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
        this.calculateDataConditionControlResult(defaultVisibleVar).then(match => {
          if (typeof callback == 'function') {
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
          if (typeof arguments[1] !== 'string') {
            // 参数化翻译
            params = arguments[1];
          } else {
            // 默认值
            defaultValue = arguments[1];
          }
        }
        if (arguments[2] != undefined && typeof arguments[2] == 'string') {
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
      } else if (this.$i18n.fallbackLocale && this.$i18n._te(arguments[0], this.$i18n.fallbackLocale, this.$i18n.messages)) {
        return this.$i18n._t(arguments[0], this.$i18n.fallbackLocale, this.$i18n.messages, params);
      }
      return defaultValue || '';
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
              Widget: widgetI18n
            });
          }
        }
      }
    },
    // 将i18ns格式的i18n，实例：fileSourceOptions
    i18nsToI18n(i18ns, code = 'code') {
      let i18n = {};
      for (let m in i18ns) {
        i18ns.forEach(item => {
          if (!i18n[item.locale]) {
            i18n[item.locale] = {};
          }
          if (item.elementId && item[code].indexOf(item.elementId) > -1) {
            item[code] = item[code].split('.');
            item[code].splice(item[code].indexOf(item.elementId), 1);
            item[code].join('.');
          }
          i18n[item.locale][item[code]] = item.content;
        });
      }
      return i18n;
    },

    // 调用二开方法
    invokeJsFunction() {
      let args = Array.from(arguments);
      let jsFunction = args.shift(0);
      // 执行二开脚本方法
      let parts = jsFunction.split('.');
      if (this.$developJsInstance != undefined) {
        if (this.$developJsInstance[parts[0]] && this.$developJsInstance[parts[0]][parts[1]]) {
          // 调用二开
          return this.$developJsInstance[parts[0]][parts[1]].apply(this.$developJsInstance[parts[0]], args);
        }
      }
      // 调用页面二开脚本方法
      if (this.$pageJsInstance && this.$pageJsInstance._JS_META_ === parts[0] && this.$pageJsInstance[parts[1]]) {
        return this.$pageJsInstance[parts[1]].apply(this.$pageJsInstance, args);
      }
    },
    executeEvent() {
      // 非默认事件为开发自定义
      let args = Array.from(arguments);
      let e = args.shift(0);
      try {
        if (e.codeSource && (e.codeSource === 'codeEditor' || e.codeSource.includes('codeEditor'))) {
          // 执行自定义代码块
          if (e.customScript) {
            this.pageContext.executeCodeSegment(e.customScript, args[0], this);
          }
        }
      } catch (error) {
        console.error('执行自定义代码块异常', error);
      }
      try {
        if (e.codeSource && (e.codeSource === 'developJsFileCode' || e.codeSource.includes('developJsFileCode')) && e.jsFunction) {
          // 执行二开脚本方法
          let parts = e.jsFunction.split('.');
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
        console.error('执行二开脚本异常', error);
      }
      try {
        if (e.codeSource && (e.codeSource === 'widgetMethod' || e.codeSource.includes('widgetMethod')) && e.widgetMethod) {
          // 执行组件内部方法
          if (this.widget.wtype === 'WidgetTemplate') {
            // 模板组件执行的是模板内容的组件方法
            this.wTemplate[e.widgetMethod].apply(this.wTemplate, args);
          } else {
            this[e.widgetMethod].apply(this, args);
          }
        }
      } catch (error) {
        console.log('执行组件内部方法异常', error);
      }
      try {
        if (e.codeSource && (e.codeSource == 'pageEvent' || e.codeSource.includes('pageEvent'))) {
          this.dispatchEventHandler({
            actionType: 'redirectPage',
            meta: e.meta,
            $evtWidget: this,
            key: e.pageEvent.pageId || generateId(),
            pageContext: this.pageContext,
            ...e.pageEvent
          });
        }
      } catch (error) {
        console.log('执行页面派发事件异常', error);
      }
      try {
        if (e.codeSource && (e.codeSource == 'widgetEvent' || e.codeSource.includes('widgetEvent'))) {
          this.dispatchConditionWidgetEvent(e.widgetEvent, this.vPageState);
        }
      } catch (error) {
        console.log('执行组件事件异常', error);
      }
    },
    dispatchConditionWidgetEvent(widgetEvent, dependentOnData) {
      if (widgetEvent) {
        let events = Array.isArray(widgetEvent) ? widgetEvent : [widgetEvent];
        events.forEach(e => {
          let executeEvent = true;
          if (e.condition && e.condition.enable && e.condition.conditions.length > 0) {
            // 判断条件是否成立
            let { conditions, match } = e.condition;
            executeEvent = match == 'all';
            for (let c = 0, clen = conditions.length; c < clen; c++) {
              let { code, operator, value } = conditions[c];
              let isTrue = expressionCompare(dependentOnData, code, operator, value);
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
            for (let item of Array.isArray(e.event) ? e.event : [e]) {
              let { eventId, eventParams, eventWid, wEventParams } = item;
              let handler = {
                actionType: 'widgetEvent',
                $evtWidget: this,
                meta: {},
                eventWid,
                eventId,
                eventParams,
                wEventParams
              };

              this.dispatchEventHandler(handler);
            }
          }
        });
      }
    },
    getVueWidgetById(id) {
      return this.pageContext.getVueWidgetById(id);
    },

    // 组件按编码查询，默认仅查询同一命名空间内的
    getVueWidgetByCode(code) {
      return this.pageContext.getVueWidgetByCode(code, this.namespace);
    },

    getPageState() {
      return this.$store.state[this.namespace];
    },

    developJsInstance() {
      return this.$developJsInstance;
    },
    invokeDevelopmentMethod() {
      try {
        for (let k in this.$developJsInstance) {
          let args = Array.from(arguments),
            method = args.shift(0); // 第一个参数是访问方法，移出后剩余的参数组就是方法入参
          if (typeof this.$developJsInstance[k][method] === 'function') {
            let returnObj = this.$developJsInstance[k][method].apply(this.$developJsInstance[k], args);
            if (returnObj != undefined) {
              // 二开有返回对象，直接返回
              return returnObj;
            }
            // 无则继续执行其他二开脚本的方法
          }
        }
      } catch (error) {
        console.error('调用二开脚本方法失败：', error);
      }
    },

    translate(key, defaultValue) {
      return this.$te(key) ? this.$t(key) : defaultValue;
    },
    resolveWidgetType(widget) {
      if (widget.forceRender === false) {
        // 针对不强制渲染的组件，不进行渲染（目前仅表单设置组件作为数据管理时候）
        return undefined;
      }
      return widget != undefined ? widget.wtype : this.widget.wtype;
    },

    executeLifecycleHook(key, params) {
      if (!this.designMode && this.vDefineEvents[key]) {
        this.executeCodeSegment(this.vDefineEvents[key].customScript, params);
      }
    },
    /**
     * 执行js代码片段
     * @param {*} script 代码
     * @param {*} params 执行代码函数块的入参
     * @param {*} $this  执行代码的this指向
     * @param {*} callback 执行代码的回调
     * @returns
     */
    executeCodeSegment(script, params, $this, callback) {
      var paramNames = []; //参数键
      var paramValues = []; //参数值
      var _this = $this ? $this : this;
      if (params) {
        for (var k in params) {
          paramNames.push(k);
          paramValues.push(params[k]);
        }
      }
      var anonymousFunction = new Function(paramNames.join(','), script);
      var rt = anonymousFunction.apply(_this, paramValues);
      if (typeof callback === 'function') {
        //处理执行结果
        callback(rt);
      }
      return anonymousFunction;
    },

    /**
     * 执行事件设置的配置
     * @param {事件设置} eventHandler
     */
    dispatchEventHandler(eventHandler) {
      if (eventHandler.pageContext == undefined) {
        eventHandler.pageContext = this.pageContext;
      }

      new DispatchEvent(eventHandler).dispatch();
    },

    getWidgetStateFromUrl(url, wid) {
      if (url.indexOf('?') != -1) {
        url = url.substring(url.indexOf('?') + 1);
      }

      let query = queryString(decodeURIComponent(url), { comma: true, allowDots: true });
      if (wid) {
        return query[wid];
      }
      return query;
    },

    updateWidgetStateAsUrl(wid /* 组件ID */, key, value, replace = false /* 存在值时候替换 */) {
      if (EASY_ENV_IS_BROWSER) {
        let url = location.pathname,
          query = location.search ? queryString(decodeURIComponent(location.search).substring(1), { comma: true, allowDots: true }) : {};
        if (query[wid] == undefined) {
          query[wid] = {};
        }
        if (replace) {
          query[wid][key] = value;
        } else {
          let values = Array.isArray(query[wid][key]) ? query[wid][key] : query[wid][key] == undefined ? [] : [query[wid][key]];
          if (values.indexOf(value) == -1) {
            values.push(value);
          }
          query[wid][key] = values;
        }
        // console.log(query);
        let search = '?' + queryStringify(query, { arrayFormat: 'comma' });
        // console.log(search);
        history.pushState({}, '', url + search);
      }
    },

    /**
     * 向上通知显示变更
     */
    emitVisibleChange(visible = true) {
      this.$emit('visibleChange', { visible });
      // 发布组件显示变更通知
      this.pageContext.emitEvent(`Widget:${this.widget.id}:VisibleChange`, visible);
      // 向上通知子元素的显示变更通知，由各自组件处理子元素的显示状态对应逻辑
      if (this.parent && this.parent.id) {
        this.pageContext.emitEvent(`Widget:${this.parent.id}:Child:VisibleChange`, { id: this.widget.id, visible });
      }
    },

    triggerDomEvent(eventName, conditionData, passMetaData, key) {
      if (this.widget.configuration.domEvents != undefined) {
        for (let i = 0, len = this.widget.configuration.domEvents.length; i < len; i++) {
          let evt = this.widget.configuration.domEvents[i];

          if (evt.id == eventName) {
            if (evt.codeSource == 'widgetEvent' || evt.codeSource.includes('widgetEvent')) {
              let widgetEvent = evt.widgetEvent;
              if (widgetEvent) {
                let events = [widgetEvent];
                if (Array.isArray(widgetEvent)) {
                  events = widgetEvent;
                }
                events.forEach(e => {
                  let executeEvent = true;
                  if (conditionData != undefined && e.condition && e.condition.enable && e.condition.conditions.length > 0) {
                    // 判断条件是否成立
                    let { conditions, match } = e.condition;
                    executeEvent = match == 'all';
                    for (let c = 0, clen = conditions.length; c < clen; c++) {
                      let { code, operator, value } = conditions[c];
                      let isTrue = expressionCompare(conditionData, code, operator, value);
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
                    let { eventId, eventParams, eventWid, wEventParams, actionType } = e;
                    this.dispatchEventHandler({
                      actionType: actionType || 'widgetEvent',
                      $evtWidget: this,
                      meta: passMetaData,
                      eventWid,
                      eventId,
                      eventParams,
                      wEventParams
                    });
                  }
                });
              }
            } else if (evt.codeSource == 'pageEvent' || evt.codeSource.includes('pageEvent')) {
              this.dispatchEventHandler({
                actionType: 'redirectPage',
                meta: passMetaData,
                $evtWidget: this,
                key: key || evt.pageEvent.pageId,
                pageContext: this.pageContext,
                ...evt.pageEvent
              });
            } else {
              this.executeEvent(evt, passMetaData);
            }
          }
        }
      }
    },
    // 更新组件的dom的参数和样式
    updateWidgetAttrAndStyleContent() {
      if (this.$el && this.$el.nodeName !== '#comment' && !this.$el.getAttribute('w-id')) {
        this.$el.setAttribute('id', this.widget.id);
        this.$el.setAttribute('w-id', this.widget.id);
        if (this.widget.configuration.code) {
          this.$el.setAttribute('w-code', this.widget.configuration.code);
        }

        if (this.widget.configuration.styleContent) {
          try {
            if (window.less != undefined) {
              window.less
                .render(`/* 组件自定义样式: */ #${this.widget.id} { ${this.widget.configuration.styleContent} } `, {})
                .then(result => {
                  if (result.css) {
                    let styleElement = document.createElement('style');
                    styleElement.textContent = result.css;
                    document.head.appendChild(styleElement);
                  }
                });
            }
          } catch (error) {
            console.error('less error', error);
          }
        }
      }
    },
    widgetDependentVariableDataSource(includeSubform = true) {
      let dyform = this.widgetDyformContext != undefined ? this.widgetDyformContext.dyform : this.dyform;
      let source = {
        ...this.vPageState,
        ...(dyform != undefined ? dyform.formData : {}), // 兼容旧配置，旧配置的表字段数据在顶层
        _FORM_DATA_: typeof this.getDyformData == 'function' ? this.getDyformData() : dyform != undefined ? dyform.formData : {}, // 主表数据
        ...this._vShowByUserData,
        ...this._vShowByDateTime,
        ...this._vShowByWorkflowData,
        _URL_PARAM_: this.vUrlParams,
        __DYFORM__:
          dyform != undefined
            ? {
                editable: dyform.displayState == 'edit'
              }
            : {},
        // 页面设计的表单区组件数据
        ...(this.widgetFormContext != undefined ? this.widgetFormContext.form : {})
      };

      if (
        this.form != undefined &&
        (this.$attrs.isSubformCell ||
          (this.widgetDyformContext &&
            this.widgetDyformContext.isSubform &&
            this.form.formElementRules[this.widget.id] &&
            this.form.formElementRules[this.widget.id].requiredCondition))
      ) {
        // 获取当前从表行数据
        source.__CURRENT_ROW_FORM_DATA__ = {
          ...this.form.formData
        };
        // 从表为原表单编辑且按从表字段必填规则时，主表数据为rootWidgetDyformContext.dyform.formData
        if (
          this.widgetDyformContext &&
          this.widgetDyformContext.isSubform &&
          this.form.formElementRules[this.widget.id] &&
          this.form.formElementRules[this.widget.id].requiredCondition &&
          this.rootWidgetDyformContext
        ) {
          Object.assign(source, this.rootWidgetDyformContext.dyform.formData);
          source._FORM_DATA_ = this.rootWidgetDyformContext.dyform.formData;
        }
      }
      if (dyform && dyform.subform) {
        let subformData = {};
        for (let subformUuid in dyform.subform) {
          let subforms = dyform.subform[subformUuid];
          if (subforms) {
            if (subforms.length > 0) {
              subformData[subformUuid] = subforms.map(item => item.formData);
            }
          }
        }
        source._SUBFORM_DATA_ = subformData;
      }

      console.log('widgetDependentVariableDataSource', source);
      return source;
    },
    fetchDataByApiLinkInvocation(apiInvocationConfig) {
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
              jsonataEvaluate(valueOption.value, data, undefined, undefined, valueOption).then(result => {
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
                      _this.dispatchConditionWidgetEvent(apiInvocationConfig.endAction.widgetEvent, result);
                    } else {
                      _this.dispatchEventHandler({
                        ...deepClone(apiInvocationConfig.endAction),
                        meta: result,
                        $evtWidget: _this
                      });
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
            .catch(error => {});
        });
      });
    },
    afterChangeableDependDataChanged() {}
  },
  watch: {
    vWatchChangeableDependDataMD5Changed: {
      handler(v, o) {
        this.refreshWidgetVisibleByCondition();
        this.afterChangeableDependDataChanged();
      }
    },
    vShow: {
      // 显示或者隐藏切换后执行事件
      handler(v, o) {
        if (this.widget.configuration.visibleToggle) {
          this.executeEvent(this.widget.configuration.visibleToggle, { $widget: this, visible: v });
        }
      }
    }
  }
};
