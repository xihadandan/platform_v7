import Vue from "vue";
import App from "./App";
import store from "@wellapp-uni-framework/store";
import "@wellapp-uni-framework/request";

Vue.config.productionTip = false;
Vue.prototype.$store = store;

App.mpType = "app";

const app = new Vue({
  store,
  ...App,
});
app.$mount();
