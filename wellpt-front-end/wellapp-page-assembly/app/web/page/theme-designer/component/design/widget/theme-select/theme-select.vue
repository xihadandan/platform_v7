<template>
  <div class="theme-style-panel">
    <a-page-header title="下拉框">
      <div slot="subTitle">平台表单下拉框</div>
      <!-- 基础样式 -->
      <a-card :class="[selectedKey == 'basicStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('basicStyle')">
        <template #title>
          基础样式
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('basicStyle')" />
        </template>
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
              ref="HoverRef"
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
                <a-select
                  :options="selectOptions"
                  class="full-line80"
                  style="width: 100%"
                  v-model="selectVal"
                  :getPopupContainer="getPopupContainer()"
                  placeholder="请选择"
                />
              </a-form-model-item>
            </a-descriptions-item>
            <a-descriptions-item label="悬停  Hover">
              <a-form-model-item prop="testSelect" required class="full-line80 theme-ant-select-error-hover">
                <a-select
                  :options="selectOptions"
                  style="width: 100%"
                  v-model="selectVal"
                  :getPopupContainer="getPopupContainer()"
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
      </a-card>
      <!-- 下拉选项 -->
      <a-card title="下拉选项" class="theme-card-select-dropdown" :style="vStyle">
        <div
          :class="[selectedKey == 'dropdownStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('dropdownStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item>
              <template #label>
                下拉面板
                <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('dropdownStyle')" />
              </template>
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
        >
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item>
              <template #label>
                下拉选项
                <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('dropdownMenuStyle')" />
              </template>
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
      </a-card>
      <!-- 允许清空 -->
      <a-card :class="[selectedKey == 'clearStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('clearStyle')">
        <template #title>
          允许清空
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('clearStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="3">
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
      </a-card>
      <!-- <a-card :style="vStyle">
        <template #title>级联选择器</template>
        <a-descriptions layout="vertical" :colon="false" :column="3">
          <a-descriptions-item label="级联选择器样式">
            <a-cascader :options="selectOptions" :getPopupContainer="getPopupContainer()" />
          </a-descriptions-item>
        </a-descriptions>
      </a-card> -->
    </a-page-header>
  </div>
</template>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import { kebabCase } from 'lodash';

export default {
  name: 'ThemeSelect',
  mixins: [themePropMixin],
  props: {
    config: Object
  },
  title: '下拉框',
  category: 'formBasicComp',
  themePropConfig: {
    // 基础样式
    basicStyle: {
      wSelectBorderColor: '--w-border-color-light',
      wSelectBorderStyle: '--w-border-style-base',
      wSelectBorderWidth: '--w-border-width-base',
      wSelectBorderRadius: '--w-border-radius-2',
      wSelectTextColor: '--w-text-color-dark',
      wSelectPlaceholderColor: '--w-text-color-lighter',
      wSelectTextSize: '--w-font-size-base',
      wSelectTextWeight: '--w-font-weight-regular',
      wSelectArrowColor: '--w-text-color-light',
      wSelectArrowSize: '--w-font-size-sm',
      wSelectSelectionBg: '--w-fill-color-base',
      wSelectRenderedMr: '--w-margin-lg',
      wSelectSelectionHeight: '--w-height-sm',

      wSelectBorderColorHover: '--w-primary-color',
      wSelectArrowSizeHover: '--w-font-size-sm',
      wSelectArrowColorHover: '--w-text-color-light',

      wSelectBorderColorFocus: '--w-primary-color',
      wSelectArrowSizeFocus: '--w-font-size-sm',
      wSelectArrowColorFocus: '--w-text-color-light',

      wSelectBorderColorDisabled: '--w-border-color-light',
      wSelectArrowSizeDisabled: '---w-font-size-base',
      wSelectArrowColorDisabled: '--w-text-color-lighter',
      wSelectTextColorDisabled: '--w-text-color-lighter',
      wSelectSelectionBgDisabled: '--w-fill-color-light'
    },
    errorStyle: {
      wSelectErrorBorderColor: '--w-danger-color',
      wSelectErrorBorderStyle: '--w-border-style-base',
      wSelectErrorBorderWidth: '--w-border-width-base',
      wSelectErrorTextColor: '--w-danger-color',
      wSelectErrorTextSize: '--w-font-size-base',
      wSelectErrorTextWeight: '--w-font-weight-regular',
      wSelectErrorArrowSize: '--w-font-size-sm',
      wSelectErrorArrowColor: '--w-danger-color',
      wSelectErrorBorderColorHover: '--w-danger-color',
      wSelectErrorBorderColorFocus: '--w-danger-color'
    },
    dropdownStyle: {
      wSelectDropdownBorderColor: '--w-border-color-base',
      wSelectDropdownBorderStyle: '--w-border-style-base',
      wSelectDropdownBorderWidth: '--w-border-width-base',
      wSelectDropdownBg: '--w-fill-color-base'
    },
    dropdownMenuStyle: {
      wSelectItemTextColor: '--w-text-color-dark',
      wSelectItemTextSize: '--w-font-size-base',
      wSelectItemTextWeight: '--w-font-weight-regular',
      wSelectItemBg: '--w-select-dropdown-bg',
      wSelectItemLrPadding: '--w-padding-2xs',
      wSelectItemHeight: '--w-height-md',

      wSelectItemColorHover: '--w-text-color-dark',
      wSelectItemBgHover: '--w-primary-color-1',
      wSelectItemColorIconHover: '--w-text-color-dark',
      wSelectItemTextWeightHover: '--w-font-weight-regular',

      wSelectItemColorSelected: '--w-primary-color',
      wSelectItemBgSelected: '--w-primary-color-1',
      wSelectItemTextWeightSelected: '--w-font-weight-regular',
      wSelectItemColorIconSelected: '--w-primary-color'
    },
    clearStyle: {
      wSelectClearColor: '--w-text-color-lighter',
      wSelectClearSize: '--w-font-size-base',
      wSelectClearRight: '--w-margin-xs',
      wSelectClearColorHover: '--w-text-color-light',
      wSelectClearSizeHover: '--w-font-size-base',
      wSelectClearColorActive: '--w-text-color-light',
      wSelectClearSizeActive: '--w-font-size-base'
    }
  },
  computed: {
    vStyle() {
      let colorVars = {},
        keys = ['basicStyle', 'errorStyle', 'dropdownStyle', 'dropdownMenuStyle', 'clearStyle'];
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
      return vars;
    }
  },
  data() {
    return {
      selectedKey: 'basicStyle',
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
      selectVal1: [],
      selectOptions: [
        {
          label: '选项一',
          value: '1',
          children: [
            {
              value: 'nanjing',
              label: 'Nanjing',
              children: [
                {
                  value: 'zhonghuamen',
                  label: 'Zhong Hua Men'
                }
              ]
            }
          ]
        },
        { label: '选项二', value: '2', disabled: true },
        { label: '选项三', value: '3' }
      ]
    };
  },
  mounted() {
    this.$emit('select', this.config.basicStyle);
    this.$refs.testForm.validate();

    this.$nextTick(() => {
      setTimeout(() => {
        // 选择面板默认打开会让页面滚到底部，给悬停控件做聚焦，让页面滚动到顶部
        this.$refs.HoverRef.focus();
      }, 200);
    });
  },
  methods: {
    onClickCard(key, refresh) {
      if (this.selectedKey != key || refresh) {
        let keyComp = {
          basicStyle: 'ThemeSelectProp',
          errorStyle: 'ThemeSelectError',
          dropdownStyle: 'ThemeSelectDropdown',
          dropdownMenuStyle: 'ThemeSelectDropdownMenu',
          clearStyle: 'ThemeSelectClear'
        };
        this.selectedKey = key;
        this.$emit('select', this.config[key], null, keyComp[key]);
      }
    },
    undoThemePropConfig(key) {
      this.config[key] = JSON.parse(JSON.stringify(this.$options.themePropConfig[key]));
      this.onClickCard(key, true);
    },
    getPopupContainer() {
      return triggerNode => triggerNode.closest('.ant-card');
    }
  }
};
</script>
