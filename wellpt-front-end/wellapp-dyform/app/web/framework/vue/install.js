import Vue from 'vue';
import '../../assets/css/form.less';

const components = require.context('../../widget', true, /\/widget[-\w]+\.vue$/);

const install = function (vue) {
  vue = vue || Vue;
  const keys = components.keys();
  // if (EASY_ENV_IS_BROWSER) {
  //   console.log('加载组件', keys);
  // }
  keys.map(fileName => {
    let comp = components(fileName).default;
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
