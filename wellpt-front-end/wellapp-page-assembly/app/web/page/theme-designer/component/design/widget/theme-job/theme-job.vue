<template>
  <div class="theme-style-panel">
    <a-page-header title="职位">
      <div slot="subTitle">平台表单职位</div>
      <!-- 基础样式 -->
      <a-card title="基础样式">
        <div
          :class="[selectedKey == 'basicStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('basicStyle')"
          :style="vStyle"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item label="默认  Default">
              <a-select
                :options="selectOptions"
                class="full-line80"
                style="width: 100%"
                :getPopupContainer="getPopupContainer()"
                v-model="selectVal"
                placeholder="请选择"
              />
            </a-descriptions-item>
            <a-descriptions-item label="悬停  Hover">
              <a-select
                :options="selectOptions"
                class="full-line80 theme-ant-select-hover"
                style="width: 100%"
                :getPopupContainer="getPopupContainer()"
                v-model="selectVal"
              />
            </a-descriptions-item>
            <a-descriptions-item label="焦点  Focus">
              <a-select
                :options="selectOptions"
                class="full-line80 ant-select-focused"
                style="width: 100%"
                :getPopupContainer="getPopupContainer()"
                v-model="selectVal"
              />
            </a-descriptions-item>
            <a-descriptions-item label="禁用  Disable">
              <a-select
                disabled
                :options="selectOptions"
                class="full-line80"
                style="width: 100%"
                :getPopupContainer="getPopupContainer()"
                v-model="selectVal"
                placeholder="请选择"
              />
            </a-descriptions-item>
          </a-descriptions>
        </div>
        <div class="ant-page-header-heading-sub-title">
          基础样式引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">下拉框</a-button>
          ，可点击进入相关组件进行样式配置
        </div>
      </a-card>
      <!-- 错误提示 -->
      <a-card title="错误提示">
        <div
          :class="[selectedKey == 'errorStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('errorStyle')"
          :style="vStyle"
        >
          <a-form-model layout="vertical" :colon="false" :model="testForm" :rules="testRules" ref="testForm">
            <a-descriptions layout="vertical" :colon="false" :column="2">
              <a-descriptions-item label="错误提示 Error">
                <a-form-model-item prop="testSelect" required class="full-line80">
                  <a-select
                    :options="selectOptions"
                    class="full-line80"
                    style="width: 100%"
                    :getPopupContainer="getPopupContainer()"
                    v-model="selectVal"
                    placeholder="请选择"
                  />
                </a-form-model-item>
              </a-descriptions-item>
              <a-descriptions-item label="悬停  Hover">
                <a-form-model-item prop="testSelect" required class="full-line80 theme-ant-select-error-hover">
                  <a-select
                    :options="selectOptions"
                    style="width: 100%"
                    :getPopupContainer="getPopupContainer()"
                    v-model="selectVal"
                    placeholder="请选择"
                  />
                </a-form-model-item>
              </a-descriptions-item>
              <a-descriptions-item label="获取焦点  Focus">
                <a-form-model-item prop="testSelect" required class="full-line80">
                  <a-select
                    :options="selectOptions"
                    class="ant-select-focused"
                    style="width: 100%"
                    :getPopupContainer="getPopupContainer()"
                    v-model="selectVal"
                    placeholder="请选择"
                  />
                </a-form-model-item>
              </a-descriptions-item>
              <a-descriptions-item :label="null"></a-descriptions-item>
            </a-descriptions>
          </a-form-model>
        </div>
        <div class="ant-page-header-heading-sub-title">
          错误提示引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">下拉框</a-button>
          ，可点击进入相关组件进行样式配置
        </div>
      </a-card>
      <!-- 下拉选项 -->
      <a-card title="下拉选项" class="theme-card-select-dropdown">
        <div
          :class="[selectedKey == 'dropdownStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('dropdownStyle')"
          :style="vStyle"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item>
              <template #label>下拉面板</template>
              <a-select
                class="full-line80"
                style="width: 100%"
                :open="true"
                :getPopupContainer="triggerNode => triggerNode.parentNode"
                :options="selectOptions"
                v-model="selectVal"
              />
            </a-descriptions-item>
          </a-descriptions>
        </div>
        <div
          :class="[selectedKey == 'dropdownMenuStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('dropdownMenuStyle')"
          :style="vStyle"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item>
              <template #label>下拉选项</template>
              <a-select
                class="full-line80"
                style="width: 100%"
                :open="true"
                mode="multiple"
                :getPopupContainer="triggerNode => triggerNode.parentNode"
                :options="selectOptions"
                v-model="selectVal1"
              />
            </a-descriptions-item>
          </a-descriptions>
        </div>
        <div class="ant-page-header-heading-sub-title" style="width: 200%">
          下拉选项引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">下拉框</a-button>
          ，可点击进入相关组件进行样式配置
        </div>
      </a-card>
      <!-- 允许清空 -->
      <a-card title="允许清空" :class="[selectedKey == 'clearStyle' ? 'border-selected' : '']" @click="onClickCard('clearStyle')">
        <a-descriptions layout="vertical" :colon="false" :column="2" :style="vStyle">
          <a-descriptions-item label="清空按钮样式">
            <a-select
              :allowClear="true"
              mode="multiple"
              :defaultValue="['1', '2']"
              class="full-line80 theme-select-allow-clear"
              :getPopupContainer="getPopupContainer()"
              style="width: 100%"
              placeholder="请选择"
            >
              <a-select-option v-for="item in selectOptions" :key="item.value">
                {{ item.label }}
              </a-select-option>
            </a-select>
          </a-descriptions-item>
        </a-descriptions>
        <div class="ant-page-header-heading-sub-title">
          允许清空引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">下拉框</a-button>
          ，可点击进入相关组件进行样式配置
        </div>
      </a-card>
    </a-page-header>
  </div>
</template>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import { kebabCase } from 'lodash';

export default {
  name: 'ThemeJob',
  mixins: [themePropMixin],
  props: {
    config: Object,
    themeStyle: {
      type: Object,
      default: () => {}
    }
  },
  title: '职位',
  category: 'formBasicComp',
  themePropConfig: {},
  computed: {
    vStyle() {
      if (!this.selectStyle) {
        this.getSelectStyle();
      }
      return this.selectStyle;
    }
  },
  data() {
    return {
      selectedKey: '',
      quoteKey: 'ThemeSelect',
      testForm: { testSelect: undefined },
      testRules: {
        testSelect: [
          {
            required: true,
            message: (
              <span class="ant-select-error">
                <a-icon type="warning" /> 必填
              </span>
            )
          }
        ]
      },
      selectVal: undefined,
      selectVal1: '',
      selectOptions: [
        { value: 'J0000000067', label: '人事专员' },
        { value: 'J0000000102', label: '研发总监1' },
        { value: 'J0000000107', label: '重要领导' }
      ]
    };
  },
  mounted() {
    this.$refs.testForm.validate();
    this.getSelectStyle();
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
    },
    getPopupContainer() {
      return triggerNode => triggerNode.closest('.ant-card');
    },
    getSelectStyle() {
      this.selectStyle = this.themeStyle[this.quoteKey];
      if (!this.selectStyle) {
        this.selectStyle = this.getComponentConfig(this.quoteKey, [
          'basicStyle',
          'errorStyle',
          'dropdownStyle',
          'dropdownMenuStyle',
          'clearStyle'
        ]);
      }
    }
  }
};
</script>
