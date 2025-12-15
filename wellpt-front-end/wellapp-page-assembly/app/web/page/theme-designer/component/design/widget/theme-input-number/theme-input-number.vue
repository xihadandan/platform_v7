<template>
  <div class="theme-style-panel">
    <a-page-header title="数字输入框">
      <div slot="subTitle">平台表单数字输入框</div>
      <a-card>
        <template #title>
          基础样式
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('basicStyle')" />
        </template>
        <div
          :class="[selectedKey == 'basicStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          :style="vStyle"
          @click="onClickCard('basicStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item label="默认">
              <a-input-number placeholder="请输入" :class="base_default" @focus="onFocus('base_default')" @blur="onBlur('base_default')" />
            </a-descriptions-item>
            <a-descriptions-item label="悬停">
              <a-input-number
                placeholder="请输入"
                :class="'theme-input-number-hover ' + base_hover"
                @focus="onFocus('base_hover')"
                @blur="onBlur('base_hover')"
              />
            </a-descriptions-item>
            <a-descriptions-item label="获取焦点">
              <a-input-number placeholder="请输入" class="theme-input-number-focus" />
            </a-descriptions-item>
            <a-descriptions-item label="禁用">
              <a-input-number v-model="inputNumber" placeholder="请输入" :disabled="true" />
            </a-descriptions-item>
          </a-descriptions>
        </div>
      </a-card>

      <a-card>
        <template #title>
          错误提示
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('errorStyle')" />
        </template>
        <div
          :class="[selectedKey == 'errorStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          :style="vStyle"
          @click="onClickCard('errorStyle')"
        >
          <a-form-model layout="vertical" :colon="false" :model="testForm" :rules="testRules" ref="testForm">
            <a-descriptions layout="vertical" :colon="false" :column="2">
              <a-descriptions-item label="错误提示">
                <a-form-model-item prop="testInput" required class="full-line80">
                  <a-input-number :class="err_default" @focus="onFocus('err_default')" @blur="onBlur('err_default')" />
                </a-form-model-item>
              </a-descriptions-item>
              <a-descriptions-item label="错误提示悬浮">
                <a-form-model-item prop="testInput" required class="full-line80">
                  <a-input-number
                    :class="'theme-input-number-hover ' + err_hover"
                    @focus="onFocus('err_hover')"
                    @blur="onBlur('err_hover')"
                  />
                </a-form-model-item>
              </a-descriptions-item>
              <a-descriptions-item label="错误提示焦点">
                <a-form-model-item prop="testInput" required class="full-line80">
                  <a-input-number class="theme-input-number-focus" />
                </a-form-model-item>
              </a-descriptions-item>
              <a-descriptions-item :label="null"></a-descriptions-item>
            </a-descriptions>
          </a-form-model>
        </div>
      </a-card>

      <!-- 上下加减按钮 -->
      <a-card :class="[selectedKey == 'upDownStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('upDownStyle')">
        <template #title>
          加减按钮
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('upDownStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2" class="widget-form-input-number">
          <a-descriptions-item label="上下默认  Default">
            <a-input-group compact :class="'theme-input-number-handler full-line80 ' + tb_default">
              <a-input-number
                v-model="inputNumber"
                @focus="onFocus('tb_default')"
                @blur="onBlur('tb_default')"
                class="full-line80"
              ></a-input-number>
            </a-input-group>
          </a-descriptions-item>
          <a-descriptions-item label="上下悬停  Hover">
            <a-input-group compact :class="'theme-input-number-handler theme-input-number-hover full-line80 ' + tb_hover">
              <a-input-number
                v-model="inputNumber"
                @focus="onFocus('tb_hover')"
                @blur="onBlur('tb_hover')"
                class="full-line80"
              ></a-input-number>
            </a-input-group>
          </a-descriptions-item>
          <a-descriptions-item label="上下点击  Click">
            <a-input-group compact :class="'theme-input-number-handler full-line80  theme-input-number-focus' + tb_focus">
              <a-input-number
                v-model="inputNumber"
                @focus="onFocus('tb_focus')"
                @blur="onBlur('tb_focus')"
                class="full-line80"
              ></a-input-number>
            </a-input-group>
          </a-descriptions-item>
          <a-descriptions-item label="上下禁用  Disable">
            <a-input-group compact :class="'theme-input-number-handler full-line80' + tb_disable">
              <a-input-number
                v-model="inputNumber"
                @focus="onFocus('tb_disable')"
                @blur="onBlur('tb_disable')"
                :disabled="true"
                class="full-line80"
              ></a-input-number>
            </a-input-group>
          </a-descriptions-item>
          <a-descriptions-item label="左右默认  Default">
            <a-input-group compact :class="lr_default">
              <a-button @click="numberStepHandle('down', 'default')" icon="minus"></a-button>
              <a-input-number
                ref="inputNumberRef_default"
                v-model="inputNumber"
                @focus="onFocus('lr_default')"
                @blur="onBlur('lr_default')"
                class="hiddenStep"
              ></a-input-number>
              <a-button @click="numberStepHandle('up', 'default')" icon="plus"></a-button>
            </a-input-group>
          </a-descriptions-item>
          <a-descriptions-item label="左右悬停  Hover">
            <a-input-group compact :class="'theme-input-number-handler theme-input-number-hover ' + lr_hover">
              <a-button @click="numberStepHandle('down', 'hover')" icon="minus"></a-button>
              <a-input-number
                ref="inputNumberRef_hover"
                v-model="inputNumber"
                @focus="onFocus('lr_hover')"
                @blur="onBlur('lr_hover')"
                class="hiddenStep"
              ></a-input-number>
              <a-button @click="numberStepHandle('up', 'hover')" icon="plus"></a-button>
            </a-input-group>
          </a-descriptions-item>
          <a-descriptions-item label="左右点击  Click">
            <a-input-group compact :class="'theme-input-number-handler theme-input-number-focus ' + lr_focus">
              <a-button @click="numberStepHandle('down', 'focus')" icon="minus"></a-button>
              <a-input-number
                ref="inputNumberRef_focus"
                v-model="inputNumber"
                @focus="onFocus('lr_focus')"
                @blur="onBlur('lr_focus')"
                class="hiddenStep"
              ></a-input-number>
              <a-button @click="numberStepHandle('up', 'focus')" icon="plus"></a-button>
            </a-input-group>
          </a-descriptions-item>
          <a-descriptions-item label="左右禁用  Disable">
            <a-input-group compact :class="lr_disable">
              <a-button @click="numberStepHandle('down', 'disable')" icon="minus" :disabled="true"></a-button>
              <a-input-number
                ref="inputNumberRef_disable"
                v-model="inputNumber"
                :disabled="true"
                @focus="onFocus('lr_disable')"
                @blur="onBlur('lr_disable')"
                class="hiddenStep"
              ></a-input-number>
              <a-button @click="numberStepHandle('up', 'disable')" icon="plus" :disabled="true"></a-button>
            </a-input-group>
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
  name: 'ThemeInputNumber',
  mixins: [themePropMixin],
  props: {
    config: Object,
    themeStyle: {
      type: Object,
      default: () => {}
    }
  },
  title: '数字输入框',
  category: 'formBasicComp',
  themePropConfig: {
    basicStyle: {
      wInputNumberBorderColor: '--w-border-color-light',
      wInputNumberBorderColorHover: '--w-primary-color',
      wInputNumberBorderColorFocus: '--w-primary-color',
      wInputNumberBorderColorDisabled: '--w-border-color-light',
      wInputNumberBackgroundColor: '--w-fill-color-base',
      wInputNumberBackgroundColorDisabled: '--w-fill-color-light',
      wInputNumberBorderWidth: '--w-border-width-base',
      wInputNumberBorderStyle: '--w-border-style-base',
      wInputNumberBorderRadius: '--w-border-radius-2',
      wInputNumberLrPadding: '--w-padding-2xs',
      wInputNumberHeight: '--w-height-sm',
      wInputNumberFontSize: '--w-font-size-base',
      wInputNumberBoxShadowColor: '--w-primary-color',
      wInputNumberBoxShadow: undefined,
      wInputNumberBoxShadowFocus: undefined,
      wInputNumberBoxShadowHover: undefined,
      wInputNumberColor: '--w-text-color-dark',
      wInputNumberPlaceholderColor: '--w-text-color-lighter',
      wInputNumberFontWeight: '--w-font-weight-regular',
      wInputNumberColorDisabled: '--w-text-color-lighter'
    },
    errorStyle: {
      wInputNumberErrorBorderColor: '--w-danger-color',
      wInputNumberErrorBorderColorHover: '--w-danger-color',
      wInputNumberErrorBorderColorFocus: '--w-danger-color',
      wInputNumberErrorBorderWidth: '--w-border-width-base',
      wInputNumberErrorBorderStyle: '--w-border-style-base',
      wInputNumberErrorWordColor: '--w-danger-color',
      wInputNumberErrorWordFontSize: '--w-font-size-base',
      wInputNumberErrorWordFontWeight: '--w-font-weight-regular'
    },
    // 上下加减按钮
    upDownStyle: {
      // 默认
      wInputNumberHandlerBorderColor: '--w-border-color-light',
      wInputNumberHandlerBorderStyle: '--w-border-style-base',
      wInputNumberHandlerBorderWidth: '--w-border-width-base',
      wInputNumberHandlerBgColor: '--w-fill-color-base',
      wInputNumberHandlerFontColor: '--w-text-color-dark',
      wInputNumberHandlerFontSize: '--w-font-size-sm',
      wInputNumberHandlerLrPadding: '--w-padding-3xs',
      // 悬停
      wInputNumberHandlerBgColorHover: '--w-fill-color-base',
      wInputNumberHandlerFontColorHover: '--w-primary-color',
      // 点击
      wInputNumberHandlerBgColorActive: '--w-fill-color-light',
      wInputNumberHandlerFontColorActive: '--w-primary-color',
      // 禁用
      wInputNumberHandlerBorderColorDisabled: '--w-border-color-light',
      wInputNumberHandlerBorderStyleDisabled: '--w-border-style-base',
      wInputNumberHandlerBorderWidthDisabled: '--w-border-width-base',
      wInputNumberHandlerBgColorDisabled: '--w-fill-color-light',
      wInputNumberHandlerFontColorDisabled: '--w-text-color-lighter'
    }
  },
  computed: {
    vStyle() {
      let colorVars = {},
        keys = ['basicStyle', 'errorStyle', 'upDownStyle'];
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
      const themeStyle = {};
      themeStyle[this.$options.name] = vars;
      this.$emit('setThemeStyle', themeStyle);
      return vars;
    }
  },
  data() {
    return {
      inputNumber: 3,
      selectedKey: 'basicStyle',
      quoteKey: 'ThemeInput',
      testForm: { testInput: undefined },
      testRules: {
        testInput: [
          {
            required: true,
            message: (
              <span class="input-error">
                <a-icon type="warning" /> 必填
              </span>
            )
          }
        ]
      },
      base_default: '',
      base_hover: '',
      err_default: '',
      err_hover: '',
      tb_default: '',
      tb_hover: '',
      tb_focus: '',
      tb_disable: '',
      lr_default: '',
      lr_hover: '',
      lr_focus: '',
      lr_disable: ''
    };
  },
  created() {},
  mounted() {
    this.$nextTick(() => {
      this.onClickCard('basicStyle', true);
    });
    this.$refs.testForm.validate();
  },
  methods: {
    onClickCard(key, refresh) {
      if (this.selectedKey != key || refresh) {
        let keyComp = {
          basicStyle: 'ThemeInputNumberProp',
          errorStyle: 'ThemeInputNumberErrorProp',
          upDownStyle: 'ThemeInputNumberUpdown'
        };
        this.selectedKey = key;
        if (!this.config[key]) {
          this.$set(this.config, key, JSON.parse(JSON.stringify(this.$options.themePropConfig[key])));
        }
        this.$emit('select', this.config[key], null, keyComp[key]);
      }
    },
    undoThemePropConfig(key) {
      this.config[key] = JSON.parse(JSON.stringify(this.$options.themePropConfig[key]));
      this.onClickCard(key, true);
    },
    //数字输入框-左右-步长处理
    numberStepHandle(type, code) {
      var $inputNumber = this.$refs['inputNumberRef_' + code];
      $inputNumber.$refs.inputNumberRef.stepFn(type);
      $inputNumber.$refs.inputNumberRef.stop();
    },
    onFocus(code) {
      this[code] = 'theme-input-number-focus';
    },
    onBlur(code) {
      this[code] = '';
    }
  }
};
</script>
