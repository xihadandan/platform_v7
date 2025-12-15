<template>
  <div class="theme-style-panel">
    <a-page-header title="复选框">
      <div slot="subTitle">平台表单复选框</div>
      <!-- 基础样式 -->
      <a-card :class="[selectedKey == 'basicStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('basicStyle')">
        <template #title>
          基础样式
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('basicStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2" class="theme-checkbox-base">
          <a-descriptions-item>
            <a-checkbox>默认</a-checkbox>
            <a-checkbox class="checkbox_hover">悬停</a-checkbox>
            <a-checkbox checked>选中</a-checkbox>
            <a-checkbox disabled>禁用</a-checkbox>
            <a-checkbox checked disabled>选中禁用</a-checkbox>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <!-- 错误提示 -->
      <a-card :class="[selectedKey == 'errorStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('errorStyle')">
        <template #title>
          错误提示
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('errorStyle')" />
        </template>
        <a-form-model layout="vertical" :colon="false" :model="testForm" :rules="testRules" ref="testForm">
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item label="错误提示 Error">
              <a-form-model-item prop="testInput" required class="full-line80">
                <a-checkbox>选项一</a-checkbox>
              </a-form-model-item>
            </a-descriptions-item>
          </a-descriptions>
        </a-form-model>
      </a-card>
      <!-- 选项组 -->
      <a-card title="选项组" :style="vStyle">
        <div
          :class="[selectedKey == 'groupItemMarginStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('groupItemMarginStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item>
              <template #label>
                水平布局-等距
                <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('groupItemMarginStyle')" />
              </template>
              <a-checkbox-group v-model="value" name="checkboxgroup" :options="plainOptions" />
            </a-descriptions-item>
          </a-descriptions>
        </div>
        <div
          :class="[selectedKey == 'groupVerticalStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('groupVerticalStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item>
              <template #label>
                垂直布局
                <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('groupVerticalStyle')" />
              </template>
              <a-checkbox-group v-model="value" class="ant-checkbox-group-vertical">
                <a-checkbox v-for="(opt, i) in plainOptions" :key="i" :style="verticalStyle" :value="opt.value">
                  {{ opt.label }}
                </a-checkbox>
              </a-checkbox-group>
            </a-descriptions-item>
          </a-descriptions>
        </div>
      </a-card>
    </a-page-header>
  </div>
</template>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import { kebabCase } from 'lodash';

export default {
  name: 'ThemeCheckbox',
  mixins: [themePropMixin],
  props: {
    config: Object
  },
  title: '复选框',
  category: 'formBasicComp',
  themePropConfig: {
    // 基础样式
    basicStyle: {
      wCheckboxTextColor: '--w-text-color-dark',
      wCheckboxTextSize: '--w-font-size-base',
      wCheckboxTextWeight: '--w-font-weight-regular',
      wCheckboxBorderColor: '--w-border-color-darker',
      wCheckboxSize: '--w-font-size-lg',
      wCheckboxLrPadding: '--w-padding-2xs',

      wCheckboxTextColorHover: '--w-text-color-dark',
      wCheckboxTextSizeHover: '--w-font-size-base',
      wCheckboxTextWeightHover: '--w-font-weight-regular',
      wCheckboxColorHover: '--w-primary-color',

      wCheckboxTextColorChecked: '--w-text-color-dark',
      wCheckboxTextSizeChecked: '--w-font-size-base',
      wCheckboxTextWeightChecked: '--w-font-weight-regular',
      wCheckboxColor: '--w-primary-color',

      wCheckboxDisabledBorderColor: '--w-border-color-light',
      wCheckboxDisabledBg: '--w-fill-color-base',
      wCheckboxDisabledBgChecked: '--w-fill-color-light',
      wCheckboxDisabledColor: '--w-text-color-lighter'
    },
    errorStyle: {
      wCheckboxErrorTextColor: '--w-danger-color',
      wCheckboxErrorTextSize: '--w-font-size-base',
      wCheckboxErrorTextWeight: '--w-font-weight-regular'
    },
    groupItemMarginStyle: {
      wCheckboxGroupItemMr: '--w-padding-2xs'
    },
    groupVerticalStyle: {
      wCheckboxGroupVerticalMr: '--w-margin-2xs',
      wCheckboxGroupVerticalMb: '--w-margin-2xs'
    }
  },
  computed: {
    vStyle() {
      let colorVars = {},
        keys = ['basicStyle', 'errorStyle', 'groupItemMarginStyle', 'groupVerticalStyle'];
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
      return vars;
    }
  },
  data() {
    return {
      selectedKey: 'basicStyle',
      testForm: { testInput: undefined },
      testRules: {
        testInput: [
          {
            required: true,
            message: (
              <span class="checkbox-error">
                <a-icon type="warning" /> 必填
              </span>
            )
          }
        ]
      },
      plainOptions: [
        { label: '选项一', value: '1' },
        { label: '选项二', value: '2' },
        { label: '选项三', value: '3' }
      ],
      value: [],
      verticalStyle: {
        display: 'block'
      }
    };
  },
  mounted() {
    this.$emit('select', this.config.basicStyle);
    this.$refs.testForm.validate();
  },
  methods: {
    onClickCard(key, refresh) {
      if (this.selectedKey != key || refresh) {
        let keyComp = {
          basicStyle: 'ThemeCheckboxProp',
          errorStyle: 'ThemeCheckboxError',
          groupItemMarginStyle: 'ThemeCheckboxGroupMargin',
          groupVerticalStyle: 'ThemeCheckboxGroupVertical'
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
