import Vue from 'vue';
import EditWrapper from './edit-wrapper.vue';
import wDesignItem from './w-design-item.vue';
import wI18nInput from './w-i18n-input.vue';
import WAppCodeI18nInput from './w-app-code-i18n-input.vue';
const install = function (vue) {
  vue = vue || Vue;
  vue.component(wDesignItem.name, wDesignItem);
  vue.component(EditWrapper.name, EditWrapper);
  vue.component(wI18nInput.name, wI18nInput);
  vue.component(WAppCodeI18nInput.name, WAppCodeI18nInput);

};

if (typeof window !== 'undefined' && window.Vue) {
  // 客户端渲染使用的install
  install(window.Vue);
  window.Vue.component(EditWrapper.name, EditWrapper);
}
