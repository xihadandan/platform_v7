import Vue from 'vue';

const components = require.context('../../widget/bigscreen', true, /\/widget[-\w]+\/widget[-\w]+\.vue$/);

import WidgetTable from '@pageAssembly/app/web/widget/widget-table/widget-table.vue';

const install = function (vue) {
  vue = vue || Vue;
  const keys = components.keys();
  if (EASY_ENV_IS_BROWSER) {
    console.log('加载组件', keys);
  }
  vue.component(WidgetTable.name, WidgetTable);

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
