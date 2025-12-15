import Vue from 'vue';
import Vuex from 'vuex';
import VueI18n from 'vue-i18n';
import adv from 'ant-design-vue';
import 'ant-design-vue/dist/antd.min.css';
import HtmlWrapper from '../../widget/html-wrapper';
import PerfectScrollbar from 'vue2-perfect-scrollbar';
import vueExtend from './lib/vue.extends';
import Scroll from '../../widget/scroll.vue';
import 'vue2-perfect-scrollbar/dist/vue2-perfect-scrollbar.css';
import Icon from '../../widget/icon.js';
import UserDisplay from '../../widget/user-display.vue';
// 加载默认平台的图标库
import '@pageAssembly/app/public/css/pt/iconfont/iconfont.css';
import '@pageAssembly/app/public/css/pt/ant-iconfont/iconfont.css';
import HeaderModifyPassword from '@pageAssembly/app/web/widget/widget-menu/component/header-modify-password.vue';

// 加载项目的字体样式文件
import '~/app/public/css/app-iconfont/iconfont.css';

// 加载主题包
import '@modules/.webpack.themepack.js';
import Error from '../../widget/error.vue';
import SvgIcon from '../../widget/svg-icon.vue';

import '../../style/css/index.less';
Vue.component(Icon.name, Icon);
Vue.component(HtmlWrapper.name, HtmlWrapper);
Vue.component(Scroll.name, Scroll);
Vue.component(Error.name, Error);
Vue.component(UserDisplay.name, UserDisplay);
Vue.component(SvgIcon.name, SvgIcon);
Vue.component(HeaderModifyPassword.name, HeaderModifyPassword);
Vue.use(adv); // ant design vue ui 框架包
Vue.use(VueI18n);
Vue.use(Vuex);
Vue.use(PerfectScrollbar);
Vue.use(vueExtend);

// Vue.use(wui);
