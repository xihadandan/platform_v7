<template>
  <div class="theme-style-panel">
    <a-page-header title="日期时间范围">
      <div slot="subTitle">平台表单日期时间范围</div>
      <!-- 日期范围 -->
      <a-card title="日期范围">
        <div
          :class="[selectedKey == 'dateRangeStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          :style="vStyle"
          @click="onClickCard('dateRangeStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item label="默认  Default">
              <a-range-picker
                :open="true"
                class="full-line80"
                style="width: 350px; min-height: 344px"
                :getCalendarContainer="triggerNode => triggerNode.parentNode"
              />
            </a-descriptions-item>
            <a-descriptions-item></a-descriptions-item>
          </a-descriptions>
        </div>
        <div class="ant-page-header-heading-sub-title">
          基础样式引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">日期时间</a-button>
          ，可点击进入相关组件进行样式配置
        </div>
      </a-card>
      <!-- 时间范围 -->
      <a-card title="时间范围">
        <div
          :class="[selectedKey == 'timeRangeStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          :style="vStyle"
          @click="onClickCard('timeRangeStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item>
              <a-range-picker
                :open="true"
                class="full-line80"
                style="width: 350px; min-height: 344px"
                :getCalendarContainer="triggerNode => triggerNode.parentNode"
              />
            </a-descriptions-item>
            <a-descriptions-item></a-descriptions-item>
          </a-descriptions>
        </div>
        <div class="ant-page-header-heading-sub-title">
          基础样式引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">日期时间</a-button>
          ，可点击进入相关组件进行样式配置
        </div>
      </a-card>
      <!-- 日期时间范围 -->
      <a-card title="日期时间范围">
        <div
          :class="[selectedKey == 'dateTimeRangeStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          :style="vStyle"
          @click="onClickCard('dateTimeRangeStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item>
              <a-range-picker
                class="full-line80"
                style="width: 350px; min-height: 344px"
                :open="true"
                :showTime="{
                  format: 'HH:mm:ss'
                }"
                :getCalendarContainer="triggerNode => triggerNode.parentNode"
              />
            </a-descriptions-item>
            <a-descriptions-item></a-descriptions-item>
          </a-descriptions>
        </div>
        <div class="ant-page-header-heading-sub-title">
          基础样式引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">日期时间</a-button>
          ，可点击进入相关组件进行样式配置
        </div>
      </a-card>
    </a-page-header>
  </div>
</template>
<script type="text/babel">
import moment from 'moment';
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import { kebabCase } from 'lodash';

export default {
  name: 'ThemeRangePicker',
  mixins: [themePropMixin],
  props: {
    config: Object,
    themeStyle: {
      type: Object,
      default: () => {}
    }
  },
  title: '日期时间范围',
  category: 'formBasicComp',
  themePropConfig: {},
  computed: {
    vStyle() {
      if (!this.quoteStyle) {
        this.getQuoteStyle();
      }
      const vStyle = { ...this.quoteStyle };
      return vStyle;
    }
  },
  data() {
    return {
      selectedKey: '',
      quoteKey: 'ThemeDatePicker',
      quoteStyle: undefined
    };
  },
  mounted() {
    this.getQuoteStyle();
  },
  methods: {
    moment,
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
    },
    getQuoteStyle() {
      this.quoteStyle = this.themeStyle[this.quoteKey];
      if (!this.quoteStyle) {
        this.quoteStyle = this.getComponentConfig(this.quoteKey, ['basicStyle', 'errorStyle', 'panelStyle', 'selectedStyle', 'clearStyle']);
      }
    }
  }
};
</script>
