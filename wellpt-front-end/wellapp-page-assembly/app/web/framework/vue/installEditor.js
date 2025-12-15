import Vue from 'vue';

import '../../widget/commons/index.js'
import '@installPageWidget'
const configuration = require.context('../../widget', true, /^\.\/widget[-\w]+\/configuration\/.+\.vue$/); // 编辑组件必须非懒加载，否则draggable首次拖拉会出现排序问题

const install = function (vue) {
  vue = vue || Vue;
  let configKeys = configuration.keys();

  configKeys.map(fileName => {
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
