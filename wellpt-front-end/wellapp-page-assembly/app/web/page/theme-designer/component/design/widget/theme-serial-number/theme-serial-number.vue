<template>
  <div class="theme-style-panel">
    <a-page-header title="流水号">
      <div slot="subTitle">平台表单流水号样式</div>
      <a-card title="基础样式">
        <div
          :class="[selectedKey == 'basicStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          :style="vStyle"
          @click="onClickCard('basicStyle')"
        >
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
        </div>
        <div class="ant-page-header-heading-sub-title">
          基础样式引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">输入框</a-button>
          ，可点击进入相关组件进行样式配置
        </div>
      </a-card>

      <a-card title="错误提示">
        <div
          :class="[selectedKey == 'errorStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          :style="vStyle"
          @click="onClickCard('errorStyle')"
        >
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
        </div>
        <div class="ant-page-header-heading-sub-title">
          基础样式引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">输入框</a-button>
          ，可点击进入相关组件进行样式配置
        </div>
      </a-card>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';
export default {
  name: 'ThemeSerialNumber',
  mixins: [themePropMixin],
  components: {},
  props: {
    config: Object,
    themeStyle: {
      type: Object,
      default: () => {}
    }
  },
  title: '流水号',
  category: 'formBasicComp',
  themePropConfig: {},
  computed: {
    vStyle() {
      return this.themeStyle[this.quoteKey];
    }
  },
  data() {
    return {
      inputText: '文字',
      inputTextClear: '文本内容',
      testForm: { testInput: undefined },
      selectedKey: '',
      quoteKey: 'ThemeInput',
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
      }
    };
  },
  mounted() {
    this.$refs.testForm.validate();
  },
  methods: {
    onClickCard(key) {
      // if (this.selectedKey != key) {
      //   this.selectedKey = key;
      // }
    },
    handleQuote() {
      this.$emit('lightQuote', {
        configKey: `componentConfig.${this.quoteKey}`,
        selectedKeys: this.quoteKey
      });
    }
  }
};
</script>
