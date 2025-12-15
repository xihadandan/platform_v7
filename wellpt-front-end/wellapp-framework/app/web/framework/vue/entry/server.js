import zh_CN from 'ant-design-vue/lib/locale-provider/zh_CN';

export default function render(options, Vue, Vuex, locali18n) {
  Vue.config.warnHandler = function (err, vm, info) {
    console.warn(err);
  };
  if (options.store && options.router) {
    return context => {
      options.router.push(context.state.url);
      const matchedComponents = options.router.getMatchedComponents();
      if (!matchedComponents) {
        return Promise.reject({ code: '404' });
      }
      return Promise.all(
        matchedComponents.map(component => {
          if (component.preFetch) {
            return component.preFetch(options.store);
          }
          return null;
        })
      ).then(() => {
        context.state = Object.assign(options.store.state, context.state);
        return new Vue(options);
      });
    };
  }

  return context => {
    Vue.prototype.$requestServContext = context.state.ctx; // egg请求服务端上下文
    Vue.prototype._$USER = context.state.ctx.USER;
    if (context.state.PAGE_EFFECTIVE_ROLES) {
      Vue.prototype._$pageEffectiveRoles = context.state.PAGE_EFFECTIVE_ROLES;
    }
    const store = new Vuex.Store({
      state: {},
      // strict: process.env.NODE_ENV !== 'production', // 非发布环境，开启严格模式 (严格模式会导致antv的逻辑异常: antv有直接操作state的代码)
      modules: {}, //
      mutations: {}
    });
    const VueApp = Vue.extend(options);
    const pageContext = Vue.prototype._CreatePageContext();
    let provide = {
      pageContext,
      USER: context.state.ctx.user,
      csrf: context.state.ctx.csrf,
      locale: JSON.parse(JSON.stringify(zh_CN)),
      _CONTEXT_STATE_: context.state._CONTEXT_STATE_
    };
    let locale = context.state.ctx.localeVariable || 'zh_CN';
    return new VueApp({
      data: function () {
        return context.state;
      },
      i18n: {
        locale,
        fallbackRoot: false,
        messages: {
          [locale]: {}
        }
      },
      provide,
      store
    });
  };
}
