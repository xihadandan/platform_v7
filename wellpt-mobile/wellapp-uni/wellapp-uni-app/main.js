import App from "./App";
import store from "wellapp-uni-framework/src/store";
import * as framework from "wellapp-uni-framework";

// #ifndef VUE3
import Vue from "vue";
Vue.config.productionTip = false;
Vue.prototype.$store = store;
Vue.config.silent = true;

// 使用平台功能
Vue.use(framework);
Vue.prototype.$backgroundAudioData = {
  playing: false,
  playTime: 0,
  formatedPlayTime: "00:00:00",
};
App.mpType = "app";
const i18n = framework.i18n;
const app = new Vue({
  store,
  i18n,
  ...App,
});
app.$mount();
// #endif

// #ifdef VUE3
import { createSSRApp } from "vue";
export function createApp() {
  const app = createSSRApp(App);
  app.use(store);
  app.use(i18n);
  app.config.globalProperties.$adpid = "1111111111";
  app.config.globalProperties.$backgroundAudioData = {
    playing: false,
    playTime: 0,
    formatedPlayTime: "00:00:00",
  };
  return {
    store,
    app,
  };
}
// #endif

// #ifdef H5
// 全局弹框组件
import globalPopup from "./packages/_/pages/popup/global-popup.vue";
import globalActionSheet from "./packages/_/pages/popup/action-sheet.vue";
import globalToast from "./packages/_/pages/toast/toast.vue";

const PopupVue = Vue.extend(globalPopup);
const popupDom = new PopupVue({
  i18n,
});
Vue.prototype.$globalPopup = popupDom.$mount();
let lastEl = document.body.lastElementChild;
if (lastEl.id !== "globalPopup-box") {
  setTimeout(() => {
    document.body.appendChild(Vue.prototype.$globalPopup.$el);
  }, 50);
}
const PopupActionVue = Vue.extend(globalActionSheet);
const popupActionDom = new PopupActionVue({
  i18n,
});
Vue.prototype.$globalActionSheet = popupActionDom.$mount();
lastEl = document.body.lastElementChild;
if (lastEl.id !== "actionsPopup-box") {
  setTimeout(() => {
    document.body.appendChild(Vue.prototype.$globalActionSheet.$el);
  }, 50);
}

const ToastVue = Vue.extend(globalToast);
const ToastVueDom = new ToastVue({
  i18n,
});
Vue.prototype.$globalToast = ToastVueDom.$mount();
lastEl = document.body.lastElementChild;
if (lastEl.id !== "globalToast-box") {
  setTimeout(() => {
    document.body.appendChild(Vue.prototype.$globalToast.$el);
  }, 50);
}
// #endif
