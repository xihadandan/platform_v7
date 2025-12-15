"use strict";
import { isEmpty } from "lodash";
import { TableViewGetCount, dataStoreCount, dataModelCount, appContext, storage } from "wellapp-uni-framework";

export default {
  inject: ["namespace", "unauthorizedResource", "$pageJsInstance"],
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
    let widget = this.$options.propsData.widget;
    this.$developJsInstance = {};
    if (widget.configuration.jsModules && widget.configuration.jsModules.length > 0) {
      for (let i = 0, len = widget.configuration.jsModules.length; i < len; i++) {
        let js = widget.configuration.jsModules[i].key;
        try {
          this.$developJsInstance[js] = appContext.jsInstance(js);
        } catch (error) {
          console.error(error);
        }
      }
    }
  },
  computed: {
    deviceVisible() {
      if (this.widget.configuration.deviceVisible && !this.widget.configuration.deviceVisible.includes("mobile")) {
        return false;
      }
      return true;
    },
    vShow() {
      if (!this.deviceVisible) {
        return false;
      }
      let configuration = this.widget.configuration;
      if (this.hiddenByRule !== undefined) {
        return !this.hiddenByRule;
      }
      if (!this.designMode && configuration.defaultVisible != undefined) {
        let visible = configuration.defaultVisible;
        // 根据页面变量决定是否展示
        if (configuration.defaultVisibleVar && configuration.defaultVisibleVar.enable) {
          let _compareData = {
            ...this.vPageState,
            ...(this._vShowByData || {}),
          };
          if (
            configuration.defaultVisibleVar.match != undefined &&
            configuration.defaultVisibleVar.conditions != undefined
          ) {
            // 多组条件判断
            let match = configuration.defaultVisibleVar.match == "all";
            for (let i = 0, len = configuration.defaultVisibleVar.conditions.length; i < len; i++) {
              let { code, operator, value } = configuration.defaultVisibleVar.conditions[i];
              let result = framework.utils.expressionCompare(_compareData, code, operator, value);
              if (configuration.defaultVisibleVar.match == "all" && !result) {
                match = false;
                break;
              }
              if (configuration.defaultVisibleVar.match == "any" && result) {
                match = true;
                break;
              }
            }
            return match ? visible : !visible;
          } else {
            let { code, value, operator } = configuration.defaultVisibleVar;
            return framework.utils.expressionCompare(_compareData, code, operator, value) ? visible : !visible;
          }
        }
        return visible;
      }
      return true;
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
  },
  data() {
    const _self = this;
    let configuration = _self.widget.configuration || {};
    let customClass = configuration.customClass;
    let isEmptyUniJsModule = isEmpty(configuration.uniJsModule);
    return {
      customClass,
      isEmptyUniJsModule,
    };
  },
  methods: {
    /**
     * 向上通知显示变更
     */
    emitVisibleChange(visible = true) {
      this.$emit("visibleChange", { visible });
      // 发布组件显示变更通知
      this.pageContext.emitEvent(`Widget:${this.widget.id}:VisibleChange`, visible);
      // 向上通知子元素的显示变更通知，由各自组件处理子元素的显示状态对应逻辑
      if (this.parent && this.parent.id) {
        this.pageContext.emitEvent(`Widget:${this.parent.id}:Child:VisibleChange`, { id: this.widget.id, visible });
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

    executeEvent() {
      // 非默认事件为开发自定义
      let args = Array.from(arguments);
      let e = args.shift(0);
      if (e.codeSource === "codeEditor") {
        // 执行自定义代码块
        if (e.customScript) {
          this.pageContext.executeCodeSegment(e.customScript, args[0], this);
        }
      } else if (e.codeSource === "developJsFileCode" && e.jsFunction) {
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
      } else if (e.codeSource === "widgetMethod" && e.widgetMethod) {
        // 执行组件内部方法
        if (this.widget.wtype === "WidgetTemplate") {
          // 模板组件执行的是模板内容的组件方法
          this.wTemplate[e.widgetMethod].apply(this.wTemplate, args);
        } else {
          this[e.widgetMethod].apply(this, args);
        }
      } else if (e.codeSource == "pageEvent") {
        this.pageContext.dispatchEvent({
          ui: this,
          $evtWidget: this,
          meta: e.meta,
          ...e.pageEvent,
        });
      }
    },
  },

  beforeMount() {
    if (!this.deviceVisible) {
      return;
    }
    // 初始化自定义事件处理
    let _this = this;
    for (let k in this.vDefineEvents) {
      let e = this.vDefineEvents[k];
      if (!["created", "beforeMount", "mounted"].includes(e.id)) {
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
};
