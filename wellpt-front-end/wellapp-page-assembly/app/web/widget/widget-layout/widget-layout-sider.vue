<template>
  <div class="widget-layout-sider-container">
    <a-layout-sider
      :key="layoutSiderKey"
      v-model="collapsed"
      :theme="vSiderTheme"
      :class="['widget-layout-sider', bgColorType]"
      :collapsedWidth="siderWidgetsEmpty ? 0 : collapsedWidth"
      :collapsible="widget.configuration.collapsible"
      :width="siderWidgetsEmpty ? 0 : siderWidth"
      :style="vSiderStyle"
      :trigger="null"
    >
      <Scroll :style="vSiderScrollStyle" :suppressScrollX="true">
        <div class="logo" v-if="vSiderLogo">
          <img :src="vSiderLogo" @load="getImageDimensions" :style="vSiderLogoStyle" />
        </div>
        <template v-for="(wgt, index) in widget.configuration.widgets">
          <component
            :key="wgt.id"
            :is="resolveWidgetType(wgt)"
            :widget="wgt"
            :index="index"
            :widgetsOfParent="widget.configuration.widgets"
            :designer="designer"
          ></component>
        </template>
      </Scroll>
    </a-layout-sider>
    <a-tooltip placement="right" :overlayStyle="{ zIndex: 10000 }">
      <template slot="title">
        <span>{{ collapsed ? $t('WidgetLayout.expand', '展开') : $t('WidgetLayout.collapse', '收起') }}</span>
      </template>
      <Icon
        v-if="widget.configuration.collapsible"
        :type="collapsed ? 'double-right' : 'double-left'"
        :class="['sider-trigger']"
        :style="{ left: collapsed ? collapsedWidth + 'px' : siderWidth + 'px' }"
        @click.stop="changeCollapsed"
      />
    </a-tooltip>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { getCookie } from '@framework/vue/utils/util';
import widgetMixin from '@framework/vue/mixin/widgetMixin';
export default {
  name: 'WidgetLayoutSider',
  mixins: [widgetMixin],
  props: {
    backgroundColorType: {
      type: String
    },
    siderWidth: {
      type: Number,
      default: 224
    },
    collapsedWidth: {
      type: Number,
      default: 64
    }
  },
  data() {
    return {
      collapsed: this.widget.configuration.defaultCollapsed === true,
      height: EASY_ENV_IS_NODE ? 1000 : 600,
      bgColorType: this.backgroundColorType || this.widget.configuration.backgroundColorType,
      layoutSiderKey: `${this.widget.id}`,
      logoNaturalWidth: 1,
      logoNaturalHeight: 1,
      collapsedLoading: false
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    // 首页布局中，且为siderTopMiddleBottom布局时，且没有widget时，隐藏sider
    siderWidgetsEmpty() {
      return (
        this.parent.main && this.parent.configuration.layoutType != 'siderTopMiddleBottom' && this.widget.configuration.widgets.length == 0
      );
    },
    vSiderLogo() {
      if (this.parent.configuration.logoPosition == 'sider') {
        let bgColorType = this.bgColorType;
        if (bgColorType == 'primary-color') {
          // 左导航情况下的主题色logo, 按深色logo处理
          bgColorType = 'dark';
        }
        let bgTypeLogo = this.widget.configuration[`${bgColorType}BgColorTypeLogo`];
        if (bgTypeLogo !== undefined) {
          return bgTypeLogo;
        }
        return this.parent.configuration.header.configuration.logo;
      }
      return undefined;
    },
    vSiderLogoStyle() {
      let vSiderLogoStyle = {};
      if (this.collapsed) {
        vSiderLogoStyle = { maxWidth: '100%' };
      } else {
        if (this.logoNaturalWidth / this.logoNaturalHeight < 1.2 && this.logoNaturalWidth / this.logoNaturalHeight > 0.8) {
          vSiderLogoStyle = { maxWidth: '120px' };
        } else if (this.logoNaturalWidth > this.logoNaturalHeight) {
          vSiderLogoStyle = { maxWidth: '100%' };
        } else {
          vSiderLogoStyle = { maxHeight: 'calc(224px - 40px)' };
        }
      }
      return vSiderLogoStyle;
    },
    vSiderTheme() {
      return this.bgColorType == 'primary-color' ? 'light' : this.bgColorType;
    },
    vSiderStyle() {
      let style = {
        // position: this.widget.configuration.positionFixed ? 'fixed' : 'relative'
      };
      if (this.designMode) {
        style['pointer-events'] = 'none';
      }
      return style;
    },
    siderIsNotEmpty() {
      return this.widget.configuration.widgets.length != 0;
    },
    vSiderScrollStyle() {
      let style = {
        height: this.vHeight
      };

      return style;
    },
    vHeight() {
      return typeof this.height == 'number' ? this.height + 'px' : this.height;
    }
  },
  provide() {
    return {
      backgroundColorType: this.bgColorType
    };
  },
  created() {},
  methods: {
    restoreLayoutSider({ configuration }) {
      this.widget.configuration = configuration;
      this.layoutSiderKey = `${this.widget.id}_${new Date().getTime()}`;
    },
    updateHeight(height) {
      let _style = window.getComputedStyle(this.$el);
      let marginHeight = parseInt(_style.marginBottom) + parseInt(_style.marginTop); // + (this.widget.configuration.collapsible ? 48 : 0);
      if (typeof height == 'number') {
        this.height = height - marginHeight;
      } else if (height.indexOf('calc(') == 0) {
        let _temp = height.substring(height.indexOf('calc(') + 5, height.length - 1).trim();
        this.height = `calc(${_temp} - ${marginHeight}px)`;
      } else {
        this.height = parseInt(height) - marginHeight;
      }
    },
    getImageDimensions(event) {
      const img = event.target;
      this.logoNaturalWidth = img.naturalWidth;
      this.logoNaturalHeight = img.naturalHeight;
    },
    changeCollapsed() {
      if (!this.collapsedLoading) {
        this.collapsed = !this.collapsed;
        this.collapsedLoading = true;
      } else {
        this.$nextTick(() => {
          this.collapsedLoading = false;
        });
      }
    }
  },
  mounted() {
    let _this = this;
    this.pageContext.handleEvent(`WidgetLayoutSider:Update:${this.widget.id}`, function (data) {
      _this.widget.configuration.widgets = Array.isArray(data.widgets) ? data.widgets : [data.widgets];
    });
  }
};
</script>
