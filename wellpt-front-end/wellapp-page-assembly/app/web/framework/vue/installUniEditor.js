import Vue from 'vue';
import '../../widget/commons/index.js'
const components = require.context('../../widget/mobile', true, /^\.\/widget-uni[-\w]+\/widget[-\w]+\.vue$/);
const configuration = require.context('../../widget/mobile', true, /^\.\/widget-uni[-\w]+\/configuration\/index.vue$/);


const install = function (vue) {
  vue = vue || Vue;

  const keys = components.keys();
  keys.map(fileName => {
    let comp = components(fileName).default;
    vue.component(comp.name, comp);
  });

  configuration.keys().map(fileName => {
    let comp = configuration(fileName).default;
    vue.component(comp.name, comp);
  });

};

if (typeof window !== 'undefined' && window.Vue) {
  // 客户端渲染使用的install
  install(window.Vue);
} else {
  // 服务端渲染使用的install
  install();
}
