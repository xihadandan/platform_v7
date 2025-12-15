<template>
  <div class="theme-style-panel widget-form-tag">
    <a-page-header title="标签组">
      <div slot="subTitle">平台表单标签组</div>
      <!-- 基础样式 -->
      <a-card :class="[selectedKey == 'basicStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('basicStyle')">
        <template #title>
          基础样式
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('basicStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2">
          <a-descriptions-item label="默认">
            <a-tag class="tag-no-border">默认无边框标签</a-tag>
            <a-tag class="tag-no-border">
              <Icon type="pticon iconfont icon-ptkj-qiehuanshitu" />
              无边框/含图标标签
            </a-tag>
            <a-tag>标签</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="彩色">
            <a-tag class="tag-no-border" color="pink">默认无边框标签</a-tag>
            <a-tag color="red">
              <Icon type="step-backward" />
              无边框/含图标标签
            </a-tag>
            <a-tag color="orange">标签</a-tag>
            <a-tag color="green">标签</a-tag>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <!-- 编辑状态 -->
      <a-card :class="[selectedKey == 'editorStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('editorStyle')">
        <template #title>
          编辑状态
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('editorStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2">
          <a-descriptions-item label="默认">
            <a-tag closable class="tag-no-border">无边框标签</a-tag>
            <a-tag closable>标签</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="彩色">
            <a-tag color="#488cee" closable>标签</a-tag>
            <a-tag color="pink" closable>标签</a-tag>
            <a-tag color="red" closable>标签</a-tag>
            <a-tag color="orange" closable>标签</a-tag>
            <a-tag color="green" closable>标签</a-tag>
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <a-card :class="[selectedKey == 'errorStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('errorStyle')">
        <template #title>
          错误提示
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('errorStyle')" />
        </template>
        <a-form-model class="widget-form-tag" layout="vertical" :colon="false" :model="testForm" :rules="testRules" ref="testForm">
          <a-descriptions layout="vertical" :colon="false" :column="1">
            <a-descriptions-item label="默认">
              <a-form-model-item prop="testInput" required class="full-line80">
                <a-tag closable>标签</a-tag>
                <a-tag closable>标签</a-tag>
                <a-tag closable>标签</a-tag>
                <a-tag closable>标签</a-tag>
              </a-form-model-item>
            </a-descriptions-item>
          </a-descriptions>
        </a-form-model>
      </a-card>
    </a-page-header>
  </div>
</template>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import { kebabCase } from 'lodash';

export default {
  name: 'ThemeTag',
  mixins: [themePropMixin],
  props: {
    config: Object
  },
  title: '标签组',
  category: 'formBasicComp',
  themePropConfig: {
    // 基础样式
    basicStyle: {
      wTagBorderColor: '--w-border-color-dark',
      wTagBorderWidth: '--w-border-width-base',
      wTagBorderStyle: '--w-border-style-base',
      wTagBorderRadius: '--w-border-radius-base',
      wTagTextColor: '--w-text-color-dark',
      wTagTextSize: '--w-font-size-sm',
      wTagTextWeight: '--w-font-weight-regular',
      wTagBackground: '--w-fill-color-light',
      wTagLrPadding: '--w-padding-2xs',
      wTagHeight: '--w-height-2xs'
    },
    editorStyle: {
      wTagEditCloseSize: '--w-font-size-sm',
      wTagEditCloseColor: '--w-text-color-light',
      wTagEditMarginRight: '--w-margin-2xs',
      wTagCloseColorHover: '--w-text-color-light',
      wTagCloseColorActive: '--w-text-color-light'
    },
    errorStyle: {
      wTagErrorWordColor: '--w-danger-color',
      wTagErrorWordFontSize: '--w-font-size-base',
      wTagErrorWordFontWeight: '--w-font-weight-regular'
    }
  },
  computed: {
    vStyle() {
      let colorVars = {},
        keys = ['basicStyle', 'editorStyle', 'errorStyle'];
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
          basicStyle: 'ThemeTagProp',
          editorStyle: 'ThemeTagEditor',
          errorStyle: 'ThemeTagErrorProp'
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
