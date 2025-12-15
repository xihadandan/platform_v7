<template>
  <div class="theme-style-panel">
    <a-page-header title="组织选择框">
      <div slot="subTitle">平台表单组织选择框</div>
      <!-- 基础样式 -->
      <a-card :class="[selectedKey == 'basicStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('basicStyle')">
        <template #title>
          基础样式
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('basicStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2">
          <a-descriptions-item label="默认  Default">
            <OrgSelect class="full-line80" :orgUuid="orgUuid" @change="onChange" />
          </a-descriptions-item>
          <a-descriptions-item label="悬停  Hover">
            <OrgSelect class="full-line80 theme-org-select-hover" :orgUuid="orgUuid" />
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
              <a-form-model-item prop="testSelect" required class="full-line80">
                <OrgSelect class="full-line80" :orgUuid="orgUuid" />
              </a-form-model-item>
            </a-descriptions-item>
            <a-descriptions-item label="悬停  Hover">
              <a-form-model-item prop="testSelect" required class="full-line80 theme-org-select-error-hover">
                <OrgSelect class="full-line80" :orgUuid="orgUuid" />
              </a-form-model-item>
            </a-descriptions-item>
          </a-descriptions>
        </a-form-model>
      </a-card>
      <!-- 返回值 -->
      <a-card :style="vStyle">
        <template #title>
          返回值
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('valueStyle')" />
        </template>
        <div
          :class="[selectedKey == 'valueStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('valueStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item label="标签">
              <OrgSelect class="full-line80" v-model="orgValue" />
            </a-descriptions-item>
            <a-descriptions-item label="文本">
              <OrgSelect class="full-line80" v-model="orgValue" displayStyle="Label" />
            </a-descriptions-item>
          </a-descriptions>
        </div>
      </a-card>
    </a-page-header>
  </div>
</template>
<script type="text/babel">
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import { kebabCase } from 'lodash';

export default {
  name: 'ThemeOrgSelect',
  mixins: [themePropMixin],
  props: {
    config: Object
  },
  title: '组织选择框',
  category: 'formBasicComp',
  themePropConfig: {
    // 基础样式
    basicStyle: {
      wOrgBorderColor: '--w-border-color-base',
      wOrgBorderStyle: '--w-border-style-base',
      wOrgBorderWidth: '--w-border-width-base',
      wOrgBorderRadius: '--w-border-radius-2',
      wOrgIconSize: '--w-font-size-base',
      wOrgIconColor: '--w-text-color-light',
      wOrgBgColor: '--w-fill-color-base',
      wOrgLrPadding: '--w-padding-xs',
      wOrgSelectHeight: '--w-height-sm',

      wOrgBorderColorHover: '--w-primary-color',
      wOrgIconSizeHover: '--w-font-size-base',
      wOrgIconColorHover: '--w-text-color-light'
    },
    errorStyle: {
      wOrgErrorBorderColor: '--w-danger-color',
      wOrgErrorBorderStyle: '--w-border-style-base',
      wOrgErrorBorderWidth: '--w-border-width-base',
      wOrgErrorIconSize: '--w-font-size-base',
      wOrgErrorIconColor: '--w-danger-color',
      wOrgErrorTextColor: '--w-danger-color',
      wOrgErrorTextSize: '--w-font-size-base',
      wOrgErrorTextWeight: '--w-font-weight-regular',

      wOrgErrorBorderColorHover: '--w-danger-color'
    },
    valueStyle: {
      wOrgTagBorderColor: '--w-primary-color-3',
      wOrgTagBorderStyle: '--w-border-style-base',
      wOrgTagBorderWidth: '--w-border-width-base',
      wOrgTagBorderRadius: '--w-border-radius-2',
      wOrgTagTextColor: '--w-primary-color',
      wOrgTagTextSize: '--w-font-size-sm',
      wOrgTagTextWeight: '--w-font-weight-regular',
      wOrgTagCloseSize: '--w-font-size-sm',
      wOrgTagBgColor: '--w-primary-color-1',
      wOrgTagLrPadding: '--w-padding-2xs',
      wOrgTagHeight: '--w-height-3xs',
      wOrgTextSize: '--w-font-size-base',
      wOrgTextColor: '--w-text-color-dark'
    }
  },
  components: {
    OrgSelect
  },
  computed: {
    vStyle() {
      let colorVars = {},
        keys = ['basicStyle', 'errorStyle', 'valueStyle'];
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
      orgUuid: '800094164648718336',
      orgValue: 'S_794569976823410688',
      testForm: { testSelect: undefined },
      testRules: {
        testSelect: [
          {
            required: true,
            message: (
              <span class="org-select-error">
                <a-icon type="warning" /> 必填
              </span>
            )
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
    onChange({ value, label, nodes }) {
      console.log('组织弹出框选择变更：', arguments);
    },
    onClickCard(key, refresh) {
      if (this.selectedKey != key || refresh) {
        let keyComp = {
          basicStyle: 'ThemeOrgSelectProp',
          errorStyle: 'ThemeOrgSelectError',
          valueStyle: 'ThemeOrgSelectValue'
        };
        this.selectedKey = key;
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
