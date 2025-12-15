import Vue from 'vue';

const commons = require.context('./', true, /.+\.vue$/);


const install = function (vue) {
  vue = vue || Vue;
  // console.log(commons.keys())
  commons.keys().map(fileName => {
    // let fn = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.vue')),
    //   compName = upperFirst(camelCase(fn));
    // vue.component(compName, () => commons(fileName));
    let comp = commons(fileName).default;
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
