<template>
  <div class="theme-style-panel">
    <a-page-header title="开关">
      <div slot="subTitle">平台表单开关</div>
      <!-- 基础样式 -->
      <a-card :class="[selectedKey == 'basicStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('basicStyle')">
        <template #title>
          基础样式
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('basicStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2">
          <a-descriptions-item label="默认  Default">
            <div style="margin-bottom: 20px">
              <a-switch default-checked style="margin-right: 30px" />
              <a-switch />
            </div>
            <div>
              <a-switch checked-children="开启" un-checked-children="关闭" default-checked style="margin-right: 18px" />
              <a-switch checked-children="开启" un-checked-children="关闭" />
            </div>
          </a-descriptions-item>
          <a-descriptions-item label="禁用  Disable">
            <div style="margin-bottom: 20px">
              <a-switch default-checked :disabled="true" style="margin-right: 30px" />
              <a-switch :disabled="true" />
            </div>
            <div>
              <a-switch :disabled="true" checked-children="开启" un-checked-children="关闭" default-checked style="margin-right: 18px" />
              <a-switch :disabled="true" checked-children="开启" un-checked-children="关闭" />
            </div>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
    </a-page-header>
  </div>
</template>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import { kebabCase } from 'lodash';

export default {
  name: 'ThemeSwitch',
  mixins: [themePropMixin],
  props: {
    config: Object
  },
  title: '开关',
  category: 'formBasicComp',
  themePropConfig: {
    // 基础样式
    basicStyle: {
      wSwitchBorderColor: '--w-border-color-light',
      wSwitchBorderWidth: '--w-border-width-base',
      wSwitchBorderStyle: '--w-border-style-base',
      wSwitchBorderRadius: '--w-border-radius-5',
      wSwitchTextColorChecked: '--w-gray-color-1',
      wSwitchTextColor: '--w-text-color-light',
      wSwitchTextSize: '--w-font-size-sm',
      wSwitchTextWeight: '--w-font-weight-regular',
      wSwitchCheckedBg: '--w-primary-color',
      wSwitchHeight: '--w-height-2xs',
      wSwitchCircleSize: '--w-font-size-2xl',
      wSwitchCircleBg: '--w-gray-color-1',

      wSwitchBorderColorDisabled: '--w-border-color-base',
      wSwitchTextColorCheckedDisabled: '--w-gray-color-1',
      wSwitchTextColorDisabled: '--w-text-color-light',
      wSwitchCheckedBgDisabled: '--w-primary-color',
      wSwitchCircleBgDisabled: '--w-gray-color-1'
    }
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
  },
  methods: {
    onClickCard(key, refresh) {
      if (this.selectedKey != key || refresh) {
        let keyComp = {
          basicStyle: 'ThemeSwitchProp'
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
