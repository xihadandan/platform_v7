<template>
  <div class="theme-style-panel">
    <a-page-header title="提示">
      <div slot="subTitle">平台表单提示样式</div>
      <a-card :class="[selectedKey == 'basicStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('basicStyle')">
        <template #title>
          基础样式
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('basicStyle')" />
        </template>
        <a-tooltip :defaultVisible="false" overlayClassName="widget-tooltip-overlay" :getPopupContainer="trigger => trigger.parentNode">
          <template #title>提示说明</template>
          <span class="widget-tooltip"><Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" /></span>
        </a-tooltip>
      </a-card>
      <a-card :class="[selectedKey == 'bubbleStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('bubbleStyle')">
        <template #title>
          气泡
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('bubbleStyle')" />
        </template>
        <div class="bubble-group">
          <a-tooltip
            :defaultVisible="true"
            v-model="visible"
            overlayClassName="widget-tooltip-overlay"
            @visibleChange="visibleChange"
            :getPopupContainer="trigger => trigger.parentNode"
          >
            <template #title>提示说明</template>
            <span class="widget-tooltip"><Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" /></span>
          </a-tooltip>
        </div>
      </a-card>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import { kebabCase } from 'lodash';
export default {
  name: 'ThemeTip',
  mixins: [themePropMixin],
  components: {},
  props: {
    config: Object
  },
  title: '提示',
  category: 'formBasicComp',
  themePropConfig: {
    basicStyle: {
      wTipIconColor: '--w-text-color-light',
      wTipIconColorHover: '--w-text-color-base',
      wTipIconFontSize: '--w-font-size-base'
    },
    bubbleStyle: {
      wTipBorderColor: '--w-border-color-base',
      wTipBorderStyle: '--w-border-style-base',
      wTipBorderWidth: undefined,
      wTipBorderRadius: '--w-border-radius-base',
      wTipColor: '--w-text-color-base',
      wTipFontSize: '--w-font-size-base',
      wTipFontWeight: '--w-font-weight-regular',
      wTipBackgroundColor: '--w-color-white',
      wTipLrPadding: '--w-padding-2xs',
      wTipBoxShadow: undefined
    }
  },
  computed: {
    vStyle() {
      let colorVars = {},
        keys = ['basicStyle', 'bubbleStyle'];
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
      console.log('样式变量: ', colorVars, this.basicStyleCssVars);
      return vars;
    }
  },
  data() {
    return {
      selectedKey: 'basicStyle',
      visible: true
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
          basicStyle: 'ThemeTipProp',
          bubbleStyle: 'ThemeTipBubbleProp'
        };
        this.selectedKey = key;
        this.$emit('select', this.config[key], null, keyComp[key]);
      }
    },
    visibleChange(visible) {
      console.log(visible, '----------tip事件');
      this.visible = true;
    },
    undoThemePropConfig(key) {
      this.config[key] = JSON.parse(JSON.stringify(this.$options.themePropConfig[key]));
      this.onClickCard(key, true);
    }
  }
};
</script>
