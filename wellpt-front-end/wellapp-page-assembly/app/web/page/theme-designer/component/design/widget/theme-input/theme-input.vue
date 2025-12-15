<template>
  <div class="theme-style-panel">
    <a-page-header title="输入框">
      <div slot="subTitle">平台表单输入框样式</div>
      <!-- 基础样式 -->
      <a-card :class="[selectedKey == 'basicStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('basicStyle')">
        <template #title>
          基础样式
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('basicStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2">
          <a-descriptions-item label="默认">
            <a-input class="full-line80" v-model="inputText" placeholder="请输入" />
          </a-descriptions-item>
          <a-descriptions-item label="悬停">
            <a-input v-model="inputText" placeholder="请输入" class="full-line80 theme-input-hover" />
          </a-descriptions-item>
          <a-descriptions-item label="获取焦点">
            <a-input v-model="inputText" placeholder="请输入" class="full-line80 theme-input-focus" />
          </a-descriptions-item>
          <a-descriptions-item label="禁用">
            <a-input v-model="inputText" placeholder="请输入" class="full-line80" :disabled="true" />
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
            <a-descriptions-item label="错误提示">
              <a-form-model-item prop="testInput" required class="full-line80">
                <a-input />
              </a-form-model-item>
            </a-descriptions-item>
            <a-descriptions-item label="错误提示悬浮">
              <a-form-model-item prop="testInput" required class="full-line80">
                <a-input class="theme-input-error-hover" />
              </a-form-model-item>
            </a-descriptions-item>
            <a-descriptions-item label="错误提示焦点">
              <a-form-model-item prop="testInput" required class="full-line80">
                <a-input class="theme-input-error-focus" />
              </a-form-model-item>
            </a-descriptions-item>
            <a-descriptions-item :label="null"></a-descriptions-item>
          </a-descriptions>
        </a-form-model>
      </a-card>
      <!-- 字符数 -->
      <a-card :class="[selectedKey == 'countNumStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('countNumStyle')">
        <template #title>
          字符数
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('countNumStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2">
          <a-descriptions-item label="常规字符计数">
            <a-input class="full-line80" :maxLength="maxLength" v-model="inputLength" placeholder="文本">
              <template slot="suffix">
                <span class="textLengthShow">{{ inputLength | textLengthFilter }}/{{ maxLength }}</span>
              </template>
            </a-input>
          </a-descriptions-item>
          <a-descriptions-item></a-descriptions-item>
        </a-descriptions>
      </a-card>
      <!-- 允许清空 -->
      <a-card :class="[selectedKey == 'clearStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('clearStyle')">
        <template #title>
          允许清空
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('clearStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2">
          <a-descriptions-item label="清空按钮样式">
            <a-input class="full-line80" v-model="inputTextClear" allowClear />
          </a-descriptions-item>
          <a-descriptions-item></a-descriptions-item>
        </a-descriptions>
      </a-card>
      <!-- 前后标签 -->
      <a-card :class="[selectedKey == 'addonStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('addonStyle')">
        <template #title>
          前后标签
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('addonStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2">
          <a-descriptions-item label="前后置标签">
            <a-input class="full-line80">
              <template slot="addonAfter">@wellsoft.com</template>
            </a-input>
          </a-descriptions-item>
          <a-descriptions-item>
            <a-input class="full-line80">
              <template slot="addonBefore">http://</template>
              <template slot="addonAfter">.com</template>
            </a-input>
          </a-descriptions-item>
          <a-descriptions-item label="下拉标签">
            <a-input class="full-line80">
              <a-select slot="addonAfter" default-value=".com" style="width: 80px" :getPopupContainer="getPopupContainer()">
                <a-select-option value=".com">.com</a-select-option>
                <a-select-option value=".cn">.cn</a-select-option>
                <a-select-option value=".net">.net</a-select-option>
              </a-select>
            </a-input>
          </a-descriptions-item>
          <a-descriptions-item>
            <a-input class="full-line80">
              <a-select slot="addonBefore" default-value="Http://" style="width: 90px" :getPopupContainer="getPopupContainer()">
                <a-select-option value="Http://">Http://</a-select-option>
                <a-select-option value="Https://">Https://</a-select-option>
              </a-select>
              <a-select slot="addonAfter" default-value=".com" style="width: 80px" :getPopupContainer="getPopupContainer()">
                <a-select-option value=".com">.com</a-select-option>
                <a-select-option value=".cn">.cn</a-select-option>
                <a-select-option value=".net">.net</a-select-option>
              </a-select>
            </a-input>
          </a-descriptions-item>
        </a-descriptions>
        <div class="ant-page-header-heading-sub-title" style="width: 200%">
          下拉框及其选项引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">下拉框</a-button>
          ，可点击进入相关组件进行样式配置
        </div>
      </a-card>
      <!-- 前后图标 -->
      <a-card
        :class="[selectedKey == 'prefixSuffixStyle' ? 'border-selected' : '']"
        :style="vStyle"
        @click="onClickCard('prefixSuffixStyle')"
      >
        <template #title>
          前后图标
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('prefixSuffixStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2">
          <a-descriptions-item label="前缀图标">
            <a-input class="full-line80" v-model="inputTextClear">
              <a-icon slot="prefix" type="user" />
            </a-input>
          </a-descriptions-item>
          <a-descriptions-item label="后缀图标">
            <a-input class="full-line80" v-model="inputTextClear">
              <a-icon slot="suffix" type="user" />
            </a-input>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <!-- 密码框 -->
      <a-card :class="[selectedKey == 'passwordStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('passwordStyle')">
        <template #title>
          脱敏切换图标
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('passwordStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2">
          <a-descriptions-item label="脱敏切换图标样式">
            <a-input-password class="full-line80" v-model="inputTextClear" />
          </a-descriptions-item>
          <a-descriptions-item></a-descriptions-item>
        </a-descriptions>
      </a-card>
    </a-page-header>
  </div>
</template>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import { kebabCase } from 'lodash';

export default {
  name: 'ThemeInput',
  mixins: [themePropMixin],
  components: {},
  props: {
    config: Object
  },
  title: '输入框',
  category: 'formBasicComp',
  themePropConfig: {
    basicStyle: {
      wInputBorderColor: '--w-border-color-light',
      wInputBorderColorHover: '--w-primary-color',
      wInputBorderColorFocus: '--w-primary-color',
      wInputBorderColorDisabled: '--w-border-color-light',
      wInputBackgroundColor: '--w-fill-color-base',
      wInputBackgroundColorDisabled: '--w-fill-color-light',
      wInputBorderWidth: '--w-border-width-base',
      wInputBorderStyle: '--w-border-style-base',
      wInputBorderRadius: '--w-border-radius-2',
      wInputLrPadding: '--w-padding-2xs',
      wInputHeight: '--w-height-sm',
      wInputFontSize: '--w-font-size-base',
      wInputBoxShadow: undefined,
      wInputBoxShadowFocus: undefined,
      wInputBoxShadowHover: undefined,
      wInputColor: '--w-text-color-dark',
      wInputPlaceholderColor: '--w-text-color-lighter',
      wInputFontWeight: '--w-font-weight-regular',
      wInputColorDisabled: '--w-text-color-lighter'
    },
    errorStyle: {
      wInputErrorBorderColor: '--w-danger-color',
      wInputErrorBorderColorHover: '--w-danger-color',
      wInputErrorBorderColorFocus: '--w-danger-color',
      wInputErrorBorderWidth: '--w-border-width-base',
      wInputErrorBorderStyle: '--w-border-style-base',
      wInputErrorWordColor: '--w-danger-color',
      wInputErrorWordFontSize: '--w-font-size-base',
      wInputErrorWordFontWeight: '--w-font-weight-regular'
    },
    countNumStyle: {
      wInputCountColor: '--w-text-color-light',
      wInputCountSize: '--w-font-size-sm',
      wInputCountWeight: '--w-font-weight-regular',
      wInputCountRightPadding: undefined
    },
    clearStyle: {
      wInputClearFontSize: '--w-font-size-base',
      wInputClearFontColor: '--w-text-color-lighter',
      wInputClearRightPadding: undefined,
      wInputClearSizeHover: '--w-font-size-base',
      wInputClearColorHover: 'var(--w-text-color-light)',
      wInputClearSizeActive: '--w-font-size-base',
      wInputClearColorActive: 'var(--w-text-color-light)'
    },
    addonStyle: {
      wInputAddonFontColor: '--w-text-color-dark',
      wInputAddonFontSize: '--w-font-size-base',
      wInputAddonFontWeight: '--w-font-weight-regular',
      wInputAddonArrowColor: '--w-text-color-light',
      wInputAddonArrowSize: '--w-font-size-sm',
      wInputAddonBgColor: '--w-fill-color-light',
      wInputAddonLeftPadding: '--w-padding-xs',
      wInputAddonRightPadding: '--w-padding-xs',

      wInputAddonFontColorHover: '--w-primary-color',
      wInputAddonArrowColorHover: '--w-text-color-light',
      wInputAddonFontColorFocus: '--w-primary-color',
      wInputAddonArrowColorFocus: '--w-text-color-light'
    },
    prefixSuffixStyle: {
      wInputAffixFontSize: '--w-font-size-base',
      wInputAffixFontColor: '--w-text-color-base'
    },
    passwordStyle: {
      wInputPasswordFontSize: '--w-font-size-base',
      wInputPasswordFontColor: '--w-text-color-light',
      wInputPasswordColorHover: '--w-text-color-base'
    }
  },
  filters: {
    //字符长度
    textLengthFilter(text) {
      return text ? text.length : 0;
    }
  },
  computed: {
    vStyle() {
      if (!this.selectStyle) {
        this.getSelectStyle();
      }
      let colorVars = {},
        keys = ['basicStyle', 'errorStyle', 'countNumStyle', 'clearStyle', 'addonStyle', 'prefixSuffixStyle', 'passwordStyle'];
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
      console.log('colorVars', colorVars);
      // console.log('输入框样式变量: ', colorVars,this.basicStyleCssVars);
      let vars1 = { ...vars, ...this.selectStyle };
      return vars1;
    }
  },
  data() {
    return {
      inputText: '文字',
      inputLength: '文字',
      maxLength: 64,
      inputTextClear: '文本内容',
      testForm: { testInput: undefined },
      selectedKey: 'basicStyle',
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
      quoteKey: 'ThemeSelect',
      selectStyle: undefined
    };
  },
  beforeCreate() {},
  created() {
    this.getSelectStyle();
  },
  beforeMount() {},
  mounted() {
    this.$emit('select', this.config.basicStyle);
    this.$refs.testForm.validate();
  },
  methods: {
    defaultConfig() {
      return {};
    },
    onClickCard(key, refresh) {
      if (this.selectedKey != key || refresh) {
        let keyComp = {
          basicStyle: 'ThemeInputProp',
          errorStyle: 'ThemeInputErrorProp',
          countNumStyle: 'ThemeInputCountProp',
          clearStyle: 'ThemeInputClearProp',
          addonStyle: 'ThemeInputAddonProp',
          prefixSuffixStyle: 'ThemeInputPrefixSuffix',
          passwordStyle: 'ThemeInputPasswordProp'
        };
        this.selectedKey = key;
        this.$emit('select', this.config[key], null, keyComp[key]);
      }
    },
    undoThemePropConfig(key) {
      this.config[key] = JSON.parse(JSON.stringify(this.$options.themePropConfig[key]));
      this.onClickCard(key, true);
    },
    handleQuote() {
      this.$emit('lightQuote', {
        configKey: `componentConfig.${this.quoteKey}`,
        selectedKeys: this.quoteKey
      });
    },
    getSelectStyle() {
      this.selectStyle = this.themeStyle && this.themeStyle[this.quoteKey];
      if (!this.selectStyle) {
        this.selectStyle = this.getComponentConfig(this.quoteKey, [
          'basicStyle',
          'errorStyle',
          'dropdownStyle',
          'dropdownMenuStyle',
          'clearStyle'
        ]);
      }
    },
    getPopupContainer() {
      return triggerNode => triggerNode.closest('.ant-card');
    }
  }
};
</script>
