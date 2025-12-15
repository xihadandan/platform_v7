<template>
  <div class="theme-style-panel">
    <a-page-header title="多行文本框">
      <div slot="subTitle">平台表单多行文本框样式</div>
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
              <a-textarea class="full-line80" v-model="inputText" placeholder="请输入" />
            </a-descriptions-item>
            <a-descriptions-item label="悬停">
              <a-textarea v-model="inputText" placeholder="请输入" class="full-line80 theme-input-textarea-hover" />
            </a-descriptions-item>
            <a-descriptions-item label="获取焦点">
              <a-textarea v-model="inputText" placeholder="请输入" class="full-line80 theme-input-textarea-focus" />
            </a-descriptions-item>
            <a-descriptions-item label="禁用">
              <a-textarea class="full-line80" v-model="inputText" placeholder="请输入" :disabled="true" />
            </a-descriptions-item>
          </a-descriptions>
        </div>
        <div class="ant-page-header-heading-sub-title">
          基础样式引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">输入框</a-button>
          （文本框高度除外），可点击进入相关组件进行样式配置
        </div>
      </a-card>
      <!-- 错误提示 -->
      <a-card title="错误提示">
        <div :class="[selectedKey == 'errorStyle' ? 'border-selected theme-card-item' : 'theme-card-item']" :style="vStyle">
          <a-form-model layout="vertical" :colon="false" :model="testForm" :rules="testRules" ref="testForm">
            <a-descriptions layout="vertical" :colon="false" :column="2">
              <a-descriptions-item label="错误提示">
                <a-form-model-item prop="testInput" required class="full-line80">
                  <a-textarea />
                </a-form-model-item>
              </a-descriptions-item>
              <a-descriptions-item label="错误提示悬浮">
                <a-form-model-item prop="testInput" required class="full-line80">
                  <a-textarea class="theme-input-textarea-error-hover" />
                </a-form-model-item>
              </a-descriptions-item>
              <a-descriptions-item label="错误提示焦点">
                <a-form-model-item prop="testInput" required class="full-line80">
                  <a-textarea class="theme-input-textarea-error-focus" />
                </a-form-model-item>
              </a-descriptions-item>
              <a-descriptions-item :label="null"></a-descriptions-item>
            </a-descriptions>
          </a-form-model>
        </div>
        <div class="ant-page-header-heading-sub-title">
          基础样式引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">输入框</a-button>
          （文本框高度除外），可点击进入相关组件进行样式配置
        </div>
      </a-card>
      <!-- 字符数 -->
      <a-card title="字符数">
        <div :class="[selectedKey == 'countNumStyle' ? 'border-selected theme-card-item' : 'theme-card-item']" :style="vStyle">
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item label="常规字符计数">
              <span class="input-textarea full-line80 resizeOver">
                <a-textarea :maxLength="maxLength" v-model="inputLength" placeholder="请输入" auto-size />
                <span class="textLengthShow">{{ inputLength | textLengthFilter }}/{{ maxLength }}</span>
              </span>
            </a-descriptions-item>
            <a-descriptions-item></a-descriptions-item>
          </a-descriptions>
        </div>
        <div class="ant-page-header-heading-sub-title">
          基础样式引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">输入框</a-button>
          （文本框高度除外），可点击进入相关组件进行样式配置
        </div>
      </a-card>
      <!-- 允许清空 -->
      <a-card title="允许清空">
        <div :class="[selectedKey == 'countNumStyle' ? 'border-selected theme-card-item' : 'theme-card-item']" :style="vStyle">
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item label="清空按钮样式">
              <a-textarea class="full-line80 theme-input-textarea-clear" v-model="inputText" placeholder="请输入" allowClear />
            </a-descriptions-item>
            <a-descriptions-item></a-descriptions-item>
          </a-descriptions>
        </div>
        <div class="ant-page-header-heading-sub-title">
          基础样式引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">输入框</a-button>
          （文本框高度除外），可点击进入相关组件进行样式配置
        </div>
      </a-card>
    </a-page-header>
  </div>
</template>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import { kebabCase } from 'lodash';

export default {
  name: 'ThemeTextarea',
  mixins: [themePropMixin],
  props: {
    config: Object,
    themeStyle: {
      type: Object,
      default: () => {}
    }
  },
  title: '多行文本框',
  category: 'formBasicComp',
  themePropConfig: {
    basicStyle: {
      wInputTextareaHeight: undefined,
      wInputTextareaMinHeight: '--w-height-3xl'
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
      if (!this.quoteStyle) {
        this.getQuoteStyle();
      }
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
      let vars = { ...this.quoteStyle, ...colorVars, ...this.basicStyleCssVars };
      return vars;
    }
  },
  data() {
    return {
      inputText: '文字',
      inputLength: '文字',
      maxLength: 200,
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
      quoteStyle: undefined
    };
  },
  mounted() {
    this.getQuoteStyle();
    this.$nextTick(() => {
      this.onClickCard('basicStyle', true);
    });
    this.$refs.testForm.validate();
  },
  methods: {
    onClickCard(key, refresh) {
      if (this.selectedKey != key || refresh) {
        this.selectedKey = key;
        if (key == 'basicStyle') {
          if (!this.config[key]) {
            this.$set(this.config, key, JSON.parse(JSON.stringify(this.$options.themePropConfig[key])));
          }
          this.$emit('select', this.config[key], null, 'ThemeInputTextareaProp');
        }
      }
    },
    handleQuote() {
      this.$emit('lightQuote', {
        configKey: `componentConfig.${this.quoteKey}`,
        selectedKeys: this.quoteKey
      });
    },
    getQuoteStyle() {
      this.quoteStyle = this.themeStyle[this.quoteKey];
      if (!this.quoteStyle) {
        this.quoteStyle = this.getComponentConfig(this.quoteKey, ['basicStyle', 'errorStyle', 'countNumStyle', 'clearStyle']);
      }
    },
    undoThemePropConfig(key) {
      this.config[key] = JSON.parse(JSON.stringify(this.$options.themePropConfig[key]));
      this.onClickCard(key, true);
    }
  }
};
</script>
