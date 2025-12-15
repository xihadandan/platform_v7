<template>
  <div class="theme-style-panel">
    <a-page-header title="单选按钮">
      <div slot="subTitle">平台表单单选按钮</div>
      <!-- 基础样式 -->
      <a-card :class="[selectedKey == 'basicStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('basicStyle')">
        <template #title>
          基础样式
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('basicStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2" class="theme-radio-base">
          <a-descriptions-item>
            <a-radio>默认</a-radio>
            <a-radio class="radio_hover">悬停</a-radio>
            <a-radio checked>选中</a-radio>
            <a-radio disabled>禁用</a-radio>
            <a-radio checked disabled>选中禁用</a-radio>
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
                <a-radio>选项一</a-radio>
              </a-form-model-item>
            </a-descriptions-item>
          </a-descriptions>
        </a-form-model>
      </a-card>
      <!-- 选项组 -->
      <a-card title="选项组" :style="vStyle">
        <div
          :class="[selectedKey == 'groupItemWidthStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('groupItemWidthStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item>
              <template #label>
                水平布局-等宽
                <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('groupItemWidthStyle')" />
              </template>
              <a-radio-group :default-value="1" class="theme-radio-group-width">
                <a-radio :value="1">选项一</a-radio>
                <a-radio :value="2">选项二</a-radio>
                <a-radio :value="3">选项三</a-radio>
                <a-radio :value="4">选项四</a-radio>
              </a-radio-group>
            </a-descriptions-item>
          </a-descriptions>
        </div>
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
              <a-radio-group :default-value="1" class="theme-radio-group-item">
                <a-radio :value="1">选项一</a-radio>
                <a-radio :value="2">选项二</a-radio>
                <a-radio :value="3">选项三</a-radio>
                <a-radio :value="4">选项四</a-radio>
              </a-radio-group>
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
              <a-radio-group :default-value="1" class="radio-group-vertical">
                <a-radio :style="verticalStyle" :value="1">选项一</a-radio>
                <a-radio :style="verticalStyle" :value="2">选项二</a-radio>
                <a-radio :style="verticalStyle" :value="3">选项三</a-radio>
                <a-radio :style="verticalStyle" :value="4">选项四</a-radio>
              </a-radio-group>
            </a-descriptions-item>
          </a-descriptions>
        </div>
      </a-card>
      <!-- 显示为按钮 -->
      <a-card title="显示为按钮" :style="vStyle">
        <div
          :class="[selectedKey == 'groupButtonSolidStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('groupButtonSolidStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item>
              <template #label>
                填充风格
                <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('groupButtonSolidStyle')" />
              </template>
              <a-radio-group :default-value="1" button-style="solid">
                <a-radio-button :value="1">选项一</a-radio-button>
                <a-radio-button :value="2">选项二</a-radio-button>
                <a-radio-button :value="3" :disabled="true">禁用</a-radio-button>
                <a-radio-button :value="4">选项四</a-radio-button>
              </a-radio-group>
              <div style="height: 20px"></div>
              <a-radio-group :default-value="1" button-style="solid" disabled>
                <a-radio-button :value="1">选项一</a-radio-button>
                <a-radio-button :value="2">选项二</a-radio-button>
                <a-radio-button :value="3" :disabled="true">禁用</a-radio-button>
                <a-radio-button :value="4">选项四</a-radio-button>
              </a-radio-group>
            </a-descriptions-item>
          </a-descriptions>
        </div>
        <div
          :class="[selectedKey == 'groupButtonlineStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('groupButtonlineStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item>
              <template #label>
                线框风格
                <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('groupButtonlineStyle')" />
              </template>
              <a-radio-group :default-value="1">
                <a-radio-button :value="1">选项一</a-radio-button>
                <a-radio-button :value="2">选项二</a-radio-button>
                <a-radio-button :value="3" :disabled="true">禁用</a-radio-button>
                <a-radio-button :value="4">选项四</a-radio-button>
              </a-radio-group>
              <div style="height: 20px"></div>
              <a-radio-group :default-value="1" disabled>
                <a-radio-button :value="1">选项一</a-radio-button>
                <a-radio-button :value="2">选项二</a-radio-button>
                <a-radio-button :value="3" :disabled="true">禁用</a-radio-button>
                <a-radio-button :value="4">选项四</a-radio-button>
              </a-radio-group>
            </a-descriptions-item>
          </a-descriptions>
        </div>
      </a-card>
      <!-- 显示为按钮 -->
      <a-card title="按钮组件尺寸" :style="vStyle">
        <div
          :class="[selectedKey == 'groupButtonDefaultStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('groupButtonDefaultStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item>
              <template #label>
                普通尺寸
                <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('groupButtonDefaultStyle')" />
              </template>
              <a-radio-group :default-value="1">
                <a-radio-button :value="1">选项一</a-radio-button>
                <a-radio-button :value="2">选项二</a-radio-button>
                <a-radio-button :value="3" :disabled="true">禁用</a-radio-button>
                <a-radio-button :value="4">选项四</a-radio-button>
              </a-radio-group>
            </a-descriptions-item>
          </a-descriptions>
        </div>
        <div
          :class="[selectedKey == 'groupButtonSmallStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('groupButtonSmallStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item>
              <template #label>
                较小尺寸
                <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('groupButtonSmallStyle')" />
              </template>
              <a-radio-group :default-value="1" size="small">
                <a-radio-button :value="1">选项一</a-radio-button>
                <a-radio-button :value="2">选项二</a-radio-button>
                <a-radio-button :value="3" :disabled="true">禁用</a-radio-button>
                <a-radio-button :value="4">选项四</a-radio-button>
              </a-radio-group>
            </a-descriptions-item>
          </a-descriptions>
        </div>
        <div
          :class="[selectedKey == 'groupButtonLargeStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('groupButtonLargeStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item>
              <template #label>
                较大尺寸
                <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('groupButtonLargeStyle')" />
              </template>
              <a-radio-group :default-value="1" size="large">
                <a-radio-button :value="1">选项一</a-radio-button>
                <a-radio-button :value="2">选项二</a-radio-button>
                <a-radio-button :value="3" :disabled="true">禁用</a-radio-button>
                <a-radio-button :value="4">选项四</a-radio-button>
              </a-radio-group>
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
import { deepClone } from '@framework/vue/utils/util';

export default {
  name: 'ThemeRadio',
  mixins: [themePropMixin],
  props: {
    config: Object
  },
  title: '单选按钮',
  category: 'formBasicComp',
  themePropConfig: {
    // 基础样式
    basicStyle: {
      // 默认
      wRadioTextColor: '--w-text-color-dark',
      wRadioTextFontSize: '--w-font-size-base',
      wRadioTextFontWeight: '--w-font-weight-regular',
      wRadioBorderColor: '--w-border-color-darker',
      wRadioFontSize: '--w-font-size-lg',
      wRadioLrPadding: '--w-padding-2xs',
      // 悬停
      wRadioTextColorHover: '--w-text-color-dark',
      wRadioTextFontSizeHover: '--w-font-size-base',
      wRadioTextFontWeightHover: '--w-font-weight-regular',
      wRadioBorderColorHover: '--w-primary-color',
      // 选中
      wRadioTextColorChecked: '--w-text-color-dark',
      wRadioTextFontSizeChecked: '--w-font-size-base',
      wRadioTextFontWeightChecked: '--w-font-weight-regular',
      wRadioBorderColorChecked: '--w-primary-color',
      // 禁用
      wRadioTextColorDisabled: '--w-text-color-lighter',
      wRadioBorderColorDisabled: '--w-border-color-light',
      wRadioBackgroundDisabled: '--w-fill-color-base',
      wRadioBackgroundDisabledChecked: '--w-fill-color-light'
    },
    errorStyle: {
      wRadioErrorBorderColor: '--w-danger-color',
      wRadioErrorBorderStyle: '--w-border-style-base',
      wRadioErrorBorderWidth: '--w-border-width-base',
      wRadioErrorWordColor: '--w-danger-color',
      wRadioErrorWordFontSize: '--w-font-size-base',
      wRadioErrorWordFontWeight: '--w-font-weight-regular'
    },
    groupItemWidthStyle: {
      wRadioGroupWidthMr: '--w-margin-2xs'
    },
    groupItemMarginStyle: {
      wRadioGroupItemMr: '--w-margin-lg'
    },
    groupVerticalStyle: {
      wRadioGroupVerticalMr: '--w-margin-2xs',
      wRadioGroupVerticalMb: '--w-margin-2xs'
    },
    groupButtonSolidStyle: {
      // 默认
      wRadioButtonSolidBorderColor: '--w-border-color-light',
      wRadioButtonSolidBorderStyle: '--w-border-style-base',
      wRadioButtonSolidBorderWidth: '--w-border-width-base',
      wRadioButtonSolidBorderRadius: '--w-border-radius-base',
      wRadioButtonSolidTextSize: '--w-font-size-base',
      wRadioButtonSolidTextWeight: '--w-font-weight-regular',
      wRadioButtonSolidColor: '--w-text-color-darker',
      wRadioButtonSolidBackground: '--w-gray-color-1',
      wRadioButtonSolidLrPadding: '--w-padding-sm',
      wRadioButtonSolidHeightBase: '--w-height-sm',
      // 悬停
      wRadioButtonSolidBorderColorHover: '--w-border-color-light',
      wRadioButtonSolidBorderStyleHover: '--w-border-style-base',
      wRadioButtonSolidBorderWidthHover: '--w-border-width-base',
      wRadioButtonSolidTextSizeHover: '--w-font-size-base',
      wRadioButtonSolidTextWeightHover: '--w-font-weight-regular',
      wRadioButtonSolidTextColorHover: '--w-primary-color',
      wRadioButtonSolidBackgroundHover: '--w-gray-color-1',
      // 选中
      wRadioButtonSolidBorderColorChecked: '--w-primary-color',
      wRadioButtonSolidBorderStyleChecked: '--w-border-style-base',
      wRadioButtonSolidBorderWidthChecked: '--w-border-width-base',
      wRadioButtonSolidTextSizeChecked: '--w-font-size-base',
      wRadioButtonSolidTextWeightChecked: '--w-font-weight-regular',
      wRadioButtonSolidTextColorChecked: '--w-gray-color-1',
      wRadioButtonSolidBackgroundChecked: '--w-primary-color',
      // 点击
      wRadioButtonSolidBorderColorActive: '--w-primary-color-7',
      wRadioButtonSolidBorderStyleActive: '--w-border-style-base',
      wRadioButtonSolidBorderWidthActive: '--w-border-width-base',
      wRadioButtonSolidTextSizeActive: '--w-gray-color-1',
      wRadioButtonSolidTextWeightActive: '--w-font-weight-regular',
      wRadioButtonSolidTextColorActive: '--w-gray-color-1',
      wRadioButtonSolidBackgroundActive: '--w-primary-color-7',
      // 禁用
      wRadioButtonSolidBorderColorDisabled: '--w-border-color-light',
      wRadioButtonSolidTextColorDisabled: '--w-text-color-lighter',
      wRadioButtonSolidBackgroundDisabled: '--w-fill-color-light'
    },
    groupButtonlineStyle: {
      // 默认
      wRadioButtonBorderColor: '--w-border-color-light',
      wRadioButtonBorderStyle: '--w-border-style-base',
      wRadioButtonBorderWidth: '--w-border-width-base',
      wRadioButtonBorderRadius: '--w-border-radius-base',
      wRadioButtonTextSize: '--w-font-size-base',
      wRadioButtonTextWeight: '--w-font-weight-regular',
      wRadioButtonColor: '--w-text-color-darker',
      wRadioButtonBg: '--w-gray-color-1',
      wRadioButtonLrPadding: '--w-padding-sm',
      wRadioButtonHeightBase: '--w-height-sm',
      // 悬停
      wRadioButtonBorderColorHover: '--w-border-color-light',
      wRadioButtonBorderStyleHover: '--w-border-style-base',
      wRadioButtonBorderWidthHover: '--w-border-width-base',
      wRadioButtonTextSizeHover: '--w-font-size-base',
      wRadioButtonTextWeightHover: '--w-font-weight-regular',
      wRadioButtonTextColorHover: '--w-primary-color',
      wRadioButtonBackgroundHover: '--w-gray-color-1',
      // 选中
      wRadioButtonBorderColorChecked: '--w-primary-color',
      wRadioButtonBorderStyleChecked: '--w-border-style-base',
      wRadioButtonBorderWidthChecked: '--w-border-width-base',
      wRadioButtonTextSizeChecked: '--w-font-size-base',
      wRadioButtonTextWeightChecked: '--w-font-weight-regular',
      wRadioButtonTextColorChecked: '--w-primary-color',
      wRadioButtonBackgroundChecked: '--w-gray-color-1',
      // 点击
      wRadioButtonBorderColorActive: '--w-primary-color-7',
      wRadioButtonBorderStyleActive: '--w-border-style-base',
      wRadioButtonBorderWidthActive: '--w-border-width-base',
      wRadioButtonTextSizeActive: '--w-font-size-base',
      wRadioButtonTextWeightActive: '--w-font-weight-regular',
      wRadioButtonTextColorActive: '--w-primary-color-7',
      wRadioButtonBackgroundActive: '--w-gray-color-1',
      // 禁用
      wRadioButtonBorderColorDisabled: '--w-border-color-light',
      wRadioButtonTextColorDisabled: '--w-text-color-lighter',
      wRadioButtonBackgroundDisabled: '--w-fill-color-light'
    },
    groupButtonSmallStyle: {
      wRadioButtonSmallBorderWidth: '--w-border-width-base',
      wRadioButtonSmallTextSize: '--w-font-size-base',
      wRadioButtonSmallTextWeight: '--w-font-weight-regular',
      wRadioButtonSmallLrPadding: '--w-padding-2xs',
      wRadioButtonSmallHeightBase: '--w-height-2xs'
    },
    groupButtonDefaultStyle: {
      wRadioButtonDefaultBorderWidth: '--w-border-width-base',
      wRadioButtonDefaultTextSize: '--w-font-size-base',
      wRadioButtonDefaultTextWeight: '--w-font-weight-regular',
      wRadioButtonDefaultLrPadding: '--w-padding-sm',
      wRadioButtonDefaultHeightBase: '--w-height-sm'
    },
    groupButtonLargeStyle: {
      wRadioButtonLargeBorderWidth: '--w-border-width-base',
      wRadioButtonLargeTextSize: '--w-font-size-lg',
      wRadioButtonLargeTextWeight: '--w-font-weight-regular',
      wRadioButtonLargeLrPadding: '--w-padding-sm',
      wRadioButtonLargeHeightBase: '--w-height-md'
    }
  },
  computed: {
    vStyle() {
      let colorVars = {},
        keys = [
          'basicStyle',
          'errorStyle',
          'groupItemWidthStyle',
          'groupItemMarginStyle',
          'groupVerticalStyle',
          'groupButtonSolidStyle',
          'groupButtonlineStyle',
          'groupButtonSmallStyle',
          'groupButtonDefaultStyle',
          'groupButtonLargeStyle'
        ];
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
          console.log(colorVars['--w-radio-text-font-size'], '--', prop);
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
      inputNumber: 3,
      selectedKey: 'basicStyle',
      testForm: { testInput: undefined },
      testRules: {
        testInput: [
          {
            required: true,
            message: (
              <span class="radio-error">
                <a-icon type="warning" /> 必填
              </span>
            )
          }
        ]
      },
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
          basicStyle: 'ThemeRadioProp',
          errorStyle: 'ThemeRadioErrorProp',
          groupItemWidthStyle: 'ThemeGroupItemWidth',
          groupItemMarginStyle: 'ThemeGroupItemMargin',
          groupVerticalStyle: 'ThemeGroupVertical',
          groupButtonSolidStyle: 'ThemeRadioButtonSolidProp',
          groupButtonlineStyle: 'ThemeRadioButtonProp',
          groupButtonSmallStyle: 'ThemeRadioButtonSmallProp',
          groupButtonDefaultStyle: 'ThemeRadioButtonDefaultProp',
          groupButtonLargeStyle: 'ThemeRadioButtonLargeProp'
        };
        this.selectedKey = key;
        if (!this.config[key]) {
          this.$set(this.config, key, deepClone(this.$options.themePropConfig[key]));
        }
        this.$emit('select', this.config[key], null, keyComp[key]);
      }
    },
    undoThemePropConfig(key) {
      this.config[key] = deepClone(this.$options.themePropConfig[key]);
      this.onClickCard(key, true);
    }
  }
};
</script>
