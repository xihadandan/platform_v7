import Vue from 'vue';

const components = require.context('./', true, /\w+\.vue$/);

const install = function (vue) {
  vue = vue || Vue;

  components.keys().map(fileName => {
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
