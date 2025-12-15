import extend from 'extend';
export default function createPageConetxt(vueInstance) {
  const CONTEXT = {
    vueInstance: vueInstance,
    emitEvent() {
      //用于兄弟组件发射事件
      if (this.vueInstance) this.vueInstance.$emit.apply(this.vueInstance, arguments);
    },

    offEvent(evtName) {
      if (this.vueInstance) this.vueInstance.$off(evtName);
      return this;
    },

    handleEvent(evtName, callback) {
      //用于兄弟组件接收事件
      if (this.vueInstance)
        this.vueInstance.$on(evtName, function () {
          callback.apply(this, arguments);
        });
    },

    // 处理跨页面的通信
    emitCrossTabEvent(evtName, evtData) {
      try {
        localStorage.setItem(evtName, JSON.stringify(evtData));
        localStorage.removeItem(evtName);

        // 手动派发自定义事件: 支持当前window触发
        window.dispatchEvent(
          new CustomEvent('localstoragechange', {
            detail: { key: evtName, oldValue: undefined, newValue: JSON.stringify(evtData) }
          })
        );
      } catch (error) {
        console.error(error);
      }
    },

    // 处理跨页面的通信
    handleCrossTabEvent(evtName, callback) {
      if (window._MY_STORAGE_EVENT_ == undefined) {
        window._MY_STORAGE_EVENT_ = [];
        window._MY_STORAGE_EVENT_LISTENER = {};
        window._MY_STORAGE_EVENT_LISTENER_CALLBACK = {};
      }
      if (!window._MY_STORAGE_EVENT_.includes(evtName)) {
        window._MY_STORAGE_EVENT_LISTENER_CALLBACK[evtName] = [callback];
        let listener = function (e) {
          if (e.key === evtName) {
            if (!e.newValue) {
              return;
            }
            if (window._MY_STORAGE_EVENT_LISTENER_CALLBACK[evtName]) {
              window._MY_STORAGE_EVENT_LISTENER_CALLBACK[evtName].forEach(function (callback) {
                new Promise((resolve, reject) => {
                  callback(JSON.parse(e.newValue));
                }).catch(error => {
                  console.error(error);
                });
              });
            }
          }
        };
        window.addEventListener('storage', listener);
        window._MY_STORAGE_EVENT_.push(evtName);
        window._MY_STORAGE_EVENT_LISTENER[evtName] = listener;
        window.addEventListener('localstoragechange', function (e) {
          listener(e.detail);
        });
      } else {
        window._MY_STORAGE_EVENT_LISTENER_CALLBACK[evtName].push(callback);
      }
    },

    offCrossTabEvent(evtName) {
      if (window._MY_STORAGE_EVENT_) {
        let index = window._MY_STORAGE_EVENT_.indexOf(evtName);
        if (index != -1) {
          window._MY_STORAGE_EVENT_.splice(index, 1);
          window.removeEventListener('storage', window._MY_STORAGE_EVENT_LISTENER[evtName]);
          delete window._MY_STORAGE_EVENT_LISTENER[evtName];
        }
      }
    },

    /**
     * 根据组件ID或者组件定义json创建vue组件实例
     * @param {组件定义ID或者组件定义json} widgetIdOrJson
     * @returns vue组件实例
     */
    newVueWidget(widgetIdOrJson) {
      // 创建构造器
      let _Component = Vue.extend({
        template: '<component :is="widget.type" :widget="widget"></component>', //
        data: function () {
          // TODO: 根据组件ID查询组件定义json
          if (typeof widgetIdOrJson === 'string') {
            return {};
          }

          return Object.assign({}, widgetIdOrJson);
        }
      });

      return new _Component();
    },

    /**
     * 挂载组件实例到指定的DOM元素内部
     * @param {组件定义ID或者组件定义json} widgetIdOrJson
     * @param {DOM元素或者CSS选择表达式} elementOrSelector
     * @param {是否独子，是则清空指定DOM元素内部所有子元素} only
     * @returns vue组件实例
     */
    mountWidgetAsChild(widgetIdOrJson, elementOrSelector, only) {
      let _compInst = this.newVueWidget(widgetIdOrJson);
      return this.mountAsChild(_compInst, elementOrSelector, only);
    },

    newVueComponent(vueOptions) {
      let propsData = vueOptions.propsData;
      delete vueOptions.propsData;
      if (vueOptions.props == undefined && propsData != undefined) {
        vueOptions.props = Object.keys(propsData);
      }
      return new (Vue.extend(vueOptions))({ propsData: propsData || {} });
    },

    mountVueComponentAsChild(vueOptions, elementOrSelector, only) {
      return this.mountAsChild(this.newVueComponent(vueOptions), elementOrSelector, only);
    },

    mountVueComponent(vueOptions, elementOrSelector) {
      let target = elementOrSelector;
      if (typeof elementOrSelector === 'string') {
        target = document.querySelector(elementOrSelector);
      }
      return this.newVueComponent(vueOptions).$mount(target);
    },

    mountAsChild(compInst, elementOrSelector, only) {
      let target = elementOrSelector;
      if (typeof elementOrSelector === 'string') {
        target = document.querySelector(elementOrSelector);
      }
      if (only) {
        // 独子，意味着清空内部所有子元素进行挂载
        while (target.firstChild) {
          target.removeChild(target.firstChild);
        }
        target.appendChild(document.createElement('div'));
        target = target.firstChild;
        return compInst.$mount(target);
      } else {
        let _div = document.createElement('div');
        target.appendChild(_div);
        return compInst.$mount(_div);
      }
    },

    /**
     * 挂载组件实例到指定的DOM元素上（DOM元素会被替换掉）
     * @param {组件定义ID或者组件定义json} widgetIdOrJson
     * @param {DOM元素或者CSS选择表达式} elementOrSelector
     * @returns vue组件实例
     */
    mountWidget(widgetIdOrJson, elementOrSelector) {
      return this.newVueWidget(widgetIdOrJson).$mount(elementOrSelector);
    },

    /**
     * 挂载组件实例到指定的DOM元素前面
     * @param {组件定义ID或者组件定义json} widgetIdOrJson
     * @param {DOM元素或者CSS选择表达式} elementOrSelector
     * @returns vue组件实例
     */
    mountWidgetBefore(widgetIdOrJson, elementOrSelector) {
      let target = elementOrSelector;
      if (typeof elementOrSelector === 'string') {
        target = document.querySelector(elementOrSelector);
      }
      let newDiv = document.createElement('div');
      newDiv.insertBefore(target);
      return this.newVueWidget(widgetIdOrJson).$mount(newDiv);
    },

    /**
     * 挂载组件实例到指定的DOM元素后面
     * @param {组件定义ID或者组件定义json} widgetIdOrJson
     * @param {DOM元素或者CSS选择表达式} elementOrSelector
     * @returns vue组件实例
     */
    mountWidgetAfter(widgetIdOrJson, elementOrSelector) {
      let target = elementOrSelector;
      if (typeof elementOrSelector === 'string') {
        target = document.querySelector(elementOrSelector);
      }
      let newDiv = document.createElement('div');
      target.parentNode.appendChild(newDiv);
      return this.newVueWidget(widgetIdOrJson).$mount(newDiv);
    },

    resolveStringVueTemplate(string, params) {
      if (string) {
        return new (Vue.extend({
          template: `<code>${string}</code>`,
          data: function () {
            return params;
          }
        }))().$mount().$el.innerHTML;
      }
      return string;
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

    registerMountedInst(_this, key) {
      if (this.__mountedInst === undefined) {
        this.__mountedInst = {};
      }
      this.__mountedInst[key] = _this;
    },

    getMountedInst(key) {
      if (this.__mountedInst) {
        return this.__mountedInst[key];
      }
    },

    commitPageState(namespace, state) {
      if (!namespace) {
        console.warn('参数[namespace]必选');
        return;
      }
      this.vueInstance.$store.commit(`${namespace}/set`, state);
    },

    getPageState(namespace) {
      return this.vueInstance.$store.state[namespace];
    },

    /**
     * 提交状态
     * @param {模块名称} module
     * @param {状态变量} state
     */
    commitRegisterStateIfNotAbsent(module, state) {
      if (this.vueInstance) {
        let $store = this.vueInstance.$store;
        if (!$store.hasModule(module)) {
          $store.registerModule(module, {
            state,
            namespaced: true,
            mutations: {
              // 修改状态
              set(state, payload) {
                if (typeof payload === 'function') {
                  payload.call(this, state);
                } else if (typeof payload === 'object') {
                  for (let k in payload) {
                    Vue.set(state, k, typeof payload === 'object' ? JSON.parse(JSON.stringify(payload[k])) : payload[k]);
                  }
                  extend(true, state, payload);
                }
              }
            }
          });
        } else {
          $store.commit(`${module}/set`, state);
        }
      }
    },

    getVueWidgetById(id) {
      let $el = document.querySelector(`[w-id='${id}']`);
      if ($el) {
        let _vue = $el.__vue__;
        while (!_vue.widget) {
          _vue = _vue.$parent;
        }
        return _vue;
      }
      return null;
    },

    getVueWidgetByCode(code, namespace) {
      let parent = namespace ? document.querySelector(`[namespace='${namespace}']`) : document;
      let $el = parent.querySelector(`[w-code='${code}']`);
      if ($el) {
        let _vue = $el.__vue__;
        while (!_vue.widget) {
          _vue = _vue.$parent;
        }
        return _vue;
      }
      return null;
    }
  };

  if (EASY_ENV_IS_NODE) {
    CONTEXT.ssrEvent = {}; // 服务端渲染事件数据
  }
  return CONTEXT;
}
