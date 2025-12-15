<template>
  <div class="theme-style-panel">
    <a-page-header title="富文本框">
      <div slot="subTitle">平台表单富文本框</div>
      <!-- 基础样式 -->
      <a-card :class="[selectedKey == 'basicStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('basicStyle')">
        <template #title>
          基础样式
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('basicStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="1">
          <a-descriptions-item label="默认  Default">
            <QuillEditor class="full-line" placeholder="请输入内容" />
          </a-descriptions-item>
          <a-descriptions-item label="悬停  Hover">
            <QuillEditor class="full-line theme-rich-text-hover" placeholder="请输入内容" />
          </a-descriptions-item>
          <a-descriptions-item label="禁用  Disable">
            <QuillEditor class="full-line" placeholder="请输入内容" :disable="true" />
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <a-card :class="[selectedKey == 'errorStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('errorStyle')">
        <template #title>
          错误提示
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('errorStyle')" />
        </template>
        <a-form-model layout="vertical" :colon="false" :model="testForm" :rules="testRules" ref="testForm">
          <a-descriptions layout="vertical" :colon="false" :column="1">
            <a-descriptions-item label="默认  Default">
              <a-form-model-item prop="testInput" required class="full-line80">
                <QuillEditor class="full-line" placeholder="请输入内容" />
              </a-form-model-item>
            </a-descriptions-item>
            <a-descriptions-item label="悬停  Hover">
              <a-form-model-item prop="testInput" required class="full-line80">
                <QuillEditor class="full-line theme-rich-text-hover" placeholder="请输入内容" />
              </a-form-model-item>
            </a-descriptions-item>
          </a-descriptions>
        </a-form-model>
      </a-card>
    </a-page-header>
  </div>
</template>
<script type="text/babel">
import QuillEditor from '@pageAssembly/app/web/lib/quill-editor';
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import { kebabCase } from 'lodash';

export default {
  name: 'ThemeRichTextEditor',
  mixins: [themePropMixin],
  props: {
    config: Object
  },
  title: '富文本框',
  category: 'formBasicComp',
  themePropConfig: {
    // 基础样式
    basicStyle: {
      wRichBorderColor: '--w-border-color-base',
      wRichBorderStyle: '--w-border-style-base',
      wRichBorderWidth: '--w-border-width-base',
      wRichBorderRadius: '--w-border-radius-2',
      wRichPlaceholderColor: '--w-text-color-lighter',
      wRichBgColor: '--w-fill-color-base',
      wRichLrPadding: '--w-padding-xs',
      wRichDefHeight: '--w-height-3xl',
      wRichBorderColorHover: '--w-primary-color',
      wRichBorderColorDisabled: '--w-border-color-light',
      wRichBgColorDisabled: '--w-fill-color-light'
    },
    errorStyle: {
      wRichErrorBorderColor: '--w-danger-color',
      wRichErrorBorderStyle: '--w-border-style-base',
      wRichErrorBorderWidth: '--w-border-width-base',
      wRichErrorBgColor: '--w-fill-color-base',
      wRichErrorBorderColorHover: '--w-danger-color',
      wRichErrorWordColor: '--w-danger-color',
      wRichErrorWordFontSize: '--w-font-size-base',
      wRichErrorWordFontWeight: '--w-font-weight-regular'
    }
  },
  components: { QuillEditor },
  computed: {
    vStyle() {
      let colorVars = {},
        keys = ['basicStyle', 'errorStyle'];
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
            message: <span>必填</span>
          }
        ]
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
          basicStyle: 'ThemeRichTextEditorProp',
          errorStyle: 'ThemeRichTextEditorErrorProp'
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
    }
  }
};
</script>
