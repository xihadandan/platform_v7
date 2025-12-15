<template>
  <div class="theme-style-panel">
    <a-page-header title="超链接">
      <div slot="subTitle">平台表单超链接样式</div>
      <a-card :class="[selectedKey == 'basicStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('basicStyle')">
        <template #title>
          基础样式
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('basicStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2">
          <a-descriptions-item label="默认">
            <a class="widget-hyperlink">超链接</a>
          </a-descriptions-item>
          <a-descriptions-item label="悬停">
            <a class="widget-hyperlink widget-hyperlink-hover">超链接</a>
          </a-descriptions-item>
          <a-descriptions-item label="点击">
            <a class="widget-hyperlink widget-hyperlink-active">超链接</a>
          </a-descriptions-item>
          <a-descriptions-item label="禁用">
            <a class="widget-hyperlink-disabled">超链接</a>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import { kebabCase } from 'lodash';
export default {
  name: 'ThemeLink',
  mixins: [themePropMixin],
  components: {},
  props: {
    config: Object
  },
  title: '超链接',
  category: 'formBasicComp',
  themePropConfig: {
    basicStyle: {
      // 默认
      wLinkTextColor: '--w-link-color',
      wLinkFontSize: '--w-font-size-base',
      wLinkFontWeight: '--w-font-weight-regular',
      wLinkBackgroundColor: undefined,
      wLinkLrPadding: '--w-padding-2xs',
      // 悬停
      wLinkColorHover: '--w-link-color-5',
      wLinkFontSizeHover: '--w-font-size-base',
      wLinkFontWeightHover: '--w-font-weight-regular',
      wLinkBackgroundColorHover: undefined,
      // 点击
      wLinkColorActive: '--w-link-color-7',
      wLinkFontSizeActive: '--w-font-size-base',
      wLinkFontWeightActive: '--w-font-weight-regular',
      wLinkBackgroundColorActive: undefined,
      // 禁用
      wLinkColorDisabled: '--w-text-color-light',
      wLinkFontSizeDisabled: '--w-font-size-base',
      wLinkFontWeightDisabled: '--w-font-weight-regular',
      wLinkBackgroundColorDisabled: undefined
    },
    countNumStyle: {},
    clearStyle: {},
    prefixSuffixAddonStyle: {},
    prefixSuffixStyle: {},
    passwordStyle: {}
  },
  computed: {
    vStyle() {
      let colorVars = {},
        keys = ['basicStyle'];
      for (let prop of keys) {
        if (this.config[prop]) {
          for (let key in this.config[prop]) {
            if (this.config[prop][key] != undefined) {
              if (key.toLowerCase().indexOf('boxshadow') != -1) {
                // 阴影样式处理
                colorVars['--' + kebabCase(key)] = this.config[prop][key].startsWith('inset ')
                  ? 'inset var(' + this.config[prop][key].split('inset ')[1] + ')'
                  : `var(${this.config[prop][key]})`;
              } else {
                colorVars['--' + kebabCase(key)] = this.config[prop][key].startsWith('--w-')
                  ? `var(${this.config[prop][key]})`
                  : this.config[prop][key];
              }
            }
          }
        }
      }

      let vars = { ...colorVars, ...this.basicStyleCssVars };
      console.log('colorVars', colorVars);
      // console.log('输入框样式变量: ', colorVars,this.basicStyleCssVars);
      return vars;
    }
  },
  data() {
    return {
      selectedKey: 'basicStyle'
    };
  },
  mounted() {
    this.$emit('select', this.config.basicStyle);
    // this.$refs.testForm.validate();
  },
  methods: {
    onClickCard(key, refresh) {
      if (this.selectedKey != key || refresh) {
        let keyComp = {
          basicStyle: 'ThemeLinkProp'
          // errorStyle: 'ThemeLinkErrorProp'
        };
        this.selectedKey = key;
        this.$emit('select', this.config[key], null, keyComp[key]);
      }
    },
    undoThemePropConfig(key) {
      this.config[key] = JSON.parse(JSON.stringify(this.$options.themePropConfig[key]));
      this.onClickCard(key, true);
    }
  }
};
</script>
