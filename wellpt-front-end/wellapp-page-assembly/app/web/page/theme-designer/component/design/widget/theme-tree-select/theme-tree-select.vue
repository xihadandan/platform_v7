<template>
  <div class="theme-style-panel">
    <a-page-header title="树形下拉框">
      <div slot="subTitle">平台表单树形下拉框</div>
      <!-- 基础样式 -->
      <a-card title="基础样式" :style="vStyle">
        <div
          :class="[selectedKey == 'basicStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('basicStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item label="默认  Default">
              <a-tree-select
                class="full-line80"
                :treeData="treeData"
                :replaceFields="replaceFields"
                :getPopupContainer="getPopupContainer()"
              />
            </a-descriptions-item>
            <a-descriptions-item label="悬停  Hover">
              <a-tree-select
                ref="HoverRef"
                class="full-line80 theme-ant-select-hover"
                :treeData="treeData"
                :replaceFields="replaceFields"
                :getPopupContainer="getPopupContainer()"
              />
            </a-descriptions-item>
            <a-descriptions-item label="焦点  Focus">
              <a-tree-select
                class="full-line80 ant-select-focused"
                :treeData="treeData"
                :replaceFields="replaceFields"
                :getPopupContainer="getPopupContainer()"
              />
            </a-descriptions-item>
            <a-descriptions-item label="禁用  Disable">
              <a-tree-select
                disabled
                class="full-line80"
                :treeData="treeData"
                :replaceFields="replaceFields"
                :getPopupContainer="getPopupContainer()"
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
      <a-card title="错误提示" :style="vStyle">
        <div
          :class="[selectedKey == 'errorStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('errorStyle')"
        >
          <a-form-model layout="vertical" :colon="false" :model="testForm" :rules="testRules" ref="testForm">
            <a-descriptions layout="vertical" :colon="false" :column="2">
              <a-descriptions-item label="错误提示 Error">
                <a-form-model-item prop="testSelect" required class="full-line80">
                  <a-tree-select :treeData="treeData" :replaceFields="replaceFields" :getPopupContainer="getPopupContainer()" />
                </a-form-model-item>
              </a-descriptions-item>
              <a-descriptions-item label="悬停  Hover">
                <a-form-model-item prop="testSelect" required class="full-line80 theme-ant-select-error-hover">
                  <a-tree-select :treeData="treeData" :replaceFields="replaceFields" :getPopupContainer="getPopupContainer()" />
                </a-form-model-item>
              </a-descriptions-item>
              <a-descriptions-item label="获取焦点  Focus">
                <a-form-model-item prop="testSelect" required class="full-line80">
                  <a-tree-select
                    class="ant-select-focused"
                    :treeData="treeData"
                    :replaceFields="replaceFields"
                    :getPopupContainer="getPopupContainer()"
                  />
                </a-form-model-item>
              </a-descriptions-item>
              <a-descriptions-item :label="null"></a-descriptions-item>
            </a-descriptions>
          </a-form-model>
        </div>
        <div class="ant-page-header-heading-sub-title">
          基础样式引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">下拉框</a-button>
          ，可点击进入相关组件进行样式配置
        </div>
      </a-card>
      <!-- 下拉选项 -->
      <a-card title="下拉选项" :style="vStyle">
        <div
          :class="[selectedKey == 'dropdownStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('dropdownStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2" :style="{ height: '340px' }">
            <a-descriptions-item label="下拉面板">
              <a-tree-select
                class="full-line80"
                :treeData="treeData"
                :replaceFields="replaceFields"
                :getPopupContainer="triggerNode => triggerNode.parentNode"
                :dropdown-style="{ maxHeight: '260px', overflow: 'auto' }"
                open
              />
            </a-descriptions-item>
            <a-descriptions-item></a-descriptions-item>
          </a-descriptions>
        </div>
        <div class="ant-page-header-heading-sub-title">
          基础样式引用了样式
          <a-button size="small" type="primary" class="theme-cite-btn" @click="handleQuote">下拉框</a-button>
          ，可点击进入相关组件进行样式配置
        </div>
      </a-card>
      <!-- 允许清空 -->
      <a-card title="允许清空" :style="vStyle">
        <div
          :class="[selectedKey == 'clearStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('clearStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item label="清空按钮样式">
              <a-tree-select
                class="full-line80"
                allowClear
                treeCheckable
                :getPopupContainer="getPopupContainer()"
                v-model="treeDataValue"
                :treeData="treeData"
                :replaceFields="replaceFields"
              />
            </a-descriptions-item>
            <a-descriptions-item></a-descriptions-item>
          </a-descriptions>
        </div>
        <div class="ant-page-header-heading-sub-title">
          基础样式引用了样式
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
  name: 'ThemeTreeSelect',
  mixins: [themePropMixin],
  props: {
    config: Object,
    themeStyle: {
      type: Object,
      default: () => {}
    }
  },
  title: '树形下拉框',
  category: 'formBasicComp',
  themePropConfig: {},
  computed: {
    vStyle() {
      if (!this.selectStyle) {
        this.getSelectStyle();
      }
      if (!this.checkboxStyle) {
        this.getCheckboxStyle();
      }
      const vStyle = { ...this.checkboxStyle, ...this.selectStyle };
      return vStyle;
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
      treeDataValue: 'bb97e21a-7197-46cb-8125-69b1aefac5c9',
      treeData: undefined,
      replaceFields: { title: 'name', key: 'id', value: 'id' },
      selectStyle: undefined,
      checkboxStyle: undefined
    };
  },
  created() {
    this.getTreeData();
    this.getSelectStyle();
    this.getCheckboxStyle();
  },
  mounted() {
    this.$refs.testForm.validate();
    this.$nextTick(() => {
      setTimeout(() => {
        // 选择面板默认打开会让页面滚到底部，给悬停控件做聚焦，让页面滚动到顶部
        this.$refs.HoverRef.focus();
      }, 300);
    });
  },
  methods: {
    getTreeData() {
      $axios
        .post('/json/data/services', {
          serviceName: 'dataDictionaryService',
          methodName: 'getAllDataDicAsTree',
          args: JSON.stringify(['96751f17-55da-43c5-9d62-1de3b9325ec4'])
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            console.log(data.data);
            data.data.children[0].disabled = true;
            this.treeData = data.data.children;
          }
        });
    },
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
    },
    getCheckboxStyle() {
      this.checkboxStyle = this.themeStyle['ThemeCheckbox'];
      if (!this.checkboxStyle) {
        this.checkboxStyle = this.getComponentConfig('ThemeCheckbox', [
          'basicStyle',
          'errorStyle',
          'groupItemMarginStyle',
          'groupVerticalStyle'
        ]);
      }
    }
  }
};
</script>
