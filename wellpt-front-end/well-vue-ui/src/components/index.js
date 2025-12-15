// main

import Input from './input';
import Button from './button';

// 加载的组件
const components = { Input, Button };

const install = function (Vue) {
  // if (install.installed) return;
  // install.installed = true;

  Object.keys(components).forEach(key => {
    Vue.component(components[key].name, components[key]);
  });
};

if (typeof window !== 'undefined' && window.Vue) {
  install(window.Vue);
}

export default {
  install,
  ...components
};
