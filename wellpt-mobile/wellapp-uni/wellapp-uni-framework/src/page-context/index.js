const componentMap = {};

export default {
  registerComponent(id, instance) {
    if (componentMap[id] == undefined) {
      componentMap[id] = instance;
    }
  },
  updateComponent(id, instance) {
    if (instance) {
      componentMap[id] = instance;
    }
  },
  delComponent(id) {
    delete componentMap[id];
  },
  getComponent(id) {
    return componentMap[id];
  },
  clearAllComponents() {
    Object.keys(componentMap).forEach((id) => {
      delete componentMap[id];
    });
    console.log("[componentManager] 所有组件实例已清除");
  },
  unregisterComponent(id) {
    if (componentMap[id]) {
      delete componentMap[id];
    }
  },

  emitEvent: function () {
    uni.$emit.apply(uni, arguments);
    return this;
  },

  handleEvent: function (eventName, fn) {
    uni.$on(eventName, fn);
    return this;
  },

  offEvent: function () {
    uni.$off(arguments[0]);
    return this;
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
    var anonymousFunction = new Function(paramNames.join(","), script);
    var rt = anonymousFunction.apply(_this, paramValues);
    if (typeof callback === "function") {
      //处理执行结果
      callback(rt);
    }
    return anonymousFunction;
  },

  getVueWidgetById(id) {
    return componentMap[id];
  },
};
