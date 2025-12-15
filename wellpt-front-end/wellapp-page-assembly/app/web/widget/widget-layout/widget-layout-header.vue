<template>
  <a-layout-header :class="['widget-layout-header', bgColorType]" :style="vHeaderStyle">
    <div class="header-main" style="display: flex">
      <div class="logo" v-if="vHeaderLogo != undefined" style="min-width: fit-content">
        <!-- <a-skeleton active :paragraph="false" v-if="logoLoading" :title="{ width: '100px' }" /> -->
        <img :src="vHeaderLogo" @load="onHeaderImgLoad" ref="logoImg" :style="vHeaderImgStyle" />
      </div>
      <div class="logo" v-if="widget.configuration.enablePrefixTitleLogo && widget.configuration.prefixTitleLogo != undefined">
        <img :src="widget.configuration.prefixTitleLogo" :style="vPrefixTitleLogoStyle" />
      </div>
      <h1 class="title" v-if="title" v-html="$t('headerTitle', title)"></h1>
      <div style="overflow: hidden; width: 100%">
        <template v-for="(wgt, index) in headerWidgets">
          <component
            :key="wgt.id"
            :is="resolveWidgetType(wgt)"
            :widget="wgt"
            :index="index"
            :widgetsOfParent="headerWidgets"
            :parent="widget"
            :designer="designer"
          ></component>
        </template>
      </div>
    </div>

    <div v-if="menuBarWidgets.length > 0" class="menu-bar">
      <!-- 菜单栏 -->
      <template v-for="(wgt, index) in menuBarWidgets">
        <component
          :key="wgt.id"
          :is="resolveWidgetType(wgt)"
          :widget="wgt"
          :index="index"
          :widgetsOfParent="menuBarWidgets"
          :parent="widget"
          :designer="designer"
        ></component>
      </template>
    </div>
  </a-layout-header>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { deepClone, generateId } from '@framework/vue/utils/util';
import '@pageAssembly/app/web/lib/quill-editor/quill-editor.less';

export default {
  name: 'WidgetLayoutHeader',
  mixins: [widgetMixin],
  props: {
    backgroundColorType: {
      type: String
    }
  },
  data() {
    let headerWidgets = this.widget.configuration.widgets,
      menuBarWidgets = [];
    if (this.parent.main) {
      if (this.widget.configuration.showHeaderMenuBar) {
        // 系统首页情况下的菜单栏展示
        // 显示菜单栏，则把菜单导航的产品菜单拆分到菜单栏内显示，系统导航顶部展示
        let widgetMenu = deepClone(this.widget.configuration.widgets[0]);
        widgetMenu.id = generateId();
        widgetMenu.configuration.mode = 'horizontal';
        widgetMenu.configuration.enableSysMenu = true;
        widgetMenu.configuration.menus = []; // 置空导航菜单，仅保留系统级导航
        headerWidgets = [widgetMenu];
        // 创建导航菜单
        let navWidget = deepClone(this.widget.configuration.widgets[0]);
        navWidget.configuration.sysFunctionMenus = []; // 置空系统级导航，保留导航菜单
        navWidget.configuration.mode = 'horizontal';
        navWidget.configuration.enableSysMenu = false;
        menuBarWidgets.push(navWidget);
      }
      // 从侧边栏获取导航组件的系统级导航生成到头部展示
      let widgetMenu = this.parent.configuration.sider.configuration.widgets[0];
      if (widgetMenu && widgetMenu.main) {
        let headerSysMenu = deepClone(widgetMenu);
        headerSysMenu.id = generateId();
        headerSysMenu.configuration.menus = [];
        headerSysMenu.configuration.enableSysMenu = true;
        headerSysMenu.configuration.mode = 'horizontal';
        headerWidgets = [headerSysMenu];
      }
    }
    let bgColorType = this.backgroundColorType || this.widget.configuration.backgroundColorType;
    return {
      headerWidgets,
      menuBarWidgets,
      bgColorType,
      subWidgetBgColorType: this.widget.configuration.bgImage ? 'transparent' : bgColorType,
      logoLoading: true,
      title: this.widget.configuration.title
    };
  },
  provide() {
    return {
      backgroundColorType: this.subWidgetBgColorType,
      layoutType: this.parent.configuration.layoutType
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    vPrefixTitleLogoStyle() {
      // 初始根据图片比例计算最小宽度，避免图片加载前后导致后面的dom元素移动现象
      let minWidth = 'fit-content';
      if (this.widget.configuration.prefixTitleLogoAspectRatio != undefined) {
        minWidth = Math.ceil(36 * this.widget.configuration.prefixTitleLogoAspectRatio) + 'px';
      }
      return {
        minWidth
      };
    },
    vHeaderImgStyle() {
      // 初始根据图片比例计算最小宽度，避免图片加载前后导致后面的dom元素移动现象
      let minWidth = 'fit-content';
      let bgColorType = this.bgColorType;
      if (bgColorType == 'primary-color') {
        // 头部的主题色logo，按暗色logo处理
        bgColorType = 'dark';
      }
      let bgTypeLogo = this.widget.configuration[`${bgColorType}BgColorTypeLogo`];
      if (bgTypeLogo !== undefined) {
        // 存在具体背景色的logo
        if (this.widget.configuration[`${bgColorType}BgColorTypeLogoAspectRatio`] != undefined) {
          minWidth = Math.ceil(36 * this.widget.configuration[`${bgColorType}BgColorTypeLogoAspectRatio`]) + 'px';
        }
      } else if (this.widget.configuration.logoAspectRatio != undefined) {
        minWidth = Math.ceil(36 * this.widget.configuration.logoAspectRatio) + 'px';
      }
      return {
        minWidth
      };
    },
    vHeaderLogo() {
      if (this.parent.configuration.logoPosition == 'header' || this.parent.configuration.logoPosition == undefined) {
        let bgColorType = this.bgColorType;
        if (bgColorType == 'primary-color') {
          // 头部的主题色logo，按暗色logo处理
          bgColorType = 'dark';
        }
        let bgTypeLogo = this.widget.configuration[`${bgColorType}BgColorTypeLogo`];
        if (bgTypeLogo !== undefined) {
          return bgTypeLogo;
        }
        return this.widget.configuration.logo;
      }
      return undefined;
    },

    vHeaderStyle() {
      let style = {};
      // if (this.widget.configuration.fixed) {
      //   style.position = 'fixed';
      //   style.top = '0px';
      //   if (this.parent.configuration.layoutType == 'topMiddleBottom') {
      //     // 没有侧边栏
      //     style.width = '100%';
      //   }
      // }
      if (this.widget.configuration.bgImage) {
        style.backgroundImage = `url(${this.widget.configuration.bgImage})`;
        style.border = 'unset';
      }
      if (this.designMode) {
        style['pointer-events'] = 'none';
      }
      return style;
    }
  },
  created() {},
  methods: {
    onHeaderImgLoad() {
      this.logoLoading = false;
    }
  },
  mounted() {
    if (this.parent.main) {
      // 主布局
      this.pageContext.handleEvent(`MainWidgetLayoutHeader:TitleLogoUpdate`, d => {
        if (d.title != undefined) {
          this.title = d.title;
        }
        if (d.logo != undefined) {
          this.widget.configuration.logo = d.logo;
        }
      });
    }
  }
};
</script>
