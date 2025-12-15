'use strict';

import Vue from 'vue';
import { sync } from 'vuex-router-sync';
import createStore from './store';
import createRouter from './router';
import index from './index.vue';
class App {
  constructor(config) {
    this.config = config;
  }

  bootstrap() {
    if (EASY_ENV_IS_NODE) {
      return this.server();
    }
    return this.client();
  }

  create(initState) {
    const { index, options, createStore, createRouter } = this.config;
    const store = createStore(initState, options);
    const router = createRouter(initState, options);
    sync(store, router);
    return {
      ...index,
      ...options,
      router,
      store
    };
  }

  client() {
    Vue.prototype.$http = require('axios');
    const options = this.create(window.__INITIAL_STATE__);
    const { router, store } = options;
    router.beforeEach((route, redirec, next) => {
      next();
    });
    router.afterEach((route, redirec) => {
      console.log('>>afterEach', route);
      if (route.matched && route.matched.length) {
        const asyncData = route.matched[0].components.default.asyncData; // 定义组件的异步获取方法
        if (asyncData) {
          asyncData(store, route);
        }
      }
    });
    const app = new Vue(options);
    const root = document.getElementById('app');
    const hydrate = root.childNodes.length > 0;
    app.$mount('#app', hydrate);
    return app;
  }

  server() {
    return context => {
      const options = this.create(context.state);
      const { store, router } = options;
      router.push(context.state.url);
      return new Promise((resolve, reject) => {
        router.onReady(() => {
          const matchedComponents = router.getMatchedComponents();
          if (!matchedComponents) {
            return reject({ code: '404' });
          }
          return Promise.all(
            matchedComponents.map(component => {
              if (component.asyncData) {
                return component.asyncData(store, store.state.route);
              }
              return null;
            })
          ).then(() => {
            context.state = {
              ...store.state,
              ...context.state
            };
            return resolve(new Vue(options));
          });
        });
      });
    };
  }
}

const options = { base: '/app' };

export default new App({
  index,
  options,
  createStore,
  createRouter
}).bootstrap();
