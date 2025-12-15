import Vue from 'vue';
import WidgetTemplate from '@pageAssembly/app/web/widget/widget-template/widget-template.js';

const components = require.context('../../widget', true, /^\.\/widget[-\w]+\/widget[-\w]+\.vue$/);
import WidgetVpage from '@pageAssembly/app/web/widget/widget-vpage.vue';

const install = function (vue) {
  vue = vue || Vue;
  const keys = components.keys();

  vue.component(WidgetTemplate.name, WidgetTemplate);
  vue.component(WidgetVpage.name, WidgetVpage);


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
