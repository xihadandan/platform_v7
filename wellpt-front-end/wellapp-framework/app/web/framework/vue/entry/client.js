import Vue from 'vue';
import Vuex from 'vuex';
import '@framework/vue';
import '~/app/web/framework/vue';
import createClientCommonApi from '@pageAssembly/app/web/framework/vue/clientCommonApi';
import { getCookie, generateId, createLocalforage, dropLocalforage, asyncLoadLocaleData } from '@framework/vue/utils/util';
import { merge } from 'lodash';
import ClientChecker from '../lib/client-checker';
import { Logger } from '@framework/vue/utils/logger';
import zh_CN from 'ant-design-vue/lib/locale-provider/zh_CN';

window.Vue = Vue; // 设置全局

Vue.config.warnHandler = function (err, vm, info) {
  console.warn(err);
};

export default function (options) {
  // http 客户端调用工具
  let $axios = require('axios');
  let csrfTokenkey = '_csrfToken';
  let arr = document.cookie.match(new RegExp('(^| )' + csrfTokenkey + '=([^;]*)(;|$)'));
  if (arr) {
    $axios.defaults.headers.common['x-csrf-token'] = arr[2];
  }
  $axios.interceptors.response.use(
    function (response) {
      let resData = response.data;
      if (resData && resData.errorCode === 'SessionExpired') {
        if (!window.__SESSION_EXPIRED__) {
          window.__SESSION_EXPIRED__ = true;
          // 获取浏览器地址
          let url = window.location.pathname + window.location.search,
            app = $app;
          try {
            url = window.top.location.pathname + window.top.location.search;
            app = window.top.$app != undefined ? window.top.$app : app;
          } catch (error) {}
          app.$error({
            title: app.$t('LoginRender.error', '错误'),
            content: app.$t('LoginRender.sessionTimedOut', '用户会话已超时, 请重新登录'),
            onOk() {
              let redirectUrl = `${window.SYSTEM_ID ? `/sys/${window.SYSTEM_ID}/login?sessionExpired` : '/login?sessionExpired'}`;
              redirectUrl += `&loginSuccessRedirectUrl=${encodeURIComponent(url)}`;
              try {
                window.top.open(redirectUrl, '_self');
              } catch (error) {
                window.open(redirectUrl, '_self');
              }
            }
          });
        }
      }
      return response;
    },
    function (error) {
      return Promise.reject(error);
    }
  );

  $axios.interceptors.request.use(
    config => {
      config.headers['Accept-Locale'] = $app.$i18n.locale;
      return config; // 返回处理后的配置
    },
    error => {
      return Promise.reject(error);
    }
  );

  Vue.prototype.$axios = $axios;
  window.$axios = $axios;
  Vue.prototype._logger = new Logger();
  Vue.prototype.$clientCommonApi = createClientCommonApi();
  Vue.config.silent = VUE_SILENT; // 关闭 vue 的错误、警告控制台提示
  const store = new Vuex.Store({
    state: {},
    // strict: process.env.NODE_ENV !== 'production', // 非发布环境，开启严格模式 (严格模式会导致antv的逻辑异常: antv有直接操作state的代码)
    modules: {}, //
    mutations: {}
  });
  store.replaceState(Object.assign({}, window.__INITIAL_STATE__));
  if (options.store) {
    store.replaceState(Object.assign({}, options.store.state));
  }
  const _CONTEXT_STATE_ = window.__INITIAL_STATE__._CONTEXT_STATE_;
  if (window.__INITIAL_STATE__.PROD_CONTEXT_PATH) {
    // 归属产品上下文
    window.PROD_CONTEXT_PATH = window.__INITIAL_STATE__.PROD_CONTEXT_PATH;
    $axios.defaults.headers.common['prod_context_path'] = window.__INITIAL_STATE__.PROD_CONTEXT_PATH;
  }
  if (_CONTEXT_STATE_.SYSTEM_ID) {
    window.SYSTEM_ID = _CONTEXT_STATE_.SYSTEM_ID;
    window.__INITIAL_STATE__.SYSTEM_ID = _CONTEXT_STATE_.SYSTEM_ID;
    $axios.defaults.headers.common['system_id'] = _CONTEXT_STATE_.SYSTEM_ID;
  }
  if (_CONTEXT_STATE_.PAGE_ID) {
    $axios.defaults.headers.common['page_id'] = _CONTEXT_STATE_.PAGE_ID;
  }
  if (_CONTEXT_STATE_.FORCE_SET_REQUEST_CLIENT_TOKEN) {
    $axios.defaults.headers.common['x-set-req-client-token'] = _CONTEXT_STATE_.FORCE_SET_REQUEST_CLIENT_TOKEN;
  }

  Vue.prototype._$USER = _CONTEXT_STATE_.USER;
  Vue.prototype._$SYSTEM_ID = _CONTEXT_STATE_.SYSTEM_ID;
  if (window.__INITIAL_STATE__.PAGE_EFFECTIVE_ROLES) {
    Vue.prototype._$pageEffectiveRoles = window.__INITIAL_STATE__.PAGE_EFFECTIVE_ROLES;
  }

  const root = document.getElementById('app');

  if (window.__INITIAL_STATE__) {
    const _data = options.data;
    options.data = function () {
      let d = _data ? _data.apply(this) : {};
      for (let k in window.__INITIAL_STATE__) {
        // 服务端返回的数据在原data函数未定义，则设值
        if (!d.hasOwnProperty(k)) {
          d[k] = window.__INITIAL_STATE__[k];
        }
      }
      return d;
    };
  }

  options.store = store;

  // 创建临时缓存数据库
  const $tempStorage = createLocalforage('WELL_STORE', '低代码临时缓存');
  Vue.prototype.$tempStorage = $tempStorage;
  window.$tempStorage = $tempStorage;

  const $localStorage = createLocalforage('WELL_LOCAL_STORE', '本地缓存', 'WELL_LOCAL_STORAGE');
  Vue.prototype.$localStorage = $localStorage;
  window.$localStorage = $localStorage;
  Vue.prototype._$SERVER_TIMESTAMP = _CONTEXT_STATE_.SERVER_TIMESTAMP;
  let locale = 'zh_CN';
  if (_CONTEXT_STATE_.LOCALE_ENABLE) {
    locale = document.cookie.match(new RegExp('(^| )' + (window.SYSTEM_ID ? 'locale.' + window.SYSTEM_ID : 'locale') + '=([^;]*)(;|$)'));
    locale = locale ? locale[2] : 'zh_CN';
  }
  options.i18n = {
    locale,
    fallbackLocale: 'zh_CN',
    fallbackRoot: false,
    messages: {
      [locale]: {}
    }
  };
  let _provide = options.provide;
  options.provide = function () {
    this.pageContext = this._CreatePageContext(this);
    // if (window.__INITIAL_STATE__._URL_STATE_ != undefined) {
    //   //后端返回的页面状态变量，提供前端vue使用
    //   this.pageContext._URL_STATE_ = window.__INITIAL_STATE__._URL_STATE_;
    //   Vue.observable(this.pageContext._URL_STATE_);
    // }
    let args = _provide != undefined ? _provide : {};
    if (typeof _provide === 'function') {
      args = _provide.call(this);
    }
    return {
      PROD_CONTEXT_PATH: window.__INITIAL_STATE__.PROD_CONTEXT_PATH,
      USER: window.__INITIAL_STATE__._CONTEXT_STATE_ != undefined ? window.__INITIAL_STATE__._CONTEXT_STATE_.USER : {},
      pageContext: this.pageContext,
      csrf: window.__INITIAL_STATE__.csrf,
      _CONTEXT_STATE_,
      locale: JSON.parse(JSON.stringify(zh_CN)),
      ...args
    };
  };
  const app = new Vue(options);
  const hydrate = root.childNodes.length > 0;
  window.$app = app;

  asyncLoadLocaleData(locale)
    .then(result => {
      if (result[1] != undefined) {
        app.$i18n.mergeLocaleMessage(locale, result[1]);
      }
      if (result[0] != undefined) {
        app._provided.locale = result[0];
        merge(app._provided.locale, result[0]);
      }
      app.$mount(root, hydrate);
      const clientChecker = new ClientChecker(app, {
        locale
      });
      clientChecker.checkUserActivity();
      clientChecker.checkPassword();
    })
    .catch(error => {
      console.error('挂载异常', error);
      app.$mount(root, hydrate);
      const clientChecker = new ClientChecker(app, {
        locale
      });
      clientChecker.checkUserActivity();
      clientChecker.checkPassword();
    });

  console.info('%cWellsoft Low Code Platform', 'color:#409EFF;font-size: 22px;font-weight:bolder');
}
