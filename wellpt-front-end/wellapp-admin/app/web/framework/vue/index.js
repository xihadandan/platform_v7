import Vue from 'vue';
import OrgSelect from '../../lib/org-select.vue';

const install = function (vue) {
  vue = vue || Vue;
  vue.component('OrgSelect', OrgSelect);
};

if (typeof window !== 'undefined' && window.Vue) {
  // 客户端渲染使用的install
  install(window.Vue);
} else {
  // 服务端渲染使用的install
  install();
}
